<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr: Adaptive Resilient Software Framework

[![Version](https://img.shields.io/badge/version-1.6.3-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/) [![Maven](https://img.shields.io/badge/Maven-3.6%2B-purple)](https://maven.apache.org/)

Samstraumr (S8r) is an enterprise Java framework for building resilient, self-healing software systems with adaptive components and event-driven architecture. Inspired by natural systems' resilience, it enables applications that autonomously monitor, adapt, and recover from disruptions.

> *"Samstraumr (Old Norse: 'unified flow'): A framework where components intelligently collaborate like organisms in an ecosystem, evolving to meet changing demands while maintaining system integrity."*

## Table of Contents

- [What is Samstraumr?](#what-is-samstraumr)
- [Why Use Samstraumr?](#why-use-samstraumr)
- [Core Concepts](#core-concepts)
- [Getting Started](#getting-started)
- [Documentation](#documentation)
- [CLI Reference](#command-line-interface)
- [When Not to Use](#when-not-to-use-samstraumr)
- [Contributing](#contributing)
- [License](#license)
- [Connect](#connect)

## What is Samstraumr?

Samstraumr is an enterprise-grade framework for building resilient, adaptive software systems using Clean Architecture principles and event-driven communication. It creates applications that:

- **Self-monitor** with built-in observability and adapt to changing conditions
- **Reconfigure under stress** by dynamically adjusting component relationships
- **Scale precisely** where demand exists without system-wide changes
- **Recover automatically** from component failures with graceful degradation
- **Evolve continuously** without requiring complete system rewrites

Samstraumr implements natural systems theory in software, bringing biological resilience patterns to enterprise applications. It excels at managing complexity in distributed systems, service ecosystems, and high-reliability applications.

Learn more:
- [Origins and Vision](./docs/concepts/origins-and-vision.md) - The 30-year journey behind Samstraumr
- [Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md) - How natural systems inspire the architecture
- [Core Concepts](./docs/concepts/core-concepts.md) - The key principles and building blocks

## Why Use Samstraumr?

### Enterprise Challenges Solved

1. **Fragile System Prevention**: Samstraumr's self-monitoring components detect and respond to problems automatically, preventing cascading failures in production systems.

2. **Technical Debt Reduction**: Clean Architecture principles and clear component boundaries prevent the accumulation of technical debt in long-lived enterprise applications.

3. **Integration Simplification**: Event-driven patterns create loosely coupled integrations between disparate systems, eliminating hidden dependencies and complex integration logic.

4. **Operational Transparency**: Comprehensive observability with built-in monitoring provides complete visibility into system behavior and component interactions.

5. **Standardized Recovery**: Consistent lifecycle management allows components to recover predictably from failures using standardized recovery paths.

### Key Benefits

For **Enterprise Architects**:
- Reduced technical debt through strict boundary enforcement
- Evolution-friendly architecture that adapts without complete rewrites
- Built-in resilience for mission-critical systems
- Structured approach to managing distributed system complexity

For **Development Teams**:
- Well-defined component responsibilities with explicit contracts
- Isolated components for easier parallel development and testing
- Consistent patterns across different system domains
- Self-documenting architecture with clear relationships

For **Business Stakeholders**:
- Lower maintenance costs through self-healing capabilities
- Improved business continuity with fault-tolerant design
- Targeted scaling that optimizes infrastructure costs
- Future-friendly investment that evolves with business needs

## Core Concepts

Samstraumr implements a cohesive set of architectural concepts:

- **Components**: Self-contained processing units with awareness of state and context
- **Composites**: Coordinated component collections forming processing pipelines
- **Machines**: Orchestrated composites implementing complete subsystems
- **Flow-Oriented Design**: Data and control moving through well-defined pathways
- **Identity Management**: Hierarchical addressing for all system elements
- **Event-Driven Communication**: Loose coupling through publish-subscribe patterns
- **Clean Architecture**: Clear separation of domain, application, and infrastructure

[Learn more about core concepts →](./docs/concepts/core-concepts.md)

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

### Quick Start Example

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

[Complete getting started guide →](./docs/guides/getting-started.md)

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

[Complete CLI reference →](./docs/reference/cli-reference.md)

### Configuration System

Samstraumr uses a unified configuration system in the `.samstraumr` directory. This provides a consistent environment for both the CLI tool and shell scripts.

#### Usage in Shell Scripts

```bash
# Source the unified configuration
source "${PROJECT_ROOT}/.samstraumr/config.sh"

# Use configuration variables
echo "Core module path: ${SAMSTRAUMR_CORE_MODULE}"
```

The configuration includes project paths, package structures, Maven settings, and command mappings.

For comprehensive build documentation, see the [Build Guide](./docs/guide/build.md).

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

[Development standards →](./docs/contribution/code-standards.md)

## License

Samstraumr is licensed under the Mozilla Public License 2.0. See [LICENSE](./LICENSE) for details.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)

[//]: # (SEO metadata)
[//]: # (Description: Samstraumr (S8r): Enterprise-grade framework for resilient, self-healing software systems with adaptive components and event-driven architecture)
[//]: # (Keywords: resilient systems, adaptive software, event-driven architecture, enterprise framework, self-healing systems, java framework, component-based architecture, systems theory, clean architecture)
[//]: # (Author: Eric C. Mumford)
[//]: # (Canonical: https://github.com/heymumford/Samstraumr)