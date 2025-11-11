# Karate Common Utilities

This directory contains reusable JavaScript utilities for Karate tests across the Samstraumr port interfaces. These utilities standardize common operations, reduce code duplication, and make tests more maintainable.

## Available Utilities

### adapter-initializer.js

Provides standardized factory functions for creating port adapter instances:

```javascript
// Import the utilities
* def adapterInit = read('../common/adapter-initializer.js')

// Create adapters
* def cacheAdapter = adapterInit.createCacheAdapter()
* def configAdapter = adapterInit.createConfigAdapter()
* def validationAdapter = adapterInit.createValidationAdapter()
* def fileSystemAdapter = adapterInit.createFileSystemAdapter()
* def taskExecutionAdapter = adapterInit.createTaskExecutionAdapter(4) // with thread pool size
* def secureTaskAdapter = adapterInit.createSecureTaskAdapter()
* def notificationAdapter = adapterInit.createNotificationAdapter()
* def eventPublisherAdapter = adapterInit.createEventPublisherAdapter()
```

### result-validator.js

Provides utilities for validating and working with operation results:

```javascript
// Import the utilities
* def validators = read('../common/result-validator.js')

// Process validation results
* def validationResult = validationAdapter.validateEntity('user', userData)
* def check = validators.processValidationResult(validationResult)
* assert check.valid == true
* match check.errors == []

// Check operation results
* def result = fileSystemAdapter.writeFile(path, content)
* assert validators.isSuccessful(result)
* def errorDetails = validators.getErrorDetails(result)
* assert errorDetails.message == null

// Extract attributes from results
* def content = validators.getAttribute(result, 'content')
* def allAttributes = validators.getAllAttributes(result)

// Work with Java Optional values
* def optional = configAdapter.getString('app.name')
* assert validators.optionalHasValue(optional)
* def value = validators.optionalValue(optional, 'default')
```

### performance-testing.js

Provides utilities for measuring and reporting performance metrics:

```javascript
// Import the utilities
* def perfTest = read('../common/performance-testing.js')

// Define an operation to test
* def operation = function() { cacheAdapter.get('test-key') }

// Measure average execution time
* def avgTime = perfTest.measureAverageTime(1000, operation)
* assert avgTime < 1.0 // Less than 1ms per operation

// Single execution measurement
* def singleTime = perfTest.measureSingleExecution(operation)

// Throughput test (operations per second)
* def throughputResults = perfTest.runThroughputTest(operation, 2) // 2 seconds
* assert throughputResults.operationsPerSecond > 5000

// Generate a performance report
* def report = perfTest.createPerformanceReport(throughputResults, 'Cache Get Operation')
* print report

// Concurrent operations test
* def concurrentResults = perfTest.runConcurrentTest(operation, 10, 100) // 10 concurrent ops, 100 iterations
* assert concurrentResults.operationsPerSecond > 1000
```

### test-data.js

Provides utilities for generating test data:

```javascript
// Import the utilities
* def testData = read('../common/test-data.js')

// Generate identifiers
* def uuid = testData.randomUuid()
* def shortId = testData.shortUuid()
* def randomStr = testData.randomString(15)
* def randomNumber = testData.randomInt(1, 100)

// Generate file paths
* def paths = testData.generateFilePaths('/tmp')
* def textFilePath = paths.textFile
* def directoryPath = paths.directory

// Generate test entities
* def component = testData.generateComponentData('comp-123')
* def user = testData.generateUserData()
* def config = testData.generateConfigData()
* def event = testData.generateEventData('CACHE_CLEARED')
```

## Usage Example

See the file `karate/examples/reusable-patterns-example.feature` for a complete example of using these utilities together.

## Best Practices

1. Always import utility files in the Background section of your Karate feature
2. Use adapter initializers instead of directly importing Java classes
3. Use the result validators to standardize success/error checking
4. Use performance testing utilities for any performance-sensitive operations
5. Use test data generators for creating random test data with proper structures

## Adding New Utilities

When adding new utilities:

1. Follow the functional programming style
2. Document each function with JSDoc comments
3. Add usage examples to this README
4. Create or update example tests to demonstrate usage