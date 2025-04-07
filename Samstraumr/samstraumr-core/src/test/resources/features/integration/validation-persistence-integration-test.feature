@L3_Integration @Functional @PortIntegration
Feature: Validation and Persistence Port Integration
  As a system developer
  I want to ensure that ValidationPort and PersistencePort work together correctly
  So that only valid data is persisted in the system

  Background:
    Given a clean system environment
    And both validation and persistence ports are properly initialized
    And standard entity validation rules are configured

  Scenario: Validating and storing a valid entity
    Given I have a valid "user" entity with the following data:
      | id        | user-123           |
      | firstName | John               |
      | lastName  | Doe                |
      | email     | john@example.com   |
      | age       | 30                 |
    When I validate and store the entity
    Then the validation should pass
    And the entity should be successfully saved
    And I should be able to retrieve the entity by its ID

  Scenario: Rejecting invalid entity during persistence
    Given I have an invalid "user" entity with the following data:
      | id        | user-456           |
      | firstName |                    |
      | lastName  | Smith              |
      | email     | invalid-email      |
      | age       | -5                 |
    When I validate and store the entity
    Then the validation should fail with appropriate error messages
    And the entity should not be saved

  Scenario: Updating an entity with valid data
    Given I have a stored "user" entity with the following data:
      | id        | user-789           |
      | firstName | Jane               |
      | lastName  | Johnson            |
      | email     | jane@example.com   |
      | age       | 25                 |
    When I update the entity with valid changes:
      | firstName | Janet              |
      | age       | 26                 |
    Then the validation should pass
    And the entity should be successfully updated
    And the retrieved entity should contain the updated values

  Scenario: Rejecting entity updates with invalid data
    Given I have a stored "user" entity with the following data:
      | id        | user-101           |
      | firstName | Bob                |
      | lastName  | Williams           |
      | email     | bob@example.com    |
      | age       | 40                 |
    When I update the entity with invalid changes:
      | firstName |                    |
      | email     | invalid-email      |
    Then the validation should fail with appropriate error messages
    And the entity should not be updated

  Scenario: Querying entities that match validation criteria
    Given the following "user" entities are stored:
      | id        | firstName | lastName  | email                | age | status   |
      | user-001  | Alice     | Adams     | alice@example.com    | 25  | active   |
      | user-002  | Bob       | Brown     | bob@example.com      | 30  | active   |
      | user-003  | Charlie   | Cooper    | charlie@example.com  | 35  | inactive |
      | user-004  | David     | Davis     | david@example.com    | 40  | active   |
      | user-005  | Eve       | Edwards   | eve@example.com      | 45  | inactive |
    When I search for entities with the following criteria:
      | status    | active             |
      | minAge    | 30                 |
    Then the search should return 2 entities
    And all returned entities should be valid according to validation rules

  Scenario: Batch validation and persistence
    Given I have the following entities to process:
      | id        | firstName | lastName  | email                | age | status   | valid |
      | user-201  | Frank     | Foster    | frank@example.com    | 25  | active   | true  |
      | user-202  | Grace     | Garcia    |                      | 30  | active   | false |
      | user-203  | Henry     | Harris    | henry@example.com    | -5  | inactive | false |
      | user-204  | Ivy       | Ingram    | ivy@example.com      | 40  | active   | true  |
    When I batch validate and persist the entities
    Then only valid entities should be persisted
    And the system should report 2 successful and 2 failed operations