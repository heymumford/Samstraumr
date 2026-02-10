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
package org.s8r.application.port;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * TaskExecutionPort defines the interface for task execution operations in the S8r framework.
 *
 * <p>This port provides asynchronous task execution, scheduling, and monitoring capabilities. It
 * follows Clean Architecture principles by defining a boundary between the application and
 * infrastructure layers for task execution concerns.
 */
public interface TaskExecutionPort {

  /** Represents a task that can be executed by the task execution system. */
  interface Task {
    /**
     * Gets the unique identifier of the task.
     *
     * @return The task identifier
     */
    String getId();

    /**
     * Gets the name of the task.
     *
     * @return The task name
     */
    String getName();

    /**
     * Gets the description of the task.
     *
     * @return The task description
     */
    String getDescription();

    /**
     * Gets the status of the task.
     *
     * @return The task status
     */
    TaskStatus getStatus();

    /**
     * Gets the scheduled execution time of the task.
     *
     * @return The scheduled execution time, or empty if not scheduled
     */
    Optional<Instant> getScheduledTime();

    /**
     * Gets the start time of the task.
     *
     * @return The start time, or empty if not started
     */
    Optional<Instant> getStartTime();

    /**
     * Gets the completion time of the task.
     *
     * @return The completion time, or empty if not completed
     */
    Optional<Instant> getCompletionTime();

    /**
     * Gets the result of the task execution.
     *
     * @return The task result, or empty if not completed or failed
     */
    Optional<Object> getResult();

    /**
     * Gets the error message if the task failed.
     *
     * @return The error message, or empty if not failed
     */
    Optional<String> getErrorMessage();

    /**
     * Gets the metadata associated with the task.
     *
     * @return A map of metadata
     */
    Map<String, Object> getMetadata();

    /**
     * Checks if the task is complete.
     *
     * @return True if the task is complete, false otherwise
     */
    boolean isComplete();

    /**
     * Checks if the task was canceled.
     *
     * @return True if the task was canceled, false otherwise
     */
    boolean isCancelled();

    /**
     * Checks if the task failed.
     *
     * @return True if the task failed, false otherwise
     */
    boolean isFailed();
  }

  /** Task status enum. */
  enum TaskStatus {
    /** Task is created but not yet queued */
    CREATED,

    /** Task is queued for execution */
    QUEUED,

    /** Task is scheduled for future execution */
    SCHEDULED,

    /** Task is currently running */
    RUNNING,

    /** Task completed successfully */
    COMPLETED,

    /** Task failed with an error */
    FAILED,

    /** Task was cancelled */
    CANCELLED,

    /** Task timed out */
    TIMED_OUT
  }

  /** Task priority enum. */
  enum TaskPriority {
    /** Lowest priority */
    LOWEST(1),

    /** Low priority */
    LOW(3),

    /** Normal priority */
    NORMAL(5),

    /** High priority */
    HIGH(7),

    /** Highest priority */
    HIGHEST(9);

    private final int value;

    TaskPriority(int value) {
      this.value = value;
    }

    /**
     * Gets the numeric value of the priority.
     *
     * @return The priority value
     */
    public int getValue() {
      return value;
    }
  }

  /** Builder for creating task execution options. */
  interface TaskOptionsBuilder {
    /**
     * Sets the task name.
     *
     * @param name The task name
     * @return This builder
     */
    TaskOptionsBuilder name(String name);

    /**
     * Sets the task description.
     *
     * @param description The task description
     * @return This builder
     */
    TaskOptionsBuilder description(String description);

    /**
     * Sets the task priority.
     *
     * @param priority The task priority
     * @return This builder
     */
    TaskOptionsBuilder priority(TaskPriority priority);

    /**
     * Sets the task timeout.
     *
     * @param timeout The timeout duration
     * @return This builder
     */
    TaskOptionsBuilder timeout(Duration timeout);

    /**
     * Sets the task execution delay.
     *
     * @param delay The delay duration
     * @return This builder
     */
    TaskOptionsBuilder delay(Duration delay);

    /**
     * Adds a metadata entry to the task.
     *
     * @param key The metadata key
     * @param value The metadata value
     * @return This builder
     */
    TaskOptionsBuilder metadata(String key, Object value);

    /**
     * Adds multiple metadata entries to the task.
     *
     * @param metadata The metadata map
     * @return This builder
     */
    TaskOptionsBuilder metadata(Map<String, Object> metadata);

    /**
     * Builds the task options.
     *
     * @return The task options
     */
    TaskOptions build();
  }

  /** Options for task execution. */
  interface TaskOptions {
    /**
     * Gets the task name.
     *
     * @return The task name
     */
    String getName();

    /**
     * Gets the task description.
     *
     * @return The task description
     */
    String getDescription();

    /**
     * Gets the task priority.
     *
     * @return The task priority
     */
    TaskPriority getPriority();

    /**
     * Gets the task timeout.
     *
     * @return The timeout duration, or empty if not set
     */
    Optional<Duration> getTimeout();

    /**
     * Gets the task execution delay.
     *
     * @return The delay duration, or empty if not set
     */
    Optional<Duration> getDelay();

    /**
     * Gets the task metadata.
     *
     * @return The metadata map
     */
    Map<String, Object> getMetadata();
  }

  /** Represents the outcome of a task operation with detailed information. */
  final class TaskResult {
    private final boolean successful;
    private final String taskId;
    private final String message;
    private final String reason;
    private final Map<String, Object> attributes;

    private TaskResult(
        boolean successful,
        String taskId,
        String message,
        String reason,
        Map<String, Object> attributes) {
      this.successful = successful;
      this.taskId = taskId;
      this.message = message;
      this.reason = reason;
      this.attributes = attributes;
    }

    /**
     * Creates a successful result.
     *
     * @param taskId The ID of the task
     * @param message A message describing the successful operation
     * @return A new TaskResult instance indicating success
     */
    public static TaskResult success(String taskId, String message) {
      return new TaskResult(true, taskId, message, null, Map.of());
    }

    /**
     * Creates a successful result with additional attributes.
     *
     * @param taskId The ID of the task
     * @param message A message describing the successful operation
     * @param attributes Additional information about the operation
     * @return A new TaskResult instance indicating success
     */
    public static TaskResult success(
        String taskId, String message, Map<String, Object> attributes) {
      return new TaskResult(true, taskId, message, null, attributes);
    }

    /**
     * Creates a failed result.
     *
     * @param taskId The ID of the task, if available
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @return A new TaskResult instance indicating failure
     */
    public static TaskResult failure(String taskId, String message, String reason) {
      return new TaskResult(false, taskId, message, reason, Map.of());
    }

    /**
     * Creates a failed result with additional attributes.
     *
     * @param taskId The ID of the task, if available
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @param attributes Additional information about the operation
     * @return A new TaskResult instance indicating failure
     */
    public static TaskResult failure(
        String taskId, String message, String reason, Map<String, Object> attributes) {
      return new TaskResult(false, taskId, message, reason, attributes);
    }

    /**
     * Checks if the operation was successful.
     *
     * @return True if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the ID of the task associated with the operation.
     *
     * @return The task ID, or empty if not available
     */
    public Optional<String> getTaskId() {
      return Optional.ofNullable(taskId);
    }

    /**
     * Gets the message associated with the operation result.
     *
     * @return The message describing the operation outcome
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the reason for a failed operation.
     *
     * @return The reason for the failure, or empty if the operation was successful
     */
    public Optional<String> getReason() {
      return Optional.ofNullable(reason);
    }

    /**
     * Gets the additional attributes associated with the operation result.
     *
     * @return A map of attributes providing additional information
     */
    public Map<String, Object> getAttributes() {
      return attributes;
    }
  }

  /**
   * Creates a task options builder.
   *
   * @return A new task options builder
   */
  TaskOptionsBuilder createTaskOptionsBuilder();

  /**
   * Initializes the task execution system.
   *
   * @return A TaskResult indicating success or failure
   */
  TaskResult initialize();

  /**
   * Submits a runnable task for execution.
   *
   * @param runnable The runnable to execute
   * @return A TaskResult containing the task ID
   */
  TaskResult submitTask(Runnable runnable);

  /**
   * Submits a runnable task for execution with options.
   *
   * @param runnable The runnable to execute
   * @param options The task options
   * @return A TaskResult containing the task ID
   */
  TaskResult submitTask(Runnable runnable, TaskOptions options);

  /**
   * Submits a callable task for execution.
   *
   * @param <T> The type of the task result
   * @param callable The callable to execute
   * @return A TaskResult containing the task ID
   */
  <T> TaskResult submitTask(Callable<T> callable);

  /**
   * Submits a callable task for execution with options.
   *
   * @param <T> The type of the task result
   * @param callable The callable to execute
   * @param options The task options
   * @return A TaskResult containing the task ID
   */
  <T> TaskResult submitTask(Callable<T> callable, TaskOptions options);

  /**
   * Schedules a runnable task for future execution.
   *
   * @param runnable The runnable to execute
   * @param executionTime The time to execute the task
   * @return A TaskResult containing the task ID
   */
  TaskResult scheduleTask(Runnable runnable, Instant executionTime);

  /**
   * Schedules a runnable task for future execution with options.
   *
   * @param runnable The runnable to execute
   * @param executionTime The time to execute the task
   * @param options The task options
   * @return A TaskResult containing the task ID
   */
  TaskResult scheduleTask(Runnable runnable, Instant executionTime, TaskOptions options);

  /**
   * Schedules a callable task for future execution.
   *
   * @param <T> The type of the task result
   * @param callable The callable to execute
   * @param executionTime The time to execute the task
   * @return A TaskResult containing the task ID
   */
  <T> TaskResult scheduleTask(Callable<T> callable, Instant executionTime);

  /**
   * Schedules a callable task for future execution with options.
   *
   * @param <T> The type of the task result
   * @param callable The callable to execute
   * @param executionTime The time to execute the task
   * @param options The task options
   * @return A TaskResult containing the task ID
   */
  <T> TaskResult scheduleTask(Callable<T> callable, Instant executionTime, TaskOptions options);

  /**
   * Gets a task by ID.
   *
   * @param taskId The task ID
   * @return A TaskResult containing the task, or a failure if not found
   */
  TaskResult getTask(String taskId);

  /**
   * Cancels a task.
   *
   * @param taskId The task ID
   * @param mayInterruptIfRunning Whether to interrupt the task if running
   * @return A TaskResult indicating success or failure
   */
  TaskResult cancelTask(String taskId, boolean mayInterruptIfRunning);

  /**
   * Gets all tasks.
   *
   * @return A TaskResult containing a list of tasks
   */
  TaskResult getAllTasks();

  /**
   * Gets tasks by status.
   *
   * @param status The task status
   * @return A TaskResult containing a list of tasks
   */
  TaskResult getTasksByStatus(TaskStatus status);

  /**
   * Registers a task completion listener.
   *
   * @param taskId The task ID
   * @param listener The listener to call when the task completes
   * @return A TaskResult indicating success or failure
   */
  TaskResult registerCompletionListener(String taskId, Consumer<Task> listener);

  /**
   * Gets statistics about the task execution system.
   *
   * @return A TaskResult containing statistics
   */
  TaskResult getStatistics();

  /**
   * Shuts down the task execution system.
   *
   * @return A TaskResult indicating success or failure
   */
  TaskResult shutdown();

  /**
   * Shuts down the task execution system and waits for tasks to complete.
   *
   * @param timeout The timeout for waiting for task completion
   * @return A TaskResult indicating success or failure
   */
  TaskResult shutdownAndWait(Duration timeout);
}
