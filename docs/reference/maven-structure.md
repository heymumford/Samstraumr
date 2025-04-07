<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Maven Structure

This document describes the Maven structure for the Samstraumr project and outlines the planned migration to the S8r framework structure.

## Current Structure

Samstraumr uses a three-tiered Maven structure:

1. **Root POM (`/pom.xml`)**
   - Serves as the project entry point
   - Contains shared configuration and properties
   - Defines quality profiles and plugin management
2. **Modules POM (`/Samstraumr/pom.xml`)**
   - Intermediate POM for managing submodules
   - Coordinates shared configuration among modules
   - Sets up common test profiles
3. **Core Module POM (`/Samstraumr/samstraumr-core/pom.xml`)**
   - Implements the actual framework functionality
   - Defines specific dependencies and test configurations
   - Contains domain-specific Maven profiles

This tiered approach provides excellent flexibility for growth, allowing you to:
- Add new modules easily at the intermediate level
- Share common configuration across all modules
- Maintain independent versioning if needed

## Maven Profiles

### Test profiles

The project includes Maven profiles for running different types of tests:

1. **Industry Standard Test Profiles**:
   - `mvn test -P smoke-tests` (basic system verification)
   - `mvn test -P unit-tests` (individual units)
   - `mvn test -P component-tests` (connected components)
   - `mvn test -P integration-tests` (interactions between parts)
   - `mvn test -P api-tests` (public interfaces)
   - `mvn test -P system-tests` (entire system)
   - `mvn test -P endtoend-tests` (user perspective)
   - `mvn test -P property-tests` (system properties)
2. **Samstraumr Test Profiles**:
   - `mvn test -P orchestration-tests` (basic system verification)
   - `mvn test -P tube-tests` (individual units)
   - `mvn test -P composite-tests` (connected components)
   - `mvn test -P flow-tests` (interactions between parts)
   - `mvn test -P machine-tests` (public interfaces)
   - `mvn test -P stream-tests` (entire system)
   - `mvn test -P acceptance-tests` (user perspective)
   - `mvn test -P adaptation-tests` (system properties)
   - `mvn test -P adam-tube-tests` (all origin tube tests)
   - `mvn test -P adam-tube-atl-tests` (critical origin tube tests)
3. **Utility Profiles**:
   - `mvn test -P skip-quality-checks` (skip quality validations)
   - `mvn test -P atl-tests` (Above The Line - critical tests)

### Quality profiles

The project includes quality-related profiles:

1. **Code Quality**:
   - `spotless` for code formatting
   - `checkstyle` for code standards
   - `spotbugs` for bug detection
2. **Coverage**:
   - `jacoco` for code coverage analysis

## S8r Migration Plan

As part of the ongoing development, the project will migrate to a new structure using "s8r" as the official abbreviation:

### Directory structure

```
/s8r/                      (Renamed from /Samstraumr/)
├── pom.xml                (Modules POM)
├── s8r-core/              (Renamed from samstraumr-core)
│   ├── pom.xml
│   ├── src/
│   └── ...
├── s8r-extensions/        (Future module)
│   ├── pom.xml
│   └── ...
└── s8r-adapters/          (Future module)
    ├── pom.xml
    └── ...
```

### Maven identifiers

The Maven artifacts and group IDs will be updated:

1. **Root POM**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-parent`
2. **Modules POM**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-modules`
3. **Core Module**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-core`

### Package structure

The package structure will follow this pattern:
- `org.s8r.component.*` for framework components
- `org.s8r.env.*` for environment utilities
- `org.s8r.util.*` for common utilities

### Version properties

Version properties will be renamed:
- `s8r.version` instead of `samstraumr.version`
- `version.file.path` to point to `/s8r/version.properties`

## Planned Modules

The S8r framework will include these modules:

1. **s8r-core**: Core framework components
2. **s8r-extensions**: Optional extensions to the core framework
   - Advanced state machines
   - Enhanced logging and monitoring
   - Additional component patterns
3. **s8r-adapters**: Integration with external systems
   - Database connectivity
   - Message broker integration
   - Cloud service adapters
4. **s8r-test**: Dedicated testing utilities
   - Testing abstractions specific to S8r
   - Mock components
   - Test utilities
5. **s8r-examples**: Example applications and patterns
   - Reference implementations
   - Tutorials
   - Benchmarks

## Best Practices

When working with the Maven structure:

1. **Dependency Management**:
   - Define all dependencies in the parent POM's `<dependencyManagement>` section
   - Specify only the artifact ID and not the version in module POMs
   - Use properties for version numbers to ensure consistency
2. **Plugin Management**:
   - Configure plugins in the parent POM's `<pluginManagement>` section
   - Use the same version of each plugin across all modules
3. **Properties**:
   - Define common properties in the parent POM
   - Use properties for versions, encoding, and other shared configuration
4. **Profiles**:
   - Define profiles for different environments (dev, test, prod)
   - Use profiles for optional features or build configurations
   - Keep profile-specific configuration to a minimum
5. **Testing**:
   - Configure Surefire plugin for unit tests
   - Configure Failsafe plugin for integration tests
   - Use appropriate naming conventions for test classes

## References

- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [Maven Best Practices](https://maven.apache.org/guides/mini/guide-configuring-maven.html)
- [Samstraumr Migration Guide](../guides/migration-guide.md)
