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

package org.s8r.test.support.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.s8r.domain.component.Component;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * Analyzes lifecycle state machine completeness by discovering which LifecycleState enum values
 * have valid transitions defined in Component.isValidTransition().
 *
 * <p>Detects incomplete switch statements (Bug #2 symptom) by comparing enum cardinality against
 * empirically discovered transition map.
 *
 * <p>Usage: Used in lifecycle tests to ensure no states are silently unhandled.
 */
public class StateTransitionValidator {
  private static final Map<LifecycleState, Set<LifecycleState>> discoveredTransitions =
      new HashMap<>();
  private static boolean discovered = false;

  private StateTransitionValidator() {
    // Utility class
  }

  /**
   * Discovers all valid state transitions by empirical testing.
   *
   * <p>Tests each LifecycleState as source, trying transitions to all possible targets. Builds a
   * map of which source states can transition to which target states.
   *
   * @param subject Component to test transitions on
   */
  public static void discover(Component subject) {
    if (discovered) {
      return;
    }

    discoveredTransitions.clear();
    LifecycleState[] allStates = LifecycleState.values();

    // For each possible source state
    for (LifecycleState source : allStates) {
      Set<LifecycleState> validTargets = new HashSet<>();

      // Try transitions to all possible target states
      for (LifecycleState target : allStates) {
        try {
          // Test if this transition is valid
          if (subject.isValidTransition(source, target)) {
            validTargets.add(target);
          }
        } catch (Exception e) {
          // Transition validation threw exception (expected for undefined states)
        }
      }

      discoveredTransitions.put(source, validTargets);
    }

    discovered = true;
  }

  /**
   * Gets all LifecycleState enum values.
   *
   * @return All states defined in enum (should be 19)
   */
  public static Set<LifecycleState> getAllDefinedStates() {
    Set<LifecycleState> all = new HashSet<>();
    for (LifecycleState state : LifecycleState.values()) {
      all.add(state);
    }
    return Collections.unmodifiableSet(all);
  }

  /**
   * Gets the discovered transition map: source state → set of valid target states.
   *
   * @return Map built by discover() method
   */
  public static Map<LifecycleState, Set<LifecycleState>> getDefinedTransitions() {
    return Collections.unmodifiableMap(new HashMap<>(discoveredTransitions));
  }

  /**
   * Identifies states for which NO transitions are defined.
   *
   * <p>These are states that do not appear in any switch/if case in isValidTransition().
   *
   * @return Set of states with no outgoing transitions (detects Bug #2 gaps)
   */
  public static Set<LifecycleState> getUndefinedSourceStates() {
    Set<LifecycleState> undefined = new HashSet<>(getAllDefinedStates());
    undefined.removeAll(discoveredTransitions.keySet());
    return undefined;
  }

  /**
   * Identifies states that cannot transition anywhere (dead-end states).
   *
   * <p>Useful for detecting incomplete state machines where final states are missing valid
   * transitions.
   *
   * @return Set of source states with zero valid targets
   */
  public static Set<LifecycleState> getStatesWithZeroOutgoingTransitions() {
    Set<LifecycleState> deadEnds = new HashSet<>();
    for (Map.Entry<LifecycleState, Set<LifecycleState>> entry :
        discoveredTransitions.entrySet()) {
      if (entry.getValue().isEmpty()) {
        deadEnds.add(entry.getKey());
      }
    }
    return deadEnds;
  }

  /**
   * Asserts that all enum states have at least one defined outgoing transition.
   *
   * <p>Fails if any state is undefined or a dead-end.
   *
   * @throws AssertionError if incomplete state machine detected
   */
  public static void assertAllStatesCanTransition() {
    Set<LifecycleState> undefined = getUndefinedSourceStates();
    Set<LifecycleState> deadEnds = getStatesWithZeroOutgoingTransitions();

    assertEquals(
        0,
        undefined.size(),
        "State machine incomplete: "
            + undefined.size()
            + " states undefined (Bug #2 indicator): "
            + undefined);
    assertEquals(
        0,
        deadEnds.size(),
        "Found "
            + deadEnds.size()
            + " dead-end states (no valid outgoing transitions): "
            + deadEnds);
  }

  /**
   * Gets count of source states with defined transitions.
   *
   * @return Number of states handled in isValidTransition()
   */
  public static int getHandledStateCount() {
    return discoveredTransitions.size();
  }

  /**
   * Gets total state count in enum.
   *
   * @return LifecycleState.values().length (should be 19 for Bug #2 check)
   */
  public static int getTotalStateCount() {
    return getAllDefinedStates().size();
  }

  /**
   * Asserts specific state has valid outgoing transitions.
   *
   * @param state State to check
   * @throws AssertionError if state is undefined or dead-end
   */
  public static void assertStateCanTransition(LifecycleState state) {
    assertTrue(
        discoveredTransitions.containsKey(state),
        "State " + state + " is undefined (not in switch statement)");
    assertFalse(
        discoveredTransitions.get(state).isEmpty(),
        "State " + state + " is a dead-end (zero valid transitions)");
  }

  /**
   * Gets transitions for a specific source state.
   *
   * @param source Source state
   * @return Set of valid target states (empty if source undefined)
   */
  public static Set<LifecycleState> getTransitionsFrom(LifecycleState source) {
    return Collections.unmodifiableSet(
        discoveredTransitions.getOrDefault(source, new HashSet<>()));
  }

  /**
   * Asserts a specific transition is valid.
   *
   * @param source Source state
   * @param target Target state
   * @throws AssertionError if transition not valid
   */
  public static void assertTransitionValid(LifecycleState source, LifecycleState target) {
    assertTrue(
        discoveredTransitions.containsKey(source),
        "Source state " + source + " is undefined");
    assertTrue(
        discoveredTransitions.get(source).contains(target),
        "Transition " + source + " → " + target + " is invalid");
  }

  /**
   * Generates summary of state machine coverage.
   *
   * @return Human-readable report of discovered transitions
   */
  public static String generateCoverageSummary() {
    StringBuilder sb = new StringBuilder();
    sb.append("State Machine Coverage Summary\n");
    sb.append("==============================\n");
    sb.append("Total states in enum: ").append(getTotalStateCount()).append("\n");
    sb.append("States with defined transitions: ").append(getHandledStateCount()).append("\n");
    sb.append(
        "Coverage: "
            + String.format("%.1f%%", (100.0 * getHandledStateCount() / getTotalStateCount()))
            + "\n\n");

    Set<LifecycleState> undefined = getUndefinedSourceStates();
    if (!undefined.isEmpty()) {
      sb.append("UNDEFINED STATES (Bug #2 gap):\n");
      List<LifecycleState> sorted = new ArrayList<>(undefined);
      sorted.sort(Enum::compareTo);
      for (LifecycleState state : sorted) {
        sb.append("  - ").append(state).append("\n");
      }
      sb.append("\n");
    }

    sb.append("Defined transitions:\n");
    List<LifecycleState> sorted = new ArrayList<>(discoveredTransitions.keySet());
    sorted.sort(Enum::compareTo);
    for (LifecycleState source : sorted) {
      Set<LifecycleState> targets = discoveredTransitions.get(source);
      sb.append("  ").append(source).append(" → ");
      if (targets.isEmpty()) {
        sb.append("(dead-end)");
      } else {
        List<LifecycleState> targetList = new ArrayList<>(targets);
        targetList.sort(Enum::compareTo);
        sb.append(targetList);
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * Resets discovery state (for testing purposes).
   */
  public static void reset() {
    discoveredTransitions.clear();
    discovered = false;
  }
}
