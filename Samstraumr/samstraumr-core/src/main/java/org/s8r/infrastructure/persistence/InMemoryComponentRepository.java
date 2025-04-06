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
import org.s8r.domain.component.Component;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * In-memory implementation of the ComponentRepository interface.
 *
 * <p>This infrastructure component provides a simple in-memory storage for components, primarily for
 * testing or simple applications. It follows Clean Architecture principles by implementing an application
 * layer port.
 */
public class InMemoryComponentRepository implements ComponentRepository {
  // Using a map to store components with their IDs as keys
  private final Map<String, Component> componentStore = new HashMap<>();

  @Override
  public void save(Component component) {
    componentStore.put(component.getId().getIdString(), component);
  }

  @Override
  public Optional<Component> findById(ComponentId id) {
    return Optional.ofNullable(componentStore.get(id.getIdString()));
  }

  @Override
  public List<Component> findAll() {
    return new ArrayList<>(componentStore.values());
  }

  /**
   * Finds components by their lifecycle state.
   *
   * @param state The lifecycle state to filter by
   * @return A list of components in the given state
   */
  public List<Component> findByState(LifecycleState state) {
    return componentStore.values().stream()
        .filter(component -> component.getLifecycleState() == state)
        .collect(Collectors.toList());
  }

  @Override
  public List<Component> findChildren(ComponentId parentId) {
    if (parentId == null) {
      return new ArrayList<>();
    }

    String parentIdStr = parentId.getIdString();
    return componentStore.values().stream()
        .filter(
            component ->
                component.getLineage().stream()
                    .anyMatch(entry -> entry.contains(parentIdStr)))
        .collect(Collectors.toList());
  }

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