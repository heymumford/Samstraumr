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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * In-memory implementation of the EventPublisherPort interface.
 *
 * <p>This adapter provides a simple in-memory event publishing system. It is primarily intended for
 * testing and development environments.
 */
public class EventPublisherAdapter implements EventPublisherPort {

  private final Map<String, List<SubscriptionEntry>> subscribers = new ConcurrentHashMap<>();
  private final Map<String, SubscriptionEntry> subscriptionsById = new ConcurrentHashMap<>();
  private final EventDispatcher eventDispatcher;
  private final ComponentRepository componentRepository;
  private final LoggerPort logger;

  /**
   * Creates a new EventPublisherAdapter with the required dependencies.
   *
   * @param eventDispatcher The event dispatcher to use
   * @param componentRepository The component repository to use
   * @param logger The logger to use
   */
  public EventPublisherAdapter(
      EventDispatcher eventDispatcher, ComponentRepository componentRepository, LoggerPort logger) {
    this.eventDispatcher = eventDispatcher;
    this.componentRepository = componentRepository;
    this.logger = logger;
  }

  private static class SubscriptionEntry {
    private final String id;
    private final String topic;
    private final EventSubscriber subscriber;

    public SubscriptionEntry(String id, String topic, EventSubscriber subscriber) {
      this.id = id;
      this.topic = topic;
      this.subscriber = subscriber;
    }
  }

  @Override
  public boolean publishEvent(String topic, String eventType, String payload) {
    return publishEvent(topic, eventType, payload, Map.of());
  }

  @Override
  public boolean publishEvent(
      String topic, String eventType, String payload, Map<String, String> properties) {
    if (!subscribers.containsKey(topic)) {
      // No subscribers for this topic
      return true;
    }

    for (SubscriptionEntry entry : subscribers.get(topic)) {
      entry.subscriber.onEvent(topic, eventType, payload, properties);
    }

    return true;
  }

  /**
   * Publishes a list of domain events to subscribers.
   *
   * @param events The domain events to publish
   * @return true if all events were published successfully
   */
  public boolean publishEvents(List<DomainEvent> events) {
    if (events == null || events.isEmpty()) {
      return true;
    }

    boolean result = true;

    for (DomainEvent event : events) {
      Map<String, String> properties = new HashMap<>();
      properties.put("eventId", event.getEventId());
      properties.put("timestamp", String.valueOf(event.getOccurredOn()));
      properties.put("entityId", event.getSourceComponentId());

      // For domain events, we'll use the event type from the event itself
      // and send an empty payload as we're just forwarding the event type
      result =
          result
              && publishEvent(
                  event.getEventType(),
                  event.getEventType(),
                  "{}", // Empty payload as we're just forwarding the event type
                  properties);
    }

    return result;
  }

  @Override
  public String subscribe(String topic, EventSubscriber subscriber) {
    String subscriptionId = UUID.randomUUID().toString();
    SubscriptionEntry entry = new SubscriptionEntry(subscriptionId, topic, subscriber);

    subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(entry);
    subscriptionsById.put(subscriptionId, entry);

    return subscriptionId;
  }

  @Override
  public boolean unsubscribe(String subscriptionId) {
    SubscriptionEntry entry = subscriptionsById.remove(subscriptionId);
    if (entry == null) {
      return false;
    }

    if (subscribers.containsKey(entry.topic)) {
      subscribers.get(entry.topic).remove(entry);
      if (subscribers.get(entry.topic).isEmpty()) {
        subscribers.remove(entry.topic);
      }
    }

    return true;
  }

  /**
   * Publishes a single domain event.
   *
   * @param event The domain event to publish, or null
   */
  public void publishEvent(DomainEvent event) {
    if (event == null) {
      logger.warn("Attempted to publish null event");
      return;
    }

    eventDispatcher.dispatch(event);
  }

  /**
   * Publishes all pending events for a component.
   *
   * @param componentId The ID of the component whose events should be published
   * @return The number of events published
   */
  public int publishPendingEvents(ComponentId componentId) {
    if (componentId == null) {
      logger.warn("Attempted to publish pending events for null component ID");
      return 0;
    }

    Optional<ComponentPort> componentOpt = componentRepository.findById(componentId);
    if (componentOpt.isEmpty()) {
      logger.warn("Component not found: {}", componentId.getIdString());
      return 0;
    }

    ComponentPort component = componentOpt.get();
    List<DomainEvent> events = component.getDomainEvents();

    if (events == null || events.isEmpty()) {
      return 0;
    }

    int count = 0;
    for (DomainEvent event : events) {
      eventDispatcher.dispatch(event);
      count++;
    }

    component.clearEvents();
    return count;
  }

  // Helper inner class to convert Map to HashMap
  private static class HashMap<K, V> extends java.util.HashMap<K, V> {
    public HashMap() {
      super();
    }

    public HashMap(Map<K, V> map) {
      super(map);
    }
  }
}
