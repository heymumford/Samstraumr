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
package org.s8r.application.port;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Port interface for event dispatching in the application layer.
 * 
 * <p>This interface defines the operations for registering event handlers and dispatching events,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface EventDispatcher {
    
    /**
     * Registers a handler for a specific event type.
     *
     * @param eventType The type of event to handle
     * @param handler The handler function
     */
    void registerHandler(String eventType, Consumer<Map<String, Object>> handler);
    
    /**
     * Unregisters a handler for a specific event type.
     *
     * @param eventType The type of event
     * @param handler The handler function to unregister
     * @return true if the handler was unregistered, false otherwise
     */
    boolean unregisterHandler(String eventType, Consumer<Map<String, Object>> handler);
    
    /**
     * Dispatches an event to all registered handlers for the event type.
     *
     * @param eventType The type of event to dispatch
     * @param eventData The event data
     * @return true if the event was dispatched successfully, false otherwise
     */
    boolean dispatchEvent(String eventType, Map<String, Object> eventData);
}