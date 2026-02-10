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

package org.s8r.adapter.in.cli;

import org.s8r.application.ServiceLocator;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.service.ProjectInitializationService;

/**
 * Command-line adapter for project initialization.
 *
 * <p>This class provides the entry point for initializing a Samstraumr project from the command
 * line. It follows the Adapter pattern from Clean Architecture, connecting external interfaces
 * (CLI) to the application layer.
 */
public class InitProjectCommand {

  /**
   * Main entry point for the command-line initializer.
   *
   * @param args Command line arguments. args[0] should be the repository path, and args[1]
   *     (optional) should be the package name.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: s8r init <project-path> [package-name]");
      System.exit(1);
    }

    String projectPath = args[0];
    String packageName = args.length > 1 ? args[1] : null;

    // Get services from the ServiceLocator
    LoggerPort logger = ServiceLocator.getServiceFactory().getLogger(InitProjectCommand.class);
    ProjectInitializationService initService =
        ServiceLocator.getServiceFactory().getService(ProjectInitializationService.class);

    // Initialize the project
    boolean success;
    if (packageName != null) {
      logger.info("Initializing project at {} with package {}", projectPath, packageName);
      success = initService.initializeProject(projectPath, packageName);
    } else {
      logger.info("Initializing project at {} with default package", projectPath);
      success = initService.initializeProject(projectPath);
    }

    // Exit with appropriate status
    if (!success) {
      System.exit(1);
    }

    System.out.println("Project initialized successfully at " + projectPath);
  }
}
