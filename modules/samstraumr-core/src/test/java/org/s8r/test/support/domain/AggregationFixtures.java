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

package org.s8r.test.support.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Preconfigured test fixtures for aggregation scenarios.
 *
 * <p>Provides test data generators and scenarios used in concurrent aggregation testing,
 * particularly for Bug #1 (AggregatorComponent race condition).
 *
 * <p>Fixtures are designed to:
 * - Simulate realistic data workloads
 * - Detect race conditions in concurrent scenarios
 * - Verify no data loss under concurrent aggregation
 */
public class AggregationFixtures {
  private AggregationFixtures() {
    // Utility class
  }

  /**
   * Standard aggregation bucket key for testing.
   */
  public static final String DEFAULT_BUCKET = "test-aggregation-bucket";

  /**
   * Alternative bucket for multi-bucket aggregation tests.
   */
  public static final String ALTERNATE_BUCKET = "alternate-bucket";

  /**
   * Generates deterministic test data.
   *
   * @param count Number of data items to generate
   * @return List of unique test items
   */
  public static List<String> generateTestData(int count) {
    List<String> data = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      data.add("item-" + i);
    }
    return data;
  }

  /**
   * Generates random test data (simulates non-deterministic workload).
   *
   * @param count Number of items to generate
   * @return List of random test items
   */
  public static List<String> generateRandomData(int count) {
    List<String> data = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      data.add("data-" + UUID.randomUUID().toString().substring(0, 8));
    }
    return data;
  }

  /**
   * Creates test scenario for single-bucket aggregation.
   *
   * @return Preconfigured aggregation scenario
   */
  public static AggregationScenario singleBucketScenario() {
    return new AggregationScenario(DEFAULT_BUCKET, 10, 100);
  }

  /**
   * Creates test scenario for multi-bucket aggregation.
   *
   * @return Preconfigured multi-bucket scenario
   */
  public static AggregationScenario multiBucketScenario() {
    return new AggregationScenario(DEFAULT_BUCKET, 5, 50);
  }

  /**
   * Creates high-contention stress test scenario.
   *
   * @return Preconfigured high-stress scenario
   */
  public static AggregationScenario highContentionScenario() {
    return new AggregationScenario(DEFAULT_BUCKET, 20, 1000);
  }

  /**
   * Represents an aggregation test scenario with parameters.
   */
  public static class AggregationScenario {
    private final String bucket;
    private final int threadCount;
    private final int itemsPerThread;

    /**
     * Creates aggregation scenario.
     *
     * @param bucket Aggregation bucket key
     * @param threadCount Number of concurrent threads
     * @param itemsPerThread Items aggregated per thread
     */
    public AggregationScenario(String bucket, int threadCount, int itemsPerThread) {
      this.bucket = bucket;
      this.threadCount = threadCount;
      this.itemsPerThread = itemsPerThread;
    }

    /**
     * Gets aggregation bucket key.
     *
     * @return Bucket identifier
     */
    public String getBucket() {
      return bucket;
    }

    /**
     * Gets number of concurrent threads.
     *
     * @return Thread count
     */
    public int getThreadCount() {
      return threadCount;
    }

    /**
     * Gets items per thread.
     *
     * @return Items per thread
     */
    public int getItemsPerThread() {
      return itemsPerThread;
    }

    /**
     * Gets expected total aggregated items (threadCount × itemsPerThread).
     *
     * @return Total expected items
     */
    public int getExpectedTotalItems() {
      return threadCount * itemsPerThread;
    }

    /**
     * Gets data items for this scenario.
     *
     * @return Test data
     */
    public List<String> getTestData() {
      return generateDeterministicData(getExpectedTotalItems());
    }

    private List<String> generateDeterministicData(int count) {
      List<String> data = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        data.add(bucket + "-item-" + i);
      }
      return data;
    }
  }

  /**
   * Validates aggregation result correctness.
   *
   * <p>Checks:
   * - All items present (no data loss)
   * - No duplicates (except if allowed by scenario)
   * - Count matches expectation
   *
   * @param aggregated Aggregated result
   * @param expected Expected items
   * @throws AssertionError if validation fails
   */
  public static void validateAggregationResult(List<String> aggregated, List<String> expected) {
    if (aggregated.size() != expected.size()) {
      throw new AssertionError(
          "Data loss detected: expected "
              + expected.size()
              + " items, got "
              + aggregated.size());
    }

    for (String item : expected) {
      if (!aggregated.contains(item)) {
        throw new AssertionError("Missing expected item: " + item);
      }
    }
  }

  /**
   * Validates that aggregation buffer is in consistent state (no partial data).
   *
   * @param bufferSize Current buffer size
   * @param expectedSize Expected size
   * @throws AssertionError if buffer state inconsistent
   */
  public static void validateBufferConsistency(int bufferSize, int expectedSize) {
    if (bufferSize != expectedSize) {
      throw new AssertionError(
          "Buffer inconsistent: expected " + expectedSize + ", got " + bufferSize);
    }
  }
}
