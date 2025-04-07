/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.Task;
import org.s8r.application.port.TaskExecutionPort.TaskOptions;
import org.s8r.application.port.TaskExecutionPort.TaskOptionsBuilder;
import org.s8r.application.port.TaskExecutionPort.TaskPriority;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Service class that provides task execution capabilities to the application layer.
 * <p>
 * This service uses the TaskExecutionPort to interact with the task execution infrastructure.
 * It provides a simplified interface for common task operations and adds additional
 * business logic as needed.
 */
public class TaskExecutionService {

    private final TaskExecutionPort taskExecutionPort;
    private final LoggerPort logger;

    /**
     * Creates a new TaskExecutionService with the specified dependencies.
     *
     * @param taskExecutionPort The task execution port implementation to use
     * @param logger            The logger to use for logging
     */
    public TaskExecutionService(TaskExecutionPort taskExecutionPort, LoggerPort logger) {
        this.taskExecutionPort = taskExecutionPort;
        this.logger = logger;
    }

    /**
     * Initializes the task execution service.
     *
     * @return True if initialization was successful, false otherwise
     */
    public boolean initialize() {
        logger.info("Initializing task execution service");
        TaskExecutionPort.TaskResult result = taskExecutionPort.initialize();
        if (!result.isSuccessful()) {
            logger.error("Failed to initialize task execution service: {}", result.getReason().orElse("Unknown reason"));
            return false;
        }
        return true;
    }

    /**
     * Creates a task options builder.
     *
     * @return A new task options builder
     */
    public TaskOptionsBuilder createOptionsBuilder() {
        return taskExecutionPort.createTaskOptionsBuilder();
    }

    /**
     * Executes a runnable task.
     *
     * @param runnable The runnable to execute
     * @return The task ID, or empty if the task could not be submitted
     */
    public Optional<String> executeTask(Runnable runnable) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.submitTask(runnable);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to execute task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Executes a runnable task with options.
     *
     * @param runnable The runnable to execute
     * @param options  The task options
     * @return The task ID, or empty if the task could not be submitted
     */
    public Optional<String> executeTask(Runnable runnable, TaskOptions options) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.submitTask(runnable, options);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to execute task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Executes a callable task.
     *
     * @param <T>      The type of the task result
     * @param callable The callable to execute
     * @return The task ID, or empty if the task could not be submitted
     */
    public <T> Optional<String> executeTask(Callable<T> callable) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.submitTask(callable);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to execute task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Executes a callable task with options.
     *
     * @param <T>      The type of the task result
     * @param callable The callable to execute
     * @param options  The task options
     * @return The task ID, or empty if the task could not be submitted
     */
    public <T> Optional<String> executeTask(Callable<T> callable, TaskOptions options) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.submitTask(callable, options);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to execute task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Schedules a runnable task for future execution.
     *
     * @param runnable      The runnable to execute
     * @param executionTime The time to execute the task
     * @return The task ID, or empty if the task could not be scheduled
     */
    public Optional<String> scheduleTask(Runnable runnable, Instant executionTime) {
        if (executionTime.isBefore(Instant.now())) {
            logger.warn("Cannot schedule task in the past: {}", executionTime);
            return Optional.empty();
        }

        TaskExecutionPort.TaskResult result = taskExecutionPort.scheduleTask(runnable, executionTime);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to schedule task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Schedules a runnable task for future execution with options.
     *
     * @param runnable      The runnable to execute
     * @param executionTime The time to execute the task
     * @param options       The task options
     * @return The task ID, or empty if the task could not be scheduled
     */
    public Optional<String> scheduleTask(Runnable runnable, Instant executionTime, TaskOptions options) {
        if (executionTime.isBefore(Instant.now())) {
            logger.warn("Cannot schedule task in the past: {}", executionTime);
            return Optional.empty();
        }

        TaskExecutionPort.TaskResult result = taskExecutionPort.scheduleTask(runnable, executionTime, options);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to schedule task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Schedules a callable task for future execution.
     *
     * @param <T>           The type of the task result
     * @param callable      The callable to execute
     * @param executionTime The time to execute the task
     * @return The task ID, or empty if the task could not be scheduled
     */
    public <T> Optional<String> scheduleTask(Callable<T> callable, Instant executionTime) {
        if (executionTime.isBefore(Instant.now())) {
            logger.warn("Cannot schedule task in the past: {}", executionTime);
            return Optional.empty();
        }

        TaskExecutionPort.TaskResult result = taskExecutionPort.scheduleTask(callable, executionTime);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to schedule task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Schedules a callable task for future execution with options.
     *
     * @param <T>           The type of the task result
     * @param callable      The callable to execute
     * @param executionTime The time to execute the task
     * @param options       The task options
     * @return The task ID, or empty if the task could not be scheduled
     */
    public <T> Optional<String> scheduleTask(Callable<T> callable, Instant executionTime, TaskOptions options) {
        if (executionTime.isBefore(Instant.now())) {
            logger.warn("Cannot schedule task in the past: {}", executionTime);
            return Optional.empty();
        }

        TaskExecutionPort.TaskResult result = taskExecutionPort.scheduleTask(callable, executionTime, options);
        if (result.isSuccessful()) {
            return result.getTaskId();
        } else {
            logger.warn("Failed to schedule task: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Gets a task by ID.
     *
     * @param taskId The task ID
     * @return The task, or empty if not found
     */
    public Optional<Task> getTask(String taskId) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.getTask(taskId);
        if (result.isSuccessful()) {
            return Optional.ofNullable((Task) result.getAttributes().get("task"));
        } else {
            if (result.getReason().isPresent() && !result.getReason().get().contains("not found")) {
                logger.warn("Failed to get task {}: {}", taskId, result.getReason().orElse("Unknown reason"));
            }
            return Optional.empty();
        }
    }

    /**
     * Cancels a task.
     *
     * @param taskId The task ID
     * @return True if the task was cancelled successfully, false otherwise
     */
    public boolean cancelTask(String taskId) {
        return cancelTask(taskId, true);
    }

    /**
     * Cancels a task.
     *
     * @param taskId              The task ID
     * @param mayInterruptIfRunning Whether to interrupt the task if running
     * @return True if the task was cancelled successfully, false otherwise
     */
    public boolean cancelTask(String taskId, boolean mayInterruptIfRunning) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.cancelTask(taskId, mayInterruptIfRunning);
        if (!result.isSuccessful()) {
            logger.warn("Failed to cancel task {}: {}", taskId, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Gets all tasks.
     *
     * @return A list of tasks, or an empty list if none found
     */
    public List<Task> getAllTasks() {
        TaskExecutionPort.TaskResult result = taskExecutionPort.getAllTasks();
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<Task> tasks = (List<Task>) result.getAttributes().get("tasks");
            return tasks != null ? tasks : List.of();
        } else {
            logger.warn("Failed to get all tasks: {}", result.getReason().orElse("Unknown reason"));
            return List.of();
        }
    }

    /**
     * Gets tasks by status.
     *
     * @param status The task status
     * @return A list of tasks with the specified status, or an empty list if none found
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.getTasksByStatus(status);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<Task> tasks = (List<Task>) result.getAttributes().get("tasks");
            return tasks != null ? tasks : List.of();
        } else {
            logger.warn("Failed to get tasks by status {}: {}", status, result.getReason().orElse("Unknown reason"));
            return List.of();
        }
    }

    /**
     * Registers a listener to be called when a task completes.
     *
     * @param taskId   The task ID
     * @param listener The listener to call when the task completes
     * @return True if the listener was registered successfully, false otherwise
     */
    public boolean onTaskCompletion(String taskId, Consumer<Task> listener) {
        TaskExecutionPort.TaskResult result = taskExecutionPort.registerCompletionListener(taskId, listener);
        if (!result.isSuccessful()) {
            logger.warn("Failed to register completion listener for task {}: {}", 
                    taskId, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Executes a task asynchronously and returns a CompletableFuture for the result.
     *
     * @param <T>      The type of the task result
     * @param callable The callable to execute
     * @return A CompletableFuture that will complete with the task result, or complete exceptionally if the task fails
     */
    public <T> CompletableFuture<T> executeAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        // Create a task that will complete the future when done
        Optional<String> taskId = executeTask(callable);
        
        if (taskId.isEmpty()) {
            future.completeExceptionally(new RuntimeException("Failed to submit task"));
            return future;
        }
        
        // Register completion listener
        onTaskCompletion(taskId.get(), task -> {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                @SuppressWarnings("unchecked")
                T result = (T) task.getResult().orElse(null);
                future.complete(result);
            } else if (task.getStatus() == TaskStatus.FAILED || task.getStatus() == TaskStatus.TIMED_OUT) {
                future.completeExceptionally(new RuntimeException(task.getErrorMessage().orElse("Task failed")));
            } else if (task.getStatus() == TaskStatus.CANCELLED) {
                future.cancel(false);
            }
        });
        
        return future;
    }

    /**
     * Executes a task asynchronously with options and returns a CompletableFuture for the result.
     *
     * @param <T>      The type of the task result
     * @param callable The callable to execute
     * @param options  The task options
     * @return A CompletableFuture that will complete with the task result, or complete exceptionally if the task fails
     */
    public <T> CompletableFuture<T> executeAsync(Callable<T> callable, TaskOptions options) {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        // Create a task that will complete the future when done
        Optional<String> taskId = executeTask(callable, options);
        
        if (taskId.isEmpty()) {
            future.completeExceptionally(new RuntimeException("Failed to submit task"));
            return future;
        }
        
        // Register completion listener
        onTaskCompletion(taskId.get(), task -> {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                @SuppressWarnings("unchecked")
                T result = (T) task.getResult().orElse(null);
                future.complete(result);
            } else if (task.getStatus() == TaskStatus.FAILED || task.getStatus() == TaskStatus.TIMED_OUT) {
                future.completeExceptionally(new RuntimeException(task.getErrorMessage().orElse("Task failed")));
            } else if (task.getStatus() == TaskStatus.CANCELLED) {
                future.cancel(false);
            }
        });
        
        return future;
    }

    /**
     * Creates a high-priority task.
     *
     * @param name        The task name
     * @param description The task description
     * @return Task options with high priority
     */
    public TaskOptions createHighPriorityTask(String name, String description) {
        return createOptionsBuilder()
                .name(name)
                .description(description)
                .priority(TaskPriority.HIGH)
                .build();
    }

    /**
     * Creates a low-priority task.
     *
     * @param name        The task name
     * @param description The task description
     * @return Task options with low priority
     */
    public TaskOptions createLowPriorityTask(String name, String description) {
        return createOptionsBuilder()
                .name(name)
                .description(description)
                .priority(TaskPriority.LOW)
                .build();
    }

    /**
     * Creates a task with a timeout.
     *
     * @param name        The task name
     * @param description The task description
     * @param timeout     The timeout duration
     * @return Task options with the specified timeout
     */
    public TaskOptions createTaskWithTimeout(String name, String description, Duration timeout) {
        return createOptionsBuilder()
                .name(name)
                .description(description)
                .timeout(timeout)
                .build();
    }

    /**
     * Gets statistics about the task execution system.
     *
     * @return A map of statistics, or an empty map if not available
     */
    public Map<String, Object> getStatistics() {
        TaskExecutionPort.TaskResult result = taskExecutionPort.getStatistics();
        if (result.isSuccessful()) {
            return result.getAttributes();
        } else {
            logger.warn("Failed to get task execution statistics: {}", result.getReason().orElse("Unknown reason"));
            return Map.of();
        }
    }

    /**
     * Shuts down the task execution service.
     *
     * @return True if the service was shut down successfully, false otherwise
     */
    public boolean shutdown() {
        logger.info("Shutting down task execution service");
        TaskExecutionPort.TaskResult result = taskExecutionPort.shutdown();
        if (!result.isSuccessful()) {
            logger.error("Failed to shut down task execution service: {}", result.getReason().orElse("Unknown reason"));
            return false;
        }
        return true;
    }

    /**
     * Shuts down the task execution service and waits for tasks to complete.
     *
     * @param timeout The timeout for waiting for task completion
     * @return True if the service was shut down successfully, false otherwise
     */
    public boolean shutdownAndWait(Duration timeout) {
        logger.info("Shutting down task execution service and waiting for tasks to complete");
        TaskExecutionPort.TaskResult result = taskExecutionPort.shutdownAndWait(timeout);
        if (!result.isSuccessful()) {
            logger.error("Failed to shut down task execution service: {}", result.getReason().orElse("Unknown reason"));
            return false;
        }
        return true;
    }
}