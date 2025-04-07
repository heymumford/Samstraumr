@L3_Integration @Functional @PortIntegration
Feature: Cache and FileSystem Port Integration
  As a system developer
  I want to ensure that CachePort and FileSystemPort work together correctly
  So that file operations are efficiently cached

  Background:
    Given a clean system environment
    And both cache and file system ports are properly initialized
    And test files are prepared in a temporary directory

  Scenario: Reading a file with cache
    Given a file "data.json" with test content exists
    When I read the file through the caching file service
    Then the file content should be returned correctly
    And the content should be stored in the cache
    When I read the same file again
    Then the content should be served from the cache
    And file read operations metrics should show cache hit

  Scenario: Writing and invalidating cache
    Given a file "config.json" exists with initial content
    And the file content is cached
    When I update the file with new content through the caching file service
    Then the file should be updated on disk
    And the cache should be invalidated for this file
    When I read the file again
    Then the new content should be returned
    And the content should be cached again

  Scenario: Handling file changes outside the service
    Given a file "shared.json" exists and its content is cached
    When the file is modified directly on the file system
    And I check if the file has changed using the caching file service
    Then the service should detect the file modification
    And the cache should be refreshed with the new content
    When I read the file
    Then the updated content should be returned

  Scenario: Caching directory listings
    Given a directory with multiple files exists
    When I list files in the directory through the caching file service
    Then the directory listing should be cached
    When I request the directory listing again
    Then the listing should be served from the cache
    When a new file is added to the directory directly
    And I request a refreshed directory listing
    Then the cache should be updated with the new listing
    And the new file should appear in the result

  Scenario: Cache expiration for file metadata
    Given file metadata is cached with a short TTL
    When I wait for the TTL to expire
    And I request the file metadata again
    Then the metadata should be fetched from the file system
    And the cache should be updated with fresh metadata

  Scenario: Batch file operations with caching
    Given a batch of files needs to be processed
    When I process the files through the caching file service
    Then each file should be processed only once
    And file contents should be cached during processing
    And batch operation performance metrics should show cache benefits

  Scenario: Cache persistence across service restarts
    Given persistent cache is enabled for the file service
    And a set of files has been cached
    When the service is restarted
    And I read a previously cached file
    Then the file should be served from the persistent cache
    And the service should verify the cache is still valid