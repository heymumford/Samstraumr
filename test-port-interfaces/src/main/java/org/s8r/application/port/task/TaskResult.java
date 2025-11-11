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

package org.s8r.application.port.task;

import java.util.Map;

/**
 * Result of a task execution.
 */
public class TaskResult {
    private final boolean success;
    private final String message;
    private final Map<String, Object> data;
    
    /**
     * Creates a new task result.
     *
     * @param success Whether the task was successful.
     * @param message The result message.
     * @param data Additional result data.
     */
    public TaskResult(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Gets whether the task was successful.
     *
     * @return Whether the task was successful.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the result message.
     *
     * @return The result message.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the additional result data.
     *
     * @return The additional result data.
     */
    public Map<String, Object> getData() {
        return data;
    }
}
