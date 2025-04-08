@L2_Integration @Functional @PortInterface @Task
Feature: Task Execution Port Interface
  As a system developer
  I want to use the TaskExecutionPort interface for asynchronous task execution
  So that I can effectively manage background operations and scheduled jobs

  Background:
    Given a clean system environment
    And the TaskExecutionPort interface is properly initialized

  Scenario: Submitting a simple task for execution
    When I submit a simple task that increments a counter
    Then the task submission should succeed
    And the task ID should be returned
    And the task should complete within 5 seconds
    And the task status should be "COMPLETED"
    And the counter should be incremented

  Scenario: Submitting a task with options
    When I submit a task with options:
      | option      | value             |
      | name        | OptionTestTask    |
      | description | Task with options |
      | priority    | HIGH              |
    Then the task submission should succeed
    And the task should have the specified options
    And the task should complete within 5 seconds

  Scenario: Submitting a callable task with a result
    When I submit a callable task that returns "Operation successful"
    Then the task submission should succeed
    And the task should complete within 5 seconds
    And the task result should be "Operation successful"

  Scenario: Scheduling a task for future execution
    When I schedule a task for execution 3 seconds in the future
    Then the task scheduling should succeed
    And the task status should be "SCHEDULED"
    And after 5 seconds, the task status should be "COMPLETED"

  Scenario: Cancelling a task before execution
    When I schedule a task for execution 10 seconds in the future
    And I cancel the task before it executes
    Then the task cancellation should succeed
    And the task status should be "CANCELLED"

  Scenario: Cancelling a running task
    When I submit a long-running task that takes 10 seconds
    And I cancel the task while it is running with interrupt allowed
    Then the task cancellation should succeed
    And the task status should be "CANCELLED"

  Scenario: Task with timeout
    When I submit a task with timeout of 2 seconds:
      | option      | value           |
      | name        | TimeoutTestTask |
      | timeout     | PT2S            |
    And the task runs for longer than the timeout
    Then the task should timeout
    And the task status should be "TIMED_OUT"

  Scenario: Task with delayed execution
    When I submit a task with a delay of 3 seconds:
      | option      | value           |
      | name        | DelayedTestTask |
      | delay       | PT3S            |
    Then the task should not execute immediately
    And after 5 seconds, the task should be completed

  Scenario: Getting task by ID
    When I submit a task and get its ID
    And I retrieve the task by its ID
    Then the retrieved task should match the submitted task

  Scenario: Getting tasks by status
    Given I have submitted the following tasks:
      | name       | status    |
      | Task1      | COMPLETED |
      | Task2      | RUNNING   |
      | Task3      | SCHEDULED |
      | Task4      | FAILED    |
    When I get tasks with status "COMPLETED"
    Then the result should include "Task1"
    And the result should not include "Task2,Task3,Task4"

  Scenario: Getting all tasks
    Given I have submitted multiple tasks
    When I request all tasks
    Then all submitted tasks should be included in the result

  Scenario: Task with metadata
    When I submit a task with metadata:
      | key        | value     |
      | category   | test      |
      | owner      | user123   |
      | priority   | high      |
    Then the task submission should succeed
    And the task metadata should contain the specified values

  Scenario: Task completion listener
    When I submit a task and register a completion listener
    Then the task should complete successfully
    And the completion listener should be called
    And the completion listener should receive the completed task

  Scenario: Task that fails with an error
    When I submit a task that throws an exception
    Then the task submission should succeed
    And the task should change to status "FAILED"
    And the task error message should contain details about the exception

  Scenario: Getting task execution statistics
    Given I have executed several tasks
    When I request task execution statistics
    Then the statistics should include counts for all task statuses
    And the statistics should include timing information

  Scenario: Shutting down the task execution system
    When I shut down the task execution system
    Then the shutdown should complete successfully
    And new task submissions should be rejected

  Scenario: Shutdown with wait for task completion
    Given I have several running tasks
    When I shut down the task execution system with a wait timeout of 5 seconds
    Then the shutdown should wait for tasks to complete
    And the shutdown should complete successfully