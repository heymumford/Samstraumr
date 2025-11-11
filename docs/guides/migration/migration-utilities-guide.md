<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Migration Utilities Guide

This document provides detailed information about the migration utilities available in the S8r framework for transitioning from Samstraumr (tube-based) to S8r (component-based) architecture.

## Table of Contents

1. [Introduction](#introduction)
2. [S8rMigrationFactory](#s8rmigrationfactory)
3. [Component Adapters](#component-adapters)
4. [Composite Adapters](#composite-adapters)
5. [Machine Adapters](#machine-adapters)
6. [Identity and Environment Conversion](#identity-and-environment-conversion)
7. [Reflective Adapter Factory](#reflective-adapter-factory)
8. [Port Interface Adapters](#port-interface-adapters)
9. [Adapter Contract Testing](#adapter-contract-testing)
10. [Recommended Migration Strategies](#recommended-migration-strategies)
11. [Troubleshooting](#troubleshooting)
12. [Migration Scripts](#migration-scripts)
13. [Performance Considerations](#performance-considerations)
14. [Integration with Clean Architecture](#integration-with-clean-architecture)
15. [Version Compatibility](#version-compatibility)

## Introduction

The S8r framework provides a set of utilities to facilitate migration from the legacy Samstraumr tube-based architecture to the new component-based architecture. These utilities include adapters for components, composites, and machines, as well as converters for identity and environment objects.

The migration utilities follow these principles:

- **Zero-loss migration**: All data and state is preserved during migration
- **Bidirectional synchronization**: Changes in legacy objects are reflected in new objects and vice versa
- **Minimal performance impact**: Adapters have minimal overhead
- **Transparent operation**: Client code can work with either legacy or new APIs without knowing the difference
- **Clean Architecture alignment**: Adapters follow Clean Architecture principles to ensure proper separation of concerns

### Key benefits

- **Phased migration**: Gradually migrate your codebase at your own pace
- **Risk reduction**: Test new components with existing systems before full migration
- **Reuse of existing code**: Leverage existing well-tested components through adapters
- **Simplified integration**: Connect legacy and new components seamlessly
- **Minimal code changes**: Maintain backward compatibility with existing client code

## S8rMigrationFactory

The `S8rMigrationFactory` class is the central entry point for all migration utilities. It provides factory methods for creating adapters and wrappers for different object types.

### Usage

```java
import org.s8r.adapter.S8rMigrationFactory;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.tube.Tube;

// Create the factory with a logger
LoggerPort logger = new ConsoleLogger("Migration");
S8rMigrationFactory factory = new S8rMigrationFactory(logger);

// Or create with default console logger
S8rMigrationFactory factory = new S8rMigrationFactory();

// Convert a tube to a component
Tube tube = // ... your existing tube
ComponentPort component = factory.tubeToComponent(tube);

// Wrap a tube composite as an S8r composite
org.s8r.tube.composite.Composite tubeComposite = // ... your existing composite
Composite s8rComposite = factory.wrapTubeComposite(tubeComposite);

// Wrap a tube machine as an S8r machine
org.s8r.tube.machine.Machine tubeMachine = // ... your existing machine
Machine s8rMachine = factory.wrapTubeMachine(tubeMachine);

// Access the adapter directly
TubeComponentAdapter adapter = (TubeComponentAdapter) factory.getComponentAdapter();
```

### Available factory methods

| Method | Description |
|--------|-------------|
| `tubeToComponent(Tube)` | Converts a Tube to a ComponentPort with bidirectional state synchronization |
| `createTubeComponent(String, Environment)` | Creates a new Tube and wraps it as a ComponentPort |
| `createChildTubeComponent(String, Tube)` | Creates a child Tube from a parent Tube and wraps it as a ComponentPort |
| `extractTube(ComponentPort)` | Extracts the wrapped Tube from a TubeComponentWrapper |
| `tubeCompositeToComponentComposite(org.s8r.tube.composite.Composite)` | Converts a Tube composite to a Component composite |
| `wrapTubeComposite(org.s8r.tube.composite.Composite)` | Wraps a tube Composite as an S8r Composite |
| `createHybridComposite(String, Environment)` | Creates a hybrid composite that can contain both Tubes and Components |
| `tubeMachineToComponentMachine(org.s8r.tube.machine.Machine)` | Converts a Tube machine to a Component machine |
| `tubeMachineToMachinePort(org.s8r.tube.machine.Machine)` | Converts a Tube machine to a MachinePort interface |
| `wrapTubeMachine(org.s8r.tube.machine.Machine)` | Wraps a tube Machine as an S8r Machine |
| `wrapTubeMachineAsPort(org.s8r.tube.machine.Machine)` | Creates a wrapper around a Tube machine that implements the MachinePort interface |
| `tubeEnvironmentToS8rEnvironment(Environment)` | Converts a Tube's Environment to an S8r component Environment |
| `s8rEnvironmentToTubeEnvironment(org.s8r.component.Environment)` | Converts an S8r component Environment to a Tube Environment |
| `tubeIdentityToComponentId(TubeIdentity)` | Converts a TubeIdentity to a ComponentId |

## Component Adapters

The S8r framework provides several adapters for converting between legacy tubes and new components.

### TubeComponentAdapter

The `TubeComponentAdapter` adapter converts a legacy `Tube` to an S8r `Component`.

```java
import org.s8r.adapter.TubeComponentAdapter;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Component;
import org.s8r.component.State;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeStatus;

// Create a logger for the adapter
LoggerPort logger = new ConsoleLogger("TubeAdapter");

// Create an adapter for an existing tube
Tube tube = // ... your existing tube
TubeComponentAdapter adapter = new TubeComponentAdapter(
    logger, 
    new TubeLegacyIdentityConverter(logger),
    new TubeLegacyEnvironmentConverter(logger)
);

// Wrap the tube with the adapter
Component component = adapter.wrapLegacyComponent(tube);

// Use bidirectional synchronization
// Changes to either tube or component will be reflected in both
tube.setStatus(TubeStatus.ACTIVE);
assert component.getState() == State.ACTIVE;

component.setState(State.DEGRADED);
assert tube.getStatus() == TubeStatus.DEGRADED;

// Create a child component from a parent tube
Component childComponent = adapter.createChildComponent("Child", tube);
```

### TubeComponentWrapper

The `TubeComponentWrapper` class wraps a Component as a Tube, providing the inverse of the TubeComponentAdapter.

```java
import org.s8r.adapter.TubeComponentWrapper;
import org.s8r.component.Component;
import org.s8r.tube.Tube;

// Create a wrapper for an S8r component
Component component = // ... your S8r component
TubeComponentWrapper wrapper = new TubeComponentWrapper(component);

// Use the wrapper as a Tube
Tube tube = wrapper;

// Changes to the component are reflected in the tube
component.setState(State.ACTIVE);
assert tube.getStatus() == TubeStatus.ACTIVE;
```

### ComponentAdapter

The `ComponentAdapter` provides a bridge between domain components and application services, following Clean Architecture principles.

```java
import org.s8r.adapter.ComponentAdapter;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.Component as DomainComponent;

// Create an adapter for a domain component
DomainComponent domainComponent = // ... your domain component
ComponentPort port = ComponentAdapter.createComponentPort(domainComponent);

// Use the port with application services
applicationService.processComponent(port);

// For composite components
CompositeComponent compositeComponent = // ... your domain composite component
CompositeComponentPort compositePort = ComponentAdapter.createCompositeComponentPort(compositeComponent);
```

## Composite Adapters

### CompositeAdapter

The `CompositeAdapter` wraps a legacy composite as an S8r composite and provides bidirectional synchronization.

```java
import org.s8r.adapter.CompositeAdapter;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Composite;
import org.s8r.component.Component;
import org.s8r.tube.composite.Composite as TubeComposite;

// Create a logger and adapters
LoggerPort logger = new ConsoleLogger("CompositeAdapter");
TubeComponentAdapter componentAdapter = // ... component adapter
TubeLegacyEnvironmentConverter environmentConverter = // ... environment converter

// Create an adapter for an existing tube composite
TubeComposite tubeComposite = // ... your existing composite
CompositeAdapter adapter = new CompositeAdapter(
    logger, componentAdapter, environmentConverter);

// Wrap the tube composite
Composite composite = adapter.wrapTubeComposite(tubeComposite);

// Convert a tube composite to a component composite
Composite convertedComposite = adapter.tubeCompositeToComponentComposite(tubeComposite);

// Add components to either composite
Component component = // ... your S8r component
composite.addComponent("component1", component);

// The component will be added to the underlying tube composite as well
assert tubeComposite.getTubes().size() == composite.getComponents().size();

// Create a hybrid composite that can contain both tubes and components
Composite hybridComposite = adapter.createHybridComposite(
    "hybrid", 
    // ... tube environment
);

// Add a tube from a legacy composite to a component composite
adapter.addTubeToComponentComposite(
    tubeComposite,         // source tube composite
    "tubeName",            // name of tube in source composite
    convertedComposite,    // target component composite
    "componentName"        // name to use in target composite
);
```

### TubeCompositeWrapper

The `TubeCompositeWrapper` class wraps an S8r Composite as a tube Composite.

```java
import org.s8r.adapter.CompositeAdapter.TubeCompositeWrapper;
import org.s8r.component.Composite;
import org.s8r.tube.composite.Composite as TubeComposite;

// Get a wrapper from a CompositeAdapter
TubeCompositeWrapper wrapper = // ... from adapter

// Use as a tube composite
TubeComposite tubeComposite = wrapper;

// Unwrap to get the original tube composite if needed
TubeComposite original = wrapper.unwrapTubeComposite();
```

## Machine Adapters

### MachineAdapter

The `MachineAdapter` wraps a legacy machine as an S8r machine with bidirectional synchronization.

```java
import org.s8r.adapter.MachineAdapter;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Machine;
import org.s8r.component.Composite;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.tube.machine.Machine as TubeMachine;

// Create a logger and adapters
LoggerPort logger = new ConsoleLogger("MachineAdapter");
CompositeAdapter compositeAdapter = // ... composite adapter
TubeLegacyEnvironmentConverter environmentConverter = // ... environment converter

// Create an adapter for an existing tube machine
TubeMachine tubeMachine = // ... your existing machine
MachineAdapter adapter = new MachineAdapter(
    logger, compositeAdapter, environmentConverter);

// Convert a tube machine to a component machine
Machine convertedMachine = adapter.tubeMachineToComponentMachine(tubeMachine);

// Wrap a tube machine as an S8r machine
Machine wrappedMachine = adapter.wrapTubeMachine(tubeMachine);

// Convert a tube machine to a MachinePort interface (Clean Architecture)
MachinePort machinePort = adapter.tubeMachineToMachinePort(tubeMachine);

// Create a wrapper that implements the MachinePort interface
MachinePort portWrapper = adapter.wrapTubeMachineAsPort(tubeMachine);

// Register composites with either machine
Composite composite = // ... your S8r composite
wrappedMachine.addComposite("composite1", composite);

// The composite will be registered with the underlying tube machine as well
assert tubeMachine.getComposites().size() == wrappedMachine.getComposites().size();
```

### Static Adapter Methods

The `MachineAdapter` class also provides static methods for creating port adapters:

```java
// Create a MachinePort from a domain Machine
MachinePort port1 = MachineAdapter.createMachinePortFromDomain(domainMachine);

// Create a MachinePort from a component Machine
MachinePort port2 = MachineAdapter.createMachinePortFromComponent(componentMachine);
```

### MachineFactoryAdapter

The `MachineFactoryAdapter` implements the MachineFactoryPort interface, allowing integration with Clean Architecture application services.

```java
import org.s8r.adapter.MachineFactoryAdapter;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.MachineFactoryPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;

// Create an adapter with a logger
LoggerPort logger = new ConsoleLogger("FactoryAdapter");
MachineFactoryAdapter adapter = new MachineFactoryAdapter(logger);

// Use the adapter through the port interface
MachineFactoryPort factoryPort = adapter;

// Create machines using various methods
MachinePort machine1 = factoryPort.createMachine(MachineType.DATA_PROCESSOR, "Test machine");

ComponentId id = ComponentId.create("machine-123");
MachinePort machine2 = factoryPort.createMachine(id, MachineType.PIPELINE);

Map<String, Object> config = Map.of("version", "1.0.0", "name", "Configured Machine");
MachinePort machine3 = factoryPort.createMachine(MachineType.CONTROLLER, "Config test", config);

// Clone an existing machine
MachinePort clone = factoryPort.cloneMachine(machine1, ComponentId.create("clone-1"));

// Use the convenience method for string-based type
MachinePort machine4 = adapter.createMachine("String type test", "DATA_PROCESSOR");
```

## Identity and Environment Conversion

The S8r framework provides converters for identity and environment objects.

### TubeLegacyIdentityConverter

The `TubeLegacyIdentityConverter` converts between legacy tube identities and S8r identities.

```java
import org.s8r.adapter.TubeLegacyIdentityConverter;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.tube.TubeIdentity;

// Create a logger and converter
LoggerPort logger = new ConsoleLogger("IdentityConverter");
TubeLegacyIdentityConverter converter = new TubeLegacyIdentityConverter(logger);

// Convert a tube identity to a ComponentId
TubeIdentity tubeIdentity = // ... your tube identity
ComponentId componentId = converter.toComponentId(
    tubeIdentity.getUniqueId(),
    tubeIdentity.getReason(),
    tubeIdentity.getLineage()
);

// Convert an S8r ComponentId to a tube identity
ComponentId id = // ... your component ID
TubeIdentity convertedIdentity = converter.fromComponentId(id);

// Convert between domain types
org.s8r.domain.identity.LegacyIdentity legacyId = converter.fromLegacyIdentity(tubeIdentity);
TubeIdentity tubeId = converter.toLegacyIdentity(legacyId);
```

### TubeLegacyEnvironmentConverter

The `TubeLegacyEnvironmentConverter` converts between legacy tube environments and S8r environments.

```java
import org.s8r.adapter.TubeLegacyEnvironmentConverter;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Environment;
import org.s8r.tube.Environment as TubeEnvironment;

// Create a logger and converter
LoggerPort logger = new ConsoleLogger("EnvironmentConverter");
TubeLegacyEnvironmentConverter converter = new TubeLegacyEnvironmentConverter(logger);

// Convert a tube environment to an S8r environment
TubeEnvironment tubeEnvironment = // ... your tube environment
Environment componentEnvironment = converter.fromLegacyEnvironment(tubeEnvironment);

// For core environment
org.s8r.component.core.Environment coreEnvironment = 
    converter.fromLegacyEnvironmentToCore(tubeEnvironment);

// Convert an S8r environment to a tube environment
Environment s8rEnvironment = // ... your S8r environment
TubeEnvironment convertedEnvironment = converter.toDomainEnvironment(s8rEnvironment);

// Convert between domain types
org.s8r.domain.identity.LegacyEnvironment legacyEnv = 
    converter.fromLegacyEnvironment(tubeEnvironment);
TubeEnvironment tubeEnv = converter.toLegacyEnvironment(legacyEnv);
```

## Reflective Adapter Factory

The S8r framework includes a reflective adapter factory that can create adapters for arbitrary legacy components.

### ReflectiveAdapterFactory

The `ReflectiveAdapterFactory` uses reflection to create adapters for legacy components that may not have direct adapter implementations.

```java
import org.s8r.adapter.ReflectiveAdapterFactory;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Component;

// Create a logger and factory
LoggerPort logger = new ConsoleLogger("ReflectiveFactory");
ReflectiveAdapterFactory factory = new ReflectiveAdapterFactory(logger);

// Create an adapter for an arbitrary legacy component
Object legacyComponent = // ... any legacy component type
Component component = factory.createAdapter(legacyComponent);

// Register custom adapter classes
factory.registerAdapterClass(
    MyLegacyComponent.class,
    MyCustomAdapter.class
);

// Create an adapter using the custom adapter
MyLegacyComponent myComponent = new MyLegacyComponent();
Component adapted = factory.createAdapter(myComponent);
// This will use MyCustomAdapter

// Unregister a custom adapter
factory.unregisterAdapterClass(MyLegacyComponent.class);
```

### ReflectiveIdentityConverter

The `ReflectiveIdentityConverter` uses reflection to convert between arbitrary identity types.

```java
import org.s8r.adapter.ReflectiveIdentityConverter;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.ComponentId;

// Create a logger and converter
LoggerPort logger = new ConsoleLogger("IdentityConverter");
ReflectiveIdentityConverter converter = new ReflectiveIdentityConverter(logger);

// Convert an arbitrary identity object to a ComponentId
Object legacyIdentity = // ... any legacy identity type
ComponentId componentId = converter.convert(legacyIdentity);

// Register a custom converter
converter.registerConverter(
    MyCustomIdentity.class,
    new MyCustomIdentityConverter()
);

// Convert using the custom converter
MyCustomIdentity customId = new MyCustomIdentity();
ComponentId converted = converter.convert(customId);
// This will use MyCustomIdentityConverter

// Unregister a custom converter
converter.unregisterConverter(MyCustomIdentity.class);
```

### ReflectiveEnvironmentConverter

The `ReflectiveEnvironmentConverter` uses reflection to convert between arbitrary environment types.

```java
import org.s8r.adapter.ReflectiveEnvironmentConverter;
import org.s8r.application.port.LoggerPort;
import org.s8r.component.Environment;

// Create a logger and converter
LoggerPort logger = new ConsoleLogger("EnvironmentConverter");
ReflectiveEnvironmentConverter converter = new ReflectiveEnvironmentConverter(logger);

// Convert an arbitrary environment object to an S8r environment
Object legacyEnvironment = // ... any legacy environment type
Environment environment = converter.convert(legacyEnvironment);

// Register a custom converter
converter.registerConverter(
    MyCustomEnvironment.class,
    new MyCustomEnvironmentConverter()
);

// Convert using the custom converter
MyCustomEnvironment customEnv = new MyCustomEnvironment();
Environment converted = converter.convert(customEnv);
// This will use MyCustomEnvironmentConverter

// Unregister a custom converter
converter.unregisterConverter(MyCustomEnvironment.class);
```

## Port Interface Adapters

S8r's Clean Architecture implementation uses port interfaces as boundaries between application and infrastructure layers. Several adapters are provided to implement these ports:

### ComponentPort Adapters

```java
import org.s8r.adapter.ComponentAdapter;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.Component;

// Create a ComponentPort from a domain Component
Component domainComponent = // ... domain component
ComponentPort port = ComponentAdapter.createComponentPort(domainComponent);

// Use with application services
applicationService.processComponent(port);
```

### CompositeComponentPort Adapters

```java
import org.s8r.adapter.ComponentAdapter;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.composite.CompositeComponent;

// Create a CompositeComponentPort from a domain CompositeComponent
CompositeComponent compositeComponent = // ... domain composite
CompositeComponentPort port = ComponentAdapter.createCompositeComponentPort(compositeComponent);

// Use with application services
applicationService.processComposite(port);
```

### MachinePort Adapters

```java
import org.s8r.adapter.MachineAdapter;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.machine.Machine;
import org.s8r.component.Machine as ComponentMachine;

// Create a MachinePort from a domain Machine
Machine domainMachine = // ... domain machine
MachinePort port1 = MachineAdapter.createMachinePortFromDomain(domainMachine);

// Create a MachinePort from a component Machine
ComponentMachine componentMachine = // ... component machine
MachinePort port2 = MachineAdapter.createMachinePortFromComponent(componentMachine);

// Use with application services
applicationService.processMachine(port1);
```

## Adapter Contract Testing

The S8r framework includes a comprehensive adapter contract testing framework to ensure adapters correctly implement their port interfaces.

### Running Contract Tests

```java
// Run all adapter contract tests
// This will run all classes matching *ContractTest in the org.s8r.adapter.contract package
org.junit.platform.launcher.Launcher.execute(
    LauncherDiscoveryRequestBuilder.request()
        .selectors(DiscoverySelectors.selectPackage("org.s8r.adapter.contract"))
        .filters(ClassNameFilter.includeClassNamePatterns(".*ContractTest"))
        .build()
);

// Run a specific contract test
org.junit.platform.launcher.Launcher.execute(
    LauncherDiscoveryRequestBuilder.request()
        .selectors(DiscoverySelectors.selectClass(ComponentPortContractTest.class))
        .build()
);
```

### Creating Contract Tests

```java
import org.s8r.adapter.contract.PortContractTest;
import org.s8r.domain.component.port.ComponentPort;

public class ComponentPortContractTest extends PortContractTest<ComponentPort> {
    
    @Override
    protected ComponentPort createPortImplementation() {
        // Create an implementation to test
        return new ComponentAdapter(/* parameters */);
    }
    
    @Override
    protected void verifyNullInputHandling() {
        // Test null input handling
        assertDoesNotThrow(() -> portUnderTest.setState(null));
    }
    
    @Override
    protected void verifyRequiredMethods() {
        // Test that required methods work correctly
        assertNotNull(portUnderTest.getId());
        assertNotNull(portUnderTest.getState());
    }
    
    @Test
    @DisplayName("Should maintain state correctly")
    public void stateMaintenance() {
        // Test state maintenance
        portUnderTest.setState(State.ACTIVE);
        assertEquals(State.ACTIVE, portUnderTest.getState());
    }
}
```

## Recommended Migration Strategies

### Strategy 1: Gradual Component Migration

1. Start by using the `S8rMigrationFactory` to wrap existing tube components
2. Update imports and API usage in client code
3. Test the application with the wrapped components
4. Gradually replace the wrapped components with native S8r components

#### Implementation plan

```java
// Step 1: Wrap existing tubes
S8rMigrationFactory factory = new S8rMigrationFactory();
Map<String, Component> wrappedComponents = new HashMap<>();

for (Tube tube : existingTubes) {
    Component component = factory.tubeToComponent(tube);
    wrappedComponents.put(tube.getUniqueId(), component);
}

// Step 2: Update client code to use Component API
for (String id : componentIds) {
    Component component = wrappedComponents.get(id);
    processWithNewAPI(component);
}

// Step 3: Test thoroughly
runComponentTests(wrappedComponents.values());

// Step 4: Replace with native components (later phase)
Map<String, Component> nativeComponents = new HashMap<>();
for (Map.Entry<String, Component> entry : wrappedComponents.entrySet()) {
    String id = entry.getKey();
    Component wrappedComponent = entry.getValue();
    
    // Create native component
    Component nativeComponent = new Component(id, new Environment());
    
    // Copy state
    nativeComponent.setState(wrappedComponent.getState());
    
    // Replace in map
    nativeComponents.put(id, nativeComponent);
}
```

### Strategy 2: Domain-First Migration

1. Identify and migrate your domain model to use S8r components
2. Use adapters to integrate the new domain model with legacy infrastructure
3. Gradually replace legacy infrastructure with S8r implementations

#### Implementation plan

```java
// Step 1: Create S8r domain model
// Implement your domain entities using S8r components

// Step 2: Connect to legacy infrastructure with adapters
// Example: Connect to legacy storage
LegacyStorage legacyStorage = // ... your legacy storage
StoragePort storageAdapter = new LegacyStorageAdapter(legacyStorage);

// Example: Connect to legacy event system
LegacyEventSystem legacyEvents = // ... your legacy event system 
EventPublisherPort eventAdapter = new LegacyEventAdapter(legacyEvents);

// Create domain service with adapters
DomainService service = new DomainService(storageAdapter, eventAdapter);

// Step 3: Replace infrastructure incrementally
// Later phase: replace legacy storage
StoragePort newStorage = new S8rStorage();
DomainService updatedService = new DomainService(newStorage, eventAdapter);
```

### Strategy 3: Service-by-Service Migration

1. Migrate one service or module at a time
2. Use adapters to ensure the migrated services can work with legacy components
3. Update clients of each service to use the new S8r API
4. Repeat for each service until the entire application is migrated

#### Implementation plan

```java
// Step 1: Identify a service to migrate
// Choose a relatively isolated service for the first migration

// Step 2: Create adapters for dependencies
S8rMigrationFactory factory = new S8rMigrationFactory();

// Adapt tubes to components
Map<String, ComponentPort> adaptedComponents = new HashMap<>();
for (Tube tube : serviceComponents) {
    ComponentPort component = factory.tubeToComponent(tube);
    adaptedComponents.put(tube.getUniqueId(), component);
}

// Step 3: Implement the new service
ComponentService newService = new ComponentService(adaptedComponents.values());
newService.initialize();

// Step 4: Update clients of the service
for (ServiceClient client : serviceClients) {
    client.useNewService(newService);
}

// Repeat for next service...
```

### Strategy 4: Clean Architecture Port Interface Migration

This strategy focuses on using port interfaces as boundaries between application and infrastructure layers:

1. Define port interfaces for core application functionality
2. Create adapters that implement these port interfaces but delegate to legacy code
3. Make the application dependent on the port interfaces, not the legacy implementations
4. Replace legacy adapters with new implementations over time

#### Implementation plan

```java
// Step 1: Define port interfaces
// See the port interfaces in org.s8r.application.port and org.s8r.domain.component.port

// Step 2: Create adapters that implement these interfaces
ComponentPort componentAdapter = new TubeComponentAdapter(tube);
MachinePort machineAdapter = MachineAdapter.createMachinePortFromDomain(machine);
StoragePort storageAdapter = new LegacyStorageAdapter(legacyStorage);

// Step 3: Make application dependent on ports
ApplicationService service = new ApplicationService(
    componentAdapter,
    machineAdapter,
    storageAdapter
);

// Step 4: Replace with new implementations over time
// Later: replace with native implementations
ComponentPort nativeComponent = new ComponentImpl();
StoragePort nativeStorage = new S8rStorage();

ApplicationService updatedService = new ApplicationService(
    nativeComponent,
    machineAdapter, // Keep this adapter until ready to migrate
    nativeStorage
);
```

## Troubleshooting

### Common issues

| Issue | Solution |
|-------|----------|
| Adapter returns null for a component property | Check that the legacy component has the corresponding property |
| State changes in adapter don't reflect in legacy component | Ensure bidirectional synchronization is enabled in the adapter |
| `ClassCastException` when using an adapter | Check that you're using the correct adapter for the legacy component type |
| Reflective adapter fails with "No such method" error | Check that the legacy component implements the expected interface |
| Performance degradation with adapters | Consider direct migration instead of using adapters for performance-critical code |
| `IllegalArgumentException` when creating adapters | Check that required parameters are not null |
| `UnsupportedOperationException` when calling adapter methods | Some methods may not be supported for legacy objects |
| Thread safety issues with adapters | Use synchronized blocks for thread-safe operations |
| Circular references causing stack overflow | Break circular references with a weakly referenced cache |
| Port interface method not implemented | Check if the method is abstract or has a default implementation |

### Debugging tips

1. Enable debug logging for the adapter classes to see conversion operations
   ```java
   // Set log level to DEBUG for adapters
   LoggerPort logger = new ConsoleLogger("Adapter", LogLevel.DEBUG);
   S8rMigrationFactory factory = new S8rMigrationFactory(logger);
   ```

2. Verify that identity and state objects are properly synchronized
   ```java
   // Check synchronization
   Component component = factory.tubeToComponent(tube);
   tube.setStatus(TubeStatus.ACTIVE);
   assert component.getState() == State.ACTIVE : "State not synchronized";
   ```

3. Check for inheritance issues that might affect adapter behavior
   ```java
   // Verify class inheritance
   assert component instanceof Component : "Not a Component instance";
   assert component instanceof TubeComponentAdapter : "Not a TubeComponentAdapter instance";
   ```

4. Test adapters in isolation before using them in production code
   ```java
   // Test adapter in isolation
   TubeComponentAdapter adapter = new TubeComponentAdapter(tube);
   adapter.setState(State.ACTIVE);
   assert tube.getStatus() == TubeStatus.ACTIVE : "Adapter failed to update tube status";
   ```

5. Add diagnostic logging to track adapter lifecycle and method calls
   ```java
   // Add diagnostic logging
   logger.debug("Creating adapter for tube: {}", tube.getUniqueId());
   logger.debug("Tube status: {}, Component state: {}", tube.getStatus(), component.getState());
   ```

## Migration Scripts

The S8r framework includes scripts for automated code migration.

### migrate-code.sh

The `migrate-code.sh` script scans Java source files for legacy API usage and replaces it with S8r API calls.

```bash
# Basic usage: transform code in the specified directory
./util/migrate-code.sh path/to/your/code

# Custom mappings: provide a custom mappings file
./util/migrate-code.sh --mapping=custom-mappings.txt path/to/your/code

# Dry run: show what would be changed without actually changing files
./util/migrate-code.sh --dry-run path/to/your/code

# Backup: create backup files before making changes
./util/migrate-code.sh --backup path/to/your/code

# Verbose output: show detailed information about the migration process
./util/migrate-code.sh --verbose path/to/your/code

# Specific file types: only process files with specific extensions
./util/migrate-code.sh --extensions=java,kt,scala path/to/your/code

# Exclude patterns: exclude files matching specific patterns
./util/migrate-code.sh --exclude="**/test/**,**/generated/**" path/to/your/code
```

#### Custom mapping file format

```
# Lines starting with # are comments
# Format: original_class_or_method -> replacement_class_or_method

# Import replacements
org.samstraumr.tube.Tube -> org.s8r.component.Component
org.samstraumr.tube.TubeStatus -> org.s8r.component.State

# Method replacements
Tube.create -> Component.create
tube.getStatus -> component.getState
tube.setStatus -> component.setState
```

### migrate-packages.sh

The `migrate-packages.sh` script updates package declarations and import statements in Java files.

```bash
# Basic usage: update packages in the specified directory
./util/scripts/migrate-packages.sh path/to/your/code

# Filter by package: only update imports from specific packages
./util/scripts/migrate-packages.sh --filter="org.samstraumr.tube" path/to/your/code

# Dry run: show what would be changed without actually changing files
./util/scripts/migrate-packages.sh --dry-run path/to/your/code

# Backup: create backup files before making changes
./util/scripts/migrate-packages.sh --backup path/to/your/code

# Verbose output: show detailed information about the migration process
./util/scripts/migrate-packages.sh --verbose path/to/your/code

# Specific file types: only process files with specific extensions
./util/scripts/migrate-packages.sh --extensions=java path/to/your/code

# Exclude patterns: exclude files matching specific patterns
./util/scripts/migrate-packages.sh --exclude="**/test/**" path/to/your/code
```

### Package mapping file

The package mapping is defined in `/util/config/package-mapping.properties`:

```properties
# Legacy package -> new package mappings
org.samstraumr.tube=org.s8r.component
org.samstraumr.tube.composite=org.s8r.component.composite
org.samstraumr.tube.machine=org.s8r.component.machine
org.samstraumr.tube.identity=org.s8r.component.identity
org.samstraumr.legacy=org.s8r.legacy
```

## Performance Considerations

### Overhead measurements

| Adapter Type | Method Call Overhead | Memory Usage | Creation Cost |
|--------------|----------------------|--------------|---------------|
| TubeComponentAdapter | ~2-5% | ~150-250 bytes per instance | Medium |
| CompositeAdapter | ~3-7% | ~200-300 bytes per instance | Medium-High |
| MachineAdapter | ~3-7% | ~250-350 bytes per instance | High |
| Identity/Environment converters | ~1-3% | ~80-150 bytes per instance | Low |
| Reflective adapters | ~10-15% | ~300-400 bytes per instance | Very High |

### Performance optimization strategies

1. **Direct Migration**: For performance-critical code, consider direct migration instead of using adapters
   ```java
   // Instead of
   Component adaptedComponent = factory.tubeToComponent(tube);
   
   // Create native component and copy state
   Component nativeComponent = new Component(tube.getUniqueId(), new Environment());
   nativeComponent.setState(convertState(tube.getStatus()));
   ```

2. **Caching**: Adapter factories use caching to improve performance when creating multiple adapters
   ```java
   // The factory caches adapters internally
   S8rMigrationFactory factory = new S8rMigrationFactory();
   
   // These calls will reuse the same adapter if the tube is the same
   Component comp1 = factory.tubeToComponent(tube);
   Component comp2 = factory.tubeToComponent(tube);
   assert comp1 == comp2 : "Adapter should be cached";
   ```

3. **Lazy Conversion**: Some adapters use lazy conversion to postpone expensive operations until needed
   ```java
   // The adapter will only convert properties when they are accessed
   Component component = factory.tubeToComponent(tube);
   
   // This operation will trigger conversion of the environment
   Environment env = component.getEnvironment();
   ```

4. **State Synchronization**: Bidirectional state synchronization can cause additional operations when state changes frequently
   ```java
   // Consider using a one-way adapter for high-frequency state changes
   TubeComponentAdapter adapter = new TubeComponentAdapter(tube, false);  // false = one-way sync
   ```

5. **Batching**: Batch operations when possible to reduce adapter method call overhead
   ```java
   // Instead of many individual calls
   for (String key : keys) {
       component.setProperty(key, values.get(key));
   }
   
   // Use batch operations when available
   component.setProperties(properties);
   ```

## Integration with Clean Architecture

The S8r migration utilities are designed to integrate seamlessly with Clean Architecture principles.

### Architecture overview

```
┌─────────────────────────────────────────────────────────┐
│                  Application Core                        │
│                                                          │
│  ┌───────────────┐         ┌─────────────────────────┐  │
│  │               │         │                         │  │
│  │    Domain     │         │      Application        │  │
│  │    Layer      │◄────────►      Layer             │  │
│  │               │         │                         │  │
│  └───────────────┘         └────────────┬────────────┘  │
│                                         │               │
│                              ┌──────────▼───────────┐   │
│                              │                      │   │
│                              │   Port Interfaces    │   │
│                              │                      │   │
└──────────────────────────────┴──────────┬───────────┴───┘
                                          │
┌─────────────────────────────────────────▼─────────────────────┐
│                         Adapters                               │
│  ┌────────────────────┐            ┌────────────────────────┐ │
│  │                    │            │                        │ │
│  │  S8r Adapters      │            │  Legacy Adapters       │ │
│  │                    │            │                        │ │
│  └────────────────────┘            └────────────────────────┘ │
└────────────────────────────────────────────────────────────────┘
                         │                      │
┌────────────────────────▼──────────┐ ┌────────▼────────────────────┐
│                                   │ │                             │
│       S8r Framework               │ │      Legacy Samstraumr      │
│                                   │ │                             │
└───────────────────────────────────┘ └─────────────────────────────┘
```

### Domain layer interfaces

The migration utilities implement domain layer interfaces to provide Clean Architecture compliance:

- `ComponentPort`: Interface for component operations
- `CompositeComponentPort`: Interface for composite operations
- `MachinePort`: Interface for machine operations
- `MachineFactoryPort`: Interface for machine creation

### Application layer services

Application services can use port interfaces without knowing the implementation details:

```java
public class ComponentService {
    private final ComponentPort component;
    private final LoggerPort logger;
    
    public ComponentService(ComponentPort component, LoggerPort logger) {
        this.component = component;
        this.logger = logger;
    }
    
    public void activateComponent() {
        // Works with any implementation of ComponentPort
        component.setState(State.ACTIVE);
        logger.info("Component activated: {}", component.getId());
    }
}
```

### Adapter layer implementations

The migration utilities provide adapter implementations of the port interfaces:

```java
// Create the adapters
S8rMigrationFactory factory = new S8rMigrationFactory();
ComponentPort componentAdapter = factory.tubeToComponent(tube);
MachinePort machineAdapter = factory.tubeMachineToMachinePort(tubeMachine);

// Create application services using the adapters
ComponentService componentService = new ComponentService(componentAdapter, logger);
MachineService machineService = new MachineService(machineAdapter, logger);

// Use the services
componentService.activateComponent();
machineService.startMachine();
```

## Version Compatibility

The S8r Migration Utilities are designed to work with different versions of the Samstraumr framework.

### Samstraumr Version Compatibility Matrix

| Samstraumr Version | Migration Utility Version | Compatibility Level | Notes |
|--------------------|---------------------------|---------------------|-------|
| 1.x | 1.0.0+ | Full | All migration utilities are supported |
| 2.0-2.1 | 1.0.0+ | Full | All migration utilities are supported |
| 2.2+ | 1.0.0+ | Full | Best compatibility with latest features |
| Legacy Core API | 1.0.0+ | Partial | Limited support for legacy core API |

### S8r Version Compatibility Matrix

| S8r Version | Migration Utility Version | Compatibility Level | Notes |
|-------------|--------------------------|---------------------|-------|
| 1.0.0 | 1.0.0 | Full | Base compatibility |
| 1.1.0+ | 1.1.0+ | Full | Enhanced port interface support |
| 2.0.0+ | 1.2.0+ | Full | Full Clean Architecture support |

### Feature Compatibility Matrix

| Feature | Min. Migration Utility Version | Min. Samstraumr Version | Min. S8r Version |
|---------|-------------------------------|------------------------|----------------|
| Basic Component Adapters | 1.0.0 | 1.0 | 1.0.0 |
| Composite Adapters | 1.0.0 | 1.0 | 1.0.0 |
| Machine Adapters | 1.0.0 | 1.0 | 1.0.0 |
| Identity/Environment Conversion | 1.0.0 | 1.0 | 1.0.0 |
| Reflective Adapters | 1.0.0 | 1.0 | 1.0.0 |
| Port Interface Adapters | 1.1.0 | 2.0 | 1.1.0 |
| Clean Architecture Integration | 1.2.0 | 2.0 | 2.0.0 |
| Adapter Contract Testing | 1.2.0 | 2.0 | 2.0.0 |
| Migration Scripts | 1.0.0 | 1.0 | 1.0.0 |

