# Clean Architecture Migration Plan

*Document version: 1.0 - April 4, 2025*

This document outlines the comprehensive plan for refactoring the Samstraumr/S8r project to follow Uncle Bob's Clean Architecture principles.

## Current Architecture Assessment

Samstraumr currently has:
- Strong domain model with biological-inspired concepts (Tube, TubeIdentity, lifecycle states)
- Hierarchical component structure (Tube → Composite → Machine)
- Mixed concerns (domain logic and infrastructure) in core classes
- Duplicate/overlapping packages (org.samstraumr.tube vs org.tube.core)
- Tightly coupled dependencies on infrastructure (logging, environment)

## Target Clean Architecture Structure

```
org.samstraumr/
├── domain/              # Core business logic, entities, rules
│   ├── component/       # Core component entities (renamed from tube)
│   ├── identity/        # Component identity concepts  
│   └── lifecycle/       # Lifecycle state management
├── application/         # Use cases, service orchestration
│   ├── service/         # Application services
│   └── port/            # Input/output interfaces
├── adapter/             # Framework implementation
│   ├── in/              # Input adapters (CLI, API)
│   └── out/             # Output adapters (persistence, logging)
└── infrastructure/      # Technical concerns
    ├── config/          # Configuration
    └── logging/         # Logging framework
```

## Step-by-Step Migration Plan

### Phase 1: Domain Layer (4-5 weeks)

**Objective**: Extract pure domain entities, free from framework dependencies

1. Create the new domain package structure
2. Identify core domain entities (Component, Identity, Lifecycle)
3. Implement domain entities without infrastructure dependencies
4. Define repository and service interfaces

**Example refactoring for `Tube.java`**:

```java
// Current implementation
public class Tube {
    private Environment environment;
    private TubeStatus status;
    private TubeLogger logger;
    
    // Constructor with direct dependency on Environment
    // Methods with logging and infrastructure concerns
}

// Clean Architecture implementation
// Domain package
public class Component {
    private final ComponentId id;
    private LifecycleState state;
    
    public Component(ComponentId id) {
        this.id = id;
        this.state = LifecycleState.CONCEPTION;
    }
    
    // Pure domain methods with no infrastructure dependencies
    public void initialize() {
        if (state != LifecycleState.CONCEPTION) {
            throw new IllegalStateException("Component must be in CONCEPTION state to initialize");
        }
        state = LifecycleState.INITIALIZED;
    }
}
```

### Phase 2: Application Layer (5-6 weeks)

**Objective**: Implement use cases and service orchestration

1. Create application service classes implementing use cases
2. Define input/output ports (interfaces) for external communication
3. Implement business operations using domain objects
4. Create DTOs (Data Transfer Objects) for service communication

**Example ComponentService**:

```java
// Application service with ports
public class ComponentService {
    private final ComponentRepository componentRepository;
    private final ComponentEventPublisher eventPublisher;
    
    public ComponentService(ComponentRepository componentRepository, 
                            ComponentEventPublisher eventPublisher) {
        this.componentRepository = componentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public void initializeComponent(ComponentId id) {
        Component component = componentRepository.findById(id)
            .orElseThrow(() -> new ComponentNotFoundException(id));
        
        component.initialize();
        componentRepository.save(component);
        eventPublisher.publishComponentInitialized(new ComponentInitializedEvent(id));
    }
}
```

### Phase 3: Adapters (4-5 weeks)

**Objective**: Implement the connection layer between application and infrastructure

1. Create adapters for persistence, logging, and other infrastructure
2. Implement input adapters for CLI and API
3. Connect domain/application layers to external systems
4. Ensure adapters properly translate between domain and external systems

**Example persistence adapter**:

```java
// Output adapter implementing domain repository interface
public class ComponentRepositoryAdapter implements ComponentRepository {
    private final ComponentStorage storage; // Infrastructure dependency
    
    public ComponentRepositoryAdapter(ComponentStorage storage) {
        this.storage = storage;
    }
    
    @Override
    public Optional<Component> findById(ComponentId id) {
        ComponentData data = storage.findById(id.getValue());
        return data != null ? Optional.of(mapToComponent(data)) : Optional.empty();
    }
    
    @Override
    public void save(Component component) {
        storage.save(mapToData(component));
    }
    
    // Mapping methods
}
```

### Phase 4: Infrastructure (3-4 weeks)

**Objective**: Implement technical concerns and configuration

1. Implement infrastructure services (logging, configuration)
2. Set up dependency injection
3. Create infrastructure services
4. Configure the application wiring

**Example logging infrastructure**:

```java
// Infrastructure implementation
public class Log4jLogger implements Logger {
    private final org.apache.logging.log4j.Logger logger;
    
    public Log4jLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }
    
    @Override
    public void info(String message) {
        logger.info(message);
    }
    
    // Other methods
}
```

## Testing Strategy

The testing approach will follow the clean architecture layers:

1. **Domain Layer Testing**
   - Pure unit tests without mocks or external dependencies
   - Test domain entities, value objects, and domain services
   - Focus on business rules and invariants

2. **Application Layer Testing**
   - Test use cases with mocked ports
   - Verify orchestration and application-level logic
   - Use in-memory implementations for repositories and services

3. **Adapter Layer Testing**
   - Test adapters with real or mocked infrastructure
   - Focus on translation between domain and external systems
   - Verify proper mapping of data

4. **End-to-End Testing**
   - Test full system with BDD approach using Cucumber
   - Verify that the entire system works together
   - Focus on user-visible behavior and acceptance criteria

## Implementation Approach

To ensure continuous progress while minimizing risk:

1. Start with a single component (e.g., Component Identity)
2. Apply Clean Architecture to create a working vertical slice
3. Gradually refactor other components following the same pattern
4. Use an adapter layer to maintain compatibility with existing code
5. Migrate tests to the new architecture in parallel

## Key Components to Refactor

1. **TubeIdentity → ComponentId (Domain)**
   - Extract pure domain concept
   - Remove logging and infrastructure dependencies
   - Implement as proper value object

2. **Environment → EnvironmentService (Application)**
   - Extract business rules to domain
   - Create application service for environment awareness
   - Implement infrastructure adapter for system information

3. **TubeLogger → LoggerPort (Application Interface)**
   - Define logger interface in application layer
   - Implement adapter for Log4j in infrastructure
   - Inject logger through constructor

4. **Composite → CompositeEntity and CompositeService**
   - Separate domain entity from orchestration logic
   - Create composite repository interface
   - Implement composite operations as use cases

## Anticipated Challenges

1. **Deeply embedded infrastructure dependencies**
   - Current code has pervasive logging, environment references
   - Need careful extraction and dependency inversion

2. **Complex lifecycle state management**
   - Lifecycle concepts are core to the system
   - Need to preserve behavior while refactoring

3. **Maintaining backward compatibility**
   - Existing code depends on current structure
   - Need adapters to bridge old and new architecture

4. **Comprehensive testing**
   - Ensure refactoring doesn't break existing behavior
   - Need tests at all levels of the architecture

## Timeline Estimate

- **Phase 1 (Domain Layer)**: 4-5 weeks
- **Phase 2 (Application Layer)**: 5-6 weeks
- **Phase 3 (Adapter Layer)**: 4-5 weeks
- **Phase 4 (Infrastructure Layer)**: 3-4 weeks
- **Integration and Testing**: 2-3 weeks

**Total Estimate**: 18-23 weeks for complete migration, with incremental value delivered throughout.

## Success Criteria

The refactoring will be considered successful when:

1. Core domain logic is free from infrastructure dependencies
2. All layers follow the dependency rule (pointing inward)
3. Components can be tested in isolation
4. The system maintains all existing functionality
5. Technical components (logging, persistence) can be changed without affecting domain logic