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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.s8r.domain.exception.InvalidMachineStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineOperation;
import org.s8r.domain.machine.MachineState;

/**
 * Validator for machine state transitions. This utility class provides validation for machine state
 * transitions and operations.
 */
public class MachineStateValidator {
  // Map from current state to set of valid next states
  private static final Map<MachineState, Set<MachineState>> VALID_TRANSITIONS =
      createValidTransitionsMap();

  // Map from operation to array of valid states for that operation
  private static final Map<MachineOperation, MachineState[]> VALID_STATES_BY_OPERATION =
      createValidStatesByOperationMap();

  /**
   * Validates a state transition from one state to another.
   *
   * @param machineId The ID of the machine
   * @param fromState The current state
   * @param toState The target state
   * @throws InvalidMachineStateTransitionException if the transition is invalid
   */
  public static void validateStateTransition(
      ComponentId machineId, MachineState fromState, MachineState toState) {

    // Allow transition to same state (no-op)
    if (fromState == toState) {
      return;
    }

    // Check if transition is valid
    Set<MachineState> validTargetStates = VALID_TRANSITIONS.get(fromState);
    if (validTargetStates == null || !validTargetStates.contains(toState)) {
      throw new InvalidMachineStateTransitionException(machineId, fromState, toState);
    }
  }

  /**
   * Validates that a machine is in a valid state for a specific operation.
   *
   * @param machineId The ID of the machine
   * @param operation The operation to perform
   * @param currentState The current state of the machine
   * @throws InvalidMachineStateTransitionException if the operation is not allowed in the current
   *     state, or if the operation is not defined
   */
  public static void validateOperationState(
      ComponentId machineId, MachineOperation operation, MachineState currentState) {

    MachineState[] validStates = VALID_STATES_BY_OPERATION.get(operation);

    if (validStates == null) {
      // Throw exception for undefined operation instead of silently falling back
      throw new InvalidMachineStateTransitionException(
          machineId,
          "Undefined operation: " + operation.getOperationName(),
          currentState,
          new MachineState[0]);
    }

    // Check if current state is in the valid states
    for (MachineState validState : validStates) {
      if (currentState == validState) {
        return;
      }
    }

    // If we get here, the operation is not allowed in the current state
    throw new InvalidMachineStateTransitionException(
        machineId, operation.getOperationName(), currentState, validStates);
  }

  /**
   * Checks if a state transition is valid without throwing an exception.
   *
   * @param fromState The current state
   * @param toState The target state
   * @return true if the transition is valid, false otherwise
   */
  public static boolean isValidStateTransition(MachineState fromState, MachineState toState) {
    // Allow transition to same state (no-op)
    if (fromState == toState) {
      return true;
    }

    // Check if transition is valid
    Set<MachineState> validTargetStates = VALID_TRANSITIONS.get(fromState);
    return validTargetStates != null && validTargetStates.contains(toState);
  }

  /**
   * Checks if an operation is allowed in a specific state without throwing an exception.
   *
   * @param operation The operation to check
   * @param currentState The current state to check
   * @return true if the operation is allowed, false if operation is not defined or not allowed in
   *     current state
   * @throws IllegalArgumentException if the operation is not defined
   */
  public static boolean isOperationAllowed(MachineOperation operation, MachineState currentState) {
    MachineState[] validStates = VALID_STATES_BY_OPERATION.get(operation);

    if (validStates == null) {
      // Throw exception for undefined operation instead of silently falling back
      throw new IllegalArgumentException("Operation not defined: " + operation.getOperationName());
    }

    // Check if current state is in the valid states
    for (MachineState validState : validStates) {
      if (currentState == validState) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets valid next states for a given current state.
   *
   * @param currentState The current state
   * @return Set of valid next states, or empty set if no transitions are allowed
   */
  public static Set<MachineState> getValidNextStates(MachineState currentState) {
    Set<MachineState> validStates = VALID_TRANSITIONS.get(currentState);
    return validStates != null ? Collections.unmodifiableSet(validStates) : Collections.emptySet();
  }

  /**
   * Gets the states in which a specific operation is allowed.
   *
   * @param operation The operation to check
   * @return Array of states in which the operation is allowed
   * @throws IllegalArgumentException if the operation is not defined
   */
  public static MachineState[] getValidStatesForOperation(MachineOperation operation) {
    MachineState[] validStates = VALID_STATES_BY_OPERATION.get(operation);
    if (validStates == null) {
      // Throw exception for undefined operation instead of silently falling back
      throw new IllegalArgumentException("Operation not defined: " + operation.getOperationName());
    }
    return validStates.clone();
  }

  /**
   * Creates the map of valid state transitions.
   *
   * @return Map from current state to set of valid next states
   */
  private static Map<MachineState, Set<MachineState>> createValidTransitionsMap() {
    Map<MachineState, Set<MachineState>> map = new EnumMap<>(MachineState.class);

    // Define valid transitions from each state

    // From CREATED
    Set<MachineState> fromCreated = new HashSet<>();
    fromCreated.add(MachineState.READY);
    fromCreated.add(MachineState.ERROR);
    fromCreated.add(MachineState.DESTROYED);
    map.put(MachineState.CREATED, fromCreated);

    // From READY
    Set<MachineState> fromReady = new HashSet<>();
    fromReady.add(MachineState.RUNNING);
    fromReady.add(MachineState.ERROR);
    fromReady.add(MachineState.DESTROYED);
    map.put(MachineState.READY, fromReady);

    // From RUNNING
    Set<MachineState> fromRunning = new HashSet<>();
    fromRunning.add(MachineState.STOPPED);
    fromRunning.add(MachineState.PAUSED);
    fromRunning.add(MachineState.ERROR);
    fromRunning.add(MachineState.DESTROYED);
    map.put(MachineState.RUNNING, fromRunning);

    // From STOPPED
    Set<MachineState> fromStopped = new HashSet<>();
    fromStopped.add(MachineState.RUNNING);
    fromStopped.add(MachineState.ERROR);
    fromStopped.add(MachineState.DESTROYED);
    map.put(MachineState.STOPPED, fromStopped);

    // From PAUSED
    Set<MachineState> fromPaused = new HashSet<>();
    fromPaused.add(MachineState.RUNNING);
    fromPaused.add(MachineState.ERROR);
    fromPaused.add(MachineState.DESTROYED);
    map.put(MachineState.PAUSED, fromPaused);

    // From ERROR
    Set<MachineState> fromError = new HashSet<>();
    fromError.add(MachineState.READY);
    fromError.add(MachineState.DESTROYED);
    map.put(MachineState.ERROR, fromError);

    // From DESTROYED (terminal state)
    map.put(MachineState.DESTROYED, Collections.emptySet());

    return Collections.unmodifiableMap(map);
  }

  /**
   * Creates the map of valid states for each operation.
   *
   * @return Map from operation to array of valid states
   */
  private static Map<MachineOperation, MachineState[]> createValidStatesByOperationMap() {
    Map<MachineOperation, MachineState[]> map = new EnumMap<>(MachineOperation.class);

    // Define valid states for each operation
    map.put(MachineOperation.INITIALIZE, new MachineState[] {MachineState.CREATED});
    map.put(MachineOperation.START, new MachineState[] {MachineState.READY, MachineState.STOPPED});
    map.put(MachineOperation.STOP, new MachineState[] {MachineState.RUNNING});
    map.put(MachineOperation.PAUSE, new MachineState[] {MachineState.RUNNING});
    map.put(MachineOperation.RESUME, new MachineState[] {MachineState.PAUSED});
    map.put(MachineOperation.RESET, new MachineState[] {MachineState.ERROR, MachineState.STOPPED});
    map.put(
        MachineOperation.SET_ERROR_STATE,
        new MachineState[] {
          MachineState.CREATED,
          MachineState.READY,
          MachineState.RUNNING,
          MachineState.PAUSED,
          MachineState.STOPPED
        });
    map.put(MachineOperation.RESET_FROM_ERROR, new MachineState[] {MachineState.ERROR});
    map.put(
        MachineOperation.DESTROY,
        new MachineState[] {
          MachineState.CREATED, MachineState.READY, MachineState.RUNNING,
          MachineState.STOPPED, MachineState.PAUSED, MachineState.ERROR
        });

    // Operations that modify machine components
    MachineState[] modifiableStates = getModifiableStates();
    map.put(MachineOperation.ADD_COMPONENT, modifiableStates);
    map.put(MachineOperation.REMOVE_COMPONENT, modifiableStates);
    map.put(MachineOperation.SET_VERSION, modifiableStates);

    return Collections.unmodifiableMap(map);
  }

  /**
   * Gets the states in which a machine is generally modifiable.
   *
   * @return Array of modifiable states
   */
  private static MachineState[] getModifiableStates() {
    return new MachineState[] {MachineState.CREATED, MachineState.READY, MachineState.STOPPED};
  }
}
