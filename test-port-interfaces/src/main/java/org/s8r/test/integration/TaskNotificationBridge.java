/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.integration;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.task.TaskResult;
import org.s8r.application.port.task.TaskStatus;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bridge component that connects TaskExecutionPort and NotificationPort interfaces.
 * This class ensures tasks can be scheduled for sending notifications and handles
 * retrying notification delivery when failures occur.
 */
public class TaskNotificationBridge {
    
    private static final Logger LOGGER = Logger.getLogger(TaskNotificationBridge.class.getName());
    private static final String TASK_PREFIX = "notification-task-";
    private static final int MAX_RETRY_ATTEMPTS = 5;
    
    private final TaskExecutionPort taskExecutionPort;
    private final NotificationPort notificationPort;
    private final Map<String, ScheduledNotification> scheduledNotifications;
    private final Map<String, RetryableNotification> retryingNotifications;
    private final Map<String, BatchNotification> batchNotifications;
    private final Map<String, RecurringNotification> recurringNotifications;
    private final Map<String, Integer> notificationDeliveryCount;
    
    /**
     * Constructs a new TaskNotificationBridge with the necessary ports.
     *
     * @param taskExecutionPort The task execution port interface
     * @param notificationPort The notification port interface
     */
    public TaskNotificationBridge(TaskExecutionPort taskExecutionPort, NotificationPort notificationPort) {
        this.taskExecutionPort = taskExecutionPort;
        this.notificationPort = notificationPort;
        this.scheduledNotifications = new ConcurrentHashMap<>();
        this.retryingNotifications = new ConcurrentHashMap<>();
        this.batchNotifications = new ConcurrentHashMap<>();
        this.recurringNotifications = new ConcurrentHashMap<>();
        this.notificationDeliveryCount = new ConcurrentHashMap<>();
    }
    
    /**
     * Schedules a notification for delivery after a specified delay.
     *
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param message The notification message
     * @param delay The delay before delivering the notification
     * @return A TaskResult representing the scheduling result
     */
    public TaskResult<String> scheduleNotification(
            String recipient, String subject, String message, Duration delay) {
        return scheduleNotification(
                recipient, subject, message, 
                NotificationSeverity.INFO, NotificationChannel.EMAIL, 
                ContentFormat.TEXT, Collections.emptyMap(), delay);
    }
    
    /**
     * Schedules a notification with advanced options for delivery after a specified delay.
     *
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @param channel The notification channel
     * @param format The content format
     * @param properties Additional properties for the notification
     * @param delay The delay before delivering the notification
     * @return A TaskResult representing the scheduling result
     */
    public TaskResult<String> scheduleNotification(
            String recipient, String subject, String message, 
            NotificationSeverity severity, NotificationChannel channel, 
            ContentFormat format, Map<String, String> properties, Duration delay) {
        
        String notificationId = UUID.randomUUID().toString();
        String taskId = TASK_PREFIX + notificationId;
        
        ScheduledNotification scheduledNotification = new ScheduledNotification(
                notificationId, recipient, subject, message, 
                severity, channel, format, 
                properties != null ? new HashMap<>(properties) : new HashMap<>(),
                Instant.now(), Instant.now().plus(delay));
        
        scheduledNotifications.put(notificationId, scheduledNotification);
        
        // Create a task that will send the notification
        Callable<String> notificationTask = () -> {
            try {
                LOGGER.info("Executing scheduled notification task for notification: " + notificationId);
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                        recipient, subject, message, severity, channel, format, 
                        properties != null ? properties : Collections.emptyMap());
                
                if (result.isSuccessful()) {
                    scheduledNotification.status = DeliveryStatus.DELIVERED;
                    scheduledNotification.deliveryTime = Optional.of(Instant.now());
                    notificationDeliveryCount.merge(recipient, 1, Integer::sum);
                    return "Notification delivered successfully: " + notificationId;
                } else {
                    scheduledNotification.status = DeliveryStatus.FAILED;
                    scheduledNotification.error = Optional.of(
                            result.getReason().orElse("Unknown delivery failure"));
                    
                    // Handle failed notification - could initiate retry here
                    LOGGER.warning("Scheduled notification delivery failed: " + notificationId +
                            " reason: " + result.getReason().orElse("Unknown"));
                    
                    return "Notification delivery failed: " + notificationId;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error executing notification task", e);
                scheduledNotification.status = DeliveryStatus.FAILED;
                scheduledNotification.error = Optional.of(e.getMessage());
                throw e;
            }
        };
        
        // Schedule the task
        TaskResult<String> taskResult = taskExecutionPort.scheduleTask(notificationTask, delay);
        
        if (taskResult.isSuccessful()) {
            scheduledNotification.taskId = taskResult.getTaskId();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("notificationId", notificationId);
            attributes.put("scheduledAt", scheduledNotification.scheduledTime.toString());
            attributes.put("scheduledDeliveryTime", scheduledNotification.scheduledDeliveryTime.toString());
            attributes.put("recipient", recipient);
            attributes.put("subject", subject);
            
            return TaskResult.scheduled(taskId, "Notification scheduled for delivery", attributes);
        } else {
            scheduledNotifications.remove(notificationId);
            
            return TaskResult.failure(taskId, TaskStatus.FAILED, 
                    "Failed to schedule notification", 
                    taskResult.getReason().orElse("Unknown scheduler error"));
        }
    }
    
    /**
     * Schedules multiple notifications for delivery after a specified delay.
     *
     * @param recipients The notification recipients
     * @param subject The notification subject
     * @param message The notification message
     * @param delay The delay before delivering the notifications
     * @return A list of TaskResults representing the scheduling results
     */
    public List<TaskResult<String>> scheduleMultipleNotifications(
            List<String> recipients, String subject, String message, Duration delay) {
        
        List<TaskResult<String>> results = new ArrayList<>();
        
        for (String recipient : recipients) {
            results.add(scheduleNotification(recipient, subject, message, delay));
        }
        
        return results;
    }
    
    /**
     * Schedules a recurring notification to be sent at regular intervals.
     *
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param message The notification message
     * @param initialDelay The initial delay before the first notification
     * @param interval The interval between notifications
     * @return A TaskResult representing the scheduling result
     */
    public TaskResult<String> scheduleRecurringNotification(
            String recipient, String subject, String message, 
            Duration initialDelay, Duration interval) {
        
        String notificationId = UUID.randomUUID().toString();
        String taskId = TASK_PREFIX + "recurring-" + notificationId;
        
        RecurringNotification recurringNotification = new RecurringNotification(
                notificationId, recipient, subject, message, 
                NotificationSeverity.INFO, NotificationChannel.EMAIL, 
                ContentFormat.TEXT, new HashMap<>(),
                Instant.now(), interval);
        
        recurringNotifications.put(notificationId, recurringNotification);
        
        // Create a recurring task for sending notifications
        Runnable notificationTask = () -> {
            try {
                LOGGER.info("Executing recurring notification task for notification: " + notificationId);
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                        recipient, subject, message, 
                        NotificationSeverity.INFO, NotificationChannel.EMAIL, 
                        ContentFormat.TEXT, Collections.emptyMap());
                
                if (result.isSuccessful()) {
                    recurringNotification.lastDeliveryTime = Instant.now();
                    recurringNotification.deliveryCount.incrementAndGet();
                    notificationDeliveryCount.merge(recipient, 1, Integer::sum);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error executing recurring notification task", e);
            }
        };
        
        // Schedule the recurring task
        TaskResult<Void> taskResult = setupRecurringTask(notificationTask, initialDelay, interval);
        
        if (taskResult.isSuccessful()) {
            recurringNotification.taskId = taskResult.getTaskId();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("notificationId", notificationId);
            attributes.put("scheduledAt", recurringNotification.creationTime.toString());
            attributes.put("initialDelay", initialDelay.toString());
            attributes.put("interval", interval.toString());
            attributes.put("recipient", recipient);
            attributes.put("subject", subject);
            
            return TaskResult.scheduled(taskId, "Recurring notification scheduled", attributes);
        } else {
            recurringNotifications.remove(notificationId);
            
            return TaskResult.failure(taskId, TaskStatus.FAILED, 
                    "Failed to schedule recurring notification", 
                    taskResult.getReason().orElse("Unknown scheduler error"));
        }
    }
    
    /**
     * Sets up a recurring task.
     * This method uses the standard task scheduler to simulate recurring tasks if they're not directly supported.
     *
     * @param task The task to execute
     * @param initialDelay The initial delay before the first execution
     * @param interval The interval between executions
     * @return A TaskResult representing the scheduling result
     */
    private TaskResult<Void> setupRecurringTask(Runnable task, Duration initialDelay, Duration interval) {
        // If the adapter supports recurring tasks directly (as our mock does), use that
        if (taskExecutionPort instanceof org.s8r.test.mock.MockTaskExecutionAdapter) {
            return ((org.s8r.test.mock.MockTaskExecutionAdapter) taskExecutionPort)
                    .scheduleRecurringTask(task, initialDelay, interval);
        }
        
        // If not, we'd simulate recurring tasks by scheduling the next one after each execution
        // This simple implementation just schedules the initial task for demonstration
        return taskExecutionPort.scheduleTask(task, initialDelay);
    }
    
    /**
     * Cancels a scheduled notification.
     *
     * @param notificationId The ID of the notification to cancel
     * @return true if the notification was canceled successfully, false otherwise
     */
    public boolean cancelScheduledNotification(String notificationId) {
        ScheduledNotification scheduledNotification = scheduledNotifications.get(notificationId);
        if (scheduledNotification == null) {
            // Check if it's a recurring notification
            RecurringNotification recurringNotification = recurringNotifications.get(notificationId);
            if (recurringNotification != null) {
                boolean result = taskExecutionPort.cancelTask(recurringNotification.taskId);
                if (result) {
                    recurringNotification.status = DeliveryStatus.CANCELED;
                }
                return result;
            }
            return false;
        }
        
        if (scheduledNotification.status != DeliveryStatus.PENDING && 
                scheduledNotification.status != DeliveryStatus.SCHEDULED) {
            return false;  // Can't cancel notifications that are already delivered or failed
        }
        
        boolean result = taskExecutionPort.cancelTask(scheduledNotification.taskId);
        if (result) {
            scheduledNotification.status = DeliveryStatus.CANCELED;
        }
        
        return result;
    }
    
    /**
     * Gets the status of a scheduled notification.
     *
     * @param notificationId The ID of the notification
     * @return The delivery status of the notification
     */
    public DeliveryStatus getScheduledNotificationStatus(String notificationId) {
        ScheduledNotification scheduledNotification = scheduledNotifications.get(notificationId);
        if (scheduledNotification == null) {
            // Check if it's a recurring notification
            RecurringNotification recurringNotification = recurringNotifications.get(notificationId);
            if (recurringNotification != null) {
                return recurringNotification.status;
            }
            
            // Check if it's a batch notification
            for (BatchNotification batchNotification : batchNotifications.values()) {
                if (batchNotification.notificationIds.contains(notificationId)) {
                    return batchNotification.status;
                }
            }
            
            return DeliveryStatus.UNKNOWN;
        }
        
        return scheduledNotification.status;
    }
    
    /**
     * Gets information about a scheduled notification.
     *
     * @param notificationId The ID of the notification
     * @return A map containing information about the notification, or an empty map if not found
     */
    public Map<String, Object> getScheduledNotificationInfo(String notificationId) {
        Map<String, Object> info = new HashMap<>();
        
        ScheduledNotification scheduledNotification = scheduledNotifications.get(notificationId);
        if (scheduledNotification != null) {
            info.put("id", scheduledNotification.id);
            info.put("recipient", scheduledNotification.recipient);
            info.put("subject", scheduledNotification.subject);
            info.put("status", scheduledNotification.status.toString());
            info.put("scheduledTime", scheduledNotification.scheduledTime.toString());
            info.put("scheduledDeliveryTime", scheduledNotification.scheduledDeliveryTime.toString());
            
            scheduledNotification.deliveryTime.ifPresent(time -> 
                    info.put("deliveryTime", time.toString()));
            
            scheduledNotification.error.ifPresent(errorMsg -> 
                    info.put("error", errorMsg));
            
            return info;
        }
        
        // Check if it's a recurring notification
        RecurringNotification recurringNotification = recurringNotifications.get(notificationId);
        if (recurringNotification != null) {
            info.put("id", recurringNotification.id);
            info.put("type", "recurring");
            info.put("recipient", recurringNotification.recipient);
            info.put("subject", recurringNotification.subject);
            info.put("status", recurringNotification.status.toString());
            info.put("creationTime", recurringNotification.creationTime.toString());
            info.put("interval", recurringNotification.interval.toString());
            info.put("deliveryCount", recurringNotification.deliveryCount.get());
            
            recurringNotification.lastDeliveryTime.ifPresent(time -> 
                    info.put("lastDeliveryTime", time.toString()));
            
            return info;
        }
        
        // Check if it's part of a batch notification
        for (BatchNotification batchNotification : batchNotifications.values()) {
            if (batchNotification.notificationIds.contains(notificationId)) {
                info.put("id", notificationId);
                info.put("type", "batch");
                info.put("batchId", batchNotification.id);
                info.put("status", batchNotification.status.toString());
                return info;
            }
        }
        
        return info;
    }
    
    /**
     * Registers events for batch notification.
     *
     * @param events The list of events to include in the batch
     * @return The ID of the batch
     */
    public String registerEventsForBatchNotification(List<Map<String, String>> events) {
        String batchId = UUID.randomUUID().toString();
        
        List<String> notificationIds = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            notificationIds.add(UUID.randomUUID().toString());
        }
        
        BatchNotification batchNotification = new BatchNotification(
                batchId, events, notificationIds, Instant.now());
        
        batchNotifications.put(batchId, batchNotification);
        
        return batchId;
    }
    
    /**
     * Schedules a batch notification to be sent after a specified delay.
     *
     * @param batchId The ID of the batch
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param delay The delay before sending the batch notification
     * @return A TaskResult representing the scheduling result
     */
    public TaskResult<String> scheduleBatchNotification(
            String batchId, String recipient, String subject, Duration delay) {
        
        BatchNotification batchNotification = batchNotifications.get(batchId);
        if (batchNotification == null) {
            return TaskResult.failure("unknown", TaskStatus.FAILED, 
                    "Batch not found", "No batch found with the specified ID");
        }
        
        String taskId = TASK_PREFIX + "batch-" + batchId;
        
        batchNotification.recipient = recipient;
        batchNotification.subject = subject;
        batchNotification.status = DeliveryStatus.SCHEDULED;
        
        // Create a task that will send the batch notification
        Callable<String> batchTask = () -> {
            try {
                LOGGER.info("Executing batch notification task for batch: " + batchId);
                
                // Create a batch summary message
                String message = createBatchSummary(batchNotification.events);
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                        recipient, subject, message, 
                        NotificationSeverity.INFO, NotificationChannel.EMAIL, 
                        ContentFormat.HTML, Collections.emptyMap());
                
                if (result.isSuccessful()) {
                    batchNotification.status = DeliveryStatus.DELIVERED;
                    batchNotification.deliveryTime = Optional.of(Instant.now());
                    notificationDeliveryCount.merge(recipient, 1, Integer::sum);
                    return "Batch notification delivered successfully: " + batchId;
                } else {
                    batchNotification.status = DeliveryStatus.FAILED;
                    batchNotification.error = Optional.of(
                            result.getReason().orElse("Unknown delivery failure"));
                    
                    LOGGER.warning("Batch notification delivery failed: " + batchId +
                            " reason: " + result.getReason().orElse("Unknown"));
                    
                    return "Batch notification delivery failed: " + batchId;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error executing batch notification task", e);
                batchNotification.status = DeliveryStatus.FAILED;
                batchNotification.error = Optional.of(e.getMessage());
                throw e;
            }
        };
        
        // Schedule the task
        TaskResult<String> taskResult = taskExecutionPort.scheduleTask(batchTask, delay);
        
        if (taskResult.isSuccessful()) {
            batchNotification.taskId = taskResult.getTaskId();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("batchId", batchId);
            attributes.put("eventCount", batchNotification.events.size());
            attributes.put("scheduledAt", batchNotification.creationTime.toString());
            attributes.put("scheduledDeliveryTime", Instant.now().plus(delay).toString());
            attributes.put("recipient", recipient);
            attributes.put("subject", subject);
            
            return TaskResult.scheduled(taskId, "Batch notification scheduled for delivery", attributes);
        } else {
            return TaskResult.failure(taskId, TaskStatus.FAILED, 
                    "Failed to schedule batch notification", 
                    taskResult.getReason().orElse("Unknown scheduler error"));
        }
    }
    
    /**
     * Sends a notification with retries on failure.
     *
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param message The notification message
     * @param severity The notification severity
     * @param channel The notification channel
     * @param format The content format
     * @param properties Additional properties for the notification
     * @return A TaskResult representing the operation result
     */
    public TaskResult<String> sendNotificationWithRetry(
            String recipient, String subject, String message, 
            NotificationSeverity severity, NotificationChannel channel, 
            ContentFormat format, Map<String, String> properties) {
        
        String notificationId = UUID.randomUUID().toString();
        String taskId = TASK_PREFIX + "retry-" + notificationId;
        
        RetryableNotification retryableNotification = new RetryableNotification(
                notificationId, recipient, subject, message, 
                severity, channel, format, 
                properties != null ? new HashMap<>(properties) : new HashMap<>(),
                Instant.now());
        
        retryingNotifications.put(notificationId, retryableNotification);
        
        // First attempt
        try {
            NotificationResult result = notificationPort.sendAdvancedNotification(
                    recipient, subject, message, severity, channel, format, 
                    properties != null ? properties : Collections.emptyMap());
            
            if (result.isSuccessful()) {
                retryableNotification.status = DeliveryStatus.DELIVERED;
                retryableNotification.deliveryTime = Instant.now();
                notificationDeliveryCount.merge(recipient, 1, Integer::sum);
                
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("notificationId", notificationId);
                attributes.put("recipient", recipient);
                attributes.put("subject", subject);
                attributes.put("attempts", 1);
                
                return TaskResult.success(taskId, "Notification delivered successfully", 
                        "Notification was delivered on first attempt", attributes);
            } else {
                // Initial attempt failed, schedule retries
                return scheduleRetries(retryableNotification, taskId);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Initial notification attempt failed", e);
            retryableNotification.retryCount = 1;  // Count the initial attempt
            retryableNotification.lastError = Optional.of(e.getMessage());
            
            // Schedule retries
            return scheduleRetries(retryableNotification, taskId);
        }
    }
    
    /**
     * Schedules retry attempts for a failed notification.
     *
     * @param notification The notification to retry
     * @param taskId The task ID for the retry operation
     * @return A TaskResult representing the scheduling result
     */
    private TaskResult<String> scheduleRetries(RetryableNotification notification, String taskId) {
        // Calculate delay for next retry using exponential backoff
        Duration delay = calculateBackoffDelay(notification.retryCount);
        
        Callable<String> retryTask = () -> {
            try {
                LOGGER.info("Executing retry attempt " + (notification.retryCount + 1) + 
                        " for notification: " + notification.id);
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                        notification.recipient, notification.subject, notification.message, 
                        notification.severity, notification.channel, notification.format, 
                        notification.properties);
                
                notification.retryCount++;
                notification.retryTimes.add(Instant.now());
                
                if (result.isSuccessful()) {
                    notification.status = DeliveryStatus.DELIVERED;
                    notification.deliveryTime = Optional.of(Instant.now());
                    notificationDeliveryCount.merge(notification.recipient, 1, Integer::sum);
                    return "Notification delivered successfully on retry attempt " + 
                            notification.retryCount + ": " + notification.id;
                } else {
                    notification.lastError = result.getReason();
                    
                    if (notification.retryCount >= MAX_RETRY_ATTEMPTS) {
                        notification.status = DeliveryStatus.FAILED;
                        return "Notification delivery failed after " + MAX_RETRY_ATTEMPTS + 
                                " attempts: " + notification.id;
                    } else {
                        // Schedule another retry
                        Duration nextDelay = calculateBackoffDelay(notification.retryCount);
                        taskExecutionPort.scheduleTask(this.createRetryTask(notification), nextDelay);
                        
                        return "Scheduling retry attempt " + (notification.retryCount + 1) + 
                                " for notification: " + notification.id;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during notification retry", e);
                notification.retryCount++;
                notification.retryTimes.add(Instant.now());
                notification.lastError = Optional.of(e.getMessage());
                
                if (notification.retryCount >= MAX_RETRY_ATTEMPTS) {
                    notification.status = DeliveryStatus.FAILED;
                    throw e;
                } else {
                    // Schedule another retry
                    Duration nextDelay = calculateBackoffDelay(notification.retryCount);
                    taskExecutionPort.scheduleTask(this.createRetryTask(notification), nextDelay);
                }
                
                throw e;
            }
        };
        
        // Schedule the retry
        TaskResult<String> taskResult = taskExecutionPort.scheduleTask(retryTask, delay);
        
        if (taskResult.isSuccessful()) {
            notification.taskId = taskResult.getTaskId();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("notificationId", notification.id);
            attributes.put("recipient", notification.recipient);
            attributes.put("subject", notification.subject);
            attributes.put("retryCount", notification.retryCount);
            attributes.put("maxRetries", MAX_RETRY_ATTEMPTS);
            attributes.put("nextRetryDelay", delay.toString());
            
            return TaskResult.scheduled(taskId, 
                    "Notification retry scheduled (attempt " + (notification.retryCount + 1) + ")", 
                    attributes);
        } else {
            notification.status = DeliveryStatus.FAILED;
            
            return TaskResult.failure(taskId, TaskStatus.FAILED, 
                    "Failed to schedule notification retry", 
                    taskResult.getReason().orElse("Unknown scheduler error"));
        }
    }
    
    /**
     * Creates a retry task for a notification.
     *
     * @param notification The notification to retry
     * @return A callable representing the retry task
     */
    private Callable<String> createRetryTask(RetryableNotification notification) {
        return () -> {
            try {
                LOGGER.info("Executing retry attempt " + (notification.retryCount + 1) + 
                        " for notification: " + notification.id);
                
                NotificationResult result = notificationPort.sendAdvancedNotification(
                        notification.recipient, notification.subject, notification.message, 
                        notification.severity, notification.channel, notification.format, 
                        notification.properties);
                
                notification.retryCount++;
                notification.retryTimes.add(Instant.now());
                
                if (result.isSuccessful()) {
                    notification.status = DeliveryStatus.DELIVERED;
                    notification.deliveryTime = Optional.of(Instant.now());
                    notificationDeliveryCount.merge(notification.recipient, 1, Integer::sum);
                    return "Notification delivered successfully on retry attempt " + 
                            notification.retryCount + ": " + notification.id;
                } else {
                    notification.lastError = result.getReason();
                    
                    if (notification.retryCount >= MAX_RETRY_ATTEMPTS) {
                        notification.status = DeliveryStatus.FAILED;
                        return "Notification delivery failed after " + MAX_RETRY_ATTEMPTS + 
                                " attempts: " + notification.id;
                    } else {
                        // Schedule another retry
                        Duration nextDelay = calculateBackoffDelay(notification.retryCount);
                        taskExecutionPort.scheduleTask(this.createRetryTask(notification), nextDelay);
                        
                        return "Scheduling retry attempt " + (notification.retryCount + 1) + 
                                " for notification: " + notification.id;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during notification retry", e);
                notification.retryCount++;
                notification.retryTimes.add(Instant.now());
                notification.lastError = Optional.of(e.getMessage());
                
                if (notification.retryCount >= MAX_RETRY_ATTEMPTS) {
                    notification.status = DeliveryStatus.FAILED;
                    throw e;
                } else {
                    // Schedule another retry
                    Duration nextDelay = calculateBackoffDelay(notification.retryCount);
                    taskExecutionPort.scheduleTask(this.createRetryTask(notification), nextDelay);
                }
                
                throw e;
            }
        };
    }
    
    /**
     * Calculates the delay for the next retry attempt using exponential backoff.
     *
     * @param retryCount The current retry count
     * @return The delay before the next retry
     */
    private Duration calculateBackoffDelay(int retryCount) {
        // Exponential backoff: baseDelay * 2^retryCount with a max cap
        long baseDelayMs = 1000;  // 1 second
        long maxDelayMs = 60000;  // 1 minute
        
        long delayMs = baseDelayMs * (long) Math.pow(2, retryCount);
        return Duration.ofMillis(Math.min(delayMs, maxDelayMs));
    }
    
    /**
     * Creates a batch summary message from a list of events.
     *
     * @param events The events to include in the summary
     * @return A formatted HTML message with the batch summary
     */
    private String createBatchSummary(List<Map<String, String>> events) {
        StringBuilder builder = new StringBuilder();
        builder.append("<h1>Batch Notification Summary</h1>\n");
        builder.append("<p>This notification contains a summary of ")
               .append(events.size())
               .append(" events.</p>\n");
        
        builder.append("<table border=\"1\">\n");
        builder.append("<tr><th>Event</th><th>Details</th></tr>\n");
        
        for (Map<String, String> event : events) {
            builder.append("<tr>\n");
            
            // Event type column
            builder.append("<td>");
            builder.append(event.getOrDefault("type", "Unknown Event"));
            builder.append("</td>\n");
            
            // Details column
            builder.append("<td>\n<ul>\n");
            for (Map.Entry<String, String> entry : event.entrySet()) {
                if (!"type".equals(entry.getKey())) {
                    builder.append("<li><strong>")
                           .append(entry.getKey())
                           .append(":</strong> ")
                           .append(entry.getValue())
                           .append("</li>\n");
                }
            }
            builder.append("</ul>\n</td>\n");
            
            builder.append("</tr>\n");
        }
        
        builder.append("</table>\n");
        builder.append("<p>Generated at: ").append(Instant.now()).append("</p>");
        
        return builder.toString();
    }
    
    /**
     * Gets information about a notification template.
     *
     * @param templateName The name of the template
     * @param parameters The parameters to apply to the template
     * @return The template with parameters applied, or null if the template doesn't exist
     */
    public String getTemplatedNotification(String templateName, Map<String, String> parameters) {
        // This would normally retrieve a template from a template service
        // For testing purposes, we'll use a simple implementation
        
        Map<String, String> templates = new HashMap<>();
        templates.put("scheduled", "Scheduled notification: {message}");
        templates.put("reminder", "Reminder: {event} is scheduled for {time}");
        templates.put("status_update", "Status update: {component} is now {status}");
        templates.put("batch_summary", "Daily summary: {count} events occurred");
        
        String template = templates.get(templateName);
        if (template == null) {
            return null;
        }
        
        // Apply parameters
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            template = template.replace("{" + param.getKey() + "}", param.getValue());
        }
        
        return template;
    }
    
    /**
     * Schedules a templated notification for delivery.
     *
     * @param templateName The name of the template
     * @param parameters The parameters to apply to the template
     * @param recipient The notification recipient
     * @param subject The notification subject
     * @param delay The delay before delivering the notification
     * @return A TaskResult representing the scheduling result
     */
    public TaskResult<String> scheduleTemplatedNotification(
            String templateName, Map<String, String> parameters,
            String recipient, String subject, Duration delay) {
        
        String message = getTemplatedNotification(templateName, parameters);
        if (message == null) {
            return TaskResult.failure("unknown", TaskStatus.FAILED, 
                    "Template not found", "No template found with the specified name");
        }
        
        return scheduleNotification(recipient, subject, message, delay);
    }
    
    /**
     * Information about a scheduled notification.
     */
    public static class ScheduledNotification {
        private final String id;
        private final String recipient;
        private final String subject;
        private final String message;
        private final NotificationSeverity severity;
        private final NotificationChannel channel;
        private final ContentFormat format;
        private final Map<String, String> properties;
        private final Instant scheduledTime;
        private final Instant scheduledDeliveryTime;
        private Optional<Instant> deliveryTime;
        private Optional<String> error;
        private DeliveryStatus status;
        private String taskId;
        
        /**
         * Creates a new ScheduledNotification with the specified parameters.
         *
         * @param id The notification ID
         * @param recipient The recipient
         * @param subject The subject
         * @param message The message
         * @param severity The severity
         * @param channel The channel
         * @param format The content format
         * @param properties Additional properties
         * @param scheduledTime The time when the notification was scheduled
         * @param scheduledDeliveryTime The time when the notification is scheduled to be delivered
         */
        ScheduledNotification(
                String id, String recipient, String subject, String message, 
                NotificationSeverity severity, NotificationChannel channel, 
                ContentFormat format, Map<String, String> properties,
                Instant scheduledTime, Instant scheduledDeliveryTime) {
            
            this.id = id;
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.severity = severity;
            this.channel = channel;
            this.format = format;
            this.properties = properties;
            this.scheduledTime = scheduledTime;
            this.scheduledDeliveryTime = scheduledDeliveryTime;
            this.deliveryTime = Optional.empty();
            this.error = Optional.empty();
            this.status = DeliveryStatus.SCHEDULED;
        }
    }
    
    /**
     * Information about a notification with retry logic.
     */
    public static class RetryableNotification {
        private final String id;
        private final String recipient;
        private final String subject;
        private final String message;
        private final NotificationSeverity severity;
        private final NotificationChannel channel;
        private final ContentFormat format;
        private final Map<String, String> properties;
        private final Instant creationTime;
        private final List<Instant> retryTimes;
        private Optional<Instant> deliveryTime;
        private Optional<String> lastError;
        private DeliveryStatus status;
        private String taskId;
        private int retryCount;
        
        /**
         * Creates a new RetryableNotification with the specified parameters.
         *
         * @param id The notification ID
         * @param recipient The recipient
         * @param subject The subject
         * @param message The message
         * @param severity The severity
         * @param channel The channel
         * @param format The content format
         * @param properties Additional properties
         * @param creationTime The time when the notification was created
         */
        RetryableNotification(
                String id, String recipient, String subject, String message, 
                NotificationSeverity severity, NotificationChannel channel, 
                ContentFormat format, Map<String, String> properties,
                Instant creationTime) {
            
            this.id = id;
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.severity = severity;
            this.channel = channel;
            this.format = format;
            this.properties = properties;
            this.creationTime = creationTime;
            this.retryTimes = new ArrayList<>();
            this.deliveryTime = Optional.empty();
            this.lastError = Optional.empty();
            this.status = DeliveryStatus.PENDING;
            this.retryCount = 0;
        }
    }
    
    /**
     * Information about a batch notification.
     */
    public static class BatchNotification {
        private final String id;
        private final List<Map<String, String>> events;
        private final List<String> notificationIds;
        private final Instant creationTime;
        private String recipient;
        private String subject;
        private Optional<Instant> deliveryTime;
        private Optional<String> error;
        private DeliveryStatus status;
        private String taskId;
        
        /**
         * Creates a new BatchNotification with the specified parameters.
         *
         * @param id The batch ID
         * @param events The events in the batch
         * @param notificationIds The IDs of the individual notifications in the batch
         * @param creationTime The time when the batch was created
         */
        BatchNotification(
                String id, List<Map<String, String>> events, List<String> notificationIds,
                Instant creationTime) {
            
            this.id = id;
            this.events = new ArrayList<>(events);
            this.notificationIds = new ArrayList<>(notificationIds);
            this.creationTime = creationTime;
            this.deliveryTime = Optional.empty();
            this.error = Optional.empty();
            this.status = DeliveryStatus.PENDING;
        }
    }
    
    /**
     * Information about a recurring notification.
     */
    public static class RecurringNotification {
        private final String id;
        private final String recipient;
        private final String subject;
        private final String message;
        private final NotificationSeverity severity;
        private final NotificationChannel channel;
        private final ContentFormat format;
        private final Map<String, String> properties;
        private final Instant creationTime;
        private final Duration interval;
        private final AtomicInteger deliveryCount;
        private Optional<Instant> lastDeliveryTime;
        private DeliveryStatus status;
        private String taskId;
        
        /**
         * Creates a new RecurringNotification with the specified parameters.
         *
         * @param id The notification ID
         * @param recipient The recipient
         * @param subject The subject
         * @param message The message
         * @param severity The severity
         * @param channel The channel
         * @param format The content format
         * @param properties Additional properties
         * @param creationTime The time when the notification was created
         * @param interval The interval between notifications
         */
        RecurringNotification(
                String id, String recipient, String subject, String message, 
                NotificationSeverity severity, NotificationChannel channel, 
                ContentFormat format, Map<String, String> properties,
                Instant creationTime, Duration interval) {
            
            this.id = id;
            this.recipient = recipient;
            this.subject = subject;
            this.message = message;
            this.severity = severity;
            this.channel = channel;
            this.format = format;
            this.properties = properties;
            this.creationTime = creationTime;
            this.interval = interval;
            this.deliveryCount = new AtomicInteger(0);
            this.lastDeliveryTime = Optional.empty();
            this.status = DeliveryStatus.SCHEDULED;
        }
    }
}