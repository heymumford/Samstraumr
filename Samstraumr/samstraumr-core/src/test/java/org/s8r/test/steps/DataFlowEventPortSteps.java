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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.event.DataFlowEventHandler;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the DataFlowEventPort interface.
 */
@IntegrationTest
public class DataFlowEventPortSteps {

    private DataFlowEventPort dataFlowEventPort;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private Map<String, ComponentId> componentIds;
    private Map<String, List<ComponentDataEvent>> receivedEvents;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        componentIds = new HashMap<>();
        receivedEvents = new ConcurrentHashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger("DataFlowEventPortTest") {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
        };
        
        // Initialize the data flow event port
        dataFlowEventPort = new DataFlowEventHandler(logger);
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        // Unsubscribe all components to clean up
        for (ComponentId componentId : componentIds.values()) {
            dataFlowEventPort.unsubscribeAll(componentId);
        }
        testContext.clear();
        componentIds.clear();
        receivedEvents.clear();
        logMessages.clear();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(dataFlowEventPort, "DataFlowEventPort should be initialized");
    }

    @Given("the DataFlowEventPort interface is properly initialized")
    public void theDataFlowEventPortInterfaceIsProperlyInitialized() {
        // Verify the data flow event port is properly initialized
        Set<String> channels = dataFlowEventPort.getAvailableChannels();
        assertNotNull(channels, "Available channels should not be null");
    }

    @Given("the following component identities are registered in the system:")
    public void theFollowingComponentIdentitiesAreRegisteredInTheSystem(DataTable dataTable) {
        List<Map<String, String>> componentData = dataTable.asMaps();
        
        for (Map<String, String> component : componentData) {
            String id = component.get("componentId");
            String name = component.get("name");
            String type = component.get("type");
            
            ComponentId componentId = createComponentId(id, name, type);
            componentIds.put(id, componentId);
        }
        
        assertFalse(componentIds.isEmpty(), "Component IDs should be registered");
    }

    /**
     * Helper method to create a ComponentId for testing.
     */
    private ComponentId createComponentId(String id, String name, String type) {
        return new ComponentId() {
            @Override
            public String getId() {
                return id;
            }
            
            @Override
            public String getShortId() {
                return id;
            }
            
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public String getType() {
                return type;
            }
            
            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                ComponentId other = (ComponentId) obj;
                return getId().equals(other.getId());
            }
            
            @Override
            public int hashCode() {
                return getId().hashCode();
            }
            
            @Override
            public String toString() {
                return "ComponentId[" + getId() + "]";
            }
        };
    }

    @Given("component {string} subscribes to data channel {string}")
    @When("component {string} subscribes to data channel {string}")
    public void componentSubscribesToDataChannel(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        // Create a handler that records received events
        Consumer<ComponentDataEvent> handler = event -> {
            receivedEvents.computeIfAbsent(componentIdStr, k -> new ArrayList<>()).add(event);
        };
        
        dataFlowEventPort.subscribe(componentId, channel, handler);
        
        // Store in context for verification
        testContext.put("lastSubscriber", componentIdStr);
        testContext.put("lastChannel", channel);
    }

    @When("component {string} unsubscribes from data channel {string}")
    public void componentUnsubscribesFromDataChannel(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        dataFlowEventPort.unsubscribe(componentId, channel);
        
        // Store in context for verification
        testContext.put("lastUnsubscriber", componentIdStr);
        testContext.put("lastUnsubscribedChannel", channel);
    }

    @When("component {string} unsubscribes from all channels")
    public void componentUnsubscribesFromAllChannels(String componentIdStr) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        dataFlowEventPort.unsubscribeAll(componentId);
        
        // Store in context for verification
        testContext.put("unsubscribedAll", componentIdStr);
    }

    @When("component {string} publishes data to channel {string} with payload:")
    public void componentPublishesDataToChannelWithPayload(String componentIdStr, String channel, DataTable dataTable) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, Object> payload = new HashMap<>();
        
        // Convert string values to appropriate types
        for (Map<String, String> row : rows) {
            String key = row.get("key");
            String value = row.get("value");
            
            // Try to convert numeric values
            if (value.matches("^-?\\d+$")) {
                payload.put(key, Integer.parseInt(value));
            } else if (value.matches("^-?\\d+\\.\\d+$")) {
                payload.put(key, Double.parseDouble(value));
            } else {
                payload.put(key, value);
            }
        }
        
        dataFlowEventPort.publishData(componentId, channel, payload);
        
        // Store in context for verification
        testContext.put("lastPublisher", componentIdStr);
        testContext.put("lastPublishedChannel", channel);
        testContext.put("lastPublishedPayload", payload);
    }

    @Then("component {string} should be subscribed to channel {string}")
    public void componentShouldBeSubscribedToChannel(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.contains(channel), 
                "Component " + componentIdStr + " should be subscribed to channel " + channel);
    }

    @Then("component {string} should not be subscribed to channel {string}")
    public void componentShouldNotBeSubscribedToChannel(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertFalse(subscriptions.contains(channel), 
                "Component " + componentIdStr + " should not be subscribed to channel " + channel);
    }

    @Then("the list of available channels should include {string}")
    public void theListOfAvailableChannelsShouldInclude(String channel) {
        Set<String> availableChannels = dataFlowEventPort.getAvailableChannels();
        assertTrue(availableChannels.contains(channel), 
                "Available channels should include " + channel);
    }

    @Then("the list of available channels should not include {string}")
    public void theListOfAvailableChannelsShouldNotInclude(String channel) {
        Set<String> availableChannels = dataFlowEventPort.getAvailableChannels();
        assertFalse(availableChannels.contains(channel), 
                "Available channels should not include " + channel);
    }

    @Then("the list of component {string} subscriptions should include {string}")
    public void theListOfComponentSubscriptionsShouldInclude(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.contains(channel), 
                "Component " + componentIdStr + " subscriptions should include " + channel);
    }

    @Then("the list of component {string} subscriptions should not include {string}")
    public void theListOfComponentSubscriptionsShouldNotInclude(String componentIdStr, String channel) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertFalse(subscriptions.contains(channel), 
                "Component " + componentIdStr + " subscriptions should not include " + channel);
    }

    @Then("component {string} should receive data on channel {string}")
    public void componentShouldReceiveDataOnChannel(String componentIdStr, String channel) {
        // Allow a short time for event propagation
        try {
            Thread.sleep(50); // Short delay for async processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        List<ComponentDataEvent> events = receivedEvents.get(componentIdStr);
        assertNotNull(events, "Component " + componentIdStr + " should have received events");
        assertFalse(events.isEmpty(), "Component " + componentIdStr + " should have received at least one event");
        
        // Check if any event is from the expected channel
        boolean foundChannelEvent = events.stream()
                .anyMatch(event -> event.getDataChannel().equals(channel));
        
        assertTrue(foundChannelEvent, 
                "Component " + componentIdStr + " should have received an event on channel " + channel);
        
        // Store the most recent event for further verification
        ComponentDataEvent latestEvent = events.get(events.size() - 1);
        testContext.put("latestEvent_" + componentIdStr, latestEvent);
    }

    @Then("the received data should contain key {string} with value {string}")
    public void theReceivedDataShouldContainKeyWithValue(String key, String expectedValueStr) {
        String lastSubscriber = (String) testContext.get("lastSubscriber");
        if (lastSubscriber == null) {
            lastSubscriber = (String) testContext.getOrDefault("lastPublisher", "?");
        }
        
        ComponentDataEvent event = (ComponentDataEvent) testContext.get("latestEvent_" + lastSubscriber);
        assertNotNull(event, "Latest event for component " + lastSubscriber + " should be available");
        
        Map<String, Object> data = event.getData();
        assertTrue(data.containsKey(key), "Event data should contain key: " + key);
        
        Object actualValue = data.get(key);
        assertNotNull(actualValue, "Value for key " + key + " should not be null");
        
        // Handle numeric values
        if (expectedValueStr.matches("^-?\\d+$")) {
            int expectedValue = Integer.parseInt(expectedValueStr);
            if (actualValue instanceof Number) {
                assertEquals(expectedValue, ((Number) actualValue).intValue(), 
                        "Value for key " + key + " should match expected integer value");
            } else {
                assertEquals(expectedValueStr, actualValue.toString(), 
                        "Value for key " + key + " should match expected value");
            }
        } else if (expectedValueStr.matches("^-?\\d+\\.\\d+$")) {
            double expectedValue = Double.parseDouble(expectedValueStr);
            if (actualValue instanceof Number) {
                assertEquals(expectedValue, ((Number) actualValue).doubleValue(), 0.0001, 
                        "Value for key " + key + " should match expected double value");
            } else {
                assertEquals(expectedValueStr, actualValue.toString(), 
                        "Value for key " + key + " should match expected value");
            }
        } else {
            assertEquals(expectedValueStr, actualValue.toString(), 
                    "Value for key " + key + " should match expected value");
        }
    }

    @Then("no components should receive data")
    public void noComponentsShouldReceiveData() {
        // Allow a short time for event propagation
        try {
            Thread.sleep(50); // Short delay for async processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String lastPublishedChannel = (String) testContext.get("lastPublishedChannel");
        assertNotNull(lastPublishedChannel, "Last published channel should be in context");
        
        // Check that no components received data from the last published channel
        boolean anyComponentReceivedData = false;
        for (Map.Entry<String, List<ComponentDataEvent>> entry : receivedEvents.entrySet()) {
            String componentId = entry.getKey();
            List<ComponentDataEvent> events = entry.getValue();
            
            for (ComponentDataEvent event : events) {
                if (event.getDataChannel().equals(lastPublishedChannel)) {
                    anyComponentReceivedData = true;
                    break;
                }
            }
        }
        
        assertFalse(anyComponentReceivedData, "No components should have received data");
    }

    @Then("all components should receive the same data payload")
    public void allComponentsShouldReceiveTheSameDataPayload() {
        String channel = (String) testContext.get("lastPublishedChannel");
        Map<String, Object> expectedPayload = (Map<String, Object>) testContext.get("lastPublishedPayload");
        
        assertNotNull(channel, "Last published channel should be in context");
        assertNotNull(expectedPayload, "Last published payload should be in context");
        
        // Build a list of components that should have received data
        List<String> componentsToCheck = new ArrayList<>();
        for (String componentId : componentIds.keySet()) {
            String lastPublisher = (String) testContext.get("lastPublisher");
            if (!componentId.equals(lastPublisher)) {
                Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentIds.get(componentId));
                if (subscriptions.contains(channel)) {
                    componentsToCheck.add(componentId);
                }
            }
        }
        
        // Check that each component received the expected payload
        for (String componentId : componentsToCheck) {
            ComponentDataEvent event = (ComponentDataEvent) testContext.get("latestEvent_" + componentId);
            assertNotNull(event, "Component " + componentId + " should have received an event");
            
            Map<String, Object> actualPayload = event.getData();
            assertEquals(expectedPayload.size(), actualPayload.size(), 
                    "Payload size should match for component " + componentId);
            
            for (Map.Entry<String, Object> entry : expectedPayload.entrySet()) {
                String key = entry.getKey();
                Object expectedValue = entry.getValue();
                
                assertTrue(actualPayload.containsKey(key), 
                        "Payload for component " + componentId + " should contain key " + key);
                
                Object actualValue = actualPayload.get(key);
                if (expectedValue instanceof Number && actualValue instanceof Number) {
                    assertEquals(((Number) expectedValue).doubleValue(), ((Number) actualValue).doubleValue(), 0.0001,
                            "Value for key " + key + " should match for component " + componentId);
                } else {
                    assertEquals(expectedValue.toString(), actualValue.toString(), 
                            "Value for key " + key + " should match for component " + componentId);
                }
            }
        }
    }

    @Then("component {string} should not be subscribed to any channels")
    public void componentShouldNotBeSubscribedToAnyChannels(String componentIdStr) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.isEmpty(), 
                "Component " + componentIdStr + " should not be subscribed to any channels");
    }

    @Then("the list of component {string} subscriptions should be empty")
    public void theListOfComponentSubscriptionsShouldBeEmpty(String componentIdStr) {
        ComponentId componentId = componentIds.get(componentIdStr);
        assertNotNull(componentId, "Component ID should exist: " + componentIdStr);
        
        Set<String> subscriptions = dataFlowEventPort.getComponentSubscriptions(componentId);
        assertTrue(subscriptions.isEmpty(), 
                "Component " + componentIdStr + " subscriptions should be empty");
    }

    @Then("component {string} should not receive its own published data")
    public void componentShouldNotReceiveItsOwnPublishedData(String componentIdStr) {
        // Allow a short time for event propagation
        try {
            Thread.sleep(50); // Short delay for async processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Check if the component received any data from the channel it published to
        String channel = (String) testContext.get("lastPublishedChannel");
        assertNotNull(channel, "Last published channel should be in context");
        
        List<ComponentDataEvent> events = receivedEvents.get(componentIdStr);
        
        // If the component didn't receive any events, the test passes
        if (events == null || events.isEmpty()) {
            return;
        }
        
        // If the component received events, ensure none are from the channel it published to
        boolean receivedOwnPublishedData = events.stream()
                .anyMatch(event -> event.getDataChannel().equals(channel) &&
                        event.getSourceId().equals(componentIds.get(componentIdStr)));
        
        assertFalse(receivedOwnPublishedData, 
                "Component " + componentIdStr + " should not receive its own published data");
    }
}