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
 * Represents the possible survival strategies that a tube can adopt. Based on the conceptual
 * statement: "The mass of the tube has only two solutions: be immortal or reproduce."
 */
public enum SurvivalStrategy {
  /**
   * The tube chooses self-sufficiency and self-management to persist indefinitely. Selected when
   * the environment is not nurturing or favorable.
   */
  IMMORTALITY,

  /**
   * The tube chooses to reproduce, transferring essential knowledge to offspring before
   * terminating. Selected when the environment is favorable.
   */
  REPRODUCTION,

  /**
   * A mixed strategy that combines elements of both immortality and reproduction. This may involve
   * limited reproduction while maintaining self-sufficiency.
   */
  HYBRID;

  /**
   * Determines the appropriate strategy based on environmental favorability
   *
   * @param favorability The calculated favorability (0.0 to 1.0)
   * @return The appropriate survival strategy
   */
  public static SurvivalStrategy fromFavorability(double favorability) {
    if (favorability >= 0.7) {
      return REPRODUCTION;
    } else if (favorability <= 0.4) {
      return IMMORTALITY;
    } else {
      return HYBRID;
    }
  }
}
