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

package org.s8r.application.port.task;

import java.util.Map;

/**
 * Represents a task in the system.
 */
public class Task {
    private String id;
    private String name;
    private String description;
    private String status;
    private String createdAt;
    private String scheduledAt;
    private String startedAt;
    private String completedAt;
    private Map<String, Object> progress;
    private TaskResult result;
    private String errorDetails;
    private boolean recurring;
    private Map<String, Object> recurringConfig;
    private String parentTaskId;
    private int iterationCount;
    
    /**
     * Gets the task ID.
     *
     * @return The task ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the task ID.
     *
     * @param id The task ID.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the task name.
     *
     * @return The task name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the task name.
     *
     * @param name The task name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the task description.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the task description.
     *
     * @param description The task description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the task status.
     *
     * @return The task status.
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the task status.
     *
     * @param status The task status.
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the task creation timestamp.
     *
     * @return The task creation timestamp.
     */
    public String getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the task creation timestamp.
     *
     * @param createdAt The task creation timestamp.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the task scheduled timestamp.
     *
     * @return The task scheduled timestamp.
     */
    public String getScheduledAt() {
        return scheduledAt;
    }
    
    /**
     * Sets the task scheduled timestamp.
     *
     * @param scheduledAt The task scheduled timestamp.
     */
    public void setScheduledAt(String scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    /**
     * Gets the task start timestamp.
     *
     * @return The task start timestamp.
     */
    public String getStartedAt() {
        return startedAt;
    }
    
    /**
     * Sets the task start timestamp.
     *
     * @param startedAt The task start timestamp.
     */
    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }
    
    /**
     * Gets the task completion timestamp.
     *
     * @return The task completion timestamp.
     */
    public String getCompletedAt() {
        return completedAt;
    }
    
    /**
     * Sets the task completion timestamp.
     *
     * @param completedAt The task completion timestamp.
     */
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
    
    /**
     * Gets the task progress.
     *
     * @return The task progress.
     */
    public Map<String, Object> getProgress() {
        return progress;
    }
    
    /**
     * Sets the task progress.
     *
     * @param progress The task progress.
     */
    public void setProgress(Map<String, Object> progress) {
        this.progress = progress;
    }
    
    /**
     * Gets the task result.
     *
     * @return The task result.
     */
    public TaskResult getResult() {
        return result;
    }
    
    /**
     * Sets the task result.
     *
     * @param result The task result.
     */
    public void setResult(TaskResult result) {
        this.result = result;
    }
    
    /**
     * Gets the task error details.
     *
     * @return The task error details.
     */
    public String getErrorDetails() {
        return errorDetails;
    }
    
    /**
     * Sets the task error details.
     *
     * @param errorDetails The task error details.
     */
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
    
    /**
     * Gets whether the task is recurring.
     *
     * @return Whether the task is recurring.
     */
    public boolean isRecurring() {
        return recurring;
    }
    
    /**
     * Sets whether the task is recurring.
     *
     * @param recurring Whether the task is recurring.
     */
    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }
    
    /**
     * Gets the recurring configuration.
     *
     * @return The recurring configuration.
     */
    public Map<String, Object> getRecurringConfig() {
        return recurringConfig;
    }
    
    /**
     * Sets the recurring configuration.
     *
     * @param recurringConfig The recurring configuration.
     */
    public void setRecurringConfig(Map<String, Object> recurringConfig) {
        this.recurringConfig = recurringConfig;
    }
    
    /**
     * Gets the parent task ID.
     *
     * @return The parent task ID.
     */
    public String getParentTaskId() {
        return parentTaskId;
    }
    
    /**
     * Sets the parent task ID.
     *
     * @param parentTaskId The parent task ID.
     */
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    
    /**
     * Gets the iteration count.
     *
     * @return The iteration count.
     */
    public int getIterationCount() {
        return iterationCount;
    }
    
    /**
     * Sets the iteration count.
     *
     * @param iterationCount The iteration count.
     */
    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }
}
