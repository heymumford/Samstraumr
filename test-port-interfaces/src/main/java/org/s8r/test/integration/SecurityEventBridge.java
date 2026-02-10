/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.integration;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.SecurityEventType;
import org.s8r.application.port.SecurityPort.SecurityResult;

/**
 * Bridge component that connects SecurityPort and EventPublisherPort interfaces.
 * This class ensures security events are published to the event system when
 * security-related operations occur.
 */
public class SecurityEventBridge {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityEventBridge.class.getName());
    private static final String SECURITY_EVENTS_TOPIC = "security.events";
    
    private final SecurityPort securityPort;
    private final EventPublisherPort eventPublisherPort;
    private final Map<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    /**
     * Constructs a new SecurityEventBridge with the necessary ports.
     *
     * @param securityPort The security port interface
     * @param eventPublisherPort The event publisher port interface
     */
    public SecurityEventBridge(SecurityPort securityPort, EventPublisherPort eventPublisherPort) {
        this.securityPort = securityPort;
        this.eventPublisherPort = eventPublisherPort;
    }
    
    /**
     * Authenticates a user and publishes appropriate security event.
     *
     * @param username The username for authentication
     * @param password The password for authentication
     * @return The authentication result
     */
    public SecurityResult authenticate(String username, String password) {
        SecurityResult result = securityPort.authenticate(username, password);
        
        if (result.isSuccessful()) {
            publishSecurityEvent(SecurityEventType.LOGIN_SUCCESS, buildUserEvent(username, result));
            // Reset failed login attempts counter
            failedLoginAttempts.remove(username);
        } else {
            Map<String, Object> details = buildUserEvent(username, result);
            details.put("reason", result.getReason().orElse("Unknown error"));
            publishSecurityEvent(SecurityEventType.LOGIN_FAILURE, details);
            
            // Track failed login attempts for security monitoring
            int attempts = failedLoginAttempts.getOrDefault(username, 0) + 1;
            failedLoginAttempts.put(username, attempts);
            
            // Check for potential brute force attacks
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                Map<String, Object> alertDetails = new HashMap<>();
                alertDetails.put("username", username);
                alertDetails.put("failedAttempts", attempts);
                alertDetails.put("attackType", "Possible brute force");
                alertDetails.put("timestamp", Instant.now().toString());
                publishSecurityEvent(SecurityEventType.POTENTIAL_ATTACK_DETECTED, alertDetails);
            }
        }
        
        return result;
    }
    
    /**
     * Authenticates a user with a token and publishes appropriate security event.
     *
     * @param token The authentication token
     * @return The authentication result
     */
    public SecurityResult authenticateWithToken(String token) {
        SecurityResult result = securityPort.authenticateWithToken(token);
        
        Map<String, Object> details = new HashMap<>();
        details.put("tokenId", maskToken(token));
        details.put("timestamp", Instant.now().toString());
        
        if (result.isSuccessful()) {
            details.put("username", result.getAttributes().getOrDefault("username", "unknown"));
            publishSecurityEvent(SecurityEventType.TOKEN_VALIDATED, details);
        } else {
            details.put("reason", result.getReason().orElse("Unknown error"));
            publishSecurityEvent(SecurityEventType.TOKEN_EXPIRED, details);
        }
        
        return result;
    }
    
    /**
     * Logs out the current user and publishes a logout event.
     *
     * @return The logout result
     */
    public SecurityResult logout() {
        String username = "unknown";
        securityPort.getCurrentAuthContext().ifPresent(auth -> {
            username = auth.getUsername();
        });
        
        SecurityResult result = securityPort.logout();
        
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("timestamp", Instant.now().toString());
        publishSecurityEvent(SecurityEventType.LOGOUT, details);
        
        return result;
    }
    
    /**
     * Checks access to a resource and publishes appropriate security event.
     *
     * @param resourceId The resource being accessed
     * @param operationType The type of operation
     * @return The access check result
     */
    public SecurityResult checkAccess(String resourceId, String operationType) {
        String username = "unknown";
        String componentId = "unknown";
        
        // Get current user if available
        if (securityPort.getCurrentAuthContext().isPresent()) {
            username = securityPort.getCurrentAuthContext().get().getUsername();
            componentId = securityPort.getCurrentAuthContext().get().getUserId();
        }
        
        SecurityResult result = securityPort.checkComponentAccess(componentId, resourceId, operationType);
        
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("componentId", componentId);
        details.put("resource", resourceId);
        details.put("operation", operationType);
        details.put("timestamp", Instant.now().toString());
        
        if (result.isSuccessful()) {
            details.put("permission", operationType);
            publishSecurityEvent(SecurityEventType.ACCESS_GRANTED, details);
        } else {
            details.put("requiredPermission", result.getAttributes().getOrDefault("requiredPermission", "UNKNOWN"));
            details.put("reason", result.getReason().orElse("Insufficient permissions"));
            publishSecurityEvent(SecurityEventType.ACCESS_DENIED, details);
        }
        
        return result;
    }
    
    /**
     * Generates a security token and publishes a token issued event.
     *
     * @param validity The token validity duration
     * @return The token generation result
     */
    public SecurityResult generateToken(Duration validity) {
        SecurityResult result = securityPort.generateToken(validity);
        
        if (result.isSuccessful()) {
            Map<String, Object> details = new HashMap<>();
            securityPort.getCurrentAuthContext().ifPresent(auth -> {
                details.put("username", auth.getUsername());
            });
            details.put("tokenId", maskToken((String) result.getAttributes().getOrDefault("token", "unknown")));
            details.put("validity", validity.toString());
            details.put("expiresAt", Instant.now().plus(validity).toString());
            details.put("timestamp", Instant.now().toString());
            publishSecurityEvent(SecurityEventType.TOKEN_ISSUED, details);
        }
        
        return result;
    }
    
    /**
     * Revokes a security token and publishes a token expired event.
     *
     * @param token The token to revoke
     * @return The token revocation result
     */
    public SecurityResult revokeToken(String token) {
        SecurityResult result = securityPort.revokeToken(token);
        
        Map<String, Object> details = new HashMap<>();
        securityPort.getCurrentAuthContext().ifPresent(auth -> {
            details.put("username", auth.getUsername());
        });
        details.put("tokenId", maskToken(token));
        details.put("timestamp", Instant.now().toString());
        details.put("revocationReason", "Manual revocation");
        
        publishSecurityEvent(SecurityEventType.TOKEN_EXPIRED, details);
        
        return result;
    }
    
    /**
     * Updates user roles and publishes a permission changed event.
     *
     * @param userId The ID of the user
     * @param roles The new roles for the user
     * @return The update result
     */
    public SecurityResult updateUserRoles(String userId, Set<String> roles) {
        SecurityResult result = securityPort.updateUserRoles(userId, roles);
        
        if (result.isSuccessful()) {
            Map<String, Object> details = new HashMap<>();
            details.put("targetUserId", userId);
            details.put("roles", roles);
            securityPort.getCurrentAuthContext().ifPresent(auth -> {
                details.put("modifierUsername", auth.getUsername());
            });
            details.put("timestamp", Instant.now().toString());
            publishSecurityEvent(SecurityEventType.PERMISSION_CHANGED, details);
        }
        
        return result;
    }
    
    /**
     * Updates security configuration and publishes a configuration changed event.
     *
     * @param configChanges Map of configuration changes
     * @return The update result
     */
    public SecurityResult updateSecurityConfig(Map<String, Object> configChanges) {
        // This method would normally call a securityPort method to update config
        // For test purposes, we'll create a simulated result
        SecurityResult result = SecurityResult.success("Security configuration updated");
        
        Map<String, Object> details = new HashMap<>(configChanges);
        securityPort.getCurrentAuthContext().ifPresent(auth -> {
            details.put("modifierUsername", auth.getUsername());
        });
        details.put("timestamp", Instant.now().toString());
        
        publishSecurityEvent(SecurityEventType.SECURITY_CONFIG_CHANGED, details);
        
        return result;
    }
    
    /**
     * Gets the security audit log for a time period.
     *
     * @param from Start time
     * @param to End time
     * @return List of audit log entries
     */
    public List<Map<String, Object>> getSecurityAuditLog(Instant from, Instant to) {
        return securityPort.getSecurityAuditLog(from, to);
    }
    
    /**
     * Publishes a security event to the event system.
     *
     * @param eventType The security event type
     * @param details The event details
     * @return true if the event was published successfully
     */
    public boolean publishSecurityEvent(SecurityEventType eventType, Map<String, Object> details) {
        Map<String, String> properties = new HashMap<>();
        properties.put("eventType", eventType.name());
        properties.put("timestamp", Instant.now().toString());
        properties.put("correlationId", UUID.randomUUID().toString());
        
        // Convert details to a single JSON string payload or similar format
        // In a real implementation, we would use a proper JSON serialization library
        StringBuilder payload = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : details.entrySet()) {
            payload.append("\"").append(entry.getKey()).append("\":\"")
                   .append(entry.getValue()).append("\",");
        }
        // Remove trailing comma if present
        if (payload.charAt(payload.length() - 1) == ',') {
            payload.setLength(payload.length() - 1);
        }
        payload.append("}");
        
        LOGGER.info("Publishing security event: " + eventType + " with details: " + details);
        
        return eventPublisherPort.publishEvent(
            SECURITY_EVENTS_TOPIC,
            eventType.name(),
            payload.toString(),
            properties
        );
    }
    
    /**
     * Builds a basic event with user details.
     *
     * @param username The username
     * @param result The security operation result
     * @return A map with common event details
     */
    private Map<String, Object> buildUserEvent(String username, SecurityResult result) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("timestamp", Instant.now().toString());
        
        // Add additional details from the result if available
        result.getAttributes().forEach((key, value) -> {
            if (!"password".equals(key)) {  // Don't include sensitive information
                details.put(key, value);
            }
        });
        
        return details;
    }
    
    /**
     * Masks a token for logging purposes.
     *
     * @param token The token to mask
     * @return A masked version of the token
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        
        // Only show first 4 and last 4 characters
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }
}