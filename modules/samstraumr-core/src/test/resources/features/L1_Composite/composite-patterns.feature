# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@L1_Component @L1_Composite @Functional @ATL @CompositeTest @Patterns
Feature: Composite Component Design Patterns
  As a framework developer
  I want composites to implement standard design patterns
  So that common component interaction patterns can be reused

  Background:
    Given the S8r framework is initialized

  @smoke @Observer
  Scenario: Observer pattern implementation in composite
    Given a composite implementing the Observer pattern
    And the composite has 3 observer components and 1 subject component
    When the subject component changes state
    Then all observer components should be notified
    And each observer should record the state change
    And the notification should include the previous and new states
    And the observer notifications should happen in registration order

  @Transformer
  Scenario: Transformer pattern implementation in composite
    Given a composite implementing the Transformer pattern
    And the composite has a chain of transformer components:
      | transformerName | transformation    |
      | Converter       | format conversion |
      | Enricher        | data enrichment   |
      | Validator       | data validation   |
    When I send input data through the transformer chain
    Then each transformer should apply its transformation
    And the transformations should be applied in sequence
    And the output should reflect all transformations
    And each transformation step should be logged

  @Filter
  Scenario: Filter pattern implementation in composite
    Given a composite implementing the Filter pattern
    And the composite has filter components with these criteria:
      | filterName   | filterCriteria    | action  |
      | TypeFilter   | messageType=INFO  | include |
      | ErrorFilter  | severity>5        | include |
      | SizeFilter   | size<1000         | include |
    When I process these messages through the filter chain:
      | messageId | messageType | severity | size  | expectedResult |
      | msg1      | INFO        | 3        | 500   | pass           |
      | msg2      | ERROR       | 7        | 1500  | reject         |
      | msg3      | INFO        | 8        | 800   | pass           |
      | msg4      | DEBUG       | 2        | 300   | reject         |
      | msg5      | ERROR       | 9        | 750   | pass           |
    Then each message should be handled according to expectation
    And filter statistics should be correctly maintained
    And the composite should report overall filter efficiency

  @Router
  Scenario: Router pattern implementation in composite
    Given a composite implementing the Router pattern
    And the composite has these routing components:
      | routeName    | routingCondition  | destination  |
      | ErrorRoute   | isError=true      | ErrorHandler |
      | UrgentRoute  | priority>8        | UrgentQueue  |
      | StandardRoute| default           | NormalQueue  |
    When I send these messages through the router:
      | messageId | isError | priority | expectedDestination |
      | msg1      | false   | 5        | NormalQueue         |
      | msg2      | true    | 3        | ErrorHandler        |
      | msg3      | false   | 9        | UrgentQueue         |
      | msg4      | true    | 10       | ErrorHandler        |
    Then each message should be routed to the correct destination
    And no messages should be lost
    And routing decisions should be logged
    And the router should maintain routing statistics

  @Aggregator
  Scenario: Aggregator pattern implementation in composite
    Given a composite implementing the Aggregator pattern
    And the composite is configured to aggregate with correlation key "orderId"
    And the composite has a completion condition of "count=3 or timeout=5s"
    When I send these message fragments:
      | fragmentId | orderId | fragment | sendTime |
      | frag1      | order1  | part1    | t+0s     |
      | frag2      | order1  | part2    | t+1s     |
      | frag3      | order2  | part1    | t+2s     |
      | frag4      | order1  | part3    | t+3s     |
      | frag5      | order3  | part1    | t+4s     |
      | frag6      | order2  | part2    | t+6s     |
    Then order1 should be aggregated by count condition
    And order2 should be aggregated by timeout condition
    And order3 should remain incomplete
    And complete aggregations should be properly assembled
    And timeout handling should be consistent with configuration

  @Saga
  Scenario: Saga pattern implementation in composite
    Given a composite implementing the Saga pattern
    And the composite manages this transaction sequence:
      | stepName       | operation       | compensationOperation |
      | ReserveCredit  | debitAccount    | creditAccount         |
      | CreateOrder    | insertOrder     | deleteOrder           |
      | UpdateInventory| reduceInventory | increaseInventory     |
      | SendNotification| sendEmail      | sendCancellation      |
    When I execute the saga with these step results:
      | scenario      | ReserveCredit | CreateOrder | UpdateInventory | SendNotification |
      | allSuccess    | success       | success     | success         | success          |
      | midFailure    | success       | success     | failure         | skipped          |
      | startFailure  | failure       | skipped     | skipped         | skipped          |
    Then for allSuccess, all steps should complete without compensation
    And for midFailure, compensation should run for ReserveCredit and CreateOrder
    And for startFailure, no compensation should be needed
    And saga execution should be fully tracked
    And the composite should maintain transaction integrity