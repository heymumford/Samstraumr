# Samstraumr Test Coverage Targets

This document defines the minimum test coverage targets for different components of the Samstraumr framework. These targets are used by the test coverage analysis tool (`s8r-test-coverage`) to identify gaps in test coverage.

## Test Coverage Standards

The Samstraumr project follows these testing standards:

1. Every layer of the Clean Architecture must have comprehensive test coverage
2. Both unit tests (JUnit) and behavior tests (Cucumber) should be used appropriately
3. Test coverage should focus on functionality, not just code lines covered
4. Critical components and core domain logic require higher coverage standards
5. Tests should validate both happy paths and error handling cases

## Minimum Test Counts by Category

| Category | Minimum Required | Current Count | Description |
|----------|-----------------|---------------|-------------|
| Domain Tests | 10 | 4 | Core domain model and business logic tests |
| Application Tests | 15 | 10 | Service layer and use case tests |
| Infrastructure Tests | 15 | 10 | External system adapters and implementation tests |
| Adapter Tests | 15 | 11 | Clean architecture boundary tests |
| Integration Features | 10 | 4 | Component interaction tests |
| System Features | 5 | 6 | End-to-end functionality tests |

## Coverage Analysis

The coverage analysis is performed by the `s8r-test-coverage` script, which:

1. Counts test files by category
2. Compares counts against the defined minimums
3. Identifies gaps in test coverage
4. Provides specific recommendations for improvement

## How to Run Coverage Analysis

```bash
# Run standalone test coverage analysis
./s8r-test-coverage

# Run test suite with coverage analysis
./s8r-test all --coverage
```

## Improving Test Coverage

To improve test coverage in specific areas:

1. **Domain Tests**:
   - Focus on core business rules
   - Test domain entities, value objects, and aggregates
   - Validate business invariants and constraints

2. **Application Tests**:
   - Test service methods that implement use cases
   - Validate input validation and error handling
   - Test interactions between services

3. **Infrastructure Tests**:
   - Test external system adapters
   - Test database interactions
   - Test file system operations

4. **Adapter Tests**:
   - Test boundary conversions between layers
   - Validate port implementations
   - Test API contracts

5. **Integration Tests**:
   - Test interactions between components
   - Validate composite behavior
   - Test event propagation

6. **System Tests**:
   - Test end-to-end workflows
   - Validate system requirements
   - Test cross-cutting concerns

## Review and Update

These coverage targets should be reviewed and updated quarterly to ensure they continue to meet the project's quality standards. As the codebase evolves, these targets may need to be adjusted.