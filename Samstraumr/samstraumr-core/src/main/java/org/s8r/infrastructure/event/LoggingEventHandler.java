/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Event handler for logging domain events in the S8r framework
 */

package org.s8r.infrastructure.event;

import org.s8r.application.port.EventDispatcher.EventHandler;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.MachineStateChangedEvent;

/**
 * Event handler that logs domain events.
 *
 * <p>This handler demonstrates how to implement the EventHandler interface for specific event types
 * and provides a useful logging capability for system monitoring and diagnostics.
 */
public class LoggingEventHandler {
  private final LoggerPort logger;

  /**
   * Creates a new logging event handler.
   *
   * @param logger The logger to use
   */
  public LoggingEventHandler(LoggerPort logger) {
    this.logger = logger;
  }

  /**
   * Creates a handler for ComponentCreatedEvent events.
   *
   * @return The event handler
   */
  public EventHandler<ComponentCreatedEvent> componentCreatedHandler() {
    return event ->
        logger.info(
            "DOMAIN EVENT: Component created - ID: {}, Type: {}, Time: {}",
            event.getComponentId().getShortId(),
            event.getComponentType(),
            event.getOccurredOn());
  }

  /**
   * Creates a handler for ComponentStateChangedEvent events.
   *
   * @return The event handler
   */
  public EventHandler<ComponentStateChangedEvent> componentStateChangedHandler() {
    return event ->
        logger.info(
            "DOMAIN EVENT: Component state changed - ID: {}, From: {}, To: {}, Reason: {}",
            event.getComponentId().getShortId(),
            event.getPreviousState(),
            event.getNewState(),
            event.getTransitionReason());
  }

  /**
   * Creates a handler for ComponentConnectionEvent events.
   *
   * @return The event handler
   */
  public EventHandler<ComponentConnectionEvent> componentConnectionHandler() {
    return event ->
        logger.info(
            "DOMAIN EVENT: Component connection created - Type: {}, Source: {}, Target: {}, Name: {}",
            event.getConnectionType(),
            event.getSourceId().getShortId(),
            event.getTargetId().getShortId(),
            event.getConnectionName());
  }

  /**
   * Creates a handler for MachineStateChangedEvent events.
   *
   * @return The event handler
   */
  public EventHandler<MachineStateChangedEvent> machineStateChangedHandler() {
    return event ->
        logger.info(
            "DOMAIN EVENT: Machine state changed - ID: {}, From: {}, To: {}, Reason: {}",
            event.getMachineId().getShortId(),
            event.getPreviousState(),
            event.getNewState(),
            event.getTransitionReason());
  }
}
