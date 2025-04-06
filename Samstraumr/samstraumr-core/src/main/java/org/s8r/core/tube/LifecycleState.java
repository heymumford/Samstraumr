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

package org.s8r.core.tube;

/**
 * Represents the different lifecycle states of a component based on the biological development
 * model.
 *
 * <p>This enum is part of the simplified package structure, replacing the more specific
 * TubeLifecycleState with a more general LifecycleState enum in the s8r.core.tube package.
 *
 * <p>The states are organized into four major categories:
 *
 * <ul>
 *   <li>Creation & Early Development (Embryonic analog)
 *   <li>Operational (Post-Embryonic analog)
 *   <li>Advanced Stages (Maturity & Aging analog)
 *   <li>Termination (Death analog)
 * </ul>
 *
 * <p>This enum extends the basic Status by providing more granular lifecycle states that map to
 * biological development stages, enabling a more nuanced model of component evolution.
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
   * Returns a string representation of this lifecycle state.
   *
   * @return A string containing the state name, description, and biological analog
   */
  @Override
  public String toString() {
    return name() + " (" + description + ", Analog: " + biologicalAnalog + ")";
  }
}
