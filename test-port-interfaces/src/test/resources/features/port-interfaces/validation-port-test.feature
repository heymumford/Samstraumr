@L2_Integration @Functional @PortInterface @Validation
Feature: Validation Port Interface
  As a system developer
  I want to use the ValidationPort interface for data validation
  So that I can keep my application core independent of validation implementations

  Background:
    Given a clean system environment
    And the ValidationPort interface is properly initialized

  Scenario: Validating strings against predefined patterns
    When I validate the string "test@example.com" against the "email" rule
    Then the validation should succeed
    When I validate the string "invalid-email" against the "email" rule
    Then the validation should fail with an appropriate error message

  Scenario: Validating numbers against numeric rules
    When I validate the number 10 against the "positive" rule
    Then the validation should succeed
    When I validate the number -5 against the "positive" rule
    Then the validation should fail with an appropriate error message

  Scenario: Validating required fields
    When I validate a field "username" with value "john" as required
    Then the validation should succeed
    When I validate a field "username" with value "" as required
    Then the validation should fail with an appropriate error message
    When I validate a field "username" with null value as required
    Then the validation should fail with an appropriate error message

  Scenario: Validating string length
    When I validate a field "username" with value "john" for length between 3 and 10
    Then the validation should succeed
    When I validate a field "username" with value "jo" for length between 3 and 10
    Then the validation should fail with an appropriate error message
    When I validate a field "username" with value "johnjohnjohn" for length between 3 and 10
    Then the validation should fail with an appropriate error message

  Scenario: Validating numeric ranges
    When I validate a field "age" with value 25 for range between 18 and 65
    Then the validation should succeed
    When I validate a field "age" with value 15 for range between 18 and 65
    Then the validation should fail with an appropriate error message
    When I validate a field "age" with value 70 for range between 18 and 65
    Then the validation should fail with an appropriate error message

  Scenario: Validating against custom regular expressions
    When I validate a field "zipcode" with value "12345" against pattern "^\\d{5}$"
    Then the validation should succeed
    When I validate a field "zipcode" with value "1234" against pattern "^\\d{5}$"
    Then the validation should fail with an appropriate error message
    When I validate a field "zipcode" with value "A1234" against pattern "^\\d{5}$"
    Then the validation should fail with an appropriate error message

  Scenario: Validating against allowed values
    When I validate a field "status" with value "active" against allowed values:
      | active   |
      | inactive |
      | pending  |
    Then the validation should succeed
    When I validate a field "status" with value "deleted" against allowed values:
      | active   |
      | inactive |
      | pending  |
    Then the validation should fail with an appropriate error message

  Scenario: Validating maps against rule sets
    Given I have registered a validation rule set "user" with rules:
      | fieldName | ruleName     |
      | username  | alphanumeric |
      | email     | email        |
      | age       | positive     |
    When I validate a map with the following values against the "user" rule set:
      | username | john123           |
      | email    | john@example.com  |
      | age      | 30                |
    Then the validation should succeed
    When I validate a map with the following values against the "user" rule set:
      | username | john@123          |
      | email    | not-an-email      |
      | age      | -5                |
    Then the validation should fail with appropriate error messages
    And the error message should contain validation failure for field "username"
    And the error message should contain validation failure for field "email"
    And the error message should contain validation failure for field "age"

  Scenario: Validating entity data
    When I validate the following component entity data:
      | id    | comp-123        |
      | name  | Test Component  |
      | state | ACTIVE          |
    Then the validation should succeed
    When I validate the following component entity data:
      | id    | c               |
      | name  |                 |
      | state | ACTIVE          |
    Then the validation should fail with appropriate error messages
    And the error message should contain validation failure for field "id"
    And the error message should contain validation failure for field "name"

  Scenario: Registering and using custom validation rules
    When I register a custom validation rule "uppercase" with pattern "^[A-Z]+$"
    And I validate the string "ABC" against the "uppercase" rule
    Then the validation should succeed
    When I validate the string "abc" against the "uppercase" rule
    Then the validation should fail with an appropriate error message