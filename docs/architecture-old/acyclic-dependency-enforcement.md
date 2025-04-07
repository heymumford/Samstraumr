# Acyclic Dependency Enforcement Strategy

This document outlines the Samstraumr project's strategy for enforcing acyclic dependencies throughout the codebase, as mandated by ADR-0012.

## Overview

Circular dependencies in software create maintenance challenges, increase coupling, and make the system harder to understand. They can lead to unintended side effects, complicate testing, and hinder modular design. Samstraumr enforces a strict acyclic dependency graph to maintain architectural integrity.

## Detection Strategy

Samstraumr uses automated tests to detect and prevent circular dependencies at various levels:

1. **Package-level dependencies**:
   - Scans Java source files to extract import statements
   - Builds a dependency graph representing package relationships
   - Uses depth-first search to detect cycles in the dependency graph
   - Reports any circular dependencies with detailed diagnostics

2. **Clean Architecture layer dependencies**:
   - Validates that dependencies flow according to Clean Architecture principles
   - Ensures inner layers (domain) don't depend on outer layers (infrastructure, adapters)
   - Enforces the dependency rule: "dependencies point inward"

3. **Module-level dependencies** (for future multi-module structure):
   - Will analyze Maven module dependencies to prevent circular references between modules
   - Currently a placeholder as the project is single-module

## Rules for Prevention

To avoid circular dependencies, follow these guidelines:

1. **Apply Dependency Inversion Principle**:
   - Use interfaces to invert dependencies
   - Place interfaces in the domain layer
   - Implement interfaces in the infrastructure or adapter layers

2. **Extract Common Interfaces to Shared Packages**:
   - When two packages need to communicate bidirectionally, extract shared interfaces to a common package
   - Example: Instead of A → B → A, create A → Shared ← B

3. **Use Event-Driven Communication**:
   - Decouple components using the event system for cross-cutting concerns
   - Components emit events without knowing subscribers
   - Subscribers handle events without direct references to emitters

4. **Merge Tightly Coupled Packages**:
   - When packages are highly interdependent, consider merging them into a cohesive unit
   - Example: If A and B cannot be decoupled, merge into a single package AB

5. **Follow Clean Architecture Boundaries**:
   - Domain layer should have zero dependencies on outer layers
   - Application layer depends only on domain
   - Infrastructure and adapters depend on domain and application, never the other way

## Implementation in Tests

The `AcyclicDependencyTest` test class verifies:

1. Source code package dependencies are acyclic
2. Test code package dependencies are acyclic (with limited allowances for test utilities)
3. Clean Architecture layer dependencies follow the dependency rule
4. Module dependencies (future) will be acyclic

The test provides detailed error messages when violations occur, suggesting ways to fix circular dependencies.

## Enforcement in CI/CD

The acyclic dependency rules are enforced through:

1. **Automated build tests**:
   - Architecture tests run on every build
   - Tests fail if circular dependencies are detected

2. **Pull request validation**:
   - Architecture tests must pass for PR approval
   - Architectural review includes checking for dependency issues

3. **Documentation generation**:
   - Dependency graphs are generated to visualize package relationships
   - Clean Architecture layer compliance reports are produced

## Tools and Resources

1. **ArchitectureAnalyzer**:
   - Internal utility for analyzing and validating dependencies
   - Provides detailed error messages with suggestions for fixes
   - Used by the automated test suite

2. **References**:
   - [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) by Robert C. Martin
   - [Acyclic Dependencies Principle](https://drive.google.com/file/d/0BwhCYaYDn8EgOGM2ZGFhNmYtNmE4ZS00OGY5LWFkZTYtMjE0ZGNjODQ0MjEx/view) by Robert C. Martin
   - [The Stable Dependencies Principle](https://wiki.c2.com/?StableDependenciesPrinciple)

## Conclusion

By enforcing acyclic dependencies, Samstraumr maintains a more maintainable, understandable, and testable architecture. The automated tests and tooling ensure architectural integrity is preserved as the codebase evolves.