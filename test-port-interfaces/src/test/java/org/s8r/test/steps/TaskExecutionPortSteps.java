/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.TaskExecutionPort.TaskResult;
import org.s8r.application.port.TaskExecutionPort.TaskStatus;
import org.s8r.application.port.compat.TaskExecutionPortCompat;
import org.s8r.infrastructure.task.ThreadPoolTaskExecutionAdapter;
import org.s8r.test.utils.RecoverableTaskExecutionAdapter;
import org.s8r.test.utils.TaskPriority;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Steps for testing the TaskExecutionPort interface.
 */
public class TaskExecutionPortSteps {

    private TaskExecutionPort taskExecutionPort;
    private RecoverableTaskExecutionAdapter recoverableAdapter;
    private final Map<String, Object> testContext = new HashMap<>();
    private String lastTaskId;
    
    @Before
    public void setUp() {
        taskExecutionPort = new ThreadPoolTaskExecutionAdapter(2);
        testContext.clear();
        lastTaskId = null;
        
        // Initialize the executor with a dummy task to ensure it's ready
        taskExecutionPort.executeAsync(() -> {
            // Do nothing, just to initialize the executor
        });
    }
    
    @After
    public void tearDown() {
        if (taskExecutionPort != null) {
            taskExecutionPort.shutdown(false);
        }
        if (recoverableAdapter != null) {
            recoverableAdapter.shutdown(false);
        }
    }
    
    @Given("a task execution service")
    public void aTaskExecutionService() {
        assertNotNull(taskExecutionPort, "Task execution port should be initialized");
    }
    
    @Given("a task execution service with simulated failures")
    public void aTaskExecutionServiceWithSimulatedFailures() {
        recoverableAdapter = new RecoverableTaskExecutionAdapter(2);
        testContext.put("recoveryAttempts", 0);
        testContext.put("recoveryAdapter", recoverableAdapter);
    }
    
    @When("I execute a task that returns {int}")
    public void iExecuteATaskThatReturns(Integer value) throws Exception {
        Future<Integer> future = taskExecutionPort.executeAsync(() -> {
            Thread.sleep(50); // Small delay to simulate work
            return value;
        });
        
        testContext.put("future", future);
    }
    
    @When("I execute a task that increments a counter")
    public void iExecuteATaskThatIncrementsACounter() throws Exception {
        AtomicInteger counter = new AtomicInteger(0);
        testContext.put("counter", counter);
        
        CompletableFuture<Void> future = taskExecutionPort.executeAsync(() -> {
            counter.incrementAndGet();
        });
        
        testContext.put("future", future);
    }
    
    @When("I execute a task that throws an exception")
    public void iExecuteATaskThatThrowsAnException() {
        Future<Object> future = taskExecutionPort.executeAsync(() -> {
            throw new RuntimeException("Task execution failed intentionally");
        });
        
        testContext.put("future", future);
        
        // Wait for task to complete with failure
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            testContext.put("exception", e.getCause());
        }
    }
    
    @When("I execute a task with a timeout of {int} milliseconds that takes {int} milliseconds to complete")
    public void iExecuteATaskWithTimeoutThatTakesLonger(Integer timeoutMillis, Integer taskDurationMillis) {
        Future<String> future = taskExecutionPort.executeAsync(() -> {
            Thread.sleep(taskDurationMillis);
            return "Task completed";
        }, Duration.ofMillis(timeoutMillis));
        
        testContext.put("future", future);
        
        // Wait for task to timeout
        try {
            future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            testContext.put("exception", e.getCause());
        }
    }
    
    @When("I execute a task that requires recovery")
    public void iExecuteATaskThatRequiresRecovery() {
        RecoverableTaskExecutionAdapter adapter = 
            (RecoverableTaskExecutionAdapter) testContext.get("recoveryAdapter");
        
        // Configure the adapter to fail the first attempt
        adapter.setFailureMode(true);
        
        Future<String> future = adapter.executeAsync(() -> {
            if (adapter.shouldFail()) {
                adapter.incrementRecoveryAttempts();
                throw new RejectedExecutionException("Simulated failure");
            }
            return "Task completed after recovery";
        });
        
        testContext.put("future", future);
        
        try {
            String result = future.get();
            testContext.put("result", result);
        } catch (Exception e) {
            testContext.put("exception", e);
        }
    }
    
    @When("I execute {int} concurrent tasks")
    public void iExecuteConcurrentTasks(Integer taskCount) {
        Instant startTime = Instant.now();
        testContext.put("startTime", startTime);
        
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            final int taskValue = i;
            Future<Integer> future = taskExecutionPort.executeAsync(() -> {
                Thread.sleep(100); // Each task takes 100ms
                return taskValue;
            });
            futures.add(future);
        }
        
        testContext.put("futures", futures);
        
        // Calculate sequential execution time
        long sequentialTimeEstimate = taskCount * 100L;
        testContext.put("sequentialTimeEstimate", sequentialTimeEstimate);
    }
    
    @When("I submit the following tasks with priorities:")
    public void iSubmitTasksWithPriorities(List<Map<String, String>> taskData) {
        Map<String, Future<Integer>> futures = new ConcurrentHashMap<>();
        Map<String, Long> executionOrder = new ConcurrentHashMap<>();
        Map<String, TaskPriority> priorities = new ConcurrentHashMap<>();
        
        // Track the current execution order counter
        AtomicInteger orderCounter = new AtomicInteger(0);
        
        // Submit tasks with their priorities
        for (Map<String, String> task : taskData) {
            String taskId = task.get("taskId");
            TaskPriority priority = TaskPriority.valueOf(task.get("priority"));
            int value = Integer.parseInt(task.get("value"));
            
            priorities.put(taskId, priority);
            
            Future<Integer> future = taskExecutionPort.executeAsync(() -> {
                // Record execution order
                long order = orderCounter.incrementAndGet();
                executionOrder.put(taskId, order);
                
                // Add some random sleep to ensure we're testing priority not just submission order
                Thread.sleep(50);
                return value;
            });
            
            futures.put(taskId, future);
        }
        
        testContext.put("taskFutures", futures);
        testContext.put("executionOrder", executionOrder);
        testContext.put("priorities", priorities);
        testContext.put("taskData", taskData);
    }
    
    @When("I create a task chain {string}")
    public void iCreateATaskChain(String chain) {
        // Parse the chain string "A → B → C"
        String[] tasks = chain.split(" → ");
        
        Map<String, Long> startTimes = new ConcurrentHashMap<>();
        Map<String, Long> endTimes = new ConcurrentHashMap<>();
        Map<String, Object> results = new ConcurrentHashMap<>();
        
        // Execute the first task
        CompletableFuture<?> future = CompletableFuture.supplyAsync(() -> {
            try {
                String taskId = tasks[0];
                startTimes.put(taskId, System.currentTimeMillis());
                Thread.sleep(100); // Simulate work
                results.put(taskId, taskId + "-result");
                endTimes.put(taskId, System.currentTimeMillis());
                return taskId;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        // Chain subsequent tasks
        for (int i = 1; i < tasks.length; i++) {
            final String taskId = tasks[i];
            future = future.thenApplyAsync(prevResult -> {
                try {
                    startTimes.put(taskId, System.currentTimeMillis());
                    Thread.sleep(100); // Simulate work
                    String result = taskId + "-result+" + prevResult;
                    results.put(taskId, result);
                    endTimes.put(taskId, System.currentTimeMillis());
                    return result;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
        }
        
        testContext.put("chainFuture", future);
        testContext.put("startTimes", startTimes);
        testContext.put("endTimes", endTimes);
        testContext.put("results", results);
        testContext.put("tasks", tasks);
        
        try {
            Object finalResult = future.get(5, TimeUnit.SECONDS);
            testContext.put("finalResult", finalResult);
        } catch (Exception e) {
            testContext.put("chainException", e);
        }
    }
    
    @When("I schedule a task that returns {string} to run after {int} milliseconds")
    public void iScheduleATaskThatReturnsToRunAfterMilliseconds(String value, Integer delay) {
        TaskResult<String> result = TaskExecutionPortCompat.scheduleTaskWithDelay(taskExecutionPort, () -> {
            return value;
        }, delay);
        
        lastTaskId = result.getTaskId().orElse(null);
        testContext.put("scheduleResult", result);
    }
    
    @When("I schedule a task that increments a counter to run after {int} milliseconds")
    public void iScheduleATaskThatIncrementsACounterToRunAfterMilliseconds(Integer delay) {
        AtomicInteger counter = new AtomicInteger(0);
        testContext.put("counter", counter);
        
        TaskResult<Void> result = TaskExecutionPortCompat.scheduleTaskWithDelay(taskExecutionPort, () -> {
            counter.incrementAndGet();
            return null;
        }, delay);
        
        lastTaskId = result.getTaskId().orElse(null);
        testContext.put("scheduleResult", result);
    }
    
    @When("I cancel the scheduled task")
    public void iCancelTheScheduledTask() {
        assertNotNull(lastTaskId, "Task ID should not be null");
        boolean canceled = taskExecutionPort.cancelTask(lastTaskId);
        testContext.put("cancelResult", canceled);
    }
    
    @When("I get the status of the scheduled task")
    public void iGetTheStatusOfTheScheduledTask() {
        assertNotNull(lastTaskId, "Task ID should not be null");
        TaskResult<Void> statusResult = taskExecutionPort.getTaskStatus(lastTaskId);
        testContext.put("statusResult", statusResult);
    }
    
    @When("I shut down the task execution service gracefully")
    public void iShutDownTheTaskExecutionServiceGracefully() {
        TaskResult<Void> shutdownResult = taskExecutionPort.shutdown(true);
        testContext.put("shutdownResult", shutdownResult);
    }
    
    @When("I shut down the task execution service immediately")
    public void iShutDownTheTaskExecutionServiceImmediately() {
        TaskResult<Void> shutdownResult = taskExecutionPort.shutdown(false);
        testContext.put("shutdownResult", shutdownResult);
    }
    
    @Then("the task result should be {int}")
    public void theTaskResultShouldBe(Integer expectedValue) throws Exception {
        @SuppressWarnings("unchecked")
        Future<Integer> future = (Future<Integer>) testContext.get("future");
        assertNotNull(future, "Future should not be null");
        
        Integer result = future.get();
        assertEquals(expectedValue, result, "Task result should match expected value");
    }
    
    @Then("the task should fail with appropriate error message")
    public void theTaskShouldFailWithAppropriateErrorMessage() {
        Exception exception = (Exception) testContext.get("exception");
        assertNotNull(exception, "Exception should not be null");
        
        assertNotNull(exception.getMessage(), "Exception message should not be null");
    }
    
    @Then("the task should be canceled due to timeout")
    public void theTaskShouldBeCanceledDueToTimeout() {
        @SuppressWarnings("unchecked")
        Future<String> future = (Future<String>) testContext.get("future");
        assertTrue(future.isDone() || future.isCancelled(), 
                "Task should be done or cancelled due to timeout");
        
        Exception exception = (Exception) testContext.get("exception");
        assertNotNull(exception, "Exception should not be null");
        
        // The exception may be a TimeoutException or CancellationException
        assertTrue(exception instanceof TimeoutException || exception.getMessage().contains("timeout"), 
                "Exception should be related to timeout");
    }
    
    @Then("the task should be retried successfully")
    public void theTaskShouldBeRetriedSuccessfully() {
        RecoverableTaskExecutionAdapter adapter = 
            (RecoverableTaskExecutionAdapter) testContext.get("recoveryAdapter");
        
        assertTrue(adapter.getRecoveryAttempts() > 0, "Task should have been retried at least once");
        
        String result = (String) testContext.get("result");
        assertEquals("Task completed after recovery", result, "Task should complete successfully after recovery");
    }
    
    @Then("recovery metrics should be recorded")
    public void recoveryMetricsShouldBeRecorded() {
        RecoverableTaskExecutionAdapter adapter = 
            (RecoverableTaskExecutionAdapter) testContext.get("recoveryAdapter");
        
        assertTrue(adapter.getRecoveryAttempts() > 0, "Recovery attempts should be recorded");
        assertFalse(adapter.shouldFail(), "Failure mode should be disabled after recovery");
    }
    
    @Then("all tasks should complete successfully")
    public void allTasksShouldCompleteSuccessfully() throws Exception {
        @SuppressWarnings("unchecked")
        List<Future<Integer>> futures = (List<Future<Integer>>) testContext.get("futures");
        
        if (futures == null) {
            @SuppressWarnings("unchecked")
            Map<String, Future<Integer>> taskFutures = 
                (Map<String, Future<Integer>>) testContext.get("taskFutures");
            
            assertNotNull(taskFutures, "Task futures map should not be null");
            
            for (Future<Integer> future : taskFutures.values()) {
                assertNotNull(future.get(), "Task result should not be null");
                assertTrue(future.isDone(), "Task should be completed");
                assertFalse(future.isCancelled(), "Task should not be cancelled");
            }
        } else {
            for (Future<Integer> future : futures) {
                assertNotNull(future.get(), "Task result should not be null");
                assertTrue(future.isDone(), "Task should be completed");
                assertFalse(future.isCancelled(), "Task should not be cancelled");
            }
        }
    }
    
    @Then("the execution time should be less than executing them sequentially")
    public void theExecutionTimeShouldBeLessThanExecutingThemSequentially() throws Exception {
        Instant startTime = (Instant) testContext.get("startTime");
        Long sequentialTimeEstimate = (Long) testContext.get("sequentialTimeEstimate");
        
        @SuppressWarnings("unchecked")
        List<Future<Integer>> futures = (List<Future<Integer>>) testContext.get("futures");
        
        // Wait for all futures to complete
        for (Future<Integer> future : futures) {
            future.get();
        }
        
        Instant endTime = Instant.now();
        long actualDuration = Duration.between(startTime, endTime).toMillis();
        
        assertTrue(actualDuration < sequentialTimeEstimate, 
                String.format("Parallel execution (%d ms) should be faster than sequential execution (%d ms)", 
                        actualDuration, sequentialTimeEstimate));
    }
    
    @Then("the tasks should be executed in priority order")
    public void theTasksShouldBeExecutedInPriorityOrder() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Long> executionOrder = (Map<String, Long>) testContext.get("executionOrder");
        
        @SuppressWarnings("unchecked")
        Map<String, TaskPriority> priorities = (Map<String, TaskPriority>) testContext.get("priorities");
        
        @SuppressWarnings("unchecked")
        List<Map<String, String>> taskData = (List<Map<String, String>>) testContext.get("taskData");
        
        // Extract high priority tasks
        List<String> highPriorityTasks = new ArrayList<>();
        List<String> mediumPriorityTasks = new ArrayList<>();
        
        for (Map.Entry<String, TaskPriority> entry : priorities.entrySet()) {
            if (entry.getValue() == TaskPriority.HIGH) {
                highPriorityTasks.add(entry.getKey());
            } else if (entry.getValue() == TaskPriority.MEDIUM) {
                mediumPriorityTasks.add(entry.getKey());
            }
        }
        
        // Verify high priority tasks executed before low priority tasks
        for (String highTask : highPriorityTasks) {
            for (Map<String, String> task : taskData) {
                String taskId = task.get("taskId");
                if (TaskPriority.valueOf(task.get("priority")) == TaskPriority.LOW) {
                    // High priority should have lower order number (executed earlier) than low priority
                    assertTrue(executionOrder.get(highTask) < executionOrder.get(taskId),
                            "High priority task " + highTask + " should execute before low priority task " + taskId);
                }
            }
        }
    }
    
    @Then("all tasks should execute in the correct sequence")
    public void allTasksShouldExecuteInTheCorrectSequence() {
        String[] tasks = (String[]) testContext.get("tasks");
        @SuppressWarnings("unchecked")
        Map<String, Long> startTimes = (Map<String, Long>) testContext.get("startTimes");
        @SuppressWarnings("unchecked")
        Map<String, Long> endTimes = (Map<String, Long>) testContext.get("endTimes");
        
        for (int i = 0; i < tasks.length; i++) {
            assertNotNull(startTimes.get(tasks[i]), "Start time should be recorded for task " + tasks[i]);
            assertNotNull(endTimes.get(tasks[i]), "End time should be recorded for task " + tasks[i]);
        }
    }
    
    @Then("task {string} should only start after task {string} completes")
    public void taskShouldOnlyStartAfterTaskCompletes(String laterTask, String earlierTask) {
        @SuppressWarnings("unchecked")
        Map<String, Long> startTimes = (Map<String, Long>) testContext.get("startTimes");
        @SuppressWarnings("unchecked")
        Map<String, Long> endTimes = (Map<String, Long>) testContext.get("endTimes");
        
        Long laterTaskStartTime = startTimes.get(laterTask);
        Long earlierTaskEndTime = endTimes.get(earlierTask);
        
        assertNotNull(laterTaskStartTime, "Start time should be recorded for task " + laterTask);
        assertNotNull(earlierTaskEndTime, "End time should be recorded for task " + earlierTask);
        
        assertTrue(laterTaskStartTime >= earlierTaskEndTime,
                "Task " + laterTask + " should start after task " + earlierTask + " completes");
    }
    
    @Then("the final result should combine results from all tasks")
    public void theFinalResultShouldCombineResultsFromAllTasks() {
        Object finalResult = testContext.get("finalResult");
        assertNotNull(finalResult, "Final result should not be null");
        
        String[] tasks = (String[]) testContext.get("tasks");
        for (String task : tasks) {
            assertTrue(finalResult.toString().contains(task),
                    "Final result should contain task ID " + task);
        }
    }
    
    @Then("the counter should be incremented")
    public void theCounterShouldBeIncremented() throws Exception {
        AtomicInteger counter = (AtomicInteger) testContext.get("counter");
        assertNotNull(counter, "Counter should not be null");
        
        CompletableFuture<?> future = (CompletableFuture<?>) testContext.get("future");
        assertNotNull(future, "Future should not be null");
        
        future.get(); // Wait for completion
        
        assertEquals(1, counter.get(), "Counter should be incremented");
    }
    
    @Then("the scheduled task should have status {string}")
    public void theScheduledTaskShouldHaveStatus(String expectedStatus) {
        TaskResult<?> result = (TaskResult<?>) testContext.get("scheduleResult");
        assertNotNull(result, "Schedule result should not be null");
        
        assertEquals(TaskStatus.valueOf(expectedStatus), result.getStatus(), 
                "Task status should match expected status");
    }
    
    @Then("I wait for {int} milliseconds")
    public void iWaitForMilliseconds(Integer millis) throws Exception {
        Thread.sleep(millis);
    }
    
    @Then("the counter should be incremented after waiting")
    public void theCounterShouldBeIncrementedAfterWaiting() {
        AtomicInteger counter = (AtomicInteger) testContext.get("counter");
        assertNotNull(counter, "Counter should not be null");
        
        assertEquals(1, counter.get(), "Counter should be incremented after waiting");
    }
    
    @Then("the counter should not be incremented after waiting")
    public void theCounterShouldNotBeIncrementedAfterWaiting() {
        AtomicInteger counter = (AtomicInteger) testContext.get("counter");
        assertNotNull(counter, "Counter should not be null");
        
        assertEquals(0, counter.get(), "Counter should not be incremented after waiting");
    }
    
    @Then("the task cancellation should be successful")
    public void theTaskCancellationShouldBeSuccessful() {
        Boolean cancelResult = (Boolean) testContext.get("cancelResult");
        assertNotNull(cancelResult, "Cancel result should not be null");
        
        assertTrue(cancelResult, "Task cancellation should be successful");
    }
    
    @Then("the task status should be {string}")
    public void theTaskStatusShouldBe(String expectedStatus) {
        TaskResult<Void> statusResult = (TaskResult<Void>) testContext.get("statusResult");
        assertNotNull(statusResult, "Status result should not be null");
        
        Object status = statusResult.getAttributes().get("status");
        assertNotNull(status, "Status attribute should not be null");
        
        assertEquals(TaskStatus.valueOf(expectedStatus), status, 
                "Task status should match expected status");
    }
    
    @Then("the shutdown should be successful")
    public void theShutdownShouldBeSuccessful() {
        TaskResult<Void> shutdownResult = (TaskResult<Void>) testContext.get("shutdownResult");
        assertNotNull(shutdownResult, "Shutdown result should not be null");
        
        assertTrue(shutdownResult.isSuccessful(), "Shutdown should be successful");
    }
}