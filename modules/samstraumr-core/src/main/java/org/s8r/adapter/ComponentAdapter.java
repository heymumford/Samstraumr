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

package org.s8r.adapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Adapter for converting between domain Component entities and ComponentPort interfaces.
 *
 * <p>This adapter facilitates Clean Architecture by providing conversions between concrete
 * implementations and port interfaces, allowing clients to depend on abstractions rather than
 * implementations.
 */
public class ComponentAdapter {

  private final LoggerPort logger;

  /**
   * Creates a new ComponentAdapter.
   *
   * @param logger Logger for recording operations
   */
  public ComponentAdapter(LoggerPort logger) {
    this.logger = logger;
  }

  /**
   * Creates a ComponentPort from a Component.
   *
   * @param component The domain component to adapt
   * @return A ComponentPort that delegates to the component
   */
  public static ComponentPort createComponentPort(Component component) {
    if (component == null) {
      return null;
    }

    if (component instanceof CompositeComponent) {
      return new CompositeComponentAdapter((CompositeComponent) component);
    } else {
      return new ComponentToPortAdapter(component);
    }
  }

  /**
   * Creates a CompositeComponentPort from a CompositeComponent.
   *
   * @param composite The domain composite component to adapt
   * @return A CompositeComponentPort that delegates to the composite component
   */
  public static CompositeComponentPort createCompositeComponentPort(CompositeComponent composite) {
    if (composite == null) {
      return null;
    }

    return new CompositeComponentAdapter(composite);
  }

  /** Adapter that wraps a Component to provide the ComponentPort interface. */
  public static class ComponentToPortAdapter implements ComponentPort {
    private final Component component;

    public ComponentToPortAdapter(Component component) {
      this.component = component;
    }

    /**
     * Gets the underlying Component instance.
     *
     * @return The wrapped Component
     */
    public Component getComponent() {
      return component;
    }

    @Override
    public ComponentId getId() {
      return component.getId();
    }

    @Override
    public LifecycleState getLifecycleState() {
      return component.getLifecycleState();
    }

    @Override
    public List<String> getLineage() {
      return component.getLineage();
    }

    @Override
    public List<String> getActivityLog() {
      return component.getActivityLog();
    }

    @Override
    public Map<String, Object> getProperties() {
      return component.getProperties();
    }

    @Override
    public Instant getCreationTime() {
      return component.getCreationTime();
    }

    @Override
    public List<DomainEvent> getDomainEvents() {
      return component.getDomainEvents();
    }

    @Override
    public void addToLineage(String entry) {
      component.addToLineage(entry);
    }

    @Override
    public void clearEvents() {
      component.clearEvents();
    }

    @Override
    public void publishData(String channel, Map<String, Object> data) {
      component.publishData(channel, data);
    }

    @Override
    public void publishData(String channel, String key, Object value) {
      component.publishData(channel, key, value);
    }

    @Override
    public void transitionTo(LifecycleState newState) {
      component.transitionTo(newState);
    }

    @Override
    public void activate() {
      component.activate();
    }

    @Override
    public void deactivate() {
      component.deactivate();
    }

    @Override
    public void terminate() {
      component.terminate();
    }
  }

  /** Adapter that wraps a CompositeComponent to provide the CompositeComponentPort interface. */
  public static class CompositeComponentAdapter extends ComponentToPortAdapter
      implements CompositeComponentPort {
    private final CompositeComponent composite;

    public CompositeComponentAdapter(CompositeComponent composite) {
      super(composite);
      this.composite = composite;
    }

    /**
     * Gets the underlying CompositeComponent.
     *
     * @return The wrapped CompositeComponent
     */
    public CompositeComponent getCompositeComponent() {
      return composite;
    }

    @Override
    public String getCompositeId() {
      // Use the ID string as the composite ID
      return composite.getId().getIdString();
    }

    @Override
    public boolean addComponent(String name, ComponentPort component) {
      // Can only add concrete Components directly
      if (component instanceof ComponentToPortAdapter) {
        Component concreteComponent = ((ComponentToPortAdapter) component).component;
        try {
          composite.addComponent(concreteComponent);
          return true;
        } catch (Exception e) {
          return false;
        }
      }
      return false;
    }

    @Override
    public Optional<ComponentPort> removeComponent(String name) {
      // Find component by ID instead of name
      for (Component component : composite.getComponents()) {
        if (component.getId().getIdString().equals(name)) {
          try {
            composite.removeComponent(component.getId());
            return Optional.of(createComponentPort(component));
          } catch (Exception e) {
            return Optional.empty();
          }
        }
      }
      return Optional.empty();
    }

    @Override
    public ComponentPort getComponent(String name) {
      // Find component by ID instead of name
      for (Component component : composite.getComponents()) {
        if (component.getId().getIdString().equals(name)) {
          return createComponentPort(component);
        }
      }
      return null;
    }

    @Override
    public boolean hasComponent(String name) {
      // Find component by ID instead of using hasComponent
      for (Component component : composite.getComponents()) {
        if (component.getId().getIdString().equals(name)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public Map<String, ComponentPort> getComponents() {
      // Convert list of components to map with ID as key
      List<Component> componentList = composite.getComponents();
      Map<String, ComponentPort> result = new HashMap<>();

      for (Component component : componentList) {
        result.put(component.getId().getIdString(), createComponentPort(component));
      }

      return Collections.unmodifiableMap(result);
    }

    @Override
    public boolean connect(String sourceName, String targetName) {
      // Find component IDs by name strings and create connection
      ComponentId sourceId = null;
      ComponentId targetId = null;

      for (Component component : composite.getComponents()) {
        String idString = component.getId().getIdString();
        if (idString.equals(sourceName)) {
          sourceId = component.getId();
        } else if (idString.equals(targetName)) {
          targetId = component.getId();
        }

        if (sourceId != null && targetId != null) {
          try {
            composite.connect(
                sourceId, targetId, ConnectionType.DATA_FLOW, "Connected via port interface");
            return true;
          } catch (Exception e) {
            return false;
          }
        }
      }

      return false;
    }

    @Override
    public boolean disconnect(String sourceName, String targetName) {
      // Find the connection to disconnect
      for (org.s8r.domain.component.composite.ComponentConnection connection :
          composite.getConnections()) {
        ComponentId sourceId = connection.getSourceId();
        ComponentId targetId = connection.getTargetId();

        if (sourceId.getIdString().equals(sourceName)
            && targetId.getIdString().equals(targetName)) {
          try {
            composite.disconnect(connection.getConnectionId());
            return true;
          } catch (Exception e) {
            return false;
          }
        }
      }
      return false;
    }

    @Override
    public Map<String, List<String>> getConnections() {
      // Create a map from connections list
      Map<String, List<String>> result = new HashMap<>();

      // Group connections by source component
      for (org.s8r.domain.component.composite.ComponentConnection connection :
          composite.getConnections()) {
        String sourceId = connection.getSourceId().getIdString();
        String targetId = connection.getTargetId().getIdString();

        result.computeIfAbsent(sourceId, k -> new ArrayList<>()).add(targetId);
      }

      return Collections.unmodifiableMap(result);
    }

    @Override
    public List<String> getConnectionsFrom(String sourceName) {
      // Get all connections from the specified source component
      List<String> targetNames = new ArrayList<>();

      for (org.s8r.domain.component.composite.ComponentConnection connection :
          composite.getConnections()) {
        if (connection.getSourceId().getIdString().equals(sourceName)) {
          targetNames.add(connection.getTargetId().getIdString());
        }
      }

      return Collections.unmodifiableList(targetNames);
    }
  }
}
