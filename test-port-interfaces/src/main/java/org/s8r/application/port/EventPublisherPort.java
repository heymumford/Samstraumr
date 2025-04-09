/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.application.port;

import org.s8r.application.port.event.EventDeliveryResult;

import java.util.List;
import java.util.Map;

/**
 * Port interface for event publishing operations.
 */
public interface EventPublisherPort {
    /**
     * Publishes an event.
     *
     * @param eventType The event type.
     * @param eventData The event data.
     * @return The result of the publish operation.
     */
    EventDeliveryResult publishEvent(String eventType, Map<String, Object> eventData);
    
    /**
     * Publishes an event asynchronously.
     *
     * @param eventType The event type.
     * @param eventData The event data.
     * @return The result of the publish operation.
     */
    EventDeliveryResult publishEventAsync(String eventType, Map<String, Object> eventData);
    
    /**
     * Publishes multiple events.
     *
     * @param events The events to publish.
     * @return The results of the publish operations.
     */
    List<EventDeliveryResult> publishEvents(List<Map<String, Object>> events);
}
