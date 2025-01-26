package org.samstraumr.tube.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TubeLogger {
    private static final Logger logger = LoggerFactory.getLogger(TubeLogger.class);

    // Method to log information messages
    public static void info(String message) {
        logger.info(message);
    }

    // Method to log warning messages
    public static void warn(String message) {
        logger.warn(message);
    }

    // Method to log error messages
    public static void error(String message) {
        logger.error(message);
    }

    // Method to log debug messages
    public static void debug(String message) {
        logger.debug(message);
    }

    // Additional logging-related functionality can be added here
}
