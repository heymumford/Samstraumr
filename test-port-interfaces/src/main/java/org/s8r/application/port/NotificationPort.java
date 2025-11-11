/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

import java.util.Map;

/**
 * Port interface for notification operations in the application layer.
 * 
 * <p>This interface defines the operations that can be performed for sending notifications,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface NotificationPort {

    /**
     * Sends a notification with the specified subject and message.
     *
     * @param recipient The recipient of the notification
     * @param subject The subject of the notification
     * @param message The message content
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendNotification(String recipient, String subject, String message);
    
    /**
     * Sends a notification with the specified subject, message, and additional properties.
     *
     * @param recipient The recipient of the notification
     * @param subject The subject of the notification
     * @param message The message content
     * @param properties Additional properties for the notification
     * @return true if the notification was sent successfully, false otherwise
     */
    boolean sendNotification(String recipient, String subject, String message, Map<String, String> properties);
    
    /**
     * Sends a notification with advanced options.
     *
     * @param recipient The recipient of the notification
     * @param subject The subject of the notification
     * @param message The message content
     * @param severity The severity of the notification
     * @param channel The channel to use for the notification
     * @param format The format of the content
     * @param properties Additional properties for the notification
     * @return A result object containing information about the notification
     */
    NotificationResult sendAdvancedNotification(
            String recipient, String subject, String message, 
            NotificationSeverity severity, NotificationChannel channel, 
            ContentFormat format, Map<String, String> properties);
    
    /**
     * Registers a notification listener for notifications.
     *
     * @param topic The topic to listen for
     * @param listener The listener to register
     */
    void registerListener(String topic, NotificationListener listener);
    
    /**
     * Unregisters a notification listener.
     *
     * @param topic The topic the listener is registered for
     * @param listener The listener to unregister
     */
    void unregisterListener(String topic, NotificationListener listener);
    
    /**
     * Gets the status of a notification.
     *
     * @param notificationId The ID of the notification
     * @return The delivery status of the notification
     */
    DeliveryStatus getNotificationStatus(String notificationId);
    
    /**
     * Interface for notification listeners.
     */
    interface NotificationListener {
        /**
         * Called when a notification is received.
         *
         * @param source The source of the notification
         * @param topic The topic of the notification
         * @param message The notification message
         * @param properties Additional properties of the notification
         */
        void onNotification(String source, String topic, String message, Map<String, String> properties);
    }
}