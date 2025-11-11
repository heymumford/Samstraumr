/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.mock;

import org.s8r.application.port.security.AuthorizationResult;
import org.s8r.application.port.security.AuthenticationResult;
import org.s8r.application.port.security.SecurityPort;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Mock implementation of SecurityPort for testing.
 */
public class MockSecurityAdapter implements SecurityPort {
    private final Map<String, String> userCredentials = new HashMap<>();
    private final Map<String, List<String>> userRoles = new HashMap<>();
    private final Map<String, String> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, Instant> sessionExpirations = new ConcurrentHashMap<>();
    private final Map<String, List<Instant>> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, String> resourceRoles = new ConcurrentHashMap<>();
    
    private int maxFailedAttempts = 3;
    private int bruteForceWindowSeconds = 300;
    private int sessionTimeoutSeconds = 1800;
    private String requiredRoleForResource = "ADMIN";
    
    // Security events
    private final List<Map<String, Object>> securityEvents = new CopyOnWriteArrayList<>();
    
    /**
     * Creates a new instance of the MockSecurityAdapter.
     *
     * @return A new MockSecurityAdapter instance.
     */
    public static MockSecurityAdapter createInstance() {
        MockSecurityAdapter adapter = new MockSecurityAdapter();
        // Add some default users
        adapter.userCredentials.put("testuser", "validPassword123");
        adapter.userCredentials.put("admin", "adminPass123");
        
        adapter.userRoles.put("testuser", Arrays.asList("USER"));
        adapter.userRoles.put("admin", Arrays.asList("USER", "ADMIN"));
        
        return adapter;
    }
    
    /**
     * Configures the adapter with the given settings.
     *
     * @param settings Map of configuration settings.
     */
    public void configure(Map<String, Object> settings) {
        if (settings.containsKey("maxFailedAttempts")) {
            this.maxFailedAttempts = ((Number) settings.get("maxFailedAttempts")).intValue();
        }
        if (settings.containsKey("bruteForceWindowSeconds")) {
            this.bruteForceWindowSeconds = ((Number) settings.get("bruteForceWindowSeconds")).intValue();
        }
        if (settings.containsKey("sessionTimeoutSeconds")) {
            this.sessionTimeoutSeconds = ((Number) settings.get("sessionTimeoutSeconds")).intValue();
        }
        if (settings.containsKey("requiredRoleForResource")) {
            this.requiredRoleForResource = (String) settings.get("requiredRoleForResource");
        }
    }

    @Override
    public AuthenticationResult authenticate(String username, String password) {
        boolean success = userCredentials.containsKey(username) && 
                          userCredentials.get(username).equals(password);
        
        if (\!success) {
            // Record failed attempt
            failedAttempts.computeIfAbsent(username, k -> new ArrayList<>()).add(Instant.now());
            
            // Check for brute force
            List<Instant> attempts = failedAttempts.get(username);
            Instant cutoff = Instant.now().minusSeconds(bruteForceWindowSeconds);
            long recentFailures = attempts.stream()
                    .filter(time -> time.isAfter(cutoff))
                    .count();
            
            // Create authentication failure event
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "AUTHENTICATION_FAILURE");
            event.put("timestamp", Instant.now().toString());
            event.put("username", username);
            event.put("sourceIp", "127.0.0.1");
            
            Map<String, Object> details = new HashMap<>();
            details.put("reason", "INVALID_CREDENTIALS");
            event.put("details", details);
            
            securityEvents.add(event);
            
            // Check if we should trigger brute force event
            if (recentFailures >= maxFailedAttempts) {
                Map<String, Object> bruteForceEvent = new HashMap<>();
                bruteForceEvent.put("eventType", "BRUTE_FORCE_ATTEMPT_DETECTED");
                bruteForceEvent.put("timestamp", Instant.now().toString());
                bruteForceEvent.put("username", username);
                bruteForceEvent.put("sourceIp", "127.0.0.1");
                
                Map<String, Object> bruteForceDetails = new HashMap<>();
                bruteForceDetails.put("attemptCount", recentFailures);
                bruteForceDetails.put("timeWindowSeconds", bruteForceWindowSeconds);
                bruteForceEvent.put("details", bruteForceDetails);
                
                securityEvents.add(bruteForceEvent);
            }
            
            return new AuthenticationResult(false, null, "Invalid credentials");
        }
        
        // Create a session token
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, username);
        sessionExpirations.put(token, Instant.now().plusSeconds(sessionTimeoutSeconds));
        
        // Create authentication success event
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "AUTHENTICATION_SUCCESS");
        event.put("timestamp", Instant.now().toString());
        event.put("username", username);
        event.put("sourceIp", "127.0.0.1");
        
        Map<String, Object> details = new HashMap<>();
        details.put("roles", userRoles.getOrDefault(username, Collections.emptyList()));
        event.put("details", details);
        
        securityEvents.add(event);
        
        return new AuthenticationResult(true, token, null);
    }

    @Override
    public AuthorizationResult checkAuthorization(String token, String resource) {
        if (\!activeSessions.containsKey(token)) {
            return new AuthorizationResult(false, "Invalid token");
        }
        
        String username = activeSessions.get(token);
        List<String> roles = userRoles.getOrDefault(username, Collections.emptyList());
        
        // For simplicity, we'll just check if the user has the required role
        boolean authorized = roles.contains(requiredRoleForResource);
        
        if (\!authorized) {
            // Create authorization failure event
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "AUTHORIZATION_FAILURE");
            event.put("timestamp", Instant.now().toString());
            event.put("username", username);
            event.put("sourceIp", "127.0.0.1");
            
            Map<String, Object> details = new HashMap<>();
            details.put("resource", resource);
            details.put("requiredRole", requiredRoleForResource);
            details.put("userRoles", roles);
            event.put("details", details);
            
            securityEvents.add(event);
        }
        
        return new AuthorizationResult(authorized, authorized ? null : "Insufficient privileges");
    }

    @Override
    public void createSession(String token) {
        // Parse the token to extract username (in a real implementation, this would validate JWT etc.)
        String username = "testuser"; // Simplified for testing
        
        // Create session
        activeSessions.put(token, username);
        sessionExpirations.put(token, Instant.now().plusSeconds(sessionTimeoutSeconds));
    }

    @Override
    public boolean checkSession(String token) {
        if (\!activeSessions.containsKey(token)) {
            return false;
        }
        
        Instant expiration = sessionExpirations.get(token);
        if (expiration.isBefore(Instant.now())) {
            // Session expired
            String username = activeSessions.get(token);
            
            // Create session expired event
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "SESSION_EXPIRED");
            event.put("timestamp", Instant.now().toString());
            event.put("username", username);
            event.put("sourceIp", "127.0.0.1");
            
            Map<String, Object> details = new HashMap<>();
            details.put("sessionId", token);
            details.put("creationTime", expiration.minusSeconds(sessionTimeoutSeconds).toString());
            details.put("expirationTime", expiration.toString());
            event.put("details", details);
            
            securityEvents.add(event);
            
            // Remove expired session
            activeSessions.remove(token);
            sessionExpirations.remove(token);
            
            return false;
        }
        
        return true;
    }

    @Override
    public void updateSecuritySetting(String settingName, String oldValue, String newValue, String changedBy) {
        // Create configuration change event
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "SECURITY_CONFIGURATION_CHANGED");
        event.put("timestamp", Instant.now().toString());
        event.put("username", changedBy);
        event.put("sourceIp", "127.0.0.1");
        
        Map<String, Object> details = new HashMap<>();
        details.put("settingName", settingName);
        details.put("oldValue", oldValue);
        details.put("newValue", newValue);
        details.put("changedBy", changedBy);
        event.put("details", details);
        
        securityEvents.add(event);
    }
    
    /**
     * Gets the list of captured security events.
     *
     * @return List of security events.
     */
    public List<Map<String, Object>> getCapturedEvents() {
        return securityEvents;
    }
}
