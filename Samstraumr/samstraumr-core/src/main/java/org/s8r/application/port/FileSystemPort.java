/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.port;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * FileSystemPort defines the interface for file system-related operations in the S8r framework.
 * <p>
 * This port handles file and directory operations, providing an abstraction over the file system
 * to allow the application core to remain independent of specific file system implementations.
 * It follows Clean Architecture principles by defining a boundary between the application and
 * infrastructure layers.
 */
public interface FileSystemPort {

    /**
     * Represents the outcome of a file system operation with detailed information.
     */
    final class FileResult {
        private final boolean successful;
        private final String message;
        private final String reason;
        private final Map<String, Object> attributes;

        private FileResult(boolean successful, String message, String reason, Map<String, Object> attributes) {
            this.successful = successful;
            this.message = message;
            this.reason = reason;
            this.attributes = attributes;
        }

        /**
         * Creates a successful result.
         *
         * @param message A message describing the successful operation
         * @return A new FileResult instance indicating success
         */
        public static FileResult success(String message) {
            return new FileResult(true, message, null, Map.of());
        }

        /**
         * Creates a successful result with additional attributes.
         *
         * @param message    A message describing the successful operation
         * @param attributes Additional information about the operation
         * @return A new FileResult instance indicating success
         */
        public static FileResult success(String message, Map<String, Object> attributes) {
            return new FileResult(true, message, null, attributes);
        }

        /**
         * Creates a failed result.
         *
         * @param message A message describing the failed operation
         * @param reason  The reason for the failure
         * @return A new FileResult instance indicating failure
         */
        public static FileResult failure(String message, String reason) {
            return new FileResult(false, message, reason, Map.of());
        }

        /**
         * Creates a failed result with additional attributes.
         *
         * @param message    A message describing the failed operation
         * @param reason     The reason for the failure
         * @param attributes Additional information about the operation
         * @return A new FileResult instance indicating failure
         */
        public static FileResult failure(String message, String reason, Map<String, Object> attributes) {
            return new FileResult(false, message, reason, attributes);
        }

        /**
         * Checks if the operation was successful.
         *
         * @return True if the operation was successful, false otherwise
         */
        public boolean isSuccessful() {
            return successful;
        }

        /**
         * Gets the message associated with the operation result.
         *
         * @return The message describing the operation outcome
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the reason for a failed operation.
         *
         * @return The reason for the failure, or null if the operation was successful
         */
        public Optional<String> getReason() {
            return Optional.ofNullable(reason);
        }

        /**
         * Gets the additional attributes associated with the operation result.
         *
         * @return A map of attributes providing additional information
         */
        public Map<String, Object> getAttributes() {
            return attributes;
        }
    }

    /**
     * Represents information about a file or directory.
     */
    final class FileInfo {
        private final String path;
        private final boolean directory;
        private final boolean regular;
        private final boolean hidden;
        private final long size;
        private final Instant creationTime;
        private final Instant lastModifiedTime;
        private final Instant lastAccessTime;
        private final Map<String, Object> attributes;

        /**
         * Creates a new FileInfo instance.
         *
         * @param path             The path of the file or directory
         * @param directory        Whether the path is a directory
         * @param regular          Whether the path is a regular file
         * @param hidden           Whether the path is hidden
         * @param size             The size of the file in bytes
         * @param creationTime     The creation time of the file
         * @param lastModifiedTime The last modified time of the file
         * @param lastAccessTime   The last access time of the file
         * @param attributes       Additional attributes of the file
         */
        public FileInfo(String path, boolean directory, boolean regular, boolean hidden,
                      long size, Instant creationTime, Instant lastModifiedTime,
                      Instant lastAccessTime, Map<String, Object> attributes) {
            this.path = path;
            this.directory = directory;
            this.regular = regular;
            this.hidden = hidden;
            this.size = size;
            this.creationTime = creationTime;
            this.lastModifiedTime = lastModifiedTime;
            this.lastAccessTime = lastAccessTime;
            this.attributes = attributes;
        }

        public String getPath() {
            return path;
        }

        public boolean isDirectory() {
            return directory;
        }

        public boolean isRegularFile() {
            return regular;
        }

        public boolean isHidden() {
            return hidden;
        }

        public long getSize() {
            return size;
        }

        public Instant getCreationTime() {
            return creationTime;
        }

        public Instant getLastModifiedTime() {
            return lastModifiedTime;
        }

        public Instant getLastAccessTime() {
            return lastAccessTime;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        /**
         * Gets the name of the file or directory (the last name in the path).
         *
         * @return The name of the file or directory
         */
        public String getName() {
            int lastSeparator = path.lastIndexOf('/');
            return lastSeparator >= 0 ? path.substring(lastSeparator + 1) : path;
        }

        /**
         * Gets the parent directory of the file or directory.
         *
         * @return The parent directory, or empty if the path has no parent
         */
        public Optional<String> getParent() {
            int lastSeparator = path.lastIndexOf('/');
            return lastSeparator > 0 ? Optional.of(path.substring(0, lastSeparator)) : Optional.empty();
        }

        /**
         * Gets the extension of the file, if any.
         *
         * @return The extension of the file, or empty if the file has no extension
         */
        public Optional<String> getExtension() {
            String name = getName();
            int lastDot = name.lastIndexOf('.');
            return lastDot > 0 ? Optional.of(name.substring(lastDot + 1)) : Optional.empty();
        }
    }

    /**
     * Enumeration of file search modes.
     */
    enum SearchMode {
        /**
         * Search only in the specified directory
         */
        CURRENT_DIRECTORY,

        /**
         * Search recursively in the specified directory and all subdirectories
         */
        RECURSIVE
    }

    /**
     * Gets information about a file or directory.
     *
     * @param path The path to the file or directory
     * @return A FileResult containing the file information if successful
     */
    FileResult getInfo(String path);

    /**
     * Checks if a file or directory exists.
     *
     * @param path The path to check
     * @return True if the file or directory exists, false otherwise
     */
    boolean exists(String path);

    /**
     * Creates a directory.
     *
     * @param path The path of the directory to create
     * @return A FileResult indicating success or failure
     */
    FileResult createDirectory(String path);

    /**
     * Creates directories recursively.
     *
     * @param path The path of the directories to create
     * @return A FileResult indicating success or failure
     */
    FileResult createDirectories(String path);

    /**
     * Deletes a file or directory.
     *
     * @param path The path of the file or directory to delete
     * @return A FileResult indicating success or failure
     */
    FileResult delete(String path);

    /**
     * Deletes a directory and all its contents recursively.
     *
     * @param path The path of the directory to delete
     * @return A FileResult indicating success or failure
     */
    FileResult deleteRecursively(String path);

    /**
     * Moves a file or directory to a new location.
     *
     * @param source      The source path of the file or directory
     * @param destination The destination path
     * @return A FileResult indicating success or failure
     */
    FileResult move(String source, String destination);

    /**
     * Copies a file to a new location.
     *
     * @param source      The source path of the file
     * @param destination The destination path
     * @return A FileResult indicating success or failure
     */
    FileResult copy(String source, String destination);

    /**
     * Copies a directory and its contents to a new location.
     *
     * @param source      The source path of the directory
     * @param destination The destination path
     * @return A FileResult indicating success or failure
     */
    FileResult copyDirectory(String source, String destination);

    /**
     * Lists the files and directories in a directory.
     *
     * @param directory The directory to list
     * @return A FileResult containing the list of file information
     */
    FileResult list(String directory);

    /**
     * Searches for files matching a pattern in a directory.
     *
     * @param directory   The directory to search in
     * @param pattern     The glob pattern to match against file names
     * @param searchMode  The search mode (current directory or recursive)
     * @return A FileResult containing the list of matching file information
     */
    FileResult search(String directory, String pattern, SearchMode searchMode);

    /**
     * Reads the contents of a file as a string.
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file (default to UTF-8 if null)
     * @return A FileResult containing the file contents
     */
    FileResult readString(String path, Charset charset);

    /**
     * Reads the contents of a file as a list of strings (lines).
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file (default to UTF-8 if null)
     * @return A FileResult containing the file lines
     */
    FileResult readLines(String path, Charset charset);

    /**
     * Reads a portion of a file as a list of strings (lines).
     *
     * @param path    The path of the file to read
     * @param charset The charset to use for reading the file (default to UTF-8 if null)
     * @param start   The line number to start from (0-based)
     * @param count   The maximum number of lines to read
     * @return A FileResult containing the file lines
     */
    FileResult readLines(String path, Charset charset, long start, long count);

    /**
     * Reads a file as bytes.
     *
     * @param path The path of the file to read
     * @return A FileResult containing the file bytes
     */
    FileResult readBytes(String path);

    /**
     * Opens a file for reading.
     *
     * @param path The path of the file to open
     * @return A FileResult containing the input stream
     */
    FileResult openInputStream(String path);

    /**
     * Opens a file for writing.
     *
     * @param path    The path of the file to open
     * @param append  Whether to append to the file if it exists
     * @return A FileResult containing the output stream
     */
    FileResult openOutputStream(String path, boolean append);

    /**
     * Writes a string to a file.
     *
     * @param path     The path of the file to write
     * @param content  The content to write to the file
     * @param charset  The charset to use for writing the file (default to UTF-8 if null)
     * @param append   Whether to append to the file if it exists
     * @return A FileResult indicating success or failure
     */
    FileResult writeString(String path, String content, Charset charset, boolean append);

    /**
     * Writes lines of text to a file.
     *
     * @param path     The path of the file to write
     * @param lines    The lines to write to the file
     * @param charset  The charset to use for writing the file (default to UTF-8 if null)
     * @param append   Whether to append to the file if it exists
     * @return A FileResult indicating success or failure
     */
    FileResult writeLines(String path, List<String> lines, Charset charset, boolean append);

    /**
     * Writes bytes to a file.
     *
     * @param path    The path of the file to write
     * @param bytes   The bytes to write to the file
     * @param append  Whether to append to the file if it exists
     * @return A FileResult indicating success or failure
     */
    FileResult writeBytes(String path, byte[] bytes, boolean append);

    /**
     * Gets the size of a file.
     *
     * @param path The path of the file
     * @return The size of the file in bytes, or -1 if the file does not exist
     */
    long size(String path);

    /**
     * Gets the last modified time of a file or directory.
     *
     * @param path The path of the file or directory
     * @return An Optional containing the last modified time, or empty if the file does not exist
     */
    Optional<Instant> getLastModifiedTime(String path);

    /**
     * Sets the last modified time of a file or directory.
     *
     * @param path The path of the file or directory
     * @param time The time to set
     * @return A FileResult indicating success or failure
     */
    FileResult setLastModifiedTime(String path, Instant time);

    /**
     * Creates a temporary file.
     *
     * @param prefix    The prefix of the file name
     * @param suffix    The suffix of the file name
     * @param directory The directory to create the file in, or null for the default temporary directory
     * @return A FileResult containing the path of the created file
     */
    FileResult createTempFile(String prefix, String suffix, String directory);

    /**
     * Creates a temporary directory.
     *
     * @param prefix    The prefix of the directory name
     * @param directory The directory to create the directory in, or null for the default temporary directory
     * @return A FileResult containing the path of the created directory
     */
    FileResult createTempDirectory(String prefix, String directory);

    /**
     * Normalizes a path, removing redundant elements.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    String normalize(String path);

    /**
     * Resolves a path against a base path.
     *
     * @param base The base path
     * @param path The path to resolve
     * @return The resolved path
     */
    String resolve(String base, String path);

    /**
     * Gets the absolute path of a file or directory.
     *
     * @param path The path to get the absolute path of
     * @return The absolute path
     */
    String getAbsolutePath(String path);

    /**
     * Checks if a path is absolute.
     *
     * @param path The path to check
     * @return True if the path is absolute, false otherwise
     */
    boolean isAbsolute(String path);

    /**
     * Gets the root directories of the file system.
     *
     * @return A list of root directory paths
     */
    List<String> getRootDirectories();

    /**
     * Gets the working directory.
     *
     * @return The working directory path
     */
    String getWorkingDirectory();

    /**
     * Sets the file permissions.
     *
     * @param path        The path of the file or directory
     * @param permissions The permissions to set as a string (e.g., "rwxr-xr--")
     * @return A FileResult indicating success or failure
     */
    FileResult setPermissions(String path, String permissions);

    /**
     * Gets the file permissions.
     *
     * @param path The path of the file or directory
     * @return A FileResult containing the permissions as a string
     */
    FileResult getPermissions(String path);

    /**
     * Sets the owner of a file or directory.
     *
     * @param path  The path of the file or directory
     * @param owner The owner to set
     * @return A FileResult indicating success or failure
     */
    FileResult setOwner(String path, String owner);

    /**
     * Gets the owner of a file or directory.
     *
     * @param path The path of the file or directory
     * @return A FileResult containing the owner
     */
    FileResult getOwner(String path);

    /**
     * Sets the group of a file or directory.
     *
     * @param path  The path of the file or directory
     * @param group The group to set
     * @return A FileResult indicating success or failure
     */
    FileResult setGroup(String path, String group);

    /**
     * Gets the group of a file or directory.
     *
     * @param path The path of the file or directory
     * @return A FileResult containing the group
     */
    FileResult getGroup(String path);

    /**
     * Creates a symbolic link.
     *
     * @param link   The path of the link to create
     * @param target The path of the target
     * @return A FileResult indicating success or failure
     */
    FileResult createSymbolicLink(String link, String target);

    /**
     * Creates a hard link.
     *
     * @param link   The path of the link to create
     * @param target The path of the target
     * @return A FileResult indicating success or failure
     */
    FileResult createLink(String link, String target);

    /**
     * Reads a symbolic link.
     *
     * @param path The path of the link to read
     * @return A FileResult containing the target of the link
     */
    FileResult readSymbolicLink(String path);

    /**
     * Gets the file system separator.
     *
     * @return The file system separator
     */
    String getSeparator();

    /**
     * Gets the file system path separator.
     *
     * @return The file system path separator
     */
    String getPathSeparator();

    /**
     * Initializes the file system port.
     *
     * @return A FileResult indicating success or failure
     */
    FileResult initialize();

    /**
     * Shuts down the file system port.
     *
     * @return A FileResult indicating success or failure
     */
    FileResult shutdown();
}