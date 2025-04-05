<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr: Adaptive Resilient Software Framework

<div align="center">
  
[![Version](https://img.shields.io/badge/version-2.0.0-blue)](https://github.com/heymumford/Samstraumr/releases) 
[![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) 
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) 
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/) 
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-purple)](https://maven.apache.org/)

</div>

<table>
<tr>
<td width="70%">

**Samstraumr (S8r)** is an enterprise Java framework for building resilient, self-healing software systems with adaptive components and event-driven architecture. Inspired by natural systems' resilience, it enables applications that autonomously monitor, adapt, and recover from disruptions.

> *"Samstraumr (Old Norse: 'unified flow'): A framework where components intelligently collaborate like organisms in an ecosystem, evolving to meet changing demands while maintaining system integrity."*

</td>
<td width="30%" style="background-color: #f8f9fa; padding: 10px; border-radius: 5px;">

### Development Status

⚠️ **ACTIVE DEVELOPMENT**

This project is currently in active TDD-based development. The CI pipeline has been simplified to focus on basic compilation and structure validation. Full test suites and quality checks are temporarily disabled to enable rapid iteration.

</td>
</tr>
</table>

<div align="center">
  
| [What is S8r?](#what-is-samstraumr) | [Why Use It?](#why-use-samstraumr) | [Core Concepts](#core-concepts) | [Getting Started](#getting-started) | [Documentation](#documentation) |
|:--:|:--:|:--:|:--:|:--:|
| [CLI Reference](#command-line-interface) | [Project Structure](#project-structure) | [Contributing](#contributing) | [License](#license) | [Connect](#connect) |

</div>

## What is Samstraumr?

<table>
<tr>
<td width="60%">

Samstraumr is an enterprise-grade framework for building resilient, adaptive software systems using Clean Architecture principles and event-driven communication. It implements natural systems theory in software, bringing biological resilience patterns to enterprise applications.

The framework excels at managing complexity in distributed systems, service ecosystems, and high-reliability applications where traditional approaches fail to address constantly changing environments.

</td>
<td width="40%">

### Key Capabilities

<table>
<tr><td>🔍 <strong>Self-monitor</strong></td><td>Built-in observability with adaptive responses</td></tr>
<tr><td>🔄 <strong>Reconfigure</strong></td><td>Dynamic component relationship adjustment</td></tr>
<tr><td>📊 <strong>Scale precisely</strong></td><td>Targeted scaling without system-wide changes</td></tr>
<tr><td>🛠️ <strong>Recover automatically</strong></td><td>Component-level failure recovery</td></tr>
<tr><td>🌱 <strong>Evolve continuously</strong></td><td>Incremental adaptation without rewrites</td></tr>
</table>

</td>
</tr>
<tr>
<td colspan="2" align="center">

### Learn More

[📜 Origins and Vision](./docs/concepts/origins-and-vision.md) • 
[🌿 Systems Theory Foundation](./docs/concepts/systems-theory-foundation.md) • 
[🧩 Core Concepts](./docs/concepts/core-concepts.md)

</td>
</tr>
</table>

## Why Use Samstraumr?

<div class="grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
<div>

### Enterprise Challenges Solved

<table>
<tr>
   <th align="left">Challenge</th>
   <th align="left">Samstraumr Solution</th>
</tr>
<tr>
   <td><strong>Fragile Systems</strong></td>
   <td>Self-monitoring components prevent cascading failures in production</td>
</tr>
<tr>
   <td><strong>Technical Debt</strong></td>
   <td>Clean Architecture with clear boundaries prevents debt accumulation</td>
</tr>
<tr>
   <td><strong>Complex Integrations</strong></td>
   <td>Event-driven patterns create loosely coupled system interactions</td>
</tr>
<tr>
   <td><strong>Operational Opacity</strong></td>
   <td>Comprehensive observability with built-in component monitoring</td>
</tr>
<tr>
   <td><strong>Inconsistent Recovery</strong></td>
   <td>Standardized lifecycle management with predictable recovery paths</td>
</tr>
</table>

</div>
<div>

### Benefits for Key Stakeholders

<table>
<tr>
   <th colspan="2" align="left">🏛️ Enterprise Architects</th>
</tr>
<tr>
   <td>• Reduced technical debt through strict boundaries</td>
   <td>• Evolution-friendly architecture adapts without rewrites</td>
</tr>
<tr>
   <td>• Built-in resilience for mission-critical systems</td>
   <td>• Structured approach to distributed complexity</td>
</tr>

<tr>
   <th colspan="2" align="left">👩‍💻 Development Teams</th>
</tr>
<tr>
   <td>• Well-defined component responsibilities</td>
   <td>• Isolated components for parallel development</td>
</tr>
<tr>
   <td>• Consistent patterns across system domains</td>
   <td>• Self-documenting architecture with clear contracts</td>
</tr>

<tr>
   <th colspan="2" align="left">💼 Business Stakeholders</th>
</tr>
<tr>
   <td>• Lower maintenance costs via self-healing</td>
   <td>• Improved continuity with fault-tolerance</td>
</tr>
<tr>
   <td>• Targeted scaling optimizes infrastructure costs</td>
   <td>• Future-friendly evolution with business needs</td>
</tr>
</table>

</div>
</div>

## Core Concepts

<table>
<tr>
<td width="40%">
<div align="center">
  
![Core Concepts Diagram](https://via.placeholder.com/400x300?text=Core+Concepts+Diagram)
<br><small><i>S8r architectural layers and relationships</i></small>

</div>
</td>
<td width="60%">

Samstraumr implements a cohesive set of architectural concepts that work together to create resilient, adaptive systems:

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>🧩 Components</strong><br>
        Self-contained processing units with state awareness
    </div>
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>🔄 Composites</strong><br>
        Coordinated component collections forming pipelines
    </div>
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>⚙️ Machines</strong><br>
        Orchestrated composites implementing subsystems
    </div>
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>➡️ Flow-Oriented</strong><br>
        Data and control along well-defined pathways
    </div>
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>🏷️ Identity</strong><br>
        Hierarchical addressing for all system elements
    </div>
    <div style="background-color: #f8f8f8; padding: 10px; border-radius: 5px;">
        <strong>📢 Event-Driven</strong><br>
        Loose coupling through publish-subscribe patterns
    </div>
</div>

<div align="center" style="margin-top: 15px;">
    <a href="./docs/concepts/core-concepts.md">📘 <strong>Learn more about core concepts</strong></a>
</div>

</td>
</tr>
</table>

## Getting Started

<table>
<tr>
<td width="33%">

### Prerequisites

<table>
  <tr><td><img src="https://img.shields.io/badge/Java-17%2B-orange" height="20"/></td><td>JDK 17 or higher</td></tr>
  <tr><td><img src="https://img.shields.io/badge/Maven-3.6%2B-purple" height="20"/></td><td>Maven 3.6 or higher</td></tr>
  <tr><td><img src="https://img.shields.io/badge/Git-latest-black" height="20"/></td><td>Git (latest version)</td></tr>
</table>

<div align="center">
    <a href="./docs/guides/prerequisites.md">📋 <strong>Setup Guide</strong></a>
</div>

</td>
<td width="33%">

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
    <version>2.0.0</version>
</dependency>
```

</td>
<td width="33%">

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

<div align="center" style="margin-top: 10px;">
    <a href="./docs/guides/getting-started.md">🚀 <strong>Complete Getting Started Guide</strong></a>
</div>

</td>
</tr>
</table>

## Documentation

<table>
<tr>
<td width="25%" style="vertical-align: top;">

### 📚 Guides

<ul style="list-style-type: none; padding-left: 5px;">
  <li>📋 <a href="./docs/guides/prerequisites.md">Prerequisites</a></li>
  <li>🚀 <a href="./docs/guides/getting-started.md">Getting Started</a></li>
  <li>🔄 <a href="./docs/guides/composition-strategies.md">Composition Strategies</a></li>
  <li>🧩 <a href="./docs/guides/component-patterns.md">Component Patterns</a></li>
  <li>🔁 <a href="./docs/guides/migration-guide.md">Migration Guide</a></li>
</ul>

</td>
<td width="25%" style="vertical-align: top;">

### 🏛️ Architecture

<ul style="list-style-type: none; padding-left: 5px;">
  <li>📐 <a href="./docs/architecture/README.md">Architecture Overview</a></li>
  <li>🧱 <a href="./docs/architecture/clean/README.md">Clean Architecture</a></li>
  <li>📢 <a href="./docs/architecture/event/README.md">Event-Driven Design</a></li>
  <li>🔗 <a href="./docs/architecture/patterns/README.md">Integration Patterns</a></li>
  <li>📊 <a href="./docs/architecture/monitoring/README.md">Monitoring & Management</a></li>
</ul>

</td>
<td width="25%" style="vertical-align: top;">

### 🧠 Core Concepts

<ul style="list-style-type: none; padding-left: 5px;">
  <li>🧩 <a href="./docs/concepts/core-concepts.md">Core Concepts</a></li>
  <li>🌿 <a href="./docs/concepts/systems-theory-foundation.md">Systems Theory</a></li>
  <li>⚙️ <a href="./docs/concepts/state-management.md">State Management</a></li>
  <li>🏷️ <a href="./docs/concepts/identity-addressing.md">Identity Addressing</a></li>
  <li>🔄 <a href="./docs/concepts/composites-and-machines.md">Composites & Machines</a></li>
</ul>

</td>
<td width="25%" style="vertical-align: top;">

### 📖 Reference & Development

<ul style="list-style-type: none; padding-left: 5px;">
  <li>⚙️ <a href="./docs/reference/configuration-reference.md">Configuration</a></li>
  <li>📘 <a href="./docs/reference/glossary.md">Glossary</a></li>
  <li>🔖 <a href="./docs/reference/version-management.md">Version Management</a></li>
  <li>📜 <a href="./docs/reference/release/changelog.md">Changelog</a></li>
  <li>❓ <a href="./docs/reference/f-a-q.md">FAQ</a></li>
</ul>

<div style="margin-top: 15px; background-color: #f0f7ff; padding: 8px; border-radius: 5px;">
  <strong>🧪 Current Development</strong><br>
  We're using <a href="./docs/dev/tdd-development.md">Test-Driven Development</a> with <a href="./docs/dev/test-bdd-cucumber.md">BDD/Cucumber</a>
</div>

</td>
</tr>
</table>

## Command-Line Interface & Tools

<table>
<tr>
<td width="60%">

### S8r Command-Line Interface

Samstraumr provides a unified CLI for all operations through the `s8r` command:

```bash
# Main command format
./s8r <command> [options] [arguments]

# Example commands
./s8r build fast                  # Fast build
./s8r test unit --parallel        # Run unit tests in parallel
./s8r version bump minor          # Bump minor version
./s8r docs target/docs pdf        # Generate PDF documentation
```

<table>
<tr><th align="left">Command</th><th align="left">Description</th></tr>
<tr><td><code>./s8r build [mode]</code></td><td>Build project (fast, test, package, install, full)</td></tr>
<tr><td><code>./s8r test &lt;type&gt;</code></td><td>Run tests (unit, component, integration, all...)</td></tr>
<tr><td><code>./s8r version &lt;cmd&gt;</code></td><td>Manage versions (get, bump, set, fix)</td></tr>
<tr><td><code>./s8r docs [dir] [fmt]</code></td><td>Generate documentation with Docmosis</td></tr>
<tr><td><code>./s8r docmosis &lt;cmd&gt;</code></td><td>Manage Docmosis integration (setup, test, install)</td></tr>
<tr><td><code>./s8r report &lt;from&gt; &lt;to&gt;</code></td><td>Generate change management reports</td></tr>
</table>

<div align="center" style="margin-top: 10px;">
    <a href="./docs/reference/cli-reference.md">🖥️ <strong>Complete CLI Reference</strong></a>
</div>

</td>
<td width="40%">

### Docmosis Integration

Samstraumr integrates with Docmosis for professional document generation, including release notes, change reports, and documentation.

```bash
# Set up Docmosis configuration
./s8r docmosis setup

# Generate documentation
./s8r docs target/docs pdf

# Generate a change report
./s8r report 1.7.2 1.7.3
```

<div style="background-color: #f0f7ff; padding: 10px; border-radius: 5px; margin-top: 10px;">
<strong>Document Generation Features:</strong><br>
• PDF, DOCX, and HTML output formats<br>
• Change management reports<br>
• Release notes generation<br>
• Template-based documentation<br>
• Centralized configuration
</div>

<div align="center" style="margin-top: 10px;">
    <a href="./docs/reference/document-generation.md">📄 <strong>Document Generation Guide</strong></a>
</div>

</td>
</tr>
</table>

## Project Structure

<table style="width: 100%; background-color: #f8f9fa; border-radius: 5px;">
<tr>
<td width="55%">

The Samstraumr repository follows a well-organized directory structure that balances depth with discoverability:

```
Samstraumr/           # Core implementation and Maven structure
docs/                 # Documentation and reference materials
quality-tools/        # Code quality configurations
util/                 # Utility scripts and tools
src/                  # Example implementations
```

Each directory serves a specific purpose and follows consistent organization patterns. The project emphasizes **file naming conventions over deep nesting** to improve navigation and maintainability.

<div align="center" style="margin-top: 10px;">
    <a href="./FOLDERS.md">📁 <strong>Directory Structure Overview</strong></a> •
    <a href="./docs/architecture/directory-structure.md">🏛️ <strong>Architecture Mapping</strong></a>
</div>

</td>
<td width="45%">

### Organization Principles

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
    <div style="background-color: white; padding: 10px; border-radius: 5px;">
        <strong>🔍 Clear Purpose</strong><br>
        Each directory has a singular, well-defined responsibility
    </div>
    <div style="background-color: white; padding: 10px; border-radius: 5px;">
        <strong>📊 Critical Mass</strong><br>
        New folders require 5+ related files
    </div>
    <div style="background-color: white; padding: 10px; border-radius: 5px;">
        <strong>📄 Documented</strong><br>
        Every directory has a README explaining its purpose
    </div>
    <div style="background-color: white; padding: 10px; border-radius: 5px;">
        <strong>🏷️ Naming Focus</strong><br>
        Prefer file naming over deep nesting
    </div>
</div>

<div align="center" style="margin-top: 15px;">
    <a href="./docs/contrib/folder-management-guidelines.md">📋 <strong>Folder Management Guidelines</strong></a>
</div>

</td>
</tr>
</table>

## Contributing & Connect

<table>
<tr>
<td width="60%">

### 🤝 How to Contribute

We welcome contributions from the community! Please read our [Contribution Guidelines](./docs/contribution/contributing.md) before submitting pull requests.

<table style="width: 100%;">
<tr>
<td style="text-align: center; width: 20%; padding: 10px;">
<strong>1</strong><br>
<span style="font-size: 24px;">🍴</span><br>
Fork repo
</td>
<td style="text-align: center; width: 20%; padding: 10px;">
<strong>2</strong><br>
<span style="font-size: 24px;">🌿</span><br>
Create branch
</td>
<td style="text-align: center; width: 20%; padding: 10px;">
<strong>3</strong><br>
<span style="font-size: 24px;">👩‍💻</span><br>
Make changes
</td>
<td style="text-align: center; width: 20%; padding: 10px;">
<strong>4</strong><br>
<span style="font-size: 24px;">🧪</span><br>
Run tests
</td>
<td style="text-align: center; width: 20%; padding: 10px;">
<strong>5</strong><br>
<span style="font-size: 24px;">📤</span><br>
Submit PR
</td>
</tr>
</table>

<div align="center" style="margin-top: 10px;">
    <a href="./docs/contribution/code-standards.md">📝 <strong>Development Standards</strong></a> • 
    <a href="./docs/contribution/git-commits.md">🔄 <strong>Commit Guidelines</strong></a> • 
    <a href="./docs/contribution/quality-checks.md">✅ <strong>Quality Checks</strong></a>
</div>

</td>
<td width="40%" style="vertical-align: top;">

### 📄 License

<div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 15px;">
<p><img src="https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg" height="20" style="vertical-align: middle;"> <strong>Mozilla Public License 2.0</strong></p>

<small>Samstraumr is open source under the MPL 2.0 license, which allows for integration with proprietary software while ensuring that changes to Samstraumr itself remain open source.</small>

<div align="center" style="margin-top: 10px;">
    <a href="./LICENSE">View Full License</a>
</div>
</div>

### 🔗 Connect

<table style="width: 100%;">
<tr>
<td><strong>Author:</strong></td>
<td><a href="mailto:heymumford@samstraumr.org">Eric C. Mumford</a></td>
</tr>
<tr>
<td><strong>GitHub:</strong></td>
<td><a href="https://github.com/heymumford">github.com/heymumford</a></td>
</tr>
<tr>
<td><strong>LinkedIn:</strong></td>
<td><a href="https://www.linkedin.com/in/eric-mumford/">linkedin.com/in/eric-mumford</a></td>
</tr>
</table>

</td>
</tr>
</table>

<div align="center" style="margin-top: 30px; color: #6c757d; font-size: 0.9em;">
    <p>Copyright © 2025 Eric C. Mumford. All rights reserved.</p>
    <p>
        <small>
            <i>Samstraumr (S8r): Enterprise-grade framework for resilient, self-healing software systems with adaptive components and event-driven architecture</i>
        </small>
    </p>
</div>

[//]: # (SEO metadata)
[//]: # (Description: Samstraumr (S8r): Enterprise-grade framework for resilient, self-healing software systems with adaptive components and event-driven architecture)
[//]: # (Keywords: resilient systems, adaptive software, event-driven architecture, enterprise framework, self-healing systems, java framework, component-based architecture, systems theory, clean architecture)
[//]: # (Author: Eric C. Mumford)
[//]: # (Canonical: https://github.com/heymumford/Samstraumr)
