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

package org.s8r.core.tube;

/**
 * Enum representing the possible operational states of a component in the system.
 *
 * <p>This enum is part of the simplified package structure, replacing the more specific TubeStatus
 * with a more general Status enum in the s8r.core.tube package.
 *
 * <p>Each state represents a distinct phase in the component's lifecycle or operational flow,
 * providing clear indicators of what the component is currently doing and what transitions are
 * valid.
 */
public enum Status {

  /**
   * The component is currently in the process of being initialized. In this state, the component is
   * preparing to start its operation, gathering any necessary resources or environment
   * configurations. The component is not yet ready to receive input.
   */
  INITIALIZING,

  /**
   * The component has completed initialization and is ready to receive input. This state signifies
   * that the component is fully operational and can begin processing when input is provided.
   */
  READY,

  /**
   * The component is active and fully operational. This state indicates that the component is
   * currently functioning normally and is able to receive, process, and output data.
   */
  ACTIVE,

  /**
   * The component is operational. This is a general state indicating that the component is
   * functioning normally and capable of performing its intended operations.
   */
  OPERATIONAL,

  /**
   * The component is waiting for external input or a signal to begin operation. In this state, the
   * component is idle, pending input or resources, and is ready to transition to RECEIVING_INPUT
   * once input is available.
   */
  WAITING,

  /**
   * The component is actively receiving input. In this state, the component is processing incoming
   * data, configurations, or commands from external sources to prepare for execution.
   */
  RECEIVING_INPUT,

  /**
   * The component is currently processing the input it has received. This state indicates that the
   * component is performing its core operations, such as data transformation, calculations, or
   * analysis.
   */
  PROCESSING_INPUT,

  /**
   * The component has completed processing and is now outputting the results. This state represents
   * the final step of the component's operation, where the processed data or results are being sent
   * to the next destination (e.g., another component or external system).
   */
  OUTPUTTING_RESULT,

  /**
   * The component has encountered an error during its operation. In this state, the component
   * either cannot proceed with its current task due and will log the failure.
   */
  ERROR,
  
  /**
   * The component is functioning but with reduced capabilities or performance.
   * In this state, the component can still perform its essential functions but
   * may be operating with limited features or at reduced efficiency.
   */
  DEGRADED,

  /**
   * The component has encountered an issue and is attempting to recover. This state indicates that
   * the component is performing recovery operations to resolve errors or inconsistencies and
   * restore normal functionality.
   */
  RECOVERING,

  /**
   * The component is temporarily paused but can resume operations. In this state, the component has
   * maintained its internal state and resources, but is not actively processing. It can be
   * reactivated to continue operations from where it left off.
   */
  PAUSED,

  /**
   * The component is currently inactive but is available to be reactivated. In the DORMANT state,
   * the component has paused its operations and is waiting for a signal to re-enter the READY or
   * WAITING state.
   */
  DORMANT,

  /**
   * The component is in the process of shutting down. In this state, the component is performing
   * cleanup operations, releasing resources, and preparing to terminate its operation gracefully.
   */
  DEACTIVATING,

  /**
   * The component has completed its shutdown process and is now terminated. In this state, the
   * component has released all resources and is no longer active in the system. This is a terminal
   * state from which the component cannot transition to any other state.
   */
  TERMINATED,

  /**
   * The status of the component could not be determined. This is typically a temporary state used
   * when a component's actual status cannot be ascertained due to communication issues or other
   * failures.
   */
  UNDETERMINED
}
