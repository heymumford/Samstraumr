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

# Directory Structure

This document provides a visual representation of the ideal S8r Framework directory structure, focusing on clarity, discoverability, and maintainability. It serves as the authoritative reference for organizing code and documents within the repository.

## Repository Root Structure

```
Samstraumr/
├── .s8r/                   # Configuration files and metadata
├── modules/                # Maven modules (core implementation)
│   ├── samstraumr-core/    # Core implementation modules
│   └── src/                # Maven site configuration
├── docs/                   # Documentation
│   ├── architecture/       # Architecture documentation
│   ├── concepts/           # Core conceptual documentation
│   ├── guides/             # User and developer guides
│   ├── reference/          # Technical reference materials
│   └── standards/          # Coding and documentation standards
├── util/                   # Utilities and scripts
│   ├── bin/                # Executable scripts 
│   ├── lib/                # Reusable libraries
│   └── scripts/            # Maintenance and build scripts
├── quality-tools/          # Code quality configuration
└── src/                    # Example applications and implementations
```

## Clean Architecture Directory Structure

The core implementation follows Clean Architecture principles with clear layering:

```
samstraumr-core/src/main/java/org/
├── s8r/                    # Framework core implementation (simplified API)
│   ├── component/          # Component-related core classes
│   │   ├── core/           # Core component definitions
│   │   ├── composite/      # Composite component structure
│   │   └── machine/        # Machine orchestration
│   └── adapter/            # Core adapters
└── samstraumr/             # Full implementation 
    ├── domain/             # Domain layer (business entities and logic)
    │   ├── component/      # Component domain entities
    │   ├── identity/       # Identity and addressing concerns
    │   ├── lifecycle/      # Lifecycle state management
    │   ├── event/          # Domain events
    │   └── exception/      # Domain exceptions
    ├── application/        # Application layer (use cases)
    │   ├── service/        # Application services
    │   ├── port/           # Input/output ports (interfaces)
    │   └── dto/            # Data transfer objects
    ├── infrastructure/     # Infrastructure layer (technical details)
    │   ├── persistence/    # Repository implementations
    │   ├── event/          # Event handling infrastructure
    │   ├── logging/        # Logging implementation
    │   └── config/         # System configuration
    └── adapter/            # Adapters layer (integration points)
        ├── in/             # Input adapters (CLI, REST, etc.)
        └── out/            # Output adapters (repositories, external systems)
```

## File Naming Conventions

Files within each directory should follow consistent naming patterns:

1. **Domain Layer**: `[Entity][Concern].java`
   - Example: `Component.java`, `ComponentFactory.java`

2. **Application Layer**: `[UseCase]Service.java` or `[Entity]Repository.java`
   - Example: `ComponentService.java`, `MachineRepository.java`

3. **Infrastructure Layer**: `[Technology][Entity][Implementation].java`
   - Example: `InMemoryComponentRepository.java`, `Slf4jLogger.java`

4. **Adapter Layer**: `[Protocol][Entity]Adapter.java`
   - Example: `RestComponentAdapter.java`, `CliMachineAdapter.java`

5. **Documentation**: `[topic]-[subtopic].md`
   - Example: `component-lifecycle.md`, `testing-strategy.md`

## Folder Creation Guidelines

### When to create folders

Create a new folder only when ALL of these criteria are met:

1. **Critical Mass**: There are 5+ related files sharing a distinct responsibility
2. **Bounded Context**: The grouping represents a meaningful domain context
3. **Lifecycle Alignment**: Files are developed and released together
4. **Architectural Cohesion**: Files collectively belong to the same architectural layer
5. **Significant Capability**: The files implement an important system capability

### When to flatten folders

Consider flattening a folder structure when:

1. Directories contain fewer than 5 files
2. Directory nesting exceeds 3 levels deep
3. Multiple small directories serve similar purposes
4. Files can be organized through naming conventions instead

## Flattening Strategies

When flattening folder structures, use these techniques:

### 1. file prefixing pattern

```
Before:
/component/
  /validation/
    email-validator.java
    password-validator.java
  /creation/
    user-creator.java
    profile-creator.java

After:
/component/
  validation-email.java
  validation-password.java
  creation-user.java
  creation-profile.java
```

### 2. package consolidation

```
Before:
/model/
  /user/
    User.java
    UserMapper.java
  /profile/
    Profile.java
    ProfileMapper.java

After:
/model/
  UserModel.java
  UserModelMapper.java
  ProfileModel.java
  ProfileModelMapper.java
```

## Directory README Requirements

Every directory must contain a README.md file that includes:

1. **Purpose Statement**: Clear explanation of the directory's role
2. **Key Responsibilities**: Bullet list of primary responsibilities
3. **Content Description**: Table of key files with descriptions
4. **Architectural Context**: Where the directory fits in the architecture
5. **Naming Conventions**: File naming patterns specific to the directory
6. **Related Directories**: Links to related parts of the system

## Benefits of This Structure

1. **Clear Architectural Boundaries**: The structure directly maps to Clean Architecture layers
2. **Discoverability**: Important files are located predictably
3. **Maintainability**: Each directory has a clear purpose and naming conventions
4. **Minimal Nesting**: Reduced folder depth improves navigation
5. **Future-Proof**: Structure allows for extension without reorganization

## Directory Maintenance Tools

The repository includes tools to help maintain the directory structure:

- `util/scripts/flatten-directories.sh`: Analyzes and suggests folder flattening
- `util/scripts/check-readmes.sh`: Verifies presence of README files in directories
- `util/scripts/analyze-directory-structure.sh`: Generates directory structure reports

See the [folders.md](../../../folders.md) file in the repository root for comprehensive folder organization guidelines.
