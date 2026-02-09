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
 * Package-private adapter that creates a MachinePort interface from a domain Machine
 * implementation. This wraps domain.Machine to provide the MachinePort interface, enabling
 * clean architecture separation between domain and component layers.
 *
 * <p><b>Note:</b> This class is intentionally package-private and should not be referenced
 * outside the org.s8r.adapter package. Use factory methods in {@link MachineAdapter} to obtain
 * instances.
 */
class DomainMachinePortAdapter implements MachinePort {
  private static final ConsoleLogger logger = new ConsoleLogger("DomainMachinePortAdapter");
  private final org.s8r.domain.machine.Machine machine;

  public DomainMachinePortAdapter(org.s8r.domain.machine.Machine machine) {
    this.machine = machine;
  }

  @Override
  public String getMachineId() {
    return machine.getId().getIdString();
  }

  @Override
  public MachineType getMachineType() {
    return machine.getType();
  }

  @Override
  public MachineState getMachineState() {
    return machine.getState();
  }

  @Override
  public void setMachineState(MachineState state) {
    if (state == MachineState.RUNNING) {
      machine.start();
    } else if (state == MachineState.STOPPED) {
      machine.stop();
    } else if (state == MachineState.DESTROYED) {
      machine.destroy();
    } else if (state == MachineState.READY) {
      machine.initialize();
    }
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
    // We would need to convert from CompositeComponentPort to CompositeComponent
    // This is simplified and won't actually work, only illustrates the pattern
    return false;
  }

  @Override
  public Optional<CompositeComponentPort> removeComposite(String name) {
    return Optional.empty();
  }

  @Override
  public CompositeComponentPort getComposite(String name) {
    // We would need an adapter from CompositeComponent to CompositeComponentPort
    return null;
  }

  @Override
  public Map<String, CompositeComponentPort> getComposites() {
    return Collections.emptyMap();
  }

  @Override
  public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
    return false;
  }

  @Override
  public Map<String, List<String>> getCompositeConnections() {
    return Collections.emptyMap();
  }

  @Override
  public Map<String, List<String>> getConnections() {
    return Collections.emptyMap();
  }

  @Override
  public ComponentId getId() {
    return machine.getId();
  }

  @Override
  public LifecycleState getLifecycleState() {
    return machine.getState() == MachineState.RUNNING ? LifecycleState.ACTIVE : LifecycleState.READY;
  }

  @Override
  public List<String> getLineage() {
    return Collections.emptyList();
  }

  @Override
  public List<String> getActivityLog() {
    return machine.getActivityLog();
  }

  @Override
  public java.time.Instant getCreationTime() {
    return machine.getCreationTime();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    return machine.getDomainEvents();
  }

  @Override
  public void addToLineage(String entry) {
    // No-op, not supported
  }

  @Override
  public void clearEvents() {
    machine.clearEvents();
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // No-op, not supported
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    // No-op, not supported
  }

  @Override
  public void transitionTo(LifecycleState newState) {
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
    return false;
  }

  @Override
  public Optional<ComponentPort> removeComponent(String name) {
    return Optional.empty();
  }

  @Override
  public ComponentPort getComponent(String name) {
    return null;
  }

  @Override
  public boolean hasComponent(String name) {
    return false;
  }

  @Override
  public Map<String, ComponentPort> getComponents() {
    return Collections.emptyMap();
  }

  @Override
  public boolean connect(String sourceName, String targetName) {
    return false;
  }

  @Override
  public boolean disconnect(String sourceName, String targetName) {
    return false;
  }

  @Override
  public List<String> getConnectionsFrom(String sourceName) {
    return Collections.emptyList();
  }

  @Override
  public Map<String, Object> getProperties() {
    return Collections.emptyMap();
  }
}
