# Samstraumr Framework

[![Version](https://img.shields.io/badge/version-1.5.2-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/)

> *"Samstraumr (Old Norse: 'unified flow'): A design framework for adaptive, self-aware software systems that embody the wisdom of natural systems."*

## What is Samstraumr?

Samstraumr is a revolutionary framework for building resilient, adaptive software systems inspired by systems theory and natural processes. It transcends traditional software architecture to create applications that:

- **Monitor themselves** and respond intelligently to changing conditions
- **Adapt to stress** by reconfiguring their internal flows and relationships
- **Scale organically** by growing precisely where needed
- **Heal autonomously** when parts experience disruption
- **Evolve over time** to better serve their purpose

For software developers, Samstraumr offers a structured way to build systems that are both more robust and more flexible. For business stakeholders, it provides a foundation for applications that require less maintenance, recover gracefully from failures, and evolve alongside changing requirements.

For researchers and system theorists, Samstraumr represents a practical implementation of living systems principles in digital form—a bridge between natural wisdom and technological innovation.

## Core Concepts

At the heart of Samstraumr lies a simple but powerful metaphor: software as a network of mindful tubes, directing the flow of data and functionality with purpose and awareness.

### Building Blocks

- **Tubes**: Self-contained processing units that are aware of their own state and context
- **Composites**: Coordinated tube collections forming processing pipelines
- **Machines**: Orchestrated composites implementing complete subsystems

### Fundamental Principles

1. **Flow-Oriented Design**: Data and control move through clearly defined pathways
2. **State Duality**: Components maintain both structural state (what they are) and dynamic state (what they're experiencing)
3. **Self-Awareness**: Components monitor their own health and performance
4. **Adaptive Response**: Systems reconfigure based on changing conditions
5. **Identity Clarity**: Every element has a precise, hierarchical identifier

## Why Samstraumr?

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

### Conceptual Foundation

- [Core Concepts](./docs/concepts/CoreConcepts.md): The fundamental building blocks and principles
- [Systems Theory Foundation](./docs/concepts/SystemsTheoryFoundation.md): The natural wisdom behind the framework
- [State Management](./docs/concepts/StateManagement.md): The dual approach to tracking component state
- [Identity and Addressing](./docs/concepts/IdentityAddressing.md): Hierarchical naming and component identification

### Implementation Guides

- [Getting Started](./docs/guides/GettingStarted.md): Your first steps with Samstraumr
- [Migration Guide](./docs/guides/MigrationGuide.md): Integrating Samstraumr with existing systems
- [Tube Patterns](./docs/guides/TubePatterns.md): Common implementation patterns for tubes
- [Composition Strategies](./docs/guides/CompositionStrategies.md): Approaches to combining tubes into larger structures

### Technical Reference

- [API Reference](./docs/reference/ConfigurationReference.md): Complete API documentation
- [Configuration](./docs/reference/ConfigurationReference.md): Configuration options and strategies
- [Glossary](./docs/reference/Glossary.md): Terminology and definitions
- [FAQ](./docs/reference/FAQ.md): Frequently asked questions

### Testing and Quality

- [Test Strategy](./docs/testing/TestStrategy.md): Comprehensive testing approach
- [BDD with Cucumber](./docs/testing/BddWithCucumber.md): Behavior-driven development approach
- [Testing Annotations](./docs/testing/TestingAnnotations.md): Standard and Samstraumr-specific test annotations
- [Quality Standards](./docs/contribution/QualityChecks.md): Code quality and performance standards

### Research and Proposals

- [LLM Context Proposal](./docs/research/LlmContextProposal.md): Integrating large language models with Samstraumr
- [Tube Lifecycle Design](./docs/proposals/TubeLifecycleDesign.md): Biological lifecycle model for tubes

## Development and Contribution

### Getting Started as a Developer

1. Clone the repository: `git clone https://github.com/heymumford/Samstraumr.git`
2. Build the project: `mvn clean install` or `./util/build-optimal.sh`
3. Run tests: `mvn test` or `./util/test-run.sh all`

### Development Standards

- **Code Style**: Follows [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with modifications
- **Documentation**: Javadoc for public APIs, inline comments for complex operations
- **Testing**: All code changes should include appropriate tests
- **Quality Checks**: All submissions must pass quality checks (`./util/scripts/check-build-quality.sh`)

### Build Commands

- **Standard Build**: `mvn clean install`
- **Optimized Build**: `./util/build-optimal.sh`
- **Fast Development**: `./util/build-optimal.sh fast`
- **Quality Checks**: `./util/scripts/check-build-quality.sh`

### Test Commands

- **All Tests**: `./util/test-run.sh all`
- **Unit Tests**: `./util/test-run.sh unit` or `./util/test-run.sh tube`
- **Industry Standard**: `./util/test-run.sh --help` for all options

## Community and Support

- **GitHub Issues**: Bug reports and feature requests
- **Discussions**: Questions and community conversation
- **Contributing**: See [Contribution Guidelines](./docs/contribution/Contributing.md)

## License

Samstraumr is licensed under the Mozilla Public License 2.0. See [LICENSE](./LICENSE) for details.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)