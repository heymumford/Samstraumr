/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Core domain implementation of Component in the S8r framework
 */

package org.samstraumr.domain.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.samstraumr.domain.identity.ComponentId;
import org.samstraumr.domain.lifecycle.LifecycleState;

/**
 * Core domain entity representing a Component in the S8r framework.
 *
 * <p>This class is a pure domain entity with no dependencies on infrastructure
 * or frameworks, following Clean Architecture principles.
 */
public class Component {
    private final ComponentId id;
    private LifecycleState lifecycleState;
    private final List<String> lineage;
    private final List<String> activityLog;
    private final Instant creationTime;
    
    private Component(ComponentId id) {
        this.id = Objects.requireNonNull(id, "Component ID cannot be null");
        this.lifecycleState = LifecycleState.CONCEPTION;
        this.lineage = new ArrayList<>();
        this.lineage.add(id.getReason());
        this.activityLog = new ArrayList<>();
        this.creationTime = Instant.now();
        
        logActivity("Component created with reason: " + id.getReason());
    }
    
    /**
     * Creates a new Component.
     *
     * @param id The component identity
     * @return A new Component
     */
    public static Component create(ComponentId id) {
        Component component = new Component(id);
        component.initialize();
        return component;
    }
    
    /**
     * Initialize the component, progressing through early lifecycle states.
     */
    private void initialize() {
        // Proceed through initial lifecycle states
        logActivity("Beginning initialization");
        
        // Initializing state
        transitionTo(LifecycleState.INITIALIZING);
        
        // Configuring state - establishing boundaries
        transitionTo(LifecycleState.CONFIGURING);
        
        // Specializing state - determining core functions
        transitionTo(LifecycleState.SPECIALIZING);
        
        // Developing features state
        transitionTo(LifecycleState.DEVELOPING_FEATURES);
        
        // Ready state
        transitionTo(LifecycleState.READY);
        
        logActivity("Initialization complete");
    }
    
    /**
     * Transitions the component to a new state.
     *
     * @param newState The new lifecycle state
     * @throws IllegalStateException if the transition is not valid
     */
    public void transitionTo(LifecycleState newState) {
        if (!isValidTransition(lifecycleState, newState)) {
            throw new IllegalStateException(
                "Invalid state transition from " + lifecycleState + " to " + newState);
        }
        
        LifecycleState oldState = lifecycleState;
        lifecycleState = newState;
        logActivity("State transition: " + oldState + " -> " + newState);
    }
    
    /**
     * Checks if a transition from one state to another is valid.
     *
     * @param from Current state
     * @param to Target state
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidTransition(LifecycleState from, LifecycleState to) {
        // This could be a more complex state machine implementation
        if (from == to) {
            return true; // Allow self-transitions
        }
        
        // Simplified validation - following the natural lifecycle progression
        switch (from) {
            case CONCEPTION:
                return to == LifecycleState.INITIALIZING;
            case INITIALIZING:
                return to == LifecycleState.CONFIGURING;
            case CONFIGURING:
                return to == LifecycleState.SPECIALIZING;
            case SPECIALIZING:
                return to == LifecycleState.DEVELOPING_FEATURES;
            case DEVELOPING_FEATURES:
                return to == LifecycleState.READY;
            case READY:
                return to == LifecycleState.ACTIVE || to == LifecycleState.TERMINATING;
            case ACTIVE:
                return to == LifecycleState.READY || to == LifecycleState.TERMINATING;
            case TERMINATING:
                return to == LifecycleState.TERMINATED;
            case TERMINATED:
                return false; // Cannot transition from terminated
            default:
                return false;
        }
    }
    
    /**
     * Activates the component.
     *
     * @throws IllegalStateException if the component cannot be activated
     */
    public void activate() {
        if (lifecycleState != LifecycleState.READY) {
            throw new IllegalStateException(
                "Cannot activate component in state: " + lifecycleState);
        }
        
        transitionTo(LifecycleState.ACTIVE);
        logActivity("Component activated");
    }
    
    /**
     * Deactivates the component, returning it to READY state.
     *
     * @throws IllegalStateException if the component cannot be deactivated
     */
    public void deactivate() {
        if (lifecycleState != LifecycleState.ACTIVE) {
            throw new IllegalStateException(
                "Cannot deactivate component in state: " + lifecycleState);
        }
        
        transitionTo(LifecycleState.READY);
        logActivity("Component deactivated");
    }
    
    /**
     * Terminates the component, releasing resources and preventing further use.
     */
    public void terminate() {
        if (lifecycleState == LifecycleState.TERMINATED) {
            return; // Already terminated
        }
        
        // Transition to terminating state
        transitionTo(LifecycleState.TERMINATING);
        logActivity("Beginning termination process");
        
        // Preserve knowledge
        preserveKnowledge();
        
        // Release resources
        releaseResources();
        
        // Final transition to terminated state
        transitionTo(LifecycleState.TERMINATED);
        logActivity("Component terminated at: " + Instant.now());
    }
    
    /**
     * Preserves knowledge before termination.
     */
    private void preserveKnowledge() {
        logActivity("Preserving knowledge before termination");
        // This would contain domain logic for knowledge preservation
    }
    
    /**
     * Releases resources during termination.
     */
    private void releaseResources() {
        logActivity("Releasing allocated resources");
        // This would contain domain logic for resource cleanup
    }
    
    /**
     * Adds an entry to the component's lineage.
     *
     * @param entry The lineage entry to add
     */
    public void addToLineage(String entry) {
        if (entry != null && !entry.isEmpty()) {
            lineage.add(entry);
            logActivity("Added to lineage: " + entry);
        }
    }
    
    /**
     * Logs component activity.
     *
     * @param activity The activity to log
     */
    private void logActivity(String activity) {
        String logEntry = Instant.now() + ": " + activity;
        activityLog.add(logEntry);
    }
    
    /**
     * Gets the component's ID.
     *
     * @return The component ID
     */
    public ComponentId getId() {
        return id;
    }
    
    /**
     * Gets the component's current lifecycle state.
     *
     * @return The current lifecycle state
     */
    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }
    
    /**
     * Gets the component's lineage (history of reasons).
     *
     * @return An unmodifiable list of lineage entries
     */
    public List<String> getLineage() {
        return Collections.unmodifiableList(lineage);
    }
    
    /**
     * Gets the component's activity log.
     *
     * @return An unmodifiable list of activity log entries
     */
    public List<String> getActivityLog() {
        return Collections.unmodifiableList(activityLog);
    }
    
    /**
     * Gets the component's creation time.
     *
     * @return The creation time
     */
    public Instant getCreationTime() {
        return creationTime;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", lifecycleState=" + lifecycleState +
                ", creationTime=" + creationTime +
                '}';
    }
}