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

package org.s8r.core.tube.test.steps;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.identity.Identity;
import org.s8r.core.tube.impl.Component;
import org.slf4j.LoggerFactory;

/**
 * Base class for Cucumber step definitions providing common functionality.
 *
 * <p>This class encapsulates the shared functionality needed across step definitions, including:
 *
 * <ul>
 *   <li>Access to the shared test context
 *   <li>Helper methods for common operations
 *   <li>Logging for test operations
 * </ul>
 */
public class BaseSteps {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BaseSteps.class);

  protected TestContext testContext = new TestContext();

  /**
   * Stores an object in the test context.
   *
   * @param key The key to store the object under
   * @param value The object to store
   */
  protected void storeInContext(String key, Object value) {
    testContext.store(key, value);
    LOGGER.debug("Stored in context: {}", key);
  }

  /**
   * Retrieves an object from the test context.
   *
   * @param key The key to retrieve
   * @param <T> The type to cast the result to
   * @return The stored object, or null if not found
   */
  protected <T> T getFromContext(String key, Class<T> type) {
    T value = testContext.get(key);
    LOGGER.debug("Retrieved from context: {}", key);
    return value;
  }

  /**
   * Creates a standard test environment.
   *
   * @return A configured environment for testing
   */
  protected Environment prepareEnvironment() {
    Environment env = new Environment();
    env.setParameter("test_environment", "true");
    env.setParameter("host", "localhost");
    env.setParameter("mode", "test");
    env.setParameter("timestamp", String.valueOf(System.currentTimeMillis()));
    return env;
  }

  /**
   * Creates a component with the specified reason and environment.
   *
   * @param reason The reason for creating the component
   * @param environment The environment to use
   * @return A new component
   */
  protected Component createComponent(String reason, Environment environment) {
    LOGGER.debug("Creating component with reason: {}", reason);
    Component component = Component.create(reason, environment);
    return component;
  }

  /**
   * Creates a child component with the specified parent.
   *
   * @param reason The reason for creating the component
   * @param environment The environment to use
   * @param parent The parent component
   * @return A new child component
   */
  protected Component createChildComponent(
      String reason, Environment environment, Component parent) {
    LOGGER.debug("Creating child component with reason: {}", reason);
    Component component = Component.createChild(reason, environment, parent);
    return component;
  }

  /** Validates that a component has a valid unique ID. */
  protected void assertComponentHasValidId() {
    Component component = getFromContext("testComponent", Component.class);
    Identity identity = component.getIdentity();
    String uniqueId = identity.getUniqueId();

    if (uniqueId == null || uniqueId.isEmpty()) {
      throw new AssertionError("Component unique ID is missing or empty");
    }
  }
}
