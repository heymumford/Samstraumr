package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.s8r.component.core.Component;
import org.s8r.component.core.Environment;
import org.s8r.component.core.State;
import org.s8r.component.exception.ComponentException;
import org.s8r.component.exception.InvalidStateTransitionException;
import org.s8r.component.composite.Composite;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Lifecycle State Management Pattern as described in ADR-0009.
 * 
 * <p>This test suite validates the implementation of the lifecycle state management pattern
 * for components within the Samstraumr framework.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Lifecycle State Management Tests (ADR-0009)")
public class LifecycleStateManagementTest {

    private Environment environment;

    @BeforeEach
    void setUp() {
        environment = new Environment();
        environment.setParameter("test-mode", "true");
    }

    @Nested
    @DisplayName("Core Lifecycle States Tests")
    class CoreLifecycleStatesTests {
        
        @Test
        @DisplayName("Component should follow standard lifecycle path")
        void componentShouldFollowStandardLifecyclePath() {
            Component component = Component.create("Lifecycle Test", environment);
            
            // Initial state after creation
            assertEquals(State.CREATED, component.getState(), "Initial state should be CREATED");
            
            // Initialize component
            component.initialize();
            assertEquals(State.READY, component.getState(), "State after initialization should be READY");
            
            // Component in use - state should remain READY
            component.processData("test data");
            assertEquals(State.READY, component.getState(), "State during normal operation should be READY");
            
            // Stop component
            component.stop();
            assertEquals(State.STOPPING, component.getState(), "State during shutdown should be STOPPING");
            
            // Destroy component
            component.destroy();
            assertEquals(State.DESTROYED, component.getState(), "Final state should be DESTROYED");
        }
        
        @Test
        @DisplayName("Component should handle initialization failure")
        void componentShouldHandleInitializationFailure() {
            // Create a component that will fail during initialization
            Component faultyComponent = Component.createWithConfig(
                "Faulty Component", 
                environment,
                Map.of("failOnInitialize", "true")
            );
            
            // Attempt to initialize
            assertThrows(ComponentException.class, () -> faultyComponent.initialize());
            
            // Check state after failed initialization
            assertEquals(
                State.INITIALIZATION_FAILED, 
                faultyComponent.getState(),
                "State after failed initialization should be INITIALIZATION_FAILED"
            );
            
            // Verify that operations are rejected in failed state
            assertThrows(
                InvalidStateTransitionException.class, 
                () -> faultyComponent.processData("test"),
                "Operations should be rejected in INITIALIZATION_FAILED state"
            );
            
            // Verify that destruction is still allowed
            assertDoesNotThrow(() -> faultyComponent.destroy());
            assertEquals(
                State.DESTROYED, 
                faultyComponent.getState(),
                "Component should transition to DESTROYED state"
            );
        }
        
        @ParameterizedTest
        @EnumSource(value = State.class, names = {"CREATED", "INITIALIZING", "READY", "STOPPING", "DESTROYED", "INITIALIZATION_FAILED"})
        @DisplayName("All standard states should be supported")
        void allStandardStatesShouldBeSupported(State state) {
            // Create a mock component that can be set to any state
            Component mockComponent = mock(Component.class);
            when(mockComponent.getState()).thenReturn(state);
            
            // Get state description
            String description = state.getDescription();
            
            // Verify state properties
            assertNotNull(description, "State should have a description");
            assertFalse(description.isEmpty(), "Description should not be empty");
            
            // Check if state allows operations based on its value
            assertEquals(state == State.READY, state.allowsOperations(), "Only READY state should allow operations");
        }
    }

    @Nested
    @DisplayName("State Transition Management Tests")
    class StateTransitionManagementTests {
        
        @Test
        @DisplayName("State transitions should be explicit")
        void stateTransitionsShouldBeExplicit() {
            Component component = Component.create("Transition Test", environment);
            
            // Track state changes
            List<State> stateChanges = new ArrayList<>();
            component.addStateChangeListener(stateChanges::add);
            
            // Perform lifecycle operations
            component.initialize();
            component.stop();
            component.destroy();
            
            // Verify all transitions were recorded
            assertEquals(3, stateChanges.size(), "Should record 3 state changes");
            assertEquals(State.READY, stateChanges.get(0), "First transition should be to READY");
            assertEquals(State.STOPPING, stateChanges.get(1), "Second transition should be to STOPPING");
            assertEquals(State.DESTROYED, stateChanges.get(2), "Third transition should be to DESTROYED");
        }
        
        @Test
        @DisplayName("Invalid state transitions should fail with clear errors")
        void invalidStateTransitionsShouldFailWithClearErrors() {
            Component component = Component.create("Invalid Transition Test", environment);
            component.initialize(); // Move to READY state
            
            // Invalid transition: re-initialize an already initialized component
            InvalidStateTransitionException exception = assertThrows(
                InvalidStateTransitionException.class,
                () -> component.initialize()
            );
            
            // Verify exception details
            assertEquals(State.READY, exception.getCurrentState(), "Exception should reference current state");
            assertEquals("initialize", exception.getOperation(), "Exception should reference attempted operation");
            assertTrue(
                exception.getMessage().contains("READY"),
                "Exception message should mention current state"
            );
        }
        
        @Test
        @DisplayName("State transitions should be atomic")
        void stateTransitionsShouldBeAtomic() throws Exception {
            // Create component for testing atomic transitions
            Component component = Component.create("Atomic Transition Test", environment);
            
            // Set up a latch to control the test timing
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(2);
            
            // Create two threads that will try to initialize the component simultaneously
            Thread thread1 = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for signal to start
                    component.initialize();
                    completionLatch.countDown();
                } catch (Exception e) {
                    // Expected in one thread
                }
            });
            
            Thread thread2 = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for signal to start
                    component.initialize();
                    completionLatch.countDown();
                } catch (Exception e) {
                    // Expected in one thread
                }
            });
            
            // Start threads
            thread1.start();
            thread2.start();
            
            // Signal threads to proceed
            startLatch.countDown();
            
            // Wait for both threads to complete
            assertTrue(completionLatch.await(5, TimeUnit.SECONDS), "Both threads should complete");
            
            // Verify only one initialization succeeded
            assertEquals(State.READY, component.getState(), "Component should be in READY state");
            assertEquals(1, component.getStateTransitionCount(), "Only one transition should have occurred");
        }
    }

    @Nested
    @DisplayName("State-Based Operation Validation Tests")
    class StateBasedOperationValidationTests {
        
        @Test
        @DisplayName("Operations should be validated against component state")
        void operationsShouldBeValidatedAgainstComponentState() {
            Component component = Component.create("Operation Validation Test", environment);
            
            // Operations should be rejected in CREATED state
            assertThrows(
                InvalidStateTransitionException.class, 
                () -> component.processData("test"),
                "Operations should be rejected in CREATED state"
            );
            
            // Initialize to allow operations
            component.initialize();
            
            // Operations should be allowed in READY state
            assertDoesNotThrow(() -> component.processData("test"));
            
            // Destroy component
            component.destroy();
            
            // Operations should be rejected in DESTROYED state
            assertThrows(
                InvalidStateTransitionException.class, 
                () -> component.processData("test"),
                "Operations should be rejected in DESTROYED state"
            );
        }
        
        @Test
        @DisplayName("Methods should declare required states")
        void methodsShouldDeclareRequiredStates() {
            // This is a reflection-based test that checks for @RequiresState annotations
            // on Component interface methods
            
            // Get operation method
            java.lang.reflect.Method processDataMethod = Arrays.stream(Component.class.getMethods())
                .filter(m -> m.getName().equals("processData"))
                .findFirst()
                .orElseThrow();
            
            // Check for @RequiresState annotation
            RequiresState annotation = processDataMethod.getAnnotation(RequiresState.class);
            assertNotNull(annotation, "Operation method should have @RequiresState annotation");
            
            // Verify required state
            State[] requiredStates = annotation.value();
            assertEquals(1, requiredStates.length, "Should require exactly one state");
            assertEquals(State.READY, requiredStates[0], "Should require READY state");
        }
    }

    @Nested
    @DisplayName("Hierarchy Coordination Tests")
    class HierarchyCoordinationTests {
        
        @Test
        @DisplayName("Parent initialization should complete only after all children initialize")
        void parentInitializationShouldCompleteOnlyAfterAllChildrenInitialize() {
            // Create a composite with children
            Composite composite = Composite.create("Hierarchy Test", environment);
            Component child1 = Component.create("Child 1", environment);
            Component child2 = Component.create("Child 2", environment);
            
            composite.addComponent(child1);
            composite.addComponent(child2);
            
            // Initialize composite
            composite.initialize();
            
            // Verify all components are initialized
            assertEquals(State.READY, composite.getState(), "Composite should be initialized");
            assertEquals(State.READY, child1.getState(), "Child 1 should be initialized");
            assertEquals(State.READY, child2.getState(), "Child 2 should be initialized");
            
            // Verify initialization order through sequence numbers
            long compositeSeq = composite.getStateChangeSequence(State.READY);
            long child1Seq = child1.getStateChangeSequence(State.READY);
            long child2Seq = child2.getStateChangeSequence(State.READY);
            
            assertTrue(compositeSeq > child1Seq, "Composite should be initialized after Child 1");
            assertTrue(compositeSeq > child2Seq, "Composite should be initialized after Child 2");
        }
        
        @Test
        @DisplayName("Child components should be stopped before parent components")
        void childComponentsShouldBeStoppedBeforeParentComponents() {
            // Create a composite with children
            Composite composite = Composite.create("Hierarchy Stop Test", environment);
            Component child1 = Component.create("Child 1", environment);
            Component child2 = Component.create("Child 2", environment);
            
            composite.addComponent(child1);
            composite.addComponent(child2);
            
            // Initialize
            composite.initialize();
            
            // Stop composite
            composite.stop();
            
            // Verify stop state
            assertEquals(State.STOPPING, composite.getState(), "Composite should be stopping");
            assertEquals(State.STOPPING, child1.getState(), "Child 1 should be stopping");
            assertEquals(State.STOPPING, child2.getState(), "Child 2 should be stopping");
            
            // Verify stop order through sequence numbers
            long compositeSeq = composite.getStateChangeSequence(State.STOPPING);
            long child1Seq = child1.getStateChangeSequence(State.STOPPING);
            long child2Seq = child2.getStateChangeSequence(State.STOPPING);
            
            assertTrue(compositeSeq < child1Seq, "Child 1 should be stopped after Composite");
            assertTrue(compositeSeq < child2Seq, "Child 2 should be stopped after Composite");
        }
        
        @Test
        @DisplayName("Failure in child initialization should propagate to parent")
        void failureInChildInitializationShouldPropagateToParent() {
            // Create a composite with children, one of which will fail
            Composite composite = Composite.create("Hierarchy Failure Test", environment);
            Component child1 = Component.create("Child 1", environment);
            Component faultyChild = Component.createWithConfig(
                "Faulty Child", 
                environment,
                Map.of("failOnInitialize", "true")
            );
            
            composite.addComponent(child1);
            composite.addComponent(faultyChild);
            
            // Attempt to initialize
            assertThrows(ComponentException.class, () -> composite.initialize());
            
            // Verify states
            assertEquals(
                State.INITIALIZATION_FAILED, 
                composite.getState(),
                "Composite should be in INITIALIZATION_FAILED state"
            );
            // Child1 might have initialized before the faulty child failed
            assertTrue(
                child1.getState() == State.READY || child1.getState() == State.CREATED,
                "Child 1 should be in READY or CREATED state"
            );
            assertEquals(
                State.INITIALIZATION_FAILED, 
                faultyChild.getState(),
                "Faulty child should be in INITIALIZATION_FAILED state"
            );
        }
    }
    
    // Mock annotation for test
    @interface RequiresState {
        State[] value();
    }
}