# Code Standards

This document outlines the coding standards for contributing to Samstraumr.

## General Principles

- **Clarity over Cleverness**: Prefer clear, readable code over clever optimizations
- **Consistency**: Follow established patterns in the codebase
- **Testability**: Design code with testing in mind
- **Documentation**: Document public APIs and complex logic

## Java Code Style

Samstraumr follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with the following modifications:

### Naming Conventions

- **Classes**: PascalCase, descriptive nouns
- **Interfaces**: PascalCase, descriptive nouns
- **Methods**: camelCase, verb phrases describing actions
- **Variables**: camelCase, descriptive nouns
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase, domain-reversed structure

### Structural Guidelines

- **Class Structure**:
  1. Static fields
  2. Instance fields
  3. Constructors
  4. Public methods
  5. Protected methods
  6. Private methods
  7. Inner classes/interfaces

- **Method Structure**:
  1. Input validation
  2. Core logic
  3. Result preparation and return

### Imports

- Use specific imports (not wildcard imports)
- Group imports in the following order:
  1. Java standard library
  2. Third-party libraries
  3. Samstraumr imports

### Documentation

- All public APIs must have Javadoc
- Include `@param`, `@return`, and `@throws` tags as appropriate
- Document any assumptions or non-obvious behaviors
- Use `{@link}` to reference other classes and methods

### Error Handling

- Use custom exceptions with descriptive messages
- Log exceptions at appropriate levels before throwing
- Never swallow exceptions without logging
- Use appropriate exception types for different error conditions

## Quality Checks

All code must pass the following quality checks:

- **Spotless**: Code formatting
- **SonarQube**: Comprehensive static code analysis
- **Checkstyle**: Coding standards
- **SpotBugs**: Bug detection
- **JaCoCo**: Code coverage (minimum threshold required)

## Testing Standards

- Tests should be isolated and not depend on external resources
- Use appropriate test categories (unit, component, integration, etc.)
- Test both happy paths and error conditions
- Use descriptive test method names explaining what is being tested
- Follow the BDD approach with Cucumber for behavioral tests

## Version Control Standards

- Make atomic commits that address a single concern
- Use descriptive commit messages in the imperative mood
- Reference issue numbers in commit messages when applicable
- Keep pull requests focused on a single feature or bug fix