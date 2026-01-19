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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.s8r.domain.component.Component;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.test.support.concurrency.ConcurrentTestFixture;

/**
 * Expresses lifecycle state machine tests as scenarios.
 *
 * <p>Captures "valid transition path" intent clearly.
 *
 * <p>Example: Component transitions CONCEPTION → INITIALIZING → CONFIGURING → ... → ACTIVE
 */
public class LifecycleScenario {
  private final List<LifecycleState> transitionPath = new ArrayList<>();
  private final Component subject;

  private LifecycleScenario(Component component) {
    this.subject = component;
  }

  /**
   * Creates scenario for given component.
   *
   * @param component Component to test
   * @return New scenario
   */
  public static LifecycleScenario forComponent(Component component) {
    return new LifecycleScenario(component);
  }

  /**
   * Adds transition to path.
   *
   * @param state Target state
   * @return This scenario (for chaining)
   */
  public LifecycleScenario thenTransitionTo(LifecycleState state) {
    transitionPath.add(state);
    return this;
  }

  /**
   * Executes transition path, validating each step.
   *
   * @throws AssertionError if any transition fails or state doesn't match
   */
  public void executeAndValidate() {
    for (LifecycleState state : transitionPath) {
      subject.transitionTo(state);
      assertEquals(
          state,
          subject.getLifecycleState(),
          "Expected state " + state + " but got " + subject.getLifecycleState());
    }
  }

  /**
   * Executes transition path under concurrent pressure.
   *
   * @param fixture Concurrent test fixture
   * @throws InterruptedException if concurrent execution interrupted
   */
  public void executeAndValidateWithConcurrentPressure(ConcurrentTestFixture fixture)
      throws InterruptedException {
    // Execute transitions on main thread
    for (LifecycleState state : transitionPath) {
      subject.transitionTo(state);
      assertEquals(state, subject.getLifecycleState());
    }

    // Concurrent pressure: try to transition to invalid states while in final state
    LifecycleState finalState = subject.getLifecycleState();
    fixture.executeOperation(
        () -> {
          try {
            // Try to transition to invalid state
            subject.transitionTo(LifecycleState.CONCEPTION);
          } catch (Exception e) {
            // Expected: transition should fail
          }
        });

    fixture.awaitCompletion(java.time.Duration.ofSeconds(5));
    // Verify still in final state
    assertEquals(finalState, subject.getLifecycleState());
  }

  /**
   * Gets the transition path.
   *
   * @return Path taken
   */
  public List<LifecycleState> getTransitionPath() {
    return new ArrayList<>(transitionPath);
  }

  /**
   * Gets subject component.
   *
   * @return Component being tested
   */
  public Component getSubject() {
    return subject;
  }
}
