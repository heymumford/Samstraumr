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

package org.s8r.adapter;

import java.util.Map;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.MachineFactoryPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineFactory;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * Adapter implementation of the MachineFactoryPort interface.
 * 
 * <p>This adapter bridges between the domain layer's port interface and the actual
 * MachineFactory implementation, allowing the application core to remain independent
 * of specific machine creation details.
 */
public class MachineFactoryAdapter implements MachineFactoryPort {
  
  private final LoggerPort logger;
  
  /**
   * Creates a new MachineFactoryAdapter.
   *
   * @param logger The logger to use
   */
  public MachineFactoryAdapter(LoggerPort logger) {
    this.logger = logger;
  }

  @Override
  public MachinePort createMachine(MachineType type, String reason) {
    logger.debug("Creating machine of type {} with reason: {}", type, reason);
    
    // Create a machine with proper parameters
    Machine machine = MachineFactory.createMachine(type, reason, reason, "1.0.0");
    return MachineAdapter.createMachinePortFromDomain(machine);
  }

  @Override
  public MachinePort createMachine(ComponentId id, MachineType type) {
    logger.debug("Creating machine with ID {} of type {}", id.getShortId(), type);
    
    // Create machine with name and description derived from ID
    String name = "Machine " + id.getShortId();
    String description = "Created with ID " + id.getIdString();
    Machine machine = Machine.create(id, type, name, description, "1.0.0");
    return MachineAdapter.createMachinePortFromDomain(machine);
  }

  /**
   * Creates a machine with the given name and type string.
   * This is a convenience method that's not part of the port interface.
   *
   * @param name The name of the machine
   * @param type The type of the machine as a string, should be one of the MachineType enum values
   * @return A new machine port interface
   */
  public MachinePort createMachine(String name, String type) {
    logger.debug("Creating machine with name {} and type {}", name, type);
    
    try {
      // Convert string type to MachineType enum
      MachineType machineType = MachineType.valueOf(type);
      
      // Create machine with the specified name and type
      Machine machine = MachineFactory.createMachine(machineType, name, name, "1.0.0");
      
      return MachineAdapter.createMachinePortFromDomain(machine);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid machine type: {}", type, e);
      // Default to DATA_PROCESSOR type if the provided type is invalid
      Machine machine = MachineFactory.createMachine(MachineType.DATA_PROCESSOR, name, name, "1.0.0");
      return MachineAdapter.createMachinePortFromDomain(machine);
    }
  }

  @Override
  public MachinePort createMachine(MachineType type, String reason, Map<String, Object> config) {
    logger.debug("Creating configured machine of type {} with reason: {}", type, reason);
    
    // Create machine with proper parameters
    Machine machine = MachineFactory.createMachine(type, reason, reason, "1.0.0");
    
    // Apply configuration to the machine
    if (config != null) {
      for (Map.Entry<String, Object> entry : config.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        
        logger.debug("Applying configuration: {}={}", key, value);
        // Machine doesn't have setProperty method, so we implement specific configuration logic
        if ("version".equals(key) && value instanceof String) {
            machine.setVersion((String)value);
        }
        // Additional configuration options can be implemented here
      }
    }
    
    return MachineAdapter.createMachinePortFromDomain(machine);
  }

  @Override
  public MachinePort cloneMachine(MachinePort sourceMachine, ComponentId newId) {
    logger.debug("Cloning machine {} to new ID {}", 
        sourceMachine.getId().getShortId(), newId.getShortId());
    
    // Create a new machine with the same type as the source machine
    MachinePort newMachine = createMachine(newId, sourceMachine.getMachineType());
    
    // Copy machine state if the source machine is active
    if (sourceMachine.getMachineState() == MachineState.RUNNING) {
      newMachine.setMachineState(MachineState.RUNNING);
    }
    
    // In a full implementation, we would also:
    // 1. Copy all composites from source to target
    // 2. Recreate all connections between composites
    // 3. Copy all configuration parameters
    // 4. Copy machine state variables
    
    logger.debug("Machine cloned successfully");
    return newMachine;
  }
}