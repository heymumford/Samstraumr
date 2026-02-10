/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.filesystem;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.test.annotation.UnitTest;

@UnitTest
public class StandardFileSystemAdapterTest {

  private LoggerPort mockLogger;
  private StandardFileSystemAdapter adapter;
  private Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    mockLogger = mock(LoggerPort.class);
    adapter = new StandardFileSystemAdapter(mockLogger);
    tempDir = Files.createTempDirectory("fsadapter-test");
    adapter.initialize();
  }

  @AfterEach
  void tearDown() throws IOException {
    if (tempDir != null && Files.exists(tempDir)) {
      Files.walk(tempDir)
          .sorted(Comparator.reverseOrder())
          .forEach(
              path -> {
                try {
                  Files.delete(path);
                } catch (IOException e) {
                  // Ignore cleanup errors
                }
              });
    }
  }

  @Test
  void initialize_shouldReturnSuccess() {
    // Act
    FileSystemPort.FileResult result = adapter.initialize();

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals("File system adapter initialized successfully", result.getMessage());
  }

  @Test
  void exists_shouldReturnTrueForExistingFile() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("test.txt"));

    // Act
    boolean result = adapter.exists(testFile.toString());

    // Assert
    assertTrue(result);
  }

  @Test
  void exists_shouldReturnFalseForNonExistingFile() {
    // Arrange
    String nonExistingPath = tempDir.resolve("non-existing.txt").toString();

    // Act
    boolean result = adapter.exists(nonExistingPath);

    // Assert
    assertFalse(result);
  }

  @Test
  void exists_shouldHandleNullPath() {
    // Act
    boolean result = adapter.exists(null);

    // Assert
    assertFalse(result);
  }

  @Test
  void createDirectory_shouldCreateNewDirectory() {
    // Arrange
    String dirPath = tempDir.resolve("newDir").toString();

    // Act
    FileSystemPort.FileResult result = adapter.createDirectory(dirPath);

    // Assert
    assertTrue(result.isSuccessful());
    assertTrue(Files.exists(Paths.get(dirPath)));
    assertTrue(Files.isDirectory(Paths.get(dirPath)));
  }

  @Test
  void createDirectory_shouldHandleExistingDirectory() throws IOException {
    // Arrange
    Path existingDir = Files.createDirectory(tempDir.resolve("existingDir"));

    // Act
    FileSystemPort.FileResult result = adapter.createDirectory(existingDir.toString());

    // Assert
    assertTrue(result.isSuccessful());
    assertEquals("Directory already exists", result.getMessage());
  }

  @Test
  void createDirectory_shouldFailForExistingFile() throws IOException {
    // Arrange
    Path existingFile = Files.createFile(tempDir.resolve("existingFile.txt"));

    // Act
    FileSystemPort.FileResult result = adapter.createDirectory(existingFile.toString());

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("A file with the same name already exists"));
  }

  @Test
  void createDirectories_shouldCreateNestedDirectories() {
    // Arrange
    String nestedDirPath = tempDir.resolve("parent/child/grandchild").toString();

    // Act
    FileSystemPort.FileResult result = adapter.createDirectories(nestedDirPath);

    // Assert
    assertTrue(result.isSuccessful());
    assertTrue(Files.exists(Paths.get(nestedDirPath)));
    assertTrue(Files.isDirectory(Paths.get(nestedDirPath)));
  }

  @Test
  void delete_shouldDeleteFile() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("toDelete.txt"));

    // Act
    FileSystemPort.FileResult result = adapter.delete(testFile.toString());

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(Files.exists(testFile));
  }

  @Test
  void delete_shouldNotDeleteNonEmptyDirectory() throws IOException {
    // Arrange
    Path testDir = Files.createDirectory(tempDir.resolve("nonEmptyDir"));
    Files.createFile(testDir.resolve("file.txt"));

    // Act
    FileSystemPort.FileResult result = adapter.delete(testDir.toString());

    // Assert
    assertFalse(result.isSuccessful());
    assertTrue(result.getReason().isPresent());
    assertTrue(result.getReason().get().contains("Directory is not empty"));
    assertTrue(Files.exists(testDir));
  }

  @Test
  void deleteRecursively_shouldDeleteDirectoryAndContents() throws IOException {
    // Arrange
    Path testDir = Files.createDirectory(tempDir.resolve("recursiveDir"));
    Files.createFile(testDir.resolve("file1.txt"));
    Path nestedDir = Files.createDirectory(testDir.resolve("nested"));
    Files.createFile(nestedDir.resolve("file2.txt"));

    // Act
    FileSystemPort.FileResult result = adapter.deleteRecursively(testDir.toString());

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(Files.exists(testDir));
  }

  @Test
  void writeAndReadString_shouldPreserveContent() {
    // Arrange
    String filePath = tempDir.resolve("test.txt").toString();
    String content = "Test content with special chars: çéñ漢字";

    // Act - Write
    FileSystemPort.FileResult writeResult =
        adapter.writeString(filePath, content, StandardCharsets.UTF_8, false);

    // Assert - Write
    assertTrue(writeResult.isSuccessful());

    // Act - Read
    FileSystemPort.FileResult readResult = adapter.readString(filePath, StandardCharsets.UTF_8);

    // Assert - Read
    assertTrue(readResult.isSuccessful());
    assertEquals(content, readResult.getAttributes().get("content"));
  }

  @Test
  void writeAndReadLines_shouldPreserveContent() {
    // Arrange
    String filePath = tempDir.resolve("lines.txt").toString();
    List<String> lines = List.of("Line 1", "Line 2", "Line 3 with special chars: çéñ漢字");

    // Act - Write
    FileSystemPort.FileResult writeResult =
        adapter.writeLines(filePath, lines, StandardCharsets.UTF_8, false);

    // Assert - Write
    assertTrue(writeResult.isSuccessful());

    // Act - Read
    FileSystemPort.FileResult readResult = adapter.readLines(filePath, StandardCharsets.UTF_8);

    // Assert - Read
    assertTrue(readResult.isSuccessful());
    @SuppressWarnings("unchecked")
    List<String> readLines = (List<String>) readResult.getAttributes().get("lines");
    assertEquals(lines, readLines);
  }

  @Test
  void readLines_withRangeParameters_shouldReturnSpecifiedLines() throws IOException {
    // Arrange
    String filePath = tempDir.resolve("range.txt").toString();
    List<String> lines = List.of("Line 1", "Line 2", "Line 3", "Line 4", "Line 5");
    Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8);

    // Act
    FileSystemPort.FileResult result = adapter.readLines(filePath, StandardCharsets.UTF_8, 1, 2);

    // Assert
    assertTrue(result.isSuccessful());
    @SuppressWarnings("unchecked")
    List<String> readLines = (List<String>) result.getAttributes().get("lines");
    assertEquals(List.of("Line 2", "Line 3"), readLines);
  }

  @Test
  void copy_shouldCopyFile() throws IOException {
    // Arrange
    Path sourceFile = Files.createFile(tempDir.resolve("source.txt"));
    Files.write(sourceFile, "Source content".getBytes(StandardCharsets.UTF_8));
    String destinationPath = tempDir.resolve("destination.txt").toString();

    // Act
    FileSystemPort.FileResult result = adapter.copy(sourceFile.toString(), destinationPath);

    // Assert
    assertTrue(result.isSuccessful());
    assertTrue(Files.exists(Paths.get(destinationPath)));
    assertEquals(
        "Source content", Files.readString(Paths.get(destinationPath), StandardCharsets.UTF_8));
  }

  @Test
  void copyDirectory_shouldCopyDirectoryAndContents() throws IOException {
    // Arrange
    Path sourceDir = Files.createDirectory(tempDir.resolve("sourceDir"));
    Files.write(sourceDir.resolve("file1.txt"), "File 1 content".getBytes(StandardCharsets.UTF_8));
    Files.createDirectory(sourceDir.resolve("subdir"));
    Files.write(
        sourceDir.resolve("subdir").resolve("file2.txt"),
        "File 2 content".getBytes(StandardCharsets.UTF_8));

    String destinationPath = tempDir.resolve("destDir").toString();

    // Act
    FileSystemPort.FileResult result = adapter.copyDirectory(sourceDir.toString(), destinationPath);

    // Assert
    assertTrue(result.isSuccessful());
    assertTrue(Files.exists(Paths.get(destinationPath)));
    assertTrue(Files.exists(Paths.get(destinationPath, "file1.txt")));
    assertTrue(Files.exists(Paths.get(destinationPath, "subdir", "file2.txt")));
    assertEquals(
        "File 1 content",
        Files.readString(Paths.get(destinationPath, "file1.txt"), StandardCharsets.UTF_8));
    assertEquals(
        "File 2 content",
        Files.readString(
            Paths.get(destinationPath, "subdir", "file2.txt"), StandardCharsets.UTF_8));
  }

  @Test
  void move_shouldMoveFile() throws IOException {
    // Arrange
    Path sourceFile = Files.createFile(tempDir.resolve("toMove.txt"));
    Files.write(sourceFile, "Content to move".getBytes(StandardCharsets.UTF_8));
    String destinationPath = tempDir.resolve("moved.txt").toString();

    // Act
    FileSystemPort.FileResult result = adapter.move(sourceFile.toString(), destinationPath);

    // Assert
    assertTrue(result.isSuccessful());
    assertFalse(Files.exists(sourceFile));
    assertTrue(Files.exists(Paths.get(destinationPath)));
    assertEquals(
        "Content to move", Files.readString(Paths.get(destinationPath), StandardCharsets.UTF_8));
  }

  @Test
  void list_shouldListFilesInDirectory() throws IOException {
    // Arrange
    Path dir = Files.createDirectory(tempDir.resolve("listDir"));
    Files.createFile(dir.resolve("file1.txt"));
    Files.createFile(dir.resolve("file2.txt"));
    Files.createDirectory(dir.resolve("subdir"));

    // Act
    FileSystemPort.FileResult result = adapter.list(dir.toString());

    // Assert
    assertTrue(result.isSuccessful());
    @SuppressWarnings("unchecked")
    List<FileSystemPort.FileInfo> files =
        (List<FileSystemPort.FileInfo>) result.getAttributes().get("files");
    assertEquals(3, files.size());

    List<String> fileNames =
        files.stream().map(FileSystemPort.FileInfo::getName).collect(Collectors.toList());

    assertTrue(fileNames.contains("file1.txt"));
    assertTrue(fileNames.contains("file2.txt"));
    assertTrue(fileNames.contains("subdir"));
  }

  @Test
  void search_shouldFindMatchingFiles() throws IOException {
    // Arrange
    Path dir = Files.createDirectory(tempDir.resolve("searchDir"));
    Files.createFile(dir.resolve("file1.txt"));
    Files.createFile(dir.resolve("file2.txt"));
    Files.createFile(dir.resolve("document.pdf"));
    Path subdir = Files.createDirectory(dir.resolve("subdir"));
    Files.createFile(subdir.resolve("file3.txt"));

    // Act - Search current directory
    FileSystemPort.FileResult resultCurrent =
        adapter.search(dir.toString(), "*.txt", FileSystemPort.SearchMode.CURRENT_DIRECTORY);

    // Assert - Current directory
    assertTrue(resultCurrent.isSuccessful());
    @SuppressWarnings("unchecked")
    List<FileSystemPort.FileInfo> filesCurrent =
        (List<FileSystemPort.FileInfo>) resultCurrent.getAttributes().get("files");
    assertEquals(2, filesCurrent.size());

    // Act - Search recursively
    FileSystemPort.FileResult resultRecursive =
        adapter.search(dir.toString(), "*.txt", FileSystemPort.SearchMode.RECURSIVE);

    // Assert - Recursive
    assertTrue(resultRecursive.isSuccessful());
    @SuppressWarnings("unchecked")
    List<FileSystemPort.FileInfo> filesRecursive =
        (List<FileSystemPort.FileInfo>) resultRecursive.getAttributes().get("files");
    assertEquals(3, filesRecursive.size());
  }

  @Test
  void getInfo_shouldReturnFileInformation() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("info.txt"));
    Files.write(testFile, "Test content".getBytes(StandardCharsets.UTF_8));

    // Act
    FileSystemPort.FileResult result = adapter.getInfo(testFile.toString());

    // Assert
    assertTrue(result.isSuccessful());
    FileSystemPort.FileInfo fileInfo =
        (FileSystemPort.FileInfo) result.getAttributes().get("fileInfo");
    assertNotNull(fileInfo);
    assertEquals(testFile.toString(), fileInfo.getPath());
    assertFalse(fileInfo.isDirectory());
    assertTrue(fileInfo.isRegularFile());
    assertEquals("info.txt", fileInfo.getName());
    assertEquals(11, fileInfo.getSize()); // "Test content" is 11 bytes
  }

  @Test
  void createTempFile_shouldCreateFileInSpecifiedDirectory() {
    // Arrange
    String prefix = "test";
    String suffix = ".tmp";
    String directory = tempDir.toString();

    // Act
    FileSystemPort.FileResult result = adapter.createTempFile(prefix, suffix, directory);

    // Assert
    assertTrue(result.isSuccessful());
    String path = (String) result.getAttributes().get("path");
    assertNotNull(path);
    assertTrue(Files.exists(Paths.get(path)));
    assertTrue(path.startsWith(tempDir.toString()));
    assertTrue(path.contains(prefix));
    assertTrue(path.endsWith(suffix));
  }

  @Test
  void createTempDirectory_shouldCreateDirectoryInSpecifiedLocation() {
    // Arrange
    String prefix = "testdir";
    String directory = tempDir.toString();

    // Act
    FileSystemPort.FileResult result = adapter.createTempDirectory(prefix, directory);

    // Assert
    assertTrue(result.isSuccessful());
    String path = (String) result.getAttributes().get("path");
    assertNotNull(path);
    assertTrue(Files.exists(Paths.get(path)));
    assertTrue(Files.isDirectory(Paths.get(path)));
    assertTrue(path.startsWith(tempDir.toString()));
    assertTrue(path.contains(prefix));
  }

  @Test
  void openInputStream_shouldOpenStream() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("stream.txt"));
    Files.write(testFile, "Stream content".getBytes(StandardCharsets.UTF_8));

    // Act
    FileSystemPort.FileResult result = adapter.openInputStream(testFile.toString());

    // Assert
    assertTrue(result.isSuccessful());
    InputStream inputStream = (InputStream) result.getAttributes().get("inputStream");
    assertNotNull(inputStream);

    // Read from stream and verify content
    byte[] buffer = new byte[100];
    int bytesRead = inputStream.read(buffer);
    String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
    assertEquals("Stream content", content);

    // Cleanup
    inputStream.close();
  }

  @Test
  void openOutputStream_shouldOpenStream() throws IOException {
    // Arrange
    String filePath = tempDir.resolve("output.txt").toString();

    // Act
    FileSystemPort.FileResult result = adapter.openOutputStream(filePath, false);

    // Assert
    assertTrue(result.isSuccessful());
    OutputStream outputStream = (OutputStream) result.getAttributes().get("outputStream");
    assertNotNull(outputStream);

    // Write to stream
    String content = "Output stream content";
    outputStream.write(content.getBytes(StandardCharsets.UTF_8));
    outputStream.close();

    // Verify written content
    assertEquals(content, Files.readString(Paths.get(filePath), StandardCharsets.UTF_8));
  }

  @Test
  void size_shouldReturnFileSize() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("size.txt"));
    String content = "Size test content";
    Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));

    // Act
    long size = adapter.size(testFile.toString());

    // Assert
    assertEquals(content.getBytes(StandardCharsets.UTF_8).length, size);
  }

  @Test
  void getLastModifiedTime_shouldReturnTime() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("time.txt"));
    Instant before = Instant.now().minusSeconds(1);

    // Act
    Optional<Instant> timeOptional = adapter.getLastModifiedTime(testFile.toString());

    // Assert
    assertTrue(timeOptional.isPresent());
    Instant time = timeOptional.get();
    Instant after = Instant.now().plusSeconds(1);

    assertTrue(
        !time.isBefore(before) && !time.isAfter(after),
        "Modified time should be between before and after");
  }

  @Test
  void setLastModifiedTime_shouldUpdateTime() throws IOException {
    // Arrange
    Path testFile = Files.createFile(tempDir.resolve("modTime.txt"));
    Instant newTime = Instant.now().minusSeconds(3600); // One hour ago

    // Act
    FileSystemPort.FileResult result = adapter.setLastModifiedTime(testFile.toString(), newTime);

    // Assert
    assertTrue(result.isSuccessful());

    // Verify time was set correctly
    Instant actualTime = Files.getLastModifiedTime(testFile).toInstant();
    // Allow a small tolerance (1 second) due to file system precision differences
    assertTrue(
        Math.abs(actualTime.getEpochSecond() - newTime.getEpochSecond()) <= 1,
        "Modified time should be set correctly within 1 second tolerance");
  }

  @Test
  void normalize_shouldNormalizePath() {
    // Arrange
    String unnormalizedPath = tempDir.toString() + "/subdir/../file.txt";

    // Act
    String normalizedPath = adapter.normalize(unnormalizedPath);

    // Assert
    assertEquals(tempDir.toString() + "/file.txt", normalizedPath);
  }

  @Test
  void resolve_shouldResolvePath() {
    // Arrange
    String base = tempDir.toString();
    String relative = "subdir/file.txt";

    // Act
    String resolved = adapter.resolve(base, relative);

    // Assert
    assertEquals(tempDir.resolve("subdir/file.txt").toString(), resolved);
  }

  @Test
  void getAbsolutePath_shouldReturnAbsolutePath() {
    // Arrange
    String relativePath = "file.txt";

    // Act
    String absolutePath = adapter.getAbsolutePath(relativePath);

    // Assert
    assertTrue(Paths.get(absolutePath).isAbsolute());
  }

  @Test
  void isAbsolute_shouldIdentifyAbsolutePath() {
    // Arrange
    String absolutePath = tempDir.toString();
    String relativePath = "file.txt";

    // Act
    boolean absoluteResult = adapter.isAbsolute(absolutePath);
    boolean relativeResult = adapter.isAbsolute(relativePath);

    // Assert
    assertTrue(absoluteResult);
    assertFalse(relativeResult);
  }

  @Test
  void getRootDirectories_shouldReturnRoots() {
    // Act
    List<String> roots = adapter.getRootDirectories();

    // Assert
    assertNotNull(roots);
    assertFalse(roots.isEmpty());
  }

  @Test
  void getWorkingDirectory_shouldReturnCurrentDirectory() {
    // Act
    String workingDir = adapter.getWorkingDirectory();

    // Assert
    assertNotNull(workingDir);
    assertEquals(System.getProperty("user.dir"), workingDir);
  }

  @Test
  void shutdown_shouldReturnSuccess() {
    // Act
    FileSystemPort.FileResult result = adapter.shutdown();

    // Assert
    assertTrue(result.isSuccessful());
  }
}
