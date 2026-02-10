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