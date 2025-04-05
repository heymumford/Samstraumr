<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# LoggingStandards

*Last update: April 3, 2025*

## Overview

This document defines the logging standards for the Samstraumr project. Consistent logging practices are essential for effective monitoring, debugging, and maintaining system health.

## Logger Initialization

All classes should initialize their loggers using SLF4J as follows:

```java
private static final Logger LOGGER = LoggerFactory.getLogger(ClassName.class);
```

## Log Levels

Use the appropriate log level for each message based on the following guidelines:

### Error

- Use for exceptional conditions that prevent normal system operation
- Should include error details and context to assist with troubleshooting
- Usually represents an issue that requires immediate attention
- Examples: initialization failures, unrecoverable exceptions, data corruption

```java
LOGGER.error("Failed to initialize Environment", exception);
LOGGER.error("Critical system error: {}", errorDetails);
```

### Warn

- Use for potential issues that don't prevent normal operation but may indicate problems
- Should highlight situations that might need attention if they recur
- Examples: deprecated API usage, retry attempts, fallbacks to secondary mechanisms

```java
LOGGER.warn("The Bundle class is deprecated. Use Composite instead.");
LOGGER.warn("Failed to get hostname", exception);
```

### Info

- Use for significant normal events and milestones
- Provides high-level operational view of system behavior
- Should be limited to important state changes and actions
- Examples: service startup/shutdown, processing stage completion, significant operations

```java
LOGGER.info("Environment initialized successfully");
LOGGER.info("Added transformation bundle: {}", name);
```

### Debug

- Use for detailed information useful during development and troubleshooting
- Should provide context about operations without flooding logs
- Typically not enabled in production environments
- Examples: parameter values, decision points, method entry/exit

```java
LOGGER.debug("Tube initialized with ID: {}", tubeId);
LOGGER.debug("Adding to lineage: {}", reason);
```

### Trace

- Use for the most detailed diagnostic information
- Should only be enabled for specific troubleshooting
- Examples: detailed process steps, low-level function calls

```java
LOGGER.trace("Mimir Log: {}", timestampedEntry);
```

## Message Format

1. **Use parameterized logging** to avoid unnecessary string concatenation:

   ```java
   // Good
   LOGGER.info("Processing data item: {}", itemId);

   // Avoid
   LOGGER.info("Processing data item: " + itemId);
   ```
2. **Include contextual information** to make logs easier to understand and search:

   ```java
   LOGGER.info("Bundle {} connected tube {} to {}", bundleId, sourceTube, targetTube);
   ```
3. **Use proper grammar and consistent terminology**:

   ```java
   // Good
   LOGGER.info("Tube {} connected to destination {}", tubeId, destinationId);

   // Avoid
   LOGGER.info("tube {} connected destination {}", tubeId, destinationId);
   ```
4. **Log exceptions properly with stack traces**:

   ```java
   try {
     // Some code that might throw
   } catch (Exception e) {
     LOGGER.error("Failed to process item {}", itemId, e);
   }
   ```

## Structured Logging

Samstraumr uses structured logging to make logs more easily searchable and analyzable. The `TubeLogger` class provides a standardized way to include contextual information:

```java
Map<String, Object> context = new HashMap<>();
context.put("operation", "dataTransformation");
context.put("dataId", dataId);

tubeLogger.logWithContext("info", "Data transformation complete", context, "Transform", "Success");
```

## Sensitive Information

Never log sensitive information such as:
- Passwords or authentication tokens
- Personal identifiable information
- Private keys or credentials

## Console Output

Avoid using `System.out.println()` or `System.err.println()` for logging. Always use the SLF4J logger.

## Log Configuration

Logging is configured in `log4j2.xml` with the following appenders:

- **ConsoleAppender**: Outputs to the console during development
- **FileAppender**: Writes to `logs/application.log` with rotation
- **ErrorFileAppender**: Captures only ERROR level messages in `logs/error.log`

## Test Logging

In test code, use appropriate log levels:

- Use INFO for test setup and teardown
- Use DEBUG for test progress information
- Ensure test logs facilitate troubleshooting when tests fail

```java
@Before
public void setUp() {
  LOGGER.info("Setting up test for {}", testName);
}

@Test
public void testFeature() {
  LOGGER.debug("Testing with parameters: {}", testParams);
  // Test code
}

@After
public void tearDown() {
  LOGGER.info("Test cleanup complete");
}
```

## Performance Considerations

1. **Check log level before constructing expensive messages**:

   ```java
   if (LOGGER.isDebugEnabled()) {
     LOGGER.debug("Complex object state: {}", generateComplexDiagnostics());
   }
   ```
2. **Limit high-frequency logging** in performance-critical sections
