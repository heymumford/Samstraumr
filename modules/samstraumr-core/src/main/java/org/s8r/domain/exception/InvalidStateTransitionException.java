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
  public LifecycleState getFromState() {
    return fromState;
  }

  public LifecycleState getToState() {
    return toState;
  }
}
