package org.samstraumr.tube.lifecycle.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all lifecycle step definitions providing common functionality.
 *
 * <p>This class contains shared methods and fields used across different step definition classes,
 * providing a consistent approach to testing tube lifecycle features.
 */
public abstract class BaseLifecycleSteps {
  protected static final Logger LOGGER = LoggerFactory.getLogger(BaseLifecycleSteps.class);

  // Test context fields - accessible to all derived step classes
  protected Tube testTube;
  protected Environment environment;
  protected Exception exceptionThrown;
  protected Map<String, Object> testContext;

  /** Constructor initializes the shared test context. */
  public BaseLifecycleSteps() {
    this.testContext = new HashMap<>();
    LOGGER.info("BaseLifecycleSteps initialized");
  }

  /**
   * Prepares a standard environment for testing.
   *
   * @return An initialized Environment instance
   * @throws AssertionError if environment creation fails
   */
  protected Environment prepareEnvironment() {
    try {
      Environment env = new Environment();
      assertNotNull(env, "Environment should be initialized");
      LOGGER.info("Test environment initialized successfully");
      return env;
    } catch (Exception e) {
      LOGGER.error("Failed to initialize Environment", e);
      fail("Failed to initialize Environment: " + e.getMessage());
      return null; // Unreachable but needed for compilation
    }
  }

  /**
   * Creates a tube with the specified reason.
   *
   * @param reason The reason for creating the tube
   * @param env The environment in which to create the tube
   * @return The created tube
   */
  protected Tube createTube(String reason, Environment env) {
    try {
      Tube tube = Tube.create(reason, env);
      assertNotNull(tube, "Tube should be created successfully");
      LOGGER.info("Created test tube with reason: {}", reason);
      return tube;
    } catch (Exception e) {
      LOGGER.error("Failed to create tube: {}", e.getMessage(), e);
      exceptionThrown = e;
      return null;
    }
  }

  /**
   * Stores an object in the test context with the specified key.
   *
   * @param key The key under which to store the object
   * @param value The object to store
   */
  protected void storeInContext(String key, Object value) {
    testContext.put(key, value);
  }

  /**
   * Retrieves an object from the test context with the specified key.
   *
   * @param <T> The type of the object to retrieve
   * @param key The key under which the object is stored
   * @param clazz The class of the object
   * @return The object from the context, or null if not found
   */
  @SuppressWarnings("unchecked")
  protected <T> T getFromContext(String key, Class<T> clazz) {
    Object value = testContext.get(key);
    if (value != null && clazz.isInstance(value)) {
      return (T) value;
    }
    return null;
  }

  /** Asserts that the test tube has a valid unique identifier. */
  protected void assertTubeHasValidId() {
    assertNotNull(testTube, "Test tube should not be null");
    assertNotNull(testTube.getUniqueId(), "Tube should have a unique ID");
    assertTrue(testTube.getUniqueId().length() > 0, "Tube ID should not be empty");
  }
}
