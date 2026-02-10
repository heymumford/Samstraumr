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
package org.s8r.infrastructure.event;

import java.util.Arrays;
import java.util.Map;

import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LoggerPort;

/** Event handler that logs events. */
public class LoggingEventHandler implements EventHandler {

  private final LoggerPort logger;
  private final String[] eventTypes;

  /**
   * Creates a new LoggingEventHandler that handles all events.
   *
   * @param logger The logger to use
   */
  public LoggingEventHandler(LoggerPort logger) {
    this(logger, new String[] {"*"});
  }

  /**
   * Creates a new LoggingEventHandler that handles the specified event types.
   *
   * @param logger The logger to use
   * @param eventTypes The event types to handle
   */
  public LoggingEventHandler(LoggerPort logger, String[] eventTypes) {
    this.logger = logger;
    this.eventTypes = eventTypes;
  }

  @Override
  public void handleEvent(
      String eventType, String source, String payload, Map<String, String> properties) {
    logger.info(
        "Event received - Type: {}, Source: {}, Payload: {}, Properties: {}",
        eventType,
        source,
        payload,
        properties);
  }

  @Override
  public String[] getEventTypes() {
    return Arrays.copyOf(eventTypes, eventTypes.length);
  }

  /**
   * Creates a specialized event handler for component creation events.
   *
   * @return A specialized event handler for component creation events
   */
  public EventHandler componentCreatedHandler() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        logger.info(
            "Component created - ID: {}, Type: {}",
            source,
            properties.getOrDefault("type", "unknown"));
      }

      @Override
      public String[] getEventTypes() {
        return new String[] {"component.created"};
      }
    };
  }

  /**
   * Creates an updated specialized event handler for component creation events.
   *
   * @return A specialized event handler for component creation events
   */
  public EventHandler componentCreatedHandlerNew() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        logger.info(
            "Component created (new) - ID: {}, Type: {}",
            source,
            properties.getOrDefault("type", "unknown"));
      }

      @Override
      public String[] getEventTypes() {
        return new String[] {"component.created.v2"};
      }
    };
  }

  /**
   * Creates a specialized event handler for component state change events.
   *
   * @return A specialized event handler for component state change events
   */
  public EventHandler componentStateChangedHandler() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        logger.info(
            "Component state changed - ID: {}, From: {}, To: {}",
            source,
            properties.getOrDefault("previousState", "unknown"),
            properties.getOrDefault("newState", "unknown"));
      }

      @Override
      public String[] getEventTypes() {
        return new String[] {"component.state.changed"};
      }
    };
  }

  /**
   * Creates a specialized event handler for component connection events.
   *
   * @return A specialized event handler for component connection events
   */
  public EventHandler componentConnectionHandler() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        logger.info(
            "Component connection - Source: {}, Target: {}, Status: {}",
            source,
            properties.getOrDefault("targetId", "unknown"),
            properties.getOrDefault("connectionStatus", "established"));
      }

      @Override
      public String[] getEventTypes() {
        return new String[] {"component.connection.created", "component.connection.removed"};
      }
    };
  }

  /**
   * Creates a specialized event handler for machine state change events.
   *
   * @return A specialized event handler for machine state change events
   */
  public EventHandler machineStateChangedHandler() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        logger.info(
            "Machine state changed - ID: {}, From: {}, To: {}",
            source,
            properties.getOrDefault("previousState", "unknown"),
            properties.getOrDefault("newState", "unknown"));
      }

      @Override
      public String[] getEventTypes() {
        return new String[] {"machine.state.changed"};
      }
    };
  }
}
