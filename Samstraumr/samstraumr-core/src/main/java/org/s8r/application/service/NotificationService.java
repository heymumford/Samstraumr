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

package org.s8r.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.NotificationPort.DeliveryStatus;
import org.s8r.application.port.NotificationPort.NotificationResult;
import org.s8r.application.port.NotificationPort.NotificationSeverity;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.machine.MachineType;

/**
 * Application service for sending notifications.
 * 
 * <p>This service provides high-level notification operations for the application,
 * using the NotificationPort interface to abstract the actual notification mechanism.
 * 
 * <p>Features:
 * <ul>
 *   <li>Component status and error notifications</li>
 *   <li>Machine creation and status notifications</li>
 *   <li>System warning and error notifications</li>
 *   <li>User-targeted notifications via multiple channels</li>
 *   <li>Push notification support</li>
 *   <li>Asynchronous notification sending</li>
 *   <li>Recipient management</li>
 * </ul>
 */
public class NotificationService {
    
    private final NotificationPort notificationPort;
    private final LoggerPort logger;
    
    // SMS configuration
    private final String defaultCountryCode;
    private final int maxSmsBatchSize;
    private final long smsRateLimitMs;
    private long lastSmsSentTime;
    
    /**
     * Constructs a new NotificationService.
     *
     * @param notificationPort The notification port to use
     * @param logger The logger to use
     */
    public NotificationService(NotificationPort notificationPort, LoggerPort logger) {
        this.notificationPort = notificationPort;
        this.logger = logger;
        
        // Default SMS configuration
        this.defaultCountryCode = "+1"; // US
        this.maxSmsBatchSize = 50;
        this.smsRateLimitMs = 1000; // 1 second between SMS batches
        this.lastSmsSentTime = 0;
        
        logger.debug("NotificationService created");
    }
    
    /**
     * Constructs a new NotificationService with SMS configuration.
     *
     * @param notificationPort The notification port to use
     * @param logger The logger to use
     * @param defaultCountryCode The default country code for SMS
     * @param maxSmsBatchSize The maximum number of SMS messages in a batch
     * @param smsRateLimitMs The minimum time between SMS batches in milliseconds
     */
    public NotificationService(NotificationPort notificationPort, LoggerPort logger, 
                             String defaultCountryCode, int maxSmsBatchSize, long smsRateLimitMs) {
        this.notificationPort = notificationPort;
        this.logger = logger;
        this.defaultCountryCode = defaultCountryCode;
        this.maxSmsBatchSize = maxSmsBatchSize;
        this.smsRateLimitMs = smsRateLimitMs;
        this.lastSmsSentTime = 0;
        
        logger.debug("NotificationService created with SMS configuration");
    }
    
    /**
     * Sends a component status notification.
     *
     * @param component The component
     * @param status The status message
     * @return The notification result
     */
    public NotificationResult sendComponentStatusNotification(ComponentPort component, String status) {
        logger.debug("Sending component status notification for component {}", component.getId());
        
        String subject = "Component Status: " + component.getId().getShortId();
        String content = "Component " + component.getName() + " (" + component.getId().getShortId() + ") status: " + status;
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("componentId", component.getId().getIdString());
        metadata.put("componentName", component.getName());
        metadata.put("componentType", component.getType());
        metadata.put("componentState", component.getState().toString());
        
        return notificationPort.sendSystemNotification(
                subject, content, NotificationSeverity.INFO);
    }
    
    /**
     * Sends a component error notification.
     *
     * @param component The component
     * @param errorMessage The error message
     * @return The notification result
     */
    public NotificationResult sendComponentErrorNotification(ComponentPort component, String errorMessage) {
        logger.debug("Sending component error notification for component {}", component.getId());
        
        String subject = "Component Error: " + component.getId().getShortId();
        String content = "Error in component " + component.getName() + 
                " (" + component.getId().getShortId() + "): " + errorMessage;
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("componentId", component.getId().getIdString());
        metadata.put("componentName", component.getName());
        metadata.put("componentType", component.getType());
        metadata.put("componentState", component.getState().toString());
        metadata.put("errorType", "component_error");
        
        return notificationPort.sendSystemNotification(
                subject, content, NotificationSeverity.ERROR);
    }
    
    /**
     * Sends a machine creation notification.
     *
     * @param machineId The machine ID
     * @param machineType The machine type
     * @param name The machine name
     * @return The notification result
     */
    public NotificationResult sendMachineCreationNotification(
            String machineId, MachineType machineType, String name) {
        logger.debug("Sending machine creation notification for machine {}", machineId);
        
        String subject = "Machine Created: " + machineId;
        String content = "A new machine has been created: " + 
                name + " (" + machineId + ") of type " + machineType;
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("machineId", machineId);
        metadata.put("machineType", machineType.toString());
        metadata.put("machineName", name);
        
        return notificationPort.sendSystemNotification(
                subject, content, NotificationSeverity.INFO);
    }
    
    /**
     * Sends a system warning notification.
     *
     * @param subject The notification subject
     * @param message The notification message
     * @return The notification result
     */
    public NotificationResult sendSystemWarning(String subject, String message) {
        logger.debug("Sending system warning notification");
        
        return notificationPort.sendSystemNotification(
                subject, message, NotificationSeverity.WARNING);
    }
    
    /**
     * Sends a system error notification.
     *
     * @param subject The notification subject
     * @param errorMessage The error message
     * @return The notification result
     */
    public NotificationResult sendSystemError(String subject, String errorMessage) {
        logger.debug("Sending system error notification");
        
        return notificationPort.sendSystemNotification(
                subject, errorMessage, NotificationSeverity.ERROR);
    }
    
    /**
     * Sends a notification to a specific user.
     *
     * @param userId The user ID
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @return The notification result
     */
    public NotificationResult sendUserNotification(
            String userId, 
            String subject, 
            String message, 
            NotificationSeverity severity) {
        logger.debug("Sending user notification to user {}", userId);
        
        // Check if user is registered
        if (!notificationPort.isRecipientRegistered(userId)) {
            logger.warn("User {} is not registered as a notification recipient", userId);
            registerUserRecipient(userId, "email", userId + "@example.com");
        }
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("notificationType", "user_notification");
        
        return notificationPort.sendNotificationToRecipient(
                userId, subject, message, severity, metadata);
    }
    
    /**
     * Registers a user as a notification recipient with email contact.
     *
     * @param userId The user ID
     * @param contactType The contact type (email, sms, etc.)
     * @param contactAddress The contact address
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUserRecipient(String userId, String contactType, String contactAddress) {
        logger.debug("Registering user {} as notification recipient", userId);
        
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", contactType);
        contactInfo.put("address", contactAddress);
        
        return notificationPort.registerRecipient(userId, contactInfo);
    }
    
    /**
     * Unregisters a user as a notification recipient.
     *
     * @param userId The user ID
     * @return true if unregistration was successful, false otherwise
     */
    public boolean unregisterUserRecipient(String userId) {
        logger.debug("Unregistering user {} as notification recipient", userId);
        
        return notificationPort.unregisterRecipient(userId);
    }
    
    /**
     * Gets the status of a notification.
     *
     * @param notificationId The notification ID
     * @return The notification status
     */
    public DeliveryStatus getNotificationStatus(String notificationId) {
        return notificationPort.getNotificationStatus(notificationId);
    }
    
    /**
     * Sends a push notification to a user.
     *
     * @param userId The user ID
     * @param title The notification title
     * @param message The notification message
     * @param severity The notification severity
     * @param data Additional notification data
     * @return The notification result
     */
    public NotificationResult sendPushNotification(
            String userId, 
            String title, 
            String message, 
            NotificationSeverity severity,
            Map<String, String> data) {
        logger.debug("Sending push notification to user {}", userId);
        
        // Check if user is registered
        if (!notificationPort.isRecipientRegistered(userId)) {
            logger.warn("User {} is not registered for push notifications", userId);
            return NotificationResult.failure("N/A", "User not registered for push notifications");
        }
        
        // Prepare metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("notificationType", "push_notification");
        metadata.put("channel", "push");
        
        // Add data to metadata if provided
        if (data != null) {
            metadata.putAll(data);
        }
        
        return notificationPort.sendNotificationToRecipient(
                userId, title, message, severity, metadata);
    }
    
    /**
     * Sends a push notification asynchronously.
     *
     * @param userId The user ID
     * @param title The notification title
     * @param message The notification message
     * @param severity The notification severity
     * @param data Additional notification data
     * @return A CompletableFuture that will be completed with the notification result
     */
    public CompletableFuture<NotificationResult> sendPushNotificationAsync(
            String userId, 
            String title, 
            String message, 
            NotificationSeverity severity,
            Map<String, String> data) {
        return CompletableFuture.supplyAsync(() -> 
            sendPushNotification(userId, title, message, severity, data));
    }
    
    /**
     * Registers a user device for push notifications.
     *
     * @param userId The user ID
     * @param deviceToken The device token
     * @param platform The device platform (e.g., android, ios, web)
     * @param appVersion The app version
     * @return true if registration was successful, false otherwise
     */
    public boolean registerPushDevice(
            String userId, String deviceToken, String platform, String appVersion) {
        logger.debug("Registering push device for user {} on platform {}", userId, platform);
        
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", "push");
        contactInfo.put("device", deviceToken);
        contactInfo.put("platform", platform);
        contactInfo.put("appVersion", appVersion);
        contactInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));
        
        return notificationPort.registerRecipient(userId, contactInfo);
    }
    
    /**
     * Sends a notification asynchronously.
     *
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @param metadata Additional notification metadata
     * @return A CompletableFuture that will be completed with the notification result
     */
    public CompletableFuture<NotificationResult> sendNotificationAsync(
            String subject, String content, NotificationSeverity severity, Map<String, String> metadata) {
        return CompletableFuture.supplyAsync(() -> 
            notificationPort.sendNotification(subject, content, severity, metadata));
    }
    
    /**
     * Sends a user notification asynchronously.
     *
     * @param userId The user ID
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @return A CompletableFuture that will be completed with the notification result
     */
    public CompletableFuture<NotificationResult> sendUserNotificationAsync(
            String userId, String subject, String message, NotificationSeverity severity) {
        return CompletableFuture.supplyAsync(() -> 
            sendUserNotification(userId, subject, message, severity));
    }
    
    /**
     * Sends a component status notification asynchronously.
     *
     * @param component The component
     * @param status The status message
     * @return A CompletableFuture that will be completed with the notification result
     */
    public CompletableFuture<NotificationResult> sendComponentStatusNotificationAsync(
            ComponentPort component, String status) {
        return CompletableFuture.supplyAsync(() -> 
            sendComponentStatusNotification(component, status));
    }
    
    /**
     * Sends a batch of notifications to multiple recipients.
     *
     * @param recipientIds The recipient IDs
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @param metadata Additional notification metadata
     * @return A map of recipient IDs to notification results
     */
    public Map<String, NotificationResult> sendBatchNotification(
            Iterable<String> recipientIds,
            String subject,
            String content,
            NotificationSeverity severity,
            Map<String, String> metadata) {
        logger.debug("Sending batch notification to multiple recipients");
        
        Map<String, NotificationResult> results = new HashMap<>();
        
        for (String recipientId : recipientIds) {
            NotificationResult result = notificationPort.sendNotificationToRecipient(
                    recipientId, subject, content, severity, metadata);
            results.put(recipientId, result);
        }
        
        return results;
    }
    
    /**
     * Sends an SMS notification to a recipient.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message content
     * @param smsType The SMS message type
     * @return The notification result
     */
    public NotificationResult sendSms(String phoneNumber, String message, NotificationPort.SmsType smsType) {
        logger.debug("Sending SMS to {}", phoneNumber);
        
        // Add country code if not present
        String formattedPhoneNumber = phoneNumber;
        if (!phoneNumber.startsWith("+")) {
            formattedPhoneNumber = defaultCountryCode + phoneNumber;
        }
        
        // Prepare metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("smsType", smsType.toString());
        metadata.put("senderTimestamp", String.valueOf(System.currentTimeMillis()));
        
        // Apply rate limiting if needed
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSmsSentTime < smsRateLimitMs) {
            try {
                Thread.sleep(smsRateLimitMs - (currentTime - lastSmsSentTime));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("SMS rate limiting was interrupted");
            }
        }
        lastSmsSentTime = System.currentTimeMillis();
        
        // Send the SMS notification
        NotificationResult result = notificationPort.sendSmsNotification(
                formattedPhoneNumber, message, smsType, metadata);
        
        if (result.isSent()) {
            logger.info("SMS sent successfully to {}", phoneNumber);
        } else {
            logger.warn("Failed to send SMS to {}: {}", phoneNumber, result.getMessage());
        }
        
        return result;
    }
    
    /**
     * Sends an SMS notification with standard type.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message content
     * @return The notification result
     */
    public NotificationResult sendSms(String phoneNumber, String message) {
        return sendSms(phoneNumber, message, NotificationPort.SmsType.STANDARD);
    }
    
    /**
     * Sends SMS notifications to multiple recipients.
     *
     * @param phoneNumbers The list of phone numbers
     * @param message The SMS message content
     * @param smsType The SMS message type
     * @return A map of phone numbers to notification results
     */
    public Map<String, NotificationResult> sendBatchSms(
            List<String> phoneNumbers, String message, NotificationPort.SmsType smsType) {
        logger.debug("Sending batch SMS to {} recipients", phoneNumbers.size());
        
        Map<String, NotificationResult> results = new HashMap<>();
        int batchCount = 0;
        
        for (String phoneNumber : phoneNumbers) {
            // Check batch size and apply rate limiting
            if (batchCount >= maxSmsBatchSize) {
                // Wait for rate limit before starting new batch
                try {
                    Thread.sleep(smsRateLimitMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("SMS batch rate limiting was interrupted");
                }
                batchCount = 0;
            }
            
            NotificationResult result = sendSms(phoneNumber, message, smsType);
            results.put(phoneNumber, result);
            batchCount++;
        }
        
        logger.info("Batch SMS completed: {} messages, {} successful", 
                phoneNumbers.size(), 
                results.values().stream().filter(NotificationResult::isSent).count());
        
        return results;
    }
    
    /**
     * Schedules an SMS notification for future delivery.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message content
     * @param scheduledTime The scheduled delivery time (ISO-8601 format)
     * @param smsType The SMS message type
     * @return The notification result with scheduling information
     */
    public NotificationResult scheduleSms(
            String phoneNumber, String message, String scheduledTime, NotificationPort.SmsType smsType) {
        logger.debug("Scheduling SMS to {} for delivery at {}", phoneNumber, scheduledTime);
        
        // Add country code if not present
        String formattedPhoneNumber = phoneNumber;
        if (!phoneNumber.startsWith("+")) {
            formattedPhoneNumber = defaultCountryCode + phoneNumber;
        }
        
        // Create a recipient ID for the phone number if not already registered
        String recipientId = "sms-" + formattedPhoneNumber.replaceAll("[^0-9]", "");
        if (!notificationPort.isRecipientRegistered(recipientId)) {
            notificationPort.registerSmsRecipient(recipientId, formattedPhoneNumber, 
                                               defaultCountryCode, true);
        }
        
        // Schedule the notification
        return notificationPort.scheduleNotification(
                recipientId, 
                "SMS Notification", 
                message, 
                NotificationSeverity.INFO, 
                scheduledTime, 
                NotificationPort.NotificationChannel.SMS);
    }
    
    /**
     * Registers a recipient for SMS notifications.
     *
     * @param phoneNumber The phone number
     * @param countryCode The country code (e.g., "+1" for US)
     * @param optIn Whether the recipient has explicitly opted in
     * @return true if registration was successful, false otherwise
     */
    public boolean registerSmsRecipient(String phoneNumber, String countryCode, boolean optIn) {
        String recipientId = "sms-" + phoneNumber.replaceAll("[^0-9]", "");
        return notificationPort.registerSmsRecipient(recipientId, phoneNumber, countryCode, optIn);
    }
    
    /**
     * Sends a formatted notification with the specified content format.
     *
     * @param recipient The recipient identifier
     * @param subject The notification subject
     * @param content The notification content
     * @param contentFormat The format of the content
     * @param severity The notification severity
     * @param channel The notification channel to use
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    public NotificationResult sendFormattedNotification(
            String recipient,
            String subject,
            String content,
            NotificationPort.ContentFormat contentFormat,
            NotificationPort.NotificationSeverity severity,
            NotificationPort.NotificationChannel channel,
            Map<String, String> metadata) {
        
        logger.debug("Sending formatted {} notification to {} via {}", 
                contentFormat, recipient, channel);
        
        // Add content format to metadata
        Map<String, String> enhancedMetadata = new HashMap<>(metadata);
        enhancedMetadata.put("contentFormat", contentFormat.toString());
        
        return notificationPort.sendFormattedNotification(
                recipient, subject, content, contentFormat, severity, channel, enhancedMetadata);
    }
    
    /**
     * Sends a Slack notification to a channel.
     *
     * @param workspace The Slack workspace
     * @param channel The Slack channel
     * @param message The message content
     * @param contentFormat The content format (PLAIN_TEXT or MARKDOWN)
     * @return The notification result
     */
    public NotificationResult sendSlackNotification(
            String workspace,
            String channel,
            String message,
            NotificationPort.ContentFormat contentFormat) {
        
        logger.debug("Sending Slack notification to {}/{}", workspace, channel);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("workspace", workspace);
        metadata.put("channel", channel);
        
        return notificationPort.sendSlackNotification(workspace, channel, message, contentFormat, metadata);
    }
    
    /**
     * Sends a Slack notification with plain text content.
     *
     * @param workspace The Slack workspace
     * @param channel The Slack channel
     * @param message The message content
     * @return The notification result
     */
    public NotificationResult sendSlackNotification(String workspace, String channel, String message) {
        return sendSlackNotification(workspace, channel, message, NotificationPort.ContentFormat.PLAIN_TEXT);
    }
    
    /**
     * Sends a Microsoft Teams notification to a channel.
     *
     * @param teamId The Teams team identifier
     * @param channel The Teams channel
     * @param message The message content
     * @param contentFormat The content format (PLAIN_TEXT, HTML, or MARKDOWN)
     * @return The notification result
     */
    public NotificationResult sendTeamsNotification(
            String teamId,
            String channel,
            String message,
            NotificationPort.ContentFormat contentFormat) {
        
        logger.debug("Sending Teams notification to {}/{}", teamId, channel);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("teamId", teamId);
        metadata.put("channel", channel);
        
        return notificationPort.sendTeamsNotification(teamId, channel, message, contentFormat, metadata);
    }
    
    /**
     * Sends a Microsoft Teams notification with plain text content.
     *
     * @param teamId The Teams team identifier
     * @param channel The Teams channel
     * @param message The message content
     * @return The notification result
     */
    public NotificationResult sendTeamsNotification(String teamId, String channel, String message) {
        return sendTeamsNotification(teamId, channel, message, NotificationPort.ContentFormat.PLAIN_TEXT);
    }
    
    /**
     * Sends a Discord notification to a channel.
     *
     * @param serverId The Discord server identifier
     * @param channel The Discord channel
     * @param message The message content
     * @param contentFormat The content format (PLAIN_TEXT or MARKDOWN)
     * @return The notification result
     */
    public NotificationResult sendDiscordNotification(
            String serverId,
            String channel,
            String message,
            NotificationPort.ContentFormat contentFormat) {
        
        logger.debug("Sending Discord notification to {}/{}", serverId, channel);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("serverId", serverId);
        metadata.put("channel", channel);
        
        return notificationPort.sendDiscordNotification(serverId, channel, message, contentFormat, metadata);
    }
    
    /**
     * Sends a Discord notification with plain text content.
     *
     * @param serverId The Discord server identifier
     * @param channel The Discord channel
     * @param message The message content
     * @return The notification result
     */
    public NotificationResult sendDiscordNotification(String serverId, String channel, String message) {
        return sendDiscordNotification(serverId, channel, message, NotificationPort.ContentFormat.PLAIN_TEXT);
    }
    
    /**
     * Sends a templated notification with variable substitution.
     *
     * @param recipient The recipient identifier
     * @param templateName The name of the template to use
     * @param variables Variables to substitute in the template
     * @param severity The notification severity
     * @param channel The notification channel to use
     * @return The notification result
     */
    public NotificationResult sendTemplatedNotification(
            String recipient,
            String templateName,
            Map<String, String> variables,
            NotificationPort.NotificationSeverity severity,
            NotificationPort.NotificationChannel channel) {
        
        logger.debug("Sending templated notification '{}' to {} via {}", 
                templateName, recipient, channel);
        
        return notificationPort.sendTemplatedNotification(
                recipient, templateName, variables, severity, channel);
    }
    
    /**
     * Registers a recipient for a specific notification channel.
     *
     * @param recipient The recipient identifier
     * @param channel The notification channel
     * @param channelSpecificInfo Channel-specific information
     * @return true if registration was successful, false otherwise
     */
    public boolean registerChannelRecipient(
            String recipient,
            NotificationPort.NotificationChannel channel,
            Map<String, String> channelSpecificInfo) {
        
        logger.debug("Registering recipient {} for {} channel", recipient, channel);
        
        return notificationPort.registerChannelRecipient(recipient, channel, channelSpecificInfo);
    }
    
    /**
     * Registers a recipient for Slack notifications.
     *
     * @param recipient The recipient identifier
     * @param workspace The Slack workspace
     * @param channel The Slack channel
     * @param webhookUrl The Slack webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    public boolean registerSlackRecipient(
            String recipient, String workspace, String channel, String webhookUrl) {
        
        logger.debug("Registering recipient {} for Slack notifications in {}/{}", 
                recipient, workspace, channel);
        
        return notificationPort.registerSlackRecipient(recipient, workspace, channel, webhookUrl);
    }
    
    /**
     * Registers a recipient for Microsoft Teams notifications.
     *
     * @param recipient The recipient identifier
     * @param teamId The Teams team ID
     * @param channel The Teams channel
     * @param webhookUrl The Teams webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    public boolean registerTeamsRecipient(
            String recipient, String teamId, String channel, String webhookUrl) {
        
        logger.debug("Registering recipient {} for Teams notifications in {}/{}", 
                recipient, teamId, channel);
        
        return notificationPort.registerTeamsRecipient(recipient, teamId, channel, webhookUrl);
    }
    
    /**
     * Registers a recipient for Discord notifications.
     *
     * @param recipient The recipient identifier
     * @param serverId The Discord server ID
     * @param channel The Discord channel
     * @param webhookUrl The Discord webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    public boolean registerDiscordRecipient(
            String recipient, String serverId, String channel, String webhookUrl) {
        
        logger.debug("Registering recipient {} for Discord notifications in {}/{}", 
                recipient, serverId, channel);
        
        return notificationPort.registerDiscordRecipient(recipient, serverId, channel, webhookUrl);
    }
}