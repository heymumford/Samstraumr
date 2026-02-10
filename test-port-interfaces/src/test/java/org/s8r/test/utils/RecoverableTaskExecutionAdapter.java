/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.utils;

import org.s8r.application.port.TaskExecutionPort;
import org.s8r.infrastructure.task.ThreadPoolTaskExecutionAdapter;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A task execution adapter that can simulate failures and recover from them.
 * Used for testing error recovery scenarios.
 */
public class RecoverableTaskExecutionAdapter extends ThreadPoolTaskExecutionAdapter {
    
    private final AtomicBoolean failureMode = new AtomicBoolean(false);
    private final AtomicInteger recoveryAttempts = new AtomicInteger(0);
    
    /**
     * Creates a new RecoverableTaskExecutionAdapter with the default thread pool size.
     */
    public RecoverableTaskExecutionAdapter() {
        super();
    }
    
    /**
     * Creates a new RecoverableTaskExecutionAdapter with the specified thread pool size.
     *
     * @param poolSize The number of threads in the pool
     */
    public RecoverableTaskExecutionAdapter(int poolSize) {
        super(poolSize);
    }
    
    /**
     * Sets the failure mode of the adapter.
     *
     * @param failureMode Whether the adapter should simulate failures
     */
    public void setFailureMode(boolean failureMode) {
        this.failureMode.set(failureMode);
    }
    
    /**
     * Checks if the adapter is in failure mode.
     *
     * @return true if the adapter is in failure mode, false otherwise
     */
    public boolean shouldFail() {
        return failureMode.get();
    }
    
    /**
     * Increments the recovery attempts counter and disables failure mode
     * if the number of attempts exceeds a threshold.
     *
     * @return The new number of recovery attempts
     */
    public int incrementRecoveryAttempts() {
        int attempts = recoveryAttempts.incrementAndGet();
        if (attempts >= 2) {
            // After two attempts, disable failure mode to allow success
            failureMode.set(false);
        }
        return attempts;
    }
    
    /**
     * Gets the number of recovery attempts.
     *
     * @return The number of recovery attempts
     */
    public int getRecoveryAttempts() {
        return recoveryAttempts.get();
    }
    
    /**
     * Resets the recovery attempts counter.
     */
    public void resetRecoveryAttempts() {
        recoveryAttempts.set(0);
    }
}