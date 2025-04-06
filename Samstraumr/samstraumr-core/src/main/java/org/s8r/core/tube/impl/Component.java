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

package org.s8r.core.tube.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.s8r.core.env.Environment;
import org.s8r.core.exception.InitializationException;
import org.s8r.core.tube.LifecycleState;
import org.s8r.core.tube.Status;
import org.s8r.core.tube.identity.Identity;
import org.s8r.core.tube.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Component concept in the S8r framework.
 *
 * <p>This class is a complete replacement of the legacy Tube implementation, providing a cleaner,
 * more maintainable approach to the component model. It implements the essential infrastructure for
 * components to maintain their hierarchical design and data processing capabilities.
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
  private volatile Timer terminationTimer;
  private String environmentState = "normal";
  private Status status = Status.INITIALIZING;
  private LifecycleState lifecycleState = LifecycleState.CONCEPTION;
  private final Identity identity;
  private final Instant conceptionTime;
  private Identity parentIdentity;
  private final Logger logger;

  private Component(
      String reason, Environment environment, String uniqueId, Identity parentIdentity) {
    this.reason = reason;
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.parentIdentity = parentIdentity;
    this.lineage = new ArrayList<>();
    this.lineage.add(reason);
    this.memoryLog = new LinkedList<>();

    // Validate environment
    if (environment == null) {
      throw new InitializationException("Environment cannot be null");
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
      this.identity = Identity.createChildIdentity(reason, environment, parentIdentity);
      logToMemory("Created with parent identity: " + parentIdentity.getUniqueId());
      logger.info(
          "Created with parent identity: " + parentIdentity.getUniqueId(), "IDENTITY", "CHILD");
    } else {
      this.identity = Identity.createAdamIdentity(reason, environment);
      logToMemory("Created as Adam component (no parent)");
      logger.info("Created as Adam component (no parent)", "IDENTITY", "ADAM");
    }

    // Proceed through early lifecycle phases
    logToMemory("Starting lifecycle development");
    this.lifecycleState = LifecycleState.INITIALIZING;
    logger.debug("Component entering INITIALIZING phase", "LIFECYCLE", "INIT");

    // Advanced initialization
    initialize();

    // Proceed through early lifecycle phases
    proceedThroughEarlyLifecycle();

    // Update status
    this.status = Status.READY;
    this.lifecycleState = LifecycleState.READY;
    logToMemory("Component initialized and ready");
    logger.info("Component initialized and ready", "LIFECYCLE", "READY");
  }

  /**
   * Creates a new component with the specified reason and environment.
   *
   * @param reason The reason for creating this component
   * @param environment The environment in which to create the component
   * @return A new component instance
   * @throws InitializationException if initialization fails
   */
  public static Component create(String reason, Environment environment) {
    if (reason == null) {
      throw new InitializationException("Reason cannot be null");
    }
    if (environment == null) {
      throw new InitializationException("Environment cannot be null");
    }

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }

  /**
   * Creates a new Adam (origin) component with the specified reason and environment. An Adam
   * component is the first component created in a system, with no parent.
   *
   * @param reason The reason for creating this Adam component
   * @param environment The environment in which to create the component
   * @return A new Adam component instance
   * @throws InitializationException if initialization fails
   */
  public static Component createAdam(String reason, Environment environment) {
    if (reason == null) {
      throw new InitializationException("Reason cannot be null");
    }
    if (environment == null) {
      throw new InitializationException("Environment cannot be null");
    }

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }

  /**
   * Creates a new Adam (origin) component with the specified reason and environment. This method
   * intentionally throws an exception if a parent is provided, as Adam components cannot have
   * parents by definition.
   *
   * @param reason The reason for creating this Adam component
   * @param environment The environment in which to create the component
   * @param parent The parent component, which should be null for Adam components
   * @return A new Adam component instance
   * @throws IllegalArgumentException if a parent is provided
   * @throws InitializationException if initialization fails
   */
  public static Component createAdam(String reason, Environment environment, Component parent) {
    if (parent != null) {
      throw new IllegalArgumentException("Adam tubes cannot have parents");
    }
    return createAdam(reason, environment);
  }

  /**
   * Creates a child component with a parent reference.
   *
   * @param reason The reason for creating this child component
   * @param environment The environment in which to create the component
   * @param parent The parent component
   * @return A new child component instance
   * @throws InitializationException if initialization fails
   */
  public static Component createChild(String reason, Environment environment, Component parent) {
    if (reason == null) {
      throw new InitializationException("Reason cannot be null");
    }
    if (environment == null) {
      throw new InitializationException("Environment cannot be null");
    }
    if (parent == null) {
      throw new InitializationException("Parent component cannot be null");
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

  /** Performs additional initialization steps. */
  private void initialize() {
    logger.debug("Initializing component...", "LIFECYCLE", "INIT");

    // Set up termination timer
    setupTerminationTimer(DEFAULT_TERMINATION_DELAY);

    // Additional initialization can be added here
    logToMemory("Additional initialization complete");
    logger.info("Component initialization complete", "LIFECYCLE", "INIT");
  }

  /**
   * Sets up a timer for component termination.
   *
   * @param delaySeconds The delay in seconds before termination
   */
  public void setupTerminationTimer(int delaySeconds) {
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

    logToMemory("Termination timer set for " + delaySeconds + " seconds");
  }

  /**
   * Sets the termination delay for this component.
   *
   * @param seconds The delay in seconds
   */
  public void setTerminationDelay(int seconds) {
    setupTerminationTimer(seconds);
  }

  /** Terminates this component, releasing resources. */
  public void terminate() {
    logToMemory("Component termination initiated");
    logger.info("Component termination initiated", "LIFECYCLE", "TERMINATE");

    // Cancel timer if active
    if (terminationTimer != null) {
      terminationTimer.cancel();
      terminationTimer = null;
      logger.debug("Termination timer canceled", "LIFECYCLE", "TERMINATE");
    }

    // Update states
    this.status = Status.TERMINATED;
    this.lifecycleState = LifecycleState.TERMINATED;

    // Perform cleanup operations
    preserveKnowledge();
    releaseResources();

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
   * Logs a message both to memory and via the logger.
   *
   * @param message The message to log
   */
  public void log(String message) {
    logToMemory(message);
    if (logger != null) {
      logger.info(message);
    }
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
   * Gets the current status of this component.
   *
   * @return The component status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * Sets the status of this component.
   *
   * @param newStatus The new status to set
   */
  public void setStatus(Status newStatus) {
    if (newStatus != this.status) {
      Status oldStatus = this.status;
      this.status = newStatus;
      logToMemory("Status changed: " + oldStatus + " -> " + newStatus);
      logger.info("Status changed: " + oldStatus + " -> " + newStatus, "STATUS");
    }
  }

  /**
   * Gets the current lifecycle state of this component.
   *
   * @return The lifecycle state
   */
  public LifecycleState getLifecycleState() {
    return lifecycleState;
  }

  /**
   * Gets the current lifecycle state of this component. This is an alias for getLifecycleState()
   * for backward compatibility.
   *
   * @return The lifecycle state
   */
  public LifecycleState getState() {
    return getLifecycleState();
  }

  /**
   * Sets the lifecycle state of this component.
   *
   * @param newState The new lifecycle state to set
   */
  public void setLifecycleState(LifecycleState newState) {
    if (newState != this.lifecycleState) {
      LifecycleState oldState = this.lifecycleState;
      this.lifecycleState = newState;
      logToMemory("Lifecycle state changed: " + oldState + " -> " + newState);
      logger.info(
          "Lifecycle state changed: " + oldState.name() + " -> " + newState.name(),
          "LIFECYCLE",
          newState.name());
    }
  }

  /**
   * Sets the lifecycle state of this component. This is an alias for setLifecycleState() for
   * backward compatibility.
   *
   * @param newState The new lifecycle state to set
   */
  public void setState(LifecycleState newState) {
    setLifecycleState(newState);
  }

  /**
   * Proceeds through the early lifecycle phases of component development. This follows the
   * biological metaphor from conception through early development.
   */
  public void proceedThroughEarlyLifecycle() {
    logToMemory("Beginning early lifecycle development");
    logger.info("Beginning early lifecycle development", "LIFECYCLE", "DEVELOPMENT");

    // CONFIGURING phase - establishing boundaries
    setLifecycleState(LifecycleState.CONFIGURING);
    logToMemory("Component entering CONFIGURING phase (analog: Blastulation)");
    // In a full implementation, this would set up internal boundaries and structures

    // SPECIALIZING phase - determining core functions
    setLifecycleState(LifecycleState.SPECIALIZING);
    logToMemory("Component entering SPECIALIZING phase (analog: Gastrulation)");
    // In a full implementation, this would determine core functionality

    // DEVELOPING_FEATURES phase - building specific capabilities
    setLifecycleState(LifecycleState.DEVELOPING_FEATURES);
    logToMemory("Component entering DEVELOPING_FEATURES phase (analog: Organogenesis)");
    // In a full implementation, this would build specific capabilities

    logToMemory("Completed early lifecycle development");
    logger.info("Completed early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
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
    lineage.add(entry);
  }

  /**
   * Gets the memory log entries for this component.
   *
   * @return An unmodifiable view of the memory log
   */
  public List<String> queryMemoryLog() {
    return Collections.unmodifiableList(memoryLog);
  }

  /**
   * Gets the memory log entries for this component. This is an alias for queryMemoryLog() for
   * backward compatibility.
   *
   * @return An unmodifiable view of the memory log
   */
  public List<String> getMemoryLog() {
    return queryMemoryLog();
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
   * Gets the logger for this component.
   *
   * @return The component logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Checks if this component is operational.
   *
   * @return true if this component is operational, false otherwise
   */
  public boolean isOperational() {
    return status == Status.OPERATIONAL
        || lifecycleState == LifecycleState.READY
        || lifecycleState == LifecycleState.ACTIVE
        || lifecycleState == LifecycleState.STABLE;
  }

  /**
   * Checks if this component is embryonic.
   *
   * @return true if this component is in an embryonic state, false otherwise
   */
  public boolean isEmbryonic() {
    return lifecycleState == LifecycleState.CONCEPTION
        || lifecycleState == LifecycleState.INITIALIZING
        || lifecycleState == LifecycleState.CONFIGURING;
  }

  /**
   * Checks if this component is initializing.
   *
   * @return true if this component is initializing, false otherwise
   */
  public boolean isInitializing() {
    return lifecycleState == LifecycleState.INITIALIZING;
  }

  /**
   * Checks if this component is configuring.
   *
   * @return true if this component is configuring, false otherwise
   */
  public boolean isConfiguring() {
    return lifecycleState == LifecycleState.CONFIGURING;
  }

  /**
   * Checks if this component is ready.
   *
   * @return true if this component is ready, false otherwise
   */
  public boolean isReady() {
    return lifecycleState == LifecycleState.READY;
  }

  /**
   * Checks if this component is active.
   *
   * @return true if this component is active, false otherwise
   */
  public boolean isActive() {
    return lifecycleState == LifecycleState.ACTIVE;
  }

  /**
   * Checks if this component is terminated.
   *
   * @return true if this component is terminated, false otherwise
   */
  public boolean isTerminated() {
    return lifecycleState == LifecycleState.TERMINATED;
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
    return "Component["
        + "id="
        + uniqueId
        + ", reason='"
        + reason
        + "', status="
        + status
        + ", lifecycleState="
        + lifecycleState
        + "]";
  }
}
