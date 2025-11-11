/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port.notification;

/**
 * Enumeration of possible delivery statuses for notifications.
 */
public enum DeliveryStatus {
    /**
     * The notification was delivered successfully.
     */
    DELIVERED,
    
    /**
     * The notification delivery failed.
     */
    FAILED,
    
    /**
     * The notification delivery is pending.
     */
    PENDING,
    
    /**
     * The notification was queued for later delivery.
     */
    QUEUED,
    
    /**
     * The notification was rejected by the delivery system.
     */
    REJECTED,
    
    /**
     * The notification was scheduled for future delivery.
     */
    SCHEDULED,
    
    /**
     * The notification was canceled before delivery.
     */
    CANCELED,
    
    /**
     * The notification delivery status is unknown.
     */
    UNKNOWN
}