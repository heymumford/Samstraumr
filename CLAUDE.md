# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

**IMPORTANT**: This file extends `~/claude.md` (universal user preferences). Read that file first for core principles, development tools, and general workflows. This file contains ONLY Samstraumr-specific information.

## Project Overview

Samstraumr (S8r) is an enterprise Java framework for building resilient, self-healing software systems with adaptive components and event-driven architecture. The framework implements Clean Architecture principles inspired by natural systems theory - components behave like biological organisms with lifecycle states, hierarchical identities, and adaptive behaviors.

**Key Architectural Concepts:**
- **Components**: Self-contained processing units with biological-inspired lifecycle (CONCEPTION → CONFIGURING → SPECIALIZING → ACTIVE → TERMINATED)
- **Composites**: Coordinated collections of components forming data processing pipelines
- **Machines**: Orchestrated composites implementing complete subsystems
- **Event-Driven**: Loose coupling through publish-subscribe communication patterns
- **Identity Hierarchy**: Every component has a hierarchical identity with lineage tracking (Adam components vs child components)
- **Validation-First**: Comprehensive validation at all system boundaries

## Technology Stack

- **Java**: 21 (minimum 17) - uses modern Java features
- **Build**: Maven 3.9+ with multi-module structure
- **Testing**: JUnit 5, Cucumber/BDD, Mockito
- **Quality**: Spotless, Checkstyle, SpotBugs, JaCoCo (80% coverage minimum)
- **Logging**: SLF4J with Log4j2

## Build and Test Commands

### Build Commands
```bash
# Standard compilation
./s8r-build compile

# Compile with tests
./s8r-build test

# Full build with quality checks
./s8r-build verify

# Clean build
./s8r-build test --clean

# Build with specific Maven profile
./s8r-build verify --profile quality-checks
```

### Test Commands
```bash
# Run all tests
./s8r-test all

# Run specific test levels
./s8r-test unit          # L0 unit tests
./s8r-test component     # L1 component tests
./s8r-test integration   # L2 integration tests
./s8r-test system        # L3 system tests

# Run with coverage
./s8r-test all --coverage

# Run specific tags
./s8r-test --tags "@Lifecycle and @Functional"

# Verify test suite structure
./s8r-test --verify

# Fix common test issues
./s8r-test --fix
```

### Single Test Execution
```bash
# Run a single test file
mvn test -Dtest=ClassName

# Run a single test method
mvn test -Dtest=ClassName#methodName

# Run isolated tests
./run-isolated-test.sh path/to/TestFile.java
```

### Quality Checks
```bash
# Format code (MUST run before committing)
mvn spotless:apply

# Check formatting
mvn spotless:check

# Run all quality checks
mvn verify -P quality-checks

# Enhanced quality profile
mvn verify -P enhanced-quality
```

## Code Architecture

### Package Structure
The codebase follows Clean Architecture with clear layer separation:

```
org.s8r/
├── component/           # Domain layer - core business logic
│   ├── Component.java   # Main component with lifecycle
│   ├── Composite.java   # Component collections
│   ├── Machine.java     # Orchestrated subsystems
│   ├── Identity.java    # Hierarchical identity system
│   ├── Environment.java # Component environment/configuration
│   ├── State.java       # Unified lifecycle/operational states
│   └── Logger.java      # Component-specific logging
├── application/         # Application layer - use cases
│   ├── service/        # Application services
│   ├── port/           # Interface definitions (hexagonal architecture)
│   └── dto/            # Data transfer objects
├── adapter/            # Adapter layer - external integrations
│   ├── in/cli/         # CLI adapters
│   └── [other adapters]
└── core/               # Legacy/transitional code being migrated
```

### Key Architectural Patterns

**Clean Architecture Layers:**
1. **Domain** (`org.s8r.component`) - Core business logic, no dependencies on outer layers
2. **Application** (`org.s8r.application`) - Use cases and ports (interfaces)
3. **Adapter** (`org.s8r.adapter`) - External integrations implementing ports

**Component Lifecycle:**
Components follow a biological-inspired state machine:
- CONCEPTION → Component created
- CONFIGURING → Boundaries established
- SPECIALIZING → Functions determined
- ACTIVE → Fully operational
- TERMINATED → Graceful shutdown

**Identity System:**
- Every component has a unique hierarchical identity
- "Adam" components (no parent) vs child components (created from parent)
- Lineage tracking for debugging and audit
- Identity includes environment parameters in checksum

**Event-Driven Communication:**
- Components communicate through events, not direct calls
- EventListener interface for subscribing to events
- StateTransitionListener for lifecycle events
- Decouples components for better testability

## Testing Standards

### Test Organization by Level

Tests follow the testing pyramid with BDD/Cucumber:

- **L0 (Unit)**: Fast, isolated component tests - `@L0_Unit`
- **L1 (Component)**: Component interaction tests - `@L1_Component`
- **L2 (Integration)**: System integration tests - `@L2_Integration`
- **L3 (System)**: End-to-end system tests - `@L3_System`

All tests must have `@ATL` tag for test discovery.

### Test File Locations

```
modules/samstraumr-core/src/test/
├── java/org/s8r/test/
│   ├── unit/          # Unit tests
│   ├── component/     # Component tests
│   ├── integration/   # Integration tests
│   └── steps/         # Cucumber step definitions
└── resources/
    ├── features/      # Cucumber .feature files
    ├── cucumber.properties
    └── junit-platform.properties
```

### Test Requirements

- All tests must be isolated (no external dependencies)
- Test both happy paths and error conditions
- Use descriptive test names: `shouldDoSomething_whenCondition_thenExpectedOutcome`
- BDD scenarios in `.feature` files with step definitions
- Minimum 80% line and branch coverage (JaCoCo)
- Use `@Tag` annotations for categorization

### Running Problematic Tests

Some tests require isolation due to static state or resource contention:

```bash
# Move tests to isolation
./move-problematic-tests.sh

# Restore tests after fixing
./restore-problematic-tests.sh
```

## Development Workflow

### Samstraumr-Specific Pre-Commit Checklist

1. Read relevant code first - understand Clean Architecture boundaries
2. Format code: `mvn spotless:apply` (REQUIRED before commit)
3. Run quality checks: `mvn verify -P quality-checks`
4. Run relevant tests: `./s8r-test [type]`
5. Ensure 80% coverage threshold met

### Samstraumr Code Conventions

- Follow Google Java Style Guide (enforced by Spotless)
- **Always** run `mvn spotless:apply` before committing
- All public APIs require Javadoc with `@param`, `@return`, `@throws`
- Component-specific exceptions: `ComponentException`, `CompositeException`, `MachineException`
- Use builder pattern for component creation (see Common Gotchas)

## Maven Configuration

### Parent POM Hierarchy

```
pom.xml (samstraumr-parent)
├── modules/pom.xml (samstraumr-modules)
│   └── samstraumr-core/pom.xml
├── test-module/pom.xml
└── test-port-interfaces/pom.xml
```

### Useful Maven Profiles

- `tdd-development` - Active by default, 80% coverage requirements
- `coverage` - Generate and enforce coverage reports
- `quality-checks` - Run all quality tools
- `enhanced-quality` - Stricter quality rules
- `skip-tests` - Skip all tests (use sparingly)

### Maven Properties

Key properties defined in parent POM:
- `maven.compiler.source=21` - Java version
- `jacoco.line.coverage=0.80` - Line coverage threshold
- `jacoco.branch.coverage=0.80` - Branch coverage threshold
- `test.tags=@ATL` - Default test tags

## CLI Tools

The repository provides extensive CLI tooling under `bin/` and symlinks in project root:

- `./s8r-build` - Build orchestrator with multiple modes
- `./s8r-test` - Unified test runner
- `./s8r-version` - Version management (bump, get, set)
- `./s8r-quality-enhanced` - Enhanced quality checks
- `./s8r-test-coverage` - Coverage analysis
- `./s8r-port-coverage` - Port interface test coverage

All scripts use bash5 (not sh or zsh) and follow conventions in `util/lib/unified-common.sh`.

## Common Gotchas

### Component Creation

Always use the Builder pattern for components:
```java
// CORRECT
Component component = Component.builder()
    .withReason("data-processor")
    .withEnvironment(env)
    .build();

// Components require non-null Environment
Environment env = new Environment.Builder("my-env")
    .withParameter("key", "value")
    .build();
```

### State Transitions

State transitions are validated - illegal transitions throw `InvalidStateTransitionException`:
```java
// Valid transitions are defined in State enum
// CONCEPTION → CONFIGURING → SPECIALIZING → ACTIVE → TERMINATED
```

### Testing Static State

Components use static state for some operations. Tests that fail intermittently may need isolation:
```bash
./move-problematic-tests.sh  # Move to isolated/
# Fix static state issues
./restore-problematic-tests.sh  # Restore after fix
```

### Maven Module References

When adding dependencies between modules, use dependency management from parent POM - versions are centralized.

## Documentation Structure

Comprehensive documentation in `docs/`:

- `docs/architecture/` - Architecture documentation and ADRs
- `docs/concepts/` - Core concepts and theory
- `docs/guides/` - How-to guides and tutorials
- `docs/reference/` - API reference and CLI docs
- `docs/dev/` - Development processes (TDD, BDD)
- `docs/contrib/` - Contribution guidelines

When changing public APIs or behavior, update relevant documentation.

## CI/CD Pipelines

Multiple GitHub Actions workflows in `.github/workflows/`:

- `samstraumr-pipeline.yml` - Main CI pipeline
- `ci-fast.yml` - Quick validation
- `ci-comprehensive.yml` - Full quality checks
- `ci-security.yml` - Security scans
- `documentation-ci.yml` - Documentation validation

All PRs must pass the main pipeline before merging.

## Areas Requiring Explicit Permission

**DO NOT modify without approval:**
- Core lifecycle management (`Component`, `State` transitions)
- Identity system (`Identity.java`, hierarchy logic)
- Maven parent POM structure and version management
- CI/CD workflow configurations (`.github/workflows/`)
- Security-related code in `application/port/security/`
- Build scripts in `util/` and `bin/`

**Always ask before:**
- Breaking API changes in public interfaces
- Changes to test infrastructure or test runners
- Changes affecting backward compatibility

## Version History

Project uses semantic versioning managed by `./s8r-version`:
- Current: 3.1.1 (see `pom.xml`)
- Version tracked in `modules/version.properties`
- Changelog in `docs/reference/release/changelog.md`

## Active Development Focus

The project is in active TDD-based development with focus on:
- Completing lifecycle test coverage
- Migrating legacy `core.tube` package to simplified `component` package
- Enhancing BDD/Cucumber test scenarios
- Improving documentation coverage

Check `KANBAN.md` for current work in progress and priorities.
