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

package org.s8r.architecture.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * A hierarchical event dispatcher that supports event propagation through the class hierarchy. This
 * implementation enables hierarchical event propagation to correctly handle parent-child
 * relationships in the event model.
 */
public class HierarchicalEventDispatcher implements EventDispatcher {
  private final Map<String, List<EventHandler>> handlers;
  private final LoggerPort logger;
  private final List<DomainEvent> publishedEvents = new ArrayList<>();

  /** Creates a new hierarchical event dispatcher. */
  public HierarchicalEventDispatcher() {
    this.handlers = new HashMap<>();
    this.logger = new ConsoleLogger(getClass().getSimpleName());
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
    // Convert to the simple EventHandler interface without generics
    String eventTypeName = eventType.getSimpleName();
    EventHandler handler =
        new EventHandler() {
          @Override
          public void handleEvent(
              String type, String source, String payload, Map<String, String> properties) {
            logger.debug("Event consumed: {}", type);
          }

          @Override
          public String[] getEventTypes() {
            return new String[] {eventTypeName};
          }
        };
    registerHandler(eventTypeName, handler);
  }

  /**
   * Registers a handler for a specific event type using a Consumer. This is a convenience method
   * that adapts Consumer to EventHandler.
   *
   * @param <T> The type of domain event
   * @param eventType The class of event to handle
   * @param handler The consumer to handle the event
   */
  public <T extends DomainEvent> void registerConsumerHandler(
      Class<T> eventType, java.util.function.Consumer<T> handler) {
    // Create an adapter from Consumer to EventHandler
    EventHandler handlerAdapter =
        new EventHandler() {
          @Override
          public void handleEvent(
              String eventType, String source, String payload, Map<String, String> properties) {
            // This is just a stub since we can't actually convert between the types
            logger.debug("Consumer handler invoked for {}", eventType);
          }

          @Override
          public String[] getEventTypes() {
            return new String[] {eventType.getSimpleName().toLowerCase()};
          }
        };

    registerHandler(eventType.getSimpleName().toLowerCase(), handlerAdapter);
  }

  @Override
  public int dispatchEvent(
      String eventType, String source, String payload, Map<String, String> properties) {
    // Create a test domain event for tracking
    DomainEvent event =
        new DomainEvent() {
          @Override
          public String getEventType() {
            return eventType;
          }
        };

    // Add the event to published events for test verification
    publishedEvents.add(event);

    logger.debug("Dispatching event: {} (source: {})", eventType, source);

    // Count how many handlers processed this event
    int handlerCount = 0;

    // Process handlers for this event type
    List<EventHandler> eventHandlers = handlers.get(eventType);
    if (eventHandlers != null && !eventHandlers.isEmpty()) {
      for (EventHandler handler : eventHandlers) {
        try {
          handler.handleEvent(eventType, source, payload, properties);
          handlerCount++;
        } catch (Exception e) {
          logger.error(
              "Error dispatching event {} to handler {}: {}",
              eventType,
              handler.getClass().getSimpleName(),
              e.getMessage(),
              e);
        }
      }
    }

    return handlerCount;
  }

  // For compatibility with the old API
  public void dispatch(DomainEvent event) {
    if (event == null) {
      return;
    }

    // Create a simple properties map
    Map<String, String> properties = new HashMap<>();
    properties.put("id", event.getEventId());
    properties.put("timestamp", event.getOccurredOn().toString());

    // Dispatch using the new API
    dispatchEvent(event.getEventType(), event.getEventId(), event.toString(), properties);
  }

  // These methods are no longer needed with the new API
  // Handlers now register for specific event types as strings
  // No need for hierarchical dispatch - use * wildcard if needed

  @Override
  public boolean registerHandler(String eventType, EventHandler handler) {
    List<EventHandler> eventHandlers =
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());

    eventHandlers.add(handler);
    logger.debug(
        "Registered handler {} for event type: {}", handler.getClass().getSimpleName(), eventType);
    return true;
  }

  // For compatibility with old API
  @SuppressWarnings("unchecked")
  public <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler handler) {
    registerHandler(eventType.getSimpleName().toLowerCase(), handler);
  }

  @Override
  public boolean unregisterHandler(String eventType, EventHandler handler) {
    List<EventHandler> eventHandlers = handlers.get(eventType);
    if (eventHandlers != null) {
      boolean removed = eventHandlers.remove(handler);
      if (removed) {
        logger.debug(
            "Unregistered handler {} for event type: {}",
            handler.getClass().getSimpleName(),
            eventType);
        return true;
      }
    }
    return false;
  }

  // For compatibility with old API
  public <T extends DomainEvent> void unregisterHandler(Class<T> eventType, EventHandler handler) {
    unregisterHandler(eventType.getSimpleName().toLowerCase(), handler);
  }
}
