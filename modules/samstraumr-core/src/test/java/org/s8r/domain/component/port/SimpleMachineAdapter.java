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

package org.s8r.domain.component.port;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * A simple adapter that implements MachinePort for testing Clean Architecture patterns.
 *
 * <p>This adapter makes it easier to test the port interface pattern without dealing with the
 * complexity of the full implementation.
 */
public class SimpleMachineAdapter implements MachinePort {

  private final Machine machine;
  private final String machineId;
  private final ComponentId componentId;
  private MachineState state = MachineState.STOPPED;
  private final Map<String, CompositeComponentPort> composites = new HashMap<>();
  private final Map<String, List<String>> connections = new HashMap<>();
  private final List<String> lineage = new ArrayList<>();
  private final List<String> activityLog = new ArrayList<>();
  private final Instant creationTime = Instant.now();

  /**
   * Creates a new SimpleMachineAdapter.
   *
   * @param machine The domain machine to adapt
   */
  public SimpleMachineAdapter(Machine machine) {
    this.machine = machine;
    this.machineId = UUID.randomUUID().toString();
    this.componentId = machine.getId();
    this.lineage.add(componentId.getIdString());
  }

  @Override
  public String getMachineId() {
    return machineId;
  }

  @Override
  public MachineType getMachineType() {
    return machine.getType();
  }

  @Override
  public MachineState getMachineState() {
    return state;
  }

  @Override
  public void setMachineState(MachineState state) {
    this.state = state;
    activityLog.add("Machine state set to " + state);
  }

  @Override
  public boolean start() {
    setMachineState(MachineState.RUNNING);
    return true;
  }

  @Override
  public boolean stop() {
    setMachineState(MachineState.STOPPED);
    return true;
  }

  @Override
  public boolean addComposite(String name, CompositeComponentPort composite) {
    composites.put(name, composite);
    activityLog.add("Added composite: " + name);
    return true;
  }

  @Override
  public Optional<CompositeComponentPort> removeComposite(String name) {
    CompositeComponentPort removed = composites.remove(name);
    if (removed != null) {
      activityLog.add("Removed composite: " + name);
      return Optional.of(removed);
    }
    return Optional.empty();
  }

  @Override
  public CompositeComponentPort getComposite(String name) {
    return composites.get(name);
  }

  @Override
  public Map<String, CompositeComponentPort> getComposites() {
    return Collections.unmodifiableMap(composites);
  }

  @Override
  public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
    List<String> targets = connections.computeIfAbsent(sourceCompositeName, k -> new ArrayList<>());
    if (!targets.contains(targetCompositeName)) {
      targets.add(targetCompositeName);
      activityLog.add("Connected " + sourceCompositeName + " to " + targetCompositeName);
      return true;
    }
    return false;
  }

  @Override
  public Map<String, List<String>> getCompositeConnections() {
    return Collections.unmodifiableMap(connections);
  }

  // CompositeComponentPort methods

  @Override
  public String getCompositeId() {
    return machineId;
  }

  @Override
  public boolean addComponent(String name, ComponentPort component) {
    // Machines don't handle components directly
    return false;
  }

  @Override
  public Optional<ComponentPort> removeComponent(String name) {
    // Machines don't handle components directly
    return Optional.empty();
  }

  @Override
  public ComponentPort getComponent(String name) {
    // Machines don't handle components directly
    return null;
  }

  @Override
  public boolean hasComponent(String name) {
    // Machines don't handle components directly
    return false;
  }

  @Override
  public Map<String, ComponentPort> getComponents() {
    // Machines don't handle components directly
    return Collections.emptyMap();
  }

  @Override
  public boolean connect(String sourceName, String targetName) {
    return connectComposites(sourceName, targetName);
  }

  @Override
  public boolean disconnect(String sourceName, String targetName) {
    List<String> targets = connections.get(sourceName);
    if (targets != null && targets.contains(targetName)) {
      targets.remove(targetName);
      activityLog.add("Disconnected " + sourceName + " from " + targetName);
      return true;
    }
    return false;
  }

  @Override
  public Map<String, List<String>> getConnections() {
    return getCompositeConnections();
  }

  @Override
  public List<String> getConnectionsFrom(String sourceName) {
    return connections.getOrDefault(sourceName, Collections.emptyList());
  }

  // ComponentPort methods

  @Override
  public ComponentId getId() {
    return componentId;
  }

  @Override
  public LifecycleState getLifecycleState() {
    return state == MachineState.RUNNING ? LifecycleState.ACTIVE : LifecycleState.READY;
  }

  @Override
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  @Override
  public List<String> getActivityLog() {
    return Collections.unmodifiableList(activityLog);
  }

  @Override
  public Instant getCreationTime() {
    return creationTime;
  }

  @Override
  public Map<String, Object> getProperties() {
    return Collections.emptyMap();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    // Simple implementation doesn't track domain events
    return Collections.emptyList();
  }

  @Override
  public void addToLineage(String entry) {
    lineage.add(entry);
  }

  @Override
  public void clearEvents() {
    // Simple implementation doesn't track domain events
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    activityLog.add("Published data to channel: " + channel);
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    activityLog.add("Published " + key + "=" + value + " to channel: " + channel);
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    if (newState == LifecycleState.ACTIVE) {
      start();
    } else if (newState == LifecycleState.READY) {
      stop();
    } else if (newState == LifecycleState.TERMINATED) {
      stop();
      activityLog.add("Machine terminated");
    }
  }

  @Override
  public void activate() {
    start();
  }

  @Override
  public void deactivate() {
    stop();
  }

  @Override
  public void terminate() {
    transitionTo(LifecycleState.TERMINATED);
  }
}
