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
 * Represents the specific operational modes a tube can adopt within its chosen survival strategy.
 */
public enum StrategyMode {
  // Immortality strategy modes
  /** Emphasizes self-sufficiency and resource conservation */
  SELF_SUFFICIENCY,

  /** Optimizes tube operation for long-term survival with minimal resources */
  OPTIMIZED_SURVIVAL,

  /** Enhances resistance to environmental threats and fluctuations */
  RESISTANT_MODE,

  /** Suspends non-essential functions during severe resource constraints */
  HIBERNATION,

  // Reproduction strategy modes
  /** Prioritizes thorough knowledge transfer to offspring */
  KNOWLEDGE_TRANSFER,

  /** Focuses on accelerated reproduction with limited knowledge transfer */
  RAPID_REPRODUCTION,

  /** Delays reproduction to maximize knowledge accumulation */
  DELAYED_TRANSFER,

  // Hybrid strategy modes
  /** Adjusts behavior based on real-time environmental conditions */
  CONDITIONAL,

  /** Limited knowledge transfer while maintaining self-preservation */
  CAUTIOUS_TRANSFER;

  /**
   * Gets the appropriate mode for the given strategy based on environmental conditions
   *
   * @param strategy The survival strategy
   * @param populationDensity Population density factor (if available)
   * @param hostilityLevel Hostility level factor (if available)
   * @param favorability Overall environmental favorability
   * @return The appropriate strategy mode
   */
  public static StrategyMode forStrategy(
      SurvivalStrategy strategy,
      Integer populationDensity,
      Integer hostilityLevel,
      double favorability) {
    switch (strategy) {
      case IMMORTALITY:
        if (hostilityLevel != null && hostilityLevel > 7) {
          return RESISTANT_MODE;
        } else if (favorability < 0.2) {
          return HIBERNATION;
        } else {
          return SELF_SUFFICIENCY;
        }

      case REPRODUCTION:
        if (populationDensity != null && populationDensity > 7) {
          return RAPID_REPRODUCTION;
        } else if (favorability > 0.9) {
          return KNOWLEDGE_TRANSFER;
        } else {
          return DELAYED_TRANSFER;
        }

      case HYBRID:
        if (favorability > 0.5) {
          return CAUTIOUS_TRANSFER;
        } else {
          return CONDITIONAL;
        }

      default:
        return SELF_SUFFICIENCY; // Default to self-sufficiency
    }
  }
}
