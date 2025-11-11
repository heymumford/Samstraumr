# Clean Architecture Migration

## Introduction

This document outlines the plan for migrating the Samstraumr framework from its legacy component-based architecture to a Clean Architecture design. The migration will be performed incrementally, maintaining backward compatibility throughout the process.

## Current Status

The codebase currently has several architectural challenges:

1. **Type Conflicts**: Two parallel hierarchies (`org.s8r.component.*` and `org.s8r.domain.*`) with incompatible types
2. **Package Flattening**: Recent directory flattening caused import mismatches and compilation issues
3. **Mixed Architecture**: Parts of the codebase use domain-centric models while others use component-based models

## Migration Strategy

The migration will follow a three-phase approach:

### Phase 1: refactor package structure (in progress)

1. ✅ Fix import statements after package flattening
2. ✅ Add temporary adapter utilities for tests
3. 🔄 Update test files to use adapters where needed
4. 🔄 Ensure all tests compile and pass
5. ⬜ Document mapping between old and new structures

### Phase 2: implement clean architecture adapters

1. ⬜ Create comprehensive adapter layer in `org.s8r.adapter.clean` package
2. ⬜ Implement bidirectional conversion between models
3. ⬜ Add tests for adapter functionality
4. ⬜ Integrate adapters into DI container
5. ⬜ Document adapter usage patterns

### Phase 3: migrate client code

1. ⬜ Update services to use domain interfaces
2. ⬜ Create repository adapters
3. ⬜ Migrate API layer
4. ⬜ Update configuration
5. ⬜ Deprecate legacy components

## Implementation Details

### Adapter pattern implementation

The adapter layer will include:

- `DomainAdapterFactory`: Factory for creating adapters
- `ComponentAdapter`: Converts between component types
- `CompositeAdapter`: Converts between composite types
- `MachineAdapter`: Converts between machine types
- `IdentityAdapter`: Converts between identity types
- `EnvironmentAdapter`: Converts between environment types

### Key mapping challenges

| Legacy Type | Domain Type | Conversion Challenges |
|-------------|-------------|----------------------|
| `org.s8r.component.Component` | `org.s8r.domain.component.Component` | State mapping, identity conversion |
| `org.s8r.component.Composite` | `org.s8r.domain.component.composite.CompositeComponent` | Component references, connections |
| `org.s8r.component.Machine` | `org.s8r.domain.machine.Machine` | State conversion, composite handling |
| `org.s8r.component.Identity` | `org.s8r.domain.identity.ComponentId` | Hierarchy mapping, lineage |
| `org.s8r.component.Environment` | `Map<String, String>` | Parameter conversion |

### Migration roadmap

1. **Q2 2025**: Complete Phase 1
2. **Q3 2025**: Implement Phase 2
3. **Q4 2025**: Begin Phase 3
4. **Q1 2026**: Complete migration

## Documentation

Comprehensive documentation will be created for:

1. ✅ Adapter usage patterns
2. ⬜ Migration guides for different component types
3. ⬜ Testing strategies for adapted components
4. ⬜ Best practices for new development

## References

- [Clean Architecture ADR](../architecture/decisions/0003-adopt-clean-architecture-for-system-design.md)
- [Using Domain Adapters Guide](../guides/migration/using-domain-adapters.md)
