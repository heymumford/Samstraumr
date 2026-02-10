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

package org.s8r.adapter.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.FileSystemPort.FileInfo;
import org.s8r.application.port.FileSystemPort.FileResult;
import org.s8r.application.port.FileSystemPort.SearchMode;
import org.s8r.infrastructure.filesystem.StandardFileSystemAdapter;

/**
 * Contract tests for the FileSystemPort interface.
 *
 * <p>This test class verifies that any implementation of the FileSystemPort interface adheres to
 * the contract defined by the interface. It tests the behavior expected by the application core
 * regardless of the specific adapter implementation.
 */
public class FileSystemPortContractTest extends PortContractTest<FileSystemPort> {

  private String testDirectory;
  private String testFile;
  private String nonExistentPath;

  @Override
  protected FileSystemPort createPortImplementation() {
    return new StandardFileSystemAdapter(logger);
  }

  @BeforeEach
  public void setUpTestFiles() {
    // Create a unique test directory and file for each test
    String uniqueId = UUID.randomUUID().toString();
    testDirectory = System.getProperty("java.io.tmpdir") + "/s8r-test-" + uniqueId;
    testFile = testDirectory + "/test-file.txt";
    nonExistentPath = testDirectory + "/non-existent-" + uniqueId;

    // Initialize the port
    portUnderTest.initialize();

    // Create the test directory
    FileResult dirResult = portUnderTest.createDirectories(testDirectory);
    assertTrue(
        dirResult.isSuccessful(), "Failed to create test directory: " + dirResult.getMessage());

    // Create a test file with some content
    String testContent = "This is a test file for FileSystemPort contract tests.";
    FileResult fileResult =
        portUnderTest.writeString(testFile, testContent, StandardCharsets.UTF_8, false);
    assertTrue(fileResult.isSuccessful(), "Failed to create test file: " + fileResult.getMessage());
  }

  @AfterEach
  public void cleanUpTestFiles() {
    if (portUnderTest.exists(testDirectory)) {
      FileResult result = portUnderTest.deleteRecursively(testDirectory);
      if (!result.isSuccessful()) {
        logger.warn("Failed to clean up test directory: {}", result.getMessage());
      }
    }
  }

  @Override
  protected void verifyNullInputHandling() {
    // This is tested in nullInputHandlingTests()
  }

  @Override
  protected void verifyRequiredMethods() {
    // This is tested across multiple method-specific tests
  }

  /** Verifies that the FileSystemPort implementation handles null inputs correctly. */
  @Test
  @DisplayName("Should handle null inputs gracefully")
  public void nullInputHandlingTests() {
    // Test null path in exists
    assertFalse(portUnderTest.exists(null), "exists(null) should return false");

    // Test null path in getInfo
    FileResult nullPathResult = portUnderTest.getInfo(null);
    assertFalse(nullPathResult.isSuccessful(), "getInfo(null) should return failure result");
    assertTrue(nullPathResult.getReason().isPresent(), "getInfo(null) should have a reason");

    // Test null path in createDirectory
    FileResult nullDirResult = portUnderTest.createDirectories(null);
    assertFalse(nullDirResult.isSuccessful(), "createDirectory(null) should return failure result");

    // Test null content in writeString
    FileResult nullContentResult =
        portUnderTest.writeString(testFile, null, StandardCharsets.UTF_8, false);
    assertFalse(
        nullContentResult.isSuccessful(), "writeString(path, null) should return failure result");

    // Test null parameters in readLines
    FileResult nullReadResult = portUnderTest.readLines(null, StandardCharsets.UTF_8);
    assertFalse(nullReadResult.isSuccessful(), "readLines(null) should return failure result");
  }

  /** Tests file and directory creation and existence checking. */
  @Test
  @DisplayName("Should create and check existence of files and directories")
  public void fileCreationAndExistenceTests() {
    // Test exists for existing and non-existent files
    assertTrue(
        portUnderTest.exists(testDirectory), "exists() should return true for existing directory");
    assertTrue(portUnderTest.exists(testFile), "exists() should return true for existing file");
    assertFalse(
        portUnderTest.exists(nonExistentPath),
        "exists() should return false for non-existent path");

    // Test creating a new directory
    String newDir = testDirectory + "/new-dir";
    FileResult dirResult = portUnderTest.createDirectories(newDir);
    assertTrue(dirResult.isSuccessful(), "createDirectory() should succeed");
    assertTrue(portUnderTest.exists(newDir), "Directory should exist after creation");

    // Test creating directories recursively
    String deepDir = testDirectory + "/level1/level2/level3";
    FileResult deepDirResult = portUnderTest.createDirectories(deepDir);
    assertTrue(deepDirResult.isSuccessful(), "createDirectories() should succeed");
    assertTrue(
        portUnderTest.exists(deepDir), "Deep directory structure should exist after creation");
  }

  /** Tests file information retrieval. */
  @Test
  @DisplayName("Should retrieve file information correctly")
  public void fileInformationTests() {
    // Test getInfo for existing file
    FileResult fileInfoResult = portUnderTest.getInfo(testFile);
    assertTrue(fileInfoResult.isSuccessful(), "getInfo() should succeed for existing file");

    @SuppressWarnings("unchecked")
    Map<String, Object> fileInfoAttrs = fileInfoResult.getAttributes();
    assertNotNull(fileInfoAttrs, "File info attributes should not be null");
    FileInfo fileInfo = (FileInfo) fileInfoAttrs.get("fileInfo");

    assertNotNull(fileInfo, "File info should be present");
    assertEquals(testFile, fileInfo.getPath(), "File path should match");
    assertTrue(fileInfo.isRegularFile(), "Should be a regular file");
    assertFalse(fileInfo.isDirectory(), "Should not be a directory");

    // Test getInfo for directory
    FileResult dirInfoResult = portUnderTest.getInfo(testDirectory);
    assertTrue(dirInfoResult.isSuccessful(), "getInfo() should succeed for existing directory");

    @SuppressWarnings("unchecked")
    Map<String, Object> dirInfoAttrs = dirInfoResult.getAttributes();
    FileInfo dirInfo = (FileInfo) dirInfoAttrs.get("fileInfo");

    assertNotNull(dirInfo, "Directory info should be present");
    assertEquals(testDirectory, dirInfo.getPath(), "Directory path should match");
    assertTrue(dirInfo.isDirectory(), "Should be a directory");
    assertFalse(dirInfo.isRegularFile(), "Should not be a regular file");

    // Test getting size
    long size = portUnderTest.size(testFile);
    assertTrue(size > 0, "File size should be positive");

    // Test getting last modified time
    Optional<Instant> lastModified = portUnderTest.getLastModifiedTime(testFile);
    assertTrue(lastModified.isPresent(), "Last modified time should be present");
    assertNotNull(lastModified.get(), "Last modified time should not be null");
  }

  /** Tests file content reading and writing. */
  @Test
  @DisplayName("Should read and write file content correctly")
  public void fileContentTests() {
    String testContent = "This is updated content for testing.";

    // Test writing a string to a file
    FileResult writeResult =
        portUnderTest.writeString(testFile, testContent, StandardCharsets.UTF_8, false);
    assertTrue(writeResult.isSuccessful(), "writeString() should succeed");

    // Test reading the content back
    FileResult readResult = portUnderTest.readString(testFile, StandardCharsets.UTF_8);
    assertTrue(readResult.isSuccessful(), "readString() should succeed");
    assertEquals(
        testContent,
        readResult.getAttributes().get("content"),
        "Read content should match written content");

    // Test writing and reading lines
    List<String> lines = List.of("Line 1", "Line 2", "Line 3");
    FileResult writeLinesResult =
        portUnderTest.writeLines(testFile, lines, StandardCharsets.UTF_8, false);
    assertTrue(writeLinesResult.isSuccessful(), "writeLines() should succeed");

    FileResult readLinesResult = portUnderTest.readLines(testFile, StandardCharsets.UTF_8);
    assertTrue(readLinesResult.isSuccessful(), "readLines() should succeed");

    @SuppressWarnings("unchecked")
    List<String> readLines = (List<String>) readLinesResult.getAttributes().get("lines");
    assertEquals(lines.size(), readLines.size(), "Number of lines should match");
    assertEquals(lines, readLines, "Lines content should match");

    // Test reading a portion of lines
    FileResult readPortionResult = portUnderTest.readLines(testFile, StandardCharsets.UTF_8, 1, 1);
    assertTrue(readPortionResult.isSuccessful(), "readLines(start, count) should succeed");

    @SuppressWarnings("unchecked")
    List<String> readPortion = (List<String>) readPortionResult.getAttributes().get("lines");
    assertEquals(1, readPortion.size(), "Should read exactly one line");
    assertEquals("Line 2", readPortion.get(0), "Should read the correct line");
  }

  /** Tests file and directory operations like copy, move, and delete. */
  @Test
  @DisplayName("Should perform file operations correctly")
  public void fileOperationsTests() {
    // Test file copy
    String copiedFile = testDirectory + "/copied-file.txt";
    FileResult copyResult = portUnderTest.copy(testFile, copiedFile);
    assertTrue(copyResult.isSuccessful(), "copy() should succeed");
    assertTrue(portUnderTest.exists(copiedFile), "Copied file should exist");

    // Test file move
    String movedFile = testDirectory + "/moved-file.txt";
    FileResult moveResult = portUnderTest.move(copiedFile, movedFile);
    assertTrue(moveResult.isSuccessful(), "move() should succeed");
    assertTrue(portUnderTest.exists(movedFile), "Moved file should exist");
    assertFalse(
        portUnderTest.exists(copiedFile), "Original file should no longer exist after move");

    // Test directory copy
    String subDir = testDirectory + "/sub-dir";
    portUnderTest.createDirectories(subDir);
    portUnderTest.writeString(
        subDir + "/sub-file.txt", "Sub-directory file content", StandardCharsets.UTF_8, false);

    String copiedDir = testDirectory + "/copied-dir";
    FileResult copyDirResult = portUnderTest.copyDirectory(subDir, copiedDir);
    assertTrue(copyDirResult.isSuccessful(), "copyDirectory() should succeed");
    assertTrue(portUnderTest.exists(copiedDir), "Copied directory should exist");
    assertTrue(
        portUnderTest.exists(copiedDir + "/sub-file.txt"),
        "Files in copied directory should exist");

    // Test file delete
    FileResult deleteResult = portUnderTest.delete(movedFile);
    assertTrue(deleteResult.isSuccessful(), "delete() should succeed for file");
    assertFalse(portUnderTest.exists(movedFile), "File should no longer exist after delete");

    // Test recursive directory delete
    FileResult deleteDirResult = portUnderTest.deleteRecursively(copiedDir);
    assertTrue(deleteDirResult.isSuccessful(), "deleteRecursively() should succeed");
    assertFalse(
        portUnderTest.exists(copiedDir), "Directory should no longer exist after recursive delete");
  }

  /** Tests file listing and searching. */
  @Test
  @DisplayName("Should list and search files correctly")
  public void fileListingAndSearchingTests() {
    // Create some additional files and directories for testing
    portUnderTest.createDirectories(testDirectory + "/list-test");
    portUnderTest.writeString(
        testDirectory + "/file1.txt", "File 1", StandardCharsets.UTF_8, false);
    portUnderTest.writeString(
        testDirectory + "/file2.txt", "File 2", StandardCharsets.UTF_8, false);
    portUnderTest.writeString(
        testDirectory + "/list-test/nested.txt", "Nested file", StandardCharsets.UTF_8, false);

    // Test listing directory contents
    FileResult listResult = portUnderTest.list(testDirectory);
    assertTrue(listResult.isSuccessful(), "list() should succeed");

    @SuppressWarnings("unchecked")
    List<FileInfo> files = (List<FileInfo>) listResult.getAttributes().get("files");
    assertNotNull(files, "Files list should not be null");
    assertTrue(files.size() >= 4, "Should list at least 4 files/directories");

    // Test searching for files - non-recursive
    FileResult searchResult =
        portUnderTest.search(testDirectory, "*.txt", SearchMode.CURRENT_DIRECTORY);
    assertTrue(searchResult.isSuccessful(), "search() should succeed");

    @SuppressWarnings("unchecked")
    List<FileInfo> searchFiles = (List<FileInfo>) searchResult.getAttributes().get("files");
    assertNotNull(searchFiles, "Search results should not be null");
    assertTrue(searchFiles.size() >= 3, "Should find at least 3 text files in current directory");

    // Test searching for files - recursive
    FileResult recursiveSearchResult =
        portUnderTest.search(testDirectory, "*.txt", SearchMode.RECURSIVE);
    assertTrue(recursiveSearchResult.isSuccessful(), "recursive search() should succeed");

    @SuppressWarnings("unchecked")
    List<FileInfo> recursiveSearchFiles =
        (List<FileInfo>) recursiveSearchResult.getAttributes().get("files");
    assertNotNull(recursiveSearchFiles, "Recursive search results should not be null");
    assertTrue(recursiveSearchFiles.size() >= 4, "Should find at least 4 text files recursively");
  }

  /** Tests path manipulation methods. */
  @Test
  @DisplayName("Should manipulate paths correctly")
  public void pathManipulationTests() {
    // Test path normalization
    String unnormalizedPath =
        testDirectory + "/../" + testDirectory.substring(testDirectory.lastIndexOf('/') + 1);
    String normalizedPath = portUnderTest.normalize(unnormalizedPath);
    assertEquals(testDirectory, normalizedPath, "Normalized path should match original");

    // Test path resolution
    String relativePath = "relative/path";
    String resolvedPath = portUnderTest.resolve(testDirectory, relativePath);
    assertTrue(resolvedPath.startsWith(testDirectory), "Resolved path should start with base path");
    assertTrue(resolvedPath.endsWith(relativePath), "Resolved path should end with relative path");

    // Test getting absolute path
    String absolutePath = portUnderTest.getAbsolutePath(relativePath);
    assertTrue(portUnderTest.isAbsolute(absolutePath), "Absolute path should be absolute");

    // Test getting working directory
    String workingDir = portUnderTest.getWorkingDirectory();
    assertNotNull(workingDir, "Working directory should not be null");
    assertTrue(portUnderTest.isAbsolute(workingDir), "Working directory should be absolute");
  }

  /** Tests temporary file and directory creation. */
  @Test
  @DisplayName("Should create temporary files and directories")
  public void temporaryFilesTests() {
    // Test creating a temporary file
    FileResult tempFileResult = portUnderTest.createTempFile("s8r-test-", ".tmp", testDirectory);
    assertTrue(tempFileResult.isSuccessful(), "createTempFile() should succeed");
    String tempFilePath = (String) tempFileResult.getAttributes().get("path");
    assertNotNull(tempFilePath, "Temp file path should not be null");
    assertTrue(portUnderTest.exists(tempFilePath), "Temp file should exist");

    // Test creating a temporary directory
    FileResult tempDirResult = portUnderTest.createTempDirectory("s8r-test-", testDirectory);
    assertTrue(tempDirResult.isSuccessful(), "createTempDirectory() should succeed");
    String tempDirPath = (String) tempDirResult.getAttributes().get("path");
    assertNotNull(tempDirPath, "Temp directory path should not be null");
    assertTrue(portUnderTest.exists(tempDirPath), "Temp directory should exist");
  }

  /** Tests input and output stream operations. */
  @Test
  @DisplayName("Should open input and output streams correctly")
  public void streamOperationsTests() {
    // Test opening an input stream
    FileResult inputStreamResult = portUnderTest.openInputStream(testFile);
    assertTrue(inputStreamResult.isSuccessful(), "openInputStream() should succeed");
    InputStream inputStream = (InputStream) inputStreamResult.getAttributes().get("inputStream");
    assertNotNull(inputStream, "Input stream should not be null");
    try {
      inputStream.close();
    } catch (Exception e) {
      fail("Failed to close input stream: " + e.getMessage());
    }

    // Test opening an output stream
    FileResult outputStreamResult = portUnderTest.openOutputStream(testFile, true);
    assertTrue(outputStreamResult.isSuccessful(), "openOutputStream() should succeed");
    OutputStream outputStream =
        (OutputStream) outputStreamResult.getAttributes().get("outputStream");
    assertNotNull(outputStream, "Output stream should not be null");
    try {
      outputStream.close();
    } catch (Exception e) {
      fail("Failed to close output stream: " + e.getMessage());
    }
  }

  /** Tests file system information methods. */
  @Test
  @DisplayName("Should provide file system information")
  public void fileSystemInfoTests() {
    // Test getting file separator
    String separator = portUnderTest.getSeparator();
    assertNotNull(separator, "Separator should not be null");

    // Test getting path separator
    String pathSeparator = portUnderTest.getPathSeparator();
    assertNotNull(pathSeparator, "Path separator should not be null");

    // Test getting root directories
    List<String> rootDirs = portUnderTest.getRootDirectories();
    assertNotNull(rootDirs, "Root directories list should not be null");
    assertFalse(rootDirs.isEmpty(), "Root directories list should not be empty");
  }
}
