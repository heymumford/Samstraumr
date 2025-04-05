/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Component state change event for the S8r framework
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
  public ComponentId getComponentId() { return componentId; }
  public LifecycleState getPreviousState() { return previousState; }
  public LifecycleState getNewState() { return newState; }
  public String getTransitionReason() { return transitionReason; }
}
