/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.infrastructure.security;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.security.SecurityPort;

/**
 * Bridge between security port and event publisher port.
 * This component publishes security events to the event system.
 */
public class SecurityEventBridge {
    private final SecurityPort securityPort;
    private final EventPublisherPort eventPublisher;
    
    private SecurityEventBridge(SecurityPort securityPort, EventPublisherPort eventPublisher) {
        this.securityPort = securityPort;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Creates a new instance of SecurityEventBridge.
     *
     * @param securityPort The security port.
     * @param eventPublisher The event publisher port.
     * @return A new SecurityEventBridge instance.
     */
    public static SecurityEventBridge create(SecurityPort securityPort, EventPublisherPort eventPublisher) {
        return new SecurityEventBridge(securityPort, eventPublisher);
    }
}
