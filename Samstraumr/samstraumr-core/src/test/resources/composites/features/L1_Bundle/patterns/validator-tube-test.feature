# ---------------------------------------------------------------------------------------
# validator-tube-test.feature - Tests for Validator Tube Pattern
#
# This feature file contains tests for tubes that implement the Validator pattern,
# which validates input data according to predefined rules.
# ---------------------------------------------------------------------------------------

@ATL @L1_Bundle @Flow @Validator
Feature: Validator Tube - Input Validation and Routing
  # This feature verifies that Validator tubes correctly validate input data against rules

  @ATL @L1_Bundle @Init @Flow @Validator
  Scenario Outline: Validator tube validates inputs and routes them based on predefined rules
    # Purpose: Verify that validator tubes correctly validate data against predefined rules
    Given a connector tube is initialized and raw input data <inputData> is provided to the system
    And predefined validation rule <validationRule> is configured
    And the connector tube is fully functional and ready for validation
    When the connector tube processes the input data
    Then the input data should be validated according to the predefined validation rule
    And valid data should be routed to the appropriate processing tube if applicable
    And invalid data should be flagged with an appropriate error message if applicable

    Examples:
      | inputData     | validationRule | expectedOutcome     |
      | validData     | rule1          | routed correctly    |
      | invalidData   | rule2          | error message       |
      | partialData   | rule3          | routed with notice  |

  @BTL @L1_Bundle @Runtime @Flow @Validator @CircuitBreaker
  Scenario: Validator tube prevents invalid data propagation
    # Purpose: Ensure validators act as circuit breakers for invalid data
    Given a validator tube is configured with strict validation rules
    When critical data validation fails
    Then the validator should block the data from propagating further
    And a detailed validation error should be logged
    And upstream systems should be notified of the validation failure

  @BTL @L1_Bundle @Runtime @Flow @Validator @Resilience
  Scenario Outline: Validator tube applies different validation levels based on context
    # Purpose: Test that validators can adjust validation strictness based on context
    Given a validator tube is configured with multi-level validation
    And the current system context is <context>
    When data with quality level <qualityLevel> is processed
    Then the validator should apply <validationLevel> validation rules
    And the validation outcome should be <outcome>

    Examples:
      | context      | qualityLevel | validationLevel | outcome        |
      | critical     | high         | strict          | pass           |
      | critical     | medium       | strict          | warn           |
      | normal       | medium       | standard        | pass           |
      | degraded     | low          | relaxed         | pass with flag |