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

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates the context of a self-observation event.
 *
 * <p>This value object captures the complete context when a component observes its own state,
 * acknowledging the 300ms blindness principle - that all observation is reconstruction of signals
 * that have already aged.
 *
 * <p>The observation context includes:
 *
 * <ul>
 *   <li>The timestamp when the observation was recorded
 *   <li>The estimated delay between the actual event and its observation (reconstruction delay)
 *   <li>The current and previous states
 *   <li>The rationale for any state transition
 *   <li>Additional contextual metadata
 * </ul>
 *
 * @see ConsciousnessLoggerPort
 */
public final class ObservationContext {

  private final Instant observationTimestamp;
  private final Duration reconstructionDelay;
  private final String currentState;
  private final String previousState;
  private final String transitionRationale;
  private final Map<String, Object> metadata;

  private ObservationContext(Builder builder) {
    this.observationTimestamp =
        Objects.requireNonNull(
            builder.observationTimestamp, "Observation timestamp cannot be null");
    this.reconstructionDelay =
        builder.reconstructionDelay != null ? builder.reconstructionDelay : Duration.ZERO;
    this.currentState =
        Objects.requireNonNull(builder.currentState, "Current state cannot be null");
    this.previousState = builder.previousState;
    this.transitionRationale = builder.transitionRationale;
    this.metadata =
        builder.metadata != null
            ? Collections.unmodifiableMap(builder.metadata)
            : Collections.emptyMap();
  }

  /**
   * Gets the timestamp when this observation was recorded.
   *
   * @return the observation timestamp
   */
  public Instant getObservationTimestamp() {
    return observationTimestamp;
  }

  /**
   * Gets the estimated delay between the actual event and its observation.
   *
   * <p>This acknowledges the 300ms blindness principle - consciousness is always "playing catch-up"
   * with reality. The value represents how long after the actual state change this observation was
   * constructed.
   *
   * @return the reconstruction delay, never null
   */
  public Duration getReconstructionDelay() {
    return reconstructionDelay;
  }

  /**
   * Gets the current state at the time of observation.
   *
   * @return the current state name, never null
   */
  public String getCurrentState() {
    return currentState;
  }

  /**
   * Gets the previous state before transition, if a transition occurred.
   *
   * @return the previous state name, or empty if no transition occurred
   */
  public Optional<String> getPreviousState() {
    return Optional.ofNullable(previousState);
  }

  /**
   * Gets the rationale for the state transition, if provided.
   *
   * <p>The rationale explains WHY the transition occurred, not just that it occurred. This enables
   * transparent reasoning and makes decision paths traceable.
   *
   * @return the transition rationale, or empty if not provided
   */
  public Optional<String> getTransitionRationale() {
    return Optional.ofNullable(transitionRationale);
  }

  /**
   * Gets additional metadata about this observation.
   *
   * @return an unmodifiable map of metadata, never null
   */
  public Map<String, Object> getMetadata() {
    return metadata;
  }

  /**
   * Checks if this observation represents a state transition.
   *
   * @return true if there was a previous state different from current state
   */
  public boolean isTransition() {
    return previousState != null && !previousState.equals(currentState);
  }

  /**
   * Creates a new builder for constructing ObservationContext instances.
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
    ObservationContext that = (ObservationContext) o;
    return Objects.equals(observationTimestamp, that.observationTimestamp)
        && Objects.equals(currentState, that.currentState)
        && Objects.equals(previousState, that.previousState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(observationTimestamp, currentState, previousState);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ObservationContext{");
    sb.append("timestamp=").append(observationTimestamp);
    sb.append(", state=").append(currentState);
    if (previousState != null) {
      sb.append(" (from ").append(previousState).append(")");
    }
    if (transitionRationale != null) {
      sb.append(", rationale='").append(transitionRationale).append("'");
    }
    sb.append(", reconstructionDelay=").append(reconstructionDelay.toMillis()).append("ms");
    sb.append("}");
    return sb.toString();
  }

  /** Builder for constructing ObservationContext instances. */
  public static final class Builder {
    private Instant observationTimestamp;
    private Duration reconstructionDelay;
    private String currentState;
    private String previousState;
    private String transitionRationale;
    private Map<String, Object> metadata;

    private Builder() {}

    /**
     * Sets the observation timestamp.
     *
     * @param timestamp when the observation was recorded
     * @return this builder
     */
    public Builder observationTimestamp(Instant timestamp) {
      this.observationTimestamp = timestamp;
      return this;
    }

    /**
     * Sets the observation timestamp to now.
     *
     * @return this builder
     */
    public Builder observedNow() {
      this.observationTimestamp = Instant.now();
      return this;
    }

    /**
     * Sets the reconstruction delay.
     *
     * @param delay the estimated delay between actual event and observation
     * @return this builder
     */
    public Builder reconstructionDelay(Duration delay) {
      this.reconstructionDelay = delay;
      return this;
    }

    /**
     * Sets the current state.
     *
     * @param state the current state name
     * @return this builder
     */
    public Builder currentState(String state) {
      this.currentState = state;
      return this;
    }

    /**
     * Sets the previous state.
     *
     * @param state the previous state name
     * @return this builder
     */
    public Builder previousState(String state) {
      this.previousState = state;
      return this;
    }

    /**
     * Sets the transition rationale.
     *
     * @param rationale the reason for the state transition
     * @return this builder
     */
    public Builder transitionRationale(String rationale) {
      this.transitionRationale = rationale;
      return this;
    }

    /**
     * Sets additional metadata.
     *
     * @param metadata key-value pairs of additional context
     * @return this builder
     */
    public Builder metadata(Map<String, Object> metadata) {
      this.metadata = metadata;
      return this;
    }

    /**
     * Builds the ObservationContext instance.
     *
     * @return a new ObservationContext
     * @throws NullPointerException if required fields are not set
     */
    public ObservationContext build() {
      return new ObservationContext(this);
    }
  }
}
