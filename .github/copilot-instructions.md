<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# GitHub Copilot Instructions for Samstraumr

This file provides guidance for GitHub Copilot when working on the Samstraumr repository.

## Repository Overview

Samstraumr (S8r) is an enterprise Java framework for building resilient, self-healing software systems with adaptive components and event-driven architecture. The framework implements Clean Architecture principles with a focus on natural systems theory patterns.

## Technology Stack

- **Language**: Java 21 (minimum Java 17)
- **Build Tool**: Maven 3.9+
- **Testing Frameworks**: 
  - JUnit 5 for unit tests
  - Cucumber/BDD for behavioral tests
  - Mockito for mocking
- **Code Quality**: Spotless, Checkstyle, SpotBugs, JaCoCo
- **Logging**: SLF4J with Log4j2 implementation

## Project Structure

```
Samstraumr/
├── modules/samstraumr-core/    # Core framework implementation
├── docs/                       # Comprehensive documentation
├── bin/                        # CLI scripts and utilities
├── util/                       # Build and utility scripts
├── quality-tools/              # Code quality configurations
├── test-module/                # Test module
└── test-port-interfaces/       # Port interface tests
```

## Coding Standards

### Java Code Style

Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with these modifications:

- **Classes**: PascalCase, descriptive nouns
- **Methods**: camelCase, verb phrases describing actions
- **Variables**: camelCase, descriptive nouns
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase, domain-reversed structure

### Code Quality

All code must pass:
- **Spotless**: Code formatting - run `mvn spotless:apply` before committing
- **Checkstyle**: Coding standards enforcement
- **SpotBugs**: Static bug detection
- **JaCoCo**: 80% minimum code coverage for line and branch coverage

### Documentation

- All public APIs must have comprehensive Javadoc
- Include `@param`, `@return`, and `@throws` tags
- Document assumptions and non-obvious behaviors
- Use `{@link}` to reference other classes and methods

### Error Handling

- Use custom exceptions with descriptive messages
- Log exceptions at appropriate levels before throwing
- Never swallow exceptions without logging
- Use appropriate exception types for different error conditions

## Testing Standards

### Test Organization

Tests are organized by type following the testing pyramid:

- **Unit Tests**: Fast, isolated tests for individual components
- **Component Tests**: Tests for component interactions
- **Integration Tests**: Tests for system-level integration
- **Lifecycle Tests**: Tests for component lifecycle management

### Test Requirements

- Tests must be isolated and not depend on external resources
- Test both happy paths and error conditions
- Use descriptive test method names explaining what is being tested
- Follow BDD approach with Cucumber for behavioral tests
- Use appropriate test categories/tags (@ATL for all tests)

### Running Tests

```bash
# Run all tests
./s8r-test all

# Run specific test type
./s8r-test unit
./s8r-test component
./s8r-test integration

# Run tests with coverage
./s8r-test all --coverage
```

## Build Procedures

### Standard Build Commands

```bash
# Full build with tests
./s8r-build

# Fast build without tests
./s8r-build fast

# Build with quality checks
./s8r-build full
```

### Before Committing

1. Format code: `mvn spotless:apply`
2. Run quality checks: Ensure all quality checks pass
3. Run relevant tests: `./s8r-test <type>`
4. Verify build: `./s8r-build`

## File Organization

### Naming Conventions

- Prefer descriptive file naming over deep nesting
- Every directory should have a README.md explaining its purpose
- New folders require 5+ related files (critical mass principle)

### Location Guidelines

- **Source Code**: Place in `modules/samstraumr-core/src/main/java/org/s8r/`
- **Tests**: Place in `modules/samstraumr-core/src/test/java/org/s8r/test/`
- **Resources**: Place in `modules/samstraumr-core/src/main/resources/`
- **Test Resources**: Place in `modules/samstraumr-core/src/test/resources/`
- **Documentation**: Place in `docs/` with appropriate subdirectory
- **Utilities**: Place in `util/` or `bin/` as appropriate

## Architectural Principles

### Core Concepts

- **Components**: Self-contained processing units with state awareness
- **Composites**: Coordinated component collections forming pipelines
- **Machines**: Orchestrated composites implementing subsystems
- **Event-Driven**: Loose coupling through publish-subscribe patterns
- **Validation**: Comprehensive checks at all system boundaries

### Design Patterns

- Follow Clean Architecture with clear layer separation
- Use event-driven communication patterns
- Implement comprehensive validation at boundaries
- Design for testability and isolation

## Sensitive Areas

### Do Not Modify Without Explicit Permission

- **Authentication/Security modules**: Require security review
- **Core lifecycle management**: Critical for framework stability
- **Version management files**: Managed by specific scripts
- **Maven POM hierarchy**: Changes affect entire build system
- **CI/CD workflows**: Impact entire development process

### Security Considerations

- Never commit secrets or sensitive data
- Use externalized configuration for all configurable values
- Follow principle of least privilege
- Validate all inputs at system boundaries
- Log security events appropriately

## Configuration Management

- Externalize all configuration - no hardcoded values
- Use appropriate configuration files (`.properties`, `.yml`)
- Configuration files should be in `src/main/resources/` or `config/`
- Document all configuration options
- Provide sensible defaults

## Version Control

### Commit Guidelines

- Use clear, descriptive commit messages
- Follow conventional commit format
- Reference issue numbers when applicable
- Keep commits focused on single logical changes
- Use imperative mood ("Add feature" not "Added feature")

### Pull Request Process

- Describe the purpose and scope of changes
- Link to related issues
- Ensure all CI checks pass
- Address review feedback promptly
- Keep PRs focused on single feature or bug fix

## Documentation Requirements

### When to Update Documentation

- Always update documentation when changing public APIs
- Update relevant guides when changing behavior
- Add examples for new features
- Update changelog for notable changes
- Keep README.md current with major changes

### Documentation Standards

- Use Markdown for all documentation
- Follow existing documentation structure
- Include code examples where appropriate
- Use consistent formatting and style
- Ensure all links are valid

## CLI Tools

The repository provides several CLI tools for development:

- `./s8r-build`: Build orchestration
- `./s8r-test`: Test execution
- `./s8r-version`: Version management
- `./s8r-quality-enhanced`: Enhanced quality checks
- See [CLI Reference](../docs/reference/cli-reference.md) for complete list

## Additional Resources

- [Contributing Guidelines](../docs/contribution/contributing.md)
- [Code Standards](../docs/contribution/code-standards.md)
- [Architecture Overview](../docs/architecture/README.md)
- [Core Concepts](../docs/concepts/core-concepts.md)
- [Testing Guide](../docs/dev/test-bdd-cucumber.md)

## Project Status

This project is under active TDD-based development. Check [KANBAN.md](../KANBAN.md) for current status and work in progress.

---

**Remember**: When in doubt, follow existing patterns in the codebase and consult the documentation. Quality and clarity take precedence over speed.
