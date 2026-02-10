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

package org.s8r.component;

/**
 * Interface for listeners that want to be notified of component state transitions.
 *
 * <p>State transition listeners allow components to notify interested parties when their lifecycle
 * state changes, enabling reactive behavior in response to lifecycle events.
 *
 * <p>To use a state transition listener:
 *
 * <ol>
 *   <li>Implement this interface in your listener class
 *   <li>Register the listener with a component using component.addStateTransitionListener()
 *   <li>Handle state changes in the onStateTransition method
 * </ol>
 */
@FunctionalInterface
public interface StateTransitionListener {

  /**
   * Called when a component's state changes.
   *
   * @param component The component whose state changed
   * @param oldState The previous state
   * @param newState The new state
   */
  void onStateTransition(Component component, State oldState, State newState);

  /**
   * Checks whether this listener is interested in transitions to the given state. By default,
   * returns true for all states.
   *
   * @param state The state to check
   * @return true if this listener is interested in transitions to the given state
   */
  default boolean isInterestedInState(State state) {
    return true;
  }

  /**
   * Called when a component is terminated and the listener is being removed. This gives the
   * listener a chance to clean up any resources.
   */
  default void onTermination() {
    // Default implementation is a no-op
  }
}
