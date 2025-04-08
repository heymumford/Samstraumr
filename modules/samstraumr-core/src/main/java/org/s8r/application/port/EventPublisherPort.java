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

import java.util.List;
import java.util.Map;

import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Port interface for publishing domain events from the application layer.
 *
 * <p>This interface allows application services to publish domain events without directly depending
 * on the event dispatcher implementation, following Clean Architecture principles. This is an
 * output port for the application core.
 */
public interface EventPublisherPort {

  /**
   * Publishes a single domain event.
   *
   * @param event The domain event to publish
   */
  void publishEvent(DomainEvent event);

  /**
   * Publishes a collection of domain events.
   *
   * @param events The list of domain events to publish
   */
  void publishEvents(List<DomainEvent> events);

  /**
   * Publishes all pending events from a component. This method will collect events from the
   * component and clear them after publishing.
   *
   * @param componentId The ID of the component whose events should be published
   * @return The number of events published
   */
  int publishPendingEvents(ComponentId componentId);

  /**
   * Registers a handler for a specific event type.
   *
   * @param eventType the event type to handle
   * @param handler the handler to call when an event of the specified type is published
   * @param <T> the event type
   */
  <T extends DomainEvent> void registerHandler(
      Class<T> eventType, java.util.function.Consumer<T> handler);

  /**
   * Publishes an event to a topic.
   *
   * @param topic The topic to publish to
   * @param eventType The type of the event
   * @param payload The event payload
   * @return true if the event was published successfully, false otherwise
   */
  boolean publishEvent(String topic, String eventType, String payload);

  /**
   * Publishes an event with additional properties.
   *
   * @param topic The topic to publish to
   * @param eventType The type of the event
   * @param payload The event payload
   * @param properties Additional properties for the event
   * @return true if the event was published successfully, false otherwise
   */
  boolean publishEvent(
      String topic, String eventType, String payload, Map<String, String> properties);

  /**
   * Subscribes to events on a topic.
   *
   * @param topic The topic to subscribe to
   * @param subscriber The subscriber to register
   * @return A subscription ID
   */
  String subscribe(String topic, EventSubscriber subscriber);

  /**
   * Unsubscribes from a topic.
   *
   * @param subscriptionId The subscription ID to unsubscribe
   * @return true if the unsubscription was successful, false otherwise
   */
  boolean unsubscribe(String subscriptionId);

  /** Interface for event subscribers. */
  interface EventSubscriber {
    /**
     * Called when an event is received.
     *
     * @param topic The topic of the event
     * @param eventType The type of the event
     * @param payload The event payload
     * @param properties Additional properties of the event
     */
    void onEvent(String topic, String eventType, String payload, Map<String, String> properties);
  }
}
