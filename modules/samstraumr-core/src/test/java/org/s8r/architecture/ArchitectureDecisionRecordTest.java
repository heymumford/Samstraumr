/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.test.annotation.UnitTest;

/** Tests for the Architecture Decision Records (ADR) functionality. */
@UnitTest
@Tag("architecture")
public class ArchitectureDecisionRecordTest {

  private static final String ADR_SCRIPT_PATH = "bin/new-adr";
  private static final String ADR_DIR_PATH = "docs/architecture/decisions";
  private static final int SCRIPT_TIMEOUT_SECONDS = 30;

  private String projectRoot;

  @BeforeEach
  void setUp() {
    // Determine the project root directory
    projectRoot = new File("").getAbsolutePath();

    // Go up from the test directory to find project root if needed
    if (projectRoot.endsWith("Samstraumr/samstraumr-core")) {
      projectRoot = Paths.get(projectRoot).getParent().getParent().toString();
    } else if (projectRoot.endsWith("Samstraumr")) {
      projectRoot = Paths.get(projectRoot).getParent().toString();
    }

    // Verify that the script exists
    Path scriptPath = Paths.get(projectRoot, ADR_SCRIPT_PATH);
    assertTrue(Files.exists(scriptPath), "ADR script not found at: " + scriptPath);
  }

  @Test
  void testScriptExists() {
    Path scriptPath = Paths.get(projectRoot, ADR_SCRIPT_PATH);
    assertTrue(Files.exists(scriptPath), "ADR script should exist at: " + scriptPath);
    assertTrue(Files.isExecutable(scriptPath), "ADR script should be executable");
  }

  @Test
  void testAdrDirectoryExists() {
    Path adrDir = Paths.get(projectRoot, ADR_DIR_PATH);
    assertTrue(Files.exists(adrDir), "ADR directory should exist at: " + adrDir);
    assertTrue(Files.isDirectory(adrDir), "ADR path should be a directory");
  }

  @Test
  void testExistingAdrFiles() throws IOException {
    Path adrDir = Paths.get(projectRoot, ADR_DIR_PATH);

    // List all .md files in the ADR directory
    List<Path> adrFiles =
        Files.list(adrDir).filter(p -> p.toString().endsWith(".md")).collect(Collectors.toList());

    // There should be at least one ADR file
    assertFalse(adrFiles.isEmpty(), "There should be at least one ADR file");

    // Check the first ADR (should be 0001)
    Path firstAdr =
        adrFiles.stream()
            .filter(p -> p.getFileName().toString().startsWith("0001-"))
            .findFirst()
            .orElse(null);

    assertNotNull(firstAdr, "First ADR (0001-) should exist");

    // Check its content
    String content = Files.readString(firstAdr);
    assertTrue(content.contains("# 1."), "First ADR should have a title starting with '# 1.'");
    assertTrue(content.contains("## Status"), "ADR should have a Status section");
    assertTrue(content.contains("## Context"), "ADR should have a Context section");
    assertTrue(content.contains("## Decision"), "ADR should have a Decision section");
    assertTrue(content.contains("## Consequences"), "ADR should have a Consequences section");
  }

  @Test
  void testCreateNewAdr() throws Exception {
    // Create a temporary directory for the test
    Path tempDir = Files.createTempDirectory("adr-test");
    try {
      // Copy existing ADRs to the temp directory to preserve the sequence
      Path sourceDir = Paths.get(projectRoot, ADR_DIR_PATH);
      Path targetDir = tempDir.resolve("decisions");
      Files.createDirectories(targetDir);

      Files.list(sourceDir)
          .filter(p -> p.toString().endsWith(".md"))
          .forEach(
              source -> {
                try {
                  Files.copy(source, targetDir.resolve(source.getFileName()));
                } catch (IOException e) {
                  fail("Failed to copy ADR files: " + e.getMessage());
                }
              });

      // Count existing ADRs to determine the next number
      long existingAdrCount =
          Files.list(targetDir)
              .filter(p -> p.toString().endsWith(".md"))
              .filter(p -> Pattern.matches("\\d{4}-.*\\.md", p.getFileName().toString()))
              .count();

      String nextAdrNumber = String.format("%04d", existingAdrCount + 1);

      // Call the new-adr script with a test title
      String testTitle = "Test Architecture Decision for Unit Test";

      ProcessBuilder processBuilder =
          new ProcessBuilder(Paths.get(projectRoot, ADR_SCRIPT_PATH).toString(), testTitle)
              .directory(tempDir.toFile());

      // Set environment variable to use temp directory
      processBuilder.environment().put("ADR_DIR", targetDir.toString());

      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      boolean completed = process.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      assertTrue(completed, "Script timed out");

      String output =
          new BufferedReader(new InputStreamReader(process.getInputStream()))
              .lines()
              .collect(Collectors.joining("\n"));

      System.out.println("Script output: " + output);

      assertEquals(0, process.exitValue(), "Script should exit with code 0");

      // Verify the new ADR was created
      Path newAdrPath =
          Files.list(targetDir)
              .filter(p -> p.toString().endsWith(".md"))
              .filter(p -> p.getFileName().toString().startsWith(nextAdrNumber))
              .findFirst()
              .orElse(null);

      assertNotNull(
          newAdrPath, "New ADR with number " + nextAdrNumber + " should exist in " + targetDir);

      // Check content of the new ADR
      String newAdrContent = Files.readString(newAdrPath);
      assertTrue(
          newAdrContent.contains("# " + (existingAdrCount + 1) + ". " + testTitle),
          "New ADR should have correct title");
      assertTrue(newAdrContent.contains("## Status"), "New ADR should have Status section");
      assertTrue(newAdrContent.contains("## Context"), "New ADR should have Context section");
      assertTrue(newAdrContent.contains("## Decision"), "New ADR should have Decision section");
      assertTrue(
          newAdrContent.contains("## Consequences"), "New ADR should have Consequences section");

      // Check for index file
      Path indexPath = targetDir.resolve("README.md");
      assertTrue(Files.exists(indexPath), "ADR index file should exist");

      // Verify the index contains the new ADR
      String indexContent = Files.readString(indexPath);
      assertTrue(indexContent.contains(testTitle), "ADR index should contain the new ADR title");

    } finally {
      // Clean up the temp directory
      deleteDirectory(tempDir);
    }
  }

  @Test
  void testAdrIndexConsistency() throws IOException {
    Path adrDir = Paths.get(projectRoot, ADR_DIR_PATH);
    Path indexFile = adrDir.resolve("README.md");

    // Ensure index file exists
    assertTrue(Files.exists(indexFile), "ADR index file should exist");

    // Read all ADR files
    List<Path> adrFiles =
        Files.list(adrDir)
            .filter(p -> p.toString().endsWith(".md"))
            .filter(p -> !p.getFileName().toString().equals("README.md"))
            .sorted()
            .collect(Collectors.toList());

    // Read the index content
    String indexContent = Files.readString(indexFile);

    // Check that each ADR is listed in the index
    for (Path adrFile : adrFiles) {
      String adrContent = Files.readString(adrFile);

      // Extract the title from the ADR (first line starting with #)
      String title =
          adrContent.lines().filter(line -> line.startsWith("# ")).findFirst().orElse("");

      if (!title.isEmpty()) {
        // Extract just the title text without the # prefix
        String titleText = title.substring(2);
        assertTrue(
            indexContent.contains(titleText),
            "ADR index should reference the ADR titled: " + titleText);
      }
    }
  }

  private void deleteDirectory(Path directory) throws IOException {
    if (Files.exists(directory)) {
      Files.walk(directory)
          .sorted((a, b) -> -a.compareTo(b))
          .forEach(
              path -> {
                try {
                  Files.delete(path);
                } catch (IOException e) {
                  System.err.println("Failed to delete " + path + ": " + e.getMessage());
                }
              });
    }
  }
}
