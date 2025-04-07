# README

This directory contains documentation for the various adapter patterns used in the Samstraumr framework. These patterns are part of the Clean Architecture implementation that separates core domain logic from external interfaces.

## Available Patterns

- [Machine Adapter](machine-adapter-implementation.md) - Pattern for converting between domain and component machine implementations

## Overview

Adapters in Clean Architecture serve as the translation layer between:

1. The domain layer, which contains the business logic and domain entities
2. The infrastructure layer, which provides concrete implementations of external interfaces

Adapters implement the port interfaces defined in the application layer, allowing the domain to remain isolated from infrastructure concerns.

## Common Principles

All adapter implementations should follow these principles:

1. **Dependency Direction**: Dependencies always point inward, toward the domain
2. **Interface Compliance**: Adapters fully implement their respective port interfaces
3. **Type Conversion**: Adapters handle conversion between domain and infrastructure types
4. **Null Safety**: Adapters provide proper null handling for all operations
5. **Factory Methods**: Adapters expose factory methods for creating instances

## Implementation Guidance

When implementing a new adapter:

1. Study the existing patterns documented here
2. Follow the consistent implementation approach
3. Ensure complete interface coverage
4. Add tests that verify both interface compliance and type conversion logic
