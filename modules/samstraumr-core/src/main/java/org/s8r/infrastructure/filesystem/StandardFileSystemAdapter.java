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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;

/**
 * Standard implementation of the FileSystemPort interface.
 *
 * <p>This adapter provides file system operations using the Java NIO.2 API.
 */
public class StandardFileSystemAdapter implements FileSystemPort {
  private final LoggerPort logger;

  /**
   * Creates a new StandardFileSystemAdapter with a logger.
   *
   * @param logger The logger to use
   */
  public StandardFileSystemAdapter(LoggerPort logger) {
    this.logger = logger;
  }

  /** Default constructor for tests. Creates a new StandardFileSystemAdapter. */
  public StandardFileSystemAdapter() {
    // For tests, this will be properly initialized in the test itself
    this.logger = null;
  }

  @Override
  public FileResult initialize() {
    if (logger != null) {
      logger.info("File system adapter initialized successfully");
    }
    return new SimpleFileResult(true, "File system adapter initialized successfully");
  }

  @Override
  public FileResult shutdown() {
    if (logger != null) {
      logger.info("File system adapter shut down successfully");
    }
    return new SimpleFileResult(true, "File system adapter shut down successfully");
  }

  @Override
  public Optional<String> readFile(Path path) throws IOException {
    if (!Files.exists(path)) {
      if (logger != null) {
        logger.warn("File not found: {}", path);
      }
      return Optional.empty();
    }

    try {
      String content = Files.readString(path);
      return Optional.of(content);
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error reading file: " + path, e);
      }
      throw e;
    }
  }

  @Override
  public boolean writeFile(Path path, String content) throws IOException {
    try {
      if (path.getParent() != null) {
        Files.createDirectories(path.getParent());
      }
      Files.writeString(path, content);
      return Files.exists(path);
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error writing to file: " + path, e);
      }
      throw e;
    }
  }

  @Override
  public boolean fileExists(Path path) {
    return Files.exists(path) && Files.isRegularFile(path);
  }

  @Override
  public boolean createDirectory(Path path) throws IOException {
    try {
      Files.createDirectories(path);
      return Files.exists(path) && Files.isDirectory(path);
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error creating directory: " + path, e);
      }
      throw e;
    }
  }

  @Override
  public FileResult createDirectory(String path) {
    try {
      boolean created = createDirectory(Path.of(path));
      return new SimpleFileResult(
          created, created ? "Directory created successfully" : "Failed to create directory");
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error creating directory: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to create directory", e.getMessage());
    }
  }

  @Override
  public List<Path> listFiles(Path path) throws IOException {
    if (!Files.exists(path) || !Files.isDirectory(path)) {
      if (logger != null) {
        logger.warn("Directory not found or not a directory: {}", path);
      }
      return List.of();
    }

    try {
      return Files.list(path).collect(Collectors.toList());
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error listing files in directory: " + path, e);
      }
      throw e;
    }
  }

  @Override
  public boolean delete(Path path) throws IOException {
    if (!Files.exists(path)) {
      if (logger != null) {
        logger.warn("Cannot delete non-existent path: {}", path);
      }
      return false;
    }

    try {
      if (Files.isDirectory(path)) {
        // Check if directory is empty
        try (var entries = Files.list(path)) {
          if (entries.findFirst().isPresent()) {
            // Delete recursively
            Files.walk(path)
                .sorted((p1, p2) -> -p1.compareTo(p2))
                .forEach(
                    p -> {
                      try {
                        Files.delete(p);
                      } catch (IOException e) {
                        throw new RuntimeException("Failed to delete: " + p, e);
                      }
                    });
          } else {
            // Empty directory can be deleted directly
            Files.delete(path);
          }
        }
      } else {
        Files.delete(path);
      }
      return !Files.exists(path);
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error deleting path: " + path, e);
      }
      throw e;
    }
  }

  @Override
  public boolean exists(String path) {
    if (path == null) {
      return false;
    }
    return Files.exists(Paths.get(path));
  }

  @Override
  public FileResult readString(String path, Charset charset) {
    try {
      Optional<String> content = readFile(Path.of(path));
      if (content.isPresent()) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("content", content.get());
        return new SimpleFileResult(true, "File read successfully", attributes);
      } else {
        return new SimpleFileResult(false, "Failed to read file", "File not found or empty");
      }
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error reading file: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to read file", e.getMessage());
    }
  }

  @Override
  public FileResult readLines(String path, Charset charset) {
    try {
      Optional<String> content = readFile(Path.of(path));
      if (content.isPresent()) {
        List<String> lines = List.of(content.get().split("\\n"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("lines", lines);
        return new SimpleFileResult(true, "File read successfully", attributes);
      } else {
        return new SimpleFileResult(false, "Failed to read file", "File not found or empty");
      }
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error reading file: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to read file", e.getMessage());
    }
  }

  @Override
  public FileResult readLines(String path, Charset charset, long start, long count) {
    try {
      Optional<String> content = readFile(Path.of(path));
      if (content.isPresent()) {
        List<String> allLines = List.of(content.get().split("\\n"));
        int startIdx = (int) start;
        int endIdx = Math.min(startIdx + (int) count, allLines.size());

        if (startIdx >= allLines.size()) {
          return new SimpleFileResult(false, "Failed to read lines", "Start index out of bounds");
        }

        List<String> lines = allLines.subList(startIdx, endIdx);
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("lines", lines);
        return new SimpleFileResult(true, "File lines read successfully", attributes);
      } else {
        return new SimpleFileResult(false, "Failed to read file", "File not found or empty");
      }
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error reading file: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to read file", e.getMessage());
    }
  }

  @Override
  public FileResult writeString(String path, String content, Charset charset, boolean append) {
    try {
      Path filePath = Path.of(path);
      if (filePath.getParent() != null) {
        Files.createDirectories(filePath.getParent());
      }

      if (append && Files.exists(filePath)) {
        String existingContent = Files.readString(filePath);
        Files.writeString(filePath, existingContent + content);
      } else {
        Files.writeString(filePath, content, charset);
      }
      return new SimpleFileResult(true, "File written successfully");
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error writing to file: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to write file", e.getMessage());
    }
  }

  @Override
  public FileResult writeLines(String path, List<String> lines, Charset charset, boolean append) {
    try {
      String content = String.join("\n", lines);
      return writeString(path, content, charset, append);
    } catch (Exception e) {
      if (logger != null) {
        logger.error("Error writing lines to file: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to write file lines", e.getMessage());
    }
  }

  @Override
  public FileResult copy(String source, String destination) {
    try {
      Files.copy(Path.of(source), Path.of(destination), StandardCopyOption.REPLACE_EXISTING);
      return new SimpleFileResult(true, "File copied successfully");
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error copying file: " + source + " to " + destination, e);
      }
      return new SimpleFileResult(false, "Failed to copy file", e.getMessage());
    }
  }

  @Override
  public FileResult copyDirectory(String source, String destination) {
    try {
      Path sourcePath = Path.of(source);
      Path destPath = Path.of(destination);

      // Create destination directory
      Files.createDirectories(destPath);

      // Copy all files recursively
      try (Stream<Path> stream = Files.walk(sourcePath)) {
        stream.forEach(
            src -> {
              try {
                Path dest = destPath.resolve(sourcePath.relativize(src));
                if (Files.isDirectory(src)) {
                  if (!Files.exists(dest)) {
                    Files.createDirectory(dest);
                  }
                  return;
                }
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
      }
      return new SimpleFileResult(true, "Directory copied successfully");
    } catch (Exception e) {
      if (logger != null) {
        logger.error("Error copying directory: " + source + " to " + destination, e);
      }
      return new SimpleFileResult(false, "Failed to copy directory", e.getMessage());
    }
  }

  @Override
  public FileResult move(String source, String destination) {
    try {
      Files.move(Path.of(source), Path.of(destination), StandardCopyOption.REPLACE_EXISTING);
      return new SimpleFileResult(true, "File moved successfully");
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error moving file: " + source + " to " + destination, e);
      }
      return new SimpleFileResult(false, "Failed to move file", e.getMessage());
    }
  }

  @Override
  public long size(String path) {
    try {
      return Files.size(Path.of(path));
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error getting file size: " + path, e);
      }
      return -1;
    }
  }

  @Override
  public Optional<Instant> getLastModifiedTime(String path) {
    try {
      FileTime fileTime = Files.getLastModifiedTime(Path.of(path));
      return Optional.of(fileTime.toInstant());
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error getting last modified time: " + path, e);
      }
      return Optional.empty();
    }
  }

  @Override
  public FileResult setLastModifiedTime(String path, Instant time) {
    try {
      Files.setLastModifiedTime(Path.of(path), FileTime.from(time));
      return new SimpleFileResult(true, "Last modified time set successfully");
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error setting last modified time: " + path, e);
      }
      return new SimpleFileResult(false, "Failed to set last modified time", e.getMessage());
    }
  }

  @Override
  public FileResult createTempFile(String prefix, String suffix, String directory) {
    try {
      Path tempFile;
      if (directory != null) {
        tempFile = Files.createTempFile(Path.of(directory), prefix, suffix);
      } else {
        tempFile = Files.createTempFile(prefix, suffix);
      }
      return new SimpleFileResult(
          true, "Temporary file created successfully", Map.of("path", tempFile.toString()));
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error creating temporary file", e);
      }
      return new SimpleFileResult(false, "Failed to create temporary file", e.getMessage());
    }
  }

  @Override
  public FileResult createTempDirectory(String prefix, String directory) {
    try {
      Path tempDir;
      if (directory != null) {
        tempDir = Files.createTempDirectory(Path.of(directory), prefix);
      } else {
        tempDir = Files.createTempDirectory(prefix);
      }
      return new SimpleFileResult(
          true, "Temporary directory created successfully", Map.of("path", tempDir.toString()));
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error creating temporary directory", e);
      }
      return new SimpleFileResult(false, "Failed to create temporary directory", e.getMessage());
    }
  }

  @Override
  public FileResult list(String directory) {
    try {
      Path dirPath = Path.of(directory);
      List<Path> files = listFiles(dirPath);
      List<FileInfo> fileInfos = new ArrayList<>();

      for (Path file : files) {
        fileInfos.add(createFileInfo(file));
      }

      return new SimpleFileResult(
          true, "Directory listed successfully", Map.of("files", fileInfos));
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error listing directory: " + directory, e);
      }
      return new SimpleFileResult(false, "Failed to list directory", e.getMessage());
    }
  }

  @Override
  public FileResult search(String directory, String pattern, SearchMode mode) {
    try {
      Path dirPath = Path.of(directory);
      List<FileInfo> matchingFiles = new ArrayList<>();

      if (mode == SearchMode.CURRENT_DIRECTORY) {
        // Only search current directory
        try (Stream<Path> paths = Files.list(dirPath)) {
          paths
              .filter(path -> path.getFileName().toString().matches(pattern))
              .forEach(path -> matchingFiles.add(createFileInfo(path)));
        }
      } else {
        // Search recursively
        try (Stream<Path> paths = Files.walk(dirPath)) {
          paths
              .filter(path -> path.getFileName().toString().matches(pattern))
              .forEach(path -> matchingFiles.add(createFileInfo(path)));
        }
      }

      return new SimpleFileResult(
          true, "Search completed successfully", Map.of("files", matchingFiles));
    } catch (IOException e) {
      if (logger != null) {
        logger.error("Error searching directory: " + directory, e);
      }
      return new SimpleFileResult(false, "Failed to search directory", e.getMessage());
    }
  }

  @Override
  public String getWorkingDirectory() {
    return System.getProperty("user.dir");
  }

  @Override
  public String normalize(String path) {
    return Path.of(path).normalize().toString();
  }

  @Override
  public String resolve(String base, String path) {
    return Path.of(base).resolve(path).toString();
  }

  @Override
  public String getAbsolutePath(String path) {
    return Path.of(path).toAbsolutePath().toString();
  }

  /**
   * Creates a FileInfo object from a Path.
   *
   * @param path The path to create a FileInfo from
   * @return A FileInfo object
   */
  private FileInfo createFileInfo(Path path) {
    try {
      BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

      return new FileInfo() {
        @Override
        public String getName() {
          return path.getFileName().toString();
        }

        @Override
        public String getPath() {
          return path.toString();
        }

        @Override
        public boolean isDirectory() {
          return Files.isDirectory(path);
        }

        @Override
        public boolean isRegularFile() {
          return Files.isRegularFile(path);
        }

        @Override
        public long getSize() {
          try {
            return Files.size(path);
          } catch (IOException e) {
            return -1;
          }
        }

        @Override
        public Instant getLastModifiedTime() {
          return attrs.lastModifiedTime().toInstant();
        }
      };
    } catch (IOException e) {
      // Simplified implementation for error cases
      return new FileInfo() {
        @Override
        public String getName() {
          return path.getFileName().toString();
        }

        @Override
        public String getPath() {
          return path.toString();
        }

        @Override
        public boolean isDirectory() {
          return false;
        }

        @Override
        public boolean isRegularFile() {
          return false;
        }

        @Override
        public long getSize() {
          return -1;
        }

        @Override
        public Instant getLastModifiedTime() {
          return Instant.EPOCH;
        }
      };
    }
  }
}
