# Copyright (c) 2025
# All rights reserved.

@port @L1
Feature: Event Publisher Port Interface
  As an application developer
  I want to use a standardized Event Publisher Port interface
  So that I can publish events in a decoupled way

  Background:
    Given an event publisher port implementation is available

  @smoke
  Scenario: Publish a simple event
    Given I have an event subscriber for topic "test-topic"
    When I publish an event to topic "test-topic" with type "test-event" and payload "Test event payload"
    Then the event should be published successfully
    And the subscriber should receive the event
    And the event should have type "test-event"
    And the event should have payload "Test event payload"

  Scenario: Publish an event with additional properties
    Given I have an event subscriber for topic "property-topic"
    When I publish an event with properties:
      | topic     | property-topic      |
      | type      | property-event      |
      | payload   | Event with props    |
      | priority  | high                |
      | source    | test-system         |
    Then the event should be published successfully
    And the subscriber should receive the event with properties:
      | priority  | high        |
      | source    | test-system |

  Scenario: Subscribe and unsubscribe from a topic
    Given I have an event subscriber
    When I subscribe the subscriber to topic "subscribe-test"
    Then I should receive a valid subscription ID
    When I publish an event to topic "subscribe-test" with type "test-event" and payload "Test payload"
    Then the subscriber should receive the event
    When I unsubscribe using the subscription ID
    And I publish another event to the same topic
    Then the subscriber should not receive the second event

  Scenario: Multiple subscribers for the same topic
    Given I have the following subscribers for topic "shared-topic":
      | subscriber1 |
      | subscriber2 |
      | subscriber3 |
    When I publish an event to topic "shared-topic" with type "shared-event" and payload "Shared event payload"
    Then all subscribers should receive the event

  Scenario: Publishing to different topics
    Given I have subscribers for the following topics:
      | topic1 |
      | topic2 |
      | topic3 |
    When I publish events to each topic
    Then each subscriber should receive only events for its topic