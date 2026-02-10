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
import org.s8r.application.port.MessagingPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.message.Message;
import org.s8r.test.integration.DataFlowMessagingBridge;
import org.s8r.test.mock.MockDataFlowEventAdapter;
import org.s8r.test.mock.MockMessagingAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Test context for DataFlowEventPort and MessagingPort integration tests.
 * 
 * <p>This class manages the test context and state for integration between 
 * DataFlowEventPort and MessagingPort operations.
 */
public class DataFlowMessagingIntegrationContext {
    private final DataFlowEventPort dataFlowEventPort;
    private final MessagingPort messagingPort;
    private final Map<String, DataFlowMessagingBridge> bridges;
    private final Map<String, ComponentId> componentIds;
    private final Map<String, List<ComponentDataEvent>> receivedDataFlowEvents;
    private final Map<String, List<Message>> receivedMessages;
    private final Map<String, Boolean> dataFlowEventReceived;
    private final Map<String, Boolean> messageReceived;
    private final Map<String, Map<String, Function<Object, Object>>> transformations;
    
    /**
     * Creates a new DataFlowMessagingIntegrationContext.
     */
    public DataFlowMessagingIntegrationContext() {
        this.dataFlowEventPort = new MockDataFlowEventAdapter();
        this.messagingPort = new MockMessagingAdapter();
        this.bridges = new ConcurrentHashMap<>();
        this.componentIds = new ConcurrentHashMap<>();
        this.receivedDataFlowEvents = new ConcurrentHashMap<>();
        this.receivedMessages = new ConcurrentHashMap<>();
        this.dataFlowEventReceived = new ConcurrentHashMap<>();
        this.messageReceived = new ConcurrentHashMap<>();
        this.transformations = new ConcurrentHashMap<>();
    }
    
    /**
     * Resets the test context.
     */
    public void reset() {
        // Clear stored events and messages
        receivedDataFlowEvents.clear();
        receivedMessages.clear();
        dataFlowEventReceived.clear();
        messageReceived.clear();
        
        // Unsubscribe all components and clean up bridges
        componentIds.values().forEach(componentId -> 
            dataFlowEventPort.unsubscribeAll(componentId));
        
        bridges.values().forEach(DataFlowMessagingBridge::shutdown);
        bridges.clear();
        
        // Clear transformations
        transformations.clear();
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
     * Gets the DataFlowEventPort implementation.
     * 
     * @return The DataFlowEventPort
     */
    public DataFlowEventPort getDataFlowEventPort() {
        return dataFlowEventPort;
    }
    
    /**
     * Gets the MessagingPort implementation.
     * 
     * @return The MessagingPort
     */
    public MessagingPort getMessagingPort() {
        return messagingPort;
    }
    
    /**
     * Subscribes a component to a data flow channel.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to subscribe to
     */
    public void subscribeComponentToDataFlow(String componentIdString, String channel) {
        ComponentId componentId = getComponentId(componentIdString);
        
        // Initialize received events list for this component if not already created
        receivedDataFlowEvents.computeIfAbsent(componentIdString, k -> new CopyOnWriteArrayList<>());
        dataFlowEventReceived.put(componentIdString + ":" + channel, false);
        
        // Subscribe with handler that adds events to the received events list
        dataFlowEventPort.subscribe(componentId, channel, event -> {
            receivedDataFlowEvents.get(componentIdString).add(event);
            dataFlowEventReceived.put(componentIdString + ":" + channel, true);
        });
    }
    
    /**
     * Registers a messaging subscriber for a channel.
     * 
     * @param channel The channel to subscribe to
     */
    public void registerMessagingSubscriber(String channel) {
        // Initialize received messages list for this channel if not already created
        receivedMessages.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>());
        messageReceived.put(channel, false);
        
        // Register subscriber with handler that adds messages to the received messages list
        messagingPort.subscribe(channel, message -> {
            receivedMessages.get(channel).add(message);
            messageReceived.put(channel, true);
        });
    }
    
    /**
     * Configures a data flow to messaging bridge.
     * 
     * @param dataFlowChannel The data flow channel
     * @param messagingChannel The messaging channel
     */
    public void configureDataFlowToMessagingBridge(String dataFlowChannel, String messagingChannel) {
        // Create a bridge component ID
        ComponentId bridgeComponentId = ComponentId.create("DataFlow to Messaging Bridge");
        
        // Create the bridge
        DataFlowMessagingBridge bridge = new DataFlowMessagingBridge(
            dataFlowEventPort, 
            messagingPort, 
            bridgeComponentId,
            dataFlowChannel,
            messagingChannel,
            false, // One-way bridge from DataFlow to Messaging
            this::transformDataForMessaging
        );
        
        // Start the bridge
        bridge.start();
        
        // Store the bridge
        bridges.put(dataFlowChannel + "->" + messagingChannel, bridge);
    }
    
    /**
     * Configures a messaging to data flow bridge.
     * 
     * @param messagingChannel The messaging channel
     * @param dataFlowChannel The data flow channel
     */
    public void configureMessagingToDataFlowBridge(String messagingChannel, String dataFlowChannel) {
        // Create a bridge component ID
        ComponentId bridgeComponentId = ComponentId.create("Messaging to DataFlow Bridge");
        
        // Create the bridge
        DataFlowMessagingBridge bridge = new DataFlowMessagingBridge(
            dataFlowEventPort, 
            messagingPort, 
            bridgeComponentId,
            dataFlowChannel,
            messagingChannel,
            true, // One-way bridge from Messaging to DataFlow
            this::transformDataForDataFlow
        );
        
        // Start the bridge
        bridge.start();
        
        // Store the bridge
        bridges.put(messagingChannel + "->" + dataFlowChannel, bridge);
    }
    
    /**
     * Configures a bidirectional bridge between DataFlow and Messaging.
     * 
     * @param dataFlowChannel The data flow channel
     * @param messagingChannel The messaging channel
     */
    public void configureBidirectionalBridge(String dataFlowChannel, String messagingChannel) {
        // Create a bridge component ID
        ComponentId bridgeComponentId = ComponentId.create("Bidirectional Bridge");
        
        // Create the bridge
        DataFlowMessagingBridge bridge = new DataFlowMessagingBridge(
            dataFlowEventPort, 
            messagingPort, 
            bridgeComponentId,
            dataFlowChannel,
            messagingChannel,
            false, // Both directions handled internally
            data -> data // No transformation
        );
        
        // Configure for bidirectional operation
        bridge.setBidirectional(true);
        
        // Start the bridge
        bridge.start();
        
        // Store the bridge
        bridges.put(dataFlowChannel + "<->" + messagingChannel, bridge);
    }
    
    /**
     * Configures a data flow to messaging bridge with error handling.
     * 
     * @param dataFlowChannel The data flow channel
     * @param messagingChannel The messaging channel
     */
    public void configureDataFlowToMessagingBridgeWithErrorHandling(String dataFlowChannel, String messagingChannel) {
        // Create a bridge component ID
        ComponentId bridgeComponentId = ComponentId.create("DataFlow to Messaging Bridge with Error Handling");
        
        // Create the bridge
        DataFlowMessagingBridge bridge = new DataFlowMessagingBridge(
            dataFlowEventPort, 
            messagingPort, 
            bridgeComponentId,
            dataFlowChannel,
            messagingChannel,
            false, // One-way bridge from DataFlow to Messaging
            this::transformDataForMessaging
        );
        
        // Enable error handling
        bridge.setErrorHandlingEnabled(true);
        
        // Start the bridge
        bridge.start();
        
        // Store the bridge
        bridges.put(dataFlowChannel + "->!" + messagingChannel, bridge);
    }
    
    /**
     * Configures data transformations for a channel.
     * 
     * @param sourceKey The source key
     * @param targetKey The target key
     * @param transformationType The transformation type
     */
    public void configureTransformation(String channel, String sourceKey, String targetKey, String transformationType) {
        Map<String, Function<Object, Object>> channelTransformations = 
            transformations.computeIfAbsent(channel, k -> new HashMap<>());
        
        Function<Object, Object> transformation = value -> value; // Default no-op transformation
        
        // Configure the transformation based on the type
        switch (transformationType.toLowerCase()) {
            case "fahrenheit":
                transformation = value -> {
                    if (value instanceof Number) {
                        double celsius = ((Number) value).doubleValue();
                        return celsius * 9.0 / 5.0 + 32.0;
                    }
                    return value;
                };
                break;
            case "celsius":
                transformation = value -> {
                    if (value instanceof Number) {
                        double fahrenheit = ((Number) value).doubleValue();
                        return (fahrenheit - 32.0) * 5.0 / 9.0;
                    }
                    return value;
                };
                break;
            case "uppercase":
                transformation = value -> value != null ? value.toString().toUpperCase() : null;
                break;
            case "lowercase":
                transformation = value -> value != null ? value.toString().toLowerCase() : null;
                break;
        }
        
        // Store the key mapping and transformation
        Map<String, String> keyMapping = new HashMap<>();
        keyMapping.put(sourceKey, targetKey);
        
        // Store the transformation
        channelTransformations.put(sourceKey + "->" + targetKey, transformation);
    }
    
    /**
     * Transforms data from DataFlow to Messaging.
     * 
     * @param data The data to transform
     * @return The transformed data
     */
    private Map<String, Object> transformDataForMessaging(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>(data);
        
        // Get the channel
        String channel = (String) data.getOrDefault("channel", "unknown");
        
        // Get transformations for this channel
        Map<String, Function<Object, Object>> channelTransformations = 
            transformations.getOrDefault(channel, Map.of());
        
        // Apply transformations
        for (Map.Entry<String, Function<Object, Object>> entry : channelTransformations.entrySet()) {
            String[] keys = entry.getKey().split("->");
            if (keys.length == 2) {
                String sourceKey = keys[0];
                String targetKey = keys[1];
                
                if (data.containsKey(sourceKey)) {
                    // Transform the value
                    Object sourceValue = data.get(sourceKey);
                    Object transformedValue = entry.getValue().apply(sourceValue);
                    
                    // Remove source key and add target key
                    result.remove(sourceKey);
                    result.put(targetKey, transformedValue);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Transforms data from Messaging to DataFlow.
     * 
     * @param data The data to transform
     * @return The transformed data
     */
    private Map<String, Object> transformDataForDataFlow(Map<String, Object> data) {
        // For simplicity, we're using the same transformation logic in both directions
        return transformDataForMessaging(data);
    }
    
    /**
     * Publishes data from a component to a data flow channel.
     * 
     * @param componentIdString The source component ID string
     * @param channel The channel to publish to
     * @param data The data to publish
     */
    public void publishDataToDataFlow(String componentIdString, String channel, Map<String, Object> data) {
        ComponentId componentId = getComponentId(componentIdString);
        dataFlowEventPort.publishData(componentId, channel, data);
    }
    
    /**
     * Publishes invalid data from a component to a data flow channel.
     * 
     * @param componentIdString The source component ID string
     * @param channel The channel to publish to
     */
    public void publishInvalidDataToDataFlow(String componentIdString, String channel) {
        // Create a map that will cause errors in the bridge
        Map<String, Object> invalidData = new HashMap<>();
        invalidData.put("_invalidMarker", new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Simulated error in data conversion");
            }
        });
        
        // Publish the invalid data
        publishDataToDataFlow(componentIdString, channel, invalidData);
    }
    
    /**
     * Publishes a message to a messaging channel.
     * 
     * @param channel The channel to publish to
     * @param data The data to publish
     */
    public void publishMessageToMessaging(String channel, Map<String, Object> data) {
        Message message = Message.builder()
            .channel(channel)
            .payload(data)
            .build();
        
        messagingPort.publish(message);
    }
    
    /**
     * Checks if a component received data on a channel.
     * 
     * @param componentIdString The component ID string
     * @param channel The channel to check
     * @return True if the component received data on the channel
     */
    public boolean hasComponentReceivedDataFlowEvent(String componentIdString, String channel) {
        return dataFlowEventReceived.getOrDefault(componentIdString + ":" + channel, false);
    }
    
    /**
     * Checks if a messaging subscriber received a message on a channel.
     * 
     * @param channel The channel to check
     * @return True if a message was received on the channel
     */
    public boolean hasMessagingSubscriberReceivedMessage(String channel) {
        return messageReceived.getOrDefault(channel, false);
    }
    
    /**
     * Gets the latest data flow event received by a component.
     * 
     * @param componentIdString The component ID string
     * @return The latest received event, or null if none
     */
    public ComponentDataEvent getLatestReceivedDataFlowEvent(String componentIdString) {
        List<ComponentDataEvent> events = receivedDataFlowEvents.getOrDefault(componentIdString, new ArrayList<>());
        return events.isEmpty() ? null : events.get(events.size() - 1);
    }
    
    /**
     * Gets the latest message received on a channel.
     * 
     * @param channel The channel
     * @return The latest message, or null if none
     */
    public Message getLatestReceivedMessage(String channel) {
        List<Message> messages = receivedMessages.getOrDefault(channel, new ArrayList<>());
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }
    
    /**
     * Checks if an error message was received on a channel.
     * 
     * @param channel The channel
     * @return True if an error message was received
     */
    public boolean hasReceivedErrorMessage(String channel) {
        List<Message> messages = receivedMessages.getOrDefault(channel, new ArrayList<>());
        
        for (Message message : messages) {
            Map<String, Object> payload = message.getPayload();
            if (payload.containsKey("error") && payload.containsKey("errorDetails")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gets the latest error message received on a channel.
     * 
     * @param channel The channel
     * @return The error message details, or null if none
     */
    public String getErrorMessageDetails(String channel) {
        List<Message> messages = receivedMessages.getOrDefault(channel, new ArrayList<>());
        
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            Map<String, Object> payload = message.getPayload();
            
            if (payload.containsKey("error") && payload.containsKey("errorDetails")) {
                return (String) payload.get("errorDetails");
            }
        }
        
        return null;
    }
}