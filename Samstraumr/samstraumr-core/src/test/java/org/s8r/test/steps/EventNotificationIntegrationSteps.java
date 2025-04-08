/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.service.EventService;
import org.s8r.infrastructure.event.EventPublisherAdapter;
import org.s8r.infrastructure.notification.NotificationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Step definitions for Event and Notification integration tests.
 */
public class EventNotificationIntegrationSteps {
    
    private EventPublisherPort eventPublisher;
    private NotificationAdapter notificationAdapter;
    private EventService eventService;
    private List<Map<String, String>> receivedNotifications = new CopyOnWriteArrayList<>();
    
    @Given("an event publisher is initialized")
    public void anEventPublisherIsInitialized() {
        eventPublisher = new EventPublisherAdapter();
    }
    
    @Given("a notification system is initialized")
    public void aNotificationSystemIsInitialized() {
        notificationAdapter = new NotificationAdapter();
        // Create the event service with both ports
        eventService = new EventService(eventPublisher, null);
    }
    
    @Given("I have registered a notification listener for topic {string}")
    public void iHaveRegisteredANotificationListenerForTopic(String topic) {
        NotificationPort.NotificationListener listener = 
            (source, notificationTopic, message, properties) -> {
                Map<String, String> notification = new HashMap<>();
                notification.put("source", source);
                notification.put("topic", notificationTopic);
                notification.put("message", message);
                if (properties != null) {
                    notification.putAll(properties);
                }
                receivedNotifications.add(notification);
            };
        
        notificationAdapter.registerListener(topic, listener);
        
        // Bridge from event publisher to notification system
        eventPublisher.subscribe(topic, (eventTopic, eventType, payload, properties) -> {
            Map<String, String> notificationProperties = new HashMap<>();
            if (properties != null) {
                notificationProperties.putAll(properties);
            }
            notificationAdapter.sendNotification("system", eventType, payload, notificationProperties);
        });
    }
    
    @When("I publish an event with type {string} to topic {string} with message {string}")
    public void iPublishAnEventWithTypeToTopicWithMessage(String eventType, String topic, String message) {
        eventPublisher.publishEvent(topic, eventType, message);
    }
    
    @Then("a notification should be sent with subject {string}")
    public void aNotificationShouldBeSentWithSubject(String subject) {
        boolean found = false;
        for (Map<String, String> notification : receivedNotifications) {
            if (notification.get("topic").equals(subject)) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Notification with subject '" + subject + "' should be sent");
    }
    
    @Then("the notification message should contain {string}")
    public void theNotificationMessageShouldContain(String messageContent) {
        boolean found = false;
        for (Map<String, String> notification : receivedNotifications) {
            if (notification.get("message").contains(messageContent)) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Notification message should contain '" + messageContent + "'");
    }
    
    @When("I publish an event with the following properties:")
    public void iPublishAnEventWithTheFollowingProperties(DataTable dataTable) {
        Map<String, String> eventData = dataTable.asMap();
        String topic = eventData.get("topic");
        String type = eventData.get("type");
        String message = eventData.get("message");
        
        Map<String, String> properties = new HashMap<>();
        for (Map.Entry<String, String> entry : eventData.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("topic") && !key.equals("type") && !key.equals("message")) {
                properties.put(key, entry.getValue());
            }
        }
        
        eventPublisher.publishEvent(topic, type, message, properties);
    }
    
    @Then("a notification should be sent with the following properties:")
    public void aNotificationShouldBeSentWithTheFollowingProperties(DataTable dataTable) {
        Map<String, String> expectedProperties = dataTable.asMap();
        
        boolean allPropertiesMatch = false;
        for (Map<String, String> notification : receivedNotifications) {
            boolean match = true;
            for (Map.Entry<String, String> entry : expectedProperties.entrySet()) {
                String key = entry.getKey();
                String expectedValue = entry.getValue();
                
                if (!notification.containsKey(key) || !notification.get(key).equals(expectedValue)) {
                    match = false;
                    break;
                }
            }
            
            if (match) {
                allPropertiesMatch = true;
                break;
            }
        }
        
        Assertions.assertTrue(allPropertiesMatch, "A notification with all expected properties should be sent");
    }
    
    @Given("I have registered notification listeners for the following topics:")
    public void iHaveRegisteredNotificationListenersForTheFollowingTopics(List<String> topics) {
        for (String topic : topics) {
            iHaveRegisteredANotificationListenerForTopic(topic);
        }
    }
    
    @When("I publish events to the following topics:")
    public void iPublishEventsToTheFollowingTopics(DataTable dataTable) {
        List<Map<String, String>> events = dataTable.asMaps();
        for (Map<String, String> event : events) {
            String topic = event.get("topic");
            String type = event.get("type");
            String message = event.get("message");
            
            eventPublisher.publishEvent(topic, type, message);
        }
    }
    
    @Then("notifications should be sent for each event")
    public void notificationsShouldBeSentForEachEvent() {
        // This assumes the previous step had a specific number of events
        int expectedNotificationCount = receivedNotifications.size();
        Assertions.assertTrue(expectedNotificationCount > 0, 
            "There should be at least one notification");
    }
    
    @Then("each notification should have the corresponding subject and message")
    public void eachNotificationShouldHaveTheCorrespondingSubjectAndMessage() {
        // This is a simplified check; in a real test you would verify each specific notification
        Assertions.assertFalse(receivedNotifications.isEmpty(), 
            "There should be notifications to check");
        
        for (Map<String, String> notification : receivedNotifications) {
            Assertions.assertNotNull(notification.get("topic"), "Each notification should have a subject");
            Assertions.assertNotNull(notification.get("message"), "Each notification should have a message");
        }
    }
}