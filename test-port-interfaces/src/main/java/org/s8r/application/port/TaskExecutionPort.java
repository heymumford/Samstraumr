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

package org.s8r.application.port;

import org.s8r.application.port.task.Task;
import org.s8r.application.port.task.TaskScheduleResult;

/**
 * Port interface for task execution operations.
 */
public interface TaskExecutionPort {
    /**
     * Schedules a task for execution.
     *
     * @param taskId The task ID.
     * @param name The task name.
     * @param description The task description.
     * @param scheduledTime The scheduled time (can be an instant string or delay in milliseconds).
     * @return The result of the scheduling operation.
     */
    TaskScheduleResult scheduleTask(String taskId, String name, String description, Object scheduledTime);
    
    /**
     * Schedules a recurring task for execution.
     *
     * @param taskId The task ID.
     * @param name The task name.
     * @param description The task description.
     * @param recurringConfig The recurring configuration.
     * @return The result of the scheduling operation.
     */
    TaskScheduleResult scheduleRecurringTask(String taskId, String name, String description, Object recurringConfig);
    
    /**
     * Gets a task by ID.
     *
     * @param taskId The task ID.
     * @return The task, or null if not found.
     */
    Task getTask(String taskId);
    
    /**
     * Cancels a task.
     *
     * @param taskId The task ID.
     * @return Whether the task was cancelled successfully.
     */
    boolean cancelTask(String taskId);
    
    /**
     * Updates a task.
     *
     * @param task The task to update.
     * @return Whether the task was updated successfully.
     */
    boolean updateTask(Task task);
}
