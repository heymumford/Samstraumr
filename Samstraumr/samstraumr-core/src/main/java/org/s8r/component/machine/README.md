# Machine Package

## Overview

The Machine package provides the highest level of composition in the S8r component model. It represents a complete processing system composed of interconnected Composites, offering system-level orchestration and management capabilities.

## Key Classes

- **Machine**: The core class representing a managed system of interconnected components
- **MachineFactory**: Factory for creating and managing Machine instances
- **MachineException**: Specialized exception for machine operations

## Architecture

In the S8r framework, the hierarchy of abstractions is:

1. **Component** - The atomic unit of functionality (core component)
2. **Composite** - A collection of connected components that work together
3. **Machine** - The highest level abstraction that orchestrates multiple composites

Machines provide:
- Comprehensive state management
- Composite interconnection
- Lifecycle control (activation, deactivation, shutdown)
- Event logging and monitoring
- System-wide coordination

## Usage

To create a machine:

```java
// Create environment
Environment env = new Environment();

// Create a machine with a specific ID
Machine machine = MachineFactory.createMachine("my-machine", env);

// Or use a factory method for common machine types
Machine processingMachine = MachineFactory.createDataProcessingMachine(env);
```

To work with composites in a machine:

```java
// Add existing composites
machine.addComposite("input", inputComposite);

// Create new composites
machine.createComposite("processor", "Data Processing Composite");

// Connect composites
machine.connect("input", "processor");
```

## Common Machine Patterns

The MachineFactory provides several preconfigured machine patterns:

- **Data Processing Machine**: Linear processing with input, transformation, and output
- **Monitoring Machine**: System observation with event monitoring and validation
- **Transformation Pipeline**: Sequential stages of transformation and validation