/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.task;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.TaskExecutionPort;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Thread pool-based implementation of the TaskExecutionPort interface.
 * <p>
 * This adapter provides task execution capabilities using Java's thread pool executors.
 * It supports task prioritization, scheduling, and monitoring.
 */
public class ThreadPoolTaskExecutionAdapter implements TaskExecutionPort {

    // Inner implementation classes

    /**
     * Implementation of the Task interface.
     */
    private class TaskImpl implements Task {
        private final String id;
        private final String name;
        private final String description;
        private volatile TaskStatus status;
        private final Instant creationTime;
        private Instant scheduledTime;
        private Instant startTime;
        private Instant completionTime;
        private Object result;
        private String errorMessage;
        private final Map<String, Object> metadata;
        private final Future<?> future;

        public TaskImpl(String id, String name, String description, Map<String, Object> metadata, Future<?> future) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.status = TaskStatus.CREATED;
            this.creationTime = Instant.now();
            this.metadata = new ConcurrentHashMap<>(metadata);
            this.future = future;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public TaskStatus getStatus() {
            return status;
        }

        @Override
        public Optional<Instant> getScheduledTime() {
            return Optional.ofNullable(scheduledTime);
        }

        @Override
        public Optional<Instant> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        @Override
        public Optional<Instant> getCompletionTime() {
            return Optional.ofNullable(completionTime);
        }

        @Override
        public Optional<Object> getResult() {
            return Optional.ofNullable(result);
        }

        @Override
        public Optional<String> getErrorMessage() {
            return Optional.ofNullable(errorMessage);
        }

        @Override
        public Map<String, Object> getMetadata() {
            return Collections.unmodifiableMap(metadata);
        }

        @Override
        public boolean isComplete() {
            return status == TaskStatus.COMPLETED 
                || status == TaskStatus.FAILED 
                || status == TaskStatus.CANCELLED 
                || status == TaskStatus.TIMED_OUT;
        }

        @Override
        public boolean isCancelled() {
            return status == TaskStatus.CANCELLED;
        }

        @Override
        public boolean isFailed() {
            return status == TaskStatus.FAILED || status == TaskStatus.TIMED_OUT;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public void setScheduledTime(Instant scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public void setStartTime(Instant startTime) {
            this.startTime = startTime;
        }

        public void setCompletionTime(Instant completionTime) {
            this.completionTime = completionTime;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public void addMetadata(String key, Object value) {
            this.metadata.put(key, value);
        }

        public Future<?> getFuture() {
            return future;
        }
    }

    /**
     * Implementation of the TaskOptions interface.
     */
    private static class TaskOptionsImpl implements TaskOptions {
        private final String name;
        private final String description;
        private final TaskPriority priority;
        private final Duration timeout;
        private final Duration delay;
        private final Map<String, Object> metadata;

        public TaskOptionsImpl(String name, String description, TaskPriority priority, 
                              Duration timeout, Duration delay, Map<String, Object> metadata) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.timeout = timeout;
            this.delay = delay;
            this.metadata = Collections.unmodifiableMap(new HashMap<>(metadata));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public TaskPriority getPriority() {
            return priority;
        }

        @Override
        public Optional<Duration> getTimeout() {
            return Optional.ofNullable(timeout);
        }

        @Override
        public Optional<Duration> getDelay() {
            return Optional.ofNullable(delay);
        }

        @Override
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }

    /**
     * Implementation of the TaskOptionsBuilder interface.
     */
    private class TaskOptionsBuilderImpl implements TaskOptionsBuilder {
        private String name = "Task";
        private String description = "";
        private TaskPriority priority = TaskPriority.NORMAL;
        private Duration timeout;
        private Duration delay;
        private final Map<String, Object> metadata = new HashMap<>();

        @Override
        public TaskOptionsBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public TaskOptionsBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public TaskOptionsBuilder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public TaskOptionsBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        @Override
        public TaskOptionsBuilder delay(Duration delay) {
            this.delay = delay;
            return this;
        }

        @Override
        public TaskOptionsBuilder metadata(String key, Object value) {
            metadata.put(key, value);
            return this;
        }

        @Override
        public TaskOptionsBuilder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        @Override
        public TaskOptions build() {
            return new TaskOptionsImpl(name, description, priority, timeout, delay, metadata);
        }
    }

    /**
     * Completion listener for task execution.
     */
    private static class TaskCompletionListener {
        private final String taskId;
        private final Consumer<Task> listener;

        public TaskCompletionListener(String taskId, Consumer<Task> listener) {
            this.taskId = taskId;
            this.listener = listener;
        }

        public String getTaskId() {
            return taskId;
        }

        public Consumer<Task> getListener() {
            return listener;
        }
    }

    /**
     * Task wrapper for prioritized execution.
     */
    private class PrioritizedTask implements Runnable, Comparable<PrioritizedTask> {
        private final String taskId;
        private final Runnable task;
        private final TaskPriority priority;

        public PrioritizedTask(String taskId, Runnable task, TaskPriority priority) {
            this.taskId = taskId;
            this.task = task;
            this.priority = priority;
        }

        @Override
        public void run() {
            TaskImpl taskImpl = tasks.get(taskId);
            if (taskImpl == null) {
                logger.warn("Task {} not found for execution", taskId);
                return;
            }

            try {
                // Update task status and start time
                taskImpl.setStatus(TaskStatus.RUNNING);
                taskImpl.setStartTime(Instant.now());
                runningTasks.incrementAndGet();

                // Execute the task
                task.run();

                // Update task status and completion time
                taskImpl.setStatus(TaskStatus.COMPLETED);
                taskImpl.setCompletionTime(Instant.now());
                completedTasks.incrementAndGet();

                // Notify listeners
                notifyCompletionListeners(taskImpl);
            } catch (Exception e) {
                // Handle task failure
                taskImpl.setStatus(TaskStatus.FAILED);
                taskImpl.setCompletionTime(Instant.now());
                taskImpl.setErrorMessage(e.getMessage());
                failedTasks.incrementAndGet();

                // Notify listeners
                notifyCompletionListeners(taskImpl);

                // Log the error
                logger.error("Task {} failed: {}", taskId, e.getMessage(), e);
            } finally {
                runningTasks.decrementAndGet();
            }
        }

        @Override
        public int compareTo(PrioritizedTask other) {
            // Higher priority values should come first
            return Integer.compare(other.priority.getValue(), this.priority.getValue());
        }
    }

    // Main class fields and methods

    private final LoggerPort logger;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final Map<String, TaskImpl> tasks = new ConcurrentHashMap<>();
    private final List<TaskCompletionListener> completionListeners = Collections.synchronizedList(new ArrayList<>());
    private ThreadPoolExecutor executor;
    private ScheduledExecutorService scheduledExecutor;
    private volatile boolean initialized = false;

    // Statistics
    private final AtomicLong submittedTasks = new AtomicLong(0);
    private final AtomicLong scheduledTasks = new AtomicLong(0);
    private final AtomicLong runningTasks = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong failedTasks = new AtomicLong(0);
    private final AtomicLong cancelledTasks = new AtomicLong(0);

    /**
     * Creates a new ThreadPoolTaskExecutionAdapter with default pool sizes.
     *
     * @param logger The logger to use for logging
     */
    public ThreadPoolTaskExecutionAdapter(LoggerPort logger) {
        this(logger, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2);
    }

    /**
     * Creates a new ThreadPoolTaskExecutionAdapter with the specified pool sizes.
     *
     * @param logger The logger to use for logging
     * @param corePoolSize The core pool size
     * @param maxPoolSize The maximum pool size
     */
    public ThreadPoolTaskExecutionAdapter(LoggerPort logger, int corePoolSize, int maxPoolSize) {
        this.logger = logger;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public TaskOptionsBuilder createTaskOptionsBuilder() {
        return new TaskOptionsBuilderImpl();
    }

    @Override
    public TaskResult initialize() {
        if (initialized) {
            return TaskResult.success(null, "Task execution system already initialized");
        }

        try {
            // Create a thread pool with priority queue
            PriorityBlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<>();
            executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maxPoolSize,
                    60, TimeUnit.SECONDS,
                    workQueue);

            // Create scheduled executor for delayed and scheduled tasks
            scheduledExecutor = Executors.newScheduledThreadPool(corePoolSize / 2);

            // Schedule cleanup of completed tasks
            scheduledExecutor.scheduleAtFixedRate(
                    this::cleanupCompletedTasks,
                    1, 1, TimeUnit.HOURS);

            initialized = true;
            logger.info("Task execution system initialized with core pool size {} and max pool size {}", 
                    corePoolSize, maxPoolSize);
            return TaskResult.success(null, "Task execution system initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize task execution system: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to initialize task execution system", e.getMessage());
        }
    }

    @Override
    public TaskResult submitTask(Runnable runnable) {
        return submitTask(runnable, createTaskOptionsBuilder().build());
    }

    @Override
    public TaskResult submitTask(Runnable runnable, TaskOptions options) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before submitting tasks");
        }

        if (runnable == null) {
            return TaskResult.failure(null, "Invalid parameters", "Runnable cannot be null");
        }

        try {
            // Generate task ID
            String taskId = UUID.randomUUID().toString();

            // Create prioritized task
            PrioritizedTask prioritizedTask = new PrioritizedTask(taskId, runnable, options.getPriority());

            // Create the task record
            Future<?> future;
            TaskImpl task = new TaskImpl(
                    taskId,
                    options.getName(),
                    options.getDescription(),
                    options.getMetadata(),
                    null); // Future will be set below

            // Apply optional delay
            if (options.getDelay().isPresent()) {
                Duration delay = options.getDelay().get();
                future = scheduledExecutor.schedule(prioritizedTask, delay.toMillis(), TimeUnit.MILLISECONDS);
                task.setStatus(TaskStatus.SCHEDULED);
                task.setScheduledTime(Instant.now().plus(delay));
                scheduledTasks.incrementAndGet();
            } else {
                future = executor.submit(prioritizedTask);
                task.setStatus(TaskStatus.QUEUED);
                submittedTasks.incrementAndGet();
            }

            // Set the task's future and store it
            java.lang.reflect.Field futureField = TaskImpl.class.getDeclaredField("future");
            futureField.setAccessible(true);
            futureField.set(task, future);
            futureField.setAccessible(false);

            // Store task in map
            tasks.put(taskId, task);

            // Apply timeout if specified
            if (options.getTimeout().isPresent()) {
                Duration timeout = options.getTimeout().get();
                scheduledExecutor.schedule(() -> {
                    if (!task.isComplete()) {
                        TaskImpl taskImpl = tasks.get(taskId);
                        if (taskImpl != null && !taskImpl.isComplete()) {
                            // Cancel the task
                            future.cancel(true);
                            taskImpl.setStatus(TaskStatus.TIMED_OUT);
                            taskImpl.setCompletionTime(Instant.now());
                            taskImpl.setErrorMessage("Task timed out after " + timeout.getSeconds() + " seconds");
                            
                            // Update statistics
                            failedTasks.incrementAndGet();
                            
                            // Notify listeners
                            notifyCompletionListeners(taskImpl);
                            
                            logger.warn("Task {} timed out after {} seconds", taskId, timeout.getSeconds());
                        }
                    }
                }, timeout.toMillis(), TimeUnit.MILLISECONDS);
            }

            logger.debug("Task {} submitted for execution with priority {}", taskId, options.getPriority());
            return TaskResult.success(taskId, "Task submitted successfully");
        } catch (Exception e) {
            logger.error("Error submitting task: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to submit task", e.getMessage());
        }
    }

    @Override
    public <T> TaskResult submitTask(Callable<T> callable) {
        return submitTask(callable, createTaskOptionsBuilder().build());
    }

    @Override
    public <T> TaskResult submitTask(Callable<T> callable, TaskOptions options) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before submitting tasks");
        }

        if (callable == null) {
            return TaskResult.failure(null, "Invalid parameters", "Callable cannot be null");
        }

        try {
            // Generate task ID
            String taskId = UUID.randomUUID().toString();

            // Create a wrapper runnable that executes the callable and stores the result
            Runnable taskWrapper = () -> {
                TaskImpl taskImpl = tasks.get(taskId);
                if (taskImpl == null) {
                    logger.warn("Task {} not found for execution", taskId);
                    return;
                }

                try {
                    // Execute the callable and store the result
                    T result = callable.call();
                    taskImpl.setResult(result);
                } catch (Exception e) {
                    // Error is handled by the PrioritizedTask wrapper
                    throw new RuntimeException(e);
                }
            };

            // Submit the task using the runnable submission method
            return submitTask(taskWrapper, options);
        } catch (Exception e) {
            logger.error("Error submitting callable task: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to submit callable task", e.getMessage());
        }
    }

    @Override
    public TaskResult scheduleTask(Runnable runnable, Instant executionTime) {
        return scheduleTask(runnable, executionTime, createTaskOptionsBuilder().build());
    }

    @Override
    public TaskResult scheduleTask(Runnable runnable, Instant executionTime, TaskOptions options) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before scheduling tasks");
        }

        if (runnable == null || executionTime == null) {
            return TaskResult.failure(null, "Invalid parameters",
                    "Runnable and execution time cannot be null");
        }

        if (executionTime.isBefore(Instant.now())) {
            return TaskResult.failure(null, "Invalid execution time",
                    "Execution time must be in the future");
        }

        try {
            // Generate task ID
            String taskId = UUID.randomUUID().toString();

            // Create prioritized task
            PrioritizedTask prioritizedTask = new PrioritizedTask(taskId, runnable, options.getPriority());

            // Calculate delay
            Duration delay = Duration.between(Instant.now(), executionTime);

            // Schedule the task
            ScheduledFuture<?> future = scheduledExecutor.schedule(
                    prioritizedTask, delay.toMillis(), TimeUnit.MILLISECONDS);

            // Create and store the task record
            TaskImpl task = new TaskImpl(
                    taskId,
                    options.getName(),
                    options.getDescription(),
                    options.getMetadata(),
                    future);
            task.setStatus(TaskStatus.SCHEDULED);
            task.setScheduledTime(executionTime);
            tasks.put(taskId, task);
            scheduledTasks.incrementAndGet();

            // Apply timeout if specified
            if (options.getTimeout().isPresent()) {
                Duration timeout = options.getTimeout().get();
                Instant timeoutInstant = executionTime.plus(timeout);
                Duration timeoutDelay = Duration.between(Instant.now(), timeoutInstant);

                scheduledExecutor.schedule(() -> {
                    if (!task.isComplete()) {
                        TaskImpl taskImpl = tasks.get(taskId);
                        if (taskImpl != null && !taskImpl.isComplete()) {
                            // Cancel the task
                            future.cancel(true);
                            taskImpl.setStatus(TaskStatus.TIMED_OUT);
                            taskImpl.setCompletionTime(Instant.now());
                            taskImpl.setErrorMessage("Task timed out after " + timeout.getSeconds() + " seconds");
                            
                            // Update statistics
                            failedTasks.incrementAndGet();
                            
                            // Notify listeners
                            notifyCompletionListeners(taskImpl);
                            
                            logger.warn("Task {} timed out after {} seconds", taskId, timeout.getSeconds());
                        }
                    }
                }, timeoutDelay.toMillis(), TimeUnit.MILLISECONDS);
            }

            logger.debug("Task {} scheduled for execution at {} with priority {}", 
                    taskId, executionTime, options.getPriority());
            return TaskResult.success(taskId, "Task scheduled successfully");
        } catch (Exception e) {
            logger.error("Error scheduling task: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to schedule task", e.getMessage());
        }
    }

    @Override
    public <T> TaskResult scheduleTask(Callable<T> callable, Instant executionTime) {
        return scheduleTask(callable, executionTime, createTaskOptionsBuilder().build());
    }

    @Override
    public <T> TaskResult scheduleTask(Callable<T> callable, Instant executionTime, TaskOptions options) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before scheduling tasks");
        }

        if (callable == null || executionTime == null) {
            return TaskResult.failure(null, "Invalid parameters",
                    "Callable and execution time cannot be null");
        }

        try {
            // Generate task ID
            String taskId = UUID.randomUUID().toString();

            // Create a wrapper runnable that executes the callable and stores the result
            Runnable taskWrapper = () -> {
                TaskImpl taskImpl = tasks.get(taskId);
                if (taskImpl == null) {
                    logger.warn("Task {} not found for execution", taskId);
                    return;
                }

                try {
                    // Execute the callable and store the result
                    T result = callable.call();
                    taskImpl.setResult(result);
                } catch (Exception e) {
                    // Error is handled by the PrioritizedTask wrapper
                    throw new RuntimeException(e);
                }
            };

            // Schedule the task using the runnable scheduling method
            return scheduleTask(taskWrapper, executionTime, options);
        } catch (Exception e) {
            logger.error("Error scheduling callable task: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to schedule callable task", e.getMessage());
        }
    }

    @Override
    public TaskResult getTask(String taskId) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before getting tasks");
        }

        if (taskId == null) {
            return TaskResult.failure(null, "Invalid parameters", "Task ID cannot be null");
        }

        TaskImpl task = tasks.get(taskId);
        if (task == null) {
            return TaskResult.failure(taskId, "Task not found", "No task exists with ID '" + taskId + "'");
        }

        return TaskResult.success(taskId, "Task found", Map.of("task", task));
    }

    @Override
    public TaskResult cancelTask(String taskId, boolean mayInterruptIfRunning) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before cancelling tasks");
        }

        if (taskId == null) {
            return TaskResult.failure(null, "Invalid parameters", "Task ID cannot be null");
        }

        TaskImpl task = tasks.get(taskId);
        if (task == null) {
            return TaskResult.failure(taskId, "Task not found", "No task exists with ID '" + taskId + "'");
        }

        try {
            if (task.isComplete()) {
                return TaskResult.failure(taskId, "Failed to cancel task", "Task is already completed");
            }

            Future<?> future = task.getFuture();
            if (future == null) {
                return TaskResult.failure(taskId, "Failed to cancel task", "Task has no associated future");
            }

            boolean cancelled = future.cancel(mayInterruptIfRunning);
            if (cancelled) {
                task.setStatus(TaskStatus.CANCELLED);
                task.setCompletionTime(Instant.now());
                cancelledTasks.incrementAndGet();

                // Notify listeners
                notifyCompletionListeners(task);

                logger.info("Task {} cancelled successfully", taskId);
                return TaskResult.success(taskId, "Task cancelled successfully");
            } else {
                return TaskResult.failure(taskId, "Failed to cancel task", "Task could not be cancelled");
            }
        } catch (Exception e) {
            logger.error("Error cancelling task {}: {}", taskId, e.getMessage(), e);
            return TaskResult.failure(taskId, "Failed to cancel task", e.getMessage());
        }
    }

    @Override
    public TaskResult getAllTasks() {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before getting tasks");
        }

        List<Task> allTasks = new ArrayList<>(tasks.values());
        return TaskResult.success(null, "Tasks retrieved successfully", Map.of("tasks", allTasks));
    }

    @Override
    public TaskResult getTasksByStatus(TaskStatus status) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before getting tasks");
        }

        if (status == null) {
            return TaskResult.failure(null, "Invalid parameters", "Status cannot be null");
        }

        List<Task> filteredTasks = tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());

        return TaskResult.success(null, "Tasks retrieved successfully", Map.of("tasks", filteredTasks));
    }

    @Override
    public TaskResult registerCompletionListener(String taskId, Consumer<Task> listener) {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before registering listeners");
        }

        if (taskId == null || listener == null) {
            return TaskResult.failure(null, "Invalid parameters", "Task ID and listener cannot be null");
        }

        TaskImpl task = tasks.get(taskId);
        if (task == null) {
            return TaskResult.failure(taskId, "Task not found", "No task exists with ID '" + taskId + "'");
        }

        // If the task is already complete, call the listener immediately
        if (task.isComplete()) {
            try {
                listener.accept(task);
                return TaskResult.success(taskId, "Listener called immediately as task is already complete");
            } catch (Exception e) {
                logger.error("Error calling completion listener for task {}: {}", taskId, e.getMessage(), e);
                return TaskResult.failure(taskId, "Failed to call completion listener", e.getMessage());
            }
        }

        // Otherwise, register the listener for later notification
        TaskCompletionListener completionListener = new TaskCompletionListener(taskId, listener);
        completionListeners.add(completionListener);

        logger.debug("Completion listener registered for task {}", taskId);
        return TaskResult.success(taskId, "Completion listener registered successfully");
    }

    @Override
    public TaskResult getStatistics() {
        if (!initialized) {
            return TaskResult.failure(null, "Task execution system not initialized",
                    "Initialize the task execution system before getting statistics");
        }

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("submittedTasks", submittedTasks.get());
        statistics.put("scheduledTasks", scheduledTasks.get());
        statistics.put("runningTasks", runningTasks.get());
        statistics.put("completedTasks", completedTasks.get());
        statistics.put("failedTasks", failedTasks.get());
        statistics.put("cancelledTasks", cancelledTasks.get());
        statistics.put("totalTasks", tasks.size());
        statistics.put("activeThreads", executor.getActiveCount());
        statistics.put("poolSize", executor.getPoolSize());
        statistics.put("corePoolSize", executor.getCorePoolSize());
        statistics.put("maxPoolSize", executor.getMaximumPoolSize());
        statistics.put("queueSize", executor.getQueue().size());
        statistics.put("completedTaskCount", executor.getCompletedTaskCount());

        return TaskResult.success(null, "Statistics retrieved successfully", statistics);
    }

    @Override
    public TaskResult shutdown() {
        if (!initialized) {
            return TaskResult.success(null, "Task execution system not initialized");
        }

        try {
            executor.shutdown();
            scheduledExecutor.shutdown();
            initialized = false;

            logger.info("Task execution system shut down successfully");
            return TaskResult.success(null, "Task execution system shut down successfully");
        } catch (Exception e) {
            logger.error("Error during task execution system shutdown: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to shut down task execution system", e.getMessage());
        }
    }

    @Override
    public TaskResult shutdownAndWait(Duration timeout) {
        if (!initialized) {
            return TaskResult.success(null, "Task execution system not initialized");
        }

        if (timeout == null) {
            return TaskResult.failure(null, "Invalid parameters", "Timeout cannot be null");
        }

        try {
            executor.shutdown();
            scheduledExecutor.shutdown();

            boolean executorTerminated = executor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);
            boolean schedulerTerminated = scheduledExecutor.awaitTermination(timeout.toMillis(), TimeUnit.MILLISECONDS);

            if (!executorTerminated) {
                executor.shutdownNow();
            }

            if (!schedulerTerminated) {
                scheduledExecutor.shutdownNow();
            }

            initialized = false;

            if (executorTerminated && schedulerTerminated) {
                logger.info("Task execution system shut down successfully");
                return TaskResult.success(null, "Task execution system shut down successfully");
            } else {
                logger.warn("Task execution system did not terminate within the timeout period");
                return TaskResult.success(null, "Task execution system shut down but some tasks did not complete");
            }
        } catch (Exception e) {
            logger.error("Error during task execution system shutdown: {}", e.getMessage(), e);
            return TaskResult.failure(null, "Failed to shut down task execution system", e.getMessage());
        }
    }

    /**
     * Notifies completion listeners for a task.
     *
     * @param task The completed task
     */
    private void notifyCompletionListeners(TaskImpl task) {
        String taskId = task.getId();
        List<TaskCompletionListener> listeners = completionListeners.stream()
                .filter(listener -> listener.getTaskId().equals(taskId))
                .collect(Collectors.toList());

        // Remove matching listeners from the list
        completionListeners.removeAll(listeners);

        // Notify all matching listeners
        for (TaskCompletionListener listener : listeners) {
            try {
                listener.getListener().accept(task);
            } catch (Exception e) {
                logger.error("Error notifying completion listener for task {}: {}", taskId, e.getMessage(), e);
            }
        }
    }

    /**
     * Cleans up completed tasks that are older than 24 hours.
     */
    private void cleanupCompletedTasks() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(24));
        
        List<String> tasksToRemove = tasks.values().stream()
                .filter(Task::isComplete)
                .filter(task -> task.getCompletionTime().isPresent())
                .filter(task -> task.getCompletionTime().get().isBefore(cutoff))
                .map(Task::getId)
                .collect(Collectors.toList());

        if (!tasksToRemove.isEmpty()) {
            for (String taskId : tasksToRemove) {
                tasks.remove(taskId);
            }
            logger.debug("Cleaned up {} completed tasks older than 24 hours", tasksToRemove.size());
        }
    }
    
    // Special reflection method for accessing private fields (used for setting the future)
    private static class Field {
        private final java.lang.reflect.Field field;
        
        private Field(java.lang.reflect.Field field) {
            this.field = field;
        }
        
        public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
            return new Field(clazz.getDeclaredField(name));
        }
        
        public void setAccessible(boolean flag) {
            field.setAccessible(flag);
        }
        
        public void set(Object obj, Object value) throws IllegalAccessException {
            field.set(obj, value);
        }
    }
}