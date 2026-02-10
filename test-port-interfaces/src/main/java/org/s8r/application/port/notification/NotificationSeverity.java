/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port.notification;

/**
 * Enumeration of notification severity levels.
 */
public enum NotificationSeverity {
    /**
     * Informational notifications.
     */
    INFO,
    
    /**
     * Warning notifications.
     */
    WARNING,
    
    /**
     * Error notifications.
     */
    ERROR,
    
    /**
     * Critical notifications that require immediate attention.
     */
    CRITICAL,
    
    /**
     * Debug notifications for development purposes.
     */
    DEBUG
}