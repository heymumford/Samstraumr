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
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.application.service;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ProjectInitializationPort;
import org.s8r.domain.exception.ComponentInitializationException;

/**
 * Service responsible for coordinating the initialization of a new Samstraumr project. This class
 * implements the application use case for project initialization.
 */
public class ProjectInitializationService {
  private final ProjectInitializationPort initializationPort;
  private final LoggerPort logger;
  private static final String DEFAULT_PACKAGE = "org.example";

  /**
   * Creates a new project initialization service.
   *
   * @param initializationPort the port for project initialization operations
   * @param logger the logger to use
   */
  public ProjectInitializationService(
      ProjectInitializationPort initializationPort, LoggerPort logger) {
    this.initializationPort = initializationPort;
    this.logger = logger;
  }

  /**
   * Initializes a new Samstraumr project at the specified path with the default package.
   *
   * @param projectPath the path where the project should be initialized
   * @return true if initialization succeeded, false otherwise
   */
  public boolean initializeProject(String projectPath) {
    return initializeProject(projectPath, DEFAULT_PACKAGE);
  }

  /**
   * Initializes a new Samstraumr project at the specified path with the given package.
   *
   * @param projectPath the path where the project should be initialized
   * @param packageName the base package name for the project
   * @return true if initialization succeeded, false otherwise
   */
  public boolean initializeProject(String projectPath, String packageName) {
    try {
      logger.info("Initializing Samstraumr project at {}", projectPath);

      // Verify this is a valid location for initialization
      if (!initializationPort.isValidRepository(projectPath)) {
        logger.error(
            "Not a valid repository for initialization. Please ensure it's a git repository.");
        return false;
      }

      // Check if a Samstraumr project already exists
      if (initializationPort.isExistingProject(projectPath)) {
        logger.error("Samstraumr project already exists at this location.");
        return false;
      }

      // Create directory structure
      logger.info("Creating directory structure");
      initializationPort.createDirectoryStructure(projectPath, packageName);

      // Create Adam component
      logger.info("Creating Adam component");
      initializationPort.createAdamComponent(projectPath, packageName);

      // Create configuration files
      logger.info("Creating configuration files");
      initializationPort.createConfigurationFiles(projectPath, packageName);

      logger.info("Samstraumr project initialization completed successfully");
      return true;
    } catch (ComponentInitializationException e) {
      logger.error("Failed to initialize Samstraumr project", e);
      return false;
    }
  }
}
