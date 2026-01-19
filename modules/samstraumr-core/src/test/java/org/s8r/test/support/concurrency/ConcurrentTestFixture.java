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

package org.s8r.test.support.concurrency;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Orchestrates coordinated multi-threaded testing with deterministic failure detection.
 *
 * <p>Uses CyclicBarrier to synchronize thread start, CountDownLatch for completion, and collects
 * exceptions from all threads for assertion.
 *
 * <p>Pattern: Arrange → Act → Assert across threads
 */
public class ConcurrentTestFixture {
  private final int threadCount;
  private final int operationsPerThread;
  private final CyclicBarrier startBarrier;
  private final CountDownLatch completionLatch;
  private final List<Throwable> capturedExceptions =
      Collections.synchronizedList(new ArrayList<>());
  private final AtomicInteger successfulOperations = new AtomicInteger(0);
  private final ExecutorService executorService;

  /**
   * Creates a new concurrent test fixture.
   *
   * @param threadCount Number of threads to use
   * @param operationsPerThread Operations per thread
   */
  public ConcurrentTestFixture(int threadCount, int operationsPerThread) {
    this.threadCount = threadCount;
    this.operationsPerThread = operationsPerThread;
    this.startBarrier = new CyclicBarrier(threadCount);
    this.completionLatch = new CountDownLatch(threadCount);
    this.executorService = Executors.newFixedThreadPool(threadCount);
  }

  /**
   * Executes operation with barrier synchronization (all threads start together).
   *
   * @param operation Runnable operation to execute
   */
  public void executeOperationWithBarrier(Runnable operation) throws InterruptedException {
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {
            try {
              // Wait for all threads to be ready
              startBarrier.await();

              // Execute operation operationsPerThread times
              for (int j = 0; j < operationsPerThread; j++) {
                try {
                  operation.run();
                  successfulOperations.incrementAndGet();
                } catch (Throwable e) {
                  capturedExceptions.add(e);
                }
              }
            } catch (Exception e) {
              capturedExceptions.add(e);
            } finally {
              completionLatch.countDown();
            }
          });
    }
  }

  /**
   * Executes operation without barrier (threads start as scheduled).
   *
   * @param operation Runnable operation to execute
   */
  public void executeOperation(Runnable operation) throws InterruptedException {
    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {
            try {
              for (int j = 0; j < operationsPerThread; j++) {
                try {
                  operation.run();
                  successfulOperations.incrementAndGet();
                } catch (Throwable e) {
                  capturedExceptions.add(e);
                }
              }
            } catch (Exception e) {
              capturedExceptions.add(e);
            } finally {
              completionLatch.countDown();
            }
          });
    }
  }

  /**
   * Waits for all operations to complete within timeout.
   *
   * @param timeout Maximum time to wait
   * @throws InterruptedException if waiting is interrupted
   */
  public void awaitCompletion(Duration timeout) throws InterruptedException {
    if (!completionLatch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
      throw new RuntimeException(
          "Operations did not complete within " + timeout.toMillis() + "ms");
    }
    executorService.shutdown();
  }

  /**
   * Returns all captured exceptions from threads.
   *
   * @return List of exceptions
   */
  public List<Throwable> getExceptions() {
    return new ArrayList<>(capturedExceptions);
  }

  /**
   * Asserts that no exceptions were thrown.
   *
   * @throws AssertionError if exceptions were captured
   */
  public void assertNoExceptions() {
    if (!capturedExceptions.isEmpty()) {
      String summary = capturedExceptions.stream()
          .map(t -> t.getClass().getSimpleName() + ": " + t.getMessage())
          .reduce("", (a, b) -> a + "\n" + b);
      throw new AssertionError(
          "Caught " + capturedExceptions.size() + " exceptions:\n" + summary);
    }
  }

  /**
   * Returns number of successful operations completed.
   *
   * @return Successful operation count
   */
  public int getSuccessfulOperations() {
    return successfulOperations.get();
  }

  /**
   * Returns expected total operations (threadCount × operationsPerThread).
   *
   * @return Expected total
   */
  public int getExpectedOperations() {
    return threadCount * operationsPerThread;
  }

  /**
   * Gets number of thread count used.
   *
   * @return Thread count
   */
  public int getThreadCount() {
    return threadCount;
  }

  /**
   * Gets operations per thread.
   *
   * @return Operations per thread
   */
  public int getOperationsPerThread() {
    return operationsPerThread;
  }

  /**
   * Gets exception count.
   *
   * @return Number of exceptions
   */
  public int getExceptionCount() {
    return capturedExceptions.size();
  }
}
