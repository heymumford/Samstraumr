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
package org.s8r.test.integration;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.SecurityEventType;
import org.s8r.application.port.SecurityPort.SecurityResult;
import org.s8r.test.mock.MockEventPublisherAdapter;
import org.s8r.test.mock.MockEventPublisherAdapter.PublishedEvent;
import org.s8r.test.mock.MockSecurityAdapter;

/**
 * Integration test context for Security-Event integration testing.
 * This class manages the state for security-event integration tests and provides
 * utility methods to verify test expectations.
 */
public class SecurityEventIntegrationContext {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityEventIntegrationContext.class.getName());
    private static final String SECURITY_EVENTS_TOPIC = "security.events";
    
    private final SecurityPort securityPort;
    private final EventPublisherPort eventPublisherPort;
    private final SecurityEventBridge bridge;
    private final MockEventPublisherAdapter mockEventPublisher;
    
    // Test state
    private String currentUsername;
    private String currentToken;
    private boolean lastOperationSuccessful;
    private SecurityResult lastResult;
    private Map<String, String> userTokens = new ConcurrentHashMap<>();
    
    /**
     * Creates a new SecurityEventIntegrationContext.
     */
    public SecurityEventIntegrationContext() {
        this.securityPort = new MockSecurityAdapter();
        this.mockEventPublisher = new MockEventPublisherAdapter();
        this.eventPublisherPort = mockEventPublisher;
        this.bridge = new SecurityEventBridge(securityPort, eventPublisherPort);
        
        // Initialize security subsystem
        securityPort.initialize();
        
        // Register event subscriber for the security events topic
        mockEventPublisher.subscribe(SECURITY_EVENTS_TOPIC, 
            (topic, eventType, payload, properties) -> {
                LOGGER.info("Received event: " + eventType + " on topic " + topic);
            });
    }
    
    /**
     * Resets the test context to a clean state.
     */
    public void reset() {
        ((MockSecurityAdapter) securityPort).shutdown();
        ((MockSecurityAdapter) securityPort).initialize();
        mockEventPublisher.clearEvents();
        currentUsername = null;
        currentToken = null;
        lastOperationSuccessful = false;
        lastResult = null;
        userTokens.clear();
    }
    
    /**
     * Registers a user with specified roles.
     * 
     * @param username The username
     * @param password The password
     * @param roles The roles for the user
     * @return The registration result
     */
    public SecurityResult registerUser(String username, String password, String... roles) {
        Set<String> roleSet = new HashSet<>();
        for (String role : roles) {
            roleSet.add(role);
        }
        
        return securityPort.registerUser(username, password, roleSet);
    }
    
    /**
     * Attempts to authenticate a user.
     * 
     * @param username The username
     * @param password The password
     * @return The authentication result
     */
    public SecurityResult authenticateUser(String username, String password) {
        lastResult = bridge.authenticate(username, password);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful) {
            currentUsername = username;
        }
        
        return lastResult;
    }
    
    /**
     * Logs out the current user.
     * 
     * @return The logout result
     */
    public SecurityResult logout() {
        lastResult = bridge.logout();
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful) {
            currentUsername = null;
        }
        
        return lastResult;
    }
    
    /**
     * Authenticates with a token.
     * 
     * @param token The authentication token
     * @return The authentication result
     */
    public SecurityResult authenticateWithToken(String token) {
        lastResult = bridge.authenticateWithToken(token);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful) {
            currentToken = token;
            if (lastResult.getAttributes().containsKey("username")) {
                currentUsername = (String) lastResult.getAttributes().get("username");
            }
        }
        
        return lastResult;
    }
    
    /**
     * Checks if a user has access to a resource.
     * 
     * @param resourceId The resource to check
     * @param operation The operation being performed
     * @return The access check result
     */
    public SecurityResult checkAccess(String resourceId, String operation) {
        lastResult = bridge.checkAccess(resourceId, operation);
        lastOperationSuccessful = lastResult.isSuccessful();
        return lastResult;
    }
    
    /**
     * Generates a token for the current user.
     * 
     * @param validityInHours The token validity in hours
     * @return The token generation result
     */
    public SecurityResult generateToken(int validityInHours) {
        lastResult = bridge.generateToken(Duration.ofHours(validityInHours));
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful && lastResult.getAttributes().containsKey("token")) {
            currentToken = (String) lastResult.getAttributes().get("token");
            if (currentUsername != null) {
                userTokens.put(currentUsername, currentToken);
            }
        }
        
        return lastResult;
    }
    
    /**
     * Validates a token.
     * 
     * @param token The token to validate
     * @return The validation result
     */
    public SecurityResult validateToken(String token) {
        lastResult = securityPort.validateToken(token);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful) {
            // The bridge will be notified through the security port
            bridge.authenticateWithToken(token);
        }
        
        return lastResult;
    }
    
    /**
     * Revokes a token.
     * 
     * @param token The token to revoke
     * @return The revocation result
     */
    public SecurityResult revokeToken(String token) {
        lastResult = bridge.revokeToken(token);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        if (lastOperationSuccessful && token.equals(currentToken)) {
            currentToken = null;
        }
        
        return lastResult;
    }
    
    /**
     * Retrieves the token for a user.
     * 
     * @param username The username
     * @return The user's token, or null if not available
     */
    public String getTokenForUser(String username) {
        return userTokens.get(username);
    }
    
    /**
     * Updates the roles for a user.
     * 
     * @param targetUsername The username to update
     * @param roles The new roles
     * @return The update result
     */
    public SecurityResult updateUserRoles(String targetUsername, String... roles) {
        // First we need to find the user ID
        String userId = findUserIdByUsername(targetUsername);
        if (userId == null) {
            return SecurityResult.failure("Update roles failed", "User not found");
        }
        
        Set<String> roleSet = new HashSet<>();
        for (String role : roles) {
            roleSet.add(role);
        }
        
        lastResult = bridge.updateUserRoles(userId, roleSet);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        return lastResult;
    }
    
    /**
     * Updates the security configuration.
     * 
     * @param configChanges The configuration changes
     * @return The update result
     */
    public SecurityResult updateSecurityConfig(Map<String, Object> configChanges) {
        lastResult = bridge.updateSecurityConfig(configChanges);
        lastOperationSuccessful = lastResult.isSuccessful();
        
        return lastResult;
    }
    
    /**
     * Simulates multiple failed login attempts.
     * 
     * @param username The username to attempt login for
     * @param attempts The number of attempts
     */
    public void simulateFailedLoginAttempts(String username, int attempts) {
        for (int i = 0; i < attempts; i++) {
            bridge.authenticate(username, "wrong_password_" + i);
        }
    }
    
    /**
     * Gets the security audit log.
     * 
     * @return The audit log entries
     */
    public List<Map<String, Object>> getSecurityAuditLog() {
        return securityPort.getSecurityAuditLog(Instant.now().minus(Duration.ofDays(1)), Instant.now());
    }
    
    /**
     * Finds a user ID by username.
     * 
     * @param username The username to look up
     * @return The user ID, or null if not found
     */
    private String findUserIdByUsername(String username) {
        // This is a mock implementation. In a real system, you would query a database.
        // For now, we'll simulate it by authenticating and checking the result.
        SecurityResult result = securityPort.authenticate(username, "dummy_password");
        if (result.isSuccessful() && result.getAttributes().containsKey("userId")) {
            String userId = (String) result.getAttributes().get("userId");
            // Make sure to log out again
            securityPort.logout();
            return userId;
        }
        
        // Try to find a user ID from event logs
        List<Map<String, Object>> events = securityPort.getSecurityAuditLog(null, null);
        for (Map<String, Object> event : events) {
            if (username.equals(event.get("username")) && event.containsKey("userId")) {
                return (String) event.get("userId");
            }
        }
        
        return null;
    }
    
    /**
     * Checks if a security event of a specific type was published.
     * 
     * @param eventType The event type to check for
     * @return true if such an event was published
     */
    public boolean wasEventPublished(String eventType) {
        return mockEventPublisher.getEventsByType(eventType).size() > 0;
    }
    
    /**
     * Gets all security events of a specific type.
     * 
     * @param eventType The event type to filter by
     * @return The list of matching events
     */
    public List<PublishedEvent> getSecurityEvents(String eventType) {
        return mockEventPublisher.getEventsByType(eventType);
    }
    
    /**
     * Gets the most recent security event of a specific type.
     * 
     * @param eventType The event type to filter by
     * @return The most recent matching event, or null if none
     */
    public PublishedEvent getLatestSecurityEvent(String eventType) {
        List<PublishedEvent> events = mockEventPublisher.getEventsByType(eventType);
        if (events.isEmpty()) {
            return null;
        }
        
        // Get the most recent event (assuming events are stored in chronological order)
        return events.get(events.size() - 1);
    }
    
    /**
     * Checks if an event contains a specific field with a specific value.
     * 
     * @param event The event to check
     * @param field The field name
     * @param value The expected value
     * @return true if the field exists and has the expected value
     */
    public boolean eventContains(PublishedEvent event, String field, String value) {
        if (event == null || event.getPayload() == null) {
            return false;
        }
        
        // Simple string search in the payload
        // In a real implementation, you would parse the JSON properly
        String payload = event.getPayload();
        return payload.contains("\"" + field + "\":\"" + value + "\"");
    }
    
    /**
     * Gets the current username.
     * 
     * @return The current username
     */
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    /**
     * Gets the current token.
     * 
     * @return The current token
     */
    public String getCurrentToken() {
        return currentToken;
    }
    
    /**
     * Checks if the last operation was successful.
     * 
     * @return true if the last operation was successful
     */
    public boolean isLastOperationSuccessful() {
        return lastOperationSuccessful;
    }
    
    /**
     * Gets the result of the last operation.
     * 
     * @return The last result
     */
    public SecurityResult getLastResult() {
        return lastResult;
    }
}