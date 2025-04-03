# Samstraumr Framework

```
Version: 0.6.1
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

[→ Systems Theory Foundation](./docs/SystemsTheoryFoundation.md)

## Core Concepts

### Tubes as Building Blocks

Tubes are self-contained processing units with defined inputs, outputs, and responsibilities. The architecture follows a hierarchical organization:

- **Atomic tubes**: Individual processing units with single responsibilities
- **Bundles**: Coordinated tube collections forming processing pipelines
- **Machines**: Orchestrated bundles implementing complete subsystems

[→ Core Concepts](./docs/CoreConcepts.md) | 
[→ Tube Patterns](./docs/TubePatterns.md) | 
[→ Bundles and Machines](./docs/BundlesAndMachines.md)

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

[→ State Management](./docs/StateManagement.md)

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

[→ Getting Started Guide](./docs/GettingStarted.md)

## Advanced Topics

- [Migration Strategies](./docs/Migration.md): Integrating with existing systems
- [Testing Approaches](./docs/Testing.md): BDD-based testing methodology
- [Design Patterns](./docs/TubePatterns.md): Common tube implementation patterns

## Community

- Contribute implementations through pull requests
- Propose ideas via GitHub issues
- Share usage patterns and experiences
- Improve documentation

## Resources

- [Glossary](./docs/Glossary.md): Terminology reference
- [FAQ](./docs/FAQ.md): Common questions and answers
- [Testing Strategy](./docs/proposals/SamstraumrTestingStrategy.md): Testing methodology
- [LLM Context Proposal](./docs/proposals/LLMContextCompositeTubeProposal.md): Experimental extension

## Development Standards

- **Encoding**: UTF-8 for all text files
- **Line Endings**: LF for all files (except CRLF for .bat/.cmd)
- **Configuration**: Enforced via .editorconfig and .gitattributes
- **Quality Checks**: `./util/quality/check-encoding.sh`
- **Version Management**: `./util/maintenance/update-version.sh <new-version>`
- **Utility Scripts**: Organized by category:
  - `util/build/`: Build automation
  - `util/quality/`: Quality assurance
  - `util/maintenance/`: Maintenance operations

  ⚠️ **IMPORTANT:** All scripts must be run from their util directory locations.

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)
