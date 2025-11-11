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
package org.s8r.test.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Utility class for bridging between old and new Notification APIs.
 * 
 * <p>This class helps migrate tests from old Notification API patterns to new ones
 * without requiring extensive code changes.
 * 
 * <p>This is a placeholder implementation that will be completed once the
 * port interface dependencies are properly resolved.
 */
public class NotificationCompatUtil {
    
    /**
     * Converts a string to an enum value.
     * 
     * @param enumClass The enum class
     * @param value The string value
     * @param defaultValue The default value if conversion fails
     * @return The corresponding enum value
     */
    public static <T extends Enum<T>> T convertEnum(Class<T> enumClass, String value, T defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
    
    /**
     * Creates a map representation of a result object.
     * 
     * @param status The status string
     * @param timestamp The timestamp
     * @param subject The subject
     * @param message The message
     * @return A map representation of the result
     */
    public static Map<String, Object> createResultMap(String status, long timestamp, String subject, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("timestamp", timestamp);
        map.put("subject", subject);
        map.put("message", message);
        return map;
    }
    
    /**
     * Gets a string representation of a result map for logging.
     * 
     * @param resultMap The result map to represent
     * @return A string representation of the result map
     */
    public static String toString(Map<String, Object> resultMap) {
        if (resultMap == null) {
            return "null";
        }
        
        return "Result[status=" + resultMap.get("status") + 
               ", subject=" + resultMap.get("subject") + 
               ", timestamp=" + resultMap.get("timestamp") + "]";
    }
}