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

package org.s8r.application.port;

import java.util.List;
import java.util.Optional;

import org.s8r.domain.component.Component;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;

/**
 * Port interface for component persistence operations.
 *
 * <p>This interface defines the contract for storing and retrieving Component entities. Following
 * the ports and adapters pattern, this is an output port in the application layer, which will be
 * implemented by adapters in the infrastructure layer.
 */
public interface ComponentRepository {

  /**
   * Saves a component.
   *
   * @param component The component to save
   */
  void save(ComponentPort component);

  /**
   * Finds a component by its ID.
   *
   * @param id The component ID to find
   * @return An Optional containing the component if found, or empty if not found
   */
  Optional<ComponentPort> findById(ComponentId id);

  /**
   * Finds all components.
   *
   * @return A list of all components
   */
  List<ComponentPort> findAll();

  /**
   * Finds child components for a parent component.
   *
   * @param parentId The parent component ID
   * @return A list of child components
   */
  List<ComponentPort> findChildren(ComponentId parentId);

  /**
   * Deletes a component.
   *
   * @param id The ID of the component to delete
   */
  void delete(ComponentId id);
}
