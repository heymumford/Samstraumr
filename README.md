<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr (S8r) Framework

[![Version](https://img.shields.io/badge/version-1.6.3-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/) [![Maven](https://img.shields.io/badge/Maven-3.6%2B-purple)](https://maven.apache.org/)

> *"S8r (abbreviated from Samstraumr, Old Norse: 'unified flow'): A design framework for adaptive, self-aware software systems that embody the wisdom of natural systems."*

## What is Samstraumr?

Samstraumr is an elegant framework for building resilient, adaptive software systems inspired by systems theory and natural processes. It transcends traditional software architecture to create enterprise applications that:

- **Monitor themselves** and respond intelligently to changing conditions
- **Adapt to stress** by reconfiguring their internal flows and relationships
- **Scale organically** by growing precisely where needed
- **Heal autonomously** when parts experience disruption
- **Evolve over time** to better serve their purpose

Samstraumr isn't meant to replace OOP or FP paradigms; rather, it provides a specialized solution for complex enterprise systems where resilience, adaptability, and long-term maintainability are paramount.

## Why Use Samstraumr?

### Problems Solved

1. **Fragile Enterprise Systems**: Traditional architectures often create brittle systems that fail under unexpected conditions. Samstraumr's self-monitoring components detect and respond to problems automatically.

2. **Rigid Application Structure**: Conventional systems resist change as they grow. Samstraumr's composable architecture allows systems to evolve with changing requirements.

3. **Integration Complexity**: Connecting diverse systems traditionally creates tight coupling and hidden dependencies. Samstraumr's event-driven patterns enable loose coupling with clean boundaries.

4. **Opaque System Behavior**: Most systems provide limited visibility into their internal operations. Samstraumr's built-in monitoring provides comprehensive observability.

5. **Inconsistent Recovery**: Standard error handling often leads to unpredictable recovery behavior. Samstraumr implements consistent lifecycle management across all components.

### Key Benefits

For **Technical Leaders**:
- Reduced technical debt through clear boundaries and self-monitoring
- Evolutionary architecture that grows without complete rewrites
- Built-in resilience and recovery mechanisms
- Structured approach to managing complexity

For **Developers**:
- Well-defined component responsibilities
- Naturally isolated units for easier testing
- Consistent patterns across diverse functionality
- Self-documenting relationships and states

For **Business Stakeholders**:
- Lower maintenance costs through self-healing capabilities
- Precise, targeted scaling in high-demand areas
- Improved business continuity during disruptions
- Future-friendly investment that evolves with requirements

## Core Concepts

Samstraumr is built on a foundation of cohesive concepts that work together to create adaptive systems:

- **Components**: Self-contained processing units with awareness of their state and context
- **Composites**: Coordinated component collections forming processing pipelines
- **Machines**: Orchestrated composites implementing complete subsystems
- **Flow-Oriented Design**: Data and control move through clearly defined pathways
- **Identity Management**: Precise, hierarchical identifiers for all system elements
- **Event-Driven Architecture**: Loose coupling through event-based communication
- **Clean Architecture**: Clear separation of domain, application, and infrastructure concerns

For deeper exploration, see the [Core Concepts documentation](./docs/concepts/core-concepts.md).

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/heymumford/Samstraumr.git
   cd Samstraumr
   ```

2. Build the project:
   ```bash
   ./s8r build
   ```

3. Run tests:
   ```bash
   ./s8r test all
   ```

4. Maven dependency (for using in your projects):
   ```xml
   <dependency>
       <groupId>org.samstraumr</groupId>
       <artifactId>samstraumr-core</artifactId>
       <version>1.6.3</version>
   </dependency>
   ```

### Quick Start

Create your first component:

```java
// Create an environment
Environment env = new Environment.Builder("validation-env")
    .withParameter("strictMode", "true")
    .build();

// Create component
Component validator = new EmailValidatorComponent(env);

// Process data
ValidationResult result = (ValidationResult) validator.process("user@example.com");
```

For a complete introduction, see the [Getting Started Guide](./docs/guides/getting-started.md).

## Documentation

### Guides

- [Prerequisites](./docs/guides/prerequisites.md)
- [Getting Started](./docs/guides/getting-started.md)
- [Composition Strategies](./docs/guides/composition-strategies.md)
- [Component Patterns](./docs/guides/component-patterns.md)
- [Migration Guide](./docs/guides/migration-guide.md)

### Architecture

- [Architecture Overview](./docs/architecture/README.md)
- [Clean Architecture](./docs/architecture/clean/README.md)
- [Event-Driven Architecture](./docs/architecture/event/README.md)
- [Integration Patterns](./docs/architecture/patterns/README.md)
- [Monitoring & Management](./docs/architecture/monitoring/README.md)

### Core Concepts

- [Core Concepts](./docs/concepts/core-concepts.md)
- [Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md)
- [State Management](./docs/concepts/state-management.md)
- [Identity Addressing](./docs/concepts/identity-addressing.md)
- [Composites & Machines](./docs/concepts/composites-and-machines.md)

### Reference

- [Configuration Reference](./docs/reference/configuration-reference.md)
- [Glossary](./docs/reference/glossary.md)
- [Version Management](./docs/reference/version-management.md)
- [FAQ](./docs/reference/f-a-q.md)

### Development

- [Testing Strategy](./docs/dev/test-strategy.md)
- [BDD with Cucumber](./docs/dev/test-bdd-cucumber.md)
- [Test Annotations](./docs/dev/test-annotations.md)

## Command-Line Interface

Samstraumr provides a unified CLI for all operations:

```bash
./s8r <command> [options]
```

Common commands:
- `./s8r build [mode]` - Build the project (fast, test, package)
- `./s8r test <type>` - Run tests (all, component, composite, etc.)
- `./s8r quality check` - Run quality checks
- `./s8r version` - Show or update version information
- `./s8r help` - Display all commands and options

For complete CLI documentation, see the [CLI Reference](./docs/reference/cli-reference.md).

## When NOT to Use Samstraumr

While Samstraumr excels at complex enterprise systems, it may not be the best choice for:

- **Simple CRUD applications** with limited business logic
- **One-off scripts** or single-purpose utilities
- **Performance-critical systems** where every microsecond counts
- **Resource-constrained environments** with extremely limited memory/CPU
- **Standalone applications** with no inter-component communication needs

## Contributing

We welcome contributions from the community! Please read our [Contribution Guidelines](./docs/contribution/contributing.md) before submitting pull requests.

### Development Process

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Run tests and quality checks
5. Submit a pull request

For more details, see [Development Standards](./docs/contribution/code-standards.md).

## Community and Support

- **GitHub Issues**: Bug reports and feature requests
- **Discussions**: Questions and community conversation
- **Contributing**: See [Contribution Guidelines](./docs/contribution/contributing.md)

## License

Samstraumr is licensed under the Mozilla Public License 2.0. See [LICENSE](./LICENSE) for details.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)