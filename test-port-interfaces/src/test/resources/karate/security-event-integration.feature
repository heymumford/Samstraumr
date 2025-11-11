Feature: Security Event Integration Tests
  As a system operator
  I want security events to be properly published via the event system
  So that security incidents can be monitored and addressed

  Background:
    * def SecurityAdapterConfig = { authenticationCacheSize: 100, tokenExpirySeconds: 300 }
    * def EventPublisherConfig = { bufferSize: 1000, asyncPublishing: true }
    
    # Initialize adapters with configurations
    * def securityAdapter = Java.type('org.s8r.test.mock.MockSecurityAdapter').create(SecurityAdapterConfig)
    * def eventPublisher = Java.type('org.s8r.test.mock.MockEventPublisherAdapter').create(EventPublisherConfig)
    
    # Create the integration bridge
    * def securityEventBridge = Java.type('org.s8r.infrastructure.security.SecurityEventBridge').create(securityAdapter, eventPublisher)

  Scenario: Authentication success events should be published
    Given the security adapter is initialized
    When a user successfully authenticates with username "testuser" and password "validPassword"
    Then an "AUTHENTICATION_SUCCESS" event should be published
    And the event should contain the username "testuser"
    And the event should contain the authentication timestamp
    And the event should contain the authentication source

  Scenario: Authentication failure events should be published
    Given the security adapter is initialized
    When a user fails to authenticate with username "testuser" and invalid password "wrongPassword"
    Then an "AUTHENTICATION_FAILURE" event should be published
    And the event should contain the username "testuser"
    And the event should contain the failure reason "INVALID_CREDENTIALS"
    And the event should contain the authentication timestamp
    And the event should contain the authentication source

  Scenario: Multiple authentication failures should trigger a brute force detection event
    Given the security adapter is initialized
    When a user fails to authenticate 5 times within 60 seconds with username "testuser"
    Then an "AUTHENTICATION_FAILURE" event should be published for each attempt
    And a "BRUTE_FORCE_ATTEMPT_DETECTED" event should be published
    And the brute force event should contain the username "testuser"
    And the brute force event should contain the number of attempts
    And the brute force event should contain the time period
    And the brute force event should contain the IP address

  Scenario: Authorization failure events should be published
    Given a user "testuser" is authenticated with roles "USER"
    When the user attempts to access a resource requiring "ADMIN" role
    Then an "AUTHORIZATION_FAILURE" event should be published
    And the event should contain the username "testuser"
    And the event should contain the requested resource
    And the event should contain the required role "ADMIN"
    And the event should contain the user's actual roles "USER"

  Scenario: Session expiration events should be published
    Given a user "testuser" has an active session
    When the session expires after the configured timeout
    Then a "SESSION_EXPIRED" event should be published
    And the event should contain the username "testuser"
    And the event should contain the session id
    And the event should contain the session creation time
    And the event should contain the session expiration time

  Scenario Outline: Security configuration change events should be published
    Given the security adapter is initialized
    When the security setting "<settingName>" is changed from "<oldValue>" to "<newValue>"
    Then a "SECURITY_CONFIGURATION_CHANGED" event should be published
    And the event should contain the setting name "<settingName>"
    And the event should contain the old value "<oldValue>"
    And the event should contain the new value "<newValue>"
    And the event should contain the user who made the change

    Examples:
      | settingName          | oldValue | newValue |
      | passwordMinLength    | 8        | 12       |
      | maxLoginAttempts     | 3        | 5        |
      | sessionTimeoutMinutes| 30       | 15       |
      | mfaRequired          | false    | true     |