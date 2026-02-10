/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.context;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.mock.MockDataFlowEventAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Test context for DataFlowEventPort tests.
 * 
 * <p>This class manages the test context and state for DataFlowEventPort operations.
 */
public class DataFlowEventPortTestContext {
    private final DataFlowEventPort dataFlowEventPort;
    private final Map<String, ComponentId> componentIds;
    private final Map<String, List<ComponentDataEvent>> receivedEvents;
    private final Map<String, Boolean> eventReceived;
    
    /**
     * Creates a new DataFlowEventPortTestContext.
     */
    public DataFlowEventPortTestContext() {
        this.dataFlowEventPort = new MockDataFlowEventAdapter();
        this.componentIds = new ConcurrentHashMap<>();
        this.receivedEvents = new ConcurrentHashMap<>();
        this.eventReceived = new ConcurrentHashMap<>();
    }
    
    /**
     * Resets the test context.
     */
    public void reset() {
        // Clear stored events, but keep component IDs
        receivedEvents.clear();
        eventReceived.clear();
        
        // Unsubscribe all components from their channels
        componentIds.values().forEach(componentId -> 
            dataFlowEventPort.unsubscribeAll(componentId));
    }
    
    /**
     * Gets the DataFlowEventPort implementation.
     * 
     * @return The DataFlowEventPort
     */
    public DataFlowEventPort getDataFlowEventPort() {
        return dataFlowEventPort;
    }
    
    /**
     * Registers a component identity in the system.
     * 
     * @param idString The component ID string
     * @param name The component name
     * @param type The component type
     */
    public void registerComponentId(String idString, String name, String type) {
        String reason = String.format("Test component of type '%s' named '%s'", type, name);
        ComponentId componentId;
        
        try {
            // Try to parse it as a UUID if it looks like one
            if (idString.contains("-")) {
                componentId = ComponentId.fromString(idString, reason);
            } else {
                // Otherwise generate a deterministic UUID from the string
                String uuid = UUID.nameUUIDFromBytes(idString.getBytes()).toString();
                componentId = ComponentId.fromString(uuid, reason);
            }
        } catch (IllegalArgumentException e) {
            // Fallback to a deterministic UUID if the ID format is invalid
            String uuid = UUID.nameUUIDFromBytes(idString.getBytes()).toString();
            componentId = ComponentId.fromString(uuid, reason);
        }
        
        componentIds.put(idString, componentId);
    }
    
    /**
     * Gets a registered component ID.
     * 
     * @param componentIdString The component ID string
     * @return The ComponentId object
     * @throws IllegalArgumentException if the component ID is not registered
     */
    public ComponentId getComponentId(String componentIdString) {
        ComponentId componentId = componentIds.get(componentIdString);
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID not registered: " + componentIdString);
        }
        return componentId;
    }
    
    /**
     * Subscribes a component to a channel with an event handler.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to subscribe to
     */
    public void subscribeComponent(String componentIdString, String channel) {
        ComponentId componentId = getComponentId(componentIdString);
        
        // Initialize received events list for this component if not already created
        receivedEvents.computeIfAbsent(componentIdString, k -> new CopyOnWriteArrayList<>());
        eventReceived.put(componentIdString + ":" + channel, false);
        
        // Subscribe with handler that adds events to the received events list
        dataFlowEventPort.subscribe(componentId, channel, event -> {
            receivedEvents.get(componentIdString).add(event);
            eventReceived.put(componentIdString + ":" + channel, true);
        });
    }
    
    /**
     * Unsubscribes a component from a channel.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to unsubscribe from
     */
    public void unsubscribeComponent(String componentIdString, String channel) {
        ComponentId componentId = getComponentId(componentIdString);
        dataFlowEventPort.unsubscribe(componentId, channel);
    }
    
    /**
     * Unsubscribes a component from all channels.
     * 
     * @param componentIdString The component ID string
     */
    public void unsubscribeComponentFromAll(String componentIdString) {
        ComponentId componentId = getComponentId(componentIdString);
        dataFlowEventPort.unsubscribeAll(componentId);
    }
    
    /**
     * Checks if a component is subscribed to a channel.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to check
     * @return True if the component is subscribed to the channel
     */
    public boolean isComponentSubscribedToChannel(String componentIdString, String channel) {
        ComponentId componentId = getComponentId(componentIdString);
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        return subscriptions.contains(channel);
    }
    
    /**
     * Gets the available channels.
     * 
     * @return A set of available channels
     */
    public Set<String> getAvailableChannels() {
        return dataFlowEventPort.getAvailableChannels();
    }
    
    /**
     * Gets the channels a component is subscribed to.
     * 
     * @param componentIdString The component ID string
     * @return A set of channel names
     */
    public Set<String> getComponentSubscriptions(String componentIdString) {
        ComponentId componentId = getComponentId(componentIdString);
        return dataFlowEventPort.getComponentSubscriptions(componentId);
    }
    
    /**
     * Publishes data from a component to a channel.
     * 
     * @param componentIdString The source component ID string
     * @param channel The channel to publish to
     * @param data The data to publish
     */
    public void publishData(String componentIdString, String channel, Map<String, Object> data) {
        ComponentId componentId = getComponentId(componentIdString);
        dataFlowEventPort.publishData(componentId, channel, data);
    }
    
    /**
     * Checks if a component received data on a channel.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to check
     * @return True if the component received data on the channel
     */
    public boolean hasComponentReceivedData(String componentIdString, String channel) {
        return eventReceived.getOrDefault(componentIdString + ":" + channel, false);
    }
    
    /**
     * Gets the data received by a component.
     * 
     * @param componentIdString The component ID string
     * @return The list of received events
     */
    public List<ComponentDataEvent> getReceivedEvents(String componentIdString) {
        return receivedEvents.getOrDefault(componentIdString, Collections.emptyList());
    }
    
    /**
     * Gets the latest data received by a component.
     * 
     * @param componentIdString The component ID string
     * @return The latest received event, or null if none
     */
    public ComponentDataEvent getLatestReceivedEvent(String componentIdString) {
        List<ComponentDataEvent> events = getReceivedEvents(componentIdString);
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }
}