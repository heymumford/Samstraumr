/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.SecurityResult;

/** Tests for the InMemorySecurityAdapter. */
class InMemorySecurityAdapterTest {

  private InMemorySecurityAdapter securityAdapter;

  @Mock private LoggerPort loggerMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    securityAdapter = new InMemorySecurityAdapter(loggerMock);

    // Initialize the security system
    SecurityResult initResult = securityAdapter.initialize();
    assertTrue(initResult.isSuccessful(), "Security system should initialize successfully");
  }

  @Test
  void testAuthenticateSuccess() {
    // Test authentication with default admin credentials
    SecurityResult result = securityAdapter.authenticate("admin", "admin123");

    assertTrue(result.isSuccessful(), "Authentication should succeed with correct credentials");
    assertEquals("Authentication successful", result.getMessage(), "Success message should match");

    // Verify user context
    Optional<SecurityPort.AuthenticationContext> authContext =
        securityAdapter.getCurrentAuthContext();
    assertTrue(authContext.isPresent(), "Auth context should be present after successful login");
    assertEquals("admin", authContext.get().getUsername(), "Username should match");
    assertTrue(authContext.get().hasRole("ADMIN"), "User should have ADMIN role");
  }

  @Test
  void testAuthenticateFailure() {
    // Test authentication with invalid credentials
    SecurityResult result = securityAdapter.authenticate("admin", "wrongpassword");

    assertFalse(result.isSuccessful(), "Authentication should fail with incorrect credentials");
    assertEquals("Authentication failed", result.getMessage(), "Failure message should match");
    assertTrue(
        result.getReason().isPresent(), "Reason should be present for failed authentication");
    assertEquals(
        "Invalid username or password",
        result.getReason().get(),
        "Reason should indicate invalid credentials");
  }

  @Test
  void testLogout() {
    // First authenticate
    securityAdapter.authenticate("admin", "admin123");
    assertTrue(securityAdapter.getCurrentAuthContext().isPresent(), "User should be authenticated");

    // Now logout
    SecurityResult result = securityAdapter.logout();

    assertTrue(result.isSuccessful(), "Logout should succeed");
    assertFalse(
        securityAdapter.getCurrentAuthContext().isPresent(),
        "No auth context should be present after logout");
  }

  @Test
  void testTokenLifecycle() {
    // Authenticate first
    securityAdapter.authenticate("admin", "admin123");

    // Generate a token
    SecurityResult generateResult = securityAdapter.generateToken(Duration.ofMinutes(5));
    assertTrue(generateResult.isSuccessful(), "Token generation should succeed");
    String token = (String) generateResult.getAttributes().get("token");
    assertNotNull(token, "Token should not be null");

    // Logout to clear the current context
    securityAdapter.logout();
    assertFalse(
        securityAdapter.getCurrentAuthContext().isPresent(),
        "No auth context should be present after logout");

    // Authenticate with the token
    SecurityResult authResult = securityAdapter.authenticateWithToken(token);
    assertTrue(authResult.isSuccessful(), "Token authentication should succeed");
    assertTrue(
        securityAdapter.getCurrentAuthContext().isPresent(),
        "Auth context should be present after token auth");

    // Validate the token
    SecurityResult validateResult = securityAdapter.validateToken(token);
    assertTrue(validateResult.isSuccessful(), "Token validation should succeed");

    // Revoke the token
    SecurityResult revokeResult = securityAdapter.revokeToken(token);
    assertTrue(revokeResult.isSuccessful(), "Token revocation should succeed");

    // Try to authenticate with the revoked token
    securityAdapter.logout(); // Clear context first
    SecurityResult failedAuthResult = securityAdapter.authenticateWithToken(token);
    assertFalse(failedAuthResult.isSuccessful(), "Authentication should fail with revoked token");
    assertTrue(
        failedAuthResult.getReason().isPresent(),
        "Reason should be present for failed authentication");
    assertEquals(
        "Token has been revoked",
        failedAuthResult.getReason().get(),
        "Reason should indicate token revoked");
  }

  @Test
  void testUserRegistrationAndRoleManagement() {
    // Register a new user
    SecurityResult registerResult =
        securityAdapter.registerUser("testuser", "password123", Set.of("USER"));
    assertTrue(registerResult.isSuccessful(), "User registration should succeed");
    String userId = (String) registerResult.getAttributes().get("userId");
    assertNotNull(userId, "User ID should not be null");

    // Authenticate as admin to update roles
    securityAdapter.authenticate("admin", "admin123");

    // Update the user's roles
    SecurityResult updateResult =
        securityAdapter.updateUserRoles(userId, Set.of("USER", "MANAGER"));
    assertTrue(updateResult.isSuccessful(), "Role update should succeed");

    // Logout admin and login as the new user
    securityAdapter.logout();
    SecurityResult authResult = securityAdapter.authenticate("testuser", "password123");
    assertTrue(authResult.isSuccessful(), "Authentication should succeed for new user");

    // Check roles
    assertTrue(securityAdapter.hasRole("USER"), "User should have USER role");
    assertTrue(securityAdapter.hasRole("MANAGER"), "User should have MANAGER role");
    assertFalse(securityAdapter.hasRole("ADMIN"), "User should not have ADMIN role");
  }

  @Test
  void testComponentAccess() {
    // The initialize method should have set up default component access

    // Test access for component1 to resourceA (should be allowed)
    SecurityResult accessResult =
        securityAdapter.checkComponentAccess("component1", "resourceA", "READ");
    assertTrue(accessResult.isSuccessful(), "Component1 should have READ access to resourceA");

    // Test access for component1 to resourceC (should be denied)
    SecurityResult deniedResult =
        securityAdapter.checkComponentAccess("component1", "resourceC", "READ");
    assertFalse(deniedResult.isSuccessful(), "Component1 should not have access to resourceC");

    // Test access for component2 to resourceA with WRITE (should be denied)
    SecurityResult writeResult =
        securityAdapter.checkComponentAccess("component2", "resourceA", "WRITE");
    assertFalse(writeResult.isSuccessful(), "Component2 should not have WRITE access to resourceA");

    // Test access for nonexistent component
    SecurityResult unknownResult =
        securityAdapter.checkComponentAccess("unknown", "resourceA", "READ");
    assertFalse(unknownResult.isSuccessful(), "Unknown component should not have access");
  }

  @Test
  void testSecurityEventLogging() {
    // Authenticate as admin
    securityAdapter.authenticate("admin", "admin123");

    // Log some security events
    securityAdapter.logSecurityEvent(
        SecurityPort.SecurityEventType.SECURITY_ALERT,
        Map.of("source", "test", "severity", "high"));

    // Get the audit log
    List<Map<String, Object>> logs =
        securityAdapter.getSecurityAuditLog(
            Instant.now().minus(Duration.ofMinutes(5)), Instant.now());

    assertFalse(logs.isEmpty(), "Audit log should not be empty");

    // Find our test entry
    boolean found =
        logs.stream()
            .anyMatch(
                entry ->
                    "SECURITY_ALERT".equals(entry.get("eventType"))
                        && "test".equals(entry.get("source"))
                        && "high".equals(entry.get("severity")));

    assertTrue(found, "Security event should be in the audit log");
  }

  @Test
  void testPermissionChecking() {
    // Authenticate as admin
    securityAdapter.authenticate("admin", "admin123");

    // Check single permission
    assertTrue(securityAdapter.hasPermission("READ"), "Admin should have READ permission");
    assertTrue(securityAdapter.hasPermission("WRITE"), "Admin should have WRITE permission");
    assertTrue(securityAdapter.hasPermission("ADMIN"), "Admin should have ADMIN permission");

    // Check multiple permissions
    assertTrue(
        securityAdapter.hasAllPermissions("READ", "WRITE"),
        "Admin should have all specified permissions");
    assertTrue(
        securityAdapter.hasAnyPermission("READ", "NONEXISTENT"),
        "Admin should have at least one of the specified permissions");

    // Check for non-existent permission
    assertFalse(
        securityAdapter.hasPermission("NONEXISTENT"),
        "Admin should not have NONEXISTENT permission");
  }

  @Test
  void testRoleChecking() {
    // Authenticate as admin
    securityAdapter.authenticate("admin", "admin123");

    // Check single role
    assertTrue(securityAdapter.hasRole("ADMIN"), "User should have ADMIN role");

    // Check multiple roles
    assertTrue(
        securityAdapter.hasAnyRole("ADMIN", "NONEXISTENT"),
        "User should have at least one of the specified roles");
    assertFalse(
        securityAdapter.hasAllRoles("ADMIN", "NONEXISTENT"),
        "User should not have all specified roles");

    // Check for non-existent role
    assertFalse(securityAdapter.hasRole("NONEXISTENT"), "User should not have NONEXISTENT role");
  }

  @Test
  void testShutdown() {
    // Authenticate
    securityAdapter.authenticate("admin", "admin123");
    assertTrue(securityAdapter.getCurrentAuthContext().isPresent(), "User should be authenticated");

    // Generate a token
    SecurityResult tokenResult = securityAdapter.generateToken(Duration.ofHours(1));
    String token = (String) tokenResult.getAttributes().get("token");

    // Shutdown
    SecurityResult shutdownResult = securityAdapter.shutdown();
    assertTrue(shutdownResult.isSuccessful(), "Shutdown should succeed");

    // Auth context should be cleared
    assertFalse(
        securityAdapter.getCurrentAuthContext().isPresent(),
        "Auth context should be cleared after shutdown");

    // Token should be invalidated
    SecurityResult validateResult = securityAdapter.validateToken(token);
    assertFalse(validateResult.isSuccessful(), "Token should be invalidated after shutdown");
  }
}
