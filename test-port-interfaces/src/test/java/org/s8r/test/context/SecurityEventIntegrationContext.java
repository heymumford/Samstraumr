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

package org.s8r.test.context;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.AuthenticationContext;
import org.s8r.application.port.SecurityPort.SecurityContext;
import org.s8r.application.port.SecurityPort.SecurityEventType;
import org.s8r.application.port.SecurityPort.SecurityResult;
import org.s8r.domain.event.DomainEvent;
import org.s8r.test.integration.SecurityEventBridge;
import org.s8r.test.mock.MockEventPublisherAdapter;
import org.s8r.test.mock.MockSecurityAdapter;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test context for SecurityPort and EventPublisherPort integration tests.
 * 
 * <p>This class manages the test context and state for integration between
 * SecurityPort and EventPublisherPort operations.
 */
public class SecurityEventIntegrationContext {
    private final SecurityPort securityPort;
    private final EventPublisherPort eventPublisherPort;
    private final SecurityEventBridge bridge;
    private final List<SecurityEvent> securityEvents;
    private final Map<String, User> registeredUsers;
    private final Map<String, String> activeTokens;
    private final Map<String, Integer> failedLoginAttempts;
    private final List<Map<String, Object>> auditLog;
    private final AtomicInteger failedAttemptThreshold;
    
    /**
     * Creates a new SecurityEventIntegrationContext.
     */
    public SecurityEventIntegrationContext() {
        this.securityPort = new MockSecurityAdapter();
        this.eventPublisherPort = new MockEventPublisherAdapter();
        this.bridge = new SecurityEventBridge(securityPort, eventPublisherPort);
        this.securityEvents = new CopyOnWriteArrayList<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.activeTokens = new ConcurrentHashMap<>();
        this.failedLoginAttempts = new ConcurrentHashMap<>();
        this.auditLog = new CopyOnWriteArrayList<>();
        this.failedAttemptThreshold = new AtomicInteger(5);
    }
    
    /**
     * Resets the test context.
     */
    public void reset() {
        securityEvents.clear();
        activeTokens.clear();
        failedLoginAttempts.clear();
        auditLog.clear();
        
        // Reset adapters
        ((MockSecurityAdapter) securityPort).reset();
        ((MockEventPublisherAdapter) eventPublisherPort).reset();
        
        // Reset the bridge
        bridge.reset();
    }
    
    /**
     * Sets up a clean environment.
     */
    public void setupCleanEnvironment() {
        reset();
        
        // Reset registered users but keep the structure
        registeredUsers.clear();
        
        // Setup event subscription
        ((MockEventPublisherAdapter) eventPublisherPort).addSubscriber(
            "security.events", (topic, eventType, payload, properties) -> {
                SecurityEvent event = new SecurityEvent(topic, eventType, payload, properties);
                securityEvents.add(event);
            });
    }
    
    /**
     * Gets the SecurityPort.
     * 
     * @return The SecurityPort
     */
    public SecurityPort getSecurityPort() {
        return securityPort;
    }
    
    /**
     * Gets the EventPublisherPort.
     * 
     * @return The EventPublisherPort
     */
    public EventPublisherPort getEventPublisherPort() {
        return eventPublisherPort;
    }
    
    /**
     * Gets the SecurityEventBridge.
     * 
     * @return The SecurityEventBridge
     */
    public SecurityEventBridge getBridge() {
        return bridge;
    }
    
    /**
     * Registers a test user.
     * 
     * @param username The username
     * @param password The password
     * @param roles The roles assigned to the user
     */
    public void registerUser(String username, String password, List<String> roles) {
        User user = new User(username, password, new HashSet<>(roles));
        registeredUsers.put(username, user);
        
        // Register in the security port
        Set<String> roleSet = new HashSet<>(roles);
        SecurityResult result = securityPort.registerUser(username, password, roleSet);
        
        if (result.isSuccessful()) {
            // Add to audit log
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("type", "USER_REGISTERED");
            logEntry.put("username", username);
            logEntry.put("timestamp", Instant.now());
            logEntry.put("roles", roles);
            auditLog.add(logEntry);
        }
    }
    
    /**
     * Attempts to log in a user.
     * 
     * @param username The username
     * @param password The password
     * @return The result of the login attempt
     */
    public SecurityResult login(String username, String password) {
        SecurityResult result = bridge.login(username, password);
        
        // Record login attempt in audit log
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("type", result.isSuccessful() ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
        logEntry.put("username", username);
        logEntry.put("timestamp", Instant.now());
        logEntry.put("success", result.isSuccessful());
        if (!result.isSuccessful()) {
            result.getReason().ifPresent(reason -> logEntry.put("reason", reason));
            
            // Track failed login attempts
            int attempts = failedLoginAttempts.getOrDefault(username, 0) + 1;
            failedLoginAttempts.put(username, attempts);
            
            // Check if threshold exceeded for potential attack detection
            if (attempts >= failedAttemptThreshold.get()) {
                Map<String, Object> attackDetails = new HashMap<>();
                attackDetails.put("username", username);
                attackDetails.put("failedAttempts", attempts);
                attackDetails.put("threshold", failedAttemptThreshold.get());
                attackDetails.put("timestamp", Instant.now().toString());
                
                bridge.publishSecurityEvent(SecurityEventType.POTENTIAL_ATTACK_DETECTED, attackDetails);
            }
        } else {
            // Reset failed login attempts on successful login
            failedLoginAttempts.remove(username);
        }
        
        auditLog.add(logEntry);
        return result;
    }
    
    /**
     * Performs multiple failed login attempts.
     * 
     * @param count The number of attempts
     * @param username The username
     */
    public void performFailedLoginAttempts(int count, String username) {
        for (int i = 0; i < count; i++) {
            login(username, "wrong_password_" + i);
        }
    }
    
    /**
     * Logs out the current user.
     * 
     * @return The result of the logout operation
     */
    public SecurityResult logout() {
        SecurityResult result = bridge.logout();
        
        // Add to audit log
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("type", "LOGOUT");
        logEntry.put("timestamp", Instant.now());
        logEntry.put("success", result.isSuccessful());
        
        // Include username if available
        Optional<AuthenticationContext> authContext = securityPort.getCurrentAuthContext();
        authContext.ifPresent(context -> logEntry.put("username", context.getUsername()));
        
        auditLog.add(logEntry);
        return result;
    }
    
    /**
     * Authenticates a user without going through the bridge.
     * 
     * @param username The username
     */
    public void authenticateUser(String username) {
        User user = registeredUsers.get(username);
        if (user != null) {
            SecurityResult result = securityPort.authenticate(username, user.getPassword());
            if (!result.isSuccessful()) {
                throw new IllegalStateException("Failed to authenticate user: " + username);
            }
        } else {
            throw new IllegalArgumentException("User not registered: " + username);
        }
    }
    
    /**
     * Attempts to access a resource.
     * 
     * @param resource The resource path
     * @return true if access is granted, false otherwise
     */
    public boolean attemptResourceAccess(String resource) {
        return bridge.attemptResourceAccess(resource);
    }
    
    /**
     * Requests a security token with a specified validity period.
     * 
     * @param validity The validity duration
     * @return The result containing the token if successful
     */
    public SecurityResult requestSecurityToken(Duration validity) {
        return bridge.generateToken(validity);
    }
    
    /**
     * Validates a security token.
     * 
     * @param token The token to validate
     * @return The result of the validation
     */
    public SecurityResult validateToken(String token) {
        return bridge.validateToken(token);
    }
    
    /**
     * Revokes a security token.
     * 
     * @param token The token to revoke
     * @return The result of the revocation
     */
    public SecurityResult revokeToken(String token) {
        return bridge.revokeToken(token);
    }
    
    /**
     * Grants a role to a user.
     * 
     * @param username The username
     * @param role The role to grant
     */
    public void grantRole(String username, String role) {
        User user = registeredUsers.get(username);
        if (user != null) {
            user.getRoles().add(role);
            
            // Update in the security port
            Set<String> updatedRoles = new HashSet<>(user.getRoles());
            SecurityResult result = securityPort.updateUserRoles(username, updatedRoles);
            
            // Record role change
            Optional<AuthenticationContext> currentUser = securityPort.getCurrentAuthContext();
            String modifierUsername = currentUser.map(AuthenticationContext::getUsername).orElse("system");
            
            Map<String, Object> details = new HashMap<>();
            details.put("username", username);
            details.put("addedRole", role);
            details.put("modifiedBy", modifierUsername);
            details.put("timestamp", Instant.now().toString());
            
            bridge.publishSecurityEvent(SecurityEventType.PERMISSION_CHANGED, details);
            
            // Add to audit log
            Map<String, Object> logEntry = new HashMap<>();
            logEntry.put("type", "ROLE_GRANTED");
            logEntry.put("username", username);
            logEntry.put("role", role);
            logEntry.put("modifiedBy", modifierUsername);
            logEntry.put("timestamp", Instant.now());
            logEntry.put("success", result.isSuccessful());
            auditLog.add(logEntry);
        } else {
            throw new IllegalArgumentException("User not registered: " + username);
        }
    }
    
    /**
     * Changes the security configuration.
     */
    public void changeSecurityConfiguration() {
        Map<String, Object> configChanges = new HashMap<>();
        configChanges.put("failedLoginThreshold", failedAttemptThreshold.incrementAndGet());
        configChanges.put("sessionTimeout", Duration.ofMinutes(30).toString());
        configChanges.put("enforceStrongPasswords", true);
        
        bridge.updateSecurityConfiguration(configChanges);
    }
    
    /**
     * Requests the security audit log.
     * 
     * @return The audit log entries
     */
    public List<Map<String, Object>> requestAuditLog() {
        // This would normally call securityPort.getSecurityAuditLog()
        // For simplicity, we're returning our internal log
        return new ArrayList<>(auditLog);
    }
    
    /**
     * Checks if a security event of a specific type was published.
     * 
     * @param eventType The event type to check for
     * @return true if an event of that type was published
     */
    public boolean wasEventPublished(String eventType) {
        return securityEvents.stream()
            .anyMatch(event -> event.getEventType().equals(eventType));
    }
    
    /**
     * Gets the most recent security event of a specific type.
     * 
     * @param eventType The event type to retrieve
     * @return The most recent event of that type, or null if none found
     */
    public SecurityEvent getLastEventOfType(String eventType) {
        return securityEvents.stream()
            .filter(event -> event.getEventType().equals(eventType))
            .reduce((first, second) -> second) // get last element
            .orElse(null);
    }
    
    /**
     * Gets all security events.
     * 
     * @return The list of security events
     */
    public List<SecurityEvent> getSecurityEvents() {
        return securityEvents;
    }
    
    /**
     * Gets a user by username.
     * 
     * @param username The username
     * @return The User object
     */
    public User getUser(String username) {
        return registeredUsers.get(username);
    }
    
    /**
     * Checks if a username exists in an event's properties.
     * 
     * @param event The event to check
     * @param username The username to look for
     * @return true if the username exists in the event properties
     */
    public boolean eventContainsUsername(SecurityEvent event, String username) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("username") &&
            event.getProperties().get("username").equals(username)) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"username\":\"" + username + "\"") ||
               event.getPayload().contains("username=" + username);
    }
    
    /**
     * Checks if a resource exists in an event's properties.
     * 
     * @param event The event to check
     * @param resource The resource to look for
     * @return true if the resource exists in the event properties
     */
    public boolean eventContainsResource(SecurityEvent event, String resource) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("resource") &&
            event.getProperties().get("resource").equals(resource)) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"resource\":\"" + resource + "\"") ||
               event.getPayload().contains("resource=" + resource);
    }
    
    /**
     * Checks if a permission exists in an event's properties.
     * 
     * @param event The event to check
     * @param permission The permission to look for
     * @return true if the permission exists in the event properties
     */
    public boolean eventContainsPermission(SecurityEvent event, String permission) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("permission") &&
            event.getProperties().get("permission").equals(permission)) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"permission\":\"" + permission + "\"") ||
               event.getPayload().contains("\"requiredPermission\":\"" + permission + "\"") ||
               event.getPayload().contains("permission=" + permission) ||
               event.getPayload().contains("requiredPermission=" + permission);
    }
    
    /**
     * Checks if an event contains timestamp information.
     * 
     * @param event The event to check
     * @return true if the event contains timestamp information
     */
    public boolean eventIncludesTimestamp(SecurityEvent event) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("timestamp")) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"timestamp\":");
    }
    
    /**
     * Checks if an event contains session information.
     * 
     * @param event The event to check
     * @return true if the event contains session information
     */
    public boolean eventIncludesSessionInfo(SecurityEvent event) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("sessionId") ||
            event.getProperties().containsKey("session")) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"sessionId\":") ||
               event.getPayload().contains("\"session\":");
    }
    
    /**
     * Checks if an event contains failure reason information.
     * 
     * @param event The event to check
     * @param reason The expected reason
     * @return true if the event contains the failure reason
     */
    public boolean eventIncludesFailureReason(SecurityEvent event, String reason) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("reason") &&
            event.getProperties().get("reason").contains(reason)) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"reason\":\"" + reason + "\"") ||
               event.getPayload().contains("\"failureReason\":\"" + reason + "\"") ||
               event.getPayload().contains("reason=" + reason) ||
               event.getPayload().contains("failureReason=" + reason);
    }
    
    /**
     * Checks if an event contains token validity information.
     * 
     * @param event The event to check
     * @return true if the event contains token validity information
     */
    public boolean eventIncludesTokenValidity(SecurityEvent event) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("validity") ||
            event.getProperties().containsKey("tokenValidity")) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"validity\":") ||
               event.getPayload().contains("\"tokenValidity\":");
    }
    
    /**
     * Checks if an event contains details about attack patterns.
     * 
     * @param event The event to check
     * @return true if the event contains attack pattern details
     */
    public boolean eventIncludesAttackDetails(SecurityEvent event) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("attackPattern") ||
            event.getProperties().containsKey("attackDetails") ||
            event.getProperties().containsKey("failedAttempts")) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"attackPattern\":") ||
               event.getPayload().contains("\"attackDetails\":") ||
               event.getPayload().contains("\"failedAttempts\":");
    }
    
    /**
     * Checks if an event contains details about configuration changes.
     * 
     * @param event The event to check
     * @return true if the event contains configuration change details
     */
    public boolean eventIncludesConfigChanges(SecurityEvent event) {
        if (event == null) {
            return false;
        }
        
        // Check properties
        if (event.getProperties().containsKey("configChanges") ||
            event.getProperties().containsKey("changes")) {
            return true;
        }
        
        // Check payload
        return event.getPayload().contains("\"configChanges\":") ||
               event.getPayload().contains("\"changes\":");
    }
    
    /**
     * User representation for test purposes.
     */
    public static class User {
        private final String username;
        private final String password;
        private final Set<String> roles;
        
        /**
         * Creates a new User.
         * 
         * @param username The username
         * @param password The password
         * @param roles The roles
         */
        public User(String username, String password, Set<String> roles) {
            this.username = username;
            this.password = password;
            this.roles = roles;
        }
        
        /**
         * Gets the username.
         * 
         * @return The username
         */
        public String getUsername() {
            return username;
        }
        
        /**
         * Gets the password.
         * 
         * @return The password
         */
        public String getPassword() {
            return password;
        }
        
        /**
         * Gets the roles.
         * 
         * @return The roles
         */
        public Set<String> getRoles() {
            return roles;
        }
    }
    
    /**
     * SecurityEvent representation for test purposes.
     */
    public static class SecurityEvent {
        private final String topic;
        private final String eventType;
        private final String payload;
        private final Map<String, String> properties;
        private final Instant timestamp;
        
        /**
         * Creates a new SecurityEvent.
         * 
         * @param topic The topic
         * @param eventType The event type
         * @param payload The payload
         * @param properties The properties
         */
        public SecurityEvent(String topic, String eventType, String payload, Map<String, String> properties) {
            this.topic = topic;
            this.eventType = eventType;
            this.payload = payload;
            this.properties = new HashMap<>(properties);
            this.timestamp = Instant.now();
        }
        
        /**
         * Gets the topic.
         * 
         * @return The topic
         */
        public String getTopic() {
            return topic;
        }
        
        /**
         * Gets the event type.
         * 
         * @return The event type
         */
        public String getEventType() {
            return eventType;
        }
        
        /**
         * Gets the payload.
         * 
         * @return The payload
         */
        public String getPayload() {
            return payload;
        }
        
        /**
         * Gets the properties.
         * 
         * @return The properties
         */
        public Map<String, String> getProperties() {
            return properties;
        }
        
        /**
         * Gets the timestamp.
         * 
         * @return The timestamp
         */
        public Instant getTimestamp() {
            return timestamp;
        }
    }
}