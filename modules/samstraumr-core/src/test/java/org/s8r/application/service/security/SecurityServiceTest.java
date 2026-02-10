/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.application.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.security.SecurityPort;
import org.s8r.application.port.security.SecurityPort.*;

class SecurityServiceTest {

  @Mock private SecurityPort securityPort;

  @Mock private LoggerPort logger;

  private SecurityService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new SecurityService(securityPort, logger);
  }

  @Test
  void authenticate_shouldReturnToken_whenCredentialsAreValid() {
    // Arrange
    String username = "testUser";
    String password = "password123";
    SecurityToken mockToken = mock(SecurityToken.class);
    User mockUser = mock(User.class);

    when(securityPort.authenticate(username, password))
        .thenReturn(AuthenticationResult.success("Authentication successful", mockToken, mockUser));

    // Act
    Optional<SecurityToken> result = service.authenticate(username, password);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockToken, result.get());
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).debug(contains("Authentication successful"), eq(username));
  }

  @Test
  void authenticate_shouldReturnEmpty_whenCredentialsAreInvalid() {
    // Arrange
    String username = "testUser";
    String password = "wrongPassword";

    when(securityPort.authenticate(username, password))
        .thenReturn(AuthenticationResult.failure("Invalid credentials", "INVALID_CREDENTIALS"));

    // Act
    Optional<SecurityToken> result = service.authenticate(username, password);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).warn(contains("Authentication failed"), eq(username), anyString());
  }

  @Test
  void authenticateWithFactors_shouldReturnToken_whenCredentialsAndFactorsAreValid() {
    // Arrange
    String username = "testUser";
    String password = "password123";
    Map<String, String> factors = Map.of("otpCode", "123456");
    SecurityToken mockToken = mock(SecurityToken.class);
    User mockUser = mock(User.class);

    when(securityPort.authenticate(username, password, factors))
        .thenReturn(AuthenticationResult.success("Authentication successful", mockToken, mockUser));

    // Act
    Optional<SecurityToken> result = service.authenticateWithFactors(username, password, factors);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockToken, result.get());
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).debug(contains("Authentication with factors successful"), eq(username));
  }

  @Test
  void authenticateWithFactors_shouldReturnEmpty_whenFactorsAreInvalid() {
    // Arrange
    String username = "testUser";
    String password = "password123";
    Map<String, String> factors = Map.of("otpCode", "wrongCode");

    when(securityPort.authenticate(username, password, factors))
        .thenReturn(AuthenticationResult.failure("Invalid factors", "INVALID_FACTORS"));

    // Act
    Optional<SecurityToken> result = service.authenticateWithFactors(username, password, factors);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).warn(contains("Authentication with factors failed"), eq(username), anyString());
  }

  @Test
  void validateToken_shouldReturnUser_whenTokenIsValid() {
    // Arrange
    String token = "validToken";
    SecurityToken mockToken = mock(SecurityToken.class);
    User mockUser = mock(User.class);

    when(securityPort.validateToken(token))
        .thenReturn(TokenValidationResult.success("Token is valid", mockUser, mockToken));

    // Act
    Optional<User> result = service.validateToken(token);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockUser, result.get());
    verify(logger).debug(contains("Validating security token"));
    verify(logger).debug(contains("Token validation successful"));
  }

  @Test
  void validateToken_shouldReturnEmpty_whenTokenIsInvalid() {
    // Arrange
    String token = "invalidToken";

    when(securityPort.validateToken(token))
        .thenReturn(TokenValidationResult.failure("Token is invalid", "INVALID_TOKEN"));

    // Act
    Optional<User> result = service.validateToken(token);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Validating security token"));
    verify(logger).warn(contains("Token validation failed"), anyString());
  }

  @Test
  void refreshToken_shouldReturnNewToken_whenRefreshTokenIsValid() {
    // Arrange
    String refreshToken = "validRefreshToken";
    SecurityToken mockToken = mock(SecurityToken.class);

    when(securityPort.refreshToken(refreshToken))
        .thenReturn(TokenRefreshResult.success("Token refreshed", mockToken, "newRefreshToken"));

    // Act
    Optional<SecurityToken> result = service.refreshToken(refreshToken);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockToken, result.get());
    verify(logger).debug(contains("Refreshing token"));
    verify(logger).debug(contains("Token refresh successful"));
  }

  @Test
  void refreshToken_shouldReturnEmpty_whenRefreshTokenIsInvalid() {
    // Arrange
    String refreshToken = "invalidRefreshToken";

    when(securityPort.refreshToken(refreshToken))
        .thenReturn(TokenRefreshResult.failure("Invalid refresh token", "INVALID_TOKEN"));

    // Act
    Optional<SecurityToken> result = service.refreshToken(refreshToken);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Refreshing token"));
    verify(logger).warn(contains("Token refresh failed"), anyString());
  }

  @Test
  void revokeToken_shouldReturnTrue_whenTokenIsRevoked() {
    // Arrange
    String token = "validToken";

    when(securityPort.revokeToken(token))
        .thenReturn(OperationResult.success("Token revoked successfully"));

    // Act
    boolean result = service.revokeToken(token);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Revoking token"));
    verify(logger).debug(contains("Token revocation successful"));
  }

  @Test
  void revokeToken_shouldReturnFalse_whenTokenRevocationFails() {
    // Arrange
    String token = "invalidToken";

    when(securityPort.revokeToken(token))
        .thenReturn(OperationResult.failure("Token revocation failed", "INVALID_TOKEN"));

    // Act
    boolean result = service.revokeToken(token);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Revoking token"));
    verify(logger).warn(contains("Token revocation failed"), anyString());
  }

  @Test
  void hasPermission_shouldReturnTrue_whenUserHasPermission() {
    // Arrange
    String userId = "user123";
    String permission = "READ_DATA";

    when(securityPort.hasPermission(userId, permission))
        .thenReturn(AuthorizationResult.success("User has permission", true));

    // Act
    boolean result = service.hasPermission(userId, permission);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(permission));
  }

  @Test
  void hasPermission_shouldReturnFalse_whenUserDoesNotHavePermission() {
    // Arrange
    String userId = "user123";
    String permission = "ADMIN";

    when(securityPort.hasPermission(userId, permission))
        .thenReturn(AuthorizationResult.success("User does not have permission", false));

    // Act
    boolean result = service.hasPermission(userId, permission);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(permission));
  }

  @Test
  void hasPermission_shouldReturnFalse_whenCheckFails() {
    // Arrange
    String userId = "user123";
    String permission = "READ_DATA";

    when(securityPort.hasPermission(userId, permission))
        .thenReturn(AuthorizationResult.failure("Check failed", "USER_NOT_FOUND"));

    // Act
    boolean result = service.hasPermission(userId, permission);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(permission));
    verify(logger).warn(contains("Permission check failed"), eq(userId), anyString());
  }

  @Test
  void hasRole_shouldReturnTrue_whenUserHasRole() {
    // Arrange
    String userId = "user123";
    String role = "USER";

    when(securityPort.hasRole(userId, role))
        .thenReturn(AuthorizationResult.success("User has role", true));

    // Act
    boolean result = service.hasRole(userId, role);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(role));
  }

  @Test
  void hasRole_shouldReturnFalse_whenUserDoesNotHaveRole() {
    // Arrange
    String userId = "user123";
    String role = "ADMIN";

    when(securityPort.hasRole(userId, role))
        .thenReturn(AuthorizationResult.success("User does not have role", false));

    // Act
    boolean result = service.hasRole(userId, role);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(role));
  }

  @Test
  void hasRole_shouldReturnFalse_whenCheckFails() {
    // Arrange
    String userId = "user123";
    String role = "USER";

    when(securityPort.hasRole(userId, role))
        .thenReturn(AuthorizationResult.failure("Check failed", "USER_NOT_FOUND"));

    // Act
    boolean result = service.hasRole(userId, role);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Checking if user"), eq(userId), eq(role));
    verify(logger).warn(contains("Role check failed"), eq(userId), anyString());
  }

  @Test
  void getRoles_shouldReturnRoles_whenUserExists() {
    // Arrange
    String userId = "user123";
    Set<String> roles = Set.of("USER", "EDITOR");

    when(securityPort.getRoles(userId)).thenReturn(roles);

    // Act
    Set<String> result = service.getRoles(userId);

    // Assert
    assertEquals(roles, result);
    verify(logger).debug(contains("Getting roles"), eq(userId));
    verify(logger).debug(contains("Retrieved"), eq(roles.size()), eq(userId));
  }

  @Test
  void getRoles_shouldReturnEmptySet_whenExceptionOccurs() {
    // Arrange
    String userId = "user123";

    when(securityPort.getRoles(userId)).thenThrow(new RuntimeException("User not found"));

    // Act
    Set<String> result = service.getRoles(userId);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Getting roles"), eq(userId));
    verify(logger)
        .error(
            contains("Failed to get roles"), eq(userId), anyString(), any(RuntimeException.class));
  }

  @Test
  void getPermissions_shouldReturnPermissions_whenUserExists() {
    // Arrange
    String userId = "user123";
    Set<String> permissions = Set.of("READ", "WRITE");

    when(securityPort.getPermissions(userId)).thenReturn(permissions);

    // Act
    Set<String> result = service.getPermissions(userId);

    // Assert
    assertEquals(permissions, result);
    verify(logger).debug(contains("Getting permissions"), eq(userId));
    verify(logger).debug(contains("Retrieved"), eq(permissions.size()), eq(userId));
  }

  @Test
  void getPermissions_shouldReturnEmptySet_whenExceptionOccurs() {
    // Arrange
    String userId = "user123";

    when(securityPort.getPermissions(userId)).thenThrow(new RuntimeException("User not found"));

    // Act
    Set<String> result = service.getPermissions(userId);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Getting permissions"), eq(userId));
    verify(logger)
        .error(
            contains("Failed to get permissions"),
            eq(userId),
            anyString(),
            any(RuntimeException.class));
  }

  @Test
  void createUser_shouldReturnUser_whenCreationSucceeds() {
    // Arrange
    String username = "newUser";
    String password = "password123";
    User mockUser = mock(User.class);

    when(securityPort.createUser(eq(username), eq(password), any(Map.class)))
        .thenReturn(UserOperationResult.success("User created successfully", mockUser));

    // Act
    Optional<User> result = service.createUser(username, password);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockUser, result.get());
    verify(logger).debug(contains("Creating user"), eq(username));
    verify(logger).info(contains("User created successfully"), eq(username));
  }

  @Test
  void createUser_shouldReturnEmpty_whenCreationFails() {
    // Arrange
    String username = "existingUser";
    String password = "password123";

    when(securityPort.createUser(eq(username), eq(password), any(Map.class)))
        .thenReturn(UserOperationResult.failure("User already exists", "USER_EXISTS"));

    // Act
    Optional<User> result = service.createUser(username, password);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Creating user"), eq(username));
    verify(logger).warn(contains("User creation failed"), eq(username), anyString());
  }

  @Test
  void createUser_withUserData_shouldReturnUser_whenCreationSucceeds() {
    // Arrange
    String username = "newUser";
    String password = "password123";
    Map<String, String> userData =
        Map.of("email", "user@example.com", "firstName", "Test", "lastName", "User");
    User mockUser = mock(User.class);

    when(securityPort.createUser(username, password, userData))
        .thenReturn(UserOperationResult.success("User created successfully", mockUser));

    // Act
    Optional<User> result = service.createUser(username, password, userData);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockUser, result.get());
    verify(logger).debug(contains("Creating user"), eq(username));
    verify(logger).info(contains("User created successfully"), eq(username));
  }

  @Test
  void updatePassword_shouldReturnTrue_whenUpdateSucceeds() {
    // Arrange
    String userId = "user123";
    String currentPassword = "oldPassword";
    String newPassword = "newPassword";

    when(securityPort.updatePassword(userId, currentPassword, newPassword))
        .thenReturn(
            PasswordUpdateResult.success("Password updated successfully", false, Optional.empty()));

    // Act
    boolean result = service.updatePassword(userId, currentPassword, newPassword);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Updating password"), eq(userId));
    verify(logger).info(contains("Password updated successfully"), eq(userId));
  }

  @Test
  void updatePassword_shouldReturnFalse_whenUpdateFails() {
    // Arrange
    String userId = "user123";
    String currentPassword = "wrongPassword";
    String newPassword = "newPassword";

    when(securityPort.updatePassword(userId, currentPassword, newPassword))
        .thenReturn(
            PasswordUpdateResult.failure("Current password is incorrect", "INVALID_CREDENTIALS"));

    // Act
    boolean result = service.updatePassword(userId, currentPassword, newPassword);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Updating password"), eq(userId));
    verify(logger).warn(contains("Password update failed"), eq(userId), anyString());
  }

  @Test
  void resetPassword_shouldReturnTrue_whenResetSucceeds() {
    // Arrange
    String resetToken = "validResetToken";
    String newPassword = "newPassword";

    when(securityPort.resetPassword(resetToken, newPassword))
        .thenReturn(
            PasswordUpdateResult.success("Password reset successfully", false, Optional.empty()));

    // Act
    boolean result = service.resetPassword(resetToken, newPassword);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Resetting password"));
    verify(logger).info(contains("Password reset successfully"));
  }

  @Test
  void resetPassword_shouldReturnFalse_whenResetFails() {
    // Arrange
    String resetToken = "invalidResetToken";
    String newPassword = "newPassword";

    when(securityPort.resetPassword(resetToken, newPassword))
        .thenReturn(PasswordUpdateResult.failure("Invalid reset token", "INVALID_TOKEN"));

    // Act
    boolean result = service.resetPassword(resetToken, newPassword);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Resetting password"));
    verify(logger).warn(contains("Password reset failed"), anyString());
  }

  @Test
  void generatePasswordResetToken_shouldReturnToken_whenGenerationSucceeds() {
    // Arrange
    String username = "testUser";
    String resetToken = "resetToken123";
    Instant expiresAt = Instant.now().plus(Duration.ofHours(24));

    when(securityPort.generatePasswordResetToken(username))
        .thenReturn(TokenGenerationResult.success("Token generated", resetToken, expiresAt));

    // Act
    Optional<String> result = service.generatePasswordResetToken(username);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(resetToken, result.get());
    verify(logger).debug(contains("Generating password reset token"), eq(username));
    verify(logger).info(contains("Password reset token generated"), eq(username));
  }

  @Test
  void generatePasswordResetToken_shouldReturnEmpty_whenGenerationFails() {
    // Arrange
    String username = "nonExistentUser";

    when(securityPort.generatePasswordResetToken(username))
        .thenReturn(TokenGenerationResult.failure("User not found", "USER_NOT_FOUND"));

    // Act
    Optional<String> result = service.generatePasswordResetToken(username);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Generating password reset token"), eq(username));
    verify(logger)
        .warn(contains("Password reset token generation failed"), eq(username), anyString());
  }

  @Test
  void encrypt_shouldReturnEncryptedData_whenEncryptionSucceeds() {
    // Arrange
    byte[] data = "sensitive data".getBytes();
    byte[] encryptedData = "encrypted".getBytes();

    when(securityPort.encrypt(eq(data), any()))
        .thenReturn(
            EncryptionResult.success("Data encrypted successfully", encryptedData, "defaultKey"));

    // Act
    Optional<byte[]> result = service.encrypt(data);

    // Assert
    assertTrue(result.isPresent());
    assertArrayEquals(encryptedData, result.get());
    verify(logger).debug(contains("Encrypting data"));
    verify(logger).debug(contains("Data encrypted successfully"));
  }

  @Test
  void encrypt_shouldReturnEmpty_whenEncryptionFails() {
    // Arrange
    byte[] data = "sensitive data".getBytes();

    when(securityPort.encrypt(eq(data), any()))
        .thenReturn(EncryptionResult.failure("Encryption failed", "ENCRYPTION_ERROR"));

    // Act
    Optional<byte[]> result = service.encrypt(data);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Encrypting data"));
    verify(logger).warn(contains("Encryption failed"), anyString());
  }

  @Test
  void encrypt_withKey_shouldReturnEncryptedData_whenEncryptionSucceeds() {
    // Arrange
    byte[] data = "sensitive data".getBytes();
    byte[] encryptedData = "encrypted".getBytes();
    String keyId = "specialKey";

    when(securityPort.encrypt(data, Optional.of(keyId)))
        .thenReturn(EncryptionResult.success("Data encrypted successfully", encryptedData, keyId));

    // Act
    Optional<byte[]> result = service.encrypt(data, Optional.of(keyId));

    // Assert
    assertTrue(result.isPresent());
    assertArrayEquals(encryptedData, result.get());
    verify(logger).debug(contains("Encrypting data"));
    verify(logger).debug(contains("Data encrypted successfully"));
  }

  @Test
  void decrypt_shouldReturnDecryptedData_whenDecryptionSucceeds() {
    // Arrange
    byte[] encryptedData = "encrypted".getBytes();
    byte[] decryptedData = "sensitive data".getBytes();

    when(securityPort.decrypt(eq(encryptedData), any()))
        .thenReturn(
            DecryptionResult.success("Data decrypted successfully", decryptedData, "defaultKey"));

    // Act
    Optional<byte[]> result = service.decrypt(encryptedData);

    // Assert
    assertTrue(result.isPresent());
    assertArrayEquals(decryptedData, result.get());
    verify(logger).debug(contains("Decrypting data"));
    verify(logger).debug(contains("Data decrypted successfully"));
  }

  @Test
  void decrypt_shouldReturnEmpty_whenDecryptionFails() {
    // Arrange
    byte[] encryptedData = "encrypted".getBytes();

    when(securityPort.decrypt(eq(encryptedData), any()))
        .thenReturn(DecryptionResult.failure("Decryption failed", "DECRYPTION_ERROR"));

    // Act
    Optional<byte[]> result = service.decrypt(encryptedData);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Decrypting data"));
    verify(logger).warn(contains("Decryption failed"), anyString());
  }

  @Test
  void decrypt_withKey_shouldReturnDecryptedData_whenDecryptionSucceeds() {
    // Arrange
    byte[] encryptedData = "encrypted".getBytes();
    byte[] decryptedData = "sensitive data".getBytes();
    String keyId = "specialKey";

    when(securityPort.decrypt(encryptedData, Optional.of(keyId)))
        .thenReturn(DecryptionResult.success("Data decrypted successfully", decryptedData, keyId));

    // Act
    Optional<byte[]> result = service.decrypt(encryptedData, Optional.of(keyId));

    // Assert
    assertTrue(result.isPresent());
    assertArrayEquals(decryptedData, result.get());
    verify(logger).debug(contains("Decrypting data"));
    verify(logger).debug(contains("Data decrypted successfully"));
  }

  @Test
  void generateSecureToken_shouldReturnToken() {
    // Arrange
    int byteLength = 16;
    String expectedToken = "secureTokenValue";

    when(securityPort.generateSecureToken(byteLength)).thenReturn(expectedToken);

    // Act
    String result = service.generateSecureToken(byteLength);

    // Assert
    assertEquals(expectedToken, result);
    verify(logger).debug(contains("Generating secure token"), eq(byteLength));
  }

  @Test
  void getAuditLogs_shouldReturnLogs() {
    // Arrange
    Optional<String> userId = Optional.of("user123");
    Optional<Collection<String>> eventTypes = Optional.of(List.of("LOGIN", "LOGOUT"));
    Optional<Instant> startTime = Optional.of(Instant.now().minus(Duration.ofDays(1)));
    Optional<Instant> endTime = Optional.of(Instant.now());
    Optional<Integer> limit = Optional.of(100);

    List<AuditLogEntry> mockLogs = List.of(mock(AuditLogEntry.class), mock(AuditLogEntry.class));

    when(securityPort.getAuditLogs(userId, eventTypes, startTime, endTime, limit))
        .thenReturn(mockLogs);

    // Act
    Collection<AuditLogEntry> result =
        service.getAuditLogs(userId, eventTypes, startTime, endTime, limit);

    // Assert
    assertEquals(mockLogs, result);
    verify(logger)
        .debug(
            contains("Getting audit logs"),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString());
    verify(logger).debug(contains("Retrieved"), eq(mockLogs.size()));
  }

  @Test
  void getAuditLogs_shouldReturnEmptyList_whenExceptionOccurs() {
    // Arrange
    Optional<String> userId = Optional.of("user123");
    Optional<Collection<String>> eventTypes = Optional.empty();
    Optional<Instant> startTime = Optional.empty();
    Optional<Instant> endTime = Optional.empty();
    Optional<Integer> limit = Optional.empty();

    when(securityPort.getAuditLogs(userId, eventTypes, startTime, endTime, limit))
        .thenThrow(new RuntimeException("Database error"));

    // Act
    Collection<AuditLogEntry> result =
        service.getAuditLogs(userId, eventTypes, startTime, endTime, limit);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger)
        .debug(
            contains("Getting audit logs"),
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString());
    verify(logger)
        .error(contains("Failed to get audit logs"), anyString(), any(RuntimeException.class));
  }

  @Test
  void logSecurityEvent_shouldReturnTrue_whenLoggingSucceeds() {
    // Arrange
    String eventType = "LOGIN";
    String userId = "user123";
    String action = "USER_LOGIN";
    String result = "SUCCESS";

    // We need to mock the builder and subsequent calls
    AuditLogEntry mockEntry = mock(AuditLogEntry.class);

    // Mock necessary operations to make the test work
    when(securityPort.generateSecureToken(anyInt())).thenReturn("logId123");
    when(securityPort.logSecurityEvent(any(AuditLogEntry.class)))
        .thenReturn(OperationResult.success("Event logged successfully"));

    // Act
    boolean logResult = service.logSecurityEvent(eventType, userId, action, result);

    // Assert
    assertTrue(logResult);
    verify(logger)
        .debug(
            contains("Logging security event"), eq(eventType), eq(userId), eq(action), eq(result));
    verify(securityPort).logSecurityEvent(any(AuditLogEntry.class));
  }

  @Test
  void logSecurityEvent_shouldReturnFalse_whenLoggingFails() {
    // Arrange
    String eventType = "LOGIN";
    String userId = "user123";
    String action = "USER_LOGIN";
    String result = "SUCCESS";

    // We need to mock the builder and subsequent calls
    AuditLogEntry mockEntry = mock(AuditLogEntry.class);

    // Mock necessary operations to make the test work
    when(securityPort.generateSecureToken(anyInt())).thenReturn("logId123");
    when(securityPort.logSecurityEvent(any(AuditLogEntry.class)))
        .thenReturn(OperationResult.failure("Event logging failed", "LOGGING_ERROR"));

    // Act
    boolean logResult = service.logSecurityEvent(eventType, userId, action, result);

    // Assert
    assertFalse(logResult);
    verify(logger)
        .debug(
            contains("Logging security event"), eq(eventType), eq(userId), eq(action), eq(result));
    verify(logger).warn(contains("Failed to log security event"), anyString());
  }

  @Test
  void logSecurityEvent_withDetails_shouldReturnTrue_whenLoggingSucceeds() {
    // Arrange
    String eventType = "LOGIN";
    String userId = "user123";
    String action = "USER_LOGIN";
    String result = "SUCCESS";
    Map<String, String> details = Map.of("ip", "192.168.1.1", "browser", "Chrome");

    // Mock necessary operations
    when(securityPort.generateSecureToken(anyInt())).thenReturn("logId123");
    when(securityPort.logSecurityEvent(any(AuditLogEntry.class)))
        .thenReturn(OperationResult.success("Event logged successfully"));

    // Act
    boolean logResult = service.logSecurityEvent(eventType, userId, action, result, details);

    // Assert
    assertTrue(logResult);
    verify(logger)
        .debug(
            contains("Logging security event with details"),
            eq(eventType),
            eq(userId),
            eq(action),
            eq(result));
    verify(securityPort).logSecurityEvent(any(AuditLogEntry.class));
  }

  @Test
  void authenticateAsync_shouldCompleteSuccessfully_whenCredentialsAreValid()
      throws ExecutionException, InterruptedException {
    // Arrange
    String username = "testUser";
    String password = "password123";
    SecurityToken mockToken = mock(SecurityToken.class);
    User mockUser = mock(User.class);

    when(securityPort.authenticate(username, password))
        .thenReturn(AuthenticationResult.success("Authentication successful", mockToken, mockUser));

    // Act
    CompletableFuture<SecurityToken> future = service.authenticateAsync(username, password);
    SecurityToken result =
        future.get(); // This will throw an exception if the future completes exceptionally

    // Assert
    assertSame(mockToken, result);
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).debug(contains("Authentication successful"), eq(username));
  }

  @Test
  void authenticateAsync_shouldCompleteExceptionally_whenCredentialsAreInvalid() {
    // Arrange
    String username = "testUser";
    String password = "wrongPassword";

    when(securityPort.authenticate(username, password))
        .thenReturn(AuthenticationResult.failure("Invalid credentials", "INVALID_CREDENTIALS"));

    // Act
    CompletableFuture<SecurityToken> future = service.authenticateAsync(username, password);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof SecurityException);
    verify(logger).debug(contains("Authenticating user"), eq(username));
    verify(logger).warn(contains("Authentication failed"), eq(username), anyString());
  }

  @Test
  void validateTokenAsync_shouldCompleteSuccessfully_whenTokenIsValid()
      throws ExecutionException, InterruptedException {
    // Arrange
    String token = "validToken";
    SecurityToken mockToken = mock(SecurityToken.class);
    User mockUser = mock(User.class);

    when(securityPort.validateToken(token))
        .thenReturn(TokenValidationResult.success("Token is valid", mockUser, mockToken));

    // Act
    CompletableFuture<User> future = service.validateTokenAsync(token);
    User result =
        future.get(); // This will throw an exception if the future completes exceptionally

    // Assert
    assertSame(mockUser, result);
    verify(logger).debug(contains("Validating security token"));
    verify(logger).debug(contains("Token validation successful"));
  }

  @Test
  void validateTokenAsync_shouldCompleteExceptionally_whenTokenIsInvalid() {
    // Arrange
    String token = "invalidToken";

    when(securityPort.validateToken(token))
        .thenReturn(TokenValidationResult.failure("Token is invalid", "INVALID_TOKEN"));

    // Act
    CompletableFuture<User> future = service.validateTokenAsync(token);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof SecurityException);
    verify(logger).debug(contains("Validating security token"));
    verify(logger).warn(contains("Token validation failed"), anyString());
  }
}
