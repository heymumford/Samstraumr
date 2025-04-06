
# Samstraumr: Adaptive Resilient Software Framework

[![Version](https://img.shields.io/badge/version-2.4.1-blue)](https://github.com/heymumford/Samstraumr/releases) 
[![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) 
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) 
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/) 
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-purple)](https://maven.apache.org/)

**Samstraumr (S8r)** is an enterprise Java framework for building resilient, self-healing software systems with adaptive components and event-driven architecture. Inspired by natural systems' resilience, it enables applications that autonomously monitor, adapt, and recover from disruptions.

> *"Samstraumr (Old Norse: 'unified flow'): A framework where components intelligently collaborate like organisms in an ecosystem, evolving to meet changing demands while maintaining system integrity."*

**⚠️ ACTIVE DEVELOPMENT:** This project is currently in active TDD-based development with simplified CI pipeline focused on basic compilation and structure validation.

| [What is S8r?](#what-is-samstraumr) | [Why Use It?](#why-use-samstraumr) | [Core Concepts](#core-concepts) | [Getting Started](#getting-started) | [Documentation](#documentation) |
|:--:|:--:|:--:|:--:|:--:|
| [CLI Reference](#command-line-interface--tools) | [Project Structure](#project-structure) | [Contributing](#contributing--connect) | [License](#license) | [Connect](#connect) |

## What is Samstraumr?

Samstraumr is an enterprise-grade framework for building resilient, adaptive software systems using Clean Architecture principles and event-driven communication. It implements natural systems theory in software, bringing biological resilience patterns to enterprise applications.

The framework excels at managing complexity in distributed systems, service ecosystems, and high-reliability applications where traditional approaches fail to address constantly changing environments.

### Key Capabilities

- 🔍 **Self-monitor**: Built-in observability with adaptive responses
- 🔄 **Reconfigure**: Dynamic component relationship adjustment  
- 📊 **Scale precisely**: Targeted scaling without system-wide changes
- 🛠️ **Recover automatically**: Component-level failure recovery
- 🌱 **Evolve continuously**: Incremental adaptation without rewrites

**Learn More:** [📜 Origins and Vision](./docs/concepts/origins-and-vision.md) • [🌿 Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md) • [🧩 Core Concepts](./docs/concepts/core-concepts.md)

## Why Use Samstraumr?

### Enterprise Challenges Solved

| Challenge | Samstraumr Solution |
|----------|-------------------|
| **Fragile Systems** | Self-monitoring components prevent cascading failures in production |
| **Technical Debt** | Clean Architecture with clear boundaries prevents debt accumulation |
| **Complex Integrations** | Event-driven patterns create loosely coupled system interactions |
| **Operational Opacity** | Comprehensive observability with built-in component monitoring |
| **Inconsistent Recovery** | Standardized lifecycle management with predictable recovery paths |

### Benefits for Key Stakeholders

**🏛️ Enterprise Architects**
- Reduced technical debt through strict boundaries
- Evolution-friendly architecture adapts without rewrites
- Built-in resilience for mission-critical systems
- Structured approach to distributed complexity

**👩‍💻 Development Teams**
- Well-defined component responsibilities
- Isolated components for parallel development
- Consistent patterns across system domains
- Self-documenting architecture with clear contracts

**💼 Business Stakeholders**
- Lower maintenance costs via self-healing
- Improved continuity with fault-tolerance
- Targeted scaling optimizes infrastructure costs
- Future-friendly evolution with business needs

## Core Concepts

![Core Concepts Diagram](https://via.placeholder.com/400x300?text=Core+Concepts+Diagram)

Samstraumr implements a cohesive set of architectural concepts that work together to create resilient, adaptive systems:

- **🧩 Components**: Self-contained processing units with state awareness
- **🔄 Composites**: Coordinated component collections forming pipelines
- **⚙️ Machines**: Orchestrated composites implementing subsystems
- **➡️ Flow-Oriented**: Data and control along well-defined pathways
- **🏷️ Identity**: Hierarchical addressing for all system elements
- **📢 Event-Driven**: Loose coupling through publish-subscribe patterns

[📘 **Learn more about core concepts**](./docs/concepts/core-concepts.md)

## Getting Started

### Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher
- **Git**: Latest version

[📋 **Setup Guide**](./docs/guides/prerequisites.md)

### Installation

```bash
# Clone the repository
git clone https://github.com/heymumford/Samstraumr.git
cd Samstraumr

# Build project
./s8r-build

# Run tests
./s8r-test all
```

Maven dependency:
```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>2.4.1</version>
</dependency>
```

### Quick Start Example

```java
// Create environment
Environment env = new Environment.Builder("validation-env")
    .withParameter("strictMode", "true")
    .build();

// Create component
Component validator = new EmailValidatorComponent(env);

// Process data
ValidationResult result = (ValidationResult) 
    validator.process("user@example.com");

// Check result
if (result.isValid()) {
    System.out.println("Email is valid!");
}
```

[🚀 **Complete Getting Started Guide**](./docs/guides/getting-started.md)

## Documentation

### 📚 Guides
- [Prerequisites](./docs/guides/prerequisites.md)
- [Getting Started](./docs/guides/getting-started.md)
- [Composition Strategies](./docs/guides/composition-strategies.md)
- [Component Patterns](./docs/guides/component-patterns.md)
- [Migration Guide](./docs/guides/migration-guide.md)

### 🏛️ Architecture
- [Architecture Overview](./docs/architecture/README.md)
- [Clean Architecture](./docs/architecture/clean/README.md)
- [Event-Driven Design](./docs/architecture/event/README.md)
- [Integration Patterns](./docs/architecture/patterns/README.md)
- [Monitoring & Management](./docs/architecture/monitoring/README.md)

### 🧠 Core Concepts
- [Core Concepts](./docs/concepts/core-concepts.md)
- [Systems Theory](./docs/concepts/systems-theory-foundation.md)
- [State Management](./docs/concepts/state-management.md)
- [Identity Addressing](./docs/concepts/identity-addressing.md)
- [Composites & Machines](./docs/concepts/composites-and-machines.md)

### 📖 Reference & Development
- [Configuration](./docs/reference/configuration-reference.md)
- [Glossary](./docs/reference/glossary.md)
- [Version Management](./docs/reference/version-management.md)
- [Changelog](./docs/reference/release/changelog.md)
- [FAQ](./docs/reference/f-a-q.md)

**🧪 Current Development**: We're using [Test-Driven Development](./docs/dev/tdd-development.md) with [BDD/Cucumber](./docs/dev/test-bdd-cucumber.md)

## Command-Line Interface & Tools

### S8r Command-Line Interface

Samstraumr provides a unified CLI for all operations:

```bash
# Main command format
./s8r <command> [options] [arguments]

# Example commands
./s8r build fast --ci            # Fast build with local CI check
./s8r test unit --parallel       # Run unit tests in parallel
./s8r version bump minor         # Bump minor version
./s8r docs target/docs pdf       # Generate PDF documentation
./s8r-docs check                 # Check documentation integrity
./s8r-ci --dry-run               # Test CI pipeline locally
```

| Command | Description |
|---------|-------------|
| `./s8r build [mode]` | Build project (fast, test, package, install, full) |
| `./s8r test <type>` | Run tests (unit, component, integration, all...) |
| `./s8r version <cmd>` | Manage versions (get, bump, set, fix) |
| `./s8r docs [dir] [fmt]` | Generate documentation with Docmosis |
| `./s8r-docs <cmd>` | Check documentation integrity (check, fix, report) |
| `./s8r-ci [options]` | Run GitHub Actions workflows locally using nektos/act |
| `./s8r docmosis <cmd>` | Manage Docmosis integration (setup, test, install) |
| `./s8r report <from> <to>` | Generate change management reports |

[🖥️ **Complete CLI Reference**](./docs/reference/cli-reference.md)

### Document Generation

Samstraumr integrates with Docmosis for professional document generation:

```bash
# Set up Docmosis configuration
./s8r docmosis setup

# Generate documentation
./s8r docs target/docs pdf

# Generate a change report
./s8r report 1.7.2 1.7.3
```

**Document Generation Features:**
- PDF, DOCX, and HTML output formats
- Change management reports
- Release notes generation
- Template-based documentation
- Centralized configuration

[📄 **Document Generation Guide**](./docs/reference/document-generation.md)

## Project Structure

The Samstraumr repository follows a well-organized directory structure:

```
Samstraumr/           # Core implementation and Maven structure
docs/                 # Documentation and reference materials
quality-tools/        # Code quality configurations
util/                 # Utility scripts and tools
src/                  # Example implementations
```

**Organization Principles:**
- 🔍 **Clear Purpose**: Each directory has a singular, well-defined responsibility
- 📊 **Critical Mass**: New folders require 5+ related files
- 📄 **Documented**: Every directory has a README explaining its purpose
- 🏷️ **Naming Focus**: Prefer file naming over deep nesting

[📁 **Directory Structure Overview**](./FOLDERS.md) • [🏛️ **Architecture Mapping**](./docs/architecture/directory-structure.md) • [📋 **Folder Management Guidelines**](./docs/contrib/folder-management-guidelines.md)

## Contributing & Connect

### 🤝 How to Contribute

We welcome contributions from the community! Please read our [Contribution Guidelines](./docs/contribution/contributing.md) before submitting pull requests.

1. 🍴 **Fork repo**
2. 🌿 **Create branch**
3. 👩‍💻 **Make changes**
4. 🧪 **Run tests**
5. 📤 **Submit PR**

[📝 **Development Standards**](./docs/contribution/code-standards.md) • [🔄 **Commit Guidelines**](./docs/contribution/git-commits.md) • [✅ **Quality Checks**](./docs/contribution/quality-checks.md)

## License and Acknowledgments

This project is licensed under the Mozilla Public License 2.0 - see the 
[LICENSE](LICENSE) file for details.

### Development Tools

This project was developed with analytical assistance from:
- Claude 3.7 Sonnet LLM by Anthropic
- Claude Code executable
- Google Gemini Deep Research LLM

These AI tools were used as paid analytical services to assist in development.
All intellectual property rights remain with Eric C. Mumford (@heymumford).

### 🔗 Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)

---

Copyright © 2025 Eric C. Mumford. All rights reserved.

[//]: # (SEO metadata)
[//]: # (Description: Samstraumr (S8r): Enterprise-grade framework for resilient, self-healing software systems with adaptive components and event-driven architecture)
[//]: # (Keywords: resilient systems, adaptive software, event-driven architecture, enterprise framework, self-healing systems, java framework, component-based architecture, systems theory, clean architecture)
[//]: # (Author: Eric C. Mumford)
[//]: # (Canonical: https://github.com/heymumford/Samstraumr)

---
