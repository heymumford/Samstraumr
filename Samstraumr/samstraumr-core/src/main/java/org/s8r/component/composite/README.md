# Composite Package

## Overview

The Composite package implements the Composite pattern for the S8r component model, enabling the creation of component structures that act as coordinated units. Composites allow for the organization of individual components into structured arrangements with defined data flows.

## Key Classes

- **Composite**: Container class for managing collections of interconnected components
- **CompositeFactory**: Factory for creating and managing Composite instances with preconfigured patterns
- **CompositeException**: Specialized exception for composite operations

## Features

Composites provide several important capabilities:

- **Component Connection Management**: Connect components in directed graph patterns
- **Data Transformation Pipelines**: Apply transformers to data as it flows through components
- **Validation Chains**: Validate data at various points in the processing flow
- **Circuit Breaker Pattern**: Provide fault tolerance through automatic circuit breaking
- **Event Logging**: Comprehensive event logging for monitoring and diagnostics

## Usage

To create and use composites:

```java
// Create environment
Environment env = new Environment();

// Create a composite with a specific ID
Composite composite = new Composite("my-composite", env);

// Add components
composite.createComponent("input", "Input Component")
         .createComponent("processor", "Processor Component")
         .createComponent("output", "Output Component");

// Connect components
composite.connect("input", "processor")
         .connect("processor", "output");

// Add transformation logic
composite.addTransformer("processor", data -> processData(data));

// Add validation logic
composite.addValidator("input", data -> validateData(data));

// Enable fault tolerance
composite.enableCircuitBreaker("processor", 3, 5000);

// Process data through the composite
Optional<ResultType> result = composite.process("input", inputData);
```

## Composite Patterns

The CompositeFactory provides several preconfigured composite patterns:

- **Transformation Composite**: Linear flow with source, transformer, and sink
- **Validation Composite**: Data validation with processor, validator, and output
- **Processing Composite**: Multi-stage pipeline with parsing, validation, processing, and output
- **Observer Composite**: Pattern for monitoring data flow without modification