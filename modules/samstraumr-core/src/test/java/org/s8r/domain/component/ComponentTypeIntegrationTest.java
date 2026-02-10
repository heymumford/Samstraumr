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

package org.s8r.domain.component;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.exception.InvalidComponentTypeException;
import org.s8r.domain.identity.ComponentId;

/** Integration tests for component type validation in Component creation. */
@DisplayName("Component Type Integration Tests")
class ComponentTypeIntegrationTest {

  @Test
  @DisplayName("Components should be created with default type when not specified")
  void componentsShouldBeCreatedWithDefaultType() {
    ComponentId id = ComponentId.create("StandardComponent");
    Component component = Component.create(id);

    // Verify default type is set
    assertEquals(ComponentType.STANDARD.getCode(), component.getComponentType());
    assertEquals(ComponentType.STANDARD, component.getComponentTypeEnum());
  }

  @Test
  @DisplayName("Components should be created with specified valid type")
  void componentsShouldBeCreatedWithSpecifiedValidType() {
    ComponentId id = ComponentId.create("ProcessorComponent");

    // Create with string type code
    Component component1 = Component.create(id, ComponentType.PROCESSOR.getCode());
    assertEquals(ComponentType.PROCESSOR.getCode(), component1.getComponentType());

    // Create with enum type
    Component component2 = Component.create(id, ComponentType.VALIDATOR);
    assertEquals(ComponentType.VALIDATOR.getCode(), component2.getComponentType());
  }

  @Test
  @DisplayName("Component creation should reject invalid types")
  void componentCreationShouldRejectInvalidTypes() {
    ComponentId id = ComponentId.create("InvalidComponent");

    // Test with null type
    assertThrows(InvalidComponentTypeException.class, () -> Component.create(id, (String) null));

    // Test with empty type
    assertThrows(InvalidComponentTypeException.class, () -> Component.create(id, ""));

    // Test with invalid type
    assertThrows(InvalidComponentTypeException.class, () -> Component.create(id, "invalid-type"));
  }

  @Test
  @DisplayName("Component should validate operations based on type")
  void componentShouldValidateOperationsBasedOnType() {
    // Create components with different types
    Component processorComponent =
        Component.create(ComponentId.create("ProcessorComponent"), ComponentType.PROCESSOR);

    Component validatorComponent =
        Component.create(ComponentId.create("ValidatorComponent"), ComponentType.VALIDATOR);

    // Test operation allowed for PROCESSOR
    assertTrue(processorComponent.isOperationAllowed("PROCESS_DATA"));
    assertDoesNotThrow(() -> processorComponent.validateOperation("PROCESS_DATA"));

    // Test operation not allowed for PROCESSOR
    assertFalse(processorComponent.isOperationAllowed("VALIDATE_DATA"));
    assertThrows(
        InvalidComponentTypeException.class,
        () -> processorComponent.validateOperation("VALIDATE_DATA"));

    // Test operation allowed for VALIDATOR
    assertTrue(validatorComponent.isOperationAllowed("VALIDATE_DATA"));
    assertDoesNotThrow(() -> validatorComponent.validateOperation("VALIDATE_DATA"));

    // Test restricted operation not allowed for any type
    assertFalse(processorComponent.isOperationAllowed("SYSTEM_ADMIN"));
    assertThrows(
        InvalidComponentTypeException.class,
        () -> processorComponent.validateOperation("SYSTEM_ADMIN"));

    assertFalse(validatorComponent.isOperationAllowed("SYSTEM_ADMIN"));
    assertThrows(
        InvalidComponentTypeException.class,
        () -> validatorComponent.validateOperation("SYSTEM_ADMIN"));
  }

  @Test
  @DisplayName("Component should allow operations without restrictions")
  void componentShouldAllowOperationsWithoutRestrictions() {
    // Create component
    Component component = Component.create(ComponentId.create("StandardComponent"));

    // Test generic operation without restrictions
    assertTrue(component.isOperationAllowed("GENERIC_OPERATION"));
    assertDoesNotThrow(() -> component.validateOperation("GENERIC_OPERATION"));
  }

  @Test
  @DisplayName("Component toString should include type")
  void componentToStringShouldIncludeType() {
    Component component =
        Component.create(ComponentId.create("TestComponent"), ComponentType.OBSERVER);

    String toString = component.toString();
    assertTrue(toString.contains("type=" + ComponentType.OBSERVER.getCode()));
  }
}
