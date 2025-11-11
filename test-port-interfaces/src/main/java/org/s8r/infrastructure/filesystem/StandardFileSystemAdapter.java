/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.filesystem;

import org.s8r.application.port.FileSystemPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Standard implementation of the FileSystemPort interface.
 * 
 * <p>This adapter provides file system operations using the Java NIO.2 API.
 */
public class StandardFileSystemAdapter implements FileSystemPort {
    
    /**
     * Default constructor for tests. Creates a new StandardFileSystemAdapter.
     */
    public StandardFileSystemAdapter() {
    }

    @Override
    public Optional<String> readFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        
        try {
            String content = Files.readString(path);
            return Optional.of(content);
        } catch (IOException e) {
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
            throw e;
        }
    }
    
    @Override
    public FileResult createDirectory(String path) {
        try {
            Path dirPath = Path.of(path);
            boolean created = createDirectory(dirPath);
            return new SimpleFileResult(created, created ? 
                "Directory created successfully" : "Failed to create directory");
        } catch (IOException e) {
            return new SimpleFileResult(false, "Failed to create directory", e.getMessage());
        }
    }

    @Override
    public List<Path> listFiles(Path path) throws IOException {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return List.of();
        }
        
        try {
            return Files.list(path).collect(Collectors.toList());
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean delete(Path path) throws IOException {
        if (!Files.exists(path)) {
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
                            .forEach(p -> {
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
            throw e;
        }
    }
}