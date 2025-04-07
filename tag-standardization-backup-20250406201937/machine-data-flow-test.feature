@L2_Integration @Functional @DataFlow
Feature: Machine Data Flow
  As a system integrator
  I want to verify data flows correctly through connected bundles forming a machine
  So that I can ensure end-to-end data processing works correctly

  Background:
    Given a new environment is initialized
    And a machine named "data-processor" is created
    And a source bundle "input-reader" is added to the machine
    And a transformation bundle "processor" is added to the machine
    And a sink bundle "output-writer" is added to the machine

@Identity @Functional
  Scenario: Machine components should initialize with valid identifiers
    When I query the machine structure
    Then all machine components should have valid identifiers
    And the machine should contain exactly 3 bundles
    And the bundles should be properly connected in sequence

@DataFlow @Transformer
  Scenario: Data should flow through the machine without loss
    Given the source bundle generates 10 data elements
    And the transformation bundle converts all text to uppercase
    When I execute the machine data flow
    Then the sink bundle should receive exactly 10 data elements
    And all output data should be uppercase

@State @Resilience @ErrorHandling
  Scenario: Machine should handle bundle failures gracefully
    Given the source bundle generates 5 data elements
    And the transformation bundle is configured to fail on the 3rd element
    And circuit breakers are enabled for all bundles
    When I execute the machine data flow
    Then the machine execution report should indicate a partial success
    And exactly 2 data elements should be successfully processed
    And the failure should be properly isolated to the transformation bundle
    And the circuit breaker should be in open state for the transformation bundle

  @DataFlow @Performance
  Scenario Outline: Machine should process data within acceptable time limits
    Given the source bundle generates <count> data elements
    And the transformation bundle converts all text to uppercase
    When I execute the machine data flow with timing
    Then all data should be processed successfully
    And the total processing time should be less than <max_time> milliseconds

    Examples:
      | count | max_time |
      | 10    | 500      |
      | 100   | 1000     |
      | 1000  | 5000     |

@State @Monitoring
  Scenario: Machine should track processing state and metadata
    Given the source bundle generates 5 data elements
    And each data element has unique metadata attached
    When I execute the machine data flow
    Then the sink bundle should receive all 5 data elements with metadata
    And the machine execution report should include full data lineage
    And the machine state log should show all processing steps