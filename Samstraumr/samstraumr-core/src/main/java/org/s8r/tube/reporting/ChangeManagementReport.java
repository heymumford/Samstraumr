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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates change management reports for Samstraumr version releases.
 *
 * <p>This class uses Docmosis to generate PDF reports that include version details, changes made,
 * and test results. It requires the docmosis-report profile to be active.
 */
public class ChangeManagementReport {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeManagementReport.class);

  // Docmosis requires dynamic loading via reflection to maintain compatibility without the JAR
  private Object docmosisEngine;
  private String templatePath;
  private String outputPath;
  private boolean docmosisAvailable;

  /**
   * Creates a new ChangeManagementReport instance.
   *
   * @param templatePath The path to the docmosis template file
   * @param outputPath The path where generated reports should be saved
   */
  public ChangeManagementReport(String templatePath, String outputPath) {
    this.templatePath = templatePath;
    this.outputPath = outputPath;

    // Check for the DOCMOSIS_KEY environment variable
    String docmosisKey = System.getenv("DOCMOSIS_KEY");
    if (docmosisKey == null || docmosisKey.trim().isEmpty()) {
      LOGGER.warn("DOCMOSIS_KEY environment variable not set. Report generation will fail.");
      LOGGER.warn("Set the key with: export DOCMOSIS_KEY=your-license-key");
      docmosisAvailable = false;
      return;
    }

    try {
      // Try to load Docmosis classes via reflection
      Class<?> engineClass = Class.forName("com.docmosis.SystemManager");
      docmosisEngine = engineClass.getMethod("getNewInstance").invoke(null);

      // Set the license key from the environment variable
      engineClass.getMethod("setKey", String.class).invoke(docmosisEngine, docmosisKey);

      // Initialize Docmosis (this would call something like systemManager.initializeOffline())
      engineClass.getMethod("initializeOffline").invoke(docmosisEngine);

      docmosisAvailable = true;
      LOGGER.info("Docmosis initialized successfully with license key from environment variable.");
    } catch (Exception e) {
      LOGGER.warn(
          "Docmosis not available on classpath or initialization failed. Report generation will fail.",
          e);
      docmosisAvailable = false;
    }
  }

  /**
   * Generates a change management report for the specified version.
   *
   * @param version The version for which to generate the report
   * @param changes List of changes made in this version
   * @param testResults Summary of test results
   * @return The path to the generated report file or null if generation failed
   */
  public String generateReport(
      String version, List<String> changes, Map<String, Boolean> testResults) {
    if (!docmosisAvailable) {
      String docmosisKey = System.getenv("DOCMOSIS_KEY");
      if (docmosisKey == null || docmosisKey.trim().isEmpty()) {
        LOGGER.error("Cannot generate report: DOCMOSIS_KEY environment variable not set.");
        LOGGER.error("Set the key with: export DOCMOSIS_KEY=your-license-key");
      } else {
        LOGGER.error(
            "Cannot generate report: Docmosis not available. Ensure profile docmosis-report is active.");
      }
      return null;
    }

    // In a real implementation, we would:
    // 1. Prepare data for the template
    Map<String, Object> data = new HashMap<>();
    data.put("version", version);
    data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    data.put("changes", changes);
    data.put("testResults", testResults);

    String outputFileName = "SamstraumrChangeReport_" + version + ".pdf";
    String outputFilePath = Paths.get(outputPath, outputFileName).toString();

    try {
      // This is a placeholder for the actual Docmosis rendering
      // In a real implementation, we would call something like:
      // docmosisEngine.render(templateName, data, outputStream);

      // For now, just create a dummy file
      createDummyOutputFile(outputFilePath, version, changes, testResults);
      LOGGER.info("Report generated successfully: {}", outputFilePath);
      return outputFilePath;
    } catch (Exception e) {
      LOGGER.error("Failed to generate report", e);
      return null;
    }
  }

  // This is just a placeholder until Docmosis is properly set up
  private void createDummyOutputFile(
      String filePath, String version, List<String> changes, Map<String, Boolean> testResults)
      throws IOException {
    StringBuilder content = new StringBuilder();
    content.append("CHANGE MANAGEMENT REPORT\n");
    content.append("Version: ").append(version).append("\n");
    content.append("Generated: ").append(LocalDateTime.now()).append("\n\n");

    content.append("CHANGES:\n");
    for (String change : changes) {
      content.append("- ").append(change).append("\n");
    }

    content.append("\nTEST RESULTS:\n");
    for (Map.Entry<String, Boolean> result : testResults.entrySet()) {
      content
          .append("- ")
          .append(result.getKey())
          .append(": ")
          .append(result.getValue() ? "PASSED" : "FAILED")
          .append("\n");
    }

    Files.writeString(Paths.get(filePath), content.toString());
  }

  /** Simple test method to generate a sample report. */
  public static void main(String[] args) {
    // Sample data for testing
    List<String> changes =
        List.of(
            "Added change management reporting",
            "Fixed issue with tube initialization",
            "Improved test coverage for core modules");

    Map<String, Boolean> testResults = new HashMap<>();
    testResults.put("Tube Tests", true);
    testResults.put("Composite Tests", true);
    testResults.put("Stream Tests", false);

    // Create output directory if it doesn't exist
    String outputDir = "target/reports";
    new File(outputDir).mkdirs();

    // Generate the report
    ChangeManagementReport report =
        new ChangeManagementReport("templates/change-report.docx", outputDir);
    String reportPath = report.generateReport("1.2.9", changes, testResults);

    if (reportPath != null) {
      System.out.println("Report generated at: " + reportPath);
    } else {
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
}
