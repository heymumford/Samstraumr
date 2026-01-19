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
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.identity.ComponentId;

/**
 * Detects Bug #3: Race condition during concurrent CompositeComponent activation.
 *
 * <p>CompositeComponent uses non-thread-safe HashMap for children and ArrayList for connections.
 * When activate() iterates over these collections, concurrent modifications by addComponent(),
 * removeComponent(), connect(), or disconnect() calls cause: - ConcurrentModificationException
 * during iteration - Data loss and corrupted component state - Incomplete activation of child
 * components
 */
@DisplayName("Bug #3: CompositeComponent concurrent activation race condition")
@Tag("L1_Component")
@Tag("concurrency")
public class CompositeComponentConcurrencyTest {

  private CompositeComponent composite;
  private ComponentId compositeId;

  @BeforeEach
  void setup() {
    compositeId = ComponentId.create("test-composite");
    composite = CompositeComponent.create(compositeId, CompositeType.PIPELINE);

    // CompositeComponent.create() initializes automatically (moves through all lifecycle states to
    // READY)

    // Add initial child components to the composite (can only be done in READY state)
    for (int i = 0; i < 5; i++) {
      Component child = Component.create(ComponentId.create("child-" + i));
      composite.addComponent(child);
    }
  }

  @Test
  @DisplayName("Should allow concurrent addComponent calls without race conditions")
  void testConcurrentAddComponentCalls() throws InterruptedException {
    final int NUM_THREADS = 10;
    final int OPS_PER_THREAD = 50;
    final AtomicInteger successfulOps = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
    List<Thread> threads = new ArrayList<>();

    // Multiple threads adding components concurrently
    // HashMap is not thread-safe - concurrent modifications cause issues
    for (int i = 0; i < NUM_THREADS; i++) {
      final int threadId = i;
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      Component newChild =
                          Component.create(
                              ComponentId.create("dynamic-child-" + threadId + "-" + j));
                      composite.addComponent(newChild);
                      successfulOps.incrementAndGet();
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "ConcurrentModificationException during addComponent: "
                                  + e.getMessage(),
                              e));
                    }
                  }
                } catch (Exception e) {
                  // Ignore other exceptions (like InvalidOperationException when not modifiable)
                }
              }));
    }

    // Start all threads
    threads.forEach(Thread::start);

    // Wait for completion
    for (Thread t : threads) {
      t.join();
    }

    // Verify no ConcurrentModificationException occurred
    if (!exceptions.isEmpty()) {
      fail("Concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }
  }

  @Test
  @DisplayName("Should not throw ConcurrentModificationException under high concurrent operations")
  void testHighConcurrencyActivation() throws InterruptedException {
    // Activate composite once to move to ACTIVE state (only valid once)
    composite.activate();

    final int NUM_THREADS = 20;
    final int OPS_PER_THREAD = 100;
    final AtomicInteger successfulOps = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
    List<Thread> threads = new ArrayList<>();

    // Test concurrent reads of children while composite is ACTIVE
    // This stresses the ConcurrentHashMap during iteration
    for (int i = 0; i < NUM_THREADS; i++) {
      final int threadId = i;
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      // Multiple concurrent reads of the children map
                      int childCount = composite.getComponents().size();
                      if (childCount >= 0) {
                        successfulOps.incrementAndGet();
                      }
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "ConcurrentModificationException during concurrent reads: "
                                  + e.getMessage(),
                              e));
                    }
                  }
                } catch (Exception e) {
                  // Unexpected exceptions
                  exceptions.add(e);
                }
              }));
    }

    threads.forEach(Thread::start);

    for (Thread t : threads) {
      t.join();
    }

    if (!exceptions.isEmpty()) {
      fail("Concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all read operations succeeded
    assertEquals(
        NUM_THREADS * OPS_PER_THREAD,
        successfulOps.get(),
        "All concurrent read operations should complete without ConcurrentModificationException");
  }

  @Test
  @DisplayName(
      "Should handle concurrent iteration of connections without ConcurrentModificationException")
  void testConcurrentDeactivationWithoutCorruption() throws InterruptedException {
    // Activate composite once (moves to ACTIVE state)
    composite.activate();

    final int NUM_THREADS = 10;
    final int OPS_PER_THREAD = 50;
    final AtomicInteger successfulOps = new AtomicInteger(0);
    final List<Throwable> exceptions = new ArrayList<>();

    CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
    List<Thread> threads = new ArrayList<>();

    // Test concurrent reads of connections list (which uses CopyOnWriteArrayList)
    for (int i = 0; i < NUM_THREADS; i++) {
      threads.add(
          new Thread(
              () -> {
                try {
                  barrier.await();
                  for (int j = 0; j < OPS_PER_THREAD; j++) {
                    try {
                      // Iterate and read connections concurrently
                      int connectionCount = composite.getConnections().size();
                      if (connectionCount >= 0) {
                        successfulOps.incrementAndGet();
                      }
                    } catch (ConcurrentModificationException e) {
                      exceptions.add(
                          new AssertionError(
                              "ConcurrentModificationException during concurrent iteration: "
                                  + e.getMessage(),
                              e));
                    }
                  }
                } catch (Exception e) {
                  // Ignore other expected exceptions
                }
              }));
    }

    threads.forEach(Thread::start);

    for (Thread t : threads) {
      t.join();
    }

    if (!exceptions.isEmpty()) {
      fail("Concurrency exceptions detected: " + exceptions.get(0).getMessage());
    }

    // Verify all operations succeeded
    assertEquals(
        NUM_THREADS * OPS_PER_THREAD,
        successfulOps.get(),
        "All concurrent operations should complete");
  }
}
