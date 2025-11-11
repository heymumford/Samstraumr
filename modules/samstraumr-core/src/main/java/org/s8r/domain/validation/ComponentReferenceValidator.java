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

package org.s8r.domain.validation;

import java.util.function.Function;

import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;

/**
 * Utility class for validating component references.
 * 
 * <p>This class provides methods to check if component references are valid,
 * throwing appropriate exceptions when they're not.
 */
public class ComponentReferenceValidator {

    /**
     * Validates that a referenced component exists.
     *
     * @param operationType The type of operation being performed
     * @param referringComponentId The ID of the component making the reference
     * @param referencedComponentId The ID of the component being referenced
     * @param existsFunction Function that checks if the component exists
     * @throws NonExistentComponentReferenceException if the referenced component doesn't exist
     */
    public static void validateComponentReference(
            String operationType,
            ComponentId referringComponentId,
            ComponentId referencedComponentId,
            Function<ComponentId, Boolean> existsFunction) {
        
        // Early null check to avoid NPE in the exists function
        if (referringComponentId == null) {
            throw new IllegalArgumentException("Referring component ID cannot be null");
        }
        
        if (referencedComponentId == null) {
            throw new IllegalArgumentException("Referenced component ID cannot be null");
        }
        
        // Check if the component exists using the provided function
        boolean exists = existsFunction.apply(referencedComponentId);
        
        if (!exists) {
            throw new NonExistentComponentReferenceException(
                    operationType, 
                    referringComponentId, 
                    referencedComponentId);
        }
    }
    
    /**
     * Validates that multiple referenced components exist.
     *
     * @param operationType The type of operation being performed
     * @param referringComponentId The ID of the component making the reference
     * @param existsFunction Function that checks if the component exists
     * @param referencedComponentIds The IDs of the components being referenced
     * @throws NonExistentComponentReferenceException if any referenced component doesn't exist
     */
    public static void validateComponentReferences(
            String operationType,
            ComponentId referringComponentId,
            Function<ComponentId, Boolean> existsFunction,
            ComponentId... referencedComponentIds) {
        
        if (referencedComponentIds == null || referencedComponentIds.length == 0) {
            return; // Nothing to validate
        }
        
        for (ComponentId referencedId : referencedComponentIds) {
            validateComponentReference(
                    operationType, 
                    referringComponentId, 
                    referencedId, 
                    existsFunction);
        }
    }
}