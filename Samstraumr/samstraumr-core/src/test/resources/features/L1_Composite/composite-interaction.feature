@L1_Component @L1_Composite @Functional @ATL @CompositeTest
Feature: Composite Component Interactions
  As a system architect
  I want to facilitate interactions between components in a composite
  So that they can collaborate effectively

  Background:
    Given the Samstraumr system is initialized
    And I have created a composite component "MainComposite" with reason "Interaction Test"

  @DataFlow
  Scenario: Data flows correctly between connected components in a composite
    Given I have created the following components in the composite:
      | name          | type          | reason         |
      | SourceComp    | Source        | Data Source    |
      | ProcessComp   | Processor     | Data Processor |
      | SinkComp      | Sink          | Data Sink      |
    When I connect "SourceComp" to "ProcessComp"
    And I connect "ProcessComp" to "SinkComp"
    And I send data "TestPayload" from "SourceComp"
    Then "ProcessComp" should receive the data "TestPayload"
    And "SinkComp" should receive the processed data

  @Events
  Scenario: Events propagate through the composite hierarchy
    Given I have created the following components in the composite:
      | name          | reason         |
      | PublisherComp | Event Source   |
      | ListenerComp1 | First Listener |
      | ListenerComp2 | Second Listener|
    When I register "ListenerComp1" as a listener for events from "PublisherComp"
    And I register "ListenerComp2" as a listener for events from "PublisherComp"
    And "PublisherComp" publishes an event "TestEvent" with payload "EventData"
    Then "ListenerComp1" should receive the event "TestEvent"
    And "ListenerComp2" should receive the event "TestEvent"
    And both listeners should process the event payload "EventData"

  @StateSync
  Scenario: State changes synchronize across related components
    Given I have created the following components in the composite:
      | name          | reason         |
      | MasterComp    | State Master   |
      | SlaveComp1    | First Slave    |
      | SlaveComp2    | Second Slave   |
    When I create a state synchronization relationship between:
      | master        | slave          |
      | MasterComp    | SlaveComp1     |
      | MasterComp    | SlaveComp2     |
    And I change the state of "MasterComp" to "SUSPENDED"
    Then the state of "SlaveComp1" should be "SUSPENDED"
    And the state of "SlaveComp2" should be "SUSPENDED"
    When I change the state of "MasterComp" to "ACTIVE"
    Then the state of "SlaveComp1" should be "ACTIVE"
    And the state of "SlaveComp2" should be "ACTIVE"

  @Resources
  Scenario: Components share resources within a composite
    Given I have created a shared resource "Database" in the composite
    And I have created the following components in the composite:
      | name          | reason           |
      | ReaderComp    | Resource Reader  |
      | WriterComp    | Resource Writer  |
    When I grant "ReaderComp" access to the "Database" resource with "READ" permission
    And I grant "WriterComp" access to the "Database" resource with "WRITE" permission
    And "WriterComp" writes data "SharedValue" to the "Database" resource
    Then "ReaderComp" should be able to read "SharedValue" from the "Database" resource

  @ErrorHandling
  Scenario: Error handling in component interactions
    Given I have created the following components in the composite:
      | name           | reason         |
      | SenderComp     | Data Sender    |
      | ReceiverComp   | Data Receiver  |
      | MonitorComp    | Error Monitor  |
    And I have connected "SenderComp" to "ReceiverComp"
    And I have registered "MonitorComp" as an error handler for "ReceiverComp"
    When I configure "ReceiverComp" to throw an exception on receiving data
    And I send data "ErrorTrigger" from "SenderComp"
    Then "ReceiverComp" should throw an exception
    And "MonitorComp" should be notified of the error
    And the composite should continue functioning