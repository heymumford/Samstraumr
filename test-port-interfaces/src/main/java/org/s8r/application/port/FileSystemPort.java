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

package org.s8r.application.port;

/**
 * Port interface for file system operations.
 * Provides methods for reading, writing, and manipulating files and directories.
 */
public interface FileSystemPort {
    
    /**
     * Reads a file from the file system.
     *
     * @param filePath The path to the file
     * @return The result of the operation
     */
    FileSystemResult readFile(String filePath);
    
    /**
     * Writes content to a file.
     *
     * @param filePath The path to the file
     * @param content The content to write
     * @return The result of the operation
     */
    FileSystemResult writeFile(String filePath, String content);
    
    /**
     * Appends content to a file.
     *
     * @param filePath The path to the file
     * @param content The content to append
     * @return The result of the operation
     */
    FileSystemResult appendToFile(String filePath, String content);
    
    /**
     * Checks if a file exists.
     *
     * @param filePath The path to the file
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String filePath);
    
    /**
     * Checks if a directory exists.
     *
     * @param directoryPath The path to the directory
     * @return true if the directory exists, false otherwise
     */
    boolean directoryExists(String directoryPath);
    
    /**
     * Creates a directory.
     *
     * @param directoryPath The path to the directory
     * @return The result of the operation
     */
    FileSystemResult createDirectory(String directoryPath);
    
    /**
     * Lists files in a directory.
     *
     * @param directoryPath The path to the directory
     * @return The result of the operation
     */
    FileSystemResult listFiles(String directoryPath);
    
    /**
     * Deletes a file.
     *
     * @param filePath The path to the file
     * @return The result of the operation
     */
    FileSystemResult deleteFile(String filePath);
    
    /**
     * Deletes a directory and its contents.
     *
     * @param directoryPath The path to the directory
     * @return The result of the operation
     */
    FileSystemResult deleteDirectory(String directoryPath);
    
    /**
     * Moves a file from one location to another.
     *
     * @param sourcePath The source path
     * @param destinationPath The destination path
     * @return The result of the operation
     */
    FileSystemResult moveFile(String sourcePath, String destinationPath);
    
    /**
     * Copies a file from one location to another.
     *
     * @param sourcePath The source path
     * @param destinationPath The destination path
     * @return The result of the operation
     */
    FileSystemResult copyFile(String sourcePath, String destinationPath);
    
    /**
     * Gets information about a file.
     *
     * @param filePath The path to the file
     * @return The result of the operation
     */
    FileSystemResult getFileInfo(String filePath);
}