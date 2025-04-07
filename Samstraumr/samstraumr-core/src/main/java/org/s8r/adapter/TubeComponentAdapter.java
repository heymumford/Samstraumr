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

import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyComponentAdapterPort;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.s8r.tube.TubeLifecycleState;
import org.s8r.tube.TubeStatus;

/**
 * A concrete implementation of LegacyComponentAdapterPort specifically for Tube components.
 * This adapter provides a direct conversion between Tube objects and Components without using reflection.
 * <p>
 * This is more efficient than the reflection-based approach when we know we're working 
 * with Tube objects specifically, and serves as the primary migration path for existing code.
 */
public class TubeComponentAdapter implements LegacyComponentAdapterPort {
    
    private final LoggerPort logger;
    private final TubeLegacyIdentityConverter identityConverter;
    private final TubeLegacyEnvironmentConverter environmentConverter;
    
    /**
     * Creates a new TubeComponentAdapter.
     *
     * @param logger Logger for recording operations
     * @param identityConverter Converter for identity objects
     * @param environmentConverter Converter for environment objects
     */
    public TubeComponentAdapter(
            LoggerPort logger,
            TubeLegacyIdentityConverter identityConverter,
            TubeLegacyEnvironmentConverter environmentConverter) {
        this.logger = logger;
        this.identityConverter = identityConverter;
        this.environmentConverter = environmentConverter;
        logger.debug("TubeComponentAdapter initialized");
    }
    
    @Override
    public Component wrapLegacyComponent(Object legacyComponent) {
        if (!(legacyComponent instanceof Tube)) {
            throw new IllegalArgumentException("Expected Tube object, got: " + 
                    (legacyComponent != null ? legacyComponent.getClass().getName() : "null"));
        }
        
        Tube tube = (Tube) legacyComponent;
        TubeIdentity tubeIdentity = tube.getIdentity();
        
        // Create component ID from tube identity
        ComponentId componentId = identityConverter.toComponentId(
                tubeIdentity.getUniqueId(), 
                tubeIdentity.getReason(), 
                tubeIdentity.getLineage());
        
        // Map the tube state to component lifecycle state
        String tubeStateString = getTubeStateString(tube);
        
        // Create wrapper component
        TubeComponentWrapper wrapper = new TubeComponentWrapper(
                componentId, 
                tube, 
                tubeStateString,
                this);
        
        logger.debug("Wrapped Tube in Component: {}", componentId.getIdString());
        return wrapper;
    }
    
    @Override
    public Component createLegacyComponent(String name, String type, String reason) {
        logger.debug("Creating new Tube component: name={}, type={}, reason={}", name, type, reason);
        
        // Create environment
        Environment environment = new Environment();
        environment.setParameter("name", name);
        environment.setParameter("type", type);
        
        // Create tube
        Tube tube = Tube.create(reason, environment);
        
        // Wrap and return
        return wrapLegacyComponent(tube);
    }
    
    @Override
    public void setLegacyComponentState(Object legacyComponent, String state) {
        if (!(legacyComponent instanceof Tube)) {
            throw new IllegalArgumentException("Expected Tube object, got: " + 
                    (legacyComponent != null ? legacyComponent.getClass().getName() : "null"));
        }
        
        Tube tube = (Tube) legacyComponent;
        
        // Map the component state to tube status and lifecycle state
        try {
            LifecycleState lifecycleState = LifecycleState.valueOf(state);
            TubeStatus tubeStatus = mapLifecycleStateToTubeStatus(lifecycleState);
            TubeLifecycleState tubeLifecycleState = mapLifecycleStateToTubeLifecycleState(lifecycleState);
            
            // Set both states on the tube
            tube.setStatus(tubeStatus);
            
            logger.debug("Set Tube state: status={}, lifecycleState={}", tubeStatus, tubeLifecycleState);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid lifecycle state: {}", state);
            throw new IllegalArgumentException("Invalid lifecycle state: " + state, e);
        }
    }
    
    @Override
    public String getLegacyComponentState(Object legacyComponent) {
        if (!(legacyComponent instanceof Tube)) {
            throw new IllegalArgumentException("Expected Tube object, got: " + 
                    (legacyComponent != null ? legacyComponent.getClass().getName() : "null"));
        }
        
        Tube tube = (Tube) legacyComponent;
        return getTubeStateString(tube);
    }
    
    @Override
    public void initializeLegacyComponent(Object legacyComponent) {
        if (!(legacyComponent instanceof Tube)) {
            throw new IllegalArgumentException("Expected Tube object, got: " + 
                    (legacyComponent != null ? legacyComponent.getClass().getName() : "null"));
        }
        
        // Tubes are already initialized during creation, so this is a no-op
        logger.debug("No explicit initialization needed for Tube components");
    }
    
    /**
     * Creates a new Tube as a child of another Tube.
     *
     * @param reason The reason for creating the child
     * @param parentTube The parent tube
     * @return A new Component wrapping the child tube
     */
    public Component createChildComponent(String reason, Tube parentTube) {
        logger.debug("Creating child Tube with reason: {}", reason);
        
        Environment environment = parentTube.getEnvironment();
        Tube childTube = Tube.createChildTube(reason, environment, parentTube);
        
        return wrapLegacyComponent(childTube);
    }
    
    /**
     * Gets the state string for a tube, considering both its status and lifecycle state.
     *
     * @param tube The tube
     * @return A string representation of the tube's state, compatible with the Component lifecycle state
     */
    private String getTubeStateString(Tube tube) {
        TubeStatus status = tube.getStatus();
        
        // Map tube status to component lifecycle state
        if (status == TubeStatus.INITIALIZING) {
            return LifecycleState.INITIALIZING.name();
        } else if (status == TubeStatus.READY) {
            return LifecycleState.READY.name();
        } else if (status == TubeStatus.ACTIVE) {
            return LifecycleState.ACTIVE.name();
        } else if (status == TubeStatus.DEACTIVATING) {
            return LifecycleState.TERMINATING.name();
        } else if (status == TubeStatus.TERMINATED) {
            return LifecycleState.TERMINATED.name();
        } else if (status == TubeStatus.ERROR || status == TubeStatus.RECOVERING) {
            return LifecycleState.DEGRADED.name();
        } else {
            logger.warn("Unknown tube status: {}, defaulting to READY", status);
            return LifecycleState.READY.name();
        }
    }
    
    /**
     * Maps a LifecycleState to a TubeStatus.
     *
     * @param state The lifecycle state
     * @return The corresponding TubeStatus
     */
    private TubeStatus mapLifecycleStateToTubeStatus(LifecycleState state) {
        if (state == LifecycleState.CONCEPTION) {
            return TubeStatus.INITIALIZING;  // No direct equivalent, use INITIALIZING
        } else if (state == LifecycleState.INITIALIZING 
                || state == LifecycleState.CONFIGURING
                || state == LifecycleState.SPECIALIZING
                || state == LifecycleState.DEVELOPING_FEATURES) {
            return TubeStatus.INITIALIZING;
        } else if (state == LifecycleState.READY) {
            return TubeStatus.READY;
        } else if (state == LifecycleState.ACTIVE) {
            return TubeStatus.ACTIVE;
        } else if (state == LifecycleState.DEGRADED) {
            return TubeStatus.ERROR;  // Close match to DEGRADED
        } else if (state == LifecycleState.TERMINATING) {
            return TubeStatus.DEACTIVATING;  // Equivalent to TERMINATING
        } else if (state == LifecycleState.TERMINATED) {
            return TubeStatus.TERMINATED;
        } else {
            logger.warn("Unknown lifecycle state: {}, defaulting to READY", state);
            return TubeStatus.READY;
        }
    }
    
    /**
     * Maps a LifecycleState to a TubeLifecycleState.
     *
     * @param state The lifecycle state
     * @return The corresponding TubeLifecycleState
     */
    private TubeLifecycleState mapLifecycleStateToTubeLifecycleState(LifecycleState state) {
        switch (state) {
            case CONCEPTION:
                return TubeLifecycleState.CONCEPTION;
            case INITIALIZING:
                return TubeLifecycleState.INITIALIZING;
            case CONFIGURING:
                return TubeLifecycleState.CONFIGURING;
            case SPECIALIZING:
                return TubeLifecycleState.SPECIALIZING;
            case DEVELOPING_FEATURES:
                return TubeLifecycleState.DEVELOPING_FEATURES;
            case READY:
            case ACTIVE:
            case DEGRADED:
                return TubeLifecycleState.READY;
            case TERMINATING:
                return TubeLifecycleState.TERMINATING;
            case TERMINATED:
                return TubeLifecycleState.TERMINATED;
            default:
                logger.warn("Unknown lifecycle state: {}, defaulting to READY", state);
                return TubeLifecycleState.READY;
        }
    }
    
    /**
     * Gets the TubeLegacyIdentityConverter used by this adapter.
     *
     * @return The identity converter
     */
    public TubeLegacyIdentityConverter getIdentityConverter() {
        return identityConverter;
    }
    
    /**
     * Gets the TubeLegacyEnvironmentConverter used by this adapter.
     *
     * @return The environment converter
     */
    public TubeLegacyEnvironmentConverter getEnvironmentConverter() {
        return environmentConverter;
    }
}