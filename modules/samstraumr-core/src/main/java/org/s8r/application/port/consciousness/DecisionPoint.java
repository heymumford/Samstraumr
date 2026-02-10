/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a decision point where a component chose one path among alternatives.
 *
 * <p>This value object captures the complete context of a decision, enabling transparent reasoning
 * where decision paths become traceable and "why" questions are answerable through log examination.
 *
 * <p>A decision point includes:
 *
 * <ul>
 *   <li>The decision identifier (what type of decision was made)
 *   <li>The chosen path (what was decided)
 *   <li>The alternatives that were not chosen
 *   <li>The rationale for the choice (why this path was chosen)
 *   <li>Contextual factors that influenced the decision
 * </ul>
 *
 * @see ConsciousnessLoggerPort
 */
public final class DecisionPoint {

  private final String decisionId;
  private final Instant decisionTimestamp;
  private final String chosenPath;
  private final List<String> alternatives;
  private final String rationale;
  private final Map<String, Object> influencingFactors;
  private final String componentId;

  private DecisionPoint(Builder builder) {
    this.decisionId = Objects.requireNonNull(builder.decisionId, "Decision ID cannot be null");
    this.decisionTimestamp =
        builder.decisionTimestamp != null ? builder.decisionTimestamp : Instant.now();
    this.chosenPath = Objects.requireNonNull(builder.chosenPath, "Chosen path cannot be null");
    this.alternatives =
        builder.alternatives != null
            ? Collections.unmodifiableList(builder.alternatives)
            : Collections.emptyList();
    this.rationale = builder.rationale;
    this.influencingFactors =
        builder.influencingFactors != null
            ? Collections.unmodifiableMap(builder.influencingFactors)
            : Collections.emptyMap();
    this.componentId = builder.componentId;
  }

  /**
   * Gets the unique identifier for this decision type.
   *
   * <p>Examples: "BATCH_SIZE_DETERMINATION", "ERROR_RECOVERY_STRATEGY", "CONNECTION_POOL_SIZING",
   * "RETRY_POLICY_SELECTION"
   *
   * @return the decision identifier, never null
   */
  public String getDecisionId() {
    return decisionId;
  }

  /**
   * Gets the timestamp when this decision was made.
   *
   * @return the decision timestamp, never null
   */
  public Instant getDecisionTimestamp() {
    return decisionTimestamp;
  }

  /**
   * Gets the path that was chosen.
   *
   * <p>Examples: "PROCESS_ALL", "USE_EXPONENTIAL_BACKOFF", "SCALE_UP"
   *
   * @return the chosen path, never null
   */
  public String getChosenPath() {
    return chosenPath;
  }

  /**
   * Gets the alternatives that were considered but not chosen.
   *
   * @return an unmodifiable list of alternative paths, never null
   */
  public List<String> getAlternatives() {
    return alternatives;
  }

  /**
   * Gets the rationale for choosing this path.
   *
   * <p>The rationale explains WHY this particular path was chosen over the alternatives. This is
   * the key to transparent reasoning.
   *
   * @return the rationale, or empty if not provided
   */
  public Optional<String> getRationale() {
    return Optional.ofNullable(rationale);
  }

  /**
   * Gets the factors that influenced this decision.
   *
   * <p>These are measurable conditions that contributed to the decision, such as:
   *
   * <ul>
   *   <li>Resource availability (memory, CPU, connections)
   *   <li>Time constraints (deadlines, timeouts)
   *   <li>Historical patterns (previous success/failure rates)
   *   <li>Configuration values
   * </ul>
   *
   * @return an unmodifiable map of influencing factors, never null
   */
  public Map<String, Object> getInfluencingFactors() {
    return influencingFactors;
  }

  /**
   * Gets the component ID that made this decision.
   *
   * @return the component ID, or empty if not provided
   */
  public Optional<String> getComponentId() {
    return Optional.ofNullable(componentId);
  }

  /**
   * Checks if alternatives were considered for this decision.
   *
   * @return true if there were alternatives to choose from
   */
  public boolean hadAlternatives() {
    return !alternatives.isEmpty();
  }

  /**
   * Creates a new builder for constructing DecisionPoint instances.
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
    DecisionPoint that = (DecisionPoint) o;
    return Objects.equals(decisionId, that.decisionId)
        && Objects.equals(decisionTimestamp, that.decisionTimestamp)
        && Objects.equals(chosenPath, that.chosenPath)
        && Objects.equals(componentId, that.componentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decisionId, decisionTimestamp, chosenPath, componentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("DecisionPoint{");
    sb.append("id='").append(decisionId).append("'");
    sb.append(", chose='").append(chosenPath).append("'");
    if (!alternatives.isEmpty()) {
      sb.append(" over ").append(alternatives);
    }
    if (rationale != null) {
      sb.append(", because='").append(rationale).append("'");
    }
    sb.append(", at=").append(decisionTimestamp);
    sb.append("}");
    return sb.toString();
  }

  /** Builder for constructing DecisionPoint instances. */
  public static final class Builder {
    private String decisionId;
    private Instant decisionTimestamp;
    private String chosenPath;
    private List<String> alternatives;
    private String rationale;
    private Map<String, Object> influencingFactors;
    private String componentId;

    private Builder() {}

    /**
     * Sets the decision identifier.
     *
     * @param id the decision type identifier
     * @return this builder
     */
    public Builder decisionId(String id) {
      this.decisionId = id;
      return this;
    }

    /**
     * Sets the decision timestamp.
     *
     * @param timestamp when the decision was made
     * @return this builder
     */
    public Builder decisionTimestamp(Instant timestamp) {
      this.decisionTimestamp = timestamp;
      return this;
    }

    /**
     * Sets the decision timestamp to now.
     *
     * @return this builder
     */
    public Builder decidedNow() {
      this.decisionTimestamp = Instant.now();
      return this;
    }

    /**
     * Sets the chosen path.
     *
     * @param path the path that was chosen
     * @return this builder
     */
    public Builder chosenPath(String path) {
      this.chosenPath = path;
      return this;
    }

    /**
     * Sets the alternatives that were considered.
     *
     * @param alternatives the alternative paths not chosen
     * @return this builder
     */
    public Builder alternatives(List<String> alternatives) {
      this.alternatives = alternatives;
      return this;
    }

    /**
     * Sets the rationale for the decision.
     *
     * @param rationale the reason for choosing this path
     * @return this builder
     */
    public Builder rationale(String rationale) {
      this.rationale = rationale;
      return this;
    }

    /**
     * Sets the influencing factors.
     *
     * @param factors the factors that influenced the decision
     * @return this builder
     */
    public Builder influencingFactors(Map<String, Object> factors) {
      this.influencingFactors = factors;
      return this;
    }

    /**
     * Sets the component ID that made this decision.
     *
     * @param componentId the component identifier
     * @return this builder
     */
    public Builder componentId(String componentId) {
      this.componentId = componentId;
      return this;
    }

    /**
     * Builds the DecisionPoint instance.
     *
     * @return a new DecisionPoint
     * @throws NullPointerException if required fields are not set
     */
    public DecisionPoint build() {
      return new DecisionPoint(this);
    }
  }
}
