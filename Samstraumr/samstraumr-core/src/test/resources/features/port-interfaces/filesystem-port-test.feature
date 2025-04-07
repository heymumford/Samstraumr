@L2_Integration @Functional @PortInterface @FileSystem
Feature: File System Port Interface
  As a system developer
  I want to use the FileSystemPort interface for file operations
  So that I can keep my application core independent of specific file system implementations

  Background:
    Given a clean system environment
    And the FileSystemPort interface is properly initialized
    And a test directory is created for file operations

  Scenario: Creating and reading a file
    When I create a file "test-file.txt" with content "This is a test file"
    Then the file should exist
    And the file content should be "This is a test file"
    And the file size should be correct

  Scenario: Creating and listing directories
    When I create a directory "test-dir"
    And I create a file "test-dir/nested-file.txt" with content "Nested file content"
    Then the directory should exist
    And listing the directory should show the nested file
    And the nested file should have the correct content

  Scenario: Reading and writing files with different character sets
    When I create a file "utf8-file.txt" with UTF-8 content "UTF-8 content with special characters: ñáéíóú"
    And I create a file "iso-file.txt" with ISO-8859-1 content "ISO-8859-1 content with special characters: ñáéíóú"
    Then I should be able to read the UTF-8 file with the correct encoding
    And I should be able to read the ISO-8859-1 file with the correct encoding

  Scenario: Copying and moving files
    When I create a file "source-file.txt" with content "Source file content"
    And I copy the file "source-file.txt" to "copy-file.txt"
    Then both files should exist with the same content
    When I move the file "copy-file.txt" to "moved-file.txt"
    Then the file "copy-file.txt" should not exist
    And the file "moved-file.txt" should exist with the correct content

  Scenario: Deleting files and directories
    When I create a directory "delete-test-dir"
    And I create a file "delete-test-dir/file1.txt" with content "File 1"
    And I create a file "delete-test-dir/file2.txt" with content "File 2"
    And I delete the file "delete-test-dir/file1.txt"
    Then the file "delete-test-dir/file1.txt" should not exist
    And the file "delete-test-dir/file2.txt" should exist
    When I recursively delete the directory "delete-test-dir"
    Then the directory "delete-test-dir" should not exist

  Scenario: Reading file information
    When I create a file "info-test.txt" with content "File info test"
    And I set the last modified time of the file
    Then I should be able to get the file information
    And the file size should match the content length
    And the last modified time should be correct
    And the file should be marked as a regular file

  Scenario: Searching for files with patterns
    When I create the following files:
      | path                  | content        |
      | search-dir/file1.txt  | Text file 1    |
      | search-dir/file2.txt  | Text file 2    |
      | search-dir/doc1.md    | Markdown doc 1 |
      | search-dir/doc2.md    | Markdown doc 2 |
      | search-dir/sub/a.txt  | Nested text    |
    Then searching for "*.txt" in "search-dir" should find 3 files
    And searching for "*.md" in "search-dir" should find 2 files
    And recursive search for "*.txt" should find all text files

  Scenario: Creating and reading temporary files
    When I create a temporary file with prefix "temp-" and suffix ".tmp"
    Then the temporary file should exist
    And I should be able to write and read from the temporary file
    And the temporary file should be in the system temp directory

  Scenario: Handling path operations
    When I get the absolute path of a relative path "relative/path"
    Then the result should be an absolute path
    When I normalize the path "../test-dir/./sub/../file.txt"
    Then the result should be the simplified path "test-dir/file.txt"
    When I resolve the path "base-dir" with "sub/file.txt"
    Then the result should be the combined path "base-dir/sub/file.txt"

  Scenario Outline: Handle file operation failures gracefully
    When I attempt to <operation> which should fail
    Then the operation should return a failure result
    And the failure result should contain an appropriate error message

    Examples:
      | operation                          |
      | read a non-existent file           |
      | write to a read-only location      |
      | create a directory in an invalid location |
      | delete a non-existent file         |
      | move a file to an invalid destination |