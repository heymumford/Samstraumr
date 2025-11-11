@L2_Integration @Functional @Security @Event @integration
Feature: Security and Event Publisher Port Integration
  As a system developer
  I want to integrate SecurityPort with EventPublisherPort
  So that security events are properly published and can be monitored

  Background:
    Given a clean system environment
    And the SecurityPort interface is properly initialized
    And the EventPublisherPort interface is properly initialized
    And the following test users are registered:
      | username | password    | roles                  |
      | admin    | admin123    | ADMIN, USER            |
      | user1    | password123 | USER                   |
      | auditor  | audit123    | AUDITOR                |
    And an event subscriber is registered for the "security.events" topic

  Scenario: Successful login publishes a login success event
    When user "admin" logs in with password "admin123"
    Then the login should be successful
    And a security event of type "LOGIN_SUCCESS" should be published
    And the event should contain the username "admin"
    And the event should include timestamp and session information

  Scenario: Failed login publishes a login failure event
    When user "admin" logs in with password "wrong_password"
    Then the login should fail
    And a security event of type "LOGIN_FAILURE" should be published
    And the event should contain the username "admin"
    And the event should include failure reason "Invalid credentials"

  Scenario: User logout publishes a logout event
    Given user "user1" is authenticated
    When the user logs out
    Then a security event of type "LOGOUT" should be published
    And the event should contain the username "user1"

  Scenario: Access denied publishes an access denied event
    Given user "user1" is authenticated
    When the user attempts to access a restricted resource "system/admin/config"
    Then access should be denied
    And a security event of type "ACCESS_DENIED" should be published
    And the event should contain the username "user1"
    And the event should contain the resource "system/admin/config"
    And the event should contain the required permission "ADMIN"

  Scenario: Access granted publishes an access granted event
    Given user "admin" is authenticated
    When the user attempts to access a restricted resource "system/admin/config"
    Then access should be granted
    And a security event of type "ACCESS_GRANTED" should be published
    And the event should contain the username "admin"
    And the event should contain the resource "system/admin/config"
    And the event should contain the permission "ADMIN"

  Scenario: Token generation publishes a token issued event
    Given user "user1" is authenticated
    When the user requests a security token with validity of "1 hour"
    Then a valid token should be generated
    And a security event of type "TOKEN_ISSUED" should be published
    And the event should contain the username "user1"
    And the event should contain token validity information

  Scenario: Token validation publishes a token validated event
    Given user "user1" is authenticated
    And the user has a valid security token
    When the token is validated
    Then the validation should be successful
    And a security event of type "TOKEN_VALIDATED" should be published
    And the event should contain the username "user1"

  Scenario: Token revocation publishes a token expired event
    Given user "user1" is authenticated
    And the user has a valid security token
    When the token is revoked
    Then a security event of type "TOKEN_EXPIRED" should be published
    And the event should contain the username "user1"

  Scenario: Role change publishes a permission changed event
    Given user "admin" is authenticated
    When user "user1" is granted the "MANAGER" role
    Then a security event of type "PERMISSION_CHANGED" should be published
    And the event should contain the username "user1"
    And the event should contain the added role "MANAGER"
    And the event should contain the modifier username "admin"

  Scenario: Security configuration change publishes a configuration changed event
    Given user "admin" is authenticated
    When the security configuration is changed
    Then a security event of type "SECURITY_CONFIG_CHANGED" should be published
    And the event should contain the username "admin"
    And the event should include details about the configuration change

  Scenario: Multiple failed login attempts trigger a security alert
    When 5 failed login attempts are made for user "user1"
    Then a security event of type "POTENTIAL_ATTACK_DETECTED" should be published
    And the event should contain details about the attack pattern
    And the event should contain the target username "user1"

  Scenario: Auditor can view security events
    Given user "auditor" is authenticated
    When the auditor requests the security audit log
    Then the audit log should be returned successfully
    And the audit log should contain all security events