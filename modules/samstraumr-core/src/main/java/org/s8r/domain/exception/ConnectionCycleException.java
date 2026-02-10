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

package org.s8r.domain.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when a connection would create a cycle in a component hierarchy. This prevents
 * infinite recursion and ensures a valid directed acyclic graph structure.
 */
public class ConnectionCycleException extends ComponentException {
  private static final long serialVersionUID = 1L;

  private final ComponentId sourceId;
  private final ComponentId targetId;
  private final List<ComponentId> cyclePath;

  /**
   * Creates a new ConnectionCycleException with the components involved in the cycle.
   *
   * @param message The error message
   * @param sourceId The ID of the source component in the new connection
   * @param targetId The ID of the target component in the new connection
   * @param cyclePath The list of component IDs that form the cycle
   */
  public ConnectionCycleException(
      String message, ComponentId sourceId, ComponentId targetId, List<ComponentId> cyclePath) {
    super(message);
    this.sourceId = sourceId;
    this.targetId = targetId;
    this.cyclePath = cyclePath;
  }

  /**
   * Gets the ID of the source component in the connection that would create a cycle.
   *
   * @return The source component ID
   */
  public ComponentId getSourceId() {
    return sourceId;
  }

  /**
   * Gets the ID of the target component in the connection that would create a cycle.
   *
   * @return The target component ID
   */
  public ComponentId getTargetId() {
    return targetId;
  }

  /**
   * Gets the list of component IDs that form the cycle.
   *
   * @return The cycle path
   */
  public List<ComponentId> getCyclePath() {
    return cyclePath;
  }

  /**
   * Creates a formatted string showing the cycle path.
   *
   * @return A string representation of the cycle path
   */
  public String getCyclePathString() {
    if (cyclePath == null || cyclePath.isEmpty()) {
      return "[]";
    }

    return cyclePath.stream().map(ComponentId::getShortId).collect(Collectors.joining(" -> "));
  }
}
