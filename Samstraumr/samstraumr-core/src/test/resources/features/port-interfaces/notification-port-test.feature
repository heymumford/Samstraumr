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
    
  Scenario: SMS notification delivery
    When I send an SMS notification to phone number "555-123-4567"
    Then the SMS notification should be successfully delivered
    And the notification status should be "DELIVERED"
    And the notification channel should be "SMS"
    
  Scenario Outline: SMS notifications with different types
    When I send an SMS notification with type "<smsType>" to phone number "555-987-6543"
    Then the SMS should be handled according to its type
    And the notification channel should be "SMS"
    
    Examples:
      | smsType  |
      | STANDARD |
      | EXTENDED |
      | BINARY   |
      | FLASH    |
      
  Scenario: SMS batch notification delivery
    When I send a batch SMS notification to multiple phone numbers:
      | 555-111-2222 |
      | 555-333-4444 |
      | 555-555-6666 |
    Then all valid SMS notifications should be delivered
    And I should receive delivery status for each phone number
    
  Scenario: Scheduling a notification for future delivery
    When I schedule a notification for delivery at "2025-05-01T09:00:00Z"
    Then the notification should be marked as "SCHEDULED"
    And the scheduled time should be stored in the notification metadata
    
  Scenario: Sending a notification via a specific channel
    When I send a notification via channel "WEBHOOK" to recipient "webhook-user"
    Then the notification should be delivered via the specified channel
    And the notification channel in the result should be "WEBHOOK"
    
  Scenario Outline: Sending notifications to messaging platforms
    When I send a "<platform>" notification to channel "<channel>"
    Then the notification should be delivered via the "<platform>" channel
    And the notification content should be properly formatted for the platform
    
    Examples:
      | platform | channel    |
      | SLACK    | #general   |
      | TEAMS    | General    |
      | DISCORD  | #announcements |
      
  Scenario Outline: Sending formatted notifications with different content types
    When I send a notification with content format "<format>"
    Then the notification should be properly rendered in the "<format>" format
    And the notification should maintain proper formatting
    
    Examples:
      | format      |
      | PLAIN_TEXT  |
      | HTML        |
      | MARKDOWN    |
      | RICH_TEXT   |
      | JSON        |
      
  Scenario: Sending templated notifications
    When I send a templated notification with the following variables:
      | name         | John Doe        |
      | action       | password reset  |
      | serviceUrl   | /account/reset  |
    Then the notification should have the variables substituted in the template
    And the notification should be properly formatted according to the template
    
  Scenario Outline: Registering recipients for different channels
    When I register a recipient for "<channel>" notifications with proper credentials
    Then the recipient should be registered successfully for "<channel>" channel
    And I should be able to send "<channel>" notifications to this recipient
    
    Examples:
      | channel  |
      | SLACK    |
      | TEAMS    |
      | DISCORD  |
      | EMAIL    |
      | SMS      |
      | PUSH     |
      | WEBHOOK  |