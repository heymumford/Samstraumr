/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain exception for invalid state transitions in the S8r framework
 */

package org.s8r.domain.exception;

import org.s8r.domain.lifecycle.LifecycleState;

/** Exception thrown when an invalid lifecycle state transition is attempted. */
public class InvalidStateTransitionException extends ComponentException {
  private static final long serialVersionUID = 1L;
  private final LifecycleState fromState;
  private final LifecycleState toState;

  /** Creates a new InvalidStateTransitionException. */
  public InvalidStateTransitionException(LifecycleState fromState, LifecycleState toState) {
    super(String.format("Invalid state transition from %s to %s", fromState, toState));
    this.fromState = fromState;
    this.toState = toState;
  }

  /** Creates a new InvalidStateTransitionException with a custom message. */
  public InvalidStateTransitionException(
      String message, LifecycleState fromState, LifecycleState toState) {
    super(message);
    this.fromState = fromState;
    this.toState = toState;
  }

  // Getters
  public LifecycleState getFromState() { return fromState; }
  public LifecycleState getToState() { return toState; }
}
