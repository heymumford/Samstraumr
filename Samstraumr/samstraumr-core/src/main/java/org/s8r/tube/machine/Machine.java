/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.tube.machine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.s8r.tube.Environment;
import org.s8r.tube.composite.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Machine represents the highest level of composition in the Samstraumr architecture. It manages
 * and orchestrates multiple Composite components to form a complete processing system.
 */
public class Machine {
  private static final Logger LOGGER = LoggerFactory.getLogger(Machine.class);

  private final String machineId;
  private final Environment environment;
  private final Map<String, Composite> composites;
  private final Map<String, List<String>> connections;
  private final List<MachineEvent> eventLog;
  private final AtomicBoolean active;
  private final Map<String, Object> state;

  /**
   * Creates a new Machine with the specified identifier in the given environment.
   *
   * @param machineId The unique identifier for this machine
   * @param environment The environment in which this machine operates
   */
  public Machine(String machineId, Environment environment) {
    this.machineId = machineId;
    this.environment = environment;
    this.composites = new ConcurrentHashMap<>();
    this.connections = new ConcurrentHashMap<>();
    this.eventLog = Collections.synchronizedList(new ArrayList<>());
    this.active = new AtomicBoolean(true);
    this.state = new ConcurrentHashMap<>();

    // Initialize basic state
    state.put("status", "INITIALIZING");
    state.put("createdAt", System.currentTimeMillis());

    logEvent("Machine initialized: " + machineId);
    LOGGER.info("Machine {} initialized", machineId);
  }

  /**
   * Adds a composite to this machine.
   *
   * @param name The name to reference this composite by within the machine
   * @param composite The composite to add
   * @return This machine instance for method chaining
   */
  public Machine addComposite(String name, Composite composite) {
    if (composites.containsKey(name)) {
      LOGGER.warn("Replacing existing composite with name: {}", name);
    }

    composites.put(name, composite);
    logEvent("Composite added to machine: " + name);
    return this;
  }

  /**
   * Creates a new composite and adds it to this machine.
   *
   * @param name The name to reference this composite by within the machine
   * @param purpose The purpose for creating this composite
   * @return This machine instance for method chaining
   */
  public Machine createComposite(String name, String purpose) {
    Composite composite = new Composite(name, environment);
    return addComposite(name, composite);
  }

  /**
   * Connects two composites in the machine to allow data flow.
   *
   * @param sourceName The name of the source composite
   * @param targetName The name of the target composite
   * @return This machine instance for method chaining
   * @throws IllegalArgumentException if either composite name doesn't exist in the machine
   */
  public Machine connect(String sourceName, String targetName) {
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
   * @return The requested composite
   * @throws IllegalArgumentException if the composite doesn't exist
   */
  public Composite getComposite(String name) {
    validateCompositeExists(name);
    return composites.get(name);
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
   * Checks if the machine is currently active.
   *
   * @return true if the machine is active, false otherwise
   */
  public boolean isActive() {
    return active.get();
  }

  /**
   * Activates the machine to allow data processing.
   *
   * @return This machine instance for method chaining
   */
  public Machine activate() {
    if (active.compareAndSet(false, true)) {
      updateState("status", "ACTIVE");
      logEvent("Machine activated: " + machineId);
      LOGGER.info("Machine {} activated", machineId);
    }
    return this;
  }

  /**
   * Deactivates the machine, preventing further data processing.
   *
   * @return This machine instance for method chaining
   */
  public Machine deactivate() {
    if (active.compareAndSet(true, false)) {
      updateState("status", "DEACTIVATED");
      logEvent("Machine deactivated: " + machineId);
      LOGGER.info("Machine {} deactivated", machineId);
    }
    return this;
  }

  /** Initiates the machine shutdown process. This begins an orderly shutdown of all composites. */
  public void shutdown() {
    if (active.compareAndSet(true, false)) {
      updateState("status", "SHUTTING_DOWN");
      logEvent("Machine shutdown initiated: " + machineId);
      LOGGER.info("Machine {} shutdown initiated", machineId);

      // Deactivate all composites
      for (Map.Entry<String, Composite> entry : composites.entrySet()) {
        entry.getValue().deactivate();
        logEvent("Composite deactivated during shutdown: " + entry.getKey());
      }

      updateState("status", "TERMINATED");
      logEvent("Machine shutdown completed: " + machineId);
      LOGGER.info("Machine {} shutdown completed", machineId);
    }
  }

  /**
   * Gets the machine's unique identifier.
   *
   * @return The machine ID
   */
  public String getMachineId() {
    return machineId;
  }

  // Private helper methods

  private void validateCompositeExists(String compositeName) {
    if (!composites.containsKey(compositeName)) {
      throw new IllegalArgumentException("Composite not found in machine: " + compositeName);
    }
  }

  /**
   * Logs an event in the machine's event log.
   *
   * @param description The description of the event
   */
  public void logEvent(String description) {
    MachineEvent event = new MachineEvent(description);
    eventLog.add(event);
    LOGGER.debug("Machine event: {}", description);
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
}
