/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.utils;

/**
 * Enumeration of task priorities for testing the task execution port.
 */
public enum TaskPriority {
    /**
     * High priority tasks should be executed before medium and low priority tasks.
     */
    HIGH(7),
    
    /**
     * Medium priority tasks should be executed after high priority tasks
     * but before low priority tasks.
     */
    MEDIUM(5),
    
    /**
     * Low priority tasks should be executed after high and medium priority tasks.
     */
    LOW(3);
    
    private final int value;
    
    TaskPriority(int value) {
        this.value = value;
    }
    
    /**
     * Gets the numeric value of the priority.
     *
     * @return The priority value
     */
    public int getValue() {
        return value;
    }
}