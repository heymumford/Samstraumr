# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L1_Component @L1_Composite @Functional @ATL @CompositeTest
Feature: Composite Component Interactions
  As a framework developer
  I want composites to manage interactions between their child components
  So that components can work together in coordinated ways

  Background:
    Given the S8r framework is initialized

  @smoke @DataFlow
  Scenario: Communication between components in a composite
    Given a composite with 3 interconnected components
      | componentName | componentType | connections       |
      | Source        | Producer      |                   |
      | Processor     | Transformer   | Source            |
      | Destination   | Consumer      | Processor         |
    When I send data through the Source component
    Then the data should flow through all components in sequence
    And each component should process the data according to its type
    And the Destination component should receive the transformed data
    And the composite should track the complete data flow

  Scenario: Event propagation through composite hierarchy
    Given a composite hierarchy with the following structure:
      | name        | level | parent      |
      | Root        | 0     |             |
      | Branch1     | 1     | Root        |
      | Branch2     | 1     | Root        |
      | Leaf1       | 2     | Branch1     |
      | Leaf2       | 2     | Branch1     |
      | Leaf3       | 2     | Branch2     |
    When an event is triggered at the Leaf1 component
    Then the event should propagate up to Branch1
    And the event should propagate up to Root
    And sibling components should not receive the event
    And the event metadata should contain the full propagation path

  Scenario: State synchronization between composite and children
    Given a composite with 5 child components
    When I change the state of the composite to "SUSPENDED"
    Then all child components should transition to "SUSPENDED" state
    When I change the state of the composite to "ACTIVE"
    Then all child components should transition to "ACTIVE" state
    When I change the state of one child component to "MAINTENANCE"
    Then the composite should detect the child state change
    And the composite should update its own state accordingly

  Scenario: Resource sharing between components in a composite
    Given a composite with shared resources:
      | resourceType | capacity | initialUsage |
      | Memory       | 100MB    | 0MB          |
      | Connections  | 10       | 0            |
      | Threads      | 5        | 0            |
    When child components request resources:
      | componentName | resourceType | amount |
      | Component1    | Memory       | 30MB   |
      | Component2    | Memory       | 40MB   |
      | Component1    | Connections  | 3      |
      | Component3    | Threads      | 2      |
    Then resources should be allocated correctly
    And the composite should track resource usage
    And attempting to exceed capacity should be handled properly
    And releasing resources should make them available again

  Scenario: Error handling within a composite
    Given a composite with error handling configuration:
      | errorType           | strategy    |
      | ValidationError     | Retry       |
      | ConnectionError     | Recover     |
      | ResourceError       | Terminate   |
      | UnknownError        | Propagate   |
    When different errors occur in child components:
      | componentName | errorType       |
      | Component1    | ValidationError |
      | Component2    | ConnectionError |
      | Component3    | ResourceError   |
      | Component4    | UnknownError    |
    Then each error should be handled according to its strategy
    And the composite should maintain a consistent state
    And error details should be logged appropriately
    And the composite should report error handling results

  @Resilience
  Scenario: Recovery of composite after child component failure
    Given a composite with 5 child components performing coordinated operations
    When one of the child components fails catastrophically
    Then the composite should isolate the failed component
    And the composite should attempt recovery based on policy
    And other child components should continue operating if possible
    And the composite should report the failure and recovery attempt status
    And a replacement component should be created if policy allows
    And operations should be resumed with minimal disruption

  @Security
  Scenario: Composite enforces security boundaries between components
    Given a composite with components of different security levels:
      | componentName | securityLevel | allowedConnections |
      | HighSec       | High          | HighSec, MediumSec |
      | MediumSec     | Medium        | MediumSec, LowSec  |
      | LowSec        | Low           | LowSec             |
    When components attempt connections across security levels:
      | fromComponent | toComponent | expectedResult |
      | HighSec       | MediumSec   | Allow          |
      | HighSec       | LowSec      | Deny           |
      | MediumSec     | HighSec     | Deny           |
      | MediumSec     | LowSec      | Allow          |
      | LowSec        | MediumSec   | Deny           |
      | LowSec        | HighSec     | Deny           |
    Then each connection attempt should be properly controlled
    And security violations should be logged and reported
    And the composite should maintain security integrity