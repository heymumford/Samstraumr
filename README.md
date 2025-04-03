# Samstraumr Framework

[![Version](https://img.shields.io/badge/version-1.2.1-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/)

```
Version: 1.2.1
License: Mozilla Public License 2.0
Maintainer: Eric C. Mumford (@heymumford)
```

## Table of Contents

- [Overview](#overview)
- [Core Concepts](#core-concepts)
    - [Tubes as Building Blocks](#tubes-as-building-blocks)
    - [Identity Notation](#identity-notation)
    - [State Management](#state-management)
- [Benefits](#benefits)
- [Getting Started](#getting-started)
- [Advanced Topics](#advanced-topics)
- [Community](#community)
- [Resources](#resources)
- [Development Standards](#development-standards)
- [Connect](#connect)

## Overview

Samstraumr (Old Norse: "unified flow") is a design framework for building adaptive software systems through Tube-Based Development (TBD). It implements principles from systems theory to create resilient, self-monitoring components.

[→ Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md)

## Core Concepts

### Tubes as Building Blocks

Tubes are self-contained processing units with defined inputs, outputs, and responsibilities. The architecture follows a hierarchical organization:

- **Atomic tubes**: Individual processing units with single responsibilities
- **Composites**: Coordinated tube collections forming processing pipelines (formerly Bundles)
- **Machines**: Orchestrated composites implementing complete subsystems

[→ Core Concepts](./docs/concepts/core-concepts.md) | 
[→ Tube Patterns](./docs/guides/tube-patterns.md) | 
[→ Composites and Machines](./docs/CompositesAndMachines.md)

### Identity Notation

The framework uses a hierarchical naming convention:

- `T<ID>`: Single tube identifier (`T7`)
- `B<ID>.T<ID>`: Bundle-scoped tube identifier (`B3.T2`)
- `M<ID>.B<ID>.T<ID>`: Fully-qualified machine-scoped identifier (`M0.B1.T4`)

This notation provides precise component addressing without ambiguity.

### State Management

Tubes maintain two complementary state models:

1. **Design State**: Core operational state
    - `FLOWING`: Normal operation
    - `BLOCKED`: Processing temporarily halted
    - `ADAPTING`: Reconfiguring for new conditions
    - `ERROR`: Fault condition

2. **Dynamic State**: Real-time operational characteristics tracked as key-value properties

[→ State Management](./docs/concepts/state-management.md)

## Benefits

- Emergent intelligence through component composition
- Resilience through self-monitoring and adaptation
- Evolutionary architecture supporting incremental growth
- Reduced technical debt through clear boundaries
- Collaborative development with well-defined interfaces
- Scalable design from small applications to distributed systems

## Getting Started

1. Create a single tube with clear responsibilities
2. Define input/output contracts
3. Implement monitoring and adaptation logic
4. Compose tubes into functional bundles

[→ Getting Started Guide](./docs/guides/getting-started.md)

## Advanced Topics

- [Migration Strategies](./docs/guides/migration-guide.md): Integrating with existing systems
- [Testing Approaches](./docs/testing/test-strategy.md): BDD-based testing methodology
- [Design Patterns](./docs/guides/tube-patterns.md): Common tube implementation patterns

## Community

- Contribute implementations through pull requests
- Propose ideas via GitHub issues
- Share usage patterns and experiences
- Improve documentation

## Resources

- [Glossary](./docs/reference/glossary.md): Terminology reference
- [FAQ](./docs/reference/faq.md): Common questions and answers
- [Testing Strategy](./docs/testing/test-strategy.md): Industry-aligned testing methodology
- [LLM Context Proposal](./docs/proposals/llm-context-composite-tube-proposal.md): Experimental extension
- [Folder Structure](./docs/FOLDER_STRUCTURE.md): Project organization guide

## Development Standards

- **Encoding**: UTF-8 for all text files
- **Line Endings**: LF for all files (except CRLF for .bat/.cmd)
- **Configuration**: Enforced via .editorconfig and .gitattributes
- **Quality Checks**: `./util/quality/check-encoding.sh`
- **Version Management**: `./util/version bump patch` or `./util/version set <new-version>`
- **Build Pipeline**: Local validation with [Act](https://github.com/nektos/act) (see [CI/CD Guide](./docs/contribution/ci-cd-guide.md))
- **Build Reports**: Generate a detailed build report with `./util/build/generate-build-report.sh --skip-tests --skip-quality` (see [Build Report Guide](./docs/contribution/build-report-guide.md))
- **Utility Scripts**: Organized by category:
  - `util/build/`: Build automation
  - `util/quality/`: Quality assurance
  - `util/maintenance/`: Maintenance operations
  - `util/badges/`: Badge generation

  ⚠️ **IMPORTANT:** All scripts must be run from their util directory locations.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)
