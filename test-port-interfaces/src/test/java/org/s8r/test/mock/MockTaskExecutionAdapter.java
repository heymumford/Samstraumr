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

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.task.Task;
import org.s8r.application.port.task.TaskResult;
import org.s8r.application.port.task.TaskScheduleResult;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of TaskExecutionPort for testing.
 */
public class MockTaskExecutionAdapter implements TaskExecutionPort {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();
    private final Timer scheduler = new Timer("MockTaskScheduler");
    
    // Configuration options
    private boolean simulateDelay = true;
    private long delayMilliseconds = 500;
    private boolean simulateErrors = false;
    private double errorRate = 0.2;
    private boolean simulateProgress = false;
    
    /**
     * Creates a new instance of the MockTaskExecutionAdapter.
     *
     * @return A new MockTaskExecutionAdapter instance.
     */
    public static MockTaskExecutionAdapter createInstance() {
        return new MockTaskExecutionAdapter();
    }
    
    /**
     * Configures the adapter with the given settings.
     *
     * @param settings Map of configuration settings.
     */
    public void configure(Map<String, Object> settings) {
        if (settings.containsKey("simulateDelay")) {
            this.simulateDelay = (Boolean) settings.get("simulateDelay");
        }
        if (settings.containsKey("delayMilliseconds")) {
            this.delayMilliseconds = ((Number) settings.get("delayMilliseconds")).longValue();
        }
        if (settings.containsKey("simulateErrors")) {
            this.simulateErrors = (Boolean) settings.get("simulateErrors");
        }
        if (settings.containsKey("errorRate")) {
            this.errorRate = ((Number) settings.get("errorRate")).doubleValue();
        }
        if (settings.containsKey("simulateProgress")) {
            this.simulateProgress = (Boolean) settings.get("simulateProgress");
        }
    }

    @Override
    public TaskScheduleResult scheduleTask(String taskId, String name, String description, Object scheduledTime) {
        Task task = new Task();
        task.setId(taskId);
        task.setName(name);
        task.setDescription(description);
        task.setStatus("SCHEDULED");
        task.setCreatedAt(Instant.now().toString());
        
        // Handle different types of scheduled times
        if (scheduledTime instanceof String) {
            task.setScheduledAt((String) scheduledTime);
        } else if (scheduledTime instanceof Number) {
            long delayMs = ((Number) scheduledTime).longValue();
            task.setScheduledAt(Instant.now().plusMillis(delayMs).toString());
        } else {
            task.setScheduledAt(Instant.now().toString());
        }
        
        tasks.put(taskId, task);
        
        // If we should simulate execution delay
        if (simulateDelay) {
            long delay = 0;
            
            // Check if we need to calculate delay from scheduled time
            if (scheduledTime instanceof String) {
                try {
                    Instant scheduledInstant = Instant.parse((String) scheduledTime);
                    delay = Duration.between(Instant.now(), scheduledInstant).toMillis();
                    if (delay < 0) {
                        delay = 0;
                    }
                } catch (Exception e) {
                    delay = delayMilliseconds;
                }
            } else if (scheduledTime instanceof Number) {
                delay = ((Number) scheduledTime).longValue();
            } else {
                delay = delayMilliseconds;
            }
            
            // Schedule execution
            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    executeTask(taskId);
                }
            }, delay);
        }
        
        return new TaskScheduleResult(true, taskId, null);
    }
    
    private void executeTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return;
        }
        
        // Update status to running
        task.setStatus("RUNNING");
        task.setStartedAt(Instant.now().toString());
        tasks.put(taskId, task);
        
        // Simulate progress updates if enabled
        if (simulateProgress) {
            for (int i = 0; i <= 100; i += 5) {
                final int progress = i;
                scheduler.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Task progressTask = tasks.get(taskId);
                        if (progressTask \!= null && "RUNNING".equals(progressTask.getStatus())) {
                            Map<String, Object> progressData = new HashMap<>();
                            progressData.put("percent", progress);
                            progressTask.setProgress(progressData);
                            tasks.put(taskId, progressTask);
                        }
                    }
                }, i * 20); // Spread progress updates over time
            }
        }
        
        // Check if we should simulate an error
        if (simulateErrors && Math.random() < errorRate) {
            // Simulate error
            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    Task errorTask = tasks.get(taskId);
                    if (errorTask \!= null) {
                        errorTask.setStatus("FAILED");
                        errorTask.setCompletedAt(Instant.now().toString());
                        errorTask.setErrorDetails("Simulated task execution error");
                        tasks.put(taskId, errorTask);
                    }
                }
            }, delayMilliseconds);
        } else {
            // Simulate successful completion
            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    Task completedTask = tasks.get(taskId);
                    if (completedTask \!= null) {
                        completedTask.setStatus("COMPLETED");
                        completedTask.setCompletedAt(Instant.now().toString());
                        completedTask.setResult(new TaskResult(true, "Task completed successfully", null));
                        tasks.put(taskId, completedTask);
                    }
                }
            }, delayMilliseconds);
        }
    }

    @Override
    public TaskScheduleResult scheduleRecurringTask(String taskId, String name, String description, Object recurringConfig) {
        Task task = new Task();
        task.setId(taskId);
        task.setName(name);
        task.setDescription(description);
        task.setStatus("SCHEDULED");
        task.setCreatedAt(Instant.now().toString());
        task.setScheduledAt(Instant.now().toString());
        task.setRecurring(true);
        
        // Extract recurring configuration
        Map<String, Object> config = (Map<String, Object>) recurringConfig;
        String interval = (String) config.get("interval");
        int count = config.containsKey("count") ? ((Number) config.get("count")).intValue() : 1;
        
        task.setRecurringConfig(config);
        tasks.put(taskId, task);
        
        // Schedule recurring executions
        for (int i = 0; i < count; i++) {
            final int iteration = i + 1;
            
            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Create a copy of the task for this iteration
                    String iterationTaskId = taskId + "-" + iteration;
                    Task iterationTask = new Task();
                    iterationTask.setId(iterationTaskId);
                    iterationTask.setName(name + " (Iteration " + iteration + ")");
                    iterationTask.setDescription(description);
                    iterationTask.setStatus("RUNNING");
                    iterationTask.setCreatedAt(Instant.now().toString());
                    iterationTask.setStartedAt(Instant.now().toString());
                    iterationTask.setParentTaskId(taskId);
                    iterationTask.setIterationCount(iteration);
                    
                    tasks.put(iterationTaskId, iterationTask);
                    
                    // Simulate completion
                    scheduler.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Task completedTask = tasks.get(iterationTaskId);
                            if (completedTask \!= null) {
                                completedTask.setStatus("COMPLETED");
                                completedTask.setCompletedAt(Instant.now().toString());
                                completedTask.setResult(new TaskResult(true, "Task iteration completed", null));
                                tasks.put(iterationTaskId, completedTask);
                                
                                // Update parent task progress
                                Task parentTask = tasks.get(taskId);
                                if (parentTask \!= null) {
                                    Map<String, Object> progress = new HashMap<>();
                                    progress.put("completedIterations", iteration);
                                    progress.put("totalIterations", count);
                                    progress.put("percent", (iteration * 100) / count);
                                    parentTask.setProgress(progress);
                                    
                                    if (iteration == count) {
                                        parentTask.setStatus("COMPLETED");
                                        parentTask.setCompletedAt(Instant.now().toString());
                                    }
                                    
                                    tasks.put(taskId, parentTask);
                                }
                            }
                        }
                    }, delayMilliseconds);
                }
            }, i * parseInterval(interval));
        }
        
        return new TaskScheduleResult(true, taskId, null);
    }
    
    private long parseInterval(String interval) {
        // Simple interval parser for duration strings like PT0.5S (0.5 seconds)
        if (interval.startsWith("PT")) {
            String value = interval.substring(2, interval.length() - 1);
            String unit = interval.substring(interval.length() - 1);
            
            double numericValue = Double.parseDouble(value);
            
            switch (unit) {
                case "S":
                    return (long) (numericValue * 1000);
                case "M":
                    return (long) (numericValue * 60 * 1000);
                case "H":
                    return (long) (numericValue * 60 * 60 * 1000);
                default:
                    return 1000;
            }
        }
        
        return 1000; // Default to 1 second
    }

    @Override
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public boolean cancelTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        task.setStatus("CANCELLED");
        task.setCompletedAt(Instant.now().toString());
        tasks.put(taskId, task);
        
        return true;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || \!tasks.containsKey(task.getId())) {
            return false;
        }
        
        tasks.put(task.getId(), task);
        return true;
    }
    
    /**
     * Updates the progress of a task.
     *
     * @param taskId The task ID.
     * @param progress The progress value (0-100).
     * @return True if the task was updated, false otherwise.
     */
    public boolean updateTaskProgress(String taskId, int progress) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return false;
        }
        
        Map<String, Object> progressData = new HashMap<>();
        progressData.put("percent", progress);
        task.setProgress(progressData);
        
        if (progress >= 100) {
            task.setStatus("COMPLETED");
            task.setCompletedAt(Instant.now().toString());
        }
        
        tasks.put(taskId, task);
        return true;
    }
}
