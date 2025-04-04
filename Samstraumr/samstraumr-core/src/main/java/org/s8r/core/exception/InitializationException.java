/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Exception for component initialization failures in the S8r framework
 */

package org.s8r.core.exception;

/**
 * Exception thrown when a component fails to initialize correctly.
 *
 * <p>This exception indicates problems during the creation or initialization phase of a component
 * (such as a Tube), including invalid parameters, missing resources, or other initialization
 * failures.
 *
 * <p>This is part of the simplified package structure, replacing the more specific exceptions like
 * TubeInitializationException with a more general, centralized exception.
 */
public class InitializationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new InitializationException with the specified detail message.
   *
   * @param message the detail message
   */
  public InitializationException(String message) {
    super(message);
  }

  /**
   * Constructs a new InitializationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public InitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new InitializationException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public InitializationException(Throwable cause) {
    super(cause);
  }
}
