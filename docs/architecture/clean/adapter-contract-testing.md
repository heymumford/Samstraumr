# Adapter Contract Testing

This document outlines the approach to adapter contract testing in the Samstraumr project, explaining the rationale, implementation patterns, and best practices.

## Overview

Contract tests verify that adapter implementations correctly implement their port interfaces as expected by the application core, regardless of their specific implementation details. This ensures that any adapter can be substituted with another implementation as long as it adheres to the contract.

## Contract Testing Structure

### Base contract test class

All contract tests extend the abstract `PortContractTest<T>` class, which provides:

- Common setup for all tests
- Abstract methods for creating port implementations
- Abstract methods for verifying null input handling
- Abstract methods for verifying required method implementations

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

### Contract test implementation

Specific contract tests implement the abstract methods and add test cases for their specific port interfaces:

```java
public class DataFlowEventPortContractTest extends PortContractTest<DataFlowEventPort> {

    private ComponentId sourceId;
    private ComponentId targetId;
    
    @Override
    protected DataFlowEventPort createPortImplementation() {
        // Create component IDs for testing
        sourceId = new ComponentId(UUID.randomUUID().toString());
        targetId = new ComponentId(UUID.randomUUID().toString());
        
        return new DataFlowEventHandler(logger);
    }
    
    @Override
    protected void verifyNullInputHandling() {
        // This is tested in nullInputHandlingTests()
    }
    
    @Override
    protected void verifyRequiredMethods() {
        // This is tested across multiple method-specific tests
    }
    
    @Test
    @DisplayName("Should handle null inputs gracefully")
    public void nullInputHandlingTests() {
        // Test null inputs
    }
    
    @Test
    @DisplayName("Should manage subscriptions correctly")
    public void subscriptionManagementTests() {
        // Test subscription management
    }
    
    @Test
    @DisplayName("Should publish data to subscribers correctly")
    public void dataPublishingTests() {
        // Test data publishing
    }
}
```

### Test fixture factory

The `TestFixtureFactory` class provides utility methods for creating test fixtures:

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

### Test suite runner

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

## Contract Test Design Principles

### 1. test the interface, not the implementation

Contract tests focus on the behavior expected by the application core, not the internal implementation details of the adapter.

```java
// Good - Tests the behavior expected by the interface
@Test
public void shouldManageSubscriptions() {
    portUnderTest.subscribe(sourceId, "channel", handler);
    assertTrue(portUnderTest.getComponentSubscriptions(sourceId).contains("channel"));
}

// Bad - Tests implementation details
@Test
public void shouldUseSpecificImplementationDetails() {
    DataFlowEventHandler handler = (DataFlowEventHandler) portUnderTest;
    handler.getInternalDataStructure().add("something");
}
```

### 2. test edge cases and error handling

Contract tests verify that adapters handle edge cases and errors as expected by the interface.

```java
@Test
public void shouldHandleNullInputs() {
    // Test with null values
    portUnderTest.subscribe(null, "channel", handler);
    portUnderTest.subscribe(sourceId, null, handler);
    portUnderTest.subscribe(sourceId, "channel", null);
    
    // No exceptions should be thrown
    
    // Output behavior should be predictable
    assertTrue(portUnderTest.getComponentSubscriptions(null).isEmpty());
}
```

### 3. test state changes

Contract tests verify that port methods correctly change the state of the port implementation.

```java
@Test
public void shouldTransitionStates() {
    // Initial state
    assertEquals(MachineState.STOPPED, portUnderTest.getMachineState());
    
    // Transition state
    portUnderTest.start();
    
    // Verify state change
    assertEquals(MachineState.RUNNING, portUnderTest.getMachineState());
}
```

### 4. test interactions between methods

Contract tests verify the interactions between methods in the port interface.

```java
@Test
public void shouldInteractBetweenMethods() {
    // Add a composite
    portUnderTest.addComposite("comp1", composite1);
    
    // Remove the composite
    Optional<CompositeComponentPort> removed = portUnderTest.removeComposite("comp1");
    
    // Verify the removed composite is the same as the added one
    assertTrue(removed.isPresent());
    assertEquals(composite1, removed.get());
    
    // Verify it's not in the composites anymore
    assertFalse(portUnderTest.getComposites().containsKey("comp1"));
}
```

## Best Practices for Writing Contract Tests

1. **Test One Behavior Per Test**: Each test should verify a single aspect of the interface.

2. **Use Clear Test Names**: Test names should clearly describe the behavior being tested.

3. **Test Default Implementations**: Many port interfaces provide default method implementations that should be tested.

4. **Use Mocks Sparingly**: Use mocks only when necessary to isolate the port implementation.

5. **Provide Basic Implementations**: Test fixtures should provide basic implementations for testing.

6. **Test All Method Variants**: Test all variants of methods, including overloaded versions.

7. **Test Threading Behavior**: For ports that are thread-safe, test their behavior in concurrent scenarios.

8. **Verify Error Handling**: Test how ports handle errors and exceptions.

9. **Test Interface Evolution**: When interfaces evolve, contract tests should be updated to verify backward compatibility.

10. **Test Documentation**: Document any undocumented behavior discovered during testing.

## Implemented Port Contract Tests

The following port interfaces have contract tests implemented:

1. **ComponentPort**: Tests for component identity, lifecycle management, and event publishing.

2. **MachinePort**: Tests for machine state management, composite management, and connections.

3. **DataFlowEventPort**: Tests for subscription management and data publishing.

4. **NotificationPort**: Tests for recipient management and notification delivery.

## Next Steps

1. **Add Coverage for Remaining Ports**: Implement contract tests for all port interfaces.

2. **Add Performance Tests**: Add tests for performance characteristics where relevant.

3. **Add Documentation**: Document contract expectations in the interface Javadoc.

4. **Add Contract Verification**: Add checks to ensure all adapters have contract tests.

---

