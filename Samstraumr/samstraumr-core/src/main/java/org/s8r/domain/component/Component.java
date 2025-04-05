/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Core domain implementation of Component in the S8r framework
 */

package org.s8r.domain.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/** Core domain entity representing a Component in the S8r framework. */
public class Component {
  private final ComponentId id;
  private LifecycleState lifecycleState;
  private final List<String> lineage = new ArrayList<>();
  private final List<String> activityLog = new ArrayList<>();
  private final Instant creationTime = Instant.now();
  private final List<DomainEvent> domainEvents = new ArrayList<>();

  protected Component(ComponentId id) {
    this.id = Objects.requireNonNull(id, "Component ID cannot be null");
    this.lifecycleState = LifecycleState.CONCEPTION;
    this.lineage.add(id.getReason());
    
    logActivity("Component created with reason: " + id.getReason());
    raiseEvent(new ComponentCreatedEvent(id, this.getClass().getSimpleName()));
  }

  /** Creates a new Component. */
  public static Component create(ComponentId id) {
    Component component = new Component(id);
    component.initialize();
    return component;
  }

  /** Initialize the component, progressing through early lifecycle states. */
  private void initialize() {
    logActivity("Beginning initialization");
    
    // Progress through initial lifecycle states
    transitionTo(LifecycleState.INITIALIZING);
    transitionTo(LifecycleState.CONFIGURING);
    transitionTo(LifecycleState.SPECIALIZING);
    transitionTo(LifecycleState.DEVELOPING_FEATURES);
    transitionTo(LifecycleState.READY);
    
    logActivity("Initialization complete");
  }

  /** Transitions the component to a new state. */
  public void transitionTo(LifecycleState newState) {
    if (!isValidTransition(lifecycleState, newState)) {
      throw new InvalidStateTransitionException(lifecycleState, newState);
    }

    LifecycleState oldState = lifecycleState;
    lifecycleState = newState;
    logActivity("State transition: " + oldState + " -> " + newState);
    raiseEvent(new ComponentStateChangedEvent(id, oldState, newState, "Explicit state transition"));
  }

  /** Checks if a transition from one state to another is valid. */
  private boolean isValidTransition(LifecycleState from, LifecycleState to) {
    if (from == to) return true; // Allow self-transitions

    switch (from) {
      case CONCEPTION: return to == LifecycleState.INITIALIZING;
      case INITIALIZING: return to == LifecycleState.CONFIGURING;
      case CONFIGURING: return to == LifecycleState.SPECIALIZING;
      case SPECIALIZING: return to == LifecycleState.DEVELOPING_FEATURES;
      case DEVELOPING_FEATURES: return to == LifecycleState.READY;
      case READY: return to == LifecycleState.ACTIVE || to == LifecycleState.TERMINATING;
      case ACTIVE: return to == LifecycleState.READY || to == LifecycleState.TERMINATING;
      case TERMINATING: return to == LifecycleState.TERMINATED;
      case TERMINATED: return false; // Cannot transition from terminated
      default: return false;
    }
  }

  /** Activates the component. */
  public void activate() {
    if (lifecycleState != LifecycleState.READY) {
      throw new InvalidOperationException("activate", this);
    }
    transitionTo(LifecycleState.ACTIVE);
    logActivity("Component activated");
  }

  /** Deactivates the component, returning it to READY state. */
  public void deactivate() {
    if (lifecycleState != LifecycleState.ACTIVE) {
      throw new InvalidOperationException("deactivate", this);
    }
    transitionTo(LifecycleState.READY);
    logActivity("Component deactivated");
  }

  /** Terminates the component, releasing resources and preventing further use. */
  public void terminate() {
    if (lifecycleState == LifecycleState.TERMINATED) return;

    transitionTo(LifecycleState.TERMINATING);
    logActivity("Beginning termination process");
    
    preserveKnowledge();
    releaseResources();
    
    transitionTo(LifecycleState.TERMINATED);
    logActivity("Component terminated at: " + Instant.now());
  }

  /** Preserves knowledge before termination. */
  private void preserveKnowledge() {
    logActivity("Preserving knowledge before termination");
    // Domain logic for knowledge preservation
  }

  /** Releases resources during termination. */
  private void releaseResources() {
    logActivity("Releasing allocated resources");
    // Domain logic for resource cleanup
  }

  /** Adds an entry to the component's lineage. */
  public void addToLineage(String entry) {
    if (entry != null && !entry.isEmpty()) {
      lineage.add(entry);
      logActivity("Added to lineage: " + entry);
    }
  }

  /** Logs component activity. */
  protected void logActivity(String activity) {
    activityLog.add(Instant.now() + ": " + activity);
  }

  // Getters
  public ComponentId getId() { return id; }
  public LifecycleState getLifecycleState() { return lifecycleState; }
  public List<String> getLineage() { return Collections.unmodifiableList(lineage); }
  public List<String> getActivityLog() { return Collections.unmodifiableList(activityLog); }
  public Instant getCreationTime() { return creationTime; }
  public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }

  // Event handling
  protected void raiseEvent(DomainEvent event) { domainEvents.add(event); }
  public void clearEvents() { domainEvents.clear(); }

  // Data publishing
  public void publishData(String channel, Map<String, Object> data) {
    logActivity("Publishing data to channel: " + channel);
    raiseEvent(new ComponentDataEvent(id, channel, data));
  }

  public void publishData(String channel, String key, Object value) {
    logActivity("Publishing data to channel: " + channel + " with key: " + key);
    raiseEvent(ComponentDataEvent.createSingleValue(id, channel, key, value));
  }

  // Object methods overrides
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((Component) o).id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }

  @Override
  public String toString() {
    return "Component{id=" + id + ", lifecycleState=" + lifecycleState + 
           ", creationTime=" + creationTime + '}';
  }
}
