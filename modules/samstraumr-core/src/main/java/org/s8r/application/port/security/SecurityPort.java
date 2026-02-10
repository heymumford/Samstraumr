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

package org.s8r.application.port.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Port interface for security operations.
 *
 * <p>This interface defines the contract for security operations in the application, including
 * authentication, authorization, and secure credential management. Following the ports and adapters
 * pattern, this is an output port in the application layer, which will be implemented by adapters
 * in the infrastructure layer.
 */
public interface SecurityPort {

  /**
   * Authenticates a user with the given credentials.
   *
   * @param username The username
   * @param password The password
   * @return The authentication result
   */
  AuthenticationResult authenticate(String username, String password);

  /**
   * Authenticates a user with the given credentials and optional additional factors.
   *
   * @param username The username
   * @param password The password
   * @param additionalFactors Additional authentication factors (e.g., OTP code)
   * @return The authentication result
   */
  AuthenticationResult authenticate(
      String username, String password, Map<String, String> additionalFactors);

  /**
   * Validates a security token.
   *
   * @param token The token to validate
   * @return The validation result containing user information if valid
   */
  TokenValidationResult validateToken(String token);

  /**
   * Revokes a security token.
   *
   * @param token The token to revoke
   * @return The revocation result
   */
  OperationResult revokeToken(String token);

  /**
   * Refreshes a security token.
   *
   * @param refreshToken The refresh token
   * @return The refresh result containing a new token if successful
   */
  TokenRefreshResult refreshToken(String refreshToken);

  /**
   * Checks if a user has a specific permission.
   *
   * @param userId The user ID
   * @param permission The permission to check
   * @return The authorization result
   */
  AuthorizationResult hasPermission(String userId, String permission);

  /**
   * Checks if a user has a specific role.
   *
   * @param userId The user ID
   * @param role The role to check
   * @return The authorization result
   */
  AuthorizationResult hasRole(String userId, String role);

  /**
   * Gets all roles for a user.
   *
   * @param userId The user ID
   * @return A set of roles assigned to the user
   */
  Set<String> getRoles(String userId);

  /**
   * Gets all permissions for a user.
   *
   * @param userId The user ID
   * @return A set of permissions granted to the user
   */
  Set<String> getPermissions(String userId);

  /**
   * Creates a new user.
   *
   * @param username The username
   * @param password The password
   * @param userData Additional user data
   * @return The user creation result
   */
  UserOperationResult createUser(String username, String password, Map<String, String> userData);

  /**
   * Updates a user's password.
   *
   * @param userId The user ID
   * @param currentPassword The current password
   * @param newPassword The new password
   * @return The password update result
   */
  PasswordUpdateResult updatePassword(String userId, String currentPassword, String newPassword);

  /**
   * Generates a password reset token.
   *
   * @param username The username
   * @return The token generation result
   */
  TokenGenerationResult generatePasswordResetToken(String username);

  /**
   * Resets a password using a reset token.
   *
   * @param resetToken The reset token
   * @param newPassword The new password
   * @return The password reset result
   */
  PasswordUpdateResult resetPassword(String resetToken, String newPassword);

  /**
   * Securely hashes a password.
   *
   * @param password The password to hash
   * @return The hashed password
   */
  String hashPassword(String password);

  /**
   * Verifies a password against a hash.
   *
   * @param password The password to verify
   * @param hash The hash to verify against
   * @return True if the password matches the hash, false otherwise
   */
  boolean verifyPassword(String password, String hash);

  /**
   * Encrypts data.
   *
   * @param data The data to encrypt
   * @param keyIdentifier An optional identifier for the encryption key to use
   * @return The encryption result
   */
  EncryptionResult encrypt(byte[] data, Optional<String> keyIdentifier);

  /**
   * Decrypts data.
   *
   * @param encryptedData The data to decrypt
   * @param keyIdentifier An optional identifier for the decryption key to use
   * @return The decryption result
   */
  DecryptionResult decrypt(byte[] encryptedData, Optional<String> keyIdentifier);

  /**
   * Generates a secure random token.
   *
   * @param byteLength The length of the token in bytes
   * @return The generated token as a string
   */
  String generateSecureToken(int byteLength);

  /**
   * Gets audit logs for security events.
   *
   * @param userId Optional user ID to filter logs for
   * @param eventTypes Optional collection of event types to filter for
   * @param startTime Optional start time for the logs
   * @param endTime Optional end time for the logs
   * @param limit Optional maximum number of logs to return
   * @return A list of audit log entries
   */
  Collection<AuditLogEntry> getAuditLogs(
      Optional<String> userId,
      Optional<Collection<String>> eventTypes,
      Optional<Instant> startTime,
      Optional<Instant> endTime,
      Optional<Integer> limit);

  /**
   * Records a security event in the audit log.
   *
   * @param event The event to record
   * @return The operation result
   */
  OperationResult logSecurityEvent(AuditLogEntry event);

  // Nested interfaces and classes

  /** Represents a user in the security system. */
  interface User {
    /**
     * Gets the user ID.
     *
     * @return The user ID
     */
    String getId();

    /**
     * Gets the username.
     *
     * @return The username
     */
    String getUsername();

    /**
     * Gets whether the user is enabled.
     *
     * @return True if the user is enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Gets whether the user's account is locked.
     *
     * @return True if the account is locked, false otherwise
     */
    boolean isLocked();

    /**
     * Gets whether the user's password has expired.
     *
     * @return True if the password has expired, false otherwise
     */
    boolean isPasswordExpired();

    /**
     * Gets the user's account creation time.
     *
     * @return The account creation time
     */
    Instant getCreatedAt();

    /**
     * Gets the user's last login time.
     *
     * @return The last login time, if available
     */
    Optional<Instant> getLastLoginAt();

    /**
     * Gets additional user attributes.
     *
     * @return A map of attribute names to values
     */
    Map<String, String> getAttributes();
  }

  /** Represents a security token. */
  interface SecurityToken {
    /**
     * Gets the token value.
     *
     * @return The token value
     */
    String getValue();

    /**
     * Gets the token type.
     *
     * @return The token type
     */
    String getType();

    /**
     * Gets the user ID associated with the token.
     *
     * @return The user ID
     */
    String getUserId();

    /**
     * Gets the token expiration time.
     *
     * @return The expiration time
     */
    Instant getExpiresAt();

    /**
     * Gets the token creation time.
     *
     * @return The creation time
     */
    Instant getCreatedAt();

    /**
     * Gets whether the token is expired.
     *
     * @return True if the token is expired, false otherwise
     */
    default boolean isExpired() {
      return Instant.now().isAfter(getExpiresAt());
    }

    /**
     * Gets the roles associated with the token.
     *
     * @return A set of roles
     */
    Set<String> getRoles();

    /**
     * Gets the permissions associated with the token.
     *
     * @return A set of permissions
     */
    Set<String> getPermissions();
  }

  /** Represents an audit log entry for a security event. */
  interface AuditLogEntry {
    /**
     * Gets the log entry ID.
     *
     * @return The log entry ID
     */
    String getId();

    /**
     * Gets the event type.
     *
     * @return The event type
     */
    String getEventType();

    /**
     * Gets the user ID associated with the event, if any.
     *
     * @return The user ID, if available
     */
    Optional<String> getUserId();

    /**
     * Gets the username associated with the event, if any.
     *
     * @return The username, if available
     */
    Optional<String> getUsername();

    /**
     * Gets the IP address associated with the event, if any.
     *
     * @return The IP address, if available
     */
    Optional<String> getIpAddress();

    /**
     * Gets the resource affected by the event, if any.
     *
     * @return The resource, if available
     */
    Optional<String> getResource();

    /**
     * Gets the action performed.
     *
     * @return The action
     */
    String getAction();

    /**
     * Gets the result of the action.
     *
     * @return The result
     */
    String getResult();

    /**
     * Gets the time the event occurred.
     *
     * @return The event time
     */
    Instant getTimestamp();

    /**
     * Gets additional details about the event.
     *
     * @return A map of detail names to values
     */
    Map<String, String> getDetails();

    /** Builder for creating AuditLogEntry instances. */
    interface Builder {
      /**
       * Sets the event type.
       *
       * @param eventType The event type
       * @return This builder
       */
      Builder eventType(String eventType);

      /**
       * Sets the user ID.
       *
       * @param userId The user ID
       * @return This builder
       */
      Builder userId(String userId);

      /**
       * Sets the username.
       *
       * @param username The username
       * @return This builder
       */
      Builder username(String username);

      /**
       * Sets the IP address.
       *
       * @param ipAddress The IP address
       * @return This builder
       */
      Builder ipAddress(String ipAddress);

      /**
       * Sets the resource.
       *
       * @param resource The resource
       * @return This builder
       */
      Builder resource(String resource);

      /**
       * Sets the action.
       *
       * @param action The action
       * @return This builder
       */
      Builder action(String action);

      /**
       * Sets the result.
       *
       * @param result The result
       * @return This builder
       */
      Builder result(String result);

      /**
       * Sets the timestamp.
       *
       * @param timestamp The timestamp
       * @return This builder
       */
      Builder timestamp(Instant timestamp);

      /**
       * Adds a detail.
       *
       * @param key The detail key
       * @param value The detail value
       * @return This builder
       */
      Builder addDetail(String key, String value);

      /**
       * Builds the AuditLogEntry.
       *
       * @return The built AuditLogEntry
       */
      AuditLogEntry build();
    }
  }

  /** Base class for operation results. */
  abstract class OperationResult {
    private final boolean successful;
    private final String message;
    private final Optional<String> errorCode;

    /**
     * Constructs a successful OperationResult.
     *
     * @param message The success message
     */
    protected OperationResult(String message) {
      this.successful = true;
      this.message = message;
      this.errorCode = Optional.empty();
    }

    /**
     * Constructs a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     */
    protected OperationResult(String message, String errorCode) {
      this.successful = false;
      this.message = message;
      this.errorCode = Optional.of(errorCode);
    }

    /**
     * Gets whether the operation was successful.
     *
     * @return True if successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the result message.
     *
     * @return The message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the error code, if any.
     *
     * @return The error code, if available
     */
    public Optional<String> getErrorCode() {
      return errorCode;
    }

    /**
     * Creates a successful OperationResult.
     *
     * @param message The success message
     * @return The OperationResult
     */
    public static OperationResult success(String message) {
      return new OperationResult(message) {};
    }

    /**
     * Creates a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The OperationResult
     */
    public static OperationResult failure(String message, String errorCode) {
      return new OperationResult(message, errorCode) {};
    }
  }

  /** Result of an authentication operation. */
  class AuthenticationResult extends OperationResult {
    private final Optional<SecurityToken> token;
    private final Optional<User> user;

    private AuthenticationResult(String message, SecurityToken token, User user) {
      super(message);
      this.token = Optional.of(token);
      this.user = Optional.of(user);
    }

    private AuthenticationResult(String message, String errorCode) {
      super(message, errorCode);
      this.token = Optional.empty();
      this.user = Optional.empty();
    }

    /**
     * Gets the security token, if authentication was successful.
     *
     * @return The security token, if available
     */
    public Optional<SecurityToken> getToken() {
      return token;
    }

    /**
     * Gets the authenticated user, if authentication was successful.
     *
     * @return The user, if available
     */
    public Optional<User> getUser() {
      return user;
    }

    /**
     * Creates a successful AuthenticationResult.
     *
     * @param message The success message
     * @param token The security token
     * @param user The authenticated user
     * @return The AuthenticationResult
     */
    public static AuthenticationResult success(String message, SecurityToken token, User user) {
      return new AuthenticationResult(message, token, user);
    }

    /**
     * Creates a failed AuthenticationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The AuthenticationResult
     */
    public static AuthenticationResult failure(String message, String errorCode) {
      return new AuthenticationResult(message, errorCode);
    }
  }

  /** Result of a token validation operation. */
  class TokenValidationResult extends OperationResult {
    private final Optional<User> user;
    private final Optional<SecurityToken> token;

    private TokenValidationResult(String message, User user, SecurityToken token) {
      super(message);
      this.user = Optional.of(user);
      this.token = Optional.of(token);
    }

    private TokenValidationResult(String message, String errorCode) {
      super(message, errorCode);
      this.user = Optional.empty();
      this.token = Optional.empty();
    }

    /**
     * Gets the user associated with the token, if validation was successful.
     *
     * @return The user, if available
     */
    public Optional<User> getUser() {
      return user;
    }

    /**
     * Gets the validated token, if validation was successful.
     *
     * @return The token, if available
     */
    public Optional<SecurityToken> getToken() {
      return token;
    }

    /**
     * Creates a successful TokenValidationResult.
     *
     * @param message The success message
     * @param user The user associated with the token
     * @param token The validated token
     * @return The TokenValidationResult
     */
    public static TokenValidationResult success(String message, User user, SecurityToken token) {
      return new TokenValidationResult(message, user, token);
    }

    /**
     * Creates a failed TokenValidationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The TokenValidationResult
     */
    public static TokenValidationResult failure(String message, String errorCode) {
      return new TokenValidationResult(message, errorCode);
    }
  }

  /** Result of a token refresh operation. */
  class TokenRefreshResult extends OperationResult {
    private final Optional<SecurityToken> newToken;
    private final Optional<String> refreshToken;

    private TokenRefreshResult(String message, SecurityToken newToken, String refreshToken) {
      super(message);
      this.newToken = Optional.of(newToken);
      this.refreshToken = Optional.of(refreshToken);
    }

    private TokenRefreshResult(String message, String errorCode) {
      super(message, errorCode);
      this.newToken = Optional.empty();
      this.refreshToken = Optional.empty();
    }

    /**
     * Gets the new security token, if refresh was successful.
     *
     * @return The new token, if available
     */
    public Optional<SecurityToken> getNewToken() {
      return newToken;
    }

    /**
     * Gets the new refresh token, if refresh was successful.
     *
     * @return The new refresh token, if available
     */
    public Optional<String> getRefreshToken() {
      return refreshToken;
    }

    /**
     * Creates a successful TokenRefreshResult.
     *
     * @param message The success message
     * @param newToken The new security token
     * @param refreshToken The new refresh token
     * @return The TokenRefreshResult
     */
    public static TokenRefreshResult success(
        String message, SecurityToken newToken, String refreshToken) {
      return new TokenRefreshResult(message, newToken, refreshToken);
    }

    /**
     * Creates a failed TokenRefreshResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The TokenRefreshResult
     */
    public static TokenRefreshResult failure(String message, String errorCode) {
      return new TokenRefreshResult(message, errorCode);
    }
  }

  /** Result of an authorization operation. */
  class AuthorizationResult extends OperationResult {
    private final boolean authorized;

    private AuthorizationResult(String message, boolean authorized) {
      super(message);
      this.authorized = authorized;
    }

    private AuthorizationResult(String message, String errorCode) {
      super(message, errorCode);
      this.authorized = false;
    }

    /**
     * Gets whether the user is authorized.
     *
     * @return True if authorized, false otherwise
     */
    public boolean isAuthorized() {
      return authorized;
    }

    /**
     * Creates a successful AuthorizationResult.
     *
     * @param message The success message
     * @param authorized Whether the user is authorized
     * @return The AuthorizationResult
     */
    public static AuthorizationResult success(String message, boolean authorized) {
      return new AuthorizationResult(message, authorized);
    }

    /**
     * Creates a failed AuthorizationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The AuthorizationResult
     */
    public static AuthorizationResult failure(String message, String errorCode) {
      return new AuthorizationResult(message, errorCode);
    }
  }

  /** Result of a user operation. */
  class UserOperationResult extends OperationResult {
    private final Optional<User> user;

    private UserOperationResult(String message, User user) {
      super(message);
      this.user = Optional.of(user);
    }

    private UserOperationResult(String message, String errorCode) {
      super(message, errorCode);
      this.user = Optional.empty();
    }

    /**
     * Gets the user, if the operation was successful.
     *
     * @return The user, if available
     */
    public Optional<User> getUser() {
      return user;
    }

    /**
     * Creates a successful UserOperationResult.
     *
     * @param message The success message
     * @param user The user
     * @return The UserOperationResult
     */
    public static UserOperationResult success(String message, User user) {
      return new UserOperationResult(message, user);
    }

    /**
     * Creates a failed UserOperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The UserOperationResult
     */
    public static UserOperationResult failure(String message, String errorCode) {
      return new UserOperationResult(message, errorCode);
    }
  }

  /** Result of a password update operation. */
  class PasswordUpdateResult extends OperationResult {
    private final boolean requiresPasswordChange;
    private final Optional<Duration> passwordExpiresIn;

    private PasswordUpdateResult(
        String message, boolean requiresPasswordChange, Optional<Duration> passwordExpiresIn) {
      super(message);
      this.requiresPasswordChange = requiresPasswordChange;
      this.passwordExpiresIn = passwordExpiresIn;
    }

    private PasswordUpdateResult(String message, String errorCode) {
      super(message, errorCode);
      this.requiresPasswordChange = false;
      this.passwordExpiresIn = Optional.empty();
    }

    /**
     * Gets whether the user is required to change their password again.
     *
     * @return True if a password change is required, false otherwise
     */
    public boolean isRequiresPasswordChange() {
      return requiresPasswordChange;
    }

    /**
     * Gets the duration until the password expires, if available.
     *
     * @return The duration until password expiration, if available
     */
    public Optional<Duration> getPasswordExpiresIn() {
      return passwordExpiresIn;
    }

    /**
     * Creates a successful PasswordUpdateResult.
     *
     * @param message The success message
     * @param requiresPasswordChange Whether a password change is required
     * @param passwordExpiresIn The duration until password expiration, if applicable
     * @return The PasswordUpdateResult
     */
    public static PasswordUpdateResult success(
        String message, boolean requiresPasswordChange, Optional<Duration> passwordExpiresIn) {
      return new PasswordUpdateResult(message, requiresPasswordChange, passwordExpiresIn);
    }

    /**
     * Creates a failed PasswordUpdateResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The PasswordUpdateResult
     */
    public static PasswordUpdateResult failure(String message, String errorCode) {
      return new PasswordUpdateResult(message, errorCode);
    }
  }

  /** Result of a token generation operation. */
  class TokenGenerationResult extends OperationResult {
    private final Optional<String> token;
    private final Optional<Instant> expiresAt;

    private TokenGenerationResult(String message, String token, Instant expiresAt) {
      super(message);
      this.token = Optional.of(token);
      this.expiresAt = Optional.of(expiresAt);
    }

    private TokenGenerationResult(String message, String errorCode) {
      super(message, errorCode);
      this.token = Optional.empty();
      this.expiresAt = Optional.empty();
    }

    /**
     * Gets the generated token, if generation was successful.
     *
     * @return The token, if available
     */
    public Optional<String> getToken() {
      return token;
    }

    /**
     * Gets the expiration time of the token, if generation was successful.
     *
     * @return The expiration time, if available
     */
    public Optional<Instant> getExpiresAt() {
      return expiresAt;
    }

    /**
     * Creates a successful TokenGenerationResult.
     *
     * @param message The success message
     * @param token The generated token
     * @param expiresAt The expiration time of the token
     * @return The TokenGenerationResult
     */
    public static TokenGenerationResult success(String message, String token, Instant expiresAt) {
      return new TokenGenerationResult(message, token, expiresAt);
    }

    /**
     * Creates a failed TokenGenerationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The TokenGenerationResult
     */
    public static TokenGenerationResult failure(String message, String errorCode) {
      return new TokenGenerationResult(message, errorCode);
    }
  }

  /** Result of an encryption operation. */
  class EncryptionResult extends OperationResult {
    private final Optional<byte[]> encryptedData;
    private final Optional<String> keyIdentifier;

    private EncryptionResult(String message, byte[] encryptedData, String keyIdentifier) {
      super(message);
      this.encryptedData = Optional.of(encryptedData);
      this.keyIdentifier = Optional.of(keyIdentifier);
    }

    private EncryptionResult(String message, String errorCode) {
      super(message, errorCode);
      this.encryptedData = Optional.empty();
      this.keyIdentifier = Optional.empty();
    }

    /**
     * Gets the encrypted data, if encryption was successful.
     *
     * @return The encrypted data, if available
     */
    public Optional<byte[]> getEncryptedData() {
      return encryptedData;
    }

    /**
     * Gets the key identifier used for encryption, if encryption was successful.
     *
     * @return The key identifier, if available
     */
    public Optional<String> getKeyIdentifier() {
      return keyIdentifier;
    }

    /**
     * Creates a successful EncryptionResult.
     *
     * @param message The success message
     * @param encryptedData The encrypted data
     * @param keyIdentifier The key identifier
     * @return The EncryptionResult
     */
    public static EncryptionResult success(
        String message, byte[] encryptedData, String keyIdentifier) {
      return new EncryptionResult(message, encryptedData, keyIdentifier);
    }

    /**
     * Creates a failed EncryptionResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The EncryptionResult
     */
    public static EncryptionResult failure(String message, String errorCode) {
      return new EncryptionResult(message, errorCode);
    }
  }

  /** Result of a decryption operation. */
  class DecryptionResult extends OperationResult {
    private final Optional<byte[]> decryptedData;
    private final Optional<String> keyIdentifier;

    private DecryptionResult(String message, byte[] decryptedData, String keyIdentifier) {
      super(message);
      this.decryptedData = Optional.of(decryptedData);
      this.keyIdentifier = Optional.of(keyIdentifier);
    }

    private DecryptionResult(String message, String errorCode) {
      super(message, errorCode);
      this.decryptedData = Optional.empty();
      this.keyIdentifier = Optional.empty();
    }

    /**
     * Gets the decrypted data, if decryption was successful.
     *
     * @return The decrypted data, if available
     */
    public Optional<byte[]> getDecryptedData() {
      return decryptedData;
    }

    /**
     * Gets the key identifier used for decryption, if decryption was successful.
     *
     * @return The key identifier, if available
     */
    public Optional<String> getKeyIdentifier() {
      return keyIdentifier;
    }

    /**
     * Creates a successful DecryptionResult.
     *
     * @param message The success message
     * @param decryptedData The decrypted data
     * @param keyIdentifier The key identifier
     * @return The DecryptionResult
     */
    public static DecryptionResult success(
        String message, byte[] decryptedData, String keyIdentifier) {
      return new DecryptionResult(message, decryptedData, keyIdentifier);
    }

    /**
     * Creates a failed DecryptionResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The DecryptionResult
     */
    public static DecryptionResult failure(String message, String errorCode) {
      return new DecryptionResult(message, errorCode);
    }
  }
}
