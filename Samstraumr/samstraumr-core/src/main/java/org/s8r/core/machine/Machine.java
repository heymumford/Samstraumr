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

package org.s8r.core.machine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.s8r.core.composite.Composite;
import org.s8r.core.env.Environment;
import org.s8r.core.tube.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Machine represents the highest level of composition in the S8r architecture.
 * 
 * <p>It manages and orchestrates multiple Composite components to form a complete processing system.
 * Machines provide the ability to connect composites together, manage their lifecycle, and 
 * implement higher-level behaviors.
 */
public class Machine {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Machine.class);
    
    private final String machineId;
    private final MachineType type;
    private final String name;
    private final String description;
    private final Environment environment;
    private final Map<String, Composite> composites;
    private final Map<String, List<String>> connections;
    private final List<MachineEvent> eventLog;
    private final Logger logger;
    private final Instant creationTime;
    private final AtomicBoolean active;
    private final Map<String, Object> state;
    private MachineState machineState;
    private String version;
    
    /**
     * Creates a new Machine.
     *
     * @param machineId The unique identifier for this machine
     * @param type The type of this machine
     * @param name The name of this machine
     * @param description The description of this machine
     * @param environment The environment in which this machine operates
     */
    Machine(String machineId, MachineType type, String name, String description, Environment environment) {
        this.machineId = Objects.requireNonNull(machineId, "Machine ID cannot be null");
        this.type = Objects.requireNonNull(type, "Machine type cannot be null");
        this.name = Objects.requireNonNull(name, "Machine name cannot be null");
        this.description = description != null ? description : "";
        this.environment = Objects.requireNonNull(environment, "Environment cannot be null");
        this.composites = new ConcurrentHashMap<>();
        this.connections = new ConcurrentHashMap<>();
        this.eventLog = new CopyOnWriteArrayList<>();
        this.active = new AtomicBoolean(false);
        this.state = new ConcurrentHashMap<>();
        this.creationTime = Instant.now();
        this.machineState = MachineState.CREATED;
        this.version = "1.0.0";
        
        // Initialize logger
        this.logger = new Logger("Machine-" + machineId);
        
        // Initialize basic state
        state.put("status", MachineState.CREATED.name());
        state.put("createdAt", creationTime);
        state.put("type", type.name());
        
        logEvent("Machine created: " + name + " (Type: " + type + ")");
        LOGGER.info("Machine {} created with type {}", machineId, type);
    }
    
    /**
     * Creates a new Machine.
     * 
     * @param machineId The machine ID
     * @param type The machine type
     * @param name The machine name
     * @param description The machine description
     * @param version The machine version
     * @param environment The environment
     * @return A new machine
     */
    public static Machine create(
            String machineId, 
            MachineType type, 
            String name, 
            String description, 
            String version,
            Environment environment) {
        Machine machine = new Machine(machineId, type, name, description, environment);
        if (version != null) {
            machine.version = version;
        }
        return machine;
    }
    
    /**
     * Creates a new Machine with a generated ID.
     * 
     * @param type The machine type
     * @param name The machine name
     * @param description The machine description
     * @param environment The environment
     * @return A new machine
     */
    public static Machine create(
            MachineType type, 
            String name, 
            String description,
            Environment environment) {
        return create(generateMachineId(type, name), type, name, description, null, environment);
    }
    
    /**
     * Adds a composite to this machine.
     *
     * @param name The name to reference this composite by within the machine
     * @param composite The composite to add
     * @return This machine instance for method chaining
     * @throws MachineException if the machine state doesn't allow adding composites
     */
    public Machine addComposite(String name, Composite composite) {
        if (!isModifiable()) {
            throw MachineException.invalidOperation("addComposite", machineId, machineState);
        }
        
        Objects.requireNonNull(name, "Composite name cannot be null");
        Objects.requireNonNull(composite, "Composite cannot be null");
        
        if (composites.containsKey(name)) {
            logger.warn("Replacing existing composite with name: " + name, "MACHINE", "COMPOSITE");
            LOGGER.warn("Machine {}: Replacing existing composite with name: {}", machineId, name);
        }
        
        composites.put(name, composite);
        logEvent("Composite added to machine: " + name);
        return this;
    }
    
    /**
     * Removes a composite from this machine.
     *
     * @param compositeName The name of the composite to remove
     * @return This machine instance for method chaining
     * @throws MachineException if the machine state doesn't allow removing composites
     */
    public Machine removeComposite(String compositeName) {
        if (!isModifiable()) {
            throw MachineException.invalidOperation("removeComposite", machineId, machineState);
        }
        
        if (!composites.containsKey(compositeName)) {
            throw MachineException.componentNotFound(machineId, compositeName);
        }
        
        composites.remove(compositeName);
        
        // Remove all connections involving this composite
        connections.remove(compositeName);
        for (List<String> targets : connections.values()) {
            targets.remove(compositeName);
        }
        
        logEvent("Composite removed from machine: " + compositeName);
        return this;
    }
    
    /**
     * Connects two composites in the machine to allow data flow.
     *
     * @param sourceName The name of the source composite
     * @param targetName The name of the target composite
     * @return This machine instance for method chaining
     * @throws MachineException if either composite doesn't exist or the machine state doesn't allow creating connections
     */
    public Machine connect(String sourceName, String targetName) {
        if (!isModifiable()) {
            throw MachineException.invalidOperation("connect", machineId, machineState);
        }
        
        validateCompositeExists(sourceName);
        validateCompositeExists(targetName);
        
        connections.computeIfAbsent(sourceName, k -> new ArrayList<>()).add(targetName);
        
        logEvent(String.format("Connected composites: %s -> %s", sourceName, targetName));
        LOGGER.debug("Connected composites in machine {}: {} -> {}", machineId, sourceName, targetName);
        return this;
    }
    
    /**
     * Gets a composite from the machine by name.
     *
     * @param name The name of the composite to retrieve
     * @return The requested composite wrapped in an Optional
     */
    public Optional<Composite> getComposite(String name) {
        return Optional.ofNullable(composites.get(name));
    }
    
    /**
     * Gets all composites in this machine.
     *
     * @return An unmodifiable map of composite names to composites
     */
    public Map<String, Composite> getComposites() {
        return Collections.unmodifiableMap(composites);
    }
    
    /**
     * Gets all connections in this machine.
     *
     * @return An unmodifiable map of source composite names to lists of target composite names
     */
    public Map<String, List<String>> getConnections() {
        return Collections.unmodifiableMap(connections);
    }
    
    /**
     * Gets all event logs from this machine.
     *
     * @return An unmodifiable list of all events logged by this machine
     */
    public List<MachineEvent> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }
    
    /**
     * Gets the machine's state.
     *
     * @return An unmodifiable map of state key-value pairs
     */
    public Map<String, Object> getState() {
        return Collections.unmodifiableMap(state);
    }
    
    /**
     * Updates the machine's state.
     *
     * @param key The state key to update
     * @param value The new value
     * @return This machine instance for method chaining
     */
    public Machine updateState(String key, Object value) {
        state.put(key, value);
        logEvent("State updated: " + key + " = " + value);
        return this;
    }
    
    /**
     * Initializes this machine, preparing it for operation.
     *
     * @return This machine instance for method chaining
     * @throws MachineException if the machine is not in the CREATED state
     */
    public Machine initialize() {
        if (machineState != MachineState.CREATED) {
            throw MachineException.invalidOperation("initialize", machineId, machineState);
        }
        
        logEvent("Initializing machine");
        
        // Initialize all composites if needed
        
        // Update machine state
        MachineState oldState = machineState;
        machineState = MachineState.READY;
        state.put("status", MachineState.READY.name());
        
        logEvent("Machine initialized successfully");
        LOGGER.info("Machine {} initialized successfully, state changed from {} to {}", 
                machineId, oldState, machineState);
        
        return this;
    }
    
    /**
     * Starts this machine, activating all composites.
     *
     * @return This machine instance for method chaining
     * @throws MachineException if the machine is not in a startable state
     */
    public Machine start() {
        if (machineState != MachineState.READY && machineState != MachineState.STOPPED && 
            machineState != MachineState.PAUSED) {
            throw MachineException.invalidOperation("start", machineId, machineState);
        }
        
        logEvent("Starting machine");
        
        // Activate all composites
        for (Map.Entry<String, Composite> entry : composites.entrySet()) {
            try {
                entry.getValue().activate();
                logEvent("Activated composite: " + entry.getKey());
            } catch (Exception e) {
                logEvent("Failed to activate composite: " + entry.getKey() + ", reason: " + e.getMessage());
                logger.error("Failed to activate composite: " + entry.getKey(), "MACHINE", "ERROR");
            }
        }
        
        // Update machine state
        MachineState oldState = machineState;
        machineState = MachineState.RUNNING;
        state.put("status", MachineState.RUNNING.name());
        active.set(true);
        
        logEvent("Machine started successfully");
        LOGGER.info("Machine {} started successfully, state changed from {} to {}", 
                machineId, oldState, machineState);
        
        return this;
    }
    
    /**
     * Stops this machine, deactivating all composites.
     *
     * @return This machine instance for method chaining
     * @throws MachineException if the machine is not in the RUNNING state
     */
    public Machine stop() {
        if (machineState != MachineState.RUNNING) {
            throw MachineException.invalidOperation("stop", machineId, machineState);
        }
        
        logEvent("Stopping machine");
        
        // Deactivate all composites
        for (Map.Entry<String, Composite> entry : composites.entrySet()) {
            try {
                entry.getValue().setWaiting();
                logEvent("Deactivated composite: " + entry.getKey());
            } catch (Exception e) {
                logEvent("Failed to deactivate composite: " + entry.getKey() + ", reason: " + e.getMessage());
                logger.error("Failed to deactivate composite: " + entry.getKey(), "MACHINE", "ERROR");
            }
        }
        
        // Update machine state
        MachineState oldState = machineState;
        machineState = MachineState.STOPPED;
        state.put("status", MachineState.STOPPED.name());
        active.set(false);
        
        logEvent("Machine stopped successfully");
        LOGGER.info("Machine {} stopped successfully, state changed from {} to {}", 
                machineId, oldState, machineState);
        
        return this;
    }
    
    /**
     * Pauses this machine, temporarily suspending its operation.
     *
     * @return This machine instance for method chaining
     * @throws MachineException if the machine is not in the RUNNING state
     */
    public Machine pause() {
        if (machineState != MachineState.RUNNING) {
            throw MachineException.invalidOperation("pause", machineId, machineState);
        }
        
        logEvent("Pausing machine");
        
        // Update machine state
        MachineState oldState = machineState;
        machineState = MachineState.PAUSED;
        state.put("status", MachineState.PAUSED.name());
        
        logEvent("Machine paused successfully");
        LOGGER.info("Machine {} paused successfully, state changed from {} to {}", 
                machineId, oldState, machineState);
        
        return this;
    }
    
    /**
     * Destroys this machine, terminating all composites and releasing resources.
     */
    public void destroy() {
        logEvent("Destroying machine");
        
        // Terminate all composites
        for (Map.Entry<String, Composite> entry : composites.entrySet()) {
            try {
                entry.getValue().terminate();
                logEvent("Terminated composite: " + entry.getKey());
            } catch (Exception e) {
                logEvent("Failed to terminate composite: " + entry.getKey() + ", reason: " + e.getMessage());
                logger.error("Failed to terminate composite: " + entry.getKey(), "MACHINE", "ERROR");
            }
        }
        
        // Update machine state
        MachineState oldState = machineState;
        machineState = MachineState.DESTROYED;
        state.put("status", MachineState.DESTROYED.name());
        active.set(false);
        
        logEvent("Machine destroyed successfully");
        LOGGER.info("Machine {} destroyed successfully, state changed from {} to {}", 
                machineId, oldState, machineState);
    }
    
    /**
     * Checks if the machine is currently active.
     *
     * @return true if the machine is active, false otherwise
     */
    public boolean isActive() {
        return active.get();
    }
    
    /**
     * Checks if the machine is in a modifiable state.
     *
     * @return true if the machine can be modified, false otherwise
     */
    private boolean isModifiable() {
        return machineState == MachineState.CREATED
                || machineState == MachineState.READY
                || machineState == MachineState.STOPPED
                || machineState == MachineState.PAUSED;
    }
    
    /**
     * Validates that a composite exists in this machine.
     *
     * @param compositeName The name of the composite to validate
     * @throws MachineException if the composite doesn't exist
     */
    private void validateCompositeExists(String compositeName) {
        if (!composites.containsKey(compositeName)) {
            throw MachineException.componentNotFound(machineId, compositeName);
        }
    }
    
    /**
     * Sets the version of this machine.
     *
     * @param version The new version
     * @return This machine instance for method chaining
     * @throws MachineException if the machine is not in a modifiable state
     */
    public Machine setVersion(String version) {
        if (!isModifiable()) {
            throw MachineException.invalidOperation("setVersion", machineId, machineState);
        }
        
        this.version = version;
        logEvent("Version updated to: " + version);
        return this;
    }
    
    /**
     * Logs an event in the machine's event log.
     *
     * @param description The description of the event
     */
    public void logEvent(String description) {
        MachineEvent event = new MachineEvent(description);
        eventLog.add(event);
        LOGGER.debug("Machine {} event: {}", machineId, description);
        logger.info(description, "MACHINE", "EVENT");
    }
    
    /**
     * Generates a unique machine ID.
     *
     * @param type The machine type
     * @param name The machine name
     * @return A unique machine ID
     */
    private static String generateMachineId(MachineType type, String name) {
        String prefix = type.name().substring(0, Math.min(3, type.name().length())).toUpperCase();
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return prefix + "-" + suffix;
    }
    
    // Getters
    
    /**
     * Gets the machine's unique identifier.
     *
     * @return The machine ID
     */
    public String getMachineId() {
        return machineId;
    }
    
    /**
     * Gets the machine's type.
     *
     * @return The machine type
     */
    public MachineType getType() {
        return type;
    }
    
    /**
     * Gets the machine's name.
     *
     * @return The machine name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the machine's description.
     *
     * @return The machine description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gets the machine's current state.
     *
     * @return The machine state
     */
    public MachineState getMachineState() {
        return machineState;
    }
    
    /**
     * Gets the machine's version.
     *
     * @return The machine version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the machine's creation time.
     *
     * @return The creation time
     */
    public Instant getCreationTime() {
        return creationTime;
    }
    
    /**
     * Gets the machine's logger.
     *
     * @return The logger
     */
    public Logger getLogger() {
        return logger;
    }
    
    /**
     * Gets the environment in which this machine operates.
     *
     * @return The environment
     */
    public Environment getEnvironment() {
        return environment;
    }
    
    /** Event logged within a machine. */
    public static class MachineEvent {
        private final String description;
        private final long timestamp;
        
        public MachineEvent(String description) {
            this.description = description;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getDescription() {
            return description;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return timestamp + ": " + description;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(machineId, ((Machine) o).machineId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(machineId);
    }
    
    @Override
    public String toString() {
        return "Machine{id=" + machineId + 
               ", name='" + name + '\'' + 
               ", type=" + type +
               ", state=" + machineState +
               ", version='" + version + '\'' +
               ", compositeCount=" + composites.size() +
               '}';
    }
}