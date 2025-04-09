/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Port interface for task execution operations in the application layer.
 * 
 * <p>This interface defines the operations for executing tasks asynchronously,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface TaskExecutionPort {
    
    /**
     * Executes a task asynchronously.
     *
     * @param task The task to execute
     * @param <T> The type of the task result
     * @return A Future representing the pending completion of the task
     */
    <T> Future<T> executeAsync(Callable<T> task);
    
    /**
     * Executes a task asynchronously with a timeout.
     *
     * @param task The task to execute
     * @param timeout The maximum time to wait for the task to complete
     * @param <T> The type of the task result
     * @return A Future representing the pending completion of the task
     */
    <T> Future<T> executeAsync(Callable<T> task, Duration timeout);
    
    /**
     * Executes a runnable task asynchronously.
     *
     * @param task The task to execute
     * @return A CompletableFuture representing the pending completion of the task
     */
    CompletableFuture<Void> executeAsync(Runnable task);
    
    /**
     * Schedules a task for execution after a delay.
     *
     * @param task The task to execute
     * @param delay The delay after which to execute the task
     * @param <T> The type of the task result
     * @return A TaskResult representing the result of the scheduling operation
     */
    <T> TaskResult<T> scheduleTask(Callable<T> task, Duration delay);
    
    /**
     * Schedules a runnable task for execution after a delay.
     *
     * @param task The task to execute
     * @param delay The delay after which to execute the task
     * @return A TaskResult representing the result of the scheduling operation
     */
    TaskResult<Void> scheduleTask(Runnable task, Duration delay);
    
    /**
     * Cancels a scheduled task.
     *
     * @param taskId The ID of the task to cancel
     * @return true if the task was canceled successfully, false otherwise
     */
    boolean cancelTask(String taskId);
    
    /**
     * Gets the status of a task.
     *
     * @param taskId The ID of the task
     * @return A TaskResult containing the status of the task
     */
    TaskResult<Void> getTaskStatus(String taskId);
    
    /**
     * Shuts down the task executor.
     *
     * @param waitForTasks Whether to wait for tasks to complete before shutting down
     * @return A TaskResult representing the result of the shutdown operation
     */
    TaskResult<Void> shutdown(boolean waitForTasks);
    
    /**
     * Represents the result of a task execution or scheduling operation.
     *
     * @param <T> The type of the task result
     */
    interface TaskResult<T> {
        /**
         * @return true if the operation was successful, false otherwise
         */
        boolean isSuccessful();
        
        /**
         * @return The ID of the task
         */
        String getTaskId();
        
        /**
         * @return The status of the task
         */
        TaskStatus getStatus();
        
        /**
         * @return The result of the task, if available
         */
        Optional<T> getResult();
        
        /**
         * @return A message describing the result of the operation
         */
        String getMessage();
        
        /**
         * @return An optional reason for failure if the operation was not successful
         */
        Optional<String> getReason();
        
        /**
         * @return Additional attributes associated with the task result
         */
        default Map<String, Object> getAttributes() {
            return Collections.emptyMap();
        }
        
        /**
         * Creates a successful TaskResult with the given result.
         * 
         * @param <T> The type of the task result
         * @param taskId The ID of the task
         * @param result The result of the task
         * @param message A message describing the result
         * @return A successful TaskResult
         */
        static <T> TaskResult<T> success(String taskId, T result, String message) {
            return new SimpleTaskResult<>(true, taskId, TaskStatus.COMPLETED, result, message, null, null);
        }
        
        /**
         * Creates a successful TaskResult with the given result and attributes.
         * 
         * @param <T> The type of the task result
         * @param taskId The ID of the task
         * @param result The result of the task
         * @param message A message describing the result
         * @param attributes Additional attributes associated with the task result
         * @return A successful TaskResult
         */
        static <T> TaskResult<T> success(String taskId, T result, String message, Map<String, Object> attributes) {
            return new SimpleTaskResult<>(true, taskId, TaskStatus.COMPLETED, result, message, null, attributes);
        }
        
        /**
         * Creates a successful TaskResult for a scheduled task.
         * 
         * @param <T> The type of the task result
         * @param taskId The ID of the task
         * @param message A message describing the result
         * @return A successful TaskResult
         */
        static <T> TaskResult<T> scheduled(String taskId, String message) {
            return new SimpleTaskResult<>(true, taskId, TaskStatus.SCHEDULED, null, message, null, null);
        }
        
        /**
         * Creates a failure TaskResult with the given reason.
         * 
         * @param <T> The type of the task result
         * @param taskId The ID of the task, if available
         * @param status The status of the task
         * @param message A message describing the failure
         * @param reason The reason for the failure
         * @return A failure TaskResult
         */
        static <T> TaskResult<T> failure(String taskId, TaskStatus status, String message, String reason) {
            return new SimpleTaskResult<>(false, taskId, status, null, message, reason, null);
        }
        
        /**
         * Creates a failure TaskResult with the given reason and attributes.
         * 
         * @param <T> The type of the task result
         * @param taskId The ID of the task, if available
         * @param status The status of the task
         * @param message A message describing the failure
         * @param reason The reason for the failure
         * @param attributes Additional attributes associated with the task result
         * @return A failure TaskResult
         */
        static <T> TaskResult<T> failure(
                String taskId, TaskStatus status, String message, String reason, Map<String, Object> attributes) {
            return new SimpleTaskResult<>(false, taskId, status, null, message, reason, attributes);
        }
    }
    
    /**
     * Simple implementation of the TaskResult interface.
     * 
     * @param <T> The type of the task result
     */
    class SimpleTaskResult<T> implements TaskResult<T> {
        private final boolean successful;
        private final String taskId;
        private final TaskStatus status;
        private final T result;
        private final String message;
        private final String reason;
        private final Map<String, Object> attributes;
        
        /**
         * Creates a new SimpleTaskResult with the specified parameters.
         *
         * @param successful Whether the operation was successful
         * @param taskId The ID of the task
         * @param status The status of the task
         * @param result The result of the task
         * @param message The message describing the result
         * @param reason The reason for failure, if applicable
         * @param attributes Additional attributes associated with the task result
         */
        SimpleTaskResult(
                boolean successful, 
                String taskId, 
                TaskStatus status, 
                T result, 
                String message, 
                String reason, 
                Map<String, Object> attributes) {
            this.successful = successful;
            this.taskId = taskId;
            this.status = status;
            this.result = result;
            this.message = message;
            this.reason = reason;
            this.attributes = attributes != null ? Collections.unmodifiableMap(attributes) : Collections.emptyMap();
        }
        
        @Override
        public boolean isSuccessful() {
            return successful;
        }
        
        @Override
        public String getTaskId() {
            return taskId;
        }
        
        @Override
        public TaskStatus getStatus() {
            return status;
        }
        
        @Override
        public Optional<T> getResult() {
            return Optional.ofNullable(result);
        }
        
        @Override
        public String getMessage() {
            return message;
        }
        
        @Override
        public Optional<String> getReason() {
            return Optional.ofNullable(reason);
        }
        
        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }
    
    /**
     * Enumeration of possible task statuses.
     */
    enum TaskStatus {
        /**
         * The task is waiting to be executed.
         */
        PENDING,
        
        /**
         * The task has been scheduled for future execution.
         */
        SCHEDULED,
        
        /**
         * The task is currently being executed.
         */
        RUNNING,
        
        /**
         * The task has completed successfully.
         */
        COMPLETED,
        
        /**
         * The task failed due to an exception.
         */
        FAILED,
        
        /**
         * The task was canceled before completion.
         */
        CANCELED,
        
        /**
         * The task timed out before completion.
         */
        TIMEOUT
    }
}