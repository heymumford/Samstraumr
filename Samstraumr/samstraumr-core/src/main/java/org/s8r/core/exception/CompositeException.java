/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.core.exception;

/**
 * Exception thrown when an error occurs in a composite component.
 * 
 * <p>This exception is used for errors related to composite creation, configuration,
 * operation, or component management within composites.
 */
public class CompositeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new composite exception with the specified detail message.
     *
     * @param message The detail message
     */
    public CompositeException(String message) {
        super(message);
    }

    /**
     * Constructs a new composite exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public CompositeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new composite exception with the specified cause.
     *
     * @param cause The cause of the exception
     */
    public CompositeException(Throwable cause) {
        super(cause);
    }
}