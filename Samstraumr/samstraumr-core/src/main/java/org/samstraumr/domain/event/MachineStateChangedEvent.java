/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Machine state change event for the S8r framework
 */

package org.samstraumr.domain.event;

import org.samstraumr.domain.identity.ComponentId;
import org.samstraumr.domain.machine.MachineState;

/** Event raised when a machine's state changes. */
public class MachineStateChangedEvent extends DomainEvent {
  private final ComponentId machineId;
  private final MachineState previousState;
  private final MachineState newState;
  private final String transitionReason;

  /** Creates a new machine state changed event. */
  public MachineStateChangedEvent(
      ComponentId machineId,
      MachineState previousState,
      MachineState newState,
      String transitionReason) {
    this.machineId = machineId;
    this.previousState = previousState;
    this.newState = newState;
    this.transitionReason = transitionReason;
  }

  // Getters
  public ComponentId getMachineId() { return machineId; }
  public MachineState getPreviousState() { return previousState; }
  public MachineState getNewState() { return newState; }
  public String getTransitionReason() { return transitionReason; }
}
