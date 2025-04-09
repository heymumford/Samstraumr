/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.application.port;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Port interface for file system operations in the application layer.
 * 
 * <p>This interface defines the operations that can be performed on a file system,
 * following the ports and adapters pattern from Clean Architecture.
 */
public interface FileSystemPort {

    /**
     * Reads the content of a file.
     *
     * @param path The path to the file
     * @return An Optional containing the file content as a string if successful, or empty if failed
     * @throws IOException If an I/O error occurs
     */
    Optional<String> readFile(Path path) throws IOException;
    
    /**
     * Writes content to a file.
     *
     * @param path The path to the file
     * @param content The content to write
     * @return true if the operation was successful, false otherwise
     * @throws IOException If an I/O error occurs
     */
    boolean writeFile(Path path, String content) throws IOException;
    
    /**
     * Checks if a file exists.
     *
     * @param path The path to the file
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(Path path);
    
    /**
     * Creates a directory.
     *
     * @param path The path to the directory
     * @return true if the directory was created, false otherwise
     * @throws IOException If an I/O error occurs
     */
    boolean createDirectory(Path path) throws IOException;
    
    /**
     * Creates a directory.
     *
     * @param path The path to the directory
     * @return A result object indicating success or failure
     */
    FileResult createDirectory(String path);
    
    /**
     * Lists files in a directory.
     *
     * @param path The path to the directory
     * @return A list of file paths
     * @throws IOException If an I/O error occurs
     */
    List<Path> listFiles(Path path) throws IOException;
    
    /**
     * Deletes a file or directory.
     *
     * @param path The path to the file or directory
     * @return true if the file or directory was deleted, false otherwise
     * @throws IOException If an I/O error occurs
     */
    boolean delete(Path path) throws IOException;
    
    /**
     * Result of a file system operation.
     */
    interface FileResult {
        /**
         * @return true if the operation was successful, false otherwise
         */
        boolean isSuccessful();
        
        /**
         * @return A message describing the result of the operation
         */
        String getMessage();
        
        /**
         * @return An optional reason for failure if the operation was not successful
         */
        Optional<String> getReason();
        
        /**
         * @return Additional attributes associated with the operation result
         */
        default Map<String, Object> getAttributes() {
            return Map.of();
        }
        
        /**
         * Creates a successful result with the given message.
         * 
         * @param message The success message
         * @return A successful FileResult
         */
        static FileResult success(String message) {
            return new SimpleFileResult(true, message);
        }
        
        /**
         * Creates a successful result with the given message and attributes.
         * 
         * @param message The success message
         * @param attributes Additional attributes
         * @return A successful FileResult
         */
        static FileResult success(String message, Map<String, Object> attributes) {
            return new SimpleFileResult(true, message, attributes);
        }
        
        /**
         * Creates a failed result with the given message and reason.
         * 
         * @param message The failure message
         * @param reason The reason for failure
         * @return A failed FileResult
         */
        static FileResult failure(String message, String reason) {
            return new SimpleFileResult(false, message, reason);
        }
    }
    
    /**
     * Information about a file.
     */
    interface FileInfo {
        /**
         * @return The file name
         */
        String getName();
        
        /**
         * @return The full path to the file
         */
        String getPath();
        
        /**
         * @return true if the file is a directory, false otherwise
         */
        boolean isDirectory();
        
        /**
         * @return true if the file is a regular file, false otherwise
         */
        boolean isRegularFile();
        
        /**
         * @return The size of the file in bytes
         */
        long getSize();
        
        /**
         * @return The last modified time of the file
         */
        Instant getLastModifiedTime();
        
        /**
         * Creates a new FileInfo instance.
         *
         * @param path The file path
         * @param isDirectory Whether the file is a directory
         * @param isRegularFile Whether the file is a regular file
         * @param isHidden Whether the file is hidden
         * @param size The file size in bytes
         * @param creationTime The creation time
         * @param lastModifiedTime The last modified time
         * @param lastAccessTime The last access time
         * @param attributes Additional attributes
         * @return A new FileInfo instance
         */
        static FileInfo create(
                String path,
                boolean isDirectory,
                boolean isRegularFile,
                boolean isHidden,
                long size,
                Instant creationTime,
                Instant lastModifiedTime,
                Instant lastAccessTime,
                Map<String, Object> attributes) {
            return new SimpleFileInfo(path, isDirectory, isRegularFile, isHidden, size, creationTime, lastModifiedTime, lastAccessTime, attributes);
        }
    }
    
    /**
     * Simple implementation of FileInfo.
     */
    class SimpleFileInfo implements FileInfo {
        private final String path;
        private final boolean isDirectory;
        private final boolean isRegularFile;
        private final boolean isHidden;
        private final long size;
        private final Instant creationTime;
        private final Instant lastModifiedTime;
        private final Instant lastAccessTime;
        private final Map<String, Object> attributes;
        
        public SimpleFileInfo(
                String path,
                boolean isDirectory,
                boolean isRegularFile,
                boolean isHidden,
                long size,
                Instant creationTime,
                Instant lastModifiedTime,
                Instant lastAccessTime,
                Map<String, Object> attributes) {
            this.path = path;
            this.isDirectory = isDirectory;
            this.isRegularFile = isRegularFile;
            this.isHidden = isHidden;
            this.size = size;
            this.creationTime = creationTime;
            this.lastModifiedTime = lastModifiedTime;
            this.lastAccessTime = lastAccessTime;
            this.attributes = attributes;
        }
        
        @Override
        public String getName() {
            Path filePath = Path.of(path);
            return filePath.getFileName().toString();
        }
        
        @Override
        public String getPath() {
            return path;
        }
        
        @Override
        public boolean isDirectory() {
            return isDirectory;
        }
        
        @Override
        public boolean isRegularFile() {
            return isRegularFile;
        }
        
        @Override
        public long getSize() {
            return size;
        }
        
        @Override
        public Instant getLastModifiedTime() {
            return lastModifiedTime;
        }
    }
    
    /**
     * Mode for searching files.
     */
    enum SearchMode {
        /**
         * Search only in the current directory.
         */
        CURRENT_DIRECTORY,
        
        /**
         * Search recursively in subdirectories.
         */
        RECURSIVE
    }
    
    /**
     * Initializes the file system port.
     * 
     * @return A result object indicating success or failure
     */
    default FileResult initialize() {
        return new SimpleFileResult(true, "File system port initialized successfully");
    }
    
    /**
     * Shuts down the file system port.
     * 
     * @return A result object indicating success or failure
     */
    default FileResult shutdown() {
        return new SimpleFileResult(true, "File system port shut down successfully");
    }
    
    /**
     * Checks if a file or directory exists at the given path.
     * 
     * @param path The path to check
     * @return true if the file or directory exists, false otherwise
     */
    default boolean exists(String path) {
        if (path == null) {
            return false;
        }
        return fileExists(Path.of(path));
    }
    
    /**
     * Creates a directory hierarchy, including any parent directories that don't exist.
     * 
     * @param path The path of the directory to create
     * @return A result object indicating success or failure
     */
    default FileResult createDirectories(String path) {
        try {
            boolean created = createDirectory(Path.of(path));
            return new SimpleFileResult(created, created ? 
                "Directories created successfully" : "Failed to create directories");
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to create directories", e.getMessage());
        }
    }
    
    /**
     * Deletes a file or directory.
     * 
     * @param path The path of the file or directory to delete
     * @return A result object indicating success or failure
     */
    default FileResult delete(String path) {
        try {
            boolean deleted = delete(Path.of(path));
            return new SimpleFileResult(deleted, deleted ? 
                "File deleted successfully" : "Failed to delete file");
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to delete file", e.getMessage());
        }
    }
    
    /**
     * Deletes a directory and all its contents recursively.
     * 
     * @param path The path of the directory to delete
     * @return A result object indicating success or failure
     */
    default FileResult deleteRecursively(String path) {
        try {
            boolean deleted = delete(Path.of(path));
            return new SimpleFileResult(deleted, deleted ? 
                "Directory deleted recursively successfully" : "Failed to delete directory recursively");
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to delete directory recursively", e.getMessage());
        }
    }
    
    /**
     * Reads a file as a string.
     * 
     * @param path The path of the file to read
     * @param charset The charset to use for reading
     * @return A result object containing the file content
     */
    default FileResult readString(String path, Charset charset) {
        try {
            Optional<String> content = readFile(Path.of(path));
            if (content.isPresent()) {
                return new SimpleFileResult(true, "File read successfully", 
                    Map.of("content", content.get()));
            } else {
                return new SimpleFileResult(false, "Failed to read file", "File not found or empty");
            }
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to read file", e.getMessage());
        }
    }
    
    /**
     * Reads a file as a list of lines.
     * 
     * @param path The path of the file to read
     * @param charset The charset to use for reading
     * @return A result object containing the lines of the file
     */
    default FileResult readLines(String path, Charset charset) {
        try {
            Optional<String> content = readFile(Path.of(path));
            if (content.isPresent()) {
                List<String> lines = List.of(content.get().split("\\n"));
                return new SimpleFileResult(true, "File read successfully", 
                    Map.of("lines", lines));
            } else {
                return new SimpleFileResult(false, "Failed to read file", "File not found or empty");
            }
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to read file", e.getMessage());
        }
    }
    
    /**
     * Simple implementation of FileResult for the default methods.
     */
    class SimpleFileResult implements FileResult {
        private final boolean successful;
        private final String message;
        private final String reason;
        private final Map<String, Object> attributes;
        
        public SimpleFileResult(boolean successful, String message) {
            this(successful, message, null, Map.of());
        }
        
        public SimpleFileResult(boolean successful, String message, String reason) {
            this(successful, message, reason, Map.of());
        }
        
        public SimpleFileResult(boolean successful, String message, Map<String, Object> attributes) {
            this(successful, message, null, attributes);
        }
        
        public SimpleFileResult(boolean successful, String message, String reason, Map<String, Object> attributes) {
            this.successful = successful;
            this.message = message;
            this.reason = reason;
            this.attributes = attributes;
        }
        
        @Override
        public boolean isSuccessful() {
            return successful;
        }
        
        @Override
        public String getMessage() {
            return message;
        }
        
        @Override
        public Optional<String> getReason() {
            return Optional.ofNullable(reason);
        }
        
        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }
}