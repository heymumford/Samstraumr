/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * Implementation of the Composite pattern for components in the S8r framework
 */

package org.s8r.component.composite;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.s8r.component.core.Component;
import org.s8r.component.core.Environment;
import org.s8r.component.exception.ComponentException;
import org.s8r.component.identity.Identity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container for multiple components that acts as a coordinated unit.
 *
 * <p>This class enables the creation of structured component arrangements
 * with defined data flow paths. It supports various patterns including transformation,
 * validation, observation, and processing chains.
 *
 * <p>Features:
 * <ul>
 *   <li>Component connection management via directed graph</li>
 *   <li>Data transformation and validation pipelines</li>
 *   <li>Circuit breaker pattern for fault tolerance</li>
 *   <li>Event logging for monitoring and diagnostics</li>
 * </ul>
 */
public class Composite {
  private static final Logger LOGGER = LoggerFactory.getLogger(Composite.class);

  private final String compositeId;
  private final Map<String, Component> components;
  private final Map<String, List<String>> connections;
  private final Map<String, CompositeFunction<?>> transformers;
  private final Map<String, CompositeFunction<Boolean>> validators;
  private final Map<String, CircuitBreaker> circuitBreakers;
  private final List<CompositeEvent> eventLog;
  private final AtomicBoolean active;
  private final Environment environment;
  private final Identity compositeIdentity;

  /**
   * Creates a new Composite with the specified identifier in the given environment.
   *
   * @param compositeId The unique identifier for this composite
   * @param environment The environment in which this composite operates
   */
  public Composite(String compositeId, Environment environment) {
    this.compositeId = compositeId;
    this.environment = environment;
    this.components = new ConcurrentHashMap<>();
    this.connections = new ConcurrentHashMap<>();
    this.transformers = new ConcurrentHashMap<>();
    this.validators = new ConcurrentHashMap<>();
    this.circuitBreakers = new ConcurrentHashMap<>();
    this.eventLog = Collections.synchronizedList(new ArrayList<>());
    this.active = new AtomicBoolean(true);

    // Create an identity for this composite
    Map<String, String> envParams = new HashMap<>();
    envParams.put("compositeId", compositeId);
    this.compositeIdentity = Identity.createAdamIdentity("Composite_" + compositeId, envParams);

    logEvent("Composite initialized: " + compositeId);
    LOGGER.info("Composite {} initialized", compositeId);
  }

  /**
   * Adds a component to this composite.
   *
   * @param name The name to reference this component by within the composite
   * @param component The component to add
   * @return This composite instance for method chaining
   */
  public Composite addComponent(String name, Component component) {
    if (components.containsKey(name)) {
      LOGGER.warn("Replacing existing component with name: {}", name);
    }

    components.put(name, component);
    logEvent("Component added to composite: " + name);
    return this;
  }

  /**
   * Creates a new component and adds it to this composite.
   *
   * @param name The name to reference this component by within the composite
   * @param reason The reason for creating this component
   * @return This composite instance for method chaining
   */
  public Composite createComponent(String name, String reason) {
    Component component = Component.create(reason, environment);
    return addComponent(name, component);
  }

  /**
   * Connects two components in the composite to allow data flow.
   *
   * @param sourceName The name of the source component
   * @param targetName The name of the target component
   * @return This composite instance for method chaining
   * @throws IllegalArgumentException if either component name doesn't exist in the composite
   */
  public Composite connect(String sourceName, String targetName) {
    validateComponentExists(sourceName);
    validateComponentExists(targetName);

    connections.computeIfAbsent(sourceName, k -> new ArrayList<>()).add(targetName);

    logEvent(String.format("Connected components: %s -> %s", sourceName, targetName));
    LOGGER.debug("Connected components in composite {}: {} -> {}", compositeId, sourceName, targetName);
    return this;
  }

  /**
   * Adds a transformer function to a component in the composite.
   *
   * @param componentName The name of the component to add the transformer to
   * @param transformer The transformer function
   * @param <T> The input and output type of the transformer
   * @return This composite instance for method chaining
   */
  public <T> Composite addTransformer(String componentName, Function<T, T> transformer) {
    validateComponentExists(componentName);
    transformers.put(componentName, new CompositeFunction<>(transformer));
    logEvent("Added transformer to component: " + componentName);
    return this;
  }

  /**
   * Adds a validator function to a component in the composite.
   *
   * @param componentName The name of the component to add the validator to
   * @param validator The validator function returning true for valid data
   * @param <T> The type of data to validate
   * @return This composite instance for method chaining
   */
  public <T> Composite addValidator(String componentName, Function<T, Boolean> validator) {
    validateComponentExists(componentName);
    validators.put(componentName, new CompositeFunction<>(validator));
    logEvent("Added validator to component: " + componentName);
    return this;
  }

  /**
   * Enables circuit breaker for a component to handle failures gracefully.
   *
   * @param componentName The name of the component to add circuit breaker to
   * @param failureThreshold Number of failures before tripping
   * @param resetTimeoutMs Reset timeout in milliseconds
   * @return This composite instance for method chaining
   */
  public Composite enableCircuitBreaker(
      String componentName, int failureThreshold, long resetTimeoutMs) {
    validateComponentExists(componentName);
    circuitBreakers.put(componentName, new CircuitBreaker(componentName, failureThreshold, resetTimeoutMs));
    logEvent("Enabled circuit breaker for component: " + componentName);
    return this;
  }

  /**
   * Processes data through the composite, starting at the specified entry component.
   *
   * @param entryPoint The name of the component to start processing from
   * @param data The data to process
   * @param <T> The type of the data
   * @return Optional containing the processed result, or empty if processing failed
   */
  public <T> Optional<T> process(String entryPoint, T data) {
    validateComponentExists(entryPoint);

    if (!active.get()) {
      LOGGER.warn("Cannot process data: Composite {} is not active", compositeId);
      return Optional.empty();
    }

    CompositeData<T> compositeData = new CompositeData<>(data);
    logEvent("Starting data processing at component: " + entryPoint);

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
  private <T> Optional<T> processInternal(String componentName, CompositeData<T> compositeData) {
    // First check if the circuit breaker is open
    CircuitBreaker circuitBreaker = circuitBreakers.get(componentName);
    if (circuitBreaker != null && circuitBreaker.isOpen()) {
      logEvent("Circuit open for component: " + componentName + ", skipping processing");
      return Optional.empty();
    }

    try {
      // Validate if this is a validator component
      if (validators.containsKey(componentName)) {
        CompositeFunction<Boolean> validator = validators.get(componentName);
        Boolean valid = (Boolean) validator.apply(compositeData.getData());

        if (!valid) {
          logEvent("Validation failed at component: " + componentName);
          return Optional.empty();
        }
        logEvent("Data passed validation at component: " + componentName);
      }

      // Transform if this is a transformer component
      if (transformers.containsKey(componentName)) {
        CompositeFunction<?> transformer = transformers.get(componentName);
        Object transformedData = transformer.apply(compositeData.getData());
        compositeData.setData((T) transformedData);
        logEvent("Data transformed at component: " + componentName);
      }

      // Get downstream connections
      List<String> downstreamComponents = connections.getOrDefault(componentName, Collections.emptyList());

      // If this is a terminal component (no downstream connections), return the result
      if (downstreamComponents.isEmpty()) {
        logEvent("Data processing completed at terminal component: " + componentName);
        return Optional.of(compositeData.getData());
      }

      // Pass data to all downstream components
      for (String downstreamComponent : downstreamComponents) {
        logEvent("Passing data to component: " + downstreamComponent);
        Optional<T> result =
            processInternal(downstreamComponent, new CompositeData<>(compositeData.getData()));
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
        logEvent("Recorded failure for circuit breaker at component: " + componentName);
      }

      logEvent("Error in component " + componentName + ": " + e.getMessage());
      LOGGER.error("Error in component {}: {}", componentName, e.getMessage(), e);
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
   * Gets the identity of this composite.
   *
   * @return The composite identity
   */
  public Identity getCompositeIdentity() {
    return compositeIdentity;
  }

  /**
   * Gets a component from the composite by name.
   *
   * @param name The name of the component to retrieve
   * @return The requested component
   * @throws IllegalArgumentException if the component doesn't exist
   */
  public Component getComponent(String name) {
    validateComponentExists(name);
    return components.get(name);
  }

  /**
   * Gets all components in this composite.
   *
   * @return An unmodifiable map of component names to components
   */
  public Map<String, Component> getComponents() {
    return Collections.unmodifiableMap(components);
  }

  /**
   * Gets all connections in this composite.
   *
   * @return An unmodifiable map of source component names to lists of target component names
   */
  public Map<String, List<String>> getConnections() {
    return Collections.unmodifiableMap(connections);
  }

  /**
   * Gets all circuit breakers in this composite.
   *
   * @return An unmodifiable map of component names to circuit breakers
   */
  public Map<String, CircuitBreaker> getCircuitBreakers() {
    return Collections.unmodifiableMap(circuitBreakers);
  }

  /**
   * Gets the environment in which this composite operates.
   *
   * @return The composite's environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  // Private helper methods

  private void validateComponentExists(String componentName) {
    if (!components.containsKey(componentName)) {
      throw new IllegalArgumentException("Component not found in composite: " + componentName);
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
    private final Instant timestamp;

    public CompositeEvent(String description) {
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
    private final String componentName;
    private final int failureThreshold;
    private final long resetTimeoutMs;
    private final AtomicBoolean open = new AtomicBoolean(false);
    private int failureCount = 0;
    private long lastFailureTime = 0;

    public CircuitBreaker(String componentName, int failureThreshold, long resetTimeoutMs) {
      this.componentName = componentName;
      this.failureThreshold = failureThreshold;
      this.resetTimeoutMs = resetTimeoutMs;
    }

    public synchronized void recordFailure() {
      failureCount++;
      lastFailureTime = System.currentTimeMillis();

      if (failureCount >= failureThreshold) {
        open.set(true);
        LOGGER.warn("Circuit breaker opened for component {}: Failure threshold reached", 
            componentName);
      }
    }

    public synchronized void reset() {
      failureCount = 0;
      open.set(false);
      LOGGER.info("Circuit breaker reset for component {}", componentName);
    }

    public synchronized boolean isOpen() {
      if (open.get()) {
        // Check if reset timeout has elapsed
        if (System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
          LOGGER.info("Circuit breaker for component {} entering half-open state", componentName);
          open.set(false); // Move to half-open state
          return false;
        }
        return true;
      }
      return false;
    }

    public String getComponentName() {
      return componentName;
    }

    public int getFailureCount() {
      return failureCount;
    }
  }
}