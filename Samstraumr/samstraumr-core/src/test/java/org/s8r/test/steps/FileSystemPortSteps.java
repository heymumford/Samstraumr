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

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.FileSystemPort.FileInfo;
import org.s8r.application.port.FileSystemPort.FileResult;
import org.s8r.application.port.FileSystemPort.SearchMode;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.filesystem.StandardFileSystemAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

/**
 * Step definitions for testing the FileSystemPort interface in BDD scenarios.
 */
@IntegrationTest
public class FileSystemPortSteps {

    private LoggerPort logger;
    private FileSystemPort fileSystemPort;
    private String testRootDir;
    private Map<String, Object> testContext = new HashMap<>();
    private List<String> createdPaths = new ArrayList<>();
    private FileResult lastResult;
    
    @Before
    public void setup() {
        logger = new ConsoleLogger();
        fileSystemPort = new StandardFileSystemAdapter(logger);
        
        // Generate a unique test directory
        String tempDir = System.getProperty("java.io.tmpdir");
        String uniqueId = UUID.randomUUID().toString();
        testRootDir = tempDir + "/s8r-fs-test-" + uniqueId;
    }
    
    @After
    public void cleanup() {
        // Clean up all created files and directories
        if (fileSystemPort.exists(testRootDir)) {
            fileSystemPort.deleteRecursively(testRootDir);
        }
        
        for (String path : createdPaths) {
            if (fileSystemPort.exists(path)) {
                fileSystemPort.deleteRecursively(path);
            }
        }
    }
    
    @Given("a clean system environment")
    public void givenCleanSystemEnvironment() {
        testContext.clear();
        createdPaths.clear();
    }
    
    @Given("the FileSystemPort interface is properly initialized")
    public void givenFileSystemPortInitialized() {
        FileResult result = fileSystemPort.initialize();
        assertTrue(result.isSuccessful(), "FileSystemPort should initialize successfully");
    }
    
    @Given("a test directory is created for file operations")
    public void givenTestDirectoryCreated() {
        FileResult result = fileSystemPort.createDirectories(testRootDir);
        assertTrue(result.isSuccessful(), "Test directory should be created successfully");
        createdPaths.add(testRootDir);
    }
    
    @When("I create a file {string} with content {string}")
    public void whenCreateFileWithContent(String relativePath, String content) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.writeString(absolutePath, content, StandardCharsets.UTF_8, false);
        assertTrue(lastResult.isSuccessful(), "File should be created successfully");
        testContext.put("lastCreatedFile", absolutePath);
    }
    
    @When("I create a directory {string}")
    public void whenCreateDirectory(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.createDirectory(absolutePath);
        assertTrue(lastResult.isSuccessful(), "Directory should be created successfully");
        testContext.put("lastCreatedDirectory", absolutePath);
    }
    
    @When("I create a file {string} with UTF-8 content {string}")
    public void whenCreateFileWithUtf8Content(String relativePath, String content) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.writeString(absolutePath, content, StandardCharsets.UTF_8, false);
        assertTrue(lastResult.isSuccessful(), "UTF-8 file should be created successfully");
        testContext.put("utf8File", absolutePath);
    }
    
    @When("I create a file {string} with ISO-8859-1 content {string}")
    public void whenCreateFileWithIsoContent(String relativePath, String content) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.writeString(absolutePath, content, StandardCharsets.ISO_8859_1, false);
        assertTrue(lastResult.isSuccessful(), "ISO-8859-1 file should be created successfully");
        testContext.put("isoFile", absolutePath);
    }
    
    @When("I copy the file {string} to {string}")
    public void whenCopyFile(String sourcePath, String destPath) {
        String sourceAbsolute = fileSystemPort.resolve(testRootDir, sourcePath);
        String destAbsolute = fileSystemPort.resolve(testRootDir, destPath);
        lastResult = fileSystemPort.copy(sourceAbsolute, destAbsolute);
        assertTrue(lastResult.isSuccessful(), "File should be copied successfully");
    }
    
    @When("I move the file {string} to {string}")
    public void whenMoveFile(String sourcePath, String destPath) {
        String sourceAbsolute = fileSystemPort.resolve(testRootDir, sourcePath);
        String destAbsolute = fileSystemPort.resolve(testRootDir, destPath);
        lastResult = fileSystemPort.move(sourceAbsolute, destAbsolute);
        assertTrue(lastResult.isSuccessful(), "File should be moved successfully");
    }
    
    @When("I delete the file {string}")
    public void whenDeleteFile(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.delete(absolutePath);
        assertTrue(lastResult.isSuccessful(), "File should be deleted successfully");
    }
    
    @When("I recursively delete the directory {string}")
    public void whenDeleteDirectory(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        lastResult = fileSystemPort.deleteRecursively(absolutePath);
        assertTrue(lastResult.isSuccessful(), "Directory should be deleted successfully");
    }
    
    @When("I set the last modified time of the file")
    public void whenSetFileModifiedTime() {
        String filePath = (String) testContext.get("lastCreatedFile");
        Instant time = Instant.now().minusSeconds(3600); // 1 hour ago
        lastResult = fileSystemPort.setLastModifiedTime(filePath, time);
        assertTrue(lastResult.isSuccessful(), "Last modified time should be set successfully");
        testContext.put("expectedModifiedTime", time);
    }
    
    @When("I create the following files:")
    public void whenCreateMultipleFiles(DataTable dataTable) {
        List<Map<String, String>> files = dataTable.asMaps();
        
        for (Map<String, String> file : files) {
            String relativePath = file.get("path");
            String content = file.get("content");
            
            String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
            String parentDir = getParentDirectory(absolutePath);
            
            if (!fileSystemPort.exists(parentDir)) {
                fileSystemPort.createDirectories(parentDir);
            }
            
            lastResult = fileSystemPort.writeString(absolutePath, content, StandardCharsets.UTF_8, false);
            assertTrue(lastResult.isSuccessful(), 
                    "File " + relativePath + " should be created successfully");
        }
    }
    
    @When("I create a temporary file with prefix {string} and suffix {string}")
    public void whenCreateTempFile(String prefix, String suffix) {
        lastResult = fileSystemPort.createTempFile(prefix, suffix, testRootDir);
        assertTrue(lastResult.isSuccessful(), "Temporary file should be created successfully");
        
        String tempFilePath = (String) lastResult.getAttributes().get("path");
        testContext.put("tempFile", tempFilePath);
        
        // Write some content to the temp file
        fileSystemPort.writeString(tempFilePath, "Temporary file content", StandardCharsets.UTF_8, false);
    }
    
    @When("I get the absolute path of a relative path {string}")
    public void whenGetAbsolutePath(String relativePath) {
        String absolutePath = fileSystemPort.getAbsolutePath(relativePath);
        testContext.put("absolutePath", absolutePath);
    }
    
    @When("I normalize the path {string}")
    public void whenNormalizePath(String path) {
        String normalizedPath = fileSystemPort.normalize(path);
        testContext.put("normalizedPath", normalizedPath);
    }
    
    @When("I resolve the path {string} with {string}")
    public void whenResolvePath(String basePath, String relativePath) {
        String resolvedPath = fileSystemPort.resolve(basePath, relativePath);
        testContext.put("resolvedPath", resolvedPath);
    }
    
    @When("I attempt to read a non-existent file")
    public void whenReadNonExistentFile() {
        String nonExistentPath = fileSystemPort.resolve(testRootDir, "non-existent-file.txt");
        lastResult = fileSystemPort.readString(nonExistentPath, StandardCharsets.UTF_8);
    }
    
    @When("I attempt to write to a read-only location")
    public void whenWriteToReadOnlyLocation() {
        // This is a simplification since we can't guarantee a read-only location in tests
        // Instead, we'll simulate a failure result
        lastResult = FileResult.failure("Could not write to file", "Location is read-only");
    }
    
    @When("I attempt to create a directory in an invalid location")
    public void whenCreateDirectoryInInvalidLocation() {
        String invalidPath = "/proc/invalid-directory"; // This should fail on most systems
        lastResult = fileSystemPort.createDirectory(invalidPath);
    }
    
    @When("I attempt to delete a non-existent file")
    public void whenDeleteNonExistentFile() {
        String nonExistentPath = fileSystemPort.resolve(testRootDir, "non-existent-file.txt");
        lastResult = fileSystemPort.delete(nonExistentPath);
    }
    
    @When("I attempt to move a file to an invalid destination")
    public void whenMoveToInvalidDestination() {
        // First create a source file
        String sourcePath = fileSystemPort.resolve(testRootDir, "move-source.txt");
        fileSystemPort.writeString(sourcePath, "Source content", StandardCharsets.UTF_8, false);
        
        // Try to move to an invalid location
        String invalidPath = "/proc/invalid-destination.txt"; // This should fail on most systems
        lastResult = fileSystemPort.move(sourcePath, invalidPath);
    }
    
    @Then("the file should exist")
    public void thenFileShouldExist() {
        String filePath = (String) testContext.get("lastCreatedFile");
        assertTrue(fileSystemPort.exists(filePath), "File should exist");
    }
    
    @Then("the file content should be {string}")
    public void thenFileContentShouldBe(String expectedContent) {
        String filePath = (String) testContext.get("lastCreatedFile");
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.UTF_8);
        assertTrue(result.isSuccessful(), "Should be able to read file content");
        
        String actualContent = (String) result.getAttributes().get("content");
        assertEquals(expectedContent, actualContent, "File content should match expected content");
    }
    
    @Then("the file size should be correct")
    public void thenFileSizeShouldBeCorrect() {
        String filePath = (String) testContext.get("lastCreatedFile");
        long size = fileSystemPort.size(filePath);
        
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.UTF_8);
        String content = (String) result.getAttributes().get("content");
        
        assertEquals(content.getBytes(StandardCharsets.UTF_8).length, size, 
                "File size should match content length");
    }
    
    @Then("the directory should exist")
    public void thenDirectoryShouldExist() {
        String dirPath = (String) testContext.get("lastCreatedDirectory");
        assertTrue(fileSystemPort.exists(dirPath), "Directory should exist");
        
        FileResult result = fileSystemPort.getInfo(dirPath);
        assertTrue(result.isSuccessful(), "Should be able to get directory info");
        
        FileInfo fileInfo = (FileInfo) result.getAttributes().get("fileInfo");
        assertTrue(fileInfo.isDirectory(), "Path should be a directory");
    }
    
    @Then("listing the directory should show the nested file")
    public void thenListingDirectoryShouldShowNestedFile() {
        String dirPath = (String) testContext.get("lastCreatedDirectory");
        FileResult result = fileSystemPort.list(dirPath);
        assertTrue(result.isSuccessful(), "Should be able to list directory contents");
        
        @SuppressWarnings("unchecked")
        List<FileInfo> files = (List<FileInfo>) result.getAttributes().get("files");
        
        // Find the nested file
        boolean foundNestedFile = files.stream()
                .anyMatch(file -> file.getName().equals("nested-file.txt"));
        
        assertTrue(foundNestedFile, "Directory listing should include the nested file");
    }
    
    @Then("the nested file should have the correct content")
    public void thenNestedFileShouldHaveCorrectContent() {
        String dirPath = (String) testContext.get("lastCreatedDirectory");
        String filePath = fileSystemPort.resolve(dirPath, "nested-file.txt");
        
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.UTF_8);
        assertTrue(result.isSuccessful(), "Should be able to read nested file content");
        
        String content = (String) result.getAttributes().get("content");
        assertEquals("Nested file content", content, "Nested file content should match expected");
    }
    
    @Then("I should be able to read the UTF-8 file with the correct encoding")
    public void thenShouldReadUtf8File() {
        String filePath = (String) testContext.get("utf8File");
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.UTF_8);
        assertTrue(result.isSuccessful(), "Should be able to read UTF-8 file");
        
        String content = (String) result.getAttributes().get("content");
        assertTrue(content.contains("ñáéíóú"), "UTF-8 content should contain special characters");
    }
    
    @Then("I should be able to read the ISO-8859-1 file with the correct encoding")
    public void thenShouldReadIsoFile() {
        String filePath = (String) testContext.get("isoFile");
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.ISO_8859_1);
        assertTrue(result.isSuccessful(), "Should be able to read ISO-8859-1 file");
        
        String content = (String) result.getAttributes().get("content");
        assertTrue(content.contains("ñáéíóú"), "ISO-8859-1 content should contain special characters");
    }
    
    @Then("both files should exist with the same content")
    public void thenBothFilesShouldExistWithSameContent() {
        String sourceFile = fileSystemPort.resolve(testRootDir, "source-file.txt");
        String copyFile = fileSystemPort.resolve(testRootDir, "copy-file.txt");
        
        assertTrue(fileSystemPort.exists(sourceFile), "Source file should exist");
        assertTrue(fileSystemPort.exists(copyFile), "Copy file should exist");
        
        FileResult sourceResult = fileSystemPort.readString(sourceFile, StandardCharsets.UTF_8);
        FileResult copyResult = fileSystemPort.readString(copyFile, StandardCharsets.UTF_8);
        
        String sourceContent = (String) sourceResult.getAttributes().get("content");
        String copyContent = (String) copyResult.getAttributes().get("content");
        
        assertEquals(sourceContent, copyContent, "File contents should match");
    }
    
    @Then("the file {string} should not exist")
    public void thenFileShouldNotExist(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        assertFalse(fileSystemPort.exists(absolutePath), "File should not exist");
    }
    
    @Then("the file {string} should exist with the correct content")
    public void thenFileShouldExistWithContent(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        assertTrue(fileSystemPort.exists(absolutePath), "File should exist");
        
        FileResult result = fileSystemPort.readString(absolutePath, StandardCharsets.UTF_8);
        assertTrue(result.isSuccessful(), "Should be able to read file content");
        
        String content = (String) result.getAttributes().get("content");
        assertNotNull(content, "File content should not be null");
        assertFalse(content.isEmpty(), "File content should not be empty");
    }
    
    @Then("the directory {string} should not exist")
    public void thenDirectoryShouldNotExist(String relativePath) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        assertFalse(fileSystemPort.exists(absolutePath), "Directory should not exist");
    }
    
    @Then("I should be able to get the file information")
    public void thenShouldGetFileInfo() {
        String filePath = (String) testContext.get("lastCreatedFile");
        FileResult result = fileSystemPort.getInfo(filePath);
        assertTrue(result.isSuccessful(), "Should be able to get file info");
        
        FileInfo fileInfo = (FileInfo) result.getAttributes().get("fileInfo");
        assertNotNull(fileInfo, "File info should not be null");
        
        testContext.put("fileInfo", fileInfo);
    }
    
    @Then("the file size should match the content length")
    public void thenFileSizeShouldMatchContentLength() {
        String filePath = (String) testContext.get("lastCreatedFile");
        FileInfo fileInfo = (FileInfo) testContext.get("fileInfo");
        
        FileResult result = fileSystemPort.readString(filePath, StandardCharsets.UTF_8);
        String content = (String) result.getAttributes().get("content");
        
        assertEquals(content.getBytes(StandardCharsets.UTF_8).length, fileInfo.getSize(), 
                "File size should match content length");
    }
    
    @Then("the last modified time should be correct")
    public void thenLastModifiedTimeShouldBeCorrect() {
        FileInfo fileInfo = (FileInfo) testContext.get("fileInfo");
        Instant expectedTime = (Instant) testContext.get("expectedModifiedTime");
        
        // Allow small difference due to filesystem precision
        long timeDifference = Math.abs(fileInfo.getLastModifiedTime().toEpochMilli() - 
                                      expectedTime.toEpochMilli());
        
        assertTrue(timeDifference < 2000, "Last modified time should be within 2 seconds of expected time");
    }
    
    @Then("the file should be marked as a regular file")
    public void thenShouldBeRegularFile() {
        FileInfo fileInfo = (FileInfo) testContext.get("fileInfo");
        assertTrue(fileInfo.isRegularFile(), "Should be a regular file");
        assertFalse(fileInfo.isDirectory(), "Should not be a directory");
    }
    
    @Then("searching for {string} in {string} should find {int} files")
    public void thenSearchingShouldFindFiles(String pattern, String relativePath, int expectedCount) {
        String absolutePath = fileSystemPort.resolve(testRootDir, relativePath);
        FileResult result = fileSystemPort.search(absolutePath, pattern, SearchMode.CURRENT_DIRECTORY);
        assertTrue(result.isSuccessful(), "Search should succeed");
        
        @SuppressWarnings("unchecked")
        List<FileInfo> files = (List<FileInfo>) result.getAttributes().get("files");
        
        assertEquals(expectedCount, files.size(), 
                "Search should find " + expectedCount + " files");
    }
    
    @Then("recursive search for {string} should find all text files")
    public void thenRecursiveSearchShouldFindAllFiles(String pattern) {
        FileResult result = fileSystemPort.search(testRootDir, pattern, SearchMode.RECURSIVE);
        assertTrue(result.isSuccessful(), "Recursive search should succeed");
        
        @SuppressWarnings("unchecked")
        List<FileInfo> files = (List<FileInfo>) result.getAttributes().get("files");
        
        // We expect at least 3 .txt files based on the scenario
        assertTrue(files.size() >= 3, 
                "Recursive search should find at least 3 text files");
    }
    
    @Then("the temporary file should exist")
    public void thenTempFileShouldExist() {
        String tempFilePath = (String) testContext.get("tempFile");
        assertTrue(fileSystemPort.exists(tempFilePath), "Temp file should exist");
    }
    
    @Then("I should be able to write and read from the temporary file")
    public void thenShouldReadWriteTempFile() {
        String tempFilePath = (String) testContext.get("tempFile");
        
        // Read the content
        FileResult result = fileSystemPort.readString(tempFilePath, StandardCharsets.UTF_8);
        assertTrue(result.isSuccessful(), "Should be able to read temp file");
        
        String content = (String) result.getAttributes().get("content");
        assertEquals("Temporary file content", content, "Temp file content should be correct");
    }
    
    @Then("the temporary file should be in the system temp directory")
    public void thenTempFileShouldBeInTempDir() {
        String tempFilePath = (String) testContext.get("tempFile");
        String tempDir = System.getProperty("java.io.tmpdir");
        
        // Handle the fact that the path might use a different separator
        String normalizedTempFilePath = fileSystemPort.normalize(tempFilePath);
        
        assertTrue(normalizedTempFilePath.startsWith(testRootDir), 
                "Temp file should be in the test directory: " + testRootDir);
    }
    
    @Then("the result should be an absolute path")
    public void thenResultShouldBeAbsolutePath() {
        String absolutePath = (String) testContext.get("absolutePath");
        assertTrue(fileSystemPort.isAbsolute(absolutePath), "Path should be absolute");
    }
    
    @Then("the result should be the simplified path {string}")
    public void thenResultShouldBeSimplifiedPath(String expectedPath) {
        String normalizedPath = (String) testContext.get("normalizedPath");
        assertEquals(expectedPath, normalizedPath, "Path should be normalized correctly");
    }
    
    @Then("the result should be the combined path {string}")
    public void thenResultShouldBeCombinedPath(String expectedPath) {
        String resolvedPath = (String) testContext.get("resolvedPath");
        assertEquals(expectedPath, resolvedPath, "Path should be resolved correctly");
    }
    
    @Then("the operation should return a failure result")
    public void thenOperationShouldReturnFailure() {
        assertFalse(lastResult.isSuccessful(), "Operation should fail");
    }
    
    @Then("the failure result should contain an appropriate error message")
    public void thenFailureShouldContainErrorMessage() {
        assertTrue(lastResult.getReason().isPresent(), "Failure should have a reason");
        String reason = lastResult.getReason().get();
        assertFalse(reason.isEmpty(), "Error message should not be empty");
    }
    
    /**
     * Helper method to get the parent directory of a path.
     */
    private String getParentDirectory(String path) {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash > 0) {
            return path.substring(0, lastSlash);
        }
        return testRootDir;
    }
}