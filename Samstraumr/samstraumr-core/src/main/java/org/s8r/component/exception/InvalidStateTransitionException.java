/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.component.exception;

import org.s8r.core.tube.LifecycleState;

/**
 * Exception thrown when a component attempts an invalid state transition.
 *
 * <p>This exception indicates that a state transition was requested that doesn't follow the allowed
 * path through the component lifecycle states.
 */
public class InvalidStateTransitionException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final LifecycleState fromState;
  private final LifecycleState toState;

  /**
   * Creates a new InvalidStateTransitionException with the specified from and to states.
   *
   * @param fromState The current state
   * @param toState The target state that can't be transitioned to
   */
  public InvalidStateTransitionException(LifecycleState fromState, LifecycleState toState) {
    super(String.format("Invalid state transition from %s to %s", fromState, toState));
    this.fromState = fromState;
    this.toState = toState;
  }

  /**
   * Gets the current state.
   *
   * @return The current state
   */
  public LifecycleState getFromState() {
    return fromState;
  }

  /**
   * Gets the target state that can't be transitioned to.
   *
   * @return The target state
   */
  public LifecycleState getToState() {
    return toState;
  }
}
