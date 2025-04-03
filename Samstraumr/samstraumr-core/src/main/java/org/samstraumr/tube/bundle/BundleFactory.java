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
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link org.samstraumr.tube.composite.CompositeFactory} instead.
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
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createComposite(environment);
    return new Bundle(composite.getCompositeId(), environment);
  }

  /**
   * Creates a new Bundle with the specified ID.
   *
   * @param bundleId The ID for the bundle
   * @param environment The environment for the bundle
   * @return The created bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createComposite(String, Environment)} instead.
   */
  @Deprecated
  public static Bundle createBundle(String bundleId, Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createComposite(bundleId, environment);
    return new Bundle(bundleId, environment);
  }

  /**
   * Creates a linear transformation bundle with source, transformer, and sink tubes.
   *
   * @param environment The environment for the bundle
   * @return The created transformation bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createTransformationComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createTransformationBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createTransformationComposite(environment);
    return new Bundle(composite.getCompositeId(), environment);
  }

  /**
   * Creates a validation bundle with processor, validator, and output tubes.
   *
   * @param environment The environment for the bundle
   * @return The created validation bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createValidationComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createValidationBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createValidationComposite(environment);
    return new Bundle(composite.getCompositeId(), environment);
  }

  /**
   * Creates a standard processing bundle with input, processing stages, and output.
   *
   * @param environment The environment for the bundle
   * @return The created processing bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createProcessingComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createProcessingBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createProcessingComposite(environment);
    return new Bundle(composite.getCompositeId(), environment);
  }

  /**
   * Creates an observer bundle with source and observer tubes.
   *
   * @param environment The environment for the bundle
   * @return The created observer bundle
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#createObserverComposite(Environment)} instead.
   */
  @Deprecated
  public static Bundle createObserverBundle(Environment environment) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Composite composite = CompositeFactory.createObserverComposite(environment);
    return new Bundle(composite.getCompositeId(), environment);
  }

  /**
   * Gets a bundle by its ID.
   *
   * @param bundleId The ID of the bundle to retrieve
   * @return Optional containing the bundle if found, empty otherwise
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#getComposite(String)} instead.
   */
  @Deprecated
  public static Optional<Bundle> getBundle(String bundleId) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Optional<Composite> composite = CompositeFactory.getComposite(bundleId);
    if (composite.isPresent()) {
      Environment env = new Environment(); // Need to create a new environment as we don't have the original
      return Optional.of(new Bundle(bundleId, env));
    }
    return Optional.empty();
  }

  /**
   * Gets all registered bundles.
   *
   * @return An unmodifiable map of bundle IDs to bundles
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#getAllComposites()} instead.
   */
  @Deprecated
  public static Map<String, Bundle> getAllBundles() {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    Map<String, Composite> composites = CompositeFactory.getAllComposites();
    Map<String, Bundle> bundles = new HashMap<>();
    
    Environment env = new Environment(); // Need to create a new environment as we don't have the original
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
   * @deprecated Use {@link org.samstraumr.tube.composite.CompositeFactory#removeComposite(String)} instead.
   */
  @Deprecated
  public static boolean removeBundle(String bundleId) {
    LOGGER.warn("BundleFactory is deprecated. Use CompositeFactory instead.");
    return CompositeFactory.removeComposite(bundleId);
  }
}