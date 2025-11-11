Feature: FileSystem Port Interface Tests
  As an application developer
  I want to use a standardized FileSystem Port interface
  So that I can perform file operations in a consistent way

  Background:
    * def fileSystemAdapter = Java.type('org.s8r.infrastructure.filesystem.BufferedFileSystemAdapter').createInstance()
    * def testFolder = 'karate-test-' + Java.type('java.util.UUID').randomUUID().toString().substring(0, 8)
    * def testPath = fileSystemAdapter.createTemporaryDirectory(testFolder)
    * def FileUtils = Java.type('org.s8r.test.utils.FileSystemTestUtils')

  Scenario: Read from and write to a file
    Given def testFile = FileUtils.combinePaths(testPath, 'test-file.txt')
    When def writeResult = fileSystemAdapter.writeFile(testFile, 'Hello, World!')
    Then assert writeResult.isSuccessful()
    And def exists = fileSystemAdapter.fileExists(testFile)
    And assert exists
    And def readResult = fileSystemAdapter.readFile(testFile)
    And assert readResult.isSuccessful()
    And assert readResult.getAttributes().get('content') == 'Hello, World!'

  Scenario: Check if a file exists
    Given def existingFile = FileUtils.combinePaths(testPath, 'existing-file.txt')
    And fileSystemAdapter.writeFile(existingFile, 'This file exists')
    And def nonExistingFile = FileUtils.combinePaths(testPath, 'non-existing-file.txt')
    When def existingResult = fileSystemAdapter.fileExists(existingFile)
    Then assert existingResult
    When def nonExistingResult = fileSystemAdapter.fileExists(nonExistingFile)
    Then assert !nonExistingResult

  Scenario: Create a directory
    Given def testDirectory = FileUtils.combinePaths(testPath, 'test-directory')
    When def createResult = fileSystemAdapter.createDirectory(testDirectory)
    Then assert createResult.isSuccessful()
    And def exists = fileSystemAdapter.directoryExists(testDirectory)
    And assert exists
    And def nestedFile = FileUtils.combinePaths(testDirectory, 'nested-file.txt')
    And def writeResult = fileSystemAdapter.writeFile(nestedFile, 'Nested file content')
    And assert writeResult.isSuccessful()

  Scenario: List files in a directory
    Given def listDir = FileUtils.combinePaths(testPath, 'list-test')
    And fileSystemAdapter.createDirectory(listDir)
    And fileSystemAdapter.writeFile(FileUtils.combinePaths(listDir, 'file1.txt'), 'Content 1')
    And fileSystemAdapter.writeFile(FileUtils.combinePaths(listDir, 'file2.txt'), 'Content 2')
    And fileSystemAdapter.writeFile(FileUtils.combinePaths(listDir, 'file3.txt'), 'Content 3')
    When def listResult = fileSystemAdapter.listFiles(listDir)
    Then assert listResult.isSuccessful()
    And def files = listResult.getAttributes().get('files')
    And assert files.length == 3
    And match files contains 'file1.txt'
    And match files contains 'file2.txt'
    And match files contains 'file3.txt'

  Scenario: Delete a file
    Given def fileToDelete = FileUtils.combinePaths(testPath, 'to-delete.txt')
    And fileSystemAdapter.writeFile(fileToDelete, 'Delete me')
    When def deleteResult = fileSystemAdapter.deleteFile(fileToDelete)
    Then assert deleteResult.isSuccessful()
    And def exists = fileSystemAdapter.fileExists(fileToDelete)
    And assert !exists

  Scenario: Delete a directory with contents
    Given def dirToDelete = FileUtils.combinePaths(testPath, 'dir-to-delete')
    And fileSystemAdapter.createDirectory(dirToDelete)
    And fileSystemAdapter.writeFile(FileUtils.combinePaths(dirToDelete, 'nested1.txt'), 'Content 1')
    And fileSystemAdapter.writeFile(FileUtils.combinePaths(dirToDelete, 'nested2.txt'), 'Content 2')
    When def deleteResult = fileSystemAdapter.deleteDirectory(dirToDelete)
    Then assert deleteResult.isSuccessful()
    And def exists = fileSystemAdapter.directoryExists(dirToDelete)
    And assert !exists

  Scenario: Copy a file
    Given def sourceFile = FileUtils.combinePaths(testPath, 'source-file.txt')
    And fileSystemAdapter.writeFile(sourceFile, 'Source file content')
    And def destFile = FileUtils.combinePaths(testPath, 'dest-file.txt')
    When def copyResult = fileSystemAdapter.copyFile(sourceFile, destFile)
    Then assert copyResult.isSuccessful()
    And def sourceExists = fileSystemAdapter.fileExists(sourceFile)
    And assert sourceExists
    And def destExists = fileSystemAdapter.fileExists(destFile)
    And assert destExists
    And def readResult = fileSystemAdapter.readFile(destFile)
    And assert readResult.getAttributes().get('content') == 'Source file content'

  Scenario: Move a file
    Given def fileToMove = FileUtils.combinePaths(testPath, 'move-source.txt')
    And fileSystemAdapter.writeFile(fileToMove, 'Move me')
    And def moveDestination = FileUtils.combinePaths(testPath, 'move-dest.txt')
    When def moveResult = fileSystemAdapter.moveFile(fileToMove, moveDestination)
    Then assert moveResult.isSuccessful()
    And def sourceExists = fileSystemAdapter.fileExists(fileToMove)
    And assert !sourceExists
    And def destExists = fileSystemAdapter.fileExists(moveDestination)
    And assert destExists
    And def readResult = fileSystemAdapter.readFile(moveDestination)
    And assert readResult.getAttributes().get('content') == 'Move me'