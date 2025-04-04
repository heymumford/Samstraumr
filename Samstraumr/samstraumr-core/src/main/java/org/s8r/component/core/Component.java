/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Core implementation of the Component concept in the S8r framework
 */
package org.s8r.component.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.component.identity.Identity;
import org.s8r.component.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Component concept in the S8r framework.
 *
 * <p>This class provides the essential infrastructure for components to maintain their hierarchical
 * design, identity, and data processing capabilities. It implements a biological-inspired lifecycle
 * model with clear state transitions and parent-child relationships.
 */
public class Component {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Component.class);
  private static final int DEFAULT_TERMINATION_DELAY = 60; // seconds
  private static final String DIGEST_ALGORITHM = "SHA-256";

  private final String uniqueId;
  private final String reason;
  private final List<String> lineage;
  private final List<String> memoryLog;
  private final Environment environment;
  private final Identity identity;
  private final Instant conceptionTime;
  private final Logger logger;
  private final Map<String, Object> properties;

  private volatile Timer terminationTimer;
  private String environmentState = "normal";
  private State state;
  private Identity parentIdentity;

  /**
   * Private constructor for creating a component instance.
   *
   * @param reason The reason for creating this component
   * @param environment The environment in which this component exists
   * @param uniqueId The unique identifier for this component
   * @param parentIdentity The parent identity, or null for an Adam component
   */
  private Component(
      String reason, Environment environment, String uniqueId, Identity parentIdentity) {
    this.reason = reason;
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.parentIdentity = parentIdentity;
    this.lineage = Collections.synchronizedList(new ArrayList<>());
    this.lineage.add(reason);
    this.memoryLog = Collections.synchronizedList(new LinkedList<>());
    this.properties = new ConcurrentHashMap<>();
    this.state = State.CONCEPTION;

    // Environment validation
    if (environment == null) {
      throw new IllegalArgumentException("Environment cannot be null");
    }
    this.environment = environment;

    // Initialize logger
    this.logger = new Logger(uniqueId);

    // Log creation
    logToMemory("Component created with reason: " + reason);
    logToMemory("Environment: " + environment.getEnvironmentId());
    logger.info("Component created with reason: " + reason, "CREATION");

    // Create identity
    if (parentIdentity != null) {
      Map<String, String> envParams = convertEnvironmentParams(environment);
      this.identity = Identity.createChildIdentity(reason, envParams, parentIdentity);
      logToMemory("Created with parent identity: " + parentIdentity.getUniqueId());
      logger.info(
          "Created with parent identity: " + parentIdentity.getUniqueId(), "IDENTITY", "CHILD");
    } else {
      Map<String, String> envParams = convertEnvironmentParams(environment);
      this.identity = Identity.createAdamIdentity(reason, envParams);
      logToMemory("Created as Adam component (no parent)");
      logger.info("Created as Adam component (no parent)", "IDENTITY", "ADAM");
    }

    // Initialize through lifecycle phases
    initialize();
  }

  /**
   * Converts environment parameters to a map of strings.
   *
   * @param environment The environment instance
   * @return A map of string parameters
   */
  private Map<String, String> convertEnvironmentParams(Environment environment) {
    Map<String, String> result = new ConcurrentHashMap<>();
    for (String key : environment.getParameterKeys()) {
      result.put(key, environment.getParameter(key));
    }
    return result;
  }

  /**
   * Creates a new component with the specified reason and environment.
   *
   * @param reason The reason for creating this component
   * @param environment The environment in which to create the component
   * @return A new component instance
   * @throws IllegalArgumentException if parameters are invalid
   */
  public static Component create(String reason, Environment environment) {
    if (reason == null) {
      throw new IllegalArgumentException("Reason cannot be null");
    }
    if (environment == null) {
      throw new IllegalArgumentException("Environment cannot be null");
    }

    String uniqueId = generateUniqueId(reason, environment);
    Component component = new Component(reason, environment, uniqueId, null);

    return component;
  }

  /**
   * Creates a child component with a parent reference.
   *
   * @param reason The reason for creating this child component
   * @param environment The environment in which to create the component
   * @param parent The parent component
   * @return A new child component instance
   * @throws IllegalArgumentException if parameters are invalid
   */
  public static Component createChild(String reason, Environment environment, Component parent) {
    if (reason == null) {
      throw new IllegalArgumentException("Reason cannot be null");
    }
    if (environment == null) {
      throw new IllegalArgumentException("Environment cannot be null");
    }
    if (parent == null) {
      throw new IllegalArgumentException("Parent component cannot be null");
    }

    String uniqueId = generateUniqueId(reason, environment);
    Component child = new Component(reason, environment, uniqueId, parent.getIdentity());

    // Add to parent's lineage if applicable
    for (String entry : parent.getLineage()) {
      child.addToLineage(entry);
    }

    // Register child with parent
    parent.registerChild(child);

    return child;
  }

  /** Performs component initialization through lifecycle phases. */
  private void initialize() {
    logger.debug("Initializing component...", "LIFECYCLE", "INIT");

    // Move through initial states
    transitionToState(State.INITIALIZING);

    // Set up termination timer
    setupTerminationTimer(DEFAULT_TERMINATION_DELAY);

    // Progress through early lifecycle phases
    proceedThroughEarlyLifecycle();

    // Transition to ready state
    transitionToState(State.READY);

    logToMemory("Component initialized and ready");
    logger.info("Component initialized and ready", "LIFECYCLE", "READY");
  }

  /**
   * Proceeds through the early lifecycle phases of component development. This follows the
   * biological metaphor from conception through early development.
   */
  public void proceedThroughEarlyLifecycle() {
    logToMemory("Beginning early lifecycle development");
    logger.info("Beginning early lifecycle development", "LIFECYCLE", "DEVELOPMENT");

    // CONFIGURING phase - establishing boundaries
    transitionToState(State.CONFIGURING);
    logToMemory("Component entering CONFIGURING phase (analog: Blastulation)");
    // In a full implementation, this would set up internal boundaries and structures

    // SPECIALIZING phase - determining core functions
    transitionToState(State.SPECIALIZING);
    logToMemory("Component entering SPECIALIZING phase (analog: Gastrulation)");
    // In a full implementation, this would determine core functionality

    // DEVELOPING_FEATURES phase - building specific capabilities
    transitionToState(State.DEVELOPING_FEATURES);
    logToMemory("Component entering DEVELOPING_FEATURES phase (analog: Organogenesis)");
    // In a full implementation, this would build specific capabilities

    logToMemory("Completed early lifecycle development");
    logger.info("Completed early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
  }

  /**
   * Sets up a timer for component termination.
   *
   * @param delaySeconds The delay in seconds before termination
   */
  public void setupTerminationTimer(int delaySeconds) {
    if (delaySeconds <= 0) {
      throw new IllegalArgumentException("Termination delay must be positive");
    }

    if (isTerminated()) {
      throw new IllegalStateException(
          "Cannot set termination delay: component is already terminated");
    }

    synchronized (this) {
      if (terminationTimer != null) {
        terminationTimer.cancel();
      }

      terminationTimer = new Timer("ComponentTerminator-" + uniqueId);
      terminationTimer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              terminate();
            }
          },
          delaySeconds * 1000L);
    }

    logToMemory("Termination timer set for " + delaySeconds + " seconds");
    logger.debug("Termination timer set for " + delaySeconds + " seconds", "LIFECYCLE", "TIMER");
  }

  /**
   * Sets the termination delay for this component.
   *
   * @param seconds The delay in seconds
   */
  public void setTerminationDelay(int seconds) {
    setupTerminationTimer(seconds);
  }

  /**
   * Transitions the component to a new state.
   *
   * @param newState The new state
   */
  private void transitionToState(State newState) {
    if (newState != this.state) {
      State oldState = this.state;
      this.state = newState;
      logToMemory("State changed: " + oldState + " -> " + newState);
      logger.info(
          "State changed: " + oldState.name() + " -> " + newState.name(),
          "LIFECYCLE",
          newState.name());
    }
  }

  /**
   * Gets the current state of this component.
   *
   * @return The current state
   */
  public State getState() {
    return state;
  }

  /**
   * Sets the component state to a new value.
   *
   * @param newState The new state
   * @throws IllegalStateException if the component is terminated
   */
  public void setState(State newState) {
    if (isTerminated()) {
      throw new IllegalStateException("Cannot change state of terminated component");
    }

    transitionToState(newState);
  }

  /**
   * Checks if this component is in a terminated state.
   *
   * @return true if terminated, false otherwise
   */
  public boolean isTerminated() {
    return state == State.TERMINATED || state == State.TERMINATING || state == State.ARCHIVED;
  }

  /** Terminates this component, releasing resources. */
  public void terminate() {
    if (isTerminated()) {
      return;
    }

    logToMemory("Component termination initiated");
    logger.info("Component termination initiated", "LIFECYCLE", "TERMINATE");

    // Cancel timer if active
    synchronized (this) {
      if (terminationTimer != null) {
        terminationTimer.cancel();
        terminationTimer = null;
        logger.debug("Termination timer canceled", "LIFECYCLE", "TERMINATE");
      }
    }

    // Transition through termination states
    transitionToState(State.TERMINATING);

    // Perform cleanup operations
    preserveKnowledge();
    releaseResources();

    // Final state transition
    transitionToState(State.TERMINATED);

    logToMemory("Component terminated");
    logger.info("Component terminated", "LIFECYCLE", "TERMINATE");
  }

  /** Preserves crucial knowledge before termination for potential future use. */
  private void preserveKnowledge() {
    logToMemory("Preserving knowledge before termination");
    logger.debug("Preserving knowledge before termination", "LIFECYCLE", "TERMINATE");
    // In a full implementation, this would archive important learnings and experiences
  }

  /** Releases all resources allocated to this component. */
  private void releaseResources() {
    logToMemory("Releasing allocated resources");
    logger.debug("Releasing allocated resources", "LIFECYCLE", "TERMINATE");
    // In a full implementation, this would clean up all allocated resources
  }

  /**
   * Registers a child component with this component.
   *
   * @param childComponent The child component to register
   */
  public void registerChild(Component childComponent) {
    if (childComponent != null) {
      logToMemory("Registering child component: " + childComponent.getUniqueId());
      logger.info(
          "Registering child component: " + childComponent.getUniqueId(), "HIERARCHY", "CHILD");

      // Add the child to our identity's descendants list
      if (this.identity != null && childComponent.getIdentity() != null) {
        this.identity.addChild(childComponent.getIdentity());
        logToMemory("Added child component identity to descendants list");
        logger.debug("Added child identity to descendants list", "HIERARCHY", "IDENTITY");
      }
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
   * Gets the unique identifier for this component.
   *
   * @return The unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the reason for creating this component.
   *
   * @return The creation reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the identity of this component.
   *
   * @return The component identity
   */
  public Identity getIdentity() {
    return identity;
  }

  /**
   * Gets the environment in which this component is operating.
   *
   * @return The environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Gets the lineage information for this component.
   *
   * @return An unmodifiable view of the lineage
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Adds an entry to the component's lineage.
   *
   * @param entry The lineage entry to add
   */
  public void addToLineage(String entry) {
    if (entry != null && !entry.isEmpty()) {
      lineage.add(entry);
      logToMemory("Added to lineage: " + entry);
    }
  }

  /**
   * Gets the memory log entries for this component.
   *
   * @return An unmodifiable view of the memory log
   */
  public List<String> getMemoryLog() {
    return Collections.unmodifiableList(memoryLog);
  }

  /**
   * Gets the size of the memory log.
   *
   * @return The memory log size
   */
  public int getMemoryLogSize() {
    return memoryLog.size();
  }

  /**
   * Gets the current environment state.
   *
   * @return The current environment state
   */
  public String getEnvironmentState() {
    return environmentState;
  }

  /**
   * Updates the component's awareness of environmental state changes.
   *
   * @param newState The new state of the environment
   */
  public void updateEnvironmentState(String newState) {
    if (newState != null && !newState.equals(environmentState)) {
      String oldState = environmentState;
      environmentState = newState;
      logToMemory("Environment state changed: " + oldState + " -> " + newState);
      logger.info("Environment state changed: " + oldState + " -> " + newState, "ENVIRONMENT");
    }
  }

  /**
   * Gets the conception time (creation timestamp) for this component.
   *
   * @return The conception time
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the parent identity of this component, if any.
   *
   * @return The parent component's identity, or null for Adam components
   */
  public Identity getParentIdentity() {
    return parentIdentity;
  }

  /**
   * Sets a property value on this component.
   *
   * @param key The property key
   * @param value The property value
   */
  public void setProperty(String key, Object value) {
    if (key != null) {
      properties.put(key, value);
      logger.debug("Property set: " + key, "PROPERTY");
    }
  }

  /**
   * Gets a property value from this component.
   *
   * @param key The property key
   * @return The property value, or null if not found
   */
  public Object getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Gets all property keys for this component.
   *
   * @return The set of property keys
   */
  public Set<String> getPropertyKeys() {
    return properties.keySet();
  }

  /**
   * Generates a unique identifier based on the reason and environment.
   *
   * @param reason The reason for creating the component
   * @param environment The environment in which to create the component
   * @return A unique identifier
   */
  private static String generateUniqueId(String reason, Environment environment) {
    try {
      MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
      String input = reason + "-" + environment.getEnvironmentId() + "-" + Instant.now();
      byte[] hash = digest.digest(input.getBytes());

      // Convert byte array to hex string
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Failed to generate unique ID", e);
      return "fallback-" + System.nanoTime();
    }
  }

  @Override
  public String toString() {
    return "Component[" + "id=" + uniqueId + ", reason='" + reason + "', state=" + state + "]";
  }
}
