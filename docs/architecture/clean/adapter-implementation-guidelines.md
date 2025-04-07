# Adapter Implementation Guidelines

This document provides comprehensive guidelines for implementing new adapters in the Samstraumr project according to Clean Architecture principles. It consolidates the patterns and best practices that have been established through the adapter reconciliation process.

## Table of Contents

1. [Adapter Implementation Process](#adapter-implementation-process)
2. [Adapter Implementation Checklist](#adapter-implementation-checklist)
3. [Adapter Design Patterns](#adapter-design-patterns)
4. [Testing Adapters](#testing-adapters)
5. [Error Handling](#error-handling)
6. [Dependency Management](#dependency-management)
7. [Example Implementations](#example-implementations)

## Adapter Implementation Process

When implementing a new adapter in the Samstraumr project, follow this structured process to ensure consistency and compliance with Clean Architecture principles:

1. **Define the Port Interface**:
   - Create an interface in the `org.s8r.application.port` package
   - Define methods that represent domain operations, not infrastructure concerns
   - Design for domain-first, with infrastructure as an implementation detail
   - Use appropriate return types that represent results and potential failures
   - Document the interface thoroughly with Javadoc comments

2. **Implement the Adapter**:
   - Create a class in the `org.s8r.infrastructure` package
   - Implement the port interface defined in step 1
   - Bridge between the domain model and infrastructure concerns
   - Handle all infrastructure-specific details within the adapter
   - Provide constructors for both production use and testing

3. **Implement Contract Tests**:
   - Create a contract test class in `org.s8r.adapter.contract` package
   - Extend the `PortContractTest<T>` base class
   - Implement tests for all interface methods
   - Test error conditions and edge cases
   - Test null input handling

4. **Implement Unit Tests**:
   - Create a unit test class in the appropriate package
   - Test the adapter implementation details
   - Use mocks for external dependencies
   - Test error handling and exceptional cases

5. **Document the Adapter**:
   - Add comprehensive Javadoc documentation
   - Document any implementation-specific details
   - Document any assumptions or limitations

## Adapter Implementation Checklist

Use this checklist to ensure that your adapter implementation follows best practices:

### Port interface checklist

- [ ] Interface is placed in the `org.s8r.application.port` package
- [ ] Interface methods represent domain operations
- [ ] Method parameters and return types use domain models
- [ ] Interface is thoroughly documented with Javadoc
- [ ] Optional values are used for operations that might not return a result
- [ ] Error handling mechanism is defined (exceptions, result objects, etc.)
- [ ] Default methods are provided where appropriate to simplify implementation

### Adapter implementation checklist

- [ ] Adapter is placed in the `org.s8r.infrastructure` package
- [ ] Adapter implements the appropriate port interface
- [ ] Adapter provides necessary constructors (both for production and testing)
- [ ] Adapter properly converts between domain and infrastructure models
- [ ] Adapter handles infrastructure-specific exceptions and errors
- [ ] Adapter is stateless or handles state in a thread-safe manner
- [ ] Adapter logs important operations and errors
- [ ] Adapter initializes and cleans up resources properly

### Testing checklist

- [ ] Contract tests extend `PortContractTest<T>` base class
- [ ] Contract tests verify all interface methods
- [ ] Contract tests verify null input handling
- [ ] Contract tests verify error handling
- [ ] Unit tests focus on implementation-specific details
- [ ] Unit tests mock external dependencies
- [ ] Integration tests verify adapter interaction with real infrastructure

## Adapter Design Patterns

The Samstraumr project uses several adapter design patterns to maintain clean architectural boundaries:

### Port adapter pattern

The primary pattern used for implementing adapters in Samstraumr is the Port Adapter Pattern. This pattern involves:

1. Defining a port interface in the application layer
2. Implementing the port interface in the infrastructure layer
3. Using the adapter to bridge between domain and infrastructure

```java
// Port interface in application layer
public interface FileSystemPort {
    FileResult readFile(String path);
    FileResult writeFile(String path, String content);
    // other methods...
}

// Adapter implementation in infrastructure layer
public class StandardFileSystemAdapter implements FileSystemPort {
    @Override
    public FileResult readFile(String path) {
        // Implement file reading using infrastructure concerns
    }
    
    @Override
    public FileResult writeFile(String path, String content) {
        // Implement file writing using infrastructure concerns
    }
    // other methods...
}
```

### Result object pattern

For operations that may fail, use the Result Object Pattern to encapsulate success/failure information:

```java
public class FileResult {
    private final boolean successful;
    private final String message;
    private final String reason;
    private final Map<String, Object> attributes;
    
    // Factory methods for success and failure results
    public static FileResult success(String message) { ... }
    public static FileResult failure(String message, String reason) { ... }
    
    // Getters for result properties
    public boolean isSuccessful() { ... }
    public String getMessage() { ... }
    public Optional<String> getReason() { ... }
    public Map<String, Object> getAttributes() { ... }
}
```

### Factory method pattern

For complex object creation, use the Factory Method Pattern:

```java
public class MachineFactoryAdapter implements MachineFactoryPort {
    public MachinePort createMachine(MachineType type, String name) {
        // Create a domain machine
        Machine machine = Machine.create(name, type);
        
        // Create and return an adapter for the machine
        return MachineAdapter.createMachinePortFromDomain(machine);
    }
}
```

### Null object pattern

For cases where a null value might be returned, consider using the Null Object Pattern:

```java
public class NullLogger implements LoggerPort {
    @Override
    public void debug(String message, Object... args) {
        // Do nothing
    }
    
    @Override
    public void info(String message, Object... args) {
        // Do nothing
    }
    
    // other methods...
}
```

## Testing Adapters

Adapters must be thoroughly tested to ensure they correctly implement their port interfaces and handle infrastructure concerns properly. The Samstraumr project uses a three-tier testing approach:

### Contract tests

Contract tests verify that adapters correctly implement their port interfaces, focusing on the behavior expected by the application core:

```java
public class ConfigurationPortContractTest extends PortContractTest<ConfigurationPort> {
    @Override
    protected ConfigurationPort createPortImplementation() {
        // Create an implementation for testing
    }
    
    @Test
    @DisplayName("Should retrieve string values correctly")
    public void stringValueTests() {
        // Test string value retrieval
    }
    
    // other tests...
}
```

### Unit tests

Unit tests focus on the internal implementation details of adapters:

```java
public class StandardFileSystemAdapterTest {
    @Test
    public void shouldHandleIOExceptions() {
        // Test how the adapter handles IO exceptions
    }
    
    // other tests...
}
```

### Integration tests

Integration tests verify that adapters interact correctly with real infrastructure:

```java
@IntegrationTest
public class DatabaseAdapterIntegrationTest {
    @Test
    public void shouldConnectToRealDatabase() {
        // Test with a real database connection
    }
    
    // other tests...
}
```

## Error Handling

Adapters must handle errors properly, following these guidelines:

1. **Infrastructure Exceptions**: 
   - Catch infrastructure-specific exceptions
   - Convert them to domain-friendly formats
   - Log the original exception for debugging

2. **Result Objects**:
   - Use result objects to represent operation outcomes
   - Include success/failure status
   - Include error messages and reasons
   - Include additional context for debugging

3. **Logging**:
   - Log all significant errors with appropriate context
   - Use different log levels appropriately (debug, info, warn, error)
   - Include enough information for troubleshooting

## Dependency Management

Adapters should manage their dependencies carefully:

1. **Inversion of Control**:
   - Accept dependencies through constructors
   - Don't create dependencies directly (except in factories)

2. **Minimal Dependencies**:
   - Keep dependencies to a minimum
   - Only depend on what's necessary

3. **Isolation**:
   - Isolate infrastructure concerns within adapters
   - Don't leak infrastructure details to the domain

## Example Implementations

The following adapter implementations serve as reference examples:

### Filesystemadapter

The `StandardFileSystemAdapter` provides a comprehensive example of implementing a port interface for file system operations:

- Uses a Result Object Pattern to represent operation outcomes
- Handles file system exceptions properly
- Converts between domain and infrastructure representations
- Properly validates input parameters
- Provides comprehensive logging
- Cleans up resources properly

### Configurationadapter

The `ConfigurationAdapter` demonstrates how to implement a port interface for configuration operations:

- Handles configuration values of different types
- Provides proper default values
- Converts between string representations and typed values
- Logs parsing errors and other issues
- Supports different configuration sources

### Machineadapter

The `MachineAdapter` demonstrates how to implement an adapter for domain objects:

- Bridges between domain and infrastructure representations
- Uses factory methods for object creation
- Handles type conversion between different representations
- Provides bidirectional conversion between domain and component types
- Implements proper state management

---

