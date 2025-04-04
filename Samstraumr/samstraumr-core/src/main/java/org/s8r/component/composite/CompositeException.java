/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Exception specific to composite operations in the S8r framework
 */

package org.s8r.component.composite;

import org.s8r.component.exception.ComponentException;

/**
 * Exception thrown for errors related to composite operations.
 * 
 * <p>This exception extends ComponentException and is specifically used for
 * errors that occur during composite creation, component connection, or data
 * processing through composites.
 */
public class CompositeException extends ComponentException {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new CompositeException with the specified detail message.
   *
   * @param message the detail message
   */
  public CompositeException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new CompositeException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public CompositeException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructs a new CompositeException with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public CompositeException(Throwable cause) {
    super(cause);
  }
}