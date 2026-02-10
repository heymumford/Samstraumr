/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

@UnitTest
public class FileSystemServiceTest {

  private FileSystemPort mockFileSystemPort;
  private LoggerPort mockLogger;
  private FileSystemService service;

  @BeforeEach
  void setUp() {
    mockFileSystemPort = mock(FileSystemPort.class);
    mockLogger = mock(LoggerPort.class);
    service = new FileSystemService(mockFileSystemPort, mockLogger);
  }

  @Test
  void initialize_shouldCallPortInitialize() {
    // Arrange
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Initialized successfully");
    when(mockFileSystemPort.initialize()).thenReturn(successResult);

    // Act
    service.initialize();

    // Assert
    verify(mockFileSystemPort).initialize();
    verify(mockLogger).info(anyString());
    verify(mockLogger, never()).error(anyString(), anyString());
  }

  @Test
  void initialize_shouldLogErrorOnFailure() {
    // Arrange
    FileSystemPort.FileResult failResult =
        FileSystemPort.FileResult.failure("Initialization failed", "Test error");
    when(mockFileSystemPort.initialize()).thenReturn(failResult);

    // Act
    service.initialize();

    // Assert
    verify(mockFileSystemPort).initialize();
    verify(mockLogger).error(contains("Failed to initialize"), eq("Test error"));
  }

  @Test
  void fileExists_shouldDelegateToPort() {
    // Arrange
    String path = "/test/path.txt";
    when(mockFileSystemPort.exists(path)).thenReturn(true);

    // Act
    boolean result = service.fileExists(path);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).exists(path);
  }

  @Test
  void createDirectory_shouldReturnTrueOnSuccess() {
    // Arrange
    String path = "/test/new/dir";
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Directory created");
    when(mockFileSystemPort.createDirectories(path)).thenReturn(successResult);

    // Act
    boolean result = service.createDirectory(path);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).createDirectories(path);
  }

  @Test
  void createDirectory_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String path = "/test/invalid/dir";
    FileSystemPort.FileResult failResult =
        FileSystemPort.FileResult.failure("Failed to create", "Access denied");
    when(mockFileSystemPort.createDirectories(path)).thenReturn(failResult);

    // Act
    boolean result = service.createDirectory(path);

    // Assert
    assertFalse(result);
    verify(mockFileSystemPort).createDirectories(path);
    verify(mockLogger).warn(contains("Failed to create directory"), eq(path), eq("Access denied"));
  }

  @Test
  void deleteFile_shouldCallPortDeleteForNonRecursive() {
    // Arrange
    String path = "/test/file.txt";
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("File deleted");
    when(mockFileSystemPort.delete(path)).thenReturn(successResult);

    // Act
    boolean result = service.deleteFile(path, false);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).delete(path);
    verify(mockFileSystemPort, never()).deleteRecursively(anyString());
  }

  @Test
  void deleteFile_shouldCallPortDeleteRecursivelyForRecursive() {
    // Arrange
    String path = "/test/dir";
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Directory deleted");
    when(mockFileSystemPort.deleteRecursively(path)).thenReturn(successResult);

    // Act
    boolean result = service.deleteFile(path, true);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).deleteRecursively(path);
    verify(mockFileSystemPort, never()).delete(anyString());
  }

  @Test
  void readFile_shouldReturnContentOnSuccess() {
    // Arrange
    String path = "/test/file.txt";
    String content = "File content";
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("content", content);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Read successfully", attrs);
    when(mockFileSystemPort.readString(eq(path), any(Charset.class))).thenReturn(successResult);

    // Act
    Optional<String> result = service.readFile(path);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(content, result.get());
    verify(mockFileSystemPort).readString(path, StandardCharsets.UTF_8);
  }

  @Test
  void readFile_shouldReturnEmptyOnFailure() {
    // Arrange
    String path = "/test/nonexistent.txt";
    FileSystemPort.FileResult failResult =
        FileSystemPort.FileResult.failure("Failed to read", "File not found");
    when(mockFileSystemPort.readString(eq(path), any(Charset.class))).thenReturn(failResult);

    // Act
    Optional<String> result = service.readFile(path);

    // Assert
    assertFalse(result.isPresent());
    verify(mockFileSystemPort).readString(path, StandardCharsets.UTF_8);
    verify(mockLogger).warn(contains("Failed to read file"), eq(path), eq("File not found"));
  }

  @Test
  void readFile_shouldUseProvidedCharset() {
    // Arrange
    String path = "/test/file.txt";
    Charset charset = StandardCharsets.ISO_8859_1;
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("content", "Content");
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Read successfully", attrs);
    when(mockFileSystemPort.readString(path, charset)).thenReturn(successResult);

    // Act
    service.readFile(path, charset);

    // Assert
    verify(mockFileSystemPort).readString(path, charset);
  }

  @Test
  void readLines_shouldReturnLinesOnSuccess() {
    // Arrange
    String path = "/test/file.txt";
    List<String> lines = List.of("Line 1", "Line 2", "Line 3");
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("lines", lines);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Read successfully", attrs);
    when(mockFileSystemPort.readLines(eq(path), any(Charset.class))).thenReturn(successResult);

    // Act
    Optional<List<String>> result = service.readLines(path);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(lines, result.get());
    verify(mockFileSystemPort).readLines(path, StandardCharsets.UTF_8);
  }

  @Test
  void readLines_withRangeParameters_shouldDelegateCorrectly() {
    // Arrange
    String path = "/test/file.txt";
    List<String> lines = List.of("Line 2", "Line 3");
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("lines", lines);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Read successfully", attrs);
    when(mockFileSystemPort.readLines(eq(path), any(Charset.class), eq(1L), eq(2L)))
        .thenReturn(successResult);

    // Act
    Optional<List<String>> result = service.readLines(path, StandardCharsets.UTF_8, 1, 2);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(lines, result.get());
    verify(mockFileSystemPort).readLines(path, StandardCharsets.UTF_8, 1, 2);
  }

  @Test
  void writeFile_shouldReturnTrueOnSuccess() {
    // Arrange
    String path = "/test/file.txt";
    String content = "File content";
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("Write successful");
    when(mockFileSystemPort.writeString(eq(path), eq(content), any(Charset.class), eq(false)))
        .thenReturn(successResult);

    // Act
    boolean result = service.writeFile(path, content);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).writeString(path, content, StandardCharsets.UTF_8, false);
  }

  @Test
  void writeFile_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String path = "/test/nonwritable.txt";
    String content = "File content";
    FileSystemPort.FileResult failResult =
        FileSystemPort.FileResult.failure("Failed to write", "Access denied");
    when(mockFileSystemPort.writeString(eq(path), eq(content), any(Charset.class), eq(false)))
        .thenReturn(failResult);

    // Act
    boolean result = service.writeFile(path, content);

    // Assert
    assertFalse(result);
    verify(mockFileSystemPort).writeString(path, content, StandardCharsets.UTF_8, false);
    verify(mockLogger).warn(contains("Failed to write to file"), eq(path), eq("Access denied"));
  }

  @Test
  void writeLines_shouldDelegateCorrectly() {
    // Arrange
    String path = "/test/file.txt";
    List<String> lines = List.of("Line 1", "Line 2");
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("Write successful");
    when(mockFileSystemPort.writeLines(eq(path), eq(lines), any(Charset.class), eq(false)))
        .thenReturn(successResult);

    // Act
    boolean result = service.writeLines(path, lines);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).writeLines(path, lines, StandardCharsets.UTF_8, false);
  }

  @Test
  void copyFile_shouldReturnTrueOnSuccess() {
    // Arrange
    String source = "/test/source.txt";
    String destination = "/test/destination.txt";
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("Copy successful");
    when(mockFileSystemPort.copy(source, destination)).thenReturn(successResult);

    // Act
    boolean result = service.copyFile(source, destination);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).copy(source, destination);
  }

  @Test
  void copyDirectory_shouldReturnTrueOnSuccess() {
    // Arrange
    String source = "/test/source";
    String destination = "/test/destination";
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("Copy successful");
    when(mockFileSystemPort.copyDirectory(source, destination)).thenReturn(successResult);

    // Act
    boolean result = service.copyDirectory(source, destination);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).copyDirectory(source, destination);
  }

  @Test
  void moveFile_shouldReturnTrueOnSuccess() {
    // Arrange
    String source = "/test/source.txt";
    String destination = "/test/destination.txt";
    FileSystemPort.FileResult successResult = FileSystemPort.FileResult.success("Move successful");
    when(mockFileSystemPort.move(source, destination)).thenReturn(successResult);

    // Act
    boolean result = service.moveFile(source, destination);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).move(source, destination);
  }

  @Test
  void getFileSize_shouldDelegateToPort() {
    // Arrange
    String path = "/test/file.txt";
    long expectedSize = 1024;
    when(mockFileSystemPort.size(path)).thenReturn(expectedSize);

    // Act
    long result = service.getFileSize(path);

    // Assert
    assertEquals(expectedSize, result);
    verify(mockFileSystemPort).size(path);
  }

  @Test
  void getLastModifiedTime_shouldDelegateToPort() {
    // Arrange
    String path = "/test/file.txt";
    Instant now = Instant.now();
    when(mockFileSystemPort.getLastModifiedTime(path)).thenReturn(Optional.of(now));

    // Act
    Optional<Instant> result = service.getLastModifiedTime(path);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(now, result.get());
    verify(mockFileSystemPort).getLastModifiedTime(path);
  }

  @Test
  void setLastModifiedTime_shouldReturnTrueOnSuccess() {
    // Arrange
    String path = "/test/file.txt";
    Instant time = Instant.now();
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Set time successful");
    when(mockFileSystemPort.setLastModifiedTime(path, time)).thenReturn(successResult);

    // Act
    boolean result = service.setLastModifiedTime(path, time);

    // Assert
    assertTrue(result);
    verify(mockFileSystemPort).setLastModifiedTime(path, time);
  }

  @Test
  void createTempFile_shouldReturnPathOnSuccess() {
    // Arrange
    String prefix = "test";
    String suffix = ".tmp";
    String directory = "/tmp";
    String tempPath = "/tmp/test1234.tmp";
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("path", tempPath);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Temp file created", attrs);
    when(mockFileSystemPort.createTempFile(prefix, suffix, directory)).thenReturn(successResult);

    // Act
    Optional<String> result = service.createTempFile(prefix, suffix, directory);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(tempPath, result.get());
    verify(mockFileSystemPort).createTempFile(prefix, suffix, directory);
  }

  @Test
  void createTempDirectory_shouldReturnPathOnSuccess() {
    // Arrange
    String prefix = "testdir";
    String directory = "/tmp";
    String tempDirPath = "/tmp/testdir5678";
    Map<String, Object> attrs = new HashMap<>();
    attrs.put("path", tempDirPath);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Temp directory created", attrs);
    when(mockFileSystemPort.createTempDirectory(prefix, directory)).thenReturn(successResult);

    // Act
    Optional<String> result = service.createTempDirectory(prefix, directory);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(tempDirPath, result.get());
    verify(mockFileSystemPort).createTempDirectory(prefix, directory);
  }

  @Test
  void listFiles_shouldReturnFilesOnSuccess() {
    // Arrange
    String directory = "/test/dir";
    List<FileSystemPort.FileInfo> fileInfos = new ArrayList<>();
    // Add some dummy FileInfo objects
    fileInfos.add(
        FileSystemPort.FileInfo.create(
            "/test/dir/file1.txt",
            false,
            true,
            false,
            100,
            Instant.now(),
            Instant.now(),
            Instant.now(),
            Map.of()));
    fileInfos.add(
        FileSystemPort.FileInfo.create(
            "/test/dir/file2.txt",
            false,
            true,
            false,
            200,
            Instant.now(),
            Instant.now(),
            Instant.now(),
            Map.of()));

    Map<String, Object> attrs = new HashMap<>();
    attrs.put("files", fileInfos);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Directory listed", attrs);
    when(mockFileSystemPort.list(directory)).thenReturn(successResult);

    // Act
    Optional<List<FileSystemPort.FileInfo>> result = service.listFiles(directory);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(2, result.get().size());
    verify(mockFileSystemPort).list(directory);
  }

  @Test
  void searchFiles_shouldUseCorrectSearchMode() {
    // Arrange
    String directory = "/test/dir";
    String pattern = "*.txt";
    List<FileSystemPort.FileInfo> fileInfos = new ArrayList<>();
    // Add a dummy FileInfo object
    fileInfos.add(
        FileSystemPort.FileInfo.create(
            "/test/dir/file1.txt",
            false,
            true,
            false,
            100,
            Instant.now(),
            Instant.now(),
            Instant.now(),
            Map.of()));

    Map<String, Object> attrs = new HashMap<>();
    attrs.put("files", fileInfos);
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Search completed", attrs);

    // Test with recursive = true
    when(mockFileSystemPort.search(
            eq(directory), eq(pattern), eq(FileSystemPort.SearchMode.RECURSIVE)))
        .thenReturn(successResult);

    // Act - Recursive
    Optional<List<FileSystemPort.FileInfo>> recursiveResult =
        service.searchFiles(directory, pattern, true);

    // Assert - Recursive
    assertTrue(recursiveResult.isPresent());
    assertEquals(1, recursiveResult.get().size());
    verify(mockFileSystemPort).search(directory, pattern, FileSystemPort.SearchMode.RECURSIVE);

    // Test with recursive = false
    when(mockFileSystemPort.search(
            eq(directory), eq(pattern), eq(FileSystemPort.SearchMode.CURRENT_DIRECTORY)))
        .thenReturn(successResult);

    // Act - Current directory
    Optional<List<FileSystemPort.FileInfo>> currentResult =
        service.searchFiles(directory, pattern, false);

    // Assert - Current directory
    assertTrue(currentResult.isPresent());
    verify(mockFileSystemPort)
        .search(directory, pattern, FileSystemPort.SearchMode.CURRENT_DIRECTORY);
  }

  @Test
  void getWorkingDirectory_shouldDelegateToPort() {
    // Arrange
    String expectedDir = "/home/user";
    when(mockFileSystemPort.getWorkingDirectory()).thenReturn(expectedDir);

    // Act
    String result = service.getWorkingDirectory();

    // Assert
    assertEquals(expectedDir, result);
    verify(mockFileSystemPort).getWorkingDirectory();
  }

  @Test
  void normalizePath_shouldDelegateToPort() {
    // Arrange
    String path = "/test/subdir/../file.txt";
    String normalizedPath = "/test/file.txt";
    when(mockFileSystemPort.normalize(path)).thenReturn(normalizedPath);

    // Act
    String result = service.normalizePath(path);

    // Assert
    assertEquals(normalizedPath, result);
    verify(mockFileSystemPort).normalize(path);
  }

  @Test
  void resolvePath_shouldDelegateToPort() {
    // Arrange
    String base = "/test";
    String path = "subdir/file.txt";
    String resolvedPath = "/test/subdir/file.txt";
    when(mockFileSystemPort.resolve(base, path)).thenReturn(resolvedPath);

    // Act
    String result = service.resolvePath(base, path);

    // Assert
    assertEquals(resolvedPath, result);
    verify(mockFileSystemPort).resolve(base, path);
  }

  @Test
  void getAbsolutePath_shouldDelegateToPort() {
    // Arrange
    String path = "relative/path.txt";
    String absolutePath = "/home/user/relative/path.txt";
    when(mockFileSystemPort.getAbsolutePath(path)).thenReturn(absolutePath);

    // Act
    String result = service.getAbsolutePath(path);

    // Assert
    assertEquals(absolutePath, result);
    verify(mockFileSystemPort).getAbsolutePath(path);
  }

  @Test
  void shutdown_shouldCallPortShutdown() {
    // Arrange
    FileSystemPort.FileResult successResult =
        FileSystemPort.FileResult.success("Shutdown successful");
    when(mockFileSystemPort.shutdown()).thenReturn(successResult);

    // Act
    service.shutdown();

    // Assert
    verify(mockFileSystemPort).shutdown();
    verify(mockLogger).info(anyString());
    verify(mockLogger, never()).error(anyString(), anyString());
  }

  @Test
  void shutdown_shouldLogErrorOnFailure() {
    // Arrange
    FileSystemPort.FileResult failResult =
        FileSystemPort.FileResult.failure("Shutdown failed", "Test error");
    when(mockFileSystemPort.shutdown()).thenReturn(failResult);

    // Act
    service.shutdown();

    // Assert
    verify(mockFileSystemPort).shutdown();
    verify(mockLogger).error(contains("Failed to shut down"), eq("Test error"));
  }
}
