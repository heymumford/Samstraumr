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

package org.s8r.domain.component.composite;

import org.s8r.domain.exception.ComponentException;
import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when an error occurs with component connections.
 *
 * <p>This exception is used for errors related to creating, managing, or using connections between
 * components in a composite structure.
 */
public class ConnectionException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final String connectionId;
  private final ComponentId sourceId;
  private final ComponentId targetId;

  /**
   * Creates a new ConnectionException with information about the connection.
   *
   * @param message The error message
   * @param connectionId The ID of the connection
   * @param sourceId The source component ID
   * @param targetId The target component ID
   */
  public ConnectionException(
      String message, String connectionId, ComponentId sourceId, ComponentId targetId) {
    super(message);
    this.connectionId = connectionId;
    this.sourceId = sourceId;
    this.targetId = targetId;
  }

  /**
   * Creates a new ConnectionException for a connection that hasn't been created yet.
   *
   * @param message The error message
   * @param sourceId The source component ID
   * @param targetId The target component ID
   */
  public ConnectionException(String message, ComponentId sourceId, ComponentId targetId) {
    super(message);
    this.connectionId = null;
    this.sourceId = sourceId;
    this.targetId = targetId;
  }

  /**
   * Creates a new ConnectionException for an existing connection.
   *
   * @param message The error message
   * @param connection The connection that caused the error
   */
  public ConnectionException(String message, ComponentConnection connection) {
    super(message);
    this.connectionId = connection.getConnectionId();
    this.sourceId = connection.getSourceId();
    this.targetId = connection.getTargetId();
  }

  /**
   * Gets the ID of the connection that caused the error.
   *
   * @return The connection ID, or null if the connection hasn't been created yet
   */
  public String getConnectionId() {
    return connectionId;
  }

  /**
   * Gets the source component ID.
   *
   * @return The source component ID
   */
  public ComponentId getSourceId() {
    return sourceId;
  }

  /**
   * Gets the target component ID.
   *
   * @return The target component ID
   */
  public ComponentId getTargetId() {
    return targetId;
  }
}
