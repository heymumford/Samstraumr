/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.tube.init;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.s8r.initialization.S8rInitializer;
import org.s8r.test.annotation.UnitTest;

/** Tests for the Samstraumr repository initialization command. */
@UnitTest
public class S8rInitTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @TempDir Path tempDir;

  @BeforeEach
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  @Test
  public void testInitCommandCreatesExpectedStructure() throws IOException {
    // Arrange
    Path repoPath = tempDir.resolve("test-repo");
    Files.createDirectory(repoPath);

    // Create a git repo in the temp directory
    createGitRepo(repoPath);

    // Act
    S8rInitializer initializer = new S8rInitializer(repoPath.toString());
    boolean result = initializer.initialize();

    // Assert
    assertTrue(result, "Initialization should complete successfully");

    // Check expected directories exist
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/org/example/component")),
        "Component directory should exist");
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/org/example/tube")),
        "Tube directory should exist");
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/org/example/machine")),
        "Machine directory should exist");
    assertTrue(
        Files.exists(repoPath.resolve("src/test/java/org/example/component")),
        "Component test directory should exist");

    // Check Adam tube was created
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/org/example/tube/AdamTube.java")),
        "Adam tube should be created");

    // Check s8r configuration files exist
    assertTrue(Files.exists(repoPath.resolve(".s8r/config.json")), "Config file should exist");

    // Verify appropriate messages were printed
    String output = outContent.toString();
    assertTrue(
        output.contains("Initializing Samstraumr repository"),
        "Should show initialization message");
    assertTrue(
        output.contains("Creating directory structure"), "Should show directory creation message");
    assertTrue(output.contains("Creating Adam tube"), "Should show Adam tube creation message");
    assertTrue(
        output.contains("Samstraumr initialization complete"), "Should show completion message");
  }

  @Test
  public void testInitCommandFailsInNonGitRepo() {
    // Arrange
    Path repoPath = tempDir.resolve("non-git-repo");
    try {
      Files.createDirectory(repoPath);
    } catch (IOException e) {
      fail("Failed to create test directory: " + e.getMessage());
    }

    // Act
    S8rInitializer initializer = new S8rInitializer(repoPath.toString());
    boolean result = initializer.initialize();

    // Assert
    assertFalse(result, "Initialization should fail in non-git repository");

    String errorOutput = errContent.toString();
    assertTrue(
        errorOutput.contains("Not a git repository"),
        "Should show error message about non-git repository");
  }

  @Test
  public void testInitCommandFailsInExistingS8rRepo() throws IOException {
    // Arrange
    Path repoPath = tempDir.resolve("existing-repo");
    Files.createDirectory(repoPath);

    // Create a git repo
    createGitRepo(repoPath);

    // Create .s8r directory to simulate existing repo
    Files.createDirectory(repoPath.resolve(".s8r"));

    // Act
    S8rInitializer initializer = new S8rInitializer(repoPath.toString());
    boolean result = initializer.initialize();

    // Assert
    assertFalse(result, "Initialization should fail in existing Samstraumr repository");

    String errorOutput = errContent.toString();
    assertTrue(
        errorOutput.contains("Samstraumr repository already exists"),
        "Should show error message about existing repository");
  }

  @Test
  public void testInitCommandWithPackageOption() throws IOException {
    // Arrange
    Path repoPath = tempDir.resolve("package-test-repo");
    Files.createDirectory(repoPath);
    createGitRepo(repoPath);

    // Act
    S8rInitializer initializer = new S8rInitializer(repoPath.toString(), "com.mycompany.project");
    boolean result = initializer.initialize();

    // Assert
    assertTrue(result, "Initialization with custom package should succeed");

    // Check custom package structure exists
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/com/mycompany/project/component")),
        "Component directory with custom package should exist");
    assertTrue(
        Files.exists(repoPath.resolve("src/main/java/com/mycompany/project/tube/AdamTube.java")),
        "Adam tube with custom package should exist");

    // Verify the package statement in Adam tube
    Path adamTubePath = repoPath.resolve("src/main/java/com/mycompany/project/tube/AdamTube.java");
    String adamTubeContent = new String(Files.readAllBytes(adamTubePath));
    assertTrue(
        adamTubeContent.contains("package com.mycompany.project.tube;"),
        "Adam tube should have correct package declaration");
  }

  /** Helper method to create a git repository in the specified directory */
  private void createGitRepo(Path repoPath) throws IOException {
    ProcessBuilder pb = new ProcessBuilder("git", "init");
    pb.directory(repoPath.toFile());
    Process process = pb.start();
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      fail("Git initialization was interrupted: " + e.getMessage());
    }

    // Create a dummy commit to initialize the repo
    Files.writeString(repoPath.resolve("README.md"), "# Test Repository");

    pb = new ProcessBuilder("git", "add", ".");
    pb.directory(repoPath.toFile());
    process = pb.start();
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      fail("Git add was interrupted: " + e.getMessage());
    }

    pb = new ProcessBuilder("git", "commit", "-m", "Initial commit");
    pb.directory(repoPath.toFile());
    process = pb.start();
    try {
      process.waitFor();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      fail("Git commit was interrupted: " + e.getMessage());
    }
  }
}
