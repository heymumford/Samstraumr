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
 * Result of an authentication operation.
 */
public class AuthenticationResult {
    private final boolean success;
    private final String token;
    private final String errorMessage;
    
    /**
     * Creates a new authentication result.
     *
     * @param success Whether the authentication was successful.
     * @param token The authentication token if successful, null otherwise.
     * @param errorMessage The error message if unsuccessful, null otherwise.
     */
    public AuthenticationResult(boolean success, String token, String errorMessage) {
        this.success = success;
        this.token = token;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets whether the authentication was successful.
     *
     * @return Whether the authentication was successful.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the authentication token if successful, null otherwise.
     *
     * @return The authentication token if successful, null otherwise.
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Gets the error message if unsuccessful, null otherwise.
     *
     * @return The error message if unsuccessful, null otherwise.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
