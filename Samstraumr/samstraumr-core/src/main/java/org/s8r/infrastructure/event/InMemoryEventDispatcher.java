/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * In-memory event dispatcher implementation for the S8r framework
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
 * In-memory implementation of the EventDispatcher interface.
 *
 * <p>This implementation stores event handlers in memory and dispatches events synchronously. It
 * provides thread-safe registration and dispatching of events.
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
    Class<? extends DomainEvent> eventType = event.getClass();
    logger.debug("Dispatching event: {} (ID: {})", eventType.getSimpleName(), event.getEventId());

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
