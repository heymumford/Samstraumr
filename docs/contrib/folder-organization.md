<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# S8r Framework Directory Structure

This document provides a comprehensive overview of the repository structure, the purpose of each directory, and guidelines for maintaining clean folder organization across the project.

## Root Directories

```
Samstraumr/           # Core framework implementation and Maven project structure
├── samstraumr-core/  # Main implementation of the S8r framework components
│   ├── src/main/     # Production code organized by package
│   └── src/test/     # Test code mirroring main package structure
└── src/              # Maven site configuration and documentation

backup-legacy/        # Archived code preserved for reference purposes

docs/                 # Framework documentation, guides, and reference materials
├── architecture/     # Architectural documents and design decisions
├── concepts/         # Core conceptual documentation
├── guides/           # User guides and tutorials
└── reference/        # Reference materials and standards

quality-tools/        # Code quality configuration files and tools

src/                  # Top-level implementation examples and templates

util/                 # Utility scripts, tools, and build helpers
├── bin/              # Executable scripts
├── lib/              # Reusable shell script libraries
└── scripts/          # Maintenance and build scripts
```

## Directory Purposes

### `/Samstraumr`
Core implementation of the S8r framework and Maven project structure. Contains the primary codebase organized according to Maven conventions, with clear separation between implementation and test code.

**Key Responsibilities:**
- Framework core implementation
- Maven project configuration
- Version management

### `/Samstraumr/samstraumr-core`
Main implementation of the S8r framework components, following Clean Architecture principles to organize code by responsibility and domain.

**Key Responsibilities:**
- Domain entities and logic
- Application services
- Interface adapters
- Infrastructure implementations
- Testing frameworks and fixtures

### `/backup-legacy`
Archived code and implementations preserved for reference and backward compatibility analysis. This directory is not actively maintained but serves as historical reference.

**Key Responsibilities:**
- Historical implementation reference
- Legacy patterns documentation
- Migration source material

### `/docs`
Framework documentation, user guides, architectural documents, and reference materials organized by audience and purpose.

**Key Responsibilities:**
- User-facing documentation
- Architecture and design documentation
- Standards and conventions
- Development guidelines

### `/quality-tools`
Configuration files for code quality tools including checkstyle, PMD, and SpotBugs.

**Key Responsibilities:**
- Static analysis configuration
- Code style rules
- Quality assurance tooling

### `/src`
Top-level implementation examples and templates for users to reference.

**Key Responsibilities:**
- Example implementations
- Integration patterns
- User templates

### `/util`
Utility scripts, build tools, and development helpers for framework maintenance.

**Key Responsibilities:**
- Build automation
- Development workflow tools
- Maintenance scripts
- CI/CD integration

## Folder Creation Guidelines

### When to Create a New Folder

A new folder should only be created when ALL of the following criteria are met:

1. You have 5+ related files that share a distinct responsibility
2. The grouping represents a bounded context in the domain
3. Files share a development and release lifecycle
4. The grouping represents a clear architectural layer or component
5. The files collectively implement a significant capability

### When NOT to Create a New Folder

Avoid creating a new folder when:

1. You have fewer than 5 related files
2. Files can be logically organized using naming conventions
3. The grouping creates artificial separation between related concepts
4. The directory would create a deep nesting structure (>3 levels deep)
5. The directory duplicates purpose with another existing directory

### Folder Flattening Strategy

When encountering deep folder structures, consider these flattening strategies:

1. **File Prefixing**: Use consistent prefixes in filenames to indicate relationships
   - Example: `component-validation.js`, `component-creation.js` instead of `/component/validation.js`, `/component/creation.js`

2. **Package Consolidation**: Combine small, related packages into a single package with clear naming
   - Example: Combine `/model/user` and `/model/profile` into `/model` with `user-model.js` and `profile-model.js`

3. **README Documentation**: Document relationships between files in the README rather than creating folders
   - Example: Document in the README that "Files prefixed with 'auth-' handle authentication concerns"

## Naming Conventions

### Java Classes

```
[Domain][Component][Type].java

Examples:
- ComponentRepository.java
- UserAuthenticationService.java
- OrderProcessingValidator.java
```

### Test Files

```
[Component][Scenario]Test.java

Examples:
- ComponentInitializationTest.java
- UserAuthenticationFailureTest.java
- OrderProcessingValidationTest.java
```

### Documentation Files

```
[topic]-[subtopic].md

Examples:
- component-lifecycle.md
- testing-strategy.md
- folder-organization.md
```

### Packages

```
org.s8r.[layer].[domain].[component]

Examples:
- org.s8r.domain.component
- org.s8r.application.service
- org.s8r.infrastructure.persistence
```

## README Requirements

Each directory must contain a README.md file that includes:

1. **Purpose**: Clear statement of the directory's purpose and responsibility
2. **Contents**: List of key files/subdirectories with brief descriptions
3. **Relationships**: How this directory relates to other parts of the system
4. **Naming Conventions**: Specific naming patterns used within this directory
5. **Usage Examples**: Brief examples of how to use the components (if applicable)

## Clean Architecture Mapping

Samstraumr follows Clean Architecture principles, with directories mapped to architectural layers:

| Layer | Directory | Purpose |
|-------|-----------|---------|
| Domain | `org.s8r.domain` | Core entities and business logic |
| Application | `org.s8r.application` | Use cases and services |
| Interfaces | `org.s8r.adapter` | Adapters for external interfaces |
| Infrastructure | `org.s8r.infrastructure` | Technical implementations |

See `docs/architecture/directory-structure.md` for detailed mapping and diagrams.

## Directory Maintenance

Regular maintenance activities include:

1. **Folder Analysis**: Run `util/scripts/flatten-directories.sh` to identify folders that could benefit from flattening
2. **README Updates**: Ensure all directories have up-to-date README files
3. **Directory Reviews**: Periodically review directory structure during refactoring efforts
4. **Documentation Updates**: Keep `FOLDERS.md` and related documentation in sync with actual structure

For detailed folder management guidelines, see `docs/contrib/folder-management-guidelines.md`

## Maximum Directory Depth Policy

The Samstraumr repository enforces a **maximum directory depth of 9 levels** from the repository root. This limit:

1. Improves navigation and discoverability
2. Reduces cognitive load when working with the codebase
3. Encourages flatter directory structures and better file organization
4. Prevents excessive hierarchy that complicates maintenance

### Identifying Deep Paths

Use the directory analysis tool to identify paths that exceed the maximum depth:

```bash
./util/scripts/flatten-directories.sh
```

### Fixing Deep Paths

If you encounter paths that exceed the 9-level limit:

1. Follow the recommendations in `docs/planning/folder-flattening-plan.md`
2. Use file prefixes instead of deep directory nesting
3. Reorganize packages to reduce path depth
4. Generate specific refactoring commands with:

```bash
./util/scripts/flatten-directories.sh --recommend
```

All pull requests must pass the directory depth verification check before they can be merged..
