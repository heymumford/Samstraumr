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
package org.s8r.application.service;

import java.util.Map;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;

/** Service for publishing and handling events. */
public class EventService {

  private final EventPublisherPort eventPublisherPort;
  private final NotificationPort notificationPort;
  private final LoggerPort logger;

  /**
   * Creates a new EventService.
   *
   * @param eventPublisherPort The event publisher port to use
   * @param notificationPort The notification port to use
   * @param logger The logger to use
   */
  public EventService(
      EventPublisherPort eventPublisherPort, NotificationPort notificationPort, LoggerPort logger) {
    this.eventPublisherPort = eventPublisherPort;
    this.notificationPort = notificationPort;
    this.logger = logger;
  }

  /**
   * Creates a new EventService.
   *
   * @param eventPublisherPort The event publisher port to use
   * @param notificationPort The notification port to use
   */
  public EventService(EventPublisherPort eventPublisherPort, NotificationPort notificationPort) {
    this.eventPublisherPort = eventPublisherPort;
    this.notificationPort = notificationPort;
    this.logger = null;
  }

  /**
   * Publishes an event.
   *
   * @param topic The topic to publish to
   * @param eventType The type of the event
   * @param payload The event payload
   * @return true if the event was published successfully, false otherwise
   */
  public boolean publishEvent(String topic, String eventType, String payload) {
    if (logger != null) {
      logger.debug("Publishing event to topic {} with type {}", topic, eventType);
    }
    return eventPublisherPort.publishEvent(topic, eventType, payload);
  }

  /**
   * Publishes an event with additional properties.
   *
   * @param topic The topic to publish to
   * @param eventType The type of the event
   * @param payload The event payload
   * @param properties Additional properties for the event
   * @return true if the event was published successfully, false otherwise
   */
  public boolean publishEvent(
      String topic, String eventType, String payload, Map<String, String> properties) {
    if (logger != null) {
      logger.debug("Publishing event to topic {} with type {} and properties", topic, eventType);
    }
    return eventPublisherPort.publishEvent(topic, eventType, payload, properties);
  }

  /**
   * Publishes an event and sends a notification.
   *
   * @param topic The topic to publish to
   * @param eventType The type of the event
   * @param payload The event payload
   * @return true if the event was published and the notification was sent successfully, false
   *     otherwise
   */
  public boolean publishEventWithNotification(String topic, String eventType, String payload) {
    boolean eventPublished = publishEvent(topic, eventType, payload);
    boolean notificationSent =
        notificationPort.sendNotification(
            "system",
            "Event " + eventType + " published",
            "Event published to topic: " + topic + "\nPayload: " + payload);

    return eventPublished && notificationSent;
  }

  /**
   * Gets the event publisher port.
   *
   * @return the event publisher port
   */
  public EventPublisherPort getEventPublisherPort() {
    return eventPublisherPort;
  }

  /**
   * Gets the notification port.
   *
   * @return the notification port
   */
  public NotificationPort getNotificationPort() {
    return notificationPort;
  }
}
