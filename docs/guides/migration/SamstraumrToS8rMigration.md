<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Migrating from Samstraumr to S8r

This guide provides detailed instructions for migrating client code from Samstraumr (tube-based) to S8r (component-based).

## Overview

Version ${samstraumr.version} introduces significant changes to the project structure:

- Project name changes from Samstraumr to S8r
- Tube-based terminology changes to Component-based terminology
- Package reorganization and significant depth reduction
- Maven coordinates update
- Consolidated state management with a unified State enum

## Dependency Updates

### Maven

Replace your dependencies in pom.xml:

**Old:**

```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>${previous.version}</version>
</dependency>
```

**New:**

```xml
<dependency>
    <groupId>org.s8r</groupId>
    <artifactId>s8r-core</artifactId>
    <version>${samstraumr.version}</version>
</dependency>
```

### Gradle

**Old:**

```groovy
implementation 'org.samstraumr:samstraumr-core:${previous.version}'
```

**New:**

```groovy
implementation 'org.s8r:s8r-core:${samstraumr.version}'
```

## Import Updates

Update your imports according to this mapping:

|                         Old Import                          |                    New Import                    |
|-------------------------------------------------------------|--------------------------------------------------|
| `org.samstraumr.tube.Tube`                                  | `org.s8r.component.core.Component`               |
| `org.samstraumr.tube.TubeStatus`                            | `org.s8r.component.core.State`                   |
| `org.samstraumr.tube.TubeLifecycleState`                    | `org.s8r.component.core.State`                   |
| `org.samstraumr.tube.TubeIdentity`                          | `org.s8r.component.identity.Identity`            |
| `org.samstraumr.tube.Environment`                           | `org.s8r.component.core.Environment`             |
| `org.samstraumr.tube.TubeLogger`                            | `org.s8r.component.logging.Logger`               |
| `org.samstraumr.tube.TubeLoggerInfo`                        | `org.s8r.component.logging.Logger.LoggerInfo`    |
| `org.samstraumr.tube.composite.Composite`                   | `org.s8r.component.composite.Composite`          |
| `org.samstraumr.tube.machine.Machine`                       | `org.s8r.component.machine.Machine`              |
| `org.samstraumr.tube.exception.TubeInitializationException` | `org.s8r.component.exception.ComponentException` |

## API Changes

### Creation

**Old:**

```java
Tube tube = Tube.create("reason", env);
```

**New:**

```java
Component component = Component.create("reason", env);
```

### Child Creation

**Old:**

```java
Tube child = Tube.createChild("reason", env, parentTube);
```

**New:**

```java
Component child = Component.createChild("reason", env, parentComponent);
```

### Status/State Checking

**Old:**

```java
if (tube.getStatus() == TubeStatus.READY) {
    // Do something
}

if (tube.getLifecycleState() == TubeLifecycleState.CHILDHOOD) {
    // Do something
}
```

**New:**

```java
if (component.getState() == State.READY) {
    // Do something
}
```

### Environment Interaction

**Old:**

```java
tube.updateEnvironmentStatus("degraded");
```

**New:**

```java
component.updateEnvironmentState("degraded");
```

### Logging

**Old:**

```java
tube.getLogger().info("Message", "TAG1", "TAG2");
```

**New:**

```java
component.getLogger().info("Message", "TAG1", "TAG2");
```

## State Mapping

The old separate concepts of TubeStatus and TubeLifecycleState are now unified in a single State enum:

|        Old Status/State         |          New State          |
|---------------------------------|-----------------------------|
| `TubeStatus.CREATED`            | `State.CONCEPTION`          |
| `TubeStatus.INITIALIZING`       | `State.INITIALIZING`        |
| `TubeStatus.READY`              | `State.READY`               |
| `TubeStatus.ACTIVE`             | `State.ACTIVE`              |
| `TubeStatus.DEGRADED`           | `State.DEGRADED`            |
| `TubeStatus.TERMINATING`        | `State.TERMINATING`         |
| `TubeStatus.TERMINATED`         | `State.TERMINATED`          |
| `TubeLifecycleState.CONCEPTION` | `State.CONCEPTION`          |
| `TubeLifecycleState.EMBRYONIC`  | `State.CONFIGURING`         |
| `TubeLifecycleState.INFANCY`    | `State.SPECIALIZING`        |
| `TubeLifecycleState.CHILDHOOD`  | `State.DEVELOPING_FEATURES` |

## Composite Changes

**Old:**

```java
Composite composite = CompositeFactory.create("reason", env);
composite.addTube(tube);
```

**New:**

```java
Composite composite = CompositeFactory.create("reason", env);
composite.addComponent(component);
```

## Machine Changes

**Old:**

```java
Machine machine = MachineFactory.create("reason", env);
machine.registerComposite(composite);
```

**New:**

```java
Machine machine = MachineFactory.create("reason", env);
machine.registerComposite(composite);
```

## Testing Migration

If you have tests using Samstraumr annotations, update them:

| Old Annotation |       New Annotation       |
|----------------|----------------------------|
| `@TubeTest`    | `@ComponentTest`           |
| `@BundleTest`  | `@CompositeTest`           |
| `@MachineTest` | `@MachineTest` (unchanged) |

## Common Migration Patterns

### Creating Components

```java
// Old
Environment env = new Environment("test", params);
Tube tube = Tube.create("testing", env);

// New
Environment env = new Environment("test", params);
Component component = Component.create("testing", env);
```

### Component Hierarchies

```java
// Old
Tube parent = Tube.create("parent", env);
Tube child1 = Tube.createChild("child1", env, parent);
Tube child2 = Tube.createChild("child2", env, parent);

// New
Component parent = Component.create("parent", env);
Component child1 = Component.createChild("child1", env, parent);
Component child2 = Component.createChild("child2", env, parent);
```

### Working with State

```java
// Old
tube.setStatus(TubeStatus.ACTIVE);
boolean isTerminated = tube.isTerminated();

// New
component.setState(State.ACTIVE);
boolean isTerminated = component.isTerminated();
```

## Migration Tool

A migration assistant script is available to help with the conversion:

```bash
./util/migrate-code.sh path/to/your/code
```

This script will:
1. Scan your Java files for Samstraumr imports and API usage
2. Create a backup of your code
3. Perform the necessary replacements
4. Generate a report of changes made

## Need Help?

If you encounter issues during migration, please:
1. Check the API documentation in the `docs/api` directory
2. Refer to the example code in `examples/migration-examples`
3. Open an issue on GitHub for specific migration challenges
