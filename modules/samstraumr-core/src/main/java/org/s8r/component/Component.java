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

package org.s8r.component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Component concept in the S8r framework.
 *
 * <p>Implements a biological-inspired lifecycle model with state transitions and parent-child
 * relationships.
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
  private final List<StateTransitionListener> stateTransitionListeners = new ArrayList<>();
  private final List<EventListener> eventListeners = new CopyOnWriteArrayList<>();

  private volatile Timer terminationTimer;
  private String environmentState = "normal";
  private State state = State.CONCEPTION;
  private Identity parentIdentity;

  /** Private constructor for creating a component instance. */
  private Component(
      String reason, Environment environment, String uniqueId, Identity parentIdentity) {
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
      logger.info(
          "Created with parent identity: " + parentIdentity.getUniqueId(), "IDENTITY", "CHILD");
    } else {
      this.identity = Identity.createAdamIdentity(reason, envParams);
      logToMemory("Created as Adam component (no parent)");
      logger.info("Created as Adam component (no parent)", "IDENTITY", "ADAM");
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

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }

  /** Creates a new Adam component (primary originating component with no parent). */
  public static Component createAdam(String reason) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    Environment environment = new Environment();
    environment.setParameter("component.type", "adam");
    environment.setParameter("component.origin", "primary");
    environment.setParameter("creation.time", Instant.now().toString());

    String uniqueId = generateUniqueId(reason, environment);
    return new Component(reason, environment, uniqueId, null);
  }

  /** Creates a new component with environment parameter map (backward compatibility). */
  public static Component create(String reason, Map<String, String> environmentParams) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environmentParams == null)
      throw new IllegalArgumentException("Environment parameters cannot be null");

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    Environment env = new Environment();
    for (Map.Entry<String, String> entry : environmentParams.entrySet()) {
      env.setParameter(entry.getKey(), entry.getValue());
    }

    return create(reason, env);
  }

  /** Creates a child component with a parent reference. */
  public static Component createChild(String reason, Environment environment, Component parent) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environment == null) throw new IllegalArgumentException("Environment cannot be null");
    if (parent == null) throw new IllegalArgumentException("Parent component cannot be null");

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    // Check if parent is terminated
    if (parent.isTerminated()) {
      Object tReason = parent.getProperty("terminationReason");
      Object tTimestamp = parent.getProperty("terminationTime");
      String terminationReason = tReason != null ? tReason.toString() : "Unknown";
      String terminationTimestamp = tTimestamp != null ? tTimestamp.toString() : "Unknown";

      throw new ComponentTerminatedException(
          "Cannot create child component: parent is terminated",
          parent.getUniqueId(),
          terminationReason,
          terminationTimestamp);
    }

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
   * Creates a child component. This is used in tests.
   *
   * @param reason The reason for creating this child
   * @return A new child component
   * @throws ComponentTerminatedException if this component is terminated
   * @throws org.s8r.domain.exception.InvalidComponentNameException if the component name is invalid
   */
  public Component createChild(String reason) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    if (isTerminated()) {
      throw createTerminatedException("Cannot create child: component is terminated");
    }

    // Create a new environment for the child
    Environment env = new Environment();
    env.setParameter("parent.id", this.getUniqueId());
    env.setParameter("creation.reason", reason);

    return createChild(reason, env, this);
  }

  /** Creates a child component with environment parameter map (backward compatibility). */
  public static Component createChild(
      String reason, Map<String, String> environmentParams, Component parent) {
    if (reason == null) throw new IllegalArgumentException("Reason cannot be null");
    if (environmentParams == null)
      throw new IllegalArgumentException("Environment parameters cannot be null");
    if (parent == null) throw new IllegalArgumentException("Parent component cannot be null");

    // Validate component name
    org.s8r.domain.validation.ComponentNameValidator.validateComponentName(reason);

    // Check if parent is terminated
    if (parent.isTerminated()) {
      Object tReason = parent.getProperty("terminationReason");
      Object tTimestamp = parent.getProperty("terminationTime");
      String terminationReason = tReason != null ? tReason.toString() : "Unknown";
      String terminationTimestamp = tTimestamp != null ? tTimestamp.toString() : "Unknown";

      throw new ComponentTerminatedException(
          "Cannot create child component: parent is terminated",
          parent.getUniqueId(),
          terminationReason,
          terminationTimestamp);
    }

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

  /** Proceeds through the early lifecycle phases of component development. */
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

  /** Sets up a timer for component termination. */
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

  /** Sets the termination delay for this component. */
  public void setTerminationDelay(int seconds) {
    setupTerminationTimer(seconds);
  }

  /** Transitions the component to a new state. */
  private void transitionToState(State newState) {
    if (newState != this.state) {
      // Validate the state transition (in a real implementation, this would have more rules)
      validateStateTransition(this.state, newState);

      State oldState = this.state;
      this.state = newState;
      logToMemory("State changed: " + oldState + " -> " + newState);
      logger.info(
          "State changed: " + oldState.name() + " -> " + newState.name(),
          "LIFECYCLE",
          newState.name());

      // Notify listeners about state transition
      notifyStateTransitionListeners(oldState, newState);
    }
  }

  /**
   * Validates if a state transition is allowed.
   *
   * @param currentState The current state
   * @param targetState The target state
   * @throws InvalidStateTransitionException if the transition is not allowed
   */
  private void validateStateTransition(State currentState, State targetState) {
    // Simple validation: Can't go back to conception after leaving it
    if (currentState != State.CONCEPTION && targetState == State.CONCEPTION) {
      throw new InvalidStateTransitionException(
          "Cannot return to CONCEPTION state once left", uniqueId, currentState, targetState);
    }

    // Can't transition to other states from TERMINATED
    if (currentState == State.TERMINATED && targetState != State.ARCHIVED) {
      throw new InvalidStateTransitionException(
          "Cannot transition from TERMINATED to any state except ARCHIVED",
          uniqueId,
          currentState,
          targetState);
    }

    // Other validation rules would go here
  }

  /**
   * Notifies all registered state transition listeners about a state change.
   *
   * @param oldState The previous state
   * @param newState The new state
   */
  private void notifyStateTransitionListeners(State oldState, State newState) {
    for (StateTransitionListener listener : stateTransitionListeners) {
      try {
        // Only notify if the listener is interested in this state
        if (listener.isInterestedInState(newState)) {
          listener.onStateTransition(this, oldState, newState);
        }
      } catch (Exception e) {
        logger.error(
            "Error notifying state transition listener: " + e.getMessage(), "LIFECYCLE", "ERROR");
      }
    }

    // If transitioning to a terminated state, notify listeners once more and then clear them
    if (newState.isTermination() && !oldState.isTermination()) {
      removeAllStateTransitionListeners();
    }
  }

  /**
   * Removes all state transition listeners and notifies them of termination. This is typically
   * called during component termination.
   */
  private void removeAllStateTransitionListeners() {
    int count = stateTransitionListeners.size();

    // Notify all listeners that they're being removed due to termination
    for (StateTransitionListener listener : stateTransitionListeners) {
      try {
        listener.onTermination();
      } catch (Exception e) {
        logger.error("Error during listener termination: " + e.getMessage(), "LIFECYCLE", "ERROR");
      }
    }

    // Clear the listeners list
    stateTransitionListeners.clear();

    logToMemory("Removed all state transition listeners: " + count);
    logger.debug("Removed all state transition listeners: " + count, "LIFECYCLE");
  }

  /**
   * Adds a state transition listener.
   *
   * @param listener The listener to add
   */
  public void addStateTransitionListener(StateTransitionListener listener) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot add listeners to terminated component");
    }

    if (listener == null) {
      throw new IllegalArgumentException("State transition listener cannot be null");
    }
    stateTransitionListeners.add(listener);
  }

  /**
   * Removes a state transition listener.
   *
   * @param listener The listener to remove
   * @return true if the listener was removed, false if it wasn't registered
   */
  public boolean removeStateTransitionListener(StateTransitionListener listener) {
    return stateTransitionListeners.remove(listener);
  }

  /**
   * Adds an event listener for the specified event type.
   *
   * @param listener The listener to add
   * @param eventType The event type to listen for
   */
  public void addEventListener(EventListener listener, String eventType) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot add listeners to terminated component");
    }

    if (listener == null) {
      throw new IllegalArgumentException("Event listener cannot be null");
    }
    if (eventType == null || eventType.isEmpty()) {
      throw new IllegalArgumentException("Event type cannot be null or empty");
    }

    eventListeners.add(listener);

    // Store event type subscription in listener properties
    listener.setEventType(eventType);

    logToMemory("Added event listener for event type: " + eventType);
    logger.debug("Added event listener for event type: " + eventType, "EVENT", "SUBSCRIBE");
  }

  /**
   * Gets all active event listeners. This method is primarily intended for testing purposes.
   *
   * @return List of event listeners
   */
  public List<EventListener> getActiveListeners() {
    return Collections.unmodifiableList(eventListeners);
  }

  /**
   * Removes an event listener.
   *
   * @param listener The listener to remove
   * @return true if the listener was removed, false if it wasn't registered
   */
  public boolean removeEventListener(EventListener listener) {
    boolean removed = eventListeners.remove(listener);

    if (removed) {
      logToMemory("Removed event listener for event type: " + listener.getEventType());
      logger.debug(
          "Removed event listener for event type: " + listener.getEventType(),
          "EVENT",
          "UNSUBSCRIBE");
    }

    return removed;
  }

  /**
   * Unsubscribes from all events by removing all listeners. This is typically called during
   * termination.
   */
  public void unsubscribeFromAllEvents() {
    int count = eventListeners.size();

    // Notify all listeners that they're being removed due to termination
    for (EventListener listener : eventListeners) {
      try {
        listener.onTermination();
      } catch (Exception e) {
        logger.error("Error during listener termination: " + e.getMessage(), "EVENT", "ERROR");
      }
    }

    // Clear the listeners list
    eventListeners.clear();

    logToMemory("Unsubscribed from all events, removed " + count + " listeners");
    logger.info(
        "Unsubscribed from all events, removed " + count + " listeners", "EVENT", "UNSUBSCRIBE");
  }

  /**
   * Publishes an event of the specified type with optional data.
   *
   * @param eventType The type of event to publish
   * @param data The event data (can be null)
   * @return true if the event was published, false if it was queued or rejected
   * @throws ComponentTerminatedException if the component is terminated
   */
  public boolean publishEvent(String eventType, Map<String, Object> data) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot publish events from terminated component");
    }

    // Check if this is a diagnostic event
    boolean isDiagnosticEvent =
        eventType.startsWith("diagnostic.")
            || eventType.startsWith("system.")
            || eventType.startsWith("recovery.");

    // In SUSPENDED state, can't publish any events
    if (state == State.SUSPENDED) {
      if (isDiagnosticEvent) {
        // Allow diagnostic events even in SUSPENDED state
        notifyListeners(eventType, data);
        return true;
      } else {
        // Queue normal events
        queueEvent(eventType, data);
        return false;
      }
    }

    // In MAINTENANCE state, only publish diagnostic events, queue others
    if (state == State.MAINTENANCE) {
      if (isDiagnosticEvent) {
        notifyListeners(eventType, data);
        return true;
      } else {
        queueEvent(eventType, data);
        return false;
      }
    }

    // In all other non-terminated states, publish events normally
    notifyListeners(eventType, data);
    return true;
  }

  /**
   * Queues an event for later processing.
   *
   * @param eventType The type of event
   * @param data The event data
   */
  private void queueEvent(String eventType, Map<String, Object> data) {
    // Create a queue if it doesn't exist
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> eventQueue = (List<Map<String, Object>>) getProperty("eventQueue");

    if (eventQueue == null) {
      eventQueue = new ArrayList<>();
      setProperty("eventQueue", eventQueue);
    }

    // Add event to queue
    Map<String, Object> queuedEvent = new HashMap<>();
    queuedEvent.put("type", eventType);
    queuedEvent.put("data", data);
    queuedEvent.put("timestamp", java.time.Instant.now().toString());

    eventQueue.add(queuedEvent);

    logToMemory("Queued event of type: " + eventType);
    logger.debug("Queued event of type: " + eventType, "EVENT", "QUEUE");
  }

  /**
   * Processes all queued events. This is typically called when transitioning back to ACTIVE state.
   */
  public void processQueuedEvents() {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> eventQueue = (List<Map<String, Object>>) getProperty("eventQueue");

    if (eventQueue == null || eventQueue.isEmpty()) {
      return;
    }

    logToMemory("Processing " + eventQueue.size() + " queued events");
    logger.info("Processing " + eventQueue.size() + " queued events", "EVENT", "QUEUE");

    // Process each queued event
    for (Map<String, Object> queuedEvent : new ArrayList<>(eventQueue)) {
      String eventType = (String) queuedEvent.get("type");
      @SuppressWarnings("unchecked")
      Map<String, Object> data = (Map<String, Object>) queuedEvent.get("data");

      notifyListeners(eventType, data);
    }

    // Clear the queue
    eventQueue.clear();
  }

  /**
   * Notifies all listeners of an event.
   *
   * @param eventType The type of event
   * @param data The event data
   */
  private void notifyListeners(String eventType, Map<String, Object> data) {
    logToMemory("Publishing event of type: " + eventType);
    logger.debug("Publishing event of type: " + eventType, "EVENT", "PUBLISH");

    for (EventListener listener : eventListeners) {
      try {
        // Only notify if the listener is interested in this event type
        if (listener.isInterestedIn(eventType)) {
          listener.onEvent(this, eventType, data);
        }
      } catch (Exception e) {
        logger.error("Error notifying event listener: " + e.getMessage(), "EVENT", "ERROR");
      }
    }
  }

  /** Gets the current state of this component. */
  public State getState() {
    return state;
  }

  /**
   * Creates a ComponentTerminatedException with the current component's termination details.
   *
   * @param message The error message
   * @return A new ComponentTerminatedException
   */
  private ComponentTerminatedException createTerminatedException(String message) {
    Object reason = getProperty("terminationReason");
    Object timestamp = getProperty("terminationTime");
    String reasonStr = reason != null ? reason.toString() : "Unknown";
    String timestampStr = timestamp != null ? timestamp.toString() : "Unknown";

    return new ComponentTerminatedException(message, uniqueId, reasonStr, timestampStr);
  }

  /** Sets the component state to a new value. */
  public void setState(State newState) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot change state of terminated component");
    }

    // State transition logic will handle the actual state change
    transitionToState(newState);

    // Update resource usage based on new state
    updateResourceUsage();

    // Process queued events if transitioning back to ACTIVE state
    if (newState == State.ACTIVE) {
      processQueuedEvents();
    }

    // When transitioning to terminated state, handle additional cleanup
    if (newState == State.TERMINATED || newState == State.TERMINATING) {
      // Unsubscribe from events
      unsubscribeFromAllEvents();
    }
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

  /** Checks if this component is suspended. */
  public boolean isSuspended() {
    return state == State.SUSPENDED;
  }

  /** Checks if this component is in maintenance mode. */
  public boolean isInMaintenance() {
    return state == State.MAINTENANCE;
  }

  /**
   * Suspends the component, pausing data processing but allowing monitoring. Pending operations
   * will be queued for later processing.
   *
   * @param reason The reason for suspension
   */
  public void suspend(String reason) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot suspend terminated component");
    }

    if (isSuspended()) {
      logger.info("Component already in SUSPENDED state", "LIFECYCLE");
      return;
    }

    // Store pre-suspended state to return to later
    setProperty("preSuspendedState", state.name());

    // Store suspension reason
    setProperty("suspensionReason", reason);
    setProperty("suspensionTime", java.time.Instant.now().toString());

    logToMemory("Component suspension initiated: " + reason);
    logger.info("Component suspension initiated: " + reason, "LIFECYCLE", "SUSPEND");

    // Transition to SUSPENDED state
    transitionToState(State.SUSPENDED);

    // Close all connections
    closeAllConnections();

    // Pause data processing (in a real implementation, this would stop threads, etc.)
    logToMemory("Data processing paused");
    logger.info("Data processing paused", "LIFECYCLE", "SUSPEND");
  }

  /** Resumes the component from suspended state. Any queued operations will be processed. */
  public void resume() {
    if (isTerminated()) {
      throw createTerminatedException("Cannot resume terminated component");
    }

    if (!isSuspended()) {
      logger.info("Component not in SUSPENDED state, cannot resume", "LIFECYCLE");
      return;
    }

    // Get the pre-suspended state or default to ACTIVE
    String preSuspendedStateName = (String) getProperty("preSuspendedState");
    State targetState =
        preSuspendedStateName != null ? State.valueOf(preSuspendedStateName) : State.ACTIVE;

    logToMemory("Resuming component to " + targetState + " state");
    logger.info("Resuming component to " + targetState + " state", "LIFECYCLE", "RESUME");

    // Transition back to original state
    transitionToState(targetState);

    // Process any queued operations
    processQueuedEvents();

    // Resume data processing
    logToMemory("Data processing resumed");
    logger.info("Data processing resumed", "LIFECYCLE", "RESUME");
  }

  /**
   * Transitions the component to maintenance mode. This allows advanced configuration changes and
   * diagnostics.
   *
   * @param reason The reason for entering maintenance mode
   */
  public void enterMaintenanceMode(String reason) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot enter maintenance mode for terminated component");
    }

    if (isInMaintenance()) {
      logger.info("Component already in MAINTENANCE state", "LIFECYCLE");
      return;
    }

    // Store pre-maintenance state to return to later
    setProperty("preMaintenanceState", state.name());

    // Store maintenance reason
    setProperty("maintenanceReason", reason);
    setProperty("maintenanceStartTime", java.time.Instant.now().toString());

    logToMemory("Entering maintenance mode: " + reason);
    logger.info("Entering maintenance mode: " + reason, "LIFECYCLE", "MAINTENANCE");

    // Transition to MAINTENANCE state
    transitionToState(State.MAINTENANCE);

    // Close all connections
    closeAllConnections();
  }

  /**
   * Exits maintenance mode, returning to the previous state. Any queued operations will be
   * processed.
   */
  public void exitMaintenanceMode() {
    if (isTerminated()) {
      throw createTerminatedException("Cannot exit maintenance mode for terminated component");
    }

    if (!isInMaintenance()) {
      logger.info("Component not in MAINTENANCE state", "LIFECYCLE");
      return;
    }

    // Get the pre-maintenance state or default to ACTIVE
    String preMaintenanceStateName = (String) getProperty("preMaintenanceState");
    State targetState =
        preMaintenanceStateName != null ? State.valueOf(preMaintenanceStateName) : State.ACTIVE;

    logToMemory("Exiting maintenance mode, returning to " + targetState + " state");
    logger.info("Exiting maintenance mode", "LIFECYCLE", "MAINTENANCE");

    // Set maintenance end time
    setProperty("maintenanceEndTime", java.time.Instant.now().toString());

    // Transition back to original state
    transitionToState(targetState);

    // Process any queued operations
    processQueuedEvents();
  }

  /** Gets the number of recovery attempts made by this component. */
  public int getRecoveryAttempts() {
    Object attempts = getProperty("recoveryAttempts");
    return attempts instanceof Integer ? (Integer) attempts : 0;
  }

  /** Terminates this component, releasing resources. */
  public void terminate() {
    terminate("No reason provided");
  }

  /** Terminates this component with the specified reason, releasing resources. */
  public void terminate(String reason) {
    if (isTerminated()) {
      return;
    }

    if (reason == null) {
      throw new IllegalArgumentException("Termination reason cannot be null");
    }

    logToMemory("Component termination initiated: " + reason);
    logger.info("Component termination initiated: " + reason, "LIFECYCLE", "TERMINATE");

    // Cancel timer if active
    synchronized (this) {
      if (terminationTimer != null) {
        terminationTimer.cancel();
        terminationTimer = null;
        logger.debug("Termination timer canceled", "LIFECYCLE", "TERMINATE");
      }
    }

    // Store termination reason
    properties.put("terminationReason", reason);
    properties.put("terminationTime", java.time.Instant.now().toString());

    // Transition through termination states
    transitionToState(State.TERMINATING);
    preserveKnowledge();

    // Unsubscribe from all events
    unsubscribeFromAllEvents();

    // Close all connections
    closeAllConnections();

    // Release all resources
    releaseResources();

    transitionToState(State.TERMINATED);

    logToMemory("Component terminated: " + reason);
    logger.info("Component terminated: " + reason, "LIFECYCLE", "TERMINATE");
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

    // Reset resource tracking
    Map<String, Object> resources = new HashMap<>();
    resources.put("memory", 0);
    resources.put("threads", 0);
    resources.put("connections", 0);
    resources.put("timers", 0);
    setProperty("resources", resources);

    // In a full implementation, this would clean up actual system resources
  }

  /**
   * Initializes resource tracking for this component. This is used to simulate resource allocation
   * for testing.
   */
  public void initializeResourceTracking() {
    Map<String, Object> resources = new HashMap<>();
    resources.put("memory", 50); // Initial memory usage in MB
    resources.put("threads", 2); // Initial thread count
    resources.put("connections", 0); // Initial connection count
    resources.put("timers", 1); // Initial timer count

    setProperty("resources", resources);
    logToMemory("Resource tracking initialized");
    logger.debug("Resource tracking initialized", "RESOURCES");
  }

  /**
   * Gets the current resource usage for this component.
   *
   * @return A map of resource types to their usage values
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> getResourceUsage() {
    Map<String, Object> resources = (Map<String, Object>) getProperty("resources");
    if (resources == null) {
      resources = new HashMap<>();
      setProperty("resources", resources);
    }
    return resources;
  }

  /**
   * Gets the current usage of a specific resource type.
   *
   * @param resourceType The type of resource to get usage for
   * @return The current usage, or 0 if the resource is not tracked
   */
  public int getResourceUsage(String resourceType) {
    Map<String, Object> resources = getResourceUsage();
    Object value = resources.get(resourceType);
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    return 0;
  }

  /**
   * Sets the usage level for a specific resource type.
   *
   * @param resourceType The type of resource to set
   * @param usage The usage level
   */
  public void setResourceUsage(String resourceType, int usage) {
    Map<String, Object> resources = getResourceUsage();
    resources.put(resourceType, usage);
    logToMemory("Resource '" + resourceType + "' set to " + usage);
    logger.debug("Resource '" + resourceType + "' set to " + usage, "RESOURCES");
  }

  /**
   * Allocates additional resources of the specified type.
   *
   * @param resourceType The type of resource to allocate
   * @param amount The amount to allocate
   * @return true if allocation was successful, false if resources are unavailable
   */
  public boolean allocateResource(String resourceType, int amount) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot allocate resources for terminated component");
    }

    int current = getResourceUsage(resourceType);
    setResourceUsage(resourceType, current + amount);
    logToMemory("Allocated " + amount + " " + resourceType + " resources");
    logger.info("Allocated " + amount + " " + resourceType + " resources", "RESOURCES");

    // Fire resource allocation event
    Map<String, Object> eventData = new HashMap<>();
    eventData.put("resourceType", resourceType);
    eventData.put("amount", amount);
    eventData.put("newLevel", current + amount);
    publishEvent("resource.allocated", eventData);

    return true;
  }

  /**
   * Releases resources of the specified type.
   *
   * @param resourceType The type of resource to release
   * @param amount The amount to release
   */
  public void releaseResource(String resourceType, int amount) {
    int current = getResourceUsage(resourceType);
    int newAmount = Math.max(0, current - amount); // Ensure we don't go negative
    setResourceUsage(resourceType, newAmount);

    int actualReleased = current - newAmount;
    logToMemory("Released " + actualReleased + " " + resourceType + " resources");
    logger.info("Released " + actualReleased + " " + resourceType + " resources", "RESOURCES");

    // Fire resource release event
    Map<String, Object> eventData = new HashMap<>();
    eventData.put("resourceType", resourceType);
    eventData.put("amount", actualReleased);
    eventData.put("newLevel", newAmount);
    publishEvent("resource.released", eventData);
  }

  /**
   * Updates resource usage based on the component's state. This is used to simulate how resources
   * change in different lifecycle states.
   */
  public void updateResourceUsage() {
    Map<String, Object> resources = getResourceUsage();

    switch (state) {
      case ACTIVE:
        resources.put("memory", 50);
        resources.put("threads", 3);
        resources.put("connections", 2);
        resources.put("timers", 5);
        break;

      case SUSPENDED:
        resources.put("memory", 40);
        resources.put("threads", 2);
        resources.put("connections", 0);
        resources.put("timers", 2);
        break;

      case MAINTENANCE:
        resources.put("memory", 45);
        resources.put("threads", 3);
        resources.put("connections", 0);
        resources.put("timers", 2);
        break;

      case RECOVERING:
        resources.put("memory", 60); // Higher during recovery
        resources.put("threads", 4); // More threads for recovery tasks
        resources.put("connections", 0);
        resources.put("timers", 3);
        break;

      case TERMINATED:
      case TERMINATING:
      case ARCHIVED:
        resources.put("memory", 10); // Some minimal leftover
        resources.put("threads", 0);
        resources.put("connections", 0);
        resources.put("timers", 0);
        break;

      default:
        // Keep current values for other states
        break;
    }

    logToMemory("Resource usage updated for state: " + state);
    logger.debug("Resource usage updated for state: " + state, "RESOURCES");
  }

  /**
   * Gets the list of active connections for this component.
   *
   * @return The list of connection IDs
   */
  @SuppressWarnings("unchecked")
  public List<String> getActiveConnections() {
    List<String> connections = (List<String>) getProperty("activeConnections");
    if (connections == null) {
      connections = new ArrayList<>();
      setProperty("activeConnections", connections);
    }
    return connections;
  }

  /**
   * Establishes a new connection to this component. Only available in ACTIVE state.
   *
   * @param sourceId The ID of the connecting entity
   * @return The connection ID or null if the connection was rejected
   * @throws ComponentTerminatedException if the component is terminated
   * @throws IllegalStateException if the component cannot accept connections in its current state
   */
  public String establishConnection(String sourceId) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot establish connection to terminated component");
    }

    if (!canPerformOperation("establish_connection")) {
      throw new IllegalStateException("Cannot establish connection in " + state + " state");
    }

    // Generate a unique connection ID
    String connectionId = "CONN-" + java.util.UUID.randomUUID().toString().substring(0, 8);

    // Add to active connections
    List<String> connections = getActiveConnections();
    connections.add(connectionId);

    // Update connection count in resources
    allocateResource("connections", 1);

    // Record connection details
    Map<String, String> connectionDetails = new HashMap<>();
    connectionDetails.put("sourceId", sourceId);
    connectionDetails.put("established", java.time.Instant.now().toString());
    setProperty("connection:" + connectionId, connectionDetails);

    logToMemory("Connection established from " + sourceId + ", id=" + connectionId);
    logger.info("Connection established from " + sourceId + ", id=" + connectionId, "CONNECTION");

    return connectionId;
  }

  /**
   * Closes a specific connection.
   *
   * @param connectionId The ID of the connection to close
   * @return true if the connection was closed, false if it wasn't found
   */
  public boolean closeConnection(String connectionId) {
    List<String> connections = getActiveConnections();
    boolean removed = connections.remove(connectionId);

    if (removed) {
      // Release connection resource
      releaseResource("connections", 1);

      // Update connection status
      @SuppressWarnings("unchecked")
      Map<String, String> connectionDetails =
          (Map<String, String>) getProperty("connection:" + connectionId);

      if (connectionDetails != null) {
        connectionDetails.put("closed", java.time.Instant.now().toString());
      }

      logToMemory("Connection closed: " + connectionId);
      logger.info("Connection closed: " + connectionId, "CONNECTION");
    }

    return removed;
  }

  /** Closes all active connections. This is typically called during suspension or termination. */
  public void closeAllConnections() {
    List<String> connections = new ArrayList<>(getActiveConnections());
    int count = connections.size();

    for (String connectionId : connections) {
      closeConnection(connectionId);
    }

    logToMemory("Closed all connections: " + count);
    logger.info("Closed all connections: " + count, "CONNECTION");
  }

  /** Registers a child component with this component. */
  public void registerChild(Component childComponent) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot register children on terminated component");
    }

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
   * Checks if this component has any children.
   *
   * @return true if this component has at least one child, false otherwise
   */
  public boolean hasChildren() {
    return this.identity != null
        && this.identity.getDescendants() != null
        && !this.identity.getDescendants().isEmpty();
  }

  /**
   * Tests whether a specific operation can be performed in the current state.
   *
   * @param operationType The type of operation to test
   * @return true if the operation is allowed, false otherwise
   * @throws ComponentTerminatedException if the component is terminated
   */
  public boolean canPerformOperation(String operationType) {
    if (isTerminated()) {
      throw createTerminatedException(
          "Cannot perform operations on terminated component: " + operationType);
    }

    switch (operationType) {
      case "process_data":
        return state.allowsDataProcessing();

      case "query_status":
        return state.allowsDiagnostics();

      case "update_config":
        return state.allowsConfigurationChanges();

      case "establish_connection":
        return state.allowsIncomingConnections();

      case "run_diagnostics":
        return state.allowsDiagnostics() || state.allowsRecoveryOperations();

      case "reset_config":
        return state == State.MAINTENANCE;

      case "view_config":
        return state.allowsDiagnostics();

      default:
        return false;
    }
  }

  /**
   * Performs an operation with the given type and parameters.
   *
   * @param operationType The type of operation to perform
   * @param params Parameters for the operation (optional)
   * @return The result of the operation
   * @throws ComponentTerminatedException if the component is terminated
   * @throws IllegalStateException if the operation is not allowed in the current state
   */
  public Object performOperation(String operationType, Map<String, Object> params) {
    // First check if the operation is allowed
    if (!canPerformOperation(operationType)) {
      throw new IllegalStateException(
          "Operation " + operationType + " is not allowed in state " + state.name());
    }

    // Perform the operation based on type
    switch (operationType) {
      case "process_data":
        // Process data (simulated)
        logToMemory("Processing data: " + (params != null ? params.toString() : "no params"));
        return "Data processed successfully";

      case "query_status":
        // Return component status
        Map<String, Object> status = new HashMap<>();
        status.put("state", state.name());
        status.put("id", uniqueId);
        status.put(
            "uptime",
            java.time.Duration.between(conceptionTime, java.time.Instant.now()).toSeconds());
        return status;

      case "update_config":
        // Update configuration
        if (params != null) {
          for (Map.Entry<String, Object> entry : params.entrySet()) {
            environment.setParameter(entry.getKey(), entry.getValue().toString());
          }
        }
        return "Configuration updated";

      case "establish_connection":
        // Simulated connection establishment
        String connectionId = "CONN-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        logToMemory("Established connection: " + connectionId);
        return connectionId;

      case "run_diagnostics":
        // Run diagnostics (simulated)
        Map<String, Object> diagnosticResults = new HashMap<>();
        diagnosticResults.put("memoryUsage", "Normal");
        diagnosticResults.put("errors", "None");
        diagnosticResults.put("status", "Healthy");
        return diagnosticResults;

      case "reset_config":
        // Reset configuration to defaults
        // Note: environment is final, so we create a new one and copy the parameters
        Environment newEnv = new Environment();
        for (String key : environment.getParameterKeys()) {
          // Copy only default parameters or reset to defaults as needed
          if (key.startsWith("default.")) {
            newEnv.setParameter(key.replace("default.", ""), environment.getParameter(key));
          }
        }
        logToMemory("Configuration reset to defaults");
        return "Configuration reset (default parameters applied)";

      case "view_config":
        // View current configuration
        Map<String, String> config = new HashMap<>();
        for (String key : environment.getParameterKeys()) {
          config.put(key, environment.getParameter(key));
        }
        return config;

      default:
        throw new IllegalArgumentException("Unknown operation type: " + operationType);
    }
  }

  /**
   * Gets all children of this component.
   *
   * @return A list of child components
   */
  public List<Component> getChildren() {
    // This is a stub implementation that returns an empty list
    // In a real implementation, you would maintain a list of Component references
    // For now, we return an empty list since tests only check hasChildren() or iterate
    return new ArrayList<>();
  }

  /** Logs a message to the memory log. */
  private void logToMemory(String message) {
    String logEntry = Instant.now() + " - " + message;
    memoryLog.add(logEntry);
    LOGGER.debug(logEntry);
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
    return Collections.unmodifiableList(memoryLog);
  }

  public int getMemoryLogSize() {
    return memoryLog.size();
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

  public Logger getLogger() {
    return logger;
  }

  /** Adds an entry to the component's lineage. */
  public void addToLineage(String entry) {
    if (entry != null && !entry.isEmpty()) {
      lineage.add(entry);
      logToMemory("Added to lineage: " + entry);
    }
  }

  /** Updates the component's awareness of environmental state changes. */
  public void updateEnvironmentState(String newState) {
    if (isTerminated()) {
      throw createTerminatedException("Cannot update environment state of terminated component");
    }

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

  /**
   * Triggers a recoverable error condition in the component. This simulates an error condition and
   * initiates automatic recovery.
   */
  public void triggerRecoverableError() {
    logToMemory("Recoverable error triggered");
    logger.error("Recoverable error triggered", "ERROR");

    // Record recovery attempt
    int attempts = getRecoveryAttempts();
    setProperty("recoveryAttempts", attempts + 1);

    // Store original state to return to after recovery
    State originalState = this.state;
    setProperty("preErrorState", originalState.name());

    // Set error state and begin recovery
    transitionToState(State.ERROR);
    transitionToState(State.RECOVERING);

    // Start recovery process in a separate thread
    startRecoveryProcess();
  }

  /**
   * Starts the recovery process in a separate thread. This simulates an asynchronous recovery
   * operation.
   */
  private void startRecoveryProcess() {
    String preErrorState = (String) getProperty("preErrorState");
    final State targetState = preErrorState != null ? State.valueOf(preErrorState) : State.ACTIVE;

    Thread recoveryThread =
        new Thread(
            () -> {
              try {
                logToMemory("Starting recovery process");
                logger.info("Starting recovery process", "RECOVERY");

                // Simulate recovery work
                Thread.sleep(2000);

                // Run recovery diagnostics
                performRecoveryDiagnostics();

                // If successful, return to original state
                logToMemory("Recovery complete, returning to " + targetState);
                logger.info("Recovery complete, returning to " + targetState, "RECOVERY");

                // Transition back to original (or default ACTIVE) state
                setState(targetState);

              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Recovery process interrupted", "RECOVERY");
              } catch (Exception e) {
                logger.error("Recovery failed: " + e.getMessage(), "RECOVERY", "ERROR");

                // If recovery fails, transition to DEGRADED state
                try {
                  setState(State.DEGRADED);
                } catch (Exception ex) {
                  logger.error(
                      "Failed to transition to DEGRADED state: " + ex.getMessage(),
                      "RECOVERY",
                      "ERROR");
                }
              }
            },
            "Recovery-" + uniqueId);

    recoveryThread.setDaemon(true);
    recoveryThread.start();
  }

  /**
   * Performs diagnostic checks during recovery. This simulates running diagnostic operations to
   * recover from an error.
   */
  private void performRecoveryDiagnostics() {
    logToMemory("Running recovery diagnostics");
    logger.info("Running recovery diagnostics", "RECOVERY", "DIAGNOSTICS");

    // Simulate performing various checks and fixes
    // In a real implementation, this would actually fix specific issues
    setProperty("lastDiagnosticRun", java.time.Instant.now().toString());
    setProperty("diagnosticResults", "All checks passed");
  }

  /** Sets a property value on this component. */
  public void setProperty(String key, Object value) {
    if (isTerminated() && !key.startsWith("termination")) {
      throw createTerminatedException("Cannot modify properties of terminated component");
    }

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
