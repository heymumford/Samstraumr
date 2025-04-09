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

package org.s8r.test.mock;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.event.EventDeliveryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mock implementation of EventPublisherPort for testing.
 */
public class MockEventPublisherAdapter implements EventPublisherPort {
    private final List<Map<String, Object>> capturedEvents = new CopyOnWriteArrayList<>();
    private boolean asyncPublishing = false;
    private int bufferSize = 1000;
    private boolean simulateErrors = false;
    
    /**
     * Creates a new instance of the MockEventPublisherAdapter.
     *
     * @return A new MockEventPublisherAdapter instance.
     */
    public static MockEventPublisherAdapter createInstance() {
        return new MockEventPublisherAdapter();
    }
    
    /**
     * Configures the adapter with the given settings.
     *
     * @param settings Map of configuration settings.
     */
    public void configure(Map<String, Object> settings) {
        if (settings.containsKey("asyncPublishing")) {
            this.asyncPublishing = (Boolean) settings.get("asyncPublishing");
        }
        if (settings.containsKey("bufferSize")) {
            this.bufferSize = ((Number) settings.get("bufferSize")).intValue();
        }
        if (settings.containsKey("simulateErrors")) {
            this.simulateErrors = (Boolean) settings.get("simulateErrors");
        }
    }

    @Override
    public EventDeliveryResult publishEvent(String eventType, Map<String, Object> eventData) {
        Map<String, Object> event = new java.util.HashMap<>(eventData);
        event.put("eventType", eventType);
        
        capturedEvents.add(event);
        
        if (simulateErrors) {
            return new EventDeliveryResult(false, "Simulated error");
        }
        
        return new EventDeliveryResult(true, null);
    }

    @Override
    public EventDeliveryResult publishEventAsync(String eventType, Map<String, Object> eventData) {
        return publishEvent(eventType, eventData);
    }

    @Override
    public List<EventDeliveryResult> publishEvents(List<Map<String, Object>> events) {
        List<EventDeliveryResult> results = new ArrayList<>();
        
        for (Map<String, Object> event : events) {
            results.add(publishEvent((String) event.get("eventType"), event));
        }
        
        return results;
    }
    
    /**
     * Gets the list of captured events.
     *
     * @return List of captured events.
     */
    public List<Map<String, Object>> getCapturedEvents() {
        return capturedEvents;
    }
    
    /**
     * Clears the list of captured events.
     */
    public void clearEvents() {
        capturedEvents.clear();
    }
}
