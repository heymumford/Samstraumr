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

import org.s8r.application.port.SecurityPort;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Optimized implementation of the SecurityPort interface.
 * 
 * <p>This adapter provides enhanced security operations with:
 * - Cached authentication results for improved performance
 * - Optimized role hierarchy traversal
 * - Fast token validation for repeated access patterns
 * - Concurrent access optimization with read-write locks
 * - Security event auditing
 */
public class OptimizedSecurityAdapter implements SecurityPort {

    private static final Duration DEFAULT_TOKEN_VALIDITY = Duration.ofHours(1);
    private static final int MAX_AUTH_CACHE_SIZE = 1000;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    
    private final Map<String, User> users;
    private final Map<String, Role> roles;
    private final Map<String, SecurityToken> tokens;
    private final Map<String, Set<String>> roleHierarchy;
    private final Map<String, Map<String, Boolean>> permissionCache;
    private final Map<String, AuthenticationCache> authCache;
    private final Map<String, FailedAuthAttempt> failedAttempts;
    
    private final ReadWriteLock userLock;
    private final ReadWriteLock roleLock;
    private final ReadWriteLock tokenLock;
    
    // Performance metrics
    private long authenticationCount;
    private long authCacheHits;
    private long validationCount;
    private long permissionCheckCount;
    private long permissionCacheHits;
    private long tokenIssuedCount;
    private long tokenRevokedCount;
    
    /**
     * Creates a new OptimizedSecurityAdapter.
     */
    public OptimizedSecurityAdapter() {
        this.users = new ConcurrentHashMap<>();
        this.roles = new ConcurrentHashMap<>();
        this.tokens = new ConcurrentHashMap<>();
        this.roleHierarchy = new ConcurrentHashMap<>();
        this.permissionCache = new ConcurrentHashMap<>();
        this.authCache = new ConcurrentHashMap<>();
        this.failedAttempts = new ConcurrentHashMap<>();
        
        this.userLock = new ReentrantReadWriteLock();
        this.roleLock = new ReentrantReadWriteLock();
        this.tokenLock = new ReentrantReadWriteLock();
        
        this.authenticationCount = 0;
        this.authCacheHits = 0;
        this.validationCount = 0;
        this.permissionCheckCount = 0;
        this.permissionCacheHits = 0;
        this.tokenIssuedCount = 0;
        this.tokenRevokedCount = 0;
        
        // Initialize with default roles
        initializeDefaultRoles();
    }
    
    /**
     * Initializes the default roles in the system.
     */
    private void initializeDefaultRoles() {
        // Create some basic roles
        Role adminRole = new Role("ADMIN", Set.of("manage_users", "manage_roles", "view_all", "edit_all"));
        Role userRole = new Role("USER", Set.of("view_own", "edit_own"));
        Role guestRole = new Role("GUEST", Set.of("view_public"));
        
        // Set up role hierarchy
        Set<String> adminParents = new HashSet<>();
        Set<String> userParents = new HashSet<>();
        userParents.add("GUEST");  // USER inherits from GUEST
        Set<String> guestParents = new HashSet<>();
        
        roleLock.writeLock().lock();
        try {
            roles.put("ADMIN", adminRole);
            roles.put("USER", userRole);
            roles.put("GUEST", guestRole);
            
            roleHierarchy.put("ADMIN", adminParents);
            roleHierarchy.put("USER", userParents);
            roleHierarchy.put("GUEST", guestParents);
        } finally {
            roleLock.writeLock().unlock();
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        authenticationCount++;
        
        // Check for account lockout due to failed attempts
        if (isAccountLocked(username)) {
            return false;
        }
        
        // Check authentication cache first
        if (authCache.containsKey(username)) {
            AuthenticationCache cacheEntry = authCache.get(username);
            if (cacheEntry.isValid() && cacheEntry.password.equals(password)) {
                authCacheHits++;
                
                // Update the cache access time
                cacheEntry.lastAccessTime = Instant.now();
                return true;
            }
        }
        
        // Proceed with regular authentication
        userLock.readLock().lock();
        try {
            if (!users.containsKey(username)) {
                recordFailedAttempt(username);
                return false;
            }
            
            User user = users.get(username);
            if (user.password.equals(password)) {
                // Valid authentication, update cache
                updateAuthCache(username, password);
                clearFailedAttempts(username);
                return true;
            } else {
                recordFailedAttempt(username);
                return false;
            }
        } finally {
            userLock.readLock().unlock();
        }
    }

    @Override
    public String issueToken(String username, List<String> requestedScopes) {
        userLock.readLock().lock();
        try {
            if (!users.containsKey(username)) {
                return null;
            }
            
            User user = users.get(username);
            List<String> grantedScopes = user.roles.stream()
                    .flatMap(role -> getRole(role).permissions.stream())
                    .filter(requestedScopes::contains)
                    .collect(Collectors.toList());
            
            String tokenId = UUID.randomUUID().toString();
            Instant expiryTime = Instant.now().plus(DEFAULT_TOKEN_VALIDITY);
            SecurityToken token = new SecurityToken(tokenId, username, grantedScopes, expiryTime);
            
            tokenLock.writeLock().lock();
            try {
                tokens.put(tokenId, token);
                tokenIssuedCount++;
                return tokenId;
            } finally {
                tokenLock.writeLock().unlock();
            }
        } finally {
            userLock.readLock().unlock();
        }
    }
    
    @Override
    public String issueToken(String username, Duration validity) {
        userLock.readLock().lock();
        try {
            if (!users.containsKey(username)) {
                return null;
            }
            
            User user = users.get(username);
            List<String> permissions = user.roles.stream()
                    .flatMap(role -> getRole(role).permissions.stream())
                    .collect(Collectors.toList());
            
            String tokenId = UUID.randomUUID().toString();
            Instant expiryTime = Instant.now().plus(validity);
            SecurityToken token = new SecurityToken(tokenId, username, permissions, expiryTime);
            
            tokenLock.writeLock().lock();
            try {
                tokens.put(tokenId, token);
                tokenIssuedCount++;
                return tokenId;
            } finally {
                tokenLock.writeLock().unlock();
            }
        } finally {
            userLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean validateToken(String tokenId) {
        validationCount++;
        
        tokenLock.readLock().lock();
        try {
            if (!tokens.containsKey(tokenId)) {
                return false;
            }
            
            SecurityToken token = tokens.get(tokenId);
            if (token.isExpired()) {
                // Token has expired, remove it
                tokenLock.readLock().unlock();
                tokenLock.writeLock().lock();
                try {
                    tokens.remove(tokenId);
                    tokenRevokedCount++;
                    return false;
                } finally {
                    tokenLock.writeLock().unlock();
                }
            }
            
            // Update the token's last access time
            token.lastAccessTime = Instant.now();
            return true;
        } finally {
            if (tokenLock.readLock().isHeldByCurrentThread()) {
                tokenLock.readLock().unlock();
            }
        }
    }
    
    @Override
    public Optional<Set<String>> getTokenPermissions(String tokenId) {
        tokenLock.readLock().lock();
        try {
            if (!tokens.containsKey(tokenId) || tokens.get(tokenId).isExpired()) {
                return Optional.empty();
            }
            
            SecurityToken token = tokens.get(tokenId);
            return Optional.of(new HashSet<>(token.permissions));
        } finally {
            tokenLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean checkPermission(String username, String permission) {
        permissionCheckCount++;
        
        // Check permission cache first
        String cacheKey = username + ":" + permission;
        Map<String, Boolean> userPermCache = permissionCache.computeIfAbsent(username, k -> new ConcurrentHashMap<>());
        
        if (userPermCache.containsKey(permission)) {
            permissionCacheHits++;
            return userPermCache.get(permission);
        }
        
        // Cache miss, perform the check
        userLock.readLock().lock();
        try {
            if (!users.containsKey(username)) {
                userPermCache.put(permission, false);
                return false;
            }
            
            User user = users.get(username);
            
            // Check each role the user has
            for (String roleName : user.roles) {
                if (hasPermissionInRoleHierarchy(roleName, permission)) {
                    userPermCache.put(permission, true);
                    return true;
                }
            }
            
            userPermCache.put(permission, false);
            return false;
        } finally {
            userLock.readLock().unlock();
        }
    }
    
    /**
     * Checks if a role or any of its parent roles have a permission.
     *
     * @param roleName The role name
     * @param permission The permission to check
     * @return true if the role or its parents have the permission
     */
    private boolean hasPermissionInRoleHierarchy(String roleName, String permission) {
        Set<String> visitedRoles = new HashSet<>();
        return hasPermissionInRoleHierarchyRecursive(roleName, permission, visitedRoles);
    }
    
    /**
     * Recursive helper for checking permissions in the role hierarchy.
     *
     * @param roleName The role name
     * @param permission The permission to check
     * @param visitedRoles Set of roles already visited
     * @return true if the role or its parents have the permission
     */
    private boolean hasPermissionInRoleHierarchyRecursive(String roleName, String permission, Set<String> visitedRoles) {
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
            if (role.permissions.contains(permission)) {
                return true;
            }
            
            // Check parent roles
            Set<String> parents = roleHierarchy.getOrDefault(roleName, Collections.emptySet());
            for (String parent : parents) {
                if (hasPermissionInRoleHierarchyRecursive(parent, permission, visitedRoles)) {
                    return true;
                }
            }
            
            return false;
        } finally {
            roleLock.readLock().unlock();
        }
    }
    
    @Override
    public boolean revokeToken(String tokenId) {
        tokenLock.writeLock().lock();
        try {
            boolean removed = tokens.remove(tokenId) != null;
            if (removed) {
                tokenRevokedCount++;
            }
            return removed;
        } finally {
            tokenLock.writeLock().unlock();
        }
    }
    
    @Override
    public void addUser(String username, String password, List<String> roles) {
        userLock.writeLock().lock();
        try {
            users.put(username, new User(username, password, new HashSet<>(roles)));
            
            // Clear any cached permissions for this user
            permissionCache.remove(username);
        } finally {
            userLock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean removeUser(String username) {
        userLock.writeLock().lock();
        try {
            boolean removed = users.remove(username) != null;
            
            if (removed) {
                // Remove any tokens for this user
                tokenLock.writeLock().lock();
                try {
                    tokens.entrySet().removeIf(entry -> entry.getValue().username.equals(username));
                } finally {
                    tokenLock.writeLock().unlock();
                }
                
                // Clear any cached permissions and authentication
                permissionCache.remove(username);
                authCache.remove(username);
                failedAttempts.remove(username);
            }
            
            return removed;
        } finally {
            userLock.writeLock().unlock();
        }
    }
    
    @Override
    public void addRole(String roleName, Set<String> permissions) {
        roleLock.writeLock().lock();
        try {
            roles.put(roleName, new Role(roleName, new HashSet<>(permissions)));
            
            if (!roleHierarchy.containsKey(roleName)) {
                roleHierarchy.put(roleName, new HashSet<>());
            }
            
            // Clear permission cache since role definitions changed
            permissionCache.clear();
        } finally {
            roleLock.writeLock().unlock();
        }
    }
    
    @Override
    public void addRoleParent(String roleName, String parentRoleName) {
        roleLock.writeLock().lock();
        try {
            if (!roles.containsKey(roleName) || !roles.containsKey(parentRoleName)) {
                return;
            }
            
            Set<String> parents = roleHierarchy.computeIfAbsent(roleName, k -> new HashSet<>());
            parents.add(parentRoleName);
            
            // Clear permission cache since role hierarchy changed
            permissionCache.clear();
        } finally {
            roleLock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<Map<String, Object>> getUserInfo(String username) {
        userLock.readLock().lock();
        try {
            if (!users.containsKey(username)) {
                return Optional.empty();
            }
            
            User user = users.get(username);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.username);
            userInfo.put("roles", user.roles);
            
            return Optional.of(userInfo);
        } finally {
            userLock.readLock().unlock();
        }
    }
    
    @Override
    public Optional<Map<String, Object>> getRoleInfo(String roleName) {
        roleLock.readLock().lock();
        try {
            if (!roles.containsKey(roleName)) {
                return Optional.empty();
            }
            
            Role role = roles.get(roleName);
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("name", role.name);
            roleInfo.put("permissions", role.permissions);
            
            if (roleHierarchy.containsKey(roleName)) {
                roleInfo.put("parents", roleHierarchy.get(roleName));
            }
            
            return Optional.of(roleInfo);
        } finally {
            roleLock.readLock().unlock();
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
            }
        }
        
        // Add or update the cache entry
        authCache.put(username, new AuthenticationCache(password));
    }
    
    /**
     * Records a failed authentication attempt.
     *
     * @param username The username
     */
    private void recordFailedAttempt(String username) {
        FailedAuthAttempt attempt = failedAttempts.computeIfAbsent(username, k -> new FailedAuthAttempt());
        attempt.count++;
        attempt.lastAttemptTime = Instant.now();
    }
    
    /**
     * Clears failed authentication attempts for a user.
     *
     * @param username The username
     */
    private void clearFailedAttempts(String username) {
        failedAttempts.remove(username);
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
        return Instant.now().isBefore(lockoutExpiry);
    }
    
    /**
     * Gets performance metrics for this adapter.
     *
     * @return A map containing performance metrics
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("authenticationCount", authenticationCount);
        metrics.put("authCacheHits", authCacheHits);
        metrics.put("authCacheHitRatio", authenticationCount > 0 ? (double) authCacheHits / authenticationCount : 0.0);
        metrics.put("validationCount", validationCount);
        metrics.put("permissionCheckCount", permissionCheckCount);
        metrics.put("permissionCacheHits", permissionCacheHits);
        metrics.put("permissionCacheHitRatio", permissionCheckCount > 0 ? (double) permissionCacheHits / permissionCheckCount : 0.0);
        metrics.put("tokenIssuedCount", tokenIssuedCount);
        metrics.put("tokenRevokedCount", tokenRevokedCount);
        metrics.put("activeTokenCount", tokens.size());
        metrics.put("userCount", users.size());
        metrics.put("roleCount", roles.size());
        
        return metrics;
    }
    
    /**
     * Resets the performance metrics.
     */
    public void resetPerformanceMetrics() {
        authenticationCount = 0;
        authCacheHits = 0;
        validationCount = 0;
        permissionCheckCount = 0;
        permissionCacheHits = 0;
        tokenIssuedCount = 0;
        tokenRevokedCount = 0;
    }
    
    /**
     * Represents a user in the system.
     */
    private static class User {
        private final String username;
        private final String password;
        private final Set<String> roles;
        
        User(String username, String password, Set<String> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }
    }
    
    /**
     * Represents a role in the system.
     */
    private static class Role {
        private final String name;
        private final Set<String> permissions;
        
        Role(String name, Set<String> permissions) {
            this.name = name;
            this.permissions = permissions;
        }
    }
    
    /**
     * Represents a security token.
     */
    private static class SecurityToken {
        private final String id;
        private final String username;
        private final List<String> permissions;
        private final Instant expiryTime;
        private Instant lastAccessTime;
        
        SecurityToken(String id, String username, List<String> permissions, Instant expiryTime) {
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
    
    /**
     * Represents a cached authentication result.
     */
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
    
    /**
     * Represents failed authentication attempts.
     */
    private static class FailedAuthAttempt {
        private int count;
        private Instant lastAttemptTime;
        
        FailedAuthAttempt() {
            this.count = 0;
            this.lastAttemptTime = Instant.now();
        }
    }
}