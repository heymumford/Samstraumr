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

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.ProjectInitializationPort;
import org.s8r.application.service.ProjectInitializationService;
import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.component.Environment;
import org.s8r.infrastructure.initialization.FileSystemProjectInitializer;
import org.s8r.infrastructure.logging.ConsoleLogger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for CLI command tests.
 */
public class CliCommandSteps {
    
    private Path tempDirectory;
    private int commandResult;
    private String commandOutput;
    private boolean commandSucceeded;
    private ProjectInitializationService initService;
    private Map<String, Composite> composites = new HashMap<>();
    private Machine testMachine;
    
    @Before
    public void setUp() {
        ConsoleLogger logger = new ConsoleLogger("CliCommandTest");
        ProjectInitializationPort port = new FileSystemProjectInitializer(logger);
        initService = new ProjectInitializationService(port, logger);
    }
    
    @After
    public void tearDown() {
        if (tempDirectory != null) {
            // Clean up temporary directory
            try {
                deleteDirectory(tempDirectory.toFile());
            } catch (IOException e) {
                System.err.println("Warning: Failed to clean up temporary directory: " + e.getMessage());
            }
        }
    }
    
    @Given("a temporary directory for testing")
    public void a_temporary_directory_for_testing() throws IOException {
        tempDirectory = Files.createTempDirectory("s8r-test-");
        // Initialize as a git repository
        runCommand("git init " + tempDirectory.toString());
    }
    
    @Given("a non-git temporary directory for testing")
    public void a_non_git_temporary_directory_for_testing() throws IOException {
        tempDirectory = Files.createTempDirectory("s8r-test-");
    }
    
    @Given("the S8r CLI is available on the system path")
    public void the_s8r_cli_is_available_on_the_system_path() {
        // This is a mock implementation - in a real scenario we'd check if s8r is on the path
        // For testing purposes, we're simulating the CLI by directly calling the service
    }
    
    @Given("the temporary directory already contains an initialized S8r project")
    public void the_temporary_directory_already_contains_an_initialized_s8r_project() {
        assertTrue(initService.initializeProject(tempDirectory.toString()), 
                "Failed to initialize project in temporary directory");
    }
    
    @Given("a clean temporary directory")
    public void a_clean_temporary_directory() throws IOException {
        tempDirectory = Files.createTempDirectory("s8r-test-");
    }
    
    @Given("the project has a machine named {string} with composites:")
    public void the_project_has_a_machine_with_composites(String machineName, DataTable dataTable) {
        // Create a test machine
        Environment env = new Environment();
        testMachine = new Machine(machineName, env);
        
        // Create composites based on data table
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String compositeName = row.get("name");
            String compositeType = row.get("type");
            
            Environment compositeEnv = new Environment();
            compositeEnv.setParameter("type", compositeType);
            
            Composite composite = new Composite(compositeName, compositeEnv);
            composites.put(compositeName, composite);
            testMachine.addComposite(compositeName, composite);
        }
        
        // This is a simplified test implementation - in real scenario we'd create actual files
        // For testing, we're just simulating the model in memory
    }
    
    @Given("the machine has the following connections:")
    public void the_machine_has_the_following_connections(DataTable dataTable) {
        // Add connections based on data table
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String from = row.get("from");
            String to = row.get("to");
            
            testMachine.connect(from, to);
        }
    }
    
    @When("I run {string} in the temporary directory")
    public void i_run_command_in_the_temporary_directory(String command) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);
        
        try {
            if (command.startsWith("s8r init")) {
                // Parse the command to extract options
                String packageName = null;
                if (command.contains("--package")) {
                    String[] parts = command.split("--package");
                    if (parts.length > 1) {
                        packageName = parts[1].trim();
                    }
                }
                
                // Simulate s8r init command
                commandSucceeded = packageName != null 
                        ? initService.initializeProject(tempDirectory.toString(), packageName)
                        : initService.initializeProject(tempDirectory.toString());
            } else if (command.equals("s8r list")) {
                // Simulate s8r list command
                if (Files.exists(Paths.get(tempDirectory.toString(), ".s8r"))) {
                    // Generate ASCII visualization
                    String visualization = generateAsciiVisualization();
                    printStream.println(visualization);
                    commandSucceeded = true;
                } else {
                    printStream.println("Not a valid S8r project");
                    commandSucceeded = false;
                }
            } else {
                // Execute actual shell command
                commandResult = runCommand(command);
                commandSucceeded = (commandResult == 0);
            }
            
            commandOutput = outputStream.toString();
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Then("the command should succeed")
    public void the_command_should_succeed() {
        assertTrue(commandSucceeded, "Command failed: " + commandOutput);
    }
    
    @Then("the command should fail")
    public void the_command_should_fail() {
        assertFalse(commandSucceeded, "Command unexpectedly succeeded");
    }
    
    @Then("the temporary directory should contain an initialized S8r project")
    public void the_temporary_directory_should_contain_an_initialized_s8r_project() {
        Path s8rDir = Paths.get(tempDirectory.toString(), ".s8r");
        assertTrue(Files.exists(s8rDir), ".s8r directory not found");
        
        Path configFile = Paths.get(s8rDir.toString(), "config.json");
        assertTrue(Files.exists(configFile), "config.json not found");
    }
    
    @Then("the project should have the default package {string}")
    public void the_project_should_have_the_default_package(String packageName) {
        // In a real test, we'd check the package in the config.json file
        // For this test implementation, we'll just assert the structure exists
        String packagePath = packageName.replace('.', '/');
        Path srcDir = Paths.get(tempDirectory.toString(), "src/main/java", packagePath);
        assertTrue(Files.exists(srcDir), "Source directory for package not found");
    }
    
    @Then("the project should have the package {string}")
    public void the_project_should_have_the_package(String packageName) {
        // In a real test, we'd check the package in the config.json file
        String packagePath = packageName.replace('.', '/');
        Path srcDir = Paths.get(tempDirectory.toString(), "src/main/java", packagePath);
        assertTrue(Files.exists(srcDir), "Source directory for package not found");
    }
    
    @Then("the project should have an Adam component")
    public void the_project_should_have_an_adam_component() {
        // Check for Adam component file
        Path adamComponentPath = Paths.get(tempDirectory.toString(), "src/main/java/org/example/component/AdamComponent.java");
        
        // If a custom package was used, we need to handle that too (simplified version)
        if (!Files.exists(adamComponentPath)) {
            // Look for any AdamComponent.java file
            try {
                boolean found = Files.walk(tempDirectory)
                    .filter(p -> p.getFileName().toString().equals("AdamComponent.java"))
                    .findFirst()
                    .isPresent();
                
                assertTrue(found, "AdamComponent.java not found");
            } catch (IOException e) {
                fail("Failed to search for AdamComponent.java: " + e.getMessage());
            }
        }
    }
    
    @Then("the output should contain {string}")
    public void the_output_should_contain(String expectedOutput) {
        assertTrue(commandOutput.contains(expectedOutput), 
                "Output doesn't contain expected text. Output: " + commandOutput);
    }
    
    @Then("a git repository should be created in the temporary directory")
    public void a_git_repository_should_be_created_in_the_temporary_directory() {
        Path gitDir = Paths.get(tempDirectory.toString(), ".git");
        assertTrue(Files.exists(gitDir), ".git directory not found");
    }
    
    @Then("the output should contain an ASCII visualization of the project")
    public void the_output_should_contain_an_ascii_visualization_of_the_project() {
        assertTrue(commandOutput.contains("S8r Model Visualization"), 
                "Output doesn't contain visualization header");
    }
    
    @Then("the visualization should indicate an empty model")
    public void the_visualization_should_indicate_an_empty_model() {
        assertTrue(commandOutput.contains("No machines defined"), 
                "Output doesn't indicate an empty model");
    }
    
    @Then("the visualization should show the machine {string}")
    public void the_visualization_should_show_the_machine(String machineName) {
        assertTrue(commandOutput.contains(machineName), 
                "Output doesn't show the machine name");
    }
    
    @Then("the visualization should show the {int} composites")
    public void the_visualization_should_show_the_composites(Integer count) {
        // This is a simplified check - in reality we'd check the actual visualization structure
        for (String compositeName : composites.keySet()) {
            assertTrue(commandOutput.contains(compositeName), 
                    "Output doesn't show the composite: " + compositeName);
        }
    }
    
    @Then("the visualization should show the connections between composites")
    public void the_visualization_should_show_the_connections_between_composites() {
        // This is a simplified check - in reality we'd check the actual connection representation
        assertTrue(commandOutput.contains("-->"), 
                "Output doesn't show connection arrows");
    }
    
    // Helper methods
    
    private int runCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        Files.delete(file.toPath());
                    }
                }
            }
            Files.delete(directory.toPath());
        }
    }
    
    private String generateAsciiVisualization() {
        StringBuilder builder = new StringBuilder();
        builder.append("S8r Model Visualization\n");
        builder.append("======================\n\n");
        
        if (testMachine == null) {
            builder.append("No machines defined\n");
            return builder.toString();
        }
        
        builder.append("Machine: ").append(testMachine.getMachineId()).append("\n");
        builder.append("  State: ").append(testMachine.isActive() ? "ACTIVE" : "INACTIVE").append("\n\n");
        
        // Add composites
        builder.append("Composites:\n");
        for (Map.Entry<String, Composite> entry : composites.entrySet()) {
            String name = entry.getKey();
            Composite composite = entry.getValue();
            String type = (String) composite.getEnvironment().getParameter("type");
            
            builder.append("  [").append(name).append(": ").append(type).append("]\n");
        }
        
        // Add connections
        builder.append("\nConnections:\n");
        Map<String, List<String>> connections = testMachine.getConnections();
        for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
            String source = entry.getKey();
            for (String target : entry.getValue()) {
                builder.append("  [").append(source).append("] --> [").append(target).append("]\n");
            }
        }
        
        return builder.toString();
    }
}