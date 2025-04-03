# Filename: TBD-CompositeTubeInteractionTest-Example.feature
# Purpose: Example feature file demonstrating TBD Composite Tube Interaction Testing (CTIT) approach
# Goals:
#   - Show proper TBD tag usage for composite testing
#   - Demonstrate interaction testing between tubes
#   - Provide a template for future CTIT tests

@L1_Bundle @CTIT
Feature: TBD Composite Tube Interaction Testing Example
  # This feature demonstrates the Composite Tube Interaction Testing (CTIT) approach
  # to verifying how tubes interact when they come together in a bundle or composite.
  # CTIT focuses on the connections and interactions between tubes, ensuring data flows
  # correctly and state changes propagate appropriately.

  @L1_Bundle @CTIT @Flow @Runtime
  Scenario: Composite tube interaction - Data flow between tubes
    # Purpose: Validates that data flows correctly between connected tubes
    # This is an "Attle" (Above The Line) test that must pass for composites to work
    Given a validator tube is connected to a transformer tube
    When data "raw_input" is sent to the validator tube
    Then the validator tube should validate the data
    And the validated data should flow to the transformer tube
    And the transformer tube should output "TRANSFORMED_RAW_INPUT"
    And the entire flow should be logged with proper tube IDs

  @L1_Bundle @CTIT @State @Runtime
  Scenario: Composite tube interaction - State propagation
    # Purpose: Validates that state changes propagate between connected tubes
    Given a composite of 3 connected tubes is created
    When the first tube changes state to "ERROR"
    Then the state change should propagate to connected tubes
    And the composite should enter "DEGRADED" state
    And all state changes should be properly logged

  @BTL @L1_Bundle @CTIT @Resilience @CircuitBreaker
  Scenario: Composite tube interaction - Error isolation
    # Purpose: Validates that errors in one tube don't crash the entire composite
    # This is a "Bottle" (Below The Line) test for additional resilience
    Given a composite with circuit breaker protection is set up
    When the middle tube encounters a critical error
    Then the circuit breaker should isolate the failing tube
    And the composite should continue operation in degraded mode
    And the error should be contained within the failing tube's boundary
    And the system should attempt recovery after a cool-down period

  @L1_Bundle @CTIT @Awareness @Runtime
  Scenario: Composite tube interaction - Resource awareness
    # Purpose: Validates that tubes in a composite share resource awareness
    Given a composite of tubes sharing the same environment
    When available memory drops below 30%
    Then all tubes should receive resource constraint notifications
    And each tube should adjust its resource consumption
    And the composite should coordinate the resource distribution
    And the system should continue operating with reduced resource usage