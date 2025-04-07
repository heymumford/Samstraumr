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

package org.s8r.component.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.component.Composite;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.Identity;
import org.s8r.component.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all component test step definitions providing common functionality.
 *
 * <p>This class contains shared methods and fields used across different step definition classes,
 * providing a consistent approach to testing component features. It establishes a shared context
 * for test state and provides utility methods for component creation and validation.
 */
public abstract class BaseComponentSteps {
  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseComponentSteps.class);

  // Test context fields - accessible to all derived step classes
  protected Component testComponent;
  protected Component parentComponent;
  protected Component originComponent;
  protected Environment environment;
  protected Exception exceptionThrown;
  protected Map<String, Object> testContext;
  protected Identity componentIdentity;
  protected Identity parentIdentity;
  protected Composite testComposite;
  protected Machine testMachine;

  /** Constructor initializes the shared test context. */
  public BaseComponentSteps() {
    this.testContext = new HashMap<>();
    LOGGER.info("BaseComponentSteps initialized");
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
   * Creates a component with the specified reason.
   *
   * @param reason The reason for creating the component
   * @param env The environment in which to create the component
   * @return The created component
   */
  protected Component createComponent(String reason, Environment env) {
    try {
      Component component = Component.create(reason, env);
      assertNotNull(component, "Component should be created successfully");
      LOGGER.info("Created test component with reason: {}", reason);
      return component;
    } catch (Exception e) {
      LOGGER.error("Failed to create component: {}", e.getMessage(), e);
      exceptionThrown = e;
      return null;
    }
  }

  /**
   * Creates a component with the specified reason using a map-based environment.
   *
   * @param reason The reason for creating the component
   * @param envParams The environment parameters
   * @return The created component
   */
  protected Component createComponent(String reason, Map<String, String> envParams) {
    try {
      Component component = Component.create(reason, envParams);
      assertNotNull(component, "Component should be created successfully");
      LOGGER.info("Created test component with reason: {} using map environment", reason);
      return component;
    } catch (Exception e) {
      LOGGER.error("Failed to create component: {}", e.getMessage(), e);
      exceptionThrown = e;
      return null;
    }
  }

  /**
   * Creates a child component with the specified reason and parent.
   *
   * @param reason The reason for creating the component
   * @param env The environment in which to create the component
   * @param parent The parent component
   * @return The created child component
   */
  protected Component createChildComponent(String reason, Environment env, Component parent) {
    try {
      Component child = Component.createChild(reason, env, parent);
      assertNotNull(child, "Child component should be created successfully");
      LOGGER.info("Created child component with reason: {}", reason);
      return child;
    } catch (Exception e) {
      LOGGER.error("Failed to create child component: {}", e.getMessage(), e);
      exceptionThrown = e;
      return null;
    }
  }

  /**
   * Creates a child component with the specified reason and parent using a map-based environment.
   *
   * @param reason The reason for creating the component
   * @param envParams The environment parameters
   * @param parent The parent component
   * @return The created child component
   */
  protected Component createChildComponent(
      String reason, Map<String, String> envParams, Component parent) {
    try {
      Component child = Component.createChild(reason, envParams, parent);
      assertNotNull(child, "Child component should be created successfully");
      LOGGER.info("Created child component with reason: {} using map environment", reason);
      return child;
    } catch (Exception e) {
      LOGGER.error("Failed to create child component: {}", e.getMessage(), e);
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

  /** Asserts that the test component has a valid unique identifier. */
  protected void assertComponentHasValidId() {
    assertNotNull(testComponent, "Test component should not be null");
    assertNotNull(testComponent.getUniqueId(), "Component should have a unique ID");
    assertTrue(testComponent.getUniqueId().length() > 0, "Component ID should not be empty");
  }

  /**
   * Asserts that the test component has the specified memory log entries.
   *
   * @param expectedEntries The expected log entries
   */
  protected void assertMemoryLogContains(String... expectedEntries) {
    assertNotNull(testComponent, "Test component should not be null");
    List<String> memoryLog = testComponent.getMemoryLog();
    assertNotNull(memoryLog, "Memory log should not be null");

    for (String expected : expectedEntries) {
      boolean found = memoryLog.stream().anyMatch(entry -> entry.contains(expected));
      assertTrue(found, "Memory log should contain: " + expected);
    }
  }
}
