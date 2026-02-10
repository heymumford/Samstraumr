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

package org.s8r.component;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.s8r.domain.exception.InvalidComponentNameException;

/**
 * Integration tests for component name validation in the Component creation methods. Verifies that
 * component name validation is properly integrated with component creation.
 */
@DisplayName("Component Name Validation Integration Tests")
class ComponentNameValidationTest {

  @Test
  @DisplayName("Component creation should validate component names")
  void componentCreationValidatesNames() {
    // Valid names should work
    assertDoesNotThrow(() -> Component.create("ValidComponent", new Environment()));
    assertDoesNotThrow(() -> Component.createAdam("component-adam"));

    // Invalid name using reserved prefix should fail
    assertThrows(
        InvalidComponentNameException.class,
        () -> Component.create("system.component", new Environment()));

    // Name with illegal characters should fail
    assertThrows(
        InvalidComponentNameException.class,
        () -> Component.createAdam("invalid name with spaces"));

    // Name that's too short should fail
    assertThrows(
        InvalidComponentNameException.class, () -> Component.create("ab", new Environment()));

    // Name with disallowed sequence should fail
    assertThrows(
        InvalidComponentNameException.class, () -> Component.createAdam("component..with..dots"));
  }

  @Test
  @DisplayName("Child component creation should validate component names")
  void childComponentCreationValidatesNames() {
    Component parent = Component.createAdam("ValidParent");

    // Valid name should work
    assertDoesNotThrow(() -> parent.createChild("ValidChild"));
    assertDoesNotThrow(() -> Component.createChild("child-component", new Environment(), parent));

    // Invalid name with disallowed characters should fail
    assertThrows(InvalidComponentNameException.class, () -> parent.createChild("invalid@child"));

    // Name that's too short should fail
    assertThrows(
        InvalidComponentNameException.class,
        () -> Component.createChild("xy", new Environment(), parent));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "system.component",
        "admin.service",
        "root.network",
        "global.manager",
        "internal.controller"
      })
  @DisplayName("Reserved prefixes should be rejected in component names")
  void reservedPrefixesShouldBeRejectedInComponentNames(String reservedName) {
    assertThrows(
        InvalidComponentNameException.class,
        () -> Component.create(reservedName, new Environment()));
  }
}
