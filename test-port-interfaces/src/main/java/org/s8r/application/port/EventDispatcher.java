/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.util.Map;

/**
 * Port interface for event dispatching in the application layer.
 * 
 * <p>This interface defines the operations for registering event handlers and dispatching events,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface EventDispatcher {

    /**
     * Registers an event handler.
     *
     * @param eventType The event type to register for
     * @param handler The handler to register
     * @return true if the handler was registered successfully, false otherwise
     */
    boolean registerHandler(String eventType, EventHandler handler);
    
    /**
     * Unregisters an event handler.
     *
     * @param eventType The event type to unregister from
     * @param handler The handler to unregister
     * @return true if the handler was unregistered successfully, false otherwise
     */
    boolean unregisterHandler(String eventType, EventHandler handler);
    
    /**
     * Dispatches an event to all registered handlers.
     *
     * @param eventType The type of the event
     * @param source The source of the event
     * @param payload The event payload
     * @param properties Additional properties of the event
     * @return The number of handlers that processed the event
     */
    int dispatchEvent(String eventType, String source, String payload, Map<String, String> properties);
    
    /**
     * Interface for event handlers in the event dispatcher.
     *
     * @param <T> The type of event
     */
    interface EventHandler<T> {
        /**
         * Handles an event.
         *
         * @param event The event to handle
         */
        void handle(T event);
    }
}