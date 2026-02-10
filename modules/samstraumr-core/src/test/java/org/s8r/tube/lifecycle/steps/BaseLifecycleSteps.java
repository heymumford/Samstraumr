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

package org.s8r.tube.lifecycle.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all lifecycle step definitions providing common functionality.
 *
 * <p>This class contains shared methods and fields used across different step definition classes,
 * providing a consistent approach to testing tube lifecycle features.
 */
public abstract class BaseLifecycleSteps {
  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseLifecycleSteps.class);

  // Test context fields - accessible to all derived step classes
  protected Tube testTube;
  protected Tube parentTube;
  protected Tube originTube;
  protected Environment environment;
  protected Exception exceptionThrown;
  protected Map<String, Object> testContext;
  protected TubeIdentity tubeIdentity;
  protected TubeIdentity parentIdentity;
  protected TubeIdentity originIdentity;

  /** Constructor initializes the shared test context. */
  public BaseLifecycleSteps() {
    this.testContext = new HashMap<>();
    LOGGER.info("BaseLifecycleSteps initialized");
  }

  /**
   * Prepares a standard environment for testing.
   *
   * @return An initialized Environment instance
   * @throws AssertionError if environment creation fails
   */
  protected Environment prepareEnvironment() {
    try {
      Environment env = new Environment();
      assertNotNull(env, "Environment should be initialized");
      LOGGER.info("Test environment initialized successfully");
      return env;
    } catch (Exception e) {
      LOGGER.error("Failed to initialize Environment", e);
      fail("Failed to initialize Environment: " + e.getMessage());
      return null; // Unreachable but needed for compilation
    }
  }

  /**
   * Creates a tube with the specified reason.
   *
   * @param reason The reason for creating the tube
   * @param env The environment in which to create the tube
   * @return The created tube
   */
  protected Tube createTube(String reason, Environment env) {
    try {
      Tube tube = Tube.create(reason, env);
      assertNotNull(tube, "Tube should be created successfully");
      LOGGER.info("Created test tube with reason: {}", reason);
      return tube;
    } catch (Exception e) {
      LOGGER.error("Failed to create tube: {}", e.getMessage(), e);
      exceptionThrown = e;
      return null;
    }
  }

  /**
   * Stores an object in the test context with the specified key.
   *
   * @param key The key under which to store the object
   * @param value The object to store
   */
  protected void storeInContext(String key, Object value) {
    testContext.put(key, value);
  }

  /**
   * Retrieves an object from the test context with the specified key.
   *
   * @param <T> The type of the object to retrieve
   * @param key The key under which the object is stored
   * @param clazz The class of the object
   * @return The object from the context, or null if not found
   */
  @SuppressWarnings("unchecked")
  protected <T> T getFromContext(String key, Class<T> clazz) {
    Object value = testContext.get(key);
    if (value != null && clazz.isInstance(value)) {
      return (T) value;
    }
    return null;
  }

  /** Asserts that the test tube has a valid unique identifier. */
  protected void assertTubeHasValidId() {
    assertNotNull(testTube, "Test tube should not be null");
    assertNotNull(testTube.getUniqueId(), "Tube should have a unique ID");
    assertTrue(testTube.getUniqueId().length() > 0, "Tube ID should not be empty");
  }

  /**
   * Creates and initializes an origin (Adam) tube with its identity.
   *
   * @param reason The reason for creating this tube
   * @return The created origin tube
   */
  protected Tube createOriginTube(String reason) {
    if (environment == null) {
      environment = prepareEnvironment();
    }

    // Create the origin tube
    originTube = Tube.create(reason, environment);
    assertNotNull(originTube, "Origin tube should be created successfully");

    // Create the identity - in a production system this would be part of the tube
    originIdentity = TubeIdentity.createAdamIdentity(reason, environment);
    assertNotNull(originIdentity, "Origin identity should be created successfully");

    storeInContext("originTube", originTube);
    storeInContext("originIdentity", originIdentity);

    return originTube;
  }

  /**
   * Creates and initializes a child tube with its identity.
   *
   * @param reason The reason for creating this tube
   * @param parentTube The parent tube
   * @param parentIdentity The parent tube's identity
   * @return The created child tube
   */
  protected Tube createChildTube(String reason, Tube parentTube, TubeIdentity parentIdentity) {
    if (environment == null) {
      environment = prepareEnvironment();
    }

    assertNotNull(parentTube, "Parent tube must not be null");
    assertNotNull(parentIdentity, "Parent identity must not be null");

    // Create the child tube
    Tube childTube = Tube.create(reason, environment);
    assertNotNull(childTube, "Child tube should be created successfully");

    // Create the child identity - in a production system this would be part of the tube
    TubeIdentity childIdentity =
        TubeIdentity.createChildIdentity(reason, environment, parentIdentity);
    assertNotNull(childIdentity, "Child identity should be created successfully");

    // Connect parent and child
    parentIdentity.addChild(childIdentity);
    parentTube.registerChild(childTube);

    return childTube;
  }
}
