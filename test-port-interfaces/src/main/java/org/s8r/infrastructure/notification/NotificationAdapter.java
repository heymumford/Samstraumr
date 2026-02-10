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
package org.s8r.infrastructure.notification;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * In-memory implementation of the NotificationPort interface.
 * 
 * <p>This adapter provides a simple in-memory notification system.
 * It is primarily intended for testing and development environments.
 */
public class NotificationAdapter implements NotificationPort {
    
    private final Map<String, List<NotificationListener>> listeners = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> notifications = new HashMap<>();
    private final Map<String, DeliveryStatus> notificationStatuses = new ConcurrentHashMap<>();

    @Override
    public boolean sendNotification(String recipient, String subject, String message) {
        return sendNotification(recipient, subject, message, Map.of());
    }

    @Override
    public boolean sendNotification(String recipient, String subject, String message, Map<String, String> properties) {
        // Store the notification
        notifications.computeIfAbsent(recipient, k -> new CopyOnWriteArrayList<>())
                .add(Map.of(
                        "subject", subject,
                        "message", message,
                        "properties", properties
                ));
        
        // Notify listeners
        if (listeners.containsKey(subject)) {
            for (NotificationListener listener : listeners.get(subject)) {
                listener.onNotification("NotificationAdapter", subject, message, properties);
            }
        }
        
        return true;
    }
    
    @Override
    public NotificationResult sendAdvancedNotification(
            String recipient, String subject, String message,
            NotificationSeverity severity, NotificationChannel channel,
            ContentFormat format, Map<String, String> properties) {
        
        // Store the notification with additional metadata
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("subject", subject);
        notificationData.put("message", message);
        notificationData.put("severity", severity);
        notificationData.put("channel", channel);
        notificationData.put("format", format);
        notificationData.put("properties", properties);
        
        notifications.computeIfAbsent(recipient, k -> new CopyOnWriteArrayList<>())
                .add(notificationData);
        
        // Generate a notification ID
        String notificationId = UUID.randomUUID().toString();
        
        // Set initial status
        notificationStatuses.put(notificationId, DeliveryStatus.DELIVERED);
        
        // Notify listeners
        if (listeners.containsKey(subject)) {
            for (NotificationListener listener : listeners.get(subject)) {
                listener.onNotification("NotificationAdapter", subject, message, properties);
            }
        }
        
        // Return a successful result
        return NotificationResult.success("Notification sent successfully", DeliveryStatus.DELIVERED, 
                Map.of(
                        "channel", channel.toString(),
                        "severity", severity.toString(),
                        "format", format.toString(),
                        "recipient", recipient
                ));
    }

    @Override
    public void registerListener(String topic, NotificationListener listener) {
        listeners.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public void unregisterListener(String topic, NotificationListener listener) {
        if (listeners.containsKey(topic)) {
            listeners.get(topic).remove(listener);
            if (listeners.get(topic).isEmpty()) {
                listeners.remove(topic);
            }
        }
    }
    
    @Override
    public DeliveryStatus getNotificationStatus(String notificationId) {
        return notificationStatuses.getOrDefault(notificationId, DeliveryStatus.UNKNOWN);
    }
    
    /**
     * Gets all notifications sent to a recipient.
     *
     * @param recipient The recipient to check
     * @return A list of notification data maps
     */
    public List<Map<String, Object>> getNotificationsFor(String recipient) {
        return notifications.getOrDefault(recipient, List.of());
    }
    
    /**
     * Clears all stored notifications.
     */
    public void clearNotifications() {
        notifications.clear();
    }
}