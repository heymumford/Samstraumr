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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.test.annotation.UnitTest;

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
            "CREATED, INITIALIZING, true",
            "INITIALIZING, INITIALIZED, true",
            "INITIALIZED, RUNNING, true",
            "RUNNING, PAUSED, true",
            "PAUSED, RUNNING, true",
            "RUNNING, STOPPING, true",
            "STOPPING, STOPPED, true",
            "STOPPED, TERMINATED, true",
            "CREATED, RUNNING, false",
            "INITIALIZING, PAUSED, false",
            "RUNNING, TERMINATED, false",
            "TERMINATED, RUNNING, false",
            "STOPPED, RUNNING, false"
        })
        @DisplayName("canTransitionTo() should validate allowed transitions")
        void canTransitionToShouldValidateAllowedTransitions(
                LifecycleState from, LifecycleState to, boolean expected) {
            assertEquals(expected, from.canTransitionTo(to), 
                    "Transition from " + from + " to " + to + " should be " + (expected ? "allowed" : "disallowed"));
        }
        
        @Test
        @DisplayName("transitionTo() should perform valid transitions")
        void transitionToShouldPerformValidTransitions() {
            // Given
            LifecycleState state = LifecycleState.CREATED;
            
            // When
            LifecycleState next = state.transitionTo(LifecycleState.INITIALIZING);
            
            // Then
            assertEquals(LifecycleState.INITIALIZING, next, "Transition should return next state");
        }
        
        @Test
        @DisplayName("transitionTo() should throw exception for invalid transitions")
        void transitionToShouldThrowExceptionForInvalidTransitions() {
            // Given
            LifecycleState state = LifecycleState.CREATED;
            
            // When/Then
            assertThrows(InvalidStateTransitionException.class, 
                    () -> state.transitionTo(LifecycleState.RUNNING),
                    "Invalid transition should throw exception");
        }
        
        @Test
        @DisplayName("transitionTo() should allow self-transitions")
        void transitionToShouldAllowSelfTransitions() {
            // Given
            LifecycleState state = LifecycleState.RUNNING;
            
            // When
            LifecycleState next = state.transitionTo(LifecycleState.RUNNING);
            
            // Then
            assertEquals(state, next, "Self-transition should be allowed");
        }
    }
    
    @Nested
    @DisplayName("State Property Tests")
    class StatePropertyTests {
        
        @ParameterizedTest
        @EnumSource(value = LifecycleState.class, 
                    names = {"CREATED", "INITIALIZING", "INITIALIZED"})
        @DisplayName("isActive() should return false for early lifecycle states")
        void isActiveShouldReturnFalseForEarlyLifecycleStates(LifecycleState state) {
            assertFalse(state.isActive(), state + " should not be active");
        }
        
        @ParameterizedTest
        @EnumSource(value = LifecycleState.class, 
                    names = {"RUNNING", "PAUSED"})
        @DisplayName("isActive() should return true for active states")
        void isActiveShouldReturnTrueForActiveStates(LifecycleState state) {
            assertTrue(state.isActive(), state + " should be active");
        }
        
        @ParameterizedTest
        @EnumSource(value = LifecycleState.class, 
                    names = {"STOPPING", "STOPPED", "TERMINATED", "ERROR"})
        @DisplayName("isActive() should return false for terminal states")
        void isActiveShouldReturnFalseForTerminalStates(LifecycleState state) {
            assertFalse(state.isActive(), state + " should not be active");
        }
        
        @ParameterizedTest
        @EnumSource(value = LifecycleState.class, 
                    names = {"CREATED", "INITIALIZING", "INITIALIZED", "RUNNING", "PAUSED", "STOPPING"})
        @DisplayName("isTerminal() should return false for non-terminal states")
        void isTerminalShouldReturnFalseForNonTerminalStates(LifecycleState state) {
            assertFalse(state.isTerminal(), state + " should not be terminal");
        }
        
        @ParameterizedTest
        @EnumSource(value = LifecycleState.class, 
                    names = {"STOPPED", "TERMINATED", "ERROR"})
        @DisplayName("isTerminal() should return true for terminal states")
        void isTerminalShouldReturnTrueForTerminalStates(LifecycleState state) {
            assertTrue(state.isTerminal(), state + " should be terminal");
        }
    }
    
    @Nested
    @DisplayName("State Comparison Tests")
    class StateComparisonTests {
        
        @Test
        @DisplayName("isBefore() should compare state progression")
        void isBeforeShouldCompareStateProgression() {
            assertTrue(LifecycleState.CREATED.isBefore(LifecycleState.INITIALIZING),
                    "CREATED should be before INITIALIZING");
            assertTrue(LifecycleState.INITIALIZING.isBefore(LifecycleState.RUNNING),
                    "INITIALIZING should be before RUNNING");
            assertTrue(LifecycleState.RUNNING.isBefore(LifecycleState.TERMINATED),
                    "RUNNING should be before TERMINATED");
            
            assertFalse(LifecycleState.RUNNING.isBefore(LifecycleState.CREATED),
                    "RUNNING should not be before CREATED");
            assertFalse(LifecycleState.CREATED.isBefore(LifecycleState.CREATED),
                    "State should not be before itself");
        }
        
        @Test
        @DisplayName("isAfter() should compare state progression")
        void isAfterShouldCompareStateProgression() {
            assertTrue(LifecycleState.TERMINATED.isAfter(LifecycleState.RUNNING),
                    "TERMINATED should be after RUNNING");
            assertTrue(LifecycleState.RUNNING.isAfter(LifecycleState.INITIALIZED),
                    "RUNNING should be after INITIALIZED");
            assertTrue(LifecycleState.INITIALIZED.isAfter(LifecycleState.CREATED),
                    "INITIALIZED should be after CREATED");
            
            assertFalse(LifecycleState.CREATED.isAfter(LifecycleState.RUNNING),
                    "CREATED should not be after RUNNING");
            assertFalse(LifecycleState.RUNNING.isAfter(LifecycleState.RUNNING),
                    "State should not be after itself");
        }
    }
    
    /**
     * Enum that defines the possible lifecycle states of components in the system.
     * In a real implementation, this would be in the domain layer.
     */
    enum LifecycleState {
        CREATED(0),
        INITIALIZING(1),
        INITIALIZED(2),
        RUNNING(3),
        PAUSED(4),
        STOPPING(5),
        STOPPED(6),
        TERMINATED(7),
        ERROR(8);
        
        private final int order;
        
        LifecycleState(int order) {
            this.order = order;
        }
        
        public boolean canTransitionTo(LifecycleState target) {
            if (this == target) {
                return true; // Self-transition is always allowed
            }
            
            switch(this) {
                case CREATED:
                    return target == INITIALIZING;
                case INITIALIZING:
                    return target == INITIALIZED || target == ERROR;
                case INITIALIZED:
                    return target == RUNNING || target == ERROR;
                case RUNNING:
                    return target == PAUSED || target == STOPPING || target == ERROR;
                case PAUSED:
                    return target == RUNNING || target == STOPPING || target == ERROR;
                case STOPPING:
                    return target == STOPPED || target == ERROR;
                case STOPPED:
                    return target == TERMINATED || target == ERROR;
                case TERMINATED:
                case ERROR:
                    return false; // Terminal states can't transition
                default:
                    return false;
            }
        }
        
        public LifecycleState transitionTo(LifecycleState target) {
            if (!canTransitionTo(target)) {
                throw new InvalidStateTransitionException(this, target);
            }
            return target;
        }
        
        public boolean isActive() {
            return this == RUNNING || this == PAUSED;
        }
        
        public boolean isTerminal() {
            return this == STOPPED || this == TERMINATED || this == ERROR;
        }
        
        public boolean isBefore(LifecycleState other) {
            return this.order < other.order;
        }
        
        public boolean isAfter(LifecycleState other) {
            return this.order > other.order;
        }
    }
}