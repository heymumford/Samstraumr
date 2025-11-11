# ---------------------------------------------------------------------------------------
# BundleConnectionTest.feature - Connection Tests for Tube Bundles
#
# This feature file contains tests for tube connections within bundles. These tests validate 
# that tubes can be properly connected together to form functional bundles that process data.
# ---------------------------------------------------------------------------------------

@L1_Component @DataFlow
Feature: Bundle Connection and Flow
  # This feature validates that tubes can be connected into bundles and data flows correctly through the connections

@Functional  @L1_Bundle @Init @DataFlow @Transformer
  Scenario: Basic tubes connect into a data transformation bundle
    # Purpose: Confirm that tubes can be connected to form a simple transformation pipeline
    Given tubes are instantiated for a simple transformation bundle
    When the tubes are connected in a linear sequence
    Then data should flow from the source tube through the transformer tube to the sink tube
    And the transformation should be applied correctly to the data

@Functional  @L1_Bundle @Runtime @DataFlow @Validator
  Scenario: Bundles validate data between tube connections
    # Purpose: Ensure that data validation occurs at connection boundaries
    Given a bundle is created with validator tubes between components
    When invalid data is sent through the bundle
    Then the validator tube should reject the invalid data
    And the rejection should be logged properly
    And valid data should continue through the bundle

@ErrorHandling  @L1_Bundle @Runtime @DataFlow @CircuitBreaker
  Scenario: Bundles handle interruptions in data flow gracefully
    # Purpose: Test that bundles can handle disruptions in data flow
    Given a multi-tube bundle is processing a continuous data stream
    When one tube in the bundle fails temporarily
    Then the circuit breaker should activate
    And the error should be contained within the failing tube
    And the bundle should recover when the failing tube is restored

  @BTL @L1_Bundle @Performance @Scale
  Scenario Outline: Bundles maintain performance under varying loads
    # Purpose: Ensure that bundles can handle increasing data volumes efficiently
    Given a standard processing bundle is created
    When <dataVolume> data items are processed through the bundle
    Then the bundle should complete processing within <timeLimit> milliseconds
    And resource usage should not exceed <resourceLimit> percent

    Examples:
      | dataVolume | timeLimit | resourceLimit |
      | 100        | 1000      | 10            |
      | 1000       | 5000      | 25            |
      | 10000      | 30000     | 50            |