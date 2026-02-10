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

package org.s8r.infrastructure.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * In-memory implementation of the ComponentRepository interface.
 *
 * <p>This infrastructure component provides a simple in-memory storage for components, primarily
 * for testing or simple applications. It follows Clean Architecture principles by implementing an
 * application layer port.
 */
public class InMemoryComponentRepository implements ComponentRepository {
  // Using a map to store component ports with their IDs as keys
  private final Map<String, ComponentPort> componentStore = new HashMap<>();
  private final LoggerPort logger;

  /**
   * Creates a new InMemoryComponentRepository.
   *
   * @param logger Logger for recording operations
   */
  public InMemoryComponentRepository(LoggerPort logger) {
    this.logger = logger;
    logger.debug("Initialized InMemoryComponentRepository");
  }

  /**
   * Checks if a component with the given ID already exists.
   *
   * @param id The component ID to check
   * @return true if a component with this ID exists, false otherwise
   */
  public boolean exists(ComponentId id) {
    return componentStore.containsKey(id.getIdString());
  }

  /**
   * Updates an existing component. This method should be used when you know the component already
   * exists and you want to update it.
   *
   * @param component The component to update
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void update(ComponentPort component) {
    String idString = component.getId().getIdString();
    if (!componentStore.containsKey(idString)) {
      throw new org.s8r.domain.exception.ComponentNotFoundException(component.getId());
    }

    componentStore.put(idString, component);
    logger.debug("Updated component: {}", idString);
  }

  @Override
  public void save(ComponentPort component) {
    // Check for duplicate components
    String idString = component.getId().getIdString();
    if (componentStore.containsKey(idString)) {
      logger.error("Attempt to save duplicate component: {}", idString);
      throw new org.s8r.domain.exception.DuplicateComponentException(component.getId());
    }

    componentStore.put(idString, component);
    logger.debug("Saved component: {}", idString);
  }

  @Override
  public Optional<ComponentPort> findById(ComponentId id) {
    return Optional.ofNullable(componentStore.get(id.getIdString()));
  }

  @Override
  public List<ComponentPort> findAll() {
    return new ArrayList<>(componentStore.values());
  }

  /**
   * Finds components by their lifecycle state.
   *
   * @param state The lifecycle state to filter by
   * @return A list of components in the given state
   */
  public List<ComponentPort> findByState(LifecycleState state) {
    return componentStore.values().stream()
        .filter(component -> component.getLifecycleState() == state)
        .collect(Collectors.toList());
  }

  @Override
  public List<ComponentPort> findChildren(ComponentId parentId) {
    if (parentId == null) {
      return new ArrayList<>();
    }

    String parentIdStr = parentId.getIdString();
    return componentStore.values().stream()
        .filter(
            component ->
                component.getLineage().stream().anyMatch(entry -> entry.contains(parentIdStr)))
        .collect(Collectors.toList());
  }

  @Override
  public void delete(ComponentId id) {
    componentStore.remove(id.getIdString());
    logger.debug("Deleted component: {}", id.getIdString());
  }

  /** Clears all components from the repository. */
  public void clear() {
    componentStore.clear();
    logger.debug("Cleared all components");
  }

  /**
   * Gets the number of components in the repository.
   *
   * @return The number of components
   */
  public int size() {
    return componentStore.size();
  }
}
