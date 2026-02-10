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

package org.s8r.tube.reporting;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Command-line tool for generating change management reports.
 *
 * <p>This class provides a command-line interface for generating change management reports using
 * the Docmosis library. It extracts version changes from Git history and includes test results in
 * the report.
 */
public class ChangeReportGenerator {

  /**
   * Main entry point for the change report generator.
   *
   * @param args Command-line arguments
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      showUsage();
      return;
    }

    String fromVersion = args[0];
    String toVersion = args[1];
    String outputDir = args.length > 2 ? args[2] : "target/reports";

    // Ensure output directory exists
    new File(outputDir).mkdirs();

    System.out.println("Generating change management report...");
    System.out.println("  From version: " + fromVersion);
    System.out.println("  To version:   " + toVersion);
    System.out.println("  Output:       " + outputDir);
    System.out.println();

    // Get changes from Git
    List<String> changes = ChangeManagementData.getChangesFromGit(fromVersion, toVersion);

    if (changes.isEmpty()) {
      System.err.println("No changes found between versions " + fromVersion + " and " + toVersion);
      return;
    }

    System.out.println("Found " + changes.size() + " changes in Git history.");

    // Get test results (using a placeholder file for now)
    Map<String, Boolean> testResults = ChangeManagementData.parseTestResults("test-results.txt");

    // Generate the report
    ChangeManagementReport report =
        new ChangeManagementReport("templates/change-report.docx", outputDir);

    String reportPath = report.generateReport(toVersion, changes, testResults);

    if (reportPath != null) {
      System.out.println();
      System.out.println("Report generated successfully: " + reportPath);
    } else {
      System.err.println();
      String docmosisKey = System.getenv("DOCMOSIS_KEY");
      if (docmosisKey == null || docmosisKey.trim().isEmpty()) {
        System.err.println("Failed to generate report: DOCMOSIS_KEY environment variable not set.");
        System.err.println("Set the key with: export DOCMOSIS_KEY=your-license-key");
      } else {
        System.err.println(
            "Failed to generate report. Ensure Docmosis is available on the classpath.");
        System.err.println("Run with: mvn clean install -P docmosis-report");
      }
    }
  }

  private static void showUsage() {
    System.out.println("Change Management Report Generator");
    System.out.println("----------------------------------");
    System.out.println(
        "Usage: java -cp target/samstraumr-core.jar org.samstraumr.tube.reporting.ChangeReportGenerator"
            + " <from-version> <to-version> [output-dir]");
    System.out.println();
    System.out.println("Arguments:");
    System.out.println("  from-version  The starting version (e.g., 1.2.8)");
    System.out.println("  to-version    The ending version (e.g., 1.2.9 or HEAD)");
    System.out.println("  output-dir    Optional output directory (default: target/reports)");
    System.out.println();
    System.out.println(
        "Example: java -cp target/samstraumr-core.jar org.samstraumr.tube.reporting.ChangeReportGenerator 1.2.8 1.2.9");
    System.out.println();
    System.out.println("Note: This requires Docmosis to be available on the classpath and");
    System.out.println("      a valid license key in the DOCMOSIS_KEY environment variable.");
    System.out.println("");
    System.out.println("Setup:");
    System.out.println("  1. export DOCMOSIS_KEY=your-license-key");
    System.out.println("  2. mvn clean install -P docmosis-report");
  }
}
