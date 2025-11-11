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

/**
 * Result of a task scheduling operation.
 */
public class TaskScheduleResult {
    private final boolean success;
    private final String taskId;
    private final String errorMessage;
    
    /**
     * Creates a new task schedule result.
     *
     * @param success Whether the scheduling was successful.
     * @param taskId The ID of the scheduled task if successful, null otherwise.
     * @param errorMessage The error message if unsuccessful, null otherwise.
     */
    public TaskScheduleResult(boolean success, String taskId, String errorMessage) {
        this.success = success;
        this.taskId = taskId;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets whether the scheduling was successful.
     *
     * @return Whether the scheduling was successful.
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gets the ID of the scheduled task if successful, null otherwise.
     *
     * @return The ID of the scheduled task if successful, null otherwise.
     */
    public String getTaskId() {
        return taskId;
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
