@L2_Integration @Functional @PortInterface @Persistence
Feature: Persistence Port Interface
  As a system developer
  I want to use the PersistencePort interface for data storage and retrieval
  So that I can keep my application core independent of storage implementations

  Background:
    Given a clean system environment
    And the PersistencePort interface is properly initialized

  Scenario: Saving a new entity
    When I save a "user" entity with ID "user-123" and the following data:
      | firstName | John             |
      | lastName  | Doe              |
      | email     | john@example.com |
      | age       | 30               |
    Then the entity should be successfully saved
    And the entity should exist in the storage
    And I should be able to retrieve the entity by ID

  Scenario: Updating an existing entity
    Given a "user" entity with ID "user-123" exists with the following data:
      | firstName | John             |
      | lastName  | Doe              |
      | email     | john@example.com |
      | age       | 30               |
    When I update the entity with the following data:
      | firstName | John             |
      | lastName  | Doe              |
      | email     | john@example.com |
      | age       | 31               |
    Then the entity should be successfully updated
    And the entity should have the updated data

  Scenario: Deleting an entity
    Given a "user" entity with ID "user-123" exists with the following data:
      | firstName | John             |
      | lastName  | Doe              |
      | email     | john@example.com |
      | age       | 30               |
    When I delete the entity
    Then the entity should be successfully deleted
    And the entity should no longer exist in the storage

  Scenario: Retrieving entities by type
    Given the following "user" entities exist:
      | id       | firstName | lastName | email             | age |
      | user-123 | John      | Doe      | john@example.com  | 30  |
      | user-456 | Jane      | Smith    | jane@example.com  | 25  |
      | user-789 | Bob       | Johnson  | bob@example.com   | 40  |
    When I retrieve all entities of type "user"
    Then I should get 3 entities
    And the entities should match the stored data

  Scenario: Finding entities by criteria
    Given the following "user" entities exist:
      | id       | firstName | lastName | age | status   |
      | user-123 | John      | Doe      | 30  | active   |
      | user-456 | Jane      | Smith    | 25  | active   |
      | user-789 | Bob       | Johnson  | 40  | inactive |
    When I search for entities with criteria:
      | status | active |
    Then I should get 2 entities
    And the entities should match the criteria

  Scenario: Counting entities by type
    Given the following "user" entities exist:
      | id       | firstName | lastName |
      | user-123 | John      | Doe      |
      | user-456 | Jane      | Smith    |
      | user-789 | Bob       | Johnson  |
    And the following "product" entities exist:
      | id         | name        |
      | product-1  | Product 1   |
      | product-2  | Product 2   |
    When I count entities of type "user"
    Then the count should be 3
    When I count entities of type "product"
    Then the count should be 2

  Scenario: Clearing all entities of a type
    Given the following "user" entities exist:
      | id       | firstName | lastName |
      | user-123 | John      | Doe      |
      | user-456 | Jane      | Smith    |
      | user-789 | Bob       | Johnson  |
    When I clear all entities of type "user"
    Then all entities of type "user" should be removed
    And the count of "user" entities should be 0

  Scenario: Handling non-existent entities
    When I try to find an entity with type "user" and ID "non-existent"
    Then the result should be empty
    When I try to update an entity with type "user" and ID "non-existent"
    Then the update operation should fail
    When I try to delete an entity with type "user" and ID "non-existent"
    Then the delete operation should fail

  Scenario: Storage type identification
    When I check the storage type
    Then the storage type should be "MEMORY"