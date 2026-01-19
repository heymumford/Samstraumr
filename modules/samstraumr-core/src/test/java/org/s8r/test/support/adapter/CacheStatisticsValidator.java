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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates cache statistics accuracy under concurrent load.
 *
 * <p>Detects Bug #6: race conditions in statistics counters when multiple threads perform cache
 * operations concurrently.
 *
 * <p>Validates:
 * - Hit/miss counters match expected operations
 * - Counters are internally consistent
 * - No overflow/underflow
 * - Accuracy within tolerance (for eventually-consistent stats)
 *
 * <p>Usage: Run cache operations concurrently, then validate statistics.
 */
public class CacheStatisticsValidator {
  private long expectedHits = 0;
  private long expectedMisses = 0;
  private long expectedEvictions = 0;
  private long tolerance = 0; // Default: exact count required

  private CacheStatisticsValidator() {
    // Use factory method
  }

  /**
   * Creates validator for cache statistics.
   *
   * @return New validator
   */
  public static CacheStatisticsValidator forCacheStatistics() {
    return new CacheStatisticsValidator();
  }

  /**
   * Sets expected hit count.
   *
   * @param hits Expected number of cache hits
   * @return This validator (for chaining)
   */
  public CacheStatisticsValidator expectingHits(long hits) {
    this.expectedHits = hits;
    return this;
  }

  /**
   * Sets expected miss count.
   *
   * @param misses Expected number of cache misses
   * @return This validator (for chaining)
   */
  public CacheStatisticsValidator expectingMisses(long misses) {
    this.expectedMisses = misses;
    return this;
  }

  /**
   * Sets expected eviction count.
   *
   * @param evictions Expected number of evictions
   * @return This validator (for chaining)
   */
  public CacheStatisticsValidator expectingEvictions(long evictions) {
    this.expectedEvictions = evictions;
    return this;
  }

  /**
   * Sets tolerance for stats accuracy (for eventually-consistent systems).
   *
   * <p>Useful for high-contention scenarios where race conditions might cause minor
   * inaccuracies.
   *
   * @param tolerancePercent Tolerance percentage (0.0-100.0)
   * @return This validator (for chaining)
   */
  public CacheStatisticsValidator withTolerance(double tolerancePercent) {
    double percent = tolerancePercent / 100.0;
    this.tolerance = Math.max(
        1, // Minimum tolerance of 1
        (long) Math.ceil(Math.max(expectedHits, Math.max(expectedMisses, expectedEvictions)) * percent));
    return this;
  }

  /**
   * Validates actual statistics against expectations.
   *
   * @param actualHits Actual hit count
   * @param actualMisses Actual miss count
   * @param actualEvictions Actual eviction count
   * @throws AssertionError if statistics out of tolerance (detects Bug #6)
   */
  public void validate(long actualHits, long actualMisses, long actualEvictions) {
    validateHits(actualHits);
    validateMisses(actualMisses);
    validateEvictions(actualEvictions);
    validateConsistency(actualHits, actualMisses, actualEvictions);
  }

  /**
   * Validates hit count accuracy.
   *
   * @param actualHits Actual hit count
   * @throws AssertionError if outside tolerance
   */
  public void validateHits(long actualHits) {
    long delta = Math.abs(actualHits - expectedHits);
    assertTrue(
        delta <= tolerance,
        String.format(
            "Hit count mismatch (Bug #6 indicator): expected %d, got %d (delta %d, tolerance %d)",
            expectedHits, actualHits, delta, tolerance));
  }

  /**
   * Validates miss count accuracy.
   *
   * @param actualMisses Actual miss count
   * @throws AssertionError if outside tolerance
   */
  public void validateMisses(long actualMisses) {
    long delta = Math.abs(actualMisses - expectedMisses);
    assertTrue(
        delta <= tolerance,
        String.format(
            "Miss count mismatch (Bug #6 indicator): expected %d, got %d (delta %d, tolerance %d)",
            expectedMisses, actualMisses, delta, tolerance));
  }

  /**
   * Validates eviction count accuracy.
   *
   * @param actualEvictions Actual eviction count
   * @throws AssertionError if outside tolerance
   */
  public void validateEvictions(long actualEvictions) {
    long delta = Math.abs(actualEvictions - expectedEvictions);
    assertTrue(
        delta <= tolerance,
        String.format(
            "Eviction count mismatch: expected %d, got %d (delta %d, tolerance %d)",
            expectedEvictions, actualEvictions, delta, tolerance));
  }

  /**
   * Validates internal consistency of statistics.
   *
   * <p>Checks:
   * - Counters >= 0 (no underflow)
   * - Total operations reasonable
   * - No overflow indicators
   *
   * @param hits Hit count
   * @param misses Miss count
   * @param evictions Eviction count
   * @throws AssertionError if inconsistency detected
   */
  public void validateConsistency(long hits, long misses, long evictions) {
    assertTrue(hits >= 0, "Hit count negative: " + hits);
    assertTrue(misses >= 0, "Miss count negative: " + misses);
    assertTrue(evictions >= 0, "Eviction count negative: " + evictions);

    long totalOps = hits + misses;
    assertTrue(
        totalOps > 0,
        "No cache operations recorded (total: " + totalOps + ")");

    // Sanity check: evictions shouldn't exceed misses (they're subset of misses)
    assertTrue(
        evictions <= misses,
        String.format("Evictions (%d) exceed misses (%d) - data inconsistency", evictions, misses));
  }

  /**
   * Calculates hit rate percentage.
   *
   * @param actualHits Hit count
   * @param actualMisses Miss count
   * @return Hit rate as percentage (0.0-100.0)
   */
  public double calculateHitRate(long actualHits, long actualMisses) {
    long total = actualHits + actualMisses;
    if (total == 0) {
      return 0.0;
    }
    return (100.0 * actualHits) / total;
  }

  /**
   * Generates validation report.
   *
   * @param actualHits Actual hit count
   * @param actualMisses Actual miss count
   * @param actualEvictions Actual eviction count
   * @return Human-readable report
   */
  public String generateReport(long actualHits, long actualMisses, long actualEvictions) {
    return String.format(
        "Cache Statistics Report\n"
            + "=======================\n"
            + "Hits: %d (expected %d, delta %d)\n"
            + "Misses: %d (expected %d, delta %d)\n"
            + "Evictions: %d (expected %d, delta %d)\n"
            + "Hit Rate: %.1f%%\n"
            + "Tolerance: %d\n",
        actualHits,
        expectedHits,
        Math.abs(actualHits - expectedHits),
        actualMisses,
        expectedMisses,
        Math.abs(actualMisses - expectedMisses),
        actualEvictions,
        expectedEvictions,
        Math.abs(actualEvictions - expectedEvictions),
        calculateHitRate(actualHits, actualMisses),
        tolerance);
  }

  /**
   * Quick validation helper for exact counts (no tolerance).
   *
   * @param actualHits Actual hits
   * @param actualMisses Actual misses
   * @param actualEvictions Actual evictions
   */
  public void validateExact(long actualHits, long actualMisses, long actualEvictions) {
    assertEquals(
        expectedHits,
        actualHits,
        "Hit count mismatch");
    assertEquals(
        expectedMisses,
        actualMisses,
        "Miss count mismatch");
    assertEquals(
        expectedEvictions,
        actualEvictions,
        "Eviction count mismatch");
  }
}
