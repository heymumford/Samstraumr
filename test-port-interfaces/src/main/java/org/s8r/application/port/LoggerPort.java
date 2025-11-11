/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

/**
 * Port interface for logging operations in the application.
 * This interface provides methods for logging at different levels.
 */
public interface LoggerPort {

    /**
     * Logs a message at the TRACE level.
     *
     * @param message The message to log
     */
    void trace(String message);

    /**
     * Logs a message at the DEBUG level.
     *
     * @param message The message to log
     */
    void debug(String message);

    /**
     * Logs a message at the INFO level.
     *
     * @param message The message to log
     */
    void info(String message);

    /**
     * Logs a message at the WARN level.
     *
     * @param message The message to log
     */
    void warn(String message);

    /**
     * Logs a message at the ERROR level.
     *
     * @param message The message to log
     */
    void error(String message);

    /**
     * Logs a message with an exception at the ERROR level.
     *
     * @param message The message to log
     * @param throwable The exception to log
     */
    void error(String message, Throwable throwable);
}