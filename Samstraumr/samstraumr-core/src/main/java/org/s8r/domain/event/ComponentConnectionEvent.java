/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Component connection event for the S8r framework
 */

package org.s8r.domain.event;

import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.identity.ComponentId;

/** Event raised when components are connected to each other. */
public class ComponentConnectionEvent extends DomainEvent {
  private final ComponentId sourceId;
  private final ComponentId targetId;
  private final ConnectionType connectionType;
  private final String connectionName;

  /** Creates a new component connection event. */
  public ComponentConnectionEvent(
      ComponentId sourceId,
      ComponentId targetId,
      ConnectionType connectionType,
      String connectionName) {
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.connectionType = connectionType;
    this.connectionName = connectionName;
  }

  // Getters
  public ComponentId getSourceId() { return sourceId; }
  public ComponentId getTargetId() { return targetId; }
  public ConnectionType getConnectionType() { return connectionType; }
  public String getConnectionName() { return connectionName; }
}
