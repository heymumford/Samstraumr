/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import org.s8r.application.port.EventDispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * In-memory implementation of the EventDispatcher interface.
 * 
 * <p>This adapter provides a simple in-memory event dispatching system.
 * It is primarily intended for testing and development environments.
 */
public class InMemoryEventDispatcher implements EventDispatcher {
    
    private final Map<String, List<EventHandler>> handlers = new ConcurrentHashMap<>();

    @Override
    public boolean registerHandler(String eventType, EventHandler handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        return true;
    }

    @Override
    public boolean unregisterHandler(String eventType, EventHandler handler) {
        if (!handlers.containsKey(eventType)) {
            return false;
        }
        
        boolean removed = handlers.get(eventType).remove(handler);
        if (handlers.get(eventType).isEmpty()) {
            handlers.remove(eventType);
        }
        
        return removed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int dispatchEvent(String eventType, String source, String payload, Map<String, String> properties) {
        if (!handlers.containsKey(eventType)) {
            // No handlers for this event type
            return 0;
        }
        
        // Prepare the event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventType", eventType);
        eventData.put("source", source);
        eventData.put("payload", payload);
        eventData.put("properties", properties);
        
        // Count successful handlers
        int handlerCount = 0;
        
        // Notify all handlers
        for (EventHandler handler : handlers.get(eventType)) {
            try {
                handler.handle(eventData);
                handlerCount++;
            } catch (Exception e) {
                // Log the exception but continue processing other handlers
                System.err.println("Error in event handler for type " + eventType + ": " + e.getMessage());
            }
        }
        
        return handlerCount;
    }
}