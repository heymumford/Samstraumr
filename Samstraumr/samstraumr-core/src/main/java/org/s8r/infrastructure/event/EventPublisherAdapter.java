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

import org.s8r.application.port.EventPublisherPort;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * In-memory implementation of the EventPublisherPort interface.
 * 
 * <p>This adapter provides a simple in-memory event publishing system.
 * It is primarily intended for testing and development environments.
 */
public class EventPublisherAdapter implements EventPublisherPort {
    
    private final Map<String, List<SubscriptionEntry>> subscribers = new ConcurrentHashMap<>();
    private final Map<String, SubscriptionEntry> subscriptionsById = new ConcurrentHashMap<>();
    
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
    public boolean publishEvent(String topic, String eventType, String payload, Map<String, String> properties) {
        if (!subscribers.containsKey(topic)) {
            // No subscribers for this topic
            return true;
        }
        
        for (SubscriptionEntry entry : subscribers.get(topic)) {
            entry.subscriber.onEvent(topic, eventType, payload, properties);
        }
        
        return true;
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
}