/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain exception for invalid component operations in the S8r framework
 */

package org.samstraumr.domain.exception;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.lifecycle.LifecycleState;

/** Exception thrown when an operation is invalid for a component's current state. */
public class InvalidOperationException extends ComponentException {
  private static final long serialVersionUID = 1L;
  private final String operation;
  private String currentState;
  private LifecycleState lifecycleState;

  /** Creates a new InvalidOperationException with component information. */
  public InvalidOperationException(String operation, Component component) {
    super(String.format("Cannot perform operation '%s' on component in state: %s",
          operation, component.getLifecycleState()));
    this.operation = operation;
    this.lifecycleState = component.getLifecycleState();
    this.currentState = this.lifecycleState.name();
  }

  /** Creates a new InvalidOperationException with a custom message and lifecycle state. */
  public InvalidOperationException(String message, String operation, LifecycleState currentState) {
    super(message);
    this.operation = operation;
    this.lifecycleState = currentState;
    this.currentState = currentState.name();
  }

  /** Creates a new InvalidOperationException with component ID and state string. */
  public InvalidOperationException(String operation, String componentId, String currentState) {
    super(String.format("Cannot perform operation '%s' on component %s in state: %s",
          operation, componentId, currentState));
    this.operation = operation;
    this.currentState = currentState;
    this.lifecycleState = null; // Not available
  }

  /** Creates a new InvalidOperationException with a cause. */
  public InvalidOperationException(String operation, Component component, Throwable cause) {
    super(String.format("Cannot perform operation '%s' on component in state: %s",
          operation, component.getLifecycleState()), cause);
    this.operation = operation;
    this.lifecycleState = component.getLifecycleState();
    this.currentState = this.lifecycleState.name();
  }

  // Getters
  public String getOperation() { return operation; }
  public LifecycleState getLifecycleState() { return lifecycleState; }
  public String getCurrentState() { return currentState; }
}
