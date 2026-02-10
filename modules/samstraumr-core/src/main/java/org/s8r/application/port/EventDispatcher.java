/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.util.Map;

import org.s8r.domain.event.DomainEvent;

/**
 * Port interface for event dispatching in the application layer.
 *
 * <p>This interface defines the operations for registering event handlers and dispatching events,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface EventDispatcher {

  /**
   * Registers an event handler.
   *
   * @param eventType The event type to register for
   * @param handler The handler to register
   * @return true if the handler was registered successfully, false otherwise
   */
  boolean registerHandler(String eventType, EventHandler handler);

  /**
   * Unregisters an event handler.
   *
   * @param eventType The event type to unregister from
   * @param handler The handler to unregister
   * @return true if the handler was unregistered successfully, false otherwise
   */
  boolean unregisterHandler(String eventType, EventHandler handler);

  /**
   * Dispatches an event to all registered handlers.
   *
   * @param eventType The type of the event
   * @param source The source of the event
   * @param payload The event payload
   * @param properties Additional properties of the event
   * @return The number of handlers that processed the event
   */
  int dispatchEvent(
      String eventType, String source, String payload, Map<String, String> properties);

  /**
   * Dispatches a domain event to all registered handlers.
   *
   * @param event The domain event to dispatch
   */
  void dispatch(DomainEvent event);
}
