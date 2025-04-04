/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain exception for Component operations in the S8r framework
 */

package org.samstraumr.domain.exception;

/** Base exception class for component-related errors in the domain layer. */
public class ComponentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** Creates a new ComponentException with the specified message. */
  public ComponentException(String message) {
    super(message);
  }

  /** Creates a new ComponentException with the specified message and cause. */
  public ComponentException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Creates a new ComponentException with the specified cause. */
  public ComponentException(Throwable cause) {
    super(cause);
  }
}
