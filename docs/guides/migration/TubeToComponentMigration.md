<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Migrating from Tube to Component

This guide helps you migrate from the legacy Tube-based architecture to the new Component-based structure in the S8r framework. The new structure offers improved simplicity, better organization, and a more intuitive API.

## Overview of Changes

The refactoring changes several key aspects of the codebase:

1. **Package Structure**: Reduces depth from 6+ levels to a maximum of 4 levels
2. **Naming Simplification**: Replaces tube-specific terminology with standard component terms
3. **File Consolidation**: Combines related functionality into fewer, more cohesive files
4. **API Improvements**: Provides a more intuitive and consistent API surface

## Package Mapping

|         Legacy Package          |              New Package              |
|---------------------------------|---------------------------------------|
| `org.samstraumr.tube`           | `org.s8r.component.core`              |
| `org.samstraumr.tube.lifecycle` | `org.s8r.component.core` (State enum) |
| `org.samstraumr.tube.identity`  | `org.s8r.component.identity`          |
| `org.samstraumr.tube.composite` | `org.s8r.component.composite`         |
| `org.samstraumr.tube.machine`   | `org.s8r.component.machine`           |
| `org.samstraumr.tube.exception` | `org.s8r.component.exception`         |

## Class Mapping

|            Legacy Class             |     New Class      |                   Notes                    |
|-------------------------------------|--------------------|--------------------------------------------|
| `Tube`                              | `Component`        | Core functionality with enhanced API       |
| `TubeStatus` & `TubeLifecycleState` | `State`            | Combined into unified enum with categories |
| `TubeLogger` & `TubeLoggerInfo`     | `Logger`           | LoggerInfo now an inner class of Logger    |
| `TubeIdentity`                      | `Identity`         | Enhanced with better lineage tracking      |
| `Environment`                       | `Environment`      | Enhanced with improved detection           |
| `Composite`                         | `Composite`        | Updated to work with Components            |
| `CompositeFactory`                  | `CompositeFactory` | Updated factory methods                    |
| `Machine`                           | `Machine`          | Updated to work with new Composites        |
| `MachineFactory`                    | `MachineFactory`   | Updated factory methods                    |

## API Changes

### Creating Components (formerly Tubes)

**Legacy**:

```java
// Create tube directly
Tube tube = Tube.create("my reason", environment);

// Create child tube
Tube child = Tube.create("child reason", environment);
tube.registerChild(child);
```

**New**:

```java
// Create component directly
Component component = Component.create("my reason", environment);

// Create child component (simplified API)
Component child = Component.createChild("child reason", environment, component);
// No need to manually register - parent-child relationship established automatically
```

### Working with State (formerly Status & LifecycleState)

**Legacy**:

```java
// Check tube status
if (tube.getStatus() == TubeStatus.READY) {
    // ...
}

// Check lifecycle state
if (tube.getLifecycleState() == TubeLifecycleState.CONCEPTION) {
    // ...
}
```

**New**:

```java
// Unified state with categories
if (component.getState() == State.READY) {
    // ...
}

// Check state category
if (component.getState().isLifecycle()) {
    // ...
}

// Rich metadata in states
String description = component.getState().getDescription();
String biologicalAnalog = component.getState().getBiologicalAnalog();
```

### Logging

**Legacy**:

```java
// Create logger and logger info separately
TubeLoggerInfo loggerInfo = new TubeLoggerInfo(tube.getUniqueId());
TubeLogger logger = new TubeLogger(loggerInfo);

// Log with tags
logger.info("Message", "TAG1", "TAG2");
```

**New**:

```java
// Simplified logger creation
Logger logger = new Logger(component.getUniqueId());

// Same logging API
logger.info("Message", "TAG1", "TAG2");

// Enhanced context logging
Map<String, Object> context = new HashMap<>();
context.put("key", "value");
logger.logWithContext("info", "Message with context", context, "TAG1");
```

### Composites

**Legacy**:

```java
// Create and add tubes
Composite composite = new Composite("compositeId", environment);
composite.addTube("name", tube);
composite.connect("source", "target");
```

**New**:

```java
// Create and add components
Composite composite = new Composite("compositeId", environment);
composite.addComponent("name", component);
composite.connect("source", "target");
```

## Migration Strategy

1. **Start with Core Components**:
   - Replace `Tube` usage with `Component`
   - Update state management to use unified `State` enum
   - Update logging to use new `Logger` class
2. **Update Composite Usage**:
   - Replace tube-based composites with component-based composites
   - Update factory method calls
3. **Update Machine Implementation**:
   - Replace composite references with the new composite implementation
   - Update factory method calls
4. **Update Tests**:
   - Update test implementations to use new classes
   - Use the new tag structure for test categorization

## Example Migration

### Before:

```java
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.TubeStatus;
import org.samstraumr.tube.composite.Composite;

// Create environment
Environment env = new Environment();

// Create tube
Tube tube = Tube.create("Process data", env);

// Check status
if (tube.getStatus() == TubeStatus.READY) {
    // Create composite
    Composite composite = new Composite("data-flow", env);
    composite.addTube("processor", tube);
    
    // Process data
    composite.process("processor", inputData);
}
```

### After:

```java
import org.s8r.component.core.Component;
import org.s8r.component.core.Environment;
import org.s8r.component.core.State;
import org.s8r.component.composite.Composite;

// Create environment
Environment env = new Environment();

// Create component
Component component = Component.create("Process data", env);

// Check state
if (component.getState() == State.READY) {
    // Create composite
    Composite composite = new Composite("data-flow", env);
    composite.addComponent("processor", component);
    
    // Process data
    composite.process("processor", inputData);
}
```

## Testing Considerations

The new structure includes a comprehensive BDD-based test suite that follows the same patterns as the legacy tests but with improved organization:

- Feature files in `component/features/L0_Core/`
- Step definitions in `org.s8r.component.test.steps`
- Test runners in `org.s8r.component.test`

To run component tests:

```bash
mvn test -Dtest=RunComponentTests
```

To run only critical ATL component tests:

```bash
mvn test -Dtest=RunATLComponentTests
```

## Getting Help

If you encounter any issues during migration, please:

1. Check the comprehensive class documentation in Javadoc
2. Refer to the updated examples in the `/docs/guides/examples/` directory
3. Consult the architecture documentation in `/docs/architecture/`

For any questions or assistance, please contact the framework maintainers.
