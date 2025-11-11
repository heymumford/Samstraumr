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

By following this migration guide, you can systematically convert Cucumber features to Karate, taking advantage of Karate's more concise syntax and powerful JavaScript integration. The migration process requires careful analysis of the original tests but results in more maintainable, efficient test code.