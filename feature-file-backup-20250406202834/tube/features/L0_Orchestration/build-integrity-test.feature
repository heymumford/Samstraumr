@L0_Orchestration @ATL @Orchestration
Feature: Core Build Integrity
  As a developer
  I want to verify that the basic Samstraumr build is intact
  So that I can trust that core functionality is available

  @Identity
  Scenario: Environment initialization
    Given a new Environment is created
    Then the environment should have valid parameters
    And the environment should contain runtime information

  @Flow
  Scenario: Basic tube creation
    Given a new Environment is created
    When a new Tube is created with reason "Orchestration test tube"
    Then the tube should have a valid unique ID
    And the tube should log its creation in Mimir

  @L1_Composite @Core
  Scenario: Composite tube creation
    Given a new Environment is created
    When a new Composite is created
    Then the composite should be active
    And the composite should have no tubes initially

  @L1_Composite @Flow
  Scenario: Composite wiring validation
    Given a new Environment is created
    And a new Composite is created
    When a source tube is added to the composite
    And a target tube is added to the composite
    And the source tube is connected to the target tube
    Then the composite should have 2 tubes
    And the source tube should be connected to the target tube