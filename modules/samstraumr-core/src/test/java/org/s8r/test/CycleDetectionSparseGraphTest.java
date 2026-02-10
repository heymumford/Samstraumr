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

package org.s8r.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.exception.ConnectionCycleException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.validation.CompositeConnectionValidator;

/**
 * Detects Bug #7: Cycle detection failure in sparse component graphs.
 *
 * <p>CompositeConnectionValidator's DFS implementation has issues detecting cycles in sparse graphs
 * where nodes may not have outgoing edges. The problem manifests when: - A target node exists but
 * isn't explicitly in the graph keys (no outgoing edges) - The DFS validation checks
 * `!graph.containsKey(start)` or `!graph.containsKey(end)` - This check fails for nodes with no
 * outgoing connections, even though they exist - As a result, cycles are not detected in sparse or
 * disconnected graph regions
 *
 * <p>The proper fix is to ensure all relevant nodes are in the graph structure during cycle
 * detection, not just nodes with outgoing connections.
 */
@DisplayName("Bug #7: Cycle detection in sparse component graphs")
@Tag("ATL")
@Tag("L1_Component")
@Tag("validation")
public class CycleDetectionSparseGraphTest {

  private ComponentId compositeId;
  private ComponentId nodeA;
  private ComponentId nodeB;
  private ComponentId nodeC;
  private ComponentId nodeD;

  @BeforeEach
  void setup() {
    compositeId = ComponentId.create("composite-test");
    nodeA = ComponentId.create("node-a");
    nodeB = ComponentId.create("node-b");
    nodeC = ComponentId.create("node-c");
    nodeD = ComponentId.create("node-d");
  }

  @Test
  @DisplayName("Should detect cycle when target node has no outgoing edges")
  void testCycleDetectionWithLeafNodeTarget() {
    // Create a sparse graph: A -> B -> C (C is a leaf with no outgoing edges)
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeB, nodeC, ConnectionType.DATA_FLOW));

    // Attempting to add C -> A should create a cycle
    // But the algorithm might fail to detect this because C is a leaf node
    // with no outgoing edges in the existing graph
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeC,
                nodeA,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should detect cycle in disconnected graph regions")
  void testCycleDetectionInDisconnectedRegions() {
    // Create two disconnected regions: A->B and C->D
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeC, nodeD, ConnectionType.DATA_FLOW));

    // Attempting to add D -> C should create a cycle in the C->D region
    // The cycle detection needs to work in disconnected graph components
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeD,
                nodeC,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should detect cycle when connecting from isolated source to path target")
  void testCycleWithIsolatedSourceToPathTarget() {
    // Create a path: B -> C -> D where B, C, D are isolated from A
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeB, nodeC, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeC, nodeD, ConnectionType.DATA_FLOW));

    // Attempting to add D -> B should create a cycle even though we're connecting
    // from an intermediate node in the path
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeD,
                nodeB,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should detect cycle in complex sparse graph topology")
  void testCycleInComplexSparseGraph() {
    // Create a complex sparse graph:
    // Region 1: A -> B
    // Region 2: C -> D
    // Connect them: B -> D
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeC, nodeD, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeB, nodeD, ConnectionType.DATA_FLOW));

    // Attempting to add D -> C should create a cycle:
    // C -> D -> (B -> D creates alternative path, but main cycle is) C -> D -> ... back to C
    // This requires proper DFS that handles all graph nodes, not just keys with outgoing edges
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeD,
                nodeC,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should not detect cycle when no cycle exists in sparse graph")
  void testNoFalsePositivesInSparseGraph() {
    // Create a sparse graph: A -> B, C -> D (no connections between regions)
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeC, nodeD, ConnectionType.DATA_FLOW));

    // Adding B -> C should NOT create a cycle (just connects two regions)
    assertDoesNotThrow(
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeB,
                nodeC,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should properly handle graph with single-node chains")
  void testCycleDetectionWithSingleNodeChains() {
    // Create: A -> B, where both are leaf nodes (no outgoing edges for B)
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));

    // Adding B -> A should create a cycle
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeB,
                nodeA,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  @Test
  @DisplayName("Should detect cycle when new connection closes a long path")
  void testCycleDetectionWithLongPath() {
    ComponentId nodeE = ComponentId.create("node-e");

    // Create a long path: A -> B -> C -> D -> E
    List<ComponentConnection> connections = new ArrayList<>();
    connections.add(createConnection(nodeA, nodeB, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeB, nodeC, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeC, nodeD, ConnectionType.DATA_FLOW));
    connections.add(createConnection(nodeD, nodeE, ConnectionType.DATA_FLOW));

    // Adding E -> A should create a cycle
    // Requires DFS to correctly traverse from E (leaf node) back to A
    assertThrows(
        ConnectionCycleException.class,
        () ->
            CompositeConnectionValidator.validateConnection(
                compositeId,
                nodeE,
                nodeA,
                ConnectionType.DATA_FLOW,
                connections,
                id -> true)); // All components exist
  }

  /** Helper to create a connection between two nodes. */
  private ComponentConnection createConnection(
      ComponentId source, ComponentId target, ConnectionType type) {
    return new ComponentConnection(source, target, type, "test-connection");
  }
}
