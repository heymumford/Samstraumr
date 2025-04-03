# Samstraumr Testing Strategy

## Table of Contents

1. [Overview](#overview)
2. [Industry-Aligned Testing Taxonomy](#industry-aligned-testing-taxonomy)
3. [Test Pyramid Structure](#test-pyramid-structure)
4. [Terminology Mapping](#terminology-mapping)
5. [Above The Line vs Below The Line](#above-the-line-vs-below-the-line)
6. [Testing Taxonomy in Detail](#testing-taxonomy-in-detail)
7. [Test Organization](#test-organization)
8. [Continuous Integration Strategy](#continuous-integration-strategy)
9. [Running Tests](#running-tests)
10. [References](#references)

## Overview

Samstraumr's testing strategy is designed to align with industry-standard testing concepts while maintaining its domain-specific terminology and philosophy. This document provides a comprehensive overview of our testing approach and how it maps to established testing nomenclature.

The strategy is built around the concepts of Behavior-Driven Development (BDD) and Test-Driven Development (TDD), with a hierarchical structure that mirrors the compositional nature of Samstraumr systems.

## Industry-Aligned Testing Taxonomy

Samstraumr's testing strategy is aligned with industry-standard testing concepts while maintaining its expressive philosophy. Our approach builds upon the work of practitioners like Martin Fowler, Robert C. Martin (Uncle Bob), Kent Beck, and other thought leaders.

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

## Terminology Mapping

Samstraumr uses domain-specific terminology that aligns with the core concepts of our system. The following table maps our terminology to industry-standard testing terms:

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

## Above The Line vs Below The Line

Our testing strategy divides tests into critical (ATL) and non-critical (BTL) categories:

- **Above The Line (ATL)**: Critical tests that must pass for the build to be considered valid. These tests ensure the core functionality works correctly and provide fast feedback on build validity.
- **Below The Line (BTL)**: Non-critical tests that are important for robustness but not for basic functionality. These tests provide additional confidence but are not blocking.

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

## Test Organization

Tests are organized by their level:

1. **L0 - Tube (Unit) Tests** - Tests individual tubes in isolation
2. **L1 - Composite (Component) Tests** - Tests tubes working together
3. **L2 - Machine (API) Tests** - Tests public interfaces
4. **L3 - Stream (System) Tests** - Tests the entire system
5. **L4 - Orchestration (Smoke) Tests** - Tests system assembly

## Continuous Integration Strategy

Our CI pipeline implements a progressive testing approach:

1. **Fast Feedback Loop**: Orchestration tests run first to provide quick feedback on build validity
2. **Critical Path Validation**: Unit and component tests run next to validate core functionality
3. **Deep Validation**: Integration and system tests run to validate end-to-end behaviors
4. **Quality Analysis**: Static analysis tools run alongside tests for code quality
5. **Extended Validation**: BTL tests run lastly for additional confidence

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

- Martin Fowler: [TestPyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- Robert C. Martin: [The Three Rules of TDD](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd)
- Mike Cohn: [Succeeding with Agile](https://www.mountaingoatsoftware.com/books/succeeding-with-agile-software-development-using-scrum)
- Kent Beck: [Test-Driven Development](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Martin Fowler's Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Testing Strategies in a Microservice Architecture](https://martinfowler.com/articles/microservice-testing/)
- [Robert C. Martin's Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
- [Growing Object-Oriented Software, Guided by Tests](https://www.amazon.com/Growing-Object-Oriented-Software-Guided-Tests/dp/0321503627)