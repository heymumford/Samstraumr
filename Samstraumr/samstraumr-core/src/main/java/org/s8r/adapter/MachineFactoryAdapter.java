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
    
    Machine machine = MachineFactory.createMachine(type, reason);
    return MachineAdapter.createMachinePort(machine);
  }

  @Override
  public MachinePort createMachine(ComponentId id, MachineType type) {
    logger.debug("Creating machine with ID {} of type {}", id.getShortId(), type);
    
    Machine machine = MachineFactory.createMachine(id, type);
    return MachineAdapter.createMachinePort(machine);
  }

  @Override
  public MachinePort createMachine(MachineType type, String reason, Map<String, Object> config) {
    logger.debug("Creating configured machine of type {} with reason: {}", type, reason);
    
    Machine machine = MachineFactory.createMachine(type, reason);
    
    // Apply configuration to the machine
    if (config != null) {
      for (Map.Entry<String, Object> entry : config.entrySet()) {
        String key = entry.getKey();
        Object value = entry.getValue();
        
        logger.debug("Applying configuration: {}={}", key, value);
        machine.setProperty(key, value);
      }
    }
    
    return MachineAdapter.createMachinePort(machine);
  }

  @Override
  public MachinePort cloneMachine(MachinePort sourceMachine, ComponentId newId) {
    logger.debug("Cloning machine {} to new ID {}", 
        sourceMachine.getId().getShortId(), newId.getShortId());
    
    // Although this is a naive implementation that doesn't truly clone all aspects,
    // it demonstrates the pattern for adhering to the port interface
    MachinePort newMachine = createMachine(newId, sourceMachine.getMachineType());
    
    // Copy relevant properties from source to target
    // In a full implementation, this would copy all relevant state
    
    logger.debug("Machine cloned successfully");
    return newMachine;
  }
}