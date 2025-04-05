# S8r Framework Directory Structure

This document provides a high-level overview of the repository structure and the purpose of each directory.

## Root Directories

```
Samstraumr/           # Core framework implementation and Maven project structure
├── samstraumr-core/  # Main implementation of the S8r framework components
└── src/              # Maven site configuration and documentation

backup-legacy/        # Archived code preserved for reference purposes

docs/                 # Framework documentation, guides, and reference materials

quality-tools/        # Code quality configuration files and tools

src/                  # Top-level implementation examples and templates

util/                 # Utility scripts, tools, and build helpers
```

## Directory Purposes

### `/Samstraumr`
Core implementation of the S8r framework and Maven project structure.

### `/Samstraumr/samstraumr-core`
Main implementation of the S8r framework components, including domain entities, services, and infrastructure.

### `/backup-legacy`
Archived code and implementations preserved for reference and backward compatibility analysis.

### `/docs`
Framework documentation, user guides, architectural documents, and reference materials.

### `/quality-tools`
Configuration files for code quality tools including checkstyle, PMD, and SpotBugs.

### `/src`
Top-level implementation examples and templates for users to reference.

### `/util`
Utility scripts, build tools, and development helpers for framework maintenance.

## Folder Creation Guidelines

A new folder should only be created when:

1. You have 5+ related files that share a distinct responsibility
2. The grouping represents a bounded context in the domain
3. Files share a lifecycle (developed/deployed together)
4. The grouping represents a clear architectural layer

Otherwise, prefer file naming conventions to organize content within an existing directory.

## Naming Conventions

In general, follow these patterns for files:

- Java source files: `[domain]-[component]-[type].java`
- Test files: `[component]-[scenario]-test.java`
- Documentation: `[topic]-[subtopic].md`

See individual folder README files for specific conventions within each directory.