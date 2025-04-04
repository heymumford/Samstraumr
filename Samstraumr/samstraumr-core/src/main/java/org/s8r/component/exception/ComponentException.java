/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Base exception for component-related errors in the S8r framework
 */

package org.s8r.component.exception;

/**
 * Base exception for component-related errors in the S8r framework.
 * 
 * <p>This exception serves as the base class for all component-specific exceptions,
 * providing a unified approach to error handling in the component model.
 */
public class ComponentException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new ComponentException with the specified detail message.
   *
   * @param message the detail message
   */
  public ComponentException(String message) {
    super(message);
  }

  /**
   * Constructs a new ComponentException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public ComponentException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new ComponentException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public ComponentException(Throwable cause) {
    super(cause);
  }
}