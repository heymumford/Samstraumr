/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain implementation of LifecycleState in the S8r framework
 */

package org.samstraumr.domain.lifecycle;

/**
 * Represents the different lifecycle states of a Component based on the biological development model.
 *
 * <p>The states are organized into four major categories:
 *
 * <ul>
 *   <li>Creation &amp; Early Development (Embryonic analog)
 *   <li>Operational (Post-Embryonic analog)
 *   <li>Advanced Stages (Maturity &amp; Aging analog)
 *   <li>Termination (Death analog)
 * </ul>
 *
 * <p>This is a pure domain entity with no dependencies on frameworks or infrastructure.
 */
public enum LifecycleState {
    // Creation & Early Development (Embryonic)
    CONCEPTION("Initial creation", "Fertilization/Zygote"),
    INITIALIZING("Early structure formation", "Cleavage"),
    CONFIGURING("Establishing boundaries", "Blastulation"),
    SPECIALIZING("Determining core functions", "Gastrulation"),
    DEVELOPING_FEATURES("Building specific capabilities", "Organogenesis"),

    // Operational (Post-Embryonic)
    READY("Prepared but not active", "Juvenile"),
    ACTIVE("Fully operational", "Active Growth"),
    WAITING("Temporarily inactive but responsive", "Dormancy"),
    ADAPTING("Adjusting to environmental changes", "Environmental Adaptation"),
    TRANSFORMING("Undergoing major changes", "Metamorphosis"),

    // Advanced Stages
    STABLE("Optimal performance", "Maturity"),
    SPAWNING("Creating child components", "Reproduction"),
    DEGRADED("Experiencing performance issues", "Senescence"),
    MAINTAINING("Undergoing repair operations", "Healing"),

    // Termination
    TERMINATING("Shutting down", "Death"),
    TERMINATED("Completed shutdown", "Deceased"),
    ARCHIVED("Knowledge preserved after termination", "Legacy");

    private final String description;
    private final String biologicalAnalog;

    /**
     * Constructs a LifecycleState with a description and biological analog.
     *
     * @param description A short description of the state
     * @param biologicalAnalog The corresponding biological developmental stage
     */
    LifecycleState(String description, String biologicalAnalog) {
        this.description = description;
        this.biologicalAnalog = biologicalAnalog;
    }

    /**
     * Gets the description of this lifecycle state.
     *
     * @return The state description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the biological analog for this lifecycle state.
     *
     * @return The biological analog description
     */
    public String getBiologicalAnalog() {
        return biologicalAnalog;
    }

    /**
     * Checks if this state is a creation or early development state.
     *
     * @return true if this is an early stage, false otherwise
     */
    public boolean isEarlyStage() {
        return this == CONCEPTION 
            || this == INITIALIZING 
            || this == CONFIGURING 
            || this == SPECIALIZING 
            || this == DEVELOPING_FEATURES;
    }

    /**
     * Checks if this state is an operational state.
     *
     * @return true if this is an operational state, false otherwise
     */
    public boolean isOperational() {
        return this == READY 
            || this == ACTIVE 
            || this == WAITING 
            || this == ADAPTING 
            || this == TRANSFORMING;
    }

    /**
     * Checks if this state is an advanced stage.
     *
     * @return true if this is an advanced stage, false otherwise
     */
    public boolean isAdvancedStage() {
        return this == STABLE 
            || this == SPAWNING 
            || this == DEGRADED 
            || this == MAINTAINING;
    }

    /**
     * Checks if this state is a termination state.
     *
     * @return true if this is a termination state, false otherwise
     */
    public boolean isTerminationStage() {
        return this == TERMINATING 
            || this == TERMINATED 
            || this == ARCHIVED;
    }

    /**
     * Returns a string representation of this lifecycle state.
     *
     * @return A string containing the state name, description, and biological analog
     */
    @Override
    public String toString() {
        return name() + " (" + description + ", Analog: " + biologicalAnalog + ")";
    }
}