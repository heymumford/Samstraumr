/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.adapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.exception.ComponentInitializationException;
import org.s8r.domain.identity.LegacyEnvironmentConverter;

/**
 * A reflective implementation of LegacyEnvironmentConverter that works with any Environment
 * implementation without direct dependencies.
 *
 * <p>This class uses reflection to interact with legacy Environment classes, removing the need for
 * direct dependencies on those classes. This aligns with Clean Architecture principles by isolating
 * the adapter implementation details from the domain layer.
 */
public class ReflectiveEnvironmentConverter implements LegacyEnvironmentConverter {

  private final String environmentClassName;
  private final LoggerPort logger;

  // Cached reflection lookups
  private Class<?> environmentClass;
  private Method setParameterMethod;
  private Method getParameterMethod;
  private Method getParameterKeysMethod;

  /**
   * Creates a new reflective environment converter for the specified Environment class.
   *
   * @param environmentClassName The fully qualified class name of the Environment implementation
   * @param logger The logger to use
   */
  public ReflectiveEnvironmentConverter(String environmentClassName, LoggerPort logger) {
    this.environmentClassName = environmentClassName;
    this.logger = logger;

    try {
      // Attempt to load the Environment class
      this.environmentClass = Class.forName(environmentClassName);

      // Find the necessary methods
      this.setParameterMethod =
          environmentClass.getMethod("setParameter", String.class, String.class);
      this.getParameterMethod = environmentClass.getMethod("getParameter", String.class);
      this.getParameterKeysMethod = environmentClass.getMethod("getParameterKeys");

      logger.debug("Successfully loaded Environment class: {}", environmentClassName);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      logger.error("Failed to initialize ReflectiveEnvironmentConverter: {}", e.getMessage());
      throw new IllegalArgumentException(
          "Could not load Environment class: " + environmentClassName, e);
    }
  }

  @Override
  public Object createLegacyEnvironment(Map<String, String> parameters) {
    try {
      // Create a new instance of the Environment class
      Object environment = environmentClass.getDeclaredConstructor().newInstance();

      // Set parameters if provided
      if (parameters != null) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
          setParameterMethod.invoke(environment, entry.getKey(), entry.getValue());
        }
      }

      return environment;
    } catch (Exception e) {
      logger.error("Failed to create legacy environment: {}", e.getMessage());
      throw new ComponentInitializationException("Error creating legacy environment", e);
    }
  }

  @Override
  public Map<String, String> extractParametersFromLegacyEnvironment(Object legacyEnvironment) {
    if (!environmentClass.isInstance(legacyEnvironment)) {
      throw new IllegalArgumentException(
          "Expected "
              + environmentClassName
              + " object, got: "
              + (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
    }

    try {
      Map<String, String> result = new HashMap<>();

      // Get parameter keys
      Object keysObj = getParameterKeysMethod.invoke(legacyEnvironment);

      if (keysObj instanceof Iterable<?>) {
        Iterable<?> keys = (Iterable<?>) keysObj;

        for (Object keyObj : keys) {
          if (keyObj instanceof String) {
            String key = (String) keyObj;
            String value = (String) getParameterMethod.invoke(legacyEnvironment, key);
            result.put(key, value);
          }
        }
      }

      return result;
    } catch (Exception e) {
      logger.error("Error extracting parameters from legacy environment: {}", e.getMessage());
      throw new IllegalStateException("Failed to extract parameters from legacy environment", e);
    }
  }

  @Override
  public String getLegacyEnvironmentClassName(Object legacyEnvironment) {
    if (legacyEnvironment == null) {
      return null;
    }
    return legacyEnvironment.getClass().getName();
  }
}
