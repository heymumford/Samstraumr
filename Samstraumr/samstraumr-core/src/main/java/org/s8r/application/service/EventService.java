/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.service;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Service for handling events in the application layer.
 * 
 * <p>This service provides a unified interface for event publishing and dispatching,
 * combining the functionality of EventPublisherPort and EventDispatcher.
 */
public class EventService {
    
    private final EventPublisherPort eventPublisher;
    private final EventDispatcher eventDispatcher;
    
    /**
     * Creates a new EventService instance.
     *
     * @param eventPublisher The event publisher to use
     * @param eventDispatcher The event dispatcher to use
     */
    public EventService(EventPublisherPort eventPublisher, EventDispatcher eventDispatcher) {
        this.eventPublisher = eventPublisher;
        this.eventDispatcher = eventDispatcher;
    }
    
    /**
     * Publishes an event to a topic.
     *
     * @param topic The topic to publish to
     * @param eventType The type of the event
     * @param payload The event payload
     * @return true if the event was published successfully, false otherwise
     */
    public boolean publish(String topic, String eventType, String payload) {
        // Publish using the event publisher
        boolean published = eventPublisher.publishEvent(topic, eventType, payload);
        
        // Also dispatch locally
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("topic", topic);
        eventData.put("payload", payload);
        boolean dispatched = eventDispatcher.dispatchEvent(eventType, eventData);
        
        return published && dispatched;
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
    public boolean publishWithProperties(String topic, String eventType, String payload, Map<String, String> properties) {
        // Publish using the event publisher
        boolean published = eventPublisher.publishEvent(topic, eventType, payload, properties);
        
        // Also dispatch locally
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("topic", topic);
        eventData.put("payload", payload);
        eventData.put("properties", properties);
        boolean dispatched = eventDispatcher.dispatchEvent(eventType, eventData);
        
        return published && dispatched;
    }
    
    /**
     * Subscribes to events on a topic.
     *
     * @param topic The topic to subscribe to
     * @param handler The handler function
     * @return A subscription ID
     */
    public String subscribe(String topic, Consumer<Map<String, Object>> handler) {
        // Create an adapter from Consumer to EventSubscriber
        EventPublisherPort.EventSubscriber subscriber = 
            (t, eventType, payload, properties) -> {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("topic", t);
                eventData.put("eventType", eventType);
                eventData.put("payload", payload);
                eventData.put("properties", properties);
                handler.accept(eventData);
            };
        
        return eventPublisher.subscribe(topic, subscriber);
    }
    
    /**
     * Registers a handler for a specific event type.
     *
     * @param eventType The type of event to handle
     * @param handler The handler function
     */
    public void registerHandler(String eventType, Consumer<Map<String, Object>> handler) {
        eventDispatcher.registerHandler(eventType, handler);
    }
    
    /**
     * Unregisters a handler for a specific event type.
     *
     * @param eventType The type of event
     * @param handler The handler function to unregister
     */
    public void unregisterHandler(String eventType, Consumer<Map<String, Object>> handler) {
        eventDispatcher.unregisterHandler(eventType, handler);
    }
}