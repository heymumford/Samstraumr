# README.md (samstraumr-core)

## Overview

**Samstraumr-Core** is the foundation of the Tube-Based Design (TBD) framework. This module provides the basic building blocks of computation and interaction, including **Tubes** (atomic units) and **Composites** (higher-order assemblies), enabling dynamic, adaptive systems. The framework leverages Java21 for its core architecture while maintaining compatibility with other tools and libraries for testing and deployment.

---

## Vocabulary and Concepts

- **Tube**: The fundamental unit of operation. Tubes process inputs, perform specific tasks, and produce outputs. They represent the atomic base of the framework.
- **Composite**: A higher-level structure made from combinations of multiple tubes. Composites allow for more complex functionality by orchestrating tubes to work together.
- **Connection**: Defines how tubes interact, ensuring that data flows smoothly between components.
- **Operations**: Encapsulated tasks that a tube performs, defined by `TubeOperations`. These actions are reusable across different contexts.
- **Workload**: The data or tasks currently being processed by a tube or composite.
- **Environment**: Provides external configurations that tubes can adapt to, managed by `Environment.java`.
- **Logger**: Each tube includes logging features for monitoring, facilitated by `TubeLogger`.
- **Status**: Reflects the current state of the tube, whether active, idle, or in error. Defined by `TubeStatus`.

---

## Structure

- **Tubes**: Located in the `tube/` directory, representing the atomic components of the system.
- **Composites**: Found in the `composites/` directory, containing more complex structures formed by multiple tubes working together.

### What the POM Defines

The POM file (`pom.xml`) defines the key configuration settings for **samstraumr-core**, including dependencies, build settings, and plugins for testing and compiling the project.

- **Java21 Compilation**: The project is compiled using Java21, ensuring modern, efficient execution.
- **JUnit 5**: Provides the testing framework used to validate the core tube and composite implementations.
- **Mockito**: Enables mocking of components for unit tests, ensuring isolated and comprehensive test coverage.
- **Cucumber**: Used for behavior-driven development (BDD) testing, allowing for descriptive test scenarios.
- **Log4j2**: Handles logging, ensuring that tubes and composites report performance and errors in real-time.
- **Maven Surefire**: Manages running test suites, ensuring tests are executed consistently with customizable reporting.

---

## Key Components

- **Tubes**: The fundamental building blocks of Samstraumr. Each tube encapsulates a distinct operation. Explore the following key classes:
  - [`Tube.java`](./src/main/java/org/samstraumr/tube/Tube.java): The core structure that defines the basic behavior of tubes.
  - [`TubeConnection.java`](./src/main/java/org/samstraumr/tube/TubeConnection.java): Manages the connections between tubes.
  - [`TubeOperations.java`](./src/main/java/org/samstraumr/tube/TubeOperations.java): Encapsulates the core operations performed by tubes.
  - [`TubeStatus.java`](./src/main/java/org/samstraumr/tube/TubeStatus.java): Tracks the status and health of each tube.
  - [`TubeLogger.java`](./src/main/java/org/samstraumr/tube/TubeLogger.java): Logs relevant tube activity and performance.
- **Composites**: Higher-order structures composed of multiple tubes working together:
  - [`composites`](./src/main/java/org/samstraumr/composites): Explore how multiple tubes are combined into composite structures.
- **Environment**: Configurations and external conditions influencing tube behavior:
  - [`Environment.java`](./src/main/java/org/samstraumr/tube/Environment.java): Manages environmental data and interactions.
- **Testing**: Comprehensive test suites for validating tubes and composites:
  - [`TubeCollaborationSteps.java`](./src/test/java/org/samstraumr/tube/steps/TubeCollaborationSteps.java): Tests the collaborative functionality of tubes.
  - [`TubeResilienceSteps.java`](./src/test/java/org/samstraumr/tube/steps/TubeResilienceSteps.java): Ensures tubes maintain resilience under stress.
  - [`tube_collaboration.feature`](./src/test/resources/features/tube_collaboration.feature): Feature test file demonstrating tube collaboration.

---

## How To Get Started

1. **Explore the Tube Architecture**: Start with the core tube structures to understand how tubes operate individually.
   - [`Tube.java`](./src/main/java/org/samstraumr/tube/Tube.java)
   - [`TubeOperations.java`](./src/main/java/org/samstraumr/tube/TubeOperations.java)
2. **Dive into Composites**: Learn how tubes combine to form more complex structures.
   - [`composites`](./src/main/java/org/samstraumr/composites)
3. **Run Tests**: Check the tests that validate the system’s robustness and collaborative nature.
   - [`TubeCollaborationSteps.java`](./src/test/java/org/samstraumr/tube/steps/TubeCollaborationSteps.java)
   - [`tube_resilience.feature`](./src/test/resources/features/tube_resilience.feature)
4. **Review Logging and Monitoring**: Understand how tubes are monitored and their status tracked.
   - [`TubeLogger.java`](./src/main/java/org/samstraumr/tube/TubeLogger.java)

## Documentation

- [What is BDD?](WhatIsBDD.md)
- [Rationale for Using BDD in Samstraumr](RationaleOnUsingBDD.md)
