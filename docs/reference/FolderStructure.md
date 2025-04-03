# Samstraumr Project Structure

*Last Updated: April 3, 2025*

This document provides a comprehensive overview of the Samstraumr project structure to help new contributors navigate the codebase effectively.

## Table of Contents

- [Overview](#overview)
- [Top-Level Structure](#top-level-structure)
- [Core Module Structure](#core-module-structure)
- [Documentation Organization](#documentation-organization)
- [Utility Scripts](#utility-scripts)
- [Naming Conventions](#naming-conventions)
- [Key Files Reference](#key-files-reference)
- [Best Practices](#best-practices)
- [Directory Structure Map](#directory-structure-map)

## Overview

Samstraumr follows a multi-module Maven project structure with a focus on maintainability and clarity. The design emphasizes a flat hierarchy with clear separation of concerns between core functionality, documentation, and utility scripts.

## Top-Level Structure

The project root contains these main directories:

| Directory | Purpose |
|-----------|---------|
| **`Samstraumr/`** | Main code module containing the core library |
| **`docs/`** | Comprehensive documentation organized by topic |
| **`util/`** | Utility scripts for building, testing, and maintenance |

Top-level files include:

- **`pom.xml`**: Parent Maven POM file
- **`README.md`**: Project introduction and overview
- **`CLAUDE.md`**: Special configuration file for AI tooling
- **`LICENSE`**: Mozilla Public License 2.0

## Core Module Structure

The core implementation resides in `Samstraumr/samstraumr-core/` and follows standard Maven conventions:

```
Samstraumr/samstraumr-core/
├── pom.xml                      # Module POM file
├── src/
│   ├── main/
│   │   ├── java/                # Source code
│   │   │   └── org/samstraumr/tube/
│   │   │       ├── bundle/      # Legacy bundle implementation
│   │   │       ├── composite/   # Composite implementation
│   │   │       └── ...          # Core tube classes
│   │   └── resources/           # Configuration files
│   │       ├── log4j2.xml       # Logging configuration
│   │       └── ...
│   └── test/
│       ├── java/                # Test source code
│       │   └── org/samstraumr/tube/
│       │       ├── steps/       # Cucumber step definitions
│       │       ├── test/        # Test utilities
│       │       └── ...          # Test suites and cases
│       └── resources/           # Test resources
│           ├── composites/      # Composite test resources
│           ├── tube/            # Tube test resources
│           └── ...
└── logs/                        # Log output directory
```

### Source Code Packages

| Package | Purpose | Key Classes |
|---------|---------|-------------|
| `org.samstraumr.tube` | Core tube implementation | `Tube`, `Environment`, `TubeStatus` |
| `org.samstraumr.tube.bundle` | Legacy bundle implementation | `Bundle`, `BundleFactory` |
| `org.samstraumr.tube.composite` | Composite pattern implementation | `Composite`, `CompositeFactory` |

### Test Code Organization

| Package | Purpose | Key Classes |
|---------|---------|-------------|
| `org.samstraumr.tube.test` | Common test utilities | `RunTests` |
| `org.samstraumr.tube.steps` | Cucumber step definitions | `*Steps.java` classes |
| `org.samstraumr.tube.test.annotations` | Test annotations | Test categorization annotations |
| `org.samstraumr.tube.test.runners` | Test runners | `ATLTestRunner`, `BTLTestRunner` |

## Documentation Organization

Documentation in the `docs/` directory is organized by topic:

| Directory | Content | Key Files |
|-----------|---------|-----------|
| `docs/concepts/` | Core theoretical concepts | `core-concepts.md`, `state-management.md` |
| `docs/guides/` | How-to guides and tutorials | `getting-started.md`, `tube-patterns.md` |
| `docs/reference/` | Technical reference information | `glossary.md`, `configuration-reference.md` |
| `docs/testing/` | Testing approach | `test-strategy.md`, `bdd-with-cucumber.md` |
| `docs/contribution/` | Contribution guidelines | `code-standards.md`, `configuration-standards.md` |
| `docs/research/` | Research papers | `llm-context-proposal.md` |
| `docs/proposals/` | Design proposals | Various design proposals |
| `docs/compatibility/` | Compatibility information | `COMPATIBILITY_REPORT.md` |
| `docs/site/` | Templates for site generation | Build report templates |

Top-level documentation files include:

- `docs/reference/folder-structure.md`: This document
- `docs/reference/standards/java-naming-standards.md`: Java naming conventions
- `docs/reference/standards/logging-standards.md`: Logging conventions and standards
- `docs/reference/standards/documentation-standards.md`: Documentation guidelines

## Utility Scripts

Utility scripts in the `util/` directory are organized by function:

| Directory | Purpose | Key Scripts |
|-----------|---------|-------------|
| `util/badges/` | Build status badge generation | `generate-badges.sh` |
| `util/build/` | Build optimization scripts | `build-optimal.sh`, `generate-build-report.sh` |
| `util/maintenance/` | Maintenance utilities | `cleanup-maven.sh`, `update-version.sh` |
| `util/quality/` | Quality check scripts | `build-checks.sh`, `check-encoding.sh` |
| `util/test/` | Test execution utilities | `run-tests.sh`, `run-atl-tests.sh`, `map-test-type.sh` |

The root of `util/` contains:

- `version`: The version management utility script
- `README.md`: Utility scripts documentation

## Naming Conventions

Samstraumr follows these file and directory naming conventions:

| Item Type | Convention | Example |
|-----------|------------|---------|
| Documentation files | kebab-case.md | `core-concepts.md` |
| Directories | lowercase-with-dashes | `util/build/` |
| Java classes | PascalCase | `TubeLogger.java` |
| Java interfaces | PascalCase | `TubeInitializer.java` |
| Shell scripts | kebab-case.sh | `run-tests.sh` |
| Properties | kebab-case.properties | `version.properties` |
| XML configuration | lowercase.xml | `log4j2.xml` |

## Key Files Reference

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `log4j2.xml` | `Samstraumr/samstraumr-core/src/main/resources/` | Logging configuration |
| `version.properties` | `Samstraumr/` | Version information |
| `checkstyle.xml` | `Samstraumr/` | Code style configuration |

### Important Documentation Files

| File | Location | Purpose |
|------|----------|---------|
| `CLAUDE.md` | Project root | Special configuration file for AI tooling |
| `docs/guides/getting-started.md` | `docs/guides/` | Initial setup guide |
| `docs/reference/glossary.md` | `docs/reference/` | Terminology definitions |
| `docs/contribution/code-standards.md` | `docs/contribution/` | Coding standards |
| `docs/reference/configuration-reference.md` | `docs/reference/` | Configuration reference |

## Best Practices

1. **Keep it Flat**: Limit directory depth to improve navigation
2. **Descriptive Names**: Use clear, descriptive names rather than abbreviations
3. **README Files**: Include README.md files in key directories
4. **Related Files**: Keep related files together
5. **Consistent Structure**: Follow established patterns for new additions
6. **Single Responsibility**: Each directory should have a clear, single purpose
7. **Cross-References**: Use relative links between documentation files
8. **Version Documentation**: Update "Last Updated" dates when changing docs

## Directory Structure Map

```
Samstraumr/
├── docs/                        # Documentation
│   ├── concepts/                # Core concepts
│   ├── contribution/            # Contribution guidelines
│   ├── guides/                  # How-to guides
│   ├── reference/               # Reference information
│   ├── research/                # Research papers
│   ├── testing/                 # Testing approach
│   ├── proposals/               # Design proposals
│   ├── compatibility/           # Compatibility information
│   └── site/                    # Site generation templates
├── Samstraumr/                  # Core module
│   ├── samstraumr-core/         # Core implementation
│   │   ├── src/                 # Source code
│   │   └── ...
│   ├── pom.xml                  # Module POM
│   └── ...
├── util/                        # Utility scripts
│   ├── badges/                  # Badge generation
│   ├── build/                   # Build utilities
│   ├── maintenance/             # Maintenance utilities
│   ├── quality/                 # Quality checks
│   ├── test/                    # Test utilities
│   └── ...
├── pom.xml                      # Parent POM
└── ...                          # Other top-level files
```