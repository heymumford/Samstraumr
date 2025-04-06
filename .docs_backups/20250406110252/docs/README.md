<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# S8r Documentation

Welcome to the S8r framework documentation. S8r (pronounced "sater") is a component-based architecture that provides a unified approach to building software systems with self-awareness and adaptive capabilities.

## About S8r

S8r represents a fundamental shift in software architecture:

- **Component-Based Design**: Build systems from self-contained, aware components
- **Unified State Management**: Single state model for both lifecycle and operational status
- **Identity & Awareness**: Components with clear identities and contextual awareness
- **Hierarchical Composition**: Build complex systems through Composites and Machines
- **Test-Driven Development**: Comprehensive testing framework with BDD support

## Documentation Map

### 🚀 Getting Started

- [Introduction](guides/introduction.md) - Overview of S8r concepts and benefits
- [Prerequisites](guides/prerequisites.md) - System requirements and dependencies
- [Getting Started](guides/getting-started.md) - Setting up S8r in your environment
- [Maven Structure](guides/MavenStructureGuide.md) - Organization and build process

### 🔍 Core Concepts

- [Core Architecture](concepts/core-concepts.md) - Component model fundamentals
- [State Management](concepts/state-management.md) - Unified lifecycle states
- [Identity System](concepts/identity-addressing.md) - Component identity
- [Composites & Machines](concepts/composites-and-machines.md) - Building complex structures

### 📘 Guides

- [Composition Patterns](guides/composition-strategies.md) - Component composition strategies
- [Migration from Samstraumr](guides/migration/SamstraumrToS8rMigration.md) - Upgrading to S8r
- [Common Component Patterns](guides/component-patterns.md) - Design patterns for components

### 📝 Reference

- [API Reference](reference/api-reference.md) - Complete API documentation
- [Configuration](reference/configuration-reference.md) - Configuration options
- [Glossary](reference/glossary.md) - Terminology and definitions
- [Examples](reference/s8r-examples.md) - Code examples and use cases

### 🧪 Development

- [Testing Strategy](dev/test-strategy.md) - Comprehensive testing approach
- [BDD with Cucumber](dev/test-bdd-cucumber.md) - Behavior-driven development
- [Contributing](contribution/contributing.md) - How to contribute to S8r
- [Code Standards](contribution/code-standards.md) - Coding standards and style
- [Clean Architecture](architecture/clean-architecture-migration.md) - Clean Architecture migration plan

## Quick Reference

### Component Creation

```java
// Create a basic component
Environment env = new Environment.Builder("test-env")
    .withParameter("key", "value")
    .build();
    
Component component = Component.create("reason", env);

// Create a child component
Component child = Component.createChild("child-reason", env, component);
```

### State Management

```java
// Check component state
if (component.getState() == State.READY) {
    component.setState(State.ACTIVE);
}

// Handle termination
component.terminate();
```

### Composite Construction

```java
// Create a composite
Composite composite = CompositeFactory.create("validation-flow", env);

// Add components
composite.addComponent("validator", validatorComponent);
composite.addComponent("processor", processorComponent);

// Connect components
composite.connect("validator", "processor");
```

### Machine Orchestration

```java
// Create a machine
Machine machine = MachineFactory.create("data-pipeline", env);

// Register composites
machine.registerComposite(inputComposite);
machine.registerComposite(processingComposite);
machine.registerComposite(outputComposite);
```

## Key Documentation

- **New to S8r**: Start with [Introduction](guides/introduction.md)
- **Migrating from Samstraumr**: See [Migration Guide](guides/migration/SamstraumrToS8rMigration.md)
- **Contributing**: Check [Contributing Guide](contribution/contributing.md)
- **Architecture**: See [Core Concepts](concepts/core-concepts.md)
- **Clean Architecture**: See [Migration Plan](architecture/clean-architecture-migration.md)
