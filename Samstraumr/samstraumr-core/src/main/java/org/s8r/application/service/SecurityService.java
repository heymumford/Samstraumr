/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service class that provides security-related functionality to the application layer.
 * <p>
 * This service uses the SecurityPort to interact with the security infrastructure.
 * It provides a simplified interface for common security operations and adds additional
 * business logic as needed.
 */
public class SecurityService {

    private final SecurityPort securityPort;
    private final LoggerPort logger;

    /**
     * Creates a new SecurityService with the specified dependencies.
     *
     * @param securityPort The security port implementation to use
     * @param logger       The logger to use for logging
     */
    public SecurityService(SecurityPort securityPort, LoggerPort logger) {
        this.securityPort = securityPort;
        this.logger = logger;
    }

    /**
     * Initializes the security system.
     */
    public void initialize() {
        logger.info("Initializing security service");
        SecurityPort.SecurityResult result = securityPort.initialize();
        if (!result.isSuccessful()) {
            logger.error("Failed to initialize security service: " + result.getReason().orElse("Unknown reason"));
        }
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username The username
     * @param password The password
     * @return True if authentication was successful, false otherwise
     */
    public boolean login(String username, String password) {
        logger.debug("Attempting login for user: " + username);
        SecurityPort.SecurityResult result = securityPort.authenticate(username, password);
        if (result.isSuccessful()) {
            logger.debug("User authenticated successfully: " + username);
            return true;
        } else {
            logger.debug("Authentication failed for user: " + username);
            return false;
        }
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        logger.debug("Logging out current user");
        securityPort.logout();
    }

    /**
     * Checks if the current user is authenticated.
     *
     * @return True if a user is currently authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return securityPort.getCurrentAuthContext().isPresent();
    }

    /**
     * Gets the username of the currently authenticated user.
     *
     * @return The username, or empty if no user is authenticated
     */
    public Optional<String> getCurrentUsername() {
        return securityPort.getCurrentAuthContext().map(SecurityPort.AuthenticationContext::getUsername);
    }

    /**
     * Checks if the current user has the specified role.
     *
     * @param role The role to check
     * @return True if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return securityPort.hasRole(role);
    }

    /**
     * Checks if the current user has the specified permission.
     *
     * @param permission The permission to check
     * @return True if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return securityPort.hasPermission(permission);
    }

    /**
     * Checks if a component has access to perform an operation on a resource.
     *
     * @param componentId   The ID of the component
     * @param resourceId    The ID of the resource
     * @param operationType The type of operation
     * @return True if access is granted, false otherwise
     */
    public boolean checkAccess(String componentId, String resourceId, String operationType) {
        SecurityPort.SecurityResult result = securityPort.checkComponentAccess(
                componentId, resourceId, operationType);
        return result.isSuccessful();
    }

    /**
     * Generates an authentication token for the current user.
     *
     * @return The generated token, or empty if generation failed
     */
    public Optional<String> generateToken() {
        return generateToken(Duration.ofHours(1));
    }

    /**
     * Generates an authentication token for the current user with a specific validity duration.
     *
     * @param validity The duration for which the token should be valid
     * @return The generated token, or empty if generation failed
     */
    public Optional<String> generateToken(Duration validity) {
        SecurityPort.SecurityResult result = securityPort.generateToken(validity);
        if (result.isSuccessful()) {
            return Optional.ofNullable((String) result.getAttributes().get("token"));
        }
        return Optional.empty();
    }

    /**
     * Validates an authentication token.
     *
     * @param token The token to validate
     * @return True if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        SecurityPort.SecurityResult result = securityPort.validateToken(token);
        return result.isSuccessful();
    }

    /**
     * Authenticates a user using a token.
     *
     * @param token The authentication token
     * @return True if authentication was successful, false otherwise
     */
    public boolean loginWithToken(String token) {
        SecurityPort.SecurityResult result = securityPort.authenticateWithToken(token);
        return result.isSuccessful();
    }

    /**
     * Revokes an authentication token.
     *
     * @param token The token to revoke
     * @return True if the token was successfully revoked, false otherwise
     */
    public boolean revokeToken(String token) {
        SecurityPort.SecurityResult result = securityPort.revokeToken(token);
        return result.isSuccessful();
    }

    /**
     * Registers a new user in the system.
     *
     * @param username The username for the new user
     * @param password The password for the new user
     * @param roles    The roles to assign to the user
     * @return True if registration was successful, false otherwise
     */
    public boolean registerUser(String username, String password, Set<String> roles) {
        SecurityPort.SecurityResult result = securityPort.registerUser(username, password, roles);
        return result.isSuccessful();
    }

    /**
     * Updates a user's roles.
     *
     * @param userId The ID of the user
     * @param roles  The new roles for the user
     * @return True if the update was successful, false otherwise
     */
    public boolean updateUserRoles(String userId, Set<String> roles) {
        SecurityPort.SecurityResult result = securityPort.updateUserRoles(userId, roles);
        return result.isSuccessful();
    }

    /**
     * Gets security audit log entries for a specific time period.
     *
     * @param from The start time of the period
     * @param to   The end time of the period
     * @return A list of log entries
     */
    public List<Map<String, Object>> getAuditLog(Instant from, Instant to) {
        return securityPort.getSecurityAuditLog(from, to);
    }

    /**
     * Logs a security event with custom details.
     *
     * @param eventType The type of security event
     * @param details   Additional details about the event
     * @return True if the event was logged successfully, false otherwise
     */
    public boolean logSecurityEvent(SecurityPort.SecurityEventType eventType, Map<String, Object> details) {
        SecurityPort.SecurityResult result = securityPort.logSecurityEvent(eventType, details);
        return result.isSuccessful();
    }

    /**
     * Shuts down the security service.
     */
    public void shutdown() {
        logger.info("Shutting down security service");
        SecurityPort.SecurityResult result = securityPort.shutdown();
        if (!result.isSuccessful()) {
            logger.error("Failed to shut down security service: " + result.getReason().orElse("Unknown reason"));
        }
    }
}