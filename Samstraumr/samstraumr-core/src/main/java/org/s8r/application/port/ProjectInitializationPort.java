/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.application.port;

import org.s8r.domain.exception.ComponentInitializationException;

/**
 * Port interface for project initialization operations.
 * <p>
 * This interface defines the operations required to initialize a Samstraumr project,
 * following the Ports and Adapters pattern from Clean Architecture. It allows the
 * application layer to coordinate project initialization without depending on
 * infrastructure details.
 */
public interface ProjectInitializationPort {
    
    /**
     * Checks if the specified path is a valid repository for Samstraumr initialization.
     * 
     * @param projectPath the path to check
     * @return true if the path is a valid repository, false otherwise
     */
    boolean isValidRepository(String projectPath);
    
    /**
     * Checks if a Samstraumr project already exists at the specified path.
     * 
     * @param projectPath the path to check
     * @return true if a Samstraumr project exists, false otherwise
     */
    boolean isExistingProject(String projectPath);
    
    /**
     * Creates the directory structure for a new Samstraumr project.
     * 
     * @param projectPath the project path
     * @param packageName the base package name
     * @throws ComponentInitializationException if an error occurs during directory creation
     */
    void createDirectoryStructure(String projectPath, String packageName) throws ComponentInitializationException;
    
    /**
     * Creates an Adam component in the new project.
     * 
     * @param projectPath the project path
     * @param packageName the base package name
     * @throws ComponentInitializationException if an error occurs during component creation
     */
    void createAdamComponent(String projectPath, String packageName) throws ComponentInitializationException;
    
    /**
     * Creates configuration files for the new project.
     * 
     * @param projectPath the project path
     * @param packageName the base package name
     * @throws ComponentInitializationException if an error occurs during file creation
     */
    void createConfigurationFiles(String projectPath, String packageName) throws ComponentInitializationException;
}