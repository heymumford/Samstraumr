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

package org.s8r.domain.exception;

import org.s8r.domain.component.Component;
import org.s8r.domain.lifecycle.LifecycleState;

/** Exception thrown when an operation is invalid for a component's current state. */
public class InvalidOperationException extends ComponentException {
  private static final long serialVersionUID = 1L;
  private final String operation;
  private String currentState;
  private LifecycleState lifecycleState;

  /** Creates a new InvalidOperationException with a simple message. */
  public InvalidOperationException(String message) {
    super(message);
    this.operation = "unknown";
    this.currentState = "unknown";
    this.lifecycleState = null;
  }

  /** Creates a new InvalidOperationException with component information. */
  public InvalidOperationException(String operation, Component component) {
    super(
        String.format(
            "Cannot perform operation '%s' on component in state: %s",
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
    super(
        String.format(
            "Cannot perform operation '%s' on component %s in state: %s",
            operation, componentId, currentState));
    this.operation = operation;
    this.currentState = currentState;
    this.lifecycleState = null; // Not available
  }

  /** Creates a new InvalidOperationException with a cause. */
  public InvalidOperationException(String operation, Component component, Throwable cause) {
    super(
        String.format(
            "Cannot perform operation '%s' on component in state: %s",
            operation, component.getLifecycleState()),
        cause);
    this.operation = operation;
    this.lifecycleState = component.getLifecycleState();
    this.currentState = this.lifecycleState.name();
  }

  // Getters
  public String getOperation() {
    return operation;
  }

  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  public String getCurrentState() {
    return currentState;
  }
}
