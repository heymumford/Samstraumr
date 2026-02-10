/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.AuthenticationContext;
import org.s8r.application.port.SecurityPort.SecurityResult;

/** Tests for the SecurityService. */
class SecurityServiceTest {

  private SecurityService securityService;

  @Mock private SecurityPort securityPortMock;

  @Mock private LoggerPort loggerMock;

  @Mock private AuthenticationContext authContextMock;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    securityService = new SecurityService(securityPortMock, loggerMock);
  }

  @Test
  void testInitialize() {
    // Setup
    when(securityPortMock.initialize()).thenReturn(SecurityResult.success("Initialized"));

    // Act
    securityService.initialize();

    // Verify
    verify(securityPortMock).initialize();
    verify(loggerMock).info(anyString());
  }

  @Test
  void testInitializeFailure() {
    // Setup
    when(securityPortMock.initialize())
        .thenReturn(SecurityResult.failure("Failed", "Initialization error"));

    // Act
    securityService.initialize();

    // Verify
    verify(securityPortMock).initialize();
    verify(loggerMock).info(anyString());
    verify(loggerMock).error(contains("Failed to initialize security service"));
  }

  @Test
  void testLoginSuccess() {
    // Setup
    when(securityPortMock.authenticate("user", "password"))
        .thenReturn(SecurityResult.success("Authenticated"));

    // Act
    boolean result = securityService.login("user", "password");

    // Verify
    assertTrue(result, "Login should succeed");
    verify(securityPortMock).authenticate("user", "password");
    verify(loggerMock, times(2)).debug(anyString());
  }

  @Test
  void testLoginFailure() {
    // Setup
    when(securityPortMock.authenticate("user", "wrong"))
        .thenReturn(SecurityResult.failure("Failed", "Invalid credentials"));

    // Act
    boolean result = securityService.login("user", "wrong");

    // Verify
    assertFalse(result, "Login should fail");
    verify(securityPortMock).authenticate("user", "wrong");
    verify(loggerMock, times(2)).debug(anyString());
  }

  @Test
  void testLogout() {
    // Act
    securityService.logout();

    // Verify
    verify(securityPortMock).logout();
    verify(loggerMock).debug(anyString());
  }

  @Test
  void testIsAuthenticated() {
    // Setup
    when(securityPortMock.getCurrentAuthContext()).thenReturn(Optional.of(authContextMock));

    // Act
    boolean result = securityService.isAuthenticated();

    // Verify
    assertTrue(result, "User should be authenticated");
    verify(securityPortMock).getCurrentAuthContext();
  }

  @Test
  void testIsNotAuthenticated() {
    // Setup
    when(securityPortMock.getCurrentAuthContext()).thenReturn(Optional.empty());

    // Act
    boolean result = securityService.isAuthenticated();

    // Verify
    assertFalse(result, "User should not be authenticated");
    verify(securityPortMock).getCurrentAuthContext();
  }

  @Test
  void testGetCurrentUsername() {
    // Setup
    when(securityPortMock.getCurrentAuthContext()).thenReturn(Optional.of(authContextMock));
    when(authContextMock.getUsername()).thenReturn("testuser");

    // Act
    Optional<String> result = securityService.getCurrentUsername();

    // Verify
    assertTrue(result.isPresent(), "Username should be present");
    assertEquals("testuser", result.get(), "Username should match");
    verify(securityPortMock).getCurrentAuthContext();
  }

  @Test
  void testHasRole() {
    // Setup
    when(securityPortMock.hasRole("ADMIN")).thenReturn(true);

    // Act
    boolean result = securityService.hasRole("ADMIN");

    // Verify
    assertTrue(result, "User should have the role");
    verify(securityPortMock).hasRole("ADMIN");
  }

  @Test
  void testHasPermission() {
    // Setup
    when(securityPortMock.hasPermission("READ")).thenReturn(true);

    // Act
    boolean result = securityService.hasPermission("READ");

    // Verify
    assertTrue(result, "User should have the permission");
    verify(securityPortMock).hasPermission("READ");
  }

  @Test
  void testCheckAccess() {
    // Setup
    when(securityPortMock.checkComponentAccess("comp1", "res1", "READ"))
        .thenReturn(SecurityResult.success("Access granted"));

    // Act
    boolean result = securityService.checkAccess("comp1", "res1", "READ");

    // Verify
    assertTrue(result, "Access should be granted");
    verify(securityPortMock).checkComponentAccess("comp1", "res1", "READ");
  }

  @Test
  void testGenerateToken() {
    // Setup
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("token", "abc123");
    when(securityPortMock.generateToken(any(Duration.class)))
        .thenReturn(SecurityResult.success("Token generated", attributes));

    // Act
    Optional<String> result = securityService.generateToken();

    // Verify
    assertTrue(result.isPresent(), "Token should be present");
    assertEquals("abc123", result.get(), "Token should match");
    verify(securityPortMock).generateToken(any(Duration.class));
  }

  @Test
  void testGenerateTokenWithValidity() {
    // Setup
    Duration validity = Duration.ofMinutes(30);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("token", "abc123");
    when(securityPortMock.generateToken(validity))
        .thenReturn(SecurityResult.success("Token generated", attributes));

    // Act
    Optional<String> result = securityService.generateToken(validity);

    // Verify
    assertTrue(result.isPresent(), "Token should be present");
    assertEquals("abc123", result.get(), "Token should match");
    verify(securityPortMock).generateToken(validity);
  }

  @Test
  void testValidateToken() {
    // Setup
    when(securityPortMock.validateToken("abc123"))
        .thenReturn(SecurityResult.success("Token valid"));

    // Act
    boolean result = securityService.validateToken("abc123");

    // Verify
    assertTrue(result, "Token should be valid");
    verify(securityPortMock).validateToken("abc123");
  }

  @Test
  void testLoginWithToken() {
    // Setup
    when(securityPortMock.authenticateWithToken("abc123"))
        .thenReturn(SecurityResult.success("Token authentication successful"));

    // Act
    boolean result = securityService.loginWithToken("abc123");

    // Verify
    assertTrue(result, "Token login should succeed");
    verify(securityPortMock).authenticateWithToken("abc123");
  }

  @Test
  void testRevokeToken() {
    // Setup
    when(securityPortMock.revokeToken("abc123"))
        .thenReturn(SecurityResult.success("Token revoked"));

    // Act
    boolean result = securityService.revokeToken("abc123");

    // Verify
    assertTrue(result, "Token should be revoked");
    verify(securityPortMock).revokeToken("abc123");
  }

  @Test
  void testRegisterUser() {
    // Setup
    Set<String> roles = Set.of("USER");
    when(securityPortMock.registerUser("newuser", "password", roles))
        .thenReturn(SecurityResult.success("User registered"));

    // Act
    boolean result = securityService.registerUser("newuser", "password", roles);

    // Verify
    assertTrue(result, "User registration should succeed");
    verify(securityPortMock).registerUser("newuser", "password", roles);
  }

  @Test
  void testUpdateUserRoles() {
    // Setup
    String userId = "user123";
    Set<String> roles = Set.of("USER", "MANAGER");
    when(securityPortMock.updateUserRoles(userId, roles))
        .thenReturn(SecurityResult.success("Roles updated"));

    // Act
    boolean result = securityService.updateUserRoles(userId, roles);

    // Verify
    assertTrue(result, "Role update should succeed");
    verify(securityPortMock).updateUserRoles(userId, roles);
  }

  @Test
  void testGetAuditLog() {
    // Setup
    Instant from = Instant.now().minus(Duration.ofDays(1));
    Instant to = Instant.now();
    List<Map<String, Object>> mockLog = new ArrayList<>();
    Map<String, Object> entry = new HashMap<>();
    entry.put("eventType", "LOGIN_SUCCESS");
    mockLog.add(entry);

    when(securityPortMock.getSecurityAuditLog(from, to)).thenReturn(mockLog);

    // Act
    List<Map<String, Object>> result = securityService.getAuditLog(from, to);

    // Verify
    assertNotNull(result, "Audit log should not be null");
    assertEquals(1, result.size(), "Audit log should have one entry");
    assertEquals("LOGIN_SUCCESS", result.get(0).get("eventType"), "Event type should match");
    verify(securityPortMock).getSecurityAuditLog(from, to);
  }

  @Test
  void testLogSecurityEvent() {
    // Setup
    SecurityPort.SecurityEventType eventType = SecurityPort.SecurityEventType.SECURITY_ALERT;
    Map<String, Object> details = Map.of("source", "test", "severity", "high");
    when(securityPortMock.logSecurityEvent(eventType, details))
        .thenReturn(SecurityResult.success("Event logged"));

    // Act
    boolean result = securityService.logSecurityEvent(eventType, details);

    // Verify
    assertTrue(result, "Event logging should succeed");
    verify(securityPortMock).logSecurityEvent(eventType, details);
  }

  @Test
  void testShutdown() {
    // Setup
    when(securityPortMock.shutdown()).thenReturn(SecurityResult.success("Shutdown"));

    // Act
    securityService.shutdown();

    // Verify
    verify(securityPortMock).shutdown();
    verify(loggerMock).info(anyString());
  }

  @Test
  void testShutdownFailure() {
    // Setup
    when(securityPortMock.shutdown())
        .thenReturn(SecurityResult.failure("Failed", "Shutdown error"));

    // Act
    securityService.shutdown();

    // Verify
    verify(securityPortMock).shutdown();
    verify(loggerMock).info(anyString());
    verify(loggerMock).error(contains("Failed to shut down security service"));
  }
}
