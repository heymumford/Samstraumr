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
package org.s8r.application.port;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * SecurityPort defines the interface for security-related operations in the S8r framework. This
 * port handles authentication, authorization, and security token management.
 *
 * <p>Following clean architecture principles, this port interface allows the application core to
 * remain independent of specific security implementations.
 */
public interface SecurityPort {

  /** Enumeration of standard permission types for security operations. */
  enum Permission {
    READ,
    WRITE,
    CREATE,
    DELETE,
    LIST,
    EXECUTE,
    ADMIN,
    ALL
  }

  /** Exception thrown when access to a resource is denied due to security restrictions. */
  class AccessDeniedException extends RuntimeException {
    private final String resource;
    private final Permission permission;

    public AccessDeniedException(String message) {
      super(message);
      this.resource = null;
      this.permission = null;
    }

    public AccessDeniedException(String message, String resource, Permission permission) {
      super(message);
      this.resource = resource;
      this.permission = permission;
    }

    public String getResource() {
      return resource;
    }

    public Permission getPermission() {
      return permission;
    }
  }

  /** Interface representing a security context for a user or component. */
  interface SecurityContext {
    /**
     * Gets the identifier for this security context.
     *
     * @return The identifier (e.g., username, component ID)
     */
    String getIdentifier();

    /**
     * Checks if this security context has a specific role.
     *
     * @param roleName The role name to check
     * @return true if the role is present, false otherwise
     */
    boolean hasRole(String roleName);

    /**
     * Gets all roles associated with this security context.
     *
     * @return List of role names
     */
    List<String> getRoles();

    /**
     * Gets additional attributes associated with this security context.
     *
     * @return Map of attributes
     */
    Map<String, Object> getAttributes();
  }

  /** Represents the outcome of a security operation with detailed information. */
  final class SecurityResult {
    private final boolean successful;
    private final String message;
    private final String reason;
    private final Map<String, Object> attributes;

    private SecurityResult(
        boolean successful, String message, String reason, Map<String, Object> attributes) {
      this.successful = successful;
      this.message = message;
      this.reason = reason;
      this.attributes = attributes;
    }

    /**
     * Creates a successful result.
     *
     * @param message A message describing the successful operation
     * @return A new SecurityResult instance indicating success
     */
    public static SecurityResult success(String message) {
      return new SecurityResult(true, message, null, Map.of());
    }

    /**
     * Creates a successful result with additional attributes.
     *
     * @param message A message describing the successful operation
     * @param attributes Additional information about the operation
     * @return A new SecurityResult instance indicating success
     */
    public static SecurityResult success(String message, Map<String, Object> attributes) {
      return new SecurityResult(true, message, null, attributes);
    }

    /**
     * Creates a failed result.
     *
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @return A new SecurityResult instance indicating failure
     */
    public static SecurityResult failure(String message, String reason) {
      return new SecurityResult(false, message, reason, Map.of());
    }

    /**
     * Creates a failed result with additional attributes.
     *
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @param attributes Additional information about the operation
     * @return A new SecurityResult instance indicating failure
     */
    public static SecurityResult failure(
        String message, String reason, Map<String, Object> attributes) {
      return new SecurityResult(false, message, reason, attributes);
    }

    /**
     * Checks if the operation was successful.
     *
     * @return True if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the message associated with the operation result.
     *
     * @return The message describing the operation outcome
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the reason for a failed operation.
     *
     * @return The reason for the failure, or null if the operation was successful
     */
    public Optional<String> getReason() {
      return Optional.ofNullable(reason);
    }

    /**
     * Gets the additional attributes associated with the operation result.
     *
     * @return A map of attributes providing additional information
     */
    public Map<String, Object> getAttributes() {
      return attributes;
    }
  }

  /** Represents a user's authentication status and associated information. */
  final class AuthenticationContext {
    private final String userId;
    private final String username;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Instant authenticatedAt;
    private final Duration sessionValidity;
    private final Map<String, Object> attributes;

    /**
     * Creates a new authentication context.
     *
     * @param userId The unique identifier of the authenticated user
     * @param username The username of the authenticated user
     * @param roles The roles assigned to the user
     * @param permissions The permissions granted to the user
     * @param authenticatedAt The time when the user was authenticated
     * @param sessionValidity The duration for which the authentication is valid
     * @param attributes Additional attributes associated with the authentication
     */
    public AuthenticationContext(
        String userId,
        String username,
        Set<String> roles,
        Set<String> permissions,
        Instant authenticatedAt,
        Duration sessionValidity,
        Map<String, Object> attributes) {
      this.userId = userId;
      this.username = username;
      this.roles = roles;
      this.permissions = permissions;
      this.authenticatedAt = authenticatedAt;
      this.sessionValidity = sessionValidity;
      this.attributes = attributes;
    }

    public String getUserId() {
      return userId;
    }

    public String getUsername() {
      return username;
    }

    public Set<String> getRoles() {
      return roles;
    }

    public Set<String> getPermissions() {
      return permissions;
    }

    public Instant getAuthenticatedAt() {
      return authenticatedAt;
    }

    public Duration getSessionValidity() {
      return sessionValidity;
    }

    public Map<String, Object> getAttributes() {
      return attributes;
    }

    /**
     * Checks if the authentication session is still valid.
     *
     * @return True if the session is valid, false otherwise
     */
    public boolean isValid() {
      return authenticatedAt.plus(sessionValidity).isAfter(Instant.now());
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param role The role to check
     * @return True if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
      return roles.contains(role);
    }

    /**
     * Checks if the user has a specific permission.
     *
     * @param permission The permission to check
     * @return True if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
      return permissions.contains(permission);
    }
  }

  /** Enumeration of common security event types. */
  enum SecurityEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    ACCESS_DENIED,
    ACCESS_GRANTED,
    TOKEN_ISSUED,
    TOKEN_VALIDATED,
    TOKEN_EXPIRED,
    PASSWORD_CHANGED,
    PERMISSION_CHANGED,
    SECURITY_CONFIG_CHANGED,
    POTENTIAL_ATTACK_DETECTED,
    SECURITY_ALERT
  }

  /**
   * Authenticates a user using their credentials.
   *
   * @param username The username for authentication
   * @param password The password for authentication
   * @return A SecurityResult indicating success or failure with details
   */
  SecurityResult authenticate(String username, String password);

  /**
   * Authenticates a user using a token.
   *
   * @param token The authentication token
   * @return A SecurityResult indicating success or failure with details
   */
  SecurityResult authenticateWithToken(String token);

  /**
   * Logs out the currently authenticated user.
   *
   * @return A SecurityResult indicating success or failure with details
   */
  SecurityResult logout();

  /**
   * Gets the current authentication context if available.
   *
   * @return An Optional containing the authentication context if available
   */
  Optional<AuthenticationContext> getCurrentAuthContext();

  /**
   * Checks if the current user has the specified role.
   *
   * @param role The role to check
   * @return True if the user has the role, false otherwise
   */
  boolean hasRole(String role);

  /**
   * Checks if the current user has the specified permission.
   *
   * @param permission The permission to check
   * @return True if the user has the permission, false otherwise
   */
  boolean hasPermission(String permission);

  /**
   * Checks if the current user has any of the specified roles.
   *
   * @param roles The roles to check
   * @return True if the user has any of the roles, false otherwise
   */
  boolean hasAnyRole(String... roles);

  /**
   * Checks if the current user has all of the specified roles.
   *
   * @param roles The roles to check
   * @return True if the user has all of the roles, false otherwise
   */
  boolean hasAllRoles(String... roles);

  /**
   * Checks if the current user has any of the specified permissions.
   *
   * @param permissions The permissions to check
   * @return True if the user has any of the permissions, false otherwise
   */
  boolean hasAnyPermission(String... permissions);

  /**
   * Checks if the current user has all of the specified permissions.
   *
   * @param permissions The permissions to check
   * @return True if the user has all of the permissions, false otherwise
   */
  boolean hasAllPermissions(String... permissions);

  /**
   * Generates a security token for the authenticated user.
   *
   * @param validity The duration for which the token is valid
   * @return A SecurityResult containing the token if successful
   */
  SecurityResult generateToken(Duration validity);

  /**
   * Validates a security token.
   *
   * @param token The token to validate
   * @return A SecurityResult indicating if the token is valid
   */
  SecurityResult validateToken(String token);

  /**
   * Revokes a security token.
   *
   * @param token The token to revoke
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult revokeToken(String token);

  /**
   * Checks if a component has access to perform an operation on a resource.
   *
   * @param componentId The ID of the component requesting access
   * @param resourceId The ID of the resource being accessed
   * @param operationType The type of operation being performed
   * @return A SecurityResult indicating if access is granted
   */
  SecurityResult checkComponentAccess(String componentId, String resourceId, String operationType);

  /**
   * Records a security event in the security audit log.
   *
   * @param eventType The type of security event
   * @param details Additional details about the event
   * @return A SecurityResult indicating if the event was recorded successfully
   */
  SecurityResult logSecurityEvent(SecurityEventType eventType, Map<String, Object> details);

  /**
   * Gets the security audit log for a specified time period.
   *
   * @param from The start time of the period
   * @param to The end time of the period
   * @return A list of security event records
   */
  List<Map<String, Object>> getSecurityAuditLog(Instant from, Instant to);

  /**
   * Gets the security configuration for a specific component.
   *
   * @param componentId The ID of the component
   * @return A map containing the security configuration
   */
  Map<String, Object> getSecurityConfig(String componentId);

  /**
   * Checks if a security context has permission to access a resource.
   *
   * @param context The security context
   * @param resource The resource being accessed
   * @param permission The permission required
   * @return true if access is allowed, false otherwise
   */
  boolean hasPermission(SecurityContext context, String resource, Permission permission);

  /**
   * Grants permission to a security context for a resource.
   *
   * @param context The security context
   * @param resource The resource to grant access to
   * @param permission The permission to grant
   */
  void grantPermission(SecurityContext context, String resource, Permission permission);

  /**
   * Revokes permission from a security context for a resource.
   *
   * @param context The security context
   * @param resource The resource to revoke access from
   * @param permission The permission to revoke
   */
  void revokePermission(SecurityContext context, String resource, Permission permission);

  /**
   * Logs an access attempt.
   *
   * @param context The security context
   * @param resource The resource being accessed
   * @param operation The operation being performed
   * @param success Whether the access was successful
   * @param details Additional details about the access
   */
  void logAccess(
      SecurityContext context, String resource, String operation, boolean success, String details);

  /**
   * Gets the audit log for a specific resource.
   *
   * @param context The security context requesting the log
   * @param resource The resource to get the audit log for
   * @return List of audit log entries
   */
  List<String> getAuditLog(SecurityContext context, String resource);

  /**
   * Checks if the security port is initialized.
   *
   * @return true if initialized, false otherwise
   */
  boolean isInitialized();

  /**
   * Registers a new user in the system.
   *
   * @param username The username for the new user
   * @param password The password for the new user
   * @param roles Initial roles for the user
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult registerUser(String username, String password, Set<String> roles);

  /**
   * Updates a user's roles.
   *
   * @param userId The ID of the user
   * @param roles The new roles for the user
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult updateUserRoles(String userId, Set<String> roles);

  /**
   * Updates a user's permissions.
   *
   * @param userId The ID of the user
   * @param permissions The new permissions for the user
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult updateUserPermissions(String userId, Set<String> permissions);

  /**
   * Initializes the security subsystem.
   *
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult initialize();

  /**
   * Shuts down the security subsystem.
   *
   * @return A SecurityResult indicating success or failure
   */
  SecurityResult shutdown();
}
