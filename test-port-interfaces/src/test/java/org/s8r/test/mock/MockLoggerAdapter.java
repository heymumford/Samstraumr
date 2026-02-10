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

package org.s8r.test.mock;

import org.s8r.application.port.LoggerPort;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of the LoggerPort interface for testing.
 */
public class MockLoggerAdapter implements LoggerPort {
    
    private final List<LogEntry> logEntries = new ArrayList<>();
    
    @Override
    public void trace(String message) {
        logEntries.add(new LogEntry(LogLevel.TRACE, message, null));
        System.out.println("TRACE: " + message);
    }
    
    @Override
    public void debug(String message) {
        logEntries.add(new LogEntry(LogLevel.DEBUG, message, null));
        System.out.println("DEBUG: " + message);
    }
    
    @Override
    public void info(String message) {
        logEntries.add(new LogEntry(LogLevel.INFO, message, null));
        System.out.println("INFO: " + message);
    }
    
    @Override
    public void warn(String message) {
        logEntries.add(new LogEntry(LogLevel.WARN, message, null));
        System.out.println("WARN: " + message);
    }
    
    @Override
    public void error(String message) {
        logEntries.add(new LogEntry(LogLevel.ERROR, message, null));
        System.err.println("ERROR: " + message);
    }
    
    @Override
    public void error(String message, Throwable throwable) {
        logEntries.add(new LogEntry(LogLevel.ERROR, message, throwable));
        System.err.println("ERROR: " + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Gets all recorded log entries.
     * 
     * @return the list of log entries
     */
    public List<LogEntry> getLogEntries() {
        return new ArrayList<>(logEntries);
    }
    
    /**
     * Clears all recorded log entries.
     */
    public void clearLogEntries() {
        logEntries.clear();
    }
    
    /**
     * Gets log entries of a specific level.
     * 
     * @param level the log level to filter by
     * @return the filtered list of log entries
     */
    public List<LogEntry> getLogEntriesForLevel(LogLevel level) {
        List<LogEntry> filtered = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            if (entry.getLevel() == level) {
                filtered.add(entry);
            }
        }
        return filtered;
    }
    
    /**
     * Gets log entries containing a specific text.
     * 
     * @param text the text to search for
     * @return the filtered list of log entries
     */
    public List<LogEntry> getLogEntriesContaining(String text) {
        List<LogEntry> filtered = new ArrayList<>();
        for (LogEntry entry : logEntries) {
            if (entry.getMessage().contains(text)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }
    
    /**
     * Represents a log entry recorded by the mock logger.
     */
    public static class LogEntry {
        private final LogLevel level;
        private final String message;
        private final Throwable throwable;
        
        public LogEntry(LogLevel level, String message, Throwable throwable) {
            this.level = level;
            this.message = message;
            this.throwable = throwable;
        }
        
        public LogLevel getLevel() {
            return level;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Throwable getThrowable() {
            return throwable;
        }
    }
    
    /**
     * Enum representing log levels.
     */
    public enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}