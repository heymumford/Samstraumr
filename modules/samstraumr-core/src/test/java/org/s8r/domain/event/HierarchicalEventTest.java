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

package org.s8r.domain.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.event.InMemoryEventDispatcher;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the hierarchical event propagation system, validating that events properly propagate
 * through the inheritance hierarchy to appropriate handlers.
 *
 * <p>This test suite demonstrates the inheritance-based event propagation system, where handlers
 * registered for parent event types receive events of child types.
 */
@UnitTest
@DisplayName("Hierarchical Event Propagation Tests")
public class HierarchicalEventTest {

  @Mock private LoggerPort mockLogger;

  private EventDispatcher eventDispatcher;

  // Test event handlers
  private TestEventHandler baseEventHandler;
  private TestEventHandler componentEventHandler;
  private TestEventHandler createdEventHandler;
  private TestEventHandler stateChangedEventHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Create the event dispatcher
    eventDispatcher = new InMemoryEventDispatcher(mockLogger);

    // Create test event handlers
    baseEventHandler = new TestEventHandler("domain.event");
    componentEventHandler = new TestEventHandler("component.event");
    createdEventHandler = new TestEventHandler("component.created");
    stateChangedEventHandler = new TestEventHandler("component.state.changed");

    // Register the event handlers
    eventDispatcher.registerHandler("domain.event", baseEventHandler);
    eventDispatcher.registerHandler("component.event", componentEventHandler);
    eventDispatcher.registerHandler("component.created", createdEventHandler);
    eventDispatcher.registerHandler("component.state.changed", stateChangedEventHandler);
  }

  @Nested
  @DisplayName("Basic Event Propagation Tests")
  class BasicEventPropagationTests {

    @Test
    @DisplayName("Base handlers should receive derived events")
    void baseHandlersShouldReceiveDerivedEvents() {
      // Create a component event
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent event = new ComponentCreatedEvent(componentId, "TestComponent");

      // Dispatch the event
      eventDispatcher.dispatch(event);

      // Verify that all appropriate handlers received the event
      assertEquals(
          1,
          baseEventHandler.getReceivedEvents().size(),
          "Base event handler should receive the event");
      assertEquals(
          1,
          componentEventHandler.getReceivedEvents().size(),
          "Component event handler should receive the event");
      assertEquals(
          1,
          createdEventHandler.getReceivedEvents().size(),
          "Created event handler should receive the event");
      assertEquals(
          0,
          stateChangedEventHandler.getReceivedEvents().size(),
          "State changed event handler should not receive the event");

      // Verify the event is correctly passed to each handler
      assertSame(
          event,
          baseEventHandler.getReceivedEvents().get(0),
          "Base handler should receive the exact event instance");
      assertSame(
          event,
          componentEventHandler.getReceivedEvents().get(0),
          "Component handler should receive the exact event instance");
      assertSame(
          event,
          createdEventHandler.getReceivedEvents().get(0),
          "Created handler should receive the exact event instance");
    }

    @Test
    @DisplayName("State changed events should propagate correctly")
    void stateChangedEventsShouldPropagateCorrectly() {
      // Create a state changed event
      ComponentId componentId = ComponentId.create("test-component");
      org.s8r.domain.lifecycle.LifecycleState oldState =
          org.s8r.domain.lifecycle.LifecycleState.INITIALIZED;
      org.s8r.domain.lifecycle.LifecycleState newState =
          org.s8r.domain.lifecycle.LifecycleState.RUNNING;
      ComponentStateChangedEvent event =
          new ComponentStateChangedEvent(componentId, oldState, newState, "Test state transition");

      // Dispatch the event
      eventDispatcher.dispatch(event);

      // Verify that all appropriate handlers received the event
      assertEquals(
          1,
          baseEventHandler.getReceivedEvents().size(),
          "Base event handler should receive the event");
      assertEquals(
          1,
          componentEventHandler.getReceivedEvents().size(),
          "Component event handler should receive the event");
      assertEquals(
          0,
          createdEventHandler.getReceivedEvents().size(),
          "Created event handler should not receive the event");
      assertEquals(
          1,
          stateChangedEventHandler.getReceivedEvents().size(),
          "State changed event handler should receive the event");

      // Verify the event is correctly passed to the state changed handler
      ComponentStateChangedEvent receivedEvent =
          (ComponentStateChangedEvent) stateChangedEventHandler.getReceivedEvents().get(0);
      assertEquals(componentId, receivedEvent.getComponentId(), "Component ID should match");
      assertEquals(oldState, receivedEvent.getPreviousState(), "Old state should match");
      assertEquals(newState, receivedEvent.getNewState(), "New state should match");
    }
  }

  @Nested
  @DisplayName("Advanced Event Propagation Tests")
  class AdvancedEventPropagationTests {

    @Test
    @DisplayName("Multiple event types should be handled independently")
    void multipleEventTypesShouldBeHandledIndependently() {
      // Create different event types
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent createdEvent = new ComponentCreatedEvent(componentId, "TestComponent");
      ComponentStateChangedEvent stateEvent =
          new ComponentStateChangedEvent(
              componentId,
              org.s8r.domain.lifecycle.LifecycleState.INITIALIZED,
              org.s8r.domain.lifecycle.LifecycleState.RUNNING,
              "Test transition");
      ComponentConnectionEvent connectionEvent =
          new ComponentConnectionEvent(
              componentId, ComponentId.create("other-component"), "connected");

      // Dispatch the events
      eventDispatcher.dispatch(createdEvent);
      eventDispatcher.dispatch(stateEvent);
      eventDispatcher.dispatch(connectionEvent);

      // Verify base handlers received all events
      assertEquals(
          3,
          baseEventHandler.getReceivedEvents().size(),
          "Base event handler should receive all events");
      assertEquals(
          3,
          componentEventHandler.getReceivedEvents().size(),
          "Component event handler should receive all events");

      // Verify specific handlers received only their events
      assertEquals(
          1,
          createdEventHandler.getReceivedEvents().size(),
          "Created event handler should receive only created events");
      assertEquals(
          1,
          stateChangedEventHandler.getReceivedEvents().size(),
          "State changed event handler should receive only state events");

      // Verify the specific events in base handlers
      assertTrue(
          baseEventHandler.getReceivedEvents().contains(createdEvent),
          "Base handler should receive created event");
      assertTrue(
          baseEventHandler.getReceivedEvents().contains(stateEvent),
          "Base handler should receive state event");
      assertTrue(
          baseEventHandler.getReceivedEvents().contains(connectionEvent),
          "Base handler should receive connection event");
    }

    @Test
    @DisplayName("Unregistering handlers should stop event delivery")
    void unregisteringHandlersShouldStopEventDelivery() {
      // Unregister the component event handler
      eventDispatcher.unregisterHandler(ComponentEvent.class, componentEventHandler);

      // Create and dispatch an event
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent event = new ComponentCreatedEvent(componentId, "TestComponent");
      eventDispatcher.dispatch(event);

      // Verify that the unregistered handler didn't receive the event
      assertEquals(
          1,
          baseEventHandler.getReceivedEvents().size(),
          "Base event handler should still receive the event");
      assertEquals(
          0,
          componentEventHandler.getReceivedEvents().size(),
          "Unregistered component event handler should not receive the event");
      assertEquals(
          1,
          createdEventHandler.getReceivedEvents().size(),
          "Created event handler should still receive the event");
    }

    @Test
    @DisplayName("Handler exceptions should not affect other handlers")
    void handlerExceptionsShouldNotAffectOtherHandlers() {
      // Create a failing handler
      @SuppressWarnings("unchecked")
      EventHandler<DomainEvent> failingHandler = mock(EventHandler.class);
      doThrow(new RuntimeException("Test exception")).when(failingHandler).handle(any());

      // Register the failing handler
      eventDispatcher.registerHandler(DomainEvent.class, failingHandler);

      // Create and dispatch an event
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent event = new ComponentCreatedEvent(componentId, "TestComponent");
      eventDispatcher.dispatch(event);

      // Verify that the exception was logged
      verify(mockLogger).error(contains("Error dispatching event"), any(), any(), any());

      // Verify that other handlers still received the event
      assertEquals(
          1,
          baseEventHandler.getReceivedEvents().size(),
          "Base event handler should still receive the event despite the exception");
      assertEquals(
          1,
          componentEventHandler.getReceivedEvents().size(),
          "Component event handler should still receive the event");
      assertEquals(
          1,
          createdEventHandler.getReceivedEvents().size(),
          "Created event handler should still receive the event");
    }
  }

  @Nested
  @DisplayName("Event Metadata Tests")
  class EventMetadataTests {

    @Test
    @DisplayName("Events should contain proper metadata")
    void eventsShouldContainProperMetadata() {
      // Create a component event
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent event = new ComponentCreatedEvent(componentId, "TestComponent");

      // Verify basic metadata
      assertNotNull(event.getEventId(), "Event ID should not be null");
      assertNotNull(event.getOccurredOn(), "Timestamp should not be null");
      assertEquals(
          "ComponentCreated", event.getEventType(), "Event type should be derived from class name");
    }

    @Test
    @DisplayName("Event timestamp should reflect creation time")
    void eventTimestampShouldReflectCreationTime() {
      // Record the current time
      long before = System.currentTimeMillis();

      // Create a component event
      ComponentId componentId = ComponentId.create("test-component");
      ComponentCreatedEvent event = new ComponentCreatedEvent(componentId, "TestComponent");

      // Record the time after creation
      long after = System.currentTimeMillis();

      // Verify timestamp is within the correct range
      long eventTime = event.getOccurredOn().toEpochMilli();
      assertTrue(
          eventTime >= before && eventTime <= after,
          "Event timestamp should be between before and after times");
    }
  }

  /**
   * Test implementation of EventHandler that records received events.
   *
   * @param <T> The type of event this handler handles
   */
  private static class TestEventHandler implements EventHandler {
    private final List<DomainEvent> receivedEvents = new ArrayList<>();
    private final String[] eventTypes;

    public TestEventHandler(String... eventTypes) {
      this.eventTypes = eventTypes != null ? eventTypes : new String[] {"*"};
    }

    @Override
    public void handleEvent(
        String eventType, String source, String payload, Map<String, String> properties) {
      // Create a simple domain event to track what was received
      ComponentId componentId = ComponentId.create(source);
      DomainEvent event =
          new DomainEvent() {
            @Override
            public String getEventType() {
              return eventType;
            }
          };
      receivedEvents.add(event);
    }

    @Override
    public String[] getEventTypes() {
      return eventTypes;
    }

    public List<DomainEvent> getReceivedEvents() {
      return receivedEvents;
    }
  }

  /** Test event for component connections. */
  private static class ComponentConnectionEvent extends ComponentEvent {
    private final ComponentId targetComponentId;
    private final String connectionType;

    public ComponentConnectionEvent(
        ComponentId sourceId, ComponentId targetId, String connectionType) {
      super(sourceId);
      this.targetComponentId = targetId;
      this.connectionType = connectionType;
    }

    public ComponentId getTargetComponentId() {
      return targetComponentId;
    }

    public String getConnectionType() {
      return connectionType;
    }
  }

  /** Base class for component-related events. */
  private static class ComponentEvent extends DomainEvent {
    private final ComponentId componentId;

    public ComponentEvent(ComponentId componentId) {
      this.componentId = componentId;
    }

    public ComponentId getComponentId() {
      return componentId;
    }
  }
}
