<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# S8r Testing

This document outlines the testing approach and implementation for the S8r framework.

## Testing Goals

1. **Comprehensive Component Validation** - Test all aspects of the new Component implementation
2. **Behavior-Driven Tests** - Use BDD to clearly specify expected behaviors
3. **Positive and Negative Paths** - Test both success paths and failure conditions
4. **Full Lifecycle Coverage** - Test components through their entire lifecycle

## Testing Structure

Tests are organized hierarchically to match the component model:

```
Component Tests
├── Core Tests                   (Basic component functionality)
├── Identity Tests               (Identity validation)
├── Lifecycle Tests              (Lifecycle state transitions)
├── Composite Tests              (Component composition)
└── Machine Tests                (Higher-level abstractions)
```

## Positive Path Tests

The following positive paths must be tested:

1. **Component Creation**

   ```gherkin
   Scenario: Creating a valid component
     Given a valid environment
     When I create a component with reason "Test Component"
     Then the component should be created successfully
     And its status should be "READY"
     And its lifecycle state should be "READY"
   ```
2. **Identity Validation**

   ```gherkin
   Scenario: Creating child component identity
     Given a parent component with identity
     When I create a child component
     Then the child should have a valid identity
     And the child's identity should reference the parent
     And the child's hierarchical address should include the parent's ID
   ```
3. **Lifecycle Progression**

   ```gherkin
   Scenario: Component progressing through lifecycle
     Given a newly created component
     When the component proceeds through early lifecycle
     Then the component should transition through the expected states
     And each lifecycle transition should be logged to memory
   ```
4. **Termination**

   ```gherkin
   Scenario: Component termination
     Given an active component
     When I terminate the component
     Then the component's status should be "TERMINATED"
     And the component's lifecycle state should be "TERMINATED"
     And all resources should be properly released
   ```

## Negative Path Tests

The following negative paths must be tested:

1. **Invalid Creation Parameters**

   ```gherkin
   Scenario: Creating a component with null reason
     Given a valid environment
     When I try to create a component with a null reason
     Then an InitializationException should be thrown
     And the exception message should contain "Reason cannot be null"
   ```
2. **Invalid Operations**

   ```gherkin
   Scenario: Operating on a terminated component
     Given a terminated component
     When I try to set a new status
     Then an IllegalStateException should be thrown
     And the exception message should indicate the component is terminated
   ```
3. **Resource Cleanup Verification**

   ```gherkin
   Scenario: Resource cleanup during termination
     Given a component with active resources
     When the component is terminated
     Then all timers should be canceled
     And termination should be logged to memory
   ```

## Test Implementation Approach

1. **Feature Files** - Create comprehensive feature files for each test area
2. **Step Definitions** - Implement step definitions that test both positive and negative paths
3. **Test Runners** - Set up runners with appropriate tags and annotations
4. **Test Data** - Create test fixtures and sample data for consistent testing

## Test Categories

Tests are categorized using annotations:

- `@ATL` - Above-the-line critical tests
- `@TubeTest` / `@UnitTest` - Component/unit tests
- `@CompositeTest` / `@ComponentTest` - Composite/component tests
- `@IdentityTest` - Identity-specific tests
- `@LifecycleTest` - Lifecycle-specific tests

## Implementation Priorities

Tests are prioritized as follows:

1. **P0: Critical Component Tests**
   - Core creation/termination
   - Exception handling
   - Basic identity validation
2. **P1: Lifecycle and Complex Cases**
   - Full lifecycle testing
   - Complex identity relationships
   - Negative path edge cases
3. **P2: Advanced Features**
   - Composite interactions
   - Machine abstractions
   - Performance scenarios
