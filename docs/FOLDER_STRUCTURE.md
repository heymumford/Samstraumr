# Samstraumr Project Structure

This document outlines the organizational structure of the Samstraumr project to help new contributors navigate the codebase effectively.

## Top-Level Structure

- **`Samstraumr/`** - Main code module containing the core library
- **`docs/`** - Comprehensive documentation 
- **`util/`** - Utility scripts for building, testing, and maintenance

## Core Module (`Samstraumr/`)

The Samstraumr core module follows Maven conventions with the following structure:

- **`Samstraumr/samstraumr-core/`** - Core implementation
  - **`src/main/java/`** - Source code
  - **`src/main/resources/`** - Configuration files and resources
  - **`src/test/java/`** - Test code
  - **`src/test/resources/`** - Test data and resources

### Source Code Organization

- **`org.samstraumr.tube`** - Core Tube implementation
- **`org.samstraumr.tube.bundle`** - Bundle implementation (legacy)
- **`org.samstraumr.tube.composite`** - Composite implementation

### Test Code Organization

Tests are organized by type and function:
- **`org.samstraumr.tube.test`** - Common test utilities
- **`org.samstraumr.tube.steps`** - Cucumber step definitions
- **`org.samstraumr.tube.test.annotations`** - Test annotations
- **`org.samstraumr.tube.test.runners`** - Test runners for different test categories

## Documentation (`docs/`)

Documentation is organized by purpose:

- **`docs/concepts/`** - Core theoretical concepts
- **`docs/guides/`** - How-to guides and tutorials
- **`docs/reference/`** - Technical reference information
- **`docs/testing/`** - Testing approach and documentation
- **`docs/contribution/`** - Guidelines for contributing
- **`docs/research/`** - Research papers and proposals
- **`docs/proposals/`** - Design proposals
- **`docs/compatibility/`** - Compatibility information
- **`docs/site/`** - Templates for site generation

## Utility Scripts (`util/`)

Utilities organized by function:

- **`util/badges/`** - Build status badge generation
- **`util/build/`** - Build optimization scripts
- **`util/maintenance/`** - Maintenance utilities
- **`util/quality/`** - Quality check scripts
- **`util/test/`** - Test execution utilities

## Naming Conventions

- **Files**: Use kebab-case for documentation (e.g., `core-concepts.md`)
- **Directories**: Use lowercase with dashes for multi-word directories (e.g., `util/test/`)
- **Java Classes**: Follow Java naming conventions with PascalCase
- **Scripts**: Use kebab-case with .sh extension for shell scripts

## Best Practices

1. **Keep it Flat**: Limit directory depth to improve navigation
2. **Descriptive Names**: Use clear, descriptive names rather than abbreviations
3. **Readme Files**: Include README.md files in key directories
4. **Related Files**: Keep related files together
5. **Consistent Structure**: Follow established patterns for new additions