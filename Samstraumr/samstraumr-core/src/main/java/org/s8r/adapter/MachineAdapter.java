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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.LoggerPort;
import org.s8r.component.Composite;
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.Machine;
import org.s8r.component.State;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

// Legacy types referenced using fully qualified names

/**
 * Adapter for converting between legacy Tube-based Machines and new Component-based Machines.
 * <p>
 * This adapter facilitates migration from legacy code by allowing existing Tube machines
 * to be used with the new Component-based architecture. The adapter handles the conversion
 * of composites, connections, and state management.
 * </p>
 * <p>
 * This adapter implements the Dependency Inversion Principle by also providing conversions
 * that return MachinePort interfaces instead of concrete Machine implementations. This 
 * allows client code to depend on abstractions rather than concrete implementations,
 * facilitating a smooth migration to Clean Architecture.
 * </p>
 */
public class MachineAdapter {
    
    private final LoggerPort logger;
    private final CompositeAdapter compositeAdapter;
    private final TubeLegacyEnvironmentConverter environmentConverter;
    
    /**
     * Gets the environment from a tube machine using reflection.
     * This is a workaround for missing getEnvironment() method.
     * 
     * @param tubeMachine The tube machine
     * @return The environment, or null if it cannot be accessed
     */
    private org.s8r.tube.Environment getTubeMachineEnvironment(org.s8r.tube.machine.Machine tubeMachine) {
        if (tubeMachine == null) {
            return null;
        }
        
        try {
            java.lang.reflect.Field field = org.s8r.tube.machine.Machine.class.getDeclaredField("environment");
            field.setAccessible(true);
            return (org.s8r.tube.Environment) field.get(tubeMachine);
        } catch (Exception e) {
            logger.error("Failed to access environment field", e);
            // Create an empty environment as fallback
            return new org.s8r.tube.Environment();
        }
    }
    
    /**
     * Creates a new MachineAdapter.
     *
     * @param logger Logger for recording operations
     * @param compositeAdapter Adapter for converting Tube composites to Component composites
     * @param environmentConverter Converter for Environment objects
     */
    public MachineAdapter(
            LoggerPort logger,
            CompositeAdapter compositeAdapter,
            TubeLegacyEnvironmentConverter environmentConverter) {
        this.logger = logger;
        this.compositeAdapter = compositeAdapter;
        this.environmentConverter = environmentConverter;
        
        logger.debug("MachineAdapter initialized");
    }
    
    /**
     * Converts a legacy Tube machine to a new Component machine.
     *
     * @param tubeMachine The legacy machine to convert
     * @return A new Component-based machine that mirrors the legacy machine
     */
    public Machine tubeMachineToComponentMachine(org.s8r.tube.machine.Machine tubeMachine) {
        logger.debug("Converting tube machine to component machine: {}", tubeMachine.getMachineId());
        
        // Convert environment
        org.s8r.tube.Environment tubeEnvironment = getTubeMachineEnvironment(tubeMachine); 
        org.s8r.component.Environment componentEnvironment = environmentConverter.fromLegacyEnvironment(tubeEnvironment);
        
        // Create new machine
        Machine componentMachine = new Machine(
                tubeMachine.getMachineId(), componentEnvironment);
        
        // Convert composites
        Map<String, org.s8r.tube.composite.Composite> tubeComposites = tubeMachine.getComposites();
        for (Map.Entry<String, org.s8r.tube.composite.Composite> entry : tubeComposites.entrySet()) {
            String compositeName = entry.getKey();
            org.s8r.tube.composite.Composite tubeComposite = entry.getValue();
            
            // Convert and add to the new machine
            Composite componentComposite = compositeAdapter.tubeCompositeToComponentComposite(tubeComposite);
            componentMachine.addComposite(compositeName, componentComposite);
            
            logger.debug("Added converted composite to machine: {}", compositeName);
        }
        
        // Copy connections
        Map<String, List<String>> connections = tubeMachine.getConnections();
        for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
            String sourceName = entry.getKey();
            for (String targetName : entry.getValue()) {
                componentMachine.connect(sourceName, targetName);
                logger.debug("Added connection to machine: {} -> {}", sourceName, targetName);
            }
        }
        
        // Copy state
        Map<String, Object> tubeState = tubeMachine.getState();
        for (Map.Entry<String, Object> entry : tubeState.entrySet()) {
            componentMachine.updateState(entry.getKey(), entry.getValue());
        }
        
        // Copy activation status
        if (!tubeMachine.isActive()) {
            componentMachine.deactivate();
        }
        
        logger.debug("Completed conversion of tube machine: {}", tubeMachine.getMachineId());
        return componentMachine;
    }
    
    /**
     * Converts a legacy Tube machine to a MachinePort interface.
     * This follows the Clean Architecture pattern by returning the port interface
     * rather than the concrete implementation.
     *
     * @param tubeMachine The legacy machine to convert
     * @return A MachinePort interface that mirrors the legacy machine
     */
    public MachinePort tubeMachineToMachinePort(org.s8r.tube.machine.Machine tubeMachine) {
        logger.debug("Converting tube machine to MachinePort: {}", tubeMachine.getMachineId());
        
        // First create a component-based machine
        Machine componentMachine = tubeMachineToComponentMachine(tubeMachine);
        
        // Wrap in a MachinePort adapter - using component adapter because this is a component machine
        return createMachinePortFromComponent(componentMachine);
    }
    
    /**
     * Creates a wrapper around a Tube machine, allowing it to be used with Component APIs.
     *
     * @param tubeMachine The Tube machine to wrap
     * @return A Component machine that delegates to the Tube machine
     */
    public Machine wrapTubeMachine(org.s8r.tube.machine.Machine tubeMachine) {
        logger.debug("Creating wrapper for tube machine: {}", tubeMachine.getMachineId());
        
        // Convert environment
        org.s8r.tube.Environment tubeEnvironment = getTubeMachineEnvironment(tubeMachine); 
        org.s8r.component.Environment componentEnvironment = environmentConverter.fromLegacyEnvironment(tubeEnvironment);
        
        // Create wrapper machine
        return new TubeMachineWrapper(
                tubeMachine.getMachineId(),
                componentEnvironment,
                tubeMachine,
                compositeAdapter,
                logger);
    }
    
    /**
     * Creates a wrapper around a Tube machine that implements the MachinePort interface.
     * This follows the Clean Architecture pattern by returning the port interface
     * rather than the concrete implementation.
     *
     * @param tubeMachine The Tube machine to wrap
     * @return A MachinePort that delegates to the Tube machine
     */
    public MachinePort wrapTubeMachineAsPort(org.s8r.tube.machine.Machine tubeMachine) {
        logger.debug("Creating MachinePort wrapper for tube machine: {}", tubeMachine.getMachineId());
        
        // First create a component wrapper
        Machine machineWrapper = wrapTubeMachine(tubeMachine);
        
        // Wrap in a MachinePort adapter
        return createMachinePortFromComponent(machineWrapper);
    }
    
    /**
     * Creates a MachinePort interface from a Machine implementation.
     * This is a factory method that provides access to the adapter while keeping
     * the adapter class itself private.
     *
     * @param machine The machine implementation to adapt
     * @return A MachinePort interface that delegates to the machine
     */
    public static MachinePort createMachinePort(org.s8r.domain.machine.Machine machine) {
        if (machine == null) {
            return null;
        }
        // Create a custom implementation of MachinePort that delegates to the domain Machine
        return new MachinePort() {
            @Override
            public String getMachineId() {
                return machine.getId().getIdString();
            }
            
            @Override
            public MachineType getMachineType() {
                return machine.getType();
            }
            
            @Override
            public MachineState getMachineState() {
                return machine.getState();
            }
            
            @Override
            public void setMachineState(MachineState state) {
                if (state == MachineState.RUNNING) {
                    machine.start();
                } else if (state == MachineState.STOPPED) {
                    machine.stop();
                } else if (state == MachineState.DESTROYED) {
                    machine.destroy();
                } else if (state == MachineState.READY) {
                    machine.initialize();
                }
            }
            
            @Override
            public boolean start() {
                try {
                    machine.start();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            public boolean stop() {
                try {
                    machine.stop();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            
            @Override
            public boolean addComposite(String name, CompositeComponentPort composite) {
                // We would need to convert from CompositeComponentPort to CompositeComponent
                // This is simplified and won't actually work, only illustrates the pattern
                return false;
            }
            
            @Override
            public java.util.Optional<CompositeComponentPort> removeComposite(String name) {
                return java.util.Optional.empty();
            }
            
            @Override
            public CompositeComponentPort getComposite(String name) {
                // We would need an adapter from CompositeComponent to CompositeComponentPort
                return null;
            }
            
            @Override
            public Map<String, CompositeComponentPort> getComposites() {
                return Collections.emptyMap();
            }
            
            @Override
            public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
                return false;
            }
            
            @Override
            public Map<String, List<String>> getCompositeConnections() {
                return Collections.emptyMap();
            }
            
            @Override
            public Map<String, List<String>> getConnections() {
                return Collections.emptyMap();
            }
            
            @Override
            public ComponentId getId() {
                return machine.getId();
            }
            
            @Override
            public LifecycleState getLifecycleState() {
                return machine.getState() == MachineState.RUNNING ? 
                    LifecycleState.ACTIVE : LifecycleState.READY;
            }
            
            @Override
            public List<String> getLineage() {
                return Collections.emptyList();
            }
            
            @Override
            public List<String> getActivityLog() {
                return machine.getActivityLog();
            }
            
            @Override
            public java.time.Instant getCreationTime() {
                return machine.getCreationTime();
            }
            
            @Override
            public List<DomainEvent> getDomainEvents() {
                return machine.getDomainEvents();
            }
            
            @Override
            public void addToLineage(String entry) {
                // No-op, not supported
            }
            
            @Override
            public void clearEvents() {
                machine.clearEvents();
            }
            
            @Override
            public void publishData(String channel, Map<String, Object> data) {
                // No-op, not supported 
            }
            
            @Override
            public void publishData(String channel, String key, Object value) {
                // No-op, not supported
            }
            
            @Override
            public void transitionTo(LifecycleState newState) {
                if (newState == LifecycleState.ACTIVE) {
                    machine.start();
                } else if (newState == LifecycleState.READY) {
                    machine.initialize();
                } else if (newState == LifecycleState.TERMINATING) {
                    machine.stop();
                } else if (newState == LifecycleState.TERMINATED) {
                    machine.destroy();
                }
            }
            
            @Override
            public void activate() {
                machine.start();
            }
            
            @Override
            public void deactivate() {
                machine.stop();
            }
            
            @Override
            public void terminate() {
                machine.destroy();
            }
            
            @Override
            public String getCompositeId() {
                return machine.getId().getIdString();
            }
            
            @Override
            public boolean addComponent(String name, ComponentPort component) {
                return false;
            }
            
            @Override
            public Optional<ComponentPort> removeComponent(String name) {
                return Optional.empty();
            }
            
            @Override
            public ComponentPort getComponent(String name) {
                return null;
            }
            
            @Override
            public boolean hasComponent(String name) {
                return false;
            }
            
            @Override
            public Map<String, ComponentPort> getComponents() {
                return Collections.emptyMap();
            }
            
            @Override
            public boolean connect(String sourceName, String targetName) {
                return false;
            }
            
            @Override
            public boolean disconnect(String sourceName, String targetName) {
                return false;
            }
            
            @Override
            public List<String> getConnectionsFrom(String sourceName) {
                return Collections.emptyList();
            }
        };
    }
    
    // This is an overloaded method to handle component Machine instances
    public static MachinePort createMachinePort(org.s8r.component.Machine machine) {
        if (machine == null) {
            return null;
        }
        // Create an adapter that converts from component Machine to MachinePort
        return new MachineToComponentPortAdapter(machine);
    }
    
    /**
     * Creates a MachinePort interface from a domain Machine implementation.
     * This method exists to provide a clean interface for using domain machines with ports.
     *
     * @param machine The domain machine to adapt
     * @return A MachinePort interface that delegates to the machine
     */
    public static MachinePort createMachinePortFromDomain(org.s8r.domain.machine.Machine machine) {
        if (machine == null) {
            return null;
        }
        // Create an adapter that converts from domain Machine to MachinePort
        return new MachineToDomainPortAdapter(machine);
    }
    
    /**
     * Creates a MachinePort interface from a component Machine implementation.
     * This is a utility method to support both domain and component machine types.
     *
     * @param machine The component machine to adapt
     * @return A MachinePort interface that delegates to the machine
     */
    public static MachinePort createMachinePortFromComponent(org.s8r.component.Machine machine) {
        if (machine == null) {
            return null;
        }
        // Create an adapter that converts from component Machine to MachinePort
        return new MachineToComponentPortAdapter(machine);
    }
    
    /**
     * Adapter that converts from component Machine to MachinePort.
     * This allows legacy component machines to be used with the port interface.
     */
    private static class MachineToComponentPortAdapter implements MachinePort {
        private final org.s8r.component.Machine machine;
        private final ComponentId componentId;
        
        public MachineToComponentPortAdapter(org.s8r.component.Machine machine) {
            this.machine = machine;
            // Create component ID for this machine
            this.componentId = ComponentId.create("Component Machine: " + machine.getMachineId());
        }
        
        @Override
        public String getMachineId() {
            return machine.getMachineId();
        }
        
        @Override
        public MachineType getMachineType() {
            // Default to DATA_PROCESSOR type as component Machine doesn't have type
            return MachineType.DATA_PROCESSOR;
        }
        
        @Override
        public MachineState getMachineState() {
            // Convert active status to machine state
            return machine.isActive() ? MachineState.RUNNING : MachineState.STOPPED;
        }
        
        @Override
        public void setMachineState(MachineState state) {
            if (state == MachineState.RUNNING) {
                machine.activate();
            } else if (state == MachineState.STOPPED) {
                machine.deactivate();
            }
        }
        
        @Override
        public boolean start() {
            try {
                machine.activate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean stop() {
            try {
                machine.deactivate();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean addComposite(String name, CompositeComponentPort composite) {
            try {
                // Handle composite port conversion
                if (composite instanceof ComponentAdapter.CompositeComponentAdapter) {
                    // Get the domain composite
                    org.s8r.domain.component.composite.CompositeComponent domainComposite = 
                        ((ComponentAdapter.CompositeComponentAdapter) composite).getCompositeComponent();
                    
                    // Create component layer composite
                    org.s8r.component.Composite componentComposite = 
                        new org.s8r.component.Composite(domainComposite.getId().getIdString(), 
                            new org.s8r.component.Environment());
                    
                    machine.addComposite(name, componentComposite);
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public java.util.Optional<CompositeComponentPort> removeComposite(String name) {
            // Component machine doesn't support removing composites
            return java.util.Optional.empty();
        }
        
        @Override
        public CompositeComponentPort getComposite(String name) {
            org.s8r.component.Composite composite = machine.getComposite(name);
            if (composite != null) {
                // Create a composite component from the component composite
                org.s8r.domain.component.composite.CompositeComponent domainComposite = 
                    org.s8r.domain.component.composite.CompositeFactory.createComposite(
                        org.s8r.domain.component.composite.CompositeType.STANDARD, 
                        "Adapted from " + composite.getCompositeId());
                
                // Return a composite port for it
                return ComponentAdapter.createCompositeComponentPort(domainComposite);
            }
            return null;
        }
        
        @Override
        public Map<String, CompositeComponentPort> getComposites() {
            Map<String, org.s8r.component.Composite> composites = machine.getComposites();
            Map<String, CompositeComponentPort> result = new HashMap<>();
            
            for (Map.Entry<String, org.s8r.component.Composite> entry : composites.entrySet()) {
                String name = entry.getKey();
                org.s8r.component.Composite composite = entry.getValue();
                
                // Create a composite component for each component composite
                org.s8r.domain.component.composite.CompositeComponent domainComposite = 
                    org.s8r.domain.component.composite.CompositeFactory.createComposite(
                        org.s8r.domain.component.composite.CompositeType.STANDARD, 
                        "Adapted from " + composite.getCompositeId());
                
                result.put(name, ComponentAdapter.createCompositeComponentPort(domainComposite));
            }
            
            return Collections.unmodifiableMap(result);
        }
        
        @Override
        public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
            try {
                machine.connect(sourceCompositeName, targetCompositeName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public Map<String, List<String>> getCompositeConnections() {
            return machine.getConnections();
        }
        
        @Override
        public Map<String, List<String>> getConnections() {
            // Same as composite connections
            return getCompositeConnections();
        }
        
        // ComponentPort methods
        
        @Override
        public ComponentId getId() {
            return componentId;
        }
        
        @Override
        public LifecycleState getLifecycleState() {
            return machine.isActive() ? LifecycleState.ACTIVE : LifecycleState.READY;
        }
        
        @Override
        public List<String> getLineage() {
            return Collections.emptyList();
        }
        
        @Override
        public List<String> getActivityLog() {
            return Collections.emptyList();
        }
        
        @Override
        public java.time.Instant getCreationTime() {
            return java.time.Instant.now();
        }
        
        @Override
        public List<DomainEvent> getDomainEvents() {
            return Collections.emptyList();
        }
        
        @Override
        public void addToLineage(String entry) {
            // No-op
        }
        
        @Override
        public void clearEvents() {
            // No-op
        }
        
        @Override
        public void publishData(String channel, Map<String, Object> data) {
            // No-op
        }
        
        @Override
        public void publishData(String channel, String key, Object value) {
            // No-op
        }
        
        @Override
        public void transitionTo(LifecycleState newState) {
            if (newState == LifecycleState.ACTIVE) {
                machine.activate();
            } else if (newState == LifecycleState.READY) {
                machine.deactivate();
            }
        }
        
        @Override
        public void activate() {
            machine.activate();
        }
        
        @Override
        public void deactivate() {
            machine.deactivate();
        }
        
        @Override
        public void terminate() {
            machine.shutdown();
        }
        
        @Override
        public String getCompositeId() {
            return machine.getMachineId();
        }
        
        @Override
        public boolean addComponent(String name, ComponentPort component) {
            return false; // Not supported at this level
        }
        
        @Override
        public Optional<ComponentPort> removeComponent(String name) {
            return Optional.empty(); // Not supported at this level
        }
        
        @Override
        public ComponentPort getComponent(String name) {
            return null; // Not supported at this level
        }
        
        @Override
        public boolean hasComponent(String name) {
            return false; // Not supported at this level
        }
        
        @Override
        public Map<String, ComponentPort> getComponents() {
            return Collections.emptyMap(); // Not supported at this level
        }
        
        @Override
        public boolean connect(String sourceName, String targetName) {
            try {
                machine.connect(sourceName, targetName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean disconnect(String sourceName, String targetName) {
            return false; // Not supported at this level
        }
        
        @Override
        public List<String> getConnectionsFrom(String sourceName) {
            Map<String, List<String>> connections = machine.getConnections();
            return connections.getOrDefault(sourceName, Collections.emptyList());
        }
    }
    
    /**
     * Adapter that wraps a Machine to provide the MachinePort interface.
     * This implements the Adapter pattern for Clean Architecture, allowing
     * component-based machines to be used through the domain layer port interfaces.
     */
    private static class MachineToDomainPortAdapter implements MachinePort {
        private final org.s8r.domain.machine.Machine machine;
        
        public MachineToDomainPortAdapter(org.s8r.domain.machine.Machine machine) {
            this.machine = machine;
        }
        
        @Override
        public String getMachineId() {
            // Get ID from the domain machine
            return machine.getId().getIdString();
        }
        
        @Override
        public MachineType getMachineType() {
            // Get the machine type from the domain machine
            return machine.getType();
        }
        
        @Override
        public MachineState getMachineState() {
            // Get machine state
            return machine.getState();
        }
        
        @Override
        public void setMachineState(MachineState state) {
            // Map the machine state to appropriate actions
            if (state == MachineState.RUNNING) {
                machine.start();
            } else if (state == MachineState.STOPPED) {
                machine.stop();
            } else if (state == MachineState.DESTROYED) {
                machine.destroy();
            } else if (state == MachineState.READY) {
                machine.initialize();
            }
            // ERROR state not handled, would require custom error handling
        }
        
        @Override
        public boolean start() {
            try {
                machine.start();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean stop() {
            try {
                machine.stop();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean addComposite(String name, CompositeComponentPort composite) {
            // This requires conversion from CompositeComponentPort to CompositeComponent
            if (composite instanceof org.s8r.adapter.ComponentAdapter.CompositeComponentAdapter) {
                // We need to get the underlying composite component
                org.s8r.domain.component.composite.CompositeComponent domainComposite = 
                    ((org.s8r.adapter.ComponentAdapter.CompositeComponentAdapter) composite).getCompositeComponent();
                
                // For domain.machine.Machine, use the addComponent method
                try {
                    machine.addComponent(domainComposite);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }
        
        @Override
        public java.util.Optional<CompositeComponentPort> removeComposite(String name) {
            // Machine doesn't support removing composites directly
            return java.util.Optional.empty();
        }
        
        @Override
        public CompositeComponentPort getComposite(String name) {
            // For domain machine, we need to find the composite component by ID
            for (org.s8r.domain.component.composite.CompositeComponent component : machine.getComponents()) {
                if (component.getId().getIdString().equals(name)) {
                    // Create a CompositeComponentPort for the domain component
                    return org.s8r.adapter.ComponentAdapter.createCompositeComponentPort(component);
                }
            }
            return null;
        }
        
        @Override
        public Map<String, CompositeComponentPort> getComposites() {
            // Convert the list of CompositeComponents to a map of CompositeComponentPorts
            List<org.s8r.domain.component.composite.CompositeComponent> components = machine.getComponents();
            Map<String, CompositeComponentPort> ports = new java.util.HashMap<>();
            
            for (org.s8r.domain.component.composite.CompositeComponent component : components) {
                // Use component ID as the key
                String idString = component.getId().getIdString();
                ports.put(idString, org.s8r.adapter.ComponentAdapter.createCompositeComponentPort(component));
            }
            
            return java.util.Collections.unmodifiableMap(ports);
        }
        
        @Override
        public boolean connectComposites(String sourceCompositeName, String targetCompositeName) {
            // Domain machine doesn't have a connect method for string names
            // Need to find components by ID and implement connection
            // Currently not supported for domain.machine.Machine
            return false;
        }
        
        @Override
        public Map<String, List<String>> getCompositeConnections() {
            // Domain machine doesn't have a simple getConnections method
            // Need to return an empty map since connections aren't supported this way
            return java.util.Collections.emptyMap();
        }
        
        @Override
        public Map<String, List<String>> getConnections() {
            // Same as getCompositeConnections for machines
            return getCompositeConnections();
        }
        
        // ComponentPort methods delegated to the machine
        
        @Override
        public org.s8r.domain.identity.ComponentId getId() {
            // Get the domain machine's ID directly
            return machine.getId();
        }
        
        @Override
        public org.s8r.domain.lifecycle.LifecycleState getLifecycleState() {
            // Map MachineState to LifecycleState
            MachineState state = machine.getState();
            if (state == MachineState.RUNNING) {
                return org.s8r.domain.lifecycle.LifecycleState.ACTIVE;
            } else if (state == MachineState.READY) {
                return org.s8r.domain.lifecycle.LifecycleState.READY;
            } else if (state == MachineState.STOPPED) {
                return org.s8r.domain.lifecycle.LifecycleState.READY;
            } else if (state == MachineState.DESTROYED) {
                return org.s8r.domain.lifecycle.LifecycleState.TERMINATED;
            } else {
                // Created or any other state
                return org.s8r.domain.lifecycle.LifecycleState.READY;
            }
        }
        
        @Override
        public List<String> getLineage() {
            // Machine doesn't have lineage concept
            return java.util.Collections.emptyList();
        }
        
        @Override
        public List<String> getActivityLog() {
            // Return the activity log from the domain machine
            return machine.getActivityLog();
        }
        
        @Override
        public java.time.Instant getCreationTime() {
            // Return the creation time from the domain machine
            return machine.getCreationTime();
        }
        
        @Override
        public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
            // Return domain events from the domain machine
            return machine.getDomainEvents();
        }
        
        @Override
        public void addToLineage(String entry) {
            // No-op, machine doesn't have lineage
        }
        
        @Override
        public void clearEvents() {
            // Clear events in the domain machine
            machine.clearEvents();
        }
        
        @Override
        public void publishData(String channel, Map<String, Object> data) {
            // No direct equivalent in Machine
        }
        
        @Override
        public void publishData(String channel, String key, Object value) {
            // No direct equivalent in Machine
        }
        
        @Override
        public void transitionTo(org.s8r.domain.lifecycle.LifecycleState newState) {
            // Map lifecycle state to machine activation
            if (newState == org.s8r.domain.lifecycle.LifecycleState.ACTIVE) {
                machine.start();
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.READY) {
                machine.initialize();
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.TERMINATING) {
                machine.stop();
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.TERMINATED) {
                machine.destroy();
            }
        }
        
        @Override
        public void activate() {
            machine.start();
        }
        
        @Override
        public void deactivate() {
            machine.stop();
        }
        
        @Override
        public void terminate() {
            machine.destroy();
        }
        
        @Override
        public String getCompositeId() {
            return machine.getId().getIdString();
        }
        
        @Override
        public boolean addComponent(String name, org.s8r.domain.component.port.ComponentPort component) {
            // Not supported at this level, components are added to composites, not machines
            return false;
        }
        
        @Override
        public java.util.Optional<org.s8r.domain.component.port.ComponentPort> removeComponent(String name) {
            // Not supported at this level
            return java.util.Optional.empty();
        }
        
        @Override
        public org.s8r.domain.component.port.ComponentPort getComponent(String name) {
            // Not supported at this level
            return null;
        }
        
        @Override
        public boolean hasComponent(String name) {
            // Not supported at this level
            return false;
        }
        
        @Override
        public Map<String, org.s8r.domain.component.port.ComponentPort> getComponents() {
            // Not supported at this level
            return java.util.Collections.emptyMap();
        }
        
        @Override
        public boolean connect(String sourceName, String targetName) {
            // Domain machine doesn't have a connect method for string names
            // This operation is not supported for domain machines
            return false;
        }
        
        @Override
        public boolean disconnect(String sourceName, String targetName) {
            // Machine doesn't support disconnecting
            return false;
        }
        
        @Override
        public List<String> getConnectionsFrom(String sourceName) {
            // Domain machine doesn't have a getConnections method that returns a map
            // This operation would need a custom implementation using domain machine APIs
            return java.util.Collections.emptyList();
        }
    }
    
    /**
     * Adapter that wraps a Composite to provide the CompositeComponentPort interface.
     */
    private static class CompositeToPortAdapter implements CompositeComponentPort {
        private final Composite composite;
        
        public CompositeToPortAdapter(Composite composite) {
            this.composite = composite;
        }
        
        @Override
        public String getCompositeId() {
            return composite.getCompositeId();
        }
        
        @Override
        public boolean addComponent(String name, org.s8r.domain.component.port.ComponentPort component) {
            // This requires conversion from ComponentPort to Component
            if (component instanceof Component) {
                composite.addComponent(name, (Component) component);
                return true;
            }
            return false;
        }
        
        @Override
        public java.util.Optional<org.s8r.domain.component.port.ComponentPort> removeComponent(String name) {
            // Composite doesn't support removing components
            return java.util.Optional.empty();
        }
        
        @Override
        public org.s8r.domain.component.port.ComponentPort getComponent(String name) {
            Component component = composite.getComponent(name);
            if (component != null) {
                return new ComponentToPortAdapter(component);
            }
            return null;
        }
        
        @Override
        public boolean hasComponent(String name) {
            return composite.getComponent(name) != null;
        }
        
        @Override
        public Map<String, org.s8r.domain.component.port.ComponentPort> getComponents() {
            Map<String, Component> components = composite.getComponents();
            Map<String, org.s8r.domain.component.port.ComponentPort> ports = new java.util.HashMap<>();
            
            for (Map.Entry<String, Component> entry : components.entrySet()) {
                ports.put(entry.getKey(), new ComponentToPortAdapter(entry.getValue()));
            }
            
            return java.util.Collections.unmodifiableMap(ports);
        }
        
        @Override
        public boolean connect(String sourceName, String targetName) {
            try {
                composite.connect(sourceName, targetName);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean disconnect(String sourceName, String targetName) {
            // Composite doesn't support disconnecting
            return false;
        }
        
        @Override
        public Map<String, List<String>> getConnections() {
            return composite.getConnections();
        }
        
        @Override
        public List<String> getConnectionsFrom(String sourceName) {
            Map<String, List<String>> connections = composite.getConnections();
            return connections.getOrDefault(sourceName, java.util.Collections.emptyList());
        }
        
        // ComponentPort methods
        
        @Override
        public org.s8r.domain.identity.ComponentId getId() {
            // Create a ComponentId based on the composite ID
            return org.s8r.domain.identity.ComponentId.create(
                    "Composite adapter for " + composite.getCompositeId());
        }
        
        @Override
        public org.s8r.domain.lifecycle.LifecycleState getLifecycleState() {
            // Map isActive to lifecycle state
            return composite.isActive() 
                ? org.s8r.domain.lifecycle.LifecycleState.ACTIVE 
                : org.s8r.domain.lifecycle.LifecycleState.READY;
        }
        
        @Override
        public List<String> getLineage() {
            // Composite doesn't have lineage
            return java.util.Collections.emptyList();
        }
        
        @Override
        public List<String> getActivityLog() {
            // Composite doesn't have activity log
            return java.util.Collections.emptyList();
        }
        
        @Override
        public java.time.Instant getCreationTime() {
            // Composite doesn't track creation time
            return java.time.Instant.now();
        }
        
        @Override
        public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
            // Composite doesn't track domain events
            return java.util.Collections.emptyList();
        }
        
        @Override
        public void addToLineage(String entry) {
            // No-op, composite doesn't have lineage
        }
        
        @Override
        public void clearEvents() {
            // No-op, composite doesn't track events
        }
        
        @Override
        public void publishData(String channel, Map<String, Object> data) {
            // No-op, composite doesn't support this directly
        }
        
        @Override
        public void publishData(String channel, String key, Object value) {
            // No-op, composite doesn't support this directly
        }
        
        @Override
        public void transitionTo(org.s8r.domain.lifecycle.LifecycleState newState) {
            // Map lifecycle state to composite activation
            if (newState == org.s8r.domain.lifecycle.LifecycleState.ACTIVE) {
                composite.activate();
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.READY ||
                       newState == org.s8r.domain.lifecycle.LifecycleState.TERMINATING) {
                composite.deactivate();
            }
        }
        
        @Override
        public void activate() {
            composite.activate();
        }
        
        @Override
        public void deactivate() {
            composite.deactivate();
        }
        
        @Override
        public void terminate() {
            // Deactivate is the closest operation
            composite.deactivate();
        }
    }
    
    /**
     * Adapter that wraps a Component to provide the ComponentPort interface.
     */
    private static class ComponentToPortAdapter implements org.s8r.domain.component.port.ComponentPort {
        private final Component component;
        
        public ComponentToPortAdapter(Component component) {
            this.component = component;
        }
        
        @Override
        public org.s8r.domain.identity.ComponentId getId() {
            // Create a ComponentId from the Component's ID or name
            // This is a simplification as Component doesn't have a ComponentId
            return org.s8r.domain.identity.ComponentId.create(
                    "Component adapter for " + component.getUniqueId());
        }
        
        @Override
        public org.s8r.domain.lifecycle.LifecycleState getLifecycleState() {
            // Map Component.State to LifecycleState
            if (component.isActive()) {
                return org.s8r.domain.lifecycle.LifecycleState.ACTIVE;
            } else if (component.isReady()) {
                return org.s8r.domain.lifecycle.LifecycleState.READY;
            } else if (component.isTerminated()) {
                return org.s8r.domain.lifecycle.LifecycleState.TERMINATED;
            } else {
                return org.s8r.domain.lifecycle.LifecycleState.READY;
            }
        }
        
        @Override
        public List<String> getLineage() {
            // Return component's lineage
            return component.getLineage();
        }
        
        @Override
        public List<String> getActivityLog() {
            // Component has memoryLog which serves as its activity log
            return component.getMemoryLog();
        }
        
        @Override
        public java.time.Instant getCreationTime() {
            // Component has conceptionTime
            return component.getConceptionTime();
        }
        
        @Override
        public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
            // Component doesn't track domain events
            return java.util.Collections.emptyList();
        }
        
        @Override
        public void addToLineage(String entry) {
            // Add to component's lineage
            component.addToLineage(entry);
        }
        
        @Override
        public void clearEvents() {
            // No-op, component doesn't track events
        }
        
        @Override
        public void publishData(String channel, Map<String, Object> data) {
            // No-op, component doesn't support this directly
        }
        
        @Override
        public void publishData(String channel, String key, Object value) {
            // No-op, component doesn't support this directly
        }
        
        @Override
        public void transitionTo(org.s8r.domain.lifecycle.LifecycleState newState) {
            // Map lifecycle state to component state
            if (newState == org.s8r.domain.lifecycle.LifecycleState.ACTIVE) {
                component.setState(State.ACTIVE);
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.READY) {
                component.setState(State.READY);
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.TERMINATING) {
                component.setState(State.TERMINATING);
            } else if (newState == org.s8r.domain.lifecycle.LifecycleState.TERMINATED) {
                component.terminate();
            }
        }
        
        @Override
        public void activate() {
            // Set the component state to active
            component.setState(State.ACTIVE);
        }
        
        @Override
        public void deactivate() {
            // Set the component state to ready
            component.setState(State.READY);
        }
        
        @Override
        public void terminate() {
            // Terminate the component
            component.terminate();
        }
    }
    
    /**
     * A Component-based Machine implementation that delegates to a Tube-based Machine.
     */
    private static class TubeMachineWrapper extends Machine {
        
        private final org.s8r.tube.machine.Machine tubeMachine;
        private final CompositeAdapter compositeAdapter;
        private final LoggerPort logger;
        private final Map<String, Composite> compositeCache;
        
        public TubeMachineWrapper(
                String machineId,
                Environment environment,
                org.s8r.tube.machine.Machine tubeMachine,
                CompositeAdapter compositeAdapter,
                LoggerPort logger) {
            super(machineId, environment);
            this.tubeMachine = tubeMachine;
            this.compositeAdapter = compositeAdapter;
            this.logger = logger;
            this.compositeCache = new ConcurrentHashMap<>();
            
            // Copy state from the tube machine
            Map<String, Object> tubeState = tubeMachine.getState();
            for (Map.Entry<String, Object> entry : tubeState.entrySet()) {
                super.updateState(entry.getKey(), entry.getValue());
            }
            
            // Set initial activation status
            if (!tubeMachine.isActive() && super.isActive()) {
                super.deactivate(); // Machine starts active by default, deactivate if tube is inactive
            }
            
            logger.debug("Created TubeMachineWrapper for machine: {}", machineId);
        }
        
        @Override
        public Machine addComposite(String name, Composite composite) {
            logger.debug("Adding composite to TubeMachineWrapper: {}", name);
            
            // Extract tube composite using reflection when needed
            org.s8r.tube.composite.Composite tubeComposite = null;
            try {
                // Try direct cast if possible
                if (composite instanceof CompositeAdapter.TubeCompositeWrapper) {
                    tubeComposite = ((CompositeAdapter.TubeCompositeWrapper) composite).unwrapTubeComposite();
                } else {
                    // Try to access via reflection
                    try {
                        java.lang.reflect.Method unwrapMethod = composite.getClass().getMethod("unwrapTubeComposite");
                        Object result = unwrapMethod.invoke(composite);
                        if (result instanceof org.s8r.tube.composite.Composite) {
                            tubeComposite = (org.s8r.tube.composite.Composite) result;
                        }
                    } catch (Exception e) {
                        logger.warn("Cannot extract tube composite: no unwrapTubeComposite method: {}", e.getMessage());
                    }
                }
                
                // Add to tube machine if we were able to get the tube composite
                if (tubeComposite != null) {
                    tubeMachine.addComposite(name, tubeComposite);
                } else {
                    logger.warn("Cannot add non-wrapper composite to tube machine: {}. " +
                            "The composite will only be available in the wrapper, not the underlying machine.", name);
                }
            } catch (Exception e) {
                logger.error("Error extracting tube composite: {}", e.getMessage());
            }
            
            // Cache the component composite
            compositeCache.put(name, composite);
            
            return super.addComposite(name, composite);
        }
        
        @Override
        public Machine connect(String sourceName, String targetName) {
            logger.debug("Connecting composites in TubeMachineWrapper: {} -> {}", sourceName, targetName);
            
            // Connect in the tube machine
            tubeMachine.connect(sourceName, targetName);
            
            // Connect in this machine too
            return super.connect(sourceName, targetName);
        }
        
        @Override
        public Machine updateState(String key, Object value) {
            logger.debug("Updating state in TubeMachineWrapper: {} = {}", key, value);
            
            // Update state in the tube machine
            tubeMachine.updateState(key, value);
            
            // Update state in this machine too
            return super.updateState(key, value);
        }
        
        @Override
        public Machine activate() {
            logger.debug("Activating TubeMachineWrapper");
            
            // Activate the tube machine
            tubeMachine.activate();
            
            // Activate this machine too (only if we were inactive before)
            if (!super.isActive()) {
                super.activate();
            }
            
            return this;
        }
        
        @Override
        public Machine deactivate() {
            logger.debug("Deactivating TubeMachineWrapper");
            
            // Deactivate the tube machine
            tubeMachine.deactivate();
            
            // Deactivate this machine too (only if we were active before)
            if (super.isActive()) {
                super.deactivate();
            }
            
            return this;
        }
        
        @Override
        public void shutdown() {
            logger.debug("Shutting down TubeMachineWrapper");
            
            // Shutdown the tube machine
            tubeMachine.shutdown();
            
            // Shutdown this machine too
            super.shutdown();
        }
        
        @Override
        public Composite getComposite(String name) {
            logger.debug("Getting composite from TubeMachineWrapper: {}", name);
            
            // Try to get from the cache first
            Composite cachedComposite = compositeCache.get(name);
            if (cachedComposite != null) {
                return cachedComposite;
            }
            
            // Create a wrapper for the tube composite and cache it
            try {
                org.s8r.tube.composite.Composite tubeComposite = tubeMachine.getComposite(name);
                Composite componentComposite = compositeAdapter.wrapTubeComposite(tubeComposite);
                compositeCache.put(name, componentComposite);
                return componentComposite;
            } catch (IllegalArgumentException e) {
                // If not found, let the parent method throw the appropriate exception
                return super.getComposite(name);
            }
        }
        
        @Override
        public Map<String, Composite> getComposites() {
            logger.debug("Getting all composites from TubeMachineWrapper");
            
            // Create wrappers for all tube composites if they don't already exist in the cache
            Map<String, org.s8r.tube.composite.Composite> tubeComposites = tubeMachine.getComposites();
            for (Map.Entry<String, org.s8r.tube.composite.Composite> entry : tubeComposites.entrySet()) {
                String name = entry.getKey();
                if (!compositeCache.containsKey(name)) {
                    compositeCache.put(name, compositeAdapter.wrapTubeComposite(entry.getValue()));
                }
            }
            
            // Return unmodifiable view of the cache
            return java.util.Collections.unmodifiableMap(compositeCache);
        }
        
        @Override
        public Map<String, List<String>> getConnections() {
            logger.debug("Getting connections from TubeMachineWrapper");
            
            // Return connections from the tube machine
            return tubeMachine.getConnections();
        }
        
        /**
         * Gets the underlying Tube machine.
         *
         * @return The wrapped Tube machine
         */
        public org.s8r.tube.machine.Machine getTubeMachine() {
            return tubeMachine;
        }
    }
}