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
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.context;

import org.s8r.application.port.security.SecurityPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Context for sharing data between SecurityPort test steps.
 */
public class SecurityPortTestContext {
    private SecurityPort securityPort;
    private SecurityPort.AuthenticationResult lastAuthResult;
    private SecurityPort.TokenValidationResult lastTokenValidationResult;
    private SecurityPort.TokenRefreshResult lastTokenRefreshResult;
    private SecurityPort.OperationResult lastOperationResult;
    private SecurityPort.TokenGenerationResult lastTokenGenerationResult;
    private SecurityPort.UserOperationResult lastUserOperationResult;
    
    private String currentUsername;
    private String currentPassword;
    private String currentToken;
    private String currentRefreshToken;
    private String currentUserId;
    private Set<String> userRoles = new HashSet<>();
    private Set<String> userPermissions = new HashSet<>();
    
    private Map<String, Object> testUsers = new HashMap<>();
    private Map<String, String> testTokens = new HashMap<>();
    private List<SecurityPort.AuditLogEntry> auditLogEntries = new ArrayList<>();

    // Performance metrics
    private long startTime;
    private long endTime;
    private List<Long> operationTimes = new ArrayList<>();
    
    // Multiple security contexts for isolation testing
    private Map<String, SecurityPort> securityContexts = new HashMap<>();

    public SecurityPort getSecurityPort() {
        return securityPort;
    }

    public void setSecurityPort(SecurityPort securityPort) {
        this.securityPort = securityPort;
    }

    public SecurityPort.AuthenticationResult getLastAuthResult() {
        return lastAuthResult;
    }

    public void setLastAuthResult(SecurityPort.AuthenticationResult lastAuthResult) {
        this.lastAuthResult = lastAuthResult;
    }

    public SecurityPort.TokenValidationResult getLastTokenValidationResult() {
        return lastTokenValidationResult;
    }

    public void setLastTokenValidationResult(SecurityPort.TokenValidationResult lastTokenValidationResult) {
        this.lastTokenValidationResult = lastTokenValidationResult;
    }

    public SecurityPort.TokenRefreshResult getLastTokenRefreshResult() {
        return lastTokenRefreshResult;
    }

    public void setLastTokenRefreshResult(SecurityPort.TokenRefreshResult lastTokenRefreshResult) {
        this.lastTokenRefreshResult = lastTokenRefreshResult;
    }

    public SecurityPort.OperationResult getLastOperationResult() {
        return lastOperationResult;
    }

    public void setLastOperationResult(SecurityPort.OperationResult lastOperationResult) {
        this.lastOperationResult = lastOperationResult;
    }

    public SecurityPort.TokenGenerationResult getLastTokenGenerationResult() {
        return lastTokenGenerationResult;
    }

    public void setLastTokenGenerationResult(SecurityPort.TokenGenerationResult lastTokenGenerationResult) {
        this.lastTokenGenerationResult = lastTokenGenerationResult;
    }

    public SecurityPort.UserOperationResult getLastUserOperationResult() {
        return lastUserOperationResult;
    }

    public void setLastUserOperationResult(SecurityPort.UserOperationResult lastUserOperationResult) {
        this.lastUserOperationResult = lastUserOperationResult;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public String getCurrentRefreshToken() {
        return currentRefreshToken;
    }

    public void setCurrentRefreshToken(String currentRefreshToken) {
        this.currentRefreshToken = currentRefreshToken;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Set<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<String> userRoles) {
        this.userRoles = userRoles;
    }

    public Set<String> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(Set<String> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public Map<String, Object> getTestUsers() {
        return testUsers;
    }

    public void addTestUser(String username, Map<String, Object> userData) {
        testUsers.put(username, userData);
    }

    public Map<String, String> getTestTokens() {
        return testTokens;
    }

    public void addTestToken(String userId, String token) {
        testTokens.put(userId, token);
    }

    public List<SecurityPort.AuditLogEntry> getAuditLogEntries() {
        return auditLogEntries;
    }

    public void setAuditLogEntries(List<SecurityPort.AuditLogEntry> auditLogEntries) {
        this.auditLogEntries = auditLogEntries;
    }

    public void addAuditLogEntry(SecurityPort.AuditLogEntry entry) {
        auditLogEntries.add(entry);
    }

    public void startPerformanceTimer() {
        startTime = System.nanoTime();
    }

    public void stopPerformanceTimer() {
        endTime = System.nanoTime();
        operationTimes.add(endTime - startTime);
    }

    public long getLastOperationTime() {
        return operationTimes.isEmpty() ? 0 : operationTimes.get(operationTimes.size() - 1);
    }

    public double getAverageOperationTime() {
        if (operationTimes.isEmpty()) {
            return 0;
        }
        long sum = 0;
        for (Long time : operationTimes) {
            sum += time;
        }
        return (double) sum / operationTimes.size() / 1_000_000.0; // Convert to milliseconds
    }

    public long getMaxOperationTime() {
        if (operationTimes.isEmpty()) {
            return 0;
        }
        long max = Long.MIN_VALUE;
        for (Long time : operationTimes) {
            if (time > max) {
                max = time;
            }
        }
        return max / 1_000_000; // Convert to milliseconds
    }

    public void clearPerformanceMetrics() {
        operationTimes.clear();
        startTime = 0;
        endTime = 0;
    }

    public void addSecurityContext(String name, SecurityPort securityPort) {
        securityContexts.put(name, securityPort);
    }

    public SecurityPort getSecurityContext(String name) {
        return securityContexts.get(name);
    }

    public void clearAllContexts() {
        securityContexts.clear();
    }
}