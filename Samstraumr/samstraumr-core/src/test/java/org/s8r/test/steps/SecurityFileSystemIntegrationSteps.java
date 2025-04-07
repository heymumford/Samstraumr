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

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.SecurityPort.AccessDeniedException;
import org.s8r.application.port.SecurityPort.Permission;
import org.s8r.application.port.SecurityPort.SecurityContext;
import org.s8r.application.service.FileSystemService;
import org.s8r.application.service.SecurityService;
import org.s8r.infrastructure.filesystem.StandardFileSystemAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.security.SecurityAdapter;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the integration between SecurityPort and FileSystemPort.
 */
@IntegrationTest
public class SecurityFileSystemIntegrationSteps {

    private SecurityPort securityPort;
    private FileSystemPort fileSystemPort;
    private SecureFileService secureFileService;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;
    private List<String> auditLog;
    private Path tempDir;
    private SecurityContext currentContext;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        auditLog = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger() {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void error(String message) {
                super.error(message);
                logMessages.add("ERROR: " + message);
            }
            
            @Override
            public void error(String message, Object... args) {
                super.error(message, args);
                logMessages.add("ERROR: " + String.format(message.replace("{}", "%s"), args));
            }
        };
        
        // Create a temporary directory for tests
        try {
            tempDir = Files.createTempDirectory("s8r-security-fs-test-");
            tempDir.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory", e);
        }
        
        // Initialize the ports
        securityPort = new TestSecurityAdapter(logger, auditLog);
        fileSystemPort = new StandardFileSystemAdapter(logger);
        
        // Initialize the secure file service
        secureFileService = new SecureFileService(fileSystemPort, securityPort, logger);
        
        // Set default security context
        currentContext = new TestSecurityContext("default", "DEFAULT");
        secureFileService.setSecurityContext(currentContext);
        
        // Reset log messages between scenarios
        logMessages.clear();
        auditLog.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
        auditLog.clear();
        
        // Clean up the temporary directory
        try {
            if (tempDir != null && Files.exists(tempDir)) {
                deleteDirectory(tempDir.toFile());
            }
        } catch (Exception e) {
            logger.error("Error cleaning up temporary directory: " + e.getMessage());
        }
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(securityPort, "SecurityPort should be initialized");
        assertNotNull(fileSystemPort, "FileSystemPort should be initialized");
        assertNotNull(secureFileService, "SecureFileService should be initialized");
    }

    @Given("both security and file system ports are properly initialized")
    public void bothSecurityAndFileSystemPortsAreProperlyInitialized() {
        // Verify both ports are initialized and working
        assertTrue(securityPort.isInitialized(), "SecurityPort should be initialized");
        assertNotNull(currentContext, "Security context should be set");
        assertTrue(Files.exists(tempDir), "Temporary directory should exist");
    }

    @Given("test files are prepared in a secured directory")
    public void testFilesArePreparedInASecuredDirectory() {
        // Store the temp directory path
        testContext.put("securedDir", tempDir.toString());
        
        // Grant permissions for the test directory to the default user
        securityPort.grantPermission(currentContext, tempDir.toString(), Permission.READ);
        securityPort.grantPermission(currentContext, tempDir.toString(), Permission.WRITE);
        securityPort.grantPermission(currentContext, tempDir.toString(), Permission.CREATE);
        securityPort.grantPermission(currentContext, tempDir.toString(), Permission.DELETE);
        securityPort.grantPermission(currentContext, tempDir.toString(), Permission.LIST);
    }

    @Given("a secured file {string} with sensitive content exists")
    public void aSecuredFileWithSensitiveContentExists(String fileName) {
        String content = "This is sensitive content that should be protected.";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
            assertTrue(Files.exists(filePath), "File should be created successfully");
            
            // Store for later use
            testContext.put("filePath", filePath.toString());
            testContext.put("fileName", fileName);
            testContext.put("originalContent", content);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Given("the user has READ permission for the file")
    public void theUserHasREADPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Grant READ permission
        securityPort.grantPermission(currentContext, filePath, Permission.READ);
        
        // Store permission state
        testContext.put("hasReadPermission", true);
    }

    @Given("the user does not have READ permission for the file")
    public void theUserDoesNotHaveREADPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Ensure no READ permission
        securityPort.revokePermission(currentContext, filePath, Permission.READ);
        
        // Store permission state
        testContext.put("hasReadPermission", false);
    }

    @Given("a secured file {string} exists with initial content")
    public void aSecuredFileExistsWithInitialContent(String fileName) {
        String initialContent = "This is the initial content of the file.";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            Files.write(filePath, initialContent.getBytes(StandardCharsets.UTF_8));
            assertTrue(Files.exists(filePath), "File should be created successfully");
            
            // Store for later use
            testContext.put("filePath", filePath.toString());
            testContext.put("fileName", fileName);
            testContext.put("originalContent", initialContent);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Given("the user has WRITE permission for the file")
    public void theUserHasWRITEPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Grant WRITE permission
        securityPort.grantPermission(currentContext, filePath, Permission.WRITE);
        
        // Store permission state
        testContext.put("hasWritePermission", true);
    }

    @Given("the user does not have WRITE permission for the file")
    public void theUserDoesNotHaveWRITEPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Ensure no WRITE permission
        securityPort.revokePermission(currentContext, filePath, Permission.WRITE);
        
        // Store permission state
        testContext.put("hasWritePermission", false);
    }

    @Given("a secured file {string} exists")
    public void aSecuredFileExists(String fileName) {
        String content = "This file can be deleted with the right permissions.";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
            assertTrue(Files.exists(filePath), "File should be created successfully");
            
            // Store for later use
            testContext.put("filePath", filePath.toString());
            testContext.put("fileName", fileName);
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Given("the user has DELETE permission for the file")
    public void theUserHasDELETEPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Grant DELETE permission
        securityPort.grantPermission(currentContext, filePath, Permission.DELETE);
        
        // Store permission state
        testContext.put("hasDeletePermission", true);
    }

    @Given("the user does not have DELETE permission for the file")
    public void theUserDoesNotHaveDELETEPermissionForTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Ensure no DELETE permission
        securityPort.revokePermission(currentContext, filePath, Permission.DELETE);
        
        // Store permission state
        testContext.put("hasDeletePermission", false);
    }

    @Given("a secured directory exists")
    public void aSecuredDirectoryExists() {
        Path dirPath = tempDir.resolve("secured-dir");
        
        try {
            Files.createDirectory(dirPath);
            assertTrue(Files.exists(dirPath), "Directory should be created successfully");
            
            // Store for later use
            testContext.put("dirPath", dirPath.toString());
        } catch (IOException e) {
            fail("Failed to create test directory: " + e.getMessage());
        }
    }

    @Given("the user has CREATE permission for the directory")
    public void theUserHasCREATEPermissionForTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Grant CREATE permission
        securityPort.grantPermission(currentContext, dirPath, Permission.CREATE);
        
        // Store permission state
        testContext.put("hasCreatePermission", true);
    }

    @Given("the user does not have CREATE permission for the directory")
    public void theUserDoesNotHaveCREATEPermissionForTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Ensure no CREATE permission
        securityPort.revokePermission(currentContext, dirPath, Permission.CREATE);
        
        // Store permission state
        testContext.put("hasCreatePermission", false);
    }

    @Given("a secured directory with multiple files exists")
    public void aSecuredDirectoryWithMultipleFilesExists() {
        Path dirPath = tempDir.resolve("list-dir");
        
        try {
            Files.createDirectory(dirPath);
            
            // Create some test files
            for (int i = 1; i <= 5; i++) {
                Path filePath = dirPath.resolve("file" + i + ".txt");
                Files.write(filePath, ("Content of file " + i).getBytes(StandardCharsets.UTF_8));
            }
            
            assertTrue(Files.exists(dirPath), "Directory should be created successfully");
            assertEquals(5, Files.list(dirPath).count(), "Directory should contain 5 files");
            
            // Store for later use
            testContext.put("dirPath", dirPath.toString());
        } catch (IOException e) {
            fail("Failed to create test directory: " + e.getMessage());
        }
    }

    @Given("the user has LIST permission for the directory")
    public void theUserHasLISTPermissionForTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Grant LIST permission
        securityPort.grantPermission(currentContext, dirPath, Permission.LIST);
        
        // Store permission state
        testContext.put("hasListPermission", true);
    }

    @Given("the user does not have LIST permission for the directory")
    public void theUserDoesNotHaveLISTPermissionForTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Ensure no LIST permission
        securityPort.revokePermission(currentContext, dirPath, Permission.LIST);
        
        // Store permission state
        testContext.put("hasListPermission", false);
    }

    @Given("user {string} has ALL permissions for the file")
    public void userHasALLPermissionsForTheFile(String username) {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Create user security context if needed
        TestSecurityContext userContext = new TestSecurityContext(username, username.toUpperCase());
        testContext.put(username + "Context", userContext);
        
        // Grant all permissions
        securityPort.grantPermission(userContext, filePath, Permission.READ);
        securityPort.grantPermission(userContext, filePath, Permission.WRITE);
        securityPort.grantPermission(userContext, filePath, Permission.DELETE);
    }

    @Given("user {string} has only READ permission for the file")
    public void userHasOnlyREADPermissionForTheFile(String username) {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Create user security context if needed
        TestSecurityContext userContext = new TestSecurityContext(username, username.toUpperCase());
        testContext.put(username + "Context", userContext);
        
        // Grant only READ permission
        securityPort.grantPermission(userContext, filePath, Permission.READ);
        
        // Ensure no other permissions
        securityPort.revokePermission(userContext, filePath, Permission.WRITE);
        securityPort.revokePermission(userContext, filePath, Permission.DELETE);
    }

    @When("I read the file through the secure file service")
    public void iReadTheFileThroughTheSecureFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        try {
            // Read the file
            String content = secureFileService.readFile(filePath);
            
            // Store the result
            testContext.put("readContent", content);
            testContext.put("readSuccess", true);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("readSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I attempt to read the file through the secure file service")
    public void iAttemptToReadTheFileThroughTheSecureFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        try {
            // Try to read the file
            String content = secureFileService.readFile(filePath);
            
            // Store the result
            testContext.put("readContent", content);
            testContext.put("readSuccess", true);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("readSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I update the file with new content through the secure file service")
    public void iUpdateTheFileWithNewContentThroughTheSecureFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // New content to write
        String newContent = "This content has been updated securely.";
        
        try {
            // Write to the file
            boolean success = secureFileService.writeFile(filePath, newContent);
            
            // Store the result
            testContext.put("writeSuccess", success);
            testContext.put("newContent", newContent);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("writeSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I attempt to update the file with new content")
    public void iAttemptToUpdateTheFileWithNewContent() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // New content to write
        String newContent = "This is an unauthorized update attempt.";
        
        try {
            // Try to write to the file
            boolean success = secureFileService.writeFile(filePath, newContent);
            
            // Store the result
            testContext.put("writeSuccess", success);
            testContext.put("newContent", newContent);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("writeSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I delete the file through the secure file service")
    public void iDeleteTheFileThroughTheSecureFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        try {
            // Delete the file
            boolean success = secureFileService.deleteFile(filePath);
            
            // Store the result
            testContext.put("deleteSuccess", success);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("deleteSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I attempt to delete the file")
    public void iAttemptToDeleteTheFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        try {
            // Try to delete the file
            boolean success = secureFileService.deleteFile(filePath);
            
            // Store the result
            testContext.put("deleteSuccess", success);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("deleteSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I create a new file in the directory through the secure file service")
    public void iCreateANewFileInTheDirectoryThroughTheSecureFileService() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        String fileName = "new-secure-file.txt";
        String filePath = Paths.get(dirPath, fileName).toString();
        String content = "This is a newly created secure file.";
        
        try {
            // Create the file
            boolean success = secureFileService.writeFile(filePath, content);
            
            // Store the result
            testContext.put("createSuccess", success);
            testContext.put("newFilePath", filePath);
            testContext.put("newFileName", fileName);
            testContext.put("newFileContent", content);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("createSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I attempt to create a new file in the directory")
    public void iAttemptToCreateANewFileInTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        String fileName = "unauthorized-file.txt";
        String filePath = Paths.get(dirPath, fileName).toString();
        String content = "This file should not be created due to permission issues.";
        
        try {
            // Try to create the file
            boolean success = secureFileService.writeFile(filePath, content);
            
            // Store the result
            testContext.put("createSuccess", success);
            testContext.put("newFilePath", filePath);
            testContext.put("newFileName", fileName);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("createSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I list files in the directory through the secure file service")
    public void iListFilesInTheDirectoryThroughTheSecureFileService() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        try {
            // List files
            List<String> files = secureFileService.listFiles(dirPath);
            
            // Store the result
            testContext.put("listedFiles", files);
            testContext.put("listSuccess", true);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("listSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("I attempt to list files in the directory")
    public void iAttemptToListFilesInTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        try {
            // Try to list files
            List<String> files = secureFileService.listFiles(dirPath);
            
            // Store the result
            testContext.put("listedFiles", files);
            testContext.put("listSuccess", true);
            testContext.put("securityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put("listSuccess", false);
            testContext.put("securityViolation", true);
            testContext.put("errorMessage", e.getMessage());
        }
    }

    @When("{string} updates the file content")
    public void userUpdatesTheFileContent(String username) {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Get user context
        TestSecurityContext userContext = (TestSecurityContext) testContext.get(username + "Context");
        assertNotNull(userContext, "User context should be in test context");
        
        // Switch to this user's context
        secureFileService.setSecurityContext(userContext);
        
        // New content to write
        String newContent = "Content updated by user: " + username;
        
        try {
            // Write to the file
            boolean success = secureFileService.writeFile(filePath, newContent);
            
            // Store the result
            testContext.put(username + "WriteSuccess", success);
            testContext.put(username + "NewContent", newContent);
            testContext.put(username + "SecurityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put(username + "WriteSuccess", false);
            testContext.put(username + "SecurityViolation", true);
            testContext.put(username + "ErrorMessage", e.getMessage());
        }
    }

    @When("{string} attempts to update the file content")
    public void userAttemptsToUpdateTheFileContent(String username) {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Get user context
        TestSecurityContext userContext = (TestSecurityContext) testContext.get(username + "Context");
        assertNotNull(userContext, "User context should be in test context");
        
        // Switch to this user's context
        secureFileService.setSecurityContext(userContext);
        
        // New content to write
        String newContent = "Unauthorized update attempt by user: " + username;
        
        try {
            // Try to write to the file
            boolean success = secureFileService.writeFile(filePath, newContent);
            
            // Store the result
            testContext.put(username + "WriteSuccess", success);
            testContext.put(username + "NewContent", newContent);
            testContext.put(username + "SecurityViolation", false);
        } catch (AccessDeniedException e) {
            testContext.put(username + "WriteSuccess", false);
            testContext.put(username + "SecurityViolation", true);
            testContext.put(username + "ErrorMessage", e.getMessage());
        }
    }

    @Then("the file content should be returned correctly")
    public void theFileContentShouldBeReturnedCorrectly() {
        String originalContent = (String) testContext.get("originalContent");
        String readContent = (String) testContext.get("readContent");
        
        assertNotNull(originalContent, "Original content should be in test context");
        assertNotNull(readContent, "Read content should be in test context");
        
        assertEquals(originalContent, readContent, "Read content should match original content");
    }

    @Then("the access should be logged with authorization details")
    public void theAccessShouldBeLoggedWithAuthorizationDetails() {
        String filePath = (String) testContext.get("filePath");
        String operation = (String) testContext.getOrDefault("operation", "READ");
        
        // Check the audit log
        boolean accessLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains(operation) && entry.contains(filePath));
        
        assertTrue(accessLogged, "Access should be logged with authorization details");
    }

    @Then("the access should be denied")
    public void theAccessShouldBeDenied() {
        Boolean securityViolation = (Boolean) testContext.get("securityViolation");
        assertNotNull(securityViolation, "Security violation flag should be in test context");
        
        assertTrue(securityViolation, "Access should be denied");
    }

    @Then("a security violation should be logged")
    public void aSecurityViolationShouldBeLogged() {
        String filePath = (String) testContext.get("filePath");
        
        // Check the audit log
        boolean violationLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("VIOLATION") && entry.contains(filePath));
        
        assertTrue(violationLogged, "Security violation should be logged");
    }

    @Then("the file content should not be accessible")
    public void theFileContentShouldNotBeAccessible() {
        String readContent = (String) testContext.get("readContent");
        Boolean readSuccess = (Boolean) testContext.get("readSuccess");
        
        assertNotNull(readSuccess, "Read success flag should be in test context");
        assertFalse(readSuccess, "Read should not succeed");
        assertNull(readContent, "Read content should be null");
    }

    @Then("the file should be updated on disk")
    public void theFileShouldBeUpdatedOnDisk() {
        String filePath = (String) testContext.get("filePath");
        String newContent = (String) testContext.get("newContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(newContent, "New content should be in test context");
        
        // Read the file directly
        try {
            String actualContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            assertEquals(newContent, actualContent, "File content on disk should match new content");
        } catch (IOException e) {
            fail("Failed to read updated file: " + e.getMessage());
        }
    }

    @Then("the write operation should be logged with authorization details")
    public void theWriteOperationShouldBeLoggedWithAuthorizationDetails() {
        String filePath = (String) testContext.get("filePath");
        
        // Check the audit log
        boolean writeLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("WRITE") && entry.contains(filePath));
        
        assertTrue(writeLogged, "Write operation should be logged with authorization details");
    }

    @Then("the write access should be denied")
    public void theWriteAccessShouldBeDenied() {
        Boolean writeSuccess = (Boolean) testContext.get("writeSuccess");
        Boolean securityViolation = (Boolean) testContext.get("securityViolation");
        
        assertNotNull(writeSuccess, "Write success flag should be in test context");
        assertNotNull(securityViolation, "Security violation flag should be in test context");
        
        assertFalse(writeSuccess, "Write should not succeed");
        assertTrue(securityViolation, "Security violation should be recorded");
    }

    @Then("the file content should not be modified")
    public void theFileContentShouldNotBeModified() {
        String filePath = (String) testContext.get("filePath");
        String originalContent = (String) testContext.get("originalContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(originalContent, "Original content should be in test context");
        
        // Read the file directly
        try {
            String actualContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            assertEquals(originalContent, actualContent, "File content on disk should match original content");
        } catch (IOException e) {
            fail("Failed to read file: " + e.getMessage());
        }
    }

    @Then("the file should be deleted from disk")
    public void theFileShouldBeDeletedFromDisk() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check if file exists
        assertFalse(Files.exists(Paths.get(filePath)), "File should be deleted from disk");
    }

    @Then("the delete operation should be logged with authorization details")
    public void theDeleteOperationShouldBeLoggedWithAuthorizationDetails() {
        String filePath = (String) testContext.get("filePath");
        
        // Check the audit log
        boolean deleteLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("DELETE") && entry.contains(filePath));
        
        assertTrue(deleteLogged, "Delete operation should be logged with authorization details");
    }

    @Then("the delete access should be denied")
    public void theDeleteAccessShouldBeDenied() {
        Boolean deleteSuccess = (Boolean) testContext.get("deleteSuccess");
        Boolean securityViolation = (Boolean) testContext.get("securityViolation");
        
        assertNotNull(deleteSuccess, "Delete success flag should be in test context");
        assertNotNull(securityViolation, "Security violation flag should be in test context");
        
        assertFalse(deleteSuccess, "Delete should not succeed");
        assertTrue(securityViolation, "Security violation should be recorded");
    }

    @Then("the file should still exist on disk")
    public void theFileShouldStillExistOnDisk() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check if file exists
        assertTrue(Files.exists(Paths.get(filePath)), "File should still exist on disk");
    }

    @Then("the file should be created with appropriate permissions")
    public void theFileShouldBeCreatedWithAppropriatePermissions() {
        String newFilePath = (String) testContext.get("newFilePath");
        assertNotNull(newFilePath, "New file path should be in test context");
        
        // Check if file exists
        assertTrue(Files.exists(Paths.get(newFilePath)), "File should be created on disk");
        
        // Check the file has the correct content
        try {
            String content = new String(Files.readAllBytes(Paths.get(newFilePath)), StandardCharsets.UTF_8);
            String expectedContent = (String) testContext.get("newFileContent");
            assertEquals(expectedContent, content, "File should have the correct content");
        } catch (IOException e) {
            fail("Failed to read created file: " + e.getMessage());
        }
    }

    @Then("the creation should be logged with authorization details")
    public void theCreationShouldBeLoggedWithAuthorizationDetails() {
        String newFilePath = (String) testContext.get("newFilePath");
        assertNotNull(newFilePath, "New file path should be in test context");
        
        // Check the audit log
        boolean createLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("CREATE") && entry.contains(newFilePath));
        
        assertTrue(createLogged, "Create operation should be logged with authorization details");
    }

    @Then("the creation access should be denied")
    public void theCreationAccessShouldBeDenied() {
        Boolean createSuccess = (Boolean) testContext.get("createSuccess");
        Boolean securityViolation = (Boolean) testContext.get("securityViolation");
        
        assertNotNull(createSuccess, "Create success flag should be in test context");
        assertNotNull(securityViolation, "Security violation flag should be in test context");
        
        assertFalse(createSuccess, "Create should not succeed");
        assertTrue(securityViolation, "Security violation should be recorded");
    }

    @Then("the file should not exist in the directory")
    public void theFileShouldNotExistInTheDirectory() {
        String dirPath = (String) testContext.get("dirPath");
        String newFileName = (String) testContext.get("newFileName");
        assertNotNull(dirPath, "Directory path should be in test context");
        assertNotNull(newFileName, "New file name should be in test context");
        
        String newFilePath = Paths.get(dirPath, newFileName).toString();
        
        // Check if file exists
        assertFalse(Files.exists(Paths.get(newFilePath)), "File should not exist in the directory");
    }

    @Then("the directory listing should be returned correctly")
    public void theDirectoryListingShouldBeReturnedCorrectly() {
        String dirPath = (String) testContext.get("dirPath");
        @SuppressWarnings("unchecked")
        List<String> listedFiles = (List<String>) testContext.get("listedFiles");
        
        assertNotNull(dirPath, "Directory path should be in test context");
        assertNotNull(listedFiles, "Listed files should be in test context");
        
        // Check that all expected files are listed
        try {
            List<String> expectedFiles = Files.list(Paths.get(dirPath))
                    .map(Path::toString)
                    .collect(Collectors.toList());
            
            assertEquals(expectedFiles.size(), listedFiles.size(), "Number of files should match");
            
            // Convert to sets for comparison regardless of order
            assertEquals(new HashSet<>(expectedFiles), new HashSet<>(listedFiles), 
                    "Listed files should match expected files");
        } catch (IOException e) {
            fail("Failed to list files directly: " + e.getMessage());
        }
    }

    @Then("the listing operation should be logged with authorization details")
    public void theListingOperationShouldBeLoggedWithAuthorizationDetails() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Check the audit log
        boolean listLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("LIST") && entry.contains(dirPath));
        
        assertTrue(listLogged, "List operation should be logged with authorization details");
    }

    @Then("the listing access should be denied")
    public void theListingAccessShouldBeDenied() {
        Boolean listSuccess = (Boolean) testContext.get("listSuccess");
        Boolean securityViolation = (Boolean) testContext.get("securityViolation");
        
        assertNotNull(listSuccess, "List success flag should be in test context");
        assertNotNull(securityViolation, "Security violation flag should be in test context");
        
        assertFalse(listSuccess, "List should not succeed");
        assertTrue(securityViolation, "Security violation should be recorded");
    }

    @Then("the directory contents should not be accessible")
    public void theDirectoryContentsShouldNotBeAccessible() {
        @SuppressWarnings("unchecked")
        List<String> listedFiles = (List<String>) testContext.get("listedFiles");
        assertNull(listedFiles, "Listed files should be null");
    }

    @Then("{string} update should succeed")
    public void userUpdateShouldSucceed(String username) {
        Boolean writeSuccess = (Boolean) testContext.get(username + "WriteSuccess");
        assertNotNull(writeSuccess, username + " write success flag should be in test context");
        
        assertTrue(writeSuccess, username + " update should succeed");
    }

    @Then("{string} update should be denied")
    public void userUpdateShouldBeDenied(String username) {
        Boolean writeSuccess = (Boolean) testContext.get(username + "WriteSuccess");
        Boolean securityViolation = (Boolean) testContext.get(username + "SecurityViolation");
        
        assertNotNull(writeSuccess, username + " write success flag should be in test context");
        assertNotNull(securityViolation, username + " security violation flag should be in test context");
        
        assertFalse(writeSuccess, username + " update should not succeed");
        assertTrue(securityViolation, username + " security violation should be recorded");
    }

    @Then("{string} should be able to read the updated content")
    public void userShouldBeAbleToReadTheUpdatedContent(String username) {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Get user context
        TestSecurityContext userContext = (TestSecurityContext) testContext.get(username + "Context");
        assertNotNull(userContext, "User context should be in test context");
        
        // Switch to this user's context
        secureFileService.setSecurityContext(userContext);
        
        String adminNewContent = (String) testContext.get("adminNewContent");
        
        try {
            // Read the file
            String readContent = secureFileService.readFile(filePath);
            
            // Verify content matches what admin wrote
            assertEquals(adminNewContent, readContent, "Read content should match admin's update");
            
            // Store result
            testContext.put(username + "ReadSuccess", true);
        } catch (AccessDeniedException e) {
            testContext.put(username + "ReadSuccess", false);
            fail(username + " should be able to read the file: " + e.getMessage());
        }
    }

    @Then("all access attempts should be properly logged")
    public void allAccessAttemptsShouldBeProperlyLogged() {
        // Check for admin successful write
        boolean adminWriteLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("WRITE") && entry.contains("admin") && !entry.contains("VIOLATION"));
        
        // Check for regular user failed write
        boolean regularWriteDeniedLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("WRITE") && entry.contains("regular") && entry.contains("VIOLATION"));
        
        // Check for regular user successful read
        boolean regularReadLogged = auditLog.stream()
                .anyMatch(entry -> entry.contains("READ") && entry.contains("regular") && !entry.contains("VIOLATION"));
        
        assertTrue(adminWriteLogged, "Admin write should be logged as successful");
        assertTrue(regularWriteDeniedLogged, "Regular user write should be logged as denied");
        assertTrue(regularReadLogged, "Regular user read should be logged as successful");
    }
    
    /**
     * Helper method to recursively delete a directory.
     */
    private void deleteDirectory(java.io.File directory) {
        java.io.File[] files = directory.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
    
    /**
     * Implementation of a secure file service that integrates SecurityPort and FileSystemPort.
     */
    private class SecureFileService {
        private final FileSystemPort fileSystemPort;
        private final SecurityPort securityPort;
        private final LoggerPort logger;
        private SecurityContext securityContext;
        
        public SecureFileService(FileSystemPort fileSystemPort, SecurityPort securityPort, LoggerPort logger) {
            this.fileSystemPort = fileSystemPort;
            this.securityPort = securityPort;
            this.logger = logger;
            this.securityContext = null;
        }
        
        public void setSecurityContext(SecurityContext securityContext) {
            this.securityContext = securityContext;
        }
        
        public String readFile(String filePath) throws AccessDeniedException {
            // Check READ permission
            if (!securityPort.hasPermission(securityContext, filePath, Permission.READ)) {
                throw new AccessDeniedException("READ access denied for " + filePath);
            }
            
            // Read file
            Optional<String> content = fileSystemPort.readTextFile(filePath);
            
            // Log successful access
            securityPort.logAccess(securityContext, filePath, "READ", true, "Read file");
            
            return content.orElse(null);
        }
        
        public boolean writeFile(String filePath, String content) throws AccessDeniedException {
            // Check if file exists
            boolean exists = Files.exists(Paths.get(filePath));
            
            if (exists) {
                // Check WRITE permission
                if (!securityPort.hasPermission(securityContext, filePath, Permission.WRITE)) {
                    throw new AccessDeniedException("WRITE access denied for " + filePath);
                }
            } else {
                // For new files, check CREATE permission on parent directory
                Path parentPath = Paths.get(filePath).getParent();
                if (parentPath != null && !securityPort.hasPermission(securityContext, parentPath.toString(), Permission.CREATE)) {
                    throw new AccessDeniedException("CREATE access denied for " + parentPath);
                }
            }
            
            // Write file
            boolean success = fileSystemPort.writeTextFile(filePath, content);
            
            // Log successful access
            securityPort.logAccess(securityContext, filePath, exists ? "WRITE" : "CREATE", 
                    success, exists ? "Wrote to file" : "Created file");
            
            return success;
        }
        
        public boolean deleteFile(String filePath) throws AccessDeniedException {
            // Check DELETE permission
            if (!securityPort.hasPermission(securityContext, filePath, Permission.DELETE)) {
                throw new AccessDeniedException("DELETE access denied for " + filePath);
            }
            
            // Delete file
            boolean success = fileSystemPort.deleteFile(filePath);
            
            // Log successful access
            securityPort.logAccess(securityContext, filePath, "DELETE", success, "Deleted file");
            
            return success;
        }
        
        public List<String> listFiles(String dirPath) throws AccessDeniedException {
            // Check LIST permission
            if (!securityPort.hasPermission(securityContext, dirPath, Permission.LIST)) {
                throw new AccessDeniedException("LIST access denied for " + dirPath);
            }
            
            // List files
            List<String> files = fileSystemPort.listFiles(dirPath);
            
            // Log successful access
            securityPort.logAccess(securityContext, dirPath, "LIST", true, "Listed files in directory");
            
            return files;
        }
    }
    
    /**
     * Test implementation of the SecurityContext.
     */
    private static class TestSecurityContext implements SecurityContext {
        private final String username;
        private final String role;
        
        public TestSecurityContext(String username, String role) {
            this.username = username;
            this.role = role;
        }
        
        @Override
        public String getIdentifier() {
            return username;
        }
        
        @Override
        public boolean hasRole(String roleName) {
            return role.equals(roleName);
        }
        
        @Override
        public List<String> getRoles() {
            return Arrays.asList(role);
        }
        
        @Override
        public Map<String, Object> getAttributes() {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("username", username);
            attributes.put("role", role);
            return attributes;
        }
        
        @Override
        public String toString() {
            return "TestSecurityContext(user=" + username + ", role=" + role + ")";
        }
    }
    
    /**
     * Test implementation of SecurityPort for testing.
     */
    private class TestSecurityAdapter implements SecurityPort {
        private final LoggerPort logger;
        private final List<String> auditLog;
        private final Map<String, Map<Permission, Set<String>>> permissions;
        private boolean initialized;
        
        public TestSecurityAdapter(LoggerPort logger, List<String> auditLog) {
            this.logger = logger;
            this.auditLog = auditLog;
            this.permissions = new HashMap<>();
            this.initialized = true;
        }
        
        @Override
        public boolean isInitialized() {
            return initialized;
        }
        
        @Override
        public boolean hasPermission(SecurityContext context, String resource, Permission permission) {
            if (context == null) {
                logger.error("Security context is null");
                return false;
            }
            
            String identifier = context.getIdentifier();
            Map<Permission, Set<String>> userPermissions = permissions.getOrDefault(identifier, new HashMap<>());
            Set<String> resourcesForPermission = userPermissions.getOrDefault(permission, new HashSet<>());
            
            // Check for exact match first
            if (resourcesForPermission.contains(resource)) {
                return true;
            }
            
            // Then check for parent directory permissions for certain operations
            if (permission == Permission.CREATE || permission == Permission.LIST) {
                return resourcesForPermission.stream()
                        .anyMatch(resource::startsWith);
            }
            
            return false;
        }
        
        @Override
        public void grantPermission(SecurityContext context, String resource, Permission permission) {
            if (context == null) {
                logger.error("Security context is null");
                return;
            }
            
            String identifier = context.getIdentifier();
            Map<Permission, Set<String>> userPermissions = permissions.computeIfAbsent(identifier, k -> new HashMap<>());
            Set<String> resourcesForPermission = userPermissions.computeIfAbsent(permission, k -> new HashSet<>());
            resourcesForPermission.add(resource);
            
            logger.debug("Granted {} permission for {} to {}", permission, resource, identifier);
        }
        
        @Override
        public void revokePermission(SecurityContext context, String resource, Permission permission) {
            if (context == null) {
                logger.error("Security context is null");
                return;
            }
            
            String identifier = context.getIdentifier();
            Map<Permission, Set<String>> userPermissions = permissions.get(identifier);
            if (userPermissions != null) {
                Set<String> resourcesForPermission = userPermissions.get(permission);
                if (resourcesForPermission != null) {
                    resourcesForPermission.remove(resource);
                    logger.debug("Revoked {} permission for {} from {}", permission, resource, identifier);
                }
            }
        }
        
        @Override
        public void logAccess(SecurityContext context, String resource, String operation, boolean success, String details) {
            if (context == null) {
                logger.error("Security context is null for access log");
                return;
            }
            
            String identifier = context.getIdentifier();
            String logEntry = String.format(
                    "[%s] %s %s %s: %s by %s (%s)",
                    success ? "SUCCESS" : "VIOLATION",
                    operation,
                    success ? "allowed" : "denied",
                    resource,
                    details,
                    identifier,
                    context.getRoles()
            );
            
            logger.info(logEntry);
            auditLog.add(logEntry);
        }
        
        @Override
        public List<String> getAuditLog(SecurityContext context, String resource) {
            if (context == null) {
                logger.error("Security context is null for audit log request");
                return new ArrayList<>();
            }
            
            // Filter audit log entries for the requested resource
            return auditLog.stream()
                    .filter(entry -> entry.contains(resource))
                    .collect(Collectors.toList());
        }
    }
}