/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.s8r.application.port.FileSystemPort;
import org.s8r.infrastructure.filesystem.StandardFileSystemAdapter;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Step definitions for FileSystem Port interface tests. */
public class FileSystemPortSteps {

  private FileSystemPort fileSystemPort;
  private Path testDirectory;
  private boolean operationResult;
  private List<Path> fileList;

  @Given("a filesystem port implementation is available")
  public void aFilesystemPortImplementationIsAvailable() {
    fileSystemPort = new StandardFileSystemAdapter();
  }

  @Given("a temporary test directory exists")
  public void aTemporaryTestDirectoryExists() throws IOException {
    testDirectory = Files.createTempDirectory("filesystem-port-test").toAbsolutePath();
    testDirectory.toFile().deleteOnExit();
  }

  @When("I write the content {string} to file {string}")
  public void iWriteTheContentToFile(String content, String filename) throws IOException {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    operationResult = fileSystemPort.writeFile(filePath, content);
  }

  @Then("the file {string} should exist")
  public void theFileShouldExist(String filename) {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    Assertions.assertTrue(fileSystemPort.fileExists(filePath), "File should exist: " + filename);
  }

  @Then("the content of file {string} should be {string}")
  public void theContentOfFileShouldBe(String filename, String expectedContent) throws IOException {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    Optional<String> contentOpt = fileSystemPort.readFile(filePath);

    Assertions.assertTrue(contentOpt.isPresent(), "File content should be readable");
    Assertions.assertEquals(
        expectedContent, contentOpt.get(), "File content should match expected content");
  }

  @Given("the file {string} exists with content {string}")
  public void theFileExistsWithContent(String filename, String content) throws IOException {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    fileSystemPort.writeFile(filePath, content);
  }

  @When("I check if file {string} exists")
  public void iCheckIfFileExists(String filename) {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    operationResult = fileSystemPort.fileExists(filePath);
  }

  @Then("the result should be true")
  public void theResultShouldBeTrue() {
    Assertions.assertTrue(operationResult, "Operation result should be true");
  }

  @Then("the result should be false")
  public void theResultShouldBeFalse() {
    Assertions.assertFalse(operationResult, "Operation result should be false");
  }

  @When("I create directory {string}")
  public void iCreateDirectory(String directoryName) throws IOException {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    operationResult = fileSystemPort.createDirectory(dirPath);
  }

  @Then("the directory {string} should exist")
  public void theDirectoryShouldExist(String directoryName) {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    Assertions.assertTrue(
        Files.exists(dirPath) && Files.isDirectory(dirPath),
        "Directory should exist: " + directoryName);
  }

  @Then("I should be able to write a file to {string}")
  public void iShouldBeAbleToWriteAFileTo(String filePath) throws IOException {
    Path path = testDirectory.resolve(filePath).toAbsolutePath();
    boolean writeResult = fileSystemPort.writeFile(path, "Nested file content");
    Assertions.assertTrue(writeResult, "Should be able to write to nested file");
    Assertions.assertTrue(fileSystemPort.fileExists(path), "Nested file should exist");
  }

  @Given("the directory {string} contains the following files:")
  public void theDirectoryContainsTheFollowingFiles(String directoryName, List<String> filenames)
      throws IOException {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    fileSystemPort.createDirectory(dirPath);

    for (String filename : filenames) {
      Path filePath = dirPath.resolve(filename).toAbsolutePath();
      fileSystemPort.writeFile(filePath, "Content for " + filename);
    }
  }

  @When("I list files in directory {string}")
  public void iListFilesInDirectory(String directoryName) throws IOException {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    fileList = fileSystemPort.listFiles(dirPath);
  }

  @Then("the list should contain the following files:")
  public void theListShouldContainTheFollowingFiles(List<String> expectedFilenames) {
    List<String> actualFilenames =
        fileList.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.toList());

    for (String expectedFilename : expectedFilenames) {
      Assertions.assertTrue(
          actualFilenames.contains(expectedFilename),
          "File list should contain: " + expectedFilename);
    }
  }

  @When("I delete the file {string}")
  public void iDeleteTheFile(String filename) throws IOException {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    operationResult = fileSystemPort.delete(filePath);
  }

  @Then("the file {string} should not exist")
  public void theFileShouldNotExist(String filename) {
    Path filePath = testDirectory.resolve(filename).toAbsolutePath();
    Assertions.assertFalse(
        fileSystemPort.fileExists(filePath), "File should not exist: " + filename);
  }

  @When("I delete the directory {string}")
  public void iDeleteTheDirectory(String directoryName) throws IOException {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    operationResult = fileSystemPort.delete(dirPath);
  }

  @Then("the directory {string} should not exist")
  public void theDirectoryShouldNotExist(String directoryName) {
    Path dirPath = testDirectory.resolve(directoryName).toAbsolutePath();
    Assertions.assertFalse(Files.exists(dirPath), "Directory should not exist: " + directoryName);
  }
}
