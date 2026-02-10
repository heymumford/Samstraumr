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

package org.s8r.application.dto;

import java.time.Instant;

import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;

/**
 * Data Transfer Object (DTO) for ComponentConnection entities.
 *
 * <p>This class is used to transfer connection data across architectural boundaries without
 * exposing domain entities. It follows Clean Architecture principles by keeping the domain entities
 * isolated from external concerns.
 */
public class ConnectionDto {
  private final String connectionId;
  private final String sourceId;
  private final String sourceShortId;
  private final String targetId;
  private final String targetShortId;
  private final String connectionType;
  private final String description;
  private final Instant creationTime;
  private final boolean active;

  /**
   * Creates a new ConnectionDto from a domain ComponentConnection entity.
   *
   * @param connection The domain connection entity
   */
  public ConnectionDto(ComponentConnection connection) {
    this.connectionId = connection.getConnectionId();
    this.sourceId = connection.getSourceId().getIdString();
    this.sourceShortId = connection.getSourceId().getShortId();
    this.targetId = connection.getTargetId().getIdString();
    this.targetShortId = connection.getTargetId().getShortId();

    ConnectionType type = connection.getType();
    this.connectionType = type.name();

    this.description = connection.getDescription();
    this.creationTime = connection.getCreationTime();
    this.active = connection.isActive();
  }

  /**
   * Creates a new ConnectionDto with the specified values.
   *
   * @param connectionId The connection ID
   * @param sourceId The source component ID
   * @param sourceShortId The source component short ID
   * @param targetId The target component ID
   * @param targetShortId The target component short ID
   * @param connectionType The connection type
   * @param description The connection description
   * @param creationTime The connection creation time
   * @param active Whether the connection is active
   */
  public ConnectionDto(
      String connectionId,
      String sourceId,
      String sourceShortId,
      String targetId,
      String targetShortId,
      String connectionType,
      String description,
      Instant creationTime,
      boolean active) {
    this.connectionId = connectionId;
    this.sourceId = sourceId;
    this.sourceShortId = sourceShortId;
    this.targetId = targetId;
    this.targetShortId = targetShortId;
    this.connectionType = connectionType;
    this.description = description;
    this.creationTime = creationTime;
    this.active = active;
  }

  // Getter methods

  /**
   * Gets the connection ID.
   *
   * @return The connection ID
   */
  public String getConnectionId() {
    return connectionId;
  }

  /**
   * Gets the source component ID.
   *
   * @return The source component ID
   */
  public String getSourceId() {
    return sourceId;
  }

  /**
   * Gets the source component short ID.
   *
   * @return The source component short ID
   */
  public String getSourceShortId() {
    return sourceShortId;
  }

  /**
   * Gets the target component ID.
   *
   * @return The target component ID
   */
  public String getTargetId() {
    return targetId;
  }

  /**
   * Gets the target component short ID.
   *
   * @return The target component short ID
   */
  public String getTargetShortId() {
    return targetShortId;
  }

  /**
   * Gets the connection type.
   *
   * @return The connection type
   */
  public String getConnectionType() {
    return connectionType;
  }

  /**
   * Gets the connection description.
   *
   * @return The connection description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the connection creation time.
   *
   * @return The creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Checks if the connection is active.
   *
   * @return true if active, false otherwise
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Factory method to create a ConnectionDto from a domain ComponentConnection entity.
   *
   * @param connection The domain connection entity
   * @return A new ConnectionDto or null if the connection is null
   */
  public static ConnectionDto fromDomain(ComponentConnection connection) {
    if (connection == null) {
      return null;
    }
    return new ConnectionDto(connection);
  }

  @Override
  public String toString() {
    return "ConnectionDto{"
        + "id='"
        + connectionId
        + '\''
        + ", sourceId='"
        + sourceShortId
        + '\''
        + ", targetId='"
        + targetShortId
        + '\''
        + ", type='"
        + connectionType
        + '\''
        + ", active="
        + active
        + '}';
  }
}
