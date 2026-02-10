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

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.test.integration.ConfigurationDrivenNotifier;
import org.s8r.test.mock.MockConfigurationAdapter;
import org.s8r.test.mock.MockNotificationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test context for ConfigurationPort and NotificationPort integration tests.
 * 
 * <p>This class manages the test context and state for integration between
 * ConfigurationPort and NotificationPort operations.
 */
public class ConfigurationNotificationIntegrationContext {
    private final ConfigurationPort configurationPort;
    private final NotificationPort notificationPort;
    private final ConfigurationDrivenNotifier notifier;
    private final List<NotificationRecord> sentNotifications;
    private final List<String> loggedMessages;
    private final Map<String, List<NotificationRecord>> batchedNotifications;
    private final AtomicBoolean notificationsEnabled;
    
    /**
     * Creates a new ConfigurationNotificationIntegrationContext.
     */
    public ConfigurationNotificationIntegrationContext() {
        this.configurationPort = new MockConfigurationAdapter();
        this.notificationPort = new MockNotificationAdapter();
        this.notifier = new ConfigurationDrivenNotifier(configurationPort, notificationPort);
        this.sentNotifications = new CopyOnWriteArrayList<>();
        this.loggedMessages = new CopyOnWriteArrayList<>();
        this.batchedNotifications = new ConcurrentHashMap<>();
        this.notificationsEnabled = new AtomicBoolean(true);
    }
    
    /**
     * Resets the test context.
     */
    public void reset() {
        sentNotifications.clear();
        loggedMessages.clear();
        batchedNotifications.clear();
        notificationsEnabled.set(true);
        
        // Reset adapters
        ((MockConfigurationAdapter) configurationPort).reset();
        ((MockNotificationAdapter) notificationPort).reset();
        
        // Reset the notifier
        notifier.reset();
    }
    
    /**
     * Gets the ConfigurationPort.
     * 
     * @return The ConfigurationPort
     */
    public ConfigurationPort getConfigurationPort() {
        return configurationPort;
    }
    
    /**
     * Gets the NotificationPort.
     * 
     * @return The NotificationPort
     */
    public NotificationPort getNotificationPort() {
        return notificationPort;
    }
    
    /**
     * Gets the ConfigurationDrivenNotifier.
     * 
     * @return The ConfigurationDrivenNotifier
     */
    public ConfigurationDrivenNotifier getNotifier() {
        return notifier;
    }
    
    /**
     * Sets a configuration property.
     * 
     * @param key The property key
     * @param value The property value
     */
    public void setConfigurationProperty(String key, String value) {
        ((MockConfigurationAdapter) configurationPort).setProperty(key, value);
    }
    
    /**
     * Sets the notification enabled flag.
     * 
     * @param enabled True if notifications are enabled
     */
    public void setNotificationsEnabled(boolean enabled) {
        notificationsEnabled.set(enabled);
        setConfigurationProperty("notification.enabled", String.valueOf(enabled));
    }
    
    /**
     * Sets the notification severity threshold.
     * 
     * @param severity The severity threshold
     */
    public void setNotificationSeverityThreshold(NotificationSeverity severity) {
        setConfigurationProperty("notification.severity.threshold", severity.name());
    }
    
    /**
     * Sends a notification using the default configuration values.
     * 
     * @param subject The notification subject
     * @param message The notification message
     * @param properties Additional properties for the notification
     * @return The result of the notification operation
     */
    public NotificationResult sendNotification(String subject, String message, Map<String, String> properties) {
        NotificationResult result = notifier.sendNotificationWithDefaultConfig(subject, message, properties);
        
        if (result.isSuccess()) {
            recordNotification(null, subject, message, null, null, null, properties, result);
        } else {
            logMessage("Failed to send notification: " + result.getMessage() + 
                      (result.getErrorDetails().isPresent() ? " - " + result.getErrorDetails().get() : ""));
        }
        
        return result;
    }
    
    /**
     * Sends a notification to a group of recipients.
     * 
     * @param groupKey The configuration key for the recipient group
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @return A list of notification results
     */
    public List<NotificationResult> sendGroupNotification(
            String groupKey, String subject, String message, NotificationSeverity severity) {
        List<NotificationResult> results = notifier.sendGroupNotification(groupKey, subject, message, severity);
        
        for (NotificationResult result : results) {
            String recipientGroup = "notification.recipients." + groupKey;
            if (result.isSuccess()) {
                recordNotification(recipientGroup, subject, message, severity, null, null, 
                                 Map.of("group", groupKey), result);
            }
        }
        
        return results;
    }
    
    /**
     * Sends a notification using a template.
     * 
     * @param templateKey The template key
     * @param parameters The template parameters
     * @return The result of the notification operation
     */
    public NotificationResult sendTemplatedNotification(String templateKey, Map<String, String> parameters) {
        NotificationResult result = notifier.sendTemplatedNotification(templateKey, parameters);
        
        if (result.isSuccess()) {
            recordNotification(null, "Template Notification", result.getMessage(), null, null, null, 
                             parameters, result);
        }
        
        return result;
    }
    
    /**
     * Sends a notification with a specific severity.
     * 
     * @param severity The notification severity
     * @return The result of the notification operation
     */
    public NotificationResult sendNotificationWithSeverity(NotificationSeverity severity) {
        NotificationResult result = notifier.sendNotificationWithSeverity(
                "Severity Test", "Test message with severity " + severity, severity);
        
        if (result.isSuccess()) {
            recordNotification(null, "Severity Test", "Test message with severity " + severity, 
                             severity, null, null, Map.of(), result);
        } else {
            // Log that notification was suppressed based on severity
            logMessage("Notification suppressed - severity " + severity + 
                      " is below threshold " + getNotificationSeverityThreshold());
        }
        
        return result;
    }
    
    /**
     * Gets the configured notification severity threshold.
     * 
     * @return The severity threshold
     */
    public NotificationSeverity getNotificationSeverityThreshold() {
        String thresholdStr = configurationPort.getString("notification.severity.threshold", "INFO");
        return NotificationSeverity.valueOf(thresholdStr);
    }
    
    /**
     * Sends an urgent notification.
     * 
     * @return A map of results by channel
     */
    public Map<NotificationChannel, NotificationResult> sendUrgentNotification() {
        Map<NotificationChannel, NotificationResult> results = notifier.sendUrgentNotification(
                "URGENT", "This is an urgent notification");
        
        for (Map.Entry<NotificationChannel, NotificationResult> entry : results.entrySet()) {
            NotificationChannel channel = entry.getKey();
            NotificationResult result = entry.getValue();
            
            if (result.isSuccess()) {
                recordNotification(null, "URGENT", "This is an urgent notification", 
                                 NotificationSeverity.CRITICAL, channel, null, 
                                 Map.of("urgent", "true"), result);
            }
        }
        
        return results;
    }
    
    /**
     * Processes a configuration update and notifies interested parties.
     * 
     * @param key The configuration key
     * @param value The new value
     * @return The notification result
     */
    public NotificationResult processConfigurationUpdate(String key, String value) {
        // Update the property
        setConfigurationProperty(key, value);
        
        // Notify about the change
        return notifier.notifyConfigurationChange(key, value);
    }
    
    /**
     * Queues notifications for batch processing.
     * 
     * @param count The number of notifications to queue
     * @return The number of batches created
     */
    public int queueBatchNotifications(int count) {
        return notifier.queueBatchNotifications(count);
    }
    
    /**
     * Records a sent notification.
     * 
     * @param recipient The recipient
     * @param subject The subject
     * @param message The message
     * @param severity The severity
     * @param channel The channel
     * @param format The content format
     * @param properties Additional properties
     * @param result The notification result
     */
    private void recordNotification(
            String recipient, String subject, String message, 
            NotificationSeverity severity, NotificationChannel channel, 
            ContentFormat format, Map<String, String> properties,
            NotificationResult result) {
        
        NotificationRecord record = new NotificationRecord(
                recipient, subject, message, severity, channel, format, 
                properties, result.getNotificationId(), result.getStatus());
        
        sentNotifications.add(record);
        
        // Add to batch if this is a batch notification
        if (properties != null && properties.containsKey("batch")) {
            String batchId = properties.get("batch");
            batchedNotifications.computeIfAbsent(batchId, k -> new ArrayList<>())
                              .add(record);
        }
    }
    
    /**
     * Logs a message.
     * 
     * @param message The message to log
     */
    public void logMessage(String message) {
        loggedMessages.add(message);
    }
    
    /**
     * Gets the list of sent notifications.
     * 
     * @return The list of sent notifications
     */
    public List<NotificationRecord> getSentNotifications() {
        return sentNotifications;
    }
    
    /**
     * Gets the list of batched notifications.
     * 
     * @param batchId The batch ID
     * @return The list of notifications in the batch
     */
    public List<NotificationRecord> getBatchNotifications(String batchId) {
        return batchedNotifications.getOrDefault(batchId, List.of());
    }
    
    /**
     * Gets the number of batches.
     * 
     * @return The number of batches
     */
    public int getBatchCount() {
        return batchedNotifications.size();
    }
    
    /**
     * Gets the list of logged messages.
     * 
     * @return The list of logged messages
     */
    public List<String> getLoggedMessages() {
        return loggedMessages;
    }
    
    /**
     * Gets the recipients from a configuration key.
     * 
     * @param groupKey The group key
     * @return The list of recipients
     */
    public List<String> getRecipientsFromConfig(String groupKey) {
        String key = "notification.recipients." + groupKey;
        String recipients = configurationPort.getString(key, "");
        return Arrays.asList(recipients.split(","));
    }
    
    /**
     * Gets the default recipient from configuration.
     * 
     * @return The default recipient
     */
    public String getDefaultRecipient() {
        return configurationPort.getString("notification.recipients.default", "");
    }
    
    /**
     * Gets the notification channels for a given type.
     * 
     * @param type The channel type
     * @return The list of channels
     */
    public List<NotificationChannel> getNotificationChannels(String type) {
        String key = "notification.channels." + type;
        String channelsStr = configurationPort.getString(key, "EMAIL");
        
        List<NotificationChannel> channels = new ArrayList<>();
        for (String channelName : channelsStr.split(",")) {
            try {
                channels.add(NotificationChannel.valueOf(channelName.trim()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid channel names
            }
        }
        
        return channels;
    }
    
    /**
     * Gets the default notification format.
     * 
     * @return The default notification format
     */
    public ContentFormat getDefaultFormat() {
        String formatStr = configurationPort.getString("notification.format.default", "TEXT");
        return ContentFormat.valueOf(formatStr);
    }
    
    /**
     * Record of a sent notification.
     */
    public static class NotificationRecord {
        private final String recipient;
        private final String subject;
        private final String message;
        private final NotificationSeverity severity;
        private final NotificationChannel channel;
        private final ContentFormat format;
        private final Map<String, String> properties;
        private final String notificationId;
        private final DeliveryStatus status;
        
        /**
         * Creates a new NotificationRecord.
         * 
         * @param recipient The recipient
         * @param subject The subject
         * @param message The message
         * @param severity The severity
         * @param channel The channel
         * @param format The content format
         * @param properties Additional properties
         * @param notificationId The notification ID
         * @param status The delivery status
         */
        public NotificationRecord(
                String recipient, String subject, String message, 
                NotificationSeverity severity, NotificationChannel channel, 
                ContentFormat format, Map<String, String> properties,
                String notificationId, DeliveryStatus status) {
            
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.severity = severity;
            this.channel = channel;
            this.format = format;
            this.properties = new HashMap<>(properties);
            this.notificationId = notificationId;
            this.status = status;
        }
        
        /**
         * Gets the recipient.
         * 
         * @return The recipient
         */
        public String getRecipient() {
            return recipient;
        }
        
        /**
         * Gets the subject.
         * 
         * @return The subject
         */
        public String getSubject() {
            return subject;
        }
        
        /**
         * Gets the message.
         * 
         * @return The message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the severity.
         * 
         * @return The severity
         */
        public NotificationSeverity getSeverity() {
            return severity;
        }
        
        /**
         * Gets the channel.
         * 
         * @return The channel
         */
        public NotificationChannel getChannel() {
            return channel;
        }
        
        /**
         * Gets the content format.
         * 
         * @return The content format
         */
        public ContentFormat getFormat() {
            return format;
        }
        
        /**
         * Gets the properties.
         * 
         * @return The properties
         */
        public Map<String, String> getProperties() {
            return properties;
        }
        
        /**
         * Gets the notification ID.
         * 
         * @return The notification ID
         */
        public String getNotificationId() {
            return notificationId;
        }
        
        /**
         * Gets the delivery status.
         * 
         * @return The delivery status
         */
        public DeliveryStatus getStatus() {
            return status;
        }
    }
}