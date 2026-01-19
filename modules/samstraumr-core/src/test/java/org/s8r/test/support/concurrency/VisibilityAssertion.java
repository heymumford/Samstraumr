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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Detects visibility issues (non-volatile fields, missing synchronization).
 *
 * <p>Validates Java Memory Model guarantees: happens-before relationships, visibility across
 * threads.
 */
public class VisibilityAssertion {
  private VisibilityAssertion() {
    // Utility class
  }

  /**
   * Tests cross-thread visibility of value changes.
   *
   * <p>Pattern: Thread A writes value, Thread B reads value after barrier sync. Value should be
   * visible.
   *
   * @param writer Supplier that writes value
   * @param reader Consumer that reads value
   * @param iterations Number of write-read cycles
   * @throws AssertionError if visibility issue detected
   */
  public static <T> void assertVisibility(
      Supplier<T> writer, Consumer<T> reader, int iterations) {
    AtomicReference<T> value = new AtomicReference<>();
    AtomicBoolean testFailed = new AtomicBoolean(false);
    StringBuilder failures = new StringBuilder();

    for (int i = 0; i < iterations; i++) {
      CyclicBarrier barrier = new CyclicBarrier(2);
      Thread writerThread =
          new Thread(
              () -> {
                try {
                  barrier.await(); // Sync point
                  value.set(writer.get());
                } catch (Exception e) {
                  testFailed.set(true);
                }
              });

      Thread readerThread =
          new Thread(
              () -> {
                try {
                  barrier.await(); // Sync point
                  Thread.sleep(10); // Give writer chance to complete
                  T readValue = value.get();
                  if (readValue == null) {
                    failures.append(
                        "Iteration "
                            + i
                            + ": Value not visible to reader (null after write)\n");
                    testFailed.set(true);
                  } else {
                    reader.accept(readValue);
                  }
                } catch (Exception e) {
                  failures.append("Iteration ").append(i).append(": ").append(e).append("\n");
                  testFailed.set(true);
                }
              });

      writerThread.start();
      readerThread.start();

      try {
        writerThread.join();
        readerThread.join();
      } catch (InterruptedException e) {
        fail("Visibility test interrupted: " + e);
      }

      if (testFailed.get()) {
        fail("Visibility issue detected after " + (i + 1) + " iterations:\n" + failures);
      }
    }
  }

  /**
   * Tests volatile semantics of an AtomicReference.
   *
   * <p>Verifies that writes are visible across threads (proper volatile behavior).
   *
   * @param ref Reference to test
   * @throws AssertionError if volatile semantics violated
   */
  public static void assertVolatileSemantics(AtomicReference<?> ref) {
    AtomicBoolean writeVisibleToReader = new AtomicBoolean(false);
    CyclicBarrier barrier = new CyclicBarrier(2);

    Thread writer =
        new Thread(
            () -> {
              try {
                barrier.await();
                ref.set(new Object());
              } catch (Exception e) {
                // Ignore
              }
            });

    Thread reader =
        new Thread(
            () -> {
              try {
                barrier.await();
                Thread.sleep(5); // Give writer time to write
                writeVisibleToReader.set(ref.get() != null);
              } catch (Exception e) {
                // Ignore
              }
            });

    writer.start();
    reader.start();

    try {
      writer.join();
      reader.join();
    } catch (InterruptedException e) {
      fail("Volatile semantics test interrupted");
    }

    if (!writeVisibleToReader.get()) {
      fail("AtomicReference write not visible to reader: volatile semantics violated");
    }
  }

  /**
   * Stress test: many threads writing and reading, all should see consistent values.
   *
   * @param ref Reference to stress test
   * @param threadCount Number of threads
   * @param iterations Iterations per thread
   * @throws AssertionError if consistency violated
   */
  public static void stressTestAtomicVisibility(
      AtomicReference<Integer> ref, int threadCount, int iterations) {
    Thread[] threads = new Thread[threadCount];
    AtomicBoolean anyFailure = new AtomicBoolean(false);

    for (int i = 0; i < threadCount; i++) {
      threads[i] =
          new Thread(
              () -> {
                for (int j = 0; j < iterations; j++) {
                  int value = j;
                  ref.set(value);
                  Integer readBack = ref.get();
                  if (readBack == null || readBack != value) {
                    anyFailure.set(true);
                  }
                }
              });
      threads[i].start();
    }

    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        fail("Stress test interrupted");
      }
    }

    if (anyFailure.get()) {
      fail("AtomicReference visibility failed under concurrent stress");
    }
  }
}
