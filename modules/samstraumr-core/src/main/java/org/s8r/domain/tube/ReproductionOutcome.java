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
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.domain.tube;

/**
 * Represents the outcome of a tube reproduction attempt, including the environmental conditions,
 * strategy used, and success rate.
 */
public class ReproductionOutcome {
  private final int attempt;
  private final String environmentalConditions;
  private final String strategy;
  private final String outcome;
  private final double successRate;

  /**
   * Creates a new reproduction outcome
   *
   * @param attempt The attempt number
   * @param environmentalConditions Description of the environmental conditions
   * @param strategy The strategy used for reproduction
   * @param outcome The outcome ("successful", "partial", "failed")
   * @param successRate The rate of success (0.0 to 1.0)
   */
  public ReproductionOutcome(
      int attempt,
      String environmentalConditions,
      String strategy,
      String outcome,
      double successRate) {
    this.attempt = attempt;
    this.environmentalConditions = environmentalConditions;
    this.strategy = strategy;
    this.outcome = outcome;
    this.successRate = successRate;
  }

  /**
   * Gets the attempt number
   *
   * @return The attempt number
   */
  public int getAttempt() {
    return attempt;
  }

  /**
   * Gets the environmental conditions description
   *
   * @return The environmental conditions
   */
  public String getEnvironmentalConditions() {
    return environmentalConditions;
  }

  /**
   * Gets the strategy used for reproduction
   *
   * @return The reproduction strategy
   */
  public String getStrategy() {
    return strategy;
  }

  /**
   * Gets the outcome of the reproduction attempt
   *
   * @return The outcome ("successful", "partial", "failed")
   */
  public String getOutcome() {
    return outcome;
  }

  /**
   * Gets the success rate of the reproduction attempt
   *
   * @return The success rate (0.0 to 1.0)
   */
  public double getSuccessRate() {
    return successRate;
  }

  /**
   * Checks if the reproduction attempt was successful
   *
   * @return true if successful, false otherwise
   */
  public boolean isSuccessful() {
    return "successful".equalsIgnoreCase(outcome);
  }

  /**
   * Checks if the reproduction attempt was partially successful
   *
   * @return true if partially successful, false otherwise
   */
  public boolean isPartial() {
    return "partial".equalsIgnoreCase(outcome);
  }

  /**
   * Checks if the reproduction attempt failed
   *
   * @return true if failed, false otherwise
   */
  public boolean isFailed() {
    return "failed".equalsIgnoreCase(outcome);
  }
}
