<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Adapter Reference

This document provides a technical reference for all adapter classes in the S8r framework to support the migration from Samstraumr to S8r.

## Core Adapters

### Componentadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts a domain component to the `ComponentPort` interface.

**Key Methods:**
- `getId()`: Returns the component ID
- `getName()`: Returns the component name
- `getState()`: Returns the component state
- `getType()`: Returns the component type
- `getEnvironment()`: Returns the component environment
- `setState(State)`: Updates the component state
- `accept(ComponentVisitor)`: Accepts a visitor for the component

**Usage Example:**
```java
ComponentAdapter adapter = new ComponentAdapter(domainComponent);
ComponentPort port = adapter;
applicationService.processComponent(port);
```

### Tubecomponentadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts a legacy Tube to the S8r Component interface.

**Key Methods:**
- `getId()`: Returns the component ID derived from tube ID
- `getName()`: Returns the component name derived from tube name
- `getState()`: Returns the component state mapped from tube status/lifecycle state
- `getEnvironment()`: Returns the component environment derived from tube environment
- `setState(State)`: Updates the tube status based on the component state
- `getLogger()`: Returns a component logger that delegates to the tube logger

**Bidirectional Synchronization:**
- Tube status changes are reflected in component state
- Component state changes are reflected in tube status
- Environment changes are synchronized between tube and component

**Usage Example:**
```java
TubeComponentAdapter adapter = new TubeComponentAdapter(tube);
Component component = adapter;
component.setState(State.ACTIVE);
// Tube status is now ACTIVE
```

### Legacycomponentadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts legacy component types to the S8r Component interface.

**Key Methods:**
- `getId()`: Returns the component ID derived from legacy ID
- `getName()`: Returns the component name derived from legacy name
- `getState()`: Returns the component state mapped from legacy state
- `getEnvironment()`: Returns the component environment derived from legacy environment
- `setState(State)`: Updates the legacy state based on the component state

**Supported Legacy Types:**
- `org.samstraumr.legacy.Component`
- `org.samstraumr.legacy.v1.Component`
- `org.samstraumr.legacy.v2.Component`

**Usage Example:**
```java
LegacyComponentAdapter adapter = new LegacyComponentAdapter(legacyComponent);
Component component = adapter;
```

### Compositeadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts a legacy Composite to the S8r Composite interface.

**Key Methods:**
- `getId()`: Returns the composite ID derived from legacy composite ID
- `getName()`: Returns the composite name derived from legacy composite name
- `getState()`: Returns the composite state mapped from legacy composite state
- `getEnvironment()`: Returns the composite environment derived from legacy composite environment
- `addComponent(Component)`: Adds a component to the composite, converting to tube if needed
- `removeComponent(Component)`: Removes a component from the composite
- `getComponents()`: Returns all components in the composite, converting tubes to components

**Bidirectional Synchronization:**
- Components added to S8r composite are added to legacy composite as tubes
- Tubes added to legacy composite are exposed as components in S8r composite
- State changes are synchronized between legacy and S8r composites

**Usage Example:**
```java
CompositeAdapter adapter = new CompositeAdapter(legacyComposite);
Composite composite = adapter;
composite.addComponent(component);
// Component is added to legacy composite as a tube
```

### Machineadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts a legacy Machine to the S8r Machine interface.

**Key Methods:**
- `getId()`: Returns the machine ID derived from legacy machine ID
- `getName()`: Returns the machine name derived from legacy machine name
- `getState()`: Returns the machine state mapped from legacy machine state
- `getEnvironment()`: Returns the machine environment derived from legacy machine environment
- `registerComposite(Composite)`: Registers a composite with the machine, converting to legacy composite if needed
- `unregisterComposite(Composite)`: Unregisters a composite from the machine
- `getComposites()`: Returns all composites in the machine, converting legacy composites to S8r composites

**Bidirectional Synchronization:**
- Composites registered with S8r machine are registered with legacy machine
- Composites registered with legacy machine are exposed in S8r machine
- State changes are synchronized between legacy and S8r machines

**Usage Example:**
```java
MachineAdapter adapter = new MachineAdapter(legacyMachine);
Machine machine = adapter;
machine.registerComposite(composite);
// Composite is registered with legacy machine
```

### Machinefactoryadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts a legacy MachineFactory to the S8r MachineFactory interface.

**Key Methods:**
- `create(String, Environment)`: Creates a new machine, adapting the environment if needed
- `createWithProperties(String, Environment, Map<String, Object>)`: Creates a new machine with properties

**Usage Example:**
```java
MachineFactoryAdapter adapter = new MachineFactoryAdapter(legacyMachineFactory);
MachineFactory factory = adapter;
Machine machine = factory.create("test", environment);
```

## Identity and Environment Adapters

### Identityadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts legacy identity objects to the S8r Identity interface.

**Key Methods:**
- `getId()`: Returns the ID string
- `getShortId()`: Returns a shortened ID for display
- `getType()`: Returns the identity type
- `getNamespace()`: Returns the identity namespace
- `equals(Object)`: Compares identities for equality

**Usage Example:**
```java
IdentityAdapter adapter = new IdentityAdapter(legacyIdentity);
Identity identity = adapter;
String id = identity.getIdString();
```

### Legacyidentityadapter

**Package:** `org.s8r.adapter`

**Purpose:** Adapts S8r Identity objects to legacy identity interfaces.

**Key Methods:**
- `getId()`: Returns the ID string
- `getShortId()`: Returns a shortened ID for display
- `getType()`: Returns the identity type
- `getNamespace()`: Returns the identity namespace

**Supported Legacy Interfaces:**
- `org.samstraumr.tube.TubeIdentity`
- `org.samstraumr.legacy.identity.Identity`

**Usage Example:**
```java
LegacyIdentityAdapter adapter = new LegacyIdentityAdapter(s8rIdentity);
TubeIdentity tubeIdentity = adapter;
```

## Converter Classes

### Tubelegacyidentityconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between tube identity objects and S8r identity objects.

**Key Methods:**
- `convert(TubeIdentity)`: Converts a tube identity to an S8r identity
- `reverseConvert(Identity)`: Converts an S8r identity to a tube identity

**Usage Example:**
```java
TubeLegacyIdentityConverter converter = new TubeLegacyIdentityConverter();
Identity identity = converter.convert(tubeIdentity);
```

### Tubelegacyenvironmentconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between tube environment objects and S8r environment objects.

**Key Methods:**
- `convert(org.samstraumr.tube.Environment)`: Converts a tube environment to an S8r environment
- `reverseConvert(Environment)`: Converts an S8r environment to a tube environment

**Usage Example:**
```java
TubeLegacyEnvironmentConverter converter = new TubeLegacyEnvironmentConverter();
Environment environment = converter.convert(tubeEnvironment);
```

### Corelegacyidentityconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between core legacy identity objects and S8r identity objects.

**Key Methods:**
- `convert(org.samstraumr.core.Identity)`: Converts a core legacy identity to an S8r identity
- `reverseConvert(Identity)`: Converts an S8r identity to a core legacy identity

**Usage Example:**
```java
CoreLegacyIdentityConverter converter = new CoreLegacyIdentityConverter();
Identity identity = converter.convert(legacyIdentity);
```

### Corelegacyenvironmentconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between core legacy environment objects and S8r environment objects.

**Key Methods:**
- `convert(org.samstraumr.core.Environment)`: Converts a core legacy environment to an S8r environment
- `reverseConvert(Environment)`: Converts an S8r environment to a core legacy environment

**Usage Example:**
```java
CoreLegacyEnvironmentConverter converter = new CoreLegacyEnvironmentConverter();
Environment environment = converter.convert(legacyEnvironment);
```

## Reflective Adapters

### Reflectiveadapterfactory

**Package:** `org.s8r.adapter`

**Purpose:** Creates adapters for arbitrary legacy objects using reflection.

**Key Methods:**
- `createAdapter(Object)`: Creates an appropriate adapter for the given object
- `registerAdapterClass(Class<?>, Class<?>)`: Registers a custom adapter class for a legacy class
- `unregisterAdapterClass(Class<?>)`: Unregisters a custom adapter class

**Supported Types:**
- `org.samstraumr.tube.Tube` → `TubeComponentAdapter`
- `org.samstraumr.tube.composite.Composite` → `CompositeAdapter`
- `org.samstraumr.tube.machine.Machine` → `MachineAdapter`
- `org.samstraumr.legacy.Component` → `LegacyComponentAdapter`

**Usage Example:**
```java
ReflectiveAdapterFactory factory = new ReflectiveAdapterFactory();
Component component = factory.createAdapter(legacyObject);
```

### Reflectiveidentityconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between arbitrary identity objects using reflection.

**Key Methods:**
- `convert(Object)`: Converts an arbitrary identity object to an S8r identity
- `registerConverter(Class<?>, LegacyIdentityConverter<?>)`: Registers a custom converter for a legacy class
- `unregisterConverter(Class<?>)`: Unregisters a custom converter

**Usage Example:**
```java
ReflectiveIdentityConverter converter = new ReflectiveIdentityConverter();
Identity identity = converter.convert(legacyIdentity);
```

### Reflectiveenvironmentconverter

**Package:** `org.s8r.adapter`

**Purpose:** Converts between arbitrary environment objects using reflection.

**Key Methods:**
- `convert(Object)`: Converts an arbitrary environment object to an S8r environment
- `registerConverter(Class<?>, LegacyEnvironmentConverter<?>)`: Registers a custom converter for a legacy class
- `unregisterConverter(Class<?>)`: Unregisters a custom converter

**Usage Example:**
```java
ReflectiveEnvironmentConverter converter = new ReflectiveEnvironmentConverter();
Environment environment = converter.convert(legacyEnvironment);
```

## Wrapper Classes

### Tubecomponentwrapper

**Package:** `org.s8r.adapter`

**Purpose:** Wraps an S8r Component as a legacy Tube.

**Key Methods:**
- `getIdentity()`: Returns the tube identity derived from component ID
- `getStatus()`: Returns the tube status derived from component state
- `getLifecycleState()`: Returns the tube lifecycle state derived from component state
- `getEnvironment()`: Returns the tube environment derived from component environment
- `setStatus(TubeStatus)`: Updates the component state based on the tube status

**Usage Example:**
```java
TubeComponentWrapper wrapper = new TubeComponentWrapper(component);
Tube tube = wrapper;
```

### Legacycomponentwrapper

**Package:** `org.s8r.adapter`

**Purpose:** Wraps an S8r Component as a legacy component.

**Key Methods:**
- `getIdentity()`: Returns the legacy identity derived from component ID
- `getStatus()`: Returns the legacy status derived from component state
- `getEnvironment()`: Returns the legacy environment derived from component environment
- `setStatus(LegacyStatus)`: Updates the component state based on the legacy status

**Usage Example:**
```java
LegacyComponentWrapper wrapper = new LegacyComponentWrapper(component);
org.samstraumr.legacy.Component legacyComponent = wrapper;
```

## S8rMigrationFactory

**Package:** `org.s8r.adapter`

**Purpose:** Central factory for creating adapters and wrappers for migration.

**Key Methods:**
- `tubeToComponent(Tube)`: Converts a tube to a component
- `wrapTubeComposite(org.samstraumr.tube.composite.Composite)`: Wraps a tube composite as an S8r composite
- `wrapTubeMachine(org.samstraumr.tube.machine.Machine)`: Wraps a tube machine as an S8r machine
- `componentToTube(Component)`: Converts a component to a tube
- `createIdentityAdapter(org.s8r.domain.identity.ComponentId)`: Creates an adapter for component identity
- `createReflectiveAdapter(Object)`: Creates a reflective adapter for an arbitrary object

**Usage Example:**
```java
S8rMigrationFactory factory = new S8rMigrationFactory();
Component component = factory.tubeToComponent(tube);
```

## Performance Characteristics

| Adapter Class | Overhead | Memory Usage | Thread Safety |
|---------------|----------|--------------|--------------|
| ComponentAdapter | Low | Low | Thread-safe |
| TubeComponentAdapter | Medium | Medium | Thread-safe |
| CompositeAdapter | Medium | Medium-High | Thread-safe |
| MachineAdapter | Medium | Medium-High | Thread-safe |
| IdentityAdapter | Very Low | Very Low | Immutable |
| ReflectiveAdapterFactory | High (first use) | Medium | Thread-safe with synchronization |

## Implementation Notes

- All adapters implement proper `equals()` and `hashCode()` methods
- Adapters cache converted objects when possible to improve performance
- Bidirectional synchronization is performed through listeners and observers
- Adapters maintain the original object identity during runtime
- Thread safety is ensured through synchronization where needed

## Legacy API Compatibility Matrix

| Legacy API Version | Compatible Adapters | Notes |
|--------------------|---------------------|-------|
| Samstraumr 1.x | TubeComponentAdapter, CompositeAdapter, MachineAdapter | Full compatibility |
| Samstraumr 2.0-2.1 | TubeComponentAdapter, CompositeAdapter, MachineAdapter | Full compatibility |
| Samstraumr 2.2+ | All adapters | Full compatibility |
| Core Legacy API | CoreLegacyIdentityConverter, CoreLegacyEnvironmentConverter | Limited compatibility |

## Exception Handling

Adapters employ the following exception handling strategies:

- `IllegalArgumentException`: Thrown when input objects are null or invalid
- `UnsupportedOperationException`: Thrown when a legacy method cannot be mapped to the new API
- `ClassCastException`: May be thrown when using the wrong adapter type
