<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Machine Component

The Machine component represents the highest level of composition in the Samstraumr architecture.
It manages and orchestrates multiple Composite components to form a complete processing system.

## Key Components

- **Machine**: The main class that represents a machine, managing multiple composites.
- **MachineFactory**: A factory for creating preconfigured machines for common patterns.

## Architecture Overview

In the Samstraumr framework, the architectural levels are:

1. **Tube**: The atomic unit of processing, handling discrete operations.
2. **Composite**: A collection of connected tubes that work together to achieve a specific goal.
3. **Machine**: A high-level orchestrator that manages multiple composites to implement complex workflows.

## Usage

Machines can be created either directly or using the factory:

```java
// Create via factory
Machine machine = MachineFactory.createDataProcessingMachine(environment);

// Create directly
Machine customMachine = new Machine("custom-machine", environment);
customMachine.createComposite("input", "Data input processing");
customMachine.createComposite("processing", "Main data processing");
customMachine.connect("input", "processing");
```

## State Management

Machines provide a unified view of the state of all their components:

```java
// Get machine state
Map<String, Object> state = machine.getState();

// Update machine state
machine.updateState("status", "PROCESSING");

// Get event log
List<Machine.MachineEvent> events = machine.getEventLog();
```

## Lifecycle Management

Machines provide methods to manage their lifecycle:

```java
// Activate a machine
machine.activate();

// Deactivate a machine
machine.deactivate();

// Shutdown a machine
machine.shutdown();
```

## Event Handling

Machines log events that occur during their operation:

```java
// Log a custom event
machine.logEvent("Custom event description");

// Get all events
List<Machine.MachineEvent> events = machine.getEventLog();
```
