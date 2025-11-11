@L2_Integration @Functional @TaskExecution @Notification @integration
Feature: Task Execution and Notification Port Integration
  As a system developer
  I want to integrate TaskExecutionPort with NotificationPort
  So that I can schedule notifications for delivery and manage notification retries efficiently

  Background:
    Given a clean system environment
    And the TaskExecutionPort interface is properly initialized
    And the NotificationPort interface is properly initialized
    And the following notification templates are registered:
      | name            | template                                          |
      | scheduled       | Scheduled notification: {message}                 |
      | reminder        | Reminder: {event} is scheduled for {time}         |
      | status_update   | Status update: {component} is now {status}        |
      | batch_summary   | Daily summary: {count} events occurred            |

  @Smoke
  Scenario: Schedule a notification for future delivery
    When I schedule a notification with the following details for delivery in 100 milliseconds:
      | recipient      | user@example.com      |
      | subject        | Scheduled Notification |
      | message        | Test scheduled message |
      | channel        | EMAIL                 |
    Then the notification task should be scheduled successfully
    And the scheduled task should have status "SCHEDULED"
    When I wait for 200 milliseconds
    Then the notification should be delivered to "user@example.com"
    And the task status should be "COMPLETED"

  @Smoke
  Scenario: Schedule multiple notifications for batch processing
    When I schedule 5 notifications for delivery in 100 milliseconds
    Then all notification tasks should be scheduled successfully
    When I wait for 200 milliseconds
    Then all 5 notifications should be delivered
    And all tasks should have status "COMPLETED"

  Scenario: Retry failed notification delivery with exponential backoff
    Given a notification channel with temporary failures
    When I send a notification that will initially fail
    Then the notification delivery should be retried using the task scheduler
    And the retry intervals should follow exponential backoff pattern
    And the notification should eventually be delivered successfully
    And the task status should be "COMPLETED"

  Scenario: Cancel a scheduled notification before delivery
    When I schedule a notification for delivery in 500 milliseconds
    Then the notification task should be scheduled successfully
    When I cancel the scheduled notification task
    Then the task should be canceled successfully
    And I wait for 600 milliseconds
    Then the notification should not be delivered
    And the task status should be "CANCELED"

  Scenario: Schedule a recurring notification
    When I schedule a recurring notification with interval 100 milliseconds
    Then the recurring notification task should be scheduled successfully
    When I wait for 250 milliseconds
    Then the notification should be delivered at least 2 times
    When I cancel the recurring notification task
    Then the recurring task should be canceled successfully
    And no further notifications should be delivered

  Scenario: Schedule notifications with different priorities
    When I schedule the following notifications with priorities:
      | recipient      | priority | subject            | delay_ms |
      | user1@example.com | HIGH     | High Priority      | 300      |
      | user2@example.com | LOW      | Low Priority       | 300      |
      | user3@example.com | HIGH     | Another High       | 300      |
      | user4@example.com | MEDIUM   | Medium Priority    | 300      |
    And I wait for 400 milliseconds
    Then the notifications should be delivered according to priority
    And high priority notifications should be delivered before lower priority ones

  Scenario: Use task chaining for multi-step notification process
    When I create a notification chain with the following steps:
      | step        | action                              |
      | prepare     | Generate notification content       |
      | validate    | Validate notification recipients    |
      | format      | Format notification for channel     |
      | deliver     | Deliver the notification            |
      | log         | Log the notification delivery       |
    Then all steps in the notification chain should complete successfully
    And each step should start only after the previous step completes
    And the notification should be delivered correctly
    And delivery should be logged accurately

  Scenario: Schedule time-sensitive notifications with deadline
    When I schedule a time-sensitive notification with deadline 200 milliseconds from now
    And I artificially slow down the notification delivery process
    And I wait for 300 milliseconds
    Then the notification task should be marked as "TIMEOUT"
    And an error should be logged about the missed deadline
    And a system alert should be generated about the timeout

  Scenario: Integration with template-based notifications
    When I schedule a templated notification with the following parameters:
      | template      | reminder                |
      | event         | Team Meeting            |
      | time          | 2:00 PM                 |
      | recipient     | team@example.com        |
      | delay_ms      | 100                     |
    And I wait for 200 milliseconds
    Then the notification with template "reminder" should be delivered
    And the notification message should be "Reminder: Team Meeting is scheduled for 2:00 PM"

  Scenario: Graceful shutdown with pending notifications
    When I schedule 3 notifications for future delivery
    And I initiate a graceful shutdown of the task execution service
    Then all pending notification tasks should be completed before shutdown
    And the shutdown should be successful
    And all notifications should be delivered

  Scenario: Schedule batch notifications based on collected events
    When I register 10 system events for batch notification
    And I schedule a batch notification summary task to run after 100 milliseconds
    And I wait for 200 milliseconds
    Then a single batch notification should be delivered
    And the batch notification should include a summary of all 10 events
    And the task status should be "COMPLETED"