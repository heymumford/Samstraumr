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

package org.s8r.infrastructure.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Implementation of the EventPublisherPort that adapts to the EventDispatcher.
 *
 * <p>This adapter implements the output port defined in the application layer and delegates to the
 * EventDispatcher infrastructure component. This follows the Clean Architecture pattern by keeping
 * the application layer dependent only on the port interfaces, not on the implementation details.
 */
public class EventPublisherAdapter implements EventPublisherPort {

  private final EventDispatcher eventDispatcher;
  private final ComponentRepository componentRepository;
  private final LoggerPort logger;
  private final Map<String, Map<String, EventSubscriber>> subscribers = new ConcurrentHashMap<>();

  /**
   * Creates a new EventPublisherAdapter with the specified dependencies.
   *
   * @param eventDispatcher The event dispatcher to use for publishing events
   * @param componentRepository The component repository for finding components
   * @param logger The logger to use for logging
   */
  public EventPublisherAdapter(
      EventDispatcher eventDispatcher, ComponentRepository componentRepository, LoggerPort logger) {
    this.eventDispatcher = eventDispatcher;
    this.componentRepository = componentRepository;
    this.logger = logger;
  }

  @Override
  public void publishEvent(DomainEvent event) {
    if (event == null) {
      logger.warn("Attempted to publish null event");
      return;
    }

    eventDispatcher.dispatch(event);
  }

  @Override
  public void publishEvents(List<DomainEvent> events) {
    if (events == null || events.isEmpty()) {
      return;
    }

    logger.debug("Publishing {} events", events.size());
    for (DomainEvent event : events) {
      publishEvent(event);
    }
  }

  @Override
  public int publishPendingEvents(ComponentId componentId) {
    if (componentId == null) {
      logger.warn("Attempted to publish events for null component ID");
      return 0;
    }

    // Find the component in the repository
    return componentRepository
        .findById(componentId)
        .map(this::publishAndClearComponentEvents)
        .orElseGet(
            () -> {
              logger.warn("Component not found for ID: {}", componentId.getIdString());
              return 0;
            });
  }

  @Override
  public <T extends DomainEvent> void registerHandler(
      Class<T> eventType, java.util.function.Consumer<T> handler) {
    if (eventType == null || handler == null) {
      logger.warn("Attempted to register handler with null event type or handler");
      return;
    }

    logger.debug("Registering handler for event type: {}", eventType.getSimpleName());
    eventDispatcher.registerHandler(eventType, handler);
  }

  /**
   * Publishes all events from a component and clears them.
   *
   * @param component The component whose events should be published
   * @return The number of events published
   */
  private int publishAndClearComponentEvents(ComponentPort component) {
    // Get the component's domain events
    List<DomainEvent> events = component.getDomainEvents();
    int eventCount = events.size();

    if (eventCount > 0) {
      // Publish all events
      publishEvents(events);

      // Clear the events from the component
      component.clearEvents();

      logger.debug(
          "Published and cleared {} events from component {}",
          eventCount,
          component.getId().getIdString());
    }

    return eventCount;
  }

  @Override
  public boolean publishEvent(String topic, String eventType, String payload) {
    return publishEvent(topic, eventType, payload, Map.of());
  }

  @Override
  public boolean publishEvent(
      String topic, String eventType, String payload, Map<String, String> properties) {
    if (topic == null || eventType == null || payload == null) {
      logger.warn("Cannot publish event with null topic, eventType, or payload");
      return false;
    }

    logger.debug("Publishing event to topic '{}' with type '{}'", topic, eventType);

    // Create event metadata
    Map<String, String> eventProperties = new HashMap<>(properties != null ? properties : Map.of());
    eventProperties.put("timestamp", String.valueOf(System.currentTimeMillis()));
    eventProperties.put("eventType", eventType);

    // Deliver to all subscribers of this topic
    Map<String, EventSubscriber> topicSubscribers = subscribers.get(topic);
    if (topicSubscribers != null && !topicSubscribers.isEmpty()) {
      for (EventSubscriber subscriber : topicSubscribers.values()) {
        try {
          subscriber.onEvent(topic, eventType, payload, eventProperties);
        } catch (Exception e) {
          logger.error("Error delivering event to subscriber: {}", e.getMessage(), e);
        }
      }
    }

    return true;
  }

  @Override
  public String subscribe(String topic, EventSubscriber subscriber) {
    if (topic == null || subscriber == null) {
      logger.warn("Cannot subscribe with null topic or subscriber");
      return null;
    }

    // Generate a unique subscription ID
    String subscriptionId = UUID.randomUUID().toString();

    // Add the subscriber
    subscribers
        .computeIfAbsent(topic, k -> new ConcurrentHashMap<>())
        .put(subscriptionId, subscriber);

    logger.debug("Added subscriber to topic '{}' with ID '{}'", topic, subscriptionId);
    return subscriptionId;
  }

  @Override
  public boolean unsubscribe(String subscriptionId) {
    if (subscriptionId == null) {
      logger.warn("Cannot unsubscribe with null subscription ID");
      return false;
    }

    // Find the subscription by ID
    for (Map.Entry<String, Map<String, EventSubscriber>> topicEntry : subscribers.entrySet()) {
      if (topicEntry.getValue().containsKey(subscriptionId)) {
        topicEntry.getValue().remove(subscriptionId);
        logger.debug(
            "Removed subscriber with ID '{}' from topic '{}'", subscriptionId, topicEntry.getKey());

        // If the topic has no more subscribers, remove it
        if (topicEntry.getValue().isEmpty()) {
          subscribers.remove(topicEntry.getKey());
        }

        return true;
      }
    }

    logger.warn("Subscription ID not found: {}", subscriptionId);
    return false;
  }
}
