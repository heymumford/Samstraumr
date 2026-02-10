/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.component.core;

/**
 * Consolidated state enum that combines both operational status and lifecycle states.
 *
 * <p>This enum is part of the simplified package structure, replacing both the Status and
 * LifecycleState enums with a unified concept. States are categorized into:
 *
 * <ul>
 *   <li>OPERATIONAL - States representing current runtime status
 *   <li>LIFECYCLE - States representing developmental phases
 *   <li>ADVANCED - States representing maturity and specialized behavior
 *   <li>TERMINATION - States representing end-of-life
 * </ul>
 */
public enum State {
  // Operational states
  INITIALIZING("Early structure formation", Category.OPERATIONAL),
  READY("Prepared but not active", Category.OPERATIONAL),
  ACTIVE("Fully operational", Category.OPERATIONAL),
  WAITING("Temporarily inactive but responsive", Category.OPERATIONAL),
  RECEIVING_INPUT("Processing incoming data", Category.OPERATIONAL),
  PROCESSING_INPUT("Performing operations on data", Category.OPERATIONAL),
  OUTPUTTING_RESULT("Generating output data", Category.OPERATIONAL),
  ERROR("Encountered an error during operation", Category.OPERATIONAL),
  RECOVERING("Attempting to recover from error", Category.OPERATIONAL),
  PAUSED("Temporarily stopped but resumable", Category.OPERATIONAL),
  DORMANT("Inactive but available to reactivate", Category.OPERATIONAL),

  // Lifecycle states
  CONCEPTION("Initial creation", Category.LIFECYCLE),
  CONFIGURING("Establishing boundaries", Category.LIFECYCLE),
  SPECIALIZING("Determining core functions", Category.LIFECYCLE),
  DEVELOPING_FEATURES("Building specific capabilities", Category.LIFECYCLE),
  ADAPTING("Adjusting to environmental changes", Category.LIFECYCLE),
  TRANSFORMING("Undergoing major changes", Category.LIFECYCLE),

  // Advanced states
  STABLE("Optimal performance", Category.ADVANCED),
  SPAWNING("Creating child components", Category.ADVANCED),
  DEGRADED("Experiencing performance issues", Category.ADVANCED),
  MAINTAINING("Undergoing repair operations", Category.ADVANCED),

  // Termination states
  DEACTIVATING("Preparing to shut down", Category.TERMINATION),
  TERMINATING("Shutting down", Category.TERMINATION),
  TERMINATED("Completed shutdown", Category.TERMINATION),
  ARCHIVED("Knowledge preserved after termination", Category.TERMINATION);

  private final String description;
  private final Category category;
  private String biologicalAnalog; // Optional biological analog for lifecycle states

  /** Categorization of different state types. */
  public enum Category {
    /** States representing current runtime status */
    OPERATIONAL,

    /** States representing developmental phases */
    LIFECYCLE,

    /** States representing maturity and specialized behavior */
    ADVANCED,

    /** States representing end-of-life */
    TERMINATION
  }

  /**
   * Constructs a State with description and category.
   *
   * @param description The description of this state
   * @param category The category this state belongs to
   */
  State(String description, Category category) {
    this.description = description;
    this.category = category;
  }

  /**
   * Sets a biological analog for this state (used primarily for lifecycle states).
   *
   * @param biologicalAnalog The biological analog
   * @return This state, for method chaining
   */
  State withBiologicalAnalog(String biologicalAnalog) {
    this.biologicalAnalog = biologicalAnalog;
    return this;
  }

  /**
   * Gets the description of this state.
   *
   * @return The state description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the category of this state.
   *
   * @return The state category
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Gets the biological analog for this state, if any.
   *
   * @return The biological analog or null if not set
   */
  public String getBiologicalAnalog() {
    return biologicalAnalog;
  }

  /**
   * Checks if this state is operational.
   *
   * @return true if this is an operational state
   */
  public boolean isOperational() {
    return category == Category.OPERATIONAL;
  }

  /**
   * Checks if this state is a lifecycle state.
   *
   * @return true if this is a lifecycle state
   */
  public boolean isLifecycle() {
    return category == Category.LIFECYCLE;
  }

  /**
   * Checks if this state is an advanced state.
   *
   * @return true if this is an advanced state
   */
  public boolean isAdvanced() {
    return category == Category.ADVANCED;
  }

  /**
   * Checks if this state is a termination state.
   *
   * @return true if this is a termination state
   */
  public boolean isTermination() {
    return category == Category.TERMINATION;
  }

  /**
   * Returns a string representation of this state.
   *
   * @return A string containing the state name, description, and category
   */
  @Override
  public String toString() {
    StringBuilder sb =
        new StringBuilder(name())
            .append(" (")
            .append(description)
            .append(", Category: ")
            .append(category);

    if (biologicalAnalog != null) {
      sb.append(", Analog: ").append(biologicalAnalog);
    }

    return sb.append(")").toString();
  }

  // Initialize biological analogs for lifecycle states using static initializer
  static {
    CONCEPTION.withBiologicalAnalog("Fertilization/Zygote");
    INITIALIZING.withBiologicalAnalog("Cleavage");
    CONFIGURING.withBiologicalAnalog("Blastulation");
    SPECIALIZING.withBiologicalAnalog("Gastrulation");
    DEVELOPING_FEATURES.withBiologicalAnalog("Organogenesis");
    ADAPTING.withBiologicalAnalog("Environmental Adaptation");
    TRANSFORMING.withBiologicalAnalog("Metamorphosis");
    STABLE.withBiologicalAnalog("Maturity");
    SPAWNING.withBiologicalAnalog("Reproduction");
    DEGRADED.withBiologicalAnalog("Senescence");
    MAINTAINING.withBiologicalAnalog("Healing");
    TERMINATING.withBiologicalAnalog("Death");
    TERMINATED.withBiologicalAnalog("Deceased");
    ARCHIVED.withBiologicalAnalog("Legacy");
  }
}
