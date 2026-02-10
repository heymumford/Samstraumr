/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.mock;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationRequest;
import org.s8r.application.port.notification.NotificationResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mock implementation of NotificationPort for testing.
 */
public class MockNotificationAdapter implements NotificationPort {
    private final List<Map<String, Object>> sentNotifications = new CopyOnWriteArrayList<>();
    private String deliveryMethod = "EMAIL";
    private boolean simulateDeliveryFailures = false;
    private int failureCount = 0;
    private boolean eventuallySucceed = true;
    private int currentFailureCount = 0;
    
    /**
     * Creates a new instance of the MockNotificationAdapter.
     *
     * @return A new MockNotificationAdapter instance.
     */
    public static MockNotificationAdapter createInstance() {
        return new MockNotificationAdapter();
    }
    
    /**
     * Configures the adapter with the given settings.
     *
     * @param settings Map of configuration settings.
     */
    public void configure(Map<String, Object> settings) {
        if (settings.containsKey("deliveryMethod")) {
            this.deliveryMethod = (String) settings.get("deliveryMethod");
        }
        if (settings.containsKey("simulateDeliveryFailures")) {
            this.simulateDeliveryFailures = (Boolean) settings.get("simulateDeliveryFailures");
        }
        if (settings.containsKey("failureCount")) {
            this.failureCount = ((Number) settings.get("failureCount")).intValue();
        }
        if (settings.containsKey("eventuallySucceed")) {
            this.eventuallySucceed = (Boolean) settings.get("eventuallySucceed");
        }
        
        // Reset the failure counter when configured
        this.currentFailureCount = 0;
    }

    @Override
    public NotificationResult sendNotification(NotificationRequest request) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("recipient", request.getRecipient());
        notification.put("subject", request.getSubject());
        notification.put("body", request.getBody());
        notification.put("deliveryMethod", request.getDeliveryMethod() \!= null ? request.getDeliveryMethod() : deliveryMethod);
        notification.put("sentAt", Instant.now().toString());
        
        boolean delivered = true;
        String errorMessage = null;
        
        // Check if we should simulate a failure
        if (simulateDeliveryFailures && currentFailureCount < failureCount) {
            delivered = false;
            errorMessage = "Simulated delivery failure";
            notification.put("deliveryStatus", "FAILED");
            currentFailureCount++;
        } else if (simulateDeliveryFailures && \!eventuallySucceed) {
            delivered = false;
            errorMessage = "Permanent delivery failure";
            notification.put("deliveryStatus", "FAILED");
        } else {
            notification.put("deliveryStatus", "DELIVERED");
        }
        
        sentNotifications.add(notification);
        
        return new NotificationResult(delivered, 
                                     delivered ? DeliveryStatus.DELIVERED : DeliveryStatus.FAILED, 
                                     errorMessage);
    }

    @Override
    public List<NotificationResult> sendNotifications(List<NotificationRequest> requests) {
        List<NotificationResult> results = new ArrayList<>();
        
        for (NotificationRequest request : requests) {
            results.add(sendNotification(request));
        }
        
        return results;
    }

    @Override
    public NotificationResult scheduleNotification(NotificationRequest request, String scheduledTime) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("recipient", request.getRecipient());
        notification.put("subject", request.getSubject());
        notification.put("body", request.getBody());
        notification.put("deliveryMethod", request.getDeliveryMethod() \!= null ? request.getDeliveryMethod() : deliveryMethod);
        notification.put("scheduledAt", scheduledTime);
        notification.put("status", "SCHEDULED");
        
        sentNotifications.add(notification);
        
        return new NotificationResult(true, DeliveryStatus.SCHEDULED, null);
    }

    @Override
    public NotificationResult cancelScheduledNotification(String notificationId) {
        for (Map<String, Object> notification : sentNotifications) {
            if (notification.containsKey("id") && notification.get("id").equals(notificationId)) {
                notification.put("status", "CANCELLED");
                return new NotificationResult(true, DeliveryStatus.CANCELLED, null);
            }
        }
        
        return new NotificationResult(false, DeliveryStatus.FAILED, "Notification not found");
    }
    
    /**
     * Gets the list of sent notifications.
     *
     * @return List of sent notifications.
     */
    public List<Map<String, Object>> getSentNotifications() {
        return sentNotifications;
    }
    
    /**
     * Clears the list of sent notifications.
     */
    public void clearNotifications() {
        sentNotifications.clear();
        currentFailureCount = 0;
    }
}
