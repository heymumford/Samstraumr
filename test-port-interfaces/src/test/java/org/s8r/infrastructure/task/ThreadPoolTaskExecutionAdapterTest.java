/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.task;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.TaskResult;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for the ThreadPoolTaskExecutionAdapter.
 */
class ThreadPoolTaskExecutionAdapterTest {

    private ThreadPoolTaskExecutionAdapter adapter;
    
    @BeforeEach
    void setUp() {
        adapter = new ThreadPoolTaskExecutionAdapter(2);
    }
    
    @AfterEach
    void tearDown() {
        adapter.shutdown(false);
    }
    
    @Test
    @DisplayName("Should execute a callable task asynchronously")
    void testExecuteAsyncCallable() throws Exception {
        // Create a simple callable that returns a value
        Future<Integer> future = adapter.executeAsync(() -> {
            Thread.sleep(100); // Simulate some work
            return 42;
        });
        
        // Verify the result
        assertEquals(42, future.get(), "The future should return the expected value");
    }
    
    @Test
    @DisplayName("Should execute a callable task with timeout")
    void testExecuteAsyncCallableWithTimeout() throws Exception {
        // Create a callable that completes within the timeout
        Future<String> future = adapter.executeAsync(() -> {
            Thread.sleep(100); // Simulate some work
            return "success";
        }, Duration.ofSeconds(1));
        
        // Verify the result
        assertEquals("success", future.get(), "The future should return the expected value");
    }
    
    @Test
    @DisplayName("Should cancel a long-running task with timeout")
    void testExecuteAsyncCallableWithTimeoutCancellation() throws Exception {
        // Create a callable that takes longer than the timeout
        Future<String> future = adapter.executeAsync(() -> {
            Thread.sleep(500); // Simulate long-running work
            return "success";
        }, Duration.ofMillis(100));
        
        // Wait for the task to be canceled
        Thread.sleep(200);
        
        // Verify the task was canceled
        assertTrue(future.isCancelled(), "The task should be canceled due to timeout");
    }
    
    @Test
    @DisplayName("Should execute a runnable task asynchronously")
    void testExecuteAsyncRunnable() throws Exception {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Create a simple runnable
        CompletableFuture<Void> future = adapter.executeAsync(() -> {
            try {
                Thread.sleep(100); // Simulate some work
                taskExecuted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Wait for the task to complete
        future.get();
        
        // Verify the task executed
        assertTrue(taskExecuted.get(), "The task should have been executed");
    }
    
    @Test
    @DisplayName("Should schedule a callable task for future execution")
    void testScheduleCallableTask() throws Exception {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Schedule a task to run after a delay
        TaskResult<Boolean> result = adapter.scheduleTask(() -> {
            taskExecuted.set(true);
            return true;
        }, Duration.ofMillis(100));
        
        // Verify the result of scheduling
        assertTrue(result.isSuccessful(), "Scheduling should be successful");
        assertEquals(TaskStatus.SCHEDULED, result.getStatus(), "Task status should be SCHEDULED");
        assertNotNull(result.getTaskId(), "Task ID should not be null");
        
        // Wait for the task to execute
        Thread.sleep(200);
        
        // Verify the task executed
        assertTrue(taskExecuted.get(), "The scheduled task should have been executed");
    }
    
    @Test
    @DisplayName("Should schedule a runnable task for future execution")
    void testScheduleRunnableTask() throws Exception {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Schedule a task to run after a delay
        TaskResult<Void> result = adapter.scheduleTask(() -> {
            taskExecuted.set(true);
        }, Duration.ofMillis(100));
        
        // Verify the result of scheduling
        assertTrue(result.isSuccessful(), "Scheduling should be successful");
        assertEquals(TaskStatus.SCHEDULED, result.getStatus(), "Task status should be SCHEDULED");
        assertNotNull(result.getTaskId(), "Task ID should not be null");
        
        // Wait for the task to execute
        Thread.sleep(200);
        
        // Verify the task executed
        assertTrue(taskExecuted.get(), "The scheduled task should have been executed");
    }
    
    @Test
    @DisplayName("Should cancel a scheduled task")
    void testCancelTask() throws Exception {
        AtomicBoolean taskExecuted = new AtomicBoolean(false);
        
        // Schedule a task to run after a delay
        TaskResult<Void> scheduleResult = adapter.scheduleTask(() -> {
            taskExecuted.set(true);
        }, Duration.ofMillis(200));
        
        String taskId = scheduleResult.getTaskId();
        
        // Cancel the task before it executes
        boolean canceled = adapter.cancelTask(taskId);
        
        // Verify the task was canceled
        assertTrue(canceled, "The task should be successfully canceled");
        
        // Wait to ensure the task doesn't execute
        Thread.sleep(300);
        
        // Verify the task did not execute
        assertFalse(taskExecuted.get(), "The canceled task should not have been executed");
        
        // Verify task status
        TaskResult<Void> statusResult = adapter.getTaskStatus(taskId);
        assertEquals(TaskStatus.CANCELED, statusResult.getAttributes().get("status"), 
                "Task status should be CANCELED");
    }
    
    @Test
    @DisplayName("Should handle task failures")
    void testTaskFailure() throws Exception {
        // Create a callable that throws an exception
        Future<String> future = adapter.executeAsync(() -> {
            throw new RuntimeException("Task failed");
        });
        
        // Wait for the task to complete (with exception)
        Exception exception = assertThrows(ExecutionException.class, () -> future.get(), 
                "ExecutionException should be thrown");
        
        // Verify the cause of the exception
        assertTrue(exception.getCause() instanceof RuntimeException, 
                "The cause should be the thrown RuntimeException");
        assertEquals("Task failed", exception.getCause().getMessage(), 
                "Exception message should match");
    }
    
    @Test
    @DisplayName("Should get task status")
    void testGetTaskStatus() throws Exception {
        // Schedule a task
        TaskResult<Integer> scheduleResult = adapter.scheduleTask(() -> {
            Thread.sleep(100);
            return 42;
        }, Duration.ofMillis(10));
        
        String taskId = scheduleResult.getTaskId();
        
        // Get initial status
        TaskResult<Void> initialStatus = adapter.getTaskStatus(taskId);
        assertTrue(initialStatus.isSuccessful(), "Status retrieval should be successful");
        assertEquals(TaskStatus.SCHEDULED, initialStatus.getAttributes().get("status"), 
                "Initial status should be SCHEDULED");
        
        // Wait for task to execute
        Thread.sleep(150);
        
        // Get final status
        TaskResult<Void> finalStatus = adapter.getTaskStatus(taskId);
        assertTrue(finalStatus.isSuccessful(), "Status retrieval should be successful");
        assertEquals(TaskStatus.COMPLETED, finalStatus.getAttributes().get("status"), 
                "Final status should be COMPLETED");
    }
    
    @Test
    @DisplayName("Should handle unknown task ID")
    void testGetNonExistentTaskStatus() {
        // Try to get status for a non-existent task
        TaskResult<Void> result = adapter.getTaskStatus("non-existent-id");
        
        // Verify the result
        assertFalse(result.isSuccessful(), "Status retrieval should fail");
        assertEquals(TaskStatus.FAILED, result.getStatus(), "Status should be FAILED");
        assertTrue(result.getReason().isPresent(), "Reason should be present");
        assertEquals("No task with the specified ID exists", result.getReason().get(), 
                "Reason should explain the task doesn't exist");
    }
    
    @Test
    @DisplayName("Should handle multiple concurrent tasks")
    void testMultipleConcurrentTasks() throws Exception {
        int taskCount = 10;
        AtomicInteger completedCount = new AtomicInteger(0);
        
        // Execute multiple tasks 
        for (int i = 0; i < taskCount; i++) {
            adapter.executeAsync(() -> {
                try {
                    Thread.sleep(50); // Simulate some work
                    completedCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Wait for all tasks to complete
        Thread.sleep(500);
        
        // Verify all tasks completed
        assertEquals(taskCount, completedCount.get(), "All tasks should have completed");
    }
    
    @Test
    @DisplayName("Should shut down gracefully")
    void testShutdownGracefully() throws Exception {
        // Execute some tasks
        AtomicInteger completedCount = new AtomicInteger(0);
        
        for (int i = 0; i < 5; i++) {
            adapter.executeAsync(() -> {
                try {
                    Thread.sleep(50);
                    completedCount.incrementAndGet();
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        }
        
        // Shut down gracefully
        TaskResult<Void> result = adapter.shutdown(true);
        
        // Verify the result
        assertTrue(result.isSuccessful(), "Shutdown should be successful");
        
        // Verify all tasks completed
        assertEquals(5, completedCount.get(), "All tasks should have completed");
    }
    
    @Test
    @DisplayName("Should shut down immediately")
    void testShutdownImmediately() throws Exception {
        // Execute multiple long-running tasks to ensure they all get cancelled
        final int taskCount = 5;
        final AtomicBoolean[] taskCompletions = new AtomicBoolean[taskCount];
        final Future<?>[] futures = new Future<?>[taskCount];
        
        for (int i = 0; i < taskCount; i++) {
            final int index = i;
            taskCompletions[i] = new AtomicBoolean(false);
            
            futures[i] = adapter.executeAsync(() -> {
                try {
                    // Long-running task that should be interrupted
                    for (int j = 0; j < 50; j++) {
                        Thread.sleep(200);
                        // Check if interrupted
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException("Task interrupted");
                        }
                    }
                    taskCompletions[index].set(true); // Should not reach here if properly cancelled
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        }
        
        // Shut down immediately
        TaskResult<Void> result = adapter.shutdown(false);
        
        // Verify the result
        assertTrue(result.isSuccessful(), "Immediate shutdown should be successful");
        
        // Wait a bit to allow cancellation to take effect
        Thread.sleep(500);
        
        // Verify all tasks were cancelled or done
        boolean allCancelledOrDone = true;
        for (int i = 0; i < taskCount; i++) {
            if (!futures[i].isCancelled() && !futures[i].isDone()) {
                allCancelledOrDone = false;
                break;
            }
        }
        
        assertTrue(allCancelledOrDone, "All tasks should be cancelled or done after immediate shutdown");
        
        // No task should have completed normally
        for (AtomicBoolean completion : taskCompletions) {
            assertFalse(completion.get(), "Task should not have completed normally");
        }
    }
}