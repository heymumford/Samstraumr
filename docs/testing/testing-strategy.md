<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Testing Strategy

## Table of Contents

1. [Overview](#overview)
2. [Industry-Aligned Testing Taxonomy](#industry-aligned-testing-taxonomy)
3. [Test Pyramid Structure](#test-pyramid-structure)
4. [Terminology Mapping](#terminology-mapping)
5. [Above The Line vs Below The Line](#above-the-line-vs-below-the-line)
6. [Testing Taxonomy in Detail](#testing-taxonomy-in-detail)
7. [Test Organization](#test-organization)
8. [Port Interface Testing](#port-interface-testing)
9. [Continuous Integration Strategy](#continuous-integration-strategy)
10. [Running Tests](#running-tests)
11. [Implementation Status](#implementation-status)
12. [Related Research](#related-research)
13. [References](#references)

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

| Industry Standard |  Samstraumr Term   |                       Description                       |
|-------------------|--------------------|---------------------------------------------------------|
| Smoke Test        | Orchestration Test | Verifies the basic system assembly and connectivity     |
| Unit Test         | Tube Test          | Tests individual tubes in isolation                     |
| Component Test    | Composite Test     | Tests connected tubes working together                  |
| Integration Test  | Flow Test          | Tests interaction between different parts of the system |
| API/Contract Test | Machine Test       | Tests public interfaces and contracts                   |
| System Test       | Stream Test        | Tests the entire system as a whole                      |
| End-to-End Test   | Acceptance Test    | Tests the system from the user's perspective            |
| Property Test     | Adaptation Test    | Tests system properties across a range of inputs        |

## Above the Line Vs Below the Line

Our testing strategy divides tests into critical (ATL) and non-critical (BTL) categories:

- **Above The Line (ATL)**: Critical tests that must pass for the build to be considered valid. These tests ensure the core functionality works correctly and provide fast feedback on build validity.
- **Below The Line (BTL)**: Non-critical tests that are important for robustness but not for basic functionality. These tests provide additional confidence but are not blocking.

This aligns with industry concepts of "critical path" and "build verification" versus "non-blocking validation."

## Testing Taxonomy in Detail

### 1. orchestration tests (smoke tests)

**Industry Equivalent**: Smoke Tests, Build Verification Tests (BVTs)

Orchestration tests are the highest level of ATL tests and serve as "smoke tests" or "sanity checks" in traditional terminology. They verify that the core system components can be assembled correctly and basic functionality works. These tests are fast, lightweight, and focus on verifying the system's essential capabilities.

Martin Fowler describes smoke tests as "a subset of test cases that cover the most important functionality of a component or system, used to aid assessment of whether main functions appear to work correctly."

**Tag**: `@Orchestration`

### 2. tube tests (unit tests)

**Industry Equivalent**: Unit Tests

Tube tests verify the behavior of individual tubes in isolation, similar to traditional unit tests. They focus on testing a single unit of code with all dependencies mocked or stubbed.

According to Robert C. Martin: "Unit tests are tests that test code in isolation. They don't test databases, or file systems, or networks, or GUIs; they test functions and methods."

**Tag**: `@Unit`

### 3. composite tests (component tests)

**Industry Equivalent**: Component Tests

Composite tests verify that collections of tubes work together correctly. These are analogous to what Martin Fowler calls "component tests" - tests that exercise a cohesive component assembled from multiple units but still isolated from the rest of the system.

**Tag**: `@Component`

### 4. flow tests (integration tests)

**Industry Equivalent**: Integration Tests

Flow tests verify the integration between different parts of the system, focusing on data flowing through interconnected tubes. This maps to traditional integration testing, which verifies that different components work together correctly.

**Tag**: `@Integration`

### 5. machine tests (api/contract tests)

**Industry Equivalent**: API Tests, Contract Tests

Machine tests verify the boundaries between machines, ensuring that they communicate according to specified contracts. This aligns with what Fowler describes as "contract tests" - tests that verify the interactions between services conform to a consumer-driven contract.

**Tag**: `@API`

### 6. stream tests (system tests)

**Industry Equivalent**: System Tests

Stream tests verify system-level behaviors, focusing on entire streams of data processing. These correspond to system tests in traditional terminology, which test the system as a whole.

**Tag**: `@System`

### 7. acceptance tests (end-to-end tests)

**Industry Equivalent**: End-to-End Tests, Acceptance Tests

Acceptance tests verify complete user journeys and business scenarios. They map directly to acceptance and end-to-end tests in traditional terminology.

**Tag**: `@Acceptance`

### 8. adaptation tests (property tests)

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

## Port Interface Testing

Port interface testing is a critical aspect of maintaining clean architecture boundaries in the Samstraumr system. Port interfaces define the boundary between the application core and infrastructure components, allowing for modularity and interchangeability of external systems.

### Port Interface Test Organization

Port interface tests are organized into two main categories:

1. **Port Interface Tests** - Tests individual port interfaces in isolation to verify their contract
2. **Port Integration Tests** - Tests the integration between different ports to verify their interoperability

Port interfaces follow Clean Architecture principles:

- **Ports**: Defined by the application core (inner circle)
- **Adapters**: Implement port interfaces in the infrastructure layer (outer circle)

### Port Interface Test Strategy

The port interface test strategy incorporates:

1. **BDD Approach**: Uses Gherkin feature files to define the expected behavior of ports
2. **Karate Integration**: Uses Karate testing framework for system-level testing of port interfaces
3. **Contract Verification**: Ensures both the port interface and adapters adhere to the defined contract
4. **Isolation**: Tests each port independently to validate its interface
5. **Integration**: Tests combinations of ports to ensure they work together properly

For more details, see:
- [Port Interface Testing](port-interface-testing.md)
- [Port Interface Test Report](../test-reports/port-interface-test-report.md)
- [Karate Testing Guide](karate-testing-guide.md)
- [Karate Syntax Reference](karate-syntax-reference.md)

## Continuous Integration Strategy

Our CI pipeline implements a progressive testing approach:

1. **Fast Feedback Loop**: Orchestration tests run first to provide quick feedback on build validity
2. **Critical Path Validation**: Unit and component tests run next to validate core functionality
3. **Deep Validation**: Integration and system tests run to validate end-to-end behaviors
4. **Quality Analysis**: Static analysis tools run alongside tests for code quality
5. **Extended Validation**: BTL tests run lastly for additional confidence

## Running Tests

Tests can be run using our specialized test runners:

```bash
# Run tests by level and type
bin/s8r-test unit           # Run unit (L0) tests
bin/s8r-test component      # Run component (L1) tests
bin/s8r-test integration    # Run integration (L2) tests
bin/s8r-test system         # Run system (L3) tests

# Run tests by function
bin/s8r-test lifecycle      # Run lifecycle tests
bin/s8r-test identity       # Run identity tests
bin/s8r-test error-handling # Run error handling tests

# Run port interface tests
bin/s8r-test-port-interfaces                # Run all port interface tests
bin/s8r-test-port-interfaces cache          # Test cache port interface
bin/s8r-test-port-interfaces notification   # Test notification port interface
bin/s8r-test-port-interfaces --integration  # Run port integration tests

# Run with verification and reporting
bin/s8r-test --verify --report              # Verify test structure and generate report
bin/s8r-test-port-interfaces --verify --report # Verify port tests and generate report
```

## Implementation Status

For the current implementation status of our testing infrastructure, see the following reports:

- [Test Suite Implementation Report](../test-reports/test-suite-implementation-report.md) - Detailed status of test implementation progress
- [Test Suite Verification Summary](../test-reports/test-suite-verification-summary.md) - Summary of verification results for existing tests
- [Test Verification Report](../test-reports/test-verification-report.md) - Comprehensive verification of test quality and coverage
- [Port Interface Test Report](../test-reports/port-interface-test-report.md) - Status of port interface tests and integration tests

### Recent Enhancements

The following recent enhancements have been made to our testing infrastructure:

1. **Lifecycle Testing** - Comprehensive test infrastructure for component lifecycle management with specialized test runners and execution modes
2. **Port Interface Testing** - Dedicated infrastructure for testing port interfaces and port integration with a specialized test runner
3. **Test Verification** - Enhanced verification tools to ensure test completeness and quality
4. **Test Organization** - Improved directory structure and naming conventions for tests

## Related Research

Our testing strategy is informed by current research in software testing, particularly in the context of AI-enhanced testing approaches:

- [Testing in the Age of AI](../research/test-in-age-of-ai.md) - Eric C. Mumford's research on how AI is transforming testing strategies and methodologies
- [AI-Enhanced Testing Integration](../research/ai-enhanced-testing-integration.md) - Practical strategies for integrating AI capabilities into the testing framework
- [QA Cognitive Transformation in AI](../research/qa-cognitive-transformation-ai.md) - How AI is reshaping QA roles and responsibilities

## References

- Martin Fowler: [TestPyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- Robert C. Martin: [The Three Rules of TDD](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd)
- Mike Cohn: [Succeeding with Agile](https://www.mountaingoatsoftware.com/books/succeeding-with-agile-software-development-using-scrum)
- Kent Beck: [Test-Driven Development](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Martin Fowler's Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Testing Strategies in a Microservice Architecture](https://martinfowler.com/articles/microservice-testing/)
- [Robert C. Martin's Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
