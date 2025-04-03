package org.samstraumr.tube.bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.composite.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link org.samstraumr.tube.composite.Composite} instead.
 */
@Deprecated
public class Bundle {
  private static final Logger LOGGER = LoggerFactory.getLogger(Bundle.class);
  private final Composite delegate;

  /**
   * Creates a new Bundle with the specified identifier in the given environment.
   *
   * @param bundleId The unique identifier for this bundle
   * @param environment The environment in which this bundle operates
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#Composite(String, Environment)} instead.
   */
  @Deprecated
  public Bundle(String bundleId, Environment environment) {
    LOGGER.warn("The Bundle class is deprecated. Use Composite instead.");
    this.delegate = new Composite(bundleId, environment);
  }

  /**
   * Adds a tube to this bundle.
   *
   * @param name The name to reference this tube by within the bundle
   * @param tube The tube to add
   * @return This bundle instance for method chaining
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#addTube(String, Tube)} instead.
   */
  @Deprecated
  public Bundle addTube(String name, Tube tube) {
    delegate.addTube(name, tube);
    return this;
  }

  /**
   * Creates a new tube and adds it to this bundle.
   *
   * @param name The name to reference this tube by within the bundle
   * @param reason The reason for creating this tube
   * @return This bundle instance for method chaining
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#createTube(String, String)} instead.
   */
  @Deprecated
  public Bundle createTube(String name, String reason) {
    delegate.createTube(name, reason);
    return this;
  }

  /**
   * Connects two tubes in the bundle to allow data flow.
   *
   * @param sourceName The name of the source tube
   * @param targetName The name of the target tube
   * @return This bundle instance for method chaining
   * @throws IllegalArgumentException if either tube name doesn't exist in the bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#connect(String, String)} instead.
   */
  @Deprecated
  public Bundle connect(String sourceName, String targetName) {
    delegate.connect(sourceName, targetName);
    return this;
  }

  /**
   * Adds a transformer function to a tube in the bundle.
   *
   * @param tubeName The name of the tube to add the transformer to
   * @param transformer The transformer function
   * @param <T> The input and output type of the transformer
   * @return This bundle instance for method chaining
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#addTransformer(String, Function)} instead.
   */
  @Deprecated
  public <T> Bundle addTransformer(String tubeName, Function<T, T> transformer) {
    delegate.addTransformer(tubeName, transformer);
    return this;
  }

  /**
   * Adds a validator function to a tube in the bundle.
   *
   * @param tubeName The name of the tube to add the validator to
   * @param validator The validator function returning true for valid data
   * @param <T> The type of data to validate
   * @return This bundle instance for method chaining
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#addValidator(String, Function)} instead.
   */
  @Deprecated
  public <T> Bundle addValidator(String tubeName, Function<T, Boolean> validator) {
    delegate.addValidator(tubeName, validator);
    return this;
  }

  /**
   * Enables circuit breaker for a tube to handle failures gracefully.
   *
   * @param tubeName The name of the tube to add circuit breaker to
   * @param failureThreshold Number of failures before tripping
   * @param resetTimeoutMs Reset timeout in milliseconds
   * @return This bundle instance for method chaining
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#enableCircuitBreaker(String, int, long)} instead.
   */
  @Deprecated
  public Bundle enableCircuitBreaker(String tubeName, int failureThreshold, long resetTimeoutMs) {
    delegate.enableCircuitBreaker(tubeName, failureThreshold, resetTimeoutMs);
    return this;
  }

  /**
   * Processes data through the bundle, starting at the specified entry tube.
   *
   * @param entryPoint The name of the tube to start processing from
   * @param data The data to process
   * @param <T> The type of the data
   * @return Optional containing the processed result, or empty if processing failed
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#process(String, Object)} instead.
   */
  @Deprecated
  public <T> Optional<T> process(String entryPoint, T data) {
    return delegate.process(entryPoint, data);
  }

  /**
   * Gets all event logs from this bundle.
   *
   * @return An unmodifiable list of all events logged by this bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getEventLog()} instead.
   */
  @Deprecated
  public List<BundleEvent> getEventLog() {
    List<org.samstraumr.tube.composite.Composite.CompositeEvent> compositeEvents = delegate.getEventLog();
    List<BundleEvent> bundleEvents = new ArrayList<>();
    
    for (org.samstraumr.tube.composite.Composite.CompositeEvent event : compositeEvents) {
      bundleEvents.add(new BundleEvent(event.getDescription(), event.getTimestamp()));
    }
    
    return Collections.unmodifiableList(bundleEvents);
  }
  
  /**
   * BundleEvent proxy class for backward compatibility.
   *
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite.CompositeEvent} instead.
   */
  @Deprecated
  public static class BundleEvent {
    private final String description;
    private final long timestamp;
    
    public BundleEvent(String description, long timestamp) {
      this.description = description;
      this.timestamp = timestamp;
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

  /**
   * Checks if the bundle is currently active.
   *
   * @return true if the bundle is active, false otherwise
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#isActive()} instead.
   */
  @Deprecated
  public boolean isActive() {
    return delegate.isActive();
  }

  /**
   * Deactivates the bundle, preventing further data processing.
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#deactivate()} instead.
   */
  @Deprecated
  public void deactivate() {
    delegate.deactivate();
  }

  /**
   * Reactivates the bundle to allow data processing.
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#activate()} instead.
   */
  @Deprecated
  public void activate() {
    delegate.activate();
  }

  /**
   * Gets the bundle's unique identifier.
   *
   * @return The bundle ID
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getCompositeId()} instead.
   */
  @Deprecated
  public String getBundleId() {
    return delegate.getCompositeId();
  }

  /**
   * Gets a tube from the bundle by name.
   *
   * @param name The name of the tube to retrieve
   * @return The requested tube
   * @throws IllegalArgumentException if the tube doesn't exist
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getTube(String)} instead.
   */
  @Deprecated
  public Tube getTube(String name) {
    return delegate.getTube(name);
  }

  /**
   * Gets all tubes in this bundle.
   *
   * @return An unmodifiable map of tube names to tubes
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getTubes()} instead.
   */
  @Deprecated
  public Map<String, Tube> getTubes() {
    return delegate.getTubes();
  }

  /**
   * Gets all connections in this bundle.
   *
   * @return An unmodifiable map of source tube names to lists of target tube names
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getConnections()} instead.
   */
  @Deprecated
  public Map<String, List<String>> getConnections() {
    return delegate.getConnections();
  }

  /**
   * Gets all circuit breakers in this bundle.
   *
   * @return An unmodifiable map of tube names to circuit breakers
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#getCircuitBreakers()} instead.
   */
  @Deprecated
  public Map<String, org.samstraumr.tube.composite.Composite.CircuitBreaker> getCircuitBreakers() {
    return delegate.getCircuitBreakers();
  }
  
  /**
   * CircuitBreaker proxy class for backward compatibility.
   *
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite.CircuitBreaker} instead.
   */
  @Deprecated
  public static class CircuitBreaker {
    private final org.samstraumr.tube.composite.Composite.CircuitBreaker delegate;
    
    public CircuitBreaker(String tubeName, int failureThreshold, long resetTimeoutMs) {
      this.delegate = new org.samstraumr.tube.composite.Composite.CircuitBreaker(
          tubeName, failureThreshold, resetTimeoutMs);
    }
    
    public void recordFailure() {
      delegate.recordFailure();
    }
    
    public void reset() {
      delegate.reset();
    }
    
    public boolean isOpen() {
      return delegate.isOpen();
    }
    
    public String getTubeName() {
      return delegate.getTubeName();
    }
    
    public int getFailureCount() {
      return delegate.getFailureCount();
    }
  }

  /**
   * Logs an event in the bundle's event log.
   *
   * @param description The description of the event
   * @deprecated Use {@link org.samstraumr.tube.composite.Composite#logEvent(String)} instead.
   */
  @Deprecated
  public void logEvent(String description) {
    delegate.logEvent(description);
  }

  /**
   * Gets the delegate Composite instance.
   *
   * @return The delegate Composite instance
   */
  public Composite getDelegate() {
    return delegate;
  }
}