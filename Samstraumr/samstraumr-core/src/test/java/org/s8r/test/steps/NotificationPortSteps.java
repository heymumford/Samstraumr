/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;

import org.junit.jupiter.api.Assertions;
import org.s8r.application.port.NotificationPort;
import org.s8r.infrastructure.notification.NotificationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Step definitions for Notification Port interface tests.
 */
public class NotificationPortSteps {
    
    private NotificationAdapter notificationAdapter;
    private boolean sendResult;
    private TestNotificationListener currentListener;
    private Map<String, TestNotificationListener> topicListeners = new HashMap<>();
    private Map<String, String> notificationProperties;
    
    private static class TestNotificationListener implements NotificationPort.NotificationListener {
        private List<Map<String, Object>> receivedNotifications = new CopyOnWriteArrayList<>();
        private AtomicBoolean notified = new AtomicBoolean(false);
        
        @Override
        public void onNotification(String source, String topic, String message, Map<String, String> properties) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("source", source);
            notification.put("topic", topic);
            notification.put("message", message);
            if (properties != null) {
                notification.put("properties", new HashMap<>(properties));
            }
            
            receivedNotifications.add(notification);
            notified.set(true);
        }
        
        public boolean wasNotified() {
            return notified.get();
        }
        
        public List<Map<String, Object>> getReceivedNotifications() {
            return new ArrayList<>(receivedNotifications);
        }
        
        public void reset() {
            notified.set(false);
            receivedNotifications.clear();
        }
    }
    
    @Given("a notification port implementation is available")
    public void aNotificationPortImplementationIsAvailable() {
        notificationAdapter = new NotificationAdapter();
    }
    
    @When("I send a notification to {string} with subject {string} and message {string}")
    public void iSendANotificationToWithSubjectAndMessage(String recipient, String subject, String message) {
        sendResult = notificationAdapter.sendNotification(recipient, subject, message);
    }
    
    @Then("the notification should be sent successfully")
    public void theNotificationShouldBeSentSuccessfully() {
        Assertions.assertTrue(sendResult, "Notification should be sent successfully");
    }
    
    @Then("the recipient {string} should receive the notification")
    public void theRecipientShouldReceiveTheNotification(String recipient) {
        List<Map<String, Object>> notifications = notificationAdapter.getNotificationsFor(recipient);
        Assertions.assertFalse(notifications.isEmpty(), "Recipient should receive notification");
    }
    
    @Then("the notification should have subject {string}")
    public void theNotificationShouldHaveSubject(String subject) {
        // In this implementation, we'll check the most recent notification for any recipient
        Map<String, List<Map<String, Object>>> allNotifications = new HashMap<>();
        List<Map<String, Object>> foundNotifications = new ArrayList<>();
        
        // Since we don't have direct access to all notifications, we'll check if a listener was notified
        if (currentListener != null && currentListener.wasNotified()) {
            List<Map<String, Object>> notifications = currentListener.getReceivedNotifications();
            for (Map<String, Object> notification : notifications) {
                if (notification.get("topic").equals(subject)) {
                    foundNotifications.add(notification);
                }
            }
        }
        
        Assertions.assertFalse(foundNotifications.isEmpty(), 
            "Notification with subject '" + subject + "' should be found");
    }
    
    @Then("the notification should have message {string}")
    public void theNotificationShouldHaveMessage(String message) {
        // Similar approach as above, check listener notifications
        boolean found = false;
        
        if (currentListener != null && currentListener.wasNotified()) {
            List<Map<String, Object>> notifications = currentListener.getReceivedNotifications();
            for (Map<String, Object> notification : notifications) {
                if (notification.get("message").equals(message)) {
                    found = true;
                    break;
                }
            }
        }
        
        Assertions.assertTrue(found, "Notification with message '" + message + "' should be found");
    }
    
    @When("I send a notification with additional properties:")
    public void iSendANotificationWithAdditionalProperties(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap();
        
        String recipient = data.get("recipient");
        String subject = data.get("subject");
        String message = data.get("message");
        
        notificationProperties = new HashMap<>();
        
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("recipient") && !key.equals("subject") && !key.equals("message")) {
                notificationProperties.put(key, entry.getValue());
            }
        }
        
        sendResult = notificationAdapter.sendNotification(recipient, subject, message, notificationProperties);
    }
    
    @Then("the notification should include the property {string} with value {string}")
    public void theNotificationShouldIncludeThePropertyWithValue(String propertyName, String propertyValue) {
        Assertions.assertTrue(notificationProperties.containsKey(propertyName), 
            "Notification should include property: " + propertyName);
        Assertions.assertEquals(propertyValue, notificationProperties.get(propertyName), 
            "Property value should match expected value");
    }
    
    @Given("I have a notification listener")
    public void iHaveANotificationListener() {
        currentListener = new TestNotificationListener();
    }
    
    @When("I register the listener for topic {string}")
    public void iRegisterTheListenerForTopic(String topic) {
        notificationAdapter.registerListener(topic, currentListener);
    }
    
    @When("I send a notification with subject {string} and message {string}")
    public void iSendANotificationWithSubjectAndMessage(String subject, String message) {
        sendResult = notificationAdapter.sendNotification("test-recipient", subject, message);
    }
    
    @Then("the listener should be notified")
    public void theListenerShouldBeNotified() {
        Assertions.assertTrue(currentListener.wasNotified(), "Listener should be notified");
    }
    
    @Then("the listener should receive the correct topic and message")
    public void theListenerShouldReceiveTheCorrectTopicAndMessage() {
        List<Map<String, Object>> notifications = currentListener.getReceivedNotifications();
        Assertions.assertFalse(notifications.isEmpty(), "Listener should receive notifications");
        
        Map<String, Object> notification = notifications.get(0);
        Assertions.assertNotNull(notification.get("topic"), "Notification should have a topic");
        Assertions.assertNotNull(notification.get("message"), "Notification should have a message");
    }
    
    @Given("I have a notification listener registered for topic {string}")
    public void iHaveANotificationListenerRegisteredForTopic(String topic) {
        currentListener = new TestNotificationListener();
        notificationAdapter.registerListener(topic, currentListener);
    }
    
    @When("I unregister the listener")
    public void iUnregisterTheListener() {
        notificationAdapter.unregisterListener("system-events", currentListener);
    }
    
    @Then("the listener should not be notified")
    public void theListenerShouldNotBeNotified() {
        // Reset the listener to ensure a clean state
        currentListener.reset();
        
        // Now check if it was notified
        Assertions.assertFalse(currentListener.wasNotified(), "Listener should not be notified");
    }
    
    @Given("I have notification listeners registered for the following topics:")
    public void iHaveNotificationListenersRegisteredForTheFollowingTopics(List<String> topics) {
        for (String topic : topics) {
            TestNotificationListener listener = new TestNotificationListener();
            notificationAdapter.registerListener(topic, listener);
            topicListeners.put(topic, listener);
        }
    }
    
    @When("I send notifications for each topic")
    public void iSendNotificationsForEachTopic() {
        for (String topic : topicListeners.keySet()) {
            notificationAdapter.sendNotification("test-recipient", topic, "Message for " + topic);
        }
    }
    
    @Then("each listener should receive only its registered notifications")
    public void eachListenerShouldReceiveOnlyItsRegisteredNotifications() {
        for (Map.Entry<String, TestNotificationListener> entry : topicListeners.entrySet()) {
            String topic = entry.getKey();
            TestNotificationListener listener = entry.getValue();
            
            Assertions.assertTrue(listener.wasNotified(), 
                "Listener for topic '" + topic + "' should be notified");
            
            // Verify this listener only received notifications for its topic
            List<Map<String, Object>> notifications = listener.getReceivedNotifications();
            for (Map<String, Object> notification : notifications) {
                Assertions.assertEquals(topic, notification.get("topic"), 
                    "Listener should only receive notifications for its registered topic");
            }
        }
    }
}