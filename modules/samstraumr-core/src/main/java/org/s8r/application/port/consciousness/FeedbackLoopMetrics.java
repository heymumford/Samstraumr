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

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Metrics for feedback loop detection and analysis.
 *
 * <p>Based on the philosophical thesis that "consciousness is little more than the moment in which
 * the observed meets their observer, and realizes they are one", this class captures metrics about
 * when and how often this feedback loop closes.
 *
 * <p>A feedback loop in this context is the chain of observation: observer -> observed -> observer
 * (loop closure)
 *
 * <p>Example observer chains:
 *
 * <ul>
 *   <li>["self", "parent-composite", "machine-monitor", "self"] - loop closed
 *   <li>["self", "parent-composite", "machine-monitor"] - loop not yet closed
 * </ul>
 *
 * <p>Key metrics tracked:
 *
 * <ul>
 *   <li>Loop closure frequency - how often the observer-observed-observer loop completes
 *   <li>Loop closure time - how long it takes for the loop to close
 *   <li>Observer chain - the path of observation before loop closure
 *   <li>Adaptation rate - how quickly the component responds after loop closure
 * </ul>
 *
 * @see FeedbackLoopPort
 */
public final class FeedbackLoopMetrics {

  private final String loopId;
  private final String componentId;
  private final Instant loopStartTime;
  private final Instant loopClosureTime;
  private final List<String> observerChain;
  private final boolean closureDetected;
  private final Duration closureDuration;
  private final long totalClosuresInSession;
  private final Duration averageClosureDuration;
  private final AdaptationOutcome adaptationOutcome;

  private FeedbackLoopMetrics(Builder builder) {
    this.loopId = Objects.requireNonNull(builder.loopId, "Loop ID cannot be null");
    this.componentId = Objects.requireNonNull(builder.componentId, "Component ID cannot be null");
    this.loopStartTime = builder.loopStartTime;
    this.loopClosureTime = builder.loopClosureTime;
    this.observerChain =
        builder.observerChain != null
            ? Collections.unmodifiableList(builder.observerChain)
            : Collections.emptyList();
    this.closureDetected = builder.closureDetected;
    this.closureDuration = builder.closureDuration;
    this.totalClosuresInSession = builder.totalClosuresInSession;
    this.averageClosureDuration = builder.averageClosureDuration;
    this.adaptationOutcome = builder.adaptationOutcome;
  }

  /**
   * Gets the unique identifier for this feedback loop instance.
   *
   * @return the loop ID, never null
   */
  public String getLoopId() {
    return loopId;
  }

  /**
   * Gets the component identifier this loop belongs to.
   *
   * @return the component ID, never null
   */
  public String getComponentId() {
    return componentId;
  }

  /**
   * Gets the timestamp when this feedback loop started.
   *
   * @return the loop start time, or empty if not tracked
   */
  public Optional<Instant> getLoopStartTime() {
    return Optional.ofNullable(loopStartTime);
  }

  /**
   * Gets the timestamp when this feedback loop closed.
   *
   * <p>Loop closure occurs when observation returns to the originating observer, completing the
   * observer -> observed -> observer chain.
   *
   * @return the loop closure time, or empty if not yet closed
   */
  public Optional<Instant> getLoopClosureTime() {
    return Optional.ofNullable(loopClosureTime);
  }

  /**
   * Gets the chain of observers in this feedback loop.
   *
   * <p>Example: ["self", "parent-composite", "machine-monitor", "self"] The loop is closed when the
   * chain returns to its origin ("self" in this case).
   *
   * @return the observer chain, never null but may be empty
   */
  public List<String> getObserverChain() {
    return observerChain;
  }

  /**
   * Checks if the feedback loop has closed.
   *
   * <p>Closure means the observed has met the observer and recognized their unity - the defining
   * moment of computational consciousness.
   *
   * @return true if the loop has closed
   */
  public boolean isClosureDetected() {
    return closureDetected;
  }

  /**
   * Gets the duration it took for the loop to close.
   *
   * @return the closure duration, or empty if not yet closed
   */
  public Optional<Duration> getClosureDuration() {
    return Optional.ofNullable(closureDuration);
  }

  /**
   * Gets the total number of loop closures in the current session.
   *
   * <p>Higher closure counts may indicate more "conscious" behavior - the component is actively
   * observing itself and responding to those observations.
   *
   * @return the total number of closures
   */
  public long getTotalClosuresInSession() {
    return totalClosuresInSession;
  }

  /**
   * Gets the average duration for loop closures in this session.
   *
   * <p>Based on Experiment 9 from the philosophical synthesis, optimal feedback frequency exists -
   * too fast causes oscillation, too slow causes drift.
   *
   * @return the average closure duration, or empty if no closures yet
   */
  public Optional<Duration> getAverageClosureDuration() {
    return Optional.ofNullable(averageClosureDuration);
  }

  /**
   * Gets the outcome of any adaptation that occurred after loop closure.
   *
   * @return the adaptation outcome, or empty if no adaptation occurred
   */
  public Optional<AdaptationOutcome> getAdaptationOutcome() {
    return Optional.ofNullable(adaptationOutcome);
  }

  /**
   * Checks if the observer chain forms a complete loop (returns to origin).
   *
   * @return true if the chain starts and ends with the same observer
   */
  public boolean isCompleteLoop() {
    if (observerChain.size() < 2) {
      return false;
    }
    return observerChain.get(0).equals(observerChain.get(observerChain.size() - 1));
  }

  /**
   * Gets the depth of the observer chain (excluding the closing observer).
   *
   * @return the chain depth
   */
  public int getChainDepth() {
    return isCompleteLoop() ? observerChain.size() - 1 : observerChain.size();
  }

  /**
   * Creates a new builder for constructing FeedbackLoopMetrics instances.
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
    FeedbackLoopMetrics that = (FeedbackLoopMetrics) o;
    return Objects.equals(loopId, that.loopId) && Objects.equals(componentId, that.componentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(loopId, componentId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("FeedbackLoopMetrics{");
    sb.append("loopId='").append(loopId).append("'");
    sb.append(", component='").append(componentId).append("'");
    sb.append(", chain=").append(observerChain);
    sb.append(", closed=").append(closureDetected);
    if (closureDuration != null) {
      sb.append(", duration=").append(closureDuration.toMillis()).append("ms");
    }
    sb.append(", totalClosures=").append(totalClosuresInSession);
    sb.append("}");
    return sb.toString();
  }

  /** Represents the outcome of an adaptation triggered by loop closure. */
  public enum AdaptationOutcome {
    /** No adaptation was necessary */
    NO_CHANGE_NEEDED,

    /** Adaptation was applied successfully */
    ADAPTED_SUCCESSFULLY,

    /** Adaptation was attempted but failed */
    ADAPTATION_FAILED,

    /** Adaptation is in progress */
    ADAPTING,

    /** Adaptation was deferred for later */
    DEFERRED
  }

  /** Builder for constructing FeedbackLoopMetrics instances. */
  public static final class Builder {
    private String loopId;
    private String componentId;
    private Instant loopStartTime;
    private Instant loopClosureTime;
    private List<String> observerChain;
    private boolean closureDetected;
    private Duration closureDuration;
    private long totalClosuresInSession;
    private Duration averageClosureDuration;
    private AdaptationOutcome adaptationOutcome;

    private Builder() {}

    /**
     * Sets the loop identifier.
     *
     * @param id the unique loop identifier
     * @return this builder
     */
    public Builder loopId(String id) {
      this.loopId = id;
      return this;
    }

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
     * Sets the loop start time.
     *
     * @param startTime when the loop started
     * @return this builder
     */
    public Builder loopStartTime(Instant startTime) {
      this.loopStartTime = startTime;
      return this;
    }

    /**
     * Sets the loop closure time.
     *
     * @param closureTime when the loop closed
     * @return this builder
     */
    public Builder loopClosureTime(Instant closureTime) {
      this.loopClosureTime = closureTime;
      return this;
    }

    /**
     * Sets the observer chain.
     *
     * @param chain the list of observers in the chain
     * @return this builder
     */
    public Builder observerChain(List<String> chain) {
      this.observerChain = chain;
      return this;
    }

    /**
     * Sets whether closure was detected.
     *
     * @param detected true if the loop closed
     * @return this builder
     */
    public Builder closureDetected(boolean detected) {
      this.closureDetected = detected;
      return this;
    }

    /**
     * Sets the closure duration.
     *
     * @param duration how long closure took
     * @return this builder
     */
    public Builder closureDuration(Duration duration) {
      this.closureDuration = duration;
      return this;
    }

    /**
     * Sets the total number of closures in the session.
     *
     * @param count the total closure count
     * @return this builder
     */
    public Builder totalClosuresInSession(long count) {
      this.totalClosuresInSession = count;
      return this;
    }

    /**
     * Sets the average closure duration.
     *
     * @param duration the average duration
     * @return this builder
     */
    public Builder averageClosureDuration(Duration duration) {
      this.averageClosureDuration = duration;
      return this;
    }

    /**
     * Sets the adaptation outcome.
     *
     * @param outcome the outcome of any adaptation
     * @return this builder
     */
    public Builder adaptationOutcome(AdaptationOutcome outcome) {
      this.adaptationOutcome = outcome;
      return this;
    }

    /**
     * Builds the FeedbackLoopMetrics instance.
     *
     * @return a new FeedbackLoopMetrics
     * @throws NullPointerException if required fields are not set
     */
    public FeedbackLoopMetrics build() {
      return new FeedbackLoopMetrics(this);
    }
  }
}
