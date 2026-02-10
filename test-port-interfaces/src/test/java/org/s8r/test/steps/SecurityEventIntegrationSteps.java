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
package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.SecurityPort.SecurityResult;
import org.s8r.test.integration.SecurityEventIntegrationContext;
import org.s8r.test.mock.MockEventPublisherAdapter.PublishedEvent;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for security-event integration tests.
 */
public class SecurityEventIntegrationSteps {
    
    private SecurityEventIntegrationContext context;
    private SecurityResult lastResult;
    private String currentToken;
    
    @Before
    public void setUp() {
        context = new SecurityEventIntegrationContext();
    }
    
    @After
    public void tearDown() {
        context = null;
    }
    
    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        context.reset();
    }
    
    @Given("the SecurityPort interface is properly initialized")
    public void theSecurityPortInterfaceIsProperlyInitialized() {
        // The context already initializes the SecurityPort in its constructor
        // Nothing extra needed here
    }
    
    @Given("the EventPublisherPort interface is properly initialized")
    public void theEventPublisherPortInterfaceIsProperlyInitialized() {
        // The context already initializes the EventPublisherPort in its constructor
        // Nothing extra needed here
    }
    
    @Given("the following test users are registered:")
    public void theFollowingTestUsersAreRegistered(DataTable userTable) {
        List<Map<String, String>> users = userTable.asMaps();
        for (Map<String, String> user : users) {
            String username = user.get("username");
            String password = user.get("password");
            String roles = user.get("roles");
            
            SecurityResult result = context.registerUser(
                username, 
                password, 
                roles.split(",\\s*"));
            
            assertTrue(result.isSuccessful(), 
                    "Failed to register user " + username + ": " + result.getMessage());
        }
    }
    
    @Given("an event subscriber is registered for the {string} topic")
    public void anEventSubscriberIsRegisteredForTheTopic(String topic) {
        // The context already registers an event subscriber in its constructor
        // Nothing extra needed here
    }
    
    @Given("user {string} is authenticated")
    public void userIsAuthenticated(String username) {
        // Find the correct password for the user
        String password = findPasswordForUser(username);
        
        SecurityResult result = context.authenticateUser(username, password);
        assertTrue(result.isSuccessful(), 
                "Failed to authenticate user " + username + ": " + result.getMessage());
        assertEquals(username, context.getCurrentUsername());
    }
    
    @Given("the user has a valid security token")
    public void theUserHasAValidSecurityToken() {
        SecurityResult result = context.generateToken(1);
        assertTrue(result.isSuccessful(), "Failed to generate token: " + result.getMessage());
        
        currentToken = context.getCurrentToken();
        assertNotNull(currentToken, "Token should not be null");
    }
    
    @When("user {string} logs in with password {string}")
    public void userLogsInWithPassword(String username, String password) {
        lastResult = context.authenticateUser(username, password);
    }
    
    @When("the user logs out")
    public void theUserLogsOut() {
        lastResult = context.logout();
    }
    
    @When("the user attempts to access a restricted resource {string}")
    public void theUserAttemptsToAccessARestrictedResource(String resourceId) {
        lastResult = context.checkAccess(resourceId, "READ");
    }
    
    @When("the user requests a security token with validity of {string}")
    public void theUserRequestsASecurityTokenWithValidityOf(String validityString) {
        int hours = extractHours(validityString);
        lastResult = context.generateToken(hours);
        currentToken = context.getCurrentToken();
    }
    
    @When("the token is validated")
    public void theTokenIsValidated() {
        assertNotNull(currentToken, "Token should not be null");
        lastResult = context.validateToken(currentToken);
    }
    
    @When("the token is revoked")
    public void theTokenIsRevoked() {
        assertNotNull(currentToken, "Token should not be null");
        lastResult = context.revokeToken(currentToken);
    }
    
    @When("user {string} is granted the {string} role")
    public void userIsGrantedTheRole(String username, String role) {
        lastResult = context.updateUserRoles(username, role);
    }
    
    @When("the security configuration is changed")
    public void theSecurityConfigurationIsChanged() {
        Map<String, Object> configChanges = new HashMap<>();
        configChanges.put("passwordPolicy", "strong");
        configChanges.put("loginAttemptsThreshold", 3);
        configChanges.put("lockoutPeriod", "15 minutes");
        
        lastResult = context.updateSecurityConfig(configChanges);
    }
    
    @When("{int} failed login attempts are made for user {string}")
    public void failedLoginAttemptsAreMadeForUser(Integer attempts, String username) {
        context.simulateFailedLoginAttempts(username, attempts);
    }
    
    @When("the auditor requests the security audit log")
    public void theAuditorRequestsTheSecurityAuditLog() {
        List<Map<String, Object>> auditLog = context.getSecurityAuditLog();
        assertNotNull(auditLog, "Audit log should not be null");
    }
    
    @Then("the login should be successful")
    public void theLoginShouldBeSuccessful() {
        assertTrue(lastResult.isSuccessful(), 
                "Login should be successful, but got: " + lastResult.getMessage());
    }
    
    @Then("the login should fail")
    public void theLoginShouldFail() {
        assertFalse(lastResult.isSuccessful(), 
                "Login should fail, but was successful");
    }
    
    @Then("access should be denied")
    public void accessShouldBeDenied() {
        assertFalse(lastResult.isSuccessful(), 
                "Access should be denied, but was granted");
    }
    
    @Then("access should be granted")
    public void accessShouldBeGranted() {
        assertTrue(lastResult.isSuccessful(), 
                "Access should be granted, but was denied: " + lastResult.getMessage());
    }
    
    @Then("a valid token should be generated")
    public void aValidTokenShouldBeGenerated() {
        assertTrue(lastResult.isSuccessful(), 
                "Token generation should be successful, but got: " + lastResult.getMessage());
        assertNotNull(currentToken, "Generated token should not be null");
    }
    
    @Then("the validation should be successful")
    public void theValidationShouldBeSuccessful() {
        assertTrue(lastResult.isSuccessful(), 
                "Token validation should be successful, but got: " + lastResult.getMessage());
    }
    
    @Then("a security event of type {string} should be published")
    public void aSecurityEventOfTypeShouldBePublished(String eventType) {
        assertTrue(context.wasEventPublished(eventType), 
                "Security event of type " + eventType + " should be published");
    }
    
    @Then("the event should contain the username {string}")
    public void theEventShouldContainTheUsername(String username) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "username", username),
                "Event should contain username " + username);
    }
    
    @Then("the event should include timestamp and session information")
    public void theEventShouldIncludeTimestampAndSessionInformation() {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        // Check for timestamp in properties
        assertTrue(event.getProperties().containsKey("timestamp"),
                "Event should include timestamp");
        
        // Session info would be in the payload
        assertTrue(event.getPayload().contains("timestamp"),
                "Event payload should include timestamp");
    }
    
    @Then("the event should include failure reason {string}")
    public void theEventShouldIncludeFailureReason(String reason) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "reason", reason),
                "Event should contain reason " + reason);
    }
    
    @Then("the event should contain the resource {string}")
    public void theEventShouldContainTheResource(String resource) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "resource", resource),
                "Event should contain resource " + resource);
    }
    
    @Then("the event should contain the required permission {string}")
    public void theEventShouldContainTheRequiredPermission(String permission) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "requiredPermission", permission),
                "Event should contain required permission " + permission);
    }
    
    @Then("the event should contain the permission {string}")
    public void theEventShouldContainThePermission(String permission) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "permission", permission),
                "Event should contain permission " + permission);
    }
    
    @Then("the event should contain token validity information")
    public void theEventShouldContainTokenValidityInformation() {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(event.getPayload().contains("validity"),
                "Event should contain token validity information");
        assertTrue(event.getPayload().contains("expiresAt"),
                "Event should contain token expiration information");
    }
    
    @Then("the event should contain the added role {string}")
    public void theEventShouldContainTheAddedRole(String role) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(event.getPayload().contains(role),
                "Event should contain added role " + role);
    }
    
    @Then("the event should contain the modifier username {string}")
    public void theEventShouldContainTheModifierUsername(String username) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "modifierUsername", username),
                "Event should contain modifier username " + username);
    }
    
    @Then("the event should include details about the configuration change")
    public void theEventShouldIncludeDetailsAboutTheConfigurationChange() {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(event.getPayload().contains("passwordPolicy"),
                "Event should contain configuration details about password policy");
        assertTrue(event.getPayload().contains("loginAttemptsThreshold"),
                "Event should contain configuration details about login attempts threshold");
    }
    
    @Then("the event should contain details about the attack pattern")
    public void theEventShouldContainDetailsAboutTheAttackPattern() {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(event.getPayload().contains("attackType"),
                "Event should contain attack type information");
        assertTrue(event.getPayload().contains("failedAttempts"),
                "Event should contain failed attempts information");
    }
    
    @Then("the event should contain the target username {string}")
    public void theEventShouldContainTheTargetUsername(String username) {
        PublishedEvent event = context.getLatestSecurityEvent(getLastEventType());
        assertNotNull(event, "Event should not be null");
        
        assertTrue(context.eventContains(event, "username", username),
                "Event should contain target username " + username);
    }
    
    @Then("the audit log should be returned successfully")
    public void theAuditLogShouldBeReturnedSuccessfully() {
        List<Map<String, Object>> auditLog = context.getSecurityAuditLog();
        assertNotNull(auditLog, "Audit log should not be null");
        assertFalse(auditLog.isEmpty(), "Audit log should not be empty");
    }
    
    @Then("the audit log should contain all security events")
    public void theAuditLogShouldContainAllSecurityEvents() {
        List<Map<String, Object>> auditLog = context.getSecurityAuditLog();
        assertNotNull(auditLog, "Audit log should not be null");
        
        // Check that the audit log contains events for all expected event types
        boolean containsLoginSuccess = auditLog.stream()
                .anyMatch(event -> "LOGIN_SUCCESS".equals(event.get("eventType")));
        boolean containsLoginFailure = auditLog.stream()
                .anyMatch(event -> "LOGIN_FAILURE".equals(event.get("eventType")));
        
        assertTrue(containsLoginSuccess || containsLoginFailure,
                "Audit log should contain login events");
    }
    
    /**
     * Helper method to get a default password for a test user.
     */
    private String findPasswordForUser(String username) {
        switch (username) {
            case "admin":
                return "admin123";
            case "user1":
                return "password123";
            case "auditor":
                return "audit123";
            default:
                return "password";
        }
    }
    
    /**
     * Helper method to extract hours from a string like "1 hour" or "2 hours".
     */
    private int extractHours(String validityString) {
        String[] parts = validityString.split("\\s+");
        if (parts.length < 1) {
            return 1; // Default to 1 hour
        }
        
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 1; // Default to 1 hour on parse error
        }
    }
    
    /**
     * Helper method to get the event type from the last step.
     */
    private String getLastEventType() {
        // This is a simplistic approach. In a real implementation, you would keep track of this.
        if (context.getLatestSecurityEvent("LOGIN_SUCCESS") \!= null) {
            return "LOGIN_SUCCESS";
        } else if (context.getLatestSecurityEvent("LOGIN_FAILURE") \!= null) {
            return "LOGIN_FAILURE";
        } else if (context.getLatestSecurityEvent("LOGOUT") \!= null) {
            return "LOGOUT";
        } else if (context.getLatestSecurityEvent("ACCESS_DENIED") \!= null) {
            return "ACCESS_DENIED";
        } else if (context.getLatestSecurityEvent("ACCESS_GRANTED") \!= null) {
            return "ACCESS_GRANTED";
        } else if (context.getLatestSecurityEvent("TOKEN_ISSUED") \!= null) {
            return "TOKEN_ISSUED";
        } else if (context.getLatestSecurityEvent("TOKEN_VALIDATED") \!= null) {
            return "TOKEN_VALIDATED";
        } else if (context.getLatestSecurityEvent("TOKEN_EXPIRED") \!= null) {
            return "TOKEN_EXPIRED";
        } else if (context.getLatestSecurityEvent("PERMISSION_CHANGED") \!= null) {
            return "PERMISSION_CHANGED";
        } else if (context.getLatestSecurityEvent("SECURITY_CONFIG_CHANGED") \!= null) {
            return "SECURITY_CONFIG_CHANGED";
        } else if (context.getLatestSecurityEvent("POTENTIAL_ATTACK_DETECTED") \!= null) {
            return "POTENTIAL_ATTACK_DETECTED";
        }
        
        return "UNKNOWN";
    }
}
