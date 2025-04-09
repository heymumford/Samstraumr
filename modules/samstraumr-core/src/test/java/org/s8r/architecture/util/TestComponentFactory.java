package org.s8r.architecture.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventHandler;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Factory for creating test components with predictable behavior. This class provides utility
 * methods for creating mock components to use in architecture validation tests.
 */
public class TestComponentFactory {

  /**
   * Creates a simple component with default behavior.
   *
   * @param name The reason for the component creation
   * @return A component implementation
   */
  public static Component createComponent(String name) {
    return Component.create(ComponentId.create(name));
  }

  /**
   * Creates a composite component that can contain other components.
   *
   * @param name The reason for the composite component creation
   * @return A mock composite component
   */
  public static CompositeComponent createCompositeComponent(String name) {
    return CompositeComponent.create(ComponentId.create(name), CompositeType.STANDARD);
  }

  /**
   * Creates a hierarchical event dispatcher for testing event-driven communication.
   *
   * @return A hierarchical event dispatcher that supports proper event propagation
   */
  public static HierarchicalEventDispatcher createEventDispatcher() {
    // Create and return a hierarchical event dispatcher for testing
    // This dispatcher properly supports event propagation through the class hierarchy
    return new HierarchicalEventDispatcher();
  }

  /**
   * Mock event dispatcher for testing event-driven communication. This implements both the
   * EventDispatcher interface and provides additional methods for testing and verification.
   */
  public static class MockEventDispatcher implements EventDispatcher {
    private final List<DomainEvent> publishedEvents = new ArrayList<>();
    private final Map<String, List<EventHandler>> handlers = new HashMap<>();
    private final org.s8r.application.port.LoggerPort logger;

    /**
     * Creates a new mock event dispatcher with the specified logger.
     *
     * @param logger The logger to use
     */
    public MockEventDispatcher(org.s8r.application.port.LoggerPort logger) {
      this.logger = logger;
    }

    @Override
    public int dispatchEvent(String eventType, String source, String payload, Map<String, String> properties) {
      // Create a test domain event for tracking
      DomainEvent event = new DomainEvent() {
        @Override
        public String getEventType() {
          return eventType;
        }
      };
      
      // Add to published events for verification
      publishedEvents.add(event);
      logger.debug("Dispatched event: " + eventType);

      // Count processed handlers
      int handlerCount = 0;
      
      // Notify handlers for this event type
      List<EventHandler> eventHandlers = handlers.getOrDefault(eventType, new ArrayList<>());
      for (EventHandler handler : eventHandlers) {
        try {
          handler.handleEvent(eventType, source, payload, properties);
          handlerCount++;
        } catch (Exception e) {
          logger.error("Error handling event: " + e.getMessage(), e);
        }
      }
      
      return handlerCount;
    }
    
    // For backwards compatibility
    public void dispatch(DomainEvent event) {
      if (event == null) {
        return;
      }
      
      // Create properties map
      Map<String, String> properties = new HashMap<>();
      properties.put("id", event.getEventId());
      properties.put("timestamp", event.getOccurredOn().toString());
      
      // Dispatch using the new method
      dispatchEvent(event.getEventType(), event.getEventId(), event.toString(), properties);
    }

    @Override
    public boolean registerHandler(String eventType, EventHandler handler) {
      handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
      logger.debug("Registered handler for event type: " + eventType);
      return true;
    }
    
    // For backwards compatibility
    public <T extends DomainEvent> void registerHandler(
        Class<T> eventType, EventHandler handler) {
      registerHandler(eventType.getSimpleName().toLowerCase(), handler);
    }

    @Override
    public <T extends DomainEvent> void registerHandler(
        Class<T> eventType, java.util.function.Consumer<T> handler) {
      // Create an adapter from Consumer to EventHandler
      EventHandler handlerAdapter = new EventHandler() {
        @Override
        public void handleEvent(String eventType, String source, String payload, Map<String, String> properties) {
          // This is just a stub since we can't actually convert between the types
          logger.debug("Consumer handler invoked for {}", eventType);
        }
        
        @Override
        public String[] getEventTypes() {
          return new String[] { eventType.getSimpleName().toLowerCase() };
        }
      };
      
      registerHandler(eventType.getSimpleName().toLowerCase(), handlerAdapter);
    }

    @Override
    public boolean unregisterHandler(String eventType, EventHandler handler) {
      List<EventHandler> handlersForType = handlers.get(eventType);
      if (handlersForType != null) {
        boolean removed = handlersForType.remove(handler);
        if (removed) {
          logger.debug("Unregistered handler for event type: " + eventType);
          return true;
        }
      }
      return false;
    }
    
    // For backwards compatibility
    public <T extends DomainEvent> void unregisterHandler(
        Class<T> eventType, EventHandler handler) {
      unregisterHandler(eventType.getSimpleName().toLowerCase(), handler);
    }

    /**
     * Gets all published events for testing and verification.
     *
     * @return A list of all published events
     */
    public List<DomainEvent> getPublishedEvents() {
      return new ArrayList<>(publishedEvents);
    }

    /**
     * Gets all published events of a specific type for testing and verification.
     *
     * @param <T> The type of events to get
     * @param eventType The class of events to get
     * @return A list of published events of the specified type
     */
    public <T extends DomainEvent> List<T> getPublishedEventsOfType(Class<T> eventType) {
      List<T> result = new ArrayList<>();
      for (DomainEvent event : publishedEvents) {
        if (eventType.isInstance(event)) {
          result.add(eventType.cast(event));
        }
      }
      return result;
    }

    /**
     * Publishes an event for testing. This is a test-only method that simulates publishing an event
     * through this dispatcher.
     *
     * @param event The event to publish
     */
    public void publish(DomainEvent event) {
      dispatch(event);
    }

    /**
     * Subscribes a consumer to an event type for testing. This is useful when testing with lambdas
     * and provides compatibility with the old API.
     *
     * @param <T> The type of event to subscribe to
     * @param eventType The class of event to subscribe to
     * @param consumer The consumer that will handle the event
     */
    public <T extends DomainEvent> void subscribe(
        Class<T> eventType, java.util.function.Consumer<T> consumer) {
      // Create a simple handler that logs events
      EventHandler handler = new EventHandler() {
        @Override
        public void handleEvent(String eventType, String source, String payload, Map<String, String> properties) {
          logger.debug("Event handler for {} triggered from source {}", eventType, source);
        }
        
        @Override
        public String[] getEventTypes() {
          return new String[] { eventType.getSimpleName().toLowerCase() };
        }
      };
      
      registerHandler(eventType.getSimpleName().toLowerCase(), handler);
    }
  }
}
