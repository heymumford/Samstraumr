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

package org.s8r.domain.component.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Port interface for composite components in the domain layer.
 *
 * <p>This interface extends the basic ComponentPort with operations specific to composite
 * components, which can contain and manage other components.
 */
public interface CompositeComponentPort extends ComponentPort {

  /**
   * Gets the composite's identifier.
   *
   * @return The composite ID string
   */
  String getCompositeId();

  /**
   * Adds a component to this composite.
   *
   * @param name The name for the component within this composite
   * @param component The component to add
   * @return true if the component was added successfully, false otherwise
   */
  boolean addComponent(String name, ComponentPort component);

  /**
   * Removes a component from this composite.
   *
   * @param name The name of the component to remove
   * @return The removed component, or empty if not found
   */
  Optional<ComponentPort> removeComponent(String name);

  /**
   * Gets a component by name.
   *
   * @param name The name of the component to retrieve
   * @return The component
   */
  ComponentPort getComponent(String name);

  /**
   * Checks if this composite contains a component with the given name.
   *
   * @param name The component name to check
   * @return true if the composite contains the component, false otherwise
   */
  boolean hasComponent(String name);

  /**
   * Gets all components in this composite.
   *
   * @return A map of component names to components
   */
  Map<String, ComponentPort> getComponents();

  /**
   * Connects two components within this composite.
   *
   * @param sourceName The name of the source component
   * @param targetName The name of the target component
   * @return true if the connection was established, false otherwise
   */
  boolean connect(String sourceName, String targetName);

  /**
   * Disconnects two components within this composite.
   *
   * @param sourceName The name of the source component
   * @param targetName The name of the target component
   * @return true if the connection was removed, false otherwise
   */
  boolean disconnect(String sourceName, String targetName);

  /**
   * Gets all connections in this composite.
   *
   * @return A map of source component names to lists of target component names
   */
  Map<String, List<String>> getConnections();

  /**
   * Gets the connections from a specific component.
   *
   * @param sourceName The name of the source component
   * @return A list of target component names, or empty list if none
   */
  List<String> getConnectionsFrom(String sourceName);
}
