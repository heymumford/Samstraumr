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

package org.s8r.infrastructure.notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;

/**
 * Adapter implementation of the NotificationPort interface.
 * 
 * <p>This adapter provides notification operations for the application using a simple
 * in-memory notification system. In a production environment, this would be connected
 * to external notification systems (email, SMS, etc.).
 */
public class NotificationAdapter implements NotificationPort {
    
    private final LoggerPort logger;
    private final ConfigurationPort configurationPort;
    private final Map<String, Map<String, String>> recipients;
    private final Map<String, NotificationRecord> notifications;
    private final String defaultRecipient;
    
    /**
     * Record class to store notification information.
     */
    private static class NotificationRecord {
        private final String id;
        private final String subject;
        private final String content;
        private final String recipient;
        private final NotificationSeverity severity;
        private final Map<String, String> metadata;
        private final LocalDateTime timestamp;
        private DeliveryStatus status;
        private String statusMessage;
        
        /**
         * Constructor for the notification record.
         */
        public NotificationRecord(
                String id, 
                String subject, 
                String content, 
                String recipient, 
                NotificationSeverity severity, 
                Map<String, String> metadata) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.recipient = recipient;
            this.severity = severity;
            this.metadata = new HashMap<>(metadata);
            this.timestamp = LocalDateTime.now();
            this.status = DeliveryStatus.PENDING;
            this.statusMessage = "Notification pending delivery";
        }
        
        /**
         * Sets the status of the notification.
         *
         * @param status The new status
         * @param message The status message
         */
        public void setStatus(DeliveryStatus status, String message) {
            this.status = status;
            this.statusMessage = message;
        }
    }
    
    /**
     * Constructs a new NotificationAdapter.
     *
     * @param logger The logger to use
     * @param configurationPort The configuration port to use
     */
    public NotificationAdapter(LoggerPort logger, ConfigurationPort configurationPort) {
        this.logger = logger;
        this.configurationPort = configurationPort;
        this.recipients = new ConcurrentHashMap<>();
        this.notifications = new ConcurrentHashMap<>();
        
        // Get default recipient from configuration
        this.defaultRecipient = configurationPort.getString("notification.default.recipient", "system");
        
        // Register system recipient
        Map<String, String> systemContact = new HashMap<>();
        systemContact.put("type", "system");
        systemContact.put("address", "system");
        this.recipients.put("system", systemContact);
        
        logger.debug("NotificationAdapter initialized");
    }
    
    @Override
    public NotificationResult sendNotification(
            String subject, 
            String content, 
            NotificationSeverity severity, 
            Map<String, String> metadata) {
        return sendNotificationToRecipient(defaultRecipient, subject, content, severity, metadata);
    }
    
    @Override
    public NotificationResult sendNotificationToRecipient(
            String recipient, 
            String subject, 
            String content, 
            NotificationSeverity severity, 
            Map<String, String> metadata) {
        logger.debug("Sending notification to recipient: {}", recipient);
        
        // Validate recipient
        if (!isRecipientRegistered(recipient)) {
            logger.warn("Recipient not registered: {}", recipient);
            return NotificationResult.failure("N/A", "Recipient not registered: " + recipient);
        }
        
        // Generate notification ID
        String notificationId = generateNotificationId();
        
        // Prepare metadata
        Map<String, String> notificationMetadata = metadata != null 
            ? new HashMap<>(metadata) 
            : new HashMap<>();
        
        // Add standard metadata
        notificationMetadata.put("timestamp", LocalDateTime.now().toString());
        notificationMetadata.put("severity", severity.toString());
        
        // Create and store notification record
        NotificationRecord record = new NotificationRecord(
                notificationId, subject, content, recipient, severity, notificationMetadata);
        notifications.put(notificationId, record);
        
        try {
            // In a real implementation, this would send the notification through an external system
            // For demo purposes, we'll just log it and consider it sent
            deliverNotification(record);
            
            logger.info("Notification sent successfully: {}", notificationId);
            record.setStatus(DeliveryStatus.SENT, "Notification sent successfully");
            return NotificationResult.success(notificationId);
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage(), e);
            record.setStatus(DeliveryStatus.FAILED, "Failed to send notification: " + e.getMessage());
            return NotificationResult.failure(notificationId, "Failed to send notification: " + e.getMessage());
        }
    }
    
    @Override
    public NotificationResult sendSystemNotification(
            String subject, 
            String content, 
            NotificationSeverity severity) {
        logger.debug("Sending system notification");
        
        // Use empty metadata for system notifications
        return sendNotificationToRecipient("system", subject, content, severity, new HashMap<>());
    }
    
    @Override
    public boolean isRecipientRegistered(String recipient) {
        return recipients.containsKey(recipient);
    }
    
    @Override
    public DeliveryStatus getNotificationStatus(String notificationId) {
        logger.debug("Getting notification status: {}", notificationId);
        
        if (!notifications.containsKey(notificationId)) {
            logger.warn("Notification not found: {}", notificationId);
            return DeliveryStatus.FAILED;
        }
        
        return notifications.get(notificationId).status;
    }
    
    @Override
    public boolean registerRecipient(String recipient, Map<String, String> contactInfo) {
        logger.debug("Registering recipient: {}", recipient);
        
        if (recipient == null || recipient.trim().isEmpty()) {
            logger.warn("Invalid recipient ID");
            return false;
        }
        
        if (contactInfo == null || contactInfo.isEmpty()) {
            logger.warn("Invalid contact information for recipient: {}", recipient);
            return false;
        }
        
        // Store recipient information
        recipients.put(recipient, new HashMap<>(contactInfo));
        logger.info("Recipient registered: {}", recipient);
        return true;
    }
    
    @Override
    public boolean unregisterRecipient(String recipient) {
        logger.debug("Unregistering recipient: {}", recipient);
        
        if (recipient == null || recipient.trim().isEmpty()) {
            logger.warn("Invalid recipient ID");
            return false;
        }
        
        // Don't allow unregistering the system recipient
        if ("system".equals(recipient)) {
            logger.warn("Cannot unregister system recipient");
            return false;
        }
        
        if (!recipients.containsKey(recipient)) {
            logger.warn("Recipient not found: {}", recipient);
            return false;
        }
        
        recipients.remove(recipient);
        logger.info("Recipient unregistered: {}", recipient);
        return true;
    }
    
    /**
     * Generates a unique notification ID.
     *
     * @return A unique notification ID
     */
    private String generateNotificationId() {
        return "NOTIF-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Delivers a notification to the recipient.
     * 
     * <p>In a real implementation, this would connect to external notification systems
     * based on the recipient's contact information.
     *
     * @param record The notification record
     */
    private void deliverNotification(NotificationRecord record) {
        Map<String, String> recipientInfo = recipients.get(record.recipient);
        String recipientType = recipientInfo.getOrDefault("type", "unknown");
        
        // Log the notification details
        logger.info("Delivering notification: {} to {} ({})", 
                record.id, record.recipient, recipientType);
        logger.info("Subject: {}", record.subject);
        logger.info("Content: {}", record.content);
        logger.info("Severity: {}", record.severity);
        
        // In a real implementation, we would use different delivery methods
        // based on the recipient type and preferences
        switch (recipientType) {
            case "email":
                // Send email notification
                simulateEmailDelivery(record, recipientInfo);
                break;
            case "sms":
                // Send SMS notification
                simulateSmsDelivery(record, recipientInfo);
                break;
            case "system":
                // Log system notification
                logger.info("SYSTEM NOTIFICATION: {} - {}", record.subject, record.content);
                break;
            default:
                // Default to logging
                logger.info("NOTIFICATION [{}]: {} - {}", 
                        recipientType, record.subject, record.content);
                break;
        }
    }
    
    /**
     * Simulates email delivery (for demo purposes).
     *
     * @param record The notification record
     * @param recipientInfo The recipient information
     */
    private void simulateEmailDelivery(NotificationRecord record, Map<String, String> recipientInfo) {
        String emailAddress = recipientInfo.getOrDefault("address", "unknown");
        logger.info("EMAIL to {}: {} - {}", emailAddress, record.subject, record.content);
        
        // Simulate delivery delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Simulates SMS delivery (for demo purposes).
     *
     * @param record The notification record
     * @param recipientInfo The recipient information
     */
    private void simulateSmsDelivery(NotificationRecord record, Map<String, String> recipientInfo) {
        String phoneNumber = recipientInfo.getOrDefault("phone", "unknown");
        logger.info("SMS to {}: {}", phoneNumber, record.content);
        
        // Simulate delivery delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gets all notifications (for testing and monitoring).
     *
     * @return A map of notification records
     */
    public Map<String, NotificationRecord> getAllNotifications() {
        return new HashMap<>(notifications);
    }
    
    /**
     * Gets all registered recipients (for testing and monitoring).
     *
     * @return A map of recipient information
     */
    public Map<String, Map<String, String>> getAllRecipients() {
        return new HashMap<>(recipients);
    }
}