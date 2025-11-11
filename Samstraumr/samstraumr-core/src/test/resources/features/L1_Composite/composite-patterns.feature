@L1_Component @L1_Composite @Functional @ATL @CompositeTest @Patterns
Feature: Composite Design Patterns
  As a system architect
  I want to implement design patterns within composites
  So that I can create reusable, flexible component structures

  Background:
    Given the Samstraumr system is initialized
    And I have created a composite component "PatternComposite" with reason "Pattern Tests"

  @Observer
  Scenario: Observer pattern implementation
    Given I have created an "Observable" component named "SubjectComponent"
    And I have created the following "Observer" components:
      | name          | reason         |
      | Observer1     | First Observer |
      | Observer2     | Second Observer|
      | Observer3     | Third Observer |
    When I register all observer components with the subject component
    And the "SubjectComponent" changes its state to "UPDATED"
    Then all observer components should be notified of the state change
    And each observer should update its internal state accordingly

  @Transformer
  Scenario: Transformer pattern implementation
    Given I have created the following components in the composite:
      | name          | type           | reason          |
      | InputComp     | Source         | Input Source    |
      | TransformerA  | Transformer    | First Transform |
      | TransformerB  | Transformer    | Second Transform|
      | OutputComp    | Sink           | Output Sink     |
    When I connect the components in a transformation pipeline:
      | from          | to             |
      | InputComp     | TransformerA   |
      | TransformerA  | TransformerB   |
      | TransformerB  | OutputComp     |
    And I configure "TransformerA" to perform "UPPERCASE" transformation
    And I configure "TransformerB" to perform "REVERSE" transformation
    And I send data "test message" from "InputComp"
    Then "TransformerA" should transform the data to "TEST MESSAGE"
    And "TransformerB" should transform the data to "EGASSEM TSET"
    And "OutputComp" should receive "EGASSEM TSET"

  @Filter
  Scenario: Filter pattern implementation
    Given I have created the following components in the composite:
      | name          | type           | reason          |
      | SourceComp    | Source         | Data Source     |
      | EvenFilter    | Filter         | Even Filter     |
      | OddFilter     | Filter         | Odd Filter      |
      | EvenSink      | Sink           | Even Collector  |
      | OddSink       | Sink           | Odd Collector   |
    When I configure "EvenFilter" with condition "number % 2 == 0"
    And I configure "OddFilter" with condition "number % 2 != 0"
    And I connect "SourceComp" to both "EvenFilter" and "OddFilter"
    And I connect "EvenFilter" to "EvenSink"
    And I connect "OddFilter" to "OddSink"
    And I send the following data from "SourceComp":
      | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 |
    Then "EvenSink" should receive only even numbers: 2, 4, 6, 8, 10
    And "OddSink" should receive only odd numbers: 1, 3, 5, 7, 9

  @Router
  Scenario: Router pattern implementation
    Given I have created the following components in the composite:
      | name          | type           | reason           |
      | SourceComp    | Source         | Message Source   |
      | RouterComp    | Router         | Message Router   |
      | HighPriority  | Sink           | Priority Channel |
      | NormalPriority| Sink           | Normal Channel   |
      | LowPriority   | Sink           | Low Channel      |
    When I configure "RouterComp" with the following routing rules:
      | condition            | destination     |
      | priority == "HIGH"   | HighPriority    |
      | priority == "NORMAL" | NormalPriority  |
      | priority == "LOW"    | LowPriority     |
    And I connect "SourceComp" to "RouterComp"
    And I connect "RouterComp" to all destination components
    And I send the following messages from "SourceComp":
      | content       | priority |
      | "Critical"    | "HIGH"   |
      | "Important"   | "NORMAL" |
      | "Info"        | "LOW"    |
      | "Emergency"   | "HIGH"   |
    Then "HighPriority" should receive messages: "Critical", "Emergency"
    And "NormalPriority" should receive message: "Important"
    And "LowPriority" should receive message: "Info"

  @Aggregator
  Scenario: Aggregator pattern implementation
    Given I have created the following components in the composite:
      | name          | type           | reason            |
      | Source1       | Source         | First Source      |
      | Source2       | Source         | Second Source     |
      | Source3       | Source         | Third Source      |
      | AggregatorComp| Aggregator     | Message Aggregator|
      | ResultSink    | Sink           | Result Collector  |
    When I configure "AggregatorComp" to correlate messages by "correlationId"
    And I configure "AggregatorComp" to aggregate when all 3 source messages are received
    And I configure "AggregatorComp" to use "CONCATENATE" aggregation strategy
    And I connect all source components to "AggregatorComp"
    And I connect "AggregatorComp" to "ResultSink"
    And I send the following correlated messages:
      | source        | content        | correlationId |
      | Source1       | "Part1"        | "GROUP-A"     |
      | Source2       | "Part2"        | "GROUP-A"     |
      | Source3       | "Part3"        | "GROUP-A"     |
    Then "AggregatorComp" should aggregate the messages with "GROUP-A" correlationId
    And "ResultSink" should receive the aggregated message "Part1Part2Part3"

  @Saga
  Scenario: Saga pattern for distributed transactions
    Given I have created the following components in the composite:
      | name          | type           | reason            |
      | OrderService  | Service        | Order Service     |
      | PaymentService| Service        | Payment Service   |
      | InventoryService| Service      | Inventory Service |
      | DeliveryService| Service       | Delivery Service  |
      | SagaCoordinator| Coordinator   | Transaction Coord |
    When I configure "SagaCoordinator" to manage the transaction across all services
    And I configure compensation handlers for each service
    And I start a transaction with order data "OrderXYZ"
    Then all services should process their part of the transaction
    And the transaction should complete successfully
    When I start a transaction with order data "FailingOrder"
    And I configure "InventoryService" to fail
    Then the transaction should be rolled back
    And all completed steps should be compensated