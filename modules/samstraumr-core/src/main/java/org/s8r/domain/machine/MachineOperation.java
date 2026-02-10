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

package org.s8r.domain.machine;

/**
 * Enumeration of machine operations that can be validated for state compliance.
 *
 * <p>This enum replaces magic strings used in machine state validation, providing type-safety and
 * refactoring support.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This enum is immutable and inherently thread-safe. All instances are created at class loading
 * time, and no mutable state is exposed. It is safe for concurrent use by multiple threads without
 * additional synchronization.
 */
public enum MachineOperation {
  /** Initialize the machine, transitioning from CREATED to READY. */
  INITIALIZE("initialize"),

  /** Start the machine, transitioning to RUNNING state. */
  START("start"),

  /** Stop the machine, transitioning to STOPPED state. */
  STOP("stop"),

  /** Pause the machine, temporarily suspending activities. */
  PAUSE("pause"),

  /** Resume the machine from a paused state. */
  RESUME("resume"),

  /** Reset the machine from an error or stopped state. */
  RESET("reset"),

  /** Transition the machine to an error state. */
  SET_ERROR_STATE("setErrorState"),

  /** Reset the machine from an error state back to READY. */
  RESET_FROM_ERROR("resetFromError"),

  /** Destroy the machine, releasing all resources. */
  DESTROY("destroy"),

  /** Add a component to the machine. */
  ADD_COMPONENT("addComponent"),

  /** Remove a component from the machine. */
  REMOVE_COMPONENT("removeComponent"),

  /** Set the version of the machine. */
  SET_VERSION("setVersion");

  private final String operationName;

  /**
   * Constructs a MachineOperation with the specified name.
   *
   * @param operationName The operation name used in validation logic
   */
  MachineOperation(String operationName) {
    this.operationName = operationName;
  }

  /**
   * Gets the operation name.
   *
   * @return The operation name
   */
  public String getOperationName() {
    return operationName;
  }

  @Override
  public String toString() {
    return operationName;
  }
}
