/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.Task;
import org.s8r.application.port.TaskExecutionPort.TaskOptions;
import org.s8r.application.port.TaskExecutionPort.TaskOptionsBuilder;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;
import org.s8r.test.annotation.UnitTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@UnitTest
public class TaskExecutionServiceTest {

    private TaskExecutionPort mockTaskExecutionPort;
    private LoggerPort mockLogger;
    private TaskExecutionService service;
    private TaskOptionsBuilder mockOptionsBuilder;
    private TaskOptions mockOptions;

    @BeforeEach
    void setUp() {
        mockTaskExecutionPort = mock(TaskExecutionPort.class);
        mockLogger = mock(LoggerPort.class);
        mockOptionsBuilder = mock(TaskOptionsBuilder.class);
        mockOptions = mock(TaskOptions.class);
        
        when(mockTaskExecutionPort.createTaskOptionsBuilder()).thenReturn(mockOptionsBuilder);
        when(mockOptionsBuilder.build()).thenReturn(mockOptions);
        when(mockOptionsBuilder.name(anyString())).thenReturn(mockOptionsBuilder);
        when(mockOptionsBuilder.description(anyString())).thenReturn(mockOptionsBuilder);
        when(mockOptionsBuilder.priority(any())).thenReturn(mockOptionsBuilder);
        when(mockOptionsBuilder.timeout(any())).thenReturn(mockOptionsBuilder);
        
        service = new TaskExecutionService(mockTaskExecutionPort, mockLogger);
    }

    @Test
    void initialize_shouldCallPortInitialize() {
        // Arrange
        when(mockTaskExecutionPort.initialize()).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Initialized successfully")
        );

        // Act
        boolean result = service.initialize();

        // Assert
        assertTrue(result);
        verify(mockTaskExecutionPort).initialize();
        verify(mockLogger).info(anyString());
        verify(mockLogger, never()).error(anyString(), anyString());
    }

    @Test
    void initialize_shouldLogErrorOnFailure() {
        // Arrange
        when(mockTaskExecutionPort.initialize()).thenReturn(
                TaskExecutionPort.TaskResult.failure(null, "Initialization failed", "Test error")
        );

        // Act
        boolean result = service.initialize();

        // Assert
        assertFalse(result);
        verify(mockTaskExecutionPort).initialize();
        verify(mockLogger).error(contains("Failed to initialize"), eq("Test error"));
    }

    @Test
    void executeTask_shouldReturnTaskIdOnSuccess() {
        // Arrange
        Runnable mockRunnable = mock(Runnable.class);
        String taskId = "task123";
        
        when(mockTaskExecutionPort.submitTask(any(Runnable.class), any(TaskOptions.class))).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task submitted successfully")
        );

        // Act
        Optional<String> result = service.executeTask(mockRunnable, mockOptions);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get());
        verify(mockTaskExecutionPort).submitTask(mockRunnable, mockOptions);
    }

    @Test
    void executeTask_shouldLogWarningAndReturnEmptyOnFailure() {
        // Arrange
        Runnable mockRunnable = mock(Runnable.class);
        
        when(mockTaskExecutionPort.submitTask(any(Runnable.class), any(TaskOptions.class))).thenReturn(
                TaskExecutionPort.TaskResult.failure(null, "Submission failed", "Test error")
        );

        // Act
        Optional<String> result = service.executeTask(mockRunnable, mockOptions);

        // Assert
        assertFalse(result.isPresent());
        verify(mockTaskExecutionPort).submitTask(mockRunnable, mockOptions);
        verify(mockLogger).warn(anyString(), eq("Test error"));
    }

    @Test
    void executeTask_withCallableShouldReturnTaskIdOnSuccess() {
        // Arrange
        Callable<String> mockCallable = mock(Callable.class);
        String taskId = "task123";
        
        when(mockTaskExecutionPort.submitTask(any(Callable.class), any(TaskOptions.class))).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task submitted successfully")
        );

        // Act
        Optional<String> result = service.executeTask(mockCallable, mockOptions);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get());
        verify(mockTaskExecutionPort).submitTask(mockCallable, mockOptions);
    }

    @Test
    void scheduleTask_shouldReturnTaskIdOnSuccess() {
        // Arrange
        Runnable mockRunnable = mock(Runnable.class);
        String taskId = "task123";
        Instant executionTime = Instant.now().plusSeconds(60);
        
        when(mockTaskExecutionPort.scheduleTask(any(Runnable.class), any(Instant.class), any(TaskOptions.class))).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task scheduled successfully")
        );

        // Act
        Optional<String> result = service.scheduleTask(mockRunnable, executionTime, mockOptions);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get());
        verify(mockTaskExecutionPort).scheduleTask(mockRunnable, executionTime, mockOptions);
    }

    @Test
    void scheduleTask_shouldReturnEmptyForPastExecutionTime() {
        // Arrange
        Runnable mockRunnable = mock(Runnable.class);
        Instant pastTime = Instant.now().minusSeconds(60);

        // Act
        Optional<String> result = service.scheduleTask(mockRunnable, pastTime, mockOptions);

        // Assert
        assertFalse(result.isPresent());
        verify(mockTaskExecutionPort, never()).scheduleTask(any(Runnable.class), any(Instant.class), any(TaskOptions.class));
        verify(mockLogger).warn(contains("Cannot schedule task in the past"), eq(pastTime));
    }

    @Test
    void scheduleTask_withCallableShouldReturnTaskIdOnSuccess() {
        // Arrange
        Callable<String> mockCallable = mock(Callable.class);
        String taskId = "task123";
        Instant executionTime = Instant.now().plusSeconds(60);
        
        when(mockTaskExecutionPort.scheduleTask(any(Callable.class), any(Instant.class), any(TaskOptions.class))).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task scheduled successfully")
        );

        // Act
        Optional<String> result = service.scheduleTask(mockCallable, executionTime, mockOptions);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(taskId, result.get());
        verify(mockTaskExecutionPort).scheduleTask(mockCallable, executionTime, mockOptions);
    }

    @Test
    void getTask_shouldReturnTaskOnSuccess() {
        // Arrange
        String taskId = "task123";
        Task mockTask = mock(Task.class);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("task", mockTask);
        
        when(mockTaskExecutionPort.getTask(taskId)).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task found", attributes)
        );

        // Act
        Optional<Task> result = service.getTask(taskId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockTask, result.get());
        verify(mockTaskExecutionPort).getTask(taskId);
    }

    @Test
    void getTask_shouldReturnEmptyOnNotFound() {
        // Arrange
        String taskId = "nonExistentTask";
        
        when(mockTaskExecutionPort.getTask(taskId)).thenReturn(
                TaskExecutionPort.TaskResult.failure(taskId, "Task not found", "No task exists with ID")
        );

        // Act
        Optional<Task> result = service.getTask(taskId);

        // Assert
        assertFalse(result.isPresent());
        verify(mockTaskExecutionPort).getTask(taskId);
        verify(mockLogger, never()).warn(anyString(), anyString(), anyString());
    }

    @Test
    void getTask_shouldLogWarningOnOtherErrors() {
        // Arrange
        String taskId = "task123";
        
        when(mockTaskExecutionPort.getTask(taskId)).thenReturn(
                TaskExecutionPort.TaskResult.failure(taskId, "Error getting task", "System error")
        );

        // Act
        Optional<Task> result = service.getTask(taskId);

        // Assert
        assertFalse(result.isPresent());
        verify(mockTaskExecutionPort).getTask(taskId);
        verify(mockLogger).warn(anyString(), eq(taskId), anyString());
    }

    @Test
    void cancelTask_shouldReturnTrueOnSuccess() {
        // Arrange
        String taskId = "task123";
        
        when(mockTaskExecutionPort.cancelTask(eq(taskId), anyBoolean())).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task cancelled successfully")
        );

        // Act
        boolean result = service.cancelTask(taskId);

        // Assert
        assertTrue(result);
        verify(mockTaskExecutionPort).cancelTask(taskId, true);
    }

    @Test
    void cancelTask_shouldReturnFalseAndLogOnFailure() {
        // Arrange
        String taskId = "task123";
        
        when(mockTaskExecutionPort.cancelTask(eq(taskId), anyBoolean())).thenReturn(
                TaskExecutionPort.TaskResult.failure(taskId, "Failed to cancel task", "Task already completed")
        );

        // Act
        boolean result = service.cancelTask(taskId);

        // Assert
        assertFalse(result);
        verify(mockTaskExecutionPort).cancelTask(taskId, true);
        verify(mockLogger).warn(anyString(), eq(taskId), anyString());
    }

    @Test
    void getAllTasks_shouldReturnTasksOnSuccess() {
        // Arrange
        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);
        List<Task> tasks = new ArrayList<>();
        tasks.add(mockTask1);
        tasks.add(mockTask2);
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("tasks", tasks);
        
        when(mockTaskExecutionPort.getAllTasks()).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Tasks retrieved successfully", attributes)
        );

        // Act
        List<Task> result = service.getAllTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(mockTask1));
        assertTrue(result.contains(mockTask2));
        verify(mockTaskExecutionPort).getAllTasks();
    }

    @Test
    void getAllTasks_shouldReturnEmptyListAndLogOnFailure() {
        // Arrange
        when(mockTaskExecutionPort.getAllTasks()).thenReturn(
                TaskExecutionPort.TaskResult.failure(null, "Failed to retrieve tasks", "System error")
        );

        // Act
        List<Task> result = service.getAllTasks();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockTaskExecutionPort).getAllTasks();
        verify(mockLogger).warn(anyString(), anyString());
    }

    @Test
    void getTasksByStatus_shouldReturnTasksOnSuccess() {
        // Arrange
        TaskStatus status = TaskStatus.RUNNING;
        Task mockTask1 = mock(Task.class);
        Task mockTask2 = mock(Task.class);
        List<Task> tasks = new ArrayList<>();
        tasks.add(mockTask1);
        tasks.add(mockTask2);
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("tasks", tasks);
        
        when(mockTaskExecutionPort.getTasksByStatus(status)).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Tasks retrieved successfully", attributes)
        );

        // Act
        List<Task> result = service.getTasksByStatus(status);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(mockTask1));
        assertTrue(result.contains(mockTask2));
        verify(mockTaskExecutionPort).getTasksByStatus(status);
    }

    @Test
    void onTaskCompletion_shouldReturnTrueOnSuccess() {
        // Arrange
        String taskId = "task123";
        Consumer<Task> listener = task -> {};
        
        when(mockTaskExecutionPort.registerCompletionListener(taskId, listener)).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Listener registered successfully")
        );

        // Act
        boolean result = service.onTaskCompletion(taskId, listener);

        // Assert
        assertTrue(result);
        verify(mockTaskExecutionPort).registerCompletionListener(taskId, listener);
    }

    @Test
    void onTaskCompletion_shouldReturnFalseAndLogOnFailure() {
        // Arrange
        String taskId = "task123";
        Consumer<Task> listener = task -> {};
        
        when(mockTaskExecutionPort.registerCompletionListener(taskId, listener)).thenReturn(
                TaskExecutionPort.TaskResult.failure(taskId, "Failed to register listener", "Task not found")
        );

        // Act
        boolean result = service.onTaskCompletion(taskId, listener);

        // Assert
        assertFalse(result);
        verify(mockTaskExecutionPort).registerCompletionListener(taskId, listener);
        verify(mockLogger).warn(anyString(), eq(taskId), anyString());
    }

    @Test
    void executeAsync_shouldReturnCompletableFuture() throws Exception {
        // Arrange
        Callable<String> mockCallable = () -> "result";
        String taskId = "task123";
        
        when(mockTaskExecutionPort.submitTask(any(Callable.class))).thenReturn(
                TaskExecutionPort.TaskResult.success(taskId, "Task submitted successfully")
        );
        
        // We need to set up the completion listener registration to actually call the listener
        doAnswer(invocation -> {
            Consumer<Task> listener = invocation.getArgument(1);
            
            // Create a mock completed task
            Task mockTask = mock(Task.class);
            when(mockTask.getStatus()).thenReturn(TaskStatus.COMPLETED);
            when(mockTask.getResult()).thenReturn(Optional.of("result"));
            
            // Call the listener
            listener.accept(mockTask);
            
            return TaskExecutionPort.TaskResult.success(taskId, "Listener called");
        }).when(mockTaskExecutionPort).registerCompletionListener(eq(taskId), any(Consumer.class));

        // Act
        CompletableFuture<String> future = service.executeAsync(mockCallable);
        String result = future.get(1, TimeUnit.SECONDS); // Short timeout for test

        // Assert
        assertEquals("result", result);
        verify(mockTaskExecutionPort).submitTask(any(Callable.class));
        verify(mockTaskExecutionPort).registerCompletionListener(eq(taskId), any(Consumer.class));
    }

    @Test
    void getStatistics_shouldReturnStatsOnSuccess() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("runningTasks", 5);
        stats.put("completedTasks", 10);
        
        when(mockTaskExecutionPort.getStatistics()).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Statistics retrieved successfully", stats)
        );

        // Act
        Map<String, Object> result = service.getStatistics();

        // Assert
        assertEquals(stats, result);
        verify(mockTaskExecutionPort).getStatistics();
    }

    @Test
    void getStatistics_shouldReturnEmptyMapAndLogOnFailure() {
        // Arrange
        when(mockTaskExecutionPort.getStatistics()).thenReturn(
                TaskExecutionPort.TaskResult.failure(null, "Failed to retrieve statistics", "System error")
        );

        // Act
        Map<String, Object> result = service.getStatistics();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockTaskExecutionPort).getStatistics();
        verify(mockLogger).warn(anyString(), anyString());
    }

    @Test
    void shutdown_shouldCallPortShutdown() {
        // Arrange
        when(mockTaskExecutionPort.shutdown()).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Shutdown successful")
        );

        // Act
        boolean result = service.shutdown();

        // Assert
        assertTrue(result);
        verify(mockTaskExecutionPort).shutdown();
        verify(mockLogger).info(anyString());
        verify(mockLogger, never()).error(anyString(), anyString());
    }

    @Test
    void shutdown_shouldLogErrorOnFailure() {
        // Arrange
        when(mockTaskExecutionPort.shutdown()).thenReturn(
                TaskExecutionPort.TaskResult.failure(null, "Shutdown failed", "Test error")
        );

        // Act
        boolean result = service.shutdown();

        // Assert
        assertFalse(result);
        verify(mockTaskExecutionPort).shutdown();
        verify(mockLogger).error(contains("Failed to shut down"), eq("Test error"));
    }

    @Test
    void shutdownAndWait_shouldCallPortShutdownAndWait() {
        // Arrange
        Duration timeout = Duration.ofSeconds(5);
        when(mockTaskExecutionPort.shutdownAndWait(timeout)).thenReturn(
                TaskExecutionPort.TaskResult.success(null, "Shutdown successful")
        );

        // Act
        boolean result = service.shutdownAndWait(timeout);

        // Assert
        assertTrue(result);
        verify(mockTaskExecutionPort).shutdownAndWait(timeout);
        verify(mockLogger).info(anyString());
        verify(mockLogger, never()).error(anyString(), anyString());
    }
}