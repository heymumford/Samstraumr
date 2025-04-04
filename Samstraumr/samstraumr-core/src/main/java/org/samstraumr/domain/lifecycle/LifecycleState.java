/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain implementation of LifecycleState in the S8r framework
 */

package org.samstraumr.domain.lifecycle;

/** 
 * Represents component lifecycle states based on biological development model.
 * Categories: Creation (Embryonic), Operational (Post-Embryonic),
 * Advanced (Maturity), and Termination.
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

  /** Constructs a LifecycleState with a description and biological analog. */
  LifecycleState(String description, String biologicalAnalog) {
    this.description = description;
    this.biologicalAnalog = biologicalAnalog;
  }

  /** Gets the description of this lifecycle state. */
  public String getDescription() { return description; }

  /** Gets the biological analog for this lifecycle state. */
  public String getBiologicalAnalog() { return biologicalAnalog; }

  /** Checks if this state is a creation or early development state. */
  public boolean isEarlyStage() {
    return this == CONCEPTION || this == INITIALIZING || this == CONFIGURING || 
           this == SPECIALIZING || this == DEVELOPING_FEATURES;
  }

  /** Checks if this state is an operational state. */
  public boolean isOperational() {
    return this == READY || this == ACTIVE || this == WAITING || 
           this == ADAPTING || this == TRANSFORMING;
  }

  /** Checks if this state is an advanced stage. */
  public boolean isAdvancedStage() {
    return this == STABLE || this == SPAWNING || this == DEGRADED || this == MAINTAINING;
  }

  /** Checks if this state is a termination state. */
  public boolean isTerminationStage() {
    return this == TERMINATING || this == TERMINATED || this == ARCHIVED;
  }

  @Override
  public String toString() {
    return name() + " (" + description + ", Analog: " + biologicalAnalog + ")";
  }
}
