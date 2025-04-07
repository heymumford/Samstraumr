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

package org.s8r.application.port;

import java.util.HashMap;
import java.util.Map;

/**
 * Port interface for notification operations.
 * 
 * <p>This interface defines standard operations for sending notifications from the application.
 * Following Clean Architecture principles, it allows the application core to remain independent
 * of specific notification implementation details (email, push notifications, etc.).
 */
public interface NotificationPort {

    /**
     * Severity levels for notifications.
     */
    enum NotificationSeverity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
    
    /**
     * Delivery status for notifications.
     */
    enum DeliveryStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        SCHEDULED
    }
    
    /**
     * Notification channel types.
     */
    enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        SYSTEM,
        WEBHOOK,
        SLACK,
        TEAMS,
        DISCORD
    }
    
    /**
     * SMS message type.
     */
    enum SmsType {
        /**
         * Standard SMS message (160 characters).
         */
        STANDARD,
        
        /**
         * Extended SMS message (multiple SMS messages).
         */
        EXTENDED,
        
        /**
         * SMS with binary payload.
         */
        BINARY,
        
        /**
         * Flash SMS (displayed immediately on the device).
         */
        FLASH
    }
    
    /**
     * Content formats for notifications.
     */
    enum ContentFormat {
        /**
         * Plain text content.
         */
        PLAIN_TEXT,
        
        /**
         * HTML formatted content.
         */
        HTML,
        
        /**
         * Markdown formatted content.
         */
        MARKDOWN,
        
        /**
         * Rich text content.
         */
        RICH_TEXT,
        
        /**
         * JSON formatted content.
         */
        JSON
    }
    
    /**
     * Notification delivery result.
     */
    class NotificationResult {
        private final String notificationId;
        private final DeliveryStatus status;
        private final String message;
        private final NotificationChannel channel;
        private final Map<String, String> metadata;
        
        /**
         * Constructs a new NotificationResult.
         *
         * @param notificationId The notification ID
         * @param status The delivery status
         * @param message Additional details about the delivery
         * @param channel The notification channel used
         * @param metadata Additional metadata about the delivery
         */
        public NotificationResult(String notificationId, DeliveryStatus status, String message, 
                                 NotificationChannel channel, Map<String, String> metadata) {
            this.notificationId = notificationId;
            this.status = status;
            this.message = message;
            this.channel = channel;
            this.metadata = metadata != null ? metadata : Map.of();
        }
        
        /**
         * Backward compatibility constructor.
         *
         * @param notificationId The notification ID
         * @param status The delivery status
         * @param message Additional details about the delivery
         */
        public NotificationResult(String notificationId, DeliveryStatus status, String message) {
            this(notificationId, status, message, NotificationChannel.EMAIL, Map.of());
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
        
        /**
         * Gets the message.
         *
         * @return The message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the notification channel.
         *
         * @return The notification channel
         */
        public NotificationChannel getChannel() {
            return channel;
        }
        
        /**
         * Gets the metadata.
         *
         * @return The metadata
         */
        public Map<String, String> getMetadata() {
            return metadata;
        }
        
        /**
         * Checks if the notification was sent successfully.
         *
         * @return true if sent, false otherwise
         */
        public boolean isSent() {
            return status == DeliveryStatus.SENT || status == DeliveryStatus.DELIVERED;
        }
        
        /**
         * Creates a success result.
         *
         * @param notificationId The notification ID
         * @return A success result
         */
        public static NotificationResult success(String notificationId) {
            return new NotificationResult(notificationId, DeliveryStatus.SENT, "Notification sent successfully");
        }
        
        /**
         * Creates a success result with channel information.
         *
         * @param notificationId The notification ID
         * @param channel The notification channel used
         * @param metadata Additional metadata
         * @return A success result
         */
        public static NotificationResult success(String notificationId, NotificationChannel channel, Map<String, String> metadata) {
            return new NotificationResult(notificationId, DeliveryStatus.SENT, 
                                        "Notification sent successfully via " + channel, channel, metadata);
        }
        
        /**
         * Creates a failure result.
         *
         * @param notificationId The notification ID
         * @param errorMessage The error message
         * @return A failure result
         */
        public static NotificationResult failure(String notificationId, String errorMessage) {
            return new NotificationResult(notificationId, DeliveryStatus.FAILED, errorMessage);
        }
        
        /**
         * Creates a failure result with channel information.
         *
         * @param notificationId The notification ID
         * @param errorMessage The error message
         * @param channel The notification channel that failed
         * @return A failure result
         */
        public static NotificationResult failure(String notificationId, String errorMessage, NotificationChannel channel) {
            return new NotificationResult(notificationId, DeliveryStatus.FAILED, errorMessage, channel, Map.of());
        }
        
        /**
         * Creates a scheduled result for delayed notifications.
         *
         * @param notificationId The notification ID
         * @param scheduledTime The scheduled delivery time
         * @return A scheduled result
         */
        public static NotificationResult scheduled(String notificationId, String scheduledTime) {
            return new NotificationResult(notificationId, DeliveryStatus.SCHEDULED, 
                                        "Notification scheduled for delivery at " + scheduledTime,
                                        NotificationChannel.EMAIL, Map.of("scheduledTime", scheduledTime));
        }
    }
    
    /**
     * Sends a notification.
     *
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendNotification(
            String subject, 
            String content, 
            NotificationSeverity severity, 
            Map<String, String> metadata);
    
    /**
     * Sends a notification to a specific recipient.
     *
     * @param recipient The recipient identifier
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendNotificationToRecipient(
            String recipient,
            String subject, 
            String content, 
            NotificationSeverity severity, 
            Map<String, String> metadata);
    
    /**
     * Sends a notification through a specific channel.
     *
     * @param recipient The recipient identifier
     * @param subject The notification subject (may be ignored by some channels)
     * @param content The notification content
     * @param severity The notification severity
     * @param channel The notification channel to use
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendNotificationViaChannel(
            String recipient,
            String subject, 
            String content, 
            NotificationSeverity severity,
            NotificationChannel channel, 
            Map<String, String> metadata);
            
    /**
     * Sends a notification with specified content format through a specific channel.
     *
     * @param recipient The recipient identifier
     * @param subject The notification subject (may be ignored by some channels)
     * @param content The notification content
     * @param contentFormat The format of the content (HTML, Markdown, etc.)
     * @param severity The notification severity
     * @param channel The notification channel to use
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendFormattedNotification(
            String recipient,
            String subject, 
            String content,
            ContentFormat contentFormat,
            NotificationSeverity severity,
            NotificationChannel channel, 
            Map<String, String> metadata);
    
    /**
     * Sends an SMS notification.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message content
     * @param smsType The SMS message type
     * @param metadata Additional SMS metadata
     * @return The notification result
     */
    NotificationResult sendSmsNotification(
            String phoneNumber,
            String message,
            SmsType smsType,
            Map<String, String> metadata);
    
    /**
     * Sends a system notification.
     *
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @return The notification result
     */
    NotificationResult sendSystemNotification(
            String subject, 
            String content, 
            NotificationSeverity severity);
    
    /**
     * Schedules a notification for future delivery.
     *
     * @param recipient The recipient identifier
     * @param subject The notification subject
     * @param content The notification content
     * @param severity The notification severity
     * @param scheduledTime The scheduled delivery time (ISO-8601 format)
     * @param channel The notification channel to use
     * @return The notification result with scheduling information
     */
    NotificationResult scheduleNotification(
            String recipient,
            String subject,
            String content,
            NotificationSeverity severity,
            String scheduledTime,
            NotificationChannel channel);
    
    /**
     * Checks if a recipient is registered to receive notifications.
     *
     * @param recipient The recipient identifier
     * @return true if the recipient is registered, false otherwise
     */
    boolean isRecipientRegistered(String recipient);
    
    /**
     * Gets the delivery status of a notification.
     *
     * @param notificationId The notification ID
     * @return The delivery status
     */
    DeliveryStatus getNotificationStatus(String notificationId);
    
    /**
     * Registers a new notification recipient.
     *
     * @param recipient The recipient identifier
     * @param contactInfo The recipient contact information
     * @return true if registration was successful, false otherwise
     */
    boolean registerRecipient(String recipient, Map<String, String> contactInfo);
    
    /**
     * Registers a recipient specifically for SMS notifications.
     *
     * @param recipient The recipient identifier
     * @param phoneNumber The recipient's phone number
     * @param countryCode The country code for the phone number
     * @param optIn Whether the recipient has opted in to SMS notifications
     * @return true if registration was successful, false otherwise
     */
    default boolean registerSmsRecipient(String recipient, String phoneNumber, String countryCode, boolean optIn) {
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", "sms");
        contactInfo.put("phone", phoneNumber);
        contactInfo.put("countryCode", countryCode);
        contactInfo.put("optIn", String.valueOf(optIn));
        contactInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));
        return registerRecipient(recipient, contactInfo);
    }
    
    /**
     * Unregisters a notification recipient.
     *
     * @param recipient The recipient identifier
     * @return true if unregistration was successful, false otherwise
     */
    boolean unregisterRecipient(String recipient);
    
    /**
     * Convenience method to send a notification with INFO severity and no metadata.
     *
     * @param subject The notification subject
     * @param content The notification content
     * @return The notification result
     */
    default NotificationResult sendNotification(String subject, String content) {
        return sendNotification(subject, content, NotificationSeverity.INFO, Map.of());
    }
    
    /**
     * Convenience method to send an SMS notification with STANDARD type.
     *
     * @param phoneNumber The recipient's phone number
     * @param message The SMS message content
     * @return The notification result
     */
    default NotificationResult sendSmsNotification(String phoneNumber, String message) {
        return sendSmsNotification(phoneNumber, message, SmsType.STANDARD, Map.of());
    }
    
    /**
     * Sends a Slack notification to a channel.
     *
     * @param workspace The Slack workspace identifier
     * @param channel The Slack channel name
     * @param message The message content
     * @param contentFormat The format of the content (PLAIN_TEXT, MARKDOWN)
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendSlackNotification(
            String workspace,
            String channel, 
            String message,
            ContentFormat contentFormat,
            Map<String, String> metadata);
            
    /**
     * Convenience method to send a Slack notification with plain text content.
     *
     * @param workspace The Slack workspace identifier
     * @param channel The Slack channel name
     * @param message The message content
     * @return The notification result
     */
    default NotificationResult sendSlackNotification(String workspace, String channel, String message) {
        return sendSlackNotification(workspace, channel, message, ContentFormat.PLAIN_TEXT, Map.of());
    }
    
    /**
     * Sends a Microsoft Teams notification to a channel.
     *
     * @param teamId The Teams team identifier
     * @param channel The Teams channel name
     * @param message The message content
     * @param contentFormat The format of the content (PLAIN_TEXT, HTML, MARKDOWN)
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendTeamsNotification(
            String teamId,
            String channel, 
            String message,
            ContentFormat contentFormat,
            Map<String, String> metadata);
            
    /**
     * Convenience method to send a Teams notification with plain text content.
     *
     * @param teamId The Teams team identifier
     * @param channel The Teams channel name
     * @param message The message content
     * @return The notification result
     */
    default NotificationResult sendTeamsNotification(String teamId, String channel, String message) {
        return sendTeamsNotification(teamId, channel, message, ContentFormat.PLAIN_TEXT, Map.of());
    }
    
    /**
     * Sends a Discord notification to a channel.
     *
     * @param serverId The Discord server identifier
     * @param channel The Discord channel name
     * @param message The message content
     * @param contentFormat The format of the content (PLAIN_TEXT, MARKDOWN)
     * @param metadata Additional notification metadata
     * @return The notification result
     */
    NotificationResult sendDiscordNotification(
            String serverId,
            String channel, 
            String message,
            ContentFormat contentFormat,
            Map<String, String> metadata);
            
    /**
     * Convenience method to send a Discord notification with plain text content.
     *
     * @param serverId The Discord server identifier
     * @param channel The Discord channel name
     * @param message The message content
     * @return The notification result
     */
    default NotificationResult sendDiscordNotification(String serverId, String channel, String message) {
        return sendDiscordNotification(serverId, channel, message, ContentFormat.PLAIN_TEXT, Map.of());
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
    NotificationResult sendTemplatedNotification(
            String recipient,
            String templateName,
            Map<String, String> variables,
            NotificationSeverity severity,
            NotificationChannel channel);
            
    /**
     * Registers a recipient for a specific notification channel.
     *
     * @param recipient The recipient identifier
     * @param channel The notification channel
     * @param channelSpecificInfo Channel-specific information (e.g., webhook URL, chat ID)
     * @return true if registration was successful, false otherwise
     */
    boolean registerChannelRecipient(String recipient, NotificationChannel channel, Map<String, String> channelSpecificInfo);
            
    /**
     * Convenience method to register a recipient for Slack notifications.
     *
     * @param recipient The recipient identifier
     * @param workspace The Slack workspace
     * @param channel The Slack channel
     * @param webhookUrl The Slack webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    default boolean registerSlackRecipient(String recipient, String workspace, String channel, String webhookUrl) {
        Map<String, String> channelInfo = new HashMap<>();
        channelInfo.put("type", "slack");
        channelInfo.put("workspace", workspace);
        channelInfo.put("channel", channel);
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            channelInfo.put("webhookUrl", webhookUrl);
        }
        channelInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));
        return registerChannelRecipient(recipient, NotificationChannel.SLACK, channelInfo);
    }
    
    /**
     * Convenience method to register a recipient for Microsoft Teams notifications.
     *
     * @param recipient The recipient identifier
     * @param teamId The Teams team ID
     * @param channel The Teams channel
     * @param webhookUrl The Teams webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    default boolean registerTeamsRecipient(String recipient, String teamId, String channel, String webhookUrl) {
        Map<String, String> channelInfo = new HashMap<>();
        channelInfo.put("type", "teams");
        channelInfo.put("teamId", teamId);
        channelInfo.put("channel", channel);
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            channelInfo.put("webhookUrl", webhookUrl);
        }
        channelInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));
        return registerChannelRecipient(recipient, NotificationChannel.TEAMS, channelInfo);
    }
    
    /**
     * Convenience method to register a recipient for Discord notifications.
     *
     * @param recipient The recipient identifier
     * @param serverId The Discord server ID
     * @param channel The Discord channel
     * @param webhookUrl The Discord webhook URL (optional)
     * @return true if registration was successful, false otherwise
     */
    default boolean registerDiscordRecipient(String recipient, String serverId, String channel, String webhookUrl) {
        Map<String, String> channelInfo = new HashMap<>();
        channelInfo.put("type", "discord");
        channelInfo.put("serverId", serverId);
        channelInfo.put("channel", channel);
        if (webhookUrl != null && !webhookUrl.isEmpty()) {
            channelInfo.put("webhookUrl", webhookUrl);
        }
        channelInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));
        return registerChannelRecipient(recipient, NotificationChannel.DISCORD, channelInfo);
    }
}