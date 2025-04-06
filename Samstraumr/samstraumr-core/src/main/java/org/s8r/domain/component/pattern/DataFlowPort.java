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

package org.s8r.domain.component.pattern;

import java.util.Map;
import java.util.function.Consumer;

import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Port that defines operations for data flow between components.
 * 
 * <p>This port allows pattern-based components to interact with the data flow system
 * without depending on application or infrastructure implementations.
 */
public interface DataFlowPort {

    /**
     * Subscribes a component to receive data on a specific channel.
     *
     * @param componentId The component to subscribe
     * @param channel The channel to subscribe to
     * @param handler The handler for processing received data
     */
    void subscribe(ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler);
    
    /**
     * Publishes data from a component to a channel.
     *
     * @param componentId The component publishing the data
     * @param channel The channel to publish to
     * @param data The data to publish
     */
    void publishData(ComponentId componentId, String channel, Map<String, Object> data);
}