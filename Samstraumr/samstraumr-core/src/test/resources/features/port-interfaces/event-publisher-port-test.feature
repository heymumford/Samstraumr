@L2_Integration @Functional @PortInterface @EventPublisher
Feature: Event Publisher Port Interface
  As a system developer
  I want to use the EventPublisherPort interface for publishing domain events
  So that I can keep my application core independent of event dispatch mechanisms

  Background:
    Given a clean system environment
    And the EventPublisherPort interface is properly initialized

  Scenario: Publishing a single domain event
    When I create a domain event of type "ComponentCreatedEvent"
    And I publish the event through the EventPublisherPort
    Then the event should be successfully published
    And event subscribers should be notified
    And the event should be available in the event log

  Scenario: Publishing multiple domain events in a batch
    When I create a list of domain events:
      | type                         | source         |
      | ComponentCreatedEvent        | Component-001  |
      | MachineStateChangedEvent     | Machine-002    |
      | ComponentStateChangedEvent   | Component-003  |
    And I publish the event list through the EventPublisherPort
    Then all events should be successfully published
    And subscribers should receive all events in the correct order
    And all events should be available in the event log

  Scenario: Publishing events from a component
    Given a component with ID "test-component-001" exists in the system
    When the component generates several domain events
    And I publish all pending events from the component
    Then all component events should be successfully published
    And the component's event queue should be empty
    And subscribers should receive all component events

  Scenario: Event publishing with error handling
    When I attempt to publish an invalid event
    Then the system should handle the error gracefully
    And an appropriate error message should be logged
    And other system operations should not be affected

  Scenario: Asynchronous event publishing
    When I publish an event asynchronously
    Then the publishing request should be accepted immediately
    And the event should be processed in the background
    And subscribers should eventually receive the event

  Scenario: Event publishing with priority levels
    When I publish events with different priority levels:
      | type                         | priority  |
      | ComponentCreatedEvent        | HIGH      |
      | MachineStateChangedEvent     | MEDIUM    |
      | ComponentStateChangedEvent   | LOW       |
    Then the events should be delivered according to their priority levels
    And high priority events should be processed before lower priority ones

  Scenario: Publishing events to specific subscribers
    Given several event subscribers are registered in the system
    When I publish an event with a specific target subscriber
    Then only the targeted subscriber should receive the event
    And other subscribers should not be notified

  Scenario: Event filtering based on type
    Given event subscribers with different type filters are registered
    When I publish events of different types
    Then subscribers should only receive events matching their filters
    And no subscriber should receive events they didn't register for