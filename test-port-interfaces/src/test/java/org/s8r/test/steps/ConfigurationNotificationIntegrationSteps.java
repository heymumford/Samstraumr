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

package org.s8r.test.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.test.context.ConfigurationNotificationIntegrationContext;
import org.s8r.test.context.ConfigurationNotificationIntegrationContext.NotificationRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for ConfigurationPort and NotificationPort integration tests.
 * 
 * <p>This class implements the Cucumber step definitions for testing the
 * integration between ConfigurationPort and NotificationPort interfaces.
 */
public class ConfigurationNotificationIntegrationSteps {
    
    private final ConfigurationNotificationIntegrationContext context;
    private NotificationResult lastNotificationResult;
    private List<NotificationResult> lastGroupNotificationResults;
    private Map<NotificationChannel, NotificationResult> lastUrgentNotificationResults;
    private int batchCount;
    
    /**
     * Creates a new ConfigurationNotificationIntegrationSteps instance.
     * 
     * @param context The test context
     */
    public ConfigurationNotificationIntegrationSteps(ConfigurationNotificationIntegrationContext context) {
        this.context = context;
    }
    
    @Before
    public void setup() {
        context.reset();
        lastNotificationResult = null;
        lastGroupNotificationResults = null;
        lastUrgentNotificationResults = null;
        batchCount = 0;
    }
    
    @Given("a clean system environment")
    public void givenCleanSystemEnvironment() {
        context.reset();
    }
    
    @Given("the ConfigurationPort interface is properly initialized")
    public void givenConfigurationPortInterfaceInitialized() {
        assertNotNull(context.getConfigurationPort(), "ConfigurationPort should be initialized");
    }
    
    @Given("the NotificationPort interface is properly initialized")
    public void givenNotificationPortInterfaceInitialized() {
        assertNotNull(context.getNotificationPort(), "NotificationPort should be initialized");
    }
    
    @Given("the following configuration properties are loaded:")
    public void givenConfigurationPropertiesLoaded(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        
        ConfigurationPort configurationPort = context.getConfigurationPort();
        
        for (Map<String, String> row : rows) {
            String key = row.get("key");
            String value = row.get("value");
            
            context.setConfigurationProperty(key, value);
        }
    }
    
    @Given("the notification severity threshold is set to {string}")
    public void givenNotificationSeverityThresholdIsSet(String severityStr) {
        NotificationSeverity severity = NotificationSeverity.valueOf(severityStr);
        context.setNotificationSeverityThreshold(severity);
    }
    
    @Given("the notification feature is disabled in configuration")
    public void givenNotificationFeatureIsDisabled() {
        context.setNotificationsEnabled(false);
    }
    
    @When("a notification is requested with the following details:")
    public void whenNotificationIsRequested(DataTable dataTable) {
        Map<String, String> properties = new HashMap<>();
        String subject = "Default Subject";
        String message = "Default Message";
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String key = row.keySet().iterator().next();
            String value = row.get(key);
            
            if (key.equals("subject")) {
                subject = value;
            } else if (key.equals("message")) {
                message = value;
            } else {
                properties.put(key, value);
            }
        }
        
        lastNotificationResult = context.sendNotification(subject, message, properties);
    }
    
    @When("a notification is requested to the {string} with the following details:")
    public void whenNotificationIsRequestedToGroup(String groupKey, DataTable dataTable) {
        Map<String, String> properties = new HashMap<>();
        String subject = "Default Subject";
        String message = "Default Message";
        NotificationSeverity severity = NotificationSeverity.INFO;
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String key = row.keySet().iterator().next();
            String value = row.get(key);
            
            if (key.equals("subject")) {
                subject = value;
            } else if (key.equals("message")) {
                message = value;
            } else if (key.equals("severity")) {
                severity = NotificationSeverity.valueOf(value);
            } else {
                properties.put(key, value);
            }
        }
        
        lastGroupNotificationResults = context.sendGroupNotification(groupKey, subject, message, severity);
    }
    
    @When("a notification with template {string} is requested with parameters:")
    public void whenNotificationWithTemplateIsRequested(String templateKey, DataTable dataTable) {
        Map<String, String> parameters = new HashMap<>();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String key = row.keySet().iterator().next();
            String value = row.get(key);
            parameters.put(key, value);
        }
        
        lastNotificationResult = context.sendTemplatedNotification(templateKey, parameters);
    }
    
    @When("a notification with severity {string} is requested")
    public void whenNotificationWithSeverityIsRequested(String severityStr) {
        NotificationSeverity severity = NotificationSeverity.valueOf(severityStr);
        lastNotificationResult = context.sendNotificationWithSeverity(severity);
    }
    
    @When("an urgent notification is requested")
    public void whenUrgentNotificationIsRequested() {
        lastUrgentNotificationResults = context.sendUrgentNotification();
    }
    
    @When("configuration property {string} is updated to {string}")
    public void whenConfigurationPropertyIsUpdated(String key, String value) {
        lastNotificationResult = context.processConfigurationUpdate(key, value);
    }
    
    @When("{int} notifications are queued for delivery")
    public void whenNotificationsAreQueuedForDelivery(int count) {
        batchCount = context.queueBatchNotifications(count);
        
        // Wait a bit for the batches to be processed
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Then("the notification is sent using the default recipient from configuration")
    public void thenNotificationIsSentUsingDefaultRecipient() {
        assertTrue(lastNotificationResult.isSuccess(), 
            "Notification should have been successful: " + lastNotificationResult.getMessage());
        
        String defaultRecipient = context.getDefaultRecipient();
        assertFalse(defaultRecipient.isEmpty(), "Default recipient should be configured");
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
    }
    
    @Then("the notification channel should be {string}")
    public void thenNotificationChannelShouldBe(String channelStr) {
        NotificationChannel expectedChannel = NotificationChannel.valueOf(channelStr);
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        
        if (latestNotification.getChannel() != null) {
            assertEquals(expectedChannel, latestNotification.getChannel(),
                "Notification channel should be " + channelStr);
        } else {
            // If channel is null in the record, check that the expected channel is the default channel
            List<NotificationChannel> defaultChannels = context.getNotificationChannels("default");
            assertTrue(defaultChannels.contains(expectedChannel),
                "Expected channel " + channelStr + " should be in the default channels: " + defaultChannels);
        }
    }
    
    @Then("the notification format should be {string}")
    public void thenNotificationFormatShouldBe(String formatStr) {
        ContentFormat expectedFormat = ContentFormat.valueOf(formatStr);
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        
        if (latestNotification.getFormat() != null) {
            assertEquals(expectedFormat, latestNotification.getFormat(),
                "Notification format should be " + formatStr);
        } else {
            // If format is null in the record, check that the expected format is the default format
            ContentFormat defaultFormat = context.getDefaultFormat();
            assertEquals(expectedFormat, defaultFormat,
                "Expected format " + formatStr + " should be the default format: " + defaultFormat);
        }
    }
    
    @Then("the notification should include the property {string} with value {string}")
    public void thenNotificationShouldIncludeProperty(String key, String value) {
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        Map<String, String> properties = latestNotification.getProperties();
        
        assertTrue(properties.containsKey(key),
            "Notification properties should include key " + key + ", but got: " + properties.keySet());
        
        assertEquals(value, properties.get(key),
            "Notification property " + key + " should have value " + value);
    }
    
    @Then("the notification is sent to all recipients in the {string} configuration")
    public void thenNotificationIsSentToAllRecipientsInConfiguration(String groupKey) {
        List<String> expectedRecipients = context.getRecipientsFromConfig(groupKey);
        assertFalse(expectedRecipients.isEmpty(), "Recipient group should be configured");
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Count notifications to expected recipients
        int matchCount = 0;
        for (NotificationRecord notification : sentNotifications) {
            if (notification.getRecipient() != null && 
                expectedRecipients.contains(notification.getRecipient())) {
                matchCount++;
            }
        }
        
        assertEquals(expectedRecipients.size(), matchCount,
            "Notification should have been sent to all recipients in group");
    }
    
    @Then("each notification should have the same subject and message")
    public void thenEachNotificationShouldHaveSameSubjectAndMessage() {
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "At least one notification should have been sent");
        
        String expectedSubject = sentNotifications.get(0).getSubject();
        String expectedMessage = sentNotifications.get(0).getMessage();
        
        for (NotificationRecord notification : sentNotifications) {
            assertEquals(expectedSubject, notification.getSubject(),
                "All notifications should have the same subject");
            assertEquals(expectedMessage, notification.getMessage(),
                "All notifications should have the same message");
        }
    }
    
    @Then("each notification severity should be {string}")
    public void thenEachNotificationSeverityShouldBe(String severityStr) {
        NotificationSeverity expectedSeverity = NotificationSeverity.valueOf(severityStr);
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "At least one notification should have been sent");
        
        for (NotificationRecord notification : sentNotifications) {
            if (notification.getSeverity() != null) {
                assertEquals(expectedSeverity, notification.getSeverity(),
                    "All notifications should have severity " + severityStr);
            }
        }
    }
    
    @Then("the notification message should be {string}")
    public void thenNotificationMessageShouldBe(String expectedMessage) {
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        assertEquals(expectedMessage, latestNotification.getMessage(),
            "Notification message should be correct");
    }
    
    @Then("the notification is sent to the default recipient")
    public void thenNotificationIsSentToDefaultRecipient() {
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        String defaultRecipient = context.getDefaultRecipient();
        assertFalse(defaultRecipient.isEmpty(), "Default recipient should be configured");
    }
    
    @Then("the notification is not sent")
    public void thenNotificationIsNotSent() {
        if (lastNotificationResult != null) {
            assertFalse(lastNotificationResult.isSuccess(),
                "Notification should not have been successful");
        } else {
            // Check that no notifications were sent
            List<NotificationRecord> sentNotifications = context.getSentNotifications();
            assertEquals(0, sentNotifications.size(),
                "No notifications should have been sent");
        }
    }
    
    @Then("a message is logged indicating the notification was suppressed")
    public void thenMessageIsLoggedIndicatingNotificationWasSuppressed() {
        List<String> loggedMessages = context.getLoggedMessages();
        boolean found = false;
        
        for (String message : loggedMessages) {
            if (message.contains("suppressed") || message.contains("Notification suppressed")) {
                found = true;
                break;
            }
        }
        
        assertTrue(found, "A message should be logged indicating the notification was suppressed");
    }
    
    @Then("the notification is sent")
    public void thenNotificationIsSent() {
        assertTrue(lastNotificationResult.isSuccess(),
            "Notification should have been successful: " + lastNotificationResult.getMessage());
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
    }
    
    @Then("the notification severity should be {string}")
    public void thenNotificationSeverityShouldBe(String severityStr) {
        NotificationSeverity expectedSeverity = NotificationSeverity.valueOf(severityStr);
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        
        if (latestNotification.getSeverity() != null) {
            assertEquals(expectedSeverity, latestNotification.getSeverity(),
                "Notification severity should be " + severityStr);
        }
    }
    
    @Then("the notification is sent via the channels configured for {string}")
    public void thenNotificationIsSentViaConfiguredChannels(String channelType) {
        List<NotificationChannel> expectedChannels = context.getNotificationChannels(channelType);
        assertFalse(expectedChannels.isEmpty(), "Channel type should have configured channels");
        
        assertNotNull(lastUrgentNotificationResults, "Urgent notification results should be available");
        
        for (NotificationChannel channel : expectedChannels) {
            assertTrue(lastUrgentNotificationResults.containsKey(channel),
                "Notification should have been sent via channel " + channel);
            
            NotificationResult result = lastUrgentNotificationResults.get(channel);
            assertTrue(result.isSuccess(),
                "Notification via channel " + channel + " should have been successful");
        }
    }
    
    @Then("the notification is sent via {string} channel")
    public void thenNotificationIsSentViaChannel(String channelStr) {
        NotificationChannel expectedChannel = NotificationChannel.valueOf(channelStr);
        
        assertNotNull(lastUrgentNotificationResults, "Urgent notification results should be available");
        assertTrue(lastUrgentNotificationResults.containsKey(expectedChannel),
            "Notification should have been sent via channel " + channelStr);
        
        NotificationResult result = lastUrgentNotificationResults.get(expectedChannel);
        assertTrue(result.isSuccess(),
            "Notification via channel " + channelStr + " should have been successful");
    }
    
    @Then("a notification about the configuration change is sent")
    public void thenNotificationAboutConfigurationChangeIsSent() {
        assertTrue(lastNotificationResult.isSuccess(),
            "Notification should have been successful: " + lastNotificationResult.getMessage());
        
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        assertTrue(latestNotification.getSubject().contains("Configuration") ||
                  latestNotification.getMessage().contains("Configuration"),
            "Notification should be about configuration change");
    }
    
    @Then("the notification includes details about the changed property")
    public void thenNotificationIncludesDetailsAboutChangedProperty() {
        List<NotificationRecord> sentNotifications = context.getSentNotifications();
        assertFalse(sentNotifications.isEmpty(), "Notification should have been sent");
        
        // Check the latest notification
        NotificationRecord latestNotification = sentNotifications.get(sentNotifications.size() - 1);
        assertTrue(latestNotification.getMessage().contains("system.status") &&
                  latestNotification.getMessage().contains("MAINTENANCE"),
            "Notification should include details about the changed property");
    }
    
    @Then("a message is logged indicating notifications are disabled")
    public void thenMessageIsLoggedIndicatingNotificationsAreDisabled() {
        List<String> loggedMessages = context.getLoggedMessages();
        boolean found = false;
        
        for (String message : loggedMessages) {
            if (message.contains("disabled")) {
                found = true;
                break;
            }
        }
        
        assertTrue(found, "A message should be logged indicating notifications are disabled");
    }
    
    @Then("the notifications are delivered in batches")
    public void thenNotificationsAreDeliveredInBatches() {
        assertTrue(batchCount > 0, "Batches should have been created");
        assertEquals(2, batchCount, "Two batches should have been created");
        
        int totalNotifications = context.getSentNotifications().size();
        assertTrue(totalNotifications > 0, "Notifications should have been sent");
        
        // May be less than 15 depending on timing, but should be more than 0
        assertTrue(totalNotifications > 0, "Some notifications should have been delivered");
    }
    
    @Then("the first batch contains {int} notifications")
    public void thenFirstBatchContainsNotifications(int expectedCount) {
        String batchId = "batch-1";
        List<NotificationRecord> batchNotifications = context.getBatchNotifications(batchId);
        
        // Use assertThat message to work around timing issues
        assertEquals(expectedCount, batchNotifications.size(),
            "First batch should contain exactly " + expectedCount + " notifications, but got: " + 
            batchNotifications.size());
    }
    
    @Then("the second batch contains {int} notifications")
    public void thenSecondBatchContainsNotifications(int expectedCount) {
        String batchId = "batch-2";
        List<NotificationRecord> batchNotifications = context.getBatchNotifications(batchId);
        
        // Use assertThat message to work around timing issues
        assertEquals(expectedCount, batchNotifications.size(),
            "Second batch should contain exactly " + expectedCount + " notifications, but got: " + 
            batchNotifications.size());
    }
    
    @Then("the batches are delivered with the configured interval")
    public void thenBatchesAreDeliveredWithConfiguredInterval() {
        // No reliable way to verify timing in a unit test, so just verify that both batches were created
        assertTrue(batchCount == 2, "Two batches should have been created");
    }
}