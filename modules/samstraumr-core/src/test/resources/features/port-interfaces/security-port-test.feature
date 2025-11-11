@L2_Integration @Functional @PortInterface @Security
Feature: Security Port Interface
  As a system developer
  I want to use the SecurityPort interface for authentication and authorization
  So that I can keep my application core independent of security implementations

  Background:
    Given a clean system environment
    And the SecurityPort interface is properly initialized
    And the following users are registered:
      | username | password | roles                  | permissions                               |
      | admin    | admin123 | ADMIN,USER             | READ_ALL,WRITE_ALL,DELETE_ALL,ADMIN_ALL   |
      | manager  | mgr456   | MANAGER,USER           | READ_ALL,WRITE_PROJECT,READ_USERS         |
      | user     | pwd789   | USER                   | READ_PROJECT,WRITE_OWN                    |

  Scenario: User authentication with valid credentials
    When I authenticate user "admin" with password "admin123"
    Then the authentication should succeed
    And the user should have role "ADMIN"
    And the user should have permission "WRITE_ALL"

  Scenario: User authentication with invalid credentials
    When I authenticate user "admin" with password "wrongPassword"
    Then the authentication should fail
    And the authentication error should contain "Invalid credentials"

  Scenario: Token generation and validation
    Given I authenticate user "manager" with password "mgr456"
    When I generate a security token with validity of 30 minutes
    Then the token generation should succeed
    And I should receive a valid token
    When I validate the generated token
    Then the token validation should succeed
    And the token should be associated with user "manager"

  Scenario: Token revocation
    Given I authenticate user "user" with password "pwd789"
    And I generate a security token with validity of 30 minutes
    When I revoke the generated token
    Then the token revocation should succeed
    When I validate the revoked token
    Then the token validation should fail
    And the validation error should contain "Token has been revoked"

  Scenario: Role-based access control
    Given I authenticate user "manager" with password "mgr456"
    Then the user should have role "MANAGER"
    And the user should not have role "ADMIN"
    And hasRole check for "MANAGER" should return true
    And hasRole check for "ADMIN" should return false
    And hasAnyRole check for "ADMIN,MANAGER" should return true
    And hasAllRoles check for "USER,MANAGER" should return true
    And hasAllRoles check for "USER,ADMIN" should return false

  Scenario: Permission-based access control
    Given I authenticate user "manager" with password "mgr456"
    Then the user should have permission "WRITE_PROJECT"
    And the user should not have permission "DELETE_ALL"
    And hasPermission check for "WRITE_PROJECT" should return true
    And hasPermission check for "DELETE_ALL" should return false
    And hasAnyPermission check for "DELETE_ALL,WRITE_PROJECT" should return true
    And hasAllPermissions check for "READ_ALL,WRITE_PROJECT" should return true
    And hasAllPermissions check for "READ_ALL,DELETE_ALL" should return false

  Scenario: Component access control
    Given I authenticate user "manager" with password "mgr456"
    When I check component "comp-123" access to resource "resource-456" for operation "READ"
    Then the access check should succeed
    When I check component "comp-123" access to resource "resource-789" for operation "DELETE"
    Then the access check should fail
    And the access check error should contain "insufficient permissions"

  Scenario: Security event logging
    Given I authenticate user "admin" with password "admin123"
    When I log a security event of type "ACCESS_GRANTED" with details:
      | resourceId | resource-123        |
      | operation  | READ                |
      | timestamp  | 2025-04-06T12:30:00 |
    Then the security event logging should succeed
    When I retrieve the security audit log
    Then the audit log should contain an event of type "ACCESS_GRANTED"
    And the audit log should include the logged details

  Scenario: User registration
    When I register a new user with:
      | username | newuser  |
      | password | newpwd123|
      | roles    | USER     |
    Then the user registration should succeed
    When I authenticate user "newuser" with password "newpwd123"
    Then the authentication should succeed
    And the user should have role "USER"

  Scenario: User role and permission updates
    Given I authenticate user "admin" with password "admin123"
    When I update user "user" roles to "USER,REPORTER"
    Then the role update should succeed
    And user "user" should have role "REPORTER"
    When I update user "user" permissions to "READ_ALL,WRITE_REPORT"
    Then the permission update should succeed
    And user "user" should have permission "WRITE_REPORT"