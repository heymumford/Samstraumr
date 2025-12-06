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

package org.s8r.domain.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.s8r.domain.exception.InvalidMachineStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineState;

/** Test class for {@link MachineStateValidator}. */
@DisplayName("Machine State Validator Tests")
class MachineStateValidatorTest {

  private final ComponentId testMachineId = ComponentId.create("TestMachine");

  @Test
  @DisplayName("Valid state transitions should pass validation")
  void validStateTransitionsShouldPass() {
    // Test valid transitions
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateStateTransition(
                testMachineId, MachineState.CREATED, MachineState.READY));

    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateStateTransition(
                testMachineId, MachineState.READY, MachineState.RUNNING));

    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateStateTransition(
                testMachineId, MachineState.RUNNING, MachineState.STOPPED));

    // Same state is always valid (no-op)
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateStateTransition(
                testMachineId, MachineState.RUNNING, MachineState.RUNNING));
  }

  @Test
  @DisplayName("Invalid state transitions should throw exception")
  void invalidStateTransitionsShouldThrow() {
    // Can't go from CREATED directly to RUNNING
    InvalidMachineStateTransitionException exception =
        assertThrows(
            InvalidMachineStateTransitionException.class,
            () ->
                MachineStateValidator.validateStateTransition(
                    testMachineId, MachineState.CREATED, MachineState.RUNNING));

    assertEquals(testMachineId, exception.getMachineId());
    assertEquals(MachineState.CREATED, exception.getFromState());
    assertEquals(MachineState.RUNNING, exception.getToState());

    // Can't go from STOPPED to PAUSED
    exception =
        assertThrows(
            InvalidMachineStateTransitionException.class,
            () ->
                MachineStateValidator.validateStateTransition(
                    testMachineId, MachineState.STOPPED, MachineState.PAUSED));

    assertEquals(MachineState.STOPPED, exception.getFromState());
    assertEquals(MachineState.PAUSED, exception.getToState());

    // Can't go from DESTROYED to any state
    exception =
        assertThrows(
            InvalidMachineStateTransitionException.class,
            () ->
                MachineStateValidator.validateStateTransition(
                    testMachineId, MachineState.DESTROYED, MachineState.READY));

    assertEquals(MachineState.DESTROYED, exception.getFromState());
    assertEquals(MachineState.READY, exception.getToState());
  }

  @ParameterizedTest
  @CsvSource({
    "initialize,CREATED,true",
    "initialize,READY,false",
    "start,READY,true",
    "start,STOPPED,true",
    "start,RUNNING,false",
    "stop,RUNNING,true",
    "stop,READY,false",
    "addComponent,CREATED,true",
    "addComponent,READY,true",
    "addComponent,RUNNING,false",
    "removeComponent,CREATED,true",
    "removeComponent,RUNNING,false",
    "destroy,CREATED,true",
    "destroy,READY,true",
    "destroy,RUNNING,true",
    "destroy,DESTROYED,false"
  })
  @DisplayName("Operation permissions should be correctly validated")
  void operationPermissionsShouldBeCorrectlyValidated(
      String operation, MachineState state, boolean shouldBeAllowed) {

    assertEquals(shouldBeAllowed, MachineStateValidator.isOperationAllowed(operation, state));

    if (shouldBeAllowed) {
      assertDoesNotThrow(
          () -> MachineStateValidator.validateOperationState(testMachineId, operation, state));
    } else {
      InvalidMachineStateTransitionException exception =
          assertThrows(
              InvalidMachineStateTransitionException.class,
              () -> MachineStateValidator.validateOperationState(testMachineId, operation, state));

      assertEquals(testMachineId, exception.getMachineId());
      assertEquals(operation, exception.getOperation());
      assertEquals(state, exception.getFromState());
      assertNotNull(exception.getValidStates());
      assertTrue(exception.getValidStates().length > 0);
    }
  }

  @Test
  @DisplayName("Operations should be validated appropriately")
  void operationsShouldBeValidatedAppropriately() {
    // Test 'initialize' operation is only allowed in CREATED state
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                testMachineId, "initialize", MachineState.CREATED));

    assertThrows(
        InvalidMachineStateTransitionException.class,
        () ->
            MachineStateValidator.validateOperationState(
                testMachineId, "initialize", MachineState.READY));

    // Test 'start' operation is allowed in READY and STOPPED states
    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                testMachineId, "start", MachineState.READY));

    assertDoesNotThrow(
        () ->
            MachineStateValidator.validateOperationState(
                testMachineId, "start", MachineState.STOPPED));

    assertThrows(
        InvalidMachineStateTransitionException.class,
        () ->
            MachineStateValidator.validateOperationState(
                testMachineId, "start", MachineState.RUNNING));
  }

  @Test
  @DisplayName("Valid next states should be correctly reported")
  void validNextStatesShouldBeCorrectlyReported() {
    // From CREATED state
    Set<MachineState> validFromCreated =
        MachineStateValidator.getValidNextStates(MachineState.CREATED);
    assertTrue(validFromCreated.contains(MachineState.READY));
    assertTrue(validFromCreated.contains(MachineState.ERROR));
    assertTrue(validFromCreated.contains(MachineState.DESTROYED));
    assertFalse(validFromCreated.contains(MachineState.RUNNING));

    // From RUNNING state
    Set<MachineState> validFromRunning =
        MachineStateValidator.getValidNextStates(MachineState.RUNNING);
    assertTrue(validFromRunning.contains(MachineState.STOPPED));
    assertTrue(validFromRunning.contains(MachineState.PAUSED));
    assertTrue(validFromRunning.contains(MachineState.ERROR));
    assertTrue(validFromRunning.contains(MachineState.DESTROYED));
    assertFalse(validFromRunning.contains(MachineState.READY));

    // From DESTROYED state (should be empty)
    Set<MachineState> validFromDestroyed =
        MachineStateValidator.getValidNextStates(MachineState.DESTROYED);
    assertTrue(validFromDestroyed.isEmpty());
  }

  @Test
  @DisplayName("Valid states for operations should be correctly reported")
  void validStatesForOperationsShouldBeCorrectlyReported() {
    // For 'initialize' operation
    MachineState[] validForInitialize =
        MachineStateValidator.getValidStatesForOperation("initialize");
    assertEquals(1, validForInitialize.length);
    assertEquals(MachineState.CREATED, validForInitialize[0]);

    // For 'start' operation
    MachineState[] validForStart = MachineStateValidator.getValidStatesForOperation("start");
    assertEquals(2, validForStart.length);
    assertTrue(containsState(validForStart, MachineState.READY));
    assertTrue(containsState(validForStart, MachineState.STOPPED));

    // For 'destroy' operation
    MachineState[] validForDestroy = MachineStateValidator.getValidStatesForOperation("destroy");
    assertEquals(6, validForDestroy.length);
    assertFalse(containsState(validForDestroy, MachineState.DESTROYED));
  }

  private boolean containsState(MachineState[] states, MachineState state) {
    for (MachineState s : states) {
      if (s == state) {
        return true;
      }
    }
    return false;
  }
}
