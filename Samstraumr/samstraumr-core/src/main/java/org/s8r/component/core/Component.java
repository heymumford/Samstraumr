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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.component.identity.Identity;
import org.s8r.component.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Component concept in the S8r framework.
 *
 * <p>Implements a biological-inspired lifecycle model with state transitions
 * and parent-child relationships.
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
  private final Map<String, Object> properties = new ConcurrentHashMap<>();

  private volatile Timer terminationTimer;
  private String environmentState = "normal";
  private State state = State.CONCEPTION;
  private Identity parentIdentity;

  /**
   * Private constructor for creating a component instance.
   */
  private Component(String reason, Environment environment, String uniqueId, Identity parentIdentity) {
    this.reason = reason;
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.parentIdentity = parentIdentity;
    this.lineage = Collections.synchronizedList(new ArrayList<>());
    this.lineage.add(reason);
    this.memoryLog = Collections.synchronizedList(new LinkedList<>());
    if (environment == null) {
        throw new IllegalArgumentException("Environment cannot be null");
    }
    this.environment = environment;
    this.logger = new Logger(uniqueId);

    // Log creation
    logToMemory("Component created with reason: " + reason);
    logToMemory("Environment: " + environment.getEnvironmentId());
    logger.info("Component created with reason: " + reason, "CREATION");

    // Create identity
    Map<String, String> envParams = convertEnvironmentParams(environment);
    if (parentIdentity != null) {
      this.identity = Identity.createChildIdentity(reason, envParams, parentIdentity);
      logToMemory("Created with parent identity: " + parentIdentity.getUniqueId());
      logger.info("Created with parent identity: " + parentIdentity.getUniqueId(), "IDENTITY", "CHILD");
    } else {
      this.identity = Identity.createAdamIdentity(reason, envParams);
      logToMemory("Created as Adam component (no parent)");
      logger.info("Created as Adam component (no parent)", "IDENTITY", "ADAM");
    }

    // Initialize through lifecycle phases
    initialize();
  }

  /**
   * Converts environment parameters to a map of strings.
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
   */
  public static Component create(String reason, Environment environment) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environment == null) throw new IllegalArgumentException("Environment cannot be null");

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }

  /**
   * Creates a new component with environment parameter map (backward compatibility).
   */
  public static Component create(String reason, Map<String, String> environmentParams) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environmentParams == null) throw new IllegalArgumentException("Environment parameters cannot be null");

    Environment env = new Environment();
    for (Map.Entry<String, String> entry : environmentParams.entrySet()) {
      env.setParameter(entry.getKey(), entry.getValue());
    }

    return create(reason, env);
  }

  /**
   * Creates a child component with a parent reference.
   */
  public static Component createChild(String reason, Environment environment, Component parent) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environment == null) throw new IllegalArgumentException("Environment cannot be null");
    if (parent == null) throw new IllegalArgumentException("Parent component cannot be null");

    String uniqueId = generateUniqueId(reason, environment);
    Component child = new Component(reason, environment, uniqueId, parent.getIdentity());

    // Add to parent's lineage
    for (String entry : parent.getLineage()) {
      child.addToLineage(entry);
    }

    // Register child with parent
    parent.registerChild(child);

    return child;
  }

  /**
   * Creates a child component with environment parameter map (backward compatibility).
   */
  public static Component createChild(String reason, Map<String, String> environmentParams, Component parent) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environmentParams == null) throw new IllegalArgumentException("Environment parameters cannot be null");
    if (parent == null) throw new IllegalArgumentException("Parent component cannot be null");

    Environment env = new Environment();
    for (Map.Entry<String, String> entry : environmentParams.entrySet()) {
      env.setParameter(entry.getKey(), entry.getValue());
    }

    return createChild(reason, env, parent);
  }

  /** Performs component initialization through lifecycle phases. */
  private void initialize() {
    logger.debug("Initializing component...", "LIFECYCLE", "INIT");

    // Move through initial states
    transitionToState(State.INITIALIZING);
    setupTerminationTimer(DEFAULT_TERMINATION_DELAY);
    proceedThroughEarlyLifecycle();
    transitionToState(State.READY);

    logToMemory("Component initialized and ready");
    logger.info("Component initialized and ready", "LIFECYCLE", "READY");
  }

  /**
   * Proceeds through the early lifecycle phases of component development.
   */
  public void proceedThroughEarlyLifecycle() {
    logToMemory("Beginning early lifecycle development");
    logger.info("Beginning early lifecycle development", "LIFECYCLE", "DEVELOPMENT");

    // Move through development phases
    transitionToState(State.CONFIGURING);
    logToMemory("Component entering CONFIGURING phase (analog: Blastulation)");

    transitionToState(State.SPECIALIZING);
    logToMemory("Component entering SPECIALIZING phase (analog: Gastrulation)");

    transitionToState(State.DEVELOPING_FEATURES);
    logToMemory("Component entering DEVELOPING_FEATURES phase (analog: Organogenesis)");

    logToMemory("Completed early lifecycle development");
    logger.info("Completed early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
  }

  /**
   * Sets up a timer for component termination.
   */
  public void setupTerminationTimer(int delaySeconds) {
    if (delaySeconds <= 0) {
      throw new IllegalArgumentException("Termination delay must be positive");
    }

    if (isTerminated()) {
      throw new IllegalStateException("Cannot set termination delay: component is already terminated");
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
   */
  public void setTerminationDelay(int seconds) {
    setupTerminationTimer(seconds);
  }

  /**
   * Transitions the component to a new state.
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

  /** Gets the current state of this component. */
  public State getState() {
    return state;
  }

  /** Sets the component state to a new value. */
  public void setState(State newState) {
    if (isTerminated()) {
      throw new IllegalStateException("Cannot change state of terminated component");
    }
    transitionToState(newState);
  }

  /** Checks if this component is in a terminated state. */
  public boolean isTerminated() {
    return state == State.TERMINATED || state == State.TERMINATING || state == State.ARCHIVED;
  }

  /** Checks if this component is operational. */
  public boolean isOperational() {
    return state == State.READY || state == State.ACTIVE || state == State.STABLE;
  }

  /** Checks if this component is embryonic. */
  public boolean isEmbryonic() {
    return state == State.CONCEPTION || state == State.INITIALIZING || state == State.CONFIGURING;
  }

  /** Checks if this component is initializing. */
  public boolean isInitializing() {
    return state == State.INITIALIZING;
  }

  /** Checks if this component is configuring. */
  public boolean isConfiguring() {
    return state == State.CONFIGURING;
  }

  /** Checks if this component is ready. */
  public boolean isReady() {
    return state == State.READY;
  }

  /** Checks if this component is active. */
  public boolean isActive() {
    return state == State.ACTIVE;
  }

  /** Checks if this component is in error recovery. */
  public boolean isInErrorRecovery() {
    return state == State.RECOVERING;
  }

  /** Checks if this component has attempted recovery. */
  public boolean hasAttemptedRecovery() {
    return getProperty("recoveryAttempts") != null;
  }

  /** Gets the number of recovery attempts made by this component. */
  public int getRecoveryAttempts() {
    Object attempts = getProperty("recoveryAttempts");
    return attempts instanceof Integer ? (Integer) attempts : 0;
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
    preserveKnowledge();
    releaseResources();
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

  /** Registers a child component with this component. */
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

  /** Logs a message to the memory log. */
  private void logToMemory(String message) {
    String logEntry = Instant.now() + " - " + message;
    memoryLog.add(logEntry);
    LOGGER.debug(logEntry);
  }

  // Getter methods
  public String getUniqueId() { return uniqueId; }
  public String getReason() { return reason; }
  public Identity getIdentity() { return identity; }
  public Environment getEnvironment() { return environment; }
  public List<String> getLineage() { return Collections.unmodifiableList(lineage); }
  public List<String> getMemoryLog() { return Collections.unmodifiableList(memoryLog); }
  public int getMemoryLogSize() { return memoryLog.size(); }
  public String getEnvironmentState() { return environmentState; }
  public Instant getConceptionTime() { return conceptionTime; }
  public Identity getParentIdentity() { return parentIdentity; }
  public Logger getLogger() { return logger; }

  /** Adds an entry to the component's lineage. */
  public void addToLineage(String entry) {
    if (entry != null && !entry.isEmpty()) {
      lineage.add(entry);
      logToMemory("Added to lineage: " + entry);
    }
  }

  /** Updates the component's awareness of environmental state changes. */
  public void updateEnvironmentState(String newState) {
    if (newState != null && !newState.equals(environmentState)) {
      String oldState = environmentState;
      environmentState = newState;
      logToMemory("Environment state changed: " + oldState + " -> " + newState);
      logger.info("Environment state changed: " + oldState + " -> " + newState, "ENVIRONMENT");
    }
  }

  /** Updates the component's environment. Alias for updateEnvironmentState(). */
  public void updateEnvironment(String newState) {
    updateEnvironmentState(newState);
  }

  /** Triggers a recoverable error condition in the component. */
  public void triggerRecoverableError() {
    logToMemory("Recoverable error triggered");
    logger.error("Recoverable error triggered", "ERROR");

    // Record recovery attempt
    int attempts = getRecoveryAttempts();
    setProperty("recoveryAttempts", attempts + 1);

    // Set error state and begin recovery
    transitionToState(State.ERROR);
    transitionToState(State.RECOVERING);
  }

  /** Sets a property value on this component. */
  public void setProperty(String key, Object value) {
    if (key != null) {
      properties.put(key, value);
      logger.debug("Property set: " + key, "PROPERTY");
    }
  }

  /** Gets a property value from this component. */
  public Object getProperty(String key) {
    return properties.get(key);
  }

  /** Gets all property keys for this component. */
  public Set<String> getPropertyKeys() {
    return properties.keySet();
  }

  /** Generates a unique identifier based on the reason and environment. */
  private static String generateUniqueId(String reason, Environment environment) {
    try {
      MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
      String input = reason + "-" + environment.getEnvironmentId() + "-" + Instant.now();
      byte[] hash = digest.digest(input.getBytes());

      // Convert byte array to hex string
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
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
    return "Component[id=" + uniqueId + ", reason='" + reason + "', state=" + state + "]";
  }
}
