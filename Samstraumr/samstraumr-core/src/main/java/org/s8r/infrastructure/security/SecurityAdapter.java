/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter for security operations in the infrastructure layer.
 * 
 * <p>This class provides security functionality like authentication and authorization.
 * It is primarily intended for testing and development environments.
 */
public class SecurityAdapter {
    
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<String, String> tokens = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Boolean>> permissions = new ConcurrentHashMap<>();
    
    /**
     * Creates a new SecurityAdapter instance.
     */
    public SecurityAdapter() {
        // Initialize with a default admin user
        users.put("admin", "admin123");
        Map<String, Boolean> adminPerms = new HashMap<>();
        adminPerms.put("*", true);
        permissions.put("admin", adminPerms);
    }
    
    /**
     * Authenticates a user with username and password.
     *
     * @param username The username
     * @param password The password
     * @return An Optional containing the authentication token if successful, or empty if failed
     */
    public Optional<String> authenticate(String username, String password) {
        if (!users.containsKey(username) || !users.get(username).equals(password)) {
            return Optional.empty();
        }
        
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return Optional.of(token);
    }
    
    /**
     * Validates an authentication token.
     *
     * @param token The token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return tokens.containsKey(token);
    }
    
    /**
     * Gets the username associated with a token.
     *
     * @param token The token
     * @return An Optional containing the username if the token is valid, or empty if not
     */
    public Optional<String> getUsernameFromToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }
    
    /**
     * Checks if a user has a permission.
     *
     * @param username The username
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String username, String permission) {
        if (!permissions.containsKey(username)) {
            return false;
        }
        
        Map<String, Boolean> userPerms = permissions.get(username);
        return userPerms.getOrDefault("*", false) || userPerms.getOrDefault(permission, false);
    }
    
    /**
     * Adds a user.
     *
     * @param username The username
     * @param password The password
     * @return true if the user was added, false if the username already exists
     */
    public boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        
        users.put(username, password);
        permissions.put(username, new HashMap<>());
        return true;
    }
    
    /**
     * Grants a permission to a user.
     *
     * @param username The username
     * @param permission The permission to grant
     * @return true if the permission was granted, false if the user doesn't exist
     */
    public boolean grantPermission(String username, String permission) {
        if (!permissions.containsKey(username)) {
            return false;
        }
        
        permissions.get(username).put(permission, true);
        return true;
    }
    
    /**
     * Revokes a permission from a user.
     *
     * @param username The username
     * @param permission The permission to revoke
     * @return true if the permission was revoked, false if the user doesn't exist
     */
    public boolean revokePermission(String username, String permission) {
        if (!permissions.containsKey(username)) {
            return false;
        }
        
        permissions.get(username).remove(permission);
        return true;
    }
    
    /**
     * Invalidates a token.
     *
     * @param token The token to invalidate
     * @return true if the token was invalidated, false if it wasn't valid
     */
    public boolean invalidateToken(String token) {
        return tokens.remove(token) != null;
    }
}