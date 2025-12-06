# Karate Testing Guide

**Author:** Eric C. Mumford (@heymumford)  
**Date:** April 10, 2025  
**Status:** Draft

## Overview

This guide provides comprehensive information on using the Karate testing framework with Samstraumr. The project has adopted Karate for system-level (L3) testing, particularly for port interfaces and their integration.

## What is Karate?

Karate is a testing framework that combines API test automation, mocks, performance testing, and UI automation into a single, unified tool. It allows for writing tests in a readable, BDD-style syntax without requiring step definitions or separate testing code.

Key features of Karate include:

1. **No Step Definitions Required**: Tests are written entirely in Gherkin syntax with embedded JavaScript.
2. **JavaScript Integration**: JavaScript can be used directly in tests for complex assertions and data manipulation.
3. **JSON/XML Support**: Built-in support for parsing and manipulating JSON and XML.
4. **Parallel Execution**: Automatic parallel test execution for faster test runs.
5. **HTML Reports**: Comprehensive HTML reports with detailed test results.

## Setting Up Karate Tests

Karate tests in Samstraumr are located in the `test-port-interfaces/src/test/java/karate` directory. The tests are organized as follows:

```
test-port-interfaces/src/test/java/karate/
├── helpers/                        # Helper JavaScript files
│   ├── component-utils.js          # Component-related utilities
│   └── security-utils.js           # Security-related utilities
├── port_interfaces/                # Port interface tests
│   ├── cache-port-test.feature     # Cache port tests
│   ├── filesystem-port-test.feature # FileSystem port tests
│   ├── security-event-integration.feature # Security-Event integration tests
│   └── task-notification-integration.feature # Task-Notification integration tests
└── karate-config.js                # Karate configuration file
```

### Karate Configuration

The `karate-config.js` file is the global configuration file for Karate. It's executed before each test and provides environment-specific configuration:

```javascript
function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev'; // default environment
  }
  
  var config = {
    s8rVersion: '3.1.1',
    baseUrl: 'http://localhost:8080',
    timeoutMs: 5000,
    
    // Helper utility
    generateId: function() {
      var uuid = java.util.UUID.randomUUID();
      return uuid.toString().substring(0, 8);
    }
  };
  
  if (env === 'dev') {
    config.mockMode = true;
    config.logLevel = 'debug';
  } else if (env === 'test') {
    config.mockMode = false;
    config.baseUrl = 'http://test-server:8080';
    config.logLevel = 'info';
  }
  
  return config;
}
```

## Running Karate Tests

The project includes a dedicated script for running Karate tests:

```bash
# Run all Karate tests
./s8r-test-karate

# Run tests in a specific environment
./s8r-test-karate dev

# Run a specific test category
./s8r-test-karate dev security
./s8r-test-karate dev task
./s8r-test-karate dev cache
./s8r-test-karate dev filesystem

# Run all port interface tests
./s8r-test-karate dev port
```

## Writing Karate Tests

Karate tests are written in Gherkin syntax with embedded JavaScript. Here's the structure of a Karate test file:

```gherkin
Feature: Feature Description
  User story or feature description

  Background:
    * def myVar = 'value'
    * def myHelper = read('classpath:karate/helpers/my-helper.js')
    * def someAdapter = Java.type('org.s8r.infrastructure.SomeAdapter').createInstance()

  Scenario: Scenario description
    Given some condition
    When some action
    Then some assertion
```

### Example: Cache Port Test

```gherkin
Feature: Cache Port Interface Tests
  As an application developer
  I want to use a standardized Cache Port interface
  So that I can implement and switch different cache providers

  Background:
    * def cacheAdapter = Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter').createInstance()
    * def cacheName = 'test-cache-' + Java.type('java.util.UUID').randomUUID()
    * def testObject = { id: 123, name: 'Test User', roles: ['USER', 'ADMIN'], active: true }

  Scenario: Initialize a cache instance
    When cacheAdapter.initialize(cacheName)
    Then assert cacheAdapter.getCacheName() == cacheName
    And assert cacheAdapter.size() == 0

  Scenario: Store and retrieve data from cache
    Given cacheAdapter.initialize(cacheName)
    When cacheAdapter.put('user-123', 'John Doe')
    Then assert cacheAdapter.containsKey('user-123')
    And def result = cacheAdapter.get('user-123')
    And assert result.isPresent()
    And assert result.get() == 'John Doe'
```

### Example: FileSystem Port Test

```gherkin
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
```

## Working with Java Objects

Karate provides the `Java.type()` function to access Java classes. This can be used to instantiate adapters and utility classes:

```gherkin
* def CacheAdapter = Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter')
* def cacheAdapter = new CacheAdapter()
```

For static methods, you can call them directly:

```gherkin
* def UUID = Java.type('java.util.UUID')
* def randomId = UUID.randomUUID().toString()
```

## Helper JavaScript Files

Helper JavaScript files provide reusable functions for tests. They are stored in the `karate/helpers` directory:

### component-utils.js

```javascript
function generateComponentId() {
  var uuid = Java.type('java.util.UUID').randomUUID();
  return 'COMP-' + uuid.toString().substring(0, 8);
}

function createTestComponent(componentId, name, type) {
  var id = componentId || generateComponentId();
  var componentName = name || 'Test Component';
  var componentType = type || 'STANDARD';
  
  var Component = Java.type('org.s8r.component.Component');
  return Component.createForTest(id, componentName, componentType);
}

module.exports = {
  generateComponentId: generateComponentId,
  createTestComponent: createTestComponent
};
```

### security-utils.js

```javascript
function generateAuthToken(username, roles) {
  var base64 = Java.type('java.util.Base64');
  var encoder = base64.getEncoder();
  
  var currentTime = new Date().getTime();
  var expiryTime = currentTime + (30 * 60 * 1000); // 30 minutes
  
  var tokenData = {
    sub: username,
    roles: roles || ['USER'],
    iat: Math.floor(currentTime / 1000),
    exp: Math.floor(expiryTime / 1000)
  };
  
  var tokenString = JSON.stringify(tokenData);
  var encodedToken = encoder.encodeToString(tokenString.getBytes('UTF-8'));
  
  return 'Bearer ' + encodedToken;
}

module.exports = {
  generateAuthToken: generateAuthToken
};
```

## Best Practices

1. **Use Descriptive Feature and Scenario Names**: Ensure that feature and scenario names clearly explain what is being tested.

2. **Create Background Sections**: Use background sections to set up common prerequisites for all scenarios in a feature.

3. **Organize Tests Logically**: Group related tests together in the same feature file.

4. **Use Helper Functions**: Create helper JavaScript files for complex or reusable logic.

5. **Validate Results**: Always validate the results of operations, including checking for success/failure status and examining the content of returned values.

6. **Test Edge Cases**: Include tests for error conditions and edge cases, not just the happy path.

7. **Use Dynamic Data**: Generate dynamic test data to avoid test dependencies and make tests repeatable.

8. **Keep Tests Independent**: Each test should be independent and not rely on the state from previous tests.

## Integration with JUnit 5

Karate tests are run through JUnit 5 using the `KarateRunner` class. This class provides methods to run specific tests or all tests together:

```java
@Tag("L3_System")
@Tag("API")
@Tag("Karate")
public class KarateRunner {

    @Karate.Test
    Karate allTests() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }

    @Karate.Test
    Karate portInterfaceTests() {
        return Karate.run("classpath:karate/port_interfaces").relativeTo(getClass());
    }

    @Karate.Test
    Karate securityEventTests() {
        return Karate.run("classpath:karate/port_interfaces/security-event-integration.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate taskNotificationTests() {
        return Karate.run("classpath:karate/port_interfaces/task-notification-integration.feature").relativeTo(getClass());
    }
    
    @Karate.Test
    Karate cachePortTests() {
        return Karate.run("classpath:karate/port_interfaces/cache-port-test.feature").relativeTo(getClass());
    }
    
    @Karate.Test
    Karate filesystemPortTests() {
        return Karate.run("classpath:karate/port_interfaces/filesystem-port-test.feature").relativeTo(getClass());
    }
}
```

## Karate Reports

Karate generates HTML reports after test execution. The reports are located in the `test-port-interfaces/target/karate-reports` directory and include:

1. **Summary Report**: `karate-summary.html` provides an overview of all tests.
2. **Feature Reports**: Individual reports for each feature file.
3. **Timeline View**: A timeline of test execution.

The `s8r-test-karate` script will automatically open the report in a browser if available.

## References

- [Karate Documentation](https://github.com/karatelabs/karate)
- [Karate Features](https://github.com/karatelabs/karate#features)
- [Karate JavaScript API](https://github.com/karatelabs/karate#karate-expressions)
- [Karate Java Integration](https://github.com/karatelabs/karate#java-interop)
- [JUnit 5 Integration](https://github.com/karatelabs/karate#junit-5)