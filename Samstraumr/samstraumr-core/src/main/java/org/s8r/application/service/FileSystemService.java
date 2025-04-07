/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class that provides file system-related functionality to the application layer.
 * <p>
 * This service uses the FileSystemPort to interact with the file system infrastructure.
 * It provides a simplified interface for common file operations and adds additional
 * business logic as needed.
 */
public class FileSystemService {

    private final FileSystemPort fileSystemPort;
    private final LoggerPort logger;

    /**
     * Creates a new FileSystemService with the specified dependencies.
     *
     * @param fileSystemPort The file system port implementation to use
     * @param logger         The logger to use for logging
     */
    public FileSystemService(FileSystemPort fileSystemPort, LoggerPort logger) {
        this.fileSystemPort = fileSystemPort;
        this.logger = logger;
    }

    /**
     * Initializes the file system service.
     */
    public void initialize() {
        logger.info("Initializing file system service");
        FileSystemPort.FileResult result = fileSystemPort.initialize();
        if (!result.isSuccessful()) {
            logger.error("Failed to initialize file system service: {}", result.getReason().orElse("Unknown reason"));
        }
    }

    /**
     * Checks if a file or directory exists.
     *
     * @param path The path to check
     * @return True if the file or directory exists, false otherwise
     */
    public boolean fileExists(String path) {
        return fileSystemPort.exists(path);
    }

    /**
     * Creates a directory, including any necessary parent directories.
     *
     * @param path The path of the directory to create
     * @return True if the directory was created successfully, false otherwise
     */
    public boolean createDirectory(String path) {
        FileSystemPort.FileResult result = fileSystemPort.createDirectories(path);
        if (!result.isSuccessful()) {
            logger.warn("Failed to create directory {}: {}", path, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Deletes a file or directory.
     *
     * @param path      The path of the file or directory to delete
     * @param recursive Whether to delete directories recursively
     * @return True if the file or directory was deleted successfully, false otherwise
     */
    public boolean deleteFile(String path, boolean recursive) {
        FileSystemPort.FileResult result;
        if (recursive) {
            result = fileSystemPort.deleteRecursively(path);
        } else {
            result = fileSystemPort.delete(path);
        }
        if (!result.isSuccessful()) {
            logger.warn("Failed to delete {}: {}", path, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Reads a file as a string.
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file, or null to use UTF-8
     * @return The contents of the file, or an empty Optional if the file could not be read
     */
    public Optional<String> readFile(String path, Charset charset) {
        Charset fileCharset = (charset != null) ? charset : StandardCharsets.UTF_8;
        FileSystemPort.FileResult result = fileSystemPort.readString(path, fileCharset);
        if (result.isSuccessful()) {
            return Optional.ofNullable((String) result.getAttributes().get("content"));
        } else {
            logger.warn("Failed to read file {}: {}", path, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Reads a file as a string using UTF-8 encoding.
     *
     * @param path The path of the file to read
     * @return The contents of the file, or an empty Optional if the file could not be read
     */
    public Optional<String> readFile(String path) {
        return readFile(path, StandardCharsets.UTF_8);
    }

    /**
     * Reads a file as a list of lines.
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file, or null to use UTF-8
     * @return The lines of the file, or an empty Optional if the file could not be read
     */
    public Optional<List<String>> readLines(String path, Charset charset) {
        Charset fileCharset = (charset != null) ? charset : StandardCharsets.UTF_8;
        FileSystemPort.FileResult result = fileSystemPort.readLines(path, fileCharset);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<String> lines = (List<String>) result.getAttributes().get("lines");
            return Optional.ofNullable(lines);
        } else {
            logger.warn("Failed to read lines from file {}: {}", path, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Reads a file as a list of lines using UTF-8 encoding.
     *
     * @param path The path of the file to read
     * @return The lines of the file, or an empty Optional if the file could not be read
     */
    public Optional<List<String>> readLines(String path) {
        return readLines(path, StandardCharsets.UTF_8);
    }

    /**
     * Reads a portion of a file as a list of lines.
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file, or null to use UTF-8
     * @param start   The line number to start from (0-based)
     * @param count   The maximum number of lines to read
     * @return The lines of the file, or an empty Optional if the file could not be read
     */
    public Optional<List<String>> readLines(String path, Charset charset, long start, long count) {
        Charset fileCharset = (charset != null) ? charset : StandardCharsets.UTF_8;
        FileSystemPort.FileResult result = fileSystemPort.readLines(path, fileCharset, start, count);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<String> lines = (List<String>) result.getAttributes().get("lines");
            return Optional.ofNullable(lines);
        } else {
            logger.warn("Failed to read lines from file {}: {}", path, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Writes a string to a file.
     *
     * @param path     The path of the file to write
     * @param content  The content to write to the file
     * @param charset  The charset to use for writing the file, or null to use UTF-8
     * @param append   Whether to append to the file if it exists
     * @return True if the file was written successfully, false otherwise
     */
    public boolean writeFile(String path, String content, Charset charset, boolean append) {
        Charset fileCharset = (charset != null) ? charset : StandardCharsets.UTF_8;
        FileSystemPort.FileResult result = fileSystemPort.writeString(path, content, fileCharset, append);
        if (!result.isSuccessful()) {
            logger.warn("Failed to write to file {}: {}", path, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Writes a string to a file using UTF-8 encoding.
     *
     * @param path    The path of the file to write
     * @param content The content to write to the file
     * @return True if the file was written successfully, false otherwise
     */
    public boolean writeFile(String path, String content) {
        return writeFile(path, content, StandardCharsets.UTF_8, false);
    }

    /**
     * Writes lines of text to a file.
     *
     * @param path     The path of the file to write
     * @param lines    The lines to write to the file
     * @param charset  The charset to use for writing the file, or null to use UTF-8
     * @param append   Whether to append to the file if it exists
     * @return True if the file was written successfully, false otherwise
     */
    public boolean writeLines(String path, List<String> lines, Charset charset, boolean append) {
        Charset fileCharset = (charset != null) ? charset : StandardCharsets.UTF_8;
        FileSystemPort.FileResult result = fileSystemPort.writeLines(path, lines, fileCharset, append);
        if (!result.isSuccessful()) {
            logger.warn("Failed to write lines to file {}: {}", path, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Writes lines of text to a file using UTF-8 encoding.
     *
     * @param path  The path of the file to write
     * @param lines The lines to write to the file
     * @return True if the file was written successfully, false otherwise
     */
    public boolean writeLines(String path, List<String> lines) {
        return writeLines(path, lines, StandardCharsets.UTF_8, false);
    }

    /**
     * Copies a file to a new location.
     *
     * @param source      The source path of the file
     * @param destination The destination path
     * @return True if the file was copied successfully, false otherwise
     */
    public boolean copyFile(String source, String destination) {
        FileSystemPort.FileResult result = fileSystemPort.copy(source, destination);
        if (!result.isSuccessful()) {
            logger.warn("Failed to copy from {} to {}: {}", source, destination, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Copies a directory and its contents to a new location.
     *
     * @param source      The source path of the directory
     * @param destination The destination path
     * @return True if the directory was copied successfully, false otherwise
     */
    public boolean copyDirectory(String source, String destination) {
        FileSystemPort.FileResult result = fileSystemPort.copyDirectory(source, destination);
        if (!result.isSuccessful()) {
            logger.warn("Failed to copy directory from {} to {}: {}", source, destination, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Moves a file or directory to a new location.
     *
     * @param source      The source path of the file or directory
     * @param destination The destination path
     * @return True if the file or directory was moved successfully, false otherwise
     */
    public boolean moveFile(String source, String destination) {
        FileSystemPort.FileResult result = fileSystemPort.move(source, destination);
        if (!result.isSuccessful()) {
            logger.warn("Failed to move from {} to {}: {}", source, destination, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Gets the size of a file.
     *
     * @param path The path of the file
     * @return The size of the file in bytes, or -1 if the file does not exist or is a directory
     */
    public long getFileSize(String path) {
        return fileSystemPort.size(path);
    }

    /**
     * Gets the last modified time of a file or directory.
     *
     * @param path The path of the file or directory
     * @return An Optional containing the last modified time, or empty if the file does not exist
     */
    public Optional<Instant> getLastModifiedTime(String path) {
        return fileSystemPort.getLastModifiedTime(path);
    }

    /**
     * Sets the last modified time of a file or directory.
     *
     * @param path The path of the file or directory
     * @param time The time to set
     * @return True if the last modified time was set successfully, false otherwise
     */
    public boolean setLastModifiedTime(String path, Instant time) {
        FileSystemPort.FileResult result = fileSystemPort.setLastModifiedTime(path, time);
        if (!result.isSuccessful()) {
            logger.warn("Failed to set last modified time for {}: {}", path, result.getReason().orElse("Unknown reason"));
        }
        return result.isSuccessful();
    }

    /**
     * Creates a temporary file.
     *
     * @param prefix    The prefix of the file name
     * @param suffix    The suffix of the file name
     * @param directory The directory to create the file in, or null for the default temporary directory
     * @return An Optional containing the path of the created file, or empty if the file could not be created
     */
    public Optional<String> createTempFile(String prefix, String suffix, String directory) {
        FileSystemPort.FileResult result = fileSystemPort.createTempFile(prefix, suffix, directory);
        if (result.isSuccessful()) {
            return Optional.ofNullable((String) result.getAttributes().get("path"));
        } else {
            logger.warn("Failed to create temporary file: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Creates a temporary directory.
     *
     * @param prefix    The prefix of the directory name
     * @param directory The directory to create the directory in, or null for the default temporary directory
     * @return An Optional containing the path of the created directory, or empty if the directory could not be created
     */
    public Optional<String> createTempDirectory(String prefix, String directory) {
        FileSystemPort.FileResult result = fileSystemPort.createTempDirectory(prefix, directory);
        if (result.isSuccessful()) {
            return Optional.ofNullable((String) result.getAttributes().get("path"));
        } else {
            logger.warn("Failed to create temporary directory: {}", result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Lists the files and directories in a directory.
     *
     * @param directory The directory to list
     * @return An Optional containing the list of file information, or empty if the directory could not be listed
     */
    public Optional<List<FileSystemPort.FileInfo>> listFiles(String directory) {
        FileSystemPort.FileResult result = fileSystemPort.list(directory);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<FileSystemPort.FileInfo> files = (List<FileSystemPort.FileInfo>) result.getAttributes().get("files");
            return Optional.ofNullable(files);
        } else {
            logger.warn("Failed to list directory {}: {}", directory, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Searches for files matching a pattern in a directory.
     *
     * @param directory  The directory to search in
     * @param pattern    The glob pattern to match against file names
     * @param recursive  Whether to search recursively in subdirectories
     * @return An Optional containing the list of matching file information, or empty if the search failed
     */
    public Optional<List<FileSystemPort.FileInfo>> searchFiles(String directory, String pattern, boolean recursive) {
        FileSystemPort.SearchMode searchMode = recursive ? 
                FileSystemPort.SearchMode.RECURSIVE : 
                FileSystemPort.SearchMode.CURRENT_DIRECTORY;
                
        FileSystemPort.FileResult result = fileSystemPort.search(directory, pattern, searchMode);
        if (result.isSuccessful()) {
            @SuppressWarnings("unchecked")
            List<FileSystemPort.FileInfo> files = (List<FileSystemPort.FileInfo>) result.getAttributes().get("files");
            return Optional.ofNullable(files);
        } else {
            logger.warn("Failed to search directory {}: {}", directory, result.getReason().orElse("Unknown reason"));
            return Optional.empty();
        }
    }

    /**
     * Gets the working directory.
     *
     * @return The working directory path
     */
    public String getWorkingDirectory() {
        return fileSystemPort.getWorkingDirectory();
    }

    /**
     * Normalizes a path, removing redundant elements.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    public String normalizePath(String path) {
        return fileSystemPort.normalize(path);
    }

    /**
     * Resolves a path against a base path.
     *
     * @param base The base path
     * @param path The path to resolve
     * @return The resolved path
     */
    public String resolvePath(String base, String path) {
        return fileSystemPort.resolve(base, path);
    }

    /**
     * Gets the absolute path of a file or directory.
     *
     * @param path The path to get the absolute path of
     * @return The absolute path
     */
    public String getAbsolutePath(String path) {
        return fileSystemPort.getAbsolutePath(path);
    }

    /**
     * Shuts down the file system service.
     */
    public void shutdown() {
        logger.info("Shutting down file system service");
        FileSystemPort.FileResult result = fileSystemPort.shutdown();
        if (!result.isSuccessful()) {
            logger.error("Failed to shut down file system service: {}", result.getReason().orElse("Unknown reason"));
        }
    }
}