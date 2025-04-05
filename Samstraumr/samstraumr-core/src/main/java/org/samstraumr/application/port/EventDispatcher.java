/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Event dispatcher port for the S8r framework
 */

package org.samstraumr.application.port;

import org.samstraumr.domain.event.DomainEvent;

/**
 * Port interface for dispatching domain events.
 *
 * <p>This port follows the Clean Architecture approach by defining an outbound port in the
 * application layer, allowing the business logic to remain independent of infrastructure concerns.
 */
public interface EventDispatcher {

  /**
   * Dispatches a domain event to all registered handlers.
   *
   * @param event The domain event to dispatch
   */
  void dispatch(DomainEvent event);

  /**
   * Registers a handler for a specific event type.
   *
   * @param <T> The type of domain event
   * @param eventType The class of the event type
   * @param handler The handler that will process events of this type
   */
  <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler);

  /**
   * Unregisters a handler for a specific event type.
   *
   * @param <T> The type of domain event
   * @param eventType The class of the event type
   * @param handler The handler to unregister
   */
  <T extends DomainEvent> void unregisterHandler(Class<T> eventType, EventHandler<T> handler);

  /**
   * Functional interface for event handlers.
   *
   * @param <T> The type of domain event this handler can process
   */
  @FunctionalInterface
  interface EventHandler<T extends DomainEvent> {

    /**
     * Handles a domain event.
     *
     * @param event The event to handle
     */
    void handle(T event);
  }
}
