@L3_Integration @Functional @PortIntegration
Feature: Security and FileSystem Port Integration
  As a system developer
  I want to ensure that SecurityPort and FileSystemPort work together correctly
  So that file operations are secured with proper access controls

  Background:
    Given a clean system environment
    And both security and file system ports are properly initialized
    And test files are prepared in a secured directory

  Scenario: Reading a file with proper authorization
    Given a secured file "confidential.txt" with sensitive content exists
    And the user has READ permission for the file
    When I read the file through the secure file service
    Then the file content should be returned correctly
    And the access should be logged with authorization details

  Scenario: Unauthorized file read attempt
    Given a secured file "restricted.txt" with sensitive content exists
    And the user does not have READ permission for the file
    When I attempt to read the file through the secure file service
    Then the access should be denied
    And a security violation should be logged
    And the file content should not be accessible

  Scenario: Writing to a file with proper authorization
    Given a secured file "writable.txt" exists with initial content
    And the user has WRITE permission for the file
    When I update the file with new content through the secure file service
    Then the file should be updated on disk
    And the write operation should be logged with authorization details

  Scenario: Unauthorized file write attempt
    Given a secured file "readonly.txt" exists with initial content
    And the user does not have WRITE permission for the file
    When I attempt to update the file with new content
    Then the write access should be denied
    And a security violation should be logged
    And the file content should not be modified

  Scenario: Deleting a file with proper authorization
    Given a secured file "deletable.txt" exists
    And the user has DELETE permission for the file
    When I delete the file through the secure file service
    Then the file should be deleted from disk
    And the delete operation should be logged with authorization details

  Scenario: Unauthorized file delete attempt
    Given a secured file "protected.txt" exists
    And the user does not have DELETE permission for the file
    When I attempt to delete the file
    Then the delete access should be denied
    And a security violation should be logged
    And the file should still exist on disk

  Scenario: Creating a file in a secured directory with proper authorization
    Given a secured directory exists
    And the user has CREATE permission for the directory
    When I create a new file in the directory through the secure file service
    Then the file should be created with appropriate permissions
    And the creation should be logged with authorization details

  Scenario: Unauthorized file creation attempt
    Given a secured directory exists
    And the user does not have CREATE permission for the directory
    When I attempt to create a new file in the directory
    Then the creation access should be denied
    And a security violation should be logged
    And the file should not exist in the directory

  Scenario: Directory listing with proper authorization
    Given a secured directory with multiple files exists
    And the user has LIST permission for the directory
    When I list files in the directory through the secure file service
    Then the directory listing should be returned correctly
    And the listing operation should be logged with authorization details

  Scenario: Unauthorized directory listing attempt
    Given a secured directory with multiple files exists
    And the user does not have LIST permission for the directory
    When I attempt to list files in the directory
    Then the listing access should be denied
    And a security violation should be logged
    And the directory contents should not be accessible

  Scenario: File access across multiple security contexts
    Given a secured file "shared.txt" exists
    And user "admin" has ALL permissions for the file
    And user "regular" has only READ permission for the file
    When "admin" updates the file content
    And "regular" attempts to update the file content
    Then "admin" update should succeed
    And "regular" update should be denied
    And "regular" should be able to read the updated content
    And all access attempts should be properly logged