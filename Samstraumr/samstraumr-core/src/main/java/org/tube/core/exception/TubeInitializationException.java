/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Exception thrown during Tube initialization in the Samstraumr framework
 */

package org.tube.core.exception;

/**
 * Exception thrown when a Tube fails to initialize correctly.
 *
 * <p>This exception indicates problems during the creation or initialization phase of a Tube, such
 * as invalid parameters, missing resources, or other initialization failures.
 */
public class TubeInitializationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new TubeInitializationException with the specified detail message.
   *
   * @param message the detail message
   */
  public TubeInitializationException(String message) {
    super(message);
  }

  /**
   * Constructs a new TubeInitializationException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public TubeInitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new TubeInitializationException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public TubeInitializationException(Throwable cause) {
    super(cause);
  }
}
