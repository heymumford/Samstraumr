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

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Integration component that bridges ConfigurationPort and NotificationPort.
 * 
 * <p>This class uses configuration values to drive notification behavior,
 * demonstrating the integration between these two ports.
 */
public class ConfigurationDrivenNotifier {
    private final ConfigurationPort configurationPort;
    private final NotificationPort notificationPort;
    private final Map<String, List<Map<String, String>>> batchedNotifications;
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger batchCounter;
    
    /**
     * Creates a new ConfigurationDrivenNotifier.
     * 
     * @param configurationPort The ConfigurationPort
     * @param notificationPort The NotificationPort
     */
    public ConfigurationDrivenNotifier(ConfigurationPort configurationPort, NotificationPort notificationPort) {
        this.configurationPort = configurationPort;
        this.notificationPort = notificationPort;
        this.batchedNotifications = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.batchCounter = new AtomicInteger(0);
    }
    
    /**
     * Resets the notifier to its initial state.
     */
    public void reset() {
        batchedNotifications.clear();
        batchCounter.set(0);
        
        // Shutdown and recreate the scheduler
        scheduler.shutdownNow();
    }
    
    /**
     * Sends a notification using default configuration values.
     * 
     * @param subject The notification subject
     * @param message The notification message
     * @param properties Additional properties for the notification
     * @return The result of the notification operation
     */
    public NotificationResult sendNotificationWithDefaultConfig(
            String subject, String message, Map<String, String> properties) {
        
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled"));
        }
        
        // Get default recipient
        String recipient = configurationPort.getString("notification.recipients.default", "");
        if (recipient.isEmpty()) {
            return NotificationResult.failure(
                "No default recipient configured",
                "notification.recipients.default is not set",
                DeliveryStatus.REJECTED,
                Map.of("reason", "no_recipient"));
        }
        
        // Get default channel and format
        String channelStr = configurationPort.getString("notification.channels.default", "EMAIL");
        String formatStr = configurationPort.getString("notification.format.default", "TEXT");
        
        try {
            NotificationChannel channel = NotificationChannel.valueOf(channelStr);
            ContentFormat format = ContentFormat.valueOf(formatStr);
            
            // Send notification with advanced options
            return notificationPort.sendAdvancedNotification(
                recipient, subject, message, 
                NotificationSeverity.INFO, channel, format, properties);
            
        } catch (IllegalArgumentException e) {
            return NotificationResult.failure(
                "Invalid channel or format",
                "Configuration contains invalid channel or format: " + e.getMessage(),
                DeliveryStatus.REJECTED,
                Map.of("reason", "invalid_config"));
        }
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
        
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return List.of(NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled")));
        }
        
        // Get recipients from configuration
        String key = "notification.recipients." + groupKey;
        String recipientsStr = configurationPort.getString(key, "");
        if (recipientsStr.isEmpty()) {
            return List.of(NotificationResult.failure(
                "No recipients configured for group",
                "No recipients found for group: " + groupKey,
                DeliveryStatus.REJECTED,
                Map.of("reason", "no_recipients", "group", groupKey)));
        }
        
        // Parse recipients
        String[] recipients = recipientsStr.split(",");
        List<NotificationResult> results = new ArrayList<>();
        
        // Check severity threshold
        NotificationSeverity threshold = getNotificationSeverityThreshold();
        if (severity.ordinal() < threshold.ordinal()) {
            return List.of(NotificationResult.failure(
                "Notification suppressed due to severity threshold",
                "Severity " + severity + " is below threshold " + threshold,
                DeliveryStatus.REJECTED,
                Map.of("reason", "severity_threshold", "severity", severity.name(), 
                     "threshold", threshold.name())));
        }
        
        // Send notification to each recipient
        for (String recipient : recipients) {
            recipient = recipient.trim();
            if (!recipient.isEmpty()) {
                Map<String, String> properties = new HashMap<>();
                properties.put("group", groupKey);
                
                NotificationResult result = notificationPort.sendNotificationToRecipient(
                    recipient, subject, message, severity, null);
                
                results.add(result);
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
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled"));
        }
        
        // Get template from configuration
        String key = "notification.templates." + templateKey;
        String template = configurationPort.getString(key, "");
        if (template.isEmpty()) {
            return NotificationResult.failure(
                "Template not found",
                "No template found with key: " + templateKey,
                DeliveryStatus.REJECTED,
                Map.of("reason", "template_not_found", "template", templateKey));
        }
        
        // Apply parameters to template
        String message = template;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        
        // Get default recipient
        String recipient = configurationPort.getString("notification.recipients.default", "");
        if (recipient.isEmpty()) {
            return NotificationResult.failure(
                "No default recipient configured",
                "notification.recipients.default is not set",
                DeliveryStatus.REJECTED,
                Map.of("reason", "no_recipient"));
        }
        
        // Send notification with basic options
        Map<String, String> properties = new HashMap<>(parameters);
        properties.put("template", templateKey);
        
        return notificationPort.sendNotification(recipient, "Template Notification", message, properties);
    }
    
    /**
     * Sends a notification with a specific severity.
     * 
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @return The result of the notification operation
     */
    public NotificationResult sendNotificationWithSeverity(
            String subject, String message, NotificationSeverity severity) {
        
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled"));
        }
        
        // Check severity threshold
        NotificationSeverity threshold = getNotificationSeverityThreshold();
        if (severity.ordinal() < threshold.ordinal()) {
            return NotificationResult.failure(
                "Notification suppressed due to severity threshold",
                "Severity " + severity + " is below threshold " + threshold,
                DeliveryStatus.REJECTED,
                Map.of("reason", "severity_threshold", "severity", severity.name(), 
                     "threshold", threshold.name()));
        }
        
        // Get default recipient
        String recipient = configurationPort.getString("notification.recipients.default", "");
        if (recipient.isEmpty()) {
            return NotificationResult.failure(
                "No default recipient configured",
                "notification.recipients.default is not set",
                DeliveryStatus.REJECTED,
                Map.of("reason", "no_recipient"));
        }
        
        // Send notification
        return notificationPort.sendNotificationToRecipient(
            recipient, subject, message, severity, null);
    }
    
    /**
     * Sends an urgent notification.
     * 
     * @param subject The notification subject
     * @param message The notification message
     * @return A map of results by channel
     */
    public Map<NotificationChannel, NotificationResult> sendUrgentNotification(
            String subject, String message) {
        
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            Map<NotificationChannel, NotificationResult> results = new HashMap<>();
            NotificationResult failure = NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled"));
            
            results.put(NotificationChannel.EMAIL, failure);
            return results;
        }
        
        // Get urgent channels from configuration
        String channelsStr = configurationPort.getString("notification.channels.urgent", "EMAIL");
        String[] channelNames = channelsStr.split(",");
        
        // Get default recipient
        String recipient = configurationPort.getString("notification.recipients.default", "");
        if (recipient.isEmpty()) {
            Map<NotificationChannel, NotificationResult> results = new HashMap<>();
            NotificationResult failure = NotificationResult.failure(
                "No default recipient configured",
                "notification.recipients.default is not set",
                DeliveryStatus.REJECTED,
                Map.of("reason", "no_recipient"));
            
            results.put(NotificationChannel.EMAIL, failure);
            return results;
        }
        
        // Send notification via each channel
        Map<NotificationChannel, NotificationResult> results = new HashMap<>();
        for (String channelName : channelNames) {
            try {
                NotificationChannel channel = NotificationChannel.valueOf(channelName.trim());
                Map<String, String> properties = new HashMap<>();
                properties.put("urgent", "true");
                properties.put("channel", channel.name());
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                    recipient, subject, message, 
                    NotificationSeverity.CRITICAL, channel, ContentFormat.TEXT, properties);
                
                results.put(channel, result);
                
            } catch (IllegalArgumentException e) {
                // Ignore invalid channel names
            }
        }
        
        return results;
    }
    
    /**
     * Notifies about a configuration change.
     * 
     * @param key The configuration key
     * @param value The new value
     * @return The notification result
     */
    public NotificationResult notifyConfigurationChange(String key, String value) {
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return NotificationResult.failure(
                "Notifications are disabled", 
                "Notifications are disabled in configuration", 
                DeliveryStatus.REJECTED,
                Map.of("reason", "disabled"));
        }
        
        // Build notification message
        String message = String.format("Configuration changed: %s = %s", key, value);
        
        // Use admin group as recipients
        String groupKey = "admin_group";
        
        // Send notification to admin group
        List<NotificationResult> results = sendGroupNotification(
            groupKey, "Configuration Change", message, NotificationSeverity.INFO);
        
        // Return first result
        return results.isEmpty() ? 
            NotificationResult.failure("No notification sent", "No recipients found", 
                                   DeliveryStatus.FAILED, Map.of()) : 
            results.get(0);
    }
    
    /**
     * Queues notifications for batch processing.
     * 
     * @param count The number of notifications to queue
     * @return The number of batches created
     */
    public int queueBatchNotifications(int count) {
        // Check if notifications are enabled
        if (!isNotificationsEnabled()) {
            return 0;
        }
        
        // Get batch configuration
        int batchSize = configurationPort.getInt("notification.batch.size", 10);
        int batchInterval = configurationPort.getInt("notification.batch.interval", 5000);
        
        // Calculate number of batches
        int batchCount = (count + batchSize - 1) / batchSize;
        
        // Create batches
        for (int i = 0; i < batchCount; i++) {
            String batchId = "batch-" + batchCounter.incrementAndGet();
            List<Map<String, String>> notifications = new ArrayList<>();
            
            // Fill batch
            int start = i * batchSize;
            int end = Math.min(start + batchSize, count);
            
            for (int j = start; j < end; j++) {
                Map<String, String> notification = new HashMap<>();
                notification.put("subject", "Batch Notification " + (j + 1));
                notification.put("message", "This is notification " + (j + 1) + " in batch " + batchId);
                notification.put("batch", batchId);
                notification.put("index", String.valueOf(j - start));
                
                notifications.add(notification);
            }
            
            // Store batch
            batchedNotifications.put(batchId, notifications);
            
            // Schedule batch delivery
            final String finalBatchId = batchId;
            scheduler.schedule(
                () -> deliverBatch(finalBatchId), 
                i * batchInterval, TimeUnit.MILLISECONDS);
        }
        
        return batchCount;
    }
    
    /**
     * Delivers a batch of notifications.
     * 
     * @param batchId The batch ID
     */
    private void deliverBatch(String batchId) {
        List<Map<String, String>> notifications = batchedNotifications.get(batchId);
        if (notifications == null) {
            return;
        }
        
        String recipient = configurationPort.getString("notification.recipients.default", "");
        if (recipient.isEmpty()) {
            return;
        }
        
        for (Map<String, String> notification : notifications) {
            String subject = notification.get("subject");
            String message = notification.get("message");
            
            notificationPort.sendNotification(recipient, subject, message, notification);
        }
    }
    
    /**
     * Checks if notifications are enabled in configuration.
     * 
     * @return True if notifications are enabled
     */
    private boolean isNotificationsEnabled() {
        return configurationPort.getBoolean("notification.enabled", true);
    }
    
    /**
     * Gets the notification severity threshold.
     * 
     * @return The severity threshold
     */
    private NotificationSeverity getNotificationSeverityThreshold() {
        String thresholdStr = configurationPort.getString("notification.severity.threshold", "INFO");
        try {
            return NotificationSeverity.valueOf(thresholdStr);
        } catch (IllegalArgumentException e) {
            return NotificationSeverity.INFO;
        }
    }
}