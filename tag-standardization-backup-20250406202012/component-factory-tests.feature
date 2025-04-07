Feature: Component Factory Tests
  As a developer
  I want to use a factory to create components
  So that I can easily instantiate components with consistent configuration

  Background:
    Given a clean component factory

  Scenario: Create a basic component using the factory
    When I create a component using the factory with name "BasicComponent"
    Then the component should be successfully created
    And the component should have the name "BasicComponent"

  Scenario: Create a component with custom properties
    When I create a component using the factory with properties:
      | name  | TestComponent |
      | type  | PROCESSOR     |
    Then the component should be successfully created
    And the component should have the property "type" with value "PROCESSOR"

  Scenario: Create multiple components from the same factory
    When I create 5 components using the factory
    Then all 5 components should be successfully created
    And each component should have a unique identifier

  Scenario: Create a component with initial state specified
    When I create a component using the factory with initial state "INITIALIZED"
    Then the component should be successfully created
    And the component should be in "INITIALIZED" state

  Scenario: Create a component with specified environment parameters
    When I create a component using the factory with environment parameters:
      | timeout     | 5000    |
      | maxRetries  | 3       |
      | logLevel    | INFO    |
    Then the component should be successfully created
    And the component should have the environment parameter "timeout" set to "5000"
    And the component should have the environment parameter "maxRetries" set to "3"
    And the component should have the environment parameter "logLevel" set to "INFO"