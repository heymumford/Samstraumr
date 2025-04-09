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
import org.s8r.infrastructure.task.ThreadPoolTaskExecutionAdapter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Steps for testing the TaskExecutionPort interface.
 */
public class TaskExecutionPortSteps {

    private TaskExecutionPort taskExecutionPort;
    private final Map<String, Object> testContext = new HashMap<>();
    private String lastTaskId;
    
    @Before
    public void setUp() {
        taskExecutionPort = new ThreadPoolTaskExecutionAdapter(2);
        testContext.clear();
        lastTaskId = null;
    }
    
    @After
    public void tearDown() {
        if (taskExecutionPort != null) {
            taskExecutionPort.shutdown(false);
        }
    }
    
    @Given("a task execution service")
    public void aTaskExecutionService() {
        assertNotNull(taskExecutionPort, "Task execution port should be initialized");
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
    
    @When("I schedule a task that returns {string} to run after {int} milliseconds")
    public void iScheduleATaskThatReturnsToRunAfterMilliseconds(String value, Integer delay) {
        TaskResult<String> result = taskExecutionPort.scheduleTask(() -> {
            return value;
        }, Duration.ofMillis(delay));
        
        lastTaskId = result.getTaskId();
        testContext.put("scheduleResult", result);
    }
    
    @When("I schedule a task that increments a counter to run after {int} milliseconds")
    public void iScheduleATaskThatIncrementsACounterToRunAfterMilliseconds(Integer delay) {
        AtomicInteger counter = new AtomicInteger(0);
        testContext.put("counter", counter);
        
        TaskResult<Void> result = taskExecutionPort.scheduleTask(() -> {
            counter.incrementAndGet();
        }, Duration.ofMillis(delay));
        
        lastTaskId = result.getTaskId();
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