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

package org.s8r.application.port.security;

/**
 * Port interface for security-related operations.
 */
public interface SecurityPort {
    /**
     * Authenticates a user with the given credentials.
     *
     * @param username The username.
     * @param password The password.
     * @return The result of the authentication.
     */
    AuthenticationResult authenticate(String username, String password);
    
    /**
     * Checks if the user is authorized to access the given resource.
     *
     * @param token The authentication token.
     * @param resource The resource to access.
     * @return The result of the authorization check.
     */
    AuthorizationResult checkAuthorization(String token, String resource);
    
    /**
     * Creates a new session for the given token.
     *
     * @param token The authentication token.
     */
    void createSession(String token);
    
    /**
     * Checks if the session for the given token is valid.
     *
     * @param token The authentication token.
     * @return Whether the session is valid.
     */
    boolean checkSession(String token);
    
    /**
     * Updates a security setting.
     *
     * @param settingName The name of the setting.
     * @param oldValue The old value.
     * @param newValue The new value.
     * @param changedBy The user who changed the setting.
     */
    void updateSecuritySetting(String settingName, String oldValue, String newValue, String changedBy);
}
