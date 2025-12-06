/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LoggerPort;

/**
 * In-memory implementation of the EventDispatcher interface.
 *
 * <p>This adapter provides a simple in-memory event dispatching system. It is primarily intended
 * for testing and development environments.
 */
public class InMemoryEventDispatcher implements EventDispatcher {

  private final Map<String, List<EventHandler>> handlers = new ConcurrentHashMap<>();
  private final LoggerPort logger;

  public InMemoryEventDispatcher(LoggerPort logger) {
    this.logger = logger;
  }

  @Override
  public boolean registerHandler(String eventType, EventHandler handler) {
    handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    return true;
  }

  @Override
  public boolean unregisterHandler(String eventType, EventHandler handler) {
    if (!handlers.containsKey(eventType)) {
      return false;
    }

    boolean removed = handlers.get(eventType).remove(handler);
    if (handlers.get(eventType).isEmpty()) {
      handlers.remove(eventType);
    }

    return removed;
  }

  @Override
  public int dispatchEvent(
      String eventType, String source, String payload, Map<String, String> properties) {
    if (!handlers.containsKey(eventType)) {
      // No handlers for this event type
      return 0;
    }

    int handlerCount = 0;

    // Notify all handlers
    for (EventHandler handler : handlers.get(eventType)) {
      try {
        // Check if the handler is interested in this event type
        boolean shouldHandle = false;
        String[] handlerEventTypes = handler.getEventTypes();
        if (handlerEventTypes != null) {
          for (String type : handlerEventTypes) {
            if (type.equals(eventType) || type.equals("*")) {
              shouldHandle = true;
              break;
            }
          }
        }

        if (shouldHandle) {
          handler.handleEvent(eventType, source, payload, properties);
          handlerCount++;
        }
      } catch (Exception e) {
        // Log the exception but continue processing other handlers
        if (logger != null) {
          logger.error("Error in event handler for type " + eventType + ": " + e.getMessage(), e);
        } else {
          System.err.println(
              "Error in event handler for type " + eventType + ": " + e.getMessage());
        }
      }
    }

    return handlerCount;
  }

  /**
   * Utility method to dispatch an event with a map of data
   *
   * @param eventType The type of event
   * @param eventData The event data
   * @return true if dispatched successfully
   */
  public boolean dispatchEvent(String eventType, Map<String, Object> eventData) {
    String source = (String) eventData.getOrDefault("source", "system");
    String payload = (String) eventData.getOrDefault("payload", "{}");

    // Convert to string map for properties
    Map<String, String> properties = new HashMap<>();
    for (Map.Entry<String, Object> entry : eventData.entrySet()) {
      if (entry.getValue() != null) {
        properties.put(entry.getKey(), entry.getValue().toString());
      }
    }

    return dispatchEvent(eventType, source, payload, properties) > 0;
  }
}
