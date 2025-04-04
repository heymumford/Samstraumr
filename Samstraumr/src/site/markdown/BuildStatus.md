# Build Status

This page shows the current flow status of the Samstraumr build pipeline, similar to how Tubes monitor their processing state.

## Pipeline Flow Status

|     Stage      |          Status          |                Description                |
|----------------|--------------------------|-------------------------------------------|
| INITIALIZATION | ${initialization.status} | Project setup and environment preparation |
| ORCHESTRATION  | ${orchestration.status}  | Basic system assembly verification        |
| PROCESSING     | ${processing.status}     | Core build and compilation                |
| VALIDATION     | ${validation.status}     | Test execution and verification           |
| ANALYSIS       | ${analysis.status}       | Code quality and metrics analysis         |
| REPORTING      | ${reporting.status}      | Report generation and publishing          |

Legend:
- <span style="color: green;">FLOWING</span>: Stage completed successfully
- <span style="color: orange;">ADAPTING</span>: Stage adapting to unexpected conditions
- <span style="color: red;">BLOCKED</span>: Stage encountered issues
- <span style="color: gray;">WAITING</span>: Stage not yet executed

## Last Build Information

- **Build Date**: ${build.date}
- **Build Version**: ${project.version}
- **Build Trigger**: ${build.trigger}
- **Build Duration**: ${build.duration}

## Pipeline Stages

### 1. INITIALIZATION

Initializes the build environment, verifies dependencies, and prepares the workspace. This stage ensures that all necessary tools and resources are available.

### 2. ORCHESTRATION

Verifies basic system assembly and connectivity, ensuring that components can be properly orchestrated and work together as expected.

### 3. PROCESSING

Performs the core build operations, including compilation, resource processing, and artifact generation.

### 4. VALIDATION

Executes the test suite to validate the system behavior against expectations. This includes unit tests, integration tests, and acceptance tests.

### 5. ANALYSIS

Analyzes code quality, test coverage, and other metrics to ensure the system meets quality standards.

### 6. REPORTING

Generates and publishes reports on the build results, providing visibility into the system's health and status.

## Recent Builds

|      Build      |     Date      |     Status      |     Duration      |     Trigger      |
|-----------------|---------------|-----------------|-------------------|------------------|
| ${build.number} | ${build.date} | ${build.status} | ${build.duration} | ${build.trigger} |
| ...             | ...           | ...             | ...               | ...              |

## Notifications

${build.notifications}
