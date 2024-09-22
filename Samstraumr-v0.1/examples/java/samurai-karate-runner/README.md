# Samurai Karate Runner

## Overview

This project provides a Docker environment for running Karate tests with a focus on Tube-Based Design (TBD) principles. It includes all necessary dependencies, such as OpenJDK, Maven, and Karate, preconfigured in an Alpine Linux-based image.

## Key Features

- **Tube-Based Design (TBD)**: The Dockerfile is organized using TBD principles, promoting modularity, encapsulation, composability, and adaptability.
- **Interactive Shell**: The Docker image allows users to interact with the container via a Bash shell for exploration and troubleshooting.
- **Non-Root Execution**: Tests are executed as a non-root user (`karate`) for enhanced security.

## Prerequisites

- Docker installed on your system.

## Building the Docker Image

To build the Docker image, run the following command from the root directory of the project:

    docker build -t karate-test-env:latest .

## Usage

### Running Karate Tests

To run your Karate tests using the Docker image, use the following command:

    docker run -v /path/to/your/tests:/karate/tests karate-test-env:latest

Replace `/path/to/your/tests` with the path to your local directory containing the Karate test files.

### Running Specific Tests

To run specific Karate tests or pass additional Maven options, you can provide arguments to the Docker run command:

    docker run -v /path/to/your/tests:/karate/tests karate-test-env:latest -Dtest=SpecificTestRunner

### Interactive Shell Access

If you want to interact with the Docker container using a Bash shell, you can start the container without any additional arguments:

    docker run -it --rm karate-test-env:latest

This command will drop you into an interactive Bash shell where you can explore the environment, run commands, and inspect the container's configuration.

## Verifying the Image

You can verify the installation of Java and Maven within the container using the following commands:

    docker run --rm karate-test-env:latest java -version
    docker run --rm karate-test-env:latest mvn -version

## Maintenance Notes

- **Updating Java**: To update the Java version, modify the `JAVA_VERSION` argument in the Dockerfile.
- **Updating Maven**: To update Maven, modify the `MAVEN_VERSION` argument in the Dockerfile.
- **Adding New Dependencies**: For new dependencies, consider extending the Karate Tube or adding a new Tube.

## Security Notes

- This image runs tests as a non-root user (`karate`) for improved security.
- Regularly update the base Alpine image to get the latest security patches.

## Tube-Based Design (TBD) Explanation

The Dockerfile is structured using Tube-Based Design (TBD) principles. Each "Tube" represents a modular, self-contained unit of functionality, such as the base system, Java environment, and Maven setup. This approach promotes better organization, maintainability, and reusability.

