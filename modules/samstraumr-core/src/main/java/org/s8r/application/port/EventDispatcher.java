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

package org.s8r.application.port;

import org.s8r.domain.event.DomainEvent;

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
   * Registers a consumer function as a handler for a specific event type.
   *
   * @param <T> The type of domain event
   * @param eventType The class of the event type
   * @param handler The consumer function that will process events of this type
   */
  <T extends DomainEvent> void registerHandler(
      Class<T> eventType, java.util.function.Consumer<T> handler);

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
