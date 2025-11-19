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

package org.s8r.component.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.component.identity.Identity;
import org.s8r.component.lifecycle.ComponentLifecycleManager;
import org.s8r.component.logging.ComponentLogger;
import org.s8r.component.termination.ComponentTerminationManager;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Component concept in the S8r framework.
 *
 * <p>Implements a biological-inspired lifecycle model with state transitions and parent-child
 * relationships.
 *
 * <p><b>Refactoring Note:</b> Refactored as part of Phase 2 God Class decomposition. Lifecycle,
 * logging, and termination concerns extracted to dedicated manager classes following Single
 * Responsibility Principle (Martin Fowler refactoring).
 */
public class Component {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Component.class);
  private static final String DIGEST_ALGORITHM = "SHA-256";

  // Core identity and configuration
  private final String uniqueId;
  private final String reason;
  private final List<String> lineage;
  private final Environment environment;
  private final Identity identity;
  private final Instant conceptionTime;
  private final Map<String, Object> properties = new ConcurrentHashMap<>();
  private Identity parentIdentity;
  private String environmentState = "normal";

  // Extracted responsibilities (Composition over inheritance)
  private final ComponentLifecycleManager lifecycleManager;
  private final ComponentLogger componentLogger;
  private final ComponentTerminationManager terminationManager;

  /** Private constructor for creating a component instance. */
  private Component(
      String reason, Environment environment, String uniqueId, Identity parentIdentity) {
    this.reason = reason;
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.parentIdentity = parentIdentity;
    this.lineage = Collections.synchronizedList(new ArrayList<>());
    this.lineage.add(reason);

    if (environment == null) {
      throw new IllegalArgumentException("Environment cannot be null");
    }
    this.environment = environment;

    // Initialize managers (composition)
    this.componentLogger = new ComponentLogger(uniqueId);
    this.lifecycleManager = new ComponentLifecycleManager(transition -> {
      // Log state transitions
      componentLogger.info(
          "State changed: " + transition.getFromState() + " -> " + transition.getToState(),
          "LIFECYCLE",
          transition.getToState().name());
    });
    this.terminationManager = new ComponentTerminationManager(uniqueId, componentLogger);

    // Log creation
    componentLogger.info("Component created with reason: " + reason, "CREATION");
    componentLogger.info("Environment: " + environment.toString(), "CREATION");

    // Create identity
    Map<String, String> envParams = convertEnvironmentParams(environment);
    if (parentIdentity != null) {
      this.identity = Identity.createChildIdentity(reason, envParams, parentIdentity);
      componentLogger.info(
          "Created with parent identity: " + parentIdentity.getUniqueId(), "IDENTITY", "CHILD");
    } else {
      this.identity = Identity.createAdamIdentity(reason, envParams);
      componentLogger.info("Created as Adam component (no parent)", "IDENTITY", "ADAM");
    }

    // Initialize through lifecycle phases
    initialize();
  }

  /** Converts environment parameters to a map of strings. */
  private Map<String, String> convertEnvironmentParams(Environment environment) {
    Map<String, String> result = new ConcurrentHashMap<>();
    for (String key : environment.getParameterKeys()) {
      result.put(key, environment.getParameter(key));
    }
    return result;
  }

  /** Creates a new component with the specified reason and environment. */
  public static Component create(String reason, Environment environment) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environment == null) throw new IllegalArgumentException("Environment cannot be null");

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }


  /** Creates a child component with a parent reference. */
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


  /** Performs component initialization through lifecycle phases. */
  private void initialize() {
    componentLogger.debug("Initializing component...", "LIFECYCLE", "INIT");

    // Delegate to lifecycle manager
    lifecycleManager.initialize(() -> {
      // Setup termination timer when ready
      terminationManager.setupDefaultTerminationTimer(this::terminate);
      componentLogger.info("Component initialized and ready", "LIFECYCLE", "READY");
    });
  }

  /** Proceeds through the early lifecycle phases of component development. */
  public void proceedThroughEarlyLifecycle() {
    componentLogger.info("Beginning early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
    lifecycleManager.proceedThroughEarlyLifecycle();
    componentLogger.info("Completed early lifecycle development", "LIFECYCLE", "DEVELOPMENT");
  }

  /** Sets up a timer for component termination. */
  public void setupTerminationTimer(int delaySeconds) {
    if (isTerminated()) {
      throw new IllegalStateException(
          "Cannot set termination delay: component is already terminated");
    }
    terminationManager.setupTerminationTimer(delaySeconds, this::terminate);
  }

  /** Sets the termination delay for this component. */
  public void setTerminationDelay(int seconds) {
    setupTerminationTimer(seconds);
  }

  /** Gets the current state of this component. */
  public State getState() {
    return lifecycleManager.getState();
  }

  /** Sets the component state to a new value. */
  public void setState(State newState) {
    lifecycleManager.transitionToState(newState);
  }

  /** Checks if this component is in a terminated state. */
  public boolean isTerminated() {
    return lifecycleManager.isTerminated();
  }

  /** Checks if this component is operational. */
  public boolean isOperational() {
    return lifecycleManager.isOperational();
  }

  /** Checks if this component is embryonic. */
  public boolean isEmbryonic() {
    return lifecycleManager.isEmbryonic();
  }

  /** Checks if this component is initializing. */
  public boolean isInitializing() {
    return lifecycleManager.isInitializing();
  }

  /** Checks if this component is configuring. */
  public boolean isConfiguring() {
    return lifecycleManager.isConfiguring();
  }

  /** Checks if this component is ready. */
  public boolean isReady() {
    return lifecycleManager.isReady();
  }

  /** Checks if this component is active. */
  public boolean isActive() {
    return lifecycleManager.isActive();
  }

  /** Checks if this component is in error recovery. */
  public boolean isInErrorRecovery() {
    return lifecycleManager.isInErrorRecovery();
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

    // Delegate to termination manager
    terminationManager.terminate(cleanupCallback -> {
      // Begin termination in lifecycle
      if (lifecycleManager.beginTermination()) {
        // Execute cleanup
        cleanupCallback.accept(() -> {});
        // Complete termination
        lifecycleManager.completeTermination();
      }
    });
  }

  /** Registers a child component with this component. */
  public void registerChild(Component childComponent) {
    if (childComponent != null) {
      componentLogger.info(
          "Registering child component: " + childComponent.getUniqueId(), "HIERARCHY", "CHILD");

      // Add the child to our identity's descendants list
      if (this.identity != null && childComponent.getIdentity() != null) {
        this.identity.addChild(childComponent.getIdentity());
        componentLogger.debug("Added child identity to descendants list", "HIERARCHY", "IDENTITY");
      }
    }
  }

  // Getter methods
  public String getUniqueId() {
    return uniqueId;
  }

  public String getReason() {
    return reason;
  }

  public Identity getIdentity() {
    return identity;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  public List<String> getMemoryLog() {
    return componentLogger.getMemoryLog();
  }

  public int getMemoryLogSize() {
    return componentLogger.getMemoryLogSize();
  }

  public String getEnvironmentState() {
    return environmentState;
  }

  public Instant getConceptionTime() {
    return conceptionTime;
  }

  public Identity getParentIdentity() {
    return parentIdentity;
  }

  public org.s8r.component.Logger getLogger() {
    return componentLogger.getLogger();
  }

  /** Adds an entry to the component's lineage. */
  public void addToLineage(String entry) {
    if (entry != null && !entry.isEmpty()) {
      lineage.add(entry);
      componentLogger.info("Added to lineage: " + entry, "LINEAGE");
    }
  }

  /** Updates the component's awareness of environmental state changes. */
  public void updateEnvironmentState(String newState) {
    if (newState != null && !newState.equals(environmentState)) {
      String oldState = environmentState;
      environmentState = newState;
      componentLogger.info("Environment state changed: " + oldState + " -> " + newState, "ENVIRONMENT");
    }
  }

  /** Updates the component's environment. Alias for updateEnvironmentState(). */
  public void updateEnvironment(String newState) {
    updateEnvironmentState(newState);
  }

  /** Triggers a recoverable error condition in the component. */
  public void triggerRecoverableError() {
    componentLogger.error("Recoverable error triggered", "ERROR");

    // Record recovery attempt
    int attempts = getRecoveryAttempts();
    setProperty("recoveryAttempts", attempts + 1);

    // Set error state and begin recovery
    lifecycleManager.transitionToState(State.ERROR);
    lifecycleManager.transitionToState(State.RECOVERING);
  }

  /** Sets a property value on this component. */
  public void setProperty(String key, Object value) {
    if (key != null) {
      properties.put(key, value);
      componentLogger.debug("Property set: " + key, "PROPERTY");
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
      String input = reason + "-" + environment.toString() + "-" + Instant.now();
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
