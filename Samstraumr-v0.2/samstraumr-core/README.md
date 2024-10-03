### README.md (samstraumr-core)

#### Overview
**Samstraumr-Core** is the foundation of the Tube-Based Design (TBD) framework. This module provides the basic building blocks of computation and interaction, including **Tubes** (atomic units) and **Composites** (higher-order assemblies), enabling dynamic, adaptive systems. The framework leverages Java21 for its core architecture while maintaining compatibility with other tools and libraries for testing and deployment.

---

#### Vocabulary and Concepts

- **Tube**: The fundamental unit of operation. Tubes process inputs, perform specific tasks, and produce outputs. They represent the atomic base of the framework.
- **Composite**: A higher-level structure made from combinations of multiple tubes. Composites allow for more complex functionality by orchestrating tubes to work together.
- **Connection**: Defines how tubes interact, ensuring that data flows smoothly between components.
- **Operations**: Encapsulated tasks that a tube performs, defined by `TubeOperations`. These actions are reusable across different contexts.
- **Workload**: The data or tasks currently being processed by a tube or composite.
- **Environment**: Provides external configurations that tubes can adapt to, managed by `Environment.java`.
- **Logger**: Each tube includes logging features for monitoring, facilitated by `TubeLogger`.
- **Status**: Reflects the current state of the tube, whether active, idle, or in error. Defined by `TubeStatus`.

---

#### Structure

- **Tubes**: Located in the `tube/` directory, representing the atomic components of the system.
- **Composites**: Found in the `composites/` directory, containing more complex structures formed by multiple tubes working together.

---

#### What the POM Defines

The POM file (`pom.xml`) defines the key configuration settings for **samstraumr-core**, including dependencies, build settings, and plugins for testing and compiling the project.

- **Java21 Compilation**: The project is compiled using Java21, ensuring modern, efficient execution.
- **JUnit 5**: Provides the testing framework used to validate the core tube and composite implementations.
- **Mockito**: Enables mocking of components for unit tests, ensuring isolated and comprehensive test coverage.
- **Cucumber**: Used for behavior-driven development (BDD) testing, allowing for descriptive test scenarios.
- **Log4j2**: Handles logging, ensuring that tubes and composites report performance and errors in real-time.
- **Maven Surefire**: Manages running test suites, ensuring tests are executed consistently with customizable reporting.

---

#### Key Components

- **Tubes** (`tube/`): This directory includes the foundational classes like `Tube.java`, `TubeLogger.java`, and `TubeOperations.java`, which are responsible for defining the behavior and structure of individual tubes.
  
- **Composites** (`composites/`): This directory will eventually house higher-level combinations of tubes that perform more complex processing.

- **Testing** (`test/`): Behavior-driven tests using **Cucumber** (`*.feature` files) and JUnit 5 are implemented to ensure functionality, resilience, and collaboration between tubes and composites.

---

#### How to Get Started

1. Explore the **tube** directory to understand how atomic tubes function.
2. Look into the **composites** directory for examples of how multiple tubes can work together to form sophisticated systems.
3. Use the **test** directory to see how the system is validated through JUnit and Cucumber BDD tests.

---
