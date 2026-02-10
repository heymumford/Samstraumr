/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.security.SecurityPort;

/**
 * Service layer for security operations.
 *
 * <p>This service provides a simplified API for security operations by encapsulating the
 * SecurityPort interface and providing methods that handle error logging and conversion between
 * different result types.
 */
public class SecurityService {
  private final SecurityPort securityPort;
  private final LoggerPort logger;

  /**
   * Constructs a SecurityService with the given SecurityPort and LoggerPort.
   *
   * @param securityPort The SecurityPort to use
   * @param logger The LoggerPort to use for logging
   */
  public SecurityService(SecurityPort securityPort, LoggerPort logger) {
    this.securityPort = securityPort;
    this.logger = logger;
  }

  /**
   * Authenticates a user with the given credentials.
   *
   * @param username The username
   * @param password The password
   * @return An Optional containing the security token if authentication was successful, or empty if
   *     unsuccessful
   */
  public Optional<SecurityPort.SecurityToken> authenticate(String username, String password) {
    logger.debug("Authenticating user: {}", username);
    SecurityPort.AuthenticationResult result = securityPort.authenticate(username, password);

    if (!result.isSuccessful()) {
      logger.warn("Authentication failed for user {}: {}", username, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Authentication successful for user: {}", username);
    return result.getToken();
  }

  /**
   * Authenticates a user with additional factors.
   *
   * @param username The username
   * @param password The password
   * @param additionalFactors Additional authentication factors
   * @return An Optional containing the security token if authentication was successful, or empty if
   *     unsuccessful
   */
  public Optional<SecurityPort.SecurityToken> authenticateWithFactors(
      String username, String password, Map<String, String> additionalFactors) {
    logger.debug("Authenticating user {} with additional factors", username);
    SecurityPort.AuthenticationResult result =
        securityPort.authenticate(username, password, additionalFactors);

    if (!result.isSuccessful()) {
      logger.warn(
          "Authentication with factors failed for user {}: {}", username, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Authentication with factors successful for user: {}", username);
    return result.getToken();
  }

  /**
   * Validates a security token.
   *
   * @param token The token to validate
   * @return An Optional containing the user associated with the token if validation was successful,
   *     or empty if unsuccessful
   */
  public Optional<SecurityPort.User> validateToken(String token) {
    logger.debug("Validating security token");
    SecurityPort.TokenValidationResult result = securityPort.validateToken(token);

    if (!result.isSuccessful()) {
      logger.warn("Token validation failed: {}", result.getMessage());
      return Optional.empty();
    }

    logger.debug("Token validation successful");
    return result.getUser();
  }

  /**
   * Refreshes a security token.
   *
   * @param refreshToken The refresh token
   * @return An Optional containing the new security token if refresh was successful, or empty if
   *     unsuccessful
   */
  public Optional<SecurityPort.SecurityToken> refreshToken(String refreshToken) {
    logger.debug("Refreshing token");
    SecurityPort.TokenRefreshResult result = securityPort.refreshToken(refreshToken);

    if (!result.isSuccessful()) {
      logger.warn("Token refresh failed: {}", result.getMessage());
      return Optional.empty();
    }

    logger.debug("Token refresh successful");
    return result.getNewToken();
  }

  /**
   * Revokes a security token.
   *
   * @param token The token to revoke
   * @return True if the token was successfully revoked, false otherwise
   */
  public boolean revokeToken(String token) {
    logger.debug("Revoking token");
    SecurityPort.OperationResult result = securityPort.revokeToken(token);

    if (!result.isSuccessful()) {
      logger.warn("Token revocation failed: {}", result.getMessage());
      return false;
    }

    logger.debug("Token revocation successful");
    return true;
  }

  /**
   * Checks if a user has a specific permission.
   *
   * @param userId The user ID
   * @param permission The permission to check
   * @return True if the user has the permission, false otherwise
   */
  public boolean hasPermission(String userId, String permission) {
    logger.debug("Checking if user {} has permission: {}", userId, permission);
    SecurityPort.AuthorizationResult result = securityPort.hasPermission(userId, permission);

    if (!result.isSuccessful()) {
      logger.warn("Permission check failed for user {}: {}", userId, result.getMessage());
      return false;
    }

    return result.isAuthorized();
  }

  /**
   * Checks if a user has a specific role.
   *
   * @param userId The user ID
   * @param role The role to check
   * @return True if the user has the role, false otherwise
   */
  public boolean hasRole(String userId, String role) {
    logger.debug("Checking if user {} has role: {}", userId, role);
    SecurityPort.AuthorizationResult result = securityPort.hasRole(userId, role);

    if (!result.isSuccessful()) {
      logger.warn("Role check failed for user {}: {}", userId, result.getMessage());
      return false;
    }

    return result.isAuthorized();
  }

  /**
   * Gets all roles for a user.
   *
   * @param userId The user ID
   * @return A set of roles assigned to the user, or an empty set if an error occurred
   */
  public Set<String> getRoles(String userId) {
    logger.debug("Getting roles for user: {}", userId);
    try {
      Set<String> roles = securityPort.getRoles(userId);
      logger.debug("Retrieved {} roles for user {}", roles.size(), userId);
      return roles;
    } catch (Exception e) {
      logger.error("Failed to get roles for user {}: {}", userId, e.getMessage(), e);
      return Collections.emptySet();
    }
  }

  /**
   * Gets all permissions for a user.
   *
   * @param userId The user ID
   * @return A set of permissions granted to the user, or an empty set if an error occurred
   */
  public Set<String> getPermissions(String userId) {
    logger.debug("Getting permissions for user: {}", userId);
    try {
      Set<String> permissions = securityPort.getPermissions(userId);
      logger.debug("Retrieved {} permissions for user {}", permissions.size(), userId);
      return permissions;
    } catch (Exception e) {
      logger.error("Failed to get permissions for user {}: {}", userId, e.getMessage(), e);
      return Collections.emptySet();
    }
  }

  /**
   * Creates a new user.
   *
   * @param username The username
   * @param password The password
   * @return An Optional containing the created user if creation was successful, or empty if
   *     unsuccessful
   */
  public Optional<SecurityPort.User> createUser(String username, String password) {
    return createUser(username, password, Collections.emptyMap());
  }

  /**
   * Creates a new user with additional data.
   *
   * @param username The username
   * @param password The password
   * @param userData Additional user data
   * @return An Optional containing the created user if creation was successful, or empty if
   *     unsuccessful
   */
  public Optional<SecurityPort.User> createUser(
      String username, String password, Map<String, String> userData) {
    logger.debug("Creating user: {}", username);
    SecurityPort.UserOperationResult result = securityPort.createUser(username, password, userData);

    if (!result.isSuccessful()) {
      logger.warn("User creation failed for {}: {}", username, result.getMessage());
      return Optional.empty();
    }

    logger.info("User created successfully: {}", username);
    return result.getUser();
  }

  /**
   * Updates a user's password.
   *
   * @param userId The user ID
   * @param currentPassword The current password
   * @param newPassword The new password
   * @return True if the password was successfully updated, false otherwise
   */
  public boolean updatePassword(String userId, String currentPassword, String newPassword) {
    logger.debug("Updating password for user: {}", userId);
    SecurityPort.PasswordUpdateResult result =
        securityPort.updatePassword(userId, currentPassword, newPassword);

    if (!result.isSuccessful()) {
      logger.warn("Password update failed for user {}: {}", userId, result.getMessage());
      return false;
    }

    logger.info("Password updated successfully for user: {}", userId);
    return true;
  }

  /**
   * Resets a user's password using a reset token.
   *
   * @param resetToken The reset token
   * @param newPassword The new password
   * @return True if the password was successfully reset, false otherwise
   */
  public boolean resetPassword(String resetToken, String newPassword) {
    logger.debug("Resetting password with token");
    SecurityPort.PasswordUpdateResult result = securityPort.resetPassword(resetToken, newPassword);

    if (!result.isSuccessful()) {
      logger.warn("Password reset failed: {}", result.getMessage());
      return false;
    }

    logger.info("Password reset successfully with token");
    return true;
  }

  /**
   * Generates a password reset token for a user.
   *
   * @param username The username
   * @return An Optional containing the reset token if generation was successful, or empty if
   *     unsuccessful
   */
  public Optional<String> generatePasswordResetToken(String username) {
    logger.debug("Generating password reset token for user: {}", username);
    SecurityPort.TokenGenerationResult result = securityPort.generatePasswordResetToken(username);

    if (!result.isSuccessful()) {
      logger.warn(
          "Password reset token generation failed for user {}: {}", username, result.getMessage());
      return Optional.empty();
    }

    logger.info("Password reset token generated for user: {}", username);
    return result.getToken();
  }

  /**
   * Encrypts data.
   *
   * @param data The data to encrypt
   * @return An Optional containing the encrypted data if encryption was successful, or empty if
   *     unsuccessful
   */
  public Optional<byte[]> encrypt(byte[] data) {
    return encrypt(data, Optional.empty());
  }

  /**
   * Encrypts data using a specific key.
   *
   * @param data The data to encrypt
   * @param keyIdentifier The key identifier
   * @return An Optional containing the encrypted data if encryption was successful, or empty if
   *     unsuccessful
   */
  public Optional<byte[]> encrypt(byte[] data, Optional<String> keyIdentifier) {
    logger.debug("Encrypting data");
    SecurityPort.EncryptionResult result = securityPort.encrypt(data, keyIdentifier);

    if (!result.isSuccessful()) {
      logger.warn("Encryption failed: {}", result.getMessage());
      return Optional.empty();
    }

    logger.debug("Data encrypted successfully");
    return result.getEncryptedData();
  }

  /**
   * Decrypts data.
   *
   * @param encryptedData The data to decrypt
   * @return An Optional containing the decrypted data if decryption was successful, or empty if
   *     unsuccessful
   */
  public Optional<byte[]> decrypt(byte[] encryptedData) {
    return decrypt(encryptedData, Optional.empty());
  }

  /**
   * Decrypts data using a specific key.
   *
   * @param encryptedData The data to decrypt
   * @param keyIdentifier The key identifier
   * @return An Optional containing the decrypted data if decryption was successful, or empty if
   *     unsuccessful
   */
  public Optional<byte[]> decrypt(byte[] encryptedData, Optional<String> keyIdentifier) {
    logger.debug("Decrypting data");
    SecurityPort.DecryptionResult result = securityPort.decrypt(encryptedData, keyIdentifier);

    if (!result.isSuccessful()) {
      logger.warn("Decryption failed: {}", result.getMessage());
      return Optional.empty();
    }

    logger.debug("Data decrypted successfully");
    return result.getDecryptedData();
  }

  /**
   * Generates a secure random token.
   *
   * @param byteLength The length of the token in bytes
   * @return The generated token
   */
  public String generateSecureToken(int byteLength) {
    logger.debug("Generating secure token of length: {}", byteLength);
    return securityPort.generateSecureToken(byteLength);
  }

  /**
   * Gets audit logs for security events.
   *
   * @param userId Optional user ID to filter logs for
   * @param eventTypes Optional collection of event types to filter for
   * @param startTime Optional start time for the logs
   * @param endTime Optional end time for the logs
   * @param limit Optional maximum number of logs to return
   * @return A collection of audit log entries
   */
  public Collection<SecurityPort.AuditLogEntry> getAuditLogs(
      Optional<String> userId,
      Optional<Collection<String>> eventTypes,
      Optional<Instant> startTime,
      Optional<Instant> endTime,
      Optional<Integer> limit) {

    logger.debug(
        "Getting audit logs with filters: userId={}, eventTypes={}, startTime={}, endTime={}, limit={}",
        userId.orElse("any"),
        eventTypes.map(types -> String.join(",", types)).orElse("any"),
        startTime.map(Instant::toString).orElse("any"),
        endTime.map(Instant::toString).orElse("any"),
        limit.map(Object::toString).orElse("unlimited"));

    try {
      Collection<SecurityPort.AuditLogEntry> logs =
          securityPort.getAuditLogs(userId, eventTypes, startTime, endTime, limit);
      logger.debug("Retrieved {} audit log entries", logs.size());
      return logs;
    } catch (Exception e) {
      logger.error("Failed to get audit logs: {}", e.getMessage(), e);
      return Collections.emptyList();
    }
  }

  /**
   * Logs a security event.
   *
   * @param eventType The event type
   * @param userId The user ID
   * @param action The action
   * @param result The result
   * @return True if the event was successfully logged, false otherwise
   */
  public boolean logSecurityEvent(String eventType, String userId, String action, String result) {
    logger.debug(
        "Logging security event: type={}, userId={}, action={}, result={}",
        eventType,
        userId,
        action,
        result);

    SecurityPort.AuditLogEntry.Builder builder =
        createAuditLogEntryBuilder()
            .eventType(eventType)
            .userId(userId)
            .action(action)
            .result(result)
            .timestamp(Instant.now());

    SecurityPort.AuditLogEntry entry = builder.build();
    SecurityPort.OperationResult opResult = securityPort.logSecurityEvent(entry);

    if (!opResult.isSuccessful()) {
      logger.warn("Failed to log security event: {}", opResult.getMessage());
      return false;
    }

    return true;
  }

  /**
   * Logs a security event with additional details.
   *
   * @param eventType The event type
   * @param userId The user ID
   * @param action The action
   * @param result The result
   * @param details Additional details
   * @return True if the event was successfully logged, false otherwise
   */
  public boolean logSecurityEvent(
      String eventType, String userId, String action, String result, Map<String, String> details) {
    logger.debug(
        "Logging security event with details: type={}, userId={}, action={}, result={}",
        eventType,
        userId,
        action,
        result);

    SecurityPort.AuditLogEntry.Builder builder =
        createAuditLogEntryBuilder()
            .eventType(eventType)
            .userId(userId)
            .action(action)
            .result(result)
            .timestamp(Instant.now());

    for (Map.Entry<String, String> detail : details.entrySet()) {
      builder.addDetail(detail.getKey(), detail.getValue());
    }

    SecurityPort.AuditLogEntry entry = builder.build();
    SecurityPort.OperationResult opResult = securityPort.logSecurityEvent(entry);

    if (!opResult.isSuccessful()) {
      logger.warn("Failed to log security event: {}", opResult.getMessage());
      return false;
    }

    return true;
  }

  /**
   * Creates a builder for audit log entries.
   *
   * @return The builder
   */
  private SecurityPort.AuditLogEntry.Builder createAuditLogEntryBuilder() {
    // This method provides a hook point for customizing the builder creation
    // in subclasses or adding default values
    return new SecurityPort.AuditLogEntry.Builder() {
      private String eventType;
      private String userId;
      private String username;
      private String ipAddress;
      private String resource;
      private String action;
      private String result;
      private Instant timestamp;
      private final Map<String, String> details = new HashMap<>();

      @Override
      public SecurityPort.AuditLogEntry.Builder eventType(String eventType) {
        this.eventType = eventType;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder userId(String userId) {
        this.userId = userId;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder username(String username) {
        this.username = username;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder resource(String resource) {
        this.resource = resource;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder action(String action) {
        this.action = action;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder result(String result) {
        this.result = result;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry.Builder addDetail(String key, String value) {
        this.details.put(key, value);
        return this;
      }

      @Override
      public SecurityPort.AuditLogEntry build() {
        // Create a unique ID for the log entry
        final String id = securityPort.generateSecureToken(8);

        return new SecurityPort.AuditLogEntry() {
          @Override
          public String getId() {
            return id;
          }

          @Override
          public String getEventType() {
            return eventType;
          }

          @Override
          public Optional<String> getUserId() {
            return Optional.ofNullable(userId);
          }

          @Override
          public Optional<String> getUsername() {
            return Optional.ofNullable(username);
          }

          @Override
          public Optional<String> getIpAddress() {
            return Optional.ofNullable(ipAddress);
          }

          @Override
          public Optional<String> getResource() {
            return Optional.ofNullable(resource);
          }

          @Override
          public String getAction() {
            return action;
          }

          @Override
          public String getResult() {
            return result;
          }

          @Override
          public Instant getTimestamp() {
            return timestamp;
          }

          @Override
          public Map<String, String> getDetails() {
            return Collections.unmodifiableMap(details);
          }
        };
      }
    };
  }

  /**
   * Performs authentication asynchronously.
   *
   * @param username The username
   * @param password The password
   * @return A CompletableFuture that will contain the security token if authentication was
   *     successful, or will complete exceptionally if unsuccessful
   */
  public CompletableFuture<SecurityPort.SecurityToken> authenticateAsync(
      String username, String password) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<SecurityPort.SecurityToken> token = authenticate(username, password);
          return token.orElseThrow(
              () -> new SecurityException("Authentication failed for user: " + username));
        });
  }

  /**
   * Validates a token asynchronously.
   *
   * @param token The token to validate
   * @return A CompletableFuture that will contain the user if validation was successful, or will
   *     complete exceptionally if unsuccessful
   */
  public CompletableFuture<SecurityPort.User> validateTokenAsync(String token) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<SecurityPort.User> user = validateToken(token);
          return user.orElseThrow(() -> new SecurityException("Token validation failed"));
        });
  }
}
