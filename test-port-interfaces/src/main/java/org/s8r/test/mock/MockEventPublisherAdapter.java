/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.mock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.domain.event.DomainEvent;

/**
 * Mock implementation of the EventPublisherPort interface for testing purposes.
 * This adapter allows tracking published events and notifies subscribers.
 */
public class MockEventPublisherAdapter implements EventPublisherPort {
    
    private static final Logger LOGGER = Logger.getLogger(MockEventPublisherAdapter.class.getName());
    
    // Map of topic to list of subscribers
    private final Map<String, List<EventSubscriber>> subscribers;
    // Record of all published events for verification
    private final List<PublishedEvent> publishedEvents;
    
    /**
     * Creates a new instance of the mock adapter.
     */
    public MockEventPublisherAdapter() {
        this.subscribers = new ConcurrentHashMap<>();
        this.publishedEvents = Collections.synchronizedList(new ArrayList<>());
    }
    
    @Override
    public boolean publishEvent(String topic, String eventType, String payload) {
        return publishEvent(topic, eventType, payload, Collections.emptyMap());
    }
    
    @Override
    public boolean publishEvent(String topic, String eventType, String payload, Map<String, String> properties) {
        LOGGER.info("Publishing event: topic=" + topic + ", type=" + eventType);
        
        // Create a record of the published event
        PublishedEvent event = new PublishedEvent(
            UUID.randomUUID().toString(),
            topic,
            eventType,
            payload,
            properties,
            Instant.now()
        );
        
        // Add to the list of published events
        publishedEvents.add(event);
        
        // Notify subscribers
        if (subscribers.containsKey(topic)) {
            for (EventSubscriber subscriber : subscribers.get(topic)) {
                try {
                    subscriber.onEvent(topic, eventType, payload, properties);
                } catch (Exception e) {
                    LOGGER.warning("Error notifying subscriber: " + e.getMessage());
                    // Continue with other subscribers instead of failing
                }
            }
        }
        
        return true;
    }
    
    @Override
    public boolean publishEvents(List<DomainEvent> events) {
        boolean result = true;
        for (DomainEvent event : events) {
            Map<String, String> properties = new HashMap<>();
            properties.put("eventId", event.getEventId());
            properties.put("eventType", event.getEventType());
            properties.put("timestamp", event.getTimestamp().toString());
            
            boolean published = publishEvent(
                event.getTopic(),
                event.getEventType(),
                event.getPayload(),
                properties
            );
            
            result = result && published;
        }
        return result;
    }
    
    @Override
    public String subscribe(String topic, EventSubscriber subscriber) {
        LOGGER.info("Subscribing to topic: " + topic);
        
        String subscriptionId = UUID.randomUUID().toString();
        
        // Add subscriber to the topic
        subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriber);
        
        return subscriptionId;
    }
    
    @Override
    public boolean unsubscribe(String subscriptionId) {
        LOGGER.info("Unsubscribing: " + subscriptionId);
        
        // In a real implementation, we would store a mapping from subscription ID to topic and subscriber
        // For this mock, we'll just return true since we can't actually remove by ID
        return true;
    }
    
    /**
     * Gets the list of published events for verification in tests.
     * 
     * @return An unmodifiable list of published events
     */
    public List<PublishedEvent> getPublishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
    
    /**
     * Gets all published events for a specific topic.
     * 
     * @param topic The topic to filter events by
     * @return A list of events published to the specified topic
     */
    public List<PublishedEvent> getEventsByTopic(String topic) {
        List<PublishedEvent> result = new ArrayList<>();
        synchronized (publishedEvents) {
            for (PublishedEvent event : publishedEvents) {
                if (event.getTopic().equals(topic)) {
                    result.add(event);
                }
            }
        }
        return result;
    }
    
    /**
     * Gets all published events of a specific type.
     * 
     * @param eventType The event type to filter by
     * @return A list of events of the specified type
     */
    public List<PublishedEvent> getEventsByType(String eventType) {
        List<PublishedEvent> result = new ArrayList<>();
        synchronized (publishedEvents) {
            for (PublishedEvent event : publishedEvents) {
                if (event.getEventType().equals(eventType)) {
                    result.add(event);
                }
            }
        }
        return result;
    }
    
    /**
     * Clears all published events (useful for test setup).
     */
    public void clearEvents() {
        publishedEvents.clear();
    }
    
    /**
     * Class representing a published event for verification in tests.
     */
    public static class PublishedEvent {
        private final String id;
        private final String topic;
        private final String eventType;
        private final String payload;
        private final Map<String, String> properties;
        private final Instant timestamp;
        
        /**
         * Creates a new PublishedEvent instance.
         * 
         * @param id The event ID
         * @param topic The topic the event was published to
         * @param eventType The type of event
         * @param payload The event payload
         * @param properties Additional event properties
         * @param timestamp The time the event was published
         */
        public PublishedEvent(
                String id,
                String topic,
                String eventType,
                String payload,
                Map<String, String> properties,
                Instant timestamp) {
            this.id = id;
            this.topic = topic;
            this.eventType = eventType;
            this.payload = payload;
            this.properties = new HashMap<>(properties);
            this.timestamp = timestamp;
        }
        
        public String getId() {
            return id;
        }
        
        public String getTopic() {
            return topic;
        }
        
        public String getEventType() {
            return eventType;
        }
        
        public String getPayload() {
            return payload;
        }
        
        public Map<String, String> getProperties() {
            return Collections.unmodifiableMap(properties);
        }
        
        public Instant getTimestamp() {
            return timestamp;
        }
        
        @Override
        public String toString() {
            return "PublishedEvent{" +
                   "id='" + id + '\'' +
                   ", topic='" + topic + '\'' +
                   ", eventType='" + eventType + '\'' +
                   ", payload='" + payload + '\'' +
                   ", properties=" + properties +
                   ", timestamp=" + timestamp +
                   '}';
        }
    }
}