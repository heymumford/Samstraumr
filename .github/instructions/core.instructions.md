---
applyTo:
  - "modules/samstraumr-core/**"
  - "modules/*/src/main/java/**"
excludeAgent: []
---

# Core Module Instructions

These instructions apply specifically to the Samstraumr core framework implementation.

## Architecture Principles

### Clean Architecture Enforcement

- Maintain strict layer separation: Entities, Use Cases, Interface Adapters, Frameworks
- Dependencies must always point inward (outer layers depend on inner layers, never the reverse)
- Use ports and adapters pattern for external integrations
- Keep business logic independent of frameworks and libraries

### Component Design

- Components must be self-contained with minimal external dependencies
- Implement proper lifecycle management (initialize, start, stop, cleanup)
- Use state machines for managing component states
- Implement comprehensive validation at component boundaries
- Design for immutability where possible

### Event-Driven Communication

- Use event bus for inter-component communication
- Events should be immutable value objects
- Event handlers must be idempotent
- Document event schemas and contracts
- Handle event processing failures gracefully

## Code Quality Requirements

### Validation

- Validate all inputs at system boundaries
- Use builder pattern with validation for complex object construction
- Provide clear, actionable error messages
- Never fail silently - always log and report errors

### Error Handling

- Use custom exception hierarchy:
  - `ComponentException` for component-level errors
  - `CompositeException` for composite-level errors
  - `ValidationException` for validation failures
  - `LifecycleException` for lifecycle management errors
- Always log exceptions before throwing
- Include contextual information in exception messages
- Chain exceptions to preserve stack traces

### Threading and Concurrency

- Document thread-safety characteristics of all classes
- Use `@ThreadSafe` and `@NotThreadSafe` annotations
- Prefer immutability over synchronization
- Use concurrent collections from `java.util.concurrent`
- Avoid nested locks to prevent deadlocks

## Testing Requirements

### Component Tests

- Test component lifecycle transitions
- Verify state management and transitions
- Test error handling and recovery
- Mock external dependencies
- Use `@ComponentTest` annotation

### Integration Tests

- Test actual component interactions
- Verify event-driven communication
- Test composite assembly and coordination
- Use `@IntegrationTest` annotation

### Coverage Requirements

- Minimum 80% line coverage for core components
- Minimum 80% branch coverage for decision logic
- 100% coverage for validation logic
- Test both success and failure paths

## Performance Considerations

- Avoid premature optimization
- Profile before optimizing
- Document performance characteristics of critical paths
- Use appropriate data structures for access patterns
- Consider memory footprint for long-lived components

## Security Considerations

- Never log sensitive data
- Validate and sanitize all external inputs
- Use secure defaults for configuration
- Follow principle of least privilege
- Document security assumptions and requirements

## Documentation Standards

- Javadoc required for all public APIs
- Document thread-safety guarantees
- Explain non-obvious design decisions
- Include usage examples for complex APIs
- Document failure modes and recovery strategies
