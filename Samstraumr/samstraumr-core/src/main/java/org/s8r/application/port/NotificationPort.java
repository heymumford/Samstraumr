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
        FAILED
    }
    
    /**
     * Notification delivery result.
     */
    class NotificationResult {
        private final String notificationId;
        private final DeliveryStatus status;
        private final String message;
        
        /**
         * Constructs a new NotificationResult.
         *
         * @param notificationId The notification ID
         * @param status The delivery status
         * @param message Additional details about the delivery
         */
        public NotificationResult(String notificationId, DeliveryStatus status, String message) {
            this.notificationId = notificationId;
            this.status = status;
            this.message = message;
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
         * Creates a failure result.
         *
         * @param notificationId The notification ID
         * @param errorMessage The error message
         * @return A failure result
         */
        public static NotificationResult failure(String notificationId, String errorMessage) {
            return new NotificationResult(notificationId, DeliveryStatus.FAILED, errorMessage);
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
     * Unregisters a notification recipient.
     *
     * @param recipient The recipient identifier
     * @return true if unregistration was successful, false otherwise
     */
    boolean unregisterRecipient(String recipient);
}