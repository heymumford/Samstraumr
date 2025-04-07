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

package org.s8r.infrastructure.config;

import org.s8r.adapter.ComponentAdapter;
import org.s8r.adapter.CompositeAdapter;
import org.s8r.adapter.MachineAdapter;
import org.s8r.adapter.TubeLegacyEnvironmentConverter;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachinePort;

/**
 * Factory for creating port adapters for Clean Architecture.
 *
 * <p>This factory provides methods for creating port interfaces from domain entities,
 * following the Dependency Inversion Principle of Clean Architecture. It allows higher-level
 * modules to depend on abstractions (ports) rather than concrete implementations.
 */
public class PortAdapterFactory {
    private final LoggerPort logger;
    private final ComponentAdapter componentAdapter;
    
    /**
     * Creates a new PortAdapterFactory with the specified logger.
     *
     * @param logger The logger to use for adapter operations
     */
    public PortAdapterFactory(LoggerPort logger) {
        this.logger = logger;
        this.componentAdapter = new ComponentAdapter(logger);
        logger.debug("PortAdapterFactory initialized");
    }
    
    /**
     * Creates a ComponentPort from a Component.
     *
     * @param component The domain component
     * @return A ComponentPort that delegates to the component
     */
    public ComponentPort createComponentPort(Component component) {
        logger.debug("Creating ComponentPort for: {}", component.getId().getIdString());
        return ComponentAdapter.createComponentPort(component);
    }
    
    /**
     * Creates a CompositeComponentPort from a CompositeComponent.
     *
     * @param composite The domain composite component
     * @return A CompositeComponentPort that delegates to the composite
     */
    public CompositeComponentPort createCompositeComponentPort(CompositeComponent composite) {
        logger.debug("Creating CompositeComponentPort for: {}", composite.getId().getIdString());
        return ComponentAdapter.createCompositeComponentPort(composite);
    }
    
    /**
     * Creates a MachinePort from a Machine.
     *
     * @param machine The domain machine
     * @return A MachinePort that delegates to the machine
     */
    public MachinePort createMachinePort(org.s8r.domain.machine.Machine machine) {
        logger.debug("Creating MachinePort for: {}", machine.getId().getIdString());
        return org.s8r.adapter.MachineAdapter.createMachinePort(machine);
    }
    
    /**
     * Creates a MachinePort from a Tube-based Machine.
     *
     * @param tubeMachine The tube-based machine
     * @return A MachinePort that delegates to the tube machine
     */
    public MachinePort createMachinePortFromTube(org.s8r.tube.machine.Machine tubeMachine) {
        logger.debug("Creating MachinePort from tube machine: {}", tubeMachine.getMachineId());
        
        // Create a machine adapter with required dependencies
        MachineAdapter machineAdapter = createMachineAdapter();
        
        // Use the adapter to create a MachinePort
        return machineAdapter.tubeMachineToMachinePort(tubeMachine);
    }
    
    /**
     * Creates a MachineAdapter instance with required dependencies.
     *
     * @return A new MachineAdapter
     */
    private MachineAdapter createMachineAdapter() {
        // Get dependencies from the container
        CompositeAdapter compositeAdapter = new CompositeAdapter(logger, null, null);
        
        // Create the TubeLegacyEnvironmentConverter with logger
        TubeLegacyEnvironmentConverter environmentConverter = new TubeLegacyEnvironmentConverter(logger);
        
        // Create adapters with proper dependency chain
        return new MachineAdapter(logger, compositeAdapter, environmentConverter);
    }
    
    /**
     * Gets the ComponentAdapter instance.
     *
     * @return The ComponentAdapter
     */
    public ComponentAdapter getComponentAdapter() {
        return componentAdapter;
    }
}