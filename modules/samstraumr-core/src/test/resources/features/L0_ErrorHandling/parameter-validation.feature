# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_ErrorHandling @ErrorHandling @BTL
Feature: Component Method Parameter Validation
  As a framework developer
  I want comprehensive parameter validation in all component methods
  So that illegal inputs are caught early with clear error messages

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Null arguments validation in component creation
    When I attempt to create an Adam component with the following invalid parameters:
      | Parameter | Value | Expected Exception            | Expected Message          |
      | reason    | null  | IllegalArgumentException      | reason cannot be null     |
      | env       | null  | IllegalArgumentException      | environment cannot be null|
    Then each attempt should fail with the expected exception and message
    And no component should be created
    And no resources should be allocated

  Scenario: Empty string arguments validation in component creation
    When I attempt to create an Adam component with the following invalid parameters:
      | Parameter | Value | Expected Exception            | Expected Message          |
      | reason    | ""    | IllegalArgumentException      | reason cannot be empty    |
    Then each attempt should fail with the expected exception and message
    And no component should be created

  Scenario: Invalid string length validation
    When I attempt to create an Adam component with reason exceeding maximum length
    Then creation should fail with exception containing "reason exceeds maximum length"
    And the exception should be of type "IllegalArgumentException"
    And the exception should include the maximum allowed length
    And the actual length of the provided reason

  Scenario: Null parameter validation in setEnvironment method
    Given a valid component exists
    When I attempt to set environment with null key
    Then the operation should fail with IllegalArgumentException containing "key cannot be null"
    When I attempt to set environment with null value
    Then the operation should fail with IllegalArgumentException containing "value cannot be null"
    And the original environment should remain unchanged

  Scenario: Null parameter validation in child component creation
    When I attempt to create a child component with the following invalid parameters:
      | Parameter | Value | Expected Exception            | Expected Message             |
      | parent    | null  | IllegalArgumentException      | parent cannot be null        |
      | reason    | null  | IllegalArgumentException      | reason cannot be null        |
      | env       | null  | IllegalArgumentException      | environment cannot be null   |
    Then each attempt should fail with the expected exception and message

  Scenario: Null parameter validation in state transition
    Given a valid component exists
    When I attempt to set state to null
    Then the operation should fail with IllegalArgumentException containing "state cannot be null"
    And the component state should remain unchanged

  Scenario: Null parameter validation in component termination
    Given a valid component exists
    When I attempt to terminate the component with null reason
    Then the operation should fail with IllegalArgumentException containing "reason cannot be null"
    And the component should not be terminated

  Scenario: Parameter validation for event listener registration
    Given a valid component exists
    When I attempt to register a null state transition listener
    Then the operation should fail with IllegalArgumentException containing "listener cannot be null"
    When I attempt to register a null event listener
    Then the operation should fail with IllegalArgumentException containing "listener cannot be null"
    And no listeners should be registered

  @resilience
  Scenario: Parameter validation during system stress
    Given the system is under high load
    When I perform 1000 component operations with both valid and invalid parameters
    Then all invalid parameters should be consistently rejected
    And all valid operations should succeed
    And parameter validation should add minimal overhead
    And error messages should provide consistent parameter details