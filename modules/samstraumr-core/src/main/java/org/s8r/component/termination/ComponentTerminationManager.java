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

package org.s8r.component.termination;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.s8r.component.logging.ComponentLogger;

/**
 * Manages component termination, including scheduled termination and cleanup.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Scheduled termination via timer
 *   <li>Cleanup and resource release
 *   <li>Knowledge preservation before termination
 * </ul>
 *
 * <p><b>Refactoring Note:</b> Extracted from Component.java as part of Phase 2 God Class
 * decomposition (Martin Fowler refactoring).
 */
public class ComponentTerminationManager {
  private static final int DEFAULT_TERMINATION_DELAY = 60; // seconds

  private volatile Timer terminationTimer;
  private final String componentId;
  private final ComponentLogger logger;

  /**
   * Creates a new termination manager.
   *
   * @param componentId The unique identifier of the component
   * @param logger The component logger
   */
  public ComponentTerminationManager(String componentId, ComponentLogger logger) {
    this.componentId = componentId;
    this.logger = logger;
  }

  /**
   * Sets up a timer for component termination.
   *
   * @param delaySeconds The delay in seconds before termination
   * @param terminationAction The action to execute when timer fires
   * @throws IllegalArgumentException if delay is not positive
   * @throws IllegalStateException if component is already terminated
   */
  public void setupTerminationTimer(int delaySeconds, Runnable terminationAction) {
    if (delaySeconds <= 0) {
      throw new IllegalArgumentException("Termination delay must be positive");
    }

    synchronized (this) {
      // Cancel existing timer if present
      if (terminationTimer != null) {
        terminationTimer.cancel();
      }

      terminationTimer = new Timer("ComponentTerminator-" + componentId);
      terminationTimer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              terminationAction.run();
            }
          },
          delaySeconds * 1000L);
    }

    logger.debug("Termination timer set for " + delaySeconds + " seconds", "LIFECYCLE", "TIMER");
  }

  /**
   * Sets up a termination timer with default delay.
   *
   * @param terminationAction The action to execute when timer fires
   */
  public void setupDefaultTerminationTimer(Runnable terminationAction) {
    setupTerminationTimer(DEFAULT_TERMINATION_DELAY, terminationAction);
  }

  /**
   * Cancels the termination timer if active.
   *
   * @return true if a timer was cancelled, false if no timer was active
   */
  public boolean cancelTimer() {
    synchronized (this) {
      if (terminationTimer != null) {
        terminationTimer.cancel();
        terminationTimer = null;
        logger.debug("Termination timer canceled", "LIFECYCLE", "TERMINATE");
        return true;
      }
      return false;
    }
  }

  /**
   * Executes the termination sequence.
   *
   * @param lifecycleTerminator Callback to initiate lifecycle termination
   */
  public void terminate(Consumer<Runnable> lifecycleTerminator) {
    logger.info("Component termination initiated", "LIFECYCLE", "TERMINATE");

    // Cancel timer if active
    cancelTimer();

    // Execute termination through lifecycle manager
    lifecycleTerminator.accept(() -> {
      preserveKnowledge();
      releaseResources();
    });

    logger.info("Component terminated", "LIFECYCLE", "TERMINATE");
  }

  /**
   * Preserves crucial knowledge before termination for potential future use.
   */
  private void preserveKnowledge() {
    logger.debug("Preserving knowledge before termination", "LIFECYCLE", "TERMINATE");
    // In a full implementation, this would archive important learnings and experiences
  }

  /**
   * Releases all resources allocated to this component.
   */
  private void releaseResources() {
    logger.debug("Releasing allocated resources", "LIFECYCLE", "TERMINATE");
    // In a full implementation, this would clean up all allocated resources
  }

  /**
   * Checks if there is an active termination timer.
   *
   * @return true if a timer is active
   */
  public boolean hasActiveTimer() {
    return terminationTimer != null;
  }
}
