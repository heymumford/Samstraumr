# ---------------------------------------------------------------------------------------
# MachineConnectivityPatternsTest.feature - Machine Connectivity Pattern Tests
#
# This feature file contains tests for various connectivity patterns between
# components within a machine, validating that different topologies work correctly
# for data flow and state management.
# ---------------------------------------------------------------------------------------

@L2_Integration @DataFlow @Connection
Feature: Machine Connectivity Patterns
  As a system designer
  I want to verify that different component connectivity patterns work correctly
  So that I can build machines with complex topologies that reliably process data

  Background:
    Given a new environment is initialized
    And a machine named "pattern-tester" is created

  @Functional @Pipeline
  Scenario: Linear pipeline pattern should process data sequentially
    Given a pipeline with 5 processing components is created
    When 10 data elements are sent through the pipeline
    Then each component should process all 10 elements in sequence
    And the output should match the expected transformation chain

  @Functional @Router
  Scenario: Broadcast pattern should distribute data to multiple recipients
    Given a source component connected to 3 parallel processors
    When the source broadcasts 5 data elements
    Then all 3 processors should receive all 5 elements
    And each processor should apply its specific transformation
    And the machine should collect results from all processors

  @Functional @Aggregator 
  Scenario: Aggregator pattern should combine data from multiple sources
    Given 3 source components generating complementary data
    And an aggregator component that combines related elements
    When each source produces 4 data elements
    Then the aggregator should create 4 complete aggregated results
    And the aggregation timestamps should reflect processing order

  @Functional @Router
  Scenario: Content-based router pattern should direct data to appropriate processors
    Given a router component with 3 destination processors
    And routing rules based on data content
    When 15 mixed-type data elements are sent to the router
    Then each element should be routed to the correct processor
    And no elements should be lost or misrouted
    And the machine should track the routing decisions

  @Behavioral @Pipeline
  Scenario: Feedback loop pattern should reprocess data when needed
    Given a processor with a conditional feedback loop
    And a completion predicate requiring 3 passes for some elements
    When 10 data elements requiring various processing passes are submitted
    Then elements should be reprocessed until they meet completion criteria
    And the machine should track the number of processing passes
    And all elements should eventually complete processing

  @Functional @Splitter
  Scenario: Splitter pattern should decompose composite data elements
    Given a splitter component that breaks data into constituent parts
    When 5 composite data elements are sent to the splitter
    Then each composite element should be decomposed correctly
    And downstream processors should receive the individual parts
    And traceability should be maintained to the original composite elements

  @ErrorHandling @Resilience
  Scenario: Transaction boundaries should isolate failures within the pattern
    Given a multi-stage processing pipeline with transaction boundaries
    When a failure is injected in one processing stage
    Then the transaction should be rolled back for that stage
    And prior successful stages should remain committed
    And the failure should not affect parallel processing paths
    And the error should be properly reported in the machine status

  @Integration @Connection
  Scenario: Complex mixed pattern with multiple topologies should process correctly
    Given a machine with mixed connectivity patterns:
      | Pattern    | Components |
      | Pipeline   | 3          |
      | Broadcast  | 1 to 3     |
      | Aggregator | 2 to 1     |
      | Router     | 1 to 2     |
    When a batch of 20 data elements is processed through the machine
    Then data should flow correctly through all pattern segments
    And the machine should produce the expected output
    And the machine should report correct statistics for each pattern segment