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

package org.s8r.domain.exception;

import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineState;

/**
 * Exception thrown when an invalid machine state transition is attempted. This exception provides
 * detailed information about the attempted transition and which states are valid for a specific
 * operation.
 */
public class InvalidMachineStateTransitionException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId machineId;
  private final MachineState fromState;
  private final MachineState toState;
  private final String operation;
  private final MachineState[] validStates;

  /**
   * Creates a new InvalidMachineStateTransitionException for a direct state transition.
   *
   * @param machineId The ID of the machine
   * @param fromState The current state
   * @param toState The target state
   */
  public InvalidMachineStateTransitionException(
      ComponentId machineId, MachineState fromState, MachineState toState) {

    super(
        String.format(
            "Invalid machine state transition from %s to %s for machine %s",
            fromState, toState, machineId.getShortId()));

    this.machineId = machineId;
    this.fromState = fromState;
    this.toState = toState;
    this.operation = "transitionTo";
    this.validStates = null;
  }

  /**
   * Creates a new InvalidMachineStateTransitionException for an operation requiring specific
   * states.
   *
   * @param machineId The ID of the machine
   * @param operation The operation being attempted
   * @param currentState The current state of the machine
   * @param validStates The states in which the operation is allowed
   */
  public InvalidMachineStateTransitionException(
      ComponentId machineId,
      String operation,
      MachineState currentState,
      MachineState... validStates) {

    super(buildMessage(machineId, operation, currentState, validStates));

    this.machineId = machineId;
    this.fromState = currentState;
    this.toState = null;
    this.operation = operation;
    this.validStates = validStates;
  }

  /**
   * Builds an error message for an invalid operation attempt based on current state.
   *
   * @param machineId The ID of the machine
   * @param operation The operation being attempted
   * @param currentState The current state of the machine
   * @param validStates The states in which the operation is allowed
   * @return A formatted error message
   */
  private static String buildMessage(
      ComponentId machineId,
      String operation,
      MachineState currentState,
      MachineState[] validStates) {

    StringBuilder message = new StringBuilder();
    message.append(
        String.format(
            "Cannot perform operation '%s' on machine %s in state %s. ",
            operation, machineId.getShortId(), currentState));

    if (validStates != null && validStates.length > 0) {
      message.append("Valid states for this operation are: ");

      for (int i = 0; i < validStates.length; i++) {
        message.append(validStates[i]);
        if (i < validStates.length - 1) {
          message.append(", ");
        }
      }
    }

    return message.toString();
  }

  /**
   * Gets the ID of the machine where the invalid transition was attempted.
   *
   * @return The machine ID
   */
  public ComponentId getMachineId() {
    return machineId;
  }

  /**
   * Gets the current state of the machine when the transition was attempted.
   *
   * @return The current state
   */
  public MachineState getFromState() {
    return fromState;
  }

  /**
   * Gets the target state of the attempted transition (if applicable).
   *
   * @return The target state, or null if this exception is for an operation
   */
  public MachineState getToState() {
    return toState;
  }

  /**
   * Gets the operation that was attempted.
   *
   * @return The operation name
   */
  public String getOperation() {
    return operation;
  }

  /**
   * Gets the valid states for the attempted operation (if applicable).
   *
   * @return The valid states, or null if this exception is for a direct transition
   */
  public MachineState[] getValidStates() {
    return validStates != null ? validStates.clone() : null;
  }
}
