/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.util.List;
import java.util.Map;

import org.s8r.domain.event.DomainEvent;

/**
 * Port interface for event publishing operations in the application layer.
 * 
 * <p>This interface defines the operations for publishing events and subscribing to topics,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface EventPublisherPort {

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
    boolean publishEvent(String topic, String eventType, String payload, Map<String, String> properties);
    
    /**
     * Publishes a list of domain events.
     * 
     * @param events The domain events to publish
     * @return true if all events were published successfully
     */
    boolean publishEvents(List<DomainEvent> events);
    
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
    
    /**
     * Interface for event subscribers.
     */
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