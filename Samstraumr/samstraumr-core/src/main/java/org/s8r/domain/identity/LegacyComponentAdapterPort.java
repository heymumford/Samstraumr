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

package org.s8r.domain.identity;

import org.s8r.domain.component.Component;

/**
 * Port interface for adapting legacy components to Clean Architecture components.
 * <p>
 * This interface defines operations for working with legacy components without
 * direct dependencies on their implementation, maintaining clean architecture
 * principles by keeping the domain layer free of adapter dependencies.
 * </p>
 */
public interface LegacyComponentAdapterPort {
    
    /**
     * Wraps a legacy component in a Clean Architecture Component.
     *
     * @param legacyComponent The legacy component to wrap
     * @return A Clean Architecture component that delegates to the legacy component
     */
    Component wrapLegacyComponent(Object legacyComponent);
    
    /**
     * Creates a legacy component with the specified parameters.
     *
     * @param name The component name
     * @param type The component type
     * @param reason The reason for creation
     * @return A wrapped legacy component
     */
    Component createLegacyComponent(String name, String type, String reason);
    
    /**
     * Sets the state of a legacy component.
     *
     * @param legacyComponent The legacy component
     * @param state The new state value
     */
    void setLegacyComponentState(Object legacyComponent, String state);
    
    /**
     * Gets the state of a legacy component.
     *
     * @param legacyComponent The legacy component
     * @return The component state
     */
    String getLegacyComponentState(Object legacyComponent);
    
    /**
     * Initializes a legacy component.
     *
     * @param legacyComponent The legacy component to initialize
     */
    void initializeLegacyComponent(Object legacyComponent);
}