/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Exception specific to machine operations in the S8r framework
 */
package org.s8r.component.machine;

import org.s8r.component.exception.ComponentException;

/**
 * Exception thrown for errors related to machine operations.
 *
 * <p>This exception extends ComponentException and is specifically used for errors that occur
 * during machine creation, composite management, or system-level operations within a machine.
 */
public class MachineException extends ComponentException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new MachineException with the specified detail message.
   *
   * @param message the detail message
   */
  public MachineException(String message) {
    super(message);
  }

  /**
   * Constructs a new MachineException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public MachineException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new MachineException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public MachineException(Throwable cause) {
    super(cause);
  }
}
