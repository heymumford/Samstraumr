# S8r Framework

[![Version](https://img.shields.io/badge/version-1.6.3-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/)

> *"S8r (abbreviated from Samstraumr, Old Norse: 'unified flow'): A design framework for adaptive, self-aware software systems that embody the wisdom of natural systems."*

## What is S8r?

S8r is a revolutionary framework for building resilient, adaptive software systems inspired by systems theory and natural processes. It transcends traditional software architecture to create applications that:

- **Monitor themselves** and respond intelligently to changing conditions
- **Adapt to stress** by reconfiguring their internal flows and relationships
- **Scale organically** by growing precisely where needed
- **Heal autonomously** when parts experience disruption
- **Evolve over time** to better serve their purpose

For software developers, S8r offers a structured way to build systems that are both more robust and more flexible. For business stakeholders, it provides a foundation for applications that require less maintenance, recover gracefully from failures, and evolve alongside changing requirements.

For researchers and system theorists, S8r represents a practical implementation of living systems principles in digital form—a bridge between natural wisdom and technological innovation.

## Core Concepts

At the heart of S8r lies a simple but powerful metaphor: software as a network of mindful components, directing the flow of data and functionality with purpose and awareness.

### Building Blocks

- **Components**: Self-contained processing units that are aware of their own state and context
- **Composites**: Coordinated component collections forming processing pipelines
- **Machines**: Orchestrated composites implementing complete subsystems

### Fundamental Principles

1. **Flow-Oriented Design**: Data and control move through clearly defined pathways
2. **State Management**: Components maintain a unified state representing both structure and behavior
3. **Self-Awareness**: Components monitor their own health and performance
4. **Adaptive Response**: Systems reconfigure based on changing conditions
5. **Identity Clarity**: Every element has a precise, hierarchical identifier

## Why S8r?

### For Technical Leaders and Architects

- **Reduced Technical Debt**: Clear boundaries and self-monitoring reduce unexpected interactions and hidden costs
- **Evolutionary Architecture**: Systems that grow and evolve without complete rewrites
- **Resilience By Design**: Built-in recovery mechanisms and adaptive responses
- **Complexity Management**: A structured approach to managing complex, dynamic systems

### For Developers

- **Clear Responsibilities**: Well-defined boundaries for each component
- **Testable Units**: Naturally isolated components for easier testing
- **Consistent Patterns**: Predictable structure across diverse functionality
- **Self-Documenting**: Explicit relationships and states improve understanding

### For Business Stakeholders

- **Reduced Maintenance Costs**: Self-healing systems require less intervention
- **Graceful Scaling**: Systems grow precisely where needed
- **Business Continuity**: Resilient systems that maintain critical functions during disruptions
- **Future-Friendly Investment**: Architecture that evolves alongside changing requirements

## Documentation

### Getting Started

- [Prerequisites](./docs/guides/prerequisites.md): System requirements and dependencies
- [Getting Started](./docs/guides/getting-started.md): Your first steps with S8r
- [Maven Structure](./docs/guides/MavenStructureGuide.md): Understanding the Maven structure

### Core Concepts

- [Core Concepts](./docs/concepts/core-concepts.md): The fundamental building blocks and principles
- [Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md): The natural wisdom behind the framework
- [State Management](./docs/concepts/state-management.md): Managing component lifecycle states
- [Identity Addressing](./docs/concepts/identity-addressing.md): Component identity and addressing
- [Composites & Machines](./docs/concepts/composites-and-machines.md): Building complex structures

### Migration

- [S8r Migration Guide](./docs/guides/migration/SamstraumrToS8rMigration.md): Migrating from Samstraumr to S8r
- [Bundle to Composite](./docs/guides/migration/BundleToCompositeRefactoring.md): Migrating from bundles to composites

### Technical Reference

- [Configuration](./docs/reference/configuration-reference.md): Configuration options and strategies
- [Glossary](./docs/reference/glossary.md): Terminology and definitions
- [FAQ](./docs/reference/f-a-q.md): Frequently asked questions

### Standards & Development

- [Documentation Standards](./docs/reference/standards/documentation-standards.md): Documentation guidelines
- [Java Naming Standards](./docs/reference/standards/JavaNamingStandards.md): Java naming conventions
- [Testing Strategy](./docs/dev/test-strategy.md): Comprehensive testing approach
- [BDD with Cucumber](./docs/dev/test-bdd-cucumber.md): Behavior-driven development approach

## Development and Contribution

### Getting Started as a Developer

1. Clone the repository: `git clone https://github.com/heymumford/Samstraumr.git`
2. Build the project: `mvn clean install` or `./s8r build`
3. Run tests: `mvn test` or `./s8r test all`

### Development Standards

- **Code Style**: Follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with modifications
- **Documentation**: Javadoc for public APIs, inline comments for complex operations
- **Testing**: All code changes should include appropriate tests
- **Quality Checks**: All submissions must pass quality checks (`./s8r quality check`)

### Command-Line Interface

S8r provides a simplified CLI for all operations:

```
./s8r <command> [options]
```

Common commands:
- `./s8r build [mode]` - Build the project (fast, test, package)
- `./s8r test <type>` - Run tests (all, component, composite, etc.)
- `./s8r quality check` - Run quality checks
- `./s8r version` - Show or update version information
- `./s8r help` - Display all commands and options

## Community and Support

- **GitHub Issues**: Bug reports and feature requests
- **Discussions**: Questions and community conversation
- **Contributing**: See [Contribution Guidelines](./docs/contribution/contributing.md)

## License

S8r is licensed under the Mozilla Public License 2.0. See [LICENSE](./LICENSE) for details.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)
