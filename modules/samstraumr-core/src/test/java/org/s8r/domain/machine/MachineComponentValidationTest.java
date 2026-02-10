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

package org.s8r.domain.machine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;

@DisplayName("Machine Component Validation Integration Tests")
class MachineComponentValidationTest {

  @Test
  @DisplayName("addComponent should add valid composite component")
  void addComponentShouldAddValidComponent() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    CompositeComponent component = mock(CompositeComponent.class);
    ComponentId componentId = ComponentId.create("test-component");
    when(component.getId()).thenReturn(componentId);

    // When
    assertDoesNotThrow(() -> machine.addComponent(component));

    // Then
    assertTrue(machine.getComponent(componentId).isPresent());
    assertEquals(component, machine.getComponent(componentId).get());
  }

  @Test
  @DisplayName("addComponent should throw exception for null component")
  void addComponentShouldThrowExceptionForNullComponent() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    // When / Then
    assertThrows(IllegalArgumentException.class, () -> machine.addComponent(null));
  }

  @Test
  @DisplayName("removeComponent should remove existing component")
  void removeComponentShouldRemoveExistingComponent() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    CompositeComponent component = mock(CompositeComponent.class);
    ComponentId componentId = ComponentId.create("test-component");
    when(component.getId()).thenReturn(componentId);
    machine.addComponent(component);

    // When
    assertDoesNotThrow(() -> machine.removeComponent(componentId));

    // Then
    assertTrue(machine.getComponent(componentId).isEmpty());
  }

  @Test
  @DisplayName("removeComponent should throw exception for non-existent component")
  void removeComponentShouldThrowExceptionForNonExistentComponent() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    ComponentId nonExistentId = ComponentId.create("non-existent");

    // When / Then
    assertThrows(ComponentNotFoundException.class, () -> machine.removeComponent(nonExistentId));
  }

  @Test
  @DisplayName("getComponent should return Optional.empty for non-existent component")
  void getComponentShouldReturnEmptyForNonExistentComponent() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    ComponentId nonExistentId = ComponentId.create("non-existent");

    // When / Then
    assertTrue(machine.getComponent(nonExistentId).isEmpty());
  }

  @Test
  @DisplayName("getComponent should throw exception for null component ID")
  void getComponentShouldThrowExceptionForNullComponentId() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    // When / Then
    assertThrows(IllegalArgumentException.class, () -> machine.getComponent(null));
  }

  @Test
  @DisplayName("Operations should validate components before execution")
  void operationsShouldValidateComponentsBeforeExecution() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    // Add a valid component
    CompositeComponent validComponent = mock(CompositeComponent.class);
    ComponentId validId = ComponentId.create("valid-component");
    when(validComponent.getId()).thenReturn(validId);
    machine.addComponent(validComponent);

    // Initialize the machine
    machine.initialize();

    // When / Then - Operations should validate components
    assertDoesNotThrow(() -> machine.start());
    assertDoesNotThrow(() -> machine.stop());
    assertDoesNotThrow(() -> machine.pause());
    assertDoesNotThrow(() -> machine.resume());

    // Clean up
    assertDoesNotThrow(() -> machine.destroy());
  }

  @Test
  @DisplayName("Machine should not allow operations in destroyed state")
  void machineShouldNotAllowOperationsInDestroyedState() {
    // Given
    Machine machine =
        Machine.create(
            ComponentId.create("test-machine"),
            MachineType.DATA_PROCESSOR,
            "Test Machine",
            "Test Description",
            "1.0.0");

    // Initialize and then destroy the machine
    machine.initialize();
    machine.destroy();

    // When / Then - Operations should throw exceptions
    CompositeComponent component = mock(CompositeComponent.class);
    when(component.getId()).thenReturn(ComponentId.create("mock-component"));

    assertThrows(InvalidOperationException.class, () -> machine.addComponent(component));
    assertThrows(InvalidOperationException.class, () -> machine.start());
    assertThrows(InvalidOperationException.class, () -> machine.stop());
    assertThrows(InvalidOperationException.class, () -> machine.pause());
    assertThrows(InvalidOperationException.class, () -> machine.resume());
  }
}
