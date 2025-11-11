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

package org.s8r.application.port.event;

/**
 * Result of an event delivery operation.
 */
public class EventDeliveryResult {
    private final boolean success;
    private final String errorMessage;
    
    /**
     * Creates a new event delivery result.
     *
     * @param success Whether the delivery was successful.
     * @param errorMessage The error message if unsuccessful, null otherwise.
     */
    public EventDeliveryResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets whether the delivery was successful.
     *
     * @return Whether the delivery was successful.
     */
    public boolean isSuccess() {
        return success;
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
