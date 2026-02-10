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

package org.s8r.tube;

/**
 * The Tube is currently in the process of being initialized. In this state, the Tube is preparing
 * to start its operation, gathering any necessary resources or environment configurations. The Tube
 * is not yet ready to receive input.
 */
/**
 * The Tube has completed initialization and is ready to receive input. This state signifies that
 * the Tube is fully operational and can begin processing when input is provided.
 */
/**
 * The Tube is active and fully operational. This state indicates that the Tube is currently
 * functioning normally and is able to receive, process, and output data.
 */
/**
 * The Tube is waiting for external input or a signal to begin operation. In this state, the Tube is
 * idle, pending input or resources, and is ready to transition to RECEIVING_INPUT once input is
 * available.
 */
/**
 * The Tube is actively receiving input. In this state, the Tube is processing incoming data,
 * configurations, or commands from external sources to prepare for execution.
 */
/**
 * The Tube is currently processing the input it has received. This state indicates that the Tube is
 * performing its core operations, such as data transformation, calculations, or analysis.
 */
/**
 * The Tube has completed processing and is now outputting the results. This state represents the
 * final step of the Tube's operation, where the processed data or results are being sent to the
 * next destination (e.g., another Tube or external system).
 */
/**
 * The Tube has encountered an error during its operation. In this state, the Tube either cannot
 * proceed with its current task due and will log the failure.
 */
/**
 * The Tube has encountered an issue and is attempting to recover. This state indicates that the
 * Tube is performing recovery operations to resolve errors or inconsistencies and restore normal
 * functionality.
 */
/**
 * The Tube is temporarily paused but can resume operations. In this state, the Tube has maintained
 * its internal state and resources, but is not actively processing. It can be reactivated to
 * continue operations from where it left off.
 */
/**
 * The Tube is currently inactive but is available to be reactivated. In the DORMANT state, the Tube
 * has paused its operations and is waiting for a signal to re-enter the READY or WAITING state.
 */
/**
 * The Tube is in the process of shutting down. In this state, the Tube is performing cleanup
 * operations, releasing resources, and preparing to terminate its operation gracefully.
 */
/**
 * The Tube has completed its shutdown process and is now terminated. In this state, the Tube has
 * released all resources and is no longer active in the system. This is a terminal state from which
 * the Tube cannot transition to any other state.
 */
public enum TubeStatus {

  /**
   * The Tube is currently in the process of being initialized. In this state, the Tube is preparing
   * to start its operation, gathering any necessary resources or environment configurations. The
   * Tube is not yet ready to receive input.
   */
  INITIALIZING,

  /**
   * The Tube has completed initialization and is ready to receive input. This state signifies that
   * the Tube is fully operational and can begin processing when input is provided.
   */
  READY,

  /**
   * The Tube is active and fully operational. This state indicates that the Tube is currently
   * functioning normally and is able to receive, process, and output data.
   */
  ACTIVE,

  /**
   * The Tube is waiting for external input or a signal to begin operation. In this state, the Tube
   * is idle, pending input or resources, and is ready to transition to RECEIVING_INPUT once input
   * is available.
   */
  WAITING,

  /**
   * The Tube is actively receiving input. In this state, the Tube is processing incoming data,
   * configurations, or commands from external sources to prepare for execution.
   */
  RECEIVING_INPUT,

  /**
   * The Tube is currently processing the input it has received. This state indicates that the Tube
   * is performing its core operations, such as data transformation, calculations, or analysis.
   */
  PROCESSING_INPUT,

  /**
   * The Tube has completed processing and is now outputting the results. This state represents the
   * final step of the Tube's operation, where the processed data or results are being sent to the
   * next destination (e.g., another Tube or external system).
   */
  OUTPUTTING_RESULT,

  /**
   * The Tube has encountered an error during its operation. In this state, the Tube either cannot
   * proceed with its current task due and will log the failure.
   */
  ERROR,

  /**
   * The Tube has encountered an issue and is attempting to recover. This state indicates that the
   * Tube is performing recovery operations to resolve errors or inconsistencies and restore normal
   * functionality.
   */
  RECOVERING,

  /**
   * The Tube is temporarily paused but can resume operations. In this state, the Tube has
   * maintained its internal state and resources, but is not actively processing. It can be
   * reactivated to continue operations from where it left off.
   */
  PAUSED,

  /**
   * The Tube is currently inactive but is available to be reactivated. In the DORMANT state, the
   * Tube has paused its operations and is waiting for a signal to re-enter the READY or WAITING
   * state.
   */
  DORMANT,

  /**
   * The Tube is in the process of shutting down. In this state, the Tube is performing cleanup
   * operations, releasing resources, and preparing to terminate its operation gracefully.
   */
  DEACTIVATING,

  /**
   * The Tube has completed its shutdown process and is now terminated. In this state, the Tube has
   * released all resources and is no longer active in the system. This is a terminal state from
   * which the Tube cannot transition to any other state.
   */
  TERMINATED,

  UNDETERMINED
}
