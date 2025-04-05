<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Maven Structure for S8r Framework

## Current Structure

The S8r framework uses a three-tiered Maven structure designed for modularity, maintainability, and future growth.

### Structure Overview

1. **Root POM (`/pom.xml`)**
   - Project entry point
   - Shared configuration and properties
   - Quality profiles and plugin management
   - Version management
2. **Modules POM (`/Samstraumr/pom.xml`)**
   - Intermediate POM for submodule management
   - Coordinates shared configuration
   - Common test profiles
3. **Core Module POM (`/Samstraumr/samstraumr-core/pom.xml`)**
   - Core framework functionality
   - Component implementation
   - Specific dependencies and test configurations
   - Domain-specific Maven profiles

## S8r Renaming Structure

To align with the abbreviated name "s8r," the following structure changes will be implemented:

### Directory Structure

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

### Maven Identifiers

The Maven artifacts and group IDs will be updated as follows:

1. **Root POM**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-parent`
2. **Modules POM**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-modules`
3. **Core Module**:
   - `groupId`: `org.s8r`
   - `artifactId`: `s8r-core`

### Package Structure

The Java package structure will follow the pattern:
- `org.s8r.component.*` for framework components
- `org.s8r.env.*` for environment utilities
- `org.s8r.util.*` for common utilities

## Benefits of This Structure

1. **Modularity**: Clear separation of concerns between modules
2. **Consistency**: Unified naming scheme across directories, artifacts, and packages
3. **Scalability**: Easy addition of new modules as the framework grows
4. **Maintainability**: Centralized configuration with localized specialization
5. **Testability**: Dedicated test profiles for different testing strategies

## Planned Future Modules

The S8r framework's tiered structure supports natural expansion with the following planned modules:

1. **s8r-core**: Core framework components
   - Component model implementation
   - Identity and state management
   - Composite and machine orchestration
2. **s8r-extensions**: Optional extensions
   - Advanced state machines
   - Enhanced logging and monitoring
   - Additional component patterns
   - Specialized composites
3. **s8r-adapters**: Integration with external systems
   - Database connectivity
   - Message broker integration
   - Cloud service adapters
   - Event stream processing
4. **s8r-test**: Testing utilities
   - Testing abstractions specific to S8r
   - Mock components
   - Test utilities
   - Performance testing tools
5. **s8r-examples**: Example applications
   - Reference implementations
   - Tutorials
   - Benchmarks

## Implementation Strategy

The implementation of this structure will be carried out in these phases:

1. **Phase 1**: Package structure migration (completed)
   - Created `org.s8r.component.*` package structure
   - Implemented core components
2. **Phase 2**: Directory and artifact renaming (in progress)
   - Rename directories to follow s8r convention
   - Update POM files with new artifact IDs and group IDs
   - Adjust dependencies and references
3. **Phase 3**: Documentation and tooling
   - Update documentation to reference the new naming convention
   - Update build scripts and CI/CD configuration
   - Ensure backward compatibility where needed
4. **Phase 4**: Integration and testing
   - Verify all components work together correctly
   - Ensure tests run properly with the new structure
   - Validate build processes
