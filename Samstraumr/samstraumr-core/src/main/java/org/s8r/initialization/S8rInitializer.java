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

package org.s8r.initialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes a new Samstraumr repository with the necessary directory structure and configuration
 * files.
 * 
 * @deprecated This class is part of the legacy initialization package and has been replaced by the
 *             Clean Architecture implementation in org.s8r.application.service.ProjectInitializationService.
 *             Use that class instead for new code.
 */
@Deprecated
public class S8rInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(S8rInitializer.class);

  private final String repoPath;
  private final String packageName;
  private static final String DEFAULT_PACKAGE = "org.example";
  
  // Delegate to the Clean Architecture implementation
  private final org.s8r.application.service.ProjectInitializationService initService;

  /**
   * Creates a new initializer for the specified repository path with default package.
   *
   * @param repoPath the repository path
   */
  public S8rInitializer(String repoPath) {
    this(repoPath, DEFAULT_PACKAGE);
  }

  /**
   * Creates a new initializer for the specified repository path and package name.
   *
   * @param repoPath the repository path
   * @param packageName the package name to use
   */
  public S8rInitializer(String repoPath, String packageName) {
    this.repoPath = repoPath;
    this.packageName = packageName;
    
    // Get the Clean Architecture implementation from the ServiceLocator
    this.initService = org.s8r.application.ServiceLocator.getServiceFactory()
        .getService(org.s8r.application.service.ProjectInitializationService.class);
    
    if (this.initService == null) {
      LOGGER.warn("Project initialization service not found in ServiceLocator. Using legacy implementation.");
    }
  }

  /**
   * Initializes a Samstraumr repository.
   *
   * @return true if initialization was successful, false otherwise
   * @deprecated Use the ProjectInitializationService in the application layer instead.
   */
  @Deprecated
  public boolean initialize() {
    // If the Clean Architecture implementation is available, delegate to it
    if (initService != null) {
      LOGGER.info("Delegating to Clean Architecture implementation");
      return initService.initializeProject(repoPath, packageName);
    }
    
    // Legacy implementation when the Clean Architecture service is not available
    LOGGER.warn("Using legacy initialization implementation");
    try {
      System.out.println("Initializing Samstraumr repository");

      // Verify this is a git repository
      if (!isGitRepository()) {
        System.err.println("Not a git repository. Please initialize a git repository first.");
        return false;
      }

      // Check if it's already a Samstraumr repository
      if (isExistingS8rRepo()) {
        System.err.println("Samstraumr repository already exists in this directory.");
        return false;
      }

      // Create directory structure
      System.out.println("Creating directory structure");
      createDirectoryStructure();

      // Create Adam tube
      System.out.println("Creating Adam tube");
      createAdamTube();

      // Create configuration files
      System.out.println("Creating configuration files");
      createConfigFiles();

      System.out.println("Samstraumr initialization complete");
      return true;
    } catch (Exception e) {
      LOGGER.error("Failed to initialize Samstraumr repository", e);
      System.err.println("Error initializing Samstraumr repository: " + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if the specified path is a git repository.
   *
   * @return true if the path is a git repository, false otherwise
   */
  private boolean isGitRepository() {
    Path gitDir = Paths.get(repoPath, ".git");
    return Files.exists(gitDir) && Files.isDirectory(gitDir);
  }

  /**
   * Checks if the specified path already contains a Samstraumr repository.
   *
   * @return true if a Samstraumr repository exists, false otherwise
   */
  private boolean isExistingS8rRepo() {
    Path s8rDir = Paths.get(repoPath, ".s8r");
    return Files.exists(s8rDir) && Files.isDirectory(s8rDir);
  }

  /**
   * Creates the necessary directory structure for a Samstraumr repository.
   *
   * @throws IOException if an I/O error occurs
   */
  private void createDirectoryStructure() throws IOException {
    // Convert package name to path format
    String packagePath = packageName.replace('.', '/');

    // Create main source directories
    createDirectory("src/main/java/" + packagePath + "/component");
    createDirectory("src/main/java/" + packagePath + "/tube");
    createDirectory("src/main/java/" + packagePath + "/machine");

    // Create test directories
    createDirectory("src/test/java/" + packagePath + "/component");
    createDirectory("src/test/java/" + packagePath + "/tube");
    createDirectory("src/test/resources/features");

    // Create config directory
    createDirectory(".s8r");
  }

  /**
   * Creates a directory at the specified path.
   *
   * @param relativePath the path relative to the repository root
   * @throws IOException if an I/O error occurs
   */
  private void createDirectory(String relativePath) throws IOException {
    Path dirPath = Paths.get(repoPath, relativePath);
    Files.createDirectories(dirPath);
  }

  /**
   * Creates an Adam tube in the repository.
   *
   * @throws IOException if an I/O error occurs
   */
  private void createAdamTube() throws IOException {
    // Convert package name to path format
    String packagePath = packageName.replace('.', '/');

    // Create the Adam tube file
    Path adamTubePath = Paths.get(repoPath, "src/main/java", packagePath, "tube/AdamTube.java");
    String adamTubeContent = generateAdamTubeContent();

    Files.writeString(adamTubePath, adamTubeContent);
  }

  /**
   * Generates the content for the Adam tube Java file.
   *
   * @return the Adam tube content
   */
  private String generateAdamTubeContent() {
    StringBuilder builder = new StringBuilder();

    builder.append("/*\n");
    builder.append(" * Adam Tube - The first tube in a Samstraumr repository\n");
    builder.append(" */\n\n");

    builder.append("package ").append(packageName).append(".tube;\n\n");

    builder.append("import java.util.HashMap;\n");
    builder.append("import java.util.Map;\n");
    builder.append("import org.s8r.tube.Tube;\n");
    builder.append("import org.s8r.tube.Environment;\n\n");

    builder.append("/**\n");
    builder.append(" * Adam Tube implementation - the first tube in this Samstraumr repository.\n");
    builder.append(" * This class demonstrates how to create and use a tube.\n");
    builder.append(" */\n");
    builder.append("public class AdamTube {\n");
    builder.append("    private final Tube tube;\n\n");

    builder.append("    /**\n");
    builder.append("     * Creates a new Adam tube.\n");
    builder.append("     */\n");
    builder.append("    public AdamTube() {\n");
    builder.append("        // Create environment with parameters\n");
    builder.append("        Environment environment = new Environment();\n");
    builder.append("        environment.setParameter(\"author\", \"Samstraumr Initializer\");\n");
    builder.append(
        "        environment.setParameter(\"purpose\", \"Demonstrate tube creation\");\n\n");

    builder.append("        // Create Adam tube\n");
    builder.append("        this.tube = Tube.create(\"Initial Adam tube for \" + \n");
    builder.append("                \"").append(packageName).append("\", environment);\n");
    builder.append("    }\n\n");

    builder.append("    /**\n");
    builder.append("     * Gets the underlying tube instance.\n");
    builder.append("     * \n");
    builder.append("     * @return the tube instance\n");
    builder.append("     */\n");
    builder.append("    public Tube getTube() {\n");
    builder.append("        return tube;\n");
    builder.append("    }\n\n");

    builder.append("    /**\n");
    builder.append("     * Demonstrates how to use this tube.\n");
    builder.append("     */\n");
    builder.append("    public void demonstrateUsage() {\n");
    builder.append(
        "        System.out.println(\"Adam Tube created with ID: \" + tube.getUniqueId());\n");
    builder.append(
        "        System.out.println(\"Identity: \" + tube.getIdentity().getUniqueId());\n");
    builder.append(
        "        System.out.println(\"Hierarchical address: \" + tube.getIdentity().getHierarchicalAddress());\n");
    builder.append(
        "        System.out.println(\"Is Adam tube: \" + tube.getIdentity().isAdamTube());\n");
    builder.append("    }\n\n");

    builder.append("    /**\n");
    builder.append("     * Main method to demonstrate tube creation.\n");
    builder.append("     * \n");
    builder.append("     * @param args command line arguments\n");
    builder.append("     */\n");
    builder.append("    public static void main(String[] args) {\n");
    builder.append("        AdamTube adamTube = new AdamTube();\n");
    builder.append("        adamTube.demonstrateUsage();\n");
    builder.append("    }\n");
    builder.append("}\n");

    return builder.toString();
  }

  /**
   * Creates configuration files for the Samstraumr repository.
   *
   * @throws IOException if an I/O error occurs
   */
  private void createConfigFiles() throws IOException {
    // Create configuration JSON
    Path configPath = Paths.get(repoPath, ".s8r/config.json");
    String configContent = generateConfigContent();

    Files.writeString(configPath, configContent);

    // Create README
    Path readmePath = Paths.get(repoPath, "README.md");
    if (!Files.exists(readmePath)) {
      String readmeContent = generateReadmeContent();
      Files.writeString(readmePath, readmeContent);
    }
  }

  /**
   * Generates the content for the config.json file.
   *
   * @return the config.json content
   */
  private String generateConfigContent() {
    return "{\n"
        + "  \"projectName\": \"Samstraumr Project\",\n"
        + "  \"packageName\": \""
        + packageName
        + "\",\n"
        + "  \"createdAt\": \""
        + java.time.Instant.now()
        + "\",\n"
        + "  \"defaultComponents\": {\n"
        + "    \"tube\": true,\n"
        + "    \"component\": true,\n"
        + "    \"machine\": true\n"
        + "  }\n"
        + "}\n";
  }

  /**
   * Generates the content for the README.md file.
   *
   * @return the README.md content
   */
  private String generateReadmeContent() {
    return "# Samstraumr Project\n\n"
        + "This project was initialized with the Samstraumr framework.\n\n"
        + "## Overview\n\n"
        + "Samstraumr is a framework for building component-based systems with a biological-inspired\n"
        + "lifecycle model. This project contains the foundation for building such systems.\n\n"
        + "## Getting Started\n\n"
        + "1. Explore the generated Adam tube in `src/main/java/"
        + packageName.replace('.', '/')
        + "/tube/AdamTube.java`\n"
        + "2. Run the Adam tube to see it in action\n"
        + "3. Create your own components by extending the framework classes\n\n"
        + "## Project Structure\n\n"
        + "- `src/main/java/"
        + packageName.replace('.', '/')
        + "/component` - Component implementations\n"
        + "- `src/main/java/"
        + packageName.replace('.', '/')
        + "/tube` - Tube implementations\n"
        + "- `src/main/java/"
        + packageName.replace('.', '/')
        + "/machine` - Machine implementations\n"
        + "- `src/test` - Test resources and implementations\n\n"
        + "## Documentation\n\n"
        + "For more information about the Samstraumr framework, see the [official documentation](https://github.com/heymumford/Samstraumr).\n";
  }
}
