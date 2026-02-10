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

package org.s8r.infrastructure.migration.feedback;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/** Statistics about migration issues. */
public class MigrationStats {

  private final int totalIssues;
  private final int affectedComponents;
  private final Map<Severity, Integer> issueCountBySeverity;
  private final Map<IssueType, Integer> issueCountByType;

  /**
   * Creates a new MigrationStats.
   *
   * @param totalIssues The total number of issues
   * @param affectedComponents The number of affected components
   * @param issueCountBySeverity The count of issues by severity
   * @param issueCountByType The count of issues by type
   */
  public MigrationStats(
      int totalIssues,
      int affectedComponents,
      Map<Severity, Integer> issueCountBySeverity,
      Map<IssueType, Integer> issueCountByType) {
    this.totalIssues = totalIssues;
    this.affectedComponents = affectedComponents;

    // Create defensive copies
    this.issueCountBySeverity = Collections.unmodifiableMap(new EnumMap<>(issueCountBySeverity));
    this.issueCountByType = Collections.unmodifiableMap(new EnumMap<>(issueCountByType));
  }

  /**
   * Gets the total number of issues.
   *
   * @return The total number of issues
   */
  public int getTotalIssues() {
    return totalIssues;
  }

  /**
   * Gets the number of affected components.
   *
   * @return The number of affected components
   */
  public int getAffectedComponents() {
    return affectedComponents;
  }

  /**
   * Gets the count of issues by severity.
   *
   * @return A map of severity to issue count
   */
  public Map<Severity, Integer> getIssueCountBySeverity() {
    return issueCountBySeverity;
  }

  /**
   * Gets the count of issues by type.
   *
   * @return A map of issue type to issue count
   */
  public Map<IssueType, Integer> getIssueCountByType() {
    return issueCountByType;
  }

  /**
   * Gets the count of issues with at least the specified severity.
   *
   * @param minSeverity The minimum severity
   * @return The count of issues with at least the specified severity
   */
  public int getIssueCountByMinSeverity(Severity minSeverity) {
    int count = 0;
    for (Map.Entry<Severity, Integer> entry : issueCountBySeverity.entrySet()) {
      if (entry.getKey().isAtLeast(minSeverity)) {
        count += entry.getValue();
      }
    }
    return count;
  }

  /**
   * Gets the percentage of issues with at least the specified severity.
   *
   * @param minSeverity The minimum severity
   * @return The percentage of issues with at least the specified severity
   */
  public double getIssuePercentageByMinSeverity(Severity minSeverity) {
    if (totalIssues == 0) {
      return 0.0;
    }

    return (double) getIssueCountByMinSeverity(minSeverity) / totalIssues * 100.0;
  }
}
