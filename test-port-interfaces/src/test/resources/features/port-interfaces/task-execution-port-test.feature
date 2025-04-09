@TaskExecution @PortTest @port
Feature: Task Execution Port Interface
  As a developer
  I want to have a port interface for task execution
  So that I can execute tasks asynchronously and schedule tasks for future execution

  Background:
    Given a task execution service

  @Smoke
  Scenario: Execute a task that returns a value
    When I execute a task that returns 42
    Then the task result should be 42

  @Smoke
  Scenario: Execute a task that modifies state
    When I execute a task that increments a counter
    Then the counter should be incremented

  @Smoke
  Scenario: Schedule a task for future execution
    When I schedule a task that returns "success" to run after 50 milliseconds
    Then the scheduled task should have status "SCHEDULED"
    And I wait for 100 milliseconds
    And I get the status of the scheduled task
    Then the task status should be "COMPLETED"

  @Smoke
  Scenario: Schedule a task that modifies state
    When I schedule a task that increments a counter to run after 50 milliseconds
    Then the scheduled task should have status "SCHEDULED"
    And I wait for 100 milliseconds
    Then the counter should be incremented after waiting

  @Smoke
  Scenario: Cancel a scheduled task
    When I schedule a task that increments a counter to run after 200 milliseconds
    Then the scheduled task should have status "SCHEDULED"
    When I cancel the scheduled task
    Then the task cancellation should be successful
    And I wait for 250 milliseconds
    Then the counter should not be incremented after waiting
    And I get the status of the scheduled task
    Then the task status should be "CANCELED"

  @Smoke
  Scenario: Shut down the task execution service gracefully
    When I execute a task that returns 42
    And I shut down the task execution service gracefully
    Then the shutdown should be successful

  @Smoke
  Scenario: Shut down the task execution service immediately
    When I execute a task that returns 42
    And I shut down the task execution service immediately
    Then the shutdown should be successful