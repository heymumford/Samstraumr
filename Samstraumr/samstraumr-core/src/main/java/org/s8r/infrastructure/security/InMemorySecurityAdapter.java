/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.security;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the SecurityPort interface.
 * <p>
 * This adapter provides a simple security implementation for development and testing purposes.
 * It stores all security-related information in memory and will lose data when the application is restarted.
 * <p>
 * For production use, this should be replaced with an adapter that connects to a proper security system.
 */
public class InMemorySecurityAdapter implements SecurityPort {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123"; // Only for development
    private static final Duration DEFAULT_TOKEN_VALIDITY = Duration.ofHours(1);

    private final Map<String, UserEntry> users = new ConcurrentHashMap<>();
    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Set<String>>> componentAccess = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> securityAuditLog = new CopyOnWriteArrayList<>();
    
    // Map for storing resource permissions by security context
    private final Map<String, Map<String, Set<Permission>>> resourcePermissions = new ConcurrentHashMap<>();
    // List for storing audit log entries
    private final List<String> accessAuditLog = new CopyOnWriteArrayList<>();

    private final ThreadLocal<AuthenticationContext> currentAuthContext = new ThreadLocal<>();
    private final LoggerPort logger;
    private boolean initialized = false;

    /**
     * Creates a new InMemorySecurityAdapter with the specified logger.
     *
     * @param logger The logger to use for logging security events
     */
    public InMemorySecurityAdapter(LoggerPort logger) {
        this.logger = logger;
    }

    /**
     * Internal class representing a user in the system.
     */
    private static class UserEntry {
        private final String userId;
        private final String username;
        private final String passwordHash;
        private final Set<String> roles;
        private final Set<String> permissions;
        private final Map<String, Object> attributes;

        public UserEntry(String userId, String username, String passwordHash, 
                        Set<String> roles, Set<String> permissions, 
                        Map<String, Object> attributes) {
            this.userId = userId;
            this.username = username;
            this.passwordHash = passwordHash;
            this.roles = new HashSet<>(roles);
            this.permissions = new HashSet<>(permissions);
            this.attributes = new HashMap<>(attributes);
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getPasswordHash() {
            return passwordHash;
        }

        public Set<String> getRoles() {
            return Collections.unmodifiableSet(roles);
        }

        public void setRoles(Set<String> roles) {
            this.roles.clear();
            this.roles.addAll(roles);
        }

        public Set<String> getPermissions() {
            return Collections.unmodifiableSet(permissions);
        }

        public void setPermissions(Set<String> permissions) {
            this.permissions.clear();
            this.permissions.addAll(permissions);
        }

        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(attributes);
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes.clear();
            this.attributes.putAll(attributes);
        }
    }

    /**
     * Internal class representing an authentication token in the system.
     */
    private static class TokenEntry {
        private final String token;
        private final String userId;
        private final Instant createdAt;
        private final Duration validity;
        private final boolean revoked;

        public TokenEntry(String token, String userId, Instant createdAt, Duration validity) {
            this.token = token;
            this.userId = userId;
            this.createdAt = createdAt;
            this.validity = validity;
            this.revoked = false;
        }

        public TokenEntry(TokenEntry original, boolean revoked) {
            this.token = original.token;
            this.userId = original.userId;
            this.createdAt = original.createdAt;
            this.validity = original.validity;
            this.revoked = revoked;
        }

        public String getToken() {
            return token;
        }

        public String getUserId() {
            return userId;
        }

        public Instant getCreatedAt() {
            return createdAt;
        }

        public Duration getValidity() {
            return validity;
        }

        public boolean isRevoked() {
            return revoked;
        }

        public boolean isValid() {
            return !revoked && createdAt.plus(validity).isAfter(Instant.now());
        }
    }

    /**
     * Simulates hashing a password. In a real implementation, this would use a secure
     * password hashing algorithm like BCrypt, PBKDF2, or Argon2.
     *
     * @param password The password to hash
     * @return A hashed representation of the password
     */
    private String hashPassword(String password) {
        // This is a placeholder. In a real system, use a proper password hashing algorithm
        return "HASHED:" + password;
    }

    /**
     * Verifies a password against its hashed version.
     *
     * @param password    The plain text password
     * @param storedHash  The stored hash to check against
     * @return True if the password matches the hash, false otherwise
     */
    private boolean verifyPassword(String password, String storedHash) {
        // This is a placeholder. In a real system, use the proper verification method
        return storedHash.equals("HASHED:" + password);
    }

    /**
     * Generates a random token.
     *
     * @return A new random token
     */
    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public SecurityResult authenticate(String username, String password) {
        if (username == null || password == null) {
            return SecurityResult.failure("Authentication failed", "Username or password cannot be null");
        }

        UserEntry user = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (user == null) {
            logSecurityEvent(SecurityEventType.LOGIN_FAILURE, Map.of(
                    "username", username,
                    "reason", "User not found"
            ));
            return SecurityResult.failure("Authentication failed", "Invalid username or password");
        }

        if (!verifyPassword(password, user.getPasswordHash())) {
            logSecurityEvent(SecurityEventType.LOGIN_FAILURE, Map.of(
                    "username", username,
                    "reason", "Invalid password"
            ));
            return SecurityResult.failure("Authentication failed", "Invalid username or password");
        }

        // Create authentication context and set it in thread local
        AuthenticationContext authContext = new AuthenticationContext(
                user.getUserId(),
                user.getUsername(),
                user.getRoles(),
                user.getPermissions(),
                Instant.now(),
                DEFAULT_TOKEN_VALIDITY,
                user.getAttributes()
        );

        currentAuthContext.set(authContext);

        logSecurityEvent(SecurityEventType.LOGIN_SUCCESS, Map.of(
                "userId", user.getUserId(),
                "username", username
        ));

        return SecurityResult.success("Authentication successful", Map.of(
                "userId", user.getUserId(),
                "roles", user.getRoles(),
                "permissions", user.getPermissions()
        ));
    }

    @Override
    public SecurityResult authenticateWithToken(String token) {
        if (token == null) {
            return SecurityResult.failure("Token authentication failed", "Token cannot be null");
        }

        TokenEntry tokenEntry = tokens.get(token);
        if (tokenEntry == null) {
            logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                    "token", token,
                    "result", "failure",
                    "reason", "Token not found"
            ));
            return SecurityResult.failure("Token authentication failed", "Invalid token");
        }

        if (!tokenEntry.isValid()) {
            logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                    "token", token,
                    "result", "failure",
                    "reason", tokenEntry.isRevoked() ? "Token revoked" : "Token expired"
            ));
            return SecurityResult.failure("Token authentication failed",
                    tokenEntry.isRevoked() ? "Token has been revoked" : "Token has expired");
        }

        UserEntry user = users.get(tokenEntry.getUserId());
        if (user == null) {
            logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                    "token", token,
                    "result", "failure",
                    "reason", "User not found"
            ));
            return SecurityResult.failure("Token authentication failed", "User not found");
        }

        // Create authentication context and set it in thread local
        AuthenticationContext authContext = new AuthenticationContext(
                user.getUserId(),
                user.getUsername(),
                user.getRoles(),
                user.getPermissions(),
                tokenEntry.getCreatedAt(),
                tokenEntry.getValidity(),
                user.getAttributes()
        );

        currentAuthContext.set(authContext);

        logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                "token", token,
                "result", "success",
                "userId", user.getUserId()
        ));

        return SecurityResult.success("Token authentication successful", Map.of(
                "userId", user.getUserId(),
                "roles", user.getRoles(),
                "permissions", user.getPermissions()
        ));
    }

    @Override
    public SecurityResult logout() {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null) {
            return SecurityResult.failure("Logout failed", "No active session");
        }

        // Remove from thread local
        currentAuthContext.remove();

        logSecurityEvent(SecurityEventType.LOGOUT, Map.of(
                "userId", authContext.getUserId(),
                "username", authContext.getUsername()
        ));

        return SecurityResult.success("Logout successful");
    }

    @Override
    public Optional<AuthenticationContext> getCurrentAuthContext() {
        return Optional.ofNullable(currentAuthContext.get());
    }

    @Override
    public boolean hasRole(String role) {
        AuthenticationContext authContext = currentAuthContext.get();
        return authContext != null && authContext.hasRole(role);
    }

    @Override
    public boolean hasPermission(String permission) {
        AuthenticationContext authContext = currentAuthContext.get();
        return authContext != null && authContext.hasPermission(permission);
    }

    @Override
    public boolean hasAnyRole(String... roles) {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null || roles == null) {
            return false;
        }

        for (String role : roles) {
            if (authContext.hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null || roles == null) {
            return false;
        }

        for (String role : roles) {
            if (!authContext.hasRole(role)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasAnyPermission(String... permissions) {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null || permissions == null) {
            return false;
        }

        for (String permission : permissions) {
            if (authContext.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasAllPermissions(String... permissions) {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null || permissions == null) {
            return false;
        }

        for (String permission : permissions) {
            if (!authContext.hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public SecurityResult generateToken(Duration validity) {
        AuthenticationContext authContext = currentAuthContext.get();
        if (authContext == null) {
            return SecurityResult.failure("Token generation failed", "No active session");
        }

        Duration tokenValidity = validity != null ? validity : DEFAULT_TOKEN_VALIDITY;
        String token = generateRandomToken();

        TokenEntry tokenEntry = new TokenEntry(
                token,
                authContext.getUserId(),
                Instant.now(),
                tokenValidity
        );

        tokens.put(token, tokenEntry);

        logSecurityEvent(SecurityEventType.TOKEN_ISSUED, Map.of(
                "token", token,
                "userId", authContext.getUserId(),
                "validity", tokenValidity.toString()
        ));

        return SecurityResult.success("Token generated successfully", Map.of(
                "token", token,
                "expiresAt", tokenEntry.getCreatedAt().plus(tokenValidity).toString()
        ));
    }

    @Override
    public SecurityResult validateToken(String token) {
        if (token == null) {
            return SecurityResult.failure("Token validation failed", "Token cannot be null");
        }

        TokenEntry tokenEntry = tokens.get(token);
        if (tokenEntry == null) {
            logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                    "token", token,
                    "result", "failure",
                    "reason", "Token not found"
            ));
            return SecurityResult.failure("Token validation failed", "Invalid token");
        }

        if (!tokenEntry.isValid()) {
            String reason = tokenEntry.isRevoked() ? "Token has been revoked" : "Token has expired";
            logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                    "token", token,
                    "result", "failure",
                    "reason", reason
            ));
            return SecurityResult.failure("Token validation failed", reason);
        }

        logSecurityEvent(SecurityEventType.TOKEN_VALIDATED, Map.of(
                "token", token,
                "result", "success",
                "userId", tokenEntry.getUserId()
        ));

        return SecurityResult.success("Token is valid", Map.of(
                "userId", tokenEntry.getUserId(),
                "expiresAt", tokenEntry.getCreatedAt().plus(tokenEntry.getValidity()).toString()
        ));
    }

    @Override
    public SecurityResult revokeToken(String token) {
        if (token == null) {
            return SecurityResult.failure("Token revocation failed", "Token cannot be null");
        }

        TokenEntry tokenEntry = tokens.get(token);
        if (tokenEntry == null) {
            return SecurityResult.failure("Token revocation failed", "Invalid token");
        }

        // Create a new entry with revoked=true
        TokenEntry revokedEntry = new TokenEntry(tokenEntry, true);
        tokens.put(token, revokedEntry);

        logSecurityEvent(SecurityEventType.TOKEN_EXPIRED, Map.of(
                "token", token,
                "userId", tokenEntry.getUserId(),
                "reason", "Manually revoked"
        ));

        return SecurityResult.success("Token revoked successfully");
    }

    @Override
    public SecurityResult checkComponentAccess(String componentId, String resourceId, String operationType) {
        if (componentId == null || resourceId == null || operationType == null) {
            return SecurityResult.failure("Access check failed", "Component ID, resource ID, and operation type cannot be null");
        }

        // Check if component has access to the resource
        Map<String, Set<String>> resourceAccess = componentAccess.get(componentId);
        if (resourceAccess == null) {
            logSecurityEvent(SecurityEventType.ACCESS_DENIED, Map.of(
                    "componentId", componentId,
                    "resourceId", resourceId,
                    "operationType", operationType,
                    "reason", "Component not registered"
            ));
            return SecurityResult.failure("Access denied", "Component not registered");
        }

        Set<String> allowedOperations = resourceAccess.get(resourceId);
        if (allowedOperations == null || !allowedOperations.contains(operationType)) {
            logSecurityEvent(SecurityEventType.ACCESS_DENIED, Map.of(
                    "componentId", componentId,
                    "resourceId", resourceId,
                    "operationType", operationType,
                    "reason", "Operation not allowed"
            ));
            return SecurityResult.failure("Access denied", "Operation not allowed");
        }

        logSecurityEvent(SecurityEventType.ACCESS_GRANTED, Map.of(
                "componentId", componentId,
                "resourceId", resourceId,
                "operationType", operationType
        ));

        return SecurityResult.success("Access granted");
    }

    @Override
    public SecurityResult logSecurityEvent(SecurityEventType eventType, Map<String, Object> details) {
        if (eventType == null) {
            return SecurityResult.failure("Event logging failed", "Event type cannot be null");
        }

        Map<String, Object> event = new HashMap<>();
        event.put("timestamp", Instant.now().toString());
        event.put("eventType", eventType.name());

        // Add current user if authenticated
        getCurrentAuthContext().ifPresent(ctx -> {
            event.put("userId", ctx.getUserId());
            event.put("username", ctx.getUsername());
        });

        // Add provided details
        if (details != null) {
            event.putAll(details);
        }

        securityAuditLog.add(Collections.unmodifiableMap(event));

        // Log to the system logger as well
        logger.debug("Security event: " + eventType.name() + " - " + details);

        return SecurityResult.success("Event logged successfully");
    }

    @Override
    public List<Map<String, Object>> getSecurityAuditLog(Instant from, Instant to) {
        // Require admin role to access security logs
        if (!hasRole(ADMIN_ROLE)) {
            logger.warn("Unauthorized attempt to access security audit logs");
            return Collections.emptyList();
        }

        if (from == null) {
            from = Instant.EPOCH;
        }

        if (to == null) {
            to = Instant.now();
        }

        final Instant finalFrom = from;
        final Instant finalTo = to;

        return securityAuditLog.stream()
                .filter(event -> {
                    String timestampStr = (String) event.get("timestamp");
                    Instant timestamp = Instant.parse(timestampStr);
                    return !timestamp.isBefore(finalFrom) && !timestamp.isAfter(finalTo);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSecurityConfig(String componentId) {
        // This is a simplified implementation. In a real system, this would fetch
        // component-specific security configuration from a secure store.
        Map<String, Object> config = new HashMap<>();
        config.put("componentId", componentId);
        config.put("allowedOperations", getAllowedOperations(componentId));
        config.put("securityLevel", "STANDARD");
        config.put("encryptionRequired", true);
        return config;
    }

    private Map<String, Set<String>> getAllowedOperations(String componentId) {
        return componentAccess.getOrDefault(componentId, Collections.emptyMap());
    }

    @Override
    public SecurityResult registerUser(String username, String password, Set<String> roles) {
        if (username == null || password == null) {
            return SecurityResult.failure("User registration failed", "Username and password cannot be null");
        }

        // Check if username already exists
        boolean userExists = users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));

        if (userExists) {
            return SecurityResult.failure("User registration failed", "Username already exists");
        }

        String userId = UUID.randomUUID().toString();
        String passwordHash = hashPassword(password);

        // Default to USER role if none provided
        Set<String> userRoles = roles != null && !roles.isEmpty() ? roles : Set.of(USER_ROLE);

        // Derive permissions from roles (in a real system, this would be more sophisticated)
        Set<String> permissions = derivePermissionsFromRoles(userRoles);

        UserEntry newUser = new UserEntry(
                userId,
                username,
                passwordHash,
                userRoles,
                permissions,
                new HashMap<>()
        );

        users.put(userId, newUser);

        logSecurityEvent(SecurityEventType.SECURITY_CONFIG_CHANGED, Map.of(
                "action", "userRegistered",
                "userId", userId,
                "username", username,
                "roles", userRoles.toString()
        ));

        return SecurityResult.success("User registered successfully", Map.of(
                "userId", userId,
                "roles", userRoles,
                "permissions", permissions
        ));
    }

    private Set<String> derivePermissionsFromRoles(Set<String> roles) {
        Set<String> permissions = new HashSet<>();

        // This is a simplified implementation. In a real system, this would likely
        // involve looking up role-permission mappings from a database.
        if (roles.contains(ADMIN_ROLE)) {
            permissions.addAll(Set.of(
                    "READ", "WRITE", "DELETE", "ADMIN", "CONFIGURE", "MANAGE_USERS", "VIEW_LOGS"
            ));
        } else if (roles.contains(USER_ROLE)) {
            permissions.addAll(Set.of(
                    "READ", "WRITE"
            ));
        }

        return permissions;
    }

    @Override
    public SecurityResult updateUserRoles(String userId, Set<String> roles) {
        if (userId == null || roles == null) {
            return SecurityResult.failure("Update failed", "User ID and roles cannot be null");
        }

        // Require admin role to update user roles
        if (!hasRole(ADMIN_ROLE)) {
            logSecurityEvent(SecurityEventType.ACCESS_DENIED, Map.of(
                    "action", "updateUserRoles",
                    "userId", userId,
                    "reason", "Insufficient permissions"
            ));
            return SecurityResult.failure("Update failed", "Insufficient permissions");
        }

        UserEntry user = users.get(userId);
        if (user == null) {
            return SecurityResult.failure("Update failed", "User not found");
        }

        // Update roles
        user.setRoles(roles);

        // Update permissions based on new roles
        Set<String> newPermissions = derivePermissionsFromRoles(roles);
        user.setPermissions(newPermissions);

        logSecurityEvent(SecurityEventType.PERMISSION_CHANGED, Map.of(
                "userId", userId,
                "username", user.getUsername(),
                "oldRoles", user.getRoles().toString(),
                "newRoles", roles.toString()
        ));

        return SecurityResult.success("User roles updated successfully", Map.of(
                "userId", userId,
                "roles", roles,
                "permissions", newPermissions
        ));
    }

    @Override
    public SecurityResult updateUserPermissions(String userId, Set<String> permissions) {
        if (userId == null || permissions == null) {
            return SecurityResult.failure("Update failed", "User ID and permissions cannot be null");
        }

        // Require admin role to update user permissions
        if (!hasRole(ADMIN_ROLE)) {
            logSecurityEvent(SecurityEventType.ACCESS_DENIED, Map.of(
                    "action", "updateUserPermissions",
                    "userId", userId,
                    "reason", "Insufficient permissions"
            ));
            return SecurityResult.failure("Update failed", "Insufficient permissions");
        }

        UserEntry user = users.get(userId);
        if (user == null) {
            return SecurityResult.failure("Update failed", "User not found");
        }

        // Update permissions
        user.setPermissions(permissions);

        logSecurityEvent(SecurityEventType.PERMISSION_CHANGED, Map.of(
                "userId", userId,
                "username", user.getUsername(),
                "oldPermissions", user.getPermissions().toString(),
                "newPermissions", permissions.toString()
        ));

        return SecurityResult.success("User permissions updated successfully", Map.of(
                "userId", userId,
                "permissions", permissions
        ));
    }

    @Override
    public SecurityResult initialize() {
        if (initialized) {
            return SecurityResult.success("Security system already initialized");
        }

        // Clear existing data
        users.clear();
        tokens.clear();
        componentAccess.clear();
        securityAuditLog.clear();

        // Create a default admin user
        registerUser(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, Set.of(ADMIN_ROLE));

        // Set up some default component access
        setupDefaultComponentAccess();

        initialized = true;

        logSecurityEvent(SecurityEventType.SECURITY_CONFIG_CHANGED, Map.of(
                "action", "initialize",
                "result", "success"
        ));

        return SecurityResult.success("Security system initialized");
    }

    private void setupDefaultComponentAccess() {
        // Set up some default access control for components
        // This is a simplified example. In a real system, this would be loaded from a configuration file
        // or database.

        // Component 1 can read and write to resources A and B
        Map<String, Set<String>> component1Access = new HashMap<>();
        component1Access.put("resourceA", Set.of("READ", "WRITE"));
        component1Access.put("resourceB", Set.of("READ", "WRITE"));
        componentAccess.put("component1", component1Access);

        // Component 2 can only read from resource A and B, but can read/write to resource C
        Map<String, Set<String>> component2Access = new HashMap<>();
        component2Access.put("resourceA", Set.of("READ"));
        component2Access.put("resourceB", Set.of("READ"));
        component2Access.put("resourceC", Set.of("READ", "WRITE"));
        componentAccess.put("component2", component2Access);
    }

    @Override
    public SecurityResult shutdown() {
        if (!initialized) {
            return SecurityResult.success("Security system not initialized");
        }

        // Invalidate all tokens
        for (Map.Entry<String, TokenEntry> entry : tokens.entrySet()) {
            if (entry.getValue().isValid()) {
                tokens.put(entry.getKey(), new TokenEntry(entry.getValue(), true));
            }
        }

        // Remove any authentication contexts
        currentAuthContext.remove();

        initialized = false;

        logSecurityEvent(SecurityEventType.SECURITY_CONFIG_CHANGED, Map.of(
                "action", "shutdown",
                "result", "success"
        ));

        return SecurityResult.success("Security system shut down");
    }
    
    @Override
    public boolean hasPermission(SecurityContext context, String resource, Permission permission) {
        if (context == null) {
            logger.error("Security context is null for permission check");
            return false;
        }
        
        String contextId = context.getIdentifier();
        Map<String, Set<Permission>> contextPermissions = resourcePermissions.getOrDefault(contextId, new HashMap<>());
        Set<Permission> resourcePerms = contextPermissions.getOrDefault(resource, new HashSet<>());
        
        // Check for ALL permission
        if (resourcePerms.contains(Permission.ALL)) {
            return true;
        }
        
        // Check for specific permission
        boolean hasPermission = resourcePerms.contains(permission);
        
        // Log this check
        String logMessage = String.format(
            "Permission check: context=%s, resource=%s, permission=%s, result=%s",
            contextId, resource, permission, hasPermission ? "GRANTED" : "DENIED"
        );
        logger.debug(logMessage);
        
        return hasPermission;
    }
    
    @Override
    public void grantPermission(SecurityContext context, String resource, Permission permission) {
        if (context == null) {
            logger.error("Security context is null for permission grant");
            return;
        }
        
        String contextId = context.getIdentifier();
        Map<String, Set<Permission>> contextPermissions = resourcePermissions.computeIfAbsent(
            contextId, k -> new ConcurrentHashMap<>()
        );
        
        Set<Permission> resourcePerms = contextPermissions.computeIfAbsent(
            resource, k -> new HashSet<>()
        );
        
        resourcePerms.add(permission);
        
        String logMessage = String.format(
            "Permission granted: context=%s, resource=%s, permission=%s",
            contextId, resource, permission
        );
        logger.debug(logMessage);
    }
    
    @Override
    public void revokePermission(SecurityContext context, String resource, Permission permission) {
        if (context == null) {
            logger.error("Security context is null for permission revocation");
            return;
        }
        
        String contextId = context.getIdentifier();
        Map<String, Set<Permission>> contextPermissions = resourcePermissions.get(contextId);
        if (contextPermissions == null) {
            return;
        }
        
        Set<Permission> resourcePerms = contextPermissions.get(resource);
        if (resourcePerms == null) {
            return;
        }
        
        resourcePerms.remove(permission);
        
        String logMessage = String.format(
            "Permission revoked: context=%s, resource=%s, permission=%s",
            contextId, resource, permission
        );
        logger.debug(logMessage);
    }
    
    @Override
    public void logAccess(SecurityContext context, String resource, String operation, boolean success, String details) {
        if (context == null) {
            logger.error("Security context is null for access logging");
            return;
        }
        
        String contextId = context.getIdentifier();
        String logEntry = String.format(
            "[%s] %s %s for resource '%s' by context '%s': %s",
            success ? "SUCCESS" : "VIOLATION",
            operation,
            success ? "granted" : "denied",
            resource,
            contextId,
            details
        );
        
        // Add to audit log
        accessAuditLog.add(logEntry);
        
        // Log to system logger
        if (success) {
            logger.info(logEntry);
        } else {
            logger.warn(logEntry);
        }
    }
    
    @Override
    public List<String> getAuditLog(SecurityContext context, String resource) {
        if (context == null) {
            logger.error("Security context is null for audit log request");
            return List.of();
        }
        
        // Filter log entries for the specified resource
        return accessAuditLog.stream()
            .filter(entry -> entry.contains(resource))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
}