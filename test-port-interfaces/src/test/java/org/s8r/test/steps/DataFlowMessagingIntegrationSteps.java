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

package org.s8r.test.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.message.Message;
import org.s8r.test.context.DataFlowMessagingIntegrationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for DataFlowEventPort and MessagingPort integration tests.
 * 
 * <p>This class implements the Cucumber step definitions for testing the 
 * integration between DataFlowEventPort and MessagingPort interfaces.
 */
public class DataFlowMessagingIntegrationSteps {
    
    private final DataFlowMessagingIntegrationContext context;
    private Map<String, Object> lastPublishedData;
    
    /**
     * Creates a new DataFlowMessagingIntegrationSteps instance.
     * 
     * @param context The test context
     */
    public DataFlowMessagingIntegrationSteps(DataFlowMessagingIntegrationContext context) {
        this.context = context;
        this.lastPublishedData = new HashMap<>();
    }
    
    @Before
    public void setup() {
        context.reset();
    }
    
    @Given("a clean system environment")
    public void givenCleanSystemEnvironment() {
        context.reset();
    }
    
    @Given("the DataFlowEventPort interface is properly initialized")
    public void givenDataFlowEventPortInterfaceInitialized() {
        assertNotNull(context.getDataFlowEventPort(), "DataFlowEventPort should be initialized");
    }
    
    @Given("the MessagingPort interface is properly initialized")
    public void givenMessagingPortInterfaceInitialized() {
        assertNotNull(context.getMessagingPort(), "MessagingPort should be initialized");
    }
    
    @Given("the following component identities are registered in the system:")
    public void givenComponentIdentitiesRegistered(DataTable dataTable) {
        List<Map<String, String>> components = dataTable.asMaps();
        for (Map<String, String> component : components) {
            String componentId = component.get("componentId");
            String name = component.get("name");
            String type = component.get("type");
            
            context.registerComponentId(componentId, name, type);
        }
    }
    
    @Given("component {string} subscribes to data channel {string}")
    public void givenComponentSubscribesToDataChannel(String componentId, String channel) {
        context.subscribeComponentToDataFlow(componentId, channel);
    }
    
    @Given("a messaging subscriber is registered for channel {string}")
    public void givenMessagingSubscriberRegistered(String channel) {
        context.registerMessagingSubscriber(channel);
    }
    
    @Given("a data flow to messaging bridge is configured for channel {string} to {string}")
    public void givenDataFlowToMessagingBridgeConfigured(String dataFlowChannel, String messagingChannel) {
        context.configureDataFlowToMessagingBridge(dataFlowChannel, messagingChannel);
    }
    
    @Given("a messaging to data flow bridge is configured for channel {string} to {string}")
    public void givenMessagingToDataFlowBridgeConfigured(String messagingChannel, String dataFlowChannel) {
        context.configureMessagingToDataFlowBridge(messagingChannel, dataFlowChannel);
    }
    
    @Given("a bidirectional bridge is configured between {string} and {string}")
    public void givenBidirectionalBridgeConfigured(String dataFlowChannel, String messagingChannel) {
        context.configureBidirectionalBridge(dataFlowChannel, messagingChannel);
    }
    
    @Given("a data flow to messaging bridge is configured for channel {string} to {string} with error handling")
    public void givenDataFlowToMessagingBridgeConfiguredWithErrorHandling(String dataFlowChannel, String messagingChannel) {
        context.configureDataFlowToMessagingBridgeWithErrorHandling(dataFlowChannel, messagingChannel);
    }
    
    @Given("a data flow to messaging bridge is configured for channel {string} to {string} with transformation:")
    public void givenDataFlowToMessagingBridgeConfiguredWithTransformation(
            String dataFlowChannel, String messagingChannel, DataTable dataTable) {
        
        // Configure the bridge
        context.configureDataFlowToMessagingBridge(dataFlowChannel, messagingChannel);
        
        // Configure transformations
        List<Map<String, String>> transformations = dataTable.asMaps();
        for (Map<String, String> transformation : transformations) {
            String sourceKey = transformation.get("sourceKey");
            String targetKey = transformation.get("targetKey");
            String transformationType = transformation.get("transformation");
            
            context.configureTransformation(dataFlowChannel, sourceKey, targetKey, transformationType);
        }
    }
    
    @When("component {string} publishes data to channel {string} with payload:")
    public void whenComponentPublishesDataToChannel(String componentId, String channel, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Object> data = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String key = row.get("key");
            String value = row.get("value");
            
            // Try to convert the value to a number if possible
            try {
                if (value.contains(".")) {
                    data.put(key, Double.parseDouble(value));
                } else {
                    data.put(key, Integer.parseInt(value));
                }
            } catch (NumberFormatException e) {
                // Not a number, use as string
                data.put(key, value);
            }
        }
        
        lastPublishedData = data;
        context.publishDataToDataFlow(componentId, channel, data);
    }
    
    @When("component {string} publishes invalid data to channel {string}")
    public void whenComponentPublishesInvalidDataToChannel(String componentId, String channel) {
        context.publishInvalidDataToDataFlow(componentId, channel);
    }
    
    @When("a message is published to messaging channel {string} with payload:")
    public void whenMessageIsPublishedToMessagingChannel(String channel, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Object> data = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String key = row.get("key");
            String value = row.get("value");
            
            // Try to convert the value to a number if possible
            try {
                if (value.contains(".")) {
                    data.put(key, Double.parseDouble(value));
                } else {
                    data.put(key, Integer.parseInt(value));
                }
            } catch (NumberFormatException e) {
                // Not a number, use as string
                data.put(key, value);
            }
        }
        
        lastPublishedData = data;
        context.publishMessageToMessaging(channel, data);
    }
    
    @Then("component {string} should receive data on channel {string}")
    public void thenComponentShouldReceiveDataOnChannel(String componentId, String channel) {
        assertTrue(context.hasComponentReceivedDataFlowEvent(componentId, channel),
            "Component " + componentId + " should have received data on channel " + channel);
    }
    
    @Then("the messaging subscriber should receive a message on channel {string}")
    public void thenMessagingSubscriberShouldReceiveMessageOnChannel(String channel) {
        assertTrue(context.hasMessagingSubscriberReceivedMessage(channel),
            "Messaging subscriber should have received a message on channel " + channel);
    }
    
    @Then("the message should contain key {string} with value {string}")
    public void thenMessageShouldContainKeyValue(String key, String expectedValueStr) {
        Message message = context.getLatestReceivedMessage(expectedValueStr);
        
        if (message == null) {
            // Get the message from any channel
            for (String channel : List.of("external-sensor-data", "external-events", "transformed-data", "error-channel")) {
                message = context.getLatestReceivedMessage(channel);
                if (message != null) {
                    break;
                }
            }
        }
        
        assertNotNull(message, "A message should have been received");
        
        Map<String, Object> payload = message.getPayload();
        assertTrue(payload.containsKey(key), 
            "Message should contain key " + key + ", but only has keys: " + payload.keySet());
        
        Object actualValue = payload.get(key);
        
        // Try to convert the expected value to a number for comparison if the actual value is a number
        if (actualValue instanceof Number) {
            try {
                if (expectedValueStr.contains(".")) {
                    double expectedValue = Double.parseDouble(expectedValueStr);
                    assertEquals(expectedValue, ((Number) actualValue).doubleValue(), 0.001,
                        "Message key " + key + " value mismatch");
                } else {
                    int expectedValue = Integer.parseInt(expectedValueStr);
                    assertEquals(expectedValue, ((Number) actualValue).intValue(),
                        "Message key " + key + " value mismatch");
                }
            } catch (NumberFormatException e) {
                // If we can't parse, compare as strings
                assertEquals(expectedValueStr, actualValue.toString(),
                    "Message key " + key + " value mismatch");
            }
        } else {
            // Compare as strings
            assertEquals(expectedValueStr, actualValue.toString(),
                "Message key " + key + " value mismatch");
        }
    }
    
    @Then("the message should contain key {string} with the value of component {string}")
    public void thenMessageShouldContainKeyWithComponentValue(String key, String componentId) {
        Message message = null;
        
        // Get the message from any channel
        for (String channel : List.of("external-sensor-data", "external-events", "transformed-data")) {
            message = context.getLatestReceivedMessage(channel);
            if (message != null) {
                break;
            }
        }
        
        assertNotNull(message, "A message should have been received");
        
        Map<String, Object> payload = message.getPayload();
        assertTrue(payload.containsKey(key), 
            "Message should contain key " + key + ", but only has keys: " + payload.keySet());
        
        Object actualValue = payload.get(key);
        String expectedValuePrefix = context.getComponentId(componentId).getIdString();
        
        // The actual value should be the component ID or contain it
        String actualValueStr = String.valueOf(actualValue);
        assertTrue(actualValueStr.contains(expectedValuePrefix),
            "Message key " + key + " should contain component ID " + expectedValuePrefix +
            ", but was: " + actualValueStr);
    }
    
    @Then("the received data should contain key {string} with value {string}")
    public void thenReceivedDataShouldContainKeyValue(String key, String expectedValueStr) {
        ComponentDataEvent event = null;
        
        // Get the event from any component
        for (String componentId : List.of("comp-123", "comp-456", "comp-789", "comp-101")) {
            event = context.getLatestReceivedDataFlowEvent(componentId);
            if (event != null) {
                break;
            }
        }
        
        assertNotNull(event, "A data flow event should have been received");
        
        Map<String, Object> data = event.getData();
        assertTrue(data.containsKey(key), 
            "Data should contain key " + key + ", but only has keys: " + data.keySet());
        
        Object actualValue = data.get(key);
        
        // Try to convert the expected value to a number for comparison if the actual value is a number
        if (actualValue instanceof Number) {
            try {
                if (expectedValueStr.contains(".")) {
                    double expectedValue = Double.parseDouble(expectedValueStr);
                    assertEquals(expectedValue, ((Number) actualValue).doubleValue(), 0.001,
                        "Data key " + key + " value mismatch");
                } else {
                    int expectedValue = Integer.parseInt(expectedValueStr);
                    assertEquals(expectedValue, ((Number) actualValue).intValue(),
                        "Data key " + key + " value mismatch");
                }
            } catch (NumberFormatException e) {
                // If we can't parse, compare as strings
                assertEquals(expectedValueStr, actualValue.toString(),
                    "Data key " + key + " value mismatch");
            }
        } else {
            // Compare as strings
            assertEquals(expectedValueStr, actualValue.toString(),
                "Data key " + key + " value mismatch");
        }
    }
    
    @Then("the messaging subscriber should receive an error message on channel {string}")
    public void thenMessagingSubscriberShouldReceiveErrorMessageOnChannel(String channel) {
        assertTrue(context.hasReceivedErrorMessage(channel),
            "Messaging subscriber should have received an error message on channel " + channel);
    }
    
    @Then("the error message should contain details about the conversion failure")
    public void thenErrorMessageShouldContainDetailsAboutConversionFailure() {
        String errorDetails = context.getErrorMessageDetails("error-channel");
        assertNotNull(errorDetails, "Error message should contain details about the conversion failure");
        assertTrue(errorDetails.contains("error"), "Error details should contain information about the error");
    }
    
    @Then("all systems should receive the same data")
    public void thenAllSystemsShouldReceiveTheSameData() {
        // Get the message
        Message message = context.getLatestReceivedMessage("external-events");
        assertNotNull(message, "A message should have been received");
        Map<String, Object> messagePayload = message.getPayload();
        
        // Get the data flow event
        ComponentDataEvent event = context.getLatestReceivedDataFlowEvent("comp-123");
        assertNotNull(event, "A data flow event should have been received");
        Map<String, Object> eventData = event.getData();
        
        // Compare the relevant fields (ignoring metadata like timestamps)
        for (String key : lastPublishedData.keySet()) {
            // Check both payloads contain the key
            assertTrue(messagePayload.containsKey(key), 
                "Message should contain key " + key);
            assertTrue(eventData.containsKey(key), 
                "Event data should contain key " + key);
            
            // Compare the values as strings
            assertEquals(String.valueOf(messagePayload.get(key)), String.valueOf(eventData.get(key)),
                "Value mismatch for key " + key);
        }
    }
}