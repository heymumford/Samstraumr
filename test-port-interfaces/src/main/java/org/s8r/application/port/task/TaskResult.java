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
     * Creates a successful task result.
     *
     * @param taskId The task ID.
     * @param message The success message.
     * @param data Additional result data.
     * @return A successful task result.
     */
    public static TaskResult success(String taskId, String message, Map<String, Object> data) {
        return new TaskResult(true, message, data);
    }

    /**
     * Creates a failed task result.
     *
     * @param taskId The task ID.
     * @param message The failure message.
     * @param error The error message.
     * @return A failed task result.
     */
    public static TaskResult failure(String taskId, String message, String error) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("error", error);
        return new TaskResult(false, message, data);
    }

    /**
     * Creates a failed task result with additional data.
     *
     * @param taskId The task ID.
     * @param message The failure message.
     * @param error The error message.
     * @param data Additional result data.
     * @return A failed task result.
     */
    public static TaskResult failure(String taskId, String message, String error, Map<String, Object> data) {
        Map<String, Object> resultData = new java.util.HashMap<>(data);
        resultData.put("error", error);
        return new TaskResult(false, message, resultData);
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
