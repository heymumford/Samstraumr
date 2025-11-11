# Reusable Karate Test Patterns for Samstraumr Port Interfaces

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
5. **Standardize error scenario testing** across all port interfaces