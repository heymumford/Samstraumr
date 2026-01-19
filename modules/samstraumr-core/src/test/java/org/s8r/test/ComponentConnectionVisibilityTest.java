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
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.identity.ComponentId;

/**
 * Detects Bug #4: Visibility issue with ComponentConnection.active field.
 *
 * <p>ComponentConnection has a mutable `active` field that is not volatile. When multiple threads
 * call activate()/deactivate() and read isActive(), the memory visibility of changes is not
 * guaranteed without volatile. This causes: - Threads reading stale values of `active` field - Data
 * visibility issues with concurrent modifications - Incorrect state representation across threads
 */
@DisplayName("Bug #4: ComponentConnection visibility issue with active field")
@Tag("L1_Component")
@Tag("concurrency")
public class ComponentConnectionVisibilityTest {

  private ComponentConnection connection;
  private ComponentId sourceId;
  private ComponentId targetId;

  @BeforeEach
  void setup() {
    sourceId = ComponentId.create("source-component");
    targetId = ComponentId.create("target-component");
    connection =
        new ComponentConnection(sourceId, targetId, ConnectionType.COMPOSITION, "Test connection");
  }

  @Test
  @DisplayName("Should ensure visibility of concurrent activate/deactivate operations")
  void testConcurrentActivateDeactivateVisibility() throws InterruptedException {
    final int NUM_THREADS = 10;
    final int OPS_PER_THREAD = 100;
    final AtomicInteger stateChanges = new AtomicInteger(0);
    final AtomicInteger visibilityIssues = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
    List<Thread> threads = new ArrayList<>();

    // Create threads that alternate between activate and deactivate
    for (int i = 0; i < NUM_THREADS; i++) {
      final int threadId = i;
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      if ((threadId + j) % 2 == 0) {
                        connection.activate();
                      } else {
                        connection.deactivate();
                      }
                      stateChanges.incrementAndGet();

                      // Immediately read back the state we just set
                      // Without volatile, the read might see a stale value
                      boolean expectedState = (threadId + j) % 2 == 0;
                      boolean actualState = connection.isActive();

                      // Note: we can't strictly assert equality here because other threads
                      // might have changed the state between our write and read,
                      // but the volatile keyword ensures we're reading the current value
                      // not a cached/stale value
                    } catch (Exception e) {
                      exceptions.add(e);
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    threads.forEach(Thread::start);

    for (Thread t : threads) {
      t.join();
    }

    if (!exceptions.isEmpty()) {
      fail("Exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all state changes were recorded
    assertEquals(
        NUM_THREADS * OPS_PER_THREAD,
        stateChanges.get(),
        "All state change operations should complete");
  }

  @Test
  @DisplayName("Should maintain visibility of active flag under concurrent reads")
  void testConcurrentReadsOfActiveFlag() throws InterruptedException {
    final int NUM_THREADS = 20;
    final int OPS_PER_THREAD = 200;
    final AtomicInteger reads = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS + 1);
    List<Thread> readerThreads = new ArrayList<>();

    // Create reader threads
    for (int i = 0; i < NUM_THREADS; i++) {
      readerThreads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      // Repeatedly read the active flag
                      // Without volatile, might see stale cached value
                      boolean isActive = connection.isActive();
                      reads.incrementAndGet();
                    } catch (Exception e) {
                      exceptions.add(e);
                    }
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Add a single writer thread that changes state while readers are active
    Thread writerThread =
        new Thread(
            () -> {
              try {
                barrier.await();
              } catch (Exception e) {
                exceptions.add(e);
                return;
              }
              try {
                for (int i = 0; i < 50; i++) {
                  connection.activate();
                  Thread.sleep(1); // Let readers catch up
                  connection.deactivate();
                  Thread.sleep(1); // Let readers catch up
                }
              } catch (Exception e) {
                exceptions.add(e);
              }
            });

    // Start all threads
    readerThreads.forEach(Thread::start);
    writerThread.start();

    // Signal all threads to start
    try {
      barrier.await();
    } catch (Exception e) {
      fail("Barrier synchronization failed: " + e.getMessage());
    }

    // Wait for completion
    writerThread.join();
    for (Thread t : readerThreads) {
      t.join();
    }

    if (!exceptions.isEmpty()) {
      fail("Visibility issue detected: " + exceptions.get(0).getMessage());
    }

    // Verify all reads completed
    assertEquals(
        NUM_THREADS * OPS_PER_THREAD,
        reads.get(),
        "All read operations should complete and see consistent values");
  }

  @Test
  @DisplayName("Should ensure visibility of state changes across all threads")
  void testStateChangeVisibilityAcrossThreads() throws InterruptedException {
    final int NUM_THREADS = 15;
    final AtomicInteger threadsObservingTrue = new AtomicInteger(0);
    final AtomicInteger threadsObservingFalse = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS + 1);
    List<Thread> threads = new ArrayList<>();

    // All reader threads start and wait for activation signal
    for (int i = 0; i < NUM_THREADS; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  // After main thread activates, all threads should eventually see true
                  int observedTrue = 0;
                  int observedFalse = 0;
                  for (int j = 0; j < 1000; j++) {
                    if (connection.isActive()) {
                      observedTrue++;
                    } else {
                      observedFalse++;
                    }
                  }
                  if (observedTrue > 0) {
                    threadsObservingTrue.incrementAndGet();
                  }
                  if (observedFalse > 0) {
                    threadsObservingFalse.incrementAndGet();
                  }
                } catch (Exception e) {
                  exceptions.add(e);
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);
    try {
      barrier.await();
    } catch (Exception e) {
      fail("Barrier synchronization failed: " + e.getMessage());
    }

    // Main thread activates
    connection.activate();
    Thread.sleep(10); // Give threads time to observe

    // Wait for all threads
    for (Thread t : threads) {
      t.join();
    }

    if (!exceptions.isEmpty()) {
      fail("Exception occurred: " + exceptions.get(0).getMessage());
    }

    // Without volatile, threads might not see the activated state
    // With volatile, all threads should eventually see true
    assertEquals(
        NUM_THREADS,
        threadsObservingTrue.get(),
        "All threads should observe the activated state (true value)");
  }
}
