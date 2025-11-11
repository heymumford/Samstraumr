/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.task;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.TaskResult;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Thread pool implementation of the TaskExecutionPort interface.
 * 
 * <p>This adapter provides task execution services using Java's 
 * ScheduledExecutorService for both immediate and delayed execution.
 */
public class ThreadPoolTaskExecutionAdapter implements TaskExecutionPort {
    
    private final ScheduledExecutorService executor;
    private final Map<String, TaskInfo<?>> taskRegistry = new ConcurrentHashMap<>();
    
    /**
     * Creates a new ThreadPoolTaskExecutionAdapter with the default thread pool size.
     */
    public ThreadPoolTaskExecutionAdapter() {
        this(Runtime.getRuntime().availableProcessors());
    }
    
    /**
     * Creates a new ThreadPoolTaskExecutionAdapter with the specified thread pool size.
     *
     * @param poolSize The number of threads in the pool
     */
    public ThreadPoolTaskExecutionAdapter(int poolSize) {
        this.executor = Executors.newScheduledThreadPool(poolSize);
    }
    
    @Override
    public <T> Future<T> executeAsync(Callable<T> task) {
        String taskId = generateTaskId();
        Future<T> future = executor.submit(task);
        taskRegistry.put(taskId, new TaskInfo<>(taskId, future, TaskStatus.RUNNING));
        
        // Update task status when completed
        CompletableFuture.runAsync(() -> {
            try {
                future.get();
                updateTaskStatus(taskId, TaskStatus.COMPLETED);
            } catch (CancellationException e) {
                updateTaskStatus(taskId, TaskStatus.CANCELED);
            } catch (Exception e) {
                updateTaskStatus(taskId, TaskStatus.FAILED);
            }
        });
        
        return future;
    }
    
    @Override
    public <T> Future<T> executeAsync(Callable<T> task, Duration timeout) {
        String taskId = generateTaskId();
        Future<T> future = executor.submit(task);
        taskRegistry.put(taskId, new TaskInfo<>(taskId, future, TaskStatus.RUNNING));
        
        // Set up timeout handling
        ScheduledFuture<?> timeoutFuture = executor.schedule(() -> {
            if (!future.isDone() && !future.isCancelled()) {
                future.cancel(true);
                updateTaskStatus(taskId, TaskStatus.TIMEOUT);
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS);
        
        // Update task status when completed
        CompletableFuture.runAsync(() -> {
            try {
                future.get();
                timeoutFuture.cancel(false);
                updateTaskStatus(taskId, TaskStatus.COMPLETED);
            } catch (CancellationException e) {
                updateTaskStatus(taskId, TaskStatus.CANCELED);
            } catch (Exception e) {
                timeoutFuture.cancel(false);
                updateTaskStatus(taskId, TaskStatus.FAILED);
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Void> executeAsync(Runnable task) {
        String taskId = generateTaskId();
        CompletableFuture<Void> future = CompletableFuture.runAsync(task, executor);
        taskRegistry.put(taskId, new TaskInfo<>(taskId, future, TaskStatus.RUNNING));
        
        // Update task status when completed
        future.whenComplete((result, exception) -> {
            if (exception != null) {
                if (exception instanceof CancellationException) {
                    updateTaskStatus(taskId, TaskStatus.CANCELED);
                } else {
                    updateTaskStatus(taskId, TaskStatus.FAILED);
                }
            } else {
                updateTaskStatus(taskId, TaskStatus.COMPLETED);
            }
        });
        
        return future;
    }
    
    @Override
    public <T> TaskResult<T> scheduleTask(Callable<T> task, Duration delay) {
        String taskId = generateTaskId();
        
        ScheduledFuture<T> future = executor.schedule(task, delay.toMillis(), TimeUnit.MILLISECONDS);
        taskRegistry.put(taskId, new TaskInfo<>(taskId, future, TaskStatus.SCHEDULED));
        
        // Update task status when the scheduled time arrives
        executor.schedule(() -> {
            if (!future.isCancelled()) {
                updateTaskStatus(taskId, TaskStatus.RUNNING);
                
                CompletableFuture.runAsync(() -> {
                    try {
                        future.get();
                        updateTaskStatus(taskId, TaskStatus.COMPLETED);
                    } catch (CancellationException e) {
                        updateTaskStatus(taskId, TaskStatus.CANCELED);
                    } catch (Exception e) {
                        updateTaskStatus(taskId, TaskStatus.FAILED);
                    }
                }, executor);
            }
        }, delay.toMillis(), TimeUnit.MILLISECONDS);
        
        return TaskResult.scheduled(taskId, "Task scheduled successfully");
    }
    
    @Override
    public TaskResult<Void> scheduleTask(Runnable task, Duration delay) {
        String taskId = generateTaskId();
        
        ScheduledFuture<?> future = executor.schedule(task, delay.toMillis(), TimeUnit.MILLISECONDS);
        taskRegistry.put(taskId, new TaskInfo<>(taskId, future, TaskStatus.SCHEDULED));
        
        // Update task status when the scheduled time arrives
        executor.schedule(() -> {
            if (!future.isCancelled()) {
                updateTaskStatus(taskId, TaskStatus.RUNNING);
                
                CompletableFuture.runAsync(() -> {
                    try {
                        future.get();
                        updateTaskStatus(taskId, TaskStatus.COMPLETED);
                    } catch (CancellationException e) {
                        updateTaskStatus(taskId, TaskStatus.CANCELED);
                    } catch (Exception e) {
                        updateTaskStatus(taskId, TaskStatus.FAILED);
                    }
                }, executor);
            }
        }, delay.toMillis(), TimeUnit.MILLISECONDS);
        
        return TaskResult.scheduled(taskId, "Task scheduled successfully");
    }
    
    @Override
    public boolean cancelTask(String taskId) {
        TaskInfo<?> taskInfo = taskRegistry.get(taskId);
        if (taskInfo == null) {
            return false;
        }
        
        boolean cancelled = taskInfo.future.cancel(true);
        if (cancelled) {
            updateTaskStatus(taskId, TaskStatus.CANCELED);
        }
        
        return cancelled;
    }
    
    @Override
    public TaskResult<Void> getTaskStatus(String taskId) {
        TaskInfo<?> taskInfo = taskRegistry.get(taskId);
        if (taskInfo == null) {
            return TaskResult.failure(
                    taskId, 
                    TaskStatus.FAILED, 
                    "Task not found", 
                    "No task with the specified ID exists"
            );
        }
        
        return TaskResult.success(
                taskId, 
                null, 
                "Task status retrieved successfully", 
                Map.of("status", taskInfo.status)
        );
    }
    
    @Override
    public TaskResult<Void> shutdown(boolean waitForTasks) {
        try {
            if (waitForTasks) {
                executor.shutdown();
                boolean terminated = executor.awaitTermination(60, TimeUnit.SECONDS);
                if (terminated) {
                    return TaskResult.success(null, null, "Executor shut down successfully");
                } else {
                    return TaskResult.failure(
                            null, 
                            TaskStatus.FAILED, 
                            "Executor shutdown timed out", 
                            "Not all tasks completed within the timeout period"
                    );
                }
            } else {
                List<Runnable> pendingTasks = executor.shutdownNow();
                
                // Cancel any registered tasks
                for (TaskInfo<?> taskInfo : taskRegistry.values()) {
                    if (!taskInfo.future.isDone() && !taskInfo.future.isCancelled()) {
                        taskInfo.future.cancel(true);
                        updateTaskStatus(taskInfo.id, TaskStatus.CANCELED);
                    }
                }
                
                return TaskResult.success(null, null, "Executor shut down immediately, " + pendingTasks.size() + " tasks were canceled");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TaskResult.failure(
                    null, 
                    TaskStatus.FAILED, 
                    "Executor shutdown interrupted", 
                    e.getMessage()
            );
        }
    }
    
    /**
     * Generates a unique task ID.
     *
     * @return A unique task ID
     */
    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Updates the status of a task in the registry.
     *
     * @param taskId The ID of the task
     * @param status The new status of the task
     */
    private void updateTaskStatus(String taskId, TaskStatus status) {
        TaskInfo<?> taskInfo = taskRegistry.get(taskId);
        if (taskInfo != null) {
            taskInfo.status = status;
        }
    }
    
    /**
     * Information about a task in the registry.
     *
     * @param <T> The type of the task result
     */
    private static class TaskInfo<T> {
        private final String id;
        private final Future<T> future;
        private volatile TaskStatus status;
        
        /**
         * Creates a new TaskInfo with the specified parameters.
         *
         * @param id The ID of the task
         * @param future The Future representing the task
         * @param status The initial status of the task
         */
        TaskInfo(String id, Future<T> future, TaskStatus status) {
            this.id = id;
            this.future = future;
            this.status = status;
        }
    }
}