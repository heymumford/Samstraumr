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

package org.s8r.infrastructure.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.DomainEvent;

/**
 * In-memory implementation of the EventDispatcher interface with hierarchical event dispatch.
 *
 * <p>This implementation stores event handlers in memory and dispatches events synchronously. It
 * provides thread-safe registration and dispatching of events.
 * 
 * <p>This dispatcher supports hierarchical event propagation, where events are dispatched to
 * handlers for the specific event type and also to handlers registered for the parent classes
 * in the inheritance hierarchy. This enables polymorphic event handling where handlers can
 * subscribe to base types and receive all derived events.
 */
public class InMemoryEventDispatcher implements EventDispatcher {
  private final Map<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>>
      handlers;
  private final LoggerPort logger;

  /**
   * Creates a new in-memory event dispatcher.
   *
   * @param logger The logger to use for event dispatching
   */
  public InMemoryEventDispatcher(LoggerPort logger) {
    this.handlers = new HashMap<>();
    this.logger = logger;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void dispatch(DomainEvent event) {
    if (event == null) {
      return;
    }

    // Get the event class
    Class<? extends DomainEvent> eventClass = event.getClass();
    logger.debug("Dispatching event: {} (ID: {})", eventClass.getSimpleName(), event.getEventId());

    // Process handlers for the exact event type
    dispatchToHandlers(event, eventClass);

    // Process handlers for parent classes for hierarchical propagation
    processParentEventHandlers(event, eventClass);
  }
  
  /**
   * Recursively processes handlers for parent event classes to implement hierarchical dispatch.
   * This enables polymorphic event handling where handlers registered for a parent event type
   * will receive events of derived types.
   *
   * @param event The event to dispatch
   * @param eventClass The current event class being processed
   */
  private void processParentEventHandlers(DomainEvent event, Class<? extends DomainEvent> eventClass) {
    // Get the superclass
    Class<?> superClass = eventClass.getSuperclass();
    
    // If the superclass is DomainEvent or a subclass of DomainEvent, dispatch to its handlers
    if (DomainEvent.class.isAssignableFrom(superClass) && superClass != Object.class) {
      @SuppressWarnings("unchecked")
      Class<? extends DomainEvent> parentEventClass = (Class<? extends DomainEvent>) superClass;
      
      // Dispatch to handlers for this parent class
      dispatchToHandlers(event, parentEventClass);
      
      // Continue with the next parent in the hierarchy
      processParentEventHandlers(event, parentEventClass);
    }
  }
  
  /**
   * Dispatches an event to handlers registered for a specific event type.
   *
   * @param event The event to dispatch
   * @param eventType The event type class to dispatch to
   */
  @SuppressWarnings("unchecked")
  private void dispatchToHandlers(DomainEvent event, Class<? extends DomainEvent> eventType) {
    List<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
    if (eventHandlers == null || eventHandlers.isEmpty()) {
      logger.debug("No handlers registered for event type: {}", eventType.getSimpleName());
      return;
    }

    for (EventHandler<? extends DomainEvent> handler : eventHandlers) {
      try {
        ((EventHandler<DomainEvent>) handler).handle(event);
      } catch (Exception e) {
        logger.error(
            "Error dispatching event {} to handler {}: {}",
            eventType.getSimpleName(),
            handler.getClass().getSimpleName(),
            e.getMessage(),
            e);
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler) {
    List<EventHandler<? extends DomainEvent>> eventHandlers =
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());

    eventHandlers.add(handler);
    logger.debug(
        "Registered handler {} for event type: {}",
        handler.getClass().getSimpleName(),
        eventType.getSimpleName());
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public <T extends DomainEvent> void registerHandler(Class<T> eventType, java.util.function.Consumer<T> handler) {
    // Create an adapter from the consumer to the EventHandler interface
    EventHandler<T> handlerAdapter = event -> handler.accept(event);
    
    registerHandler(eventType, handlerAdapter);
    logger.debug(
        "Registered consumer handler for event type: {}",
        eventType.getSimpleName());
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends DomainEvent> void unregisterHandler(
      Class<T> eventType, EventHandler<T> handler) {
    List<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
    if (eventHandlers != null) {
      eventHandlers.remove(handler);
      logger.debug(
          "Unregistered handler {} for event type: {}",
          handler.getClass().getSimpleName(),
          eventType.getSimpleName());
    }
  }
}
