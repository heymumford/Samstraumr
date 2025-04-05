/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for machines in the S8r framework
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
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

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

  private Machine(ComponentId id, MachineType type, String name, String description, String version) {
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

  /** Adds a composite component to this machine. */
  public void addComponent(CompositeComponent component) {
    if (!isModifiable()) {
      throw new InvalidOperationException("addComponent", this.id.getShortId(), getStateName());
    }

    components.put(component.getId().getIdString(), component);
    logActivity("Added component: " + component.getId().getShortId());
  }

  /** Removes a component from this machine. */
  public void removeComponent(ComponentId componentId) {
    if (!isModifiable()) {
      throw new InvalidOperationException("removeComponent", this.id.getShortId(), getStateName());
    }

    String componentIdStr = componentId.getIdString();
    CompositeComponent removed = components.remove(componentIdStr);

    if (removed == null) {
      throw new ComponentNotFoundException(componentId);
    }

    logActivity("Removed component: " + componentId.getShortId());
  }

  /** Gets a component by its ID. */
  public Optional<CompositeComponent> getComponent(ComponentId componentId) {
    return Optional.ofNullable(components.get(componentId.getIdString()));
  }

  /** Gets all components in this machine. */
  public List<CompositeComponent> getComponents() {
    return Collections.unmodifiableList(new ArrayList<>(components.values()));
  }

  /** Initializes this machine, preparing it for operation. */
  public void initialize() {
    if (state != MachineState.CREATED) {
      throw new InvalidOperationException("initialize", this.id.getShortId(), getStateName());
    }

    logActivity("Initializing machine");

    // Initialize all components
    for (CompositeComponent component : components.values()) {
      if (component.getLifecycleState() == LifecycleState.READY) {
        logActivity("Component already initialized: " + component.getId().getShortId());
      } else {
        logActivity("Initializing component: " + component.getId().getShortId());
      }
    }

    MachineState oldState = state;
    state = MachineState.READY;
    logActivity("Machine initialized successfully");

    raiseEvent(new MachineStateChangedEvent(id, oldState, state, "Initialization completed"));
  }

  /** Starts this machine, activating all components. */
  public void start() {
    if (state != MachineState.READY && state != MachineState.STOPPED) {
      throw new InvalidOperationException("start", this.id.getShortId(), getStateName());
    }

    logActivity("Starting machine");

    // Activate all components
    for (CompositeComponent component : components.values()) {
      try {
        component.activate();
        logActivity("Activated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity("Failed to activate component: " + component.getId().getShortId() + 
                    ", reason: " + e.getMessage());
      }
    }

    MachineState oldState = state;
    state = MachineState.RUNNING;
    logActivity("Machine started successfully");

    raiseEvent(new MachineStateChangedEvent(id, oldState, state, "Machine started"));
  }

  /** Stops this machine, deactivating all components. */
  public void stop() {
    if (state != MachineState.RUNNING) {
      throw new InvalidOperationException("stop", this.id.getShortId(), getStateName());
    }

    logActivity("Stopping machine");

    // Deactivate all components
    for (CompositeComponent component : components.values()) {
      try {
        component.deactivate();
        logActivity("Deactivated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity("Failed to deactivate component: " + component.getId().getShortId() + 
                    ", reason: " + e.getMessage());
      }
    }

    MachineState oldState = state;
    state = MachineState.STOPPED;
    logActivity("Machine stopped successfully");

    raiseEvent(new MachineStateChangedEvent(id, oldState, state, "Machine stopped"));
  }

  /** Destroys this machine, terminating all components and releasing resources. */
  public void destroy() {
    logActivity("Destroying machine");

    // Terminate all components
    for (CompositeComponent component : components.values()) {
      try {
        component.terminate();
        logActivity("Terminated component: " + component.getId().getShortId());
      } catch (Exception e) {
        logActivity("Failed to terminate component: " + component.getId().getShortId() + 
                    ", reason: " + e.getMessage());
      }
    }

    MachineState oldState = state;
    state = MachineState.DESTROYED;
    logActivity("Machine destroyed successfully");

    raiseEvent(new MachineStateChangedEvent(id, oldState, state, "Machine destroyed"));
  }

  /** Sets the version of this machine. */
  public void setVersion(String version) {
    if (!isModifiable()) {
      throw new InvalidOperationException("setVersion", this.id.getShortId(), getStateName());
    }

    this.version = version;
    logActivity("Version updated to: " + version);
  }

  /** Checks if this machine is in a modifiable state. */
  private boolean isModifiable() {
    return state == MachineState.CREATED || 
           state == MachineState.READY || 
           state == MachineState.STOPPED;
  }

  /** Gets the current state name of this machine. */
  private String getStateName() {
    return state.name();
  }

  /** Logs an activity for this machine. */
  private void logActivity(String activity) {
    activityLog.add(Instant.now() + " - " + activity);
  }

  // Getters
  public ComponentId getId() { return id; }
  public MachineType getType() { return type; }
  public String getName() { return name; }
  public String getDescription() { return description; }
  public String getVersion() { return version; }
  public Instant getCreationTime() { return creationTime; }
  public MachineState getState() { return state; }
  public List<String> getActivityLog() { return Collections.unmodifiableList(activityLog); }
  public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }

  // Event handling
  public void clearEvents() { domainEvents.clear(); }
  protected void raiseEvent(DomainEvent event) { domainEvents.add(event); }

  // Object methods overrides
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return Objects.equals(id, ((Machine) o).id);
  }

  @Override
  public int hashCode() { return Objects.hash(id); }

  @Override
  public String toString() {
    return "Machine{id=" + id.getShortId() + 
           ", name='" + name + '\'' + 
           ", type=" + type + 
           ", state=" + state + 
           ", version='" + version + '\'' + 
           ", componentCount=" + components.size() + '}';
  }
}
