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

/**
 * Express concurrent tests as: precondition → concurrent phase → postcondition.
 *
 * <p>Validates invariants after concurrent execution. Applies Martin Fowler's "properties under
 * load" pattern.
 *
 * <p>Example: "Given component in ACTIVE state, when 20 threads add data, then aggregation
 * buffer size equals total items added (no data loss)"
 *
 * @param <T> Type of subject being tested
 */
public abstract class StressTestScenario<T> {
  /**
   * Sets up precondition: component/state before concurrency.
   *
   * @return Subject to test under concurrent load
   */
  public abstract T setupPrecondition();

  /**
   * Executes concurrent operations on subject.
   *
   * @param subject Subject created by precondition
   * @param fixture Fixture for coordinating threads
   */
  public abstract void concurrentPhase(T subject, ConcurrentTestFixture fixture);

  /**
   * Validates postcondition: invariant check after concurrent phase.
   *
   * @param subject Subject after concurrent operations
   * @throws AssertionError if invariant violated
   */
  public abstract void validatePostcondition(T subject);

  /**
   * Execute full scenario with default parameters.
   *
   * @throws AssertionError if any phase fails
   * @throws InterruptedException if thread execution interrupted
   */
  public void execute() throws InterruptedException {
    execute(10, 100);
  }

  /**
   * Execute full scenario with custom parameters.
   *
   * @param threadCount Number of concurrent threads
   * @param operationsPerThread Operations per thread
   * @throws AssertionError if any phase fails
   * @throws InterruptedException if thread execution interrupted
   */
  public void execute(int threadCount, int operationsPerThread) throws InterruptedException {
    // Setup
    T subject = setupPrecondition();

    // Concurrent phase
    ConcurrentTestFixture fixture = new ConcurrentTestFixture(threadCount, operationsPerThread);
    concurrentPhase(subject, fixture);
    fixture.awaitCompletion(Duration.ofSeconds(30));

    // Validate
    fixture.assertNoExceptions();
    validatePostcondition(subject);
  }

  /**
   * Convenience method: execute and ensure no exceptions.
   *
   * @throws InterruptedException if thread execution interrupted
   */
  public final void executeAndAssertClean() throws InterruptedException {
    execute();
  }
}
