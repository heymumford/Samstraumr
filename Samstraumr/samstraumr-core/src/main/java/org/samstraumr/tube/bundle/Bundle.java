package org.samstraumr.tube.bundle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Bundle represents a collection of connected Tubes that form a processing pipeline. Bundles
 * allow data to flow between tubes in a directed manner.
 */
public class Bundle {
  private static final Logger logger = LoggerFactory.getLogger(Bundle.class);

  private final String bundleId;
  private final Map<String, Tube> tubes;
  private final Map<String, List<String>> connections;
  private final Map<String, BundleFunction<?>> transformers;
  private final Map<String, BundleFunction<Boolean>> validators;
  private final Map<String, CircuitBreaker> circuitBreakers;
  private final List<BundleEvent> eventLog;
  private final AtomicBoolean active;
  private final Environment environment;

  /**
   * Creates a new Bundle with the specified identifier in the given environment.
   *
   * @param bundleId The unique identifier for this bundle
   * @param environment The environment in which this bundle operates
   */
  public Bundle(String bundleId, Environment environment) {
    this.bundleId = bundleId;
    this.environment = environment;
    this.tubes = new ConcurrentHashMap<>();
    this.connections = new ConcurrentHashMap<>();
    this.transformers = new ConcurrentHashMap<>();
    this.validators = new ConcurrentHashMap<>();
    this.circuitBreakers = new ConcurrentHashMap<>();
    this.eventLog = Collections.synchronizedList(new ArrayList<>());
    this.active = new AtomicBoolean(true);

    logEvent("Bundle initialized: " + bundleId);
    logger.info("Bundle {} initialized", bundleId);
  }

  /**
   * Adds a tube to this bundle.
   *
   * @param name The name to reference this tube by within the bundle
   * @param tube The tube to add
   * @return This bundle instance for method chaining
   */
  public Bundle addTube(String name, Tube tube) {
    if (tubes.containsKey(name)) {
      logger.warn("Replacing existing tube with name: {}", name);
    }

    tubes.put(name, tube);
    logEvent("Tube added to bundle: " + name);
    return this;
  }

  /**
   * Creates a new tube and adds it to this bundle.
   *
   * @param name The name to reference this tube by within the bundle
   * @param reason The reason for creating this tube
   * @return This bundle instance for method chaining
   */
  public Bundle createTube(String name, String reason) {
    Tube tube = Tube.create(reason, environment);
    return addTube(name, tube);
  }

  /**
   * Connects two tubes in the bundle to allow data flow.
   *
   * @param sourceName The name of the source tube
   * @param targetName The name of the target tube
   * @return This bundle instance for method chaining
   * @throws IllegalArgumentException if either tube name doesn't exist in the bundle
   */
  public Bundle connect(String sourceName, String targetName) {
    validateTubeExists(sourceName);
    validateTubeExists(targetName);

    connections.computeIfAbsent(sourceName, k -> new ArrayList<>()).add(targetName);

    logEvent(String.format("Connected tubes: %s -> %s", sourceName, targetName));
    logger.debug("Connected tubes in bundle {}: {} -> {}", bundleId, sourceName, targetName);
    return this;
  }

  /**
   * Adds a transformer function to a tube in the bundle.
   *
   * @param tubeName The name of the tube to add the transformer to
   * @param transformer The transformer function
   * @param <T> The input and output type of the transformer
   * @return This bundle instance for method chaining
   */
  public <T> Bundle addTransformer(String tubeName, Function<T, T> transformer) {
    validateTubeExists(tubeName);
    transformers.put(tubeName, new BundleFunction<>(transformer));
    logEvent("Added transformer to tube: " + tubeName);
    return this;
  }

  /**
   * Adds a validator function to a tube in the bundle.
   *
   * @param tubeName The name of the tube to add the validator to
   * @param validator The validator function returning true for valid data
   * @param <T> The type of data to validate
   * @return This bundle instance for method chaining
   */
  public <T> Bundle addValidator(String tubeName, Function<T, Boolean> validator) {
    validateTubeExists(tubeName);
    validators.put(tubeName, new BundleFunction<>(validator));
    logEvent("Added validator to tube: " + tubeName);
    return this;
  }

  /**
   * Enables circuit breaker for a tube to handle failures gracefully.
   *
   * @param tubeName The name of the tube to add circuit breaker to
   * @param failureThreshold Number of failures before tripping
   * @param resetTimeoutMs Reset timeout in milliseconds
   * @return This bundle instance for method chaining
   */
  public Bundle enableCircuitBreaker(String tubeName, int failureThreshold, long resetTimeoutMs) {
    validateTubeExists(tubeName);
    circuitBreakers.put(tubeName, new CircuitBreaker(tubeName, failureThreshold, resetTimeoutMs));
    logEvent("Enabled circuit breaker for tube: " + tubeName);
    return this;
  }

  /**
   * Processes data through the bundle, starting at the specified entry tube.
   *
   * @param entryPoint The name of the tube to start processing from
   * @param data The data to process
   * @param <T> The type of the data
   * @return Optional containing the processed result, or empty if processing failed
   */
  public <T> Optional<T> process(String entryPoint, T data) {
    validateTubeExists(entryPoint);

    if (!active.get()) {
      logger.warn("Cannot process data: Bundle {} is not active", bundleId);
      return Optional.empty();
    }

    BundleData<T> bundleData = new BundleData<>(data);
    logEvent("Starting data processing at tube: " + entryPoint);

    try {
      return processInternal(entryPoint, bundleData);
    } catch (Exception e) {
      logger.error("Error processing data through bundle {}: {}", bundleId, e.getMessage(), e);
      logEvent("Processing error: " + e.getMessage());
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<T> processInternal(String tubeName, BundleData<T> bundleData) {
    // First check if the circuit breaker is open
    CircuitBreaker circuitBreaker = circuitBreakers.get(tubeName);
    if (circuitBreaker != null && circuitBreaker.isOpen()) {
      logEvent("Circuit open for tube: " + tubeName + ", skipping processing");
      return Optional.empty();
    }

    try {
      // Validate if this is a validator tube
      if (validators.containsKey(tubeName)) {
        BundleFunction<Boolean> validator = validators.get(tubeName);
        Boolean valid = (Boolean) validator.apply(bundleData.getData());

        if (!valid) {
          logEvent("Validation failed at tube: " + tubeName);
          return Optional.empty();
        }
        logEvent("Data passed validation at tube: " + tubeName);
      }

      // Transform if this is a transformer tube
      if (transformers.containsKey(tubeName)) {
        BundleFunction<?> transformer = transformers.get(tubeName);
        Object transformedData = transformer.apply(bundleData.getData());
        bundleData.setData((T) transformedData);
        logEvent("Data transformed at tube: " + tubeName);
      }

      // Get downstream connections
      List<String> downstreamTubes = connections.getOrDefault(tubeName, Collections.emptyList());

      // If this is a terminal tube (no downstream connections), return the result
      if (downstreamTubes.isEmpty()) {
        logEvent("Data processing completed at terminal tube: " + tubeName);
        return Optional.of(bundleData.getData());
      }

      // Pass data to all downstream tubes
      for (String downstreamTube : downstreamTubes) {
        logEvent("Passing data to tube: " + downstreamTube);
        Optional<T> result =
            processInternal(downstreamTube, new BundleData<>(bundleData.getData()));
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
      logger.error("Error in tube {}: {}", tubeName, e.getMessage(), e);
      return Optional.empty();
    }
  }

  /**
   * Gets all event logs from this bundle.
   *
   * @return An unmodifiable list of all events logged by this bundle
   */
  public List<BundleEvent> getEventLog() {
    return Collections.unmodifiableList(eventLog);
  }

  /**
   * Checks if the bundle is currently active.
   *
   * @return true if the bundle is active, false otherwise
   */
  public boolean isActive() {
    return active.get();
  }

  /** Deactivates the bundle, preventing further data processing. */
  public void deactivate() {
    if (active.compareAndSet(true, false)) {
      logEvent("Bundle deactivated: " + bundleId);
      logger.info("Bundle {} deactivated", bundleId);
    }
  }

  /** Reactivates the bundle to allow data processing. */
  public void activate() {
    if (active.compareAndSet(false, true)) {
      logEvent("Bundle activated: " + bundleId);
      logger.info("Bundle {} activated", bundleId);
    }
  }

  /**
   * Gets the bundle's unique identifier.
   *
   * @return The bundle ID
   */
  public String getBundleId() {
    return bundleId;
  }

  /**
   * Gets a tube from the bundle by name.
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
   * Gets all tubes in this bundle.
   *
   * @return An unmodifiable map of tube names to tubes
   */
  public Map<String, Tube> getTubes() {
    return Collections.unmodifiableMap(tubes);
  }

  /**
   * Gets all connections in this bundle.
   *
   * @return An unmodifiable map of source tube names to lists of target tube names
   */
  public Map<String, List<String>> getConnections() {
    return Collections.unmodifiableMap(connections);
  }

  // Private helper methods

  private void validateTubeExists(String tubeName) {
    if (!tubes.containsKey(tubeName)) {
      throw new IllegalArgumentException("Tube not found in bundle: " + tubeName);
    }
  }

  private void logEvent(String description) {
    BundleEvent event = new BundleEvent(description);
    eventLog.add(event);
    logger.debug("Bundle event: {}", description);
  }

  /** Wrapper class for data flowing through the bundle. */
  private static class BundleData<T> {
    private T data;

    public BundleData(T data) {
      this.data = data;
    }

    public T getData() {
      return data;
    }

    public void setData(T data) {
      this.data = data;
    }
  }

  /** Event logged within a bundle. */
  public static class BundleEvent {
    private final String description;
    private final long timestamp;

    public BundleEvent(String description) {
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

  /** Type-erased wrapper for bundle functions to allow storing in maps. */
  private static class BundleFunction<T> {
    private final Function<Object, T> function;

    @SuppressWarnings("unchecked")
    public <I> BundleFunction(Function<I, T> function) {
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
        logger.warn("Circuit breaker opened for tube {}: Failure threshold reached", tubeName);
      }
    }

    public synchronized void reset() {
      failureCount = 0;
      open.set(false);
      logger.info("Circuit breaker reset for tube {}", tubeName);
    }

    public synchronized boolean isOpen() {
      if (open.get()) {
        // Check if reset timeout has elapsed
        if (System.currentTimeMillis() - lastFailureTime > resetTimeoutMs) {
          logger.info("Circuit breaker for tube {} entering half-open state", tubeName);
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
