# Behavior-Driven Development in Samstraumr

## Table of Contents

1. [Introduction to BDD](#introduction-to-bdd)
2. [Why Samstraumr Uses BDD](#why-samstraumr-uses-bdd)
3. [Principles of BDD](#principles-of-bdd)
4. [BDD with Cucumber in Samstraumr](#bdd-with-cucumber-in-samstraumr)
5. [Feature File Structure](#feature-file-structure)
6. [Step Definitions](#step-definitions)
7. [Test Categories and Tags](#test-categories-and-tags)
8. [Running Tests](#running-tests)
9. [Best Practices](#best-practices)
10. [References](#references)

## Introduction to BDD

Behavior-Driven Development (BDD) is a collaborative approach to software development that bridges the gap between technical and non-technical team members. It enhances communication by using a simple, shared language to describe the behavior of an application, ensuring that all stakeholders have a clear understanding of project requirements.

BDD focuses on defining the behavior of a system from the outside in, encouraging collaboration between developers, QA, and non-technical stakeholders by using a shared language to describe the system's behavior.

### Underlying Principles of BDD

1. **Focus on Expected Behavior**: Instead of concentrating on testing individual components (as in Test-Driven Development), BDD emphasizes specifying the expected behavior of the system from the user's perspective.

2. **Use of Ubiquitous Language**: BDD encourages using a common language derived from the business domain. This shared vocabulary helps prevent misunderstandings and aligns the development team with business goals.

3. **Collaboration Among Stakeholders**: Developers, testers, and business analysts work together to define requirements. This collaboration ensures that everyone has the same understanding of what needs to be built.

4. **Executable Specifications**: Requirements are written in a clear format that can be turned into automated tests. These tests serve as both documentation and validation of the system's behavior.

### Practical Operations of BDD

1. **Writing User Stories**:

   - **Template**:
     - **As a** [user role]
     - **I want** [feature]
     - **So that** [benefit]

   - **Purpose**: Captures who needs what and why, focusing on delivering business value.

2. **Defining Acceptance Criteria with Scenarios**:

   - **Format**:
     - **Given** [initial context]
     - **When** [event occurs]
     - **Then** [outcome]

   - **Purpose**: Breaks down user stories into specific, testable examples of desired behavior.

3. **Automating Scenarios**:

   - **Tools**: Use BDD frameworks like Cucumber, JBehave, or SpecFlow to turn written scenarios into executable tests.

   - **Purpose**: Ensures that specifications are always up-to-date and that the system behaves as expected.

4. **Developing to Meet Specifications**:

   - **Process**:
     - Write code to fulfill the scenarios.
     - Run automated tests to verify behavior.
     - Refactor code while keeping tests green.

   - **Purpose**: Aligns development with defined behaviors, reducing defects and rework.

5. **Continuous Feedback and Refinement**:

   - **Feedback Loop**: Failing tests highlight deviations from expected behavior, allowing for immediate correction.

   - **Collaboration**: Regular discussions among team members to refine requirements based on new insights or changes.

## Why Samstraumr Uses BDD

Samstraumr has adopted BDD as a central part of its testing and documentation strategy for several compelling reasons:

### Alignment with Samstraumr Philosophy

Samstraumr, a framework for creating adaptive, self-aware software systems, represents a paradigm shift in software development. Its tube-based design and living systems approach offer flexibility and resilience that require an equally innovative testing and documentation strategy. Traditional testing and documentation methods fall short when applied to Samstraumr's dynamic, evolving systems.

BDD's focus on behavior meshes perfectly with Samstraumr's concept of tubes with evolving purposes, and Cucumber's natural language scenarios reflect the "ubiquitous language" principle, bridging technical and non-technical stakeholders.

### Living Documentation

Cucumber features serve as executable specifications that evolve alongside the system they describe. This aligns with Samstraumr's adaptive nature, ensuring documentation remains current.

### Holistic Testing

BDD scenarios can describe both unit-level tube behavior and system-wide interactions, capturing Samstraumr's multi-layered complexity.

### Cynefin Framework Integration

BDD supports different approaches for various Cynefin domains:
- Simple: Straightforward scenarios for well-understood behaviors.
- Complicated: Detailed scenarios with multiple steps for complex but knowable processes.
- Complex: Scenarios that allow for emergent behaviors and multiple valid outcomes.
- Chaotic: Rapid prototyping of scenarios to probe and respond to unpredictable situations.

### Benefits of BDD for Samstraumr

1. **Enhanced Communication**: A shared language improves collaboration between technical and non-technical team members.

2. **Clear Requirements**: Well-defined behaviors reduce ambiguity and misunderstandings.

3. **Quality Assurance**: Automated tests catch issues early, leading to more reliable software.

4. **Adaptability**: The iterative nature of BDD accommodates changes in requirements smoothly, which is essential for Samstraumr's evolving nature.

## BDD with Cucumber in Samstraumr

Samstraumr implements BDD using Cucumber, a tool that allows us to:

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

## References

- [Dan North: Introducing BDD](https://dannorth.net/introducing-bdd/)
- [Martin Fowler: Given-When-Then](https://martinfowler.com/bliki/GivenWhenThen.html)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [BDD 101: The Gherkin Language](https://cucumber.io/docs/gherkin/reference/)