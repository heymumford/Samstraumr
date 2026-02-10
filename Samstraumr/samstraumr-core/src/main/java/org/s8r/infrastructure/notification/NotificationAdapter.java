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

import java.util.HashMap;
import java.util.Map;
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