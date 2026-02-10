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
package org.s8r.domain.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents the environment in which a tube exists, including various environmental factors that
 * influence tube behavior and decisions.
 */
public class TubeEnvironment {

  private static final Logger LOGGER = Logger.getLogger(TubeEnvironment.class.getName());

  private final Map<String, EnvironmentalFactor> factors;

  /** Creates a new tube environment with empty factors */
  public TubeEnvironment() {
    this.factors = new HashMap<>();
  }

  /**
   * Adds an environmental factor to the environment
   *
   * @param factor The factor to add
   */
  public void addFactor(EnvironmentalFactor factor) {
    factors.put(factor.getName(), factor);
    LOGGER.fine(
        "Added environmental factor: "
            + factor.getName()
            + " with value: "
            + factor.getValue()
            + " and classification: "
            + factor.getClassification());
  }

  /**
   * Updates an existing environmental factor
   *
   * @param factor The updated factor
   */
  public void updateFactor(EnvironmentalFactor factor) {
    if (factors.containsKey(factor.getName())) {
      factors.put(factor.getName(), factor);
      LOGGER.fine(
          "Updated environmental factor: "
              + factor.getName()
              + " with value: "
              + factor.getValue()
              + " and classification: "
              + factor.getClassification());
    } else {
      addFactor(factor);
    }
  }

  /**
   * Gets an environmental factor by name
   *
   * @param name The name of the factor
   * @return The environmental factor, or null if not found
   */
  public EnvironmentalFactor getFactor(String name) {
    return factors.get(name);
  }

  /**
   * Gets all environmental factors
   *
   * @return Map of all environmental factors
   */
  public Map<String, EnvironmentalFactor> getAllFactors() {
    return new HashMap<>(factors);
  }

  /**
   * Calculates the overall favorability of the environment for reproduction
   *
   * @return A value between 0.0 (completely unfavorable) and 1.0 (completely favorable)
   */
  public double calculateFavorability() {
    if (factors.isEmpty()) {
      return 0.5; // Neutral if no factors are present
    }

    int favorableCount = 0;
    for (EnvironmentalFactor factor : factors.values()) {
      if (factor.isFavorable()) {
        favorableCount++;
      }
    }

    return (double) favorableCount / factors.size();
  }

  /**
   * Determines if the environment is overall favorable for reproduction
   *
   * @return true if the environment is favorable, false otherwise
   */
  public boolean isFavorableForReproduction() {
    return calculateFavorability() >= 0.6; // 60% or more factors are favorable
  }

  /**
   * Gets a summary of the environmental conditions
   *
   * @return A string summarizing the environmental conditions
   */
  public String getSummary() {
    StringBuilder summary = new StringBuilder("Environment Summary [");
    for (EnvironmentalFactor factor : factors.values()) {
      summary
          .append(factor.getName())
          .append("=")
          .append(factor.getValue())
          .append("(")
          .append(factor.getClassification())
          .append("), ");
    }

    if (!factors.isEmpty()) {
      summary.delete(summary.length() - 2, summary.length()); // Remove last comma and space
    }

    summary
        .append("] - Overall Favorability: ")
        .append(String.format("%.2f", calculateFavorability() * 100))
        .append("%");

    return summary.toString();
  }
}
