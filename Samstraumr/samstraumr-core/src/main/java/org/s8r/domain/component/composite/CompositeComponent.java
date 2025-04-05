/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for composite components in the S8r framework
 */

package org.s8r.domain.component.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Implements the Composite pattern for components, allowing component compositions.
 *
 * <p>CompositeComponent is a specialized Component that can contain other components and manage
 * connections between them. It follows Clean Architecture principles with no dependencies on
 * infrastructure or frameworks.
 */
public class CompositeComponent extends Component {
  private final Map<String, Component> children = new HashMap<>();
  private final List<ComponentConnection> connections = new ArrayList<>();
  private final CompositeType compositeType;

  /**
   * Creates a new CompositeComponent.
   *
   * @param id The component identity
   * @param compositeType The type of composite
   * @return A new CompositeComponent
   */
  public static CompositeComponent create(ComponentId id, CompositeType compositeType) {
    return new CompositeComponent(id, compositeType);
  }

  private CompositeComponent(ComponentId id, CompositeType compositeType) {
    super(id);
    this.compositeType = compositeType;
    logActivity("CompositeComponent created with type: " + compositeType);
  }

  /**
   * Gets the type of this composite.
   *
   * @return The composite type
   */
  public CompositeType getCompositeType() {
    return compositeType;
  }

  /**
   * Adds a child component to this composite.
   *
   * @param child The component to add
   * @throws DuplicateComponentException if a component with the same ID already exists
   * @throws InvalidOperationException if the composite is not in a valid state for adding children
   */
  public void addComponent(Component child) {
    if (!isModifiable()) {
      throw new InvalidOperationException("addComponent", this);
    }

    String childId = child.getId().getIdString();
    if (children.containsKey(childId)) {
      throw new DuplicateComponentException(child.getId());
    }

    children.put(childId, child);
    logActivity("Added child component: " + childId);

    // Create a composition relationship connection
    ComponentConnection connection =
        new ComponentConnection(
            getId(), child.getId(), ConnectionType.COMPOSITION, "Parent-child relationship");
    connections.add(connection);
  }

  /**
   * Removes a child component from this composite.
   *
   * @param componentId The ID of the component to remove
   * @throws ComponentNotFoundException if the component is not found
   * @throws InvalidOperationException if the composite is not in a valid state for removing
   *     children
   */
  public void removeComponent(ComponentId componentId) {
    if (!isModifiable()) {
      throw new InvalidOperationException("removeComponent", this);
    }

    String componentIdStr = componentId.getIdString();
    Component removedComponent = children.remove(componentIdStr);

    if (removedComponent == null) {
      throw new ComponentNotFoundException(componentId);
    }

    // Remove all connections involving this component
    connections.removeIf(
        conn -> conn.getSourceId().equals(componentId) || conn.getTargetId().equals(componentId));

    logActivity("Removed child component: " + componentIdStr);
  }

  /**
   * Gets a child component by its ID.
   *
   * @param componentId The component ID
   * @return An Optional containing the component if found
   */
  public Optional<Component> getComponent(ComponentId componentId) {
    return Optional.ofNullable(children.get(componentId.getIdString()));
  }

  /**
   * Gets all child components.
   *
   * @return An unmodifiable list of child components
   */
  public List<Component> getComponents() {
    return Collections.unmodifiableList(new ArrayList<>(children.values()));
  }

  /**
   * Checks if this composite contains a specific component.
   *
   * @param componentId The component ID to check
   * @return true if the component is contained in this composite, false otherwise
   */
  public boolean containsComponent(ComponentId componentId) {
    return children.containsKey(componentId.getIdString());
  }

  /**
   * Creates a connection between two components in this composite.
   *
   * @param sourceId The source component ID
   * @param targetId The target component ID
   * @param type The type of connection
   * @param description A description of the connection
   * @return The created connection
   * @throws ComponentNotFoundException if either component is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public ComponentConnection connect(
      ComponentId sourceId, ComponentId targetId, ConnectionType type, String description) {
    if (!isModifiable()) {
      throw new InvalidOperationException("connect", this);
    }

    // Verify both components exist
    if (!containsComponent(sourceId)) {
      throw new ComponentNotFoundException(sourceId);
    }

    if (!containsComponent(targetId)) {
      throw new ComponentNotFoundException(targetId);
    }

    ComponentConnection connection = new ComponentConnection(sourceId, targetId, type, description);
    connections.add(connection);

    logActivity(
        String.format(
            "Created %s connection from %s to %s",
            type, sourceId.getShortId(), targetId.getShortId()));

    // Raise event for the new connection
    raiseEvent(new ComponentConnectionEvent(sourceId, targetId, type, description));

    return connection;
  }

  /**
   * Removes a connection between components.
   *
   * @param connectionId The ID of the connection to remove
   * @throws ConnectionException if the connection is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public void disconnect(String connectionId) {
    if (!isModifiable()) {
      throw new InvalidOperationException("disconnect", this);
    }

    // Find the connection first so we can include its details in the exception if needed
    ComponentConnection connectionToRemove =
        connections.stream()
            .filter(conn -> conn.getConnectionId().equals(connectionId))
            .findFirst()
            .orElseThrow(
                () -> new ConnectionException("Connection not found: " + connectionId, null, null));

    connections.remove(connectionToRemove);
    logActivity("Removed connection: " + connectionId);
  }

  /**
   * Gets all connections in this composite.
   *
   * @return An unmodifiable list of connections
   */
  public List<ComponentConnection> getConnections() {
    return Collections.unmodifiableList(connections);
  }

  /**
   * Gets connections of a specific type.
   *
   * @param type The connection type to filter by
   * @return A list of connections of the specified type
   */
  public List<ComponentConnection> getConnectionsByType(ConnectionType type) {
    return connections.stream().filter(conn -> conn.getType() == type).collect(Collectors.toList());
  }

  /**
   * Gets all connections involving a specific component.
   *
   * @param componentId The component ID
   * @return A list of connections involving the component
   */
  public List<ComponentConnection> getConnectionsForComponent(ComponentId componentId) {
    return connections.stream()
        .filter(
            conn ->
                conn.getSourceId().equals(componentId) || conn.getTargetId().equals(componentId))
        .collect(Collectors.toList());
  }

  /** Activates all child components when the composite is activated. */
  @Override
  public void activate() {
    super.activate();

    // Activate all children (if they're ready)
    for (Component child : children.values()) {
      if (child.getLifecycleState() == LifecycleState.READY) {
        try {
          child.activate();
          logActivity("Activated child: " + child.getId().getShortId());
        } catch (Exception e) {
          logActivity(
              "Failed to activate child: "
                  + child.getId().getShortId()
                  + ", reason: "
                  + e.getMessage());
        }
      }
    }

    // Activate all connections
    for (ComponentConnection connection : connections) {
      connection.activate();
    }
  }

  /** Deactivates all child components when the composite is deactivated. */
  @Override
  public void deactivate() {
    // Deactivate all children first
    for (Component child : children.values()) {
      if (child.getLifecycleState() == LifecycleState.ACTIVE) {
        try {
          child.deactivate();
          logActivity("Deactivated child: " + child.getId().getShortId());
        } catch (Exception e) {
          logActivity(
              "Failed to deactivate child: "
                  + child.getId().getShortId()
                  + ", reason: "
                  + e.getMessage());
        }
      }
    }

    // Deactivate all connections
    for (ComponentConnection connection : connections) {
      connection.deactivate();
    }

    // Finally deactivate the composite itself
    super.deactivate();
  }

  /** Terminates all child components when the composite is terminated. */
  @Override
  public void terminate() {
    // Terminate all children first
    for (Component child : children.values()) {
      if (child.getLifecycleState() != LifecycleState.TERMINATED) {
        try {
          child.terminate();
          logActivity("Terminated child: " + child.getId().getShortId());
        } catch (Exception e) {
          logActivity(
              "Failed to terminate child: "
                  + child.getId().getShortId()
                  + ", reason: "
                  + e.getMessage());
        }
      }
    }

    // Then terminate the composite itself
    super.terminate();
  }

  /**
   * Checks if this composite can be modified (add/remove components or connections).
   *
   * @return true if the composite is in a modifiable state, false otherwise
   */
  private boolean isModifiable() {
    LifecycleState state = getLifecycleState();
    return state.isEarlyStage()
        || state == LifecycleState.READY
        || state == LifecycleState.ADAPTING;
  }

  @Override
  public String toString() {
    return "CompositeComponent{"
        + "id="
        + getId()
        + ", compositeType="
        + compositeType
        + ", childCount="
        + children.size()
        + ", connectionCount="
        + connections.size()
        + ", lifecycleState="
        + getLifecycleState()
        + '}';
  }
}
