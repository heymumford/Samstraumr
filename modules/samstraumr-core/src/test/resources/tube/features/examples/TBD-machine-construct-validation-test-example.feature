
# Filename: TBD-MachineConstructValidationTest-Example.feature
# Purpose: Example feature file demonstrating TBD Machine Construct Validation Testing (MCVT) approach
# Goals:
  # - Show proper TBD tag usage for machine-level testing
  # - Demonstrate end-to-end testing of machine constructs
  # - Provide a template for future MCVT tests


@ATL @L2_Machine @MCVT
Feature: TBD Machine Construct Validation Testing Example
  # This feature demonstrates the Machine Construct Validation Testing (MCVT) approach
  # to verifying how machines composed of multiple composites and tubes function as a whole.
  # MCVT focuses on end-to-end functionality, ensuring that machines interact properly
  # with external systems and fulfill their business requirements.

  @ATL @L2_Machine @MCVT @Flow @Runtime
  Scenario: Machine validation - End-to-end data processing
    # Purpose: Validates that a machine processes data end-to-end correctly
    # This is an "Attle" (Above The Line) test that must pass for machines to work
    Given a data processing machine with input, validation, transformation and output stages
    When raw customer data "name:John Doe,email:john@example.com,order:123" is submitted
    Then the input stage should receive and parse the data
    And the validation stage should verify the data format
    And the transformation stage should process the data into a standardized format
    And the output stage should deliver the result to the external system
    And each stage should log its operations with correlation IDs
    And the machine should report successful completion

  @ATL @L2_Machine @MCVT @State @Runtime
  Scenario: Machine validation - Coordinated state management
    # Purpose: Validates that a machine coordinates state across its components
    Given a machine with multiple interconnected composites
    When an external system triggers a state change event
    Then the state change should be propagated through all components
    And each composite should adjust its behavior accordingly
    And the machine should maintain a consistent overall state
    And state transitions should be coordinated in the correct order
    And the machine should report its new state to external systems

  @BTL @L2_Machine @MCVT @Resilience @CircuitBreaker
  Scenario: Machine validation - System-wide fault tolerance
    # Purpose: Validates that a machine handles failures gracefully
    # This is a "Bottle" (Below The Line) test for machine resilience
    Given a machine with critical and non-critical components
    When a non-critical component experiences a catastrophic failure
    Then the circuit breaker should isolate the failing component
    And the machine should continue operating in degraded mode
    And critical functionalities should remain available
    And the machine should attempt to restart the failed component
    And alert systems should be notified of the degraded state
    And performance metrics should reflect the degraded capacity

  @ATL @L3_System @MCVT @Acceptance
  Scenario: Machine validation - Business requirement fulfillment
    # Purpose: Validates that a machine meets user/business requirements
    Given a complete business process implemented as a machine
    When a user initiates the standard business workflow
    Then all business rules should be correctly applied
    And the workflow should complete within expected timeframes
    And all required output artifacts should be generated
    And the system should maintain a complete audit trail
    And the business outcome should match the expected result
    And performance should meet defined service level agreements