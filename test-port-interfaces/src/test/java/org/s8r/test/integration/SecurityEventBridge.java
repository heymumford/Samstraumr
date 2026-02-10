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

package org.s8r.test.integration;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.AuthenticationContext;
import org.s8r.application.port.SecurityPort.SecurityEventType;
import org.s8r.application.port.SecurityPort.SecurityResult;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Bridge connecting SecurityPort and EventPublisherPort.
 * 
 * <p>This class connects security operations with the event publishing system,
 * ensuring that security events are properly published when security operations occur.
 */
public class SecurityEventBridge {
    private static final String SECURITY_EVENTS_TOPIC = "security.events";
    
    private final SecurityPort securityPort;
    private final EventPublisherPort eventPublisherPort;
    private final Map<SecurityEventType, Long> eventCounts;
    private final AtomicLong eventSequence;
    private final Lock operationLock;
    
    /**
     * Creates a new SecurityEventBridge.
     * 
     * @param securityPort The SecurityPort implementation
     * @param eventPublisherPort The EventPublisherPort implementation
     */
    public SecurityEventBridge(SecurityPort securityPort, EventPublisherPort eventPublisherPort) {
        this.securityPort = securityPort;
        this.eventPublisherPort = eventPublisherPort;
        this.eventCounts = new ConcurrentHashMap<>();
        this.eventSequence = new AtomicLong(0);
        this.operationLock = new ReentrantLock();
    }
    
    /**
     * Resets the internal state of the bridge.
     */
    public void reset() {
        eventCounts.clear();
        eventSequence.set(0);
    }
    
    /**
     * Attempts to log in a user and publishes appropriate events.
     * 
     * @param username The username
     * @param password The password
     * @return The result of the authentication operation
     */
    public SecurityResult login(String username, String password) {
        try {
            operationLock.lock();
            
            // Perform the login operation
            SecurityResult result = securityPort.authenticate(username, password);
            
            // Publish an event based on the result
            if (result.isSuccessful()) {
                publishLoginSuccessEvent(username, result);
            } else {
                publishLoginFailureEvent(username, result);
            }
            
            return result;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Logs out the current user and publishes a logout event.
     * 
     * @return The result of the logout operation
     */
    public SecurityResult logout() {
        try {
            operationLock.lock();
            
            // Get the current auth context before logout
            Optional<AuthenticationContext> authContext = securityPort.getCurrentAuthContext();
            String username = authContext.map(AuthenticationContext::getUsername).orElse("unknown");
            
            // Perform the logout operation
            SecurityResult result = securityPort.logout();
            
            // Publish the logout event if successful
            if (result.isSuccessful()) {
                Map<String, Object> details = new HashMap<>();
                details.put("username", username);
                details.put("timestamp", Instant.now().toString());
                
                publishSecurityEvent(SecurityEventType.LOGOUT, details);
            }
            
            return result;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Attempts to access a resource and publishes appropriate access events.
     * 
     * @param resource The resource path
     * @return true if access is granted, false otherwise
     */
    public boolean attemptResourceAccess(String resource) {
        try {
            operationLock.lock();
            
            // Get the current auth context
            Optional<AuthenticationContext> authContext = securityPort.getCurrentAuthContext();
            if (authContext.isEmpty()) {
                return false;
            }
            
            AuthenticationContext context = authContext.get();
            String username = context.getUsername();
            
            // Determine required permission based on resource path
            String requiredPermission = determineRequiredPermission(resource);
            
            // Check if the user has the required permission
            boolean hasAccess = securityPort.hasPermission(requiredPermission);
            
            // Publish event based on access result
            Map<String, Object> details = new HashMap<>();
            details.put("username", username);
            details.put("resource", resource);
            details.put("requiredPermission", requiredPermission);
            details.put("timestamp", Instant.now().toString());
            
            if (hasAccess) {
                publishSecurityEvent(SecurityEventType.ACCESS_GRANTED, details);
            } else {
                publishSecurityEvent(SecurityEventType.ACCESS_DENIED, details);
            }
            
            return hasAccess;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Generates a security token for the current user and publishes a token issued event.
     * 
     * @param validity The validity duration
     * @return The result containing the token if successful
     */
    public SecurityResult generateToken(Duration validity) {
        try {
            operationLock.lock();
            
            // Get the current auth context
            Optional<AuthenticationContext> authContext = securityPort.getCurrentAuthContext();
            if (authContext.isEmpty()) {
                return SecurityResult.failure("Token generation failed", "User not authenticated");
            }
            
            String username = authContext.get().getUsername();
            
            // Generate the token
            SecurityResult result = securityPort.generateToken(validity);
            
            // Publish token issued event if successful
            if (result.isSuccessful()) {
                Map<String, Object> details = new HashMap<>();
                details.put("username", username);
                details.put("tokenValidity", validity.toString());
                details.put("timestamp", Instant.now().toString());
                
                // Add token ID if available in the result
                result.getAttributes().forEach((key, value) -> {
                    if (key.equals("tokenId") || key.equals("token_id")) {
                        details.put("tokenId", value);
                    }
                });
                
                publishSecurityEvent(SecurityEventType.TOKEN_ISSUED, details);
            }
            
            return result;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Validates a security token and publishes a token validated event if successful.
     * 
     * @param token The token to validate
     * @return The result of the validation
     */
    public SecurityResult validateToken(String token) {
        try {
            operationLock.lock();
            
            // Validate the token
            SecurityResult result = securityPort.validateToken(token);
            
            // Publish token validated event if successful
            if (result.isSuccessful()) {
                Map<String, Object> details = new HashMap<>();
                details.put("tokenId", extractTokenId(token));
                details.put("timestamp", Instant.now().toString());
                
                // Include username if available
                result.getAttributes().forEach((key, value) -> {
                    if (key.equals("username") || key.equals("user")) {
                        details.put("username", value);
                    }
                });
                
                publishSecurityEvent(SecurityEventType.TOKEN_VALIDATED, details);
            }
            
            return result;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Revokes a security token and publishes a token expired event.
     * 
     * @param token The token to revoke
     * @return The result of the revocation
     */
    public SecurityResult revokeToken(String token) {
        try {
            operationLock.lock();
            
            // Get username before token revocation if possible
            String username = extractUsernameFromToken(token);
            
            // Revoke the token
            SecurityResult result = securityPort.revokeToken(token);
            
            // Publish token expired event
            Map<String, Object> details = new HashMap<>();
            details.put("tokenId", extractTokenId(token));
            details.put("timestamp", Instant.now().toString());
            
            if (username != null) {
                details.put("username", username);
            }
            
            publishSecurityEvent(SecurityEventType.TOKEN_EXPIRED, details);
            
            return result;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Updates the security configuration and publishes a configuration changed event.
     * 
     * @param configChanges The configuration changes
     */
    public void updateSecurityConfiguration(Map<String, Object> configChanges) {
        try {
            operationLock.lock();
            
            // Get the current user
            Optional<AuthenticationContext> authContext = securityPort.getCurrentAuthContext();
            String username = authContext.map(AuthenticationContext::getUsername).orElse("system");
            
            // In a real implementation, we would apply the config changes to the securityPort
            // For test purposes, we just publish the event
            
            Map<String, Object> details = new HashMap<>();
            details.put("username", username);
            details.put("changes", configChanges);
            details.put("timestamp", Instant.now().toString());
            
            publishSecurityEvent(SecurityEventType.SECURITY_CONFIG_CHANGED, details);
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * Publishes a security event.
     * 
     * @param eventType The type of security event
     * @param details The event details
     * @return true if the event was published successfully, false otherwise
     */
    public boolean publishSecurityEvent(SecurityEventType eventType, Map<String, Object> details) {
        // Convert details to string payload (in a real implementation this might be JSON)
        StringBuilder payload = new StringBuilder();
        payload.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : details.entrySet()) {
            if (!first) {
                payload.append(",");
            }
            payload.append("\"").append(entry.getKey()).append("\":\"")
                  .append(entry.getValue()).append("\"");
            first = false;
        }
        payload.append("}");
        
        // Convert details to properties
        Map<String, String> properties = new HashMap<>();
        for (Map.Entry<String, Object> entry : details.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        
        // Add event metadata
        properties.put("eventId", generateEventId());
        properties.put("eventType", eventType.name());
        properties.put("eventTime", Instant.now().toString());
        
        // Track event counts
        incrementEventCount(eventType);
        
        // Publish the event
        return eventPublisherPort.publishEvent(
                SECURITY_EVENTS_TOPIC, 
                eventType.name(), 
                payload.toString(), 
                properties);
    }
    
    /**
     * Publishes a login success event.
     * 
     * @param username The username
     * @param result The authentication result
     */
    private void publishLoginSuccessEvent(String username, SecurityResult result) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("timestamp", Instant.now().toString());
        details.put("sessionId", generateSessionId());
        
        publishSecurityEvent(SecurityEventType.LOGIN_SUCCESS, details);
    }
    
    /**
     * Publishes a login failure event.
     * 
     * @param username The username
     * @param result The authentication result
     */
    private void publishLoginFailureEvent(String username, SecurityResult result) {
        Map<String, Object> details = new HashMap<>();
        details.put("username", username);
        details.put("timestamp", Instant.now().toString());
        
        // Add failure reason if available
        result.getReason().ifPresent(reason -> details.put("reason", reason));
        
        publishSecurityEvent(SecurityEventType.LOGIN_FAILURE, details);
    }
    
    /**
     * Increments the event count for a specific event type.
     * 
     * @param eventType The event type
     */
    private void incrementEventCount(SecurityEventType eventType) {
        eventCounts.compute(eventType, (type, count) -> (count == null) ? 1L : count + 1L);
    }
    
    /**
     * Gets the count of events for a specific type.
     * 
     * @param eventType The event type
     * @return The count
     */
    public long getEventCount(SecurityEventType eventType) {
        return eventCounts.getOrDefault(eventType, 0L);
    }
    
    /**
     * Generates a unique event ID.
     * 
     * @return The event ID
     */
    private String generateEventId() {
        return "evt-" + UUID.randomUUID().toString() + "-" + eventSequence.incrementAndGet();
    }
    
    /**
     * Generates a session ID.
     * 
     * @return The session ID
     */
    private String generateSessionId() {
        return "sess-" + UUID.randomUUID().toString();
    }
    
    /**
     * Determines the required permission for a resource.
     * 
     * @param resource The resource path
     * @return The required permission
     */
    private String determineRequiredPermission(String resource) {
        // Simple permission mapping based on resource path
        if (resource.startsWith("system/admin")) {
            return "ADMIN";
        } else if (resource.startsWith("system")) {
            return "SYSTEM";
        } else if (resource.contains("/write/")) {
            return "WRITE";
        } else if (resource.contains("/read/")) {
            return "READ";
        } else {
            return "USER";
        }
    }
    
    /**
     * Extracts a token ID from a token.
     * 
     * @param token The token
     * @return The token ID
     */
    private String extractTokenId(String token) {
        // Simple token ID extraction based on token format
        return "tid-" + token.hashCode();
    }
    
    /**
     * Extracts a username from a token.
     * 
     * @param token The token
     * @return The username, or null if unavailable
     */
    private String extractUsernameFromToken(String token) {
        // In a real implementation, this would decode the token to extract the username
        // For testing purposes, this is a stub
        
        // Validate the token first to get user info
        SecurityResult result = securityPort.validateToken(token);
        if (result.isSuccessful() && result.getAttributes().containsKey("username")) {
            return (String) result.getAttributes().get("username");
        }
        
        return null;
    }
}