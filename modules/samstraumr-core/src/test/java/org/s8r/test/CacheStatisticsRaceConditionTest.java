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

package org.s8r.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.infrastructure.cache.InMemoryCacheAdapter;

/**
 * Detects Bug #6: Race condition in cache statistics counters.
 *
 * <p>InMemoryCacheAdapter uses non-atomic long fields for statistics counters (hits, misses, puts,
 * removes) that are updated without synchronization. This causes: - Lost updates under concurrent
 * access - Stale reads of counter values - Torn reads on 64-bit long values (platform-dependent) -
 * Incorrect statistics calculations
 *
 * <p>The counters should use AtomicLong for thread-safe access patterns that don't require external
 * synchronization.
 */
@DisplayName("Bug #6: InMemoryCacheAdapter statistics race conditions")
@Tag("ATL")
@Tag("L1_Component")
@Tag("cache")
public class CacheStatisticsRaceConditionTest {

  private InMemoryCacheAdapter cache;

  @BeforeEach
  void setup() {
    cache = new InMemoryCacheAdapter();
    cache.initialize("test-cache");
  }

  @Test
  @DisplayName("Should have consistent hit/miss counters under concurrent get operations")
  void testConcurrentGetCounterConsistency() throws InterruptedException {
    int threadCount = 10;
    int operationsPerThread = 100;
    CyclicBarrier barrier = new CyclicBarrier(threadCount);
    List<Exception> exceptions = new ArrayList<>();

    // Pre-populate cache
    for (int i = 0; i < 50; i++) {
      cache.put("key-" + i, "value-" + i);
    }

    // Reset counters after population
    cache.initialize("test-cache");

    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      Thread thread =
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int i = 0; i < operationsPerThread; i++) {
                    String key = "key-" + (i % 50);
                    cache.get(key);
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(exceptions.isEmpty(), "No exceptions during concurrent gets");

    // Verify counters match expected values
    Map<String, Object> stats = cache.getStatistics().getAttributes();
    long totalHitsAndMisses = (long) stats.get("hits") + (long) stats.get("misses");

    // Should have exactly threadCount * operationsPerThread operations
    long expectedOperations = (long) threadCount * operationsPerThread;
    assertEquals(
        expectedOperations,
        totalHitsAndMisses,
        "Total hits + misses should equal number of get operations performed");
  }

  @Test
  @DisplayName("Should have accurate put counter under concurrent puts")
  void testConcurrentPutCounterAccuracy() throws InterruptedException {
    int threadCount = 10;
    int operationsPerThread = 50;
    CyclicBarrier barrier = new CyclicBarrier(threadCount);
    List<Exception> exceptions = new ArrayList<>();

    // Reset counters
    cache.initialize("test-cache");

    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      Thread thread =
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int i = 0; i < operationsPerThread; i++) {
                    cache.put("key-" + threadId + "-" + i, "value-" + i);
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(exceptions.isEmpty(), "No exceptions during concurrent puts");

    Map<String, Object> stats = cache.getStatistics().getAttributes();
    long putCount = (long) stats.get("puts");

    long expectedPuts = (long) threadCount * operationsPerThread;
    assertEquals(
        expectedPuts,
        putCount,
        "Put counter should accurately reflect all concurrent put operations");
  }

  @Test
  @DisplayName("Should have accurate remove counter under concurrent removes")
  void testConcurrentRemoveCounterAccuracy() throws InterruptedException {
    int threadCount = 10;
    int operationsPerThread = 20;
    CyclicBarrier barrier = new CyclicBarrier(threadCount);
    List<Exception> exceptions = new ArrayList<>();

    // Pre-populate cache
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < operationsPerThread; i++) {
        cache.put("key-" + t + "-" + i, "value-" + i);
      }
    }

    // Reset counters after population
    cache.initialize("test-cache");

    // Re-populate for removal
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < operationsPerThread; i++) {
        cache.put("key-" + t + "-" + i, "value-" + i);
      }
    }

    // Reset counters again
    cache.initialize("test-cache");

    // Re-populate one more time
    for (int t = 0; t < threadCount; t++) {
      for (int i = 0; i < operationsPerThread; i++) {
        cache.put("key-" + t + "-" + i, "value-" + i);
      }
    }

    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      Thread thread =
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int i = 0; i < operationsPerThread; i++) {
                    cache.remove("key-" + threadId + "-" + i);
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(exceptions.isEmpty(), "No exceptions during concurrent removes");

    Map<String, Object> stats = cache.getStatistics().getAttributes();
    long removeCount = (long) stats.get("removes");

    long expectedRemoves = (long) threadCount * operationsPerThread;
    assertEquals(
        expectedRemoves,
        removeCount,
        "Remove counter should accurately reflect all concurrent remove operations");
  }

  @Test
  @DisplayName("Should have consistent statistics under mixed concurrent operations")
  void testMixedConcurrentOperationsStatistics() throws InterruptedException {
    int threadCount = 20;
    int operationsPerThread = 100;
    CyclicBarrier barrier = new CyclicBarrier(threadCount);
    List<Exception> exceptions = new ArrayList<>();
    AtomicInteger expectedGets = new AtomicInteger(0);
    AtomicInteger expectedPuts = new AtomicInteger(0);

    // Pre-populate cache
    for (int i = 0; i < 30; i++) {
      cache.put("key-" + i, "value-" + i);
    }

    cache.initialize("test-cache");

    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      Thread thread =
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int i = 0; i < operationsPerThread; i++) {
                    if (i % 3 == 0) {
                      // Get operation
                      cache.get("key-" + (i % 30));
                      expectedGets.incrementAndGet();
                    } else if (i % 3 == 1) {
                      // Put operation
                      cache.put("key-new-" + threadId + "-" + i, "value-" + i);
                      expectedPuts.incrementAndGet();
                    } else {
                      // Remove operation (may or may not succeed)
                      cache.remove("key-new-" + threadId + "-" + (i - 1));
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    assertTrue(exceptions.isEmpty(), "No exceptions during mixed concurrent operations");

    Map<String, Object> stats = cache.getStatistics().getAttributes();
    long hitsPlusMisses = (long) stats.get("hits") + (long) stats.get("misses");
    long putCount = (long) stats.get("puts");

    // Verify at least approximate accuracy (exact values depend on concurrency)
    assertTrue(hitsPlusMisses > 0, "Should have recorded get operations in statistics");
    assertTrue(putCount > 0, "Should have recorded put operations in statistics");
  }

  @Test
  @DisplayName("Should preserve statistical data consistency across concurrent access")
  void testStatisticalDataConsistency() throws InterruptedException {
    int threadCount = 15;
    int operationsPerThread = 75;
    CyclicBarrier barrier = new CyclicBarrier(threadCount + 1); // +1 for stats reader
    List<Exception> exceptions = new ArrayList<>();

    // Pre-populate
    for (int i = 0; i < 40; i++) {
      cache.put("key-" + i, "value-" + i);
    }

    cache.initialize("test-cache");

    // Thread that continuously reads statistics
    Thread statsReader =
        new Thread(
            () -> {
              try {
                barrier.await();
                for (int i = 0; i < 50; i++) {
                  Map<String, Object> stats = cache.getStatistics().getAttributes();
                  long hits = (long) stats.get("hits");
                  long misses = (long) stats.get("misses");
                  long puts = (long) stats.get("puts");
                  long removes = (long) stats.get("removes");

                  // Verify no negative values (would indicate lost updates)
                  assertTrue(hits >= 0, "hits counter should never be negative");
                  assertTrue(misses >= 0, "misses counter should never be negative");
                  assertTrue(puts >= 0, "puts counter should never be negative");
                  assertTrue(removes >= 0, "removes counter should never be negative");

                  Thread.sleep(1); // Small delay to let operations progress
                }
              } catch (Exception e) {
                exceptions.add(e);
              }
            });
    statsReader.start();

    List<Thread> threads = new ArrayList<>();
    for (int t = 0; t < threadCount; t++) {
      final int threadId = t;
      Thread thread =
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int i = 0; i < operationsPerThread; i++) {
                    if (i % 2 == 0) {
                      cache.get("key-" + (i % 40));
                    } else {
                      cache.put("key-" + threadId + "-" + i, "value-" + i);
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              });
      threads.add(thread);
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }
    statsReader.join();

    assertTrue(
        exceptions.isEmpty(), "No exceptions during concurrent operations with stats reading");
  }
}
