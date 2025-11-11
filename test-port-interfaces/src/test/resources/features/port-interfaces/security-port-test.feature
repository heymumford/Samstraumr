@Security @PortTest @port
Feature: Security Port Interface
  As a system developer
  I want to use the SecurityPort interface for authentication and authorization
  So that I can keep my application core independent of security implementations

  Background:
    Given a clean security system environment
    And the SecurityPort interface is properly initialized
    And the following users are registered:
      | username | password | roles                  | permissions                               |
      | admin    | admin123 | ADMIN,USER             | READ_ALL,WRITE_ALL,DELETE_ALL,ADMIN_ALL   |
      | manager  | mgr456   | MANAGER,USER           | READ_ALL,WRITE_PROJECT,READ_USERS         |
      | user     | pwd789   | USER                   | READ_PROJECT,WRITE_OWN                    |

  @Smoke
  Scenario: User authentication with valid credentials
    When I authenticate user "admin" with password "admin123"
    Then the authentication should succeed
    And the user should have role "ADMIN"
    And the user should have permission "WRITE_ALL"

  @Smoke
  Scenario: User authentication with invalid credentials
    When I authenticate user "admin" with password "wrongPassword"
    Then the authentication should fail
    And the authentication error should contain "Invalid credentials"

  @Smoke
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

  @Error
  Scenario: Security initialization failure handling
    Given the security system is already initialized
    When I attempt to re-initialize the security system
    Then the system should handle the initialization gracefully
    And the security system should remain in a valid state

  @Error
  Scenario: Invalid token handling
    When I attempt to validate an invalid token "invalid-token-12345"
    Then the token validation should fail
    And the validation error should contain "Invalid token"

  @Error
  Scenario: Security context isolation
    Given I authenticate user "user" with password "pwd789"
    Then the user should have specific permissions
    When I create a second security context for admin
    Then the contexts should be isolated
    And permissions should not leak between contexts

  @Concurrency
  Scenario: Concurrent authentication attempts
    When 10 concurrent authentication attempts are made
    Then all successful authentications should create valid security contexts
    And failed authentications should not create security contexts
    
  @Concurrency
  Scenario: Concurrent token operations
    Given I authenticate user "admin" with password "admin123"
    When 20 tokens are generated concurrently
    And half of the tokens are revoked concurrently
    Then all generated tokens should be properly tracked
    And revoked tokens should be invalidated
    And valid tokens should remain functional

  @Audit
  Scenario: Security audit log validation
    Given multiple security events have occurred:
      | eventType      | user    | resource      | result   |
      | LOGIN_SUCCESS  | admin   | n/a           | success  |
      | ACCESS_DENIED  | user    | confidential  | denied   |
      | TOKEN_ISSUED   | manager | n/a           | success  |
      | LOGIN_FAILURE  | unknown | n/a           | failure  |
    When I retrieve the security audit log for the last hour
    Then all security events should be recorded
    And the audit log entries should contain all relevant details

  @Performance
  Scenario: Security permission check performance
    Given a security context with 1000 permission entries
    When I measure the performance of 500 permission checks
    Then the average check time should be under 1 millisecond
    And no security checks should exceed 10 milliseconds

  @Resource
  Scenario: Resource-based permission management
    Given a security context for resource access
    When I grant permissions on specific resources:
      | resource      | permission |
      | /documents/1  | READ       |
      | /documents/2  | WRITE      |
      | /system/logs  | READ       |
    Then permission checks should correctly identify access rights
    And revoking a permission should remove access
    And the resource permissions should be isolated between contexts