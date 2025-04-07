@L3_Integration @Functional @PortIntegration
Feature: Event Publisher and Notification Port Integration
  As a system developer
  I want to ensure that EventPublisherPort and NotificationPort work together correctly
  So that system events can trigger appropriate notifications

  Background:
    Given a clean system environment
    And both event publisher and notification ports are properly initialized
    And notification subscribers are registered for events

  Scenario: Publishing an event that triggers a notification
    Given I have a domain event of type "UserRegisteredEvent" for user "user-123"
    When I publish the event
    Then the event should be successfully published
    And a notification should be sent to system administrators
    And the notification content should include event details

  Scenario: Publishing a critical event that triggers immediate notifications
    Given I have a domain event of type "SystemFailureEvent" with severity "CRITICAL"
    When I publish the event with high priority
    Then the event should be successfully published
    And notifications should be sent to all registered emergency contacts
    And the notification should have priority flag set

  Scenario: Filtering events for specific notification targets
    Given I have the following domain events:
      | type                  | source     | severity | recipient    |
      | ComponentCreatedEvent | component1 | INFO     | developers   |
      | SecurityAlertEvent    | auth       | WARNING  | security     |
      | SystemMetricEvent     | monitor    | INFO     | operations   |
    When I publish all events
    Then each event should trigger notifications only to relevant recipients

  Scenario: Batching events for consolidated notifications
    Given I have multiple domain events of the same type:
      | type              | source     | count |
      | MetricUpdateEvent | service1   | 5     |
    When I publish the events within a short time period
    Then the notifications should be consolidated
    And only a single summary notification should be sent

  Scenario: Event-triggered notifications with templates
    Given the system has notification templates for different event types
    When I publish an event that matches a template
    Then the notification should be formatted according to the template
    And the template variables should be populated from the event data

  Scenario: Asynchronous event publication with delayed notifications
    Given I have a scheduled domain event for future execution
    When I publish the event with a delay parameter
    Then the event should be queued for later processing
    And the notification should be sent at the scheduled time

  Scenario: Notification delivery tracking for event-triggered notifications
    Given I have a domain event that requires delivery confirmation
    When I publish the event
    Then the notification should be sent with tracking enabled
    And the system should record notification delivery status
    And notification delivery metrics should be updated