# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L0_Unit @L0_Lifecycle @Lifecycle @Functional @ATL
Feature: Component Lifecycle State-Dependent Behavior
  As a framework developer
  I want components to exhibit appropriate state-dependent behavior
  So that they function correctly throughout their lifecycle

  Background:
    Given the S8r framework is initialized

  @smoke
  Scenario: Active state enables full component functionality
    Given a component in "ACTIVE" state
    When I test the component's functionality
    Then all operations should succeed
    And data processing should be enabled
    And monitoring should be enabled
    And configuration changes should be allowed
    And the component should accept incoming connections

  Scenario: Suspended state pauses data processing
    Given a component in "ACTIVE" state with ongoing data processing
    When I transition the component to "SUSPENDED" state
    Then data processing should be paused
    And pending operations should be queued
    And monitoring should remain enabled
    And configuration changes should be restricted
    And the component should reject new data processing requests
    When I transition the component back to "ACTIVE" state
    Then queued operations should resume
    And data processing should continue

  Scenario: Maintenance state allows configuration changes
    Given a component in "ACTIVE" state
    When I transition the component to "MAINTENANCE" state
    Then advanced configuration changes should be allowed
    And data processing should be paused
    And monitoring should remain enabled
    And diagnostic operations should be available
    When I make configuration changes in maintenance mode
    And I transition the component back to "ACTIVE" state
    Then the new configuration should be applied
    And data processing should resume with new configuration

  Scenario: Different states expose different operations
    When I create a component
    Then in "ACTIVE" state the following operations should be available:
      | process_data       |
      | query_status       |
      | update_config      |
      | establish_connection |
    And in "SUSPENDED" state the following operations should be available:
      | query_status       |
      | view_config        |
    And in "MAINTENANCE" state the following operations should be available:
      | query_status       |
      | update_config      |
      | reset_config       |
      | run_diagnostics    |
    And in "TERMINATED" state no operations should be available

  Scenario: Component lifecycle affects resource allocation
    When I create a component with monitored resources
    Then resources should be allocated during initialization
    And resources should be fully available in "ACTIVE" state
    And resources should be partially released in "SUSPENDED" state
    And resources should be partially released in "MAINTENANCE" state
    And all resources should be released in "TERMINATED" state

  Scenario: Component lifecycle affects event subscriptions
    Given a component that publishes and subscribes to events
    When the component is in "ACTIVE" state
    Then it should publish and receive events
    When the component is in "SUSPENDED" state
    Then it should not publish events but still receive them
    When the component is in "MAINTENANCE" state
    Then it should publish diagnostic events but queue normal events
    When the component is in "TERMINATED" state
    Then it should unsubscribe from all events
    And it should not publish any events

  @Resilience
  Scenario: Component should maintain state constraints after recovery
    Given a component in "ACTIVE" state
    When I simulate a recoverable error
    Then the component should enter "RECOVERING" state
    And state constraints should be maintained during recovery
    And operations appropriate for "RECOVERING" state should be available
    When recovery completes
    Then the component should return to "ACTIVE" state
    And normal operations should resume