/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer port for logging
 */

package org.samstraumr.application.port;

/**
 * Port interface for logging operations.
 *
 * <p>This interface defines the contract for logging operations in the application.
 * Following the ports and adapters pattern, this is an output port in the application layer,
 * which will be implemented by adapters in the infrastructure layer.
 */
public interface LoggerPort {
    
    /**
     * Logs a debug message.
     *
     * @param message The message to log
     */
    void debug(String message);
    
    /**
     * Logs a debug message with an exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    void debug(String message, Throwable e);
    
    /**
     * Logs an info message.
     *
     * @param message The message to log
     */
    void info(String message);
    
    /**
     * Logs an info message with an exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    void info(String message, Throwable e);
    
    /**
     * Logs a warning message.
     *
     * @param message The message to log
     */
    void warn(String message);
    
    /**
     * Logs a warning message with an exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    void warn(String message, Throwable e);
    
    /**
     * Logs an error message.
     *
     * @param message The message to log
     */
    void error(String message);
    
    /**
     * Logs an error message with an exception.
     *
     * @param message The message to log
     * @param e The exception to log
     */
    void error(String message, Throwable e);
}