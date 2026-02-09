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
import org.s8r.component.State;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * Package-private adapter that wraps a component.Component to provide the ComponentPort
 * interface, enabling conversion between component and domain architectural layers.
 *
 * <p><b>Note:</b> This class is intentionally package-private and should not be referenced
 * outside the org.s8r.adapter package. Use factory methods in {@link MachineAdapter} to obtain
 * instances.
 */
class ComponentToPortAdapter implements ComponentPort {
  private static final ConsoleLogger logger = new ConsoleLogger("ComponentToPortAdapter");
  private final Component component;

  public ComponentToPortAdapter(Component component) {
    this.component = component;
  }

  @Override
  public ComponentId getId() {
    // Create a ComponentId from the Component's ID or name
    // This is a simplification as Component doesn't have a ComponentId
    return ComponentId.create("Component adapter for " + component.getUniqueId());
  }

  @Override
  public LifecycleState getLifecycleState() {
    // Map Component.State to LifecycleState
    if (component.isActive()) {
      return LifecycleState.ACTIVE;
    } else if (component.isReady()) {
      return LifecycleState.READY;
    } else if (component.isTerminated()) {
      return LifecycleState.TERMINATED;
    } else {
      return LifecycleState.READY;
    }
  }

  @Override
  public List<String> getLineage() {
    // Return component's lineage
    return component.getLineage();
  }

  @Override
  public List<String> getActivityLog() {
    // Component has memoryLog which serves as its activity log
    return component.getMemoryLog();
  }

  @Override
  public java.time.Instant getCreationTime() {
    // Component has conceptionTime
    return component.getConceptionTime();
  }

  @Override
  public List<DomainEvent> getDomainEvents() {
    // Component doesn't track domain events
    return Collections.emptyList();
  }

  @Override
  public void addToLineage(String entry) {
    // Add to component's lineage
    component.addToLineage(entry);
  }

  @Override
  public void clearEvents() {
    // No-op, component doesn't track events
  }

  @Override
  public void publishData(String channel, Map<String, Object> data) {
    // No-op, component doesn't support this directly
  }

  @Override
  public void publishData(String channel, String key, Object value) {
    // No-op, component doesn't support this directly
  }

  @Override
  public void transitionTo(LifecycleState newState) {
    // Map lifecycle state to component state
    if (newState == LifecycleState.ACTIVE) {
      component.setState(State.ACTIVE);
    } else if (newState == LifecycleState.READY) {
      component.setState(State.READY);
    } else if (newState == LifecycleState.TERMINATING) {
      component.setState(State.TERMINATING);
    } else if (newState == LifecycleState.TERMINATED) {
      component.terminate();
    }
  }

  @Override
  public void activate() {
    // Set the component state to active
    component.setState(State.ACTIVE);
  }

  @Override
  public void deactivate() {
    // Set the component state to ready
    component.setState(State.READY);
  }

  @Override
  public void terminate() {
    // Terminate the component
    component.terminate();
  }

  @Override
  public Map<String, Object> getProperties() {
    // Legacy components expose properties via property map
    Map<String, Object> properties = new HashMap<>();
    for (String key : component.getPropertyKeys()) {
      properties.put(key, component.getProperty(key));
    }
    return Collections.unmodifiableMap(properties);
  }
}
