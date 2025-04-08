# Copyright (c) 2025
# All rights reserved.

@port @L1
Feature: FileSystem Port Interface
  As an application developer
  I want to use a standardized FileSystem Port interface
  So that I can perform file operations in a consistent way

  Background:
    Given a filesystem port implementation is available
    And a temporary test directory exists

  @smoke
  Scenario: Read from and write to a file
    When I write the content "Hello, World!" to file "test-file.txt"
    Then the file "test-file.txt" should exist
    And the content of file "test-file.txt" should be "Hello, World!"

  Scenario: Check if a file exists
    Given the file "existing-file.txt" exists with content "This file exists"
    When I check if file "existing-file.txt" exists
    Then the result should be true
    When I check if file "non-existing-file.txt" exists
    Then the result should be false

  Scenario: Create a directory
    When I create directory "test-directory"
    Then the directory "test-directory" should exist
    And I should be able to write a file to "test-directory/nested-file.txt"

  Scenario: List files in a directory
    Given the directory "list-test" contains the following files:
      | file1.txt |
      | file2.txt |
      | file3.txt |
    When I list files in directory "list-test"
    Then the list should contain the following files:
      | file1.txt |
      | file2.txt |
      | file3.txt |

  Scenario: Delete a file
    Given the file "to-delete.txt" exists with content "Delete me"
    When I delete the file "to-delete.txt"
    Then the file "to-delete.txt" should not exist

  Scenario: Delete a directory with contents
    Given the directory "dir-to-delete" contains the following files:
      | nested1.txt |
      | nested2.txt |
    When I delete the directory "dir-to-delete"
    Then the directory "dir-to-delete" should not exist