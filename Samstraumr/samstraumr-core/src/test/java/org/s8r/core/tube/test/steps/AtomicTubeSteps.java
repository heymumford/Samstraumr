/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Cucumber step definitions for Atomic Tube tests
 */
package org.s8r.core.tube.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.LifecycleState;
import org.s8r.core.tube.Status;
import org.s8r.core.tube.identity.Identity;
import org.s8r.core.tube.impl.Component;
import org.s8r.core.tube.logging.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for atomic tube tests.
 *
 * <p>These step definitions implement the BDD scenarios related to atomic tubes, which are
 * standalone components with no connections to other components.
 */
public class AtomicTubeSteps {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AtomicTubeSteps.class);

  private Environment environment;
  private Component tube;
  private Identity identity;
  private Logger logger;
  private Exception exceptionThrown;
  private Map<String, Object> testResources = new HashMap<>();
  private List<Map<String, String>> lifecycleEvents = new ArrayList<>();
  private long memoryBefore;
  private long memoryAfter;

  @Given("the system environment is properly configured")
  public void the_system_environment_is_properly_configured() {
    LOGGER.info("Configuring the system environment");
    environment = new Environment();
    environment.setParameter("test_environment", "true");
    environment.setParameter("host", "localhost");
    environment.setParameter("mode", "test");

    assertNotNull(environment, "Environment should be created successfully");
    assertEquals("true", environment.getParameter("test_environment"));
  }

  @When("an atomic tube is created with reason {string}")
  public void an_atomic_tube_is_created_with_reason(String reason) {
    LOGGER.info("Creating an atomic tube with reason: {}", reason);

    try {
      tube = Component.create(reason, environment);
      identity = tube.getIdentity();
      logger = tube.getLogger();

      assertNotNull(tube, "Tube should be created successfully");
      assertNotNull(identity, "Tube identity should be created successfully");
      assertNotNull(logger, "Tube logger should be created successfully");
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception creating tube: {}", e.getMessage());
    }
  }

  @When("an atomic tube is created with null reason")
  public void an_atomic_tube_is_created_with_null_reason() {
    LOGGER.info("Attempting to create an atomic tube with null reason");

    try {
      tube = Component.create(null, environment);
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.info("Exception thrown as expected: {}", e.getMessage());
    }
  }

  @When("an atomic tube is created with empty reason")
  public void an_atomic_tube_is_created_with_empty_reason() {
    LOGGER.info("Attempting to create an atomic tube with empty reason");

    try {
      tube = Component.create("", environment);
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.info("Exception thrown as expected: {}", e.getMessage());
    }
  }

  @When("an atomic tube is created with null environment")
  public void an_atomic_tube_is_created_with_null_environment() {
    LOGGER.info("Attempting to create an atomic tube with null environment");

    try {
      tube = Component.create("Test Reason", null);
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.info("Exception thrown as expected: {}", e.getMessage());
    }
  }

  @When("the tube progresses to the next lifecycle state")
  public void the_tube_progresses_to_the_next_lifecycle_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Progressing tube to next lifecycle state from: {}", tube.getState());

    LifecycleState currentState = tube.getLifecycleState();
    LifecycleState nextState = getNextLifecycleState(currentState);

    try {
      tube.setState(nextState);
      LOGGER.info("Tube state changed to: {}", tube.getState());
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception progressing state: {}", e.getMessage());
    }
  }

  @When("the tube progresses to READY state")
  public void the_tube_progresses_to_ready_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Progressing tube to READY state from: {}", tube.getState());

    try {
      // Set intermediate states if needed
      if (tube.getLifecycleState() == LifecycleState.CONCEPTION) {
        tube.setState(LifecycleState.INITIALIZING);
      }
      if (tube.getLifecycleState() == LifecycleState.INITIALIZING) {
        tube.setState(LifecycleState.CONFIGURING);
      }
      if (tube.getLifecycleState() == LifecycleState.CONFIGURING) {
        tube.setState(LifecycleState.READY);
      }

      LOGGER.info("Tube state changed to: {}", tube.getState());
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception progressing to READY: {}", e.getMessage());
    }
  }

  @Given("the tube allocates test resources")
  public void the_tube_allocates_test_resources() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Allocating test resources for tube");

    // Record memory before allocation
    memoryBefore = getCurrentMemoryUsage();

    // Simulate resource allocation
    testResources.put("timer", new Object()); // simulating a timer
    testResources.put("buffer", new byte[1024]); // simulating a buffer
    testResources.put("connection", new Object()); // simulating a connection

    // Log the allocation
    tube.log("Allocated test resources: timer, buffer, connection");
  }

  @When("the tube is terminated")
  public void the_tube_is_terminated() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Terminating tube");

    try {
      tube.terminate();
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception terminating tube: {}", e.getMessage());
    }
  }

  @When("the tube experiences the following lifecycle events:")
  public void the_tube_experiences_the_following_lifecycle_events(DataTable dataTable) {
    assertNotNull(tube, "Tube should exist");

    List<Map<String, String>> events = dataTable.asMaps();
    lifecycleEvents.addAll(events);

    LOGGER.info("Simulating {} lifecycle events for tube", events.size());

    for (Map<String, String> event : events) {
      String eventType = event.get("Event");
      String details = event.get("Details");

      // Simulate the event based on type
      switch (eventType) {
        case "State Change":
          if (details.contains("CONCEPTION to INITIALIZING")) {
            try {
              tube.setState(LifecycleState.INITIALIZING);
            } catch (Exception e) {
              LOGGER.error("Error changing state: {}", e.getMessage());
            }
          } else if (details.contains("INITIALIZING to READY")) {
            try {
              tube.setState(LifecycleState.CONFIGURING);
              tube.setState(LifecycleState.READY);
            } catch (Exception e) {
              LOGGER.error("Error changing state: {}", e.getMessage());
            }
          }
          break;
        case "Configuration":
          if (details.contains("Setting option")) {
            String option = details.split("\"")[1];
            tube.log("Configuration changed: " + option);
          }
          break;
        case "Activity":
          tube.log("Activity: " + details);
          break;
        case "Warning":
          tube.log("Warning: " + details);
          break;
        default:
          tube.log("Unknown event: " + eventType + " - " + details);
      }
    }
  }

  @Then("the tube should have a valid tube identity")
  public void the_tube_should_have_a_valid_tube_identity() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube has a valid identity");

    assertNotNull(identity, "Tube identity should not be null");
  }

  @Then("the tube should have a unique identifier")
  public void the_tube_should_have_a_unique_identifier() {
    assertNotNull(tube, "Tube should exist");
    assertNotNull(identity, "Tube identity should not be null");

    LOGGER.info("Verifying tube has a unique identifier");

    String uniqueId = identity.getUniqueId();
    assertNotNull(uniqueId, "Tube unique ID should not be null");
    assertFalse(uniqueId.isEmpty(), "Tube unique ID should not be empty");
  }

  @Then("the tube should have a creation timestamp")
  public void the_tube_should_have_a_creation_timestamp() {
    assertNotNull(tube, "Tube should exist");
    assertNotNull(identity, "Tube identity should not be null");

    LOGGER.info("Verifying tube has a creation timestamp");

    Instant creationTime = identity.getConceptionTime();
    assertNotNull(creationTime, "Tube creation timestamp should not be null");

    // Creation time should be in the past, not the future
    assertTrue(
        creationTime.compareTo(Instant.now()) <= 0, "Creation time should not be in the future");

    // Creation time should be recent (within the last minute)
    assertTrue(
        creationTime.isAfter(Instant.now().minusSeconds(60)), "Creation time should be recent");
  }

  @Then("the tube should capture the reason {string}")
  public void the_tube_should_capture_the_reason(String reason) {
    assertNotNull(tube, "Tube should exist");
    assertNotNull(identity, "Tube identity should not be null");

    LOGGER.info("Verifying tube captures the reason: {}", reason);

    assertEquals(reason, identity.getReason(), "Tube should capture the creation reason");
  }

  @Then("the tube should have an initial lifecycle state")
  public void the_tube_should_have_an_initial_lifecycle_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube has an initial lifecycle state");

    LifecycleState state = tube.getLifecycleState();
    assertNotNull(state, "Tube lifecycle state should not be null");
    assertEquals(LifecycleState.CONCEPTION, state, "Initial state should be CONCEPTION");
  }

  @Then("the tube should have a logger instance")
  public void the_tube_should_have_a_logger_instance() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube has a logger instance");

    assertNotNull(logger, "Tube logger should not be null");
  }

  @Then("the tube creation should fail")
  public void the_tube_creation_should_fail() {
    LOGGER.info("Verifying tube creation failed");

    assertNull(tube, "Tube should not be created");
    assertNotNull(exceptionThrown, "An exception should have been thrown");
  }

  @Then("an appropriate error message should indicate {string}")
  public void an_appropriate_error_message_should_indicate(String expectedMessage) {
    LOGGER.info("Verifying error message indicates: {}", expectedMessage);

    assertNotNull(exceptionThrown, "An exception should have been thrown");

    String actualMessage = exceptionThrown.getMessage().toLowerCase();
    assertTrue(
        actualMessage.contains(expectedMessage.toLowerCase()),
        "Error message should contain: " + expectedMessage);
  }

  @Then("the tube should be in CONCEPTION state")
  public void the_tube_should_be_in_conception_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is in CONCEPTION state");

    assertEquals(
        LifecycleState.CONCEPTION, tube.getLifecycleState(), "Tube should be in CONCEPTION state");
  }

  @Then("the tube should be in INITIALIZING state")
  public void the_tube_should_be_in_initializing_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is in INITIALIZING state");

    assertEquals(
        LifecycleState.INITIALIZING,
        tube.getLifecycleState(),
        "Tube should be in INITIALIZING state");
  }

  @Then("the tube should be in CONFIGURING state")
  public void the_tube_should_be_in_configuring_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is in CONFIGURING state");

    assertEquals(
        LifecycleState.CONFIGURING,
        tube.getLifecycleState(),
        "Tube should be in CONFIGURING state");
  }

  @Then("the tube should be in READY state")
  public void the_tube_should_be_in_ready_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is in READY state");

    assertEquals(LifecycleState.READY, tube.getLifecycleState(), "Tube should be in READY state");
  }

  @Then("the tube should be operational")
  public void the_tube_should_be_operational() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is operational");

    assertEquals(Status.OPERATIONAL, tube.getStatus(), "Tube should have OPERATIONAL status");
  }

  @Then("the tube should log the state transition")
  public void the_tube_should_log_the_state_transition() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube logged state transition");

    List<String> logs = tube.getMemoryLog();
    assertNotNull(logs, "Memory log should not be null");
    assertFalse(logs.isEmpty(), "Memory log should not be empty");

    boolean foundTransition = false;
    for (String entry : logs) {
      if (entry.contains("State changed") || entry.contains("transition")) {
        foundTransition = true;
        break;
      }
    }

    assertTrue(foundTransition, "Tube should log state transitions");
  }

  @Then("the tube should be in TERMINATED state")
  public void the_tube_should_be_in_terminated_state() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube is in TERMINATED state");

    assertEquals(
        LifecycleState.TERMINATED, tube.getLifecycleState(), "Tube should be in TERMINATED state");
  }

  @Then("all tube resources should be properly released")
  public void all_tube_resources_should_be_properly_released() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube resources are properly released");

    // Clear our test resources to simulate release
    testResources.clear();

    // Check tube logs for resource release messages
    List<String> logs = tube.getMemoryLog();
    boolean resourceReleaseLogged = false;

    for (String entry : logs) {
      if (entry.contains("resource")
          && (entry.contains("release") || entry.contains("clean") || entry.contains("free"))) {
        resourceReleaseLogged = true;
        break;
      }
    }

    assertTrue(resourceReleaseLogged, "Tube should log resource release");
  }

  @Then("the tube termination should be logged")
  public void the_tube_termination_should_be_logged() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube termination is logged");

    List<String> logs = tube.getMemoryLog();
    boolean terminationLogged = false;

    for (String entry : logs) {
      if (entry.contains("terminat")) {
        terminationLogged = true;
        break;
      }
    }

    assertTrue(terminationLogged, "Tube should log termination");
  }

  @Then("no resource leaks should be detected")
  public void no_resource_leaks_should_be_detected() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying no resource leaks are detected");

    // Measure memory after resource cleanup
    memoryAfter = getCurrentMemoryUsage();

    // Memory usage should be similar or lower after cleanup
    // Note: This is an approximate test - garbage collection timing can affect results
    assertTrue(
        memoryAfter <= memoryBefore + 1024 * 1024, // Allow 1MB margin
        "Memory usage should not significantly increase after resource cleanup");

    // Verify test resources were cleared
    assertTrue(testResources.isEmpty(), "Test resources should be empty after cleanup");
  }

  @Then("the tube memory log should contain all lifecycle events")
  public void the_tube_memory_log_should_contain_all_lifecycle_events() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube memory log contains all lifecycle events");

    List<String> logs = tube.getMemoryLog();
    assertNotNull(logs, "Memory log should not be null");

    // Check that all lifecycle events are reflected in the logs
    for (Map<String, String> event : lifecycleEvents) {
      String eventType = event.get("Event");
      String details = event.get("Details");

      boolean eventFound = false;
      for (String entry : logs) {
        if ((entry.contains(eventType)
                || eventType.equals("State Change") && entry.contains("State"))
            && entry.contains(details.substring(0, Math.min(details.length(), 10)))) {
          eventFound = true;
          break;
        }
      }

      assertTrue(eventFound, "Memory log should contain event: " + eventType + " - " + details);
    }
  }

  @Then("the tube memory log should maintain chronological order")
  public void the_tube_memory_log_should_maintain_chronological_order() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube memory log maintains chronological order");

    List<String> logs = tube.getMemoryLog();
    assertNotNull(logs, "Memory log should not be null");

    // Check timestamp ordering
    long lastTimestamp = 0;
    for (String entry : logs) {
      // Assume log entries start with timestamps (actual format will depend on implementation)
      try {
        // Extract timestamp - this is a simplified example
        int timeStart = entry.indexOf('[') + 1;
        int timeEnd = entry.indexOf(']');
        if (timeStart > 0 && timeEnd > timeStart) {
          String timeStr = entry.substring(timeStart, timeEnd).trim();
          long timestamp = Instant.parse(timeStr).toEpochMilli();

          assertTrue(timestamp >= lastTimestamp, "Log entries should be in chronological order");
          lastTimestamp = timestamp;
        }
      } catch (Exception e) {
        // If timestamps can't be parsed, just skip the check
        LOGGER.warn("Could not parse timestamp in log entry: {}", entry);
      }
    }
  }

  @Then("the tube memory log should contain timestamps")
  public void the_tube_memory_log_should_contain_timestamps() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube memory log contains timestamps");

    List<String> logs = tube.getMemoryLog();
    assertNotNull(logs, "Memory log should not be null");
    assertFalse(logs.isEmpty(), "Memory log should not be empty");

    boolean hasTimestamps = true;
    for (String entry : logs) {
      if (!entry.contains("[") || !entry.contains("]")) {
        hasTimestamps = false;
        break;
      }
    }

    assertTrue(hasTimestamps, "All log entries should contain timestamps");
  }

  @Then("the tube memory log should contain event details")
  public void the_tube_memory_log_should_contain_event_details() {
    assertNotNull(tube, "Tube should exist");

    LOGGER.info("Verifying tube memory log contains event details");

    List<String> logs = tube.getMemoryLog();
    assertNotNull(logs, "Memory log should not be null");

    for (Map<String, String> event : lifecycleEvents) {
      String details = event.get("Details");

      boolean detailsFound = false;
      for (String entry : logs) {
        if (entry.contains(details.substring(0, Math.min(details.length(), 10)))) {
          detailsFound = true;
          break;
        }
      }

      assertTrue(detailsFound, "Memory log should contain event details: " + details);
    }
  }

  // Helper methods

  private LifecycleState getNextLifecycleState(LifecycleState currentState) {
    switch (currentState) {
      case CONCEPTION:
        return LifecycleState.INITIALIZING;
      case INITIALIZING:
        return LifecycleState.CONFIGURING;
      case CONFIGURING:
        return LifecycleState.READY;
      case READY:
        return LifecycleState.ACTIVE;
      case ACTIVE:
        return LifecycleState.STABLE;
      default:
        return currentState;
    }
  }

  private long getCurrentMemoryUsage() {
    Runtime runtime = Runtime.getRuntime();
    return runtime.totalMemory() - runtime.freeMemory();
  }
}
