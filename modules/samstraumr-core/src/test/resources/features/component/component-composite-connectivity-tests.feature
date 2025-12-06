# Filename: composite-connectivity-tests.feature
# Purpose: Validates the connectivity and data flow aspects of Composites
# Goals:
# - Verify component connection within composites
# - Test data processing through connected components
# - Validate transformation and validation patterns
# Dependencies:
# - BDD step definitions in org.s8r.component.test.steps package
# - Composite and Component implementations in org.s8r.component
#


@L1_Component
Feature: Composite Component Connectivity
  Composites should properly connect components and manage data flow between them
  following structured patterns such as transformers, validators, and observers.

@Functional @L1_Composite @Connectivity @Connection
  Scenario: Basic component connection in composite
    Given a composite with ID "basic-connection-test"
    When components are added to the composite:
      | Name     | Reason                   |
      | source   | Source Component         |
      | processor| Processing Component     |
      | sink     | Sink Component           |
    And components are connected in the composite:
      | Source   | Target    |
      | source   | processor |
      | processor| sink      |
    Then the composite should have the expected components
    And the composite should have the expected connections
    And the connection graph should be properly formed

@Functional @L1_Composite @Connectivity @Transformer
  Scenario: Data transformation in composite
    Given a composite with ID "transformer-test"
    When a transformer composite is created with components:
      | Name        | Reason                   |
      | source      | Source Component         |
      | transformer | Transformer Component    |
      | sink        | Sink Component           |
    And a transformer is added to "transformer" that converts text to uppercase
    When data "test input" is processed through the composite starting at "source"
    Then the output from the composite should be "TEST INPUT"
    And the transformer should log its operation

@Functional @L1_Composite @Connectivity @Validator
  Scenario: Data validation in composite
    Given a composite with ID "validator-test"
    When a validator composite is created with components:
      | Name      | Reason                   |
      | source    | Source Component         |
      | validator | Validator Component      |
      | sink      | Sink Component           |
    And a validator is added to "validator" that checks if data contains "valid"
    When data "this is valid input" is processed through the composite starting at "source"
    Then the composite should return a result
    When data "this is invalid input" is processed through the composite starting at "source"
    Then the composite should not return a result
    And the validator should log validation status

@Functional @L1_Composite @Connectivity @CircuitBreaker
  Scenario: Circuit breaker in composite
    Given a composite with ID "circuit-breaker-test"
    When a processing composite is created with circuit breaker enabled on "processor"
    And a transformer is added to "processor" that throws exceptions
    When data is processed through the composite multiple times
    Then the circuit breaker should open after failure threshold is reached
    And subsequent processing attempts should be skipped
    And after the timeout period the circuit breaker should reset to half-open

@Functional @L1_Composite @Connectivity @Observer
  Scenario: Observer pattern in composite
    Given a composite with ID "observer-test"
    When an observer composite is created
    And a transformer is added to "source" that modifies data
    And data "original data" is processed through the composite starting at "source"
    Then the observer should log the data processing
    And the original data transformation should be preserved