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
package org.s8r.infrastructure.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.FileSystemPort.FileResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Tests for the StandardFileSystemAdapter.
 */
class StandardFileSystemAdapterTest {

    private StandardFileSystemAdapter adapter;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        adapter = new StandardFileSystemAdapter();
    }
    
    @Test
    @DisplayName("Should write and read a file")
    void testWriteAndReadFile() throws IOException {
        Path filePath = tempDir.resolve("test-file.txt");
        String content = "Hello, World!";
        
        // Write file
        boolean writeResult = adapter.writeFile(filePath, content);
        assertTrue(writeResult, "Writing a file should succeed");
        assertTrue(Files.exists(filePath), "File should exist after writing");
        
        // Read file
        Optional<String> readResult = adapter.readFile(filePath);
        assertTrue(readResult.isPresent(), "Reading an existing file should return a non-empty Optional");
        assertEquals(content, readResult.get(), "Read content should match written content");
    }
    
    @Test
    @DisplayName("Should return empty Optional for non-existent file")
    void testReadNonExistentFile() throws IOException {
        Path nonExistentPath = tempDir.resolve("non-existent-file.txt");
        
        Optional<String> result = adapter.readFile(nonExistentPath);
        assertFalse(result.isPresent(), "Reading a non-existent file should return an empty Optional");
    }
    
    @Test
    @DisplayName("Should check if file exists")
    void testFileExists() throws IOException {
        Path filePath = tempDir.resolve("exists-test.txt");
        Path nonExistentPath = tempDir.resolve("does-not-exist.txt");
        
        // Create a file
        Files.writeString(filePath, "Content");
        
        assertTrue(adapter.fileExists(filePath), "Should return true for existing file");
        assertFalse(adapter.fileExists(nonExistentPath), "Should return false for non-existent file");
    }
    
    @Test
    @DisplayName("Should create a directory")
    void testCreateDirectory() throws IOException {
        Path dirPath = tempDir.resolve("test-dir");
        
        boolean result = adapter.createDirectory(dirPath);
        assertTrue(result, "Creating a directory should succeed");
        assertTrue(Files.exists(dirPath), "Directory should exist after creation");
        assertTrue(Files.isDirectory(dirPath), "Created path should be a directory");
    }
    
    @Test
    @DisplayName("Should create a directory with string path")
    void testCreateDirectoryWithStringPath() {
        String dirPath = tempDir.resolve("test-string-dir").toString();
        
        FileResult result = adapter.createDirectory(dirPath);
        assertTrue(result.isSuccessful(), "Creating a directory with string path should succeed");
        assertEquals("Directory created successfully", result.getMessage());
        assertTrue(Files.exists(Path.of(dirPath)), "Directory should exist after creation");
    }
    
    @Test
    @DisplayName("Should create nested directories")
    void testCreateNestedDirectories() throws IOException {
        Path dirPath = tempDir.resolve("parent/child/grandchild");
        
        boolean result = adapter.createDirectory(dirPath);
        assertTrue(result, "Creating nested directories should succeed");
        assertTrue(Files.exists(dirPath), "Nested directory should exist after creation");
    }
    
    @Test
    @DisplayName("Should list files in a directory")
    void testListFiles() throws IOException {
        // Create test files and directories
        Path dir = tempDir.resolve("list-test-dir");
        Files.createDirectory(dir);
        
        Path file1 = dir.resolve("file1.txt");
        Path file2 = dir.resolve("file2.txt");
        Path subdir = dir.resolve("subdir");
        
        Files.writeString(file1, "Content 1");
        Files.writeString(file2, "Content 2");
        Files.createDirectory(subdir);
        
        // List files
        List<Path> files = adapter.listFiles(dir);
        
        assertEquals(3, files.size(), "Should list all files and directories");
        assertTrue(files.contains(file1), "Should include file1");
        assertTrue(files.contains(file2), "Should include file2");
        assertTrue(files.contains(subdir), "Should include subdirectory");
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent directory")
    void testListFilesNonExistentDirectory() throws IOException {
        Path nonExistentDir = tempDir.resolve("non-existent-dir");
        
        List<Path> files = adapter.listFiles(nonExistentDir);
        assertTrue(files.isEmpty(), "Should return empty list for non-existent directory");
    }
    
    @Test
    @DisplayName("Should delete a file")
    void testDeleteFile() throws IOException {
        Path filePath = tempDir.resolve("delete-test.txt");
        Files.writeString(filePath, "Delete me");
        
        assertTrue(Files.exists(filePath), "File should exist before deletion");
        
        boolean result = adapter.delete(filePath);
        assertTrue(result, "Deleting a file should succeed");
        assertFalse(Files.exists(filePath), "File should not exist after deletion");
    }
    
    @Test
    @DisplayName("Should delete an empty directory")
    void testDeleteEmptyDirectory() throws IOException {
        Path dirPath = tempDir.resolve("empty-dir");
        Files.createDirectory(dirPath);
        
        assertTrue(Files.exists(dirPath), "Directory should exist before deletion");
        
        boolean result = adapter.delete(dirPath);
        assertTrue(result, "Deleting an empty directory should succeed");
        assertFalse(Files.exists(dirPath), "Directory should not exist after deletion");
    }
    
    @Test
    @DisplayName("Should delete a directory with content")
    void testDeleteDirectoryWithContent() throws IOException {
        // Create a directory with content
        Path dirPath = tempDir.resolve("dir-with-content");
        Path filePath = dirPath.resolve("file.txt");
        Path subdirPath = dirPath.resolve("subdir");
        Path subfilePath = subdirPath.resolve("subfile.txt");
        
        Files.createDirectory(dirPath);
        Files.writeString(filePath, "Content");
        Files.createDirectory(subdirPath);
        Files.writeString(subfilePath, "Subcontent");
        
        assertTrue(Files.exists(dirPath), "Directory should exist before deletion");
        assertTrue(Files.exists(filePath), "File should exist before deletion");
        assertTrue(Files.exists(subdirPath), "Subdirectory should exist before deletion");
        assertTrue(Files.exists(subfilePath), "Subfile should exist before deletion");
        
        boolean result = adapter.delete(dirPath);
        assertTrue(result, "Deleting a directory with content should succeed");
        assertFalse(Files.exists(dirPath), "Directory should not exist after deletion");
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existent path")
    void testDeleteNonExistentPath() throws IOException {
        Path nonExistentPath = tempDir.resolve("non-existent-path");
        
        boolean result = adapter.delete(nonExistentPath);
        assertFalse(result, "Deleting a non-existent path should return false");
    }
}