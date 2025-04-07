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

package org.s8r.adapter;

import java.util.Map;

import org.s8r.domain.component.Component;
import org.s8r.domain.exception.ComponentException;
import org.s8r.domain.exception.InvalidStateTransitionException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.lifecycle.LifecycleState;

/**
 * A wrapper that implements the Clean Architecture Component interface by delegating to a legacy component.
 * <p>
 * This wrapper maintains the interface contract of the new Component type while using an underlying
 * legacy component for actual implementation. This supports a smooth transition path
 * for legacy code while maintaining clean architecture principles.
 * </p>
 */
public class LegacyComponentWrapper extends Component {
    
    private final Object legacyComponent;
    private final LegacyComponentAdapterPort adapter;
    private final String legacyType;
    
    /**
     * Creates a new legacy component wrapper.
     *
     * @param componentId The component ID
     * @param name The component name
     * @param type The component type
     * @param state The initial state
     * @param legacyComponent The legacy component to delegate to
     * @param adapter The adapter to use for interacting with the legacy component
     */
    LegacyComponentWrapper(
            ComponentId componentId,
            String name,
            String type,
            String state,
            Object legacyComponent,
            LegacyComponentAdapterPort adapter) {
        super(componentId);
        this.legacyComponent = legacyComponent;
        this.adapter = adapter;
        this.legacyType = type;
        
        logActivity("Legacy component wrapper created for " + type);
    }
    
    @Override
    public LifecycleState getLifecycleState() {
        // Synchronize with the legacy component's state before returning
        String legacyState = adapter.getLegacyComponentState(legacyComponent);
        
        // Convert the string state to a LifecycleState enum if possible
        try {
            return LifecycleState.valueOf(legacyState);
        } catch (IllegalArgumentException e) {
            // If the state doesn't match our enum, return the super implementation
            return super.getLifecycleState();
        }
    }
    
    @Override
    public void transitionTo(LifecycleState newState) {
        if (newState == null) {
            throw new InvalidStateTransitionException(getLifecycleState(), null);
        }
        
        // Update the legacy component state
        try {
            adapter.setLegacyComponentState(legacyComponent, newState.name());
            super.transitionTo(newState); // Let parent handle events and validation
        } catch (Exception e) {
            throw new InvalidStateTransitionException(getLifecycleState(), newState);
        }
    }
    
    /**
     * Initializes the underlying legacy component.
     *
     * @throws ComponentException if initialization fails
     */
    public void initializeLegacy() throws ComponentException {
        try {
            adapter.initializeLegacyComponent(legacyComponent);
            logActivity("Legacy component initialized");
        } catch (Exception e) {
            logActivity("Legacy component initialization failed: " + e.getMessage());
            throw new ComponentException("Failed to initialize legacy component: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String toString() {
        return "LegacyComponentWrapper{id=" + getId() + ", type=" + legacyType + 
               ", state=" + getLifecycleState() + "}";
    }
    
    /**
     * Gets the underlying legacy component.
     *
     * @return The legacy component object
     */
    public Object getLegacyComponent() {
        return legacyComponent;
    }
}