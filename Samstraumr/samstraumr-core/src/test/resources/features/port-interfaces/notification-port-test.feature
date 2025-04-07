@L2_Integration @Functional @PortInterface @Notification
Feature: Notification Port Interface
  As a system developer
  I want to use the NotificationPort interface for sending notifications
  So that I can keep my application core independent of notification delivery mechanisms

  Background:
    Given a clean system environment
    And the NotificationPort interface is properly initialized
    And notification recipients are registered in the system

  Scenario: Sending a system notification
    When I send a system notification with subject "System Alert" and content "System maintenance required"
    Then the notification should be successfully delivered
    And the notification status should be "DELIVERED"
    And the system logs should contain the notification details

  Scenario: Sending a user notification
    When I send a notification to recipient "test-user" with subject "User Alert" and content "Your attention is required"
    Then the notification should be successfully delivered to the recipient
    And the notification status should be "DELIVERED"
    And the recipient should be able to view the notification details

  Scenario: Notification with metadata
    When I send a notification with metadata:
      | key       | value        |
      | category  | maintenance  |
      | priority  | high         |
      | actionUrl | /maintenance |
    Then the notification should include the metadata
    And the metadata should be accessible in the notification result

  Scenario: Notification delivery failure
    When I send a notification to an unregistered recipient "unknown-user"
    Then the notification delivery should fail
    And the notification status should be "FAILED"
    And an appropriate error message should be provided

  Scenario: Push notification delivery
    When I send a push notification to recipient "device-user" with title "Push Alert"
    Then the push notification should be delivered to the user's device
    And the notification should include the appropriate push payload
    And the notification status should be "DELIVERED"

  Scenario: Asynchronous notification delivery
    When I send an asynchronous notification with subject "Async Alert"
    Then the notification request should be accepted
    And the notification should be delivered asynchronously
    And I should be able to check the notification status later

  Scenario: Batch notification delivery
    When I send a batch notification to multiple recipients:
      | user1 |
      | user2 |
      | user3 |
    Then all valid notifications should be delivered
    And I should receive delivery status for each recipient
    And failed deliveries should be properly reported

  Scenario: Notification recipient management
    When I register a new recipient "new-user" with contact information:
      | type    | email                |
      | address | new-user@example.com |
    Then the recipient should be successfully registered
    And I should be able to send notifications to this recipient
    And I should be able to unregister the recipient later

  Scenario Outline: Notification deliveries with different severities
    When I send a notification with severity "<severity>"
    Then the notification should be delivered with the appropriate styling
    And the notification should be handled according to its severity level

    Examples:
      | severity |
      | INFO     |
      | WARNING  |
      | ERROR    |
      | CRITICAL |

  Scenario: Notification delivery tracking
    When I send a notification and record its ID
    Then I should be able to query the notification status using the ID
    And I should be able to retrieve the notification metadata using the ID