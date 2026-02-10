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

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a complete consciousness log entry.
 *
 * <p>This value object combines all aspects of consciousness logging into a single structured entry
 * that can be serialized for output. It provides the complete context needed for debugging and
 * system understanding.
 *
 * <h2>Structure</h2>
 *
 * <p>A consciousness log entry contains:
 *
 * <ul>
 *   <li><b>Identity Section</b>: UUID, lineage, hierarchical address
 *   <li><b>Narrative Section</b>: Answers to existential questions
 *   <li><b>Observation Section</b>: State, timestamp, rationale
 *   <li><b>Decision Section</b>: Choice made and alternatives
 *   <li><b>Feedback Loop Section</b>: Loop closure information
 *   <li><b>Metadata</b>: Level, tags, additional context
 * </ul>
 *
 * <h2>Sample JSON Output</h2>
 *
 * <pre>
 * {
 *   "version": "1.0",
 *   "entryId": "CLE-2025-12-06T10:15:30.123Z-a1b2c3d4",
 *   "level": "INFO",
 *   "message": "Processing batch complete",
 *   "identity": {
 *     "uuid": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *     "shortId": "a1b2c3d4",
 *     "lineage": [
 *       "Genesis: Adam component created",
 *       "Created data-processor-group for batch operations",
 *       "Spawned transformer for CSV to JSON conversion"
 *     ],
 *     "hierarchicalAddress": "M<machine-1>.B<bundle-2>.C<a1b2c3d4>",
 *     "isAdam": false,
 *     "conceptionTime": "2025-12-06T08:00:00.000Z"
 *   },
 *   "narrative": {
 *     "whatAmI": "DataTransformationComponent processing customer records",
 *     "whyDoIExist": "Created to transform raw CSV input into normalized JSON output",
 *     "whoDoIRelateTo": [
 *       "InputReader<ir-001>",
 *       "OutputWriter<ow-002>",
 *       "ErrorHandler<eh-003>"
 *     ],
 *     "currentPurposeUnderstanding": "Now optimized for high-volume streaming",
 *     "purposeEvolved": true
 *   },
 *   "observation": {
 *     "timestamp": "2025-12-06T10:15:30.123Z",
 *     "reconstructionDelay": "312ms",
 *     "currentState": "ACTIVE",
 *     "previousState": "PROCESSING_INPUT",
 *     "isTransition": true,
 *     "transitionRationale": "Batch processing complete, ready for next batch"
 *   },
 *   "decision": {
 *     "decisionId": "BATCH_SIZE_DETERMINATION",
 *     "decisionTimestamp": "2025-12-06T10:15:29.800Z",
 *     "chosenPath": "PROCESS_ALL",
 *     "alternatives": ["PROCESS_PARTIAL", "DEFER_TO_NEXT_CYCLE"],
 *     "rationale": "Memory available: 80%, CPU idle: 65%, deadline: 30min remaining",
 *     "influencingFactors": {
 *       "memoryAvailablePercent": 80,
 *       "cpuIdlePercent": 65,
 *       "deadlineMinutesRemaining": 30,
 *       "batchSize": 1000
 *     }
 *   },
 *   "feedbackLoop": {
 *     "loopId": "FL-789",
 *     "observerChain": ["self", "parent-composite", "machine-monitor", "self"],
 *     "closureDetected": true,
 *     "closureTime": "2025-12-06T10:15:29.811Z",
 *     "closureDurationMs": 123,
 *     "totalClosuresInSession": 47,
 *     "averageClosureDurationMs": 115,
 *     "adaptationOutcome": "ADAPTED_SUCCESSFULLY"
 *   },
 *   "metadata": {
 *     "tags": ["BATCH", "PROCESSING", "SUCCESS"],
 *     "sequenceNumber": 1234,
 *     "sessionId": "session-xyz",
 *     "additionalContext": {
 *       "recordsProcessed": 1000,
 *       "processingTimeMs": 450
 *     }
 *   }
 * }
 * </pre>
 */
public final class ConsciousnessLogEntry {

  /** Version of the log entry format */
  public static final String FORMAT_VERSION = "1.0";

  private final String entryId;
  private final Instant timestamp;
  private final String level;
  private final String message;
  private final IdentityChain identityChain;
  private final ComponentNarrative narrative;
  private final ObservationContext observation;
  private final DecisionPoint decision;
  private final FeedbackLoopMetrics feedbackLoop;
  private final Map<String, Object> metadata;
  private final String[] tags;

  private ConsciousnessLogEntry(Builder builder) {
    this.entryId =
        builder.entryId != null
            ? builder.entryId
            : generateEntryId(builder.timestamp, builder.identityChain);
    this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
    this.level = builder.level != null ? builder.level : "INFO";
    this.message = builder.message;
    this.identityChain = builder.identityChain;
    this.narrative = builder.narrative;
    this.observation = builder.observation;
    this.decision = builder.decision;
    this.feedbackLoop = builder.feedbackLoop;
    this.metadata =
        builder.metadata != null
            ? Collections.unmodifiableMap(builder.metadata)
            : Collections.emptyMap();
    this.tags = builder.tags != null ? builder.tags.clone() : new String[0];
  }

  private static String generateEntryId(Instant timestamp, IdentityChain chain) {
    String ts = timestamp != null ? timestamp.toString() : Instant.now().toString();
    String shortId = chain != null ? chain.getShortId() : "unknown";
    return "CLE-" + ts + "-" + shortId;
  }

  /**
   * Gets the format version.
   *
   * @return the format version
   */
  public String getVersion() {
    return FORMAT_VERSION;
  }

  /**
   * Gets the unique entry identifier.
   *
   * @return the entry ID
   */
  public String getEntryId() {
    return entryId;
  }

  /**
   * Gets the entry timestamp.
   *
   * @return the timestamp
   */
  public Instant getTimestamp() {
    return timestamp;
  }

  /**
   * Gets the log level.
   *
   * @return the level
   */
  public String getLevel() {
    return level;
  }

  /**
   * Gets the log message.
   *
   * @return the message, or empty if not set
   */
  public Optional<String> getMessage() {
    return Optional.ofNullable(message);
  }

  /**
   * Gets the identity chain section.
   *
   * @return the identity chain, or empty if not set
   */
  public Optional<IdentityChain> getIdentityChain() {
    return Optional.ofNullable(identityChain);
  }

  /**
   * Gets the narrative section.
   *
   * @return the narrative, or empty if not set
   */
  public Optional<ComponentNarrative> getNarrative() {
    return Optional.ofNullable(narrative);
  }

  /**
   * Gets the observation section.
   *
   * @return the observation, or empty if not set
   */
  public Optional<ObservationContext> getObservation() {
    return Optional.ofNullable(observation);
  }

  /**
   * Gets the decision section.
   *
   * @return the decision, or empty if not set
   */
  public Optional<DecisionPoint> getDecision() {
    return Optional.ofNullable(decision);
  }

  /**
   * Gets the feedback loop section.
   *
   * @return the feedback loop metrics, or empty if not set
   */
  public Optional<FeedbackLoopMetrics> getFeedbackLoop() {
    return Optional.ofNullable(feedbackLoop);
  }

  /**
   * Gets the metadata.
   *
   * @return the metadata map, never null
   */
  public Map<String, Object> getMetadata() {
    return metadata;
  }

  /**
   * Gets the tags.
   *
   * @return a copy of the tags array
   */
  public String[] getTags() {
    return tags.clone();
  }

  /**
   * Checks if this entry represents a state transition.
   *
   * @return true if there is an observation with transition
   */
  public boolean isTransition() {
    return observation != null && observation.isTransition();
  }

  /**
   * Checks if this entry includes a decision.
   *
   * @return true if a decision is present
   */
  public boolean hasDecision() {
    return decision != null;
  }

  /**
   * Checks if this entry includes feedback loop closure.
   *
   * @return true if feedback loop closed
   */
  public boolean hasFeedbackLoopClosure() {
    return feedbackLoop != null && feedbackLoop.isClosureDetected();
  }

  /**
   * Creates a new builder for constructing ConsciousnessLogEntry instances.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConsciousnessLogEntry that = (ConsciousnessLogEntry) o;
    return Objects.equals(entryId, that.entryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(entryId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ConsciousnessLogEntry{");
    sb.append("id='").append(entryId).append("'");
    sb.append(", level=").append(level);
    if (message != null) {
      sb.append(", message='").append(message).append("'");
    }
    if (identityChain != null) {
      sb.append(", component=").append(identityChain.getShortId());
    }
    if (observation != null && observation.isTransition()) {
      sb.append(", transition=").append(observation.getPreviousState().orElse("?"));
      sb.append("->").append(observation.getCurrentState());
    }
    if (feedbackLoop != null && feedbackLoop.isClosureDetected()) {
      sb.append(", loopClosed=true");
    }
    sb.append("}");
    return sb.toString();
  }

  /** Builder for constructing ConsciousnessLogEntry instances. */
  public static final class Builder {
    private String entryId;
    private Instant timestamp;
    private String level;
    private String message;
    private IdentityChain identityChain;
    private ComponentNarrative narrative;
    private ObservationContext observation;
    private DecisionPoint decision;
    private FeedbackLoopMetrics feedbackLoop;
    private Map<String, Object> metadata;
    private String[] tags;

    private Builder() {}

    public Builder entryId(String id) {
      this.entryId = id;
      return this;
    }

    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder now() {
      this.timestamp = Instant.now();
      return this;
    }

    public Builder level(String level) {
      this.level = level;
      return this;
    }

    public Builder info() {
      return level("INFO");
    }

    public Builder debug() {
      return level("DEBUG");
    }

    public Builder warn() {
      return level("WARN");
    }

    public Builder error() {
      return level("ERROR");
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder identityChain(IdentityChain chain) {
      this.identityChain = chain;
      return this;
    }

    public Builder narrative(ComponentNarrative narrative) {
      this.narrative = narrative;
      return this;
    }

    public Builder observation(ObservationContext observation) {
      this.observation = observation;
      return this;
    }

    public Builder decision(DecisionPoint decision) {
      this.decision = decision;
      return this;
    }

    public Builder feedbackLoop(FeedbackLoopMetrics feedbackLoop) {
      this.feedbackLoop = feedbackLoop;
      return this;
    }

    public Builder metadata(Map<String, Object> metadata) {
      this.metadata = metadata;
      return this;
    }

    public Builder tags(String... tags) {
      this.tags = tags;
      return this;
    }

    /**
     * Builds the ConsciousnessLogEntry instance.
     *
     * @return a new ConsciousnessLogEntry
     */
    public ConsciousnessLogEntry build() {
      return new ConsciousnessLogEntry(this);
    }
  }
}
