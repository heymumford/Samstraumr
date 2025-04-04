/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Infrastructure implementation of LoggerPort using SLF4J
 */

package org.samstraumr.infrastructure.logging;

import org.samstraumr.application.port.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J implementation of the LoggerPort interface.
 *
 * <p>This adapter connects the application layer's LoggerPort to the SLF4J logging framework,
 * following Clean Architecture principles by implementing an application layer port.
 */
public class Slf4jLogger implements LoggerPort {
    private final Logger logger;
    
    /**
     * Creates a new SLF4J logger for the specified class.
     *
     * @param clazz The class to create the logger for
     */
    public Slf4jLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Creates a new SLF4J logger with the specified name.
     *
     * @param name The logger name
     */
    public Slf4jLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }
    
    @Override
    public void debug(String message) {
        logger.debug(message);
    }
    
    @Override
    public void debug(String message, Throwable e) {
        logger.debug(message, e);
    }
    
    @Override
    public void info(String message) {
        logger.info(message);
    }
    
    @Override
    public void info(String message, Throwable e) {
        logger.info(message, e);
    }
    
    @Override
    public void warn(String message) {
        logger.warn(message);
    }
    
    @Override
    public void warn(String message, Throwable e) {
        logger.warn(message, e);
    }
    
    @Override
    public void error(String message) {
        logger.error(message);
    }
    
    @Override
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }
}