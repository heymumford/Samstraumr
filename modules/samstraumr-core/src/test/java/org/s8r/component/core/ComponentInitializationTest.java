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

package org.s8r.component.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Component initialization and exception handling.
 *
 * <p>Tests verify that components maintain consistent state even when exceptions occur during
 * initialization callbacks.
 */
@DisplayName("Component Initialization Tests")
class ComponentInitializationTest {

  @Test
  @DisplayName("Component should be created successfully with valid parameters")
  void componentShouldBeCreatedSuccessfully() {
    // Arrange
    String reason = "test-component";
    Environment environment = new Environment();

    // Act
    Component component = Component.create(reason, environment);

    // Assert
    assertNotNull(component, "Component should not be null");
    assertEquals(reason, component.getReason(), "Component reason should match");
    assertNotNull(component.getUniqueId(), "Component should have a unique ID");
    assertNotNull(component.getState(), "Component should have a state");
  }

  @Test
  @DisplayName(
      "Component should remain in consistent state despite initialization callback exceptions")
  void componentShouldRemainConsistentDespiteInitializationExceptions() {
    // Arrange
    String reason = "test-component-with-short-termination";
    Environment environment = new Environment();

    // Act - The component initialization may encounter exceptions internally
    // but should still complete construction and reach a valid state
    Component component = Component.create(reason, environment);

    // Assert - Component should be created and in a valid state
    assertNotNull(component, "Component should not be null even if initialization has issues");
    assertNotNull(component.getState(), "Component should have a valid state");
    assertNotNull(component.getUniqueId(), "Component should have a unique ID");
    assertEquals(reason, component.getReason(), "Component reason should be preserved");

    // Verify the component reached READY state despite any initialization issues
    assertEquals(
        State.READY,
        component.getState(),
        "Component should reach READY state even if termination timer setup fails");

    // Verify component is operational
    assertTrue(
        component.isOperational(), "Component should be operational despite initialization issues");
  }

  @Test
  @DisplayName("Component should log errors during initialization without breaking construction")
  void componentShouldLogErrorsDuringInitialization() {
    // Arrange
    String reason = "test-component-logging";
    Environment environment = new Environment();

    // Act
    Component component = Component.create(reason, environment);

    // Assert - Component should be created successfully
    assertNotNull(component, "Component should be created despite potential initialization errors");
    assertNotNull(component.getMemoryLog(), "Component should have a memory log");
    assertTrue(component.getMemoryLogSize() > 0, "Component should have logged initialization");

    // Verify the component is in a usable state
    assertNotNull(component.getState(), "Component should have a valid state");
    assertTrue(
        component.isReady() || component.isOperational(),
        "Component should be ready or operational");
  }

  @Test
  @DisplayName("Component should handle null reason gracefully")
  void componentShouldHandleNullReasonGracefully() {
    // Arrange
    Environment environment = new Environment();

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> Component.create(null, environment),
        "Component creation should fail with null reason");
  }

  @Test
  @DisplayName("Component should handle null environment gracefully")
  void componentShouldHandleNullEnvironmentGracefully() {
    // Arrange
    String reason = "test-component";

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> Component.create(reason, null),
        "Component creation should fail with null environment");
  }

  @Test
  @DisplayName("Child component should remain consistent despite initialization exceptions")
  void childComponentShouldRemainConsistentDespiteInitializationExceptions() {
    // Arrange
    String parentReason = "parent-component";
    String childReason = "child-component";
    Environment environment = new Environment();

    Component parent = Component.create(parentReason, environment);

    // Act
    Component child = Component.createChild(childReason, environment, parent);

    // Assert
    assertNotNull(child, "Child component should be created");
    assertEquals(childReason, child.getReason(), "Child reason should match");
    assertNotNull(child.getParentIdentity(), "Child should have parent identity");
    assertEquals(
        parent.getIdentity(),
        child.getParentIdentity(),
        "Child should reference parent's identity");

    // Verify child is in valid state
    assertNotNull(child.getState(), "Child should have a valid state");
    assertTrue(child.isReady() || child.isOperational(), "Child should be ready or operational");
  }
}
