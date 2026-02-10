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

package org.s8r.tube.init;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ProjectInitializationPort;
import org.s8r.application.service.ProjectInitializationService;
import org.s8r.test.annotation.UnitTest;

/** Tests for the Samstraumr repository initialization command. */
@UnitTest
public class S8rInitTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @TempDir Path tempDir;

  @Mock private LoggerPort mockLogger;

  @Mock private ProjectInitializationPort mockInitPort;

  private ProjectInitializationService initService;

  private AutoCloseable mockCloseable;

  @BeforeEach
  public void setUp() {
    mockCloseable = MockitoAnnotations.openMocks(this);
    initService = new ProjectInitializationService(mockInitPort, mockLogger);

    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  public void tearDown() throws Exception {
    System.setOut(originalOut);
    System.setErr(originalErr);
    mockCloseable.close();
  }

  @Test
  public void testInitCommandCreatesExpectedStructure() throws IOException {
    // Arrange
    Path repoPath = tempDir.resolve("test-repo");
    Files.createDirectory(repoPath);

    // Create a git repo in the temp directory
    createGitRepo(repoPath);

    // Configure mock
    when(mockInitPort.isValidRepository(repoPath.toString())).thenReturn(true);
    when(mockInitPort.isExistingProject(repoPath.toString())).thenReturn(false);

    // Act
    boolean result = initService.initializeProject(repoPath.toString(), "org.example");

    // Assert
    assertTrue(result, "Initialization should complete successfully");

    // Verify the correct methods were called on the port
    verify(mockInitPort).createDirectoryStructure(repoPath.toString(), "org.example");
    verify(mockInitPort).createAdamComponent(repoPath.toString(), "org.example");
    verify(mockInitPort).createConfigurationFiles(repoPath.toString(), "org.example");

    // Verify logging
    verify(mockLogger).info("Initializing Samstraumr project at {}", repoPath.toString());
    verify(mockLogger).info("Creating directory structure");
    verify(mockLogger).info("Creating Adam component");
    verify(mockLogger).info("Creating configuration files");
    verify(mockLogger).info("Samstraumr project initialization completed successfully");
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

    // Configure mock for invalid repository
    when(mockInitPort.isValidRepository(repoPath.toString())).thenReturn(false);

    // Act
    boolean result = initService.initializeProject(repoPath.toString(), "org.example");

    // Assert
    assertFalse(result, "Initialization should fail in non-git repository");

    // Verify error was logged
    verify(mockLogger)
        .error("Not a valid repository for initialization. Please ensure it's a git repository.");

    // Verify no other interactions with port
    verify(mockInitPort, never()).createDirectoryStructure(anyString(), anyString());
    verify(mockInitPort, never()).createAdamComponent(anyString(), anyString());
    verify(mockInitPort, never()).createConfigurationFiles(anyString(), anyString());
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

    // Configure mocks
    when(mockInitPort.isValidRepository(repoPath.toString())).thenReturn(true);
    when(mockInitPort.isExistingProject(repoPath.toString())).thenReturn(true);

    // Act
    boolean result = initService.initializeProject(repoPath.toString(), "org.example");

    // Assert
    assertFalse(result, "Initialization should fail in existing Samstraumr repository");

    // Verify error message logged
    verify(mockLogger).error("Samstraumr project already exists at this location.");

    // Verify no other interactions with port
    verify(mockInitPort, never()).createDirectoryStructure(anyString(), anyString());
    verify(mockInitPort, never()).createAdamComponent(anyString(), anyString());
    verify(mockInitPort, never()).createConfigurationFiles(anyString(), anyString());
  }

  @Test
  public void testInitCommandWithPackageOption() throws IOException {
    // Arrange
    Path repoPath = tempDir.resolve("package-test-repo");
    Files.createDirectory(repoPath);
    createGitRepo(repoPath);

    // Configure mocks
    when(mockInitPort.isValidRepository(repoPath.toString())).thenReturn(true);
    when(mockInitPort.isExistingProject(repoPath.toString())).thenReturn(false);

    // Act
    boolean result = initService.initializeProject(repoPath.toString(), "com.mycompany.project");

    // Assert
    assertTrue(result, "Initialization with custom package should succeed");

    // Verify with custom package name
    verify(mockInitPort).createDirectoryStructure(repoPath.toString(), "com.mycompany.project");
    verify(mockInitPort).createAdamComponent(repoPath.toString(), "com.mycompany.project");
    verify(mockInitPort).createConfigurationFiles(repoPath.toString(), "com.mycompany.project");
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
