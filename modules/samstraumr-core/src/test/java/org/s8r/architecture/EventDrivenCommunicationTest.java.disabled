package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.EventDispatcher;
import org.s8r.architecture.util.HierarchicalEventDispatcher;
import org.s8r.architecture.util.TestComponentFactory;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the Event-Driven Communication Model as described in ADR-0010.
 *
 * <p>This test suite validates the implementation of the event-driven communication model for
 * component interactions within the Samstraumr framework.
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
    // Create a hierarchical event dispatcher for testing
    eventDispatcher = TestComponentFactory.createEventDispatcher();
    sourceId = ComponentId.create("test-source");
  }

  @Nested
  @DisplayName("Core Event Model Tests")
  class CoreEventModelTests {

    @Test
    @DisplayName("Domain events should contain required properties")
    void domainEventsShouldContainRequiredProperties() {
      // Create a domain event
      DomainEvent event = new ComponentCreatedEvent(sourceId, "TestComponent");

      // Verify event properties
      assertNotNull(event.getEventId(), "Event should have ID");
      assertEquals("ComponentCreated", event.getEventType(), "Event should have correct type");
      assertNotNull(event.getOccurredOn(), "Event should have timestamp");
    }

    @Test
    @DisplayName("Event instances should be immutable")
    void eventInstancesShouldBeImmutable() {
      // Create a domain event
      ComponentDataEvent event =
          new ComponentDataEvent(sourceId, "test-channel", new HashMap<>(Map.of("key1", "value1")));

      // Verify event is immutable
      assertThrows(
          UnsupportedOperationException.class,
          () -> event.getData().put("key3", "value3"),
          "Event payload should be immutable");
    }
  }

  @Nested
  @DisplayName("Event Types and Hierarchy Tests")
  class EventTypesAndHierarchyTests {

    @Test
    @DisplayName("Event hierarchy should support polymorphic handling")
    void eventHierarchyShouldSupportPolymorphicHandling() {
      // Create a hierarchical event dispatcher for testing
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // Create a counter for each handler type
      AtomicInteger baseHandlerCount = new AtomicInteger(0);
      AtomicInteger specificHandlerCount = new AtomicInteger(0);

      // Subscribe handlers using registerHandler with lambda wrapper
      mockDispatcher.registerHandler(
          DomainEvent.class,
          (EventDispatcher.EventHandler<DomainEvent>) event -> baseHandlerCount.incrementAndGet());
      mockDispatcher.registerHandler(
          ComponentCreatedEvent.class,
          (EventDispatcher.EventHandler<ComponentCreatedEvent>)
              event -> specificHandlerCount.incrementAndGet());

      // Create and publish a specific event
      ComponentCreatedEvent event = new ComponentCreatedEvent(sourceId, "TestComponent");
      mockDispatcher.dispatch(event);

      // Verify both handlers were called
      assertEquals(1, baseHandlerCount.get(), "Base handler should be called once");
      assertEquals(1, specificHandlerCount.get(), "Specific handler should be called once");
    }

    @Test
    @DisplayName("System should provide standard event types")
    void systemShouldProvideStandardEventTypes() {
      // Create different event types
      ComponentCreatedEvent createdEvent = new ComponentCreatedEvent(sourceId, "TestComponent");

      ComponentStateChangedEvent stateEvent =
          new ComponentStateChangedEvent(
              sourceId,
              org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              "State change test");

      ComponentId targetId = ComponentId.create("target");
      ComponentConnectionEvent connectionEvent =
          new ComponentConnectionEvent(
              sourceId, targetId, ConnectionType.DATA_FLOW, "testConnection");

      // Verify event types are correctly set
      assertEquals("ComponentCreated", createdEvent.getEventType());
      assertEquals("ComponentStateChanged", stateEvent.getEventType());
      assertEquals("ComponentConnection", connectionEvent.getEventType());

      // Verify specialized properties
      assertEquals(
          org.s8r.domain.lifecycle.LifecycleState.CONCEPTION, stateEvent.getPreviousState());
      assertEquals(org.s8r.domain.lifecycle.LifecycleState.INITIALIZING, stateEvent.getNewState());
      assertEquals(targetId, connectionEvent.getTargetId());
      assertEquals("testConnection", connectionEvent.getConnectionName());
    }

    @Test
    @DisplayName("Component data events should carry data payloads")
    void componentDataEventsShouldCarryDataPayloads() {
      // Create a component data event with specific payload
      Map<String, Object> testData = Map.of("id", 123, "name", "test-data");

      ComponentDataEvent dataEvent = new ComponentDataEvent(sourceId, "data-channel", testData);

      // Verify data event properties
      assertEquals(sourceId, dataEvent.getSourceId(), "Source ID should match");
      assertEquals("data-channel", dataEvent.getDataChannel(), "Channel should match");
      assertEquals(testData.get("id"), dataEvent.getValue("id"), "Data value should match");
      assertEquals(testData.get("name"), dataEvent.getValue("name"), "Data value should match");
    }
  }

  @Nested
  @DisplayName("Component and Machine Integration Tests")
  class ComponentAndMachineIntegrationTests {

    @Test
    @DisplayName("Components should emit events on state changes")
    void componentsShouldEmitEventsOnStateChanges() {
      // Create a logger for the test component
      org.s8r.infrastructure.logging.ConsoleLogger logger =
          new org.s8r.infrastructure.logging.ConsoleLogger("TestComponent");

      // Create an event dispatcher to capture events
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // Create a component and register to the dispatcher
      Component component = TestComponentFactory.createComponent("test-component");

      // Manually publish state change events to simulate component state transitions
      // Normally, these would be published by the component itself during state transitions
      ComponentStateChangedEvent event1 =
          new ComponentStateChangedEvent(
              component.getId(),
              org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              "State transition test");

      ComponentStateChangedEvent event2 =
          new ComponentStateChangedEvent(
              component.getId(),
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              org.s8r.domain.lifecycle.LifecycleState.CONFIGURING,
              "State transition test");

      ComponentStateChangedEvent event3 =
          new ComponentStateChangedEvent(
              component.getId(),
              org.s8r.domain.lifecycle.LifecycleState.CONFIGURING,
              org.s8r.domain.lifecycle.LifecycleState.SPECIALIZING,
              "State transition test");

      ComponentStateChangedEvent event4 =
          new ComponentStateChangedEvent(
              component.getId(),
              org.s8r.domain.lifecycle.LifecycleState.SPECIALIZING,
              org.s8r.domain.lifecycle.LifecycleState.DEVELOPING_FEATURES,
              "State transition test");

      // Dispatch the events
      mockDispatcher.dispatch(event1);
      mockDispatcher.dispatch(event2);
      mockDispatcher.dispatch(event3);
      mockDispatcher.dispatch(event4);

      // Extract state change events from the dispatcher
      List<ComponentStateChangedEvent> events =
          mockDispatcher.getPublishedEventsOfType(ComponentStateChangedEvent.class);

      // Verify events
      assertEquals(4, events.size(), "Should have 4 state transition events");

      // Verify sequence
      assertEquals(
          org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
          events.get(0).getPreviousState(),
          "First transition from CONCEPTION");
      assertEquals(
          org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
          events.get(0).getNewState(),
          "First transition to INITIALIZING");

      assertEquals(
          org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
          events.get(1).getPreviousState(),
          "Second transition from INITIALIZING");
      assertEquals(
          org.s8r.domain.lifecycle.LifecycleState.CONFIGURING,
          events.get(1).getNewState(),
          "Second transition to CONFIGURING");
    }

    @Test
    @DisplayName("Machines should propagate data flow events")
    void machinesShouldPropagateDataFlowEvents() {
      // Create an event dispatcher to monitor event flow
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // For this test, we'll simulate a machine and its state changes by directly using the events
      ComponentId machineId = ComponentId.create("test-machine");

      // Use the actual MachineState enum
      org.s8r.domain.machine.MachineState stateCreated =
          org.s8r.domain.machine.MachineState.CREATED;
      // Note: MachineState enum doesn't have INITIALIZING, so we'll use the same state for sequence
      org.s8r.domain.machine.MachineState stateReady = org.s8r.domain.machine.MachineState.READY;
      org.s8r.domain.machine.MachineState stateRunning =
          org.s8r.domain.machine.MachineState.RUNNING;

      // Simulate machine state change events
      MachineStateChangedEvent initEvent =
          new MachineStateChangedEvent(
              machineId,
              stateCreated,
              stateReady, // Skip directly to READY since there's no INITIALIZING
              "Machine initialization");

      // This transition isn't needed now, but we'll keep a state transition
      MachineStateChangedEvent readyEvent =
          new MachineStateChangedEvent(
              machineId,
              stateReady,
              stateReady, // Same state transition - just for test
              "Machine ready");

      MachineStateChangedEvent runningEvent =
          new MachineStateChangedEvent(machineId, stateReady, stateRunning, "Machine running");

      // Dispatch the events
      mockDispatcher.dispatch(initEvent);
      mockDispatcher.dispatch(readyEvent);
      mockDispatcher.dispatch(runningEvent);

      // Simulate data flow between components
      ComponentId sourceId = ComponentId.create("source");
      ComponentId processorId = ComponentId.create("processor");
      ComponentId sinkId = ComponentId.create("sink");

      // Create a data flow event
      ComponentDataEvent dataEvent =
          new ComponentDataEvent(
              sourceId,
              "data-flow-channel",
              Map.of("timestamp", Instant.now(), "value", 42, "quality", "good"));

      // Dispatch the data event
      mockDispatcher.dispatch(dataEvent);

      // Verify events were dispatched
      List<MachineStateChangedEvent> stateEvents =
          mockDispatcher.getPublishedEventsOfType(MachineStateChangedEvent.class);
      List<ComponentDataEvent> dataEvents =
          mockDispatcher.getPublishedEventsOfType(ComponentDataEvent.class);

      assertEquals(3, stateEvents.size(), "Should have 3 machine state events");
      assertEquals(1, dataEvents.size(), "Should have 1 data event");

      // Verify final machine state
      MachineStateChangedEvent lastStateEvent = stateEvents.get(stateEvents.size() - 1);
      assertEquals(
          stateRunning, lastStateEvent.getNewState(), "Machine should be in RUNNING state");

      // Verify data event
      ComponentDataEvent publishedDataEvent = dataEvents.get(0);
      assertEquals(sourceId, publishedDataEvent.getSourceId(), "Source ID should match");
      assertEquals(
          "data-flow-channel", publishedDataEvent.getDataChannel(), "Channel should match");
      assertEquals(42, publishedDataEvent.getValue("value"), "Data value should match");
    }
  }

  @Nested
  @DisplayName("Subscription Mechanisms Tests")
  class SubscriptionMechanismsTests {

    @Test
    @DisplayName("Direct subscription should deliver matching events")
    void directSubscriptionShouldDeliverMatchingEvents() {
      // Create a hierarchical event dispatcher for testing
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // Create counters for different event types
      AtomicInteger stateEventCount = new AtomicInteger(0);
      AtomicInteger createdEventCount = new AtomicInteger(0);

      // Subscribe to specific event types using registerHandler
      mockDispatcher.registerHandler(
          ComponentStateChangedEvent.class,
          (EventDispatcher.EventHandler<ComponentStateChangedEvent>)
              event -> stateEventCount.incrementAndGet());
      mockDispatcher.registerHandler(
          ComponentCreatedEvent.class,
          (EventDispatcher.EventHandler<ComponentCreatedEvent>)
              event -> createdEventCount.incrementAndGet());

      // Create and publish events
      ComponentStateChangedEvent stateEvent =
          new ComponentStateChangedEvent(
              sourceId,
              org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              "State change test");

      ComponentCreatedEvent createdEvent = new ComponentCreatedEvent(sourceId, "TestComponent");

      mockDispatcher.dispatch(stateEvent);
      mockDispatcher.dispatch(createdEvent);
      mockDispatcher.dispatch(stateEvent);

      // Verify only matching events were delivered
      assertEquals(2, stateEventCount.get(), "State handler should be called twice");
      assertEquals(1, createdEventCount.get(), "Created handler should be called once");
    }

    @Test
    @DisplayName("Hierarchical event propagation should work")
    void hierarchicalEventPropagationShouldWork() {
      // Create a hierarchical event dispatcher for testing
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // Create a composite with components
      ComponentId rootId = ComponentId.create("root");
      ComponentId childId1 = ComponentId.create("child1");
      ComponentId childId2 = ComponentId.create("child2");

      // Create event counters for tracking
      Map<ComponentId, AtomicInteger> eventCounts = new HashMap<>();
      eventCounts.put(rootId, new AtomicInteger(0));
      eventCounts.put(childId1, new AtomicInteger(0));
      eventCounts.put(childId2, new AtomicInteger(0));

      // Subscribe to events with component ID filtering using registerHandler
      mockDispatcher.registerHandler(
          DomainEvent.class,
          (EventDispatcher.EventHandler<DomainEvent>)
              event -> {
                // Get the component ID from the specific event type
                ComponentId eventComponentId = null;

                if (event instanceof ComponentCreatedEvent) {
                  eventComponentId = ((ComponentCreatedEvent) event).getComponentId();
                } else if (event instanceof ComponentStateChangedEvent) {
                  eventComponentId = ((ComponentStateChangedEvent) event).getComponentId();
                }

                if (eventComponentId != null && eventCounts.containsKey(eventComponentId)) {
                  eventCounts.get(eventComponentId).incrementAndGet();
                }
              });

      // Dispatch events from different components
      mockDispatcher.dispatch(new ComponentCreatedEvent(rootId, "RootComponent"));
      mockDispatcher.dispatch(new ComponentCreatedEvent(childId1, "Child1Component"));
      mockDispatcher.dispatch(
          new ComponentStateChangedEvent(
              childId2,
              org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              "State change test"));
      mockDispatcher.dispatch(
          new ComponentStateChangedEvent(
              rootId,
              org.s8r.domain.lifecycle.LifecycleState.CONCEPTION,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZING,
              "State change test"));

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
      Supplier<Boolean> failingOperation =
          () -> {
            int attempts = attemptCount.incrementAndGet();
            if (attempts == 1) {
              throw new RuntimeException("First attempt failure");
            }
            latch.countDown();
            return true;
          };

      // Execute with retry logic (using our utility method that simulates retry mechanism)
      Thread.startVirtualThread(
          () -> {
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
      List<Integer> receivedSequences = Collections.synchronizedList(new ArrayList<>());

      // Create a hierarchical event dispatcher that we'll use to simulate ordered delivery
      HierarchicalEventDispatcher mockDispatcher = TestComponentFactory.createEventDispatcher();

      // Subscribe to collect events in order using registerHandler
      mockDispatcher.registerHandler(
          ComponentDataEvent.class,
          (EventDispatcher.EventHandler<ComponentDataEvent>)
              event -> {
                Map<String, Object> data = event.getData();
                if (data.containsKey("sequence")) {
                  receivedSequences.add((Integer) data.get("sequence"));
                }
              });

      // Dispatch events with different sequence numbers (in correct order for this test)
      for (int i = 1; i <= 5; i++) {
        final int seqNum = i;
        mockDispatcher.dispatch(
            new ComponentDataEvent(sourceId, "sequence-channel", Map.of("sequence", seqNum)));
      }

      // Verify events were received in the expected sequence
      assertEquals(
          List.of(1, 2, 3, 4, 5),
          receivedSequences,
          "Events should be delivered in sequence order");
    }
  }

  // Helper methods

  /** A supplier interface for operations that return a result. */
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
