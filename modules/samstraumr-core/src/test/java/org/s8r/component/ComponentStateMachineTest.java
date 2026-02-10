/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.component;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Direct JUnit tests for Component lifecycle state machine.
 *
 * <p>This test class verifies the Component state transitions and behavior in different states
 * without using Cucumber.
 */
public class ComponentStateMachineTest {

  private Component component;

  @BeforeEach
  void setUp() {
    component = Component.createAdam("State Machine Test");
  }

  @Test
  @DisplayName("Component should start in CONCEPTION state")
  void componentShouldStartInConceptionState() {
    assertEquals(State.CONCEPTION, component.getState());
  }

  @Test
  @DisplayName("Component should transition through early lifecycle states")
  void componentShouldTransitionThroughEarlyLifecycleStates() {
    // Component is initialized in constructor, so it should already be in READY state
    // We'll verify the memory log to see if it went through all the expected states

    List<String> memoryLog = component.getMemoryLog();

    // Count state transitions
    int transitionCount = countStateTransitions(memoryLog);

    // We expect transitions through CONCEPTION, INITIALIZING, CONFIGURING, etc.
    assertTrue(transitionCount >= 3, "Should have at least 3 state transitions in early lifecycle");

    // Verify final state is READY
    assertEquals(State.READY, component.getState());
  }

  @Test
  @DisplayName("Component should transition from READY to ACTIVE")
  void componentShouldTransitionFromReadyToActive() {
    // Transition to ACTIVE
    component.setState(State.ACTIVE);

    // Verify state
    assertEquals(State.ACTIVE, component.getState());
    assertTrue(component.isOperational(), "Component should be operational in ACTIVE state");
  }

  @Test
  @DisplayName("Component should reject invalid state transitions")
  void componentShouldRejectInvalidStateTransitions() {
    // Try to transition from READY to CONCEPTION (invalid)
    assertThrows(
        InvalidStateTransitionException.class,
        () -> {
          component.setState(State.CONCEPTION);
        });
  }

  @Test
  @DisplayName("Terminated component should reject all transitions except to ARCHIVED")
  void terminatedComponentShouldRejectAllTransitionsExceptToArchived() {
    // Terminate the component
    component.terminate("Test termination");

    // Verify it's in TERMINATED state
    assertEquals(State.TERMINATED, component.getState());

    // Try to transition to ACTIVE (should fail)
    assertThrows(
        Exception.class,
        () -> {
          component.setState(State.ACTIVE);
        });

    // Try to transition to ARCHIVED (should succeed)
    component.setState(State.ARCHIVED);
    assertEquals(State.ARCHIVED, component.getState());
  }

  @Test
  @DisplayName("Component termination should follow proper sequence")
  void componentTerminationShouldFollowProperSequence() {
    // First set to ACTIVE
    component.setState(State.ACTIVE);

    // Terminate
    component.terminate("Test termination");

    // Verify final state
    assertEquals(State.TERMINATED, component.getState());

    // Verify it went through TERMINATING
    List<String> memoryLog = component.getMemoryLog();
    boolean wentThroughTerminating =
        memoryLog.stream()
            .anyMatch(line -> line.contains("State changed:") && line.contains("TERMINATING"));

    assertTrue(wentThroughTerminating, "Component should go through TERMINATING state");
  }

  @Test
  @DisplayName("Component resource usage should change with state transitions")
  void componentResourceUsageShouldChangeWithStateTransitions() {
    // Initialize resource tracking
    component.initializeResourceTracking();

    // Get initial resource usage
    int initialMemory = component.getResourceUsage("memory");

    // Transition to ACTIVE
    component.setState(State.ACTIVE);

    // Get active resource usage
    int activeMemory = component.getResourceUsage("memory");

    // Transition to SUSPENDED
    component.setState(State.SUSPENDED);

    // Get suspended resource usage
    int suspendedMemory = component.getResourceUsage("memory");

    // Terminate
    component.terminate("Test termination");

    // Get terminated resource usage
    int terminatedMemory = component.getResourceUsage("memory");

    // Verify resource changes
    // In active state, resource usage is typically higher
    assertTrue(activeMemory >= initialMemory, "Active memory usage should be >= initial");

    // In suspended state, resource usage might be lower or the same
    // In terminated state, resource usage should be minimal
    assertTrue(terminatedMemory < activeMemory, "Terminated memory usage should be < active");
  }

  @Test
  @DisplayName("Component suspension and resumption should work correctly")
  void componentSuspensionAndResumptionShouldWorkCorrectly() {
    // First set to ACTIVE
    component.setState(State.ACTIVE);

    // Establish a connection
    component.establishConnection("Test connection");

    // Suspend
    component.suspend("Test suspension");

    // Verify state
    assertEquals(State.SUSPENDED, component.getState());

    // Verify connections are closed
    assertEquals(
        0,
        component.getResourceUsage("connections"),
        "All connections should be closed in SUSPENDED state");

    // Verify original state is stored
    Object preSuspendedState = component.getProperty("preSuspendedState");
    assertEquals("ACTIVE", preSuspendedState, "Pre-suspended state should be ACTIVE");

    // Resume
    component.resume();

    // Verify state is restored
    assertEquals(State.ACTIVE, component.getState());
  }

  @Test
  @DisplayName("Maintenance mode should support advanced configuration")
  void maintenanceModeShouldSupportAdvancedConfiguration() {
    // First set to ACTIVE
    component.setState(State.ACTIVE);

    // Enter maintenance mode
    component.enterMaintenanceMode("Test maintenance");

    // Verify state
    assertEquals(State.MAINTENANCE, component.getState());

    // Verify original state is stored
    Object preMaintenanceState = component.getProperty("preMaintenanceState");
    assertEquals("ACTIVE", preMaintenanceState, "Pre-maintenance state should be ACTIVE");

    // Make configuration changes
    Environment env = component.getEnvironment();
    env.setParameter("advanced.setting1", "new-value1");

    // Exit maintenance mode
    component.exitMaintenanceMode();

    // Verify state is restored
    assertEquals(State.ACTIVE, component.getState());

    // Verify configuration changes were applied
    assertEquals("new-value1", env.getParameter("advanced.setting1"));
  }

  @Test
  @DisplayName("Component should handle error and recovery correctly")
  void componentShouldHandleErrorAndRecoveryCorrectly() {
    // First set to ACTIVE
    component.setState(State.ACTIVE);

    // Transition to ERROR state
    component.setState(State.ERROR);

    // Verify state
    assertEquals(State.ERROR, component.getState());

    // Simulate recovery
    component.setState(State.RECOVERING);
    assertEquals(State.RECOVERING, component.getState());

    // Return to ACTIVE
    component.setState(State.ACTIVE);
    assertEquals(State.ACTIVE, component.getState());
  }

  @Test
  @DisplayName("Component state categories should be correct")
  void componentStateCategoriesShouldBeCorrect() {
    // Check a few states from each category
    assertTrue(State.ACTIVE.isOperational(), "ACTIVE should be OPERATIONAL");
    assertTrue(State.READY.isOperational(), "READY should be OPERATIONAL");
    assertTrue(State.SUSPENDED.isOperational(), "SUSPENDED should be OPERATIONAL");

    assertTrue(State.CONCEPTION.isLifecycle(), "CONCEPTION should be LIFECYCLE");
    assertTrue(State.CONFIGURING.isLifecycle(), "CONFIGURING should be LIFECYCLE");

    assertTrue(State.STABLE.isAdvanced(), "STABLE should be ADVANCED");
    assertTrue(State.SPAWNING.isAdvanced(), "SPAWNING should be ADVANCED");

    assertTrue(State.TERMINATING.isTermination(), "TERMINATING should be TERMINATION");
    assertTrue(State.TERMINATED.isTermination(), "TERMINATED should be TERMINATION");
  }

  // Helper methods
  private int countStateTransitions(List<String> memoryLog) {
    int count = 0;
    for (String line : memoryLog) {
      if (line.contains("State changed:")) {
        count++;
      }
    }
    return count;
  }
}
