/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain model for machine states in the S8r framework
 */

package org.samstraumr.domain.machine;

/**
 * Defines the possible states of a machine.
 *
 * <p>This enum represents the different operational states that a machine can be in during its
 * lifecycle.
 */
public enum MachineState {
  /** Machine has been created but not initialized. */
  CREATED("Machine has been created"),

  /** Machine has been initialized and is ready to start. */
  READY("Machine is initialized and ready to start"),

  /** Machine is running with active components. */
  RUNNING("Machine is running with active components"),

  /** Machine has been temporarily stopped but can be restarted. */
  STOPPED("Machine is stopped but can be restarted"),

  /** Machine is paused but maintains its state. */
  PAUSED("Machine is paused but maintains state"),

  /** Machine is in an error state and requires intervention. */
  ERROR("Machine is in an error state"),

  /** Machine has been destroyed and cannot be restarted. */
  DESTROYED("Machine has been destroyed");

  private final String description;

  /**
   * Constructs a MachineState with a description.
   *
   * @param description A description of the state
   */
  MachineState(String description) {
    this.description = description;
  }

  /**
   * Gets the description of this state.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Checks if this state is a terminal state.
   *
   * @return true if this is a terminal state, false otherwise
   */
  public boolean isTerminal() {
    return this == DESTROYED;
  }

  /**
   * Checks if this state is an error state.
   *
   * @return true if this is an error state, false otherwise
   */
  public boolean isError() {
    return this == ERROR;
  }

  /**
   * Checks if this state is an active state.
   *
   * @return true if this is an active state, false otherwise
   */
  public boolean isActive() {
    return this == RUNNING;
  }

  /**
   * Checks if this state is a standby state.
   *
   * @return true if this is a standby state, false otherwise
   */
  public boolean isStandby() {
    return this == READY || this == STOPPED || this == PAUSED;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ")";
  }
}
