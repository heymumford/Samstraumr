/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.component.exception;

/**
 * Base exception class for component-related exceptions.
 *
 * <p>This exception serves as the parent class for all exceptions related to component operations
 * in the S8r framework.
 */
public class ComponentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new ComponentException with the specified error message.
   *
   * @param message The error message
   */
  public ComponentException(String message) {
    super(message);
  }

  /**
   * Creates a new ComponentException with the specified error message and cause.
   *
   * @param message The error message
   * @param cause The cause of the exception
   */
  public ComponentException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new ComponentException with the specified cause.
   *
   * @param cause The cause of the exception
   */
  public ComponentException(Throwable cause) {
    super(cause);
  }
}
