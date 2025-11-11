# Samstraumr Validation System

The Samstraumr framework implements a comprehensive validation system to prevent invalid operations, detect errors early, and provide clear feedback to developers.

## Validation Philosophy

Our validation approach follows these key principles:

1. **Fail Fast**: Detect and report errors at the earliest possible point
2. **Clear Context**: Provide detailed information about what failed and why
3. **Consistent Patterns**: Use similar validation patterns across the framework
4. **Domain Integration**: Ensure validation rules align with domain constraints

## Validation Components

The validation system consists of specialized validator classes and exception types for different aspects of the system:

### Validator Classes

| Validator | Responsibility |
|-----------|----------------|
| `ComponentNameValidator` | Validates component names follow conventions and constraints |
| `ComponentReferenceValidator` | Ensures referenced components exist |
| `ComponentTypeValidator` | Verifies component types are appropriate for their context |
| `CompositeConnectionValidator` | Validates connections between components, preventing cycles |
| `MachineComponentValidator` | Ensures machine components are valid composites |
| `MachineStateValidator` | Verifies state transitions are allowed |

### Exception Types

| Exception | Purpose |
|-----------|---------|
| `InvalidComponentNameException` | Reports invalid component name patterns |
| `NonExistentComponentReferenceException` | Reports references to non-existent components |
| `InvalidCompositeTypeException` | Reports when a component is not a valid composite type |
| `ConnectionCycleException` | Reports circular references between components |
| `InvalidMachineStateTransitionException` | Reports invalid state transitions |

## Integration Points

Validation is integrated at key points in the framework:

### Component Creation and Management

```java
// Component name validation
ComponentNameValidator.validateComponentName(name);

// Component reference validation
ComponentReferenceValidator.validateComponentReference(
    "connect", sourceId, targetId, this::containsComponent);
```

### Machine Operations

```java
// Machine state validation
MachineStateValidator.validateStateTransition(id, currentState, newState);

// Machine component validation
MachineComponentValidator.validateMachineComponent(this, component);
```

### Composite Connections

```java
// Connection validation including cycle detection
CompositeConnectionValidator.validateConnection(
    compositeId, sourceId, targetId, type, existingConnections, this::containsComponent);
```

## Validation Strategy Implementation

### Early Validation

Validation is performed at public API boundaries before any state changes occur:

```java
public void addComponent(CompositeComponent component) {
    // Validate state allows this operation
    MachineStateValidator.validateOperationState(id, "addComponent", state);
    
    // Validate component
    MachineComponentValidator.validateMachineComponent(this, component);
    
    // Operation proceeds only after validation passes
    components.put(component.getId().getIdString(), component);
    logActivity("Added component: " + component.getId().getShortId());
}
```

### Exception Context

Exceptions include detailed context for troubleshooting:

```java
public class InvalidMachineStateTransitionException extends ComponentException {
    private final ComponentId machineId;
    private final MachineState fromState;
    private final MachineState toState;
    private final String operation;
    private final MachineState[] validStates;
    
    // Methods to access context information
}
```

## Testing Validation

The validation system is thoroughly tested with both unit and integration tests:

1. **Unit tests** for each validator to verify specific validation rules
2. **Integration tests** to ensure validation works in component interactions
3. **Manual tests** that demonstrate validation behavior for developers

## References

- [ADR-0013: Implement Comprehensive Validation System](./decisions/0013-implement-comprehensive-validation-system.md)
- [Error Handling Strategy](./decisions/0011-adopt-standardized-error-handling-strategy.md)