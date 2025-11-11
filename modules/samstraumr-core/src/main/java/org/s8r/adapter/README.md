<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# S8r Migration Utilities

This package provides adapters and converters for migrating from the legacy Samstraumr (Tube-based) system to the new S8r (Component-based) architecture.

## Overview

The migration utilities in this package support the Strangler Fig pattern for gradually migrating from legacy code to the new Clean Architecture implementation. These utilities allow existing code to continue functioning while new code uses the improved Component-based API.

## Key Components

### S8rMigrationFactory

The main entry point for migration utilities. Creates all the necessary adapters and converters for migration.

```java
// Create migration factory
S8rMigrationFactory factory = new S8rMigrationFactory();

// Use factory methods for conversion
Component component = factory.tubeToComponent(tube);
```

### TubeComponentAdapter

Converts between Tube and Component objects. Allows using Tube objects with Component-based code.

### TubeComponentWrapper

A Component implementation that delegates to a Tube. This wrapper implements the new Component API while using a legacy Tube internally.

### CompositeAdapter

Converts between legacy Tube Composites and new Component Composites. This supports migrating entire composites with their internal tubes, connections, and data flow.

### MachineAdapter

Converts between legacy Tube Machines and new Component Machines. This enables migrating the highest level of orchestration in the system, including all composites, connections, and state management.

The MachineAdapter provides two primary operations:
1. `tubeMachineToComponentMachine`: Direct conversion creating a new Component Machine from a Tube Machine
2. `wrapTubeMachine`: Creates a wrapper that delegates to a Tube Machine, with bidirectional state synchronization

The MachineAdapter provides:

1. **Machine State Synchronization**:
   - Bidirectional state synchronization between wrapper and wrapped machine
   - All state updates are automatically propagated in both directions
   - State is kept consistent across the entire machine hierarchy

2. **Composite Management**:
   - Lazy loading and caching of composite wrappers
   - Automatic wrapping of new tube composites added after wrapper creation
   - Support for adding both wrapped and native component composites

3. **Connection Management**:
   - Maintains topology connections between composites
   - Propagates connections to the underlying tube machine
   - Preserves entire machine structure during conversion

4. **Lifecycle Management**:
   - Full support for machine lifecycle events (activate, deactivate, shutdown)
   - Event propagation between wrapped and wrapper machine
   - Proper cleanup of resources during shutdown

### TubeLegacyIdentityConverter

Converts between TubeIdentity and ComponentId objects. This allows identity objects to be migrated between systems.

### TubeLegacyEnvironmentConverter

Converts between the legacy and new Environment types. This enables parameter transfer between systems.

## Approaches

The package provides two approaches to adapter implementation:

1. **Direct Approach**: Classes like `TubeComponentAdapter` directly implement adapters for known types. This is more efficient for common migration scenarios.

2. **Reflective Approach**: Classes like `ReflectiveAdapterFactory` use reflection to work with legacy code without direct dependencies. This is more flexible but less performant.

## Usage Examples

See the [TubeToComponentMigration.md](../../../../docs/guides/migration/TubeToComponentMigration.md) guide for detailed examples of using these utilities.

### Converting a Tube Machine to a Component Machine

```java
// Create migration factory
S8rMigrationFactory factory = new S8rMigrationFactory();

// Convert a Tube machine to a Component machine
Machine componentMachine = factory.tubeMachineToComponentMachine(tubeMachine);

// Use the converted machine with Component APIs
componentMachine.addComposite("new", composite);
```

### Wrapping a Tube Machine

```java
// Create migration factory
S8rMigrationFactory factory = new S8rMigrationFactory();

// Wrap a Tube machine for use with Component APIs
Machine wrappedMachine = factory.wrapTubeMachine(tubeMachine);

// Changes to the wrapper propagate to the wrapped machine
wrappedMachine.updateState("key", "value");
```

### Working with Machine Components

```java
// Wrap a Tube machine
Machine wrappedMachine = factory.wrapTubeMachine(tubeMachine);

// Get a wrapped composite
Composite wrappedComposite = wrappedMachine.getComposite("flow");

// Add a new component to the wrapped composite
wrappedComposite.addComponent("processor", 
    Component.create("Data processor", componentEnv));

// Add a new composite
wrappedMachine.addComposite("newFlow", 
    factory.createHybridComposite("hybrid-flow", tubeEnv));

// Connect composites
wrappedMachine.connect("flow", "newFlow");

// Activate/deactivate
wrappedMachine.deactivate();
wrappedMachine.activate();

// Shutdown completely
wrappedMachine.shutdown();
```

## Best Practices

1. Use `S8rMigrationFactory` as the main entry point
2. Wrap legacy components early in the migration
3. Create new components using the new API
4. Gradually replace wrapped components with native implementations
5. Use Machine-level wrappers for complex migrations involving multiple composites
6. Take advantage of caching in wrappers to improve performance
7. Ensure state synchronization works correctly in bi-directional scenarios
8. Test thoroughly during migration to ensure behavior is preserved

## Implementation Notes

The adapters use reflection where necessary to access protected members of legacy classes. This approach avoids modifying the legacy codebase while enabling the necessary integration between old and new systems.

### Design Decisions

1. **State Synchronization**: Both TubeComponentWrapper and TubeMachineWrapper implement bidirectional state synchronization. This ensures changes in either the wrapper or the wrapped object propagate to the other.

2. **Caching Strategy**: The wrappers implement caching to avoid redundant conversion operations. Components and composites are cached after first access.

3. **Lifecycle Management**: Activation states and state transitions are carefully managed to ensure consistency between legacy and new implementations.

4. **Reflection Usage**: Reflection is used to access otherwise inaccessible fields and methods, particularly for environment access in legacy objects that don't expose their environment.

### Resolved Issues

The following issues have been addressed:

1. **Type Compatibility**: Added conversion methods in MachineAdapter and CompositeAdapter to handle type incompatibilities.

2. **Environment Access**: Implemented reflection-based methods for accessing environment fields in all adapters.

3. **Component Hierarchy**: Improved mapping between domain.component and component hierarchy with consistent conversion methods.

4. **Symbol Resolution**: Added explicit mappings for TubeStatus and LifecycleState constants in the adapters.

5. **Package Structure**: Using bidirectional symlinks to ensure proper resolution between different package structures.

### Future Enhancements

Several enhancements are planned for future releases:

1. **Performance Optimization**: Further optimize the reflection-based operations for better performance.

2. **Event Propagation**: Enhance event propagation between wrapped and wrapper objects.

3. **Comprehensive Testing**: Add more comprehensive test coverage for complex migration scenarios.

4. **Documentation**: Provide more examples for common migration patterns.

5. **Package Restructuring**: Ultimately eliminate the need for symlinks by restructuring the package organization.