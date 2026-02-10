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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.exception.ComponentInitializationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * A reflective implementation of LegacyIdentityConverter that works with any Identity
 * implementation without direct dependencies.
 *
 * <p>This class uses reflection to interact with legacy Identity classes, removing the need for
 * direct dependencies on those classes. This aligns with Clean Architecture principles by isolating
 * the adapter implementation details from the domain layer.
 */
public class ReflectiveIdentityConverter implements LegacyIdentityConverter {

  private final String identityClassName;
  private final LoggerPort logger;
  private final ReflectiveEnvironmentConverter environmentConverter;

  // Cached reflection lookups
  private Class<?> identityClass;
  private Method createAdamIdentityMethod;
  private Method createChildIdentityMethod;
  private Method getUniqueIdMethod;
  private Method getReasonMethod;
  private Method getLineageMethod;
  private Method getEnvironmentContextMethod;
  private Method isAdamTubeMethod;
  private Method addToLineageMethod;
  private Method addEnvironmentContextMethod;

  /**
   * Creates a new reflective identity converter for the specified Identity class.
   *
   * @param identityClassName The fully qualified class name of the Identity implementation
   * @param environmentConverter The environment converter to use
   * @param logger The logger to use
   */
  public ReflectiveIdentityConverter(
      String identityClassName,
      ReflectiveEnvironmentConverter environmentConverter,
      LoggerPort logger) {
    this.identityClassName = identityClassName;
    this.environmentConverter = environmentConverter;
    this.logger = logger;

    try {
      // Attempt to load the Identity class
      this.identityClass = Class.forName(identityClassName);

      // Get the Environment class name from the environmentConverter
      String environmentClassName =
          environmentConverter.getLegacyEnvironmentClassName(
              environmentConverter.createLegacyEnvironment(null));
      Class<?> environmentClass = Class.forName(environmentClassName);

      // Find the necessary static methods
      this.createAdamIdentityMethod =
          identityClass.getMethod("createAdamIdentity", String.class, environmentClass);
      this.createChildIdentityMethod =
          identityClass.getMethod(
              "createChildIdentity", String.class, environmentClass, identityClass);

      // Find the necessary instance methods
      this.getUniqueIdMethod = identityClass.getMethod("getUniqueId");
      this.getReasonMethod = identityClass.getMethod("getReason");
      this.getLineageMethod = identityClass.getMethod("getLineage");
      this.getEnvironmentContextMethod = identityClass.getMethod("getEnvironmentContext");
      this.isAdamTubeMethod = identityClass.getMethod("isAdamTube");
      this.addToLineageMethod = identityClass.getMethod("addToLineage", String.class);
      this.addEnvironmentContextMethod =
          identityClass.getMethod("addEnvironmentContext", String.class, String.class);

      logger.debug("Successfully loaded Identity class: {}", identityClassName);
    } catch (Exception e) {
      logger.error("Failed to initialize ReflectiveIdentityConverter: {}", e.getMessage());
      throw new IllegalArgumentException("Could not load Identity class: " + identityClassName, e);
    }
  }

  @Override
  public ComponentId toComponentId(
      String legacyId, String legacyReason, List<String> legacyLineage) {
    return ComponentId.fromValues(legacyId, legacyReason, new ArrayList<>(legacyLineage));
  }

  @Override
  public String toLegacyIdString(ComponentId componentId) {
    if (componentId == null) {
      return null;
    }
    return componentId.getIdString();
  }

  @Override
  public Object createLegacyAdamIdentity(String reason, Object legacyEnvironment) {
    try {
      return createAdamIdentityMethod.invoke(null, reason, legacyEnvironment);
    } catch (Exception e) {
      logger.error("Failed to create legacy Adam identity: {}", e.getMessage());
      throw new ComponentInitializationException("Error creating legacy Adam identity", e);
    }
  }

  @Override
  public Object createLegacyChildIdentity(
      String reason, Object legacyEnvironment, Object parentLegacyIdentity) {
    if (!identityClass.isInstance(parentLegacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (parentLegacyIdentity != null
                  ? parentLegacyIdentity.getClass().getName()
                  : "null"));
    }

    try {
      return createChildIdentityMethod.invoke(
          null, reason, legacyEnvironment, parentLegacyIdentity);
    } catch (Exception e) {
      logger.error("Failed to create legacy child identity: {}", e.getMessage());
      throw new ComponentInitializationException("Error creating legacy child identity", e);
    }
  }

  @Override
  public Map<String, Object> extractFromLegacyIdentity(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      Map<String, Object> result = new HashMap<>();

      result.put("id", getUniqueIdMethod.invoke(legacyIdentity));
      result.put("reason", getReasonMethod.invoke(legacyIdentity));
      result.put("lineage", getLineageMethod.invoke(legacyIdentity));
      result.put("environmentContext", getEnvironmentContextMethod.invoke(legacyIdentity));
      result.put("isAdam", isAdamTubeMethod.invoke(legacyIdentity));

      return result;
    } catch (Exception e) {
      logger.error("Failed to extract from legacy identity: {}", e.getMessage());
      throw new IllegalStateException("Error extracting from legacy identity", e);
    }
  }

  @Override
  public void addToLegacyIdentityLineage(Object legacyIdentity, String lineageEntry) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      addToLineageMethod.invoke(legacyIdentity, lineageEntry);
    } catch (Exception e) {
      logger.error("Failed to add to legacy identity lineage: {}", e.getMessage());
      throw new IllegalStateException("Error adding to legacy identity lineage", e);
    }
  }

  @Override
  public List<String> getLegacyIdentityLineage(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      @SuppressWarnings("unchecked")
      List<String> lineage = (List<String>) getLineageMethod.invoke(legacyIdentity);
      return new ArrayList<>(lineage);
    } catch (Exception e) {
      logger.error("Failed to get legacy identity lineage: {}", e.getMessage());
      throw new IllegalStateException("Error getting legacy identity lineage", e);
    }
  }

  @Override
  public String getLegacyIdentityId(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      return (String) getUniqueIdMethod.invoke(legacyIdentity);
    } catch (Exception e) {
      logger.error("Failed to get legacy identity ID: {}", e.getMessage());
      throw new IllegalStateException("Error getting legacy identity ID", e);
    }
  }

  @Override
  public String getLegacyIdentityReason(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      return (String) getReasonMethod.invoke(legacyIdentity);
    } catch (Exception e) {
      logger.error("Failed to get legacy identity reason: {}", e.getMessage());
      throw new IllegalStateException("Error getting legacy identity reason", e);
    }
  }

  @Override
  public Map<String, String> getLegacyIdentityEnvironmentContext(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      @SuppressWarnings("unchecked")
      Map<String, String> context =
          (Map<String, String>) getEnvironmentContextMethod.invoke(legacyIdentity);
      return new HashMap<>(context);
    } catch (Exception e) {
      logger.error("Failed to get legacy identity environment context: {}", e.getMessage());
      throw new IllegalStateException("Error getting legacy identity environment context", e);
    }
  }

  @Override
  public void addToLegacyIdentityEnvironmentContext(
      Object legacyIdentity, String key, String value) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      addEnvironmentContextMethod.invoke(legacyIdentity, key, value);
    } catch (Exception e) {
      logger.error("Failed to add to legacy identity environment context: {}", e.getMessage());
      throw new IllegalStateException("Error adding to legacy identity environment context", e);
    }
  }

  @Override
  public boolean isLegacyAdamIdentity(Object legacyIdentity) {
    if (!identityClass.isInstance(legacyIdentity)) {
      throw new IllegalArgumentException(
          "Expected "
              + identityClassName
              + " object, got: "
              + (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
    }

    try {
      return (Boolean) isAdamTubeMethod.invoke(legacyIdentity);
    } catch (Exception e) {
      logger.error("Failed to check if legacy identity is Adam: {}", e.getMessage());
      throw new IllegalStateException("Error checking if legacy identity is Adam", e);
    }
  }
}
