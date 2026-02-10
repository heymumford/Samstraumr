/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.infrastructure.filesystem;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.FileSystemResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buffered implementation of the FileSystemPort interface.
 * This adapter provides a file system implementation with buffered I/O for
 * improved performance.
 */
public class BufferedFileSystemAdapter implements FileSystemPort {

    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private final int bufferSize;
    private final Map<String, FileOutputStream> openWriteStreams;
    private final Map<String, FileInputStream> openReadStreams;
    
    /**
     * Creates a new BufferedFileSystemAdapter with default settings.
     */
    public BufferedFileSystemAdapter() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Creates a new BufferedFileSystemAdapter with the specified buffer size.
     *
     * @param bufferSize The buffer size to use for I/O operations
     */
    public BufferedFileSystemAdapter(int bufferSize) {
        this.bufferSize = bufferSize;
        this.openWriteStreams = new ConcurrentHashMap<>();
        this.openReadStreams = new ConcurrentHashMap<>();
    }
    
    /**
     * Creates a new instance of the BufferedFileSystemAdapter.
     *
     * @return A new BufferedFileSystemAdapter instance
     */
    public static BufferedFileSystemAdapter createInstance() {
        return new BufferedFileSystemAdapter();
    }
    
    /**
     * Creates a temporary directory for testing.
     *
     * @param dirName The name of the directory to create
     * @return The path to the created directory
     */
    public String createTemporaryDirectory(String dirName) {
        try {
            Path tempDir = Files.createTempDirectory(dirName);
            return tempDir.toString();
        } catch (IOException e) {
            return System.getProperty("java.io.tmpdir") + File.separator + dirName;
        }
    }

    @Override
    public FileSystemResult readFile(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return FileSystemResult.failure("File does not exist", "Path: " + filePath);
        }
        
        if (!file.isFile()) {
            return FileSystemResult.failure("Path is not a file", "Path: " + filePath);
        }
        
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr, bufferSize)) {
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            
            // Remove the last line separator if content is not empty
            if (content.length() > 0) {
                content.setLength(content.length() - System.lineSeparator().length());
            }
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("content", content.toString());
            attributes.put("size", file.length());
            attributes.put("lastModified", file.lastModified());
            
            return FileSystemResult.success("File read successfully", attributes);
        } catch (IOException e) {
            return FileSystemResult.failure("Error reading file", e.getMessage());
        }
    }

    @Override
    public FileSystemResult writeFile(String filePath, String content) {
        File file = new File(filePath);
        
        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                return FileSystemResult.failure("Failed to create parent directories", "Path: " + parentDir.getPath());
            }
        }
        
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw, bufferSize)) {
            
            writer.write(content);
            writer.flush();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("size", file.length());
            attributes.put("path", file.getAbsolutePath());
            
            return FileSystemResult.success("File written successfully", attributes);
        } catch (IOException e) {
            return FileSystemResult.failure("Error writing file", e.getMessage());
        }
    }

    @Override
    public FileSystemResult appendToFile(String filePath, String content) {
        File file = new File(filePath);
        
        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                return FileSystemResult.failure("Failed to create parent directories", "Path: " + parentDir.getPath());
            }
        }
        
        try (FileOutputStream fos = new FileOutputStream(file, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw, bufferSize)) {
            
            writer.write(content);
            writer.flush();
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("size", file.length());
            attributes.put("path", file.getAbsolutePath());
            
            return FileSystemResult.success("Content appended successfully", attributes);
        } catch (IOException e) {
            return FileSystemResult.failure("Error appending to file", e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    @Override
    public boolean directoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }

    @Override
    public FileSystemResult createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (directory.exists()) {
            if (directory.isDirectory()) {
                return FileSystemResult.success("Directory already exists", Map.of("path", directory.getAbsolutePath()));
            } else {
                return FileSystemResult.failure("Path exists but is not a directory", "Path: " + directoryPath);
            }
        }
        
        boolean created = directory.mkdirs();
        if (created) {
            return FileSystemResult.success("Directory created successfully", Map.of("path", directory.getAbsolutePath()));
        } else {
            return FileSystemResult.failure("Failed to create directory", "Path: " + directoryPath);
        }
    }

    @Override
    public FileSystemResult listFiles(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (!directory.exists()) {
            return FileSystemResult.failure("Directory does not exist", "Path: " + directoryPath);
        }
        
        if (!directory.isDirectory()) {
            return FileSystemResult.failure("Path is not a directory", "Path: " + directoryPath);
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return FileSystemResult.failure("Error listing files", "Path: " + directoryPath);
        }
        
        String[] fileNames = Arrays.stream(files)
            .filter(File::isFile)
            .map(File::getName)
            .toArray(String[]::new);
        
        String[] directoryNames = Arrays.stream(files)
            .filter(File::isDirectory)
            .map(File::getName)
            .toArray(String[]::new);
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("files", fileNames);
        attributes.put("directories", directoryNames);
        attributes.put("fileCount", fileNames.length);
        attributes.put("directoryCount", directoryNames.length);
        
        return FileSystemResult.success("Files listed successfully", attributes);
    }

    @Override
    public FileSystemResult deleteFile(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return FileSystemResult.success("File does not exist", Map.of("path", filePath));
        }
        
        if (!file.isFile()) {
            return FileSystemResult.failure("Path is not a file", "Path: " + filePath);
        }
        
        // Close any open streams for this file
        closeFile(filePath);
        
        boolean deleted = file.delete();
        if (deleted) {
            return FileSystemResult.success("File deleted successfully", Map.of("path", filePath));
        } else {
            return FileSystemResult.failure("Failed to delete file", "Path: " + filePath);
        }
    }

    @Override
    public FileSystemResult deleteDirectory(String directoryPath) {
        return deleteDirectoryRecursively(directoryPath);
    }
    
    private FileSystemResult deleteDirectoryRecursively(String directoryPath) {
        File directory = new File(directoryPath);
        
        if (!directory.exists()) {
            return FileSystemResult.success("Directory does not exist", Map.of("path", directoryPath));
        }
        
        if (!directory.isDirectory()) {
            return FileSystemResult.failure("Path is not a directory", "Path: " + directoryPath);
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return FileSystemResult.failure("Error listing files in directory", "Path: " + directoryPath);
        }
        
        // Delete all files and subdirectories
        for (File file : files) {
            if (file.isDirectory()) {
                FileSystemResult result = deleteDirectoryRecursively(file.getAbsolutePath());
                if (!result.isSuccessful()) {
                    return result;
                }
            } else {
                // Close any open streams for this file
                closeFile(file.getAbsolutePath());
                
                boolean deleted = file.delete();
                if (!deleted) {
                    return FileSystemResult.failure(
                        "Failed to delete file in directory",
                        "Path: " + file.getAbsolutePath()
                    );
                }
            }
        }
        
        // Delete the empty directory
        boolean deleted = directory.delete();
        if (deleted) {
            return FileSystemResult.success("Directory deleted successfully", Map.of("path", directoryPath));
        } else {
            return FileSystemResult.failure("Failed to delete directory", "Path: " + directoryPath);
        }
    }

    @Override
    public FileSystemResult moveFile(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        
        if (!sourceFile.exists()) {
            return FileSystemResult.failure("Source file does not exist", "Path: " + sourcePath);
        }
        
        if (!sourceFile.isFile()) {
            return FileSystemResult.failure("Source path is not a file", "Path: " + sourcePath);
        }
        
        // Ensure parent directories exist
        File parentDir = destinationFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                return FileSystemResult.failure(
                    "Failed to create parent directories for destination",
                    "Path: " + parentDir.getPath()
                );
            }
        }
        
        // Close any open streams for the source file
        closeFile(sourcePath);
        
        try {
            Files.move(
                Paths.get(sourcePath),
                Paths.get(destinationPath),
                StandardCopyOption.REPLACE_EXISTING
            );
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sourcePath", sourcePath);
            attributes.put("destinationPath", destinationPath);
            
            return FileSystemResult.success("File moved successfully", attributes);
        } catch (IOException e) {
            return FileSystemResult.failure("Error moving file", e.getMessage());
        }
    }

    @Override
    public FileSystemResult copyFile(String sourcePath, String destinationPath) {
        File sourceFile = new File(sourcePath);
        File destinationFile = new File(destinationPath);
        
        if (!sourceFile.exists()) {
            return FileSystemResult.failure("Source file does not exist", "Path: " + sourcePath);
        }
        
        if (!sourceFile.isFile()) {
            return FileSystemResult.failure("Source path is not a file", "Path: " + sourcePath);
        }
        
        // Ensure parent directories exist
        File parentDir = destinationFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                return FileSystemResult.failure(
                    "Failed to create parent directories for destination",
                    "Path: " + parentDir.getPath()
                );
            }
        }
        
        try {
            Files.copy(
                Paths.get(sourcePath),
                Paths.get(destinationPath),
                StandardCopyOption.REPLACE_EXISTING
            );
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sourcePath", sourcePath);
            attributes.put("destinationPath", destinationPath);
            attributes.put("size", destinationFile.length());
            
            return FileSystemResult.success("File copied successfully", attributes);
        } catch (IOException e) {
            return FileSystemResult.failure("Error copying file", e.getMessage());
        }
    }

    @Override
    public FileSystemResult getFileInfo(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return FileSystemResult.failure("File does not exist", "Path: " + filePath);
        }
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("path", file.getAbsolutePath());
        attributes.put("name", file.getName());
        attributes.put("size", file.length());
        attributes.put("lastModified", file.lastModified());
        attributes.put("isFile", file.isFile());
        attributes.put("isDirectory", file.isDirectory());
        attributes.put("isHidden", file.isHidden());
        
        if (file.isFile()) {
            attributes.put("extension", getFileExtension(file.getName()));
        }
        
        return FileSystemResult.success("File info retrieved successfully", attributes);
    }
    
    /**
     * Gets the file extension of a file name.
     *
     * @param fileName The file name
     * @return The file extension, or an empty string if there is no extension
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
    
    /**
     * Closes any open streams for the specified file.
     *
     * @param filePath The file path
     */
    private void closeFile(String filePath) {
        FileOutputStream writeStream = openWriteStreams.remove(filePath);
        if (writeStream != null) {
            try {
                writeStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        
        FileInputStream readStream = openReadStreams.remove(filePath);
        if (readStream != null) {
            try {
                readStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}