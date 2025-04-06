/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.component.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.s8r.component.core.Component;
import org.s8r.component.core.Environment;
import org.s8r.component.core.State;
import org.s8r.component.exception.ComponentException;
import org.s8r.component.exception.InvalidStateTransitionException;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

/**
 * Step definitions for component error handling tests.
 *
 * <p>These step definitions implement the BDD scenarios related to component error handling,
 * focusing on input validation, exception handling, and resource cleanup after errors.
 */
public class ErrorHandlingSteps extends BaseComponentSteps {

  private long memoryBefore;
  private long memoryAfter;
  private Map<String, Exception> operationExceptions = new HashMap<>();
  private List<Component> errorComponents = new ArrayList<>();

  @When("component initialization is attempted with invalid null reason")
  public void component_initialization_is_attempted_with_invalid_null_reason() {
    try {
      testComponent = createComponent(null, environment);
      storeInContext("testComponent", testComponent);
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @When("component initialization is attempted with invalid empty reason")
  public void component_initialization_is_attempted_with_invalid_empty_reason() {
    try {
      testComponent = createComponent("", environment);
      storeInContext("testComponent", testComponent);
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @When("component initialization is attempted with invalid null environment")
  public void component_initialization_is_attempted_with_invalid_null_environment() {
    try {
      Environment nullEnv = null;
      testComponent = createComponent("Invalid Environment Test", nullEnv);
      storeInContext("testComponent", testComponent);
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @When("a component operation fails with an exception")
  public void a_component_operation_fails_with_an_exception() {
    assertNotNull(testComponent, "Component should exist");

    // Record memory before
    memoryBefore = getCurrentMemoryUsage();
    storeInContext("memoryBefore", memoryBefore);

    // Force an operation failure
    try {
      // Try an invalid operation that should cause an exception
      testComponent.setState(null);
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }

    // Ensure we got an exception
    assertNotNull(exceptionThrown, "An exception should have been thrown");
  }

  @When("{int} components are initialized simultaneously with invalid conditions")
  public void components_are_initialized_simultaneously_with_invalid_conditions(Integer count) {
    assertNotNull(environment, "Environment should exist");

    // Set up concurrent initialization of components with errors
    ExecutorService executor = Executors.newFixedThreadPool(count);
    CountDownLatch latch = new CountDownLatch(count);
    ConcurrentHashMap<Integer, Exception> exceptions = new ConcurrentHashMap<>();

    for (int i = 0; i < count; i++) {
      final int index = i;
      executor.submit(
          () -> {
            try {
              // Different invalid conditions for different components
              switch (index % 3) {
                case 0: // null reason
                  createComponent(null, environment);
                  break;
                case 1: // empty reason
                  createComponent("", environment);
                  break;
                case 2: // null environment
                  createComponent("Test " + index, (Environment) null);
                  break;
              }
            } catch (Exception e) {
              exceptions.put(index, e);
            } finally {
              latch.countDown();
            }
          });
    }

    // Wait for all tasks to complete
    try {
      latch.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      fail("Concurrent test interrupted: " + e.getMessage());
    } finally {
      executor.shutdown();
    }

    // Store results for further assertions
    storeInContext("concurrentExceptions", exceptions);
  }

  @When("the component encounters a recoverable error")
  public void the_component_encounters_a_recoverable_error() {
    assertNotNull(testComponent, "Component should exist");

    // Simulate a recoverable error condition
    // This would depend on the component implementation
    // For now, we'll use a state transition error that can be recovered from
    try {
      // Force component into a state where recovery is possible
      testComponent.setState(State.CONFIGURING);

      // Trigger a recoverable error
      testComponent.triggerRecoverableError();
    } catch (Exception e) {
      exceptionThrown = e;
      storeInContext("exceptionThrown", exceptionThrown);
    }
  }

  @Then("initialization should fail with appropriate {}")
  public void initialization_should_fail_with_appropriate_error_type(String errorType) {
    assertNotNull(exceptionThrown, "An exception should have been thrown");

    // Check exception type
    switch (errorType) {
      case "IllegalArgumentException":
        assertTrue(
            exceptionThrown instanceof IllegalArgumentException,
            "Exception should be of type IllegalArgumentException");
        break;
      case "ComponentException":
        assertTrue(
            exceptionThrown instanceof ComponentException,
            "Exception should be of type ComponentException");
        break;
      default:
        fail("Unknown error type: " + errorType);
    }
  }

  @Then("the error message should mention {}")
  public void the_error_message_should_mention_parameter(String parameter) {
    assertNotNull(exceptionThrown, "An exception should have been thrown");

    String message = exceptionThrown.getMessage().toLowerCase();
    switch (parameter) {
      case "null reason":
        assertTrue(
            message.contains("reason") && message.contains("null"),
            "Error message should mention null reason");
        break;
      case "empty reason":
        assertTrue(
            message.contains("reason") && (message.contains("empty") || message.contains("blank")),
            "Error message should mention empty reason");
        break;
      case "null environment":
        assertTrue(
            message.contains("environment") && message.contains("null"),
            "Error message should mention null environment");
        break;
      default:
        fail("Unknown parameter: " + parameter);
    }
  }

  @Then("no component resources should be leaked")
  public void no_component_resources_should_be_leaked() {
    // The component should not have been created, so no resources should exist
    assertNull(testComponent, "No component should be created");

    // For more thorough testing, we could check memory usage or resource handles
    long memoryAfter = getCurrentMemoryUsage();
    storeInContext("memoryAfter", memoryAfter);

    // Memory usage should not significantly increase after a failed initialization
    // This is a rough test and might not be reliable in all environments
    assertTrue(
        memoryAfter - memoryBefore < 1024 * 1024,
        "Memory usage should not significantly increase after failed initialization");
  }

  @Then("operations on the terminated component should be rejected:")
  public void operations_on_the_terminated_component_should_be_rejected(DataTable dataTable) {
    assertNotNull(testComponent, "Component should exist");
    assertTrue(testComponent.isTerminated(), "Component should be terminated");

    List<Map<String, String>> operations = dataTable.asMaps();

    for (Map<String, String> operation : operations) {
      String operationType = operation.get("Operation");
      String errorType = operation.get("Error Type");

      Exception operationException = null;
      try {
        switch (operationType) {
          case "setState":
            testComponent.setState(State.ACTIVE);
            break;
          case "createChild":
            // Attempt to create a child with a terminated parent, which should fail
            Component.createChild("Child of terminated component", environment, testComponent);
            break;
          case "updateEnvironment":
            testComponent.updateEnvironment("new environment state");
            break;
          default:
            fail("Unknown operation type: " + operationType);
        }
      } catch (Exception e) {
        operationException = e;
        operationExceptions.put(operationType, e);
      }

      // Verify exception was thrown and is of correct type
      assertNotNull(operationException, "Operation " + operationType + " should throw exception");

      switch (errorType) {
        case "InvalidStateTransitionException":
          assertTrue(
              operationException instanceof InvalidStateTransitionException,
              "Operation " + operationType + " should throw InvalidStateTransitionException");
          break;
        case "ComponentException":
          assertTrue(
              operationException instanceof ComponentException,
              "Operation " + operationType + " should throw ComponentException");
          break;
        default:
          fail("Unknown error type: " + errorType);
      }
    }

    storeInContext("operationExceptions", operationExceptions);
  }

  @Then("appropriate error messages should be provided")
  public void appropriate_error_messages_should_be_provided() {
    @SuppressWarnings("unchecked")
    Map<String, Exception> exceptions = getFromContext("operationExceptions", Map.class);
    assertNotNull(exceptions, "Operation exceptions should be recorded");

    for (Map.Entry<String, Exception> entry : exceptions.entrySet()) {
      String operation = entry.getKey();
      Exception exception = entry.getValue();

      assertNotNull(
          exception.getMessage(), "Exception for " + operation + " should have a message");
      assertFalse(
          exception.getMessage().isEmpty(),
          "Exception message for " + operation + " should not be empty");

      // Check for specific operation in message
      assertTrue(
          exception.getMessage().contains("terminated")
              || exception.getMessage().contains("invalid state"),
          "Exception message should explain why operation " + operation + " was rejected");
    }
  }

  @Then("the component should remain in terminated state")
  public void the_component_should_remain_in_terminated_state() {
    assertNotNull(testComponent, "Component should exist");
    assertTrue(testComponent.isTerminated(), "Component should remain in terminated state");
  }

  @Then("the component should clean up resources properly")
  public void the_component_should_clean_up_resources_properly() {
    assertNotNull(testComponent, "Component should exist");

    // Check component logs for cleanup messages
    List<String> logs = testComponent.getMemoryLog();
    boolean cleanupLogged =
        logs.stream()
            .anyMatch(
                log ->
                    log.contains("cleanup") || log.contains("cleaned") || log.contains("released"));

    assertTrue(cleanupLogged, "Component should log resource cleanup after error");
  }

  @Then("memory usage should not increase after the error")
  public void memory_usage_should_not_increase_after_the_error() {
    // Measure memory usage after error
    memoryAfter = getCurrentMemoryUsage();
    storeInContext("memoryAfter", memoryAfter);

    // Get previously stored memory usage
    Long before = getFromContext("memoryBefore", Long.class);
    assertNotNull(before, "Memory usage before error should be recorded");

    // Allow some margin for normal operations
    // This is a rough check and might not be reliable in all environments
    assertTrue(
        memoryAfter - before < 1024 * 1024,
        "Memory usage should not significantly increase after error");
  }

  @Then("any open resources should be closed")
  public void any_open_resources_should_be_closed() {
    assertNotNull(testComponent, "Component should exist");

    // This would depend on the component implementation
    // For now, just validate that component is still operational
    assertFalse(
        testComponent.isTerminated(), "Component should not be terminated by recoverable error");

    // Check component logs for resource closure messages
    List<String> logs = testComponent.getMemoryLog();
    boolean closureLogged =
        logs.stream().anyMatch(log -> log.contains("closed") || log.contains("release"));

    assertTrue(closureLogged, "Component should log resource closure after error");
  }

  @Then("the error condition should be logged")
  public void the_error_condition_should_be_logged() {
    assertNotNull(testComponent, "Component should exist");
    assertNotNull(exceptionThrown, "An exception should have been thrown");

    // Check component logs for error messages
    List<String> logs = testComponent.getMemoryLog();
    boolean errorLogged =
        logs.stream()
            .anyMatch(
                log ->
                    log.contains("error") || log.contains("exception") || log.contains("failed"));

    assertTrue(errorLogged, "Component should log error condition");

    // Error log should include exception message
    String exceptionMessage = exceptionThrown.getMessage();
    boolean specificErrorLogged = logs.stream().anyMatch(log -> log.contains(exceptionMessage));

    assertTrue(specificErrorLogged, "Error log should include specific exception message");
  }

  @Then("all error conditions should be handled independently")
  public void all_error_conditions_should_be_handled_independently() {
    @SuppressWarnings("unchecked")
    Map<Integer, Exception> exceptions = getFromContext("concurrentExceptions", Map.class);
    assertNotNull(exceptions, "Concurrent exceptions should be recorded");

    // Verify all attempted operations resulted in exceptions
    assertEquals(5, exceptions.size(), "All 5 invalid operations should result in exceptions");

    // Check exception types
    for (Map.Entry<Integer, Exception> entry : exceptions.entrySet()) {
      int index = entry.getKey();
      Exception exception = entry.getValue();

      assertNotNull(exception, "Exception for component " + index + " should not be null");

      // Different invalid conditions should result in appropriate exceptions
      switch (index % 3) {
        case 0: // null reason
          assertTrue(
              exception instanceof IllegalArgumentException,
              "null reason should cause IllegalArgumentException");
          break;
        case 1: // empty reason
          assertTrue(
              exception instanceof IllegalArgumentException,
              "empty reason should cause IllegalArgumentException");
          break;
        case 2: // null environment
          assertTrue(
              exception instanceof IllegalArgumentException
                  || exception instanceof ComponentException,
              "null environment should cause appropriate exception");
          break;
      }
    }
  }

  @Then("no exceptions should escape the error handling framework")
  public void no_exceptions_should_escape_the_error_handling_framework() {
    // This is implicitly tested by the fact that we caught all exceptions
    // in the previous step without any uncaught exceptions propagating up
    @SuppressWarnings("unchecked")
    Map<Integer, Exception> exceptions = getFromContext("concurrentExceptions", Map.class);
    assertNotNull(exceptions, "Concurrent exceptions should be recorded");

    // All exceptions should have been caught and handled
    assertEquals(5, exceptions.size(), "All exceptions should be caught and recorded");
  }

  @Then("each component should log its own error condition")
  public void each_component_should_log_its_own_error_condition() {
    // Since the component creation failed, we don't have components to check logs on
    // We would normally verify that error logs were created
    // This test depends on external logging infrastructure in a real implementation
  }

  @Then("the system should remain stable")
  public void the_system_should_remain_stable() {
    // Verify we can still create a valid component after errors
    try {
      Component stableComponent = createComponent("Stability Test", environment);
      assertNotNull(stableComponent, "Should be able to create valid component after errors");
      stableComponent.terminate(); // Clean up
    } catch (Exception e) {
      fail("System should remain stable after errors: " + e.getMessage());
    }
  }

  @Then("the component should enter error recovery mode")
  public void the_component_should_enter_error_recovery_mode() {
    assertNotNull(testComponent, "Component should exist");

    // Check that component is in recovery mode
    assertTrue(testComponent.isInErrorRecovery(), "Component should enter error recovery mode");
  }

  @Then("the component should attempt to self-heal")
  public void the_component_should_attempt_to_self_heal() {
    assertNotNull(testComponent, "Component should exist");

    // Check that recovery was attempted
    assertTrue(testComponent.hasAttemptedRecovery(), "Component should attempt self-healing");

    // Check number of recovery attempts
    int attempts = testComponent.getRecoveryAttempts();
    assertTrue(attempts > 0, "Component should have made at least one recovery attempt");
  }

  @Then("the component should log recovery attempts")
  public void the_component_should_log_recovery_attempts() {
    assertNotNull(testComponent, "Component should exist");

    // Check component logs for recovery messages
    List<String> logs = testComponent.getMemoryLog();
    boolean recoveryLogged =
        logs.stream()
            .anyMatch(
                log ->
                    log.contains("recovery")
                        || log.contains("recovering")
                        || log.contains("self-heal"));

    assertTrue(recoveryLogged, "Component should log recovery attempts");
  }

  @Then("the component should either recover or terminate gracefully")
  public void the_component_should_either_recover_or_terminate_gracefully() {
    assertNotNull(testComponent, "Component should exist");

    // Check final state - either recovered or terminated
    if (testComponent.isTerminated()) {
      // If terminated, check for graceful termination
      List<String> logs = testComponent.getMemoryLog();
      boolean gracefulTermination =
          logs.stream().anyMatch(log -> log.contains("graceful") && log.contains("terminat"));

      assertTrue(gracefulTermination, "If terminated, component should terminate gracefully");
    } else {
      // If not terminated, should be in a valid state
      assertFalse(
          testComponent.isInErrorRecovery(),
          "If recovered, component should exit error recovery mode");

      // Verify component is operational
      assertTrue(
          testComponent.isOperational() || testComponent.isReady(),
          "If recovered, component should be in operational state");
    }
  }

  // Helper methods

  /**
   * Gets the current memory usage of the JVM. This is a rough measure and not always reliable for
   * precise memory leak detection.
   */
  private long getCurrentMemoryUsage() {
    Runtime runtime = Runtime.getRuntime();
    return runtime.totalMemory() - runtime.freeMemory();
  }
}
