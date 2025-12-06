# Migrating Cucumber Tests to Karate Framework

**Author:** Eric C. Mumford (@heymumford)  
**Date:** April 10, 2025  
**Status:** Active  

## Overview

This document provides guidelines and best practices for migrating existing Cucumber BDD tests to the Karate testing framework in the Samstraumr project. The migration focuses initially on L3_System level tests, which benefit most from Karate's simplified approach to API testing.

## Why Migrate from Cucumber to Karate?

The migration offers several advantages:

1. **No Step Definitions Required**: Karate eliminates the need for separate Java step definitions, reducing maintenance overhead.
2. **Simplified API Testing**: Built-in support for API testing with native JSON and XML handling.
3. **More Concise Tests**: Tests are more readable and concise with embedded JavaScript.
4. **Better Performance**: Parallel execution and faster test runs.
5. **Enhanced Debugging**: Better visualization of test failures.

## Migration Strategy

The migration follows a phased approach:

### Phase 1: Initial Setup and Conversion Templates (COMPLETED)
- ✅ Set up Karate dependencies and configuration
- ✅ Create reusable JavaScript utilities
- ✅ Establish patterns for migrating common test scenarios
- ✅ Create initial examples of migrated tests

### Phase 2: L3_System Test Migration (CURRENT)
- ⏳ Migrate integration tests (external system interactions)
- ⏳ Migrate end-to-end tests (complex system workflows)
- ⏳ Migrate performance and scalability tests

### Phase 3: Broader Migration
- 📋 Selectively migrate other test levels as appropriate
- 📋 Refine integration with existing test infrastructure

## Migration Process

Follow these steps when migrating a Cucumber feature file to Karate:

### 1. Analyze the Cucumber Feature

Examine the original feature file and its step definitions to understand:
- The test's intent and scenarios
- The required setup (given steps)
- The actions being performed (when steps)
- The assertions being made (then steps)

### 2. Create Karate Feature Structure

Create a new Karate feature file with the same name in the appropriate directory:
- `/test-port-interfaces/src/test/java/karate/L3_System/` for system-level tests

Start with the basic structure:

```gherkin
Feature: [Same title as original]
  As a [same role]
  I want to [same capability]
  So that [same benefit]

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Initialize test environment and common components
    # [Environment setup specific to the feature]
```

### 3. Convert Scenarios

For each scenario in the original feature:

1. Copy the scenario name and description
2. Analyze the step definitions to understand the Java implementation
3. Rewrite as Karate steps, using:
   - Direct Java calls using `Java.type()`
   - JavaScript functions for complex logic
   - The reusable utility functions
   - Karate's assertion syntax

#### Example: Original Cucumber Scenario

```gherkin
@Performance
Scenario: System should scale to handle varying load
  Given a system configured for dynamic scaling
  And current resource utilization is monitored
  When the input load increases by 300%
  Then the system should allocate additional resources
  And processing throughput should increase proportionally
  And latency should remain within acceptable parameters
  And resource utilization should stabilize at an efficient level
  And when the load decreases, resources should be released
```

#### Example: Migrated Karate Scenario

```gherkin
@Performance
Scenario: System should scale to handle varying load
  # Set up a scalable system
  * def ScalableSystemBuilder = Java.type('org.s8r.system.ScalableSystemBuilder')
  * def scalableSystem = ScalableSystemBuilder.create(env)
      .withMinWorkers(1)
      .withMaxWorkers(5)
      .withScalingThreshold(0.7) // Scale up at 70% capacity
      .withScaleDownThreshold(0.3) // Scale down at 30% capacity
      .build()
  
  # Start the system
  * scalableSystem.start()
  * def initialWorkerCount = scalableSystem.getWorkerCount()
  * assert initialWorkerCount == 1
  
  # Generate a high load
  * def highLoadMessages = []
  * for (var i = 0; i < 1000; i++) highLoadMessages.push({ id: i, size: 'large', processingTimeMs: 50 })
  
  # Submit high load in parallel threads
  * def submitThreads = Java.type('org.s8r.test.utils.ConcurrentTestUtils').createSubmitThreads(scalableSystem, highLoadMessages, 10)
  * submitThreads.startAll()
  
  # Wait for scaling to occur
  * Java.type('java.lang.Thread').sleep(2000) // Give time for scaling
  
  # Verify the system scaled up
  * def scaledUpWorkerCount = scalableSystem.getWorkerCount()
  * assert scaledUpWorkerCount > initialWorkerCount
  
  # Additional verifications...
```

### 4. Use Reusable Patterns

Leverage the reusable patterns and utilities:

1. **adapter-initializer.js**: For creating adapter instances
   ```gherkin
   * def fileSystemAdapter = adapterInit.createFileSystemAdapter()
   ```

2. **result-validator.js**: For validating operation results
   ```gherkin
   * assert validators.isSuccessful(result)
   * def errorDetails = validators.getErrorDetails(result)
   ```

3. **performance-testing.js**: For measuring performance
   ```gherkin
   * def perfResults = perfTest.measureAverageTime(10, operation)
   ```

4. **test-data.js**: For generating test data
   ```gherkin
   * def testId = testData.shortUuid()
   * def userData = testData.generateUserData()
   ```

### 5. Test and Refine

After migrating:
1. Update the test runner to include the new Karate tests
2. Run the tests to identify any issues
3. Refine the implementation as needed
4. Document any special considerations in comments

## Making Use of JavaScript in Karate

Karate allows embedding JavaScript directly in the feature file, which can simplify many test operations:

### Data Manipulation
```gherkin
* def users = []
* for (var i = 0; i < 10; i++) users.push({ id: i, name: 'User ' + i })
```

### Custom Functions
```gherkin
* def isValidFormat = function(str) { return str.match(/^[A-Z]{2}-\d{4}$/) !== null }
* assert isValidFormat('AB-1234')
```

### Complex Assertions
```gherkin
* def integrityCounts = { intact: 0, corrupt: 0 }
* for (var i = 0; i < records.length; i++) {
    if (verifyIntegrity(records[i])) {
      integrityCounts.intact++;
    } else {
      integrityCounts.corrupt++;
    }
  }
* assert integrityCounts.intact == 100
* assert integrityCounts.corrupt == 0
```

## Common Conversion Patterns

### 1. Background Setup

**Cucumber:**
```gherkin
Background:
  Given a clean system environment
  And all system dependencies are available
```

**Karate:**
```gherkin
Background:
  # Import utilities
  * def adapterInit = read('../common/adapter-initializer.js')
  * def validators = read('../common/result-validator.js')
  * def testData = read('../common/test-data.js')
  
  # Setup environment
  * def envBuilder = Java.type('org.s8r.core.env.Environment').Builder
  * def env = new envBuilder('system-test-env-' + testData.shortUuid())
      .withParameter('testMode', 'true')
      .withParameter('logLevel', 'INFO')
      .build()
```

### 2. Complex Given Statements

**Cucumber:**
```gherkin
Given a data ingestion machine that reads from external sources
And a data transformation machine that normalizes data
And a data enrichment machine that adds contextual information
And a data storage machine that persists results
```

**Karate:**
```gherkin
# Set up pipeline components
* def ComponentFactory = Java.type('org.s8r.component.ComponentFactory')
* def ingestionMachine = ComponentFactory.createMachine(env, 'DataIngestionMachine')
* def transformationMachine = ComponentFactory.createMachine(env, 'DataTransformationMachine')
* def enrichmentMachine = ComponentFactory.createMachine(env, 'DataEnrichmentMachine')
* def storageMachine = ComponentFactory.createMachine(env, 'DataStorageMachine')

# Connect the pipeline
* def pipelineBuilder = Java.type('org.s8r.core.pipeline.PipelineBuilder').create(env)
* def pipeline = pipelineBuilder
    .addMachine(ingestionMachine)
    .addMachine(transformationMachine)
    .addMachine(enrichmentMachine)
    .addMachine(storageMachine)
    .connect(ingestionMachine, 'output', transformationMachine, 'input')
    .connect(transformationMachine, 'output', enrichmentMachine, 'input')
    .connect(enrichmentMachine, 'output', storageMachine, 'input')
    .build()
```

### 3. When Statements

**Cucumber:**
```gherkin
When 100 records are processed through the complete pipeline
```

**Karate:**
```gherkin
# Create test data records
* def testRecords = []
* for (var i = 0; i < 100; i++) testRecords.push({ id: i, data: 'Record-' + i, timestamp: new Date().getTime() })

# Process records through pipeline
* pipeline.start()
* def ingestionInput = ingestionMachine.getPort('input')
* def processingResult = ingestionInput.submitBatch(testRecords)
* assert validators.isSuccessful(processingResult)
```

### 4. Then Statements and Assertions

**Cucumber:**
```gherkin
Then all records should be successfully processed
And the data should maintain integrity throughout the pipeline
And the system should track the full data lineage
And the processing time should be within acceptable limits
```

**Karate:**
```gherkin
# Verify all records were processed correctly
* def storedRecords = storageMachine.getAllStoredRecords()
* assert storedRecords.length == 100

# Verify data integrity
* def verifyIntegrity = function(record) { 
    return record.id != null && 
           record.data != null &&
           record.data.startsWith('Record-') &&
           record.timestamp != null && 
           record.enriched === true; 
  }
* def integrityCounts = { intact: 0, corrupt: 0 }
* for (var i = 0; i < storedRecords.length; i++) {
    if (verifyIntegrity(storedRecords[i])) {
      integrityCounts.intact++;
    } else {
      integrityCounts.corrupt++;
    }
  }
* assert integrityCounts.intact == 100
* assert integrityCounts.corrupt == 0

# Verify data lineage tracking
* def lineageResult = pipeline.getLineageInfo(storedRecords[0].id)
* assert validators.isSuccessful(lineageResult)
* def lineage = validators.getAttribute(lineageResult, 'lineage')
* assert lineage.path.length >= 4 // Should have passed through all machines

# Verify performance
* def processingTime = validators.getAttribute(completionResult, 'processingTimeMs')
* assert processingTime < 5000 // Less than 5 seconds for 100 records
```

## Handling Special Cases

### 1. Cucumber Step Definition with Complex Logic

When the original step definition contains complex Java code:

1. Analyze the code to understand its purpose
2. Create a JavaScript function that captures the same logic
3. Use Java interoperability when needed for specific Java functionality

### 2. Cucumber Hooks (Before, After)

Karate does not have explicit hooks, but you can:

- Use the Background section for setup common to all scenarios
- Place cleanup logic at the end of each scenario if needed
- For complex setup/teardown, create utility Java classes that can be called from Karate

### 3. Dependency Injection in Cucumber

Karate does not use dependency injection like Cucumber. Instead:

- Create instances directly using `Java.type()`
- Use factory methods in your Java classes
- Pass dependencies explicitly in your Karate tests

## Adding Tests to Test Runners

Add new test methods to the `KarateL3SystemRunner` class for easy execution:

```java
@Karate.Test
Karate systemIntegrationTests() {
    return Karate.run("classpath:karate/L3_System/system-integration-test.feature").relativeTo(getClass());
}

@Karate.Test
Karate systemEndToEndTests() {
    return Karate.run("classpath:karate/L3_System/system-end-to-end-test.feature").relativeTo(getClass());
}
```

## Conclusion

By following this migration guide, you can systematically convert Cucumber features to Karate, taking advantage of Karate's more concise syntax and powerful JavaScript integration. The migration process requires careful analysis of the original tests but results in more maintainable, efficient test code.# Karate Test Syntax Reference

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
- [Samstraumr Karate Testing Guide](karate-testing-guide.md)# Reusable Karate Test Patterns for Samstraumr Port Interfaces

This document provides a comprehensive guide to the reusable Karate test patterns developed for the Samstraumr port interfaces. These patterns standardize the way port interfaces are tested, making tests more consistent, maintainable, and efficient.

## Introduction

The Karate testing framework provides a flexible way to test port interfaces with a combination of BDD-style Gherkin syntax and JavaScript capabilities. Our reusable patterns build on this foundation to create a standardized approach to testing across all port interfaces.

## Pattern Libraries

### 1. Adapter Initialization

Located in `test-port-interfaces/src/test/java/karate/common/adapter-initializer.js`, this library provides standardized factory functions for creating port adapter instances:

```javascript
// Example usage in a Karate feature file
* def adapterInit = read('../common/adapter-initializer.js')
* def cacheAdapter = adapterInit.createCacheAdapter()
* def validationAdapter = adapterInit.createValidationAdapter()
* def configAdapter = adapterInit.createConfigAdapterWithProps(properties)
```

Benefits:
- Centralizes adapter creation logic in one place
- Makes tests more resilient to changes in adapter creation APIs
- Simplifies test setup with consistent initialization patterns

### 2. Result Validation

Located in `test-port-interfaces/src/test/java/karate/common/result-validator.js`, this library provides utilities for validating and working with operation results:

```javascript
// Example usage in a Karate feature file
* def validators = read('../common/result-validator.js')
* def result = fileSystemAdapter.writeFile(path, content)
* assert validators.isSuccessful(result)
* def content = validators.getAttribute(result, 'content')
```

Benefits:
- Standardizes result validation across all tests
- Handles Java-to-JavaScript conversion for complex result objects
- Provides consistent error handling and attribute extraction

### 3. Performance Testing

Located in `test-port-interfaces/src/test/java/karate/common/performance-testing.js`, this library provides utilities for measuring and reporting performance metrics:

```javascript
// Example usage in a Karate feature file
* def perfTest = read('../common/performance-testing.js')
* def operation = function() { cacheAdapter.get('test-key') }
* def avgTime = perfTest.measureAverageTime(1000, operation)
* assert avgTime < 1.0 // Less than 1ms per operation
```

Benefits:
- Provides consistent performance measurement across all tests
- Supports various testing patterns (single execution, average time, throughput)
- Generates standardized performance reports

### 4. Test Data Generation

Located in `test-port-interfaces/src/test/java/karate/common/test-data.js`, this library provides utilities for generating test data:

```javascript
// Example usage in a Karate feature file
* def testData = read('../common/test-data.js')
* def user = testData.generateUserData()
* def config = testData.generateConfigData()
* def paths = testData.generateFilePaths('/tmp')
```

Benefits:
- Generates consistent test data across all tests
- Provides realistic data with appropriate structure
- Supports both random and deterministic data generation

## Usage Patterns

### Basic Port Interface Test Pattern

The standard pattern for testing a port interface using the reusable utilities is:

1. Import utility libraries in the Background section
2. Create adapter instances using the adapter initializer
3. Generate necessary test data
4. Execute operations on the port interface
5. Validate results using the result validators
6. If performance-sensitive, add performance tests

Example:

```gherkin
Feature: Cache Port Interface Tests

  Background:
    # Import utility libraries
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    
    # Initialize adapters
    * def cacheAdapter = adapterInit.createCacheAdapter()
    
    # Generate test data
    * def cacheName = 'test-cache-' + testData.shortUuid()
    * def testObject = testData.generateUserData()

  Scenario: Store and retrieve data from cache
    # Initialize cache
    When cacheAdapter.initialize(cacheName)
    
    # Store data
    Then cacheAdapter.put('user-123', testObject)
    
    # Validate retrieval
    * def result = cacheAdapter.get('user-123')
    * assert validators.optionalHasValue(result)
    * def retrievedObject = validators.optionalValue(result)
    * assert retrievedObject.id == testObject.id
```

### Integration Test Pattern

For testing the integration between two or more port interfaces:

1. Import utility libraries
2. Create adapter instances for all relevant ports
3. Create bridge/integration class if needed
4. Execute integration operations
5. Validate results across multiple adapters

Example:

```gherkin
Feature: Cache-FileSystem Integration Tests

  Background:
    # Import utility libraries
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    
    # Initialize adapters
    * def cacheAdapter = adapterInit.createCacheAdapter()
    * def fileSystemAdapter = adapterInit.createFileSystemAdapter()
    * def bridge = Java.type('org.s8r.infrastructure.integration.CacheFileSystemBridge').createInstance(cacheAdapter, fileSystemAdapter)
    
    # Generate test data
    * def tempDir = fileSystemAdapter.createTemporaryDirectory('integration-test-' + testData.shortUuid())
    * def testFilePath = tempDir + '/cached-data.json'
    * def cacheKey = 'file-' + testData.shortUuid()
    * def testData = testData.generateUserData()

  Scenario: Cache data and persist to filesystem
    # Store in cache
    * bridge.cacheAndPersist(cacheKey, testData, testFilePath)
    
    # Verify in cache
    * def cacheResult = cacheAdapter.get(cacheKey)
    * assert validators.optionalHasValue(cacheResult)
    
    # Verify in filesystem
    * def fileResult = fileSystemAdapter.readFile(testFilePath)
    * assert validators.isSuccessful(fileResult)
```

### Performance Test Pattern

For performance testing a port interface:

1. Import utility libraries including performance testing
2. Define operations to test
3. Measure performance metrics
4. Assert against performance requirements

Example:

```gherkin
Feature: Cache Performance Tests

  Background:
    # Import utility libraries
    * def adapterInit = read('../common/adapter-initializer.js')
    * def perfTest = read('../common/performance-testing.js')
    * def testData = read('../common/test-data.js')
    
    # Initialize adapters
    * def cacheAdapter = adapterInit.createCacheAdapter()
    * def cacheName = 'perf-cache-' + testData.shortUuid()
    * cacheAdapter.initialize(cacheName)
    
    # Prepare test data
    * def testKey = 'test-key'
    * def testValue = testData.generateUserData()
    * cacheAdapter.put(testKey, testValue)

  Scenario: Cache Get Performance
    # Define operation to test
    * def getOperation = function() { cacheAdapter.get(testKey) }
    
    # Measure average time
    * def avgTime = perfTest.measureAverageTime(1000, getOperation)
    * print 'Average get time:', avgTime, 'ms'
    * assert avgTime < 0.5 // Less than 0.5ms
    
    # Measure throughput
    * def throughput = perfTest.runThroughputTest(getOperation, 2)
    * print 'Operations per second:', throughput.operationsPerSecond
    * assert throughput.operationsPerSecond > 10000 // More than 10K ops/sec
```

## Best Practices

1. **Always import utility files in the Background section** to ensure they're available for all scenarios.

2. **Use adapter initializers instead of directly importing Java classes** to maintain adaptability to implementation changes.

3. **Standardize result validation** with validator utilities to ensure consistent error handling.

4. **Generate test data** using the test data utilities to ensure consistent and comprehensive test coverage.

5. **Include performance tests** for performance-sensitive operations to catch regressions early.

6. **Use descriptive scenario names** that clearly explain what's being tested.

7. **Follow the given-when-then pattern** to maintain readability:
   - Given: Set up test data and initialize adapters
   - When: Execute operations
   - Then: Validate results

8. **Keep tests focused** on specific aspects of functionality.

9. **Avoid test dependencies** between scenarios to ensure tests can run independently.

10. **Document complex operations** with comments to explain intent.

## Implementation Examples

See the example feature file at `test-port-interfaces/src/test/java/karate/examples/reusable-patterns-example.feature` for a complete example of using all the reusable patterns together.

## Next Steps

1. **Refactor existing Karate tests** to use the reusable patterns
2. **Create additional test data generators** for specialized domains
3. **Add advanced performance test patterns** for concurrent and load testing
4. **Develop additional result validator utilities** for specialized port interfaces
5. **Standardize error scenario testing** across all port interfaces# Karate Testing Guide

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