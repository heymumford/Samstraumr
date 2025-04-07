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

package org.s8r.domain.component.port;

import java.util.Map;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;

/**
 * Port interface for machine factory operations.
 * 
 * <p>This interface defines the operations for creating machine instances,
 * allowing the domain logic to remain independent of specific machine 
 * implementation details. It follows the Factory Method pattern,
 * creating machine objects that adhere to the MachinePort interface.
 */
public interface MachineFactoryPort {

  /**
   * Creates a new machine with the specified type and reason.
   *
   * @param type The type of machine to create
   * @param reason The reason for creating the machine
   * @return A new machine port interface
   */
  MachinePort createMachine(MachineType type, String reason);
  
  /**
   * Creates a new machine with the specified ID and type.
   *
   * @param id The ID for the new machine
   * @param type The type of machine to create
   * @return A new machine port interface
   */
  MachinePort createMachine(ComponentId id, MachineType type);
  
  /**
   * Creates a new machine with the specified configuration.
   *
   * @param type The type of machine to create
   * @param reason The reason for creating the machine
   * @param config Configuration parameters for the machine
   * @return A new machine port interface
   */
  MachinePort createMachine(MachineType type, String reason, Map<String, Object> config);
  
  /**
   * Creates a machine by cloning an existing machine.
   *
   * @param sourceMachine The machine to clone
   * @param newId The ID for the cloned machine
   * @return A new machine port interface that is a clone of the source
   */
  MachinePort cloneMachine(MachinePort sourceMachine, ComponentId newId);
}