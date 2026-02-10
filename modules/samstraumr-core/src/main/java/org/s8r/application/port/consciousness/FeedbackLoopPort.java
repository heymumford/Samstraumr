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
import java.util.List;
import java.util.Optional;

/**
 * Port interface for feedback loop detection and metrics.
 *
 * <p>Based on the philosophical thesis that "consciousness is little more than the moment in which
 * the observed meets their observer, and realizes they are one", this port provides mechanisms to
 * detect when this feedback loop closes.
 *
 * <p>A feedback loop in this context represents:
 *
 * <ol>
 *   <li>Component A observes itself (or is observed by B)
 *   <li>The observation propagates through the system
 *   <li>The observation returns to the original observer
 *   <li>Loop closure: "the observed meets the observer"
 * </ol>
 *
 * <p>This supports Experiment 9: "Systems with faster feedback loops (observer-observed-observer)
 * adapt more quickly to changing requirements."
 */
public interface FeedbackLoopPort {

  /**
   * Starts tracking a new feedback loop.
   *
   * @param componentId the component initiating the loop
   * @return the unique loop identifier
   */
  String startLoop(String componentId);

  /**
   * Records an observation in an active loop.
   *
   * @param loopId the loop identifier
   * @param observerId the identifier of the observing component
   */
  void recordObservation(String loopId, String observerId);

  /**
   * Checks if a loop has closed (observation returned to origin).
   *
   * @param loopId the loop identifier
   * @return true if the loop has closed
   */
  boolean isLoopClosed(String loopId);

  /**
   * Gets the current observer chain for a loop.
   *
   * @param loopId the loop identifier
   * @return the list of observers in order, may be empty
   */
  List<String> getObserverChain(String loopId);

  /**
   * Gets metrics for a specific feedback loop.
   *
   * @param loopId the loop identifier
   * @return the loop metrics, or empty if loop not found
   */
  Optional<FeedbackLoopMetrics> getLoopMetrics(String loopId);

  /**
   * Gets aggregate metrics for a component's feedback loops.
   *
   * @param componentId the component identifier
   * @return aggregate metrics across all the component's loops
   */
  FeedbackLoopMetrics getAggregateMetrics(String componentId);

  /**
   * Gets the total number of loop closures for a component.
   *
   * @param componentId the component identifier
   * @return the total closure count
   */
  long getTotalClosures(String componentId);

  /**
   * Gets the average loop closure duration for a component.
   *
   * @param componentId the component identifier
   * @return the average duration, or empty if no closures recorded
   */
  Optional<Duration> getAverageClosureDuration(String componentId);

  /**
   * Gets the closure frequency (closures per minute) for a component.
   *
   * @param componentId the component identifier
   * @return the closure frequency
   */
  double getClosureFrequency(String componentId);

  /**
   * Registers a listener for loop closure events.
   *
   * @param componentId the component to listen for
   * @param listener the listener to invoke on closure
   */
  void onLoopClosure(String componentId, LoopClosureListener listener);

  /**
   * Removes a loop closure listener.
   *
   * @param componentId the component identifier
   * @param listener the listener to remove
   */
  void removeClosureListener(String componentId, LoopClosureListener listener);

  /**
   * Marks that an adaptation occurred after loop closure.
   *
   * @param loopId the loop identifier
   * @param outcome the outcome of the adaptation
   */
  void recordAdaptation(String loopId, FeedbackLoopMetrics.AdaptationOutcome outcome);

  /**
   * Ends tracking of a feedback loop.
   *
   * @param loopId the loop identifier
   */
  void endLoop(String loopId);

  /** Listener interface for loop closure events. */
  @FunctionalInterface
  interface LoopClosureListener {
    /**
     * Called when a feedback loop closes.
     *
     * @param metrics the metrics for the closed loop
     */
    void onClosure(FeedbackLoopMetrics metrics);
  }
}
