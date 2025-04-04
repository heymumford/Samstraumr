/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer port for Component repository
 */

package org.samstraumr.application.port;

import java.util.List;
import java.util.Optional;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.identity.ComponentId;

/**
 * Port interface for component persistence operations.
 *
 * <p>This interface defines the contract for storing and retrieving Component entities.
 * Following the ports and adapters pattern, this is an output port in the application layer,
 * which will be implemented by adapters in the infrastructure layer.
 */
public interface ComponentRepository {
    
    /**
     * Saves a component.
     *
     * @param component The component to save
     */
    void save(Component component);
    
    /**
     * Finds a component by its ID.
     *
     * @param id The component ID to find
     * @return An Optional containing the component if found, or empty if not found
     */
    Optional<Component> findById(ComponentId id);
    
    /**
     * Finds all components.
     *
     * @return A list of all components
     */
    List<Component> findAll();
    
    /**
     * Finds child components for a parent component.
     *
     * @param parentId The parent component ID
     * @return A list of child components
     */
    List<Component> findChildren(ComponentId parentId);
    
    /**
     * Deletes a component.
     *
     * @param id The ID of the component to delete
     */
    void delete(ComponentId id);
}