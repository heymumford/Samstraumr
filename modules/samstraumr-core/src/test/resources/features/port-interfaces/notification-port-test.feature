@L0_Unit # Copyright (c) 2025
# All rights reserved.

@port @L1
Feature: Notification Port Interface
  As an application developer
  I want to use a standardized Notification Port interface
  So that I can send notifications to users

  Background:
    Given a notification port implementation is available

  @smoke
  Scenario: Send a basic notification
    When I send a notification to "recipient@example.com" with subject "Test Notification" and message "This is a test"
    Then the notification should be sent successfully
    And the recipient "recipient@example.com" should receive the notification
    And the notification should have subject "Test Notification"
    And the notification should have message "This is a test"

  Scenario: Send a notification with additional properties
    When I send a notification with additional properties:
      | recipient | recipient@example.com     |
      | subject   | Test With Properties      |
      | message   | This is a test with props |
      | urgency   | high                      |
      | category  | system                    |
    Then the notification should be sent successfully
    And the notification should include the property "urgency" with value "high"
    And the notification should include the property "category" with value "system"

  Scenario: Register a notification listener
    Given I have a notification listener
    When I register the listener for topic "user-events"
    And I send a notification with subject "user-events" and message "User event occurred"
    Then the listener should be notified
    And the listener should receive the correct topic and message

  Scenario: Unregister a notification listener
    Given I have a notification listener registered for topic "system-events"
    When I unregister the listener
    And I send a notification with subject "system-events" and message "System event occurred"
    Then the listener should not be notified

  Scenario: Multiple notification listeners
    Given I have notification listeners registered for the following topics:
      | system-events |
      | user-events   |
      | data-events   |
    When I send notifications for each topic
    Then each listener should receive only its registered notifications