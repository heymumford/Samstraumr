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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.LoggerPort;
import org.s8r.component.Composite;
import org.s8r.component.Environment;
import org.s8r.component.Machine;

/**
 * Adapter for converting between legacy Tube-based Machines and new Component-based Machines.
 * <p>
 * This adapter facilitates migration from legacy code by allowing existing Tube machines
 * to be used with the new Component-based architecture. The adapter handles the conversion
 * of composites, connections, and state management.
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