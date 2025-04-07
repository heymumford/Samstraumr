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

package org.s8r.adapter.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.s8r.adapter.ComponentAdapter;
import org.s8r.application.port.ComponentRepository;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * In-memory implementation of the ComponentRepository interface.
 *
 * <p>This adapter provides a simple in-memory storage for components, primarily for testing or
 * simple applications. It follows Clean Architecture principles by implementing an application
 * layer port.
 */
public class InMemoryComponentRepository implements ComponentRepository {
  // Using a map to store components with their IDs as keys
  private final Map<String, Component> componentStore = new HashMap<>();

  /**
   * Saves a component.
   *
   * @param component The component port to save
   */
  @Override
  public void save(ComponentPort componentPort) {
    // We can only store concrete component instances
    if (componentPort instanceof ComponentAdapter.ComponentToPortAdapter) {
      Component component = ((ComponentAdapter.ComponentToPortAdapter) componentPort).getComponent();
      componentStore.put(component.getId().getIdString(), component);
    } else {
      throw new IllegalArgumentException("Cannot save non-adapter component: " + componentPort.getClass().getName());
    }
  }

  /**
   * Finds a component by its ID.
   *
   * @param id The component ID to find
   * @return An Optional containing the component port if found, or empty if not found
   */
  @Override
  public Optional<ComponentPort> findById(ComponentId id) {
    Component component = componentStore.get(id.getIdString());
    return Optional.ofNullable(component)
        .map(ComponentAdapter::createComponentPort);
  }

  /**
   * Finds all components.
   *
   * @return A list of all component ports
   */
  @Override
  public List<ComponentPort> findAll() {
    return componentStore.values().stream()
        .map(ComponentAdapter::createComponentPort)
        .collect(Collectors.toList());
  }

  /**
   * Finds components by lifecycle state.
   *
   * @param state The lifecycle state to filter by
   * @return A list of components in the specified state
   */
  public List<ComponentPort> findByState(LifecycleState state) {
    return componentStore.values().stream()
        .filter(component -> component.getLifecycleState() == state)
        .map(ComponentAdapter::createComponentPort)
        .collect(Collectors.toList());
  }

  /**
   * Finds child components for a parent component.
   *
   * @param parentId The parent component ID
   * @return A list of child component ports
   */
  @Override
  public List<ComponentPort> findChildren(ComponentId parentId) {
    if (parentId == null) {
      return new ArrayList<>();
    }

    String parentIdStr = parentId.getIdString();
    return componentStore.values().stream()
        .filter(
            component ->
                component.getLineage().stream()
                    .anyMatch(entry -> entry.contains(parentIdStr)))
        .map(ComponentAdapter::createComponentPort)
        .collect(Collectors.toList());
  }

  /**
   * Deletes a component.
   *
   * @param id The ID of the component to delete
   */
  @Override
  public void delete(ComponentId id) {
    componentStore.remove(id.getIdString());
  }

  /** Clears all components from the repository. */
  public void clear() {
    componentStore.clear();
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