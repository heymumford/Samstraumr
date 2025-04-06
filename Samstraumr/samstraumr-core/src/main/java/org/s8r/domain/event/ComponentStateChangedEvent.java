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

package org.s8r.domain.event;

import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/** Event raised when a component's lifecycle state changes. */
public class ComponentStateChangedEvent extends DomainEvent {
  private final ComponentId componentId;
  private final LifecycleState previousState;
  private final LifecycleState newState;
  private final String transitionReason;

  /** Creates a new component state changed event. */
  public ComponentStateChangedEvent(
      ComponentId componentId,
      LifecycleState previousState,
      LifecycleState newState,
      String transitionReason) {
    this.componentId = componentId;
    this.previousState = previousState;
    this.newState = newState;
    this.transitionReason = transitionReason;
  }

  // Getters
  public ComponentId getComponentId() {
    return componentId;
  }

  public LifecycleState getPreviousState() {
    return previousState;
  }

  public LifecycleState getNewState() {
    return newState;
  }

  public String getTransitionReason() {
    return transitionReason;
  }
}
