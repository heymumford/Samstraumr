/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.integration;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.test.mock.MockNotificationAdapter;
import org.s8r.test.mock.MockTaskExecutionAdapter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Test context for the TaskNotification integration tests.
 * This class manages the test state and provides utility methods for test verification.
 */
public class TaskNotificationIntegrationContext {
    
    private final TaskExecutionPort taskExecutionPort;
    private final NotificationPort notificationPort;
    private final TaskNotificationBridge bridge;
    private final MockTaskExecutionAdapter mockTaskExecutionAdapter;
    private final MockNotificationAdapter mockNotificationAdapter;
    
    private Map<String, String> lastScheduledNotificationIds = new HashMap<>();
    private boolean notificationFailureMode = false;
    private int notificationDeliverySlowdown = 0;
    
    /**
     * Creates a new TaskNotificationIntegrationContext with mock adapters.
     */
    public TaskNotificationIntegrationContext() {
        this.mockTaskExecutionAdapter = new MockTaskExecutionAdapter();
        this.mockNotificationAdapter = new MockNotificationAdapter();
        this.taskExecutionPort = mockTaskExecutionAdapter;
        this.notificationPort = mockNotificationAdapter;
        this.bridge = new TaskNotificationBridge(taskExecutionPort, notificationPort);
    }
    
    /**
     * Gets the task execution port.
     *
     * @return The task execution port
     */
    public TaskExecutionPort getTaskExecutionPort() {
        return taskExecutionPort;
    }
    
    /**
     * Gets the notification port.
     *
     * @return The notification port
     */
    public NotificationPort getNotificationPort() {
        return notificationPort;
    }
    
    /**
     * Gets the bridge.
     *
     * @return The task notification bridge
     */
    public TaskNotificationBridge getBridge() {
        return bridge;
    }
    
    /**
     * Gets the mock task execution adapter.
     *
     * @return The mock task execution adapter
     */
    public MockTaskExecutionAdapter getMockTaskExecutionAdapter() {
        return mockTaskExecutionAdapter;
    }
    
    /**
     * Gets the mock notification adapter.
     *
     * @return The mock notification adapter
     */
    public MockNotificationAdapter getMockNotificationAdapter() {
        return mockNotificationAdapter;
    }
    
    /**
     * Stores a notification ID for a given purpose.
     *
     * @param purpose The purpose of the notification
     * @param notificationId The notification ID
     */
    public void setLastNotificationId(String purpose, String notificationId) {
        lastScheduledNotificationIds.put(purpose, notificationId);
    }
    
    /**
     * Gets the notification ID for a given purpose.
     *
     * @param purpose The purpose of the notification
     * @return The notification ID
     */
    public String getLastNotificationId(String purpose) {
        return lastScheduledNotificationIds.get(purpose);
    }
    
    /**
     * Checks if a notification was delivered to a recipient.
     *
     * @param recipient The recipient to check
     * @return true if a notification was delivered to the recipient, false otherwise
     */
    public boolean wasNotificationDelivered(String recipient) {
        return mockNotificationAdapter.getSentNotifications().values().stream()
                .anyMatch(notification -> notification.getRecipient().equals(recipient));
    }
    
    /**
     * Gets the count of notifications delivered to a recipient.
     *
     * @param recipient The recipient to check
     * @return The number of notifications delivered to the recipient
     */
    public int getNotificationCount(String recipient) {
        return (int) mockNotificationAdapter.getSentNotifications().values().stream()
                .filter(notification -> notification.getRecipient().equals(recipient))
                .count();
    }
    
    /**
     * Checks if the notification with the given ID has been delivered.
     *
     * @param notificationId The notification ID to check
     * @return true if the notification was delivered, false otherwise
     */
    public boolean isNotificationDelivered(String notificationId) {
        Map<String, Object> info = bridge.getScheduledNotificationInfo(notificationId);
        if (info.isEmpty()) {
            return false;
        }
        
        String status = (String) info.get("status");
        return DeliveryStatus.DELIVERED.name().equals(status);
    }
    
    /**
     * Enables notification failure mode.
     * In this mode, notifications will initially fail and need to be retried.
     *
     * @param enable Whether to enable or disable failure mode
     */
    public void setNotificationFailureMode(boolean enable) {
        this.notificationFailureMode = enable;
    }
    
    /**
     * Checks if notification failure mode is enabled.
     *
     * @return true if failure mode is enabled, false otherwise
     */
    public boolean isNotificationFailureMode() {
        return notificationFailureMode;
    }
    
    /**
     * Sets a delay for notification delivery.
     * This simulates slow notification processing.
     *
     * @param milliseconds The delay in milliseconds
     */
    public void setNotificationDeliverySlowdown(int milliseconds) {
        this.notificationDeliverySlowdown = milliseconds;
    }
    
    /**
     * Gets the notification delivery slowdown.
     *
     * @return The delay in milliseconds
     */
    public int getNotificationDeliverySlowdown() {
        return notificationDeliverySlowdown;
    }
    
    /**
     * Simulates a long-running task by adding an artificial delay.
     *
     * @param taskId The ID of the task to slow down
     * @param delay The delay to add
     */
    public void simulateSlowTask(String taskId, Duration delay) {
        mockTaskExecutionAdapter.simulateSlowTask(taskId, delay);
    }
    
    /**
     * Simulates a failure for the specified task.
     *
     * @param taskId The ID of the task to fail
     * @param reason The reason for the failure
     */
    public void simulateTaskFailure(String taskId, String reason) {
        mockTaskExecutionAdapter.simulateTaskFailure(taskId, reason);
    }
}