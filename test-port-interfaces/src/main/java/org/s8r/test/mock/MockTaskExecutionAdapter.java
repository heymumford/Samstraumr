/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.mock;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;
import org.s8r.application.port.TaskExecutionPort.TaskResult;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mock implementation of the TaskExecutionPort for testing.
 * 
 * <p>This adapter provides an in-memory implementation of the TaskExecutionPort
 * interface for use in testing.
 */
public class MockTaskExecutionAdapter implements TaskExecutionPort {
    
    private static final Logger LOGGER = Logger.getLogger(MockTaskExecutionAdapter.class.getName());
    
    private final Map<String, TaskInfo<?>> tasks;
    private final Map<String, RecurringTask> recurringTasks;
    private final ThreadPoolExecutor executor;
    private final ScheduledThreadPoolExecutor scheduledExecutor;
    private final AtomicBoolean running;
    
    /**
     * Creates a new MockTaskExecutionAdapter.
     */
    public MockTaskExecutionAdapter() {
        this.tasks = new ConcurrentHashMap<>();
        this.recurringTasks = new ConcurrentHashMap<>();
        this.executor = new ThreadPoolExecutor(
            2, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(2);
        this.running = new AtomicBoolean(true);
    }
    
    @Override
    public <T> Future<T> executeAsync(Callable<T> task) {
        if (!running.get()) {
            throw new IllegalStateException("Task executor has been shut down");
        }
        
        String taskId = UUID.randomUUID().toString();
        
        CompletableFuture<T> future = new CompletableFuture<>();
        
        executor.execute(() -> {
            try {
                TaskInfo<T> taskInfo = new TaskInfo<>(taskId, task, null, Instant.now());
                taskInfo.status = TaskStatus.RUNNING;
                tasks.put(taskId, taskInfo);
                
                T result = task.call();
                
                taskInfo.status = TaskStatus.COMPLETED;
                taskInfo.result = result;
                taskInfo.completedAt = Instant.now();
                
                future.complete(result);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Task execution failed", e);
                
                @SuppressWarnings("unchecked")
                TaskInfo<T> taskInfo = (TaskInfo<T>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.FAILED;
                    taskInfo.error = e;
                    taskInfo.completedAt = Instant.now();
                }
                
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    @Override
    public <T> Future<T> executeAsync(Callable<T> task, Duration timeout) {
        if (!running.get()) {
            throw new IllegalStateException("Task executor has been shut down");
        }
        
        String taskId = UUID.randomUUID().toString();
        
        CompletableFuture<T> future = new CompletableFuture<>();
        
        executor.execute(() -> {
            try {
                TaskInfo<T> taskInfo = new TaskInfo<>(taskId, task, timeout, Instant.now());
                taskInfo.status = TaskStatus.RUNNING;
                tasks.put(taskId, taskInfo);
                
                // Schedule a timeout task
                Instant startTime = Instant.now();
                Future<?> timeoutFuture = scheduledExecutor.schedule(() -> {
                    if (!future.isDone()) {
                        taskInfo.status = TaskStatus.TIMEOUT;
                        taskInfo.completedAt = Instant.now();
                        future.completeExceptionally(new TimeoutException("Task execution timed out"));
                    }
                }, timeout.toMillis(), TimeUnit.MILLISECONDS);
                
                // Execute the task
                T result = task.call();
                
                // If we get here before the timeout, cancel the timeout task
                timeoutFuture.cancel(false);
                
                // Check if we've already timed out
                if (!future.isDone()) {
                    taskInfo.status = TaskStatus.COMPLETED;
                    taskInfo.result = result;
                    taskInfo.completedAt = Instant.now();
                    future.complete(result);
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Task execution failed", e);
                
                @SuppressWarnings("unchecked")
                TaskInfo<T> taskInfo = (TaskInfo<T>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.FAILED;
                    taskInfo.error = e;
                    taskInfo.completedAt = Instant.now();
                }
                
                if (!future.isDone()) {
                    future.completeExceptionally(e);
                }
            }
        });
        
        return future;
    }
    
    @Override
    public CompletableFuture<Void> executeAsync(Runnable task) {
        if (!running.get()) {
            throw new IllegalStateException("Task executor has been shut down");
        }
        
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        executor.execute(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Task execution failed", e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    @Override
    public <T> TaskResult scheduleTask(Callable<T> task, Instant executionTime) {
        if (!running.get()) {
            return TaskResult.failure("unknown",
                "Task executor has been shut down", "Task executor has been shut down");
        }
        
        String taskId = UUID.randomUUID().toString();
        Duration delay = Duration.between(Instant.now(), executionTime);

        // Create a FutureTask that will update the task status when complete
        FutureTask<T> futureTask = new FutureTask<>(() -> {
            try {
                @SuppressWarnings("unchecked")
                TaskInfo<T> taskInfo = (TaskInfo<T>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.RUNNING;
                }

                T result = task.call();

                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.COMPLETED;
                    taskInfo.result = result;
                    taskInfo.completedAt = Instant.now();
                }

                return result;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Scheduled task execution failed", e);

                @SuppressWarnings("unchecked")
                TaskInfo<T> taskInfo = (TaskInfo<T>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.FAILED;
                    taskInfo.error = e;
                    taskInfo.completedAt = Instant.now();
                }

                throw e;
            }
        });

        TaskInfo<T> taskInfo = new TaskInfo<>(taskId, task, delay, executionTime);
        taskInfo.status = TaskStatus.SCHEDULED;
        taskInfo.future = futureTask;
        tasks.put(taskId, taskInfo);

        // Schedule the task for execution
        scheduledExecutor.schedule(futureTask, delay.toMillis(), TimeUnit.MILLISECONDS);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("scheduledAt", Instant.now().toString());
        attributes.put("executionTime", executionTime.toString());

        return TaskResult.success(taskId, "Task scheduled for execution", attributes);
    }
    
    @Override
    public TaskResult scheduleTask(Runnable task, Instant executionTime) {
        if (!running.get()) {
            return TaskResult.failure("unknown",
                "Task executor has been shut down", "Task executor has been shut down");
        }

        String taskId = UUID.randomUUID().toString();
        Duration delay = Duration.between(Instant.now(), executionTime);

        // Create a FutureTask that will update the task status when complete
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            try {
                TaskInfo<Void> taskInfo = (TaskInfo<Void>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.RUNNING;
                }

                task.run();

                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.COMPLETED;
                    taskInfo.completedAt = Instant.now();
                }

                return null;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Scheduled task execution failed", e);

                TaskInfo<Void> taskInfo = (TaskInfo<Void>) tasks.get(taskId);
                if (taskInfo != null) {
                    taskInfo.status = TaskStatus.FAILED;
                    taskInfo.error = e;
                    taskInfo.completedAt = Instant.now();
                }

                throw e;
            }
        });

        TaskInfo<Void> taskInfo = new TaskInfo<>(taskId, null, delay, executionTime);
        taskInfo.status = TaskStatus.SCHEDULED;
        taskInfo.future = futureTask;
        tasks.put(taskId, taskInfo);

        // Schedule the task for execution
        scheduledExecutor.schedule(futureTask, delay.toMillis(), TimeUnit.MILLISECONDS);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("scheduledAt", Instant.now().toString());
        attributes.put("executionTime", executionTime.toString());

        return TaskResult.success(taskId, "Task scheduled for execution", attributes);
    }
    
    /**
     * Schedules a recurring task to execute at fixed intervals.
     *
     * @param task The task to execute
     * @param initialDelay The initial delay before the first execution
     * @param interval The interval between executions
     * @return A TaskResult containing the ID of the recurring task
     */
    public TaskResult scheduleRecurringTask(Runnable task, Duration initialDelay, Duration interval) {
        if (!running.get()) {
            return TaskResult.failure("unknown",
                "Task executor has been shut down", "Task executor has been shut down");
        }
        
        String taskId = UUID.randomUUID().toString();
        
        RecurringTask recurringTask = new RecurringTask(taskId, task, initialDelay, interval);
        recurringTasks.put(taskId, recurringTask);
        
        // Schedule the recurring task
        recurringTask.scheduledFuture = scheduledExecutor.scheduleAtFixedRate(
            () -> {
                try {
                    task.run();
                    recurringTask.executionCount.incrementAndGet();
                    recurringTask.lastExecutionTime = Instant.now();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Recurring task execution failed", e);
                }
            },
            initialDelay.toMillis(),
            interval.toMillis(),
            TimeUnit.MILLISECONDS
        );
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("scheduledAt", Instant.now().toString());
        attributes.put("initialExecutionTime", Instant.now().plus(initialDelay).toString());
        attributes.put("interval", interval.toString());

        return TaskResult.success(taskId, "Recurring task scheduled", attributes);
    }
    
    @Override
    public TaskResult cancelTask(String taskId, boolean mayInterruptIfRunning) {
        // Check if it's a recurring task
        if (recurringTasks.containsKey(taskId)) {
            RecurringTask recurringTask = recurringTasks.get(taskId);
            boolean result = recurringTask.scheduledFuture.cancel(mayInterruptIfRunning);
            if (result) {
                recurringTask.canceled = true;
                Map<String, Object> attributes = new HashMap<>();
                return TaskResult.success(taskId, "Recurring task canceled", attributes);
            } else {
                return TaskResult.failure(taskId, "Failed to cancel recurring task",
                    "Task may have already completed or been canceled");
            }
        }

        // Check if it's a regular task
        TaskInfo<?> taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            return TaskResult.failure(taskId, "Task not found",
                "No task found with the specified ID");
        }

        if (taskInfo.future == null) {
            return TaskResult.failure(taskId, "Task has no future",
                "Task does not have an associated future for cancellation");
        }

        boolean result = taskInfo.future.cancel(mayInterruptIfRunning);
        if (result) {
            taskInfo.status = TaskStatus.CANCELED;
            taskInfo.completedAt = Instant.now();
            Map<String, Object> attributes = new HashMap<>();
            return TaskResult.success(taskId, "Task canceled", attributes);
        } else {
            return TaskResult.failure(taskId, "Failed to cancel task",
                "Task may have already completed or been canceled");
        }
    }
    
    @Override
    public TaskResult getTask(String taskId) {
        // Check if it's a recurring task
        if (recurringTasks.containsKey(taskId)) {
            RecurringTask recurringTask = recurringTasks.get(taskId);
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("executionCount", recurringTask.executionCount.get());
            attributes.put("lastExecutionTime", recurringTask.lastExecutionTime != null 
                ? recurringTask.lastExecutionTime.toString() : "N/A");
            attributes.put("interval", recurringTask.interval.toString());
            
            if (recurringTask.canceled) {
                return TaskResult.failure(taskId,
                    "Recurring task was canceled", "Task canceled by user");
            }

            return TaskResult.success(taskId, "Recurring task is active", attributes);
        }

        // Check if it's a regular task
        TaskInfo<?> taskInfo = tasks.get(taskId);
        if (taskInfo == null) {
            return TaskResult.failure(taskId,
                "Task not found", "No task found with the specified ID");
        }
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("scheduledAt", taskInfo.scheduledAt.toString());
        if (taskInfo.scheduledExecutionTime != null) {
            attributes.put("scheduledExecutionTime", taskInfo.scheduledExecutionTime.toString());
        }
        if (taskInfo.completedAt != null) {
            attributes.put("completedAt", taskInfo.completedAt.toString());
        }
        
        switch (taskInfo.status) {
            case PENDING:
            case SCHEDULED:
                return TaskResult.success(taskId, "Task is scheduled", attributes);
            case RUNNING:
                return TaskResult.success(taskId, "Task is running", attributes);
            case COMPLETED:
                return TaskResult.success(taskId, "Task completed successfully", attributes);
            case FAILED:
                return TaskResult.failure(taskId,
                    "Task execution failed",
                    taskInfo.error != null ? taskInfo.error.getMessage() : "Unknown error",
                    attributes);
            case CANCELED:
                return TaskResult.failure(taskId,
                    "Task was canceled", "Task canceled by user", attributes);
            case TIMEOUT:
                return TaskResult.failure(taskId,
                    "Task execution timed out", "Task exceeded maximum execution time",
                    attributes);
            default:
                return TaskResult.failure(taskId,
                    "Unknown task status", "Task status could not be determined",
                    attributes);
        }
    }
    
    @Override
    public TaskResult shutdown() {
        if (!running.compareAndSet(true, false)) {
            return TaskResult.failure("shutdown",
                "Task executor is already shut down", "Already shut down");
        }

        // Cancel all recurring tasks
        for (String taskId : recurringTasks.keySet()) {
            RecurringTask recurringTask = recurringTasks.get(taskId);
            recurringTask.scheduledFuture.cancel(false);
            recurringTask.canceled = true;
        }

        // Immediate shutdown
        executor.shutdownNow();
        scheduledExecutor.shutdownNow();

        Map<String, Object> attributes = new HashMap<>();
        return TaskResult.success("shutdown", "Task executor shut down", attributes);
    }

    @Override
    public TaskResult shutdownAndWait(Duration timeout) {
        if (!running.compareAndSet(true, false)) {
            return TaskResult.failure("shutdown",
                "Task executor is already shut down", "Already shut down");
        }

        // Cancel all recurring tasks
        for (String taskId : recurringTasks.keySet()) {
            RecurringTask recurringTask = recurringTasks.get(taskId);
            recurringTask.scheduledFuture.cancel(false);
            recurringTask.canceled = true;
        }

        // Wait for completion of existing tasks
        executor.shutdown();
        scheduledExecutor.shutdown();

        try {
            // Wait for tasks to complete
            long timeoutMillis = timeout.toMillis();
            if (!executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                scheduledExecutor.shutdownNow();
            }

            Map<String, Object> attributes = new HashMap<>();
            return TaskResult.success("shutdown", "Task executor shut down gracefully", attributes);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            scheduledExecutor.shutdownNow();

            return TaskResult.failure("shutdown",
                "Task executor shutdown was interrupted", e.getMessage());
        }
    }
    
    /**
     * Gets all tasks that have been submitted.
     * 
     * @return Map of task IDs to task information
     */
    public Map<String, TaskInfo<?>> getTasks() {
        return new HashMap<>(tasks);
    }
    
    /**
     * Gets all recurring tasks that have been scheduled.
     * 
     * @return Map of task IDs to recurring task information
     */
    public Map<String, RecurringTask> getRecurringTasks() {
        return new HashMap<>(recurringTasks);
    }
    
    /**
     * Simulates the behavior where a task runs longer than expected.
     * This is useful for testing timeout behavior.
     *
     * @param taskId The ID of the task to slow down
     * @param extraDelay The additional delay to add to the task execution
     */
    public void simulateSlowTask(String taskId, Duration extraDelay) {
        TaskInfo<?> taskInfo = tasks.get(taskId);
        if (taskInfo != null) {
            taskInfo.artificialDelay = extraDelay;
        }
    }
    
    /**
     * Simulates a task failure.
     *
     * @param taskId The ID of the task to fail
     * @param reason The reason for the failure
     */
    public void simulateTaskFailure(String taskId, String reason) {
        TaskInfo<?> taskInfo = tasks.get(taskId);
        if (taskInfo != null) {
            taskInfo.simulateFailure = true;
            taskInfo.failureReason = reason;
        }
    }
    
    /**
     * Information about a task.
     */
    public static class TaskInfo<T> {
        private final String id;
        private final Callable<T> task;
        private final Duration timeout;
        private final Instant scheduledAt;
        private Instant scheduledExecutionTime;
        private Instant completedAt;
        private TaskStatus status;
        private T result;
        private Exception error;
        private Future<?> future;
        private Duration artificialDelay;
        private boolean simulateFailure;
        private String failureReason;
        
        /**
         * Creates a new TaskInfo with the specified parameters.
         *
         * @param id The task ID
         * @param task The task to execute
         * @param timeout The timeout duration, or null for no timeout
         * @param scheduledExecutionTime The scheduled execution time, or null for immediate execution
         */
        TaskInfo(String id, Callable<T> task, Duration timeout, Instant scheduledExecutionTime) {
            this.id = id;
            this.task = task;
            this.timeout = timeout;
            this.scheduledAt = Instant.now();
            this.scheduledExecutionTime = scheduledExecutionTime;
            this.status = TaskStatus.PENDING;
        }
        
        /**
         * Gets the task ID.
         * 
         * @return The task ID
         */
        public String getId() {
            return id;
        }
        
        /**
         * Gets the task.
         * 
         * @return The task
         */
        public Callable<T> getTask() {
            return task;
        }
        
        /**
         * Gets the timeout duration.
         * 
         * @return The timeout duration, or null for no timeout
         */
        public Optional<Duration> getTimeout() {
            return Optional.ofNullable(timeout);
        }
        
        /**
         * Gets the scheduled time.
         * 
         * @return The time when the task was scheduled
         */
        public Instant getScheduledAt() {
            return scheduledAt;
        }
        
        /**
         * Gets the scheduled execution time.
         * 
         * @return The scheduled execution time, or null for immediate execution
         */
        public Optional<Instant> getScheduledExecutionTime() {
            return Optional.ofNullable(scheduledExecutionTime);
        }
        
        /**
         * Gets the completion time.
         * 
         * @return The time when the task was completed, or null if not completed
         */
        public Optional<Instant> getCompletedAt() {
            return Optional.ofNullable(completedAt);
        }
        
        /**
         * Gets the task status.
         * 
         * @return The task status
         */
        public TaskStatus getStatus() {
            return status;
        }
        
        /**
         * Gets the task result.
         * 
         * @return The task result, or null if not completed or failed
         */
        public Optional<T> getResult() {
            return Optional.ofNullable(result);
        }
        
        /**
         * Gets the error that occurred during task execution.
         * 
         * @return The error, or null if the task completed successfully or is not yet complete
         */
        public Optional<Exception> getError() {
            return Optional.ofNullable(error);
        }
        
        /**
         * Gets the artificial delay for this task.
         * 
         * @return The artificial delay, or null if no delay
         */
        public Optional<Duration> getArtificialDelay() {
            return Optional.ofNullable(artificialDelay);
        }
        
        /**
         * Checks if this task should simulate a failure.
         * 
         * @return true if the task should simulate a failure, false otherwise
         */
        public boolean shouldSimulateFailure() {
            return simulateFailure;
        }
        
        /**
         * Gets the reason for the simulated failure.
         * 
         * @return The reason, or null if no failure simulation
         */
        public Optional<String> getFailureReason() {
            return Optional.ofNullable(failureReason);
        }
    }
    
    /**
     * Information about a recurring task.
     */
    public static class RecurringTask {
        private final String id;
        private final Runnable task;
        private final Duration initialDelay;
        private final Duration interval;
        private final java.util.concurrent.atomic.AtomicInteger executionCount;
        private java.util.concurrent.ScheduledFuture<?> scheduledFuture;
        private Instant lastExecutionTime;
        private boolean canceled;
        
        /**
         * Creates a new RecurringTask with the specified parameters.
         *
         * @param id The task ID
         * @param task The task to execute
         * @param initialDelay The initial delay before the first execution
         * @param interval The interval between executions
         */
        RecurringTask(String id, Runnable task, Duration initialDelay, Duration interval) {
            this.id = id;
            this.task = task;
            this.initialDelay = initialDelay;
            this.interval = interval;
            this.executionCount = new java.util.concurrent.atomic.AtomicInteger(0);
            this.canceled = false;
        }
        
        /**
         * Gets the task ID.
         * 
         * @return The task ID
         */
        public String getId() {
            return id;
        }
        
        /**
         * Gets the task.
         * 
         * @return The task
         */
        public Runnable getTask() {
            return task;
        }
        
        /**
         * Gets the initial delay.
         * 
         * @return The initial delay
         */
        public Duration getInitialDelay() {
            return initialDelay;
        }
        
        /**
         * Gets the interval.
         * 
         * @return The interval
         */
        public Duration getInterval() {
            return interval;
        }
        
        /**
         * Gets the execution count.
         * 
         * @return The number of times the task has been executed
         */
        public int getExecutionCount() {
            return executionCount.get();
        }
        
        /**
         * Gets the last execution time.
         * 
         * @return The last time the task was executed, or null if not yet executed
         */
        public Optional<Instant> getLastExecutionTime() {
            return Optional.ofNullable(lastExecutionTime);
        }
        
        /**
         * Checks if this task has been canceled.
         * 
         * @return true if the task has been canceled, false otherwise
         */
        public boolean isCanceled() {
            return canceled;
        }
    }
    
    /**
     * Exception thrown when a task execution times out.
     */
    public static class TimeoutException extends RuntimeException {
        /**
         * Creates a new TimeoutException with the specified message.
         *
         * @param message The exception message
         */
        public TimeoutException(String message) {
            super(message);
        }
    }
}