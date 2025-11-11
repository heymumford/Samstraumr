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

package org.s8r.infrastructure.integration;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.notification.NotificationRequest;
import org.s8r.application.port.task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bridge between the task execution and notification ports.
 * This component monitors tasks and sends notifications based on task status changes.
 */
public class TaskNotificationBridge {
    private final TaskExecutionPort taskExecutor;
    private final NotificationPort notificationPort;
    private final Map<String, Map<String, Object>> registeredNotifications = new ConcurrentHashMap<>();
    private final Timer monitoringTimer = new Timer("TaskNotificationMonitor");
    
    private TaskNotificationBridge(TaskExecutionPort taskExecutor, NotificationPort notificationPort) {
        this.taskExecutor = taskExecutor;
        this.notificationPort = notificationPort;
        startMonitoring();
    }
    
    /**
     * Creates a new instance of the TaskNotificationBridge.
     *
     * @param taskExecutor The task execution port.
     * @param notificationPort The notification port.
     * @return A new TaskNotificationBridge instance.
     */
    public static TaskNotificationBridge create(TaskExecutionPort taskExecutor, NotificationPort notificationPort) {
        return new TaskNotificationBridge(taskExecutor, notificationPort);
    }
    
    private void startMonitoring() {
        // Start a timer to periodically check task status
        monitoringTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkTasks();
            }
        }, 100, 100);
    }
    
    private void checkTasks() {
        for (String taskId : registeredNotifications.keySet()) {
            Task task = taskExecutor.getTask(taskId);
            if (task == null) {
                continue;
            }
            
            Map<String, Object> config = registeredNotifications.get(taskId);
            String type = (String) config.get("type");
            
            switch (type) {
                case "completion":
                    if ("COMPLETED".equals(task.getStatus())) {
                        sendTaskCompletionNotification(task, config);
                    }
                    break;
                case "error":
                    if ("FAILED".equals(task.getStatus())) {
                        sendTaskErrorNotification(task, config);
                    }
                    break;
                case "recurring":
                    if (task.getIterationCount() > 0) {
                        Integer lastNotifiedIteration = (Integer) config.get("lastNotifiedIteration");
                        if (lastNotifiedIteration == null) {
                            lastNotifiedIteration = 0;
                        }
                        
                        if (task.getIterationCount() > lastNotifiedIteration) {
                            sendRecurringTaskNotification(task, config);
                            config.put("lastNotifiedIteration", task.getIterationCount());
                        }
                    }
                    break;
                case "progress":
                    if (task.getProgress() \!= null && task.getProgress().containsKey("percent")) {
                        int currentProgress = ((Number) task.getProgress().get("percent")).intValue();
                        @SuppressWarnings("unchecked")
                        List<Integer> thresholds = (List<Integer>) config.get("thresholds");
                        Integer lastNotifiedThreshold = (Integer) config.get("lastNotifiedThreshold");
                        
                        if (lastNotifiedThreshold == null) {
                            lastNotifiedThreshold = -1;
                        }
                        
                        for (Integer threshold : thresholds) {
                            if (currentProgress >= threshold && threshold > lastNotifiedThreshold) {
                                sendProgressNotification(task, config, threshold);
                                config.put("lastNotifiedThreshold", threshold);
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }
    
    /**
     * Registers a notification to be sent when a task completes.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerTaskCompletionNotification(String taskId, String recipient, String template) {
        return registerTaskCompletionNotification(taskId, recipient, template, null);
    }
    
    /**
     * Registers a notification to be sent when a task completes.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @param options Additional options.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerTaskCompletionNotification(String taskId, String recipient, String template, Map<String, Object> options) {
        Map<String, Object> config = new HashMap<>();
        config.put("type", "completion");
        config.put("recipient", recipient);
        config.put("template", template);
        
        if (options \!= null) {
            config.putAll(options);
        }
        
        registeredNotifications.put(taskId, config);
        return true;
    }
    
    /**
     * Registers a notification to be sent when a task fails.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerTaskErrorNotification(String taskId, String recipient, String template) {
        return registerTaskErrorNotification(taskId, recipient, template, null);
    }
    
    /**
     * Registers a notification to be sent when a task fails.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @param options Additional options.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerTaskErrorNotification(String taskId, String recipient, String template, Map<String, Object> options) {
        Map<String, Object> config = new HashMap<>();
        config.put("type", "error");
        config.put("recipient", recipient);
        config.put("template", template);
        
        if (options \!= null) {
            config.putAll(options);
        }
        
        registeredNotifications.put(taskId, config);
        return true;
    }
    
    /**
     * Registers notifications for recurring task iterations.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerRecurringTaskNotification(String taskId, String recipient, String template) {
        return registerRecurringTaskNotification(taskId, recipient, template, null);
    }
    
    /**
     * Registers notifications for recurring task iterations.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @param options Additional options.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerRecurringTaskNotification(String taskId, String recipient, String template, Map<String, Object> options) {
        Map<String, Object> config = new HashMap<>();
        config.put("type", "recurring");
        config.put("recipient", recipient);
        config.put("template", template);
        config.put("lastNotifiedIteration", 0);
        
        if (options \!= null) {
            config.putAll(options);
        }
        
        registeredNotifications.put(taskId, config);
        return true;
    }
    
    /**
     * Registers notifications for task progress thresholds.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @param thresholds The progress thresholds to notify at.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerProgressNotifications(String taskId, String recipient, String template, List<Integer> thresholds) {
        return registerProgressNotifications(taskId, recipient, template, thresholds, null);
    }
    
    /**
     * Registers notifications for task progress thresholds.
     *
     * @param taskId The task ID.
     * @param recipient The notification recipient.
     * @param template The notification template.
     * @param thresholds The progress thresholds to notify at.
     * @param options Additional options.
     * @return True if the notification was registered, false otherwise.
     */
    public boolean registerProgressNotifications(String taskId, String recipient, String template, List<Integer> thresholds, Map<String, Object> options) {
        Map<String, Object> config = new HashMap<>();
        config.put("type", "progress");
        config.put("recipient", recipient);
        config.put("template", template);
        config.put("thresholds", thresholds);
        config.put("lastNotifiedThreshold", -1);
        
        if (options \!= null) {
            config.putAll(options);
        }
        
        registeredNotifications.put(taskId, config);
        return true;
    }
    
    private void sendTaskCompletionNotification(Task task, Map<String, Object> config) {
        String template = (String) config.get("template");
        String recipient = (String) config.get("recipient");
        String subject = replacePlaceholders(template, task);
        String body = "Task '" + task.getName() + "' has completed successfully.";
        
        // Check if we should retry on failure
        boolean retry = config.containsKey("retry") && (Boolean) config.get("retry");
        int maxRetries = config.containsKey("maxRetries") ? ((Number) config.get("maxRetries")).intValue() : 0;
        int retryCount = config.containsKey("retryCount") ? ((Number) config.get("retryCount")).intValue() : 0;
        
        NotificationRequest request = new NotificationRequest();
        request.setRecipient(recipient);
        request.setSubject(subject);
        request.setBody(body);
        
        if (config.containsKey("deliveryMethod")) {
            request.setDeliveryMethod((String) config.get("deliveryMethod"));
        }
        
        // Send notification
        var result = notificationPort.sendNotification(request);
        
        // Handle retry if needed
        if (\!result.isSuccess() && retry && retryCount < maxRetries) {
            config.put("retryCount", retryCount + 1);
            
            // Schedule a retry
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendTaskCompletionNotification(task, config);
                }
            }, 200 * (retryCount + 1)); // Exponential backoff
        }
    }
    
    private void sendTaskErrorNotification(Task task, Map<String, Object> config) {
        String template = (String) config.get("template");
        String recipient = (String) config.get("recipient");
        String subject = replacePlaceholders(template, task);
        String body = "Task '" + task.getName() + "' has failed.\n\nError: " + task.getErrorDetails();
        
        NotificationRequest request = new NotificationRequest();
        request.setRecipient(recipient);
        request.setSubject(subject);
        request.setBody(body);
        
        if (config.containsKey("deliveryMethod")) {
            request.setDeliveryMethod((String) config.get("deliveryMethod"));
        }
        
        notificationPort.sendNotification(request);
    }
    
    private void sendRecurringTaskNotification(Task task, Map<String, Object> config) {
        String template = (String) config.get("template");
        String recipient = (String) config.get("recipient");
        String subject = replacePlaceholders(template, task);
        String body = "Recurring task '" + task.getName() + "' has completed iteration " + task.getIterationCount() + ".";
        
        NotificationRequest request = new NotificationRequest();
        request.setRecipient(recipient);
        request.setSubject(subject);
        request.setBody(body);
        
        if (config.containsKey("deliveryMethod")) {
            request.setDeliveryMethod((String) config.get("deliveryMethod"));
        }
        
        notificationPort.sendNotification(request);
    }
    
    private void sendProgressNotification(Task task, Map<String, Object> config, int threshold) {
        String template = (String) config.get("template");
        String recipient = (String) config.get("recipient");
        String subject = "Task Progress: " + task.getName();
        String body = replacePlaceholders(template, task);
        
        NotificationRequest request = new NotificationRequest();
        request.setRecipient(recipient);
        request.setSubject(subject);
        request.setBody(body);
        
        if (config.containsKey("deliveryMethod")) {
            request.setDeliveryMethod((String) config.get("deliveryMethod"));
        }
        
        notificationPort.sendNotification(request);
    }
    
    private String replacePlaceholders(String template, Task task) {
        String result = template;
        
        // Replace task properties
        result = result.replace("#{taskId}", task.getId());
        result = result.replace("#{taskName}", task.getName());
        result = result.replace("#{taskStatus}", task.getStatus());
        
        // Replace iteration count if available
        if (task.getIterationCount() > 0) {
            result = result.replace("#{iterationCount}", String.valueOf(task.getIterationCount()));
        }
        
        // Replace progress if available
        if (task.getProgress() \!= null && task.getProgress().containsKey("percent")) {
            int progressPercent = ((Number) task.getProgress().get("percent")).intValue();
            result = result.replace("#{progressPercent}", String.valueOf(progressPercent));
        }
        
        return result;
    }
}
