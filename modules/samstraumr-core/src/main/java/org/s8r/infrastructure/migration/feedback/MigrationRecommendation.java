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

package org.s8r.infrastructure.migration.feedback;

import java.util.Objects;

/** A recommendation for resolving migration issues. */
public class MigrationRecommendation {

  private final String id;
  private final String description;
  private final String solution;
  private final Severity severity;
  private final int issueCount;

  /**
   * Creates a new MigrationRecommendation.
   *
   * @param id The recommendation ID
   * @param description The description of the issue
   * @param solution The recommended solution
   * @param severity The severity of the issues
   * @param issueCount The number of issues this recommendation addresses
   */
  public MigrationRecommendation(
      String id, String description, String solution, Severity severity, int issueCount) {
    this.id = id;
    this.description = description;
    this.solution = solution;
    this.severity = severity;
    this.issueCount = issueCount;
  }

  /**
   * Gets the recommendation ID.
   *
   * @return The recommendation ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the description of the issue.
   *
   * @return The description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the recommended solution.
   *
   * @return The solution
   */
  public String getSolution() {
    return solution;
  }

  /**
   * Gets the severity of the issues.
   *
   * @return The severity
   */
  public Severity getSeverity() {
    return severity;
  }

  /**
   * Gets the number of issues this recommendation addresses.
   *
   * @return The issue count
   */
  public int getIssueCount() {
    return issueCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MigrationRecommendation that = (MigrationRecommendation) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "%s: %s (Affects %d issues with %s severity)", id, description, issueCount, severity);
  }
}
