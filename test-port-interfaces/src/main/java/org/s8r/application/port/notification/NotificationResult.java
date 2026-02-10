/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port.notification;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Result of a notification operation.
 */
public class NotificationResult {
    private final boolean success;
    private final String notificationId;
    private final DeliveryStatus status;
    private final String message;
    private final String errorDetails;
    private final Instant timestamp;
    private final Map<String, String> metadata;
    
    /**
     * Creates a new NotificationResult with the specified parameters.
     *
     * @param success Whether the notification was successful
     * @param notificationId The ID of the notification
     * @param status The delivery status
     * @param message A message describing the result
     * @param errorDetails Details of any error that occurred
     * @param metadata Additional metadata about the notification
     */
    private NotificationResult(
            boolean success, 
            String notificationId, 
            DeliveryStatus status, 
            String message, 
            String errorDetails,
            Map<String, String> metadata) {
        this.success = success;
        this.notificationId = notificationId;
        this.status = status;
        this.message = message;
        this.errorDetails = errorDetails;
        this.timestamp = Instant.now();
        this.metadata = metadata != null ? Collections.unmodifiableMap(metadata) : Collections.emptyMap();
    }
    
    /**
     * Indicates whether the notification was successful.
     *
     * @return true if the notification was successful, false otherwise
     */
    public boolean isSuccessful() {
        return success;
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
     * Gets the message describing the result.
     *
     * @return The message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets details of any error that occurred.
     *
     * @return An Optional containing the error details, or empty if no error occurred
     */
    public Optional<String> getErrorDetails() {
        return Optional.ofNullable(errorDetails);
    }
    
    /**
     * Gets the reason for failure, if any.
     *
     * @return An Optional containing the error reason, or empty if no error occurred
     */
    public Optional<String> getReason() {
        return Optional.ofNullable(errorDetails);
    }
    
    /**
     * Gets the timestamp of the notification.
     *
     * @return The timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the metadata about the notification.
     *
     * @return A map of metadata
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    /**
     * Creates a successful result.
     *
     * @param message The success message
     * @return A new NotificationResult indicating success
     */
    public static NotificationResult success(String message) {
        return new NotificationResult(true, generateId(), DeliveryStatus.DELIVERED, message, null, null);
    }
    
    /**
     * Creates a successful result with the given status and metadata.
     *
     * @param message The success message
     * @param status The delivery status
     * @param metadata Additional metadata
     * @return A new NotificationResult indicating success
     */
    public static NotificationResult success(String message, DeliveryStatus status, Map<String, String> metadata) {
        return new NotificationResult(true, generateId(), status, message, null, metadata);
    }
    
    /**
     * Creates a failed result.
     *
     * @param message The failure message
     * @param errorDetails Details of the error
     * @return A new NotificationResult indicating failure
     */
    public static NotificationResult failure(String message, String errorDetails) {
        return new NotificationResult(false, generateId(), DeliveryStatus.FAILED, message, errorDetails, null);
    }
    
    /**
     * Creates a failed result with the given status and metadata.
     *
     * @param message The failure message
     * @param errorDetails Details of the error
     * @param status The delivery status
     * @param metadata Additional metadata
     * @return A new NotificationResult indicating failure
     */
    public static NotificationResult failure(
            String message, String errorDetails, DeliveryStatus status, Map<String, String> metadata) {
        return new NotificationResult(false, generateId(), status, message, errorDetails, metadata);
    }
    
    /**
     * Generates a notification ID.
     *
     * @return A new notification ID
     */
    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}