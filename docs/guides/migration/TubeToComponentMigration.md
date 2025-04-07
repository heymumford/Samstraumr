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

## Migration Utilities

To assist with migration, S8r provides a comprehensive set of migration utilities in the `org.s8r.adapter` package. These utilities make it easier to transition from legacy code to the new architecture without breaking existing functionality.

### S8rMigrationFactory

The `S8rMigrationFactory` is the main entry point for using migration utilities:

```java
import org.s8r.adapter.S8rMigrationFactory;
import org.s8r.domain.component.Component;
import org.s8r.tube.Tube;
import org.s8r.tube.Environment;

// Create the migration factory
S8rMigrationFactory migrationFactory = new S8rMigrationFactory();
```

### Converting Existing Tubes to Components

You can wrap your existing Tube instances to use them with new Component-based code:

```java
// Existing Tube
Environment env = new Environment();
env.setParameter("name", "MyTube");
Tube tube = Tube.create("Processing data", env);

// Convert to Component
Component component = migrationFactory.tubeToComponent(tube);

// Now you can use this Component with new Component-based code
component.activate();

// If you need to get back the original Tube
Tube originalTube = migrationFactory.extractTube(component);
```

### Creating New Components backed by Tubes

For a gradual migration, you can create new components that use the legacy Tube implementation:

```java
// Create a new Component backed by a Tube
Environment env = new Environment();
env.setParameter("name", "NewComponent");
Component component = migrationFactory.createTubeComponent("Analyzing data", env);
```

### Converting Between Environment Types

```java
// Convert Tube Environment to S8r Environment
Environment tubeEnv = new Environment();
tubeEnv.setParameter("setting1", "value1");
org.s8r.component.core.Environment s8rEnv = migrationFactory.tubeEnvironmentToS8rEnvironment(tubeEnv);
```

## Migration Strategy

The migration strategy follows the Strangler Fig pattern, allowing you to gradually replace parts of the system while maintaining compatibility with existing code.

1. **Use Migration Utilities First**:
   - Wrap existing Tube instances with TubeComponentWrappers using the S8rMigrationFactory
   - This allows gradual migration without breaking existing functionality
   - Use the wrapped API to ensure compatibility during transition

2. **Start with Core Components**:
   - Replace `Tube` usage with `Component`
   - Update state management to use unified `State` enum
   - Update logging to use new `Logger` class
   - Test thoroughly to ensure behavior is preserved

3. **Update Composite Usage**:
   - Replace tube-based composites with component-based composites
   - Use the CompositeAdapter to create wrappers or convert legacy composites
   - Update factory method calls and composite creation patterns
   - Connect components using the new API

4. **Update Machine Implementation**:
   - Use MachineAdapter to wrap or convert legacy machines
   - Maintain connection topology during migration
   - Ensure state changes propagate properly between systems
   - Gradually replace wrapped machines with native implementations
   - Use machine-level wrappers for complex migrations

5. **Update Tests**:
   - Update test implementations to use new classes
   - Use the new tag structure for test categorization
   - Ensure test coverage across both legacy and new code
   - Add tests specific to the migration utilities

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

### During Migration (using utilities):

```java
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.s8r.adapter.S8rMigrationFactory;
import org.s8r.domain.component.Component;
import org.s8r.component.composite.Composite;
import org.s8r.component.core.State;
import org.s8r.tube.composite.Composite as TubeComposite;

// Create migration factory
S8rMigrationFactory migrationFactory = new S8rMigrationFactory();

// Use existing tube code
Environment env = new Environment();
Tube tube = Tube.create("Process data", env);

// Convert to Component 
Component component = migrationFactory.tubeToComponent(tube);

// Use new Component-based APIs
if (component.getState() == State.READY) {
    // Create composite with new Component API
    Composite composite = new Composite("data-flow", 
        migrationFactory.tubeEnvironmentToS8rEnvironment(env));
    composite.addComponent("processor", component);
    
    // Process data with new API
    composite.process("processor", inputData);
}

// If you need to access the original Tube from the component
Tube originalTube = migrationFactory.extractTube(component);

// ---- Working with Composites ----

// Option 1: Convert an existing Tube composite to a Component composite
TubeComposite tubeComposite = new TubeComposite("legacy-flow", env);
tubeComposite.addTube("input", Tube.create("Input", env));
tubeComposite.addTube("process", Tube.create("Process", env));
tubeComposite.connect("input", "process");

// Convert the entire composite
Composite convertedComposite = migrationFactory.tubeCompositeToComponentComposite(tubeComposite);

// Option 2: Wrap a Tube composite to use with Component APIs
Composite wrappedComposite = migrationFactory.wrapTubeComposite(tubeComposite);

// Option 3: Create a hybrid composite with both Tubes and Components
Composite hybridComposite = migrationFactory.createHybridComposite("hybrid", env);

// Add a tube from the legacy composite to the hybrid
migrationFactory.addTubeToComponentComposite(
    tubeComposite, "process", hybridComposite, "legacyProcessor");

// Add a native component
hybridComposite.addComponent(
    "newComponent", 
    Component.create("New component", migrationFactory.tubeEnvironmentToS8rEnvironment(env)));

// Connect legacy and new
hybridComposite.connect("legacyProcessor", "newComponent");

// ---- Working with Machines ----

// Option 1: Convert an existing Tube machine to a Component machine
import org.s8r.tube.machine.Machine as TubeMachine;
import org.s8r.component.Machine;

// Create a tube machine
TubeMachine tubeMachine = new TubeMachine("legacy-machine", env);
tubeMachine.addComposite("flow", tubeComposite);

// Convert the entire machine
Machine convertedMachine = migrationFactory.tubeMachineToComponentMachine(tubeMachine);
// This creates a completely new Machine with all composites and connections preserved

// Option 2: Wrap a Tube machine to use with Component APIs
Machine wrappedMachine = migrationFactory.wrapTubeMachine(tubeMachine);

// Use the machine with new API
wrappedMachine.addComposite("newFlow", convertedComposite);
wrappedMachine.connect("flow", "newFlow");

// State changes propagate in both directions
wrappedMachine.updateState("machineStatus", "processing");
// The underlying tubeMachine is updated too
assert tubeMachine.getState().get("machineStatus").equals("processing");

// Lifecycle management works bidirectionally
wrappedMachine.deactivate();
assert !tubeMachine.isActive(); // tube machine gets deactivated

tubeMachine.activate();
assert wrappedMachine.isActive(); // wrapper reflects activation

// Shutdown also propagates
wrappedMachine.shutdown();
assert !tubeMachine.isActive(); // tube machine gets shut down properly
```

### After (fully migrated):

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
