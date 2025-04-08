# Copyright (c) 2025
# All rights reserved.

@integration @L1 @ports
Feature: Validation and Persistence Integration
  As an application developer
  I want to ensure the Validation and Persistence ports work together correctly
  So that data is validated before being stored

  Background:
    Given a validation service is initialized
    And a persistence service is initialized

  @smoke
  Scenario: Valid data is persisted successfully
    Given I have a valid data object
    When I validate and persist the object
    Then the validation should pass
    And the object should be persisted successfully
    And I should be able to retrieve the object

  Scenario: Invalid data is not persisted
    Given I have an invalid data object
    When I validate and persist the object
    Then the validation should fail
    And the object should not be persisted
    And I should receive validation errors

  Scenario: Validation rules are applied consistently
    Given I have the following data objects:
      | id | name   | email               | age | valid |
      | 1  | John   | john@example.com    | 30  | true  |
      | 2  | Jane   | jane@example.com    | 25  | true  |
      | 3  | Invalid| not-an-email        | 17  | false |
      | 4  | Empty  |                     | 40  | false |
      | 5  | Young  | young@example.com   | 15  | false |
    When I validate and persist each object
    Then only valid objects should be persisted
    And I should receive validation errors for invalid objects