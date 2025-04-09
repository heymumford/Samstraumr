@L2_Integration @Functional @DataFlow @Messaging @integration
Feature: DataFlow Event and Messaging Port Integration
  As a system developer
  I want to integrate DataFlowEventPort with MessagingPort
  So that component data events can be published to messaging channels and vice versa

  Background:
    Given a clean system environment
    And the DataFlowEventPort interface is properly initialized
    And the MessagingPort interface is properly initialized
    And the following component identities are registered in the system:
      | componentId   | name            | type      |
      | comp-123      | Processor       | processor |
      | comp-456      | DataSource      | source    |
      | comp-789      | DataSink        | sink      |
      | comp-101      | Bridge          | bridge    |

  Scenario: Convert DataFlow event to Messaging channel message
    Given component "comp-123" subscribes to data channel "sensor-data"
    And a messaging subscriber is registered for channel "external-sensor-data"
    And a data flow to messaging bridge is configured for channel "sensor-data" to "external-sensor-data"
    When component "comp-456" publishes data to channel "sensor-data" with payload:
      | key           | value      |
      | temperature   | 25.5       |
      | humidity      | 60         |
    Then component "comp-123" should receive data on channel "sensor-data"
    And the messaging subscriber should receive a message on channel "external-sensor-data"
    And the message should contain key "temperature" with value "25.5"
    And the message should contain key "humidity" with value "60"
    And the message should contain key "sourceComponent" with the value of component "comp-456"

  Scenario: Convert Messaging channel message to DataFlow event
    Given a messaging subscriber is registered for channel "external-control"
    And component "comp-789" subscribes to data channel "control-data"
    And a messaging to data flow bridge is configured for channel "external-control" to "control-data"
    When a message is published to messaging channel "external-control" with payload:
      | key           | value      |
      | command       | activate   |
      | target        | sensor-1   |
    Then component "comp-789" should receive data on channel "control-data"
    And the received data should contain key "command" with value "activate"
    And the received data should contain key "target" with value "sensor-1"
    
  Scenario: Bidirectional communication between DataFlow and Messaging
    Given component "comp-123" subscribes to data channel "internal-events"
    And a messaging subscriber is registered for channel "external-events"
    And a bidirectional bridge is configured between "internal-events" and "external-events"
    When component "comp-456" publishes data to channel "internal-events" with payload:
      | key           | value      |
      | event         | started    |
      | source        | internal   |
    Then component "comp-123" should receive data on channel "internal-events"
    And the messaging subscriber should receive a message on channel "external-events"
    And the message should contain key "event" with value "started"
    When a message is published to messaging channel "external-events" with payload:
      | key           | value      |
      | event         | updated    |
      | source        | external   |
    Then component "comp-123" should receive data on channel "internal-events"
    And the received data should contain key "event" with value "updated"
    And the received data should contain key "source" with value "external"

  Scenario: Error handling during DataFlow to Messaging conversion
    Given component "comp-123" subscribes to data channel "error-test"
    And a messaging subscriber is registered for channel "error-channel"
    And a data flow to messaging bridge is configured for channel "error-test" to "error-channel" with error handling
    When component "comp-456" publishes invalid data to channel "error-test"
    Then component "comp-123" should receive data on channel "error-test"
    And the messaging subscriber should receive an error message on channel "error-channel"
    And the error message should contain details about the conversion failure

  Scenario: Data transformation during DataFlow to Messaging conversion
    Given component "comp-123" subscribes to data channel "transform-test"
    And a messaging subscriber is registered for channel "transformed-data"
    And a data flow to messaging bridge is configured for channel "transform-test" to "transformed-data" with transformation:
      | sourceKey     | targetKey    | transformation |
      | temp_c        | temperature  | fahrenheit     |
      | hum_pct       | humidity     | none           |
    When component "comp-456" publishes data to channel "transform-test" with payload:
      | key           | value      |
      | temp_c        | 20.0       |
      | hum_pct       | 55         |
    Then component "comp-123" should receive data on channel "transform-test"
    And the messaging subscriber should receive a message on channel "transformed-data"
    And the message should contain key "temperature" with value "68.0"
    And the message should contain key "humidity" with value "55"