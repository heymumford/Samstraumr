# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_ErrorHandling @ErrorHandling @BTL
Feature: Component Operation Exception Handling
  As a framework developer
  I want robust exception handling during component operations
  So that errors are properly contained and handled

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Standard exception hierarchy for component operations
    When I trigger various component exceptions
    Then each exception should be of the appropriate type in the exception hierarchy
    And each exception should include the component identity
    And each exception should include contextual information
    And framework exceptions should extend from a common base exception
    And exception chaining should be used for nested exceptions

  Scenario: Exception handling for data processing operations
    Given a component processing data
    When I provide invalid data that causes processing failures
    Then appropriate exceptions should be thrown with data details
    And partial processing results should be rolled back
    And the component should remain in a consistent state
    And the exceptions should provide remediation suggestions
    And the error should be logged with data context

  Scenario: Exception handling for resource exhaustion
    Given a component with resource constraints
    When I perform operations that exceed resource limits
    Then resource limit exceptions should be thrown
    And the exceptions should include current and maximum resource levels
    And the component should gracefully degrade under resource pressure
    And critical operations should be prioritized
    And the component should recover when resources become available

  Scenario: Exception handling for configuration errors
    Given a component with invalid configuration
    When I attempt operations that require valid configuration
    Then configuration exceptions should be thrown
    And the exceptions should identify the invalid configuration settings
    And the exceptions should suggest valid configuration values
    And operations should fail fast when configuration is invalid
    And configuration errors should be logged with detailed context

  Scenario: Exception handling for connectivity issues
    Given a component that depends on external connections
    When connections are unavailable or time out
    Then connectivity exceptions should be thrown
    And the exceptions should include connection details
    And the component should attempt reconnection strategies
    And operations should be cancellable during connection attempts
    And connection failures should be logged with timing information

  Scenario: Exception handling for concurrent operations
    Given a component processing concurrent operations
    When concurrent operations interfere with each other
    Then appropriate concurrency exceptions should be thrown
    And the component should maintain thread safety
    And deadlocks should be detected and reported
    And partial results should be properly managed
    And concurrent failures should be logged with thread information

  Scenario: Exception propagation through component hierarchy
    Given a component hierarchy with nested operations
    When an exception occurs in a deeply nested operation
    Then the exception should be properly propagated up the call stack
    And each level should enrich the exception with relevant context
    And the original cause should be preserved
    And the component hierarchy should remain in a consistent state
    And exception propagation should be logged at each level

  @Resilience
  Scenario: Exception recovery strategies
    Given a component with recovery strategies for common exceptions
    When I trigger recoverable exceptions
    Then the component should attempt appropriate recovery actions
    And successfully recovered operations should complete normally
    And repeatedly failing operations should employ escalating strategies
    And recovery attempts should be rate-limited to prevent resource drain
    And recovery actions should be logged with outcome information