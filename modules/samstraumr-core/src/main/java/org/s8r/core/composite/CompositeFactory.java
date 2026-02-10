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

package org.s8r.core.composite;

import org.s8r.core.env.Environment;
import org.s8r.core.exception.CompositeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating composite instances.
 *
 * <p>This factory simplifies the creation of different types of composites and ensures they are
 * properly configured according to their intended purpose.
 */
public class CompositeFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompositeFactory.class);

  /**
   * Creates a new composite with the specified name, type, and environment.
   *
   * @param name The name of the composite
   * @param type The type of the composite
   * @param environment The environment in which the composite will operate
   * @return A new composite instance
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createComposite(
      String name, CompositeType type, Environment environment) {
    LOGGER.info("Creating new composite: {} of type {}", name, type);

    if (name == null || name.trim().isEmpty()) {
      throw new CompositeException("Composite name cannot be null or empty");
    }

    if (type == null) {
      throw new CompositeException("Composite type cannot be null");
    }

    if (environment == null) {
      throw new CompositeException("Environment cannot be null");
    }

    try {
      return new Composite(name, type, environment);
    } catch (Exception e) {
      throw new CompositeException("Failed to create composite: " + e.getMessage(), e);
    }
  }

  /**
   * Creates a new observer composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new observer composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createObserverComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.OBSERVER, environment);
  }

  /**
   * Creates a new transformer composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new transformer composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createTransformerComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.TRANSFORMER, environment);
  }

  /**
   * Creates a new validator composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new validator composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createValidatorComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.VALIDATOR, environment);
  }

  /**
   * Creates a new processor composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new processor composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createProcessorComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.PROCESSOR, environment);
  }

  /**
   * Creates a new service composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new service composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createServiceComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.SERVICE, environment);
  }

  /**
   * Creates a new controller composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new controller composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createControllerComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.CONTROLLER, environment);
  }

  /**
   * Creates a new adapter composite.
   *
   * @param name The name of the composite
   * @param environment The environment in which the composite will operate
   * @return A new adapter composite
   * @throws CompositeException if the composite cannot be created
   */
  public static Composite createAdapterComposite(String name, Environment environment) {
    return createComposite(name, CompositeType.ADAPTER, environment);
  }

  /** Private constructor to prevent instantiation of this utility class. */
  private CompositeFactory() {
    // Private constructor to prevent instantiation
  }
}
