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

package org.s8r.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * Package-private adapter that wraps a domain Machine to provide the MachinePort interface.
 *
 * <p><b>Consolidated Responsibility:</b> This adapter handles domain.Machine→MachinePort
 * conversion exclusively. It was consolidated with DomainMachinePortAdapter as they shared
 * identical implementations of core state-management methods, eliminating redundancy while
 * preserving the adapter pattern semantics.
 *
 * <p><b>Note:</b> This class is intentionally package-private and should not be referenced
 * outside the org.s8r.adapter package. Use factory methods in {@link MachineAdapter} to obtain
 * instances.
 */
class MachineToDomainPortAdapter implements MachinePort {
  private static final ConsoleLogger logger = new ConsoleLogger("MachineToDomainPortAdapter");
  private final org.s8r.domain.machine.Machine machine;

  public MachineToDomainPortAdapter(org.s8r.domain.machine.Machine machine) {
    this.machine = machine;
  }

  @Override
  public String getMachineId() {
    // Get ID from the domain machine
    return machine.getId().getIdString();
  }

  @Override
  public MachineType getMachineType() {
    // Get the machine type from the domain machine
    return machine.getType();
  }

  @Override
  public MachineState getMachineState() {
    // Get machine state
    return machine.getState();
  }

  @Override
  public void setMachineState(MachineState state) {
    // Map the machine state to appropriate actions
    if (state == MachineState.RUNNING) {
      machine.start();
    } else if (state == MachineState.STOPPED) {
      machine.stop();
    } else if (state == MachineState.DESTROYED) {
      machine.destroy();
    } else if (state == MachineState.READY) {
      machine.initialize();
    }
    // ERROR state not handled, would require custom error handling
  }

  @Override
  public boolean start() {
    try {
      machine.start();
      return true;
    } catch (Exception e) {
      logger.warn("Failed to start machine " + getMachineId() + ": " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean stop() {
    try {
      machine.stop();
      return true;
    } catch (Exception e) {
      logger.warn("Failed to stop machine " + getMachineId() + ": " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean addComposite(String name, CompositeComponentPort composite) {
    // This requires conversion from CompositeComponentPort to CompositeComponent
    if (composite instanceof ComponentAdapter.CompositeComponentAdapter) {
      // We need to get the underlying composite component
      org.s8r.domain.component.composite.CompositeComponent domainComposite =
          ((ComponentAdapter.CompositeComponentAdapter) composite).getCompositeComponent();

      // For domain.machine.Machine, use the addComponent method
      try {
        machine.addComponent(domainComposite);
        return true;
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  @Override
  public Optional<CompositeComponentPort> removeComposite(String name) {
    // Machine doesn't support removing composites directly
    return Optional.empty();
  }

  @Override
  public CompositeComponentPort getComposite(String name) {
    // For domain machine, we need to find the composite component by ID
    for (org.s8r.domain.component.composite.CompositeComponent component :
        machine.getComponents()) {
      if (component.getId().getIdString().equals(name)) {
        // Create a CompositeComponentPort for the domain component
        return ComponentAdapter.createCompositeComponentPort(component);
      }
    }
    return null;
  }

  @Override
  public Map<String, CompositeComponentPort> getComposites() {
    // Convert the list of CompositeComponents to a map of CompositeComponentPorts
    List<org.s8r.domain.component.composite.CompositeComponent> components =
        machine.getComponents();
    Map<String, CompositeComponentPort> ports = new java.util.HashMap<>();

    for (org.s8r.domain.component.composite.CompositeComponent component : components) {
      // Use component ID as the key
      String idString = component.getId().getIdString();
      ports.put(idString, ComponentAdapter.createCompositeComponentPort(component));
    }

    return Collections.unmodifiableMap(ports);
  }

  @Override
  public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
    // Domain machine doesn't have a connect method for string names
    // Need to find components by ID and implement connection
    // Currently not supported for domain.machine.Machine
    return false;
  }

  @Override
  public Map<String, List<String>> getCompositeConnections() {
    // Domain machine doesn't have a simple getConnections method
    // Need to return an empty map since connections aren't supported this way
    return Collections.emptyMap();
  }

  @Override
  public Map<String, List<String>> getConnections() {
    // Same as getCompositeConnections for machines
    return getCompositeConnections();
  }

  // ComponentPort methods delegated to the machine

  @Override
  public ComponentId getId() {
    // Get the domain machine's ID directly
    return machine.getId();
  }

  @Override
  public LifecycleState getLifecycleState() {
    // Map MachineState to LifecycleState
    MachineState state = machine.getState();
    if (state == MachineState.RUNNING) {
      return LifecycleState.ACTIVE;
    } else if (state == MachineState.READY) {
      return LifecycleState.READY;
    } else if (state == MachineState.STOPPED) {
      return LifecycleState.READY;
    } else if (state == MachineState.DESTROYED) {
      return LifecycleState.TERMINATED;
    } else {
      // Created or any other state
      return LifecycleState.READY;
    }
  }

  @Override
  public List<String> getLineage() {
    // Machine doesn't have lineage concept
    return Collections.emptyList();
  }

  @Override
  public List<String> getActivityLog() {
    // Return the activity log from the domain machine
    return machine.getActivityLog();
  }

  @Override
  public java.time.Instant getCreationTime() {
    // Return the creation time from the domain machine
    return machine.getCreationTime();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    // Return domain events from the domain machine
    return machine.getDomainEvents();
  }

  @Override
  public void addToLineage(String entry) {
    // No-op, machine doesn't have lineage
  }

  @Override
  public void clearEvents() {
    // Clear events in the domain machine
    machine.clearEvents();
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // No direct equivalent in Machine
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    // No direct equivalent in Machine
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    // Map lifecycle state to machine activation
    if (newState == LifecycleState.ACTIVE) {
      machine.start();
    } else if (newState == LifecycleState.READY) {
      machine.initialize();
    } else if (newState == LifecycleState.TERMINATING) {
      machine.stop();
    } else if (newState == LifecycleState.TERMINATED) {
      machine.destroy();
    }
  }

  @Override
  public void activate() {
    machine.start();
  }

  @Override
  public void deactivate() {
    machine.stop();
  }

  @Override
  public void terminate() {
    machine.destroy();
  }

  @Override
  public String getCompositeId() {
    return machine.getId().getIdString();
  }

  @Override
  public boolean addComponent(String name, ComponentPort component) {
    // Not supported at this level, components are added to composites, not machines
    return false;
  }

  @Override
  public Optional<ComponentPort> removeComponent(String name) {
    // Not supported at this level
    return Optional.empty();
  }

  @Override
  public ComponentPort getComponent(String name) {
    // Not supported at this level
    return null;
  }

  @Override
  public boolean hasComponent(String name) {
    // Not supported at this level
    return false;
  }

  @Override
  public Map<String, ComponentPort> getComponents() {
    // Not supported at this level
    return Collections.emptyMap();
  }

  @Override
  public boolean connect(String sourceName, String targetName) {
    // Domain machine doesn't have a connect method for string names
    // This operation is not supported for domain machines
    return false;
  }

  @Override
  public boolean disconnect(String sourceName, String targetName) {
    // Machine doesn't support disconnecting
    return false;
  }

  @Override
  public List<String> getConnectionsFrom(String sourceName) {
    // Domain machine doesn't have a getConnections method that returns a map
    // This operation would need a custom implementation using domain machine APIs
    return Collections.emptyList();
  }

  @Override
  public Map<String, Object> getProperties() {
    return Collections.emptyMap(); // Domain machines don't expose properties through this adapter
  }
}
