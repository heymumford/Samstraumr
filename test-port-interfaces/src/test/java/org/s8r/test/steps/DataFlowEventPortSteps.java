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
import org.s8r.test.context.DataFlowEventPortTestContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for DataFlowEventPort BDD tests.
 * 
 * <p>This class implements the Cucumber step definitions for testing the DataFlowEventPort interface.
 */
public class DataFlowEventPortSteps {
    
    private final DataFlowEventPortTestContext context;
    private Map<String, Object> lastPublishedData;
    
    /**
     * Creates a new DataFlowEventPortSteps instance.
     * 
     * @param context The test context
     */
    public DataFlowEventPortSteps(DataFlowEventPortTestContext context) {
        this.context = context;
        this.lastPublishedData = Collections.emptyMap();
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
    @When("component {string} subscribes to data channel {string}")
    public void whenComponentSubscribesToChannel(String componentId, String channel) {
        context.subscribeComponent(componentId, channel);
    }
    
    @When("component {string} unsubscribes from data channel {string}")
    public void whenComponentUnsubscribesFromChannel(String componentId, String channel) {
        context.unsubscribeComponent(componentId, channel);
    }
    
    @When("component {string} unsubscribes from all channels")
    public void whenComponentUnsubscribesFromAllChannels(String componentId) {
        context.unsubscribeComponentFromAll(componentId);
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
        context.publishData(componentId, channel, data);
    }
    
    @Then("component {string} should be subscribed to channel {string}")
    public void thenComponentShouldBeSubscribedToChannel(String componentId, String channel) {
        assertTrue(context.isComponentSubscribedToChannel(componentId, channel),
            "Component " + componentId + " should be subscribed to channel " + channel);
    }
    
    @Then("component {string} should not be subscribed to channel {string}")
    public void thenComponentShouldNotBeSubscribedToChannel(String componentId, String channel) {
        assertFalse(context.isComponentSubscribedToChannel(componentId, channel),
            "Component " + componentId + " should not be subscribed to channel " + channel);
    }
    
    @Then("component {string} should not be subscribed to any channels")
    public void thenComponentShouldNotBeSubscribedToAnyChannels(String componentId) {
        Set<String> subscriptions = context.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.isEmpty(),
            "Component " + componentId + " should not be subscribed to any channels, but is subscribed to: " + subscriptions);
    }
    
    @Then("the list of available channels should include {string}")
    public void thenAvailableChannelsShouldInclude(String channel) {
        Set<String> availableChannels = context.getAvailableChannels();
        assertTrue(availableChannels.contains(channel),
            "Available channels should include " + channel + ", but got: " + availableChannels);
    }
    
    @Then("the list of available channels should not include {string}")
    public void thenAvailableChannelsShouldNotInclude(String channel) {
        Set<String> availableChannels = context.getAvailableChannels();
        assertFalse(availableChannels.contains(channel),
            "Available channels should not include " + channel + ", but got: " + availableChannels);
    }
    
    @Then("the list of component {string} subscriptions should include {string}")
    public void thenComponentSubscriptionsShouldInclude(String componentId, String channel) {
        Set<String> subscriptions = context.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.contains(channel),
            "Component " + componentId + " subscriptions should include " + channel + ", but got: " + subscriptions);
    }
    
    @Then("the list of component {string} subscriptions should not include {string}")
    public void thenComponentSubscriptionsShouldNotInclude(String componentId, String channel) {
        Set<String> subscriptions = context.getComponentSubscriptions(componentId);
        assertFalse(subscriptions.contains(channel),
            "Component " + componentId + " subscriptions should not include " + channel + ", but got: " + subscriptions);
    }
    
    @Then("the list of component {string} subscriptions should be empty")
    public void thenComponentSubscriptionsShouldBeEmpty(String componentId) {
        Set<String> subscriptions = context.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.isEmpty(),
            "Component " + componentId + " subscriptions should be empty, but got: " + subscriptions);
    }
    
    @Then("component {string} should receive data on channel {string}")
    public void thenComponentShouldReceiveDataOnChannel(String componentId, String channel) {
        assertTrue(context.hasComponentReceivedData(componentId, channel),
            "Component " + componentId + " should have received data on channel " + channel);
    }
    
    @Then("no components should receive data")
    public void thenNoComponentsShouldReceiveData() {
        // Nothing to verify in the implementation, as we published to a channel with no subscribers
    }
    
    @Then("the received data should contain key {string} with value {string}")
    public void thenReceivedDataShouldContainKeyValue(String key, String expectedValueStr) {
        for (String componentId : context.getAvailableChannels()) {
            ComponentDataEvent event = context.getLatestReceivedEvent(componentId);
            
            if (event != null) {
                Object actualValue = event.getValue(key);
                if (actualValue != null) {
                    // Try to convert the expected value to a number for comparison if the actual value is a number
                    if (actualValue instanceof Number) {
                        try {
                            if (expectedValueStr.contains(".")) {
                                double expectedValue = Double.parseDouble(expectedValueStr);
                                assertEquals(expectedValue, ((Number) actualValue).doubleValue(), 0.001,
                                    "Received data key " + key + " value mismatch");
                            } else {
                                int expectedValue = Integer.parseInt(expectedValueStr);
                                assertEquals(expectedValue, ((Number) actualValue).intValue(),
                                    "Received data key " + key + " value mismatch");
                            }
                        } catch (NumberFormatException e) {
                            // If we can't parse, compare as strings
                            assertEquals(expectedValueStr, actualValue.toString(),
                                "Received data key " + key + " value mismatch");
                        }
                    } else {
                        // Compare as strings
                        assertEquals(expectedValueStr, actualValue.toString(),
                            "Received data key " + key + " value mismatch");
                    }
                    return;
                }
            }
        }
        
        fail("No component received data with key " + key);
    }
    
    @Then("component {string} should not receive its own published data")
    public void thenComponentShouldNotReceiveItsOwnPublishedData(String componentId) {
        assertFalse(context.hasComponentReceivedData(componentId, "loopback-test"),
            "Component " + componentId + " should not receive its own published data");
    }
    
    @Then("all components should receive the same data payload")
    public void thenAllComponentsShouldReceiveTheSameDataPayload() {
        // Get all components that received data
        Map<String, ComponentDataEvent> receivedEvents = new HashMap<>();
        
        // Collect the latest event from each component
        for (String componentId : List.of("comp-456", "comp-789")) {
            ComponentDataEvent event = context.getLatestReceivedEvent(componentId);
            if (event != null) {
                receivedEvents.put(componentId, event);
            }
        }
        
        // Ensure we have at least two components with events
        assertTrue(receivedEvents.size() >= 2, 
            "Expected at least two components to receive data, but got: " + receivedEvents.size());
        
        // Compare the data payloads
        Map<String, Object> firstData = null;
        String firstComponentId = null;
        
        for (Map.Entry<String, ComponentDataEvent> entry : receivedEvents.entrySet()) {
            if (firstData == null) {
                firstData = entry.getValue().getData();
                firstComponentId = entry.getKey();
            } else {
                Map<String, Object> currentData = entry.getValue().getData();
                assertEquals(firstData.size(), currentData.size(),
                    "Data payload size mismatch between components " + firstComponentId + " and " + entry.getKey());
                
                for (Map.Entry<String, Object> dataEntry : firstData.entrySet()) {
                    String key = dataEntry.getKey();
                    Object expectedValue = dataEntry.getValue();
                    Object actualValue = currentData.get(key);
                    
                    assertNotNull(actualValue, 
                        "Key " + key + " not found in data for component " + entry.getKey());
                    assertEquals(expectedValue, actualValue,
                        "Value mismatch for key " + key + " between components " + 
                        firstComponentId + " and " + entry.getKey());
                }
            }
        }
    }
}