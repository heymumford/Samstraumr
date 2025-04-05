/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Implementation of the TubeLifecycleState concept in the Samstraumr framework
 *
 * This class implements the core functionality for TubeLifecycleState in the Samstraumr
 * tube-based processing framework. It provides the essential infrastructure for
 * the tube ecosystem to maintain its hierarchical design and data processing capabilities.
 *
 * Key features:
 * - Implementation of the TubeLifecycleState concept
 * - Integration with the tube substrate model
 * - Support for hierarchical tube organization
 */

package org.samstraumr.tube;

/**
 * Represents the different lifecycle states of a Tube based on the biological development model.
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
 * <p>This enum extends the basic TubeStatus by providing more granular lifecycle states that map to
 * biological development stages, enabling a more nuanced model of tube evolution.
 */
/**
 * Constructs a TubeLifecycleState with a description and biological analog.
 *
 * @param description A short description of the state
 * @param biologicalAnalog The corresponding biological developmental stage
 */
/**
 * Gets the description of this lifecycle state.
 *
 * @return The state description
 */
/**
 * Gets the biological analog for this lifecycle state.
 *
 * @return The biological analog description
 */
/**
 * Returns a string representation of this lifecycle state.
 *
 * @return A string containing the state name, description, and biological analog
 */
public enum TubeLifecycleState {
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
  SPAWNING("Creating child tubes", "Reproduction"),
  DEGRADED("Experiencing performance issues", "Senescence"),
  MAINTAINING("Undergoing repair operations", "Healing"),

  // Termination
  TERMINATING("Shutting down", "Death"),
  TERMINATED("Completed shutdown", "Deceased"),
  ARCHIVED("Knowledge preserved after termination", "Legacy");

  private final String description;
  private final String biologicalAnalog;

  /**
   * Constructs a TubeLifecycleState with a description and biological analog.
   *
   * @param description A short description of the state
   * @param biologicalAnalog The corresponding biological developmental stage
   */
  TubeLifecycleState(String description, String biologicalAnalog) {
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
