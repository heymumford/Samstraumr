/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;

/**
 * Optimized implementation of the SecurityPort interface.
 *
 * <p>This adapter provides enhanced security operations with: - Cached authentication results for
 * improved performance - Optimized role hierarchy traversal - Fast token validation for repeated
 * access patterns - Concurrent access optimization with read-write locks - Security metrics
 * tracking
 */
public class OptimizedSecurityAdapter implements SecurityPort {

  private static final Duration DEFAULT_TOKEN_VALIDITY = Duration.ofHours(1);
  private static final Duration DEFAULT_SESSION_VALIDITY = Duration.ofHours(4);
  private static final int MAX_AUTH_CACHE_SIZE = 1000;
  private static final int MAX_FAILED_ATTEMPTS = 5;
  private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

  private final LoggerPort logger;
  private final Map<String, User> users;
  private final Map<String, Role> roles;
  private final Map<String, Token> tokens;
  private final Map<String, Set<String>> roleHierarchy;
  private final Map<String, Map<String, Boolean>> permissionCache;
  private final Map<String, AuthenticationCache> authCache;
  private final Map<String, FailedAuthAttempt> failedAttempts;
  private final List<SecurityEvent> securityEventLog;

  private final ReadWriteLock userLock;
  private final ReadWriteLock roleLock;
  private final ReadWriteLock tokenLock;
  private final ReadWriteLock logLock;

  private boolean initialized;
  private AuthenticationContext currentAuthContext;

  // Performance metrics
  private final AtomicLong authenticationCount = new AtomicLong(0);
  private final AtomicLong authCacheHits = new AtomicLong(0);
  private final AtomicLong validationCount = new AtomicLong(0);
  private final AtomicLong permissionCheckCount = new AtomicLong(0);
  private final AtomicLong permissionCacheHits = new AtomicLong(0);
  private final AtomicLong tokenIssuedCount = new AtomicLong(0);
  private final AtomicLong tokenRevokedCount = new AtomicLong(0);
  private final AtomicLong securityEventCount = new AtomicLong(0);

  /**
   * Creates a new OptimizedSecurityAdapter with a logger.
   *
   * @param logger The logger to use
   */
  public OptimizedSecurityAdapter(LoggerPort logger) {
    this.logger = logger;
    this.users = new ConcurrentHashMap<>();
    this.roles = new ConcurrentHashMap<>();
    this.tokens = new ConcurrentHashMap<>();
    this.roleHierarchy = new ConcurrentHashMap<>();
    this.permissionCache = new ConcurrentHashMap<>();
    this.authCache = new ConcurrentHashMap<>();
    this.failedAttempts = new ConcurrentHashMap<>();
    this.securityEventLog = Collections.synchronizedList(new java.util.ArrayList<>());

    this.userLock = new ReentrantReadWriteLock();
    this.roleLock = new ReentrantReadWriteLock();
    this.tokenLock = new ReentrantReadWriteLock();
    this.logLock = new ReentrantReadWriteLock();

    this.initialized = false;
    this.currentAuthContext = null;
  }

  @Override
  public SecurityResult initialize() {
    if (initialized) {
      logger.warn("Security adapter already initialized");
      return SecurityResult.success("Security adapter already initialized");
    }

    try {
      // Initialize default roles
      initializeDefaultRoles();
      initialized = true;

      logSecurityEvent(
          SecurityEventType.SECURITY_CONFIG_CHANGED,
          Map.of("action", "initialize", "timestamp", Instant.now().toString()));

      logger.info("Security adapter initialized successfully");
      return SecurityResult.success("Security adapter initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize security adapter", e);
      return SecurityResult.failure("Failed to initialize security adapter", e.getMessage());
    }
  }

  /** Initializes the default roles in the system. */
  private void initializeDefaultRoles() {
    // Create some basic roles
    Role adminRole =
        new Role("ADMIN", Set.of("manage_users", "manage_roles", "view_all", "edit_all"));
    Role userRole = new Role("USER", Set.of("view_own", "edit_own"));
    Role guestRole = new Role("GUEST", Set.of("view_public"));

    // Set up role hierarchy
    Set<String> adminParents = new HashSet<>();
    Set<String> userParents = new HashSet<>();
    userParents.add("GUEST"); // USER inherits from GUEST
    Set<String> guestParents = new HashSet<>();

    roleLock.writeLock().lock();
    try {
      roles.put("ADMIN", adminRole);
      roles.put("USER", userRole);
      roles.put("GUEST", guestRole);

      roleHierarchy.put("ADMIN", adminParents);
      roleHierarchy.put("USER", userParents);
      roleHierarchy.put("GUEST", guestParents);

      logger.debug("Default roles initialized: ADMIN, USER, GUEST");
    } finally {
      roleLock.writeLock().unlock();
    }
  }

  @Override
  public SecurityResult shutdown() {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Security adapter not initialized", "Not initialized");
    }

    try {
      // Clean up resources
      tokenLock.writeLock().lock();
      try {
        tokens.clear();
      } finally {
        tokenLock.writeLock().unlock();
      }

      authCache.clear();
      permissionCache.clear();
      initialized = false;
      currentAuthContext = null;

      logSecurityEvent(
          SecurityEventType.SECURITY_CONFIG_CHANGED,
          Map.of("action", "shutdown", "timestamp", Instant.now().toString()));

      logger.info("Security adapter shut down successfully");
      return SecurityResult.success("Security adapter shut down successfully");
    } catch (Exception e) {
      logger.error("Failed to shut down security adapter", e);
      return SecurityResult.failure("Failed to shut down security adapter", e.getMessage());
    }
  }

  @Override
  public SecurityResult authenticate(String username, String password) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Security adapter not initialized", "Not initialized");
    }

    authenticationCount.incrementAndGet();

    // Check for account lockout due to failed attempts
    if (isAccountLocked(username)) {
      logger.warn("Account locked due to too many failed attempts: {}", username);

      logSecurityEvent(
          SecurityEventType.LOGIN_FAILURE,
          Map.of("username", username, "reason", "account_locked"));

      return SecurityResult.failure("Authentication failed", "Account locked");
    }

    // Check authentication cache first
    if (authCache.containsKey(username)) {
      AuthenticationCache cacheEntry = authCache.get(username);
      if (cacheEntry.isValid() && cacheEntry.password.equals(password)) {
        authCacheHits.incrementAndGet();

        // Update the cache access time
        cacheEntry.lastAccessTime = Instant.now();

        User user = users.get(username);
        if (user != null) {
          createAuthenticationContext(user);

          logSecurityEvent(
              SecurityEventType.LOGIN_SUCCESS, Map.of("username", username, "method", "cached"));

          logger.debug("Authenticated from cache: {}", username);
          return SecurityResult.success("Authentication successful");
        }
      }
    }

    // Proceed with regular authentication
    userLock.readLock().lock();
    try {
      if (!users.containsKey(username)) {
        recordFailedAttempt(username);

        logSecurityEvent(
            SecurityEventType.LOGIN_FAILURE,
            Map.of("username", username, "reason", "user_not_found"));

        logger.debug("Authentication failed - user not found: {}", username);
        return SecurityResult.failure("Authentication failed", "Invalid credentials");
      }

      User user = users.get(username);
      if (user.password.equals(password)) {
        // Valid authentication, update cache
        updateAuthCache(username, password);
        clearFailedAttempts(username);

        createAuthenticationContext(user);

        logSecurityEvent(
            SecurityEventType.LOGIN_SUCCESS, Map.of("username", username, "userId", user.id));

        logger.info("User authenticated successfully: {}", username);
        return SecurityResult.success(
            "Authentication successful", Map.of("userId", user.id, "roles", user.roles));
      } else {
        recordFailedAttempt(username);

        logSecurityEvent(
            SecurityEventType.LOGIN_FAILURE,
            Map.of("username", username, "reason", "invalid_password"));

        logger.debug("Authentication failed - invalid password: {}", username);
        return SecurityResult.failure("Authentication failed", "Invalid credentials");
      }
    } finally {
      userLock.readLock().unlock();
    }
  }

  @Override
  public SecurityResult authenticateWithToken(String token) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Security adapter not initialized", "Not initialized");
    }

    validationCount.incrementAndGet();

    tokenLock.readLock().lock();
    try {
      if (!tokens.containsKey(token)) {
        logSecurityEvent(
            SecurityEventType.LOGIN_FAILURE,
            Map.of("method", "token", "reason", "token_not_found"));

        logger.debug("Token authentication failed - token not found");
        return SecurityResult.failure("Authentication failed", "Invalid token");
      }

      Token tokenObj = tokens.get(token);
      if (tokenObj.isExpired()) {
        // Token has expired, remove it
        tokenLock.readLock().unlock();
        tokenLock.writeLock().lock();
        try {
          tokens.remove(token);
          tokenRevokedCount.incrementAndGet();

          logSecurityEvent(SecurityEventType.TOKEN_EXPIRED, Map.of("username", tokenObj.username));

          logger.debug("Token expired for user: {}", tokenObj.username);
          return SecurityResult.failure("Authentication failed", "Token expired");
        } finally {
          tokenLock.writeLock().unlock();
          tokenLock.readLock().lock(); // Reacquire read lock
        }
      }

      // Update token's last access time
      tokenObj.lastAccessTime = Instant.now();

      // Get user associated with token
      userLock.readLock().lock();
      try {
        User user = users.get(tokenObj.username);
        if (user == null) {
          logger.warn("Token exists but user not found: {}", tokenObj.username);
          return SecurityResult.failure("Authentication failed", "User not found");
        }

        createAuthenticationContext(user);

        logSecurityEvent(
            SecurityEventType.LOGIN_SUCCESS, Map.of("username", user.username, "method", "token"));

        logger.debug("Token authentication successful: {}", user.username);
        return SecurityResult.success(
            "Authentication successful", Map.of("userId", user.id, "tokenId", token));
      } finally {
        userLock.readLock().unlock();
      }
    } finally {
      tokenLock.readLock().unlock();
    }
  }

  @Override
  public SecurityResult logout() {
    if (currentAuthContext == null) {
      logger.debug("Logout requested but no active session");
      return SecurityResult.failure("Logout failed", "No active session");
    }

    String username = currentAuthContext.getUsername();
    logSecurityEvent(SecurityEventType.LOGOUT, Map.of("username", username));

    currentAuthContext = null;
    logger.info("User logged out: {}", username);
    return SecurityResult.success("Logout successful");
  }

  @Override
  public Optional<AuthenticationContext> getCurrentAuthContext() {
    return Optional.ofNullable(currentAuthContext);
  }

  @Override
  public boolean hasRole(String role) {
    if (currentAuthContext == null) {
      return false;
    }
    return currentAuthContext.hasRole(role);
  }

  @Override
  public boolean hasPermission(String permission) {
    if (currentAuthContext == null) {
      return false;
    }
    return currentAuthContext.hasPermission(permission);
  }

  @Override
  public boolean hasAnyRole(String... roles) {
    if (currentAuthContext == null || roles == null || roles.length == 0) {
      return false;
    }

    for (String role : roles) {
      if (currentAuthContext.hasRole(role)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAllRoles(String... roles) {
    if (currentAuthContext == null || roles == null || roles.length == 0) {
      return false;
    }

    for (String role : roles) {
      if (!currentAuthContext.hasRole(role)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean hasAnyPermission(String... permissions) {
    if (currentAuthContext == null || permissions == null || permissions.length == 0) {
      return false;
    }

    for (String permission : permissions) {
      if (currentAuthContext.hasPermission(permission)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasAllPermissions(String... permissions) {
    if (currentAuthContext == null || permissions == null || permissions.length == 0) {
      return false;
    }

    for (String permission : permissions) {
      if (!currentAuthContext.hasPermission(permission)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public SecurityResult generateToken(Duration validity) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Token generation failed", "Security adapter not initialized");
    }

    if (currentAuthContext == null) {
      logger.warn("Token generation failed: no authentication context");
      return SecurityResult.failure("Token generation failed", "Not authenticated");
    }

    String username = currentAuthContext.getUsername();
    Set<String> permissions = currentAuthContext.getPermissions();

    String tokenId = UUID.randomUUID().toString();
    Duration tokenValidity = validity != null ? validity : DEFAULT_TOKEN_VALIDITY;
    Instant expiryTime = Instant.now().plus(tokenValidity);

    Token token = new Token(tokenId, username, new ArrayList<>(permissions), expiryTime);

    tokenLock.writeLock().lock();
    try {
      tokens.put(tokenId, token);
      tokenIssuedCount.incrementAndGet();

      logSecurityEvent(
          SecurityEventType.TOKEN_ISSUED,
          Map.of("username", username, "tokenId", tokenId, "expiry", expiryTime.toString()));

      logger.info("Token generated for user: {}", username);
      return SecurityResult.success(
          "Token generated successfully",
          Map.of("token", tokenId, "expiry", expiryTime.toString()));
    } finally {
      tokenLock.writeLock().unlock();
    }
  }

  @Override
  public SecurityResult validateToken(String token) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Token validation failed", "Security adapter not initialized");
    }

    validationCount.incrementAndGet();

    tokenLock.readLock().lock();
    try {
      if (!tokens.containsKey(token)) {
        logger.debug("Token validation failed: token not found");
        return SecurityResult.failure("Token validation failed", "Invalid token");
      }

      Token tokenObj = tokens.get(token);
      if (tokenObj.isExpired()) {
        // Token has expired, remove it
        tokenLock.readLock().unlock();
        tokenLock.writeLock().lock();
        try {
          tokens.remove(token);
          tokenRevokedCount.incrementAndGet();

          logSecurityEvent(
              SecurityEventType.TOKEN_EXPIRED,
              Map.of("username", tokenObj.username, "tokenId", token));

          logger.debug("Token expired: {}", token);
          return SecurityResult.failure("Token validation failed", "Token expired");
        } finally {
          tokenLock.writeLock().unlock();
          tokenLock.readLock().lock(); // Reacquire read lock
        }
      }

      // Update the token's last access time
      tokenObj.lastAccessTime = Instant.now();

      logger.debug("Token validated successfully: {}", token);
      return SecurityResult.success(
          "Token is valid",
          Map.of("username", tokenObj.username, "expiry", tokenObj.expiryTime.toString()));
    } finally {
      tokenLock.readLock().unlock();
    }
  }

  @Override
  public SecurityResult revokeToken(String token) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Token revocation failed", "Security adapter not initialized");
    }

    tokenLock.writeLock().lock();
    try {
      Token removedToken = tokens.remove(token);
      if (removedToken != null) {
        tokenRevokedCount.incrementAndGet();

        logSecurityEvent(
            SecurityEventType.SECURITY_CONFIG_CHANGED,
            Map.of("action", "token_revoked", "username", removedToken.username));

        logger.info("Token revoked: {}", token);
        return SecurityResult.success("Token revoked successfully");
      } else {
        logger.debug("Token revocation failed: token not found");
        return SecurityResult.failure("Token revocation failed", "Token not found");
      }
    } finally {
      tokenLock.writeLock().unlock();
    }
  }

  @Override
  public SecurityResult checkComponentAccess(
      String componentId, String resourceId, String operationType) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Access check failed", "Security adapter not initialized");
    }

    // Create a security context for the component
    SecurityContext componentContext = createComponentContext(componentId);

    // Check if the component has permission for the operation
    Permission permission;
    try {
      permission = Permission.valueOf(operationType.toUpperCase());
    } catch (IllegalArgumentException e) {
      logger.warn("Invalid operation type: {}", operationType);
      return SecurityResult.failure("Access check failed", "Invalid operation type");
    }

    boolean hasAccess = hasPermission(componentContext, resourceId, permission);

    // Log the access attempt
    logAccess(
        componentContext,
        resourceId,
        operationType,
        hasAccess,
        "Component access check: " + (hasAccess ? "GRANTED" : "DENIED"));

    if (hasAccess) {
      logger.debug(
          "Component access granted: {} -> {} ({})", componentId, resourceId, operationType);
      return SecurityResult.success("Access granted");
    } else {
      logger.debug(
          "Component access denied: {} -> {} ({})", componentId, resourceId, operationType);
      return SecurityResult.failure("Access denied", "Insufficient permissions");
    }
  }

  @Override
  public SecurityResult logSecurityEvent(SecurityEventType eventType, Map<String, Object> details) {
    if (!initialized && eventType != SecurityEventType.SECURITY_CONFIG_CHANGED) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Event logging failed", "Security adapter not initialized");
    }

    SecurityEvent event =
        new SecurityEvent(
            UUID.randomUUID().toString(),
            Instant.now(),
            eventType,
            currentAuthContext != null ? currentAuthContext.getUsername() : "SYSTEM",
            new HashMap<>(details));

    logLock.writeLock().lock();
    try {
      securityEventLog.add(event);
      securityEventCount.incrementAndGet();

      // Trim log if it gets too large (keep last 10000 events)
      if (securityEventLog.size() > 10000) {
        securityEventLog.remove(0);
      }

      logger.debug("Security event logged: {} - {}", eventType, details);
      return SecurityResult.success("Event logged successfully");
    } finally {
      logLock.writeLock().unlock();
    }
  }

  @Override
  public List<Map<String, Object>> getSecurityAuditLog(Instant from, Instant to) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return Collections.emptyList();
    }

    logLock.readLock().lock();
    try {
      return securityEventLog.stream()
          .filter(event -> !event.timestamp.isBefore(from) && !event.timestamp.isAfter(to))
          .map(
              event -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("id", event.id);
                entry.put("timestamp", event.timestamp);
                entry.put("type", event.type);
                entry.put("username", event.username);
                entry.put("details", event.details);
                return entry;
              })
          .collect(Collectors.toList());
    } finally {
      logLock.readLock().unlock();
    }
  }

  @Override
  public Map<String, Object> getSecurityConfig(String componentId) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return Collections.emptyMap();
    }

    // Get component roles and permissions
    SecurityContext componentContext = createComponentContext(componentId);

    Map<String, Object> config = new HashMap<>();
    config.put("componentId", componentId);
    config.put("roles", componentContext.getRoles());

    // Get all resources this component has access to
    Map<String, Set<Permission>> resourcePermissions = new HashMap<>();

    roleLock.readLock().lock();
    try {
      for (String roleName : componentContext.getRoles()) {
        if (roles.containsKey(roleName)) {
          Role role = roles.get(roleName);
          for (String permission : role.permissions) {
            // Parse permission string in format "resource:operation"
            String[] parts = permission.split(":");
            if (parts.length == 2) {
              String resource = parts[0];
              try {
                Permission operation = Permission.valueOf(parts[1].toUpperCase());
                resourcePermissions.computeIfAbsent(resource, k -> new HashSet<>()).add(operation);
              } catch (IllegalArgumentException e) {
                // Ignore invalid permission format
              }
            }
          }
        }
      }
    } finally {
      roleLock.readLock().unlock();
    }

    config.put("resourcePermissions", resourcePermissions);

    return config;
  }

  @Override
  public boolean hasPermission(SecurityContext context, String resource, Permission permission) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return false;
    }

    permissionCheckCount.incrementAndGet();

    // Check permission cache first
    String cacheKey = context.getIdentifier() + ":" + resource + ":" + permission;
    Map<String, Boolean> contextPermCache =
        permissionCache.computeIfAbsent(context.getIdentifier(), k -> new ConcurrentHashMap<>());

    if (contextPermCache.containsKey(cacheKey)) {
      permissionCacheHits.incrementAndGet();
      return contextPermCache.get(cacheKey);
    }

    // Cache miss, perform the check
    boolean hasPermission = false;

    // Special case for ALL permission
    if (permission == Permission.ALL) {
      hasPermission = hasAllPermissions(context, resource);
    } else {
      // Check each role the context has
      for (String roleName : context.getRoles()) {
        if (hasPermissionInRoleHierarchy(roleName, resource, permission)) {
          hasPermission = true;
          break;
        }
      }
    }

    // Cache the result
    contextPermCache.put(cacheKey, hasPermission);

    logger.debug(
        "Permission check: {} -> {} ({}) = {}",
        context.getIdentifier(),
        resource,
        permission,
        hasPermission);
    return hasPermission;
  }

  /**
   * Checks if the context has all permissions for a resource.
   *
   * @param context The security context
   * @param resource The resource
   * @return true if the context has all permissions
   */
  private boolean hasAllPermissions(SecurityContext context, String resource) {
    for (Permission permission : Permission.values()) {
      if (permission != Permission.ALL && !hasPermission(context, resource, permission)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if a role or any of its parent roles have a permission.
   *
   * @param roleName The role name
   * @param resource The resource
   * @param permission The permission to check
   * @return true if the role or its parents have the permission
   */
  private boolean hasPermissionInRoleHierarchy(
      String roleName, String resource, Permission permission) {
    Set<String> visitedRoles = new HashSet<>();
    return hasPermissionInRoleHierarchyRecursive(roleName, resource, permission, visitedRoles);
  }

  /**
   * Recursive helper for checking permissions in the role hierarchy.
   *
   * @param roleName The role name
   * @param resource The resource
   * @param permission The permission to check
   * @param visitedRoles Set of roles already visited
   * @return true if the role or its parents have the permission
   */
  private boolean hasPermissionInRoleHierarchyRecursive(
      String roleName, String resource, Permission permission, Set<String> visitedRoles) {
    // Prevent circular dependencies
    if (visitedRoles.contains(roleName)) {
      return false;
    }
    visitedRoles.add(roleName);

    roleLock.readLock().lock();
    try {
      if (!roles.containsKey(roleName)) {
        return false;
      }

      // Check if this role has the permission
      Role role = roles.get(roleName);
      String permissionStr = resource + ":" + permission.name();
      if (role.permissions.contains(permissionStr)) {
        return true;
      }

      // Check parent roles
      Set<String> parents = roleHierarchy.getOrDefault(roleName, Collections.emptySet());
      for (String parent : parents) {
        if (hasPermissionInRoleHierarchyRecursive(parent, resource, permission, visitedRoles)) {
          return true;
        }
      }

      return false;
    } finally {
      roleLock.readLock().unlock();
    }
  }

  @Override
  public void grantPermission(SecurityContext context, String resource, Permission permission) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return;
    }

    // Get roles for this context
    List<String> contextRoles = context.getRoles();
    if (contextRoles.isEmpty()) {
      logger.warn("Context has no roles: {}", context.getIdentifier());
      return;
    }

    // Grant permission to the first role
    String roleName = contextRoles.get(0);
    String permissionStr = resource + ":" + permission.name();

    roleLock.writeLock().lock();
    try {
      Role role = roles.getOrDefault(roleName, new Role(roleName, new HashSet<>()));
      role.permissions.add(permissionStr);
      roles.put(roleName, role);

      // Clear permission cache
      permissionCache.clear();

      logSecurityEvent(
          SecurityEventType.PERMISSION_CHANGED,
          Map.of(
              "action",
              "grant",
              "role",
              roleName,
              "resource",
              resource,
              "permission",
              permission.name()));

      logger.info(
          "Permission granted: {} -> {} ({}) for {}",
          roleName,
          resource,
          permission,
          context.getIdentifier());
    } finally {
      roleLock.writeLock().unlock();
    }
  }

  @Override
  public void revokePermission(SecurityContext context, String resource, Permission permission) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return;
    }

    // Get roles for this context
    List<String> contextRoles = context.getRoles();
    if (contextRoles.isEmpty()) {
      logger.warn("Context has no roles: {}", context.getIdentifier());
      return;
    }

    // Process each role
    String permissionStr = resource + ":" + permission.name();

    roleLock.writeLock().lock();
    try {
      for (String roleName : contextRoles) {
        if (roles.containsKey(roleName)) {
          Role role = roles.get(roleName);
          boolean removed = role.permissions.remove(permissionStr);
          if (removed) {
            logSecurityEvent(
                SecurityEventType.PERMISSION_CHANGED,
                Map.of(
                    "action",
                    "revoke",
                    "role",
                    roleName,
                    "resource",
                    resource,
                    "permission",
                    permission.name()));

            logger.info(
                "Permission revoked: {} -> {} ({}) for {}",
                roleName,
                resource,
                permission,
                context.getIdentifier());
          }
        }
      }

      // Clear permission cache
      permissionCache.clear();
    } finally {
      roleLock.writeLock().unlock();
    }
  }

  @Override
  public void logAccess(
      SecurityContext context, String resource, String operation, boolean success, String details) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return;
    }

    SecurityEventType eventType =
        success ? SecurityEventType.ACCESS_GRANTED : SecurityEventType.ACCESS_DENIED;

    Map<String, Object> eventDetails = new HashMap<>();
    eventDetails.put("contextId", context.getIdentifier());
    eventDetails.put("resource", resource);
    eventDetails.put("operation", operation);
    eventDetails.put("details", details);

    logSecurityEvent(eventType, eventDetails);
  }

  @Override
  public List<String> getAuditLog(SecurityContext context, String resource) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return Collections.emptyList();
    }

    logLock.readLock().lock();
    try {
      return securityEventLog.stream()
          .filter(
              event ->
                  (event.type == SecurityEventType.ACCESS_GRANTED
                          || event.type == SecurityEventType.ACCESS_DENIED)
                      && event.details.containsKey("resource")
                      && resource.equals(event.details.get("resource")))
          .map(
              event ->
                  String.format(
                      "%s - %s - %s - %s - %s",
                      event.timestamp,
                      event.type,
                      event.username,
                      event.details.getOrDefault("operation", ""),
                      event.details.getOrDefault("details", "")))
          .collect(Collectors.toList());
    } finally {
      logLock.readLock().unlock();
    }
  }

  @Override
  public boolean isInitialized() {
    return initialized;
  }

  @Override
  public SecurityResult registerUser(String username, String password, Set<String> roles) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("User registration failed", "Security adapter not initialized");
    }

    userLock.writeLock().lock();
    try {
      if (users.containsKey(username)) {
        logger.warn("User registration failed: username already exists: {}", username);
        return SecurityResult.failure("User registration failed", "Username already exists");
      }

      String userId = UUID.randomUUID().toString();
      User user = new User(userId, username, password, new HashSet<>(roles));
      users.put(username, user);

      // Clear any cached permissions for this user
      permissionCache.remove(username);

      logSecurityEvent(
          SecurityEventType.SECURITY_CONFIG_CHANGED,
          Map.of("action", "register_user", "username", username, "userId", userId));

      logger.info("User registered: {} ({})", username, userId);
      return SecurityResult.success("User registered successfully", Map.of("userId", userId));
    } finally {
      userLock.writeLock().unlock();
    }
  }

  @Override
  public SecurityResult updateUserRoles(String userId, Set<String> roles) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Role update failed", "Security adapter not initialized");
    }

    userLock.writeLock().lock();
    try {
      // Find user by ID
      Optional<User> userOpt = users.values().stream().filter(u -> u.id.equals(userId)).findFirst();

      if (userOpt.isEmpty()) {
        logger.warn("Role update failed: user not found: {}", userId);
        return SecurityResult.failure("Role update failed", "User not found");
      }

      User user = userOpt.get();
      user.roles.clear();
      user.roles.addAll(roles);

      // Clear any cached permissions for this user
      permissionCache.remove(user.username);

      logSecurityEvent(
          SecurityEventType.SECURITY_CONFIG_CHANGED,
          Map.of(
              "action",
              "update_roles",
              "username",
              user.username,
              "userId",
              userId,
              "roles",
              roles));

      logger.info("User roles updated: {} ({})", user.username, userId);
      return SecurityResult.success("Roles updated successfully");
    } finally {
      userLock.writeLock().unlock();
    }
  }

  @Override
  public SecurityResult updateUserPermissions(String userId, Set<String> permissions) {
    if (!initialized) {
      logger.warn("Security adapter not initialized");
      return SecurityResult.failure("Permission update failed", "Security adapter not initialized");
    }

    userLock.readLock().lock();
    try {
      // Find user by ID
      Optional<User> userOpt = users.values().stream().filter(u -> u.id.equals(userId)).findFirst();

      if (userOpt.isEmpty()) {
        logger.warn("Permission update failed: user not found: {}", userId);
        return SecurityResult.failure("Permission update failed", "User not found");
      }

      User user = userOpt.get();

      // Create or update a role specifically for this user
      String userSpecificRole = "USER_" + userId;

      roleLock.writeLock().lock();
      try {
        // Create the role if it doesn't exist
        Role role =
            roles.getOrDefault(userSpecificRole, new Role(userSpecificRole, new HashSet<>()));

        // Update permissions
        role.permissions.clear();
        role.permissions.addAll(permissions);

        // Save the role
        roles.put(userSpecificRole, role);

        // Ensure the user has this role
        userLock.readLock().unlock();
        userLock.writeLock().lock();
        try {
          if (!user.roles.contains(userSpecificRole)) {
            user.roles.add(userSpecificRole);
          }
        } finally {
          userLock.writeLock().unlock();
          userLock.readLock().lock();
        }

        // Clear permission cache
        permissionCache.clear();

        logSecurityEvent(
            SecurityEventType.PERMISSION_CHANGED,
            Map.of("action", "update_permissions", "username", user.username, "userId", userId));

        logger.info("User permissions updated: {} ({})", user.username, userId);
        return SecurityResult.success("Permissions updated successfully");
      } finally {
        roleLock.writeLock().unlock();
      }
    } finally {
      userLock.readLock().unlock();
    }
  }

  /**
   * Creates a SecurityContext for a component.
   *
   * @param componentId The component ID
   * @return A SecurityContext for the component
   */
  private SecurityContext createComponentContext(String componentId) {
    userLock.readLock().lock();
    try {
      // Look for a specific user for this component
      User componentUser =
          users.values().stream()
              .filter(u -> u.id.equals(componentId) || u.username.equals(componentId))
              .findFirst()
              .orElse(null);

      if (componentUser != null) {
        return new ComponentSecurityContext(
            componentId,
            new ArrayList<>(componentUser.roles),
            Map.of("type", "component", "username", componentUser.username));
      }

      // Component not found as user, create a default context
      return new ComponentSecurityContext(
          componentId, Collections.singletonList("COMPONENT"), Map.of("type", "component"));
    } finally {
      userLock.readLock().unlock();
    }
  }

  /**
   * Creates an authentication context for a user.
   *
   * @param user The user
   */
  private void createAuthenticationContext(User user) {
    // Get all permissions for the user's roles
    Set<String> permissions = new HashSet<>();

    roleLock.readLock().lock();
    try {
      for (String roleName : user.roles) {
        // Add direct permissions from the role
        if (roles.containsKey(roleName)) {
          permissions.addAll(roles.get(roleName).permissions);
        }

        // Add permissions from parent roles
        Set<String> visitedRoles = new HashSet<>();
        addParentRolePermissions(roleName, permissions, visitedRoles);
      }
    } finally {
      roleLock.readLock().unlock();
    }

    // Create authentication context
    currentAuthContext =
        new AuthenticationContext(
            user.id,
            user.username,
            user.roles,
            permissions,
            Instant.now(),
            DEFAULT_SESSION_VALIDITY,
            Map.of());
  }

  /**
   * Recursively adds permissions from parent roles.
   *
   * @param roleName The role name
   * @param permissions The set to add permissions to
   * @param visitedRoles Set of roles already visited
   */
  private void addParentRolePermissions(
      String roleName, Set<String> permissions, Set<String> visitedRoles) {
    // Prevent circular dependencies
    if (visitedRoles.contains(roleName)) {
      return;
    }
    visitedRoles.add(roleName);

    // Add permissions from parent roles
    Set<String> parents = roleHierarchy.getOrDefault(roleName, Collections.emptySet());
    for (String parent : parents) {
      if (roles.containsKey(parent)) {
        permissions.addAll(roles.get(parent).permissions);
        addParentRolePermissions(parent, permissions, visitedRoles);
      }
    }
  }

  /**
   * Gets a role by name.
   *
   * @param roleName The role name
   * @return The role, or a default role if not found
   */
  private Role getRole(String roleName) {
    roleLock.readLock().lock();
    try {
      return roles.getOrDefault(roleName, new Role(roleName, Collections.emptySet()));
    } finally {
      roleLock.readLock().unlock();
    }
  }

  /**
   * Updates the authentication cache for a user.
   *
   * @param username The username
   * @param password The password
   */
  private void updateAuthCache(String username, String password) {
    // Check if cache is full
    if (authCache.size() >= MAX_AUTH_CACHE_SIZE && !authCache.containsKey(username)) {
      // Evict the oldest entry
      String oldestUsername = null;
      Instant oldestAccess = Instant.MAX;

      for (Map.Entry<String, AuthenticationCache> entry : authCache.entrySet()) {
        Instant accessTime = entry.getValue().lastAccessTime;
        if (accessTime.isBefore(oldestAccess)) {
          oldestAccess = accessTime;
          oldestUsername = entry.getKey();
        }
      }

      if (oldestUsername != null) {
        authCache.remove(oldestUsername);
        logger.debug("Evicted authentication cache entry: {}", oldestUsername);
      }
    }

    // Add or update the cache entry
    authCache.put(username, new AuthenticationCache(password));
    logger.debug("Updated authentication cache for: {}", username);
  }

  /**
   * Records a failed authentication attempt.
   *
   * @param username The username
   */
  private void recordFailedAttempt(String username) {
    FailedAuthAttempt attempt =
        failedAttempts.computeIfAbsent(username, k -> new FailedAuthAttempt());
    attempt.count++;
    attempt.lastAttemptTime = Instant.now();

    logger.debug(
        "Failed authentication attempt recorded for {}: {} attempts", username, attempt.count);

    if (attempt.count >= MAX_FAILED_ATTEMPTS) {
      logger.warn("Account locked due to too many failed attempts: {}", username);
    }
  }

  /**
   * Clears failed authentication attempts for a user.
   *
   * @param username The username
   */
  private void clearFailedAttempts(String username) {
    failedAttempts.remove(username);
    logger.debug("Cleared failed authentication attempts for: {}", username);
  }

  /**
   * Checks if an account is locked due to too many failed attempts.
   *
   * @param username The username
   * @return true if the account is locked
   */
  private boolean isAccountLocked(String username) {
    if (!failedAttempts.containsKey(username)) {
      return false;
    }

    FailedAuthAttempt attempt = failedAttempts.get(username);
    if (attempt.count < MAX_FAILED_ATTEMPTS) {
      return false;
    }

    // Check if the lockout period has passed
    Instant lockoutExpiry = attempt.lastAttemptTime.plus(LOCKOUT_DURATION);
    boolean isLocked = Instant.now().isBefore(lockoutExpiry);

    // If lockout period has passed, clear the failed attempts
    if (!isLocked) {
      clearFailedAttempts(username);
    }

    return isLocked;
  }

  /**
   * Gets performance metrics for this adapter.
   *
   * @return A map containing performance metrics
   */
  public Map<String, Object> getPerformanceMetrics() {
    Map<String, Object> metrics = new HashMap<>();

    metrics.put("authenticationCount", authenticationCount.get());
    metrics.put("authCacheHits", authCacheHits.get());
    metrics.put(
        "authCacheHitRatio",
        authenticationCount.get() > 0
            ? (double) authCacheHits.get() / authenticationCount.get()
            : 0.0);
    metrics.put("validationCount", validationCount.get());
    metrics.put("permissionCheckCount", permissionCheckCount.get());
    metrics.put("permissionCacheHits", permissionCacheHits.get());
    metrics.put(
        "permissionCacheHitRatio",
        permissionCheckCount.get() > 0
            ? (double) permissionCacheHits.get() / permissionCheckCount.get()
            : 0.0);
    metrics.put("tokenIssuedCount", tokenIssuedCount.get());
    metrics.put("tokenRevokedCount", tokenRevokedCount.get());
    metrics.put("securityEventCount", securityEventCount.get());
    metrics.put("activeTokenCount", tokens.size());
    metrics.put("userCount", users.size());
    metrics.put("roleCount", roles.size());

    return metrics;
  }

  /** Resets the performance metrics. */
  public void resetPerformanceMetrics() {
    authenticationCount.set(0);
    authCacheHits.set(0);
    validationCount.set(0);
    permissionCheckCount.set(0);
    permissionCacheHits.set(0);
    tokenIssuedCount.set(0);
    tokenRevokedCount.set(0);
    securityEventCount.set(0);

    logger.info("Performance metrics reset");
  }

  /** Represents a user in the system. */
  private static class User {
    private final String id;
    private final String username;
    private final String password;
    private final Set<String> roles;

    User(String id, String username, String password, Set<String> roles) {
      this.id = id;
      this.username = username;
      this.password = password;
      this.roles = roles;
    }
  }

  /** Represents a role in the system. */
  private static class Role {
    private final String name;
    private final Set<String> permissions;

    Role(String name, Set<String> permissions) {
      this.name = name;
      this.permissions = permissions;
    }
  }

  /** Represents a security token. */
  private static class Token {
    private final String id;
    private final String username;
    private final List<String> permissions;
    private final Instant expiryTime;
    private Instant lastAccessTime;

    Token(String id, String username, List<String> permissions, Instant expiryTime) {
      this.id = id;
      this.username = username;
      this.permissions = permissions;
      this.expiryTime = expiryTime;
      this.lastAccessTime = Instant.now();
    }

    boolean isExpired() {
      return Instant.now().isAfter(expiryTime);
    }
  }

  /** Represents a cached authentication result. */
  private static class AuthenticationCache {
    private final String password;
    private Instant lastAccessTime;

    AuthenticationCache(String password) {
      this.password = password;
      this.lastAccessTime = Instant.now();
    }

    boolean isValid() {
      // Cache entry is valid for 1 hour
      return Instant.now().isBefore(lastAccessTime.plus(Duration.ofHours(1)));
    }
  }

  /** Represents failed authentication attempts. */
  private static class FailedAuthAttempt {
    private int count;
    private Instant lastAttemptTime;

    FailedAuthAttempt() {
      this.count = 0;
      this.lastAttemptTime = Instant.now();
    }
  }

  /** Represents a security event. */
  private static class SecurityEvent {
    private final String id;
    private final Instant timestamp;
    private final SecurityEventType type;
    private final String username;
    private final Map<String, Object> details;

    SecurityEvent(
        String id,
        Instant timestamp,
        SecurityEventType type,
        String username,
        Map<String, Object> details) {
      this.id = id;
      this.timestamp = timestamp;
      this.type = type;
      this.username = username;
      this.details = details;
    }
  }

  /** Implementation of the SecurityContext interface for components. */
  private static class ComponentSecurityContext implements SecurityContext {
    private final String identifier;
    private final List<String> roles;
    private final Map<String, Object> attributes;

    ComponentSecurityContext(
        String identifier, List<String> roles, Map<String, Object> attributes) {
      this.identifier = identifier;
      this.roles = Collections.unmodifiableList(roles);
      this.attributes = Collections.unmodifiableMap(attributes);
    }

    @Override
    public String getIdentifier() {
      return identifier;
    }

    @Override
    public boolean hasRole(String roleName) {
      return roles.contains(roleName);
    }

    @Override
    public List<String> getRoles() {
      return roles;
    }

    @Override
    public Map<String, Object> getAttributes() {
      return attributes;
    }
  }
}
