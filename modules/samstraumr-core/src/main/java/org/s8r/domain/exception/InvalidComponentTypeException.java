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

package org.s8r.domain.exception;

import org.s8r.domain.identity.ComponentId;

/**
 * Exception thrown when a component has an invalid type or when a component
 * type is not allowed for a specific operation.
 */
public class InvalidComponentTypeException extends ComponentException {
    private static final long serialVersionUID = 1L;
    
    private final String invalidType;
    private final ComponentId componentId;
    private final String operation;
    
    /**
     * Creates a new InvalidComponentTypeException when the type is not recognized.
     *
     * @param type The invalid component type
     * @param componentId The ID of the component with the invalid type
     */
    public InvalidComponentTypeException(String type, ComponentId componentId) {
        super(String.format("Invalid component type '%s' for component %s", 
                type, componentId.getShortId()));
        this.invalidType = type;
        this.componentId = componentId;
        this.operation = null;
    }
    
    /**
     * Creates a new InvalidComponentTypeException when the type is not allowed for a specific operation.
     *
     * @param type The invalid component type
     * @param componentId The ID of the component with the invalid type
     * @param operation The operation that is not allowed for this component type
     */
    public InvalidComponentTypeException(String type, ComponentId componentId, String operation) {
        super(String.format("Component type '%s' not allowed for operation '%s' (component %s)", 
                type, operation, componentId.getShortId()));
        this.invalidType = type;
        this.componentId = componentId;
        this.operation = operation;
    }
    
    /**
     * Gets the invalid component type.
     *
     * @return The invalid type
     */
    public String getInvalidType() {
        return invalidType;
    }
    
    /**
     * Gets the ID of the component with the invalid type.
     *
     * @return The component ID
     */
    public ComponentId getComponentId() {
        return componentId;
    }
    
    /**
     * Gets the operation that was attempted (if applicable).
     *
     * @return The operation, or null if not applicable
     */
    public String getOperation() {
        return operation;
    }
}