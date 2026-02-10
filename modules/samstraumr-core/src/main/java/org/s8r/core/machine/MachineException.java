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

package org.s8r.core.machine;

/**
 * Exception thrown when a machine encounters an error condition.
 *
 * <p>This exception is used to indicate various error conditions that can occur when working with
 * machines, such as invalid state transitions, component not found errors, or operational failures.
 */
public class MachineException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new machine exception with the specified message.
   *
   * @param message The detail message
   */
  public MachineException(String message) {
    super(message);
  }

  /**
   * Constructs a new machine exception with the specified message and cause.
   *
   * @param message The detail message
   * @param cause The cause of the exception
   */
  public MachineException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a machine exception for an invalid operation.
   *
   * @param operation The operation that was attempted
   * @param machineId The machine ID
   * @param state The current state of the machine
   * @return A new machine exception
   */
  public static MachineException invalidOperation(
      String operation, String machineId, MachineState state) {
    return new MachineException(
        String.format(
            "Cannot perform operation '%s' on machine '%s' in state '%s'",
            operation, machineId, state));
  }

  /**
   * Creates a machine exception for a component not found error.
   *
   * @param machineId The machine ID
   * @param componentId The component ID that wasn't found
   * @return A new machine exception
   */
  public static MachineException componentNotFound(String machineId, String componentId) {
    return new MachineException(
        String.format("Component '%s' not found in machine '%s'", componentId, machineId));
  }

  /**
   * Creates a machine exception for an unsupported operation.
   *
   * @param operation The unsupported operation
   * @param machineId The machine ID
   * @return A new machine exception
   */
  public static MachineException unsupportedOperation(String operation, String machineId) {
    return new MachineException(
        String.format("Operation '%s' is not supported for machine '%s'", operation, machineId));
  }
}
