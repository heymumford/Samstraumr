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
package org.s8r.domain.environment;

/**
 * Represents an environmental factor that can influence tube behavior and decision-making
 * processes.
 */
public class EnvironmentalFactor {
  private final String name;
  private int value;
  private final int threshold;
  private String classification;

  /**
   * Creates a new environmental factor
   *
   * @param name The name of the factor
   * @param value The value of the factor
   * @param threshold The threshold value that determines factor classification
   * @param classification The classification of the factor (e.g., "scarce", "abundant")
   */
  public EnvironmentalFactor(String name, int value, int threshold, String classification) {
    this.name = name;
    this.value = value;
    this.threshold = threshold;
    this.classification = classification;
  }

  /**
   * Gets the name of the environmental factor
   *
   * @return The factor name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the value of the environmental factor
   *
   * @return The factor value
   */
  public int getValue() {
    return value;
  }

  /**
   * Sets the value of the environmental factor
   *
   * @param value The new factor value
   */
  public void setValue(int value) {
    this.value = value;
    updateClassification();
  }

  /**
   * Gets the threshold value for this factor
   *
   * @return The threshold value
   */
  public int getThreshold() {
    return threshold;
  }

  /**
   * Gets the classification of the factor
   *
   * @return The classification (e.g., "scarce", "abundant")
   */
  public String getClassification() {
    return classification;
  }

  /**
   * Sets the classification of the factor
   *
   * @param classification The new classification
   */
  public void setClassification(String classification) {
    this.classification = classification;
  }

  /** Updates the classification based on the current value and threshold */
  private void updateClassification() {
    if (value < threshold) {
      if (name.equals("hostilityLevel")) {
        classification = "benign";
      } else {
        classification = "scarce";
      }
    } else {
      if (name.equals("hostilityLevel")) {
        classification = "hostile";
      } else {
        classification = "abundant";
      }
    }
  }

  /**
   * Determines if this factor is favorable for reproductive strategy
   *
   * @return true if the factor is favorable for reproduction
   */
  public boolean isFavorable() {
    if (name.equals("hostilityLevel")) {
      return value < threshold; // Lower hostility is favorable
    } else {
      return value >= threshold; // Higher resources/stability are favorable
    }
  }
}
