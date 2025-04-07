/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.infrastructure.filesystem;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Standard implementation of the FileSystemPort interface using Java NIO.
 * <p>
 * This adapter provides a robust implementation of file system operations
 * using Java's NIO package. It ensures proper error handling and logging
 * for all file operations.
 */
public class StandardFileSystemAdapter implements FileSystemPort {

    private final LoggerPort logger;
    private final Path workingDirectory;
    private boolean initialized = false;

    /**
     * Creates a new StandardFileSystemAdapter instance.
     *
     * @param logger The logger to use for logging
     */
    public StandardFileSystemAdapter(LoggerPort logger) {
        this.logger = logger;
        this.workingDirectory = Paths.get(System.getProperty("user.dir"));
    }

    /**
     * Creates a FileInfo object from a Path and its BasicFileAttributes.
     *
     * @param path       The path
     * @param attributes The file attributes
     * @return A new FileInfo instance
     */
    private FileInfo createFileInfo(Path path, BasicFileAttributes attributes) {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("isSymbolicLink", Files.isSymbolicLink(path));

        try {
            attrs.put("isReadable", Files.isReadable(path));
            attrs.put("isWritable", Files.isWritable(path));
            attrs.put("isExecutable", Files.isExecutable(path));
        } catch (Exception e) {
            logger.warn("Could not determine permissions for {}: {}", path, e.getMessage());
        }

        boolean isHidden = false;
        try {
            isHidden = Files.isHidden(path);
        } catch (IOException e) {
            logger.warn("Could not determine hidden status for {}: {}", path, e.getMessage());
        }

        return new FileInfo(
                path.toString(),
                attributes.isDirectory(),
                attributes.isRegularFile(),
                isHidden,
                attributes.size(),
                attributes.creationTime().toInstant(),
                attributes.lastModifiedTime().toInstant(),
                attributes.lastAccessTime().toInstant(),
                attrs
        );
    }

    @Override
    public FileResult getInfo(String path) {
        if (path == null) {
            return FileResult.failure("Could not get file info", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            BasicFileAttributes attributes = Files.readAttributes(filePath, BasicFileAttributes.class);
            FileInfo fileInfo = createFileInfo(filePath, attributes);
            return FileResult.success("File info retrieved successfully", Map.of("fileInfo", fileInfo));
        } catch (NoSuchFileException e) {
            return FileResult.failure("Could not get file info", "File does not exist: " + path);
        } catch (IOException e) {
            logger.error("Error getting file info for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not get file info", e.getMessage());
        }
    }

    @Override
    public boolean exists(String path) {
        if (path == null) {
            return false;
        }

        try {
            return Files.exists(Paths.get(path));
        } catch (InvalidPathException e) {
            logger.warn("Invalid path: {}", path);
            return false;
        }
    }

    @Override
    public FileResult createDirectory(String path) {
        if (path == null) {
            return FileResult.failure("Could not create directory", "Path cannot be null");
        }

        try {
            Path dirPath = Paths.get(path);
            if (Files.exists(dirPath)) {
                if (Files.isDirectory(dirPath)) {
                    return FileResult.success("Directory already exists");
                } else {
                    return FileResult.failure("Could not create directory", "A file with the same name already exists");
                }
            }

            Files.createDirectory(dirPath);
            return FileResult.success("Directory created successfully");
        } catch (IOException e) {
            logger.error("Error creating directory {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not create directory", e.getMessage());
        }
    }

    @Override
    public FileResult createDirectories(String path) {
        if (path == null) {
            return FileResult.failure("Could not create directories", "Path cannot be null");
        }

        try {
            Path dirPath = Paths.get(path);
            if (Files.exists(dirPath)) {
                if (Files.isDirectory(dirPath)) {
                    return FileResult.success("Directories already exist");
                } else {
                    return FileResult.failure("Could not create directories", "A file with the same name already exists");
                }
            }

            Files.createDirectories(dirPath);
            return FileResult.success("Directories created successfully");
        } catch (IOException e) {
            logger.error("Error creating directories {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not create directories", e.getMessage());
        }
    }

    @Override
    public FileResult delete(String path) {
        if (path == null) {
            return FileResult.failure("Could not delete file", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.success("File does not exist");
            }

            if (Files.isDirectory(filePath) && Files.list(filePath).findAny().isPresent()) {
                return FileResult.failure("Could not delete directory", "Directory is not empty");
            }

            Files.delete(filePath);
            return FileResult.success("File deleted successfully");
        } catch (IOException e) {
            logger.error("Error deleting file {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not delete file", e.getMessage());
        }
    }

    @Override
    public FileResult deleteRecursively(String path) {
        if (path == null) {
            return FileResult.failure("Could not delete directory recursively", "Path cannot be null");
        }

        try {
            Path dirPath = Paths.get(path);
            if (!Files.exists(dirPath)) {
                return FileResult.success("Directory does not exist");
            }

            if (!Files.isDirectory(dirPath)) {
                return FileResult.failure("Could not delete directory recursively", "Path is not a directory");
            }

            Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            logger.warn("Could not delete {}: {}", p, e.getMessage());
                        }
                    });

            return FileResult.success("Directory deleted successfully");
        } catch (IOException e) {
            logger.error("Error deleting directory recursively {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not delete directory recursively", e.getMessage());
        }
    }

    @Override
    public FileResult move(String source, String destination) {
        if (source == null || destination == null) {
            return FileResult.failure("Could not move file", "Source and destination cannot be null");
        }

        try {
            Path sourcePath = Paths.get(source);
            Path destPath = Paths.get(destination);

            if (!Files.exists(sourcePath)) {
                return FileResult.failure("Could not move file", "Source file does not exist");
            }

            if (Files.exists(destPath)) {
                return FileResult.failure("Could not move file", "Destination file already exists");
            }

            Files.move(sourcePath, destPath, StandardCopyOption.ATOMIC_MOVE);
            return FileResult.success("File moved successfully");
        } catch (IOException e) {
            logger.error("Error moving file from {} to {}: {}", source, destination, e.getMessage(), e);
            return FileResult.failure("Could not move file", e.getMessage());
        }
    }

    @Override
    public FileResult copy(String source, String destination) {
        if (source == null || destination == null) {
            return FileResult.failure("Could not copy file", "Source and destination cannot be null");
        }

        try {
            Path sourcePath = Paths.get(source);
            Path destPath = Paths.get(destination);

            if (!Files.exists(sourcePath)) {
                return FileResult.failure("Could not copy file", "Source file does not exist");
            }

            if (Files.isDirectory(sourcePath)) {
                return FileResult.failure("Could not copy file", "Source is a directory, use copyDirectory instead");
            }

            if (Files.exists(destPath) && Files.isDirectory(destPath)) {
                destPath = destPath.resolve(sourcePath.getFileName());
            }

            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            return FileResult.success("File copied successfully");
        } catch (IOException e) {
            logger.error("Error copying file from {} to {}: {}", source, destination, e.getMessage(), e);
            return FileResult.failure("Could not copy file", e.getMessage());
        }
    }

    @Override
    public FileResult copyDirectory(String source, String destination) {
        if (source == null || destination == null) {
            return FileResult.failure("Could not copy directory", "Source and destination cannot be null");
        }

        try {
            Path sourcePath = Paths.get(source);
            Path destPath = Paths.get(destination);

            if (!Files.exists(sourcePath)) {
                return FileResult.failure("Could not copy directory", "Source directory does not exist");
            }

            if (!Files.isDirectory(sourcePath)) {
                return FileResult.failure("Could not copy directory", "Source is not a directory");
            }

            if (Files.exists(destPath) && !Files.isDirectory(destPath)) {
                return FileResult.failure("Could not copy directory", "Destination exists and is not a directory");
            }

            if (!Files.exists(destPath)) {
                Files.createDirectories(destPath);
            }

            // Walk the source directory and copy each file/directory
            Files.walk(sourcePath)
                    .forEach(sourcedChild -> {
                        try {
                            Path targetChild = destPath.resolve(sourcePath.relativize(sourcedChild));
                            if (Files.isDirectory(sourcedChild)) {
                                if (!Files.exists(targetChild)) {
                                    Files.createDirectory(targetChild);
                                }
                            } else {
                                Files.copy(sourcedChild, targetChild, StandardCopyOption.REPLACE_EXISTING);
                            }
                        } catch (IOException e) {
                            logger.warn("Could not copy {}: {}", sourcedChild, e.getMessage());
                        }
                    });

            return FileResult.success("Directory copied successfully");
        } catch (IOException e) {
            logger.error("Error copying directory from {} to {}: {}", source, destination, e.getMessage(), e);
            return FileResult.failure("Could not copy directory", e.getMessage());
        }
    }

    @Override
    public FileResult list(String directory) {
        if (directory == null) {
            return FileResult.failure("Could not list directory", "Directory path cannot be null");
        }

        try {
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                return FileResult.failure("Could not list directory", "Directory does not exist");
            }

            if (!Files.isDirectory(dirPath)) {
                return FileResult.failure("Could not list directory", "Path is not a directory");
            }

            List<FileInfo> fileInfos = Files.list(dirPath)
                    .map(path -> {
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                            return createFileInfo(path, attrs);
                        } catch (IOException e) {
                            logger.warn("Could not read attributes for {}: {}", path, e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return FileResult.success("Directory listed successfully", Map.of("files", fileInfos));
        } catch (IOException e) {
            logger.error("Error listing directory {}: {}", directory, e.getMessage(), e);
            return FileResult.failure("Could not list directory", e.getMessage());
        }
    }

    @Override
    public FileResult search(String directory, String pattern, SearchMode searchMode) {
        if (directory == null || pattern == null) {
            return FileResult.failure("Could not search directory", "Directory path and pattern cannot be null");
        }

        try {
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                return FileResult.failure("Could not search directory", "Directory does not exist");
            }

            if (!Files.isDirectory(dirPath)) {
                return FileResult.failure("Could not search directory", "Path is not a directory");
            }

            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

            List<FileInfo> fileInfos;
            if (searchMode == SearchMode.CURRENT_DIRECTORY) {
                fileInfos = Files.list(dirPath)
                        .filter(path -> matcher.matches(path.getFileName()))
                        .map(path -> {
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                return createFileInfo(path, attrs);
                            } catch (IOException e) {
                                logger.warn("Could not read attributes for {}: {}", path, e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                fileInfos = Files.walk(dirPath)
                        .filter(path -> matcher.matches(path.getFileName()))
                        .map(path -> {
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                                return createFileInfo(path, attrs);
                            } catch (IOException e) {
                                logger.warn("Could not read attributes for {}: {}", path, e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            return FileResult.success("Search completed successfully", Map.of("files", fileInfos));
        } catch (IOException e) {
            logger.error("Error searching directory {}: {}", directory, e.getMessage(), e);
            return FileResult.failure("Could not search directory", e.getMessage());
        }
    }

    @Override
    public FileResult readString(String path, Charset charset) {
        if (path == null) {
            return FileResult.failure("Could not read file", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not read file", "File does not exist");
            }

            if (Files.isDirectory(filePath)) {
                return FileResult.failure("Could not read file", "Path is a directory");
            }

            Charset fileCharset = charset != null ? charset : StandardCharsets.UTF_8;
            String content = Files.readString(filePath, fileCharset);
            return FileResult.success("File read successfully", Map.of("content", content));
        } catch (IOException e) {
            logger.error("Error reading file {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not read file", e.getMessage());
        }
    }

    @Override
    public FileResult readLines(String path, Charset charset) {
        if (path == null) {
            return FileResult.failure("Could not read file lines", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not read file lines", "File does not exist");
            }

            if (Files.isDirectory(filePath)) {
                return FileResult.failure("Could not read file lines", "Path is a directory");
            }

            Charset fileCharset = charset != null ? charset : StandardCharsets.UTF_8;
            List<String> lines = Files.readAllLines(filePath, fileCharset);
            return FileResult.success("File lines read successfully", Map.of("lines", lines));
        } catch (IOException e) {
            logger.error("Error reading file lines {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not read file lines", e.getMessage());
        }
    }

    @Override
    public FileResult readLines(String path, Charset charset, long start, long count) {
        if (path == null) {
            return FileResult.failure("Could not read file lines", "Path cannot be null");
        }

        if (start < 0 || count < 0) {
            return FileResult.failure("Could not read file lines", "Start and count must be non-negative");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not read file lines", "File does not exist");
            }

            if (Files.isDirectory(filePath)) {
                return FileResult.failure("Could not read file lines", "Path is a directory");
            }

            Charset fileCharset = charset != null ? charset : StandardCharsets.UTF_8;
            List<String> lines;

            try (Stream<String> lineStream = Files.lines(filePath, fileCharset)) {
                lines = lineStream.skip(start).limit(count).collect(Collectors.toList());
            }

            return FileResult.success("File lines read successfully", Map.of(
                    "lines", lines,
                    "start", start,
                    "count", count
            ));
        } catch (IOException e) {
            logger.error("Error reading file lines {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not read file lines", e.getMessage());
        }
    }

    @Override
    public FileResult readBytes(String path) {
        if (path == null) {
            return FileResult.failure("Could not read file bytes", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not read file bytes", "File does not exist");
            }

            if (Files.isDirectory(filePath)) {
                return FileResult.failure("Could not read file bytes", "Path is a directory");
            }

            byte[] bytes = Files.readAllBytes(filePath);
            return FileResult.success("File bytes read successfully", Map.of("bytes", bytes));
        } catch (IOException e) {
            logger.error("Error reading file bytes {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not read file bytes", e.getMessage());
        }
    }

    @Override
    public FileResult openInputStream(String path) {
        if (path == null) {
            return FileResult.failure("Could not open input stream", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not open input stream", "File does not exist");
            }

            if (Files.isDirectory(filePath)) {
                return FileResult.failure("Could not open input stream", "Path is a directory");
            }

            InputStream inputStream = Files.newInputStream(filePath);
            return FileResult.success("Input stream opened successfully", Map.of("inputStream", inputStream));
        } catch (IOException e) {
            logger.error("Error opening input stream {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not open input stream", e.getMessage());
        }
    }

    @Override
    public FileResult openOutputStream(String path, boolean append) {
        if (path == null) {
            return FileResult.failure("Could not open output stream", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                return FileResult.failure("Could not open output stream", "Path is a directory");
            }

            OpenOption[] options = append
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            OutputStream outputStream = Files.newOutputStream(filePath, options);
            return FileResult.success("Output stream opened successfully", Map.of("outputStream", outputStream));
        } catch (IOException e) {
            logger.error("Error opening output stream {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not open output stream", e.getMessage());
        }
    }

    @Override
    public FileResult writeString(String path, String content, Charset charset, boolean append) {
        if (path == null || content == null) {
            return FileResult.failure("Could not write to file", "Path and content cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                return FileResult.failure("Could not write to file", "Path is a directory");
            }

            // Ensure parent directories exist
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Charset fileCharset = charset != null ? charset : StandardCharsets.UTF_8;
            OpenOption[] options = append
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            Files.writeString(filePath, content, fileCharset, options);
            return FileResult.success("File written successfully");
        } catch (IOException e) {
            logger.error("Error writing to file {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not write to file", e.getMessage());
        }
    }

    @Override
    public FileResult writeLines(String path, List<String> lines, Charset charset, boolean append) {
        if (path == null || lines == null) {
            return FileResult.failure("Could not write lines to file", "Path and lines cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                return FileResult.failure("Could not write lines to file", "Path is a directory");
            }

            // Ensure parent directories exist
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Charset fileCharset = charset != null ? charset : StandardCharsets.UTF_8;
            OpenOption[] options = append
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            Files.write(filePath, lines, fileCharset, options);
            return FileResult.success("File lines written successfully");
        } catch (IOException e) {
            logger.error("Error writing lines to file {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not write lines to file", e.getMessage());
        }
    }

    @Override
    public FileResult writeBytes(String path, byte[] bytes, boolean append) {
        if (path == null || bytes == null) {
            return FileResult.failure("Could not write bytes to file", "Path and bytes cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath) && Files.isDirectory(filePath)) {
                return FileResult.failure("Could not write bytes to file", "Path is a directory");
            }

            // Ensure parent directories exist
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            OpenOption[] options = append
                    ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                    : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

            Files.write(filePath, bytes, options);
            return FileResult.success("File bytes written successfully");
        } catch (IOException e) {
            logger.error("Error writing bytes to file {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not write bytes to file", e.getMessage());
        }
    }

    @Override
    public long size(String path) {
        if (path == null) {
            return -1;
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                return -1;
            }

            return Files.size(filePath);
        } catch (IOException e) {
            logger.warn("Error getting file size for {}: {}", path, e.getMessage());
            return -1;
        }
    }

    @Override
    public Optional<Instant> getLastModifiedTime(String path) {
        if (path == null) {
            return Optional.empty();
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return Optional.empty();
            }

            return Optional.of(Files.getLastModifiedTime(filePath).toInstant());
        } catch (IOException e) {
            logger.warn("Error getting last modified time for {}: {}", path, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public FileResult setLastModifiedTime(String path, Instant time) {
        if (path == null || time == null) {
            return FileResult.failure("Could not set last modified time", "Path and time cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not set last modified time", "File does not exist");
            }

            FileTime fileTime = FileTime.from(time);
            Files.setLastModifiedTime(filePath, fileTime);
            return FileResult.success("Last modified time set successfully");
        } catch (IOException e) {
            logger.error("Error setting last modified time for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not set last modified time", e.getMessage());
        }
    }

    @Override
    public FileResult createTempFile(String prefix, String suffix, String directory) {
        if (prefix == null) {
            return FileResult.failure("Could not create temporary file", "Prefix cannot be null");
        }

        try {
            Path tempFile;
            if (directory != null) {
                Path dirPath = Paths.get(directory);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                } else if (!Files.isDirectory(dirPath)) {
                    return FileResult.failure("Could not create temporary file", "Specified directory is not a directory");
                }
                tempFile = Files.createTempFile(dirPath, prefix, suffix);
            } else {
                tempFile = Files.createTempFile(prefix, suffix);
            }

            return FileResult.success("Temporary file created successfully", Map.of("path", tempFile.toString()));
        } catch (IOException e) {
            logger.error("Error creating temporary file: {}", e.getMessage(), e);
            return FileResult.failure("Could not create temporary file", e.getMessage());
        }
    }

    @Override
    public FileResult createTempDirectory(String prefix, String directory) {
        if (prefix == null) {
            return FileResult.failure("Could not create temporary directory", "Prefix cannot be null");
        }

        try {
            Path tempDir;
            if (directory != null) {
                Path dirPath = Paths.get(directory);
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                } else if (!Files.isDirectory(dirPath)) {
                    return FileResult.failure("Could not create temporary directory", "Specified directory is not a directory");
                }
                tempDir = Files.createTempDirectory(dirPath, prefix);
            } else {
                tempDir = Files.createTempDirectory(prefix);
            }

            return FileResult.success("Temporary directory created successfully", Map.of("path", tempDir.toString()));
        } catch (IOException e) {
            logger.error("Error creating temporary directory: {}", e.getMessage(), e);
            return FileResult.failure("Could not create temporary directory", e.getMessage());
        }
    }

    @Override
    public String normalize(String path) {
        if (path == null) {
            return null;
        }

        try {
            return Paths.get(path).normalize().toString();
        } catch (InvalidPathException e) {
            logger.warn("Invalid path for normalization: {}", path);
            return path;
        }
    }

    @Override
    public String resolve(String base, String path) {
        if (base == null || path == null) {
            return null;
        }

        try {
            return Paths.get(base).resolve(path).toString();
        } catch (InvalidPathException e) {
            logger.warn("Invalid path for resolution: {} or {}", base, path);
            return path;
        }
    }

    @Override
    public String getAbsolutePath(String path) {
        if (path == null) {
            return null;
        }

        try {
            return Paths.get(path).toAbsolutePath().toString();
        } catch (InvalidPathException e) {
            logger.warn("Invalid path for absolute path: {}", path);
            return path;
        }
    }

    @Override
    public boolean isAbsolute(String path) {
        if (path == null) {
            return false;
        }

        try {
            return Paths.get(path).isAbsolute();
        } catch (InvalidPathException e) {
            logger.warn("Invalid path for absolute check: {}", path);
            return false;
        }
    }

    @Override
    public List<String> getRootDirectories() {
        return StreamSupport.stream(FileSystems.getDefault().getRootDirectories().spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory.toString();
    }

    @Override
    public FileResult setPermissions(String path, String permissions) {
        if (path == null || permissions == null) {
            return FileResult.failure("Could not set permissions", "Path and permissions cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not set permissions", "File does not exist");
            }

            Set<PosixFilePermission> filePermissions = PosixFilePermissions.fromString(permissions);
            Files.setPosixFilePermissions(filePath, filePermissions);
            return FileResult.success("Permissions set successfully");
        } catch (UnsupportedOperationException e) {
            logger.warn("Setting POSIX file permissions is not supported on this platform");
            return FileResult.failure("Could not set permissions", "Setting POSIX file permissions is not supported on this platform");
        } catch (IllegalArgumentException e) {
            return FileResult.failure("Could not set permissions", "Invalid permissions format");
        } catch (IOException e) {
            logger.error("Error setting permissions for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not set permissions", e.getMessage());
        }
    }

    @Override
    public FileResult getPermissions(String path) {
        if (path == null) {
            return FileResult.failure("Could not get permissions", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not get permissions", "File does not exist");
            }

            Set<PosixFilePermission> filePermissions = Files.getPosixFilePermissions(filePath);
            String permissionsString = PosixFilePermissions.toString(filePermissions);
            return FileResult.success("Permissions retrieved successfully", Map.of("permissions", permissionsString));
        } catch (UnsupportedOperationException e) {
            logger.warn("Getting POSIX file permissions is not supported on this platform");
            return FileResult.failure("Could not get permissions", "Getting POSIX file permissions is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error getting permissions for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not get permissions", e.getMessage());
        }
    }

    @Override
    public FileResult setOwner(String path, String owner) {
        if (path == null || owner == null) {
            return FileResult.failure("Could not set owner", "Path and owner cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not set owner", "File does not exist");
            }

            UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
            UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(owner);
            Files.setOwner(filePath, userPrincipal);
            return FileResult.success("Owner set successfully");
        } catch (UnsupportedOperationException e) {
            logger.warn("Setting file owner is not supported on this platform");
            return FileResult.failure("Could not set owner", "Setting file owner is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error setting owner for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not set owner", e.getMessage());
        }
    }

    @Override
    public FileResult getOwner(String path) {
        if (path == null) {
            return FileResult.failure("Could not get owner", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not get owner", "File does not exist");
            }

            UserPrincipal owner = Files.getOwner(filePath);
            return FileResult.success("Owner retrieved successfully", Map.of("owner", owner.getName()));
        } catch (UnsupportedOperationException e) {
            logger.warn("Getting file owner is not supported on this platform");
            return FileResult.failure("Could not get owner", "Getting file owner is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error getting owner for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not get owner", e.getMessage());
        }
    }

    @Override
    public FileResult setGroup(String path, String group) {
        if (path == null || group == null) {
            return FileResult.failure("Could not set group", "Path and group cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not set group", "File does not exist");
            }

            UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
            GroupPrincipal groupPrincipal = lookupService.lookupPrincipalByGroupName(group);
            
            PosixFileAttributeView view = Files.getFileAttributeView(filePath, PosixFileAttributeView.class);
            if (view == null) {
                return FileResult.failure("Could not set group", "POSIX file attributes not supported on this platform");
            }
            
            view.setGroup(groupPrincipal);
            return FileResult.success("Group set successfully");
        } catch (UnsupportedOperationException e) {
            logger.warn("Setting file group is not supported on this platform");
            return FileResult.failure("Could not set group", "Setting file group is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error setting group for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not set group", e.getMessage());
        }
    }

    @Override
    public FileResult getGroup(String path) {
        if (path == null) {
            return FileResult.failure("Could not get group", "Path cannot be null");
        }

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return FileResult.failure("Could not get group", "File does not exist");
            }

            PosixFileAttributeView view = Files.getFileAttributeView(filePath, PosixFileAttributeView.class);
            if (view == null) {
                return FileResult.failure("Could not get group", "POSIX file attributes not supported on this platform");
            }
            
            GroupPrincipal group = view.readAttributes().group();
            return FileResult.success("Group retrieved successfully", Map.of("group", group.getName()));
        } catch (UnsupportedOperationException e) {
            logger.warn("Getting file group is not supported on this platform");
            return FileResult.failure("Could not get group", "Getting file group is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error getting group for {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not get group", e.getMessage());
        }
    }

    @Override
    public FileResult createSymbolicLink(String link, String target) {
        if (link == null || target == null) {
            return FileResult.failure("Could not create symbolic link", "Link and target cannot be null");
        }

        try {
            Path linkPath = Paths.get(link);
            Path targetPath = Paths.get(target);

            if (Files.exists(linkPath)) {
                return FileResult.failure("Could not create symbolic link", "Link already exists");
            }

            // Ensure parent directories exist
            Path parent = linkPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.createSymbolicLink(linkPath, targetPath);
            return FileResult.success("Symbolic link created successfully");
        } catch (UnsupportedOperationException e) {
            logger.warn("Creating symbolic links is not supported on this platform");
            return FileResult.failure("Could not create symbolic link", "Creating symbolic links is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error creating symbolic link from {} to {}: {}", link, target, e.getMessage(), e);
            return FileResult.failure("Could not create symbolic link", e.getMessage());
        }
    }

    @Override
    public FileResult createLink(String link, String target) {
        if (link == null || target == null) {
            return FileResult.failure("Could not create hard link", "Link and target cannot be null");
        }

        try {
            Path linkPath = Paths.get(link);
            Path targetPath = Paths.get(target);

            if (Files.exists(linkPath)) {
                return FileResult.failure("Could not create hard link", "Link already exists");
            }

            if (!Files.exists(targetPath)) {
                return FileResult.failure("Could not create hard link", "Target does not exist");
            }

            // Ensure parent directories exist
            Path parent = linkPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Files.createLink(linkPath, targetPath);
            return FileResult.success("Hard link created successfully");
        } catch (UnsupportedOperationException e) {
            logger.warn("Creating hard links is not supported on this platform");
            return FileResult.failure("Could not create hard link", "Creating hard links is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error creating hard link from {} to {}: {}", link, target, e.getMessage(), e);
            return FileResult.failure("Could not create hard link", e.getMessage());
        }
    }

    @Override
    public FileResult readSymbolicLink(String path) {
        if (path == null) {
            return FileResult.failure("Could not read symbolic link", "Path cannot be null");
        }

        try {
            Path linkPath = Paths.get(path);
            if (!Files.exists(linkPath)) {
                return FileResult.failure("Could not read symbolic link", "Link does not exist");
            }

            if (!Files.isSymbolicLink(linkPath)) {
                return FileResult.failure("Could not read symbolic link", "Path is not a symbolic link");
            }

            Path targetPath = Files.readSymbolicLink(linkPath);
            return FileResult.success("Symbolic link read successfully", Map.of("target", targetPath.toString()));
        } catch (UnsupportedOperationException e) {
            logger.warn("Reading symbolic links is not supported on this platform");
            return FileResult.failure("Could not read symbolic link", "Reading symbolic links is not supported on this platform");
        } catch (IOException e) {
            logger.error("Error reading symbolic link {}: {}", path, e.getMessage(), e);
            return FileResult.failure("Could not read symbolic link", e.getMessage());
        }
    }

    @Override
    public String getSeparator() {
        return File.separator;
    }

    @Override
    public String getPathSeparator() {
        return File.pathSeparator;
    }

    @Override
    public FileResult initialize() {
        if (initialized) {
            return FileResult.success("File system adapter already initialized");
        }

        try {
            // Check if the file system is accessible by checking the working directory
            if (!Files.exists(workingDirectory)) {
                return FileResult.failure("Could not initialize file system adapter", "Working directory does not exist");
            }

            initialized = true;
            logger.info("File system adapter initialized successfully");
            return FileResult.success("File system adapter initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing file system adapter: {}", e.getMessage(), e);
            return FileResult.failure("Could not initialize file system adapter", e.getMessage());
        }
    }

    @Override
    public FileResult shutdown() {
        if (!initialized) {
            return FileResult.success("File system adapter not initialized");
        }

        try {
            // Perform any cleanup operations here
            initialized = false;
            logger.info("File system adapter shut down successfully");
            return FileResult.success("File system adapter shut down successfully");
        } catch (Exception e) {
            logger.error("Error shutting down file system adapter: {}", e.getMessage(), e);
            return FileResult.failure("Could not shut down file system adapter", e.getMessage());
        }
    }
}