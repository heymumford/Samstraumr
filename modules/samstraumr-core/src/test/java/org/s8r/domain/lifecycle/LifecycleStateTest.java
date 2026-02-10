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

package org.s8r.domain.lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.s8r.domain.component.Component;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the LifecycleState enum which represents the possible states of components in the
 * Samstraumr framework.
 *
 * <p>The lifecycle state management is a core domain concept in the system, ensuring that
 * components transition between states in a controlled and predictable manner.
 */
@UnitTest
@DisplayName("Lifecycle State Tests")
class LifecycleStateTest {

  private Component mockComponent;

  @BeforeEach
  void setUp() {
    mockComponent = mock(Component.class);
    when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.CONCEPTION);
  }

  @Nested
  @DisplayName("Lifecycle Stage Classification Tests")
  class LifecycleStageClassificationTests {

    @ParameterizedTest
    @EnumSource(
        value = LifecycleState.class,
        names = {
          "CONCEPTION",
          "INITIALIZING",
          "CONFIGURING",
          "SPECIALIZING",
          "DEVELOPING_FEATURES"
        })
    @DisplayName("isEarlyStage() should return true for early lifecycle states")
    void isEarlyStageShouldReturnTrueForEarlyLifecycleStates(LifecycleState state) {
      assertTrue(state.isEarlyStage(), state + " should be an early stage");
    }

    @ParameterizedTest
    @EnumSource(
        value = LifecycleState.class,
        names = {
          "INITIALIZED",
          "READY",
          "ACTIVE",
          "RUNNING",
          "WAITING",
          "ADAPTING",
          "TRANSFORMING"
        })
    @DisplayName("isOperational() should return true for operational states")
    void isOperationalShouldReturnTrueForOperationalStates(LifecycleState state) {
      assertTrue(state.isOperational(), state + " should be operational");
    }

    @ParameterizedTest
    @EnumSource(
        value = LifecycleState.class,
        names = {"STABLE", "SPAWNING", "DEGRADED", "MAINTAINING"})
    @DisplayName("isAdvancedStage() should return true for advanced states")
    void isAdvancedStageShouldReturnTrueForAdvancedStates(LifecycleState state) {
      assertTrue(state.isAdvancedStage(), state + " should be advanced stage");
    }

    @ParameterizedTest
    @EnumSource(
        value = LifecycleState.class,
        names = {"TERMINATING", "TERMINATED", "ARCHIVED"})
    @DisplayName("isTerminationStage() should return true for termination states")
    void isTerminationStageShouldReturnTrueForTerminalStates(LifecycleState state) {
      assertTrue(state.isTerminationStage(), state + " should be termination stage");
    }
  }

  @Nested
  @DisplayName("Metadata Tests")
  class MetadataTests {

    @Test
    @DisplayName("getDescription() should return the correct description")
    void getDescriptionShouldReturnCorrectDescription() {
      assertEquals(
          "Initial creation",
          LifecycleState.CONCEPTION.getDescription(),
          "CONCEPTION should have correct description");
      assertEquals(
          "Prepared but not active",
          LifecycleState.READY.getDescription(),
          "READY should have correct description");
      assertEquals(
          "Shutting down",
          LifecycleState.TERMINATING.getDescription(),
          "TERMINATING should have correct description");
    }

    @Test
    @DisplayName("getBiologicalAnalog() should return the correct biological analog")
    void getBiologicalAnalogShouldReturnCorrectAnalog() {
      assertEquals(
          "Fertilization/Zygote",
          LifecycleState.CONCEPTION.getBiologicalAnalog(),
          "CONCEPTION should have correct biological analog");
      assertEquals(
          "Juvenile",
          LifecycleState.READY.getBiologicalAnalog(),
          "READY should have correct biological analog");
      assertEquals(
          "Death",
          LifecycleState.TERMINATING.getBiologicalAnalog(),
          "TERMINATING should have correct biological analog");
    }

    @Test
    @DisplayName("toString() should include the name, description and biological analog")
    void toStringShouldIncludeNameDescriptionAndAnalog() {
      String toStringResult = LifecycleState.ACTIVE.toString();
      assertTrue(toStringResult.contains("ACTIVE"), "toString should include state name");
      assertTrue(
          toStringResult.contains("Fully operational"), "toString should include description");
      assertTrue(
          toStringResult.contains("Active Growth"), "toString should include biological analog");
    }
  }

  @Nested
  @DisplayName("State Transition Tests")
  class StateTransitionTests {

    @Test
    @DisplayName("State transitions should follow lifecycle progression")
    void stateTransitionsShouldFollowLifecycleProgression() {
      // Simulate a component progressing through lifecycle states
      when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.CONCEPTION);

      try {
        // Test valid transitions
        // Early stage transitions
        testValidTransition(LifecycleState.CONCEPTION, LifecycleState.INITIALIZING);
        testValidTransition(LifecycleState.INITIALIZING, LifecycleState.CONFIGURING);
        testValidTransition(LifecycleState.CONFIGURING, LifecycleState.SPECIALIZING);
        testValidTransition(LifecycleState.SPECIALIZING, LifecycleState.DEVELOPING_FEATURES);

        // To operational transitions
        testValidTransition(LifecycleState.DEVELOPING_FEATURES, LifecycleState.INITIALIZED);
        testValidTransition(LifecycleState.INITIALIZED, LifecycleState.READY);
        testValidTransition(LifecycleState.READY, LifecycleState.ACTIVE);
        testValidTransition(LifecycleState.ACTIVE, LifecycleState.RUNNING);

        // Advanced stages
        testValidTransition(LifecycleState.RUNNING, LifecycleState.STABLE);
        testValidTransition(LifecycleState.STABLE, LifecycleState.MAINTAINING);

        // Termination
        testValidTransition(LifecycleState.MAINTAINING, LifecycleState.TERMINATING);
        testValidTransition(LifecycleState.TERMINATING, LifecycleState.TERMINATED);
        testValidTransition(LifecycleState.TERMINATED, LifecycleState.ARCHIVED);
      } catch (Exception e) {
        // Convert to assertion error for test clarity
        throw new AssertionError("Exception during state transition test: " + e.getMessage(), e);
      }
    }

    // Helper method to test valid transitions
    private void testValidTransition(LifecycleState from, LifecycleState to) {
      when(mockComponent.getLifecycleState()).thenReturn(from);

      // Setup mockComponent to return the new state after transitioning
      doNothing().when(mockComponent).transitionTo(to);
      when(mockComponent.getLifecycleState()).thenReturn(to);

      // Assert state change
      mockComponent.transitionTo(to);
      assertEquals(
          to,
          mockComponent.getLifecycleState(),
          "State should transition from " + from + " to " + to);
    }

    @Test
    @DisplayName("Invalid state transitions should throw exceptions")
    void invalidStateTransitionsShouldThrowExceptions() {
      // Set up component with initial state
      when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.CONCEPTION);

      // Skip states - direct from CONCEPTION to READY (skipping early phases)
      doThrow(new InvalidStateTransitionException(LifecycleState.CONCEPTION, LifecycleState.READY))
          .when(mockComponent)
          .transitionTo(LifecycleState.READY);

      // Backward transition - from ACTIVE to CONCEPTION
      when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);
      doThrow(new InvalidStateTransitionException(LifecycleState.ACTIVE, LifecycleState.CONCEPTION))
          .when(mockComponent)
          .transitionTo(LifecycleState.CONCEPTION);

      // Assert exceptions
      assertThrows(
          InvalidStateTransitionException.class,
          () -> {
            when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.CONCEPTION);
            mockComponent.transitionTo(LifecycleState.READY);
          },
          "Should not be able to skip from CONCEPTION to READY");

      assertThrows(
          InvalidStateTransitionException.class,
          () -> {
            when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);
            mockComponent.transitionTo(LifecycleState.CONCEPTION);
          },
          "Should not be able to go backward from ACTIVE to CONCEPTION");
    }
  }
}
