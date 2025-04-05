/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * In-memory implementation of ComponentRepository for the S8r framework
 */

package org.samstraumr.adapter.out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.samstraumr.application.port.ComponentRepository;
import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.exception.DuplicateComponentException;
import org.samstraumr.domain.identity.ComponentId;

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

  // Using a map to track parent-child relationships
  private final Map<String, List<String>> parentChildMap = new HashMap<>();

  @Override
  public void save(Component component) {
    String idString = component.getId().getIdString();
    // Prevent duplicate components during creation (if we want to enforce this)
    // Only throw if we're adding a new component, not updating an existing one
    if (!componentStore.containsKey(idString)
        && component.getLifecycleState().isEarlyStage()
        && findById(component.getId()).isPresent()) {
      throw new DuplicateComponentException(component.getId());
    }
    componentStore.put(idString, component);
  }

  @Override
  public Optional<Component> findById(ComponentId id) {
    return Optional.ofNullable(componentStore.get(id.getIdString()));
  }

  @Override
  public List<Component> findAll() {
    return new ArrayList<>(componentStore.values());
  }

  @Override
  public List<Component> findChildren(ComponentId parentId) {
    List<String> childIds = parentChildMap.getOrDefault(parentId.getIdString(), new ArrayList<>());
    return childIds.stream()
        .map(componentStore::get)
        .filter(component -> component != null)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(ComponentId id) {
    componentStore.remove(id.getIdString());

    // Remove any parent-child relationships
    parentChildMap.remove(id.getIdString());
    for (Map.Entry<String, List<String>> entry : parentChildMap.entrySet()) {
      entry.getValue().remove(id.getIdString());
    }
  }

  /**
   * Registers a parent-child relationship between components.
   *
   * @param parentId The parent component ID
   * @param childId The child component ID
   */
  public void registerParentChildRelationship(ComponentId parentId, ComponentId childId) {
    String parentIdStr = parentId.getIdString();
    String childIdStr = childId.getIdString();

    parentChildMap.computeIfAbsent(parentIdStr, k -> new ArrayList<>()).add(childIdStr);
  }

  /** Clears all components from the repository. */
  public void clear() {
    componentStore.clear();
    parentChildMap.clear();
  }
}
