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

package org.s8r.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.s8r.component.InvalidStateTransitionException;
import org.s8r.test.annotation.UnitTest;
import org.s8r.test.util.ComponentCompatUtil;

/**
 * Tests for the LifecycleState enum which represents the possible states
 * of components in the Samstraumr framework.
 * 
 * <p>The lifecycle state management is a core domain concept in the system,
 * ensuring that components transition between states in a controlled and
 * predictable manner.
 */
@UnitTest
@DisplayName("Lifecycle State Tests")
class LifecycleStateTest {

    @Nested
    @DisplayName("State Transition Tests")
    class StateTransitionTests {
        
        @ParameterizedTest
        @CsvSource({
            "CONCEPTION, INITIALIZING, true",
            "INITIALIZING, CONFIGURING, true",
            "CONFIGURING, READY, true",
            "READY, ACTIVE, true",
            "ACTIVE, SUSPENDED, true",
            "ACTIVE, TERMINATING, true",
            "TERMINATING, TERMINATED, true",
            "TERMINATED, ARCHIVED, true",
            "CONCEPTION, ACTIVE, false",
            "INITIALIZING, SUSPENDED, false",
            "ACTIVE, ARCHIVED, false",
            "TERMINATED, ACTIVE, false",
            "ARCHIVED, ACTIVE, false"
        })
        @DisplayName("canTransitionTo() should validate allowed transitions")
        void canTransitionToShouldValidateAllowedTransitions(
                String fromStr, String toStr, boolean expected) {
            // Convert string state names to State enum
            State from = ComponentCompatUtil.convertState(fromStr);
            State to = ComponentCompatUtil.convertState(toStr);
            
            // Test transition using Component API
            Component component = Component.createAdam("Test component");
            component.setState(from); // Set initial state
            
            // Check if transition is possible
            boolean canTransition = true;
            try {
                component.setState(to);
            } catch (InvalidStateTransitionException e) {
                canTransition = false;
            }
            
            assertEquals(expected, canTransition, 
                    "Transition from " + from + " to " + to + " should be " + (expected ? "allowed" : "disallowed"));
        }
        
        @Test
        @DisplayName("setState() should perform valid transitions")
        void setStateShouldPerformValidTransitions() {
            // Given
            Component component = Component.createAdam("Test component");
            component.setState(State.CONCEPTION);
            
            // When
            component.setState(State.INITIALIZING);
            
            // Then
            assertEquals(State.INITIALIZING, component.getState(), "Transition should update state");
        }
        
        @Test
        @DisplayName("setState() should throw exception for invalid transitions")
        void setStateShouldThrowExceptionForInvalidTransitions() {
            // Given
            Component component = Component.createAdam("Test component");
            component.setState(State.CONCEPTION);
            
            // When/Then
            assertThrows(InvalidStateTransitionException.class, 
                    () -> component.setState(State.ACTIVE),
                    "Invalid transition should throw exception");
        }
        
        @Test
        @DisplayName("setState() should allow compatible transitions")
        void setStateShouldAllowCompatibleTransitions() {
            // Given
            Component component = Component.createAdam("Test component");
            component.setState(State.ACTIVE);
            
            // When
            component.setState(State.ACTIVE); // Self-transition
            
            // Then
            assertEquals(State.ACTIVE, component.getState(), "Self-transition should be allowed");
        }
    }
    
    @Nested
    @DisplayName("State Property Tests")
    class StatePropertyTests {
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"CONCEPTION", "INITIALIZING", "CONFIGURING"})
        @DisplayName("isOperational() should return false for early lifecycle states")
        void isOperationalShouldReturnFalseForEarlyLifecycleStates(State state) {
            assertFalse(state.isOperational(), state + " should not be operational");
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"READY", "ACTIVE", "SUSPENDED"})
        @DisplayName("isOperational() should return true for operational states")
        void isOperationalShouldReturnTrueForOperationalStates(State state) {
            assertTrue(state.isOperational(), state + " should be operational");
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"TERMINATING", "TERMINATED", "ARCHIVED", "ERROR"})
        @DisplayName("isOperational() should return false for terminal states")
        void isOperationalShouldReturnFalseForTerminalStates(State state) {
            assertFalse(state.isOperational(), state + " should not be operational");
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"CONCEPTION", "INITIALIZING", "CONFIGURING", "READY", "ACTIVE", "SUSPENDED"})
        @DisplayName("isTermination() should return false for non-termination states")
        void isTerminationShouldReturnFalseForNonTerminationStates(State state) {
            assertFalse(state.isTermination(), state + " should not be in termination category");
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"TERMINATING", "TERMINATED", "ARCHIVED"})
        @DisplayName("isTermination() should return true for termination states")
        void isTerminationShouldReturnTrueForTerminationStates(State state) {
            assertTrue(state.isTermination(), state + " should be in termination category");
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, 
                    names = {"CONCEPTION", "INITIALIZING", "CONFIGURING"})
        @DisplayName("isLifecycle() should return true for lifecycle states")
        void isLifecycleShouldReturnTrueForLifecycleStates(State state) {
            assertTrue(state.isLifecycle(), state + " should be in lifecycle category");
        }
    }
    
    @Nested
    @DisplayName("State Comparison Tests")
    class StateComparisonTests {
        
        @Test
        @DisplayName("State ordering should follow lifecycle progression")
        void stateOrderingShouldFollowLifecycleProgression() {
            // Compare various state ordinals to validate progression
            assertTrue(State.CONCEPTION.ordinal() < State.INITIALIZING.ordinal(),
                    "CONCEPTION should come before INITIALIZING");
            assertTrue(State.INITIALIZING.ordinal() < State.CONFIGURING.ordinal(),
                    "INITIALIZING should come before CONFIGURING");
            assertTrue(State.CONFIGURING.ordinal() < State.READY.ordinal(),
                    "CONFIGURING should come before READY");
            assertTrue(State.READY.ordinal() < State.ACTIVE.ordinal(),
                    "READY should come before ACTIVE");
            assertTrue(State.ACTIVE.ordinal() < State.TERMINATING.ordinal(),
                    "ACTIVE should come before TERMINATING");
            assertTrue(State.TERMINATING.ordinal() < State.TERMINATED.ordinal(),
                    "TERMINATING should come before TERMINATED");
            assertTrue(State.TERMINATED.ordinal() < State.ARCHIVED.ordinal(),
                    "TERMINATED should come before ARCHIVED");
        }
        
        @Test
        @DisplayName("State transitions should follow allowed paths")
        void stateTransitionsShouldFollowAllowedPaths() {
            // Test transition paths through the component lifecycle
            Component component = Component.createAdam("Test component");
            
            // Initial state is CONCEPTION or READY (depending on implementation)
            State initialState = component.getState();
            assertTrue(initialState == State.CONCEPTION || initialState == State.READY,
                    "Initial state should be either CONCEPTION or READY");
            
            // Transition through various states
            if (initialState == State.CONCEPTION) {
                component.setState(State.INITIALIZING);
                assertEquals(State.INITIALIZING, component.getState());
                
                component.setState(State.CONFIGURING);
                assertEquals(State.CONFIGURING, component.getState());
                
                component.setState(State.READY);
                assertEquals(State.READY, component.getState());
            }
            
            // From READY, transition to ACTIVE
            component.setState(State.ACTIVE);
            assertEquals(State.ACTIVE, component.getState());
            
            // Test transition to SUSPENDED and back
            component.setState(State.SUSPENDED);
            assertEquals(State.SUSPENDED, component.getState());
            
            component.setState(State.ACTIVE);
            assertEquals(State.ACTIVE, component.getState());
            
            // Test termination path
            component.setState(State.TERMINATING);
            assertEquals(State.TERMINATING, component.getState());
            
            component.setState(State.TERMINATED);
            assertEquals(State.TERMINATED, component.getState());
            
            component.setState(State.ARCHIVED);
            assertEquals(State.ARCHIVED, component.getState());
        }
    }
    
    /**
     * This test uses the State enum from org.s8r.component.State
     * instead of defining its own LifecycleState enum.
     * 
     * The State enum provides the following capabilities:
     * - Validation of state transitions
     * - Categorization of states (operational, lifecycle, etc.)
     * - State comparison operations
     * 
     * Any reference to LifecycleState should now use State instead.
     */
}