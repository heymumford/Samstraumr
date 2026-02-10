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

package org.s8r.application.port.consciousness;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for identity chain construction and querying.
 *
 * <p>This port synthesizes the three philosophical models of identity:
 *
 * <ul>
 *   <li><b>Substrate Identity</b>: UUID, conception timestamp, parent lineage
 *   <li><b>Memory Identity</b>: State narrative, experience history, learned behaviors
 *   <li><b>Narrative Identity</b>: Self-description, relationship network, purpose
 * </ul>
 *
 * <p>This supports Experiment 1: "Systems maintaining identity chains with temporal context enable
 * faster root cause analysis than systems with traditional logging."
 *
 * <p>Traditional logging: timestamp, level, message, trace ID
 *
 * <p>Identity chain logging: UUID, lineage, state narrative, relationship network, decision
 * rationale
 */
public interface IdentityChainPort {

  /**
   * Gets the complete identity chain for a component.
   *
   * @param componentId the component identifier
   * @return the identity chain, or empty if not found
   */
  Optional<IdentityChain> getIdentityChain(String componentId);

  /**
   * Builds an identity chain from a component's current state.
   *
   * <p>This captures a snapshot of the component's complete identity at this moment.
   *
   * @param componentId the component identifier
   * @return the built identity chain
   */
  IdentityChain buildIdentityChain(String componentId);

  /**
   * Gets the lineage for a component (from Adam to current).
   *
   * @param componentId the component identifier
   * @return the lineage list
   */
  List<String> getLineage(String componentId);

  /**
   * Traces lineage back to the Adam component.
   *
   * @param componentId the starting component identifier
   * @return the Adam component identifier
   */
  String traceToAdam(String componentId);

  /**
   * Checks if two components share a common ancestor.
   *
   * @param componentId1 first component identifier
   * @param componentId2 second component identifier
   * @return true if they share a common ancestor
   */
  boolean shareCommonAncestor(String componentId1, String componentId2);

  /**
   * Gets all descendants of a component.
   *
   * @param componentId the component identifier
   * @return the list of descendant identifiers
   */
  List<String> getDescendants(String componentId);

  /**
   * Gets siblings of a component (same parent).
   *
   * @param componentId the component identifier
   * @return the list of sibling identifiers
   */
  List<String> getSiblings(String componentId);

  /**
   * Updates the state narrative for a component.
   *
   * @param componentId the component identifier
   * @param narrative the new state narrative
   * @param rationale the reason for the update
   */
  void updateStateNarrative(String componentId, String narrative, String rationale);

  /**
   * Records a decision in the component's identity chain.
   *
   * @param componentId the component identifier
   * @param decision the decision point to record
   */
  void recordDecision(String componentId, DecisionPoint decision);

  /**
   * Gets recent decisions from a component's identity chain.
   *
   * @param componentId the component identifier
   * @param limit maximum number of decisions to return
   * @return the list of recent decision descriptions
   */
  List<String> getRecentDecisions(String componentId, int limit);

  /**
   * Records a learned behavior for a component.
   *
   * @param componentId the component identifier
   * @param behaviorKey the behavior identifier
   * @param behaviorValue the behavior data
   */
  void recordLearnedBehavior(String componentId, String behaviorKey, Object behaviorValue);

  /**
   * Gets the hierarchical address for a component.
   *
   * @param componentId the component identifier
   * @return the hierarchical address (M{machine}.B{bundle}.C{component})
   */
  Optional<String> getHierarchicalAddress(String componentId);
}
