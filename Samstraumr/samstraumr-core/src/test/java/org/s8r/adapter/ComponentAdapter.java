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

/**
 * Adapter class to convert between org.s8r.component.Component and org.s8r.domain.component.Component.
 * 
 * <p>This is a temporary adapter to help with the transition during package flattening.
 * It provides methods to convert between the two component types, enabling tests to pass
 * while the codebase is being restructured.
 */
public class ComponentAdapter {

    /**
     * Converts a org.s8r.domain.component.Component to org.s8r.component.Component.
     * 
     * @param domainComponent The domain component to convert
     * @return A component instance from the flattened package
     */
    public static org.s8r.component.Component fromDomainComponent(org.s8r.domain.component.Component domainComponent) {
        if (domainComponent == null) {
            return null;
        }
        
        // Create a new component with the same ID/reason as the domain component
        org.s8r.component.Environment env = new org.s8r.component.Environment();
        return org.s8r.component.Component.create(
            domainComponent.getId().getReason(), 
            env
        );
    }
    
    /**
     * Converts a org.s8r.component.Component to org.s8r.domain.component.Component.
     * 
     * @param component The component to convert
     * @return A domain component instance
     */
    public static org.s8r.domain.component.Component toDomainComponent(org.s8r.component.Component component) {
        if (component == null) {
            return null;
        }
        
        // Create a domain component with the component's ID
        return org.s8r.domain.component.Component.create(
            org.s8r.domain.identity.ComponentId.create(component.getReason())
        );
    }
}