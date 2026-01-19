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

package org.s8r.test.support.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Property-based testing for cycle detection algorithms.
 *
 * <p>Expresses cycle detection tests as scenarios:
 * - Build specific graph topology
 * - Run cycle detector
 * - Verify expected cycles found
 *
 * <p>Catches Bug #7: false negatives in sparse/disconnected graphs.
 *
 * <p>Scenarios covered:
 * - Simple cycle (A→B→C→A)
 * - Multiple independent cycles
 * - Disconnected components with embedded cycles
 * - No cycles (DAG)
 * - Single-node self-cycles
 * - Complex multi-level cycles
 *
 * @param <N> Node type in graph
 */
public class CycleDetectionScenario<N> {
  private final Map<N, Set<N>> graph = new HashMap<>();
  private final String description;
  private final boolean expectedHasCycle;

  /**
   * Creates cycle detection scenario.
   *
   * @param description Human-readable scenario description
   * @param expectedHasCycle Whether this scenario should have cycles
   */
  public CycleDetectionScenario(String description, boolean expectedHasCycle) {
    this.description = description;
    this.expectedHasCycle = expectedHasCycle;
  }

  /**
   * Adds directed edge to graph.
   *
   * @param from Source node
   * @param to Target node
   * @return This scenario (for chaining)
   */
  public CycleDetectionScenario<N> addEdge(N from, N to) {
    graph.putIfAbsent(from, new HashSet<>());
    graph.putIfAbsent(to, new HashSet<>());
    graph.get(from).add(to);
    return this;
  }

  /**
   * Adds multiple edges from one node.
   *
   * @param from Source node
   * @param targets Target nodes
   * @return This scenario (for chaining)
   */
  @SafeVarargs
  public final CycleDetectionScenario<N> addEdgesFrom(N from, N... targets) {
    for (N to : targets) {
      addEdge(from, to);
    }
    return this;
  }

  /**
   * Creates simple cycle: A→B→A.
   *
   * @return Scenario with simple 2-node cycle
   */
  public static <N> CycleDetectionScenario<N> simpleCycle(N nodeA, N nodeB) {
    return new CycleDetectionScenario<>("Simple cycle A→B→A", true)
        .addEdge(nodeA, nodeB)
        .addEdge(nodeB, nodeA);
  }

  /**
   * Creates self-cycle: A→A.
   *
   * @param node Node with self-edge
   * @return Scenario with self-cycle
   */
  public static <N> CycleDetectionScenario<N> selfCycle(N node) {
    return new CycleDetectionScenario<>("Self-cycle A→A", true).addEdge(node, node);
  }

  /**
   * Creates three-node cycle: A→B→C→A.
   *
   * @return Scenario with 3-node cycle
   */
  public static <N> CycleDetectionScenario<N> threeNodeCycle(N nodeA, N nodeB, N nodeC) {
    return new CycleDetectionScenario<>("Three-node cycle A→B→C→A", true)
        .addEdge(nodeA, nodeB)
        .addEdge(nodeB, nodeC)
        .addEdge(nodeC, nodeA);
  }

  /**
   * Creates DAG (directed acyclic graph) - no cycles.
   *
   * @return Scenario with no cycles
   */
  public static <N> CycleDetectionScenario<N> dag(N nodeA, N nodeB, N nodeC) {
    return new CycleDetectionScenario<>("DAG (no cycles)", false)
        .addEdge(nodeA, nodeB)
        .addEdge(nodeB, nodeC);
  }

  /**
   * Creates disconnected graph: cycle in one component, DAG in another (Bug #7 test case).
   *
   * @return Scenario testing disconnected components
   */
  public static <N> CycleDetectionScenario<N> disconnectedWithCycle(
      N cycleA, N cycleB, N dagA, N dagB) {
    return new CycleDetectionScenario<>(
            "Disconnected graph: cycle in component 1, DAG in component 2", true)
        .addEdge(cycleA, cycleB)
        .addEdge(cycleB, cycleA)
        .addEdge(dagA, dagB);
  }

  /**
   * Gets graph representation.
   *
   * @return Unmodifiable adjacency list
   */
  public Map<N, Set<N>> getGraph() {
    return Collections.unmodifiableMap(graph);
  }

  /**
   * Gets scenario description.
   *
   * @return Human-readable description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets whether this scenario should have cycles.
   *
   * @return True if cycles expected
   */
  public boolean expectsCycle() {
    return expectedHasCycle;
  }

  /**
   * Gets node count in graph.
   *
   * @return Number of nodes
   */
  public int getNodeCount() {
    return graph.size();
  }

  /**
   * Gets edge count in graph.
   *
   * @return Number of directed edges
   */
  public int getEdgeCount() {
    int count = 0;
    for (Set<N> neighbors : graph.values()) {
      count += neighbors.size();
    }
    return count;
  }

  /**
   * Tests cycle detector against this scenario.
   *
   * <p>Verifies:
   * - If scenario expects cycle, detector finds one
   * - If scenario expects no cycle, detector returns false
   *
   * @param cycleDetector Cycle detection function: graph → has cycle?
   * @throws AssertionError if detector behavior doesn't match expectation
   */
  public void validateCycleDetector(BiFunction<Map<N, Set<N>>, N, Boolean> cycleDetector) {
    // For disconnected graphs, test from each component
    Set<N> visited = new HashSet<>();
    for (N node : graph.keySet()) {
      if (!visited.contains(node)) {
        boolean hasCycle = cycleDetector.apply(graph, node);

        if (expectedHasCycle && !hasCycle) {
          throw new AssertionError(
              "CYCLE NOT DETECTED: "
                  + description
                  + " (Bug #7 false negative) - starting from node "
                  + node);
        }
        if (!expectedHasCycle && hasCycle) {
          throw new AssertionError(
              "FALSE POSITIVE: " + description + " (detector found cycle when none expected)");
        }

        // Mark all reachable nodes as visited
        markReachable(node, visited);
      }
    }
  }

  /**
   * Tests cycle detector handles specific node as entry point.
   *
   * @param node Entry node for detection
   * @param cycleDetector Cycle detection function
   * @throws AssertionError if detector result mismatches expectation
   */
  public void validateFromNode(N node, BiFunction<Map<N, Set<N>>, N, Boolean> cycleDetector) {
    boolean hasCycle = cycleDetector.apply(graph, node);

    if (expectedHasCycle && !hasCycle) {
      throw new AssertionError(
          "CYCLE NOT DETECTED starting from " + node + ": " + description);
    }
    if (!expectedHasCycle && hasCycle) {
      throw new AssertionError(
          "FALSE POSITIVE from " + node + ": " + description);
    }
  }

  /**
   * DFS traversal to mark all reachable nodes.
   *
   * @param node Current node
   * @param visited Set of visited nodes
   */
  private void markReachable(N node, Set<N> visited) {
    if (visited.contains(node)) {
      return;
    }
    visited.add(node);

    Set<N> neighbors = graph.get(node);
    if (neighbors != null) {
      for (N neighbor : neighbors) {
        markReachable(neighbor, visited);
      }
    }
  }

  /**
   * Generates test report.
   *
   * @return Human-readable scenario report
   */
  public String generateReport() {
    return String.format(
        "%s (%d nodes, %d edges, expects%s cycle)",
        description, getNodeCount(), getEdgeCount(), expectedHasCycle ? "" : " NO");
  }

  /**
   * Standard test suite: covers common graph topologies.
   *
   * @return List of scenarios to test cycle detector against
   */
  public static List<CycleDetectionScenario<String>> standardTestSuite() {
    List<CycleDetectionScenario<String>> scenarios = new ArrayList<>();

    // Simple cases
    scenarios.add(simpleCycle("A", "B"));
    scenarios.add(selfCycle("A"));
    scenarios.add(threeNodeCycle("A", "B", "C"));
    scenarios.add(dag("A", "B", "C"));

    // Complex cases
    scenarios.add(disconnectedWithCycle("A", "B", "C", "D"));

    // Multiple independent cycles
    CycleDetectionScenario<String> multipleCycles =
        new CycleDetectionScenario<>("Two independent cycles", true)
            .addEdge("A", "B")
            .addEdge("B", "A")
            .addEdge("C", "D")
            .addEdge("D", "E")
            .addEdge("E", "C");
    scenarios.add(multipleCycles);

    return scenarios;
  }
}
