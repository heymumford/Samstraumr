# Samstraumr

[![Version](https://img.shields.io/badge/version-1.2.9-blue)](https://github.com/heymumford/Samstraumr/releases) [![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml) [![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/)

A design framework for building adaptive software systems using Tube-Based Development (TBD).

## Description

Samstraumr (Old Norse: "unified flow") is a Java-based framework that implements principles from systems theory to create resilient, self-monitoring components called "tubes." These tubes can be composed to form processing pipelines that exhibit emergent intelligence, self-adaptation, and fault tolerance.

## Requirements

- Java 17 or higher
- Maven 3.6.3 or higher

## Installation

```bash
# Clone the repository
git clone https://github.com/heymumford/Samstraumr.git

# Build the project
cd Samstraumr
mvn clean install
```

## Usage

Add Samstraumr to your Maven project:

```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>1.2.6</version>
</dependency>
```

Create a simple tube:

```java
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.TubeStatus;

public class HelloTube extends Tube {
    @Override
    public void process(Object input) {
        System.out.println("Processing: " + input);
        // Processing logic here
        setStatus(TubeStatus.FLOWING);
    }
}
```

## Documentation

For comprehensive documentation, see:

- [Core Concepts](./docs/concepts/core-concepts.md)
- [Getting Started Guide](./docs/guides/getting-started.md)
- [API Reference](./docs/reference/api-reference.md)

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](./docs/contribution/contributing.md) for details.

## License

This project is licensed under the Mozilla Public License 2.0 - see [LICENSE](./LICENSE) for details.

## Contact

Eric C. Mumford ([@heymumford](https://github.com/heymumford))
