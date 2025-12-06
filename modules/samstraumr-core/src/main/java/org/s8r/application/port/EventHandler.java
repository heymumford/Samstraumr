/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.util.Map;

/**
 * Interface for event handlers in the application layer.
 *
 * <p>This interface defines the contract for handling events within the application, following the
 * ports and adapters pattern from Clean Architecture.
 */
public interface EventHandler {

  /**
   * Handles an event.
   *
   * @param eventType The type of the event
   * @param source The source of the event
   * @param payload The event payload
   * @param properties Additional properties of the event
   */
  void handleEvent(String eventType, String source, String payload, Map<String, String> properties);

  /**
   * Gets the event types this handler is interested in.
   *
   * @return An array of event types
   */
  String[] getEventTypes();
}
