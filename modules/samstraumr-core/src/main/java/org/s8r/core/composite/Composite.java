/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.core.composite;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.core.env.Environment;
import org.s8r.core.exception.CompositeException;
import org.s8r.core.exception.InitializationException;
import org.s8r.core.tube.LifecycleState;
import org.s8r.core.tube.Status;
import org.s8r.core.tube.identity.Identity;
import org.s8r.core.tube.impl.Component;
import org.s8r.core.tube.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Composite is a collection of components that work together to achieve a common purpose.
 *
 * <p>Composites provide structure and organization to groups of related components, similar to how
 * tissues organize cells in biological systems. They manage component relationships, coordinate
 * state changes, and present a unified interface to the outside world.
 *
 * <p>This implementation follows the Component model but adds capabilities for managing multiple
 * sub-components with unified lifecycle and state handling.
 */
public class Composite {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Composite.class);

  private final String uniqueId;
  private final String name;
  private final CompositeType type;
  private final List<Component> components;
  private final Map<String, Component> componentsById;
  private final Environment environment;
  private final Logger logger;
  private final Identity identity;
  private final Instant creationTime;
  private LifecycleState lifecycleState;
  private Status status;
  private final List<String> memoryLog;
  private final Map<String, Object> sharedContext;

  /**
   * Creates a new Composite instance.
   *
   * @param name The name of the composite
   * @param type The type of the composite
   * @param environment The environment in which the composite operates
   * @throws CompositeException if initialization fails
   */
  Composite(String name, CompositeType type, Environment environment) {
    this.name = name;
    this.type = type;
    this.environment = environment;
    this.uniqueId = generateUniqueId();
    this.creationTime = Instant.now();
    this.components = new CopyOnWriteArrayList<>();
    this.componentsById = new ConcurrentHashMap<>();
    this.memoryLog = Collections.synchronizedList(new ArrayList<>());
    this.sharedContext = new ConcurrentHashMap<>();

    // Initialize logger
    this.logger = new Logger("Composite-" + uniqueId);

    try {
      // Create identity
      this.identity = Identity.createAdamIdentity("Composite: " + name, environment);
      logToMemory("Created composite identity: " + identity.getUniqueId());

      // Initialize state
      this.lifecycleState = LifecycleState.CONCEPTION;
      this.status = Status.INITIALIZING;

      // Advanced initialization
      initialize();

      // Set initial state
      proceedThroughEarlyLifecycle();
      this.lifecycleState = LifecycleState.READY;
      this.status = Status.READY;

      logger.info("Composite created and initialized: " + name, "COMPOSITE", "CREATION");
    } catch (Exception e) {
      logger.error("Failed to initialize composite: " + e.getMessage(), "COMPOSITE", "ERROR");
      throw new CompositeException("Failed to initialize composite: " + e.getMessage(), e);
    }
  }

  /** Performs additional initialization for the composite. */
  private void initialize() {
    logToMemory("Initializing composite: " + name);
    logger.info("Initializing composite: " + name, "COMPOSITE", "INIT");

    // Initialize shared context with basic information
    sharedContext.put("compositeName", name);
    sharedContext.put("compositeId", uniqueId);
    sharedContext.put("compositeType", type.name());
    sharedContext.put("creationTime", creationTime);

    logToMemory("Composite initialization complete");
  }

  /**
   * Adds a component to this composite.
   *
   * @param component The component to add
   * @return This composite instance for method chaining
   * @throws CompositeException if the component cannot be added
   */
  public Composite addComponent(Component component) {
    if (component == null) {
      throw new CompositeException("Cannot add null component to composite");
    }

    String componentId = component.getUniqueId();
    if (componentsById.containsKey(componentId)) {
      throw new CompositeException(
          "Component with ID " + componentId + " already exists in this composite");
    }

    components.add(component);
    componentsById.put(componentId, component);
    logToMemory("Added component: " + component.getUniqueId() + " - " + component.getReason());
    logger.info("Added component: " + component.getUniqueId(), "COMPOSITE", "ADD_COMPONENT");

    // Link the component with this composite
    // No need to register as child - that would be handled in a full implementation

    return this;
  }

  /**
   * Removes a component from this composite.
   *
   * @param componentId The ID of the component to remove
   * @return true if the component was removed, false if it wasn't found
   */
  public boolean removeComponent(String componentId) {
    Component component = componentsById.get(componentId);
    if (component != null) {
      componentsById.remove(componentId);
      components.remove(component);
      logToMemory("Removed component: " + componentId);
      logger.info("Removed component: " + componentId, "COMPOSITE", "REMOVE_COMPONENT");
      return true;
    }
    return false;
  }

  /**
   * Connects one component to another within this composite.
   *
   * @param sourceId The ID of the source component
   * @param targetId The ID of the target component
   * @param connectionType The type of connection
   * @throws CompositeException if the connection cannot be established
   */
  public void connectComponents(String sourceId, String targetId, String connectionType) {
    Component source = componentsById.get(sourceId);
    Component target = componentsById.get(targetId);

    if (source == null) {
      throw new CompositeException("Source component not found: " + sourceId);
    }
    if (target == null) {
      throw new CompositeException("Target component not found: " + targetId);
    }

    // In a full implementation, this would establish an actual connection mechanism
    // For now, we just log the connection
    logToMemory(
        "Connected component " + sourceId + " to " + targetId + " (" + connectionType + ")");
    logger.info("Connected components: " + sourceId + " → " + targetId, "COMPOSITE", "CONNECT");

    // Store connection in shared context
    Map<String, Object> connectionInfo = new HashMap<>();
    connectionInfo.put("sourceId", sourceId);
    connectionInfo.put("targetId", targetId);
    connectionInfo.put("type", connectionType);
    connectionInfo.put("established", Instant.now());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> connections =
        (List<Map<String, Object>>)
            sharedContext.computeIfAbsent("connections", k -> new ArrayList<Map<String, Object>>());
    connections.add(connectionInfo);
  }

  /**
   * Activates all components in this composite.
   *
   * @return This composite instance for method chaining
   */
  public Composite activate() {
    if (lifecycleState != LifecycleState.READY && lifecycleState != LifecycleState.WAITING) {
      throw new CompositeException("Cannot activate composite in state: " + lifecycleState);
    }

    logToMemory("Activating composite: " + name);
    logger.info("Activating composite: " + name, "COMPOSITE", "ACTIVATE");

    // Activate all components
    for (Component component : components) {
      if (component.getLifecycleState() == LifecycleState.READY
          || component.getLifecycleState() == LifecycleState.WAITING) {
        component.setLifecycleState(LifecycleState.ACTIVE);
        component.setStatus(Status.OPERATIONAL);
      }
    }

    // Update composite state
    this.lifecycleState = LifecycleState.ACTIVE;
    this.status = Status.OPERATIONAL;

    logToMemory("Composite activated: " + name);
    logger.info("Composite activated: " + name, "COMPOSITE", "ACTIVE");

    return this;
  }

  /**
   * Sets the composite to a waiting state.
   *
   * @return This composite instance for method chaining
   */
  public Composite setWaiting() {
    logToMemory("Setting composite to waiting state: " + name);
    logger.info("Setting composite to waiting state: " + name, "COMPOSITE", "WAITING");

    // Set components to waiting state
    for (Component component : components) {
      if (component.getLifecycleState() == LifecycleState.ACTIVE) {
        component.setLifecycleState(LifecycleState.WAITING);
      }
    }

    // Update composite state
    this.lifecycleState = LifecycleState.WAITING;

    return this;
  }

  /** Terminates this composite and all its components. */
  public void terminate() {
    logToMemory("Terminating composite: " + name);
    logger.info("Terminating composite: " + name, "COMPOSITE", "TERMINATE");

    // Terminate all components
    for (Component component : components) {
      component.terminate();
    }

    // Update composite state
    this.lifecycleState = LifecycleState.TERMINATED;
    this.status = Status.TERMINATED;

    logToMemory("Composite terminated: " + name);
    logger.info("Composite terminated: " + name, "COMPOSITE", "TERMINATED");
  }

  /** Proceeds through the early lifecycle phases. */
  private void proceedThroughEarlyLifecycle() {
    logToMemory("Beginning early lifecycle development for composite");
    logger.info("Beginning early lifecycle development", "LIFECYCLE", "DEVELOPMENT");

    // CONFIGURING phase - establishing boundaries
    this.lifecycleState = LifecycleState.CONFIGURING;
    logToMemory("Composite entering CONFIGURING phase");
    logger.info("Composite entering CONFIGURING phase", "LIFECYCLE", "CONFIGURING");

    // SPECIALIZING phase - determining core functions
    this.lifecycleState = LifecycleState.SPECIALIZING;
    logToMemory("Composite entering SPECIALIZING phase");
    logger.info("Composite entering SPECIALIZING phase", "LIFECYCLE", "SPECIALIZING");

    // DEVELOPING_FEATURES phase - building specific capabilities
    this.lifecycleState = LifecycleState.DEVELOPING_FEATURES;
    logToMemory("Composite entering DEVELOPING_FEATURES phase");
    logger.info("Composite entering DEVELOPING_FEATURES phase", "LIFECYCLE", "DEVELOPING");

    logToMemory("Completed early lifecycle development for composite");
    logger.info("Completed early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
  }

  /** Updates the composite's state based on its components' states. */
  public void updateState() {
    logToMemory("Updating composite state based on components");

    int readyCount = 0;
    int activeCount = 0;
    int degradedCount = 0;
    int terminatedCount = 0;

    for (Component component : components) {
      LifecycleState state = component.getLifecycleState();

      if (state == LifecycleState.READY) {
        readyCount++;
      } else if (state == LifecycleState.ACTIVE) {
        activeCount++;
      } else if (state == LifecycleState.DEGRADED) {
        degradedCount++;
      } else if (state == LifecycleState.TERMINATED) {
        terminatedCount++;
      }
    }

    // Determine the composite's state based on component states
    LifecycleState previousState = this.lifecycleState;

    if (components.isEmpty()) {
      // Empty composite stays in its current state
      return;
    }

    if (terminatedCount == components.size()) {
      this.lifecycleState = LifecycleState.TERMINATED;
      this.status = Status.TERMINATED;
    } else if (degradedCount > 0) {
      this.lifecycleState = LifecycleState.DEGRADED;
      this.status = Status.DEGRADED;
    } else if (activeCount > 0) {
      this.lifecycleState = LifecycleState.ACTIVE;
      this.status = Status.OPERATIONAL;
    } else if (readyCount > 0) {
      this.lifecycleState = LifecycleState.READY;
      this.status = Status.READY;
    }

    if (previousState != this.lifecycleState) {
      logToMemory("Composite state changed: " + previousState + " -> " + this.lifecycleState);
      logger.info(
          "Composite state changed: " + previousState + " -> " + this.lifecycleState,
          "COMPOSITE",
          "STATE_CHANGE");
    }
  }

  /**
   * Logs a message to the memory log.
   *
   * @param message The message to log
   */
  private void logToMemory(String message) {
    String logEntry = Instant.now() + " - " + message;
    memoryLog.add(logEntry);
    LOGGER.debug(logEntry);
  }

  /**
   * Generates a unique ID for this composite.
   *
   * @return A unique ID
   */
  private String generateUniqueId() {
    return "COMP-" + UUID.randomUUID().toString();
  }

  /**
   * Gets the unique ID of this composite.
   *
   * @return The unique ID
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the name of this composite.
   *
   * @return The name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the type of this composite.
   *
   * @return The composite type
   */
  public CompositeType getType() {
    return type;
  }

  /**
   * Gets the environment in which this composite operates.
   *
   * @return The environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Gets the components in this composite.
   *
   * @return An unmodifiable list of components
   */
  public List<Component> getComponents() {
    return Collections.unmodifiableList(components);
  }

  /**
   * Gets a component by its ID.
   *
   * @param componentId The component ID
   * @return The component, or null if not found
   */
  public Component getComponent(String componentId) {
    return componentsById.get(componentId);
  }

  /**
   * Gets the logger for this composite.
   *
   * @return The logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Gets the identity of this composite.
   *
   * @return The identity
   */
  public Identity getIdentity() {
    return identity;
  }

  /**
   * Gets the memory log of this composite.
   *
   * @return An unmodifiable list of memory log entries
   */
  public List<String> getMemoryLog() {
    return Collections.unmodifiableList(memoryLog);
  }

  /**
   * Gets the lifecycle state of this composite.
   *
   * @return The lifecycle state
   */
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * Gets the status of this composite.
   *
   * @return The status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Gets the creation time of this composite.
   *
   * @return The creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Gets a value from the shared context.
   *
   * @param key The key
   * @return The value, or null if not found
   */
  public Object getSharedContextValue(String key) {
    return sharedContext.get(key);
  }

  /**
   * Sets a value in the shared context.
   *
   * @param key The key
   * @param value The value
   */
  public void setSharedContextValue(String key, Object value) {
    sharedContext.put(key, value);
  }

  /**
   * Checks if the component is in an operational state.
   *
   * @return true if the component is operational, false otherwise
   */
  public boolean isOperational() {
    return this.lifecycleState == LifecycleState.ACTIVE
        || this.lifecycleState == LifecycleState.READY
        || this.lifecycleState == LifecycleState.STABLE;
  }

  /**
   * An internal adapter class to expose the composite as a component for registration.
   *
   * <p>Note: This functionality is simplified in this implementation. In a full implementation,
   * there would be more sophisticated component integration mechanics.
   */
  private static class ComponentAdapter {
    private static Component createComponentAdapter(Composite composite) {
      try {
        return Component.create(
            "Composite Adapter: " + composite.getName(), composite.getEnvironment());
      } catch (InitializationException e) {
        throw new CompositeException("Failed to create component adapter", e);
      }
    }
  }
}
