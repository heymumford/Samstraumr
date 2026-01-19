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

package org.s8r.test.support.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Preconfigured test fixtures and test data generators for adapter testing.
 *
 * <p>Provides:
 * - Common adapter test scenarios
 * - Test data generators for different adapter types
 * - Configuration templates
 * - Fixture builders following builder pattern
 */
public class AdapterTestFixtures {
  private AdapterTestFixtures() {
    // Utility class
  }

  /**
   * Creates builder for adapter configuration.
   *
   * @return New configuration builder
   */
  public static AdapterConfigBuilder adapterConfig() {
    return new AdapterConfigBuilder();
  }

  /**
   * Builder for adapter test configuration.
   */
  public static class AdapterConfigBuilder {
    private final Map<String, String> properties = new HashMap<>();
    private boolean enableCaching = false;
    private int concurrencyLevel = 1;

    /**
     * Sets adapter property.
     *
     * @param key Property key
     * @param value Property value
     * @return This builder (for chaining)
     */
    public AdapterConfigBuilder withProperty(String key, String value) {
      properties.put(key, value);
      return this;
    }

    /**
     * Enables caching in adapter.
     *
     * @return This builder (for chaining)
     */
    public AdapterConfigBuilder withCaching() {
      this.enableCaching = true;
      return this;
    }

    /**
     * Sets concurrency level for adapter.
     *
     * @param level Concurrency level (1 = single-threaded, N = N concurrent threads)
     * @return This builder (for chaining)
     */
    public AdapterConfigBuilder withConcurrencyLevel(int level) {
      this.concurrencyLevel = level;
      return this;
    }

    /**
     * Builds configuration.
     *
     * @return Immutable configuration map
     */
    public Map<String, Object> build() {
      Map<String, Object> config = new HashMap<>(properties);
      config.put("caching.enabled", enableCaching);
      config.put("concurrency.level", concurrencyLevel);
      return config;
    }
  }

  /**
   * Standard test data for cache adapter testing.
   */
  public static class CacheTestData {
    public static final String KEY_1 = "cache-key-1";
    public static final String KEY_2 = "cache-key-2";
    public static final String KEY_3 = "cache-key-3";

    public static final String VALUE_1 = "cache-value-1";
    public static final String VALUE_2 = "cache-value-2";
    public static final String VALUE_3 = "cache-value-3";

    private CacheTestData() {
      // Constant class
    }

    /**
     * Gets test data entries.
     *
     * @return Map of test key-value pairs
     */
    public static Map<String, String> getTestEntries() {
      Map<String, String> entries = new HashMap<>();
      entries.put(KEY_1, VALUE_1);
      entries.put(KEY_2, VALUE_2);
      entries.put(KEY_3, VALUE_3);
      return entries;
    }
  }

  /**
   * Standard test data for persistence adapter testing.
   */
  public static class PersistenceTestData {
    public static final String RECORD_ID_1 = "record-001";
    public static final String RECORD_ID_2 = "record-002";
    public static final String RECORD_ID_3 = "record-003";

    public static final String TABLE_NAME = "test_table";
    public static final String DATABASE_NAME = "test_db";

    private PersistenceTestData() {
      // Constant class
    }

    /**
     * Gets sample record data.
     *
     * @return Map of record ID to record data
     */
    public static Map<String, Map<String, Object>> getSampleRecords() {
      Map<String, Map<String, Object>> records = new HashMap<>();

      Map<String, Object> record1 = new HashMap<>();
      record1.put("id", RECORD_ID_1);
      record1.put("name", "Test Record 1");
      record1.put("value", 100);
      records.put(RECORD_ID_1, record1);

      Map<String, Object> record2 = new HashMap<>();
      record2.put("id", RECORD_ID_2);
      record2.put("name", "Test Record 2");
      record2.put("value", 200);
      records.put(RECORD_ID_2, record2);

      Map<String, Object> record3 = new HashMap<>();
      record3.put("id", RECORD_ID_3);
      record3.put("name", "Test Record 3");
      record3.put("value", 300);
      records.put(RECORD_ID_3, record3);

      return records;
    }
  }

  /**
   * Standard test data for event adapter testing.
   */
  public static class EventTestData {
    public static final String EVENT_TYPE_1 = "component-started";
    public static final String EVENT_TYPE_2 = "component-failed";
    public static final String EVENT_TYPE_3 = "data-aggregated";

    public static final String EVENT_SOURCE_1 = "component-adapter";
    public static final String EVENT_SOURCE_2 = "persistence-adapter";

    private EventTestData() {
      // Constant class
    }

    /**
     * Gets sample event data.
     *
     * @return Map of event type to event data
     */
    public static Map<String, Map<String, Object>> getSampleEvents() {
      Map<String, Map<String, Object>> events = new HashMap<>();

      Map<String, Object> event1 = new HashMap<>();
      event1.put("type", EVENT_TYPE_1);
      event1.put("source", EVENT_SOURCE_1);
      event1.put("timestamp", System.currentTimeMillis());
      events.put(EVENT_TYPE_1, event1);

      Map<String, Object> event2 = new HashMap<>();
      event2.put("type", EVENT_TYPE_2);
      event2.put("source", EVENT_SOURCE_1);
      event2.put("timestamp", System.currentTimeMillis());
      events.put(EVENT_TYPE_2, event2);

      Map<String, Object> event3 = new HashMap<>();
      event3.put("type", EVENT_TYPE_3);
      event3.put("source", EVENT_SOURCE_2);
      event3.put("timestamp", System.currentTimeMillis());
      events.put(EVENT_TYPE_3, event3);

      return events;
    }
  }

  /**
   * Creates concurrent adapter stress test configuration.
   *
   * @param threadCount Number of concurrent threads
   * @param operationsPerThread Operations per thread
   * @return Configuration for stress testing
   */
  public static Map<String, Object> concurrentStressTestConfig(
      int threadCount, int operationsPerThread) {
    Map<String, Object> config = new HashMap<>();
    config.put("test.concurrency.threads", threadCount);
    config.put("test.concurrency.operations_per_thread", operationsPerThread);
    config.put("test.concurrency.total_operations", threadCount * operationsPerThread);
    return config;
  }

  /**
   * Creates failure injection configuration for resilience testing.
   *
   * @param failureRatePercent Percentage of operations that should fail (0-100)
   * @return Configuration for failure testing
   */
  public static Map<String, Object> failureInjectionConfig(double failureRatePercent) {
    Map<String, Object> config = new HashMap<>();
    config.put("test.failure_injection.enabled", true);
    config.put("test.failure_injection.rate_percent", failureRatePercent);
    return config;
  }

  /**
   * Generates test report template.
   *
   * @param adapterName Name of adapter being tested
   * @param testType Type of test (unit, integration, stress, etc.)
   * @return Report template
   */
  public static String generateTestReportTemplate(String adapterName, String testType) {
    return String.format(
        "Adapter Test Report\n"
            + "===================\n"
            + "Adapter: %s\n"
            + "Test Type: %s\n"
            + "Timestamp: %s\n"
            + "Status: [PENDING]\n"
            + "Results: [PENDING]\n",
        adapterName, testType, System.currentTimeMillis());
  }
}
