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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;

/** Test for the BufferedFileSystemAdapter. */
public class BufferedFileSystemAdapterTest {

  @TempDir Path tempDir;

  private BufferedFileSystemAdapter fileSystem;
  private TestLogger logger;

  @BeforeEach
  void setUp() {
    logger = new TestLogger();
    fileSystem = new BufferedFileSystemAdapter(logger);
  }

  @AfterEach
  void tearDown() throws Exception {
    fileSystem.shutdown();
  }

  @Test
  void testReadWriteFile() throws IOException {
    // Create a test file
    Path testFile = tempDir.resolve("test.txt");
    String content = "Hello, world!";

    // Write the file
    assertTrue(fileSystem.writeFile(testFile, content));

    // Read the file
    Optional<String> readContent = fileSystem.readFile(testFile);
    assertTrue(readContent.isPresent());
    assertEquals(content, readContent.get());

    // Check if file exists
    assertTrue(fileSystem.fileExists(testFile));
  }

  @Test
  void testCacheEffectiveness() throws IOException {
    // Create a test file
    Path testFile = tempDir.resolve("cache-test.txt");
    String content = "Content to cache";

    // Write the file
    assertTrue(fileSystem.writeFile(testFile, content));

    // Read the file multiple times to test caching
    for (int i = 0; i < 5; i++) {
      Optional<String> readContent = fileSystem.readFile(testFile);
      assertTrue(readContent.isPresent());
      assertEquals(content, readContent.get());
    }

    // Check cache metrics
    Map<String, Object> metrics = fileSystem.getPerformanceMetrics();

    // First read should be a cache miss, subsequent reads should be cache hits
    assertTrue((Long) metrics.get("cacheHits") >= 4, "Expected at least 4 cache hits");
    assertTrue((Long) metrics.get("cacheMisses") >= 1, "Expected at least 1 cache miss");
    assertEquals(5, (Long) metrics.get("totalReads"), "Expected 5 total reads");

    // Reset metrics and verify
    fileSystem.resetPerformanceMetrics();
    metrics = fileSystem.getPerformanceMetrics();
    assertEquals(0, (Long) metrics.get("cacheHits"));
    assertEquals(0, (Long) metrics.get("cacheMisses"));
    assertEquals(0, (Long) metrics.get("totalReads"));
  }

  @Test
  void testCreateDirectory() throws IOException {
    Path dirPath = tempDir.resolve("test-dir");

    // Create directory
    assertTrue(fileSystem.createDirectory(dirPath));

    // Verify directory exists
    assertTrue(Files.exists(dirPath));
    assertTrue(Files.isDirectory(dirPath));
  }

  @Test
  void testListFiles() throws IOException {
    // Create some test files
    Path dir = tempDir.resolve("list-dir");
    fileSystem.createDirectory(dir);

    Path file1 = dir.resolve("file1.txt");
    Path file2 = dir.resolve("file2.txt");
    Path file3 = dir.resolve("file3.txt");

    fileSystem.writeFile(file1, "Content 1");
    fileSystem.writeFile(file2, "Content 2");
    fileSystem.writeFile(file3, "Content 3");

    // List files
    List<Path> files = fileSystem.listFiles(dir);

    // Verify
    assertEquals(3, files.size());
    assertTrue(files.stream().anyMatch(f -> f.getFileName().toString().equals("file1.txt")));
    assertTrue(files.stream().anyMatch(f -> f.getFileName().toString().equals("file2.txt")));
    assertTrue(files.stream().anyMatch(f -> f.getFileName().toString().equals("file3.txt")));
  }

  @Test
  void testDelete() throws IOException {
    // Create a test file
    Path testFile = tempDir.resolve("delete-test.txt");
    fileSystem.writeFile(testFile, "Delete me");

    // Verify file exists
    assertTrue(Files.exists(testFile));

    // Delete the file
    assertTrue(fileSystem.delete(testFile));

    // Verify file no longer exists
    assertFalse(Files.exists(testFile));
  }

  @Test
  void testCopyFile() throws IOException {
    // Create a source file
    Path sourceFile = tempDir.resolve("source.txt");
    String content = "Content to copy";
    fileSystem.writeFile(sourceFile, content);

    // Copy to destination
    Path destFile = tempDir.resolve("destination.txt");
    FileSystemPort.FileResult result = fileSystem.copy(sourceFile.toString(), destFile.toString());

    // Verify result
    assertTrue(result.isSuccessful());

    // Verify destination file contents
    Optional<String> readContent = fileSystem.readFile(destFile);
    assertTrue(readContent.isPresent());
    assertEquals(content, readContent.get());
  }

  /** Simple logger implementation for testing. */
  private static class TestLogger implements LoggerPort {
    @Override
    public void trace(String message) {
      // No-op for test
    }

    @Override
    public void trace(String format, Object... args) {
      // No-op for test
    }

    @Override
    public void debug(String message) {
      // No-op for test
    }

    @Override
    public void debug(String format, Object... args) {
      // No-op for test
    }

    @Override
    public void info(String message) {
      // No-op for test
    }

    @Override
    public void info(String format, Object... args) {
      // No-op for test
    }

    @Override
    public void warn(String message) {
      // No-op for test
    }

    @Override
    public void warn(String format, Object... args) {
      // No-op for test
    }

    @Override
    public void error(String message) {
      // No-op for test
    }

    @Override
    public void error(String format, Object... args) {
      // No-op for test
    }

    @Override
    public void error(String message, Throwable throwable) {
      // No-op for test
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public boolean isDebugEnabled() {
      return true;
    }

    @Override
    public String getName() {
      return "TestLogger";
    }
  }
}
