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

package org.s8r.domain.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.exception.ConnectionCycleException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;

/**
 * Validator for composite connections to prevent cycles and ensure valid component references. This
 * utility class provides validation for connections between components in a composite structure.
 */
public class CompositeConnectionValidator {

  /**
   * Validates a potential new connection between components, checking for cycles and ensuring
   * components exist.
   *
   * @param compositeId The ID of the composite that manages these components
   * @param sourceId The ID of the source component
   * @param targetId The ID of the target component
   * @param connectionType The type of connection
   * @param existingConnections The list of existing connections
   * @param componentExistsFunction A function that checks if a component exists
   * @throws NonExistentComponentReferenceException if a referenced component doesn't exist
   * @throws ConnectionCycleException if the connection would create a cycle
   */
  public static void validateConnection(
      ComponentId compositeId,
      ComponentId sourceId,
      ComponentId targetId,
      ConnectionType connectionType,
      List<ComponentConnection> existingConnections,
      Predicate<ComponentId> componentExistsFunction) {

    // Skip validation for certain connection types that don't form a hierarchy
    if (!isDirectedConnectionType(connectionType)) {
      return;
    }

    // First validate that both components exist
    validateComponentsExist(compositeId, sourceId, targetId, componentExistsFunction);

    // Then check if adding this connection would create a cycle
    validateNoCycles(compositeId, sourceId, targetId, existingConnections);
  }

  /**
   * Validates that all components in a connection exist.
   *
   * @param compositeId The ID of the containing composite
   * @param sourceId The source component ID
   * @param targetId The target component ID
   * @param componentExistsFunction A function that checks if a component exists
   * @throws NonExistentComponentReferenceException if a component doesn't exist
   */
  private static void validateComponentsExist(
      ComponentId compositeId,
      ComponentId sourceId,
      ComponentId targetId,
      Predicate<ComponentId> componentExistsFunction) {

    // First validate source component
    if (!componentExistsFunction.test(sourceId)) {
      throw new NonExistentComponentReferenceException("connect", compositeId, sourceId);
    }

    // Then validate target component
    if (!componentExistsFunction.test(targetId)) {
      throw new NonExistentComponentReferenceException("connect", compositeId, targetId);
    }
  }

  /**
   * Validates that adding a new connection won't create a cycle in the component graph.
   *
   * @param compositeId The ID of the composite managing these components
   * @param sourceId The source component ID for the new connection
   * @param targetId The target component ID for the new connection
   * @param existingConnections The list of existing connections
   * @throws ConnectionCycleException if a cycle would be created
   */
  private static void validateNoCycles(
      ComponentId compositeId,
      ComponentId sourceId,
      ComponentId targetId,
      List<ComponentConnection> existingConnections) {

    // Skip validation if there are no existing connections
    if (existingConnections == null || existingConnections.isEmpty()) {
      return;
    }

    // Build the adjacency graph from existing connections
    Map<ComponentId, Set<ComponentId>> graph = buildConnectionGraph(existingConnections);

    // Add the new potential connection
    addToGraph(graph, sourceId, targetId);

    // Check if there's a path from target to source (which would form a cycle)
    List<ComponentId> cyclePath = findCycle(graph, targetId, sourceId);

    if (!cyclePath.isEmpty()) {
      // Add the source and target to complete the cycle path
      cyclePath.add(0, sourceId);
      cyclePath.add(targetId);

      throw new ConnectionCycleException(
          String.format(
              "Connection from %s to %s would create a cycle: %s",
              sourceId.getShortId(),
              targetId.getShortId(),
              cyclePath.stream()
                  .map(ComponentId::getShortId)
                  .reduce((a, b) -> a + " -> " + b)
                  .orElse("")),
          sourceId,
          targetId,
          cyclePath);
    }
  }

  /**
   * Builds a connection graph from a list of connections.
   *
   * @param connections The list of connections
   * @return A map representing the connection graph, where keys are source IDs and values are sets
   *     of target IDs
   */
  private static Map<ComponentId, Set<ComponentId>> buildConnectionGraph(
      List<ComponentConnection> connections) {
    Map<ComponentId, Set<ComponentId>> graph = new HashMap<>();

    for (ComponentConnection connection : connections) {
      // Only consider directed connections
      if (isDirectedConnectionType(connection.getType())) {
        addToGraph(graph, connection.getSourceId(), connection.getTargetId());
      }
    }

    return graph;
  }

  /**
   * Adds a new edge to the connection graph.
   *
   * @param graph The connection graph
   * @param from The source component ID
   * @param to The target component ID
   */
  private static void addToGraph(
      Map<ComponentId, Set<ComponentId>> graph, ComponentId from, ComponentId to) {
    Set<ComponentId> targets = graph.getOrDefault(from, new HashSet<>());
    targets.add(to);
    graph.put(from, targets);

    // Ensure the target node exists in the graph even if it has no outgoing connections
    if (!graph.containsKey(to)) {
      graph.put(to, new HashSet<>());
    }
  }

  /**
   * Finds a cycle in the connection graph by checking if there's a path from the start to the end.
   *
   * @param graph The connection graph
   * @param start The starting component ID
   * @param end The ending component ID
   * @return A list of component IDs that form the cycle, or an empty list if no cycle is found
   */
  private static List<ComponentId> findCycle(
      Map<ComponentId, Set<ComponentId>> graph, ComponentId start, ComponentId end) {
    // Ensure both start and end nodes are in the graph, even if they have no outgoing edges
    // This is necessary to handle sparse graphs where nodes may not be keys in the map
    if (!graph.containsKey(start)) {
      graph.put(start, new HashSet<>());
    }
    if (!graph.containsKey(end)) {
      graph.put(end, new HashSet<>());
    }

    Set<ComponentId> visited = new HashSet<>();
    List<ComponentId> path = new ArrayList<>();

    if (depthFirstSearch(graph, start, end, visited, path)) {
      return path;
    }

    return Collections.emptyList();
  }

  /**
   * Performs a depth-first search to find a path between two nodes in the graph.
   *
   * @param graph The connection graph
   * @param current The current node being checked
   * @param target The target node to find
   * @param visited The set of already visited nodes
   * @param path The current path being built
   * @return true if a path is found, false otherwise
   */
  private static boolean depthFirstSearch(
      Map<ComponentId, Set<ComponentId>> graph,
      ComponentId current,
      ComponentId target,
      Set<ComponentId> visited,
      List<ComponentId> path) {

    // Mark the current node as visited
    visited.add(current);
    path.add(current);

    // If we've reached the target, we've found a path
    if (current.equals(target)) {
      return true;
    }

    // Check all adjacent nodes
    for (ComponentId adjacent : graph.getOrDefault(current, Collections.emptySet())) {
      if (!visited.contains(adjacent)) {
        if (depthFirstSearch(graph, adjacent, target, visited, path)) {
          return true;
        }
      }
    }

    // Remove the current node from the path as we backtrack
    path.remove(path.size() - 1);
    return false;
  }

  /**
   * Checks if a connection type is directed (forms a hierarchy). Some connection types like PEER or
   * SIBLING don't form hierarchical relationships.
   *
   * @param connectionType The connection type to check
   * @return true if the connection type is directed, false otherwise
   */
  private static boolean isDirectedConnectionType(ConnectionType connectionType) {
    // Use the isDirectional method to determine if a connection type forms a hierarchy
    return connectionType.isDirectional();
  }
}
