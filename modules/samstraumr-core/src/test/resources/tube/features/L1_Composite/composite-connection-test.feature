# ---------------------------------------------------------------------------------------
# CompositeConnectionTest.feature - Connection Tests for Tube Composites
#
# This feature file contains tests for tube connections within composites. These tests validate 
# that tubes can be properly connected together to form functional composites that process data.
# ---------------------------------------------------------------------------------------

@L1_Component @DataFlow
Feature: Composite Connection and Flow
  # This feature validates that tubes can be connected into composites and data flows correctly through the connections

@Functional  @L1_Composite @Init @DataFlow @Transformer
  Scenario: Basic tubes connect into a data transformation composite
    # Purpose: Confirm that tubes can be connected to form a simple transformation pipeline
    Given tubes are instantiated for a simple transformation composite
    When the tubes are connected in a linear sequence
    Then data should flow from the source tube through the transformer tube to the sink tube
    And the transformation should be applied correctly to the data

@Functional  @L1_Composite @Runtime @DataFlow @Validator
  Scenario: Composites validate data between tube connections
    # Purpose: Ensure that data validation occurs at connection boundaries
    Given a composite is created with validator tubes between components
    When invalid data is sent through the composite
    Then the validator tube should reject the invalid data
    And the rejection should be logged properly
    And valid data should continue through the composite

@ErrorHandling  @L1_Composite @Runtime @DataFlow @CircuitBreaker
  Scenario: Composites handle interruptions in data flow gracefully
    # Purpose: Test that composites can handle disruptions in data flow
    Given a multi-tube composite is processing a continuous data stream
    When one tube in the composite fails temporarily
    Then the circuit breaker should activate
    And the error should be contained within the failing tube
    And the composite should recover when the failing tube is restored

  @BTL @L1_Composite @Performance @Scale
  Scenario Outline: Composites maintain performance under varying loads
    # Purpose: Ensure that composites can handle increasing data volumes efficiently
    Given a standard processing composite is created
    When <dataVolume> data items are processed through the composite
    Then the composite should complete processing within <timeLimit> milliseconds
    And resource usage should not exceed <resourceLimit> percent

    Examples:
      | dataVolume | timeLimit | resourceLimit |
      | 100        | 1000      | 10            |
      | 1000       | 5000      | 25            |
      | 10000      | 30000     | 50            |