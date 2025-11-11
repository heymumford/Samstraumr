# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Component @ErrorHandling @BTL
Feature: Component Exception Handling
  As a framework developer
  I want proper exception handling for component operations
  So that the system fails safely and provides clear error messages

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Create component with null reason
    When I attempt to create an Adam component with null reason
    Then component creation should fail with exception containing "reason cannot be null"
    And the exception should be of type "ComponentInitializationException"
    And no component resources should be allocated

  Scenario: Create component with empty reason
    When I attempt to create an Adam component with empty reason ""
    Then component creation should fail with exception containing "reason cannot be empty"
    And the exception should be of type "ComponentInitializationException"
    And no component resources should be allocated

  Scenario: Create child component with null parent
    When I attempt to create a child component with null parent
    Then component creation should fail with exception containing "parent cannot be null"
    And the exception should be of type "ComponentInitializationException"
    And no component resources should be allocated

  Scenario: Attempt invalid state transition
    Given an Adam component exists with reason "State Transition Test"
    When I attempt to set the component state to an invalid next state
    Then the operation should fail with exception containing "invalid state transition"
    And the exception should be of type "InvalidStateTransitionException"
    And the component should remain in its original state

  Scenario: Attempt to update environment with null key
    Given an Adam component exists with reason "Null Environment Key Test"
    When I attempt to update the environment with a null key
    Then the operation should fail with exception containing "environment key cannot be null"
    And the exception should be of type "IllegalArgumentException"
    And the component environment should remain unchanged

  Scenario: Attempt to update environment with null value
    Given an Adam component exists with reason "Null Environment Value Test"
    When I attempt to update the environment with a null value
    Then the operation should fail with exception containing "environment value cannot be null"
    And the exception should be of type "IllegalArgumentException"
    And the component environment should remain unchanged

  Scenario: Access methods after component termination
    Given an Adam component exists with reason "Post-Termination Access Test"
    And the component is terminated with reason "Normal termination"
    When I attempt to access component methods after termination
    Then each operation should fail with exception containing "component is terminated"
    And the exception should be of type "ComponentOperationException"

  @Resilience
  Scenario: Handle resource allocation failure during creation
    When I create a component with simulated resource allocation failure
    Then component creation should fail with exception containing "resource allocation failed"
    And the exception should be of type "ComponentInitializationException"
    And partial resources should be properly cleaned up

  @Resilience
  Scenario: Handle exception during termination
    Given an Adam component exists with reason "Termination Exception Test"
    When I terminate the component with simulated exception during cleanup
    Then termination should complete despite the exception
    And the component should be in "TERMINATED" state
    And a warning should be logged about cleanup failure
    And all available resources should still be released