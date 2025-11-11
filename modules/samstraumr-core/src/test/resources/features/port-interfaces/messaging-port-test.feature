@L2_Integration @Functional @PortInterface @Messaging
Feature: Messaging Port Interface
  As a system developer
  I want to use the MessagingPort interface for inter-component messaging
  So that I can enable reliable communication between components

  Background:
    Given a clean system environment
    And the MessagingPort interface is properly initialized

  Scenario: Creating a messaging channel
    When I create a messaging channel "test-channel" of type "QUEUE"
    Then the messaging channel should be created successfully
    And the channel "test-channel" should be available
    And the channel "test-channel" should be of type "QUEUE"

  Scenario: Creating messaging channels of different types
    When I create a messaging channel "queue-channel" of type "QUEUE"
    And I create a messaging channel "topic-channel" of type "TOPIC"
    And I create a messaging channel "request-reply-channel" of type "REQUEST_REPLY"
    Then the channel "queue-channel" should be of type "QUEUE"
    And the channel "topic-channel" should be of type "TOPIC"
    And the channel "request-reply-channel" should be of type "REQUEST_REPLY"

  Scenario: Creating a channel with custom properties
    When I create a channel "custom-channel" with properties:
      | property     | value    |
      | durable      | true     |
      | autoDelete   | false    |
      | maxMessageSize | 1048576 |
      | messageTtl   | PT1H     |
    Then the channel "custom-channel" should be created successfully
    And the channel "custom-channel" should have property "durable" set to "true"
    And the channel "custom-channel" should have property "autoDelete" set to "false"
    And the channel "custom-channel" should have property "maxMessageSize" set to "1048576"
    And the channel "custom-channel" should have property "messageTtl" set to "PT1H"

  Scenario: Sending a message to a queue
    Given a messaging channel "test-queue" of type "QUEUE" exists
    When I send a message to channel "test-queue" with payload:
      """
      {
        "id": "msg-123",
        "data": "Test message content"
      }
      """
    Then the message should be sent successfully to channel "test-queue"

  Scenario: Subscribing to a channel and receiving messages
    Given a messaging channel "subscribe-test" of type "TOPIC" exists
    When I subscribe to channel "subscribe-test"
    And I send a message to channel "subscribe-test" with payload:
      """
      {
        "id": "msg-456",
        "data": "Subscription test message"
      }
      """
    Then I should receive a message on channel "subscribe-test"
    And the message payload should contain "Subscription test message"

  Scenario: Multiple subscribers to a topic channel
    Given a messaging channel "multi-sub-topic" of type "TOPIC" exists
    When subscriber "sub1" subscribes to channel "multi-sub-topic"
    And subscriber "sub2" subscribes to channel "multi-sub-topic"
    And I send a message to channel "multi-sub-topic" with payload:
      """
      {
        "id": "msg-789",
        "data": "Multi-subscriber test"
      }
      """
    Then subscriber "sub1" should receive the message on channel "multi-sub-topic"
    And subscriber "sub2" should receive the message on channel "multi-sub-topic"
    And the channel "multi-sub-topic" should have 2 subscribers

  Scenario: Unsubscribing from a channel
    Given a messaging channel "unsub-test" of type "TOPIC" exists
    And I subscribe to channel "unsub-test" with subscription ID "sub-123"
    When I unsubscribe from channel "unsub-test" with subscription ID "sub-123"
    Then the unsubscription should be successful
    And I should not receive messages on channel "unsub-test"

  Scenario: Sending a message with delivery options
    Given a messaging channel "delivery-options-test" of type "QUEUE" exists
    When I send a message to channel "delivery-options-test" with options:
      | option       | value    |
      | priority     | 9        |
      | persistent   | true     |
      | timeToLive   | PT5M     |
      | deliveryMode | EXACTLY_ONCE |
    Then the message should be sent successfully to channel "delivery-options-test"
    And the message delivery should respect the specified options

  Scenario: Request-reply messaging pattern
    Given a messaging channel "request-reply-test" of type "REQUEST_REPLY" exists
    And I register a reply handler for channel "request-reply-test" that replies with "This is the reply"
    When I send a request to channel "request-reply-test" with payload:
      """
      {
        "id": "req-123",
        "data": "Request data"
      }
      """
    Then I should receive a reply from channel "request-reply-test"
    And the reply payload should contain "This is the reply"

  Scenario: Request-reply with timeout
    Given a messaging channel "timeout-test" of type "REQUEST_REPLY" exists
    And I register a reply handler for channel "timeout-test" that delays 2 seconds before replying
    When I send a request to channel "timeout-test" with timeout of 1 second
    Then the request should timeout
    And I should receive an appropriate timeout error

  Scenario: Deleting a channel
    Given a messaging channel "delete-test" of type "QUEUE" exists
    When I delete the channel "delete-test"
    Then the channel deletion should be successful
    And the channel "delete-test" should no longer be available

  Scenario: Getting all channels
    Given the following messaging channels exist:
      | name     | type  |
      | channel1 | QUEUE |
      | channel2 | TOPIC |
      | channel3 | REQUEST_REPLY |
    When I request all available channels
    Then the response should include channels "channel1,channel2,channel3"

  Scenario: Shutting down the messaging system
    When I shut down the messaging system
    Then the messaging system should shut down gracefully
    And all resources should be released properly