/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.bundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.composite.Composite;
import org.samstraumr.tube.composite.CompositeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated This class is deprecated and will be removed in a future release. Use {@link
 *     org.samstraumr.tube.composite.CompositeFactory} instead.
 */
@Deprecated
public class BundleFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(BundleFactory.class);

  private BundleFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a new Bundle with a generated ID.
   *
   * @param environment The environment for the bundle
   * @return The created bundle
   * @deprecated Use {@link
   *     org.samstraumr.tube.composite.CompositeFactory#createComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createComposite(environment);
    Bundle bundle = new Bundle(composite.getCompositeId(), environment);
    return bundle;
  }

  /**
   * Creates a new Bundle with the specified ID.
   *
   * @param bundleId The ID for the bundle
   * @param environment The environment for the bundle
   * @return The created bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createComposite(String,
   *     Environment)} instead.
   */
  @Deprecated
  public static Bundle createBundle(String bundleId, Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    // We need to call this but we don't need the result
    if (bundleId != null) {
      CompositeFactory.createComposite(bundleId, environment);
    }
    Bundle bundle = new Bundle(bundleId, environment);
    return bundle;
  }

  /**
   * Creates a linear transformation bundle with source, transformer, and sink tubes.
   *
   * @param environment The environment for the bundle
   * @return The created transformation bundle
   * @deprecated Use {@link
   *     org.samstraumr.tube.composite.CompositeFactory#createTransformationComposite(Environment)}
   *     instead.
   */
  @Deprecated
  public static Bundle createTransformationBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");

    // Create a new Bundle directly with the transformation structure
    Bundle bundle = new Bundle("transformation-" + System.currentTimeMillis(), environment);

    // Add tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("transformer", "Transformer Tube")
        .createTube("sink", "Sink Tube");

    // Connect tubes
    bundle.connect("source", "transformer").connect("transformer", "sink");

    LOGGER.info("Created transformation bundle: {}", bundle.getBundleId());
    return bundle;
  }

  /**
   * Creates a validation bundle with processor, validator, and output tubes.
   *
   * @param environment The environment for the bundle
   * @return The created validation bundle
   * @deprecated Use {@link
   *     org.samstraumr.tube.composite.CompositeFactory#createValidationComposite(Environment)}
   *     instead.
   */
  @Deprecated
  public static Bundle createValidationBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");

    // Create a new Bundle directly with the validation structure
    Bundle bundle = new Bundle("validation-" + System.currentTimeMillis(), environment);

    // Add tubes
    bundle
        .createTube("processor", "Processor Tube")
        .createTube("validator", "Validator Tube")
        .createTube("output", "Output Tube");

    // Connect tubes
    bundle.connect("processor", "validator").connect("validator", "output");

    // Enable circuit breaker on validator
    bundle.enableCircuitBreaker("validator", 3, 5000);

    LOGGER.info("Created validation bundle: {}", bundle.getBundleId());
    return bundle;
  }

  /**
   * Creates a standard processing bundle with input, processing stages, and output.
   *
   * @param environment The environment for the bundle
   * @return The created processing bundle
   * @deprecated Use {@link
   *     org.samstraumr.tube.composite.CompositeFactory#createProcessingComposite(Environment)}
   *     instead.
   */
  @Deprecated
  public static Bundle createProcessingBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");

    // Create a new Bundle directly with the processing structure
    Bundle bundle = new Bundle("processing-" + System.currentTimeMillis(), environment);

    // Add tubes
    bundle
        .createTube("input", "Input Tube")
        .createTube("parser", "Parser Tube")
        .createTube("validator", "Validator Tube")
        .createTube("processor", "Processor Tube")
        .createTube("formatter", "Formatter Tube")
        .createTube("output", "Output Tube");

    // Connect tubes in sequence
    bundle
        .connect("input", "parser")
        .connect("parser", "validator")
        .connect("validator", "processor")
        .connect("processor", "formatter")
        .connect("formatter", "output");

    // Enable circuit breakers
    bundle.enableCircuitBreaker("parser", 2, 10000).enableCircuitBreaker("processor", 3, 15000);

    LOGGER.info("Created standard processing bundle: {}", bundle.getBundleId());
    return bundle;
  }

  /**
   * Creates an observer bundle with source and observer tubes.
   *
   * @param environment The environment for the bundle
   * @return The created observer bundle
   * @deprecated Use {@link
   *     org.samstraumr.tube.composite.CompositeFactory#createObserverComposite(Environment)}
   *     instead.
   */
  @Deprecated
  public static Bundle createObserverBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");

    // Create a new Bundle directly with the observer structure
    Bundle bundle = new Bundle("observer-" + System.currentTimeMillis(), environment);

    // Add tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("observer", "Observer Tube")
        .createTube("output", "Output Tube");

    // Connect tubes - observer is connected to source to monitor signals
    bundle.connect("source", "observer").connect("observer", "output");

    // Configure observer tube to just pass through data (monitoring only)
    bundle.addTransformer(
        "observer",
        data -> {
          // Observer pattern just logs the data without modifying it
          LOGGER.info("Observer tube observed: {}", data);
          bundle.logEvent("Observer observed data: " + data);
          return data;
        });

    LOGGER.info("Created observer bundle: {}", bundle.getBundleId());
    return bundle;
  }

  /**
   * Gets a bundle by its ID.
   *
   * @param bundleId The ID of the bundle to retrieve
   * @return Optional containing the bundle if found, empty otherwise
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#getComposite(String)}
   *     instead.
   */
  @Deprecated
  public static Optional<Bundle> getBundle(String bundleId) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Optional<Composite> composite = CompositeFactory.getComposite(bundleId);
    if (composite.isPresent()) {
      Environment env =
          new Environment(); // Need to create a new environment as we don't have the original
      return Optional.of(new Bundle(bundleId, env));
    }
    return Optional.empty();
  }

  /**
   * Gets all registered bundles.
   *
   * @return An unmodifiable map of bundle IDs to bundles
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#getAllComposites()}
   *     instead.
   */
  @Deprecated
  public static Map<String, Bundle> getAllBundles() {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Map<String, Composite> composites = CompositeFactory.getAllComposites();
    Map<String, Bundle> bundles = new HashMap<>();

    Environment env =
        new Environment(); // Need to create a new environment as we don't have the original
    for (Map.Entry<String, Composite> entry : composites.entrySet()) {
      bundles.put(entry.getKey(), new Bundle(entry.getKey(), env));
    }

    return Collections.unmodifiableMap(bundles);
  }

  /**
   * Removes a bundle from the registry.
   *
   * @param bundleId The ID of the bundle to remove
   * @return true if the bundle was removed, false if it wasn't found
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#removeComposite(String)}
   *     instead.
   */
  @Deprecated
  public static boolean removeBundle(String bundleId) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    return CompositeFactory.removeComposite(bundleId);
  }
}
