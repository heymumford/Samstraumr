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

package org.s8r.migration.feedback;

/** Enumeration of migration issue severity levels. */
public enum Severity {
  /**
   * Critical severity - immediate attention required. These issues will cause application failure
   * or data loss.
   */
  CRITICAL(4, "Critical"),

  /**
   * Error severity - important to fix. These issues will cause incorrect behavior or partial
   * failure.
   */
  ERROR(3, "Error"),

  /** Warning severity - should be addressed. These issues may cause problems in some scenarios. */
  WARNING(2, "Warning"),

  /**
   * Info severity - informational only. These issues are unlikely to cause problems but may be
   * useful to know.
   */
  INFO(1, "Info"),

  /**
   * Debug severity - for diagnostic use only. These issues are purely for development and debugging
   * purposes.
   */
  DEBUG(0, "Debug");

  private final int level;
  private final String displayName;

  Severity(int level, String displayName) {
    this.level = level;
    this.displayName = displayName;
  }

  /**
   * Gets the numeric level of this severity.
   *
   * @return The severity level (higher means more severe)
   */
  public int getLevel() {
    return level;
  }

  /**
   * Gets the display name of this severity.
   *
   * @return The display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Checks if this severity is at least as severe as the specified severity.
   *
   * @param other The severity to compare against
   * @return true if this severity is at least as severe as the other
   */
  public boolean isAtLeast(Severity other) {
    return this.level >= other.level;
  }

  /**
   * Checks if this severity is more severe than the specified severity.
   *
   * @param other The severity to compare against
   * @return true if this severity is more severe than the other
   */
  public boolean isMoreSevereThan(Severity other) {
    return this.level > other.level;
  }

  @Override
  public String toString() {
    return displayName;
  }
}
