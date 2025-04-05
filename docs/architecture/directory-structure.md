# S8r Framework Directory Structure

This document provides a visual representation of the ideal S8r Framework directory structure, focusing on clarity, discoverability, and maintainability.

## Repository Root Structure

```
Samstraumr/
├── .samstraumr/            # Configuration files and metadata
├── Samstraumr/             # Maven project structure (core implementation)
│   ├── samstraumr-core/    # Core implementation modules
│   └── src/                # Maven site configuration
├── docs/                   # Documentation
│   ├── architecture/       # Architecture documentation
│   ├── guides/             # User and developer guides
│   ├── reference/          # Technical reference materials
│   ├── standards/          # Coding and documentation standards
│   └── diagrams/           # Visual representation of concepts
├── util/                   # Utilities and scripts
│   ├── scripts/            # Maintenance and build scripts
│   └── templates/          # Code and documentation templates
├── quality-tools/          # Code quality configuration
└── examples/               # Example applications and implementations
```

## Clean Architecture Directory Structure

The core implementation follows Clean Architecture principles with clear layering:

```
samstraumr-core/src/main/java/org/samstraumr/
├── domain/                 # Domain layer (business entities and logic)
│   ├── component/          # Component domain entities
│   ├── identity/           # Identity and addressing concerns
│   ├── lifecycle/          # Lifecycle state management
│   ├── event/              # Domain events
│   └── exception/          # Domain exceptions
├── application/            # Application layer (use cases)
│   ├── service/            # Application services
│   ├── port/               # Input/output ports (interfaces)
│   └── dto/                # Data transfer objects
├── infrastructure/         # Infrastructure layer (technical details)
│   ├── persistence/        # Repository implementations
│   ├── event/              # Event handling infrastructure
│   ├── logging/            # Logging implementation
│   └── config/             # System configuration
└── adapter/                # Adapters layer (integration points)
    ├── in/                 # Input adapters (CLI, REST, etc.)
    └── out/                # Output adapters (repositories, external systems)
```

## File Naming Conventions

Files within each directory should follow consistent naming patterns:

1. **Domain Layer**: `[entity]-[concern].java`
   - Example: `Component.java`, `ComponentFactory.java`

2. **Application Layer**: `[use case]-Service.java` or `[entity]-Repository.java`
   - Example: `ComponentService.java`, `MachineRepository.java`

3. **Infrastructure Layer**: `[technology]-[entity]-[implementation].java`
   - Example: `InMemoryComponentRepository.java`, `Slf4jLogger.java`

4. **Adapter Layer**: `[protocol]-[entity]-Adapter.java`
   - Example: `RestComponentAdapter.java`, `CliMachineAdapter.java`

5. **Documentation**: `[status]-[topic]-[type].md`
   - Example: `active-documentation-plan.md`, `component-architecture.md`

## Benefits of This Structure

1. **Clear Architectural Boundaries**: The structure directly maps to Clean Architecture layers
2. **Discoverability**: Important files are located predictably
3. **Maintainability**: Each directory has a clear purpose and naming conventions
4. **Minimal Nesting**: Reduced folder depth improves navigation
5. **Future-Proof**: Structure allows for extension without reorganization