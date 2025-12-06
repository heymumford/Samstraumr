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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.exception.InvalidMachineStateTransitionException;
import org.s8r.domain.identity.ComponentId;

/** Integration tests for machine state validation. */
@DisplayName("Machine State Validation Integration Tests")
class MachineStateValidationTest {

  private Machine machine;
  private ComponentId machineId;
  private CompositeComponent testComponent;

  @BeforeEach
  void setUp() {
    // Create a test machine
    machineId = ComponentId.create("TestMachine");
    machine =
        Machine.create(
            machineId,
            MachineType.STANDARD,
            "Test Machine",
            "A machine for testing state validation",
            "1.0.0");

    // Create a test component to add to the machine
    ComponentId componentId = ComponentId.create("TestComponent");
    testComponent = CompositeComponent.create(componentId, CompositeType.STANDARD);
  }

  @Test
  @DisplayName("Machine should follow correct lifecycle state transitions")
  void machineShouldFollowCorrectLifecycleTransitions() {
    // Verify initial state
    assertEquals(MachineState.CREATED, machine.getState());

    // Test initialize -> start -> stop cycle
    machine.initialize();
    assertEquals(MachineState.READY, machine.getState());

    machine.start();
    assertEquals(MachineState.RUNNING, machine.getState());

    machine.stop();
    assertEquals(MachineState.STOPPED, machine.getState());

    // Test restart
    machine.start();
    assertEquals(MachineState.RUNNING, machine.getState());

    // Test pause and resume
    machine.pause();
    assertEquals(MachineState.PAUSED, machine.getState());

    machine.resume();
    assertEquals(MachineState.RUNNING, machine.getState());

    // Test destroy (terminal state)
    machine.destroy();
    assertEquals(MachineState.DESTROYED, machine.getState());
  }

  @Test
  @DisplayName("Invalid state transitions should be prevented")
  void invalidStateTransitionsShouldBePrevented() {
    // Try to start before initializing
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.start());

    // Try to stop when not running
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.stop());

    // Initialize the machine
    machine.initialize();

    // Try to initialize again
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.initialize());

    // Try to resume when not paused
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.resume());
  }

  @Test
  @DisplayName("Error state transitions should be handled correctly")
  void errorStateTransitionsShouldBeHandledCorrectly() {
    // Initialize the machine
    machine.initialize();
    assertEquals(MachineState.READY, machine.getState());

    // Set error state
    boolean result = machine.setErrorState("Test error condition");
    assertTrue(result);
    assertEquals(MachineState.ERROR, machine.getState());

    // Try to start when in error state
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.start());

    // Reset from error
    machine.resetFromError();
    assertEquals(MachineState.READY, machine.getState());

    // Now we can start
    machine.start();
    assertEquals(MachineState.RUNNING, machine.getState());
  }

  @Test
  @DisplayName("Component modifications should only be allowed in appropriate states")
  void componentModificationsShouldOnlyBeAllowedInAppropriateStates() {
    // Can add components in CREATED state
    machine.addComponent(testComponent);

    // Initialize and verify we can still add components
    machine.initialize();
    CompositeComponent secondComponent =
        CompositeComponent.create(ComponentId.create("SecondComponent"), CompositeType.STANDARD);
    machine.addComponent(secondComponent);

    // Start the machine
    machine.start();

    // Should not be able to add components while running
    CompositeComponent thirdComponent =
        CompositeComponent.create(ComponentId.create("ThirdComponent"), CompositeType.STANDARD);

    assertThrows(
        InvalidMachineStateTransitionException.class, () -> machine.addComponent(thirdComponent));

    // Stop the machine and verify we can add components again
    machine.stop();
    machine.addComponent(thirdComponent);

    // Set version should also only work in modifiable states
    machine.setVersion("1.0.1");

    // Start again and verify version can't be changed
    machine.start();
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.setVersion("1.0.2"));
  }

  @Test
  @DisplayName("Destroyed machines should reject all operations")
  void destroyedMachinesShouldRejectAllOperations() {
    // Initialize and destroy the machine
    machine.initialize();
    machine.destroy();
    assertEquals(MachineState.DESTROYED, machine.getState());

    // All operations should be rejected
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.initialize());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.start());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.stop());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.pause());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.resume());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.resetFromError());

    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.setVersion("2.0.0"));

    assertThrows(
        InvalidMachineStateTransitionException.class, () -> machine.addComponent(testComponent));

    // Even destroy should be rejected (can't destroy twice)
    assertThrows(InvalidMachineStateTransitionException.class, () -> machine.destroy());

    // Error state setting should return false but not throw
    boolean result = machine.setErrorState("Test error");
    assertFalse(result);
  }

  @Test
  @DisplayName("Machine should support explicit state transitions via setState")
  void machineShouldSupportExplicitStateTransitions() {
    // Use setState to transition directly
    machine.setState(MachineState.READY, "Direct transition to READY");
    assertEquals(MachineState.READY, machine.getState());

    // Valid transitions should work
    machine.setState(MachineState.RUNNING, "Direct transition to RUNNING");
    assertEquals(MachineState.RUNNING, machine.getState());

    // Invalid transitions should be rejected
    assertThrows(
        InvalidMachineStateTransitionException.class,
        () -> machine.setState(MachineState.CREATED, "Invalid transition back to CREATED"));

    // Should be able to transition to ERROR from any active state
    machine.setState(MachineState.ERROR, "Error condition");
    assertEquals(MachineState.ERROR, machine.getState());

    // Cannot transition from DESTROYED state
    machine.setState(MachineState.DESTROYED, "Destroying machine");
    assertEquals(MachineState.DESTROYED, machine.getState());

    assertThrows(
        InvalidMachineStateTransitionException.class,
        () -> machine.setState(MachineState.READY, "Invalid transition from DESTROYED"));
  }
}
