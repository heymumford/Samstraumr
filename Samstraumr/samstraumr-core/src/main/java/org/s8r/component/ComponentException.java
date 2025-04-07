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
