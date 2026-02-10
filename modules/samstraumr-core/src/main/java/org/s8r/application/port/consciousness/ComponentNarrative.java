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
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates a component's self-narrative - its answer to existential questions.
 *
 * <p>Based on the Narrative/Constructivist Model of identity, this value object captures how a
 * component understands and describes itself. Every Samstraumr-conformant component must be able to
 * answer:
 *
 * <ul>
 *   <li><b>What am I?</b> - The component's self-description of its nature and capabilities
 *   <li><b>Why do I exist?</b> - The purpose or reason for the component's creation
 *   <li><b>Who do I relate to?</b> - The component's relationship network
 * </ul>
 *
 * <p>The narrative is not static - it evolves as the component learns and adapts. Each narrative
 * snapshot captures the component's self-understanding at a particular moment.
 *
 * @see NarrativePort
 */
public final class ComponentNarrative {

  private final String componentId;
  private final Instant narrativeTimestamp;
  private final String whatAmI;
  private final String whyDoIExist;
  private final List<String> whoDoIRelateTo;
  private final String currentPurposeUnderstanding;
  private final List<String> evolutionHistory;

  private ComponentNarrative(Builder builder) {
    this.componentId = Objects.requireNonNull(builder.componentId, "Component ID cannot be null");
    this.narrativeTimestamp =
        builder.narrativeTimestamp != null ? builder.narrativeTimestamp : Instant.now();
    this.whatAmI = Objects.requireNonNull(builder.whatAmI, "What am I answer cannot be null");
    this.whyDoIExist =
        Objects.requireNonNull(builder.whyDoIExist, "Why do I exist answer cannot be null");
    this.whoDoIRelateTo =
        builder.whoDoIRelateTo != null
            ? Collections.unmodifiableList(builder.whoDoIRelateTo)
            : Collections.emptyList();
    this.currentPurposeUnderstanding = builder.currentPurposeUnderstanding;
    this.evolutionHistory =
        builder.evolutionHistory != null
            ? Collections.unmodifiableList(builder.evolutionHistory)
            : Collections.emptyList();
  }

  /**
   * Gets the component identifier this narrative belongs to.
   *
   * @return the component ID, never null
   */
  public String getComponentId() {
    return componentId;
  }

  /**
   * Gets the timestamp when this narrative was captured.
   *
   * @return the narrative timestamp, never null
   */
  public Instant getNarrativeTimestamp() {
    return narrativeTimestamp;
  }

  /**
   * Gets the component's answer to "What am I?"
   *
   * <p>This describes the component's nature, capabilities, and role in the system. Example:
   * "DataTransformationComponent processing customer records with CSV to JSON conversion
   * capabilities, supporting batch and streaming modes"
   *
   * @return the self-description, never null
   */
  public String getWhatAmI() {
    return whatAmI;
  }

  /**
   * Gets the component's answer to "Why do I exist?"
   *
   * <p>This describes the purpose for which the component was created. Example: "Created to
   * transform raw CSV input from legacy systems into normalized JSON output for the new API layer,
   * bridging the gap during system migration"
   *
   * @return the existence rationale, never null
   */
  public String getWhyDoIExist() {
    return whyDoIExist;
  }

  /**
   * Gets the component's answer to "Who do I relate to?"
   *
   * <p>This lists the component's relationship network - other components it communicates with,
   * depends on, or serves. Example: ["InputReader<ir-001>", "OutputWriter<ow-002>",
   * "ErrorHandler<eh-003>"]
   *
   * @return the relationship list, never null but may be empty
   */
  public List<String> getWhoDoIRelateTo() {
    return whoDoIRelateTo;
  }

  /**
   * Gets the component's current understanding of its purpose.
   *
   * <p>This may differ from the original reason for creation as the component evolves and adapts.
   * It represents the component's current self-understanding of its role.
   *
   * @return the current purpose understanding, or empty if not evolved from original
   */
  public Optional<String> getCurrentPurposeUnderstanding() {
    return Optional.ofNullable(currentPurposeUnderstanding);
  }

  /**
   * Gets the history of narrative evolution.
   *
   * <p>Each entry represents a significant change in the component's self-understanding, capturing
   * how its narrative has evolved over time.
   *
   * @return the evolution history, never null but may be empty
   */
  public List<String> getEvolutionHistory() {
    return evolutionHistory;
  }

  /**
   * Checks if this component has relationships.
   *
   * @return true if the component relates to other components
   */
  public boolean hasRelationships() {
    return !whoDoIRelateTo.isEmpty();
  }

  /**
   * Checks if the component's purpose understanding has evolved from its original reason.
   *
   * @return true if purpose understanding has evolved
   */
  public boolean hasPurposeEvolved() {
    return currentPurposeUnderstanding != null && !currentPurposeUnderstanding.equals(whyDoIExist);
  }

  /**
   * Creates a new builder for constructing ComponentNarrative instances.
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
    ComponentNarrative that = (ComponentNarrative) o;
    return Objects.equals(componentId, that.componentId)
        && Objects.equals(narrativeTimestamp, that.narrativeTimestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(componentId, narrativeTimestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ComponentNarrative{\n");
    sb.append("  componentId='").append(componentId).append("'\n");
    sb.append("  whatAmI='").append(whatAmI).append("'\n");
    sb.append("  whyDoIExist='").append(whyDoIExist).append("'\n");
    sb.append("  whoDoIRelateTo=").append(whoDoIRelateTo).append("\n");
    if (currentPurposeUnderstanding != null) {
      sb.append("  currentUnderstanding='").append(currentPurposeUnderstanding).append("'\n");
    }
    sb.append("  timestamp=").append(narrativeTimestamp).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /** Builder for constructing ComponentNarrative instances. */
  public static final class Builder {
    private String componentId;
    private Instant narrativeTimestamp;
    private String whatAmI;
    private String whyDoIExist;
    private List<String> whoDoIRelateTo;
    private String currentPurposeUnderstanding;
    private List<String> evolutionHistory;

    private Builder() {}

    /**
     * Sets the component identifier.
     *
     * @param id the component identifier
     * @return this builder
     */
    public Builder componentId(String id) {
      this.componentId = id;
      return this;
    }

    /**
     * Sets the narrative timestamp.
     *
     * @param timestamp when this narrative was captured
     * @return this builder
     */
    public Builder narrativeTimestamp(Instant timestamp) {
      this.narrativeTimestamp = timestamp;
      return this;
    }

    /**
     * Sets the narrative timestamp to now.
     *
     * @return this builder
     */
    public Builder capturedNow() {
      this.narrativeTimestamp = Instant.now();
      return this;
    }

    /**
     * Sets the answer to "What am I?"
     *
     * @param description the component's self-description
     * @return this builder
     */
    public Builder whatAmI(String description) {
      this.whatAmI = description;
      return this;
    }

    /**
     * Sets the answer to "Why do I exist?"
     *
     * @param purpose the reason for the component's existence
     * @return this builder
     */
    public Builder whyDoIExist(String purpose) {
      this.whyDoIExist = purpose;
      return this;
    }

    /**
     * Sets the answer to "Who do I relate to?"
     *
     * @param relationships the list of related components
     * @return this builder
     */
    public Builder whoDoIRelateTo(List<String> relationships) {
      this.whoDoIRelateTo = relationships;
      return this;
    }

    /**
     * Sets the current purpose understanding.
     *
     * @param understanding the evolved purpose understanding
     * @return this builder
     */
    public Builder currentPurposeUnderstanding(String understanding) {
      this.currentPurposeUnderstanding = understanding;
      return this;
    }

    /**
     * Sets the evolution history.
     *
     * @param history the list of narrative evolution events
     * @return this builder
     */
    public Builder evolutionHistory(List<String> history) {
      this.evolutionHistory = history;
      return this;
    }

    /**
     * Builds the ComponentNarrative instance.
     *
     * @return a new ComponentNarrative
     * @throws NullPointerException if required fields are not set
     */
    public ComponentNarrative build() {
      return new ComponentNarrative(this);
    }
  }
}
