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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the DocumentGenerator class, particularly focusing on the fallback mechanism for Java
 * 21 compatibility.
 */
@Tag("UnitTest")
public class DocumentGeneratorTest {

  @TempDir Path tempDir;

  /** Test that the fallback mechanism works correctly when Docmosis is not available. */
  @Test
  public void testFallbackDocumentGeneration() throws Exception {
    // Create a sample template file
    Path templateDir = tempDir.resolve("templates");
    Files.createDirectories(templateDir);

    Path templateFile = templateDir.resolve("test-template.docx");
    Files.createFile(templateFile);

    // Create output directory
    Path outputDirPath = tempDir.resolve("output");
    Files.createDirectories(outputDirPath);
    String outputDir = outputDirPath.toString();

    // Call the document generation with a non-existent Docmosis library
    // This should trigger the fallback
    assertDoesNotThrow(
        () -> {
          // Set system property to point to our temporary template directory
          System.setProperty("templates.dir", templateDir.toString());

          // Set a test Docmosis key for testing
          System.setProperty("docmosis.key", "TEST_KEY_FOR_JAVA21_COMPATIBILITY_TEST");

          DocumentGenerator.generateDocumentation(outputDir, "pdf");
        });

    // Check if the fallback file was created
    Path fallbackFile = Paths.get(outputDir, "test-template.txt");
    assertTrue(Files.exists(fallbackFile), "Fallback document should have been created");

    // Verify content of fallback file
    String content = Files.readString(fallbackFile);
    assertTrue(
        content.contains("DOCUMENT GENERATION FALLBACK"),
        "Fallback document should contain expected header");
    assertTrue(
        content.contains("Template: test-template.docx"),
        "Fallback document should contain template information");
  }
}
