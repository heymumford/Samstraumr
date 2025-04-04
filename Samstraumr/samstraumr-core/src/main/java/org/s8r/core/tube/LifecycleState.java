/*
 * Implementation of component lifecycle states based on biological development model
 *
 * This enum defines the various lifecycle states components can transition through,
 * using a biological development analogy to model the component's evolution from
 * creation to termination.
 */

package org.s8r.core.tube;

/**
 * Represents the different lifecycle states of a component based on the biological development model.
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