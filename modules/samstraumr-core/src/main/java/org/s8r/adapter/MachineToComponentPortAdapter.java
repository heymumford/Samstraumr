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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * Package-private adapter that converts from component.Machine to MachinePort. This enables legacy
 * component-layer machines to be used with domain-layer port interfaces, supporting incremental
 * migration strategies.
 *
 * <p><b>Note:</b> This class is intentionally package-private and should not be referenced outside
 * the org.s8r.adapter package. Use factory methods in {@link MachineAdapter} to obtain instances.
 */
class MachineToComponentPortAdapter implements MachinePort {
  private static final ConsoleLogger logger = new ConsoleLogger("MachineToComponentPortAdapter");
  private final Machine machine;
  private final ComponentId componentId;

  public MachineToComponentPortAdapter(Machine machine) {
    this.machine = machine;
    // Create component ID for this machine
    this.componentId = ComponentId.create("Component Machine: " + machine.getMachineId());
  }

  @Override
  public String getMachineId() {
    return machine.getMachineId();
  }

  @Override
  public MachineType getMachineType() {
    // Default to DATA_PROCESSOR type as component Machine doesn't have type
    return MachineType.DATA_PROCESSOR;
  }

  @Override
  public MachineState getMachineState() {
    // Convert active status to machine state
    return machine.isActive() ? MachineState.RUNNING : MachineState.STOPPED;
  }

  @Override
  public void setMachineState(MachineState state) {
    if (state == MachineState.RUNNING) {
      machine.activate();
    } else if (state == MachineState.STOPPED) {
      machine.deactivate();
    }
  }

  @Override
  public boolean start() {
    try {
      machine.activate();
      return true;
    } catch (Exception e) {
      logger.warn("Failed to activate machine " + getMachineId() + ": " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean stop() {
    try {
      machine.deactivate();
      return true;
    } catch (Exception e) {
      logger.warn("Failed to deactivate machine " + getMachineId() + ": " + e.getMessage());
      return false;
    }
  }

  @Override
  public boolean addComposite(String name, CompositeComponentPort composite) {
    try {
      // Handle composite port conversion
      if (composite instanceof ComponentAdapter.CompositeComponentAdapter) {
        // Get the domain composite
        org.s8r.domain.component.composite.CompositeComponent domainComposite =
            ((ComponentAdapter.CompositeComponentAdapter) composite).getCompositeComponent();

        // Create component layer composite
        Composite componentComposite =
            new Composite(
                domainComposite.getId().getIdString(), new org.s8r.component.Environment());

        machine.addComposite(name, componentComposite);
        return true;
      }
      return false;
    } catch (Exception e) {
      logger.warn(
          "Failed to add composite '"
              + name
              + "' to machine "
              + getMachineId()
              + ": "
              + e.getMessage());
      return false;
    }
  }

  @Override
  public Optional<CompositeComponentPort> removeComposite(String name) {
    // Component machine doesn't support removing composites
    return Optional.empty();
  }

  @Override
  public CompositeComponentPort getComposite(String name) {
    Composite composite = machine.getComposite(name);
    if (composite != null) {
      // Create a composite component from the component composite
      org.s8r.domain.component.composite.CompositeComponent domainComposite =
          org.s8r.domain.component.composite.CompositeFactory.createComposite(
              org.s8r.domain.component.composite.CompositeType.STANDARD,
              "Adapted from " + composite.getCompositeId());

      // Return a composite port for it
      return ComponentAdapter.createCompositeComponentPort(domainComposite);
    }
    return null;
  }

  @Override
  public Map<String, CompositeComponentPort> getComposites() {
    Map<String, Composite> composites = machine.getComposites();
    Map<String, CompositeComponentPort> result = new HashMap<>();

    for (Map.Entry<String, Composite> entry : composites.entrySet()) {
      String name = entry.getKey();
      Composite composite = entry.getValue();

      // Create a composite component for each component composite
      org.s8r.domain.component.composite.CompositeComponent domainComposite =
          org.s8r.domain.component.composite.CompositeFactory.createComposite(
              org.s8r.domain.component.composite.CompositeType.STANDARD,
              "Adapted from " + composite.getCompositeId());

      result.put(name, ComponentAdapter.createCompositeComponentPort(domainComposite));
    }

    return Collections.unmodifiableMap(result);
  }

  @Override
  public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
    try {
      machine.connect(sourceCompositeName, targetCompositeName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public Map<String, List<String>> getCompositeConnections() {
    return machine.getConnections();
  }

  @Override
  public Map<String, List<String>> getConnections() {
    // Same as composite connections
    return getCompositeConnections();
  }

  // ComponentPort methods

  @Override
  public ComponentId getId() {
    return componentId;
  }

  @Override
  public LifecycleState getLifecycleState() {
    return machine.isActive() ? LifecycleState.ACTIVE : LifecycleState.READY;
  }

  @Override
  public List<String> getLineage() {
    return Collections.emptyList();
  }

  @Override
  public List<String> getActivityLog() {
    return Collections.emptyList();
  }

  @Override
  public java.time.Instant getCreationTime() {
    return java.time.Instant.now();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    return Collections.emptyList();
  }

  @Override
  public void addToLineage(String entry) {
    // No-op
  }

  @Override
  public void clearEvents() {
    // No-op
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // No-op
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    // No-op
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    if (newState == LifecycleState.ACTIVE) {
      machine.activate();
    } else if (newState == LifecycleState.READY) {
      machine.deactivate();
    }
  }

  @Override
  public void activate() {
    machine.activate();
  }

  @Override
  public void deactivate() {
    machine.deactivate();
  }

  @Override
  public void terminate() {
    machine.shutdown();
  }

  @Override
  public String getCompositeId() {
    return machine.getMachineId();
  }

  @Override
  public boolean addComponent(String name, ComponentPort component) {
    return false; // Not supported at this level
  }

  @Override
  public Optional<ComponentPort> removeComponent(String name) {
    return Optional.empty(); // Not supported at this level
  }

  @Override
  public ComponentPort getComponent(String name) {
    return null; // Not supported at this level
  }

  @Override
  public boolean hasComponent(String name) {
    return false; // Not supported at this level
  }

  @Override
  public Map<String, ComponentPort> getComponents() {
    return Collections.emptyMap(); // Not supported at this level
  }

  @Override
  public boolean connect(String sourceName, String targetName) {
    try {
      machine.connect(sourceName, targetName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean disconnect(String sourceName, String targetName) {
    return false; // Not supported at this level
  }

  @Override
  public List<String> getConnectionsFrom(String sourceName) {
    Map<String, List<String>> connections = machine.getConnections();
    return connections.getOrDefault(sourceName, Collections.emptyList());
  }

  @Override
  public Map<String, Object> getProperties() {
    return Collections.emptyMap(); // Legacy component machines don't have properties
  }
}
