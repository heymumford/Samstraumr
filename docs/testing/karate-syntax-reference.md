# Karate Test Syntax Reference

**Author:** Eric C. Mumford (@heymumford)  
**Date:** April 10, 2025  
**Status:** Complete

## Overview

This document provides a concise reference for Karate test syntax with specific examples relevant to Samstraumr port interface testing.

## Basic Structure

Karate tests are written in `.feature` files using Gherkin syntax with special Karate extensions.

```gherkin
Feature: Feature name
  Feature description

  Background:
    # Steps that run before each scenario

  Scenario: Scenario name
    # Test steps
```

## Karate Step Prefixes

Karate steps always begin with `*` (asterisk), avoiding the traditional Given/When/Then prefixes internally:

```gherkin
* def variable = value
* def result = someOperation()
* match result == expectedValue
```

The Given/When/Then prefixes can be used for readability but are ignored by Karate:

```gherkin
Given def adapter = Java.type('org.s8r.infrastructure.SomeAdapter').createInstance()
When def result = adapter.operation()
Then assert result.isSuccessful()
```

## Common Operations

### Variable Assignment

```gherkin
* def username = 'testuser'                 # String
* def count = 5                            # Number
* def isActive = true                      # Boolean
* def user = { id: 1, name: 'Test User' }  # JSON object
* def roles = ['ADMIN', 'USER']            # Array
```

### Java Integration

```gherkin
# Import a Java class
* def UUID = Java.type('java.util.UUID')
* def randomId = UUID.randomUUID().toString()

# Create a Java object
* def cacheAdapter = Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter').createInstance()
* def taskExecutor = new Java.type('org.s8r.test.mock.MockTaskExecutionAdapter')()
```

### Reading Helper Files

```gherkin
# Read JavaScript helper
* def SecurityUtils = read('classpath:karate/helpers/security-utils.js')
* def token = SecurityUtils.generateAuthToken('admin', ['ADMIN'])

# Read JSON file
* def testData = read('classpath:test-data/users.json')
```

### Assertions

```gherkin
# Using assert
* assert variable == expectedValue
* assert result.isSuccessful()

# Using match for deep equality
* match response == { id: '#number', name: 'John' }
* match $.items[*].status contains 'ACTIVE'

# Contains
* match array contains 'value'
* match events contains deep { eventType: 'AUTH_SUCCESS' }

# Schema validation with wildcards
* match response == { id: '#number', name: '#string', active: '#boolean' }
```

### Conditionals and Loops

```gherkin
# If condition
* if (condition) karate.call('some-reusable.feature')

# For loop
* def items = [1, 2, 3]
* def result = []
* for (var i = 0; i < items.length; i++) result.push(items[i] * 2)
```

### Multi-line expressions

```gherkin
* eval
  """
  for (var i = 0; i < 3; i++) {
    var result = someFunction(i);
    if (result.valid) {
      break;
    }
  }
  """
```

## Samstraumr-Specific Patterns

### Port Adapter Initialization

```gherkin
* def cacheAdapter = Java.type('org.s8r.infrastructure.cache.EnhancedInMemoryCacheAdapter').createInstance()
* def cacheName = 'test-cache-' + Java.type('java.util.UUID').randomUUID()
* cacheAdapter.initialize(cacheName)
```

### Testing Port Operations

```gherkin
# Operation with success check
* def result = cacheAdapter.put('key1', 'value1')
* assert result.isSuccessful()

# Checking operation result content
* def getResult = cacheAdapter.get('key1')
* assert getResult.isPresent()
* assert getResult.get() == 'value1'
```

### Setting up Integration Bridges

```gherkin
* def securityAdapter = Java.type('org.s8r.test.mock.MockSecurityAdapter').createInstance()
* def eventPublisher = Java.type('org.s8r.test.mock.MockEventPublisherAdapter').createInstance()
* def securityEventBridge = Java.type('org.s8r.infrastructure.security.SecurityEventBridge').create(securityAdapter, eventPublisher)
```

### Testing Integration Scenarios

```gherkin
# Test sequence for integration
* securityAdapter.configure({})
* def authResult = securityAdapter.authenticate(username, validPassword)
* assert authResult.success
* def events = eventPublisher.getCapturedEvents()
* match events contains deep { eventType: 'AUTHENTICATION_SUCCESS', username: '#(username)' }
```

## Tagging Best Practices

Karate uses tags for test filtering and organization:

```gherkin
@L3_System @API @Cache
Feature: Cache Port Interface Tests
```

Samstraumr tagging conventions:

| Tag Category | Examples | Purpose |
|--------------|----------|---------|
| Architecture Layer | `@L1_Component`, `@L2_Integration`, `@L3_System` | Test pyramid level |
| Domain Area | `@Cache`, `@Security`, `@FileSystem` | Functional area |
| Test Type | `@Smoke`, `@Performance`, `@API` | Test characteristics |
| Status | `@WIP`, `@Ignore` | Test status |

## Build Integration

### Maven Configuration

```xml
<dependency>
    <groupId>com.intuit.karate</groupId>
    <artifactId>karate-junit5</artifactId>
    <version>${karate.version}</version>
    <scope>test</scope>
</dependency>
```

Maven execution with proper Java options:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            --add-opens java.base/java.lang=ALL-UNNAMED
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.lang.reflect=ALL-UNNAMED
            ${argLine}
        </argLine>
    </configuration>
</plugin>
```

### Shell Script Integration

Custom script for Karate test execution:

```bash
#!/bin/bash
# Set test class and environment
TEST_CLASS="org.s8r.test.karate.KarateRunner"
KARATE_ENV="dev"

# Add Java options for compatibility with Java 21
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Run tests with Maven
mvn test -Dtest=$TEST_CLASS -Dkarate.env=$KARATE_ENV -DargLine="$JAVA_OPTS" -pl test-port-interfaces
```

### Test Orchestration

In Samstraumr, use the s8r-test-karate script with specific flags:

```bash
# Run all Karate tests
./s8r-test-karate

# Run tests in a specific environment
./s8r-test-karate dev

# Run a specific test category
./s8r-test-karate dev cache
./s8r-test-karate dev filesystem
```

## Resources

- [Karate Documentation](https://github.com/karatelabs/karate)
- [Karate Assertions](https://github.com/karatelabs/karate#match)
- [Java Interop](https://github.com/karatelabs/karate#java-interop)
- [Samstraumr Karate Testing Guide](karate-testing-guide.md)