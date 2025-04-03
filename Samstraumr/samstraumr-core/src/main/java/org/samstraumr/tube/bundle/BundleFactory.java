package org.samstraumr.tube.bundle;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.samstraumr.tube.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(BundleFactory.class);
  private static final Map<String, Bundle> BUNDLE_REGISTRY = new ConcurrentHashMap<>();

  private BundleFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a new Bundle with a generated ID.
   *
   * @param environment The environment for the bundle
   * @return The created bundle
   */
  public static Bundle createBundle(Environment environment) {
    String bundleId = generateBundleId();
    return createBundle(bundleId, environment);
  }

  /**
   * Creates a new Bundle with the specified ID.
   *
   * @param bundleId The ID for the bundle
   * @param environment The environment for the bundle
   * @return The created bundle
   */
  public static Bundle createBundle(String bundleId, Environment environment) {
    Bundle bundle = new Bundle(bundleId, environment);
    BUNDLE_REGISTRY.put(bundleId, bundle);
    LOGGER.info("Created and registered bundle: {}", bundleId);
    return bundle;
  }

  /**
   * Creates a linear transformation bundle with source, transformer, and sink tubes.
   *
   * @param environment The environment for the bundle
   * @return The created transformation bundle
   */
  public static Bundle createTransformationBundle(Environment environment) {
    Bundle bundle = createBundle("transformation-" + generateBundleId(), environment);

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
   */
  public static Bundle createValidationBundle(Environment environment) {
    Bundle bundle = createBundle("validation-" + generateBundleId(), environment);

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
   */
  public static Bundle createProcessingBundle(Environment environment) {
    Bundle bundle = createBundle("processing-" + generateBundleId(), environment);

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
   */
  public static Bundle createObserverBundle(Environment environment) {
    Bundle bundle = createBundle("observer-" + generateBundleId(), environment);

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
          // Add to the bundle event log for test verification
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
   */
  public static Optional<Bundle> getBundle(String bundleId) {
    return Optional.ofNullable(BUNDLE_REGISTRY.get(bundleId));
  }

  /**
   * Gets all registered bundles.
   *
   * @return An unmodifiable map of bundle IDs to bundles
   */
  public static Map<String, Bundle> getAllBundles() {
    return Collections.unmodifiableMap(BUNDLE_REGISTRY);
  }

  /**
   * Removes a bundle from the registry.
   *
   * @param bundleId The ID of the bundle to remove
   * @return true if the bundle was removed, false if it wasn't found
   */
  public static boolean removeBundle(String bundleId) {
    Bundle bundle = BUNDLE_REGISTRY.remove(bundleId);
    if (bundle != null) {
      bundle.deactivate();
      LOGGER.info("Removed bundle from registry: {}", bundleId);
      return true;
    }
    return false;
  }

  /**
   * Generates a unique bundle ID.
   *
   * @return A unique bundle ID
   */
  private static String generateBundleId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
