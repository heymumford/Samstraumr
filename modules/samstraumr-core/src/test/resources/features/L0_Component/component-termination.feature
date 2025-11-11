# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Component @Functional @ATL @Lifecycle
Feature: Component Termination
  As a framework developer
  I want to terminate components properly
  So that resources are released and the lifecycle ends correctly

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Terminate an Adam component normally
    Given an Adam component exists with reason "Termination Test Component"
    When I terminate the component with reason "Normal termination"
    Then the component should be in "TERMINATED" state
    And the component log should contain an entry for "termination"
    And all component resources should be released

  Scenario: Terminate a child component normally
    Given an Adam component exists with reason "Parent Component"
    And a child component exists with reason "Child Component"
    When I terminate the child component with reason "Normal child termination"
    Then the child component should be in "TERMINATED" state
    And the parent component should remain in "ACTIVE" state
    And the child component log should contain an entry for "termination"
    And all child component resources should be released

  Scenario: Terminate parent component with automatic child termination
    Given an Adam component exists with reason "Auto-Termination Parent"
    And 3 child components exist with the parent
    When I terminate the parent component with reason "Parent termination with children"
    Then the parent component should be in "TERMINATED" state
    And all child components should be in "TERMINATED" state
    And the parent component log should contain an entry for "termination"
    And all child component logs should contain entries for "termination"
    And all resources should be released for parent and children

  @Performance
  Scenario: Terminate component with timer cancellation
    Given an Adam component exists with reason "Timer Component"
    And the component has active timers
    When I terminate the component with reason "Termination with timers"
    Then the component should be in "TERMINATED" state
    And all component timers should be cancelled
    And all component resources should be released

  @ErrorHandling @BTL
  Scenario: Attempt to perform operations on a terminated component
    Given an Adam component exists with reason "Post-Termination Test"
    And the component is terminated with reason "Pre-test termination"
    When I attempt to perform operations on the terminated component
    Then all operations should fail with exception containing "component is terminated"

  @ErrorHandling @BTL
  Scenario: Attempt to terminate a component twice
    Given an Adam component exists with reason "Double Termination Test"
    And the component is terminated with reason "First termination"
    When I attempt to terminate the component again with reason "Second termination"
    Then the second termination should be ignored
    And the component should remain in "TERMINATED" state
    And a warning should be logged about redundant termination