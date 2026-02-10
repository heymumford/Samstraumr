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

package org.s8r.application.port.compat;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.task.TaskResult;
import org.s8r.application.port.task.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Compatibility class that provides methods to bridge between the different TaskExecutionPort APIs.
 * This class exists to ease transition between the older API used in tests and the newer API in the core module.
 */
public class TaskExecutionPortCompat {

    /**
     * Converts a task result to a string representation of success/failure.
     *
     * @param result The task result
     * @return "success" if the result is successful, "failure" otherwise
     */
    public static String resultToString(TaskResult<?> result) {
        return result.isSuccessful() ? "success" : "failure";
    }
    
    /**
     * Creates a task result that represents a successful operation.
     *
     * @param <T> The type of result
     * @param taskId The task ID
     * @param message The result message
     * @return A successful task result
     */
    public static <T> TaskResult<T> createSuccessResult(String taskId, String message) {
        return TaskResult.success(taskId, message);
    }
    
    /**
     * Creates a task result that represents a failed operation.
     *
     * @param <T> The type of result
     * @param taskId The task ID
     * @param message The error message
     * @param reason The reason for the failure
     * @return A failed task result
     */
    public static <T> TaskResult<T> createFailureResult(String taskId, String message, String reason) {
        return TaskResult.failure(taskId, message, reason);
    }
    
    /**
     * Schedules a task for execution at the specified time.
     *
     * @param <T> The type of task result
     * @param port The task execution port
     * @param callable The task to execute
     * @param executionTime The time at which to execute the task
     * @return A task result representing the scheduling outcome
     */
    public static <T> TaskResult<T> scheduleTask(
            TaskExecutionPort port, 
            Callable<T> callable, 
            Instant executionTime) {
        
        Duration delay = Duration.between(Instant.now(), executionTime);
        if (delay.isNegative()) {
            delay = Duration.ZERO;
        }
        
        return port.scheduleTask(callable, delay);
    }
    
    /**
     * Schedules a task for execution after the specified delay.
     *
     * @param <T> The type of task result
     * @param port The task execution port
     * @param callable The task to execute
     * @param delayMillis The delay in milliseconds
     * @return A task result representing the scheduling outcome
     */
    public static <T> TaskResult<T> scheduleTaskWithDelay(
            TaskExecutionPort port, 
            Callable<T> callable, 
            long delayMillis) {
        
        return port.scheduleTask(callable, Duration.ofMillis(delayMillis));
    }
    
    /**
     * Executes a task asynchronously and registers a completion listener.
     *
     * @param <T> The type of task result
     * @param port The task execution port
     * @param callable The task to execute
     * @param listener The listener to call when the task completes
     * @return A Future representing the task result
     */
    public static <T> Future<T> executeAsyncWithListener(
            TaskExecutionPort port, 
            Callable<T> callable, 
            Consumer<TaskResult<T>> listener) {
        
        Future<T> future = port.executeAsync(callable);
        
        // Create a CompletableFuture that wraps the original Future and notifies the listener
        CompletableFuture<T> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                T result = future.get();
                listener.accept(TaskResult.success(null, "Task completed successfully", Map.of("result", result)));
                return result;
            } catch (Exception e) {
                listener.accept(TaskResult.failure(null, "Task failed", e.getMessage()));
                throw new RuntimeException(e);
            }
        });
        
        return completableFuture;
    }
}