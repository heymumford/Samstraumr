# Copyright (c) 2025
# All rights reserved.

@integration @L1 @ports
Feature: Event and Notification Integration
  As an application developer
  I want to ensure the Event and Notification ports work together correctly
  So that events can trigger notifications in the system

  Background:
    Given an event publisher is initialized
    And a notification system is initialized

  @smoke
  Scenario: Events trigger notifications
    Given I have registered a notification listener for topic "system-events"
    When I publish an event with type "user.login" to topic "system-events" with message "User logged in"
    Then a notification should be sent with subject "user.login"
    And the notification message should contain "User logged in"

  Scenario: Events with properties trigger notifications with properties
    Given I have registered a notification listener for topic "data-events"
    When I publish an event with the following properties:
      | topic     | data-events    |
      | type      | data.update    |
      | message   | Data updated   |
      | userId    | 12345          |
      | severity  | high           |
    Then a notification should be sent with the following properties:
      | subject   | data.update    |
      | message   | Data updated   |
      | userId    | 12345          |
      | severity  | high           |

  Scenario: Multiple events trigger corresponding notifications
    Given I have registered notification listeners for the following topics:
      | system-events  |
      | user-events    |
      | data-events    |
    When I publish events to the following topics:
      | topic         | type          | message               |
      | system-events | system.start  | System started        |
      | user-events   | user.create   | New user created      |
      | data-events   | data.import   | Data import completed |
    Then notifications should be sent for each event
    And each notification should have the corresponding subject and message