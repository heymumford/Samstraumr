/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import org.s8r.application.port.EventDispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.List;

/**
 * In-memory implementation of the EventDispatcher interface.
 * 
 * <p>This adapter provides a simple in-memory event dispatching system.
 * It is primarily intended for testing and development environments.
 */
public class InMemoryEventDispatcher implements EventDispatcher {
    
    private final Map<String, List<Consumer<Map<String, Object>>>> handlers = new ConcurrentHashMap<>();

    @Override
    public void registerHandler(String eventType, Consumer<Map<String, Object>> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public boolean unregisterHandler(String eventType, Consumer<Map<String, Object>> handler) {
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
    public boolean dispatchEvent(String eventType, Map<String, Object> eventData) {
        if (!handlers.containsKey(eventType)) {
            // No handlers for this event type
            return true;
        }
        
        // Add event type to the data
        eventData.put("eventType", eventType);
        
        // Notify all handlers
        for (Consumer<Map<String, Object>> handler : handlers.get(eventType)) {
            try {
                handler.accept(eventData);
            } catch (Exception e) {
                // Log the exception but continue processing other handlers
                System.err.println("Error in event handler for type " + eventType + ": " + e.getMessage());
            }
        }
        
        return true;
    }
}