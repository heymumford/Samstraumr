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

package org.s8r.test.integration;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.MessagingPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Bridge between DataFlowEventPort and MessagingPort.
 * 
 * <p>This class implements a bridge that connects DataFlowEventPort and MessagingPort,
 * enabling data to flow between the two interfaces in either or both directions.
 */
public class DataFlowMessagingBridge {
    private final DataFlowEventPort dataFlowEventPort;
    private final MessagingPort messagingPort;
    private final ComponentId bridgeComponentId;
    private final String dataFlowChannel;
    private final String messagingChannel;
    private final boolean reverseDirection; // If true, flow is Messaging -> DataFlow
    private final Function<Map<String, Object>, Map<String, Object>> transformationFunction;
    
    private boolean bidirectional;
    private boolean errorHandlingEnabled;
    private final AtomicBoolean active;
    
    /**
     * Creates a new DataFlowMessagingBridge.
     * 
     * @param dataFlowEventPort The DataFlowEventPort
     * @param messagingPort The MessagingPort
     * @param bridgeComponentId The bridge component ID
     * @param dataFlowChannel The data flow channel
     * @param messagingChannel The messaging channel
     * @param reverseDirection If true, flow is Messaging -> DataFlow
     * @param transformationFunction Function to transform data between systems
     */
    public DataFlowMessagingBridge(
            DataFlowEventPort dataFlowEventPort,
            MessagingPort messagingPort,
            ComponentId bridgeComponentId,
            String dataFlowChannel,
            String messagingChannel,
            boolean reverseDirection,
            Function<Map<String, Object>, Map<String, Object>> transformationFunction) {
        
        this.dataFlowEventPort = dataFlowEventPort;
        this.messagingPort = messagingPort;
        this.bridgeComponentId = bridgeComponentId;
        this.dataFlowChannel = dataFlowChannel;
        this.messagingChannel = messagingChannel;
        this.reverseDirection = reverseDirection;
        this.transformationFunction = transformationFunction;
        this.bidirectional = false;
        this.errorHandlingEnabled = false;
        this.active = new AtomicBoolean(false);
    }
    
    /**
     * Sets whether the bridge is bidirectional.
     * 
     * @param bidirectional True if the bridge should be bidirectional
     */
    public void setBidirectional(boolean bidirectional) {
        this.bidirectional = bidirectional;
    }
    
    /**
     * Sets whether error handling is enabled.
     * 
     * @param errorHandlingEnabled True if error handling should be enabled
     */
    public void setErrorHandlingEnabled(boolean errorHandlingEnabled) {
        this.errorHandlingEnabled = errorHandlingEnabled;
    }
    
    /**
     * Starts the bridge.
     */
    public void start() {
        if (active.compareAndSet(false, true)) {
            if (!reverseDirection || bidirectional) {
                // Set up DataFlow -> Messaging direction
                dataFlowEventPort.subscribe(bridgeComponentId, dataFlowChannel, this::handleDataFlowEvent);
            }
            
            if (reverseDirection || bidirectional) {
                // Set up Messaging -> DataFlow direction
                messagingPort.subscribe(messagingChannel, this::handleMessagingEvent);
            }
        }
    }
    
    /**
     * Shuts down the bridge.
     */
    public void shutdown() {
        if (active.compareAndSet(true, false)) {
            dataFlowEventPort.unsubscribe(bridgeComponentId, dataFlowChannel);
            messagingPort.unsubscribe(messagingChannel);
        }
    }
    
    /**
     * Handles a data flow event.
     * 
     * @param event The data flow event
     */
    private void handleDataFlowEvent(ComponentDataEvent event) {
        if (!active.get()) {
            return;
        }
        
        try {
            // Get the data from the event
            Map<String, Object> data = new HashMap<>(event.getData());
            
            // Add metadata
            data.put("sourceComponent", event.getSourceId().getIdString());
            data.put("eventId", event.getEventId());
            data.put("timestamp", event.getOccurredOn().toEpochMilli());
            data.put("channel", event.getDataChannel());
            
            // Transform the data
            Map<String, Object> transformedData = transformationFunction.apply(data);
            
            // Create a message
            Message message = Message.builder()
                .channel(messagingChannel)
                .payload(transformedData)
                .id(UUID.randomUUID().toString())
                .build();
            
            // Send the message
            messagingPort.publish(message);
        } catch (Exception e) {
            if (errorHandlingEnabled) {
                // Create an error message
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "DataFlowToMessagingConversionError");
                errorData.put("errorDetails", e.getMessage());
                errorData.put("sourceComponent", event.getSourceId().getIdString());
                errorData.put("timestamp", System.currentTimeMillis());
                
                // Create and send an error message
                Message errorMessage = Message.builder()
                    .channel(messagingChannel)
                    .payload(errorData)
                    .id(UUID.randomUUID().toString())
                    .build();
                
                messagingPort.publish(errorMessage);
            } else {
                // Re-throw the exception
                throw new RuntimeException("Error converting DataFlow event to Message", e);
            }
        }
    }
    
    /**
     * Handles a messaging event.
     * 
     * @param message The message
     */
    private void handleMessagingEvent(Message message) {
        if (!active.get()) {
            return;
        }
        
        try {
            // Get the data from the message
            Map<String, Object> data = new HashMap<>(message.getPayload());
            
            // Add metadata if not already present
            data.putIfAbsent("messageId", message.getId());
            data.putIfAbsent("timestamp", System.currentTimeMillis());
            data.putIfAbsent("channel", message.getChannel());
            
            // Transform the data
            Map<String, Object> transformedData = transformationFunction.apply(data);
            
            // Publish to the data flow channel
            dataFlowEventPort.publishData(bridgeComponentId, dataFlowChannel, transformedData);
        } catch (Exception e) {
            if (errorHandlingEnabled) {
                // Create error data
                Map<String, Object> errorData = new HashMap<>();
                errorData.put("error", "MessagingToDataFlowConversionError");
                errorData.put("errorDetails", e.getMessage());
                errorData.put("messageId", message.getId());
                errorData.put("timestamp", System.currentTimeMillis());
                
                // Publish error data to data flow
                dataFlowEventPort.publishData(bridgeComponentId, dataFlowChannel, errorData);
            } else {
                // Re-throw the exception
                throw new RuntimeException("Error converting Message to DataFlow event", e);
            }
        }
    }
    
    /**
     * Gets the data flow channel.
     * 
     * @return The data flow channel
     */
    public String getDataFlowChannel() {
        return dataFlowChannel;
    }
    
    /**
     * Gets the messaging channel.
     * 
     * @return The messaging channel
     */
    public String getMessagingChannel() {
        return messagingChannel;
    }
    
    /**
     * Checks if the bridge is active.
     * 
     * @return True if the bridge is active
     */
    public boolean isActive() {
        return active.get();
    }
    
    /**
     * Checks if the bridge is bidirectional.
     * 
     * @return True if the bridge is bidirectional
     */
    public boolean isBidirectional() {
        return bidirectional;
    }
    
    /**
     * Checks if error handling is enabled.
     * 
     * @return True if error handling is enabled
     */
    public boolean isErrorHandlingEnabled() {
        return errorHandlingEnabled;
    }
}