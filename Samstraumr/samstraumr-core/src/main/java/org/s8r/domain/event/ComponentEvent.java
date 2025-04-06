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

package org.s8r.domain.event;

import org.s8r.domain.identity.ComponentId;

/**
 * Base class for all component-related events in the system.
 * <p>
 * This abstract class serves as the foundation for all events that relate to components.
 * It provides common attributes and behaviors for component events.
 * </p>
 */
public abstract class ComponentEvent extends DomainEvent {
    private final ComponentId componentId;

    /**
     * Creates a new component event.
     *
     * @param componentId The identifier of the component this event relates to
     */
    protected ComponentEvent(ComponentId componentId) {
        this.componentId = componentId;
    }

    /**
     * Gets the identifier of the component this event relates to.
     *
     * @return The component identifier
     */
    public ComponentId getComponentId() {
        return componentId;
    }
}