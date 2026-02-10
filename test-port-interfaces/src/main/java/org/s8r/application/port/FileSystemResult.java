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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Result of a file system operation.
 * Provides information about the success or failure of the operation,
 * along with any attributes or error details.
 */
public class FileSystemResult {
    private final boolean successful;
    private final String message;
    private final String errorDetails;
    private final Map<String, Object> attributes;

    /**
     * Creates a new FileSystemResult.
     *
     * @param successful Whether the operation was successful
     * @param message The result message
     * @param errorDetails The error details (if any)
     * @param attributes Additional attributes
     */
    public FileSystemResult(boolean successful, String message, String errorDetails, Map<String, Object> attributes) {
        this.successful = successful;
        this.message = message;
        this.errorDetails = errorDetails;
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
    }

    /**
     * Creates a successful result with the given message and attributes.
     *
     * @param message The success message
     * @param attributes Additional attributes
     * @return A successful FileSystemResult
     */
    public static FileSystemResult success(String message, Map<String, Object> attributes) {
        return new FileSystemResult(true, message, null, attributes);
    }

    /**
     * Creates a successful result with the given message.
     *
     * @param message The success message
     * @return A successful FileSystemResult
     */
    public static FileSystemResult success(String message) {
        return new FileSystemResult(true, message, null, Collections.emptyMap());
    }

    /**
     * Creates a failure result with the given message and error details.
     *
     * @param message The failure message
     * @param errorDetails The error details
     * @return A failure FileSystemResult
     */
    public static FileSystemResult failure(String message, String errorDetails) {
        return new FileSystemResult(false, message, errorDetails, Collections.emptyMap());
    }

    /**
     * Gets whether the operation was successful.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Gets the result message.
     *
     * @return The result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the error details.
     *
     * @return The error details, or null if the operation was successful
     */
    public String getErrorDetails() {
        return errorDetails;
    }

    /**
     * Gets the attributes.
     *
     * @return The attributes
     */
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Gets an attribute value.
     *
     * @param key The attribute key
     * @return The attribute value, or null if not found
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Gets an attribute value as a string.
     *
     * @param key The attribute key
     * @return The attribute value as a string, or null if not found
     */
    public String getAttributeAsString(String key) {
        Object value = attributes.get(key);
        return value != null ? value.toString() : null;
    }
}