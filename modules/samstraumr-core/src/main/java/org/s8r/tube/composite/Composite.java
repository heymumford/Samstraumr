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

package org.s8r.tube.composite;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Composite {
  private static final Logger LOGGER = LoggerFactory.getLogger(Composite.class);

  private final String compositeId;
  private final Map<String, Tube> tubes;
  private final Map<String, List<String>> connections;
  private final Map<String, CompositeFunction<?>> transformers;
  private final Map<String, CompositeFunction<Boolean>> validators;
  private final Map<String, CircuitBreaker> circuitBreakers;
  private final List<CompositeEvent> eventLog;
  private final AtomicBoolean active;
  private final Environment environment;

  /**
   * Creates a new Composite with the specified identifier in the given environment.
   *
   * @param compositeId The unique identifier for this composite
   * @param environment The environment in which this composite operates
   */
  public Composite(String compositeId, Environment environment) {
    this.compositeId = compositeId;
    this.environment = environment;
    this.tubes = new ConcurrentHashMap<>();
    this.connections = new ConcurrentHashMap<>();
    this.transformers = new ConcurrentHashMap<>();
    this.validators = new ConcurrentHashMap<>();
    this.circuitBreakers = new ConcurrentHashMap<>();
    this.eventLog = Collections.synchronizedList(new ArrayList<>());
    this.active = new AtomicBoolean(true);

    logEvent("Composite initialized: " + compositeId);
    LOGGER.info("Composite {} initialized", compositeId);
  }

  /**
   * Adds a tube to this composite.
   *
   * @param name The name to reference this tube by within the composite
   * @param tube The tube to add
   * @return This composite instance for method chaining
   */
  public Composite addTube(String name, Tube tube) {
    if (tubes.containsKey(name)) {
      LOGGER.warn("Replacing existing tube with name: {}", name);
    }

    tubes.put(name, tube);
    logEvent("Tube added to composite: " + name);
    return this;
  }

  /**
   * Creates a new tube and adds it to this composite.
   *
   * @param name The name to reference this tube by within the composite
   * @param reason The reason for creating this tube
   * @return This composite instance for method chaining
   */
  public Composite createTube(String name, String reason) {
    Tube tube = Tube.create(reason, environment);
    return addTube(name, tube);
  }

  /**
   * Connects two tubes in the composite to allow data flow.
   *
   * @param sourceName The name of the source tube
   * @param targetName The name of the target tube
   * @return This composite instance for method chaining
   * @throws IllegalArgumentException if either tube name doesn't exist in the composite
   */
  public Composite connect(String sourceName, String targetName) {
    validateTubeExists(sourceName);
    validateTubeExists(targetName);

    connections.computeIfAbsent(sourceName, k -> new ArrayList<>()).add(targetName);

    logEvent(String.format("Connected tubes: %s -> %s", sourceName, targetName));
    LOGGER.debug("Connected tubes in composite {}: {} -> {}", compositeId, sourceName, targetName);
    return this;
  }

  /**
   * Adds a transformer function to a tube in the composite.
   *
   * @param tubeName The name of the tube to add the transformer to
   * @param transformer The transformer function
   * @param <T> The input and output type of the transformer
   * @return This composite instance for method chaining
   */
  public <T> Composite addTransformer(String tubeName, Function<T, T> transformer) {
    validateTubeExists(tubeName);
    transformers.put(tubeName, new CompositeFunction<>(transformer));
    logEvent("Added transformer to tube: " + tubeName);
    return this;
  }

  /**
   * Adds a validator function to a tube in the composite.
   *
   * @param tubeName The name of the tube to add the validator to
   * @param validator The validator function returning true for valid data
   * @param <T> The type of data to validate
   * @return This composite instance for method chaining
   */
  public <T> Composite addValidator(String tubeName, Function<T, Boolean> validator) {
    validateTubeExists(tubeName);
    validators.put(tubeName, new CompositeFunction<>(validator));
    logEvent("Added validator to tube: " + tubeName);
    return this;
  }

  /**
   * Enables circuit breaker for a tube to handle failures gracefully.
   *
   * @param tubeName The name of the tube to add circuit breaker to
   * @param failureThreshold Number of failures before tripping
   * @param resetTimeoutMs Reset timeout in milliseconds
   * @return This composite instance for method chaining
   */
  public Composite enableCircuitBreaker(
      String tubeName, int failureThreshold, long resetTimeoutMs) {
    validateTubeExists(tubeName);
    circuitBreakers.put(tubeName, new CircuitBreaker(tubeName, failureThreshold, resetTimeoutMs));
    logEvent("Enabled circuit breaker for tube: " + tubeName);
    return this;
  }

  /**
   * Processes data through the composite, starting at the specified entry tube.
   *
   * @param entryPoint The name of the tube to start processing from
   * @param data The data to process
   * @param <T> The type of the data
   * @return Optional containing the processed result, or empty if processing failed
   */
  public <T> Optional<T> process(String entryPoint, T data) {
    validateTubeExists(entryPoint);

    if (!active.get()) {
      LOGGER.warn("Cannot process data: Composite {} is not active", compositeId);
      return Optional.empty();
    }

    CompositeData<T> compositeData = new CompositeData<>(data);
    logEvent("Starting data processing at tube: " + entryPoint);

    try {
      return processInternal(entryPoint, compositeData);
    } catch (Exception e) {
      LOGGER.error(
          "Error processing data through composite {}: {}", compositeId, e.getMessage(), e);
      logEvent("Processing error: " + e.getMessage());
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<T> processInternal(String tubeName, CompositeData<T> compositeData) {
    // First check if the circuit breaker is open
    CircuitBreaker circuitBreaker = circuitBreakers.get(tubeName);
    if (circuitBreaker != null && circuitBreaker.isOpen()) {
      logEvent("Circuit open for tube: " + tubeName + ", skipping processing");
      return Optional.empty();
    }

    try {
      // Validate if this is a validator tube
      if (validators.containsKey(tubeName)) {
        CompositeFunction<Boolean> validator = validators.get(tubeName);
        Boolean valid = (Boolean) validator.apply(compositeData.getData());

        if (!valid) {
          logEvent("Validation failed at tube: " + tubeName);
          return Optional.empty();
        }
        logEvent("Data passed validation at tube: " + tubeName);
      }

      // Transform if this is a transformer tube
      if (transformers.containsKey(tubeName)) {
        CompositeFunction<?> transformer = transformers.get(tubeName);
        Object transformedData = transformer.apply(compositeData.getData());
        compositeData.setData((T) transformedData);
        logEvent("Data transformed at tube: " + tubeName);
      }

      // Get downstream connections
      List<String> downstreamTubes = connections.getOrDefault(tubeName, Collections.emptyList());

      // If this is a terminal tube (no downstream connections), return the result
      if (downstreamTubes.isEmpty()) {
        logEvent("Data processing completed at terminal tube: " + tubeName);
        return Optional.of(compositeData.getData());
      }

      // Pass data to all downstream tubes
      for (String downstreamTube : downstreamTubes) {
        logEvent("Passing data to tube: " + downstreamTube);
        Optional<T> result =
            processInternal(downstreamTube, new CompositeData<>(compositeData.getData()));
        if (result.isPresent()) {
          return result; // Return first successful result
        }
      }

      // If we get here, no downstream processing was successful
      return Optional.empty();

    } catch (Exception e) {
      // Record failure with circuit breaker if enabled
      if (circuitBreaker != null) {
        circuitBreaker.recordFailure();
        logEvent("Recorded failure for circuit breaker at tube: " + tubeName);
      }

      logEvent("Error in tube " + tubeName + ": " + e.getMessage());
      LOGGER.error("Error in tube {}: {}", tubeName, e.getMessage(), e);
      return Optional.empty();
    }
  }

  /**
   * Gets all event logs from this composite.
   *
   * @return An unmodifiable list of all events logged by this composite
   */
  public List<CompositeEvent> getEventLog() {
    return Collections.unmodifiableList(eventLog);
  }

  /**
   * Checks if the composite is currently active.
   *
   * @return true if the composite is active, false otherwise
   */
  public boolean isActive() {
    return active.get();
  }

  /** Deactivates the composite, preventing further data processing. */
  public void deactivate() {
    if (active.compareAndSet(true, false)) {
      logEvent("Composite deactivated: " + compositeId);
      LOGGER.info("Composite {} deactivated", compositeId);
    }
  }

  /** Reactivates the composite to allow data processing. */
  public void activate() {
    if (active.compareAndSet(false, true)) {
      logEvent("Composite activated: " + compositeId);
      LOGGER.info("Composite {} activated", compositeId);
    }
  }

  /**
   * Gets the composite's unique identifier.
   *
   * @return The composite ID
   */
  public String getCompositeId() {
    return compositeId;
  }

  /**
   * Gets a tube from the composite by name.
   *
   * @param name The name of the tube to retrieve
   * @return The requested tube
   * @throws IllegalArgumentException if the tube doesn't exist
   */
  public Tube getTube(String name) {
    validateTubeExists(name);
    return tubes.get(name);
  }

  /**
   * Gets all tubes in this composite.
   *
   * @return An unmodifiable map of tube names to tubes
   */
  public Map<String, Tube> getTubes() {
    return Collections.unmodifiableMap(tubes);
  }

  /**
   * Gets all connections in this composite.
   *
   * @return An unmodifiable map of source tube names to lists of target tube names
   */
  public Map<String, List<String>> getConnections() {
    return Collections.unmodifiableMap(connections);
  }

  /**
   * Gets all circuit breakers in this composite.
   *
   * @return An unmodifiable map of tube names to circuit breakers
   */
  public Map<String, CircuitBreaker> getCircuitBreakers() {
    return Collections.unmodifiableMap(circuitBreakers);
  }

  // Private helper methods

  private void validateTubeExists(String tubeName) {
    if (!tubes.containsKey(tubeName)) {
      throw new IllegalArgumentException("Tube not found in composite: " + tubeName);
    }
  }

  /**
   * Logs an event in the composite's event log.
   *
   * @param description The description of the event
   */
  public void logEvent(String description) {
    CompositeEvent event = new CompositeEvent(description);
    eventLog.add(event);
    LOGGER.debug("Composite event: {}", description);
  }

  /** Wrapper class for data flowing through the composite. */
  private static class CompositeData<T> {
    private T data;

    public CompositeData(T data) {
      this.data = data;
    }

    public T getData() {
      return data;
    }

    public void setData(T data) {
      this.data = data;
    }
  }

  /** Event logged within a composite. */
  public static class CompositeEvent {
    private final String description;
    private final long timestamp;

    public CompositeEvent(String description) {
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

  /** Type-erased wrapper for composite functions to allow storing in maps. */
  private static class CompositeFunction<T> {
    private final Function<Object, T> function;

    @SuppressWarnings("unchecked")
    public <I> CompositeFunction(Function<I, T> function) {
      this.function = (Function<Object, T>) function;
    }

    public T apply(Object input) {
      return function.apply(input);
    }
  }

  /** Circuit breaker implementation for fault tolerance. */
  public static class CircuitBreaker {
    private final String tubeName;
    private final int failureThreshold;
    private final long resetTimeoutMs;
    private final AtomicBoolean open = new AtomicBoolean(false);
    private int failureCount = 0;
    private long lastFailureTime = 0;

    public CircuitBreaker(String tubeName, int failureThreshold, long resetTimeoutMs) {
      this.tubeName = tubeName;
      this.failureThreshold = failureThreshold;
      this.resetTimeoutMs = resetTimeoutMs;
    }

    public synchronized void recordFailure() {
      failureCount++;
      lastFailureTime = System.currentTimeMillis();

      if (failureCount >= failureThreshold) {
        open.set(true);
        LOGGER.warn("Circuit breaker opened for tube {}: Failure threshold reached", tubeName);
      }
    }

    public synchronized void reset() {
      failureCount = 0;
      open.set(false);
      LOGGER.info("Circuit breaker reset for tube {}", tubeName);
    }

    public synchronized boolean isOpen() {
      if (open.get()) {
        // Check if reset timeout has elapsed
        if (System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
          LOGGER.info("Circuit breaker for tube {} entering half-open state", tubeName);
          open.set(false); // Move to half-open state
          return false;
        }
        return true;
      }
      return false;
    }

    public String getTubeName() {
      return tubeName;
    }

    public int getFailureCount() {
      return failureCount;
    }
  }
}
