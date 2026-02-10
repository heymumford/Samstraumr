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
  public ComponentId getSourceId() {
    return sourceId;
  }

  public ComponentId getTargetId() {
    return targetId;
  }

  public ConnectionType getConnectionType() {
    return connectionType;
  }

  public String getConnectionName() {
    return connectionName;
  }
}
