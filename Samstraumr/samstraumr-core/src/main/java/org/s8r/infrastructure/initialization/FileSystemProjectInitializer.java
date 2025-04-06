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

package org.s8r.infrastructure.initialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.ProjectInitializationPort;
import org.s8r.domain.exception.ComponentInitializationException;

/**
 * File system implementation of ProjectInitializationPort.
 * <p>
 * This class provides the infrastructure implementation for project initialization
 * operations using file system APIs. It handles file and directory operations needed
 * to set up a new Samstraumr project.
 */
public class FileSystemProjectInitializer implements ProjectInitializationPort {
    
    private final LoggerPort logger;
    
    /**
     * Creates a new file system project initializer.
     *
     * @param logger the logger to use
     */
    public FileSystemProjectInitializer(LoggerPort logger) {
        this.logger = logger;
    }
    
    @Override
    public boolean isValidRepository(String projectPath) {
        Path gitDir = Paths.get(projectPath, ".git");
        return Files.exists(gitDir) && Files.isDirectory(gitDir);
    }
    
    @Override
    public boolean isExistingProject(String projectPath) {
        Path s8rDir = Paths.get(projectPath, ".s8r");
        return Files.exists(s8rDir) && Files.isDirectory(s8rDir);
    }
    
    @Override
    public void createDirectoryStructure(String projectPath, String packageName) throws ComponentInitializationException {
        try {
            // Convert package name to path format
            String packagePath = packageName.replace('.', '/');
            
            // Create main source directories
            createDirectory(projectPath, "src/main/java/" + packagePath + "/component");
            createDirectory(projectPath, "src/main/java/" + packagePath + "/tube");
            createDirectory(projectPath, "src/main/java/" + packagePath + "/machine");
            
            // Create test directories
            createDirectory(projectPath, "src/test/java/" + packagePath + "/component");
            createDirectory(projectPath, "src/test/java/" + packagePath + "/tube");
            createDirectory(projectPath, "src/test/resources/features");
            
            // Create config directory
            createDirectory(projectPath, ".s8r");
        } catch (IOException e) {
            throw new ComponentInitializationException("Failed to create directory structure", e);
        }
    }
    
    @Override
    public void createAdamComponent(String projectPath, String packageName) throws ComponentInitializationException {
        try {
            // Convert package name to path format
            String packagePath = packageName.replace('.', '/');
            
            // Create the Adam tube file
            Path adamComponentPath = Paths.get(projectPath, "src/main/java", packagePath, "component/AdamComponent.java");
            String adamComponentContent = generateAdamComponentContent(packageName);
            
            Files.writeString(adamComponentPath, adamComponentContent);
        } catch (IOException e) {
            throw new ComponentInitializationException("Failed to create Adam component", e);
        }
    }
    
    @Override
    public void createConfigurationFiles(String projectPath, String packageName) throws ComponentInitializationException {
        try {
            // Create configuration JSON
            Path configPath = Paths.get(projectPath, ".s8r/config.json");
            String configContent = generateConfigContent(packageName);
            Files.writeString(configPath, configContent);
            
            // Create README if it doesn't exist
            Path readmePath = Paths.get(projectPath, "README.md");
            if (!Files.exists(readmePath)) {
                String readmeContent = generateReadmeContent(packageName);
                Files.writeString(readmePath, readmeContent);
            }
        } catch (IOException e) {
            throw new ComponentInitializationException("Failed to create configuration files", e);
        }
    }
    
    /**
     * Creates a directory at the specified path.
     *
     * @param basePath the base project path
     * @param relativePath the path relative to the base path
     * @throws IOException if an I/O error occurs
     */
    private void createDirectory(String basePath, String relativePath) throws IOException {
        Path dirPath = Paths.get(basePath, relativePath);
        Files.createDirectories(dirPath);
    }
    
    /**
     * Generates the content for the Adam component Java file.
     *
     * @param packageName the package name
     * @return the Adam component content
     */
    private String generateAdamComponentContent(String packageName) {
        StringBuilder builder = new StringBuilder();
        
        builder.append("/*\n");
        builder.append(" * Adam Component - The first component in a Samstraumr repository\n");
        builder.append(" */\n\n");
        
        builder.append("package ").append(packageName).append(".component;\n\n");
        
        builder.append("import java.util.HashMap;\n");
        builder.append("import java.util.Map;\n");
        builder.append("import org.s8r.component.core.Component;\n");
        builder.append("import org.s8r.component.core.Environment;\n\n");
        
        builder.append("/**\n");
        builder.append(" * Adam Component implementation - the first component in this Samstraumr repository.\n");
        builder.append(" * This class demonstrates how to create and use a component.\n");
        builder.append(" */\n");
        builder.append("public class AdamComponent {\n");
        builder.append("    private final Component component;\n\n");
        
        builder.append("    /**\n");
        builder.append("     * Creates a new Adam component.\n");
        builder.append("     */\n");
        builder.append("    public AdamComponent() {\n");
        builder.append("        // Create environment with parameters\n");
        builder.append("        Environment environment = new Environment();\n");
        builder.append("        environment.setParameter(\"author\", \"Samstraumr Initializer\");\n");
        builder.append("        environment.setParameter(\"purpose\", \"Demonstrate component creation\");\n\n");
        
        builder.append("        // Create Adam component\n");
        builder.append("        this.component = Component.create(\"Initial Adam component for \" +\n");
        builder.append("                \"").append(packageName).append("\", environment);\n");
        builder.append("    }\n\n");
        
        builder.append("    /**\n");
        builder.append("     * Gets the underlying component instance.\n");
        builder.append("     * \n");
        builder.append("     * @return the component instance\n");
        builder.append("     */\n");
        builder.append("    public Component getComponent() {\n");
        builder.append("        return component;\n");
        builder.append("    }\n\n");
        
        builder.append("    /**\n");
        builder.append("     * Demonstrates how to use this component.\n");
        builder.append("     */\n");
        builder.append("    public void demonstrateUsage() {\n");
        builder.append("        System.out.println(\"Adam Component created with ID: \" + component.getUniqueId());\n");
        builder.append("        System.out.println(\"Identity: \" + component.getIdentity().getUniqueId());\n");
        builder.append("        System.out.println(\"Is Adam component: \" + component.getIdentity().isAdam());\n");
        builder.append("    }\n\n");
        
        builder.append("    /**\n");
        builder.append("     * Main method to demonstrate component creation.\n");
        builder.append("     * \n");
        builder.append("     * @param args command line arguments\n");
        builder.append("     */\n");
        builder.append("    public static void main(String[] args) {\n");
        builder.append("        AdamComponent adamComponent = new AdamComponent();\n");
        builder.append("        adamComponent.demonstrateUsage();\n");
        builder.append("    }\n");
        builder.append("}\n");
        
        return builder.toString();
    }
    
    /**
     * Generates the content for the config.json file.
     *
     * @param packageName the package name
     * @return the config.json content
     */
    private String generateConfigContent(String packageName) {
        return "{\n" +
               "  \"projectName\": \"Samstraumr Project\",\n" +
               "  \"packageName\": \"" + packageName + "\",\n" +
               "  \"createdAt\": \"" + Instant.now() + "\",\n" +
               "  \"defaultComponents\": {\n" +
               "    \"component\": true,\n" +
               "    \"composite\": true,\n" +
               "    \"machine\": true\n" +
               "  }\n" +
               "}\n";
    }
    
    /**
     * Generates the content for the README.md file.
     *
     * @param packageName the package name
     * @return the README.md content
     */
    private String generateReadmeContent(String packageName) {
        return "# Samstraumr Project\n\n" +
               "This project was initialized with the Samstraumr framework.\n\n" +
               "## Overview\n\n" +
               "Samstraumr is a framework for building component-based systems with a biological-inspired " +
               "lifecycle model. This project contains the foundation for building such systems.\n\n" +
               "## Getting Started\n\n" +
               "1. Explore the generated Adam component in `src/main/java/" + packageName.replace('.', '/') + 
               "/component/AdamComponent.java`\n" +
               "2. Run the Adam component to see it in action\n" +
               "3. Create your own components by extending the framework classes\n\n" +
               "## Project Structure\n\n" +
               "- `src/main/java/" + packageName.replace('.', '/') + "/component` - Component implementations\n" +
               "- `src/main/java/" + packageName.replace('.', '/') + "/tube` - Tube implementations (legacy)\n" +
               "- `src/main/java/" + packageName.replace('.', '/') + "/machine` - Machine implementations\n" +
               "- `src/test` - Test resources and implementations\n\n" +
               "## Documentation\n\n" +
               "For more information about the Samstraumr framework, see the " +
               "[official documentation](https://github.com/heymumford/Samstraumr).\n";
    }
}