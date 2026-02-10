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

package org.s8r.application.port.security;

/**
 * Result of an authorization operation.
 */
public class AuthorizationResult {
    private final boolean authorized;
    private final String reason;
    
    /**
     * Creates a new authorization result.
     *
     * @param authorized Whether the user is authorized.
     * @param reason The reason if not authorized, null otherwise.
     */
    public AuthorizationResult(boolean authorized, String reason) {
        this.authorized = authorized;
        this.reason = reason;
    }
    
    /**
     * Gets whether the user is authorized.
     *
     * @return Whether the user is authorized.
     */
    public boolean isAuthorized() {
        return authorized;
    }
    
    /**
     * Gets the reason if not authorized, null otherwise.
     *
     * @return The reason if not authorized, null otherwise.
     */
    public String getReason() {
        return reason;
    }
}
