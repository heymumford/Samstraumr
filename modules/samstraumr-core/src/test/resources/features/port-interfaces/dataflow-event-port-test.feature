@L2_Integration @Functional @PortInterface @DataFlow
Feature: DataFlow Event Port Interface
  As a system developer
  I want to use the DataFlowEventPort interface for component data communication
  So that I can enable components to communicate data without direct dependencies

  Background:
    Given a clean system environment
    And the DataFlowEventPort interface is properly initialized
    And the following component identities are registered in the system:
      | componentId   | name            | type      |
      | comp-123      | Processor       | processor |
      | comp-456      | DataSource      | source    |
      | comp-789      | DataSink        | sink      |
      | comp-101      | Filter          | filter    |
      | comp-202      | Transformer     | transform |

  Scenario: Component subscription to a data channel
    When component "comp-123" subscribes to data channel "sensor-data"
    Then component "comp-123" should be subscribed to channel "sensor-data"
    And the list of available channels should include "sensor-data"
    And the list of component "comp-123" subscriptions should include "sensor-data"

  Scenario: Component unsubscription from a data channel
    Given component "comp-123" subscribes to data channel "sensor-data"
    When component "comp-123" unsubscribes from data channel "sensor-data"
    Then component "comp-123" should not be subscribed to channel "sensor-data"
    And the list of available channels should not include "sensor-data"
    And the list of component "comp-123" subscriptions should not include "sensor-data"

  Scenario: Component subscription to multiple channels
    When component "comp-123" subscribes to data channel "sensor-data"
    And component "comp-123" subscribes to data channel "control-data"
    Then component "comp-123" should be subscribed to channel "sensor-data"
    And component "comp-123" should be subscribed to channel "control-data"
    And the list of available channels should include "sensor-data"
    And the list of available channels should include "control-data"
    And the list of component "comp-123" subscriptions should include "sensor-data"
    And the list of component "comp-123" subscriptions should include "control-data"

  Scenario: Multiple components subscribe to the same channel
    When component "comp-123" subscribes to data channel "shared-data"
    And component "comp-456" subscribes to data channel "shared-data"
    Then component "comp-123" should be subscribed to channel "shared-data"
    And component "comp-456" should be subscribed to channel "shared-data"
    And the list of available channels should include "shared-data"
    And the list of component "comp-123" subscriptions should include "shared-data"
    And the list of component "comp-456" subscriptions should include "shared-data"

  Scenario: Component publishes data to a channel with a subscriber
    Given component "comp-456" subscribes to data channel "sensor-data"
    When component "comp-123" publishes data to channel "sensor-data" with payload:
      | key           | value      |
      | temperature   | 25.5       |
      | humidity      | 60         |
    Then component "comp-456" should receive data on channel "sensor-data"
    And the received data should contain key "temperature" with value "25.5"
    And the received data should contain key "humidity" with value "60"

  Scenario: Component publishes data to a channel with no subscribers
    When component "comp-123" publishes data to channel "empty-channel" with payload:
      | key           | value     |
      | status        | ready     |
    Then no components should receive data

  Scenario: Component publishes data to a channel with multiple subscribers
    Given component "comp-456" subscribes to data channel "broadcast-channel"
    And component "comp-789" subscribes to data channel "broadcast-channel"
    When component "comp-123" publishes data to channel "broadcast-channel" with payload:
      | key           | value       |
      | message       | test        |
    Then component "comp-456" should receive data on channel "broadcast-channel"
    And component "comp-789" should receive data on channel "broadcast-channel"
    And all components should receive the same data payload

  Scenario: Component unsubscribes from all channels
    Given component "comp-123" subscribes to data channel "channel-1"
    And component "comp-123" subscribes to data channel "channel-2"
    And component "comp-123" subscribes to data channel "channel-3"
    When component "comp-123" unsubscribes from all channels
    Then component "comp-123" should not be subscribed to any channels
    And the list of component "comp-123" subscriptions should be empty

  Scenario: Publishing data without loopback to the publisher
    Given component "comp-123" subscribes to data channel "loopback-test"
    When component "comp-123" publishes data to channel "loopback-test" with payload:
      | key           | value     |
      | test          | loopback  |
    Then component "comp-123" should not receive its own published data