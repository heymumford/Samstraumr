/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.mock;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.s8r.application.port.SecurityPort;

/**
 * Mock implementation of the SecurityPort interface for testing purposes.
 * This adapter simulates security operations for integration testing.
 */
public class MockSecurityAdapter implements SecurityPort {
    
    private static final Logger LOGGER = Logger.getLogger(MockSecurityAdapter.class.getName());
    
    private final Map<String, UserEntry> users = new ConcurrentHashMap<>();
    private final Map<String, TokenEntry> tokens = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> auditLog = new ArrayList<>();
    private final Map<String, Map<String, Object>> securityConfig = new ConcurrentHashMap<>();
    
    private AuthenticationContext currentAuthContext = null;
    private boolean initialized = false;
    
    /**
     * Creates a new instance of the mock adapter.
     */
    public MockSecurityAdapter() {
        // Initialize with some default configuration
        securityConfig.put("global", new HashMap<>());
        securityConfig.get("global").put("maxLoginAttempts", 5);
        securityConfig.get("global").put("defaultTokenValidity", Duration.ofHours(1));
        securityConfig.get("global").put("auditLogEnabled", true);
    }
    
    @Override
    public SecurityResult authenticate(String username, String password) {
        if (!users.containsKey(username)) {
            logSecurityEvent(SecurityEventType.LOGIN_FAILURE, Map.of(
                "username", username,
                "reason", "User not found"
            ));
            return SecurityResult.failure("Authentication failed", "User not found");
        }
        
        UserEntry user = users.get(username);
        
        if (!user.password.equals(password)) {
            user.failedLoginAttempts++;
            
            logSecurityEvent(SecurityEventType.LOGIN_FAILURE, Map.of(
                "username", username,
                "reason", "Invalid credentials",
                "failedAttempts", user.failedLoginAttempts
            ));
            
            return SecurityResult.failure("Authentication failed", "Invalid credentials");
        }
        
        // Reset failed login attempts
        user.failedLoginAttempts = 0;
        
        // Create authentication context
        currentAuthContext = createAuthContext(user);
        
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("username", username);
        eventDetails.put("roles", String.join(",", user.roles));
        logSecurityEvent(SecurityEventType.LOGIN_SUCCESS, eventDetails);
        
        return SecurityResult.success("Authentication successful", Map.of(
            "username", username,
            "userId", user.userId,
            "roles", String.join(",", user.roles)
        ));
    }
    
    @Override
    public SecurityResult authenticateWithToken(String token) {
        if (!tokens.containsKey(token)) {
            return SecurityResult.failure("Token authentication failed", "Invalid token");
        }
        
        TokenEntry tokenEntry = tokens.get(token);
        
        // Check if token is expired
        if (tokenEntry.expiresAt.isBefore(Instant.now())) {
            tokens.remove(token);
            return SecurityResult.failure("Token authentication failed", "Token expired");
        }
        
        // Check if token is revoked
        if (tokenEntry.revoked) {
            return SecurityResult.failure("Token authentication failed", "Token revoked");
        }
        
        UserEntry user = users.get(tokenEntry.username);
        if (user == null) {
            return SecurityResult.failure("Token authentication failed", "User not found");
        }
        
        // Create authentication context
        currentAuthContext = createAuthContext(user);
        
        return SecurityResult.success("Token authentication successful", Map.of(
            "username", user.username,
            "userId", user.userId,
            "tokenId", token
        ));
    }
    
    @Override
    public SecurityResult logout() {
        AuthenticationContext auth = currentAuthContext;
        currentAuthContext = null;
        
        if (auth != null) {
            return SecurityResult.success("Logout successful", Map.of(
                "username", auth.getUsername()
            ));
        }
        
        return SecurityResult.failure("Logout failed", "No active session");
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
        if (currentAuthContext == null) {
            return SecurityResult.failure("Token generation failed", "No active session");
        }
        
        // Generate a random token
        String token = UUID.randomUUID().toString();
        String username = currentAuthContext.getUsername();
        
        // Store the token
        tokens.put(token, new TokenEntry(
            token,
            username,
            Instant.now(),
            Instant.now().plus(validity),
            false
        ));
        
        return SecurityResult.success("Token generated successfully", Map.of(
            "token", token,
            "validUntil", Instant.now().plus(validity).toString(),
            "username", username
        ));
    }
    
    @Override
    public SecurityResult validateToken(String token) {
        if (!tokens.containsKey(token)) {
            return SecurityResult.failure("Token validation failed", "Invalid token");
        }
        
        TokenEntry tokenEntry = tokens.get(token);
        
        // Check if token is expired
        if (tokenEntry.expiresAt.isBefore(Instant.now())) {
            tokens.remove(token);
            return SecurityResult.failure("Token validation failed", "Token expired");
        }
        
        // Check if token is revoked
        if (tokenEntry.revoked) {
            return SecurityResult.failure("Token validation failed", "Token revoked");
        }
        
        UserEntry user = users.get(tokenEntry.username);
        if (user == null) {
            return SecurityResult.failure("Token validation failed", "User not found");
        }
        
        return SecurityResult.success("Token validated successfully", Map.of(
            "username", user.username,
            "userId", user.userId,
            "tokenId", token,
            "validUntil", tokenEntry.expiresAt.toString()
        ));
    }
    
    @Override
    public SecurityResult revokeToken(String token) {
        if (!tokens.containsKey(token)) {
            return SecurityResult.failure("Token revocation failed", "Invalid token");
        }
        
        TokenEntry tokenEntry = tokens.get(token);
        tokenEntry.revoked = true;
        
        return SecurityResult.success("Token revoked successfully");
    }
    
    @Override
    public SecurityResult checkComponentAccess(String componentId, String resourceId, String operationType) {
        if (currentAuthContext == null) {
            return SecurityResult.failure("Access check failed", "No active session");
        }
        
        // Determine required permission based on resource and operation
        String requiredPermission = determineRequiredPermission(resourceId, operationType);
        
        // Check if user has the required permission
        if (requiredPermission.equals("ADMIN") && !currentAuthContext.hasRole("ADMIN")) {
            return SecurityResult.failure("Access denied", "Insufficient permissions", Map.of(
                "requiredPermission", "ADMIN",
                "resource", resourceId,
                "operation", operationType
            ));
        }
        
        // For test purposes, we'll implement some basic access control rules
        if (resourceId.startsWith("system/admin/") && !currentAuthContext.hasRole("ADMIN")) {
            return SecurityResult.failure("Access denied", "Insufficient permissions", Map.of(
                "requiredPermission", "ADMIN",
                "resource", resourceId,
                "operation", operationType
            ));
        }
        
        return SecurityResult.success("Access granted", Map.of(
            "resource", resourceId,
            "operation", operationType
        ));
    }
    
    @Override
    public SecurityResult logSecurityEvent(SecurityEventType eventType, Map<String, Object> details) {
        Map<String, Object> eventRecord = new HashMap<>(details);
        eventRecord.put("eventType", eventType.name());
        eventRecord.put("timestamp", Instant.now().toString());
        eventRecord.put("eventId", UUID.randomUUID().toString());
        
        if (currentAuthContext != null) {
            eventRecord.putIfAbsent("username", currentAuthContext.getUsername());
            eventRecord.putIfAbsent("userId", currentAuthContext.getUserId());
        }
        
        auditLog.add(eventRecord);
        
        LOGGER.info("Security event logged: " + eventType + " - " + details);
        
        return SecurityResult.success("Security event logged");
    }
    
    @Override
    public List<Map<String, Object>> getSecurityAuditLog(Instant from, Instant to) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Map<String, Object> event : auditLog) {
            String timestampStr = (String) event.get("timestamp");
            
            if (timestampStr != null) {
                try {
                    Instant timestamp = Instant.parse(timestampStr);
                    if ((from == null || timestamp.isAfter(from) || timestamp.equals(from)) && 
                        (to == null || timestamp.isBefore(to) || timestamp.equals(to))) {
                        result.add(new HashMap<>(event));
                    }
                } catch (Exception e) {
                    LOGGER.warning("Invalid timestamp format in audit log: " + timestampStr);
                }
            }
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getSecurityConfig(String componentId) {
        return securityConfig.getOrDefault(componentId, securityConfig.get("global"));
    }
    
    @Override
    public boolean hasPermission(SecurityContext context, String resource, Permission permission) {
        if (context == null) {
            return false;
        }
        
        // For testing purposes, we'll implement some basic rules
        if (permission == Permission.ADMIN && !context.hasRole("ADMIN")) {
            return false;
        }
        
        if (resource.startsWith("system/admin/") && !context.hasRole("ADMIN")) {
            return false;
        }
        
        // For regular users
        if (context.hasRole("USER")) {
            if (permission == Permission.READ || permission == Permission.LIST) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void grantPermission(SecurityContext context, String resource, Permission permission) {
        // In a real implementation, this would store permissions in a database
        LOGGER.info("Granted permission " + permission + " on " + resource + " to " + context.getIdentifier());
    }
    
    @Override
    public void revokePermission(SecurityContext context, String resource, Permission permission) {
        // In a real implementation, this would remove permissions from a database
        LOGGER.info("Revoked permission " + permission + " on " + resource + " from " + context.getIdentifier());
    }
    
    @Override
    public void logAccess(SecurityContext context, String resource, String operation, boolean success, String details) {
        Map<String, Object> accessRecord = new HashMap<>();
        accessRecord.put("username", context.getIdentifier());
        accessRecord.put("resource", resource);
        accessRecord.put("operation", operation);
        accessRecord.put("success", success);
        accessRecord.put("details", details);
        accessRecord.put("timestamp", Instant.now().toString());
        
        auditLog.add(accessRecord);
        
        LOGGER.info("Access logged: " + context.getIdentifier() + " " + 
                   (success ? "succeeded" : "failed") + " " + operation + " on " + resource);
    }
    
    @Override
    public List<String> getAuditLog(SecurityContext context, String resource) {
        // Check if the context has permission to view audit logs
        if (!context.hasRole("ADMIN") && !context.hasRole("AUDITOR")) {
            return List.of("Access denied: Insufficient permissions to view audit logs");
        }
        
        return auditLog.stream()
            .filter(event -> resource == null || event.getOrDefault("resource", "").equals(resource))
            .map(event -> event.toString())
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public SecurityResult registerUser(String username, String password, Set<String> roles) {
        if (users.containsKey(username)) {
            return SecurityResult.failure("User registration failed", "Username already exists");
        }
        
        String userId = UUID.randomUUID().toString();
        
        // Create permissions based on roles
        Set<String> permissions = new HashSet<>();
        for (String role : roles) {
            permissions.addAll(getPermissionsForRole(role));
        }
        
        users.put(username, new UserEntry(userId, username, password, roles, permissions));
        
        return SecurityResult.success("User registered successfully", Map.of(
            "userId", userId,
            "username", username
        ));
    }
    
    @Override
    public SecurityResult updateUserRoles(String userId, Set<String> roles) {
        // Find user by ID
        Optional<UserEntry> userOpt = users.values().stream()
            .filter(u -> u.userId.equals(userId))
            .findFirst();
        
        if (userOpt.isEmpty()) {
            return SecurityResult.failure("Update roles failed", "User not found");
        }
        
        UserEntry user = userOpt.get();
        
        // Update roles
        user.roles.clear();
        user.roles.addAll(roles);
        
        // Update permissions based on new roles
        user.permissions.clear();
        for (String role : roles) {
            user.permissions.addAll(getPermissionsForRole(role));
        }
        
        return SecurityResult.success("User roles updated");
    }
    
    @Override
    public SecurityResult updateUserPermissions(String userId, Set<String> permissions) {
        // Find user by ID
        Optional<UserEntry> userOpt = users.values().stream()
            .filter(u -> u.userId.equals(userId))
            .findFirst();
        
        if (userOpt.isEmpty()) {
            return SecurityResult.failure("Update permissions failed", "User not found");
        }
        
        UserEntry user = userOpt.get();
        
        // Update permissions
        user.permissions.clear();
        user.permissions.addAll(permissions);
        
        return SecurityResult.success("User permissions updated");
    }
    
    @Override
    public SecurityResult initialize() {
        initialized = true;
        return SecurityResult.success("Security subsystem initialized");
    }
    
    @Override
    public SecurityResult shutdown() {
        initialized = false;
        currentAuthContext = null;
        return SecurityResult.success("Security subsystem shut down");
    }
    
    /**
     * Class representing a user in the system.
     */
    private static class UserEntry {
        private final String userId;
        private final String username;
        private final String password;
        private final Set<String> roles;
        private final Set<String> permissions;
        private int failedLoginAttempts;
        
        public UserEntry(String userId, String username, String password, Set<String> roles, Set<String> permissions) {
            this.userId = userId;
            this.username = username;
            this.password = password;
            this.roles = new HashSet<>(roles);
            this.permissions = new HashSet<>(permissions);
            this.failedLoginAttempts = 0;
        }
    }
    
    /**
     * Class representing a security token.
     */
    private static class TokenEntry {
        private final String tokenId;
        private final String username;
        private final Instant createdAt;
        private final Instant expiresAt;
        private boolean revoked;
        
        public TokenEntry(String tokenId, String username, Instant createdAt, Instant expiresAt, boolean revoked) {
            this.tokenId = tokenId;
            this.username = username;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
            this.revoked = revoked;
        }
    }
    
    /**
     * Creates an authentication context for a user.
     * 
     * @param user The user entry
     * @return The authentication context
     */
    private AuthenticationContext createAuthContext(UserEntry user) {
        return new AuthenticationContext(
            user.userId,
            user.username,
            new HashSet<>(user.roles),
            new HashSet<>(user.permissions),
            Instant.now(),
            Duration.ofHours(1),
            new HashMap<>()
        );
    }
    
    /**
     * Determines the required permission for a resource and operation.
     * 
     * @param resourceId The resource being accessed
     * @param operationType The type of operation
     * @return The required permission
     */
    private String determineRequiredPermission(String resourceId, String operationType) {
        if (resourceId.startsWith("system/admin/")) {
            return "ADMIN";
        }
        
        if (operationType.equals("READ") || operationType.equals("LIST")) {
            return "READ";
        }
        
        if (operationType.equals("WRITE") || operationType.equals("UPDATE")) {
            return "WRITE";
        }
        
        if (operationType.equals("CREATE")) {
            return "CREATE";
        }
        
        if (operationType.equals("DELETE")) {
            return "DELETE";
        }
        
        if (operationType.equals("EXECUTE")) {
            return "EXECUTE";
        }
        
        return operationType;
    }
    
    /**
     * Gets the permissions associated with a role.
     * 
     * @param role The role name
     * @return The set of permissions for the role
     */
    private Set<String> getPermissionsForRole(String role) {
        switch (role) {
            case "ADMIN":
                return new HashSet<>(Arrays.asList(
                    "READ", "WRITE", "CREATE", "DELETE", "LIST", "EXECUTE", "ADMIN"
                ));
            case "USER":
                return new HashSet<>(Arrays.asList(
                    "READ", "LIST"
                ));
            case "MANAGER":
                return new HashSet<>(Arrays.asList(
                    "READ", "WRITE", "CREATE", "LIST"
                ));
            case "AUDITOR":
                return new HashSet<>(Arrays.asList(
                    "READ", "LIST", "AUDIT"
                ));
            default:
                return new HashSet<>();
        }
    }
}