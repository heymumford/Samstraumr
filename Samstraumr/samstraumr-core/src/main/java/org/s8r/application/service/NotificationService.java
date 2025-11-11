/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.service;

import org.s8r.application.port.NotificationPort;

import java.util.Map;

/**
 * Service for handling notifications in the application layer.
 * 
 * <p>This service provides an abstraction over the NotificationPort to handle
 * application-specific notification logic.
 */
public class NotificationService {
    
    private final NotificationPort notificationPort;
    
    /**
     * Creates a new NotificationService instance.
     *
     * @param notificationPort The notification port to use
     */
    public NotificationService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }
    
    /**
     * Sends a notification to a recipient.
     *
     * @param recipient The recipient of the notification
     * @param subject The subject of the notification
     * @param message The message content
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean notify(String recipient, String subject, String message) {
        return notificationPort.sendNotification(recipient, subject, message);
    }
    
    /**
     * Sends a notification with additional properties to a recipient.
     *
     * @param recipient The recipient of the notification
     * @param subject The subject of the notification
     * @param message The message content
     * @param properties Additional properties for the notification
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean notifyWithProperties(String recipient, String subject, String message, Map<String, String> properties) {
        return notificationPort.sendNotification(recipient, subject, message, properties);
    }
    
    /**
     * Sends a system notification to all registered listeners.
     *
     * @param subject The subject of the notification
     * @param message The message content
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean broadcastSystemNotification(String subject, String message) {
        return notificationPort.sendNotification("SYSTEM", subject, message);
    }
    
    /**
     * Registers a listener for notifications on a specific topic.
     *
     * @param topic The topic to listen for
     * @param listener The listener to register
     */
    public void listenFor(String topic, NotificationPort.NotificationListener listener) {
        notificationPort.registerListener(topic, listener);
    }
    
    /**
     * Stops listening for notifications on a specific topic.
     *
     * @param topic The topic to stop listening for
     * @param listener The listener to unregister
     */
    public void stopListening(String topic, NotificationPort.NotificationListener listener) {
        notificationPort.unregisterListener(topic, listener);
    }
}