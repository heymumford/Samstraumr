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

package org.s8r.test.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.UUID;

/**
 * Utility class for file system operations in tests.
 * Provides helper methods for working with files and directories in test scenarios.
 * This class is designed to be used by both JUnit and Karate tests.
 */
public class FileSystemTestUtils {
    
    /**
     * Combines path components into a single path, ensuring proper separators.
     * 
     * @param base The base path
     * @param components Additional path components to combine
     * @return The combined path
     */
    public static String combinePaths(String base, String... components) {
        Path result = Paths.get(base);
        
        for (String component : components) {
            result = result.resolve(component);
        }
        
        return result.toString();
    }
    
    /**
     * Creates a temporary test directory.
     * 
     * @param prefix The directory name prefix
     * @return The path to the created directory
     */
    public static String createTempDirectory(String prefix) {
        try {
            Path tempDir = Files.createTempDirectory(prefix);
            return tempDir.toAbsolutePath().toString();
        } catch (IOException e) {
            // Fallback to Java's temp directory if creating with Files API fails
            String tmpDir = System.getProperty("java.io.tmpdir");
            String dirName = prefix + "-" + System.currentTimeMillis();
            String fullPath = combinePaths(tmpDir, dirName);
            
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            return fullPath;
        }
    }
    
    /**
     * Creates a temporary file in the specified directory.
     *
     * @param directory The directory in which to create the file
     * @param filename The name of the file to create
     * @return The absolute path to the created file
     * @throws IOException If an I/O error occurs
     */
    public static String createTempFile(String directory, String filename) throws IOException {
        Path dirPath = Paths.get(directory);
        Path filePath = dirPath.resolve(filename);
        Files.createFile(filePath);
        return filePath.toAbsolutePath().toString();
    }

    /**
     * Creates a file with the given content.
     *
     * @param directory The directory in which to create the file
     * @param filename The name of the file to create
     * @param content The content to write to the file
     * @return The absolute path to the created file
     * @throws IOException If an I/O error occurs
     */
    public static String createFileWithContent(String directory, String filename, String content) throws IOException {
        Path dirPath = Paths.get(directory);
        Path filePath = dirPath.resolve(filename);
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(content);
        }
        
        return filePath.toAbsolutePath().toString();
    }
    
    /**
     * Gets the parent directory of a path.
     * 
     * @param path The path
     * @return The parent directory
     */
    public static String getParentDirectory(String path) {
        return Paths.get(path).getParent().toString();
    }
    
    /**
     * Gets the file name from a path.
     * 
     * @param path The path
     * @return The file name
     */
    public static String getFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }
    
    /**
     * Reads the content of a file.
     *
     * @param filePath The path to the file to read
     * @return The content of the file as a string
     * @throws IOException If an I/O error occurs
     */
    public static String readFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Deletes a file.
     *
     * @param filePath The path to the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }

    /**
     * Deletes a directory and all its contents recursively.
     *
     * @param dirPath The path to the directory to delete
     * @return true if the directory was deleted, false otherwise
     */
    public static boolean deleteDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }
        
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file.getAbsolutePath());
                } else {
                    file.delete();
                }
            }
        }
        
        return dir.delete();
    }

    /**
     * Checks if a file exists.
     *
     * @param filePath The path to the file to check
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Gets the file size in bytes.
     *
     * @param filePath The path to the file
     * @return The size of the file in bytes, or -1 if the file does not exist
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }
        return file.length();
    }

    /**
     * Stores Java Properties to a file.
     *
     * @param properties The Properties object to store
     * @param filePath The path to the file
     * @param comments Comments to include in the properties file
     * @throws IOException If an I/O error occurs
     */
    public static void storeProperties(Properties properties, String filePath, String comments) throws IOException {
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, comments);
        }
    }

    /**
     * Loads Java Properties from a file.
     *
     * @param properties The Properties object to load into
     * @param filePath The path to the file
     * @throws IOException If an I/O error occurs
     */
    public static void loadProperties(Properties properties, String filePath) throws IOException {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
    }

    /**
     * Generates a unique file name with the given prefix and extension.
     *
     * @param prefix The prefix for the file name
     * @param extension The file extension (without the dot)
     * @return A unique file name
     */
    public static String generateUniqueFileName(String prefix, String extension) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return prefix + "-" + uuid + "." + extension;
    }
}