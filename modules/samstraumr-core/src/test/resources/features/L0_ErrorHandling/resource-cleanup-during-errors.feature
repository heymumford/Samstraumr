# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_ErrorHandling @ErrorHandling @BTL
Feature: Resource Cleanup During Error Conditions
  As a framework developer
  I want proper resource cleanup to occur during error conditions
  So that resource leaks are prevented even when exceptions occur

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Resource cleanup during initialization failure
    When I create a component with simulated initialization failure
    Then all allocated resources should be properly released
    And the exception should contain information about the initialization failure
    And resource cleanup should be logged
    And no resource leaks should occur
    And the component should not be registered in the component registry

  Scenario: Resource cleanup during operation failure
    Given a component with acquired operation resources
    When a critical operation fails with exception
    Then all operation resources should be properly released
    And the component should return to a consistent state
    And the exception should be propagated to the caller
    And resource cleanup should be logged
    And no resource leaks should occur

  Scenario: Resource cleanup during termination errors
    Given a component with resources that will fail to release cleanly
    When I terminate the component
    Then best-effort cleanup should occur for all resources
    And unreleasable resources should be handled gracefully
    And cleanup errors should be logged but not thrown
    And the component should still reach "TERMINATED" state
    And no resource leaks should occur despite cleanup failures

  Scenario: Resource cleanup during state transition errors
    Given a component that will fail during state transition
    When I attempt a state transition that will fail
    Then all transition-specific resources should be released
    And the component should revert to its previous state
    And the exception should be propagated to the caller
    And cleanup details should be logged
    And no resource leaks should occur

  Scenario: Resource cleanup during concurrent operation failures
    Given a component handling multiple concurrent operations
    When several operations fail simultaneously
    Then resources from all failed operations should be released
    And the component should remain usable
    And exceptions should be properly isolated to their operation contexts
    And cleanup should be thread-safe
    And no resource leaks should occur

  Scenario: Cascading resource cleanup during error propagation
    Given a parent component with child components using shared resources
    When an operation fails in the parent component
    Then cleanup should properly handle parent-child resource dependencies
    And resources should be released in the correct order
    And both parent and child components should remain in consistent states
    And resource cleanup should be logged with component hierarchy information
    And no resource leaks should occur across the component hierarchy

  Scenario: Resource cleanup during out-of-memory conditions
    Given a component operating under low memory conditions
    When I perform an operation that triggers an OutOfMemoryError
    Then critical resources should still be released
    And the component should attempt to reach a safe state
    And error information should be logged before memory is exhausted
    And the system should prioritize releasing memory-intensive resources first

  @Resilience
  Scenario: Resource cleanup during system shutdown
    Given a system with multiple components using various resources
    When the system is shut down during active operations
    Then all components should release their resources properly
    And shutdown hooks should handle incomplete operations
    And resources should be released in priority order
    And the shutdown process should wait for critical cleanup to complete
    And cleanup errors should be logged but not prevent shutdown
    And no resource leaks should occur during shutdown