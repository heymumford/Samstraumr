---
applyTo:
  - "modules/*/src/test/java/**"
  - "test-module/**"
  - "test-port-interfaces/**"
  - "lifecycle-test/**"
  - "**/src/test/**"
excludeAgent: []
---

# Test Code Instructions

These instructions apply to all test code in the Samstraumr repository.

## Testing Philosophy

- Follow the testing pyramid: many unit tests, fewer integration tests, minimal end-to-end tests
- Tests should be fast, isolated, and deterministic
- Each test should verify a single behavior
- Tests are documentation - make them readable and clear

## Test Organization

### Test Types

- **Unit Tests** (`@UnitTest`): Test individual classes in isolation
  - Fast execution (milliseconds)
  - No external dependencies
  - Use mocks/stubs for collaborators
  
- **Component Tests** (`@ComponentTest`): Test component behavior and interactions
  - Test lifecycle management
  - Verify state transitions
  - Test error handling and recovery
  
- **Integration Tests** (`@IntegrationTest`): Test system integration points
  - Test actual component interactions
  - Verify event-driven communication
  - Test composite assembly
  
- **Lifecycle Tests**: Test full component lifecycle scenarios
  - Placed in `lifecycle-test/` directory
  - Use `./s8r-test-lifecycle` to run

### Test Categories

All tests must be tagged with `@ATL` (All Test Level) annotation plus their specific category.

## Naming Conventions

### Test Class Names

- Format: `[ClassName]Test` for unit tests
- Format: `[ClassName]ComponentTest` for component tests
- Format: `[ClassName]IntegrationTest` for integration tests
- Example: `ValidatorTest`, `ComponentLifecycleComponentTest`

### Test Method Names

Use descriptive names that explain what is being tested:

```java
@Test
void shouldReturnValidationErrorWhenInputIsNull() {
    // test implementation
}

@Test
void shouldTransitionToStartedStateWhenStartMethodCalled() {
    // test implementation
}
```

Format: `should[ExpectedBehavior]When[Condition]`

## Test Structure

### Arrange-Act-Assert Pattern

```java
@Test
void shouldCalculateSumCorrectly() {
    // Arrange
    Calculator calculator = new Calculator();
    int a = 5;
    int b = 3;
    
    // Act
    int result = calculator.add(a, b);
    
    // Assert
    assertEquals(8, result);
}
```

### Given-When-Then (BDD)

For Cucumber/BDD tests:

```gherkin
Given a component in INITIALIZED state
When the start method is called
Then the component should transition to STARTED state
```

## Mocking Guidelines

- Use Mockito for mocking
- Prefer `@Mock` and `@InjectMocks` annotations
- Mock external dependencies, not classes under test
- Verify interactions when behavior is important
- Stub return values when state is important

```java
@Mock
private EventBus eventBus;

@InjectMocks
private MyComponent component;

@Test
void shouldPublishEventWhenProcessingComplete() {
    // Arrange
    when(eventBus.publish(any())).thenReturn(true);
    
    // Act
    component.process();
    
    // Assert
    verify(eventBus).publish(argThat(event -> 
        event instanceof ProcessingCompleteEvent
    ));
}
```

## Test Data Management

- Use test builders for complex objects
- Keep test data in test resources (`src/test/resources/`)
- Use meaningful test data that represents real scenarios
- Avoid magic numbers - use named constants

## Assertions

- Use descriptive assertion messages
- Use appropriate assertion methods:
  - `assertEquals()` for value equality
  - `assertSame()` for reference equality
  - `assertThrows()` for exception testing
  - `assertTimeout()` for performance tests
- Use AssertJ for fluent assertions when helpful

## Testing Asynchronous Code

- Use appropriate timeouts
- Use `CountDownLatch` or `CompletableFuture` for coordination
- Test both success and timeout scenarios
- Avoid `Thread.sleep()` - use proper synchronization

## Error Path Testing

- Test all error conditions
- Verify exception types and messages
- Test recovery mechanisms
- Test edge cases and boundary conditions

## Test Independence

- Tests must not depend on execution order
- Clean up test state in `@AfterEach`
- Don't share mutable state between tests
- Each test should be able to run in isolation

## Performance Testing

- Keep unit tests fast (< 100ms each)
- Mark slow tests with `@Slow` annotation
- Use `@Timeout` annotation for tests with time constraints
- Profile tests that take longer than expected

## Test Coverage

- Aim for 80% line coverage minimum
- Aim for 80% branch coverage minimum
- 100% coverage for validation logic
- Don't write tests just for coverage - test behavior

## Test Documentation

- Document complex test setup
- Explain non-obvious test scenarios
- Link to issues when testing bug fixes
- Use clear variable names that explain test intent

## Running Tests

```bash
# Run all tests
./s8r-test all

# Run specific test type
./s8r-test unit
./s8r-test component
./s8r-test integration

# Run with coverage
./s8r-test all --coverage

# Run lifecycle tests
./s8r-test-lifecycle
```

## Common Anti-Patterns to Avoid

- ❌ Testing implementation details instead of behavior
- ❌ Writing tests that depend on other tests
- ❌ Using real external dependencies in unit tests
- ❌ Catching exceptions to verify they were thrown
- ❌ Testing private methods directly
- ❌ Creating overly complex test setup
- ❌ Using random data that makes tests non-deterministic
