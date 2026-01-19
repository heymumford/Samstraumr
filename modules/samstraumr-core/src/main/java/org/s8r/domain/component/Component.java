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

package org.s8r.domain.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.InvalidComponentTypeException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.validation.ComponentTypeValidator;

/** Core domain entity representing a Component in the S8r framework. */
public class Component {
  private final ComponentId id;
  private LifecycleState lifecycleState;
  private final String componentType;
  private final List<String> lineage = new ArrayList<>();
  private final List<String> activityLog = new ArrayList<>();
  private final Instant creationTime = Instant.now();
  private final List<DomainEvent> domainEvents = new ArrayList<>();
  private final Map<String, Object> properties = new ConcurrentHashMap<>();

  /**
   * Creates a new Component with the default type.
   *
   * @param id The component ID
   */
  public Component(ComponentId id) {
    this(id, ComponentType.STANDARD.getCode());
  }

  /**
   * Creates a new Component with a specific type.
   *
   * @param id The component ID
   * @param componentType The component type code
   * @throws InvalidComponentTypeException if the component type is invalid
   */
  public Component(ComponentId id, String componentType) {
    this.id = Objects.requireNonNull(id, "Component ID cannot be null");

    // Validate component type
    ComponentTypeValidator.validateComponentType(componentType, id);
    this.componentType = componentType;

    this.lifecycleState = LifecycleState.CONCEPTION;
    this.lineage.add(id.getReason());

    logActivity("Component created with reason: " + id.getReason() + ", type: " + componentType);
    raiseEvent(new ComponentCreatedEvent(id, componentType));
  }

  /**
   * Creates a new Component with the default component type.
   *
   * @param id The component ID
   * @return A new component
   */
  public static Component create(ComponentId id) {
    Component component = new Component(id);
    component.initialize();
    return component;
  }

  /**
   * Creates a new Component with the specified component type.
   *
   * @param id The component ID
   * @param componentType The component type code
   * @return A new component
   * @throws InvalidComponentTypeException if the component type is invalid
   */
  public static Component create(ComponentId id, String componentType) {
    Component component = new Component(id, componentType);
    component.initialize();
    return component;
  }

  /**
   * Creates a new Component with the specified ComponentType enum.
   *
   * @param id The component ID
   * @param componentType The component type
   * @return A new component
   */
  public static Component create(ComponentId id, ComponentType componentType) {
    return create(id, componentType.getCode());
  }

  /** Initialize the component, progressing through early lifecycle states. */
  protected void initialize() {
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
      // Creation & Early Development (Embryonic)
      case CONCEPTION:
        return to == LifecycleState.INITIALIZING;
      case INITIALIZING:
        return to == LifecycleState.CONFIGURING;
      case CONFIGURING:
        return to == LifecycleState.SPECIALIZING;
      case SPECIALIZING:
        return to == LifecycleState.DEVELOPING_FEATURES;
      case DEVELOPING_FEATURES:
        return to == LifecycleState.INITIALIZED || to == LifecycleState.READY;

      // Operational (Post-Embryonic)
      case INITIALIZED:
        return to == LifecycleState.READY || to == LifecycleState.CONFIGURING;
      case READY:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.RUNNING
            || to == LifecycleState.TERMINATING;
      case ACTIVE:
        return to == LifecycleState.READY
            || to == LifecycleState.RUNNING
            || to == LifecycleState.WAITING
            || to == LifecycleState.ADAPTING
            || to == LifecycleState.TRANSFORMING
            || to == LifecycleState.TERMINATING;
      case RUNNING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.READY
            || to == LifecycleState.WAITING
            || to == LifecycleState.ADAPTING
            || to == LifecycleState.TRANSFORMING
            || to == LifecycleState.TERMINATING;
      case WAITING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.RUNNING
            || to == LifecycleState.READY
            || to == LifecycleState.ADAPTING
            || to == LifecycleState.TERMINATING;
      case ADAPTING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.RUNNING
            || to == LifecycleState.READY
            || to == LifecycleState.WAITING
            || to == LifecycleState.STABLE
            || to == LifecycleState.TERMINATING;
      case TRANSFORMING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.READY
            || to == LifecycleState.STABLE
            || to == LifecycleState.SPAWNING
            || to == LifecycleState.TERMINATING;

      // Advanced Stages
      case STABLE:
        return to == LifecycleState.SPAWNING
            || to == LifecycleState.DEGRADED
            || to == LifecycleState.MAINTAINING
            || to == LifecycleState.ACTIVE
            || to == LifecycleState.READY
            || to == LifecycleState.TERMINATING;
      case SPAWNING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.READY
            || to == LifecycleState.STABLE
            || to == LifecycleState.TERMINATING;
      case DEGRADED:
        return to == LifecycleState.MAINTAINING
            || to == LifecycleState.ACTIVE
            || to == LifecycleState.READY
            || to == LifecycleState.TERMINATING;
      case MAINTAINING:
        return to == LifecycleState.ACTIVE
            || to == LifecycleState.STABLE
            || to == LifecycleState.READY
            || to == LifecycleState.TERMINATING;

      // Termination
      case TERMINATING:
        return to == LifecycleState.TERMINATED;
      case TERMINATED:
        return to == LifecycleState.ARCHIVED;
      case ARCHIVED:
        return false; // Cannot transition from archived

      // Default (should not reach)
      default:
        return false;
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

  /**
   * Checks if this component type allows a specific operation.
   *
   * @param operation The operation to check
   * @return true if the operation is allowed, false otherwise
   * @throws InvalidComponentTypeException if the component type is invalid
   */
  public boolean isOperationAllowed(String operation) {
    return ComponentTypeValidator.isAllowedForOperation(componentType, operation);
  }

  /**
   * Validates that this component type allows a specific operation.
   *
   * @param operation The operation to validate
   * @throws InvalidComponentTypeException if the operation is not allowed for this component type
   */
  public void validateOperation(String operation) {
    ComponentTypeValidator.validateComponentTypeForOperation(componentType, id, operation);
  }

  // Getters
  public ComponentId getId() {
    return id;
  }

  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * Gets the component type code.
   *
   * @return The component type code
   */
  public String getComponentType() {
    return componentType;
  }

  /**
   * Gets the component type as an enum value.
   *
   * @return The component type enum value
   * @throws IllegalStateException if the component type is not recognized (should not happen due to
   *     validation)
   */
  public ComponentType getComponentTypeEnum() {
    return ComponentType.fromCode(componentType)
        .orElseThrow(() -> new IllegalStateException("Unknown component type: " + componentType));
  }

  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  public List<String> getActivityLog() {
    return Collections.unmodifiableList(activityLog);
  }

  public Instant getCreationTime() {
    return creationTime;
  }

  public List<DomainEvent> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  // Event handling
  protected void raiseEvent(DomainEvent event) {
    domainEvents.add(event);
  }

  public void clearEvents() {
    domainEvents.clear();
  }

  // Data publishing
  public void publishData(String channel, Map<String, Object> data) {
    logActivity("Publishing data to channel: " + channel);
    raiseEvent(new ComponentDataEvent(id, channel, data));
  }

  public void publishData(String channel, String key, Object value) {
    logActivity("Publishing data to channel: " + channel + " with key: " + key);
    raiseEvent(ComponentDataEvent.createSingleValue(id, channel, key, value));
  }

  // Property management
  /**
   * Gets all properties of this component.
   *
   * @return An unmodifiable map of property names to values
   */
  public Map<String, Object> getProperties() {
    return Collections.unmodifiableMap(properties);
  }

  // Object methods overrides
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((Component) o).id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Component{id="
        + id
        + ", type="
        + componentType
        + ", lifecycleState="
        + lifecycleState
        + ", creationTime="
        + creationTime
        + '}';
  }
}
