# Clean Architecture Migration Dependency Suppressions

## Overview

During the migration to Clean Architecture, certain dependency violations need to be temporarily allowed to maintain functionality. This document lists the current suppressions, the reasons for each, and the plan for resolving them.

## Current Suppressions

### Adapter Dependencies on Legacy Code

These suppressions allow the adapter layer to temporarily depend on implementation classes during migration:

```java
// Core dependencies
"org.s8r.adapter -> org.s8r.core.env.Environment"
"org.s8r.adapter -> org.s8r.core.tube.identity.Identity"

// Tube dependencies
"org.s8r.adapter -> org.s8r.tube.TubeIdentity"
"org.s8r.adapter -> org.s8r.tube.Environment"
"org.s8r.adapter -> org.s8r.tube.Tube"
"org.s8r.adapter -> org.s8r.tube.TubeLifecycleState"
"org.s8r.adapter -> org.s8r.tube.TubeStatus"

// Component dependencies
"org.s8r.adapter -> org.s8r.component.Component"
"org.s8r.adapter -> org.s8r.component.Composite"
"org.s8r.adapter -> org.s8r.component.Machine"
"org.s8r.adapter -> org.s8r.component.Environment"

// Infrastructure dependencies
"org.s8r.adapter -> org.s8r.infrastructure.logging.ConsoleLogger"
```

## Rationale

1. **Adapter Layer Responsibilities**:
   The adapter layer is responsible for converting between legacy system components and the new domain model. It needs to directly reference both the legacy components (e.g., Tube, Component) and the domain model.

2. **Package Flattening Transition**:
   During the recent package flattening operation, we needed to maintain compatibility between different components that were previously in nested packages.

3. **Legacy Integration**:
   S8rMigrationFactory and related adapters need direct references to legacy components to perform their bridging functions.

4. **Testing Requirements**:
   Some dependencies are maintained to support test fixtures and validation.

## Resolution Plan

### Short-term Solutions (1-2 weeks)

1. **Interface Extraction**:
   - Define interfaces in the domain layer that capture essential behavior
   - Move implementation-specific details out of adapter code

2. **Dependency Inversion**:
   - Use domain interfaces instead of concrete types where possible
   - Implement factory methods that return interface types

### Medium-term Solutions (3-4 weeks)

1. **Reflection-based Adapters**:
   - Replace direct dependencies with reflection-based adapters
   - Use dynamic class loading to eliminate compile-time dependencies

2. **Event-based Communication**:
   - Replace direct calls with events where appropriate
   - Use domain events to decouple components

### Long-term Solutions (1-3 months)

1. **Complete Migration**:
   - Gradually replace all legacy components with clean architecture implementations
   - Remove adapter code that is no longer needed
   - Remove suppressions as dependencies are properly resolved

2. **Documentation**:
   - Maintain this document with the current state of suppressions
   - Track progress on eliminating each suppressed dependency

## Monitoring

The architecture tests automatically detect violations not covered by these suppressions. Any new dependency from the adapter layer to implementation classes should be carefully considered and added to this list only if absolutely necessary.

The goal is to progressively reduce this list until all dependencies follow Clean Architecture principles.