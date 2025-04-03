# Samstraumr Testing Strategy

## Industry-Aligned Testing Taxonomy

Samstraumr's testing strategy is aligned with industry-standard testing concepts while maintaining its expressive philosophy. This document maps our testing approach to established testing nomenclature developed by practitioners like Martin Fowler, Robert C. Martin (Uncle Bob), Kent Beck, and other thought leaders.

## Test Pyramid Structure

Our test strategy is organized according to a modified version of Mike Cohn's Test Pyramid, with considerations for the specific domain of distributed, flow-based systems:

```
                    /\
                   /  \
                  / UI \
                 /------\
                /  API   \
               /----------\
              / Integration \
             /--------------\
            /   Component    \
           /------------------\
          /       Unit         \
         /----------------------\
```

### Test Categories

| Industry Standard | Samstraumr Terminology | Description | Speed | Cost | ATL/BTL |
|-------------------|------------------------|-------------|-------|------|---------|
| **Smoke Tests** | **Orchestration Tests** | Basic build validation | Very Fast | Very Low | ATL |
| **Unit Tests** | **Tube Tests** | Tests for individual tubes in isolation | Fast | Low | ATL |
| **Component Tests** | **Composite Tests** | Tests for tube composites | Medium | Medium | ATL |
| **Integration Tests** | **Flow Tests** | Tests for data flowing through tubes | Medium | Medium | Mix |
| **API/Contract Tests** | **Machine Tests** | Tests for machine boundaries | Medium | Medium | Mix |
| **System Tests** | **Stream Tests** | Tests for system-level behaviors | Slow | High | BTL |
| **End-to-End Tests** | **Acceptance Tests** | Tests for complete user journeys | Very Slow | Very High | BTL |
| **Property Tests** | **Adaptation Tests** | Tests for system adaptability | Varies | High | BTL |

## Above the Line (ATL) vs Below the Line (BTL)

Our testing strategy divides tests into critical (ATL) and non-critical (BTL) categories:

- **Above The Line (ATL)**: Tests that must pass for a build to be considered valid
- **Below The Line (BTL)**: Tests that provide additional confidence but are not blocking

This aligns with industry concepts of "critical path" and "build verification" versus "non-blocking validation."

## Testing Taxonomy in Detail

### 1. Orchestration Tests (Smoke Tests)

**Industry Equivalent**: Smoke Tests, Build Verification Tests (BVTs)

Orchestration tests are the highest level of ATL tests and serve as "smoke tests" or "sanity checks" in traditional terminology. They verify that the core system components can be assembled correctly and basic functionality works. These tests are fast, lightweight, and focus on verifying the system's essential capabilities.

Martin Fowler describes smoke tests as "a subset of test cases that cover the most important functionality of a component or system, used to aid assessment of whether main functions appear to work correctly."

**Tag**: `@Orchestration`

### 2. Tube Tests (Unit Tests)

**Industry Equivalent**: Unit Tests

Tube tests verify the behavior of individual tubes in isolation, similar to traditional unit tests. They focus on testing a single unit of code with all dependencies mocked or stubbed.

According to Robert C. Martin: "Unit tests are tests that test code in isolation. They don't test databases, or file systems, or networks, or GUIs; they test functions and methods."

**Tag**: `@Unit`

### 3. Composite Tests (Component Tests)

**Industry Equivalent**: Component Tests

Composite tests verify that collections of tubes work together correctly. These are analogous to what Martin Fowler calls "component tests" - tests that exercise a cohesive component assembled from multiple units but still isolated from the rest of the system.

**Tag**: `@Component`

### 4. Flow Tests (Integration Tests)

**Industry Equivalent**: Integration Tests

Flow tests verify the integration between different parts of the system, focusing on data flowing through interconnected tubes. This maps to traditional integration testing, which verifies that different components work together correctly.

**Tag**: `@Integration`

### 5. Machine Tests (API/Contract Tests)

**Industry Equivalent**: API Tests, Contract Tests

Machine tests verify the boundaries between machines, ensuring that they communicate according to specified contracts. This aligns with what Fowler describes as "contract tests" - tests that verify the interactions between services conform to a consumer-driven contract.

**Tag**: `@API`

### 6. Stream Tests (System Tests)

**Industry Equivalent**: System Tests

Stream tests verify system-level behaviors, focusing on entire streams of data processing. These correspond to system tests in traditional terminology, which test the system as a whole.

**Tag**: `@System`

### 7. Acceptance Tests (End-to-End Tests)

**Industry Equivalent**: End-to-End Tests, Acceptance Tests

Acceptance tests verify complete user journeys and business scenarios. They map directly to acceptance and end-to-end tests in traditional terminology.

**Tag**: `@Acceptance`

### 8. Adaptation Tests (Property Tests)

**Industry Equivalent**: Property Tests, Chaos Tests

Adaptation tests verify the system's ability to adapt to changing conditions and failures. These align with property-based testing and chaos engineering practices.

**Tag**: `@Property`

## Testing Methodology

Samstraumr employs a Behavior-Driven Development (BDD) approach using Cucumber for feature-level tests, complemented by JUnit for more granular unit and component testing. This aligns with industry practices for combining specification by example with traditional testing approaches.

### BDD Feature Organization

Feature files are organized hierarchically:

- L0_Orchestration: Core build verification features
- L1_Tube: Unit-level features for individual tubes
- L1_Composite: Component-level features for composite tubes
- L2_Machine: Integration-level features for machines
- L3_System: System-level features for the entire application

## Continuous Integration Strategy

Our CI pipeline implements a progressive testing approach:

1. **Fast Feedback Loop**: Orchestration tests run first to provide quick feedback on build validity
2. **Critical Path Validation**: Unit and component tests run next to validate core functionality
3. **Deep Validation**: Integration and system tests run to validate end-to-end behaviors
4. **Quality Analysis**: Static analysis tools run alongside tests for code quality
5. **Extended Validation**: BTL tests run lastly for additional confidence

## References

- Martin Fowler: [TestPyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- Robert C. Martin: [The Three Rules of TDD](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd)
- Mike Cohn: [Succeeding with Agile](https://www.mountaingoatsoftware.com/books/succeeding-with-agile-software-development-using-scrum)
- Kent Beck: [Test-Driven Development](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)