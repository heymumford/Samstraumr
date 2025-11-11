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

import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Exception thrown when components are identified as potential duplicates based on property analysis.
 *
 * <p>This extends the basic DuplicateComponentException to provide additional information about why
 * components are considered duplicates based on their properties rather than just their IDs.
 */
public class PropertyBasedDuplicateComponentException extends DuplicateComponentException {
    private static final long serialVersionUID = 1L;

    private final ComponentId existingComponentId;
    private final Set<String> duplicateProperties;
    private final double similarityScore;
    private final String duplicateReason;

    /**
     * Creates a new PropertyBasedDuplicateComponentException with detailed information about the duplicate.
     *
     * @param newComponentId The ID of the component that was detected as a duplicate
     * @param existingComponentId The ID of the existing component that matches
     * @param duplicateProperties Set of property names that were used to determine the duplication
     * @param similarityScore A score between 0 and 1 indicating how similar the components are
     * @param duplicateReason A description of why the components are considered duplicates
     */
    public PropertyBasedDuplicateComponentException(
            ComponentId newComponentId,
            ComponentId existingComponentId,
            Set<String> duplicateProperties,
            double similarityScore,
            String duplicateReason) {
        super(buildMessage(newComponentId, existingComponentId, duplicateProperties, duplicateReason), newComponentId);
        this.existingComponentId = existingComponentId;
        this.duplicateProperties = new HashSet<>(duplicateProperties);
        this.similarityScore = similarityScore;
        this.duplicateReason = duplicateReason;
    }

    /**
     * Creates a new PropertyBasedDuplicateComponentException with information derived from component analysis.
     *
     * @param newComponent The component that was detected as a duplicate
     * @param existingComponent The existing component that matches
     * @param duplicateProperties Set of property names that were used to determine the duplication
     * @param similarityScore A score between 0 and 1 indicating how similar the components are
     * @param duplicateReason A description of why the components are considered duplicates
     */
    public PropertyBasedDuplicateComponentException(
            ComponentPort newComponent,
            ComponentPort existingComponent,
            Set<String> duplicateProperties,
            double similarityScore,
            String duplicateReason) {
        this(
            newComponent.getId(),
            existingComponent.getId(),
            duplicateProperties,
            similarityScore,
            duplicateReason
        );
    }

    /**
     * Gets the ID of the existing component that was found to be a duplicate.
     *
     * @return The existing component's ID
     */
    public ComponentId getExistingComponentId() {
        return existingComponentId;
    }

    /**
     * Gets the set of property names that were used to determine the duplication.
     *
     * @return The set of duplicate property names
     */
    public Set<String> getDuplicateProperties() {
        return new HashSet<>(duplicateProperties);
    }

    /**
     * Gets the similarity score between the components.
     *
     * @return A score between 0 and 1 indicating how similar the components are
     */
    public double getSimilarityScore() {
        return similarityScore;
    }

    /**
     * Gets a description of why the components are considered duplicates.
     *
     * @return The duplicate reason
     */
    public String getDuplicateReason() {
        return duplicateReason;
    }

    /**
     * Builds a detailed error message for the exception.
     */
    private static String buildMessage(
            ComponentId newComponentId,
            ComponentId existingComponentId,
            Set<String> duplicateProperties,
            String duplicateReason) {
        return String.format(
            "Potential duplicate component detected: Component with ID %s appears to be a duplicate of existing component %s. Reason: %s. Matching properties: %s",
            newComponentId,
            existingComponentId,
            duplicateReason,
            String.join(", ", duplicateProperties)
        );
    }
}