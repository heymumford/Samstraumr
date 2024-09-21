
# Installation Guide

This document outlines the steps required to install and set up the **Samstraumr Causality Engine** project.

## Prerequisites

Before you begin, ensure you have the following installed on your system:

- **Docker**: [Get Docker](https://www.docker.com/products/docker-desktop)
- **Git**: [Get Git](https://git-scm.com/downloads)

For additional development:

- **Java 21 (Eclipse Temurin)**: Java Development Kit version 21
- **Python 3.x**: For Python utilities and scripts
- **R**: For data processing and visualization utilities

## Cloning the Repository

To get started, clone the repository to your local machine:

```bash
git clone https://github.com/your-username/samstraumr-causality-engine.git
cd samstraumr-causality-engine
```

## Building the Docker Image

This project is set up to run inside a Docker container for ease of setup and environment consistency.

To build the Docker image:

```bash
docker build -t samstraumr-causality-engine .
```

## Running the Docker Container

After building the Docker image, you can run the container with the following command:

```bash
docker run -it samstraumr-causality-engine
```

This will launch the container and run the Java application as described in the `Dockerfile`.

## Running Tests

Once inside the Docker container, you can run the tests for the Java, Python, and R components as follows:

### Running Java Tests

```bash
mvn test
```

### Running Python Tests

```bash
pytest
```

### Running R Tests

```bash
Rscript -e "testthat::test_dir('app/r/src/com/samstraumr/utils')"
```

## Manual Development Setup (Optional)

If you prefer to work outside the Docker container, ensure the following tools are installed:

1. **Java 21**: Download and install [Eclipse Temurin JDK 21](https://adoptium.net/).
2. **Python 3.x**: Install [Python](https://www.python.org/downloads/).
3. **R**: Install [R](https://cran.r-project.org/).

### Installing Python Dependencies

To install Python dependencies:

```bash
pip install pandas matplotlib pytest
```

### Installing R Dependencies

To install R dependencies:

```bash
R -e "install.packages(c('ggplot2', 'forecast', 'testthat', 'dplyr'), repos='http://cran.us.r-project.org')"
```

Now you're ready to develop locally or contribute to the project.

## Troubleshooting

If you encounter any issues during the installation or setup, please refer to the project’s issue tracker or reach out to the maintainers for assistance.

