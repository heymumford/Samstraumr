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

package org.s8r.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.pattern.AggregatorComponent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Detects Bug #1: Race condition in AggregatorComponent buffer management.
 *
 * <p>When multiple threads call processData() concurrently, the ArrayList values in
 * aggregationBuffer are not thread-safe. This causes: - ConcurrentModificationException during
 * iteration - Data loss due to unsynchronized modifications - Corruption of aggregation state
 */
@DisplayName("Bug #1: AggregatorComponent concurrent buffer race condition")
@Tag("ATL")
@Tag("L1_Component")
@Tag("concurrency")
public class AggregatorComponentConcurrencyTest {

  private AggregatorComponent aggregator;
  private ComponentId sourceComponentId;

  @BeforeEach
  void setup() {
    // Create component ID for test events
    sourceComponentId = ComponentId.create("test-aggregator-source");

    // Create count-based aggregator with low threshold to trigger publication
    aggregator =
        AggregatorComponent.createCountBased(
            "test-aggregator", "input-channel", "output-channel", 5);

    // Add simple sum aggregator
    aggregator.addAggregator(
        "value",
        (a, b) -> {
          if (a instanceof Integer && b instanceof Integer) {
            return (Integer) a + (Integer) b;
          }
          return b;
        });
  }

  @Test
  @DisplayName("Should not lose data under concurrent aggregation (Bug #1 indicator)")
  void testConcurrentDataProcessingDoesNotLoseData() throws InterruptedException {
    final int THREAD_COUNT = 10;
    final int ITEMS_PER_THREAD = 200;
    final AtomicInteger successfulOperations = new AtomicInteger(0);
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
    List<Thread> threads = new ArrayList<>();

    // Create threads
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await(); // Synchronize all threads to start concurrently

                  for (int j = 0; j < ITEMS_PER_THREAD; j++) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("value", 1);
                    data.put("item", "item-" + j);

                    try {
                      aggregator.processData(
                          new ComponentDataEvent(sourceComponentId, "input-channel", data));
                      successfulOperations.incrementAndGet();
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError("Race condition detected: " + e.getMessage(), e));
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);

    // Wait for all threads to complete
    for (Thread t : threads) {
      t.join();
    }

    // Verify no exceptions occurred
    if (!exceptions.isEmpty()) {
      fail("Concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all operations completed
    int expectedOperations = THREAD_COUNT * ITEMS_PER_THREAD;
    assertEquals(
        expectedOperations,
        successfulOperations.get(),
        "Data loss detected: expected " + expectedOperations + " operations");
  }

  @Test
  @DisplayName("Should handle rapid concurrent calls to processData without corruption")
  void testRapidConcurrentProcessDataCalls() throws InterruptedException {
    final int THREAD_COUNT = 20;
    final int OPS_PER_THREAD = 100;
    final AtomicInteger successfulOperations = new AtomicInteger(0);
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
    List<Thread> threads = new ArrayList<>();

    // Create threads
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();

                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      Map<String, Object> data = new HashMap<>();
                      data.put("value", Math.random());
                      data.put("timestamp", System.nanoTime());

                      aggregator.processData(
                          new ComponentDataEvent(sourceComponentId, "input-channel", data));
                      successfulOperations.incrementAndGet();
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "Race condition detected (ConcurrentModificationException)", e));
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);

    // Wait for all threads to complete
    for (Thread t : threads) {
      t.join();
    }

    // Verify no exceptions
    if (!exceptions.isEmpty()) {
      fail("Exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all operations completed
    assertEquals(
        THREAD_COUNT * OPS_PER_THREAD,
        successfulOperations.get(),
        "Not all operations completed successfully");
  }

  @Test
  @DisplayName(
      "Should maintain data integrity during concurrent processData and triggerAggregation")
  void testConcurrentProcessAndAggregation() throws InterruptedException {
    final int THREAD_COUNT = 15;
    final int OPS_PER_THREAD = 50;
    final AtomicInteger processedItems = new AtomicInteger(0);
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
    List<Thread> threads = new ArrayList<>();

    // Create threads
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await(); // Synchronize all threads to start concurrently

                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      // Mix of processData and triggerAggregation calls
                      if (processedItems.get() % 2 == 0) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("value", 1);
                        aggregator.processData(
                            new ComponentDataEvent(sourceComponentId, "input-channel", data));
                      } else {
                        aggregator.triggerAggregation();
                      }
                      processedItems.incrementAndGet();
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "Race condition during concurrent process/aggregate: "
                                  + e.getMessage(),
                              e));
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);

    // Wait for all threads to complete
    for (Thread t : threads) {
      t.join();
    }

    // Verify no exceptions occurred
    if (!exceptions.isEmpty()) {
      fail("Concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify operations completed
    assertEquals(
        THREAD_COUNT * OPS_PER_THREAD,
        processedItems.get(),
        "Not all process/aggregate operations completed");
  }

  @Test
  @DisplayName("Should not throw ConcurrentModificationException under high concurrency")
  void testHighConcurrencyStressTest() throws InterruptedException {
    final int THREAD_COUNT = 50;
    final int OPS_PER_THREAD = 200;
    final AtomicInteger itemsAdded = new AtomicInteger(0);
    final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
    List<Thread> threads = new ArrayList<>();

    // Create threads: 50 threads × 200 ops = 10,000 concurrent operations
    for (int i = 0; i < THREAD_COUNT; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await(); // Synchronize all threads to start concurrently

                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      Map<String, Object> data = new HashMap<>();
                      data.put("value", 1);
                      aggregator.processData(
                          new ComponentDataEvent(sourceComponentId, "input-channel", data));
                      itemsAdded.incrementAndGet();
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "ConcurrentModificationException under high concurrency (Bug #1): "
                                  + e.getMessage(),
                              e));
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);

    // Wait for all threads to complete
    for (Thread t : threads) {
      t.join();
    }

    // Verify no exceptions occurred
    if (!exceptions.isEmpty()) {
      fail("High concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all operations completed
    assertEquals(
        THREAD_COUNT * OPS_PER_THREAD,
        itemsAdded.get(),
        "High concurrency stress test failed: not all operations completed");
  }
}
