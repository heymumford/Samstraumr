# Folder Management Guidelines

## Overview

The S8r Framework follows a carefully designed directory structure to balance organization with accessibility. This document provides guidelines for managing folders and determining when to create new directories versus using file naming conventions.

## When to Create a New Folder

A new folder should only be created when:

1. You have 5+ related files that share a distinct responsibility
2. The grouping represents a bounded context in your domain
3. Files share a lifecycle (developed/deployed together)
4. The grouping is a fundamental part of the architectural layering
5. The grouping is required by the build system or framework conventions

## When NOT to Create a New Folder

Avoid creating new folders when:

1. You have fewer than 5 related files
2. The distinction can be clearly expressed in the filename
3. The grouping is temporary or experimental
4. The folder would contain only a single file
5. The categorization is based on arbitrary or changing criteria

## Preferred Alternatives to Deep Nesting

Instead of creating deep directory structures, prefer:

1. **Clear File Naming Conventions**: `domain-component-function.java` instead of `/domain/component/function.java`
2. **Package Organization**: Use Java package structure rather than filesystem folders for code organization
3. **Readme Documentation**: Document relationships between files in README.md files
4. **Interface Segregation**: Split large interfaces/classes instead of grouping many small ones

## Folder Documentation Requirements

Every directory must contain a README.md file that includes:

1. A clear one-sentence purpose statement
2. Content criteria for what belongs in the directory
3. Naming conventions specific to the directory
4. Guidance on what should NOT be included in the directory
5. References to related directories

## Process for Adding a New Directory

1. Discuss the need for the new directory with the team
2. Document the purpose and conventions in a README.md following the template
3. Update the FOLDERS.md file in the project root
4. Announce the new directory structure in the changelog

## Example Decision Process

**Scenario**: You have 3 validator classes for different aspects of component identity.

**Bad Decision**: Create a `/validators/identity/` folder structure.

**Good Decision**: Name the files `identity-name-validator.java`, `identity-format-validator.java`, and `identity-uniqueness-validator.java` and place them in the existing `/validators/` directory with proper naming conventions.

## Directory Audit Schedule

The repository directory structure should be reviewed:

1. Before each major release
2. When the codebase exceeds 20% growth since the last audit
3. When new architectural patterns are introduced

During audits, folders containing fewer than 3 files should be evaluated for consolidation.