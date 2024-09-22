
# Samstraumr Causality Engine

**Samstraumr** is a framework designed for creating modular, scalable, and adaptable systems. The **Causality Engine** is a practical implementation of Samstraumr principles, built to perform time-series data correlation and causality analysis with a focus on simplicity, resilience, and system evolution.

## Project Structure

The project is structured as follows:

```
samstraumr-causality-engine/
├── app/
│   ├── Dockerfile                # Dockerfile to build the environment
│   ├── LICENSE                   # License information
│   ├── README.md                 # Project documentation
│   ├── pom.xml                   # Maven configuration for Java
│   ├── java/                     # Java source code for the causality engine
│   │   └── src/
│   │       └── main/
│   │           └── java/com/samstraumr/tubes/
│   ├── python/                   # Python utility scripts for the engine
│   │   └── src/com/samstraumr/utils/
│   └── r/                        # R scripts for additional data processing and visualization
│       └── src/com/samstraumr/utils/
├── docs/                         # Documentation for installation, API, and usage
│   ├── API.md
│   └── INSTALLATION.md
└── reorg-proj.sh                 # Script used to reorganize the project structure
```

## Getting Started

### Prerequisites

- **Docker**: Used to build and run the project in a clean and reproducible environment.
- **Java 21 (Eclipse Temurin)**: The core of the causality engine.
- **Python 3** and **R**: For additional utilities and data handling.

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/samstraumr-causality-engine.git
   cd samstraumr-causality-engine
   ```

2. **Build the Docker image**:
   ```bash
   docker build -t samstraumr-causality-engine .
   ```

3. **Run the Docker container**:
   ```bash
   docker run -it samstraumr-causality-engine
   ```

### Running Tests

The project includes tests for **Java**, **Python**, and **R** components. To run all tests:

1. **Inside the Docker container**, execute the test commands:

   **Java**:
   ```bash
   mvn test
   ```

   **Python**:
   ```bash
   pytest
   ```

   **R**:
   ```bash
   Rscript -e "testthat::test_dir('app/r/src/com/samstraumr/utils')"
   ```

## Features

- **Causality Analysis**: Identify correlations and potential causality in time-series data.
- **Modular Design**: Built following the Samstraumr philosophy, focusing on modularity, scalability, and adaptability.
- **Polyglot System**: Leverages Java, Python, and R for a robust and flexible approach to data handling.
- **Dockerized**: Easily deployable and scalable using Docker.

## Documentation

For detailed installation instructions and API documentation, refer to the files inside the `docs/` folder:

- [INSTALLATION.md](docs/INSTALLATION.md)
- [API.md](docs/API.md)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

We welcome contributions to Samstraumr! If you'd like to contribute, please read our [contributing guidelines](CONTRIBUTING.md) for details on the process.
