# 0012 Enforce Acyclic Dependencies

Date: 2025-04-06

## Status

Accepted

## Context

As the Samstraumr codebase grows in complexity, we face increasing risk of circular dependencies emerging between packages and modules. Circular dependencies create several problems:

1. **Testing difficulties**: Components in a circular dependency can't be tested in isolation
2. **Build complications**: Circular dependencies can create build-order problems
3. **Code rigidity**: Changes to one component in a circular dependency can affect many others
4. **Reasoning challenges**: Circular dependencies make it harder to understand component boundaries
5. **Refactoring barriers**: Breaking circular dependencies becomes more difficult over time

While our Clean Architecture approach helps prevent many circular dependencies through its layered structure, we need a more comprehensive strategy to detect and prevent circular dependencies at all levels of the codebase.

## Decision

We will enforce acyclic dependencies (no cycles) throughout the codebase by implementing:

1. **Automated detection tools**: Create a `CircularDependencyAnalyzer` tool that uses graph analysis to identify dependency cycles

2. **Development-time checks**: Integrate circular dependency checks into the development workflow via:
   - Git pre-commit hooks (for changes involving multiple files)
   - Git pre-push hooks (for larger changes before they enter the shared codebase)

3. **CI/CD verification**: Add circular dependency checks to continuous integration pipelines

4. **Architectural tests**: Create test classes that verify the absence of circular dependencies 

5. **Documentation and guidelines**: Provide clear guidance on how to avoid and resolve circular dependencies

## Consequences

### Positive

- **Better modularity**: Enforcing acyclic dependencies leads to cleaner component boundaries
- **Simplified testing**: Components without circular dependencies are easier to test in isolation
- **Build improvements**: Avoiding circular dependencies eliminates certain build-order problems
- **Improved reasoning**: Developers can reason about components independently
- **Easier refactoring**: Code without circular dependencies is easier to change safely

### Negative

- **Additional overhead**: Developers must consider package dependencies more carefully
- **Potential reimplementation**: Some circular dependencies may require significant refactoring to resolve
- **Initial learning curve**: Developers need to learn strategies for avoiding circular dependencies

### Neutral

- **More interfaces**: Resolving circular dependencies often requires creating additional interfaces
- **Different architectural patterns**: May require using different patterns like mediators and events

## Implementation

We will implement the following:

1. **CircularDependencyAnalyzer**: A Java tool that builds a dependency graph from source files and detects cycles.

2. **Git Hooks**:
   - `pre-commit`: Runs a fast circular dependency check on staged files
   - `pre-push`: Runs a comprehensive check before pushing to protected branches

3. **Integration with Build System**:
   - Add circular dependency checks to Maven build process
   - Fail builds that introduce circular dependencies

4. **Architecture Tests**:
   - Add test cases specifically for circular dependency detection
   - Include these tests in the architecture validation suite

5. **Documentation**:
   - Create a comprehensive guide on circular dependency prevention
   - Document refactoring strategies for fixing circular dependencies

## Compliance Verification

Compliance with this architectural decision will be verified through:

1. **Automated checks** in Git hooks and CI/CD pipelines
2. **Architecture tests** that detect circular dependencies
3. **Code reviews** that scrutinize new package dependencies

Any circular dependencies found must be refactored before code can be merged into protected branches.

## Notes

When refactoring to remove circular dependencies, we recommend these strategies:

1. **Dependency Inversion**: Create interfaces in a shared package
2. **Mediator Pattern**: Use a mediator component for communication
3. **Event System**: Use events for decoupled communication
4. **Extract Common**: Move shared code to a common package

## References

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Acyclic Dependencies Principle](https://en.wikipedia.org/wiki/Acyclic_dependencies_principle)
