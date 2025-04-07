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

package org.s8r.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.s8r.component.Composite;
import org.s8r.component.Environment;
import org.s8r.component.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Highest level of composition in the S8r architecture, orchestrating multiple Composites.
 *
 * <p>Machine represents a complete processing system composed of interconnected Composites. It
 * provides a comprehensive management layer for complex workflows, state management, and
 * system-level operations.
 *
 * <p>Key features include:
 *
 * <ul>
 *   <li>Composite management and interconnection
 *   <li>State tracking and event logging
 *   <li>Lifecycle management (activate/deactivate/shutdown)
 *   <li>Identity-based hierarchical organization
 * </ul>
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
  private final Identity machineIdentity;

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
    state.put("createdAt", Instant.now());

    // Create an identity for this machine
    Map<String, String> envParams = new HashMap<>();
    envParams.put("machineId", machineId);
    this.machineIdentity = Identity.createAdamIdentity("Machine_" + machineId, envParams);

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
   * Gets the machine's identity.
   *
   * @return The machine identity
   */
  public Identity getMachineIdentity() {
    return machineIdentity;
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

  /**
   * Initiates the machine shutdown process.
   *
   * <p>This begins an orderly shutdown of all composites, ensuring a proper cleanup of resources
   * and final state preservation.
   */
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
      updateState("terminatedAt", Instant.now());
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

  /**
   * Gets the environment in which this machine operates.
   *
   * @return The machine's environment
   */
  public Environment getEnvironment() {
    return environment;
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
    private final Instant timestamp;

    public MachineEvent(String description) {
      this.description = description;
      this.timestamp = Instant.now();
    }

    public String getDescription() {
      return description;
    }

    public Instant getTimestamp() {
      return timestamp;
    }

    @Override
    public String toString() {
      return timestamp + ": " + description;
    }
  }
}
