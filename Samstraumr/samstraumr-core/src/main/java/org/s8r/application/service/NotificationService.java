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
import java.util.Map;

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
 */
public class NotificationService {
    
    private final NotificationPort notificationPort;
    private final LoggerPort logger;
    
    /**
     * Constructs a new NotificationService.
     *
     * @param notificationPort The notification port to use
     * @param logger The logger to use
     */
    public NotificationService(NotificationPort notificationPort, LoggerPort logger) {
        this.notificationPort = notificationPort;
        this.logger = logger;
        
        logger.debug("NotificationService created");
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
}