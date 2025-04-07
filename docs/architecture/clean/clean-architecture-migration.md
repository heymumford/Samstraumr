# Clean Architecture Migration Plan

## Overview

This document outlines the plan for migrating the Samstraumr codebase to a Clean Architecture pattern. The migration is prompted by issues encountered during the package flattening operation, which revealed type incompatibilities and architectural inconsistencies.

## Clean Architecture Principles

Clean Architecture organizes code into concentric layers:

1. **Domain Layer** (Innermost)
   - Contains business entities and rules
   - Has no dependencies on outer layers
   - Defines interfaces for outer layers to implement

2. **Application Layer**
   - Contains use cases that orchestrate domain entities
   - Depends only on the domain layer
   - Defines port interfaces for infrastructure

3. **Infrastructure Layer** (Outermost)
   - Contains framework-specific code
   - Implements interfaces defined by inner layers
   - May depend on external libraries and frameworks

4. **Adapters**
   - Bridge between layers
   - Convert between different data formats

## Current State Assessment

The codebase currently has several architectural issues:

1. **Mixed Concerns**: Domain logic mixed with infrastructure code
2. **Improper Dependencies**: Outer layers are directly imported by inner layers
3. **Type Incompatibilities**: Domain models and implementation models are not aligned
4. **Missing Interfaces**: Many components use concrete classes instead of interfaces
5. **Adapter Placement**: Adapters placed in wrong layers, causing dependency cycles

## Migration Strategy

The migration will proceed in three phases:

### Phase 1: Refactor Package Structure (Initial Clean-up)

1. **Complete Package Flattening**:
   - Fix remaining import statements
   - Update all references to flattened packages

2. **Establish Clean Architecture Packages**:
   - org.s8r.domain: Business entities and rules
   - org.s8r.application: Use cases and ports
   - org.s8r.infrastructure: External interfaces
   - org.s8r.adapter: Bridges between layers

3. **Document Architectural Boundaries**:
   - Create README.md files for each package
   - Document dependency rules in architecture tests

### Phase 2: Implement Adapters (Bridge Between Models)

1. **Define Clear Interfaces in Domain Layer**:
   - Create interfaces for all domain concepts
   - Move implementation-specific details out of domain

2. **Create Proper Adapter Implementations**:
   - Move existing adapters to correct packages
   - Implement new adapters for all conversions
   - Follow Interface Segregation and Dependency Inversion principles

3. **Fix Architecture Violations**:
   - Remove direct dependencies on implementation classes
   - Replace with interfaces and Dependency Injection
   - Update architecture tests to verify compliance

### Phase 3: Migrate Client Code (Gradual Transition)

1. **Identify Client Code**:
   - Map all code using legacy models
   - Prioritize by importance and complexity

2. **Create Migration Plan for Each Module**:
   - Document steps for migrating each module
   - Create tests to verify correct behavior after migration

3. **Execute Migration Module by Module**:
   - Update code to use domain models and interfaces
   - Use adapters where necessary for legacy integration
   - Run tests to verify functionality

4. **Remove Legacy Code**:
   - Deprecate legacy models
   - Remove adapters once migration is complete
   - Clean up any remaining technical debt

## Implementation Details

### Domain Model Interfaces

```java
// Domain interfaces
package org.s8r.domain.component.port;

public interface ComponentPort {
    String getId();
    void activate();
    void deactivate();
    // ...
}

public interface EnvironmentPort {
    String getParameter(String key);
    void setParameter(String key, String value);
    // ...
}
```

### Adapter Implementation

```java
// Adapter implementation
package org.s8r.adapter.component;

import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.EnvironmentPort;

public class ComponentAdapter implements ComponentPort {
    private final org.s8r.component.Component legacyComponent;
    
    public ComponentAdapter(org.s8r.component.Component legacyComponent) {
        this.legacyComponent = legacyComponent;
    }
    
    @Override
    public String getId() {
        return legacyComponent.getId();
    }
    
    @Override
    public void activate() {
        legacyComponent.activate();
    }
    
    // ...
}
```

### Service Implementation Using Interfaces

```java
// Application service using interfaces
package org.s8r.application.service;

import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.ComponentRepositoryPort;

public class ComponentService {
    private final ComponentRepositoryPort repository;
    
    public ComponentService(ComponentRepositoryPort repository) {
        this.repository = repository;
    }
    
    public void activateComponent(String id) {
        ComponentPort component = repository.findById(id);
        component.activate();
        repository.save(component);
    }
    
    // ...
}
```

## Mapping Challenges

The following mappings present particular challenges:

| Domain Concept | Legacy Implementation | Mapping Challenge | Solution Approach |
|----------------|------------------------|-------------------|-------------------|
| Component | org.s8r.component.Component | Different lifecycle states | Create state adapters with mapping logic |
| Identity | ComponentId vs TubeIdentity | Different identity formats | Bidirectional converters with validation |
| Environment | Different parameter handling | Parameter type mismatches | Type-safe converters with defaults |
| Events | Different event models | Event propagation | Event adapter layer with translation |
| Logging | Multiple logging implementations | Logger integration | Unified logging facade |

## Timeline and Milestones

| Phase | Milestone | Timeline | Success Criteria |
|-------|-----------|----------|------------------|
| 1 | Package structure refactored | Week 1-2 | Clean compile with no import errors |
| 1 | Architecture tests in place | Week 2 | Tests detecting violations |
| 2 | Domain interfaces defined | Week 3 | Complete interface set in domain layer |
| 2 | Adapter implementation | Week 3-4 | Adapters passing unit tests |
| 2 | Architecture violations fixed | Week 4-5 | All architecture tests passing |
| 3 | Client code migration plan | Week 5 | Documented plan for each module |
| 3 | Core modules migrated | Week 6-8 | Core functionality using domain models |
| 3 | All modules migrated | Week 8-10 | Complete migration with all tests passing |
| 3 | Legacy code removed | Week 10-12 | Codebase simplified with no adapters |

## Dependency Suppressions

During the migration, some architecture violations are temporarily allowed through suppressions. These are documented in [clean-architecture-migration-suppressions.md](clean-architecture-migration-suppressions.md).

Key suppressions include:
- Adapter dependencies on legacy component classes
- Adapter dependencies on tube and core packages
- Adapter dependencies on infrastructure classes

These suppressions allow the codebase to compile and function while we progressively migrate to proper Clean Architecture. The goal is to eliminate these suppressions over time as each component is properly refactored.

## Risks and Mitigation

| Risk | Impact | Mitigation |
|------|--------|------------|
| Breaking changes to APIs | High | Maintain backward compatibility layers during transition |
| Test coverage gaps | Medium | Add tests before refactoring each module |
| Complex legacy dependencies | High | Use adapter pattern for gradual migration |
| Performance overhead of adapters | Low | Profile and optimize critical paths |
| Incomplete migration | Medium | Clear milestone tracking and incremental approach |
| Architecture violations | Medium | Document temporary suppressions and remove progressively |

## Conclusion

This migration plan provides a structured approach to transitioning the Samstraumr codebase to Clean Architecture. By following these steps, we can resolve the current issues while establishing a more maintainable and flexible architecture for the future.

The immediate focus should be on fixing the compilation issues using temporary adapters, then progressively implementing a proper Clean Architecture through the phases outlined above.