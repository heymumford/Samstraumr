# Adapter Contract Test Implementation

This document provides a detailed overview of the adapter contract test implementation in the Samstraumr project. It explains the purpose, structure, and benefits of the contract testing approach.

## Overview

The adapter contract test suite ensures that all adapter implementations correctly fulfill the contracts defined by their port interfaces. This is a critical part of maintaining the Clean Architecture boundaries in the Samstraumr project.

## Key Components

### Base Contract Test Class

All contract tests extend the abstract `PortContractTest<T>` class, which provides:

- Common setup code
- Logger initialization
- Abstract methods for implementation-specific testing

```java
public abstract class PortContractTest<T> {
    
    protected LoggerPort logger;
    protected T portUnderTest;
    
    @BeforeEach
    public void baseSetUp() {
        logger = new ConsoleLogger();
        portUnderTest = createPortImplementation();
    }
    
    protected abstract T createPortImplementation();
    protected abstract void verifyNullInputHandling();
    protected abstract void verifyRequiredMethods();
}
```

### Test Fixture Factory

The `TestFixtureFactory` class provides standardized methods for creating test fixtures:

```java
public class TestFixtureFactory {
    
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    
    public static LoggerPort getLogger() {
        return getCachedInstance("logger", ConsoleLogger::new);
    }
    
    public static ConfigurationPort getConfigurationPort() {
        return getCachedInstance("configPort", 
                () -> new ConfigurationAdapter(getLogger()));
    }
    
    // Other factory methods...
}
```

### Contract Test Suite

The `RunAdapterContractTests` class defines a JUnit 5 test suite for running all contract tests:

```java
@Suite
@SuiteDisplayName("Adapter Contract Tests")
@SelectPackages("org.s8r.adapter.contract")
@IncludeClassNamePatterns(".*ContractTest")
public class RunAdapterContractTests {
    // This class is just a marker for the test suite
}
```

## Implemented Contract Tests

### Component Port Contract Test

Tests for the `ComponentPort` interface, covering:

- Component identity information
- Lifecycle state management
- Domain event handling
- Data publishing

```java
public class ComponentPortContractTest extends PortContractTest<ComponentPort> {
    // Tests for ComponentPort contract
}
```

### Machine Port Contract Test

Tests for the `MachinePort` interface, covering:

- Machine identity and state
- Component management
- Composite connections
- State transitions

```java
public class MachinePortContractTest extends PortContractTest<MachinePort> {
    // Tests for MachinePort contract
}
```

### Data Flow Event Port Contract Test

Tests for the `DataFlowEventPort` interface, covering:

- Subscription management
- Data publishing
- Channel management
- Error handling

```java
public class DataFlowEventPortContractTest extends PortContractTest<DataFlowEventPort> {
    // Tests for DataFlowEventPort contract
}
```

### Configuration Port Contract Test

Tests for the `ConfigurationPort` interface, covering:

- String, integer, and boolean value retrieval
- Default value handling
- String list parsing
- Configuration subsets

```java
public class ConfigurationPortContractTest extends PortContractTest<ConfigurationPort> {
    // Tests for ConfigurationPort contract
}
```

### File System Port Contract Test

Tests for the `FileSystemPort` interface, covering:

- File and directory operations
- Content reading and writing
- File information retrieval
- Path manipulation

```java
public class FileSystemPortContractTest extends PortContractTest<FileSystemPort> {
    // Tests for FileSystemPort contract
}
```

## Testing Approach

The contract tests follow a consistent approach:

1. **Create Implementation**: Each test creates an implementation of the port interface to test against.

2. **Test Core Functionality**: Tests cover all methods defined in the port interface.

3. **Test Error Handling**: Tests verify that the implementation handles errors and edge cases correctly.

4. **Test Null Inputs**: Tests verify that the implementation handles null inputs gracefully.

## Benefits of Contract Testing

The adapter contract test implementation provides several benefits:

1. **Interface Compliance**: Ensures that all adapters correctly implement their port interfaces.

2. **Behavior Verification**: Verifies that adapters behave as expected by the application core.

3. **Documentation**: Serves as executable documentation for the expected behavior of port interfaces.

4. **Regression Prevention**: Catches regressions when adapter implementations change.

5. **Clean Architecture Enforcement**: Helps maintain the separation between domain and infrastructure layers.

## Usage Guidelines

When implementing a new adapter:

1. Create a contract test for the port interface if one doesn't exist.
2. Extend the `PortContractTest<T>` base class.
3. Implement the abstract methods to test your specific port interface.
4. Add test methods for all interface methods.
5. Run the contract tests to verify your implementation.

## Future Enhancements

Planned enhancements to the contract testing approach:

1. **Automatic Verification**: Integrate contract test verification into the build process.
2. **Test Coverage Analysis**: Analyze contract test coverage to identify gaps.
3. **Contract Documentation**: Generate interface contract documentation from the tests.
4. **Performance Testing**: Add performance tests for adapter implementations.

---

The adapter contract tests provide a robust framework for verifying adapter implementations and maintaining the Clean Architecture principles in the Samstraumr project.