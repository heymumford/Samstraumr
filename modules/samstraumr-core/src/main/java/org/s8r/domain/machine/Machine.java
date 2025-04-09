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

package org.s8r.domain.machine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.InvalidMachineStateTransitionException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.validation.MachineStateValidator;

/** Represents a machine that coordinates multiple composite components. */
public class Machine {
  private final ComponentId id;
  private final MachineType type;
  private final String name;
  private final String description;
  private final Instant creationTime = Instant.now();
  private final Map<String, CompositeComponent> components = new HashMap<>();
  private final List<String> activityLog = new ArrayList<>();
  private final List<DomainEvent> domainEvents = new ArrayList<>();
  private MachineState state;
  private String version;

  private Machine(
      ComponentId id, MachineType type, String name, String description, String version) {
    this.id = Objects.requireNonNull(id, "Machine ID cannot be null");
    this.type = Objects.requireNonNull(type, "Machine type cannot be null");
    this.name = Objects.requireNonNull(name, "Machine name cannot be null");
    this.description = description != null ? description : "";
    this.version = version != null ? version : "1.0.0";
    this.state = MachineState.CREATED;

    logActivity("Machine created: " + name + " (" + type + ")");
  }

  /** Creates a new Machine. */
  public static Machine create(
      ComponentId id, MachineType type, String name, String description, String version) {
    return new Machine(id, type, name, description, version);
  }

  /** 
   * Adds a composite component to this machine.
   *
   * @param component The composite component to add
   * @throws InvalidOperationException if the operation is not allowed in the current state
   * @throws IllegalArgumentException if the component is null
   * @throws InvalidCompositeTypeException if the component is not a valid composite
   */
  public void addComponent(CompositeComponent component) {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "addComponent", state);
    
    // Validate that the component is a valid composite component
    org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);

    components.put(component.getId().getIdString(), component);
    logActivity("Added component: " + component.getId().getShortId());
  }

  /**
   * Removes a component from this machine.
   *
   * @param componentId The ID of the component to remove
   * @throws InvalidOperationException if the operation is not allowed in the current state
   * @throws ComponentNotFoundException if the component is not found
   */
  public void removeComponent(ComponentId componentId) {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "removeComponent", state);
    
    // Validate that the component exists
    if (componentId == null) {
      throw new IllegalArgumentException("Component ID cannot be null");
    }

    String componentIdStr = componentId.getIdString();
    CompositeComponent removed = components.remove(componentIdStr);

    if (removed == null) {
      throw new ComponentNotFoundException(componentId);
    }

    logActivity("Removed component: " + componentId.getShortId());
  }

  /**
   * Gets a component by its ID.
   *
   * @param componentId The ID of the component to retrieve
   * @return An Optional containing the component if found, or empty if not found
   * @throws IllegalArgumentException if the component ID is null
   */
  public Optional<CompositeComponent> getComponent(ComponentId componentId) {
    if (componentId == null) {
      throw new IllegalArgumentException("Component ID cannot be null");
    }
    
    return Optional.ofNullable(components.get(componentId.getIdString()));
  }

  /** Gets all components in this machine. */
  public List<CompositeComponent> getComponents() {
    return Collections.unmodifiableList(new ArrayList<>(components.values()));
  }

  /**
   * Initializes this machine, preparing it for operation.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void initialize() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "initialize", state);

    logActivity("Initializing machine");

    // Initialize all components
    for (CompositeComponent component : components.values()) {
      // Validate component before initialization (redundant here but included for completeness)
      org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
      
      if (component.getLifecycleState() == LifecycleState.READY) {
        logActivity("Component already initialized: " + component.getId().getShortId());
      } else {
        logActivity("Initializing component: " + component.getId().getShortId());
      }
    }

    // Transition to READY state using our new setState method
    setState(MachineState.READY, "Initialization completed");
  }

  /**
   * Starts this machine, activating all components.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void start() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "start", state);

    logActivity("Starting machine");

    // Activate all components
    for (CompositeComponent component : components.values()) {
      try {
        // Validate component before activation
        org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
        
        component.activate();
        logActivity("Activated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity(
            "Failed to activate component: "
                + component.getId().getShortId()
                + ", reason: "
                + e.getMessage());
      }
    }

    // Transition to RUNNING state using our new setState method
    setState(MachineState.RUNNING, "Machine started");
  }

  /**
   * Stops this machine, deactivating all components.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void stop() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "stop", state);

    logActivity("Stopping machine");

    // Deactivate all components
    for (CompositeComponent component : components.values()) {
      try {
        // Validate component before deactivation
        org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
        
        component.deactivate();
        logActivity("Deactivated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity(
            "Failed to deactivate component: "
                + component.getId().getShortId()
                + ", reason: "
                + e.getMessage());
      }
    }

    // Transition to STOPPED state using our new setState method
    setState(MachineState.STOPPED, "Machine stopped");
  }

  /**
   * Pause this machine, temporarily suspending activities but maintaining state.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void pause() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "pause", state);
    
    logActivity("Pausing machine");
    
    // Suspend components but don't stop them completely
    for (CompositeComponent component : components.values()) {
      try {
        // Validate component before suspension
        org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
        
        if (component.getLifecycleState().isActive()) {
          // We deactivate here, but we could have a more specialized suspend method
          component.deactivate();
          logActivity("Suspended component: " + component.getId().getShortId());
        }
      } catch (Exception e) {
        logActivity(
            "Failed to suspend component: "
                + component.getId().getShortId()
                + ", reason: "
                + e.getMessage());
      }
    }
    
    // Transition to PAUSED state
    setState(MachineState.PAUSED, "Machine paused");
  }
  
  /**
   * Resume this machine from a paused state.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void resume() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "resume", state);
    
    logActivity("Resuming machine");
    
    // Resume components
    for (CompositeComponent component : components.values()) {
      try {
        // Validate component before resuming
        org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
        
        if (component.getLifecycleState().isStandby()) {
          component.activate();
          logActivity("Resumed component: " + component.getId().getShortId());
        }
      } catch (Exception e) {
        logActivity(
            "Failed to resume component: "
                + component.getId().getShortId()
                + ", reason: "
                + e.getMessage());
      }
    }
    
    // Transition to RUNNING state
    setState(MachineState.RUNNING, "Machine resumed");
  }

  /**
   * Destroys this machine, terminating all components and releasing resources.
   *
   * @throws InvalidOperationException if the operation is not allowed in the current state
   */
  public void destroy() {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "destroy", state);
    
    logActivity("Destroying machine");

    // Terminate all components
    for (CompositeComponent component : components.values()) {
      try {
        // Validate component before termination
        org.s8r.domain.validation.MachineComponentValidator.validateMachineComponent(this, component);
        
        component.terminate();
        logActivity("Terminated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity(
            "Failed to terminate component: "
                + component.getId().getShortId()
                + ", reason: "
                + e.getMessage());
      }
    }

    // Transition to DESTROYED state using our new setState method
    setState(MachineState.DESTROYED, "Machine destroyed");
  }

  /** Sets the version of this machine. */
  public void setVersion(String version) {
    // Validate that this operation is allowed in the current state
    MachineStateValidator.validateOperationState(id, "setVersion", state);

    this.version = version;
    logActivity("Version updated to: " + version);
  }

  /**
   * Sets the machine state to a new state, validating the transition.
   *
   * @param newState The new state to transition to
   * @param reason The reason for the state change
   * @throws InvalidMachineStateTransitionException if the transition is invalid
   */
  public void setState(MachineState newState, String reason) {
    MachineStateValidator.validateStateTransition(id, state, newState);
    
    MachineState oldState = state;
    state = newState;
    logActivity("State changed to " + newState + ": " + reason);
    
    raiseEvent(new MachineStateChangedEvent(id, oldState, state, reason));
  }
  
  /**
   * Attempts to transition the machine to the error state.
   *
   * @param errorReason The reason for entering the error state
   * @return true if the transition succeeded, false if already in error or destroyed state
   */
  public boolean setErrorState(String errorReason) {
    // Skip if already in ERROR state or DESTROYED state
    if (state == MachineState.ERROR || state == MachineState.DESTROYED) {
      return false;
    }
    
    try {
      setState(MachineState.ERROR, "Error: " + errorReason);
      return true;
    } catch (InvalidMachineStateTransitionException e) {
      logActivity("Failed to transition to ERROR state: " + e.getMessage());
      return false;
    }
  }

  /**
   * Resets the machine from an error state back to a ready state.
   *
   * @throws InvalidMachineStateTransitionException if the machine is not in the ERROR state
   */
  public void resetFromError() {
    // Validate using operation validation
    MachineStateValidator.validateOperationState(id, "reset", state);
    
    // Transition back to READY state
    setState(MachineState.READY, "Reset from error state");
  }
  
  /** Checks if this machine is in a modifiable state. */
  private boolean isModifiable() {
    return MachineStateValidator.isOperationAllowed("addComponent", state);
  }

  /** Logs an activity for this machine. */
  private void logActivity(String activity) {
    activityLog.add(Instant.now() + " - " + activity);
  }

  // Getters
  public ComponentId getId() {
    return id;
  }

  public MachineType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public Instant getCreationTime() {
    return creationTime;
  }

  public MachineState getState() {
    return state;
  }

  public List<String> getActivityLog() {
    return Collections.unmodifiableList(activityLog);
  }

  public List<DomainEvent> getDomainEvents() {
    return Collections.unmodifiableList(domainEvents);
  }

  // Event handling
  public void clearEvents() {
    domainEvents.clear();
  }

  protected void raiseEvent(DomainEvent event) {
    domainEvents.add(event);
  }

  // Object methods overrides
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((Machine) o).id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Machine{id="
        + id.getShortId()
        + ", name='"
        + name
        + '\''
        + ", type="
        + type
        + ", state="
        + state
        + ", version='"
        + version
        + '\''
        + ", componentCount="
        + components.size()
        + '}';
  }
}
