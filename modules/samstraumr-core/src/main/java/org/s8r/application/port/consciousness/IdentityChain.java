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
 * Represents the complete identity chain for consciousness logging.
 *
 * <p>This value object captures the full identity context needed for consciousness-aware logging,
 * synthesizing the three philosophical models of identity:
 *
 * <ul>
 *   <li><b>Substrate Identity</b>: UUID, conception timestamp, parent lineage
 *   <li><b>Memory Identity</b>: State narrative, experience history, learned behaviors
 *   <li><b>Narrative Identity</b>: Self-description, relationship network, purpose
 * </ul>
 *
 * <p>This enables the Experiment 1 hypothesis: "Systems maintaining identity chains with temporal
 * context enable faster root cause analysis than systems with traditional logging."
 *
 * <h3>Comparison with Traditional Logging</h3>
 *
 * <p><b>Traditional:</b> timestamp, level, message, trace ID
 *
 * <p><b>Identity Chain:</b> UUID, lineage, state narrative, relationship network, decision
 * rationale
 *
 * @see ConsciousnessLoggerPort
 */
public final class IdentityChain {

  // Substrate Identity
  private final String uuid;
  private final Instant conceptionTime;
  private final List<String> lineage;
  private final String hierarchicalAddress;
  private final boolean isAdamComponent;

  // Memory Identity
  private final String currentState;
  private final String stateNarrative;
  private final List<String> recentDecisions;
  private final Map<String, Object> learnedBehaviors;

  // Narrative Identity
  private final ComponentNarrative narrative;
  private final List<String> relationshipNetwork;

  private IdentityChain(Builder builder) {
    // Substrate
    this.uuid = Objects.requireNonNull(builder.uuid, "UUID cannot be null");
    this.conceptionTime = builder.conceptionTime != null ? builder.conceptionTime : Instant.now();
    this.lineage =
        builder.lineage != null
            ? Collections.unmodifiableList(builder.lineage)
            : Collections.emptyList();
    this.hierarchicalAddress = builder.hierarchicalAddress;
    this.isAdamComponent = builder.isAdamComponent;

    // Memory
    this.currentState = builder.currentState;
    this.stateNarrative = builder.stateNarrative;
    this.recentDecisions =
        builder.recentDecisions != null
            ? Collections.unmodifiableList(builder.recentDecisions)
            : Collections.emptyList();
    this.learnedBehaviors =
        builder.learnedBehaviors != null
            ? Collections.unmodifiableMap(builder.learnedBehaviors)
            : Collections.emptyMap();

    // Narrative
    this.narrative = builder.narrative;
    this.relationshipNetwork =
        builder.relationshipNetwork != null
            ? Collections.unmodifiableList(builder.relationshipNetwork)
            : Collections.emptyList();
  }

  // ===================== Substrate Identity Accessors =====================

  /**
   * Gets the universally unique identifier for this component.
   *
   * @return the UUID, never null
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Gets the conception (creation) timestamp.
   *
   * @return the conception time, never null
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the lineage - the chain of reasons/purposes from Adam to this component.
   *
   * @return the lineage list, never null
   */
  public List<String> getLineage() {
    return lineage;
  }

  /**
   * Gets the hierarchical address in the component ecosystem.
   *
   * <p>Format: M{machineId}.B{bundleId}.C{componentId} Example:
   * "M<machine-1>.B<bundle-2>.C<a1b2c3d4>"
   *
   * @return the hierarchical address, or empty if not set
   */
  public Optional<String> getHierarchicalAddress() {
    return Optional.ofNullable(hierarchicalAddress);
  }

  /**
   * Checks if this is an Adam (primordial) component without a parent.
   *
   * @return true if this is an Adam component
   */
  public boolean isAdamComponent() {
    return isAdamComponent;
  }

  // ===================== Memory Identity Accessors =====================

  /**
   * Gets the current state name.
   *
   * @return the current state, or empty if not set
   */
  public Optional<String> getCurrentState() {
    return Optional.ofNullable(currentState);
  }

  /**
   * Gets the narrative explanation of the current state.
   *
   * <p>Rather than just the state name, this provides context about what the state means for this
   * particular component in its current situation.
   *
   * @return the state narrative, or empty if not set
   */
  public Optional<String> getStateNarrative() {
    return Optional.ofNullable(stateNarrative);
  }

  /**
   * Gets the recent decisions made by this component.
   *
   * <p>This provides decision context for debugging - what paths were chosen and why, enabling
   * faster root cause analysis.
   *
   * @return the list of recent decision descriptions, never null
   */
  public List<String> getRecentDecisions() {
    return recentDecisions;
  }

  /**
   * Gets learned behaviors - patterns the component has developed over time.
   *
   * @return the learned behaviors map, never null
   */
  public Map<String, Object> getLearnedBehaviors() {
    return learnedBehaviors;
  }

  // ===================== Narrative Identity Accessors =====================

  /**
   * Gets the component's self-narrative.
   *
   * @return the narrative, or empty if not set
   */
  public Optional<ComponentNarrative> getNarrative() {
    return Optional.ofNullable(narrative);
  }

  /**
   * Gets the relationship network - components this one relates to.
   *
   * @return the relationship network, never null
   */
  public List<String> getRelationshipNetwork() {
    return relationshipNetwork;
  }

  // ===================== Utility Methods =====================

  /**
   * Gets the lineage depth (number of ancestors).
   *
   * @return the lineage depth
   */
  public int getLineageDepth() {
    return lineage.size();
  }

  /**
   * Checks if this component has any relationships.
   *
   * @return true if the component has relationships
   */
  public boolean hasRelationships() {
    return !relationshipNetwork.isEmpty();
  }

  /**
   * Creates a short identifier suitable for log prefixes.
   *
   * @return a short identifier (first 8 chars of UUID)
   */
  public String getShortId() {
    return uuid.length() > 8 ? uuid.substring(0, 8) : uuid;
  }

  /**
   * Creates a new builder for constructing IdentityChain instances.
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
    IdentityChain that = (IdentityChain) o;
    return Objects.equals(uuid, that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("IdentityChain{\n");
    sb.append("  uuid='").append(uuid).append("'\n");
    sb.append("  hierarchicalAddress='").append(hierarchicalAddress).append("'\n");
    sb.append("  lineage=").append(lineage).append("\n");
    sb.append("  isAdam=").append(isAdamComponent).append("\n");
    sb.append("  currentState='").append(currentState).append("'\n");
    if (stateNarrative != null) {
      sb.append("  stateNarrative='").append(stateNarrative).append("'\n");
    }
    sb.append("  relationships=").append(relationshipNetwork).append("\n");
    sb.append("  conception=").append(conceptionTime).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /** Builder for constructing IdentityChain instances. */
  public static final class Builder {
    // Substrate
    private String uuid;
    private Instant conceptionTime;
    private List<String> lineage;
    private String hierarchicalAddress;
    private boolean isAdamComponent;

    // Memory
    private String currentState;
    private String stateNarrative;
    private List<String> recentDecisions;
    private Map<String, Object> learnedBehaviors;

    // Narrative
    private ComponentNarrative narrative;
    private List<String> relationshipNetwork;

    private Builder() {}

    // Substrate setters

    public Builder uuid(String uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder conceptionTime(Instant time) {
      this.conceptionTime = time;
      return this;
    }

    public Builder lineage(List<String> lineage) {
      this.lineage = lineage;
      return this;
    }

    public Builder hierarchicalAddress(String address) {
      this.hierarchicalAddress = address;
      return this;
    }

    public Builder isAdamComponent(boolean isAdam) {
      this.isAdamComponent = isAdam;
      return this;
    }

    // Memory setters

    public Builder currentState(String state) {
      this.currentState = state;
      return this;
    }

    public Builder stateNarrative(String narrative) {
      this.stateNarrative = narrative;
      return this;
    }

    public Builder recentDecisions(List<String> decisions) {
      this.recentDecisions = decisions;
      return this;
    }

    public Builder learnedBehaviors(Map<String, Object> behaviors) {
      this.learnedBehaviors = behaviors;
      return this;
    }

    // Narrative setters

    public Builder narrative(ComponentNarrative narrative) {
      this.narrative = narrative;
      return this;
    }

    public Builder relationshipNetwork(List<String> relationships) {
      this.relationshipNetwork = relationships;
      return this;
    }

    /**
     * Builds the IdentityChain instance.
     *
     * @return a new IdentityChain
     * @throws NullPointerException if required fields are not set
     */
    public IdentityChain build() {
      return new IdentityChain(this);
    }
  }
}
