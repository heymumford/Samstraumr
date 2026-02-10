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
