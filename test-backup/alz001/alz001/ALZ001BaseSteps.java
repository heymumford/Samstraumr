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

package org.s8r.test.steps.alz001;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Assertions;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.steps.alz001.base.ALZ001TestContext;


/**
 * Base class for all ALZ001 step definitions.
 * 
 * <p>Provides shared test context, setup and teardown methods, and utility methods
 * for ALZ001 test scenarios.
 */
public class ALZ001BaseSteps {
  
  /**
   * Thread-safe test context for storing objects between steps.
   * This is a shared resource that all step definition classes can use.
   */
  protected final ALZ001TestContext context = new ALZ001TestContext();
  
  /**
   * Logger for test execution.
   */
  protected final ConsoleLogger logger = new ConsoleLogger("ALZ001Test");
  
  /**
   * Sets up the test context with default configuration values.
   * Called before each test scenario to ensure a clean state.
   */
  protected void setUp() {
    // Clear previous test context
    context.clear();
    
    // Create and store default configuration values
    Map<String, Object> testConfig = new HashMap<>();
    testConfig.put("simulation_timestep_ms", 100);
    testConfig.put("protein_expression_interval_ms", 500);
    testConfig.put("neuronal_update_interval_ms", 250);
    testConfig.put("environmental_update_interval_ms", 1000);
    testConfig.put("prediction_window_ms", 10000);
    testConfig.put("default_tau_threshold", 120.0);
    testConfig.put("default_amyloid_threshold", 800.0);
    testConfig.put("default_connectivity_threshold", 0.7);
    testConfig.put("default_oxidative_stress_baseline", 10.0);
    testConfig.put("default_inflammatory_response_baseline", 5.0);
    testConfig.put("default_cognitive_reserve", 85.0);
    testConfig.put("default_confidence_threshold", 0.8);
    
    // Store the configuration in the context
    context.store("testConfig", testConfig);
  }
  
  /**
   * Cleans up the test context after test execution.
   * Called after each test scenario to ensure proper cleanup.
   */
  protected void tearDown() {
    context.clear();
  }
  
  /**
   * Gets a configuration value from the test configuration.
   *
   * @param <T> The type of configuration value
   * @param key The configuration key
   * @return The configuration value, cast to the expected type
   */
  @SuppressWarnings("unchecked")
  protected <T> T getConfigValue(String key) {
    Map<String, Object> testConfig = context.retrieve("testConfig");
    return (T) testConfig.get(key);
  }
  
  /**
   * Sets a configuration value in the test configuration.
   *
   * @param key The configuration key
   * @param value The configuration value
   */
  protected void setConfigValue(String key, Object value) {
    Map<String, Object> testConfig = context.retrieve("testConfig");
    testConfig.put(key, value);
  }
  
  /**
   * Logs an informational message.
   *
   * @param message The message to log
   */
  protected void logInfo(String message) {
    logger.info(message);
  }
  
  /**
   * Logs a warning message.
   *
   * @param message The message to log
   */
  protected void logWarning(String message) {
    logger.warn(message);
  }
  
  /**
   * Logs an error message.
   *
   * @param message The message to log
   */
  protected void logError(String message) {
    logger.error(message);
  }
  
  /**
   * Asserts that a value is within the specified range.
   *
   * @param message The assertion message
   * @param value The value to test
   * @param min The minimum acceptable value (inclusive)
   * @param max The maximum acceptable value (inclusive)
   */
  protected void assertInRange(String message, double value, double min, double max) {
    Assertions.assertTrue(
        value >= min && value <= max,
        message + " - Expected value between " + min + " and " + max + ", but got " + value);
  }
  
  /**
   * Asserts that the actual state matches the expected state.
   *
   * @param expectedState The expected state
   * @param actualState The actual state
   */
  protected void assertStateEquals(String expectedState, String actualState) {
    Assertions.assertEquals(expectedState, actualState, "State should match expected value");
  }
  
  /**
   * Mock class representing a protein expression measurement.
   */
  protected static class ProteinMeasurement {
    private String proteinType;
    private double value;
    private String unit;
    private double normalRangeMin;
    private double normalRangeMax;
    
    public ProteinMeasurement(String proteinType, double value, String unit, 
                              double normalRangeMin, double normalRangeMax) {
      this.proteinType = proteinType;
      this.value = value;
      this.unit = unit;
      this.normalRangeMin = normalRangeMin;
      this.normalRangeMax = normalRangeMax;
    }
    
    public String getProteinType() {
      return proteinType;
    }
    
    public double getValue() {
      return value;
    }
    
    public String getUnit() {
      return unit;
    }
    
    public double getNormalRangeMin() {
      return normalRangeMin;
    }
    
    public double getNormalRangeMax() {
      return normalRangeMax;
    }
    
    public boolean isInNormalRange() {
      return value >= normalRangeMin && value <= normalRangeMax;
    }
  }
  
  /**
   * Mock class representing a time series data point.
   */
  protected static class TimeSeriesDataPoint {
    private long timestamp;
    private double value;
    private String label;
    
    public TimeSeriesDataPoint(long timestamp, double value, String label) {
      this.timestamp = timestamp;
      this.value = value;
      this.label = label;
    }
    
    public long getTimestamp() {
      return timestamp;
    }
    
    public double getValue() {
      return value;
    }
    
    public String getLabel() {
      return label;
    }
  }
}