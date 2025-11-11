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

  @Error
  Scenario: Handle task execution failure
    When I execute a task that throws an exception
    Then the task should fail with appropriate error message
    And the task status should be "FAILED"

  @Error
  Scenario: Task execution timeout
    When I execute a task with a timeout of 50 milliseconds that takes 200 milliseconds to complete
    Then the task should be canceled due to timeout
    And the task status should be "TIMEOUT"

  @Error
  Scenario: Recover from service interruption
    Given a task execution service with simulated failures
    When I execute a task that requires recovery
    Then the task should be retried successfully
    And recovery metrics should be recorded

  @Performance
  Scenario: Execute multiple tasks concurrently
    When I execute 10 concurrent tasks
    Then all tasks should complete successfully
    And the execution time should be less than executing them sequentially

  @Priority
  Scenario: Execute tasks with different priorities
    When I submit the following tasks with priorities:
      | taskId | priority | value |
      | task1  | HIGH     | 1     |
      | task2  | LOW      | 2     |
      | task3  | HIGH     | 3     |
      | task4  | MEDIUM   | 4     |
    Then the tasks should be executed in priority order
    And all tasks should complete successfully

  @Chaining
  Scenario: Chain dependent tasks
    When I create a task chain "A → B → C"
    Then all tasks should execute in the correct sequence
    And task "B" should only start after task "A" completes
    And task "C" should only start after task "B" completes
    And the final result should combine results from all tasks