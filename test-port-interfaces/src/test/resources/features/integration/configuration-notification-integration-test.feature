@L2_Integration @Functional @Configuration @Notification @integration
Feature: Configuration and Notification Port Integration
  As a system developer
  I want to integrate ConfigurationPort with NotificationPort
  So that system notifications can be delivered based on configuration settings

  Background:
    Given a clean system environment
    And the ConfigurationPort interface is properly initialized
    And the NotificationPort interface is properly initialized
    And the following configuration properties are loaded:
      | key                                     | value                               |
      | notification.recipients.default         | admin@example.com                   |
      | notification.recipients.alerts          | alerts@example.com,ops@example.com  |
      | notification.templates.welcome          | Welcome to Samstraumr, {username}!  |
      | notification.templates.error            | Error occurred: {error_message}     |
      | notification.channels.default           | EMAIL                               |
      | notification.channels.urgent            | EMAIL,SMS                           |
      | notification.retry.count                | 3                                   |
      | notification.retry.delay                | 1000                                |
      | notification.enabled                    | true                                |
      | notification.severity.threshold         | WARNING                             |
      | notification.format.default             | HTML                                |
      | notification.batch.size                 | 10                                  |
      | notification.batch.interval             | 5000                                |
      | notification.recipients.admin_group     | admin1@example.com,admin2@example.com |

  Scenario: Send notification using default configuration values
    When a notification is requested with the following details:
      | subject        | Test Subject        |
      | message        | Test Message        |
      | property_key   | property_value      |
    Then the notification is sent using the default recipient from configuration
    And the notification channel should be "EMAIL"
    And the notification format should be "HTML"
    And the notification should include the property "property_key" with value "property_value"

  Scenario: Send notification to configured group of recipients
    When a notification is requested to the "admin_group" with the following details:
      | subject        | Admin Alert         |
      | message        | Important message   |
      | severity       | WARNING             |
    Then the notification is sent to all recipients in the "admin_group" configuration
    And each notification should have the same subject and message
    And each notification severity should be "WARNING"

  Scenario: Use configured template for notification message
    When a notification with template "welcome" is requested with parameters:
      | username       | testuser            |
    Then the notification message should be "Welcome to Samstraumr, testuser!"
    And the notification is sent to the default recipient

  Scenario: Notification with severity below configured threshold is not sent
    Given the notification severity threshold is set to "WARNING"
    When a notification with severity "INFO" is requested
    Then the notification is not sent
    And a message is logged indicating the notification was suppressed

  Scenario: Notification with severity above configured threshold is sent
    Given the notification severity threshold is set to "WARNING" 
    When a notification with severity "ERROR" is requested
    Then the notification is sent
    And the notification severity should be "ERROR"

  Scenario: Respect configured notification channels for urgent messages
    When an urgent notification is requested
    Then the notification is sent via the channels configured for "urgent"
    And the notification is sent via "EMAIL" channel
    And the notification is sent via "SMS" channel

  Scenario: Configuration update triggers notification
    When configuration property "system.status" is updated to "MAINTENANCE"
    Then a notification about the configuration change is sent
    And the notification includes details about the changed property
    And the notification is sent to all recipients in the "admin_group" configuration

  Scenario: Notification is not sent when notifications are disabled
    Given the notification feature is disabled in configuration
    When a notification is requested with the following details:
      | subject        | Test Disabled       |
      | message        | Should not be sent  |
    Then the notification is not sent
    And a message is logged indicating notifications are disabled

  Scenario: Batch notifications respect configured batch size
    When 15 notifications are queued for delivery
    Then the notifications are delivered in batches
    And the first batch contains 10 notifications
    And the second batch contains 5 notifications
    And the batches are delivered with the configured interval