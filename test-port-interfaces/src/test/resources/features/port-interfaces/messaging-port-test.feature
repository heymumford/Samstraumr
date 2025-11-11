@L2_Integration @Functional @PortInterface @Messaging
Feature: Messaging Port Interface
  As a system developer
  I want to use the MessagingPort interface for message-based communication
  So that I can implement messaging patterns in my application

  Background:
    Given a clean messaging environment
    And the MessagingPort interface is initialized

  Scenario: Creating a channel
    When I create a "test-queue" channel of type "QUEUE"
    Then the channel should be created successfully
    And the channel should be retrievable
    And the channel should have 0 subscribers

  Scenario: Sending a message to a channel
    Given a "test-topic" channel of type "TOPIC" exists
    When I create a message with payload "Hello, World!"
    And I send the message to the "test-topic" channel
    Then the send operation should be successful

  Scenario: Subscribing to a channel
    Given a "news-feed" channel of type "TOPIC" exists
    When I subscribe to the "news-feed" channel
    Then the subscription should be successful
    And the channel should have 1 subscriber

  Scenario: Receiving messages from a subscribed channel
    Given a "events" channel of type "TOPIC" exists
    And I am subscribed to the "events" channel
    When a message with payload "New Event" is sent to the "events" channel
    Then I should receive the message with payload "New Event"

  Scenario: Unsubscribing from a channel
    Given a "notifications" channel of type "TOPIC" exists
    And I am subscribed to the "notifications" channel
    When I unsubscribe from the "notifications" channel
    Then the unsubscription should be successful
    And the channel should have 0 subscribers

  Scenario: Request-reply messaging
    Given a "request-service" channel of type "REQUEST_REPLY" exists
    And a reply handler is registered for the "request-service" channel
    When I send a request with payload "Get Data" to the "request-service" channel
    Then I should receive a reply
    And the reply payload should contain "Data Response"

  Scenario: Message delivery with options
    Given a "priority-queue" channel of type "QUEUE" exists
    When I create a message with payload "Priority Message"
    And I set the following delivery options:
      | timeToLive | 60000      |
      | priority   | 9          |
      | persistent | true       |
      | mode       | AT_LEAST_ONCE |
    And I send the message with delivery options to the "priority-queue" channel
    Then the send operation should be successful

  Scenario: Channel with custom properties
    When I create a channel with the following properties:
      | name        | custom-channel   |
      | type        | TOPIC            |
      | maxSubscribers | 10            |
      | maxMessageSize | 1048576       |
      | messageTtl  | 3600000          |
      | durable     | true             |
      | autoDelete  | false            |
    Then the channel should be created successfully
    And the channel properties should match the specified values

  Scenario: Deleting a channel
    Given a "temporary" channel of type "QUEUE" exists
    When I delete the "temporary" channel
    Then the delete operation should be successful
    And the channel should no longer exist