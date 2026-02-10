/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.component.lifecycle;

import java.util.Objects;
import java.util.function.Consumer;

import org.s8r.component.core.State;

/**
 * Manages the lifecycle state transitions of a component.
 *
 * <p>This class encapsulates all state transition logic, validation, and notification. It follows
 * the Single Responsibility Principle by focusing solely on lifecycle management.
 *
 * <p><b>Refactoring Note:</b> Extracted from Component.java as part of Phase 2 God Class
 * decomposition (Martin Fowler refactoring).
 */
public class ComponentLifecycleManager {
  private State state = State.CONCEPTION;
  private final Consumer<StateTransition> transitionListener;

  /**
   * Creates a new lifecycle manager.
   *
   * @param transitionListener Callback invoked on each state transition (for logging/events)
   */
  public ComponentLifecycleManager(Consumer<StateTransition> transitionListener) {
    this.transitionListener =
        Objects.requireNonNull(transitionListener, "Transition listener cannot be null");
  }

  /**
   * Transitions the component to a new state.
   *
   * @param newState The target state
   * @throws IllegalStateException if the component is terminated
   */
  public void transitionToState(State newState) {
    Objects.requireNonNull(newState, "New state cannot be null");

    if (isTerminated()) {
      throw new IllegalStateException("Cannot change state of terminated component");
    }

    if (newState != this.state) {
      State oldState = this.state;
      this.state = newState;

      // Notify listener of transition
      transitionListener.accept(new StateTransition(oldState, newState));
    }
  }

  /**
   * Proceeds through the early lifecycle phases of component development.
   *
   * <p>This method orchestrates the component's progression through embryonic development phases.
   */
  public void proceedThroughEarlyLifecycle() {
    transitionToState(State.CONFIGURING);
    transitionToState(State.SPECIALIZING);
    transitionToState(State.DEVELOPING_FEATURES);
  }

  /**
   * Initializes the component through its initial lifecycle phases.
   *
   * @param onReady Callback to execute when component reaches READY state
   */
  public void initialize(Runnable onReady) {
    transitionToState(State.INITIALIZING);
    proceedThroughEarlyLifecycle();
    transitionToState(State.READY);

    if (onReady != null) {
      onReady.run();
    }
  }

  /**
   * Begins the termination sequence.
   *
   * @return true if termination was initiated, false if already terminated
   */
  public boolean beginTermination() {
    if (isTerminated()) {
      return false;
    }

    transitionToState(State.TERMINATING);
    return true;
  }

  /** Completes the termination sequence. */
  public void completeTermination() {
    if (state == State.TERMINATING) {
      transitionToState(State.TERMINATED);
    }
  }

  // Query methods

  /** Gets the current state. */
  public State getState() {
    return state;
  }

  /** Checks if this component is in a terminated state. */
  public boolean isTerminated() {
    return state == State.TERMINATED || state == State.TERMINATING || state == State.ARCHIVED;
  }

  /** Checks if this component is operational. */
  public boolean isOperational() {
    return state == State.READY || state == State.ACTIVE || state == State.STABLE;
  }

  /** Checks if this component is embryonic. */
  public boolean isEmbryonic() {
    return state == State.CONCEPTION || state == State.INITIALIZING || state == State.CONFIGURING;
  }

  /** Checks if this component is initializing. */
  public boolean isInitializing() {
    return state == State.INITIALIZING;
  }

  /** Checks if this component is configuring. */
  public boolean isConfiguring() {
    return state == State.CONFIGURING;
  }

  /** Checks if this component is ready. */
  public boolean isReady() {
    return state == State.READY;
  }

  /** Checks if this component is active. */
  public boolean isActive() {
    return state == State.ACTIVE;
  }

  /** Checks if this component is in error recovery. */
  public boolean isInErrorRecovery() {
    return state == State.RECOVERING;
  }

  /** Represents a state transition event. */
  public static class StateTransition {
    private final State fromState;
    private final State toState;

    public StateTransition(State fromState, State toState) {
      this.fromState = fromState;
      this.toState = toState;
    }

    public State getFromState() {
      return fromState;
    }

    public State getToState() {
      return toState;
    }

    @Override
    public String toString() {
      return fromState + " -> " + toState;
    }
  }
}
