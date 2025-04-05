/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for component connections in the S8r framework
 */

package org.samstraumr.domain.component.composite;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.samstraumr.domain.identity.ComponentId;

/**
 * Represents a connection between two components in a composite structure.
 *
 * <p>This class is a domain value object that defines the relationship between components,
 * including directionality and connection type. It follows Clean Architecture principles with no
 * dependencies on infrastructure or frameworks.
 */
public final class ComponentConnection {
  private final String connectionId;
  private final ComponentId sourceId;
  private final ComponentId targetId;
  private final ConnectionType type;
  private final String description;
  private final Instant creationTime;
  private boolean active;

  /**
   * Creates a new ComponentConnection.
   *
   * @param sourceId The ID of the source component
   * @param targetId The ID of the target component
   * @param type The type of connection
   * @param description A description of the connection purpose
   */
  public ComponentConnection(
      ComponentId sourceId, ComponentId targetId, ConnectionType type, String description) {
    this.connectionId = UUID.randomUUID().toString();
    this.sourceId = Objects.requireNonNull(sourceId, "Source component ID cannot be null");
    this.targetId = Objects.requireNonNull(targetId, "Target component ID cannot be null");
    this.type = Objects.requireNonNull(type, "Connection type cannot be null");
    this.description = description != null ? description : "";
    this.creationTime = Instant.now();
    this.active = true;
  }

  /**
   * Gets the unique ID of this connection.
   *
   * @return The connection ID
   */
  public String getConnectionId() {
    return connectionId;
  }

  /**
   * Gets the ID of the source component.
   *
   * @return The source component ID
   */
  public ComponentId getSourceId() {
    return sourceId;
  }

  /**
   * Gets the ID of the target component.
   *
   * @return The target component ID
   */
  public ComponentId getTargetId() {
    return targetId;
  }

  /**
   * Gets the type of this connection.
   *
   * @return The connection type
   */
  public ConnectionType getType() {
    return type;
  }

  /**
   * Gets the description of this connection.
   *
   * @return The connection description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the creation time of this connection.
   *
   * @return The creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Checks if this connection is active.
   *
   * @return true if the connection is active, false otherwise
   */
  public boolean isActive() {
    return active;
  }

  /** Activates this connection. */
  public void activate() {
    active = true;
  }

  /** Deactivates this connection. */
  public void deactivate() {
    active = false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ComponentConnection that = (ComponentConnection) o;
    return Objects.equals(connectionId, that.connectionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connectionId);
  }

  @Override
  public String toString() {
    return "ComponentConnection{"
        + "connectionId='"
        + connectionId
        + '\''
        + ", sourceId="
        + sourceId
        + ", targetId="
        + targetId
        + ", type="
        + type
        + ", active="
        + active
        + '}';
  }
}
