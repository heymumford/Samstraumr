package org.samstraumr.tube;

/**
 * Enum representing the various lifecycle statuses of a Tube in the Samstraumr framework. Tubes
 * move through these states as they perform operations, handle inputs, and manage errors.
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

  UNDETERMINED
}
