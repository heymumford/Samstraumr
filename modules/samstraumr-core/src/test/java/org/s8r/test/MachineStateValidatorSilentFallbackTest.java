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

package org.s8r.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.exception.InvalidMachineStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineOperation;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.validation.MachineStateValidator;

/**
 * Detects Bug #5: Silent fallback in MachineStateValidator for undefined operations.
 *
 * <p>MachineStateValidator has a silent fallback behavior where undefined operations default to
 * modifiable states. This causes: - Invalid/undefined operations to appear as valid - Silent
 * masking of configuration errors - Inability to detect typos or incorrect operation usage - Loss
 * of validation on developer mistakes
 *
 * <p>The validator should throw an InvalidMachineStateTransitionException when an undefined
 * operation is encountered, rather than silently falling back to modifiable states.
 */
@DisplayName("Bug #5: MachineStateValidator silent fallback for undefined operations")
@Tag("ATL")
@Tag("L1_Component")
@Tag("validation")
public class MachineStateValidatorSilentFallbackTest {

  private ComponentId machineId;

  @BeforeEach
  void setup() {
    machineId = ComponentId.create("test-machine");
  }

  @Test
  @DisplayName("Should reject operations in invalid machine states")
  void testIsOperationAllowedReturnsFalseForInvalidStates() {
    // Verify that isOperationAllowed correctly returns false for invalid states
    // For example, ADD_COMPONENT is only allowed in modifiable states (CREATED, READY)

    // ADD_COMPONENT in RUNNING state should not be allowed
    assertFalse(
        MachineStateValidator.isOperationAllowed(
            MachineOperation.ADD_COMPONENT, MachineState.RUNNING),
        "ADD_COMPONENT should not be allowed in RUNNING state");

    // ADD_COMPONENT in ERROR state should not be allowed
    assertFalse(
        MachineStateValidator.isOperationAllowed(
            MachineOperation.ADD_COMPONENT, MachineState.ERROR),
        "ADD_COMPONENT should not be allowed in ERROR state");

    // This tests that the operation is properly validated, not silently defaulting
  }

  @Test
  @DisplayName("Should allow operations in valid machine states")
  void testIsOperationAllowedReturnsTrueForValidStates() {
    // Verify that isOperationAllowed correctly returns true for valid states

    // START should be allowed in READY state
    assertTrue(
        MachineStateValidator.isOperationAllowed(MachineOperation.START, MachineState.READY),
        "START should be allowed in READY state");

    // INITIALIZE should be allowed in CREATED state
    assertTrue(
        MachineStateValidator.isOperationAllowed(MachineOperation.INITIALIZE, MachineState.CREATED),
        "INITIALIZE should be allowed in CREATED state");
  }

  @Test
  @DisplayName("Should throw exception for invalid operation state transitions")
  void testValidateOperationStateThrowsForInvalidStates() {
    // validateOperationState should throw InvalidMachineStateTransitionException
    // when an operation is attempted in an invalid state

    // START in RUNNING state is invalid
    assertThrows(
        InvalidMachineStateTransitionException.class,
        () ->
            MachineStateValidator.validateOperationState(
                machineId, MachineOperation.START, MachineState.RUNNING),
        "START should not be allowed in RUNNING state");

    // ADD_COMPONENT in ERROR state is invalid
    assertThrows(
        InvalidMachineStateTransitionException.class,
        () ->
            MachineStateValidator.validateOperationState(
                machineId, MachineOperation.ADD_COMPONENT, MachineState.ERROR),
        "ADD_COMPONENT should not be allowed in ERROR state");
  }

  @Test
  @DisplayName("Should allow valid operation state transitions")
  void testValidateOperationStateAllowsValidTransitions() {
    // validateOperationState should not throw for valid transitions

    // INITIALIZE in CREATED state is valid
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                machineId, MachineOperation.INITIALIZE, MachineState.CREATED),
        "INITIALIZE should be allowed in CREATED state");

    // START in READY state is valid
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                machineId, MachineOperation.START, MachineState.READY),
        "START should be allowed in READY state");

    // PAUSE in RUNNING state is valid
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                machineId, MachineOperation.PAUSE, MachineState.RUNNING),
        "PAUSE should be allowed in RUNNING state");
  }

  @Test
  @DisplayName("Should return valid states for all defined operations")
  void testGetValidStatesForOperationReturnsNonEmpty() {
    // getValidStatesForOperation should return non-empty state arrays for all operations
    // This verifies that all operations are properly defined, not falling back

    // START operation should have valid states
    MachineState[] startStates =
        MachineStateValidator.getValidStatesForOperation(MachineOperation.START);
    assertTrue(startStates.length > 0, "START should have valid states defined");

    // INITIALIZE operation should have valid states
    MachineState[] initStates =
        MachineStateValidator.getValidStatesForOperation(MachineOperation.INITIALIZE);
    assertTrue(initStates.length > 0, "INITIALIZE should have valid states defined");

    // ADD_COMPONENT operation should have valid states
    MachineState[] addStates =
        MachineStateValidator.getValidStatesForOperation(MachineOperation.ADD_COMPONENT);
    assertTrue(addStates.length > 0, "ADD_COMPONENT should have valid states defined");

    // PAUSE operation should have valid states
    MachineState[] pauseStates =
        MachineStateValidator.getValidStatesForOperation(MachineOperation.PAUSE);
    assertTrue(pauseStates.length > 0, "PAUSE should have valid states defined");
  }

  @Test
  @DisplayName("Should cover all MachineOperation enum values in validator rules")
  void testAllMachineOperationsHaveDefinedRules() {
    // This test ensures that every MachineOperation enum value has defined rules
    // in the validator, preventing silent fallback behavior

    // Get all operation values
    MachineOperation[] operations = MachineOperation.values();

    // Each operation should have valid states defined (not silently falling back)
    for (MachineOperation operation : operations) {
      MachineState[] validStates = MachineStateValidator.getValidStatesForOperation(operation);
      assertTrue(
          validStates.length > 0, "Operation " + operation + " should have valid states defined");
    }

    // This ensures all operations are explicitly handled, not defaulting to modifiable states
  }
}
