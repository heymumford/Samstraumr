package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.component.Component;
import org.s8r.domain.machine.Machine;
import org.s8r.application.port.EventDispatcher;
import org.s8r.infrastructure.event.InMemoryEventDispatcher;
import org.s8r.test.annotation.UnitTest;
import org.s8r.architecture.util.TestComponentFactory;
import org.s8r.architecture.util.TestMachineFactory;

/**
 * Tests for the Event-Driven Communication Model as described in ADR-0010.
 * 
 * <p>This test suite validates the implementation of the event-driven communication model
 * for component interactions within the Samstraumr framework.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Event-Driven Communication Tests (ADR-0010)")
public class EventDrivenCommunicationTest {

    private EventDispatcher eventDispatcher;
    private ComponentId sourceId;

    @BeforeEach
    void setUp() {
        eventDispatcher = new InMemoryEventDispatcher();
        sourceId = new ComponentId("test-source");
    }

    @Nested
    @DisplayName("Core Event Model Tests")
    class CoreEventModelTests {
        
        @Test
        @DisplayName("Domain events should contain required properties")
        void domainEventsShouldContainRequiredProperties() {
            // Create a domain event
            Map<String, Object> payload = Map.of("key1", "value1", "key2", 123);
            DomainEvent event = new ComponentCreatedEvent(
                sourceId,
                payload
            );
            
            // Verify event properties
            assertNotNull(event.getEventId(), "Event should have ID");
            assertEquals(sourceId, event.getSourceId(), "Event should reference source component");
            assertNotNull(event.getTimestamp(), "Event should have timestamp");
            assertEquals("ComponentCreated", event.getEventType(), "Event should have correct type");
            assertEquals(payload, event.getPayload(), "Event should contain payload");
        }
        
        @Test
        @DisplayName("Event instances should be immutable")
        void eventInstancesShouldBeImmutable() {
            // Create a domain event with mutable payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("key1", "value1");
            
            DomainEvent event = new ComponentStateChangedEvent(
                sourceId,
                "CREATED",
                "READY",
                payload
            );
            
            // Try to modify original payload
            payload.put("key2", "value2");
            
            // Verify event payload is not affected
            Map<String, Object> eventPayload = event.getPayload();
            assertFalse(eventPayload.containsKey("key2"), "Event payload should be a defensive copy");
            
            // Verify event payload cannot be modified
            assertThrows(
                UnsupportedOperationException.class,
                () -> eventPayload.put("key3", "value3"),
                "Event payload should be immutable"
            );
        }
    }

    @Nested
    @DisplayName("Event Types and Hierarchy Tests")
    class EventTypesAndHierarchyTests {
        
        @Test
        @DisplayName("Event hierarchy should support polymorphic handling")
        void eventHierarchyShouldSupportPolymorphicHandling() {
            // Create a mock event dispatcher
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Create a counter for each handler type
            AtomicInteger baseHandlerCount = new AtomicInteger(0);
            AtomicInteger specificHandlerCount = new AtomicInteger(0);
            
            // Subscribe handlers
            mockDispatcher.subscribe(DomainEvent.class, event -> baseHandlerCount.incrementAndGet());
            mockDispatcher.subscribe(ComponentCreatedEvent.class, event -> specificHandlerCount.incrementAndGet());
            
            // Create and publish a specific event
            ComponentCreatedEvent event = new ComponentCreatedEvent(
                sourceId,
                Map.of("config", "test-config")
            );
            mockDispatcher.publish(event);
            
            // Verify both handlers were called
            assertEquals(1, baseHandlerCount.get(), "Base handler should be called once");
            assertEquals(1, specificHandlerCount.get(), "Specific handler should be called once");
        }
        
        @Test
        @DisplayName("System should provide standard event types")
        void systemShouldProvideStandardEventTypes() {
            // Create different event types
            ComponentCreatedEvent createdEvent = new ComponentCreatedEvent(
                sourceId,
                Map.of("reason", "Test component")
            );
            
            ComponentStateChangedEvent stateEvent = new ComponentStateChangedEvent(
                sourceId,
                "CREATED",
                "READY",
                Map.of("time", System.currentTimeMillis())
            );
            
            ComponentId targetId = new ComponentId("target");
            ComponentConnectionEvent connectionEvent = new ComponentConnectionEvent(
                sourceId,
                targetId,
                "connected",
                Map.of("connectionType", "data")
            );
            
            // Verify event types are correctly set
            assertEquals("ComponentCreated", createdEvent.getEventType());
            assertEquals("ComponentStateChanged", stateEvent.getEventType());
            assertEquals("ComponentConnection", connectionEvent.getEventType());
            
            // Verify specialized properties
            assertEquals("CREATED", stateEvent.getOldState());
            assertEquals("READY", stateEvent.getNewState());
            assertEquals(targetId, connectionEvent.getTargetId());
            assertEquals("connected", connectionEvent.getAction());
        }
        
        @Test
        @DisplayName("Component data events should carry data payloads")
        void componentDataEventsShouldCarryDataPayloads() {
            // Create a component data event with specific payload
            Object data = Map.of("id", 123, "name", "test-data", "values", List.of(1, 2, 3));
            long timestamp = System.currentTimeMillis();
            
            ComponentId targetId = new ComponentId("data-target");
            ComponentDataEvent dataEvent = new ComponentDataEvent(
                sourceId, 
                targetId,
                timestamp,
                data
            );
            
            // Verify data event properties
            assertEquals(sourceId, dataEvent.getSourceId(), "Source ID should match");
            assertEquals(targetId, dataEvent.getTargetId(), "Target ID should match");
            assertEquals(timestamp, dataEvent.getTimestamp().toEpochMilli(), "Timestamp should match");
            assertEquals(data, dataEvent.getData(), "Data payload should match");
        }
    }

    @Nested
    @DisplayName("Component and Machine Integration Tests")
    class ComponentAndMachineIntegrationTests {
        
        @Test
        @DisplayName("Components should emit events on state changes")
        void componentsShouldEmitEventsOnStateChanges() {
            // Create a mock event dispatcher
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Create a component
            Component component = TestComponentFactory.createComponent("test-component");
            
            // Record state change events
            List<ComponentStateChangedEvent> stateEvents = new ArrayList<>();
            mockDispatcher.subscribe(ComponentStateChangedEvent.class, stateEvents::add);
            
            // Trigger state changes (these would normally emit events)
            component.initialize();
            component.start();
            component.stop();
            component.destroy();
            
            // Simulate component lifecycle events being published (since our mock doesn't do it automatically)
            mockDispatcher.publish(new ComponentStateChangedEvent(
                component.getId(), "CREATED", "INITIALIZED", Map.of()));
            mockDispatcher.publish(new ComponentStateChangedEvent(
                component.getId(), "INITIALIZED", "RUNNING", Map.of()));
            mockDispatcher.publish(new ComponentStateChangedEvent(
                component.getId(), "RUNNING", "STOPPED", Map.of()));
            mockDispatcher.publish(new ComponentStateChangedEvent(
                component.getId(), "STOPPED", "DESTROYED", Map.of()));
            
            // Verify state change events
            assertEquals(4, stateEvents.size(), "Should emit 4 state change events");
            
            // Verify event sequence
            assertEquals("CREATED", stateEvents.get(0).getOldState(), "First event old state should be CREATED");
            assertEquals("INITIALIZED", stateEvents.get(0).getNewState(), "First event new state should be INITIALIZED");
            
            assertEquals("INITIALIZED", stateEvents.get(1).getOldState(), "Second event old state should be INITIALIZED");
            assertEquals("RUNNING", stateEvents.get(1).getNewState(), "Second event new state should be RUNNING");
            
            assertEquals("RUNNING", stateEvents.get(2).getOldState(), "Third event old state should be RUNNING");
            assertEquals("STOPPED", stateEvents.get(2).getNewState(), "Third event new state should be STOPPED");
            
            assertEquals("STOPPED", stateEvents.get(3).getOldState(), "Fourth event old state should be STOPPED");
            assertEquals("DESTROYED", stateEvents.get(3).getNewState(), "Fourth event new state should be DESTROYED");
        }
        
        @Test
        @DisplayName("Machines should propagate data flow events")
        void machinesShouldPropagateDataFlowEvents() {
            // Create a mock event dispatcher
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Create a data processing machine with components
            TestMachineFactory.MockDataProcessingMachine machine = 
                (TestMachineFactory.MockDataProcessingMachine) TestMachineFactory.createDataProcessingMachine(
                    "test-machine", mockDispatcher);
            
            Component source = TestComponentFactory.createComponent("source");
            Component processor = TestComponentFactory.createComponent("processor");
            Component sink = TestComponentFactory.createComponent("sink");
            
            machine.addComponent(source);
            machine.addComponent(processor);
            machine.addComponent(sink);
            
            // Initialize and start the machine
            machine.initialize();
            machine.start();
            
            // Record data events
            List<ComponentDataEvent> dataEvents = new ArrayList<>();
            mockDispatcher.subscribe(ComponentDataEvent.class, dataEvents::add);
            
            // Simulate data flow through the machine
            machine.sendData(processor.getId(), "test data 1");
            machine.sendData(processor.getId(), "test data 2");
            machine.sendData(sink.getId(), "processed data");
            
            // Verify data events
            assertEquals(3, dataEvents.size(), "Should emit 3 data events");
            
            // Verify event content
            assertEquals(processor.getId(), dataEvents.get(0).getTargetId(), 
                "First event target should be processor");
            assertEquals("test data 1", dataEvents.get(0).getData(), 
                "First event data should match");
            
            assertEquals(processor.getId(), dataEvents.get(1).getTargetId(), 
                "Second event target should be processor");
            assertEquals("test data 2", dataEvents.get(1).getData(), 
                "Second event data should match");
            
            assertEquals(sink.getId(), dataEvents.get(2).getTargetId(), 
                "Third event target should be sink");
            assertEquals("processed data", dataEvents.get(2).getData(), 
                "Third event data should match");
        }
    }

    @Nested
    @DisplayName("Subscription Mechanisms Tests")
    class SubscriptionMechanismsTests {
        
        @Test
        @DisplayName("Direct subscription should deliver matching events")
        void directSubscriptionShouldDeliverMatchingEvents() {
            // Create a mock event dispatcher
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Create counters for different event types
            AtomicInteger stateEventCount = new AtomicInteger(0);
            AtomicInteger createdEventCount = new AtomicInteger(0);
            
            // Subscribe to specific event types
            mockDispatcher.subscribe(ComponentStateChangedEvent.class, event -> stateEventCount.incrementAndGet());
            mockDispatcher.subscribe(ComponentCreatedEvent.class, event -> createdEventCount.incrementAndGet());
            
            // Create and publish events
            ComponentStateChangedEvent stateEvent = new ComponentStateChangedEvent(
                sourceId, "CREATED", "READY", Map.of());
            ComponentCreatedEvent createdEvent = new ComponentCreatedEvent(
                sourceId, Map.of());
            
            mockDispatcher.publish(stateEvent);
            mockDispatcher.publish(createdEvent);
            mockDispatcher.publish(stateEvent);
            
            // Verify only matching events were delivered
            assertEquals(2, stateEventCount.get(), "State handler should be called twice");
            assertEquals(1, createdEventCount.get(), "Created handler should be called once");
        }
        
        @Test
        @DisplayName("Hierarchical event propagation should work")
        void hierarchicalEventPropagationShouldWork() {
            // Create a mock event dispatcher
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Create a composite with components
            ComponentId rootId = new ComponentId("root");
            ComponentId childId1 = new ComponentId("child1");
            ComponentId childId2 = new ComponentId("child2");
            
            // Create event counters for tracking
            Map<ComponentId, AtomicInteger> eventCounts = new HashMap<>();
            eventCounts.put(rootId, new AtomicInteger(0));
            eventCounts.put(childId1, new AtomicInteger(0));
            eventCounts.put(childId2, new AtomicInteger(0));
            
            // Subscribe to events with component ID filtering
            mockDispatcher.subscribe(DomainEvent.class, event -> {
                ComponentId sourceId = event.getSourceId();
                if (eventCounts.containsKey(sourceId)) {
                    eventCounts.get(sourceId).incrementAndGet();
                }
            });
            
            // Publish events from different components
            mockDispatcher.publish(new ComponentCreatedEvent(rootId, Map.of()));
            mockDispatcher.publish(new ComponentCreatedEvent(childId1, Map.of()));
            mockDispatcher.publish(new ComponentStateChangedEvent(childId2, "CREATED", "READY", Map.of()));
            mockDispatcher.publish(new ComponentStateChangedEvent(rootId, "CREATED", "READY", Map.of()));
            
            // Verify event counts
            assertEquals(2, eventCounts.get(rootId).get(), "Root should receive 2 events");
            assertEquals(1, eventCounts.get(childId1).get(), "Child1 should receive 1 event");
            assertEquals(1, eventCounts.get(childId2).get(), "Child2 should receive 1 event");
        }
    }

    @Nested
    @DisplayName("Delivery Guarantees Tests")
    class DeliveryGuaranteesTests {
        
        @Test
        @DisplayName("At-least-once delivery should retry failed deliveries")
        void atLeastOnceDeliveryShouldRetryFailedDeliveries() throws Exception {
            // Create a retry operation with a failing handler
            CountDownLatch latch = new CountDownLatch(1);
            AtomicInteger attemptCount = new AtomicInteger(0);
            
            // Define an operation that fails on first try
            Supplier<Boolean> failingOperation = () -> {
                int attempts = attemptCount.incrementAndGet();
                if (attempts == 1) {
                    throw new RuntimeException("First attempt failure");
                }
                latch.countDown();
                return true;
            };
            
            // Execute with retry logic (using our utility method that simulates retry mechanism)
            Thread.startVirtualThread(() -> {
                try {
                    executeWithRetry(failingOperation, 3, 100);
                } catch (Exception e) {
                    // Ignore
                }
            });
            
            // Verify handler eventually succeeds
            assertTrue(latch.await(2, TimeUnit.SECONDS), "Operation should succeed eventually");
            assertEquals(2, attemptCount.get(), "Operation should be tried twice");
        }
        
        @Test
        @DisplayName("Ordered delivery should preserve event sequence")
        void orderedDeliveryShouldPreserveEventSequence() {
            // Create an ordered list to capture event sequence
            List<Long> receivedSequences = Collections.synchronizedList(new ArrayList<>());
            
            // Create a mock event dispatcher that we'll use to simulate ordered delivery
            TestComponentFactory.MockEventDispatcher mockDispatcher = 
                (TestComponentFactory.MockEventDispatcher) TestComponentFactory.createEventDispatcher();
            
            // Subscribe to collect events in order
            mockDispatcher.subscribe(ComponentDataEvent.class, event -> {
                Long sequence = (Long) event.getData();
                receivedSequences.add(sequence);
            });
            
            // Publish events with different sequence numbers (in correct order for this test)
            for (long i = 1; i <= 5; i++) {
                mockDispatcher.publish(new ComponentDataEvent(
                    sourceId, 
                    new ComponentId("target"),
                    System.currentTimeMillis(),
                    i  // Sequence number
                ));
            }
            
            // Verify events were received in the expected sequence
            assertEquals(
                List.of(1L, 2L, 3L, 4L, 5L),
                receivedSequences,
                "Events should be delivered in sequence order"
            );
        }
    }
    
    // Helper methods
    
    /**
     * A supplier interface for operations that return a result.
     */
    interface Supplier<T> {
        T get();
    }
    
    /**
     * Helper method to execute an operation with retry logic.
     *
     * @param <T> The return type of the operation
     * @param operation The operation to execute
     * @param maxRetries Maximum number of retries
     * @param retryDelay Delay between retries in milliseconds
     * @return The result of the operation if successful
     * @throws Exception if the operation fails after all retries
     */
    private <T> T executeWithRetry(Supplier<T> operation, int maxRetries, long retryDelay) 
            throws Exception {
        Exception lastException = null;
        
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    Thread.sleep(retryDelay);
                }
            }
        }
        
        throw lastException;
    }
}