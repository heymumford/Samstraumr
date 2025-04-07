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
 * to external notification systems (email, SMS, push notifications, etc.).
 * 
 * <p>Features:
 * <ul>
 *   <li>Supports multiple notification channels (email, SMS, push)</li>
 *   <li>Tracks notification delivery status</li>
 *   <li>Supports recipient registration and management</li>
 *   <li>Provides system-wide notifications</li>
 *   <li>Configurable default recipient</li>
 * </ul>
 */
public class NotificationAdapter implements NotificationPort {
    
    private final LoggerPort logger;
    private final ConfigurationPort configurationPort;
    private final Map<String, Map<String, String>> recipients;
    private final Map<String, NotificationRecord> notifications;
    private final String defaultRecipient;
    
    // Channel types supported by this adapter
    private static final String CHANNEL_EMAIL = "email";
    private static final String CHANNEL_SMS = "sms";
    private static final String CHANNEL_PUSH = "push";
    private static final String CHANNEL_SYSTEM = "system";
    
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
        private LocalDateTime deliveryTimestamp;
        private String channel;
        private int retryCount;
        private int maxRetries;
        
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
            this.retryCount = 0;
            this.maxRetries = 3; // Default max retries
            this.channel = determineChannel(recipient, metadata);
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
            
            if (status == DeliveryStatus.DELIVERED || status == DeliveryStatus.SENT) {
                this.deliveryTimestamp = LocalDateTime.now();
            }
        }
        
        /**
         * Increments the retry count.
         * 
         * @return true if retry limit not reached, false otherwise
         */
        public boolean incrementRetry() {
            this.retryCount++;
            return this.retryCount <= this.maxRetries;
        }
        
        /**
         * Gets the time elapsed since the notification was created.
         * 
         * @return The elapsed time in seconds
         */
        public long getElapsedTimeSeconds() {
            return java.time.Duration.between(timestamp, LocalDateTime.now()).getSeconds();
        }
        
        /**
         * Determines the delivery channel for this notification.
         * 
         * @param recipient The recipient
         * @param metadata The notification metadata
         * @return The determined channel
         */
        private String determineChannel(String recipient, Map<String, String> metadata) {
            // Use channel from metadata if specified
            if (metadata.containsKey("channel")) {
                return metadata.get("channel");
            }
            
            // For system recipient, use system channel
            if ("system".equals(recipient)) {
                return CHANNEL_SYSTEM;
            }
            
            // Default to email
            return CHANNEL_EMAIL;
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
        systemContact.put("type", CHANNEL_SYSTEM);
        systemContact.put("address", "system");
        this.recipients.put("system", systemContact);
        
        // Add test recipients if enabled
        boolean addTestRecipients = configurationPort.getBoolean("notification.test.recipients.enabled", false);
        if (addTestRecipients) {
            initializeTestRecipients();
        }
        
        // Start notification cleaner if enabled
        boolean cleanupEnabled = configurationPort.getBoolean("notification.cleanup.enabled", true);
        if (cleanupEnabled) {
            scheduleNotificationCleanup();
        }
        
        logger.info("NotificationAdapter initialized with default recipient: {}", defaultRecipient);
    }
    
    /**
     * Initializes test recipients for development and testing purposes.
     */
    private void initializeTestRecipients() {
        // Add email recipient
        Map<String, String> emailContact = new HashMap<>();
        emailContact.put("type", CHANNEL_EMAIL);
        emailContact.put("address", "test@example.com");
        this.recipients.put("email-user", emailContact);
        
        // Add SMS recipient
        Map<String, String> smsContact = new HashMap<>();
        smsContact.put("type", CHANNEL_SMS);
        smsContact.put("phone", "555-123-4567");
        this.recipients.put("sms-user", smsContact);
        
        // Add push notification recipient
        Map<String, String> pushContact = new HashMap<>();
        pushContact.put("type", CHANNEL_PUSH);
        pushContact.put("device", "device-token-123");
        pushContact.put("platform", "android");
        this.recipients.put("push-user", pushContact);
        
        logger.debug("Test recipients initialized");
    }
    
    /**
     * Schedules periodic cleanup of old notifications.
     */
    private void scheduleNotificationCleanup() {
        // Simple cleanup thread that runs every minute
        Thread cleanupThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Sleep for a minute
                    Thread.sleep(60000);
                    
                    // Perform cleanup
                    cleanupOldNotifications();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Notification cleanup thread interrupted");
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("notification-cleanup");
        cleanupThread.start();
        
        logger.debug("Notification cleanup scheduled");
    }
    
    /**
     * Cleans up old notifications to prevent memory leaks.
     */
    private void cleanupOldNotifications() {
        int retentionDays = configurationPort.getInt("notification.retention.days", 30);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
        int cleanupCount = 0;
        
        for (Map.Entry<String, NotificationRecord> entry : new HashMap<>(notifications).entrySet()) {
            NotificationRecord record = entry.getValue();
            if (record.timestamp.isBefore(cutoffTime)) {
                notifications.remove(entry.getKey());
                cleanupCount++;
            }
        }
        
        if (cleanupCount > 0) {
            logger.debug("Cleaned up {} old notifications", cleanupCount);
        }
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
        logger.debug("Subject: {}", record.subject);
        logger.debug("Content: {}", record.content);
        logger.debug("Severity: {}", record.severity);
        logger.debug("Channel: {}", record.channel);
        
        // In a real implementation, we would use different delivery methods
        // based on the recipient type and preferences
        boolean deliverySuccess = false;
        
        try {
            switch (recipientType) {
                case CHANNEL_EMAIL:
                    // Send email notification
                    deliverySuccess = simulateEmailDelivery(record, recipientInfo);
                    break;
                case CHANNEL_SMS:
                    // Send SMS notification
                    deliverySuccess = simulateSmsDelivery(record, recipientInfo);
                    break;
                case CHANNEL_PUSH:
                    // Send push notification
                    deliverySuccess = simulatePushDelivery(record, recipientInfo);
                    break;
                case CHANNEL_SYSTEM:
                    // Log system notification
                    logger.info("SYSTEM NOTIFICATION: {} - {}", record.subject, record.content);
                    deliverySuccess = true;
                    break;
                default:
                    // Default to logging
                    logger.info("NOTIFICATION [{}]: {} - {}", 
                            recipientType, record.subject, record.content);
                    deliverySuccess = true;
                    break;
            }
            
            // Update status based on delivery result
            if (deliverySuccess) {
                record.setStatus(DeliveryStatus.DELIVERED, "Notification delivered successfully");
            } else if (record.incrementRetry()) {
                record.setStatus(DeliveryStatus.PENDING, 
                        "Delivery failed, will retry (attempt " + record.retryCount + ")");
                // In a real implementation, we would schedule a retry
                logger.warn("Notification delivery failed, will retry: {}", record.id);
            } else {
                record.setStatus(DeliveryStatus.FAILED, 
                        "Delivery failed after " + record.retryCount + " attempts");
                logger.error("Notification delivery failed permanently: {}", record.id);
            }
            
        } catch (Exception e) {
            logger.error("Error delivering notification: {}", e.getMessage(), e);
            record.setStatus(DeliveryStatus.FAILED, "Delivery error: " + e.getMessage());
        }
    }
    
    /**
     * Simulates email delivery (for demo purposes).
     *
     * @param record The notification record
     * @param recipientInfo The recipient information
     * @return true if delivery was successful, false otherwise
     */
    private boolean simulateEmailDelivery(NotificationRecord record, Map<String, String> recipientInfo) {
        String emailAddress = recipientInfo.getOrDefault("address", "unknown");
        logger.info("EMAIL to {}: {} - {}", emailAddress, record.subject, record.content);
        
        // Simulate delivery delay
        try {
            Thread.sleep(100);
            
            // Simulate occasional delivery failures (10% chance)
            if (Math.random() < 0.1) {
                logger.warn("Simulated email delivery failure to: {}", emailAddress);
                return false;
            }
            
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Simulates SMS delivery (for demo purposes).
     *
     * @param record The notification record
     * @param recipientInfo The recipient information
     * @return true if delivery was successful, false otherwise
     */
    private boolean simulateSmsDelivery(NotificationRecord record, Map<String, String> recipientInfo) {
        String phoneNumber = recipientInfo.getOrDefault("phone", "unknown");
        logger.info("SMS to {}: {}", phoneNumber, record.content);
        
        // SMS messages are typically shorter, so truncate if needed
        String smsContent = record.content;
        if (smsContent.length() > 160) {
            smsContent = smsContent.substring(0, 157) + "...";
        }
        
        // Simulate delivery delay
        try {
            Thread.sleep(100);
            
            // Simulate occasional delivery failures (15% chance)
            if (Math.random() < 0.15) {
                logger.warn("Simulated SMS delivery failure to: {}", phoneNumber);
                return false;
            }
            
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Simulates push notification delivery (for demo purposes).
     *
     * @param record The notification record
     * @param recipientInfo The recipient information
     * @return true if delivery was successful, false otherwise
     */
    private boolean simulatePushDelivery(NotificationRecord record, Map<String, String> recipientInfo) {
        String deviceToken = recipientInfo.getOrDefault("device", "unknown");
        String platform = recipientInfo.getOrDefault("platform", "unknown");
        
        logger.info("PUSH to {} device {}: {}", 
                platform, deviceToken, record.subject);
        
        // Construct push payload
        Map<String, Object> pushPayload = new HashMap<>();
        pushPayload.put("title", record.subject);
        pushPayload.put("body", record.content);
        pushPayload.put("severity", record.severity.toString());
        
        // Add notification ID to payload for tracking
        pushPayload.put("notificationId", record.id);
        
        // Add custom data from metadata
        Map<String, String> customData = new HashMap<>();
        for (Map.Entry<String, String> entry : record.metadata.entrySet()) {
            if (!entry.getKey().startsWith("_")) { // Skip internal metadata
                customData.put(entry.getKey(), entry.getValue());
            }
        }
        pushPayload.put("data", customData);
        
        logger.debug("Push payload: {}", pushPayload);
        
        // Simulate delivery delay
        try {
            Thread.sleep(150);
            
            // Simulate occasional delivery failures (20% chance)
            if (Math.random() < 0.2) {
                logger.warn("Simulated push delivery failure to: {} ({})", 
                        deviceToken, platform);
                return false;
            }
            
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
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