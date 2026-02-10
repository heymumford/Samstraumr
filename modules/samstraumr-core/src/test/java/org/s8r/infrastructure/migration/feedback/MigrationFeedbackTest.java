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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for the MigrationFeedback system. */
class MigrationFeedbackTest {

  private MigrationIssueLogger logger;

  @BeforeEach
  void setUp() {
    // Clear any existing issues
    MigrationFeedback.clear();

    // Configure the system for testing
    MigrationFeedbackConfig config =
        new MigrationFeedbackConfig.Builder()
            .setMinSeverity(Severity.DEBUG)
            .enableRecommendations(true)
            .build();
    MigrationFeedback.configure(config);

    // Create a logger for testing
    logger = new MigrationIssueLogger("TestComponent");
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    MigrationFeedback.clear();
  }

  @Test
  void testReportAndRetrieveIssues() {
    // Report some test issues
    logger.reportTypeMismatch("status", "TubeStatus", "State");
    logger.reportPropertyNotFound("metadata.lifecycle");
    logger.reportStateTransitionIssue("ACTIVE", "TERMINATED", "Invalid direct transition");

    // Get all issues
    List<MigrationIssue> issues = MigrationFeedback.getIssues();

    // Verify the issues were reported
    assertEquals(3, issues.size(), "Should have reported 3 issues");

    // Verify issue types
    List<MigrationIssue> typeMismatchIssues =
        MigrationFeedback.getIssuesByType(IssueType.TYPE_MISMATCH);
    List<MigrationIssue> missingPropertyIssues =
        MigrationFeedback.getIssuesByType(IssueType.MISSING_PROPERTY);
    List<MigrationIssue> stateTransitionIssues =
        MigrationFeedback.getIssuesByType(IssueType.STATE_TRANSITION);

    assertEquals(1, typeMismatchIssues.size(), "Should have 1 type mismatch issue");
    assertEquals(1, missingPropertyIssues.size(), "Should have 1 missing property issue");
    assertEquals(1, stateTransitionIssues.size(), "Should have 1 state transition issue");

    // Verify issue properties
    MigrationIssue typeMismatchIssue = typeMismatchIssues.get(0);
    assertEquals("status", typeMismatchIssue.getProperty(), "Property should be 'status'");
    assertEquals(
        "TubeStatus", typeMismatchIssue.getLegacyValue(), "Legacy value should be 'TubeStatus'");
    assertEquals("State", typeMismatchIssue.getS8rValue(), "S8r value should be 'State'");

    MigrationIssue stateTransitionIssue = stateTransitionIssues.get(0);
    assertEquals("ACTIVE", stateTransitionIssue.getLegacyValue(), "From state should be 'ACTIVE'");
    assertEquals(
        "TERMINATED", stateTransitionIssue.getS8rValue(), "To state should be 'TERMINATED'");
  }

  @Test
  void testGetStatistics() {
    // Report some test issues with different severities
    logger.reportTypeMismatch("status", "TubeStatus", "State"); // WARNING
    logger.reportPropertyNotFound("metadata.lifecycle"); // WARNING
    logger.reportCustomIssue(IssueType.REFLECTION_ERROR, Severity.ERROR, "Failed to access field");
    logger.reportCustomIssue(IssueType.LIFECYCLE, Severity.INFO, "Lifecycle change detected");

    // Get statistics
    MigrationStats stats = MigrationFeedback.getStats();

    // Verify overall statistics
    assertEquals(4, stats.getTotalIssues(), "Total issues should be 4");
    assertEquals(1, stats.getAffectedComponents(), "Should have 1 affected component");

    // Verify counts by severity
    Map<Severity, Integer> countBySeverity = stats.getIssueCountBySeverity();
    assertEquals(1, countBySeverity.getOrDefault(Severity.ERROR, 0), "Should have 1 ERROR issue");
    assertEquals(
        2, countBySeverity.getOrDefault(Severity.WARNING, 0), "Should have 2 WARNING issues");
    assertEquals(1, countBySeverity.getOrDefault(Severity.INFO, 0), "Should have 1 INFO issue");

    // Verify counts by type
    Map<IssueType, Integer> countByType = stats.getIssueCountByType();
    assertEquals(
        1,
        countByType.getOrDefault(IssueType.TYPE_MISMATCH, 0),
        "Should have 1 TYPE_MISMATCH issue");
    assertEquals(
        1,
        countByType.getOrDefault(IssueType.MISSING_PROPERTY, 0),
        "Should have 1 MISSING_PROPERTY issue");
    assertEquals(
        1,
        countByType.getOrDefault(IssueType.REFLECTION_ERROR, 0),
        "Should have 1 REFLECTION_ERROR issue");
    assertEquals(
        1, countByType.getOrDefault(IssueType.LIFECYCLE, 0), "Should have 1 LIFECYCLE issue");
  }

  @Test
  void testRecommendations() {
    // Report multiple similar issues to trigger recommendations
    for (int i = 0; i < 5; i++) {
      logger.reportTypeMismatch("status", "TubeStatus", "State");
    }

    for (int i = 0; i < 4; i++) {
      logger.reportStateTransitionIssue("ACTIVE", "TERMINATED", "Invalid direct transition");
    }

    // Get recommendations
    List<MigrationRecommendation> recommendations = MigrationFeedback.getRecommendations();

    // Verify recommendations were generated
    assertFalse(recommendations.isEmpty(), "Should have generated recommendations");

    // Verify at least one recommendation includes our type mismatch
    boolean hasTypeMismatchRecommendation =
        recommendations.stream()
            .anyMatch(
                r ->
                    r.getDescription().contains("TubeStatus")
                        && r.getDescription().contains("State"));

    assertTrue(
        hasTypeMismatchRecommendation,
        "Should have a recommendation for TubeStatus to State conversion");
  }

  @Test
  void testFilteringByMinimumSeverity() {
    // Configure the system with minimum severity WARNING
    MigrationFeedbackConfig config =
        new MigrationFeedbackConfig.Builder().setMinSeverity(Severity.WARNING).build();
    MigrationFeedback.configure(config);

    // Report issues with different severities
    logger.reportCustomIssue(IssueType.REFLECTION_ERROR, Severity.ERROR, "Error issue");
    logger.reportCustomIssue(IssueType.TYPE_MISMATCH, Severity.WARNING, "Warning issue");
    logger.reportCustomIssue(IssueType.LIFECYCLE, Severity.INFO, "Info issue - should be filtered");
    logger.reportCustomIssue(
        IssueType.DEPENDENCY, Severity.DEBUG, "Debug issue - should be filtered");

    // Get all issues
    List<MigrationIssue> issues = MigrationFeedback.getIssues();

    // Verify only issues with severity >= WARNING were reported
    assertEquals(2, issues.size(), "Should have 2 issues (ERROR and WARNING)");

    // Check issue severities
    boolean allHighSeverity =
        issues.stream().allMatch(issue -> issue.getSeverity().isAtLeast(Severity.WARNING));

    assertTrue(allHighSeverity, "All issues should have severity >= WARNING");
  }

  @Test
  void testMaxIssuesPerComponent() {
    // Configure the system with max 2 issues per component
    MigrationFeedbackConfig config =
        new MigrationFeedbackConfig.Builder().setMaxIssuesPerComponent(2).build();
    MigrationFeedback.configure(config);

    // Report 5 issues for the same component
    for (int i = 0; i < 5; i++) {
      logger.reportCustomIssue(IssueType.TYPE_MISMATCH, Severity.WARNING, "Issue " + (i + 1));
    }

    // Get issues for the component
    List<MigrationIssue> issues = MigrationFeedback.getIssuesForComponent("TestComponent");

    // Verify only max issues were reported
    assertEquals(2, issues.size(), "Should have only 2 issues for the component");
  }

  @Test
  void testTextReportGeneration() {
    // Report some test issues
    logger.reportTypeMismatch("status", "TubeStatus", "State");
    logger.reportStateTransitionIssue("ACTIVE", "TERMINATED", "Invalid direct transition");

    // Generate a text report
    String report = MigrationFeedback.generateTextReport();

    // Verify the report contains expected sections and information
    assertTrue(report.contains("Migration Feedback Report"), "Report should have a title");
    assertTrue(report.contains("Total Issues: 2"), "Report should mention 2 issues");
    assertTrue(report.contains("TubeStatus"), "Report should mention 'TubeStatus'");
    assertTrue(report.contains("ACTIVE"), "Report should mention 'ACTIVE' state");
    assertTrue(report.contains("TERMINATED"), "Report should mention 'TERMINATED' state");
  }
}
