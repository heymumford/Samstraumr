package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.application.port.EventDispatcher;
import org.s8r.test.annotation.UnitTest;
import org.s8r.architecture.util.TestComponentFactory;
import org.s8r.architecture.util.HierarchicalEventDispatcher;

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

    private ComponentId testId;
    private EventDispatcher eventDispatcher;

    @BeforeEach
    void setUp() {
        testId = ComponentId.create("test-component");
        eventDispatcher = TestComponentFactory.createEventDispatcher();
    }

    @Nested
    @DisplayName("Core Lifecycle States Tests")
    class CoreLifecycleStatesTests {
        
        @Test
        @DisplayName("Component should follow standard lifecycle path")
        void componentShouldFollowStandardLifecyclePath() {
            Component component = Component.create(testId);
            
            // Initial state after creation
            assertEquals(LifecycleState.CONCEPTION, component.getLifecycleState(), 
                "Initial state should be CONCEPTION");
            
            // Transition through lifecycle states
            component.transitionTo(LifecycleState.INITIALIZING);
            assertEquals(LifecycleState.INITIALIZING, component.getLifecycleState(), 
                "State should be INITIALIZING");
            
            component.transitionTo(LifecycleState.CONFIGURING);
            assertEquals(LifecycleState.CONFIGURING, component.getLifecycleState(), 
                "State should be CONFIGURING");
            
            component.transitionTo(LifecycleState.SPECIALIZING);
            assertEquals(LifecycleState.SPECIALIZING, component.getLifecycleState(), 
                "State should be SPECIALIZING");
            
            component.transitionTo(LifecycleState.DEVELOPING_FEATURES);
            assertEquals(LifecycleState.DEVELOPING_FEATURES, component.getLifecycleState(), 
                "State should be DEVELOPING_FEATURES");
            
            component.transitionTo(LifecycleState.READY);
            assertEquals(LifecycleState.READY, component.getLifecycleState(), 
                "State should be READY");
            
            component.activate();
            assertEquals(LifecycleState.ACTIVE, component.getLifecycleState(), 
                "State should be ACTIVE");
            
            component.deactivate();
            assertEquals(LifecycleState.READY, component.getLifecycleState(), 
                "State should be READY");
            
            component.terminate();
            assertEquals(LifecycleState.TERMINATED, component.getLifecycleState(), 
                "Final state should be TERMINATED");
        }
        
        @Test
        @DisplayName("Invalid state transitions should be prevented")
        void invalidStateTransitionsShouldBePrevented() {
            Component component = Component.create(testId);
            
            // Try invalid transition (skipping states)
            assertThrows(InvalidStateTransitionException.class, 
                () -> component.transitionTo(LifecycleState.ACTIVE),
                "Should throw exception on invalid transition");
            
            // Setup correct state for testing more transitions
            component.transitionTo(LifecycleState.INITIALIZING);
            component.transitionTo(LifecycleState.CONFIGURING);
            component.transitionTo(LifecycleState.SPECIALIZING);
            component.transitionTo(LifecycleState.DEVELOPING_FEATURES);
            component.transitionTo(LifecycleState.READY);
            
            // Try direct activation methods
            component.activate(); // This should work
            
            // Try multiple activations (already active)
            assertThrows(InvalidStateTransitionException.class, 
                () -> component.activate(),
                "Should throw exception when activating an already active component");
            
            // Try deactivating and then terminating (valid sequence)
            component.deactivate();
            component.terminate();
            
            // Try to reactivate after termination
            assertThrows(InvalidStateTransitionException.class, 
                () -> component.activate(),
                "Should throw exception when activating a terminated component");
        }
        
        @Test
        @DisplayName("Lifecycle states should have descriptive properties")
        void lifecycleStatesShouldHaveDescriptiveProperties() {
            // Verify descriptions on states
            for (LifecycleState state : LifecycleState.values()) {
                assertNotNull(state.getDescription(), "State should have a description");
                assertFalse(state.getDescription().isEmpty(), "Description should not be empty");
                
                assertNotNull(state.getBiologicalAnalog(), "State should have a biological analog");
                assertFalse(state.getBiologicalAnalog().isEmpty(), "Biological analog should not be empty");
            }
            
            // Check specific state categories
            assertTrue(LifecycleState.CONCEPTION.isEarlyStage(), "CONCEPTION should be an early stage");
            assertTrue(LifecycleState.ACTIVE.isOperational(), "ACTIVE should be operational");
            assertTrue(LifecycleState.TERMINATING.isTerminationStage(), "TERMINATING should be a termination stage");
        }
    }

    @Nested
    @DisplayName("Event Emission Tests")
    class EventEmissionTests {
        
        @Test
        @DisplayName("State transitions should emit events")
        void stateTransitionsShouldEmitEvents() {
            // Create a component
            Component component = Component.create(testId);
            
            // Track state change events
            List<ComponentStateChangedEvent> stateEvents = new ArrayList<>();
            HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();
            
            // Perform state transitions and collect events
            component.transitionTo(LifecycleState.INITIALIZING);
            component.transitionTo(LifecycleState.CONFIGURING);
            component.transitionTo(LifecycleState.SPECIALIZING);
            
            // Extract events from the component
            List<ComponentStateChangedEvent> events = component.getDomainEvents().stream()
                .filter(e -> e instanceof ComponentStateChangedEvent)
                .map(e -> (ComponentStateChangedEvent) e)
                .toList();
            
            // Verify events
            assertEquals(3, events.size(), "Should have 3 state transition events");
            
            // Check first transition event
            ComponentStateChangedEvent firstEvent = events.get(0);
            assertEquals(component.getId(), firstEvent.getComponentId(), "Event should reference the component");
            assertEquals(LifecycleState.CONCEPTION, firstEvent.getPreviousState(), "First event should transition from CONCEPTION");
            assertEquals(LifecycleState.INITIALIZING, firstEvent.getNewState(), "First event should transition to INITIALIZING");
        }
        
        @Test
        @DisplayName("Termination should emit proper events")
        void terminationShouldEmitProperEvents() {
            // Create a component and progress to READY state
            Component component = Component.create(testId);
            component.transitionTo(LifecycleState.INITIALIZING);
            component.transitionTo(LifecycleState.CONFIGURING);
            component.transitionTo(LifecycleState.SPECIALIZING);
            component.transitionTo(LifecycleState.DEVELOPING_FEATURES);
            component.transitionTo(LifecycleState.READY);
            
            // Clear events from initialization
            component.clearEvents();
            
            // Terminate the component
            component.terminate();
            
            // Get the termination events
            List<ComponentStateChangedEvent> events = component.getDomainEvents().stream()
                .filter(e -> e instanceof ComponentStateChangedEvent)
                .map(e -> (ComponentStateChangedEvent) e)
                .toList();
            
            // Verify termination events
            assertEquals(2, events.size(), "Should have 2 state transition events during termination");
            
            // First event should be transition to TERMINATING
            ComponentStateChangedEvent firstEvent = events.get(0);
            assertEquals(LifecycleState.READY, firstEvent.getPreviousState(), "First event should transition from READY");
            assertEquals(LifecycleState.TERMINATING, firstEvent.getNewState(), "First event should transition to TERMINATING");
            
            // Second event should be transition to TERMINATED
            ComponentStateChangedEvent secondEvent = events.get(1);
            assertEquals(LifecycleState.TERMINATING, secondEvent.getPreviousState(), "Second event should transition from TERMINATING");
            assertEquals(LifecycleState.TERMINATED, secondEvent.getNewState(), "Second event should transition to TERMINATED");
        }
    }

    @Nested
    @DisplayName("Lifecycle Management Features")
    class LifecycleManagementFeaturesTests {
        
        @Test
        @DisplayName("Component should maintain activity log")
        void componentShouldMaintainActivityLog() {
            Component component = Component.create(testId);
            
            // Perform some lifecycle operations
            component.transitionTo(LifecycleState.INITIALIZING);
            component.addToLineage("Test lineage entry");
            component.transitionTo(LifecycleState.CONFIGURING);
            
            // Verify activity log
            List<String> activityLog = component.getActivityLog();
            assertFalse(activityLog.isEmpty(), "Activity log should not be empty");
            
            // Check for key activities
            assertTrue(
                activityLog.stream().anyMatch(entry -> entry.contains("Component created")),
                "Activity log should contain creation entry"
            );
            
            assertTrue(
                activityLog.stream().anyMatch(entry -> entry.contains("State transition")),
                "Activity log should contain state transition entries"
            );
            
            assertTrue(
                activityLog.stream().anyMatch(entry -> entry.contains("lineage")),
                "Activity log should contain lineage updates"
            );
        }
        
        @Test
        @DisplayName("Component should manage lineage")
        void componentShouldManageLineage() {
            Component component = Component.create(ComponentId.create("lineage-test"));
            
            // Add lineage entries
            component.addToLineage("First generation");
            component.addToLineage("Second generation");
            
            // Verify lineage
            List<String> lineage = component.getLineage();
            assertEquals(3, lineage.size(), "Lineage should have 3 entries (including creation reason)");
            assertEquals("lineage-test", lineage.get(0), "First entry should be creation reason");
            assertEquals("First generation", lineage.get(1), "Second entry should be first generation");
            assertEquals("Second generation", lineage.get(2), "Third entry should be second generation");
        }
    }
}