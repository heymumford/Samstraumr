# Bdd with Cucumber

> **⚠️ DEPRECATED**: This document has been merged into a comprehensive BDD documentation document.
>
> Please refer to the consolidated document for the most up-to-date information:
> [BDD Documentation](bdd-documentation.md)

This document explains Samstraumr's approach to Behavior-Driven Development (BDD) using Cucumber.

## Introduction to BDD

Behavior-Driven Development is an approach that focuses on defining the behavior of a system from the outside in. It encourages collaboration between developers, QA, and non-technical stakeholders by using a shared language to describe the system's behavior.

## Why Cucumber?

Cucumber allows us to:

1. Write specifications in plain language
2. Map those specifications to executable tests
3. Document the system behavior in a human-readable format
4. Create living documentation that stays in sync with the code

## Feature File Structure

Cucumber features in Samstraumr follow this structure:

```gherkin
@L1_Bundle @Identity @ATL
Feature: Tube Identification

  Background:
    Given a new tube is created with ID "example.tube"
    
  Scenario: Tube reports its identity correctly
    When the tube is queried for its ID
    Then the reported ID should be "example.tube"
```

## Step Definitions

Step definitions map the plain language in feature files to executable code:

```java
@Given("a new tube is created with ID {string}")
public void createTubeWithId(String id) {
    tube = new Tube(id);
}

@When("the tube is queried for its ID")
public void queryTubeForId() {
    reportedId = tube.getId();
}

@Then("the reported ID should be {string}")
public void checkReportedId(String expectedId) {
    assertEquals(expectedId, reportedId);
}
```

## Test Categories and Tags

Samstraumr uses tags to categorize tests:

- **Layer Tags**: `@L0_Tube`, `@L1_Bundle`, `@L2_Machine`, `@L3_System`
- **Criticality Tags**: `@ATL` (critical), `@BTL` (robustness)
- **Capability Tags**: `@Identity`, `@Flow`, `@State`, `@Awareness`
- **Lifecycle Tags**: `@Init`, `@Runtime`, `@Termination`
- **Pattern Tags**: `@Observer`, `@Transformer`, `@Validator`, `@CircuitBreaker`
- **Non-functional Tags**: `@Performance`, `@Resilience`, `@Scale`

## Running Tests

Run tests with specific tags using:

```bash
./run-tests.sh --tags="@L1_Bundle and @Identity"
```

## Best Practices

1. **Feature Files**:
   - Focus on business value
   - Use domain terminology
   - Keep scenarios independent
   - Use declarative (what) not imperative (how) language
2. **Step Definitions**:
   - Keep steps reusable
   - Maintain appropriate granularity
   - Follow the Arrange-Act-Assert pattern
   - Use domain objects rather than UI details
3. **Test Organization**:
   - Group related scenarios in a feature
   - Use the Background section for common setup
   - Apply tags consistently for test selection
   - Structure directories by layer and capability

## Aligning with Industry Standards

Samstraumr maintains a dual terminology system:

| Industry Standard | Samstraumr Equivalent |
|-------------------|-----------------------|
| Unit Test         | Tube Test             |
| Component Test    | Bundle Test           |
| Integration Test  | Machine Test          |
| System Test       | System Test           |
| End-to-End Test   | Flow Test             |
