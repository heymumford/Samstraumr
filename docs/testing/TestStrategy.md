# Samstraumr Testing Strategy

This document outlines Samstraumr's testing strategy, aligning our domain-specific terminology with industry-standard testing practices.

## Terminology Mapping

Samstraumr uses domain-specific terminology that aligns with the core concepts of our system. The table below maps our terminology to industry-standard testing terms:

| Industry Standard | Samstraumr Term | Description |
|-------------------|-----------------|-------------|
| Smoke Test        | Orchestration Test | Verifies the basic system assembly and connectivity |
| Unit Test         | Tube Test       | Tests individual tubes in isolation |
| Component Test    | Composite Test  | Tests connected tubes working together |
| Integration Test  | Flow Test       | Tests interaction between different parts of the system |
| API/Contract Test | Machine Test    | Tests public interfaces and contracts |
| System Test       | Stream Test     | Tests the entire system as a whole |
| End-to-End Test   | Acceptance Test | Tests the system from the user's perspective |
| Property Test     | Adaptation Test | Tests system properties across a range of inputs |

## Test Pyramid

Our testing strategy follows the test pyramid approach, with more tests at the lower levels:

```
                 /\
                /  \
               /    \
              /      \
             /        \
            /          \
           /            \
          /              \
         /                \
        /                  \
       /                    \
      /                      \
     /                        \
    /                          \
   /                            \
  /                              \
 /________________________________\
```

From top to bottom:

1. **End-to-End/Acceptance Tests** - A small number of tests that verify the system meets user requirements
2. **System/Stream Tests** - Tests that verify the entire system works correctly
3. **API/Machine Tests** - Tests that verify public interfaces work correctly
4. **Integration/Flow Tests** - Tests that verify different parts interact correctly
5. **Component/Composite Tests** - Tests that verify components work together
6. **Unit/Tube Tests** - The largest number of tests, verifying individual units work correctly

## Test Organization

Tests are organized by their level:

1. **L0 - Tube (Unit) Tests** - Tests individual tubes in isolation
2. **L1 - Composite (Component) Tests** - Tests tubes working together
3. **L2 - Machine (API) Tests** - Tests public interfaces
4. **L3 - Stream (System) Tests** - Tests the entire system
5. **L4 - Orchestration (Smoke) Tests** - Tests system assembly

## Above The Line (ATL) vs Below The Line (BTL)

Tests are also categorized as:

- **Above The Line (ATL)** - Critical tests that must pass for the build to be considered valid
- **Below The Line (BTL)** - Non-critical tests that are important for robustness but not critical for basic functionality

## Test Implementation

Tests are implemented using:

- JUnit 5 for the test framework
- Cucumber for behavior-driven tests
- Custom annotations that support both Samstraumr and industry-standard terminology

### Custom Annotations

We provide custom annotations that support both terminology sets:

```java
@UnitTest / @TubeTest
@ComponentTest / @CompositeTest  
@IntegrationTest / @FlowTest
@ApiTest / @MachineTest
@SystemTest / @StreamTest
@EndToEndTest / @AcceptanceTest
@PropertyTest / @AdaptationTest
@SmokeTest / @OrchestrationTest
```

## Running Tests

Tests can be run using both terminology sets with our custom runner:

```bash
# Run unit tests (Samstraumr's Tube tests)
./run-tests.sh unit

# Run both unit tests and tube tests
./run-tests.sh --both unit

# Run with specific Maven profile
./run-tests.sh --profile btl-tests integration
```

## References

Our testing strategy is influenced by:

- [Martin Fowler's Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Testing Strategies in a Microservice Architecture](https://martinfowler.com/articles/microservice-testing/)
- [Robert C. Martin's Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
- [Growing Object-Oriented Software, Guided by Tests](https://www.amazon.com/Growing-Object-Oriented-Software-Guided-Tests/dp/0321503627)