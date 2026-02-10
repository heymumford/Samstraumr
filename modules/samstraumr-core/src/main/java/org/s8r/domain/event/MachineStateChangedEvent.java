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

package org.s8r.domain.event;

import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineState;

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
  public ComponentId getMachineId() {
    return machineId;
  }

  public MachineState getPreviousState() {
    return previousState;
  }

  public MachineState getNewState() {
    return newState;
  }

  public String getTransitionReason() {
    return transitionReason;
  }
}
