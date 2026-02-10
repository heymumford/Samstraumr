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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.EventDispatcher.EventHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;

/**
 * Tests for the InMemoryEventDispatcher.
 */
class InMemoryEventDispatcherTest {

    private InMemoryEventDispatcher dispatcher;
    
    @BeforeEach
    void setUp() {
        dispatcher = new InMemoryEventDispatcher();
    }
    
    @Test
    @DisplayName("Should register an event handler")
    void testRegisterHandler() {
        EventHandler<Map<String, Object>> handler = eventData -> {};
        boolean result = dispatcher.registerHandler("test-event", handler);
        assertTrue(result, "Registering a handler should succeed");
    }
    
    @Test
    @DisplayName("Should dispatch an event with no handlers")
    void testDispatchEventWithNoHandlers() {
        int handlerCount = dispatcher.dispatchEvent("test-event", "test-source", "test-payload", Map.of());
        assertEquals(0, handlerCount, "Dispatching an event with no handlers should return 0");
    }
    
    @Test
    @DisplayName("Should dispatch an event to a single handler")
    void testDispatchEventToSingleHandler() {
        AtomicBoolean eventHandled = new AtomicBoolean(false);
        AtomicReference<Map<String, Object>> receivedEventData = new AtomicReference<>();
        
        EventHandler<Map<String, Object>> handler = eventData -> {
            eventHandled.set(true);
            Map<String, Object> copy = new HashMap<>();
            for (Map.Entry<String, Object> entry : eventData.entrySet()) {
                copy.put(entry.getKey(), entry.getValue());
            }
            receivedEventData.set(copy);
        };
        
        // Register the handler
        dispatcher.registerHandler("test-event", handler);
        
        // Dispatch an event
        Map<String, String> properties = Map.of("key1", "value1", "key2", "value2");
        int handlerCount = dispatcher.dispatchEvent("test-event", "test-source", "test-payload", properties);
        
        // Verify the handler was called
        assertEquals(1, handlerCount, "Dispatch should return the number of handlers called");
        assertTrue(eventHandled.get(), "The handler should have been called");
        
        // Verify the event data
        assertNotNull(receivedEventData.get(), "Event data should not be null");
        assertEquals("test-event", receivedEventData.get().get("eventType"), "Event type should match");
        assertEquals("test-source", receivedEventData.get().get("source"), "Event source should match");
        assertEquals("test-payload", receivedEventData.get().get("payload"), "Event payload should match");
        
        @SuppressWarnings("unchecked")
        Map<String, String> receivedProperties = (Map<String, String>) receivedEventData.get().get("properties");
        assertNotNull(receivedProperties, "Event properties should not be null");
        assertEquals(2, receivedProperties.size(), "Event properties should have 2 entries");
        assertEquals("value1", receivedProperties.get("key1"), "Property key1 should have correct value");
        assertEquals("value2", receivedProperties.get("key2"), "Property key2 should have correct value");
    }
    
    @Test
    @DisplayName("Should dispatch events to multiple handlers")
    void testDispatchEventToMultipleHandlers() {
        AtomicInteger handler1Count = new AtomicInteger(0);
        AtomicInteger handler2Count = new AtomicInteger(0);
        
        EventHandler<Map<String, Object>> handler1 = eventData -> handler1Count.incrementAndGet();
        EventHandler<Map<String, Object>> handler2 = eventData -> handler2Count.incrementAndGet();
        
        // Register the handlers
        dispatcher.registerHandler("test-event", handler1);
        dispatcher.registerHandler("test-event", handler2);
        
        // Dispatch an event
        int handlerCount = dispatcher.dispatchEvent("test-event", "test-source", "test-payload", Map.of());
        
        // Verify both handlers were called
        assertEquals(2, handlerCount, "Dispatch should return the number of handlers called");
        assertEquals(1, handler1Count.get(), "Handler 1 should have been called once");
        assertEquals(1, handler2Count.get(), "Handler 2 should have been called once");
    }
    
    @Test
    @DisplayName("Should handle exceptions in event handlers")
    void testHandleExceptionInHandler() {
        AtomicInteger successfulHandlerCount = new AtomicInteger(0);
        
        EventHandler<Map<String, Object>> goodHandler = eventData -> successfulHandlerCount.incrementAndGet();
        EventHandler<Map<String, Object>> badHandler = eventData -> { throw new RuntimeException("Test exception"); };
        
        // Register the handlers
        dispatcher.registerHandler("test-event", goodHandler);
        dispatcher.registerHandler("test-event", badHandler);
        
        // Dispatch an event - this should not throw an exception despite the bad handler
        int handlerCount = dispatcher.dispatchEvent("test-event", "test-source", "test-payload", Map.of());
        
        // Only the successful handler should be counted
        assertEquals(1, handlerCount, "Dispatch should return the number of successful handlers");
        assertEquals(1, successfulHandlerCount.get(), "Good handler should have been called");
    }
    
    @Test
    @DisplayName("Should unregister a handler")
    void testUnregisterHandler() {
        AtomicInteger handlerCount = new AtomicInteger(0);
        EventHandler<Map<String, Object>> handler = eventData -> handlerCount.incrementAndGet();
        
        // Register the handler
        dispatcher.registerHandler("test-event", handler);
        
        // Dispatch an event
        dispatcher.dispatchEvent("test-event", "test-source", "test-payload", Map.of());
        assertEquals(1, handlerCount.get(), "Handler should have been called once");
        
        // Unregister the handler
        boolean unregistered = dispatcher.unregisterHandler("test-event", handler);
        assertTrue(unregistered, "Unregistering a handler should succeed");
        
        // Dispatch another event
        dispatcher.dispatchEvent("test-event", "test-source", "test-payload", Map.of());
        assertEquals(1, handlerCount.get(), "Handler should not have been called after unregistering");
    }
    
    @Test
    @DisplayName("Should handle unregistering a non-existent handler")
    void testUnregisterNonExistentHandler() {
        EventHandler<Map<String, Object>> handler = eventData -> {};
        
        // Try to unregister a handler that was never registered
        boolean unregistered = dispatcher.unregisterHandler("test-event", handler);
        assertFalse(unregistered, "Unregistering a non-existent handler should return false");
    }
}