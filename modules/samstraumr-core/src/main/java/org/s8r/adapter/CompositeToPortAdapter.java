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

import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * Package-private adapter that wraps a component.Composite to provide the CompositeComponentPort
 * interface, enabling conversion between component and domain architectural layers.
 *
 * <p><b>Note:</b> This class is intentionally package-private and should not be referenced outside
 * the org.s8r.adapter package. Use factory methods in {@link MachineAdapter} to obtain instances.
 */
class CompositeToPortAdapter implements CompositeComponentPort {
  private static final ConsoleLogger logger = new ConsoleLogger("CompositeToPortAdapter");
  private final Composite composite;

  public CompositeToPortAdapter(Composite composite) {
    this.composite = composite;
  }

  @Override
  public String getCompositeId() {
    return composite.getCompositeId();
  }

  @Override
  public boolean addComponent(String name, ComponentPort component) {
    // This requires conversion from ComponentPort to Component
    if (component instanceof Component) {
      composite.addComponent(name, (Component) component);
      return true;
    }
    return false;
  }

  @Override
  public java.util.Optional<ComponentPort> removeComponent(String name) {
    // Composite doesn't support removing components
    return java.util.Optional.empty();
  }

  @Override
  public ComponentPort getComponent(String name) {
    Component component = composite.getComponent(name);
    if (component != null) {
      return new ComponentToPortAdapter(component);
    }
    return null;
  }

  @Override
  public boolean hasComponent(String name) {
    return composite.getComponent(name) != null;
  }

  @Override
  public Map<String, ComponentPort> getComponents() {
    Map<String, Component> components = composite.getComponents();
    Map<String, ComponentPort> ports = new HashMap<>();

    for (Map.Entry<String, Component> entry : components.entrySet()) {
      ports.put(entry.getKey(), new ComponentToPortAdapter(entry.getValue()));
    }

    return Collections.unmodifiableMap(ports);
  }

  @Override
  public boolean connect(String sourceName, String targetName) {
    try {
      composite.connect(sourceName, targetName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean disconnect(String sourceName, String targetName) {
    // Composite doesn't support disconnecting
    return false;
  }

  @Override
  public Map<String, List<String>> getConnections() {
    return composite.getConnections();
  }

  @Override
  public List<String> getConnectionsFrom(String sourceName) {
    Map<String, List<String>> connections = composite.getConnections();
    return connections.getOrDefault(sourceName, Collections.emptyList());
  }

  // ComponentPort methods

  @Override
  public ComponentId getId() {
    // Create a ComponentId based on the composite ID
    return ComponentId.create("Composite adapter for " + composite.getCompositeId());
  }

  @Override
  public LifecycleState getLifecycleState() {
    // Map isActive to lifecycle state
    return composite.isActive() ? LifecycleState.ACTIVE : LifecycleState.READY;
  }

  @Override
  public List<String> getLineage() {
    // Composite doesn't have lineage
    return Collections.emptyList();
  }

  @Override
  public List<String> getActivityLog() {
    // Composite doesn't have activity log
    return Collections.emptyList();
  }

  @Override
  public java.time.Instant getCreationTime() {
    // Composite doesn't track creation time
    return java.time.Instant.now();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    // Composite doesn't track domain events
    return Collections.emptyList();
  }

  @Override
  public void addToLineage(String entry) {
    // No-op, composite doesn't have lineage
  }

  @Override
  public void clearEvents() {
    // No-op, composite doesn't track events
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // No-op, composite doesn't support this directly
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    // No-op, composite doesn't support this directly
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    // Map lifecycle state to composite activation
    if (newState == LifecycleState.ACTIVE) {
      composite.activate();
    } else if (newState == LifecycleState.READY || newState == LifecycleState.TERMINATING) {
      composite.deactivate();
    }
  }

  @Override
  public void activate() {
    composite.activate();
  }

  @Override
  public void deactivate() {
    composite.deactivate();
  }

  @Override
  public void terminate() {
    // Deactivate is the closest operation
    composite.deactivate();
  }

  @Override
  public Map<String, Object> getProperties() {
    // Legacy composites don't have a centralized properties map
    return Collections.emptyMap();
  }
}
