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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.CachePort.CacheRegion;
import org.s8r.application.port.CachePort.CacheResult;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.filesystem.StandardFileSystemAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the integration between CachePort and FileSystemPort.
 */
@IntegrationTest
public class CacheFileSystemIntegrationSteps {

    private CachePort cachePort;
    private FileSystemPort fileSystemPort;
    private CachingFileService cachingFileService;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;
    private Path tempDir;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
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
        };
        
        // Create a temporary directory for tests
        try {
            tempDir = Files.createTempDirectory("s8r-cache-fs-test-");
            tempDir.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary directory", e);
        }
        
        // Initialize the cache and file system ports
        cachePort = new TestCacheAdapter(logger);
        fileSystemPort = new StandardFileSystemAdapter(logger);
        
        // Initialize the caching file service
        cachingFileService = new CachingFileService(fileSystemPort, cachePort, logger);
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
        
        // Clean up the temporary directory
        try {
            if (tempDir != null && Files.exists(tempDir)) {
                deleteDirectory(tempDir.toFile());
            }
        } catch (Exception e) {
            logger.error("Error cleaning up temporary directory: " + e.getMessage());
        }
        
        // Shutdown the cache
        cachePort.shutdown();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(cachePort, "CachePort should be initialized");
        assertNotNull(fileSystemPort, "FileSystemPort should be initialized");
        assertNotNull(cachingFileService, "CachingFileService should be initialized");
    }

    @Given("both cache and file system ports are properly initialized")
    public void bothCacheAndFileSystemPortsAreProperlyInitialized() {
        // Ensure the cache is initialized
        boolean cacheInitialized = cachePort.initialize();
        assertTrue(cacheInitialized, "Cache should be initialized successfully");
        
        // Ensure temp directory exists for the file system
        assertTrue(Files.exists(tempDir), "Temporary directory should exist");
    }

    @Given("test files are prepared in a temporary directory")
    public void testFilesArePreparedInATemporaryDirectory() {
        // Store the temp directory path
        testContext.put("tempDir", tempDir.toString());
    }

    @Given("a file {string} with test content exists")
    public void aFileWithTestContentExists(String fileName) {
        String content = "{\n  \"name\": \"Test Data\",\n  \"value\": 42,\n  \"active\": true\n}";
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

    @Given("a file {string} exists with initial content")
    public void aFileExistsWithInitialContent(String fileName) {
        String initialContent = "{\n  \"setting\": \"initial value\",\n  \"enabled\": true\n}";
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

    @Given("the file content is cached")
    public void theFileContentIsCached() {
        String filePath = (String) testContext.get("filePath");
        String fileName = (String) testContext.get("fileName");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(fileName, "File name should be in test context");
        
        // Read the file through the caching service
        String content = cachingFileService.readFile(filePath);
        
        // Verify content was cached
        assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath), 
                "File content should be cached after reading");
        
        // Store the read content
        testContext.put("readContent", content);
    }

    @Given("a file {string} exists and its content is cached")
    public void aFileExistsAndItsContentIsCached(String fileName) {
        // Create the file
        String content = "{\n  \"shared\": \"initial data\",\n  \"lastModified\": \"2025-04-07\"\n}";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
            assertTrue(Files.exists(filePath), "File should be created successfully");
            
            // Store for later use
            testContext.put("filePath", filePath.toString());
            testContext.put("fileName", fileName);
            testContext.put("originalContent", content);
            
            // Read through caching service to cache it
            String cachedContent = cachingFileService.readFile(filePath.toString());
            assertEquals(content, cachedContent, "Cached content should match file content");
            
            // Verify content was cached
            assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath.toString()), 
                    "File content should be cached after reading");
            
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
    }

    @Given("a directory with multiple files exists")
    public void aDirectoryWithMultipleFilesExists() {
        Path dirPath = tempDir.resolve("testdir");
        
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

    @Given("file metadata is cached with a short TTL")
    public void fileMetadataIsCachedWithAShortTTL() {
        String fileName = "metadata-test.txt";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            // Create the file
            Files.write(filePath, "Metadata test content".getBytes(StandardCharsets.UTF_8));
            
            // Store for later use
            testContext.put("metadataFilePath", filePath.toString());
            testContext.put("metadataFileName", fileName);
            
            // Get metadata through caching service (with short TTL)
            Map<String, Object> metadata = cachingFileService.getFileMetadataWithTTL(
                    filePath.toString(), Duration.ofMillis(500));
            
            assertNotNull(metadata, "Metadata should be retrieved successfully");
            assertTrue(metadata.containsKey("size"), "Metadata should include file size");
            
            // Store the original metadata
            testContext.put("originalMetadata", new HashMap<>(metadata));
            
        } catch (IOException e) {
            fail("Failed to create metadata test file: " + e.getMessage());
        }
    }

    @Given("a batch of files needs to be processed")
    public void aBatchOfFilesNeedsToBeProcessed() {
        Path batchDir = tempDir.resolve("batch");
        
        try {
            Files.createDirectory(batchDir);
            
            // Create some test files
            List<String> batchFiles = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                Path filePath = batchDir.resolve("batch_file" + i + ".txt");
                Files.write(filePath, ("Batch file " + i + " content").getBytes(StandardCharsets.UTF_8));
                batchFiles.add(filePath.toString());
            }
            
            assertTrue(Files.exists(batchDir), "Batch directory should be created successfully");
            assertEquals(10, Files.list(batchDir).count(), "Batch directory should contain 10 files");
            
            // Store for later use
            testContext.put("batchDir", batchDir.toString());
            testContext.put("batchFiles", batchFiles);
        } catch (IOException e) {
            fail("Failed to create batch files: " + e.getMessage());
        }
    }

    @Given("persistent cache is enabled for the file service")
    public void persistentCacheIsEnabledForTheFileService() {
        // Create a test file
        String fileName = "persistent-cache-test.txt";
        Path filePath = tempDir.resolve(fileName);
        
        try {
            Files.write(filePath, "Persistent cache test content".getBytes(StandardCharsets.UTF_8));
            
            // Store for later use
            testContext.put("persistentFilePath", filePath.toString());
            testContext.put("persistentFileName", fileName);
            
            // Enable persistent cache in our service
            cachingFileService.enablePersistentCache(true);
            
            // Read the file to cache it
            String content = cachingFileService.readFile(filePath.toString());
            assertNotNull(content, "File content should be read successfully");
            
            // Verify content was cached
            assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath.toString()), 
                    "File content should be cached after reading");
            
        } catch (IOException e) {
            fail("Failed to create persistent cache test file: " + e.getMessage());
        }
    }

    @When("I read the file through the caching file service")
    public void iReadTheFileThroughTheCachingFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Read the file
        String content = cachingFileService.readFile(filePath);
        
        // Store the result
        testContext.put("readContent", content);
        testContext.put("cacheHit", false); // First read is never a cache hit
    }

    @When("I read the same file again")
    public void iReadTheSameFileAgain() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Reset cache metrics
        cachingFileService.resetMetrics();
        
        // Read the file again
        String content = cachingFileService.readFile(filePath);
        
        // Store the result
        testContext.put("secondReadContent", content);
        testContext.put("cacheHit", cachingFileService.getMetrics().get("cacheHits") > 0);
    }

    @When("I update the file with new content through the caching file service")
    public void iUpdateTheFileWithNewContentThroughTheCachingFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // New content to write
        String newContent = "{\n  \"setting\": \"updated value\",\n  \"enabled\": false,\n  \"timestamp\": \"2025-04-07\"\n}";
        
        // Write to the file
        boolean success = cachingFileService.writeFile(filePath, newContent);
        
        // Store the result
        testContext.put("writeSuccess", success);
        testContext.put("newContent", newContent);
    }

    @When("the file is modified directly on the file system")
    public void theFileIsModifiedDirectlyOnTheFileSystem() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // New content to write directly
        String newContent = "{\n  \"shared\": \"modified data\",\n  \"lastModified\": \"2025-04-08\"\n}";
        
        try {
            // Write directly to the file
            Files.write(Paths.get(filePath), newContent.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
            
            // Store the modification
            testContext.put("externalModification", true);
            testContext.put("externalContent", newContent);
            
        } catch (IOException e) {
            fail("Failed to modify file directly: " + e.getMessage());
        }
    }

    @When("I check if the file has changed using the caching file service")
    public void iCheckIfTheFileHasChangedUsingTheCachingFileService() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check if the file has changed
        boolean hasChanged = cachingFileService.hasFileChanged(filePath);
        
        // Store the result
        testContext.put("fileChanged", hasChanged);
    }

    @When("I list files in the directory through the caching file service")
    public void iListFilesInTheDirectoryThroughTheCachingFileService() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // List files
        List<String> files = cachingFileService.listFiles(dirPath);
        
        // Store the result
        testContext.put("listedFiles", files);
        testContext.put("cacheHit", false); // First listing is never a cache hit
    }

    @When("I request the directory listing again")
    public void iRequestTheDirectoryListingAgain() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Reset cache metrics
        cachingFileService.resetMetrics();
        
        // List files again
        List<String> files = cachingFileService.listFiles(dirPath);
        
        // Store the result
        testContext.put("secondListedFiles", files);
        testContext.put("cacheHit", cachingFileService.getMetrics().get("cacheHits") > 0);
    }

    @When("a new file is added to the directory directly")
    public void aNewFileIsAddedToTheDirectoryDirectly() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Add a new file directly
        String newFileName = "new_file.txt";
        Path newFilePath = Paths.get(dirPath, newFileName);
        
        try {
            Files.write(newFilePath, "New file content".getBytes(StandardCharsets.UTF_8));
            
            // Store the new file info
            testContext.put("newFilePath", newFilePath.toString());
            testContext.put("newFileName", newFileName);
            
        } catch (IOException e) {
            fail("Failed to add new file: " + e.getMessage());
        }
    }

    @When("I request a refreshed directory listing")
    public void iRequestARefreshedDirectoryListing() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Force a refresh of the directory listing
        List<String> files = cachingFileService.listFilesWithRefresh(dirPath);
        
        // Store the result
        testContext.put("refreshedFiles", files);
    }

    @When("I wait for the TTL to expire")
    public void iWaitForTheTTLToExpire() {
        // Wait a bit more than the TTL (500ms)
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Thread interrupted while waiting for TTL to expire");
        }
    }

    @When("I request the file metadata again")
    public void iRequestTheFileMetadataAgain() {
        String filePath = (String) testContext.get("metadataFilePath");
        assertNotNull(filePath, "Metadata file path should be in test context");
        
        // Reset cache metrics
        cachingFileService.resetMetrics();
        
        // Get metadata again
        Map<String, Object> metadata = cachingFileService.getFileMetadata(filePath);
        
        // Store the result
        testContext.put("refreshedMetadata", metadata);
        testContext.put("cacheHit", cachingFileService.getMetrics().get("cacheHits") > 0);
    }

    @When("I process the files through the caching file service")
    public void iProcessTheFilesThroughTheCachingFileService() {
        @SuppressWarnings("unchecked")
        List<String> batchFiles = (List<String>) testContext.get("batchFiles");
        assertNotNull(batchFiles, "Batch files should be in test context");
        
        // Reset cache metrics
        cachingFileService.resetMetrics();
        
        // Process each file
        Map<String, String> processedContents = new HashMap<>();
        for (String filePath : batchFiles) {
            String content = cachingFileService.readAndProcessFile(filePath);
            processedContents.put(filePath, content);
        }
        
        // Process the same files again to test cache
        Map<String, String> secondProcessedContents = new HashMap<>();
        for (String filePath : batchFiles) {
            String content = cachingFileService.readAndProcessFile(filePath);
            secondProcessedContents.put(filePath, content);
        }
        
        // Store the results
        testContext.put("processedContents", processedContents);
        testContext.put("secondProcessedContents", secondProcessedContents);
        testContext.put("batchMetrics", cachingFileService.getMetrics());
    }

    @When("the service is restarted")
    public void theServiceIsRestarted() {
        // Simulate a service restart by creating a new service
        cachingFileService = new CachingFileService(fileSystemPort, cachePort, logger);
        
        // Re-enable persistent cache
        cachingFileService.enablePersistentCache(true);
    }

    @When("I read a previously cached file")
    public void iReadAPreviouslyCachedFile() {
        String filePath = (String) testContext.get("persistentFilePath");
        assertNotNull(filePath, "Persistent file path should be in test context");
        
        // Reset cache metrics
        cachingFileService.resetMetrics();
        
        // Read the file
        String content = cachingFileService.readFile(filePath);
        
        // Store the result
        testContext.put("persistentReadContent", content);
        testContext.put("persistentCacheHit", cachingFileService.getMetrics().get("cacheHits") > 0);
    }

    @Then("the file content should be returned correctly")
    public void theFileContentShouldBeReturnedCorrectly() {
        String originalContent = (String) testContext.get("originalContent");
        String readContent = (String) testContext.get("readContent");
        
        assertNotNull(originalContent, "Original content should be in test context");
        assertNotNull(readContent, "Read content should be in test context");
        
        assertEquals(originalContent, readContent, "Read content should match original content");
    }

    @Then("the content should be stored in the cache")
    public void theContentShouldBeStoredInTheCache() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check if the content is in the cache
        assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath), 
                "File content should be in the cache");
    }

    @Then("the content should be served from the cache")
    public void theContentShouldBeServedFromTheCache() {
        Boolean cacheHit = (Boolean) testContext.get("cacheHit");
        assertNotNull(cacheHit, "Cache hit flag should be in test context");
        
        assertTrue(cacheHit, "Content should be served from the cache");
    }

    @Then("file read operations metrics should show cache hit")
    public void fileReadOperationsMetricsShouldShowCacheHit() {
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheHits"), "Metrics should include cache hits");
        assertTrue(metrics.get("cacheHits") > 0, "Cache hits should be greater than zero");
    }

    @Then("the file should be updated on disk")
    public void theFileShouldBeUpdatedOnDisk() {
        String filePath = (String) testContext.get("filePath");
        String newContent = (String) testContext.get("newContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(newContent, "New content should be in test context");
        
        try {
            // Read the file directly
            String actualContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            
            assertEquals(newContent, actualContent, "File content on disk should match new content");
        } catch (IOException e) {
            fail("Failed to read updated file: " + e.getMessage());
        }
    }

    @Then("the cache should be invalidated for this file")
    public void theCacheShouldBeInvalidatedForThisFile() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check metrics to see if the cache was invalidated
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheInvalidations"), "Metrics should include cache invalidations");
        assertTrue(metrics.get("cacheInvalidations") > 0, "Cache invalidations should be greater than zero");
    }

    @Then("the new content should be returned")
    public void theNewContentShouldBeReturned() {
        String filePath = (String) testContext.get("filePath");
        String newContent = (String) testContext.get("newContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(newContent, "New content should be in test context");
        
        // Read the file again
        String readContent = cachingFileService.readFile(filePath);
        
        assertEquals(newContent, readContent, "Read content should match new content");
    }

    @Then("the content should be cached again")
    public void theContentShouldBeCachedAgain() {
        String filePath = (String) testContext.get("filePath");
        assertNotNull(filePath, "File path should be in test context");
        
        // Check if the content is in the cache
        assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath), 
                "New file content should be in the cache");
    }

    @Then("the service should detect the file modification")
    public void theServiceShouldDetectTheFileModification() {
        Boolean fileChanged = (Boolean) testContext.get("fileChanged");
        assertNotNull(fileChanged, "File changed flag should be in test context");
        
        assertTrue(fileChanged, "Service should detect file modification");
    }

    @Then("the cache should be refreshed with the new content")
    public void theCacheShouldBeRefreshedWithTheNewContent() {
        String filePath = (String) testContext.get("filePath");
        String externalContent = (String) testContext.get("externalContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(externalContent, "External content should be in test context");
        
        // Check metrics to see if the cache was refreshed
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheRefreshes"), "Metrics should include cache refreshes");
        assertTrue(metrics.get("cacheRefreshes") > 0, "Cache refreshes should be greater than zero");
    }

    @Then("the updated content should be returned")
    public void theUpdatedContentShouldBeReturned() {
        String filePath = (String) testContext.get("filePath");
        String externalContent = (String) testContext.get("externalContent");
        
        assertNotNull(filePath, "File path should be in test context");
        assertNotNull(externalContent, "External content should be in test context");
        
        // Read the file again
        String readContent = cachingFileService.readFile(filePath);
        
        assertEquals(externalContent, readContent, "Read content should match external content");
    }

    @Then("the directory listing should be cached")
    public void theDirectoryListingShouldBeCached() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Check if the listing is in the cache
        assertTrue(cachePort.containsKey(CacheRegion.GENERAL, dirPath + "-listing"), 
                "Directory listing should be in the cache");
    }

    @Then("the listing should be served from the cache")
    public void theListingShouldBeServedFromTheCache() {
        Boolean cacheHit = (Boolean) testContext.get("cacheHit");
        assertNotNull(cacheHit, "Cache hit flag should be in test context");
        
        assertTrue(cacheHit, "Listing should be served from the cache");
        
        // Also check that the files match between first and second listing
        @SuppressWarnings("unchecked")
        List<String> listedFiles = (List<String>) testContext.get("listedFiles");
        @SuppressWarnings("unchecked")
        List<String> secondListedFiles = (List<String>) testContext.get("secondListedFiles");
        
        assertNotNull(listedFiles, "Listed files should be in test context");
        assertNotNull(secondListedFiles, "Second listed files should be in test context");
        
        assertEquals(listedFiles.size(), secondListedFiles.size(), "Number of files should match");
        assertEquals(new HashSet<>(listedFiles), new HashSet<>(secondListedFiles), "Files should be the same");
    }

    @Then("the cache should be updated with the new listing")
    public void theCacheShouldBeUpdatedWithTheNewListing() {
        String dirPath = (String) testContext.get("dirPath");
        assertNotNull(dirPath, "Directory path should be in test context");
        
        // Check metrics to see if the cache was updated
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheUpdates"), "Metrics should include cache updates");
        assertTrue(metrics.get("cacheUpdates") > 0, "Cache updates should be greater than zero");
    }

    @Then("the new file should appear in the result")
    public void theNewFileShouldAppearInTheResult() {
        @SuppressWarnings("unchecked")
        List<String> refreshedFiles = (List<String>) testContext.get("refreshedFiles");
        String newFileName = (String) testContext.get("newFileName");
        
        assertNotNull(refreshedFiles, "Refreshed files should be in test context");
        assertNotNull(newFileName, "New file name should be in test context");
        
        // Check if the new file is in the listing
        boolean containsNewFile = refreshedFiles.stream()
                .map(File::new)
                .map(File::getName)
                .anyMatch(name -> name.equals(newFileName));
                
        assertTrue(containsNewFile, "Refreshed listing should include the new file");
    }

    @Then("the metadata should be fetched from the file system")
    public void theMetadataShouldBeFetchedFromTheFileSystem() {
        Boolean cacheHit = (Boolean) testContext.get("cacheHit");
        assertNotNull(cacheHit, "Cache hit flag should be in test context");
        
        assertFalse(cacheHit, "Metadata should be fetched from the file system, not the cache");
    }

    @Then("the cache should be updated with fresh metadata")
    public void theCacheShouldBeUpdatedWithFreshMetadata() {
        String filePath = (String) testContext.get("metadataFilePath");
        assertNotNull(filePath, "Metadata file path should be in test context");
        
        // Check if the metadata is in the cache
        assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath + "-metadata"), 
                "Metadata should be in the cache again");
        
        // Check metrics to see if the cache was updated
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheUpdates"), "Metrics should include cache updates");
        assertTrue(metrics.get("cacheUpdates") > 0, "Cache updates should be greater than zero");
    }

    @Then("each file should be processed only once")
    public void eachFileShouldBeProcessedOnlyOnce() {
        @SuppressWarnings("unchecked")
        Map<String, String> processedContents = (Map<String, String>) testContext.get("processedContents");
        @SuppressWarnings("unchecked")
        Map<String, String> secondProcessedContents = (Map<String, String>) testContext.get("secondProcessedContents");
        
        assertNotNull(processedContents, "Processed contents should be in test context");
        assertNotNull(secondProcessedContents, "Second processed contents should be in test context");
        
        // Check that all content matches between first and second processing
        for (String filePath : processedContents.keySet()) {
            assertEquals(processedContents.get(filePath), secondProcessedContents.get(filePath),
                    "Processed content should match for " + filePath);
        }
    }

    @Then("file contents should be cached during processing")
    public void fileContentsShouldBeCachedDuringProcessing() {
        @SuppressWarnings("unchecked")
        List<String> batchFiles = (List<String>) testContext.get("batchFiles");
        assertNotNull(batchFiles, "Batch files should be in test context");
        
        // Check that all files are cached
        for (String filePath : batchFiles) {
            assertTrue(cachePort.containsKey(CacheRegion.GENERAL, filePath), 
                    "File content should be cached for " + filePath);
        }
    }

    @Then("batch operation performance metrics should show cache benefits")
    public void batchOperationPerformanceMetricsShouldShowCacheBenefits() {
        @SuppressWarnings("unchecked")
        Map<String, Integer> metrics = (Map<String, Integer>) testContext.get("batchMetrics");
        assertNotNull(metrics, "Batch metrics should be in test context");
        
        // Check that we have cache hits from the second pass
        assertTrue(metrics.containsKey("cacheHits"), "Metrics should include cache hits");
        assertTrue(metrics.get("cacheHits") > 0, "Cache hits should be greater than zero");
        
        // Should have fewer file reads than total operations
        assertTrue(metrics.get("fileReads") < metrics.get("totalOperations"), 
                "File reads should be fewer than total operations");
    }

    @Then("the file should be served from the persistent cache")
    public void theFileShouldBeServedFromThePersistentCache() {
        Boolean persistentCacheHit = (Boolean) testContext.get("persistentCacheHit");
        assertNotNull(persistentCacheHit, "Persistent cache hit flag should be in test context");
        
        assertTrue(persistentCacheHit, "File should be served from the persistent cache");
    }

    @Then("the service should verify the cache is still valid")
    public void theServiceShouldVerifyTheCacheIsStillValid() {
        // Check metrics to see if the service validated the cache
        Map<String, Integer> metrics = cachingFileService.getMetrics();
        
        assertTrue(metrics.containsKey("cacheValidations"), "Metrics should include cache validations");
        assertTrue(metrics.get("cacheValidations") > 0, "Cache validations should be greater than zero");
    }
    
    /**
     * Helper method to recursively delete a directory.
     */
    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
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
     * Implementation of a caching file service that integrates CachePort and FileSystemPort.
     */
    private class CachingFileService {
        private final FileSystemPort fileSystemPort;
        private final CachePort cachePort;
        private final LoggerPort logger;
        private final Map<String, Integer> metrics;
        private boolean persistentCache;
        
        public CachingFileService(FileSystemPort fileSystemPort, CachePort cachePort, LoggerPort logger) {
            this.fileSystemPort = fileSystemPort;
            this.cachePort = cachePort;
            this.logger = logger;
            this.metrics = new HashMap<>();
            this.persistentCache = false;
            resetMetrics();
        }
        
        public void resetMetrics() {
            metrics.clear();
            metrics.put("cacheHits", 0);
            metrics.put("cacheMisses", 0);
            metrics.put("cacheInvalidations", 0);
            metrics.put("cacheRefreshes", 0);
            metrics.put("cacheUpdates", 0);
            metrics.put("cacheValidations", 0);
            metrics.put("fileReads", 0);
            metrics.put("fileWrites", 0);
            metrics.put("totalOperations", 0);
        }
        
        public Map<String, Integer> getMetrics() {
            return new HashMap<>(metrics);
        }
        
        public void enablePersistentCache(boolean enable) {
            this.persistentCache = enable;
        }
        
        public String readFile(String filePath) {
            incrementMetric("totalOperations");
            
            // Try to get from cache first
            CacheResult cacheResult = cachePort.get(CacheRegion.GENERAL, filePath, String.class);
            
            if (cacheResult.isSuccessful() && cacheResult.getAttributes().containsKey("value")) {
                incrementMetric("cacheHits");
                logger.debug("Cache hit for file: {}", filePath);
                return (String) cacheResult.getAttributes().get("value");
            }
            
            incrementMetric("cacheMisses");
            logger.debug("Cache miss for file: {}", filePath);
            
            // Read from file system
            incrementMetric("fileReads");
            Optional<String> fileContent = fileSystemPort.readTextFile(filePath);
            
            if (fileContent.isPresent()) {
                String content = fileContent.get();
                
                // Cache the content
                cachePort.put(CacheRegion.GENERAL, filePath, content);
                incrementMetric("cacheUpdates");
                
                return content;
            } else {
                logger.warn("Failed to read file: {}", filePath);
                return null;
            }
        }
        
        public boolean writeFile(String filePath, String content) {
            incrementMetric("totalOperations");
            incrementMetric("fileWrites");
            
            // Write to file system
            boolean success = fileSystemPort.writeTextFile(filePath, content);
            
            if (success) {
                // Invalidate cache
                cachePort.remove(CacheRegion.GENERAL, filePath);
                incrementMetric("cacheInvalidations");
                logger.debug("Cache invalidated for file after write: {}", filePath);
            }
            
            return success;
        }
        
        public boolean hasFileChanged(String filePath) {
            incrementMetric("totalOperations");
            
            // Get file metadata
            Map<String, Object> metadata = fileSystemPort.getFileMetadata(filePath);
            
            if (metadata.containsKey("lastModified")) {
                long lastModified = (long) metadata.get("lastModified");
                
                // Check if cache has this file
                if (cachePort.containsKey(CacheRegion.GENERAL, filePath + "-timestamp")) {
                    // Get cached timestamp
                    CacheResult timestampResult = cachePort.get(CacheRegion.GENERAL, 
                            filePath + "-timestamp", Long.class);
                    
                    if (timestampResult.isSuccessful() && timestampResult.getAttributes().containsKey("value")) {
                        long cachedTimestamp = (Long) timestampResult.getAttributes().get("value");
                        
                        // Compare timestamps
                        boolean changed = lastModified > cachedTimestamp;
                        
                        if (changed) {
                            // File has changed, refresh cache
                            String content = readFile(filePath);
                            cachePort.put(CacheRegion.GENERAL, filePath + "-timestamp", lastModified);
                            incrementMetric("cacheRefreshes");
                            logger.debug("File changed, cache refreshed: {}", filePath);
                        } else {
                            incrementMetric("cacheValidations");
                        }
                        
                        return changed;
                    }
                }
                
                // No cached timestamp, store it
                cachePort.put(CacheRegion.GENERAL, filePath + "-timestamp", lastModified);
                incrementMetric("cacheUpdates");
                // Assume file has changed if no cached timestamp
                return true;
            }
            
            return false;
        }
        
        public List<String> listFiles(String dirPath) {
            incrementMetric("totalOperations");
            
            // Try to get from cache first
            CacheResult cacheResult = cachePort.get(CacheRegion.GENERAL, 
                    dirPath + "-listing", List.class);
            
            if (cacheResult.isSuccessful() && cacheResult.getAttributes().containsKey("value")) {
                incrementMetric("cacheHits");
                logger.debug("Cache hit for directory listing: {}", dirPath);
                
                @SuppressWarnings("unchecked")
                List<String> listing = (List<String>) cacheResult.getAttributes().get("value");
                return listing;
            }
            
            incrementMetric("cacheMisses");
            logger.debug("Cache miss for directory listing: {}", dirPath);
            
            // List from file system
            List<String> files = fileSystemPort.listFiles(dirPath);
            
            // Cache the listing
            cachePort.put(CacheRegion.GENERAL, dirPath + "-listing", files);
            incrementMetric("cacheUpdates");
            
            return files;
        }
        
        public List<String> listFilesWithRefresh(String dirPath) {
            incrementMetric("totalOperations");
            
            // Invalidate cache
            cachePort.remove(CacheRegion.GENERAL, dirPath + "-listing");
            incrementMetric("cacheInvalidations");
            
            // List from file system
            List<String> files = fileSystemPort.listFiles(dirPath);
            
            // Cache the listing
            cachePort.put(CacheRegion.GENERAL, dirPath + "-listing", files);
            incrementMetric("cacheUpdates");
            
            return files;
        }
        
        public Map<String, Object> getFileMetadata(String filePath) {
            incrementMetric("totalOperations");
            
            // Try to get from cache first
            CacheResult cacheResult = cachePort.get(CacheRegion.GENERAL, 
                    filePath + "-metadata", Map.class);
            
            if (cacheResult.isSuccessful() && cacheResult.getAttributes().containsKey("value")) {
                incrementMetric("cacheHits");
                logger.debug("Cache hit for file metadata: {}", filePath);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) cacheResult.getAttributes().get("value");
                return metadata;
            }
            
            incrementMetric("cacheMisses");
            logger.debug("Cache miss for file metadata: {}", filePath);
            
            // Get metadata from file system
            Map<String, Object> metadata = fileSystemPort.getFileMetadata(filePath);
            
            // Cache the metadata
            cachePort.put(CacheRegion.GENERAL, filePath + "-metadata", metadata);
            incrementMetric("cacheUpdates");
            
            return metadata;
        }
        
        public Map<String, Object> getFileMetadataWithTTL(String filePath, Duration ttl) {
            incrementMetric("totalOperations");
            
            // Try to get from cache first
            CacheResult cacheResult = cachePort.get(CacheRegion.GENERAL, 
                    filePath + "-metadata", Map.class);
            
            if (cacheResult.isSuccessful() && cacheResult.getAttributes().containsKey("value")) {
                incrementMetric("cacheHits");
                logger.debug("Cache hit for file metadata with TTL: {}", filePath);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> metadata = (Map<String, Object>) cacheResult.getAttributes().get("value");
                return metadata;
            }
            
            incrementMetric("cacheMisses");
            logger.debug("Cache miss for file metadata with TTL: {}", filePath);
            
            // Get metadata from file system
            Map<String, Object> metadata = fileSystemPort.getFileMetadata(filePath);
            
            // Cache the metadata with TTL
            cachePort.put(CacheRegion.GENERAL, filePath + "-metadata", metadata, ttl);
            incrementMetric("cacheUpdates");
            
            return metadata;
        }
        
        public String readAndProcessFile(String filePath) {
            incrementMetric("totalOperations");
            
            // Read the file (using cache)
            String content = readFile(filePath);
            
            if (content != null) {
                // Simulate processing by adding a prefix
                return "PROCESSED: " + content;
            }
            
            return null;
        }
        
        private void incrementMetric(String metricName) {
            metrics.compute(metricName, (k, v) -> v == null ? 1 : v + 1);
        }
    }
    
    /**
     * Test implementation of CachePort for testing.
     */
    private class TestCacheAdapter implements CachePort {
        private final LoggerPort logger;
        private final Map<CacheRegion, Map<String, CacheEntry>> cache;
        private boolean initialized;
        
        public TestCacheAdapter(LoggerPort logger) {
            this.logger = logger;
            this.cache = new HashMap<>();
            for (CacheRegion region : CacheRegion.values()) {
                cache.put(region, new HashMap<>());
            }
            this.initialized = false;
        }
        
        @Override
        public CacheResult initialize() {
            if (initialized) {
                return CacheResult.failure("Cache already initialized", "Already initialized");
            }
            
            initialized = true;
            logger.info("Cache initialized");
            
            return CacheResult.success("Cache initialized successfully");
        }
        
        @Override
        public <T> CacheResult get(CacheRegion region, String key, Class<T> type) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = regionCache.get(key);
            
            if (entry == null) {
                logger.debug("Cache miss for key: {} in region: {}", key, region);
                return CacheResult.failure("Key not found", "not_found", Map.of("key", key, "region", region));
            }
            
            // Check if expired
            if (entry.isExpired()) {
                regionCache.remove(key);
                logger.debug("Cache entry expired: {} in region: {}", key, region);
                return CacheResult.failure("Key expired", "expired", Map.of("key", key, "region", region));
            }
            
            // Verify type compatibility
            if (!type.isInstance(entry.getValue())) {
                logger.debug("Cache type mismatch for key: {} in region: {}", key, region);
                return CacheResult.failure("Type mismatch", "type_mismatch", 
                        Map.of("key", key, "region", region, "expectedType", type.getName(), 
                              "actualType", entry.getValue().getClass().getName()));
            }
            
            logger.debug("Cache hit for key: {} in region: {}", key, region);
            
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue();
            return CacheResult.success("Value retrieved successfully", 
                    Map.of("key", key, "region", region, "value", value));
        }
        
        @Override
        public <T> CacheResult put(CacheRegion region, String key, T value) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = new CacheEntry(value);
            regionCache.put(key, entry);
            
            logger.debug("Value stored in cache: key={}, region={}", key, region);
            
            return CacheResult.success("Value stored successfully", 
                    Map.of("key", key, "region", region));
        }
        
        @Override
        public <T> CacheResult put(CacheRegion region, String key, T value, Duration ttl) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = new CacheEntry(value, ttl);
            regionCache.put(key, entry);
            
            logger.debug("Value stored in cache with TTL: key={}, region={}, ttl={}s", 
                    key, region, ttl.getSeconds());
            
            return CacheResult.success("Value stored successfully with TTL", 
                    Map.of("key", key, "region", region, "ttl", ttl.toMillis()));
        }
        
        @Override
        public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, 
                java.util.function.Function<String, T> provider) {
            
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            CacheResult getResult = get(region, key, type);
            if (getResult.isSuccessful()) {
                return getResult;
            }
            
            // Compute the value
            T value = provider.apply(key);
            if (value == null) {
                return CacheResult.failure("Provider returned null value", "null_value", 
                        Map.of("key", key, "region", region));
            }
            
            // Store in cache
            CacheResult putResult = put(region, key, value);
            if (!putResult.isSuccessful()) {
                return putResult;
            }
            
            logger.debug("Value computed and stored: key={}, region={}", key, region);
            
            return CacheResult.success("Value computed and stored successfully", 
                    Map.of("key", key, "region", region, "value", value, "computed", true));
        }
        
        @Override
        public <T> CacheResult getOrCompute(CacheRegion region, String key, Class<T> type, 
                java.util.function.Function<String, T> provider, Duration ttl) {
            
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            CacheResult getResult = get(region, key, type);
            if (getResult.isSuccessful()) {
                return getResult;
            }
            
            // Compute the value
            T value = provider.apply(key);
            if (value == null) {
                return CacheResult.failure("Provider returned null value", "null_value", 
                        Map.of("key", key, "region", region));
            }
            
            // Store in cache with TTL
            CacheResult putResult = put(region, key, value, ttl);
            if (!putResult.isSuccessful()) {
                return putResult;
            }
            
            logger.debug("Value computed and stored with TTL: key={}, region={}, ttl={}s", 
                    key, region, ttl.getSeconds());
            
            return CacheResult.success("Value computed and stored successfully with TTL", 
                    Map.of("key", key, "region", region, "value", value, 
                          "computed", true, "ttl", ttl.toMillis()));
        }
        
        @Override
        public CacheResult remove(CacheRegion region, String key) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            if (regionCache.remove(key) != null) {
                logger.debug("Value removed from cache: key={}, region={}", key, region);
                return CacheResult.success("Value removed successfully", 
                        Map.of("key", key, "region", region));
            } else {
                logger.debug("Key not found for removal: key={}, region={}", key, region);
                return CacheResult.failure("Key not found", "not_found", 
                        Map.of("key", key, "region", region));
            }
        }
        
        @Override
        public boolean containsKey(CacheRegion region, String key) {
            if (!initialized) {
                return false;
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            CacheEntry entry = regionCache.get(key);
            
            if (entry == null) {
                return false;
            }
            
            // Check if expired
            if (entry.isExpired()) {
                regionCache.remove(key);
                return false;
            }
            
            return true;
        }
        
        @Override
        public CacheResult getKeys(CacheRegion region) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            
            // Remove expired entries
            regionCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            
            return CacheResult.success("Keys retrieved successfully", 
                    Map.of("keys", regionCache.keySet(), "region", region, "count", regionCache.size()));
        }
        
        @Override
        public CacheResult clearRegion(CacheRegion region) {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            Map<String, CacheEntry> regionCache = cache.get(region);
            int count = regionCache.size();
            regionCache.clear();
            
            logger.debug("Cache region cleared: region={}, entries={}", region, count);
            
            return CacheResult.success("Cache region cleared successfully", 
                    Map.of("region", region, "cleared", count));
        }
        
        @Override
        public CacheResult clearAll() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            int totalCount = 0;
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry> regionCache = cache.get(region);
                totalCount += regionCache.size();
                regionCache.clear();
            }
            
            logger.debug("All cache regions cleared: total entries={}", totalCount);
            
            return CacheResult.success("All cache regions cleared successfully", 
                    Map.of("cleared", totalCount));
        }
        
        @Override
        public CacheResult getStatistics() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            int totalEntries = 0;
            Map<String, Integer> regionCounts = new HashMap<>();
            
            for (CacheRegion region : CacheRegion.values()) {
                Map<String, CacheEntry> regionCache = cache.get(region);
                regionCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
                int count = regionCache.size();
                totalEntries += count;
                regionCounts.put(region.name(), count);
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("entries", totalEntries);
            stats.put("regionCounts", regionCounts);
            
            logger.debug("Cache statistics: entries={}", totalEntries);
            
            return CacheResult.success("Statistics retrieved successfully", stats);
        }
        
        @Override
        public CacheResult shutdown() {
            if (!initialized) {
                return CacheResult.failure("Cache not initialized", "Not initialized");
            }
            
            for (CacheRegion region : CacheRegion.values()) {
                cache.get(region).clear();
            }
            
            initialized = false;
            logger.debug("Cache shutdown complete");
            
            return CacheResult.success("Cache shutdown successfully");
        }
        
        @Override
        public StorageType getStorageType() {
            return StorageType.MEMORY;
        }
        
        /**
         * Represents a cache entry with TTL support.
         */
        private static class CacheEntry {
            private final Object value;
            private final long creationTime;
            private final Long expirationTime;  // null means no expiration
            
            public CacheEntry(Object value) {
                this.value = value;
                this.creationTime = System.currentTimeMillis();
                this.expirationTime = null;
            }
            
            public CacheEntry(Object value, Duration ttl) {
                this.value = value;
                this.creationTime = System.currentTimeMillis();
                this.expirationTime = ttl != null ? 
                        System.currentTimeMillis() + ttl.toMillis() : null;
            }
            
            public Object getValue() {
                return value;
            }
            
            public long getCreationTime() {
                return creationTime;
            }
            
            public boolean isExpired() {
                return expirationTime != null && System.currentTimeMillis() > expirationTime;
            }
        }
    }
}