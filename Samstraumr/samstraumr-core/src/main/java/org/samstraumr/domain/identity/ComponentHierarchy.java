/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain implementation of Component Hierarchy in the S8r framework
 */

package org.samstraumr.domain.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the hierarchical relationship between components.
 *
 * <p>This class manages parent-child relationships and hierarchical addressing of components in the
 * system, following Clean Architecture principles with no dependencies on infrastructure or
 * frameworks.
 */
public class ComponentHierarchy {
  private final ComponentId id;
  private final ComponentId parentId;
  private final List<ComponentId> childrenIds;
  private final String hierarchicalAddress;
  private final boolean isRoot;

  /**
   * Creates a root hierarchy (no parent).
   *
   * @param id The component ID
   * @return A new root hierarchy
   */
  public static ComponentHierarchy createRoot(ComponentId id) {
    return new ComponentHierarchy(id, null, new ArrayList<>(), "CO<" + id.getShortId() + ">", true);
  }

  /**
   * Creates a child hierarchy with a parent.
   *
   * @param id The component ID
   * @param parentHierarchy The parent's hierarchy
   * @return A new child hierarchy
   */
  public static ComponentHierarchy createChild(ComponentId id, ComponentHierarchy parentHierarchy) {
    Objects.requireNonNull(parentHierarchy, "Parent hierarchy cannot be null");

    String hierarchicalAddress =
        parentHierarchy.getHierarchicalAddress() + ".CO<" + id.getShortId() + ">";

    return new ComponentHierarchy(
        id, parentHierarchy.getId(), new ArrayList<>(), hierarchicalAddress, false);
  }

  private ComponentHierarchy(
      ComponentId id,
      ComponentId parentId,
      List<ComponentId> childrenIds,
      String hierarchicalAddress,
      boolean isRoot) {
    this.id = Objects.requireNonNull(id, "Component ID cannot be null");
    this.parentId = parentId; // Can be null for root components
    this.childrenIds = childrenIds;
    this.hierarchicalAddress =
        Objects.requireNonNull(hierarchicalAddress, "Hierarchical address cannot be null");
    this.isRoot = isRoot;
  }

  /**
   * Gets the component ID.
   *
   * @return The component ID
   */
  public ComponentId getId() {
    return id;
  }

  /**
   * Gets the parent component ID.
   *
   * @return The parent ID or null if this is a root component
   */
  public ComponentId getParentId() {
    return parentId;
  }

  /**
   * Gets the hierarchical address of this component.
   *
   * @return The hierarchical address string
   */
  public String getHierarchicalAddress() {
    return hierarchicalAddress;
  }

  /**
   * Checks if this component is a root component (no parent).
   *
   * @return true if this is a root component, false otherwise
   */
  public boolean isRoot() {
    return isRoot;
  }

  /**
   * Gets the list of child component IDs.
   *
   * @return An unmodifiable list of child IDs
   */
  public List<ComponentId> getChildrenIds() {
    return Collections.unmodifiableList(childrenIds);
  }

  /**
   * Adds a child component ID.
   *
   * @param childId The child component ID to add
   */
  public void addChild(ComponentId childId) {
    Objects.requireNonNull(childId, "Child ID cannot be null");
    childrenIds.add(childId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ComponentHierarchy that = (ComponentHierarchy) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ComponentHierarchy{"
        + "id="
        + id
        + ", hierarchicalAddress='"
        + hierarchicalAddress
        + '\''
        + ", isRoot="
        + isRoot
        + ", childCount="
        + childrenIds.size()
        + '}';
  }
}
