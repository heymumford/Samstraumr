package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.s8r.test.annotation.UnitTest;

/** Tests for the C4 diagram generation functionality. */
@UnitTest
@Tag("architecture")
public class DiagramGenerationTest {

  private static final String DIAGRAM_SCRIPT_PATH = "bin/generate-diagrams.sh";
  private static final String DEFAULT_OUTPUT_DIR = "docs/diagrams";
  private static final int SCRIPT_TIMEOUT_SECONDS = 60;

  @TempDir Path tempOutputDir;

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
    Path scriptPath = Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH);
    assertTrue(Files.exists(scriptPath), "Diagram generation script not found at: " + scriptPath);
  }

  @Test
  void testScriptExists() {
    Path scriptPath = Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH);
    assertTrue(
        Files.exists(scriptPath), "Diagram generation script should exist at: " + scriptPath);
    assertTrue(Files.isExecutable(scriptPath), "Diagram generation script should be executable");
  }

  @Test
  void testScriptHelp() throws Exception {
    ProcessBuilder processBuilder =
        new ProcessBuilder(Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(), "--help");

    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    boolean completed = process.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertTrue(completed, "Script timed out");

    assertEquals(0, process.exitValue(), "Script should exit with code 0");

    String output =
        new BufferedReader(new InputStreamReader(process.getInputStream()))
            .lines()
            .collect(Collectors.joining("\n"));

    assertTrue(output.contains("Usage:"), "Help output should contain usage information");
    assertTrue(output.contains("--async"), "Help output should contain async option");
    assertTrue(output.contains("--format"), "Help output should contain format option");
    assertTrue(output.contains("--type"), "Help output should contain type option");
  }

  @Test
  void testGenerateSingleDiagram() throws Exception {
    // Use a temporary directory for the output
    String outputDir = tempOutputDir.toString();

    ProcessBuilder processBuilder =
        new ProcessBuilder(
            Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(),
            "--type",
            "context",
            "--format",
            "svg",
            "--output-dir",
            outputDir);

    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    boolean completed = process.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertTrue(completed, "Script timed out");

    int exitCode = process.exitValue();
    String output =
        new BufferedReader(new InputStreamReader(process.getInputStream()))
            .lines()
            .collect(Collectors.joining("\n"));

    System.out.println("Script output: " + output);

    assertEquals(0, exitCode, "Script should exit with code 0");

    // Check if the SVG file was created
    Path diagramFile = Paths.get(outputDir, "samstraumr_context_diagram.svg");
    assertTrue(Files.exists(diagramFile), "Diagram file should exist at: " + diagramFile);

    // Check if the README.md was created
    Path readmeFile = Paths.get(outputDir, "README.md");
    assertTrue(Files.exists(readmeFile), "README.md file should exist at: " + readmeFile);

    // Check README.md content
    String readmeContent = Files.readString(readmeFile);
    assertTrue(
        readmeContent.contains("# Samstraumr C4 Architecture Diagrams"),
        "README should have title");
    assertTrue(
        readmeContent.contains("## Context Diagram"),
        "README should include Context Diagram section");
  }

  @Test
  void testCreatePythonScript() throws Exception {
    // Delete the Python script if it exists
    Path pythonScriptPath = Paths.get(projectRoot, "bin", "c4_diagrams.py");
    Files.deleteIfExists(pythonScriptPath);

    assertFalse(Files.exists(pythonScriptPath), "Python script should not exist at this point");

    // Run diagram generation which should create the Python script
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(),
            "--type",
            "context",
            "--format",
            "svg",
            "--output-dir",
            tempOutputDir.toString());

    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    boolean completed = process.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertTrue(completed, "Script timed out");

    assertEquals(0, process.exitValue(), "Script should exit with code 0");

    // Verify the Python script was created
    assertTrue(
        Files.exists(pythonScriptPath),
        "Python script should have been created at: " + pythonScriptPath);
    assertTrue(Files.isExecutable(pythonScriptPath), "Python script should be executable");
  }

  @Test
  void testAsyncGeneration() throws Exception {
    // Use a temporary directory for the output
    String outputDir = tempOutputDir.toString();

    ProcessBuilder processBuilder =
        new ProcessBuilder(
            Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(),
            "--async",
            "--type",
            "all",
            "--format",
            "svg",
            "--output-dir",
            outputDir);

    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    boolean completed = process.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertTrue(completed, "Script timed out");

    assertEquals(0, process.exitValue(), "Script should exit with code 0");

    // Since it's async, we need to wait a bit for the log file to be created
    Path logFile = Paths.get(outputDir, "diagram-generation.log");

    // Wait up to 5 seconds for the log file to appear
    for (int i = 0; i < 50 && !Files.exists(logFile); i++) {
      Thread.sleep(100);
    }

    assertTrue(Files.exists(logFile), "Log file should exist at: " + logFile);

    // Wait for diagrams to be generated (look for .last-generated file)
    Path lastGeneratedFile = Paths.get(outputDir, ".last-generated");

    // Wait up to 10 seconds for the generation to complete
    for (int i = 0; i < 100 && !Files.exists(lastGeneratedFile); i++) {
      Thread.sleep(100);
    }

    // Even if the generation isn't complete, verify that the log shows activity
    if (Files.exists(logFile)) {
      String logContent = Files.readString(logFile);
      assertTrue(
          logContent.contains("Generating")
              || logContent.contains("diagram")
              || logContent.contains("architecture"),
          "Log file should contain evidence of diagram generation activity");
    }
  }

  @Test
  void testCleanOption() throws Exception {
    // Use a temporary directory for the output
    String outputDir = tempOutputDir.toString();

    // First generate some diagrams
    ProcessBuilder processBuilder1 =
        new ProcessBuilder(
            Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(),
            "--type",
            "context",
            "--format",
            "svg",
            "--output-dir",
            outputDir);

    processBuilder1.redirectErrorStream(true);
    Process process1 = processBuilder1.start();
    process1.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertEquals(0, process1.exitValue(), "First script run should exit with code 0");

    // Verify at least one diagram was created
    Path diagramFile = Paths.get(outputDir, "samstraumr_context_diagram.svg");
    assertTrue(Files.exists(diagramFile), "Diagram file should exist");

    // Create a dummy file to ensure it gets removed by --clean
    Path dummyFile = Paths.get(outputDir, "dummy_diagram.svg");
    Files.writeString(dummyFile, "dummy content");
    assertTrue(Files.exists(dummyFile), "Dummy file should exist");

    // Run with --clean option
    ProcessBuilder processBuilder2 =
        new ProcessBuilder(
            Paths.get(projectRoot, DIAGRAM_SCRIPT_PATH).toString(),
            "--clean",
            "--type",
            "context",
            "--format",
            "svg",
            "--output-dir",
            outputDir);

    processBuilder2.redirectErrorStream(true);
    Process process2 = processBuilder2.start();
    process2.waitFor(SCRIPT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    assertEquals(0, process2.exitValue(), "Second script run should exit with code 0");

    // Verify the dummy file was removed
    assertFalse(Files.exists(dummyFile), "Dummy file should have been removed by --clean");

    // But the new diagram should exist
    assertTrue(Files.exists(diagramFile), "New diagram file should exist");
  }
}
