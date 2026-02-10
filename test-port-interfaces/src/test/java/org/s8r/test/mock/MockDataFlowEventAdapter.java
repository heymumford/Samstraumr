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

package org.s8r.test.mock;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Mock implementation of the DataFlowEventPort for testing.
 * 
 * <p>This adapter provides a thread-safe implementation of the DataFlowEventPort 
 * interface for use in testing.
 */
public class MockDataFlowEventAdapter implements DataFlowEventPort {
    
    private final Map<String, Map<ComponentId, Consumer<ComponentDataEvent>>> channelSubscriptions;
    private final Map<ComponentId, Set<String>> componentSubscriptions;
    
    /**
     * Creates a new MockDataFlowEventAdapter.
     */
    public MockDataFlowEventAdapter() {
        this.channelSubscriptions = new ConcurrentHashMap<>();
        this.componentSubscriptions = new ConcurrentHashMap<>();
    }
    
    @Override
    public void subscribe(ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be null or empty");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Event handler cannot be null");
        }
        
        // Add to channel subscriptions
        channelSubscriptions.computeIfAbsent(channel, k -> new ConcurrentHashMap<>())
            .put(componentId, handler);
        
        // Track component's subscriptions
        componentSubscriptions.computeIfAbsent(componentId, k -> ConcurrentHashMap.newKeySet())
            .add(channel);
    }
    
    @Override
    public void unsubscribe(ComponentId componentId, String channel) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be null or empty");
        }
        
        // Remove from channel subscriptions
        if (channelSubscriptions.containsKey(channel)) {
            channelSubscriptions.get(channel).remove(componentId);
            if (channelSubscriptions.get(channel).isEmpty()) {
                channelSubscriptions.remove(channel);
            }
        }
        
        // Update component's subscriptions
        if (componentSubscriptions.containsKey(componentId)) {
            componentSubscriptions.get(componentId).remove(channel);
            if (componentSubscriptions.get(componentId).isEmpty()) {
                componentSubscriptions.remove(componentId);
            }
        }
    }
    
    @Override
    public void unsubscribeAll(ComponentId componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        
        // Get all channels the component is subscribed to
        Set<String> channels = new HashSet<>(getComponentSubscriptions(componentId));
        
        // Unsubscribe from each channel
        for (String channel : channels) {
            unsubscribe(componentId, channel);
        }
        
        // Ensure component is removed from tracking
        componentSubscriptions.remove(componentId);
    }
    
    @Override
    public Set<String> getAvailableChannels() {
        return Set.copyOf(channelSubscriptions.keySet());
    }
    
    @Override
    public Set<String> getComponentSubscriptions(ComponentId componentId) {
        if (componentId == null) {
            throw new IllegalArgumentException("Component ID cannot be null");
        }
        return Set.copyOf(componentSubscriptions.getOrDefault(componentId, Set.of()));
    }
    
    @Override
    public void publishData(ComponentId sourceId, String channel, Map<String, Object> data) {
        if (sourceId == null) {
            throw new IllegalArgumentException("Source component ID cannot be null");
        }
        if (channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("Channel name cannot be null or empty");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        // Create a component data event
        ComponentDataEvent event = new ComponentDataEvent(sourceId, channel, data);
        
        // No subscribers for this channel
        if (!channelSubscriptions.containsKey(channel)) {
            return;
        }
        
        // Deliver to subscribers (excluding the publisher to prevent loopback)
        channelSubscriptions.get(channel).forEach((componentId, handler) -> {
            if (!componentId.equals(sourceId)) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    // Log error (in a real implementation)
                    System.err.println("Error delivering data event to component " + 
                        componentId + ": " + e.getMessage());
                }
            }
        });
    }
}