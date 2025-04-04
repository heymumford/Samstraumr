/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer service for Component management
 */

package org.samstraumr.application.service;

import java.util.List;
import java.util.Optional;

import org.samstraumr.application.port.ComponentRepository;
import org.samstraumr.application.port.LoggerPort;
import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.identity.ComponentId;

/**
 * Application service for managing components.
 *
 * <p>This service orchestrates operations on components, following Clean Architecture principles
 * by depending only on domain entities and application layer ports (interfaces).
 */
public class ComponentService {
    private final ComponentRepository componentRepository;
    private final LoggerPort logger;
    
    /**
     * Creates a new ComponentService.
     *
     * @param componentRepository The component repository
     * @param logger The logger
     */
    public ComponentService(ComponentRepository componentRepository, LoggerPort logger) {
        this.componentRepository = componentRepository;
        this.logger = logger;
    }
    
    /**
     * Creates a new component.
     *
     * @param reason The reason for creating the component
     * @return The ID of the created component
     */
    public ComponentId createComponent(String reason) {
        logger.info("Creating new component with reason: " + reason);
        
        ComponentId id = ComponentId.create(reason);
        Component component = Component.create(id);
        
        componentRepository.save(component);
        logger.info("Component created successfully: " + id.getIdString());
        
        return id;
    }
    
    /**
     * Creates a child component.
     *
     * @param reason The reason for creating the component
     * @param parentId The parent component's ID
     * @return The ID of the created child component
     * @throws IllegalArgumentException if the parent component doesn't exist
     */
    public ComponentId createChildComponent(String reason, ComponentId parentId) {
        logger.info("Creating child component with reason: " + reason);
        
        // Find parent component
        Component parent = componentRepository.findById(parentId)
            .orElseThrow(() -> new IllegalArgumentException("Parent component not found: " + parentId.getIdString()));
        
        // Create child component
        ComponentId childId = ComponentId.create(reason);
        Component child = Component.create(childId);
        
        // Add parent's lineage to child
        parent.getLineage().forEach(child::addToLineage);
        
        // Save the child component
        componentRepository.save(child);
        logger.info("Child component created successfully: " + childId.getIdString());
        
        return childId;
    }
    
    /**
     * Activates a component.
     *
     * @param id The component ID
     * @throws IllegalArgumentException if the component doesn't exist
     * @throws IllegalStateException if the component cannot be activated
     */
    public void activateComponent(ComponentId id) {
        logger.info("Activating component: " + id.getIdString());
        
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id.getIdString()));
        
        component.activate();
        componentRepository.save(component);
        
        logger.info("Component activated successfully: " + id.getIdString());
    }
    
    /**
     * Deactivates a component.
     *
     * @param id The component ID
     * @throws IllegalArgumentException if the component doesn't exist
     * @throws IllegalStateException if the component cannot be deactivated
     */
    public void deactivateComponent(ComponentId id) {
        logger.info("Deactivating component: " + id.getIdString());
        
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id.getIdString()));
        
        component.deactivate();
        componentRepository.save(component);
        
        logger.info("Component deactivated successfully: " + id.getIdString());
    }
    
    /**
     * Terminates a component.
     *
     * @param id The component ID
     * @throws IllegalArgumentException if the component doesn't exist
     */
    public void terminateComponent(ComponentId id) {
        logger.info("Terminating component: " + id.getIdString());
        
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id.getIdString()));
        
        component.terminate();
        componentRepository.save(component);
        
        logger.info("Component terminated successfully: " + id.getIdString());
    }
    
    /**
     * Gets a component by ID.
     *
     * @param id The component ID
     * @return An Optional containing the component if found
     */
    public Optional<Component> getComponent(ComponentId id) {
        logger.debug("Getting component: " + id.getIdString());
        return componentRepository.findById(id);
    }
    
    /**
     * Gets all components.
     *
     * @return A list of all components
     */
    public List<Component> getAllComponents() {
        logger.debug("Getting all components");
        return componentRepository.findAll();
    }
    
    /**
     * Gets child components for a parent component.
     *
     * @param parentId The parent component ID
     * @return A list of child components
     */
    public List<Component> getChildComponents(ComponentId parentId) {
        logger.debug("Getting child components for parent: " + parentId.getIdString());
        return componentRepository.findChildren(parentId);
    }
    
    /**
     * Adds an entry to a component's lineage.
     *
     * @param id The component ID
     * @param entry The lineage entry to add
     * @throws IllegalArgumentException if the component doesn't exist
     */
    public void addToLineage(ComponentId id, String entry) {
        logger.debug("Adding lineage entry to component: " + id.getIdString());
        
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id.getIdString()));
        
        component.addToLineage(entry);
        componentRepository.save(component);
        
        logger.debug("Lineage entry added successfully");
    }
}