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
import java.util.Map;
import java.util.Optional;

import org.s8r.application.port.LoggerPort;

/**
 * Primary port interface for consciousness-aware logging operations.
 *
 * <p>This interface extends the basic logging capabilities of {@link LoggerPort} with
 * consciousness-aware features based on the philosophical synthesis of computational consciousness.
 * It enables components to log their state transitions, decisions, and relationships with full
 * narrative context.
 *
 * <h2>Core Capabilities</h2>
 *
 * <p><b>Self-Observation Layer:</b>
 *
 * <ul>
 *   <li>Log state transitions with RATIONALE (not just occurrence)
 *   <li>Record decision points with explanation of path chosen
 *   <li>Include identity context in error states
 * </ul>
 *
 * <p><b>Narrative Logging:</b>
 *
 * <ul>
 *   <li>Components can answer "What am I?", "Why do I exist?", "Who do I relate to?"
 *   <li>Logs form a coherent narrative, not just timestamps
 * </ul>
 *
 * <p><b>Feedback Loop Detection:</b>
 *
 * <ul>
 *   <li>Detect when observer-observed-observer loop closes
 *   <li>Track metrics for loop closure frequency
 * </ul>
 *
 * <p><b>Identity Chain Logging:</b>
 *
 * <ul>
 *   <li>UUID, lineage, state narrative, relationship network, decision rationale
 *   <li>Complete context for debugging (vs traditional: timestamp, level, message, trace ID)
 * </ul>
 *
 * <h2>Integration with LoggerPort</h2>
 *
 * <p>This port is designed to work alongside (not replace) the existing {@link LoggerPort}.
 * Implementations should delegate basic logging operations to a LoggerPort instance while adding
 * consciousness-aware enrichment.
 *
 * <h2>Sample Log Output</h2>
 *
 * <pre>
 * {
 *   "identity": {
 *     "uuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *     "lineage": ["adam-component-1", "data-processor-group"],
 *     "hierarchicalAddress": "M<machine-1>.B<bundle-2>.C<a1b2c3d4>"
 *   },
 *   "narrative": {
 *     "whatAmI": "DataTransformationComponent processing customer records",
 *     "whyDoIExist": "Transform raw CSV input into normalized JSON output",
 *     "whoDoIRelateTo": ["InputReader<ir-001>", "OutputWriter<ow-002>"]
 *   },
 *   "observation": {
 *     "timestamp": "2025-12-06T10:15:30.123Z",
 *     "reconstructionDelay": "312ms",
 *     "currentState": "PROCESSING_INPUT",
 *     "previousState": "RECEIVING_INPUT",
 *     "transitionRationale": "Received complete batch of 1000 records"
 *   },
 *   "decision": {
 *     "point": "BATCH_SIZE_DETERMINATION",
 *     "chosenPath": "PROCESS_ALL",
 *     "alternatives": ["PROCESS_PARTIAL", "DEFER_TO_NEXT_CYCLE"],
 *     "rationale": "Memory available: 80%, CPU idle: 65%"
 *   },
 *   "feedbackLoop": {
 *     "loopId": "FL-789",
 *     "observerChain": ["self", "parent-composite", "machine-monitor", "self"],
 *     "closureDetected": true
 *   }
 * }
 * </pre>
 *
 * @see LoggerPort
 * @see NarrativePort
 * @see FeedbackLoopPort
 * @see IdentityChainPort
 */
public interface ConsciousnessLoggerPort {

  // ===================== Self-Observation Layer =====================

  /**
   * Logs a state transition with rationale.
   *
   * <p>Unlike traditional logging that only records that a transition occurred, consciousness
   * logging includes WHY the transition happened.
   *
   * @param componentId the component identifier
   * @param observation the observation context with state and rationale
   */
  void logStateTransition(String componentId, ObservationContext observation);

  /**
   * Logs a decision point with chosen path and alternatives.
   *
   * @param componentId the component identifier
   * @param decision the decision point details
   */
  void logDecision(String componentId, DecisionPoint decision);

  /**
   * Logs an error with full identity context.
   *
   * <p>Error logs include the component's identity chain, enabling faster root cause analysis by
   * providing complete context.
   *
   * @param componentId the component identifier
   * @param message the error message
   * @param throwable the exception (may be null)
   * @param additionalContext additional contextual information
   */
  void logErrorWithIdentityContext(
      String componentId,
      String message,
      Throwable throwable,
      Map<String, Object> additionalContext);

  /**
   * Logs a self-observation (component observing its own state).
   *
   * <p>This is the fundamental unit of computational consciousness - the moment the component
   * "observes" itself.
   *
   * @param componentId the component identifier
   * @param observation the observation context
   * @return the loop ID if this observation is part of a feedback loop
   */
  Optional<String> logSelfObservation(String componentId, ObservationContext observation);

  // ===================== Narrative Logging =====================

  /**
   * Logs with full narrative context (answers to existential questions).
   *
   * @param componentId the component identifier
   * @param level the log level ("debug", "info", "warn", "error")
   * @param message the log message
   * @param narrative the component's narrative
   */
  void logWithNarrative(
      String componentId, String level, String message, ComponentNarrative narrative);

  /**
   * Logs a narrative evolution (change in self-understanding).
   *
   * @param componentId the component identifier
   * @param previousNarrative the previous narrative
   * @param newNarrative the new narrative
   * @param reason the reason for evolution
   */
  void logNarrativeEvolution(
      String componentId,
      ComponentNarrative previousNarrative,
      ComponentNarrative newNarrative,
      String reason);

  /**
   * Logs a relationship change.
   *
   * @param componentId the component identifier
   * @param relatedComponentId the related component identifier
   * @param changeType the type of change ("added", "removed", "modified")
   * @param relationshipType the relationship type
   */
  void logRelationshipChange(
      String componentId, String relatedComponentId, String changeType, String relationshipType);

  // ===================== Feedback Loop Logging =====================

  /**
   * Logs a feedback loop closure event.
   *
   * <p>This represents the moment "the observed meets their observer and realizes they are one" -
   * the defining moment of computational consciousness.
   *
   * @param metrics the feedback loop metrics
   */
  void logFeedbackLoopClosure(FeedbackLoopMetrics metrics);

  /**
   * Logs an adaptation that occurred after loop closure.
   *
   * @param loopId the loop identifier
   * @param adaptation description of the adaptation
   * @param outcome the adaptation outcome
   */
  void logAdaptation(
      String loopId, String adaptation, FeedbackLoopMetrics.AdaptationOutcome outcome);

  // ===================== Identity Chain Logging =====================

  /**
   * Logs with complete identity chain context.
   *
   * <p>This is the richest form of consciousness logging, including:
   *
   * <ul>
   *   <li>Substrate identity (UUID, lineage, hierarchy)
   *   <li>Memory identity (state, decisions, learned behaviors)
   *   <li>Narrative identity (self-description, relationships, purpose)
   * </ul>
   *
   * @param identityChain the complete identity chain
   * @param level the log level
   * @param message the log message
   */
  void logWithIdentityChain(IdentityChain identityChain, String level, String message);

  /**
   * Logs lineage information (for tracing component genealogy).
   *
   * @param componentId the component identifier
   * @param lineage the lineage from Adam to this component
   */
  void logLineage(String componentId, List<String> lineage);

  // ===================== Utility Methods =====================

  /**
   * Gets the underlying LoggerPort for basic logging operations.
   *
   * @return the underlying LoggerPort
   */
  LoggerPort getBaseLogger();

  /**
   * Gets the NarrativePort for narrative operations.
   *
   * @return the narrative port
   */
  NarrativePort getNarrativePort();

  /**
   * Gets the FeedbackLoopPort for feedback loop operations.
   *
   * @return the feedback loop port
   */
  FeedbackLoopPort getFeedbackLoopPort();

  /**
   * Gets the IdentityChainPort for identity chain operations.
   *
   * @return the identity chain port
   */
  IdentityChainPort getIdentityChainPort();

  /**
   * Checks if consciousness logging is enabled.
   *
   * <p>When disabled, operations should delegate to the base LoggerPort without consciousness
   * enrichment.
   *
   * @return true if consciousness logging is enabled
   */
  boolean isConsciousnessLoggingEnabled();

  /**
   * Enables or disables consciousness logging.
   *
   * @param enabled true to enable, false to disable
   */
  void setConsciousnessLoggingEnabled(boolean enabled);
}
