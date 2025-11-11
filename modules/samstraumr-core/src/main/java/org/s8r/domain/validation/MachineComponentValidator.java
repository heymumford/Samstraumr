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
import java.util.function.Predicate;

import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.exception.InvalidCompositeTypeException;
import org.s8r.domain.exception.NonExistentComponentReferenceException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;

/**
 * Utility class for validating machine components.
 * 
 * <p>This class provides methods to ensure that components added to machines
 * exist and meet the requirements for being part of a machine.
 */
public class MachineComponentValidator {
    
    /**
     * Validates that a component exists before adding it to a machine.
     *
     * @param machineId The ID of the machine
     * @param componentId The ID of the component to validate
     * @param existsFunction Function that checks if the component exists
     * @throws NonExistentComponentReferenceException if the component doesn't exist
     */
    public static void validateComponentExists(
            ComponentId machineId,
            ComponentId componentId,
            Function<ComponentId, Boolean> existsFunction) {
        
        ComponentReferenceValidator.validateComponentReference(
                "addComponent", 
                machineId, 
                componentId, 
                existsFunction);
    }
    
    /**
     * Validates that a component is a composite component.
     *
     * @param machineId The ID of the machine
     * @param component The component to validate
     * @throws InvalidCompositeTypeException if the component is not a composite
     */
    public static void validateIsCompositeComponent(
            ComponentId machineId,
            Object component) {
        
        if (!(component instanceof CompositeComponent)) {
            if (component == null) {
                throw new IllegalArgumentException("Component cannot be null");
            }
            
            throw new InvalidCompositeTypeException(
                    machineId,
                    component.getClass().getName());
        }
    }
    
    /**
     * Validates that a component meets all requirements for being added to a machine.
     *
     * @param machine The machine to which the component will be added
     * @param component The component to validate
     * @param existsFunction Function that checks if the component exists
     * @throws NonExistentComponentReferenceException if the component doesn't exist
     * @throws InvalidCompositeTypeException if the component is not a composite
     */
    public static void validateMachineComponent(
            Machine machine,
            CompositeComponent component) {
        
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        
        // Validate that the component is a composite (already guaranteed by the parameter type)
        validateIsCompositeComponent(machine.getId(), component);
        
        // Additional validation rules can be added here
        // For example, we could check if the component's state is compatible with the machine's state
    }
    
    /**
     * Validates multiple components for a machine.
     *
     * @param machine The machine to which the components will be added
     * @param components The components to validate
     * @throws NonExistentComponentReferenceException if any component doesn't exist
     * @throws InvalidCompositeTypeException if any component is not a composite
     */
    public static void validateMachineComponents(
            Machine machine,
            CompositeComponent... components) {
        
        if (components == null || components.length == 0) {
            return; // Nothing to validate
        }
        
        for (CompositeComponent component : components) {
            validateMachineComponent(machine, component);
        }
    }
    
    /**
     * Validates that a component reference exists and is a valid composite.
     *
     * @param machine The machine making the reference
     * @param componentId The ID of the component to validate
     * @param existsFunction Function that checks if the component exists
     * @param retrieveFunction Function to retrieve the component by ID
     * @throws NonExistentComponentReferenceException if the component doesn't exist
     * @throws InvalidCompositeTypeException if the component is not a composite
     */
    public static void validateComponentReference(
            Machine machine,
            ComponentId componentId,
            Function<ComponentId, Boolean> existsFunction,
            Function<ComponentId, Object> retrieveFunction) {
        
        // Validate that the component exists
        validateComponentExists(machine.getId(), componentId, existsFunction);
        
        // Validate that the component is a composite
        Object component = retrieveFunction.apply(componentId);
        validateIsCompositeComponent(machine.getId(), component);
    }
}