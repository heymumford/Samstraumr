# Getting Started

## Table of Contents
- [Setting Up Your Environment](#setting-up-your-environment)
- [Using the CLI](#using-the-cli)
- [Creating Your First Tube](#creating-your-first-tube)
- [Understanding the Flow](#understanding-the-flow)
- [Connecting Tubes Together](#connecting-tubes-together)
- [Monitoring and Adaptation](#monitoring-and-adaptation)
- [Building Your First Bundle](#building-your-first-bundle)
- [Common Patterns](#common-patterns)
- [Troubleshooting](#troubleshooting)
- [Next Steps](#next-steps)

## Setting Up Your Environment

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- IDE with Java support

### Adding samstraumr to your project

Maven dependency:

```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>1.4.1</version>
</dependency>
```

### Project structure

Recommended project organization:

## Using the CLI

Samstraumr includes a unified CLI interface called `s8r` that simplifies common tasks like building, testing, and version management.

### Basic commands

```bash
# Getting Started
./s8r build

# Getting Started
./s8r test all

# Getting Started
./s8r version get
```

### Building the project

```bash
# Getting Started
./s8r build fast

# Getting Started
./s8r build -c test

# Getting Started
./s8r build install
```

### Running tests

```bash
# Getting Started
./s8r test unit                # Unit tests
./s8r test integration         # Integration tests
./s8r test atl                 # Above-The-Line (critical) tests

# Getting Started
./s8r test -p atl-tests flow
```

### Managing versions

The CLI includes a complete version management system:

```bash
# Getting Started
./s8r version get

# Getting Started
./s8r version bump patch       # Bug fixes (1.2.3 -> 1.2.4)
./s8r version bump minor       # New features (1.2.3 -> 1.3.0)
./s8r version bump major       # Breaking changes (1.2.3 -> 2.0.0)

# Getting Started
./s8r version set 1.5.0
```

For complete documentation of version management, see [Version Management](../reference/version-management.md).

### Quality checks

```bash
# Getting Started
./s8r quality check

# Getting Started
./s8r quality spotless         # Code formatting
./s8r quality checkstyle       # Style checks
./s8r quality encoding         # File encoding checks
```

### Getting help

Every command has built-in help available:

```bash
# Getting Started
./s8r help

# Getting Started
./s8r help build
./s8r help test
./s8r help version
```


## Creating Your First Tube

### 1. implement basic tube

```java
package org.yourdomain.tubes;

import org.samstraumr.tube.Tube;
import org.samstraumr.tube.core.BirthCertificate;
import org.samstraumr.tube.core.TubeState;
import org.samstraumr.tube.api.TubeProcessor;
import org.samstraumr.tube.api.TubeMonitor;
import org.samstraumr.tube.api.TubeResourceManager;
import org.samstraumr.tube.logging.TubeLogger;

import java.time.Instant;
import java.util.regex.Pattern;

public class EmailValidatorTube implements Tube {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final BirthCertificate identity;
    private TubeState designState;
    private DynamicState dynamicState;
    private final TubeLogger logger;
    private final TubeProcessor processor;
    private final TubeMonitor monitor;
    private final TubeResourceManager resources;

    public EmailValidatorTube() {
        // Identity and state
        this.identity = new BirthCertificate.Builder()
            .withID("T1")
            .withPurpose("Email validation")
            .withCreationTime(Instant.now())
            .build();

        this.designState = TubeState.FLOWING;
        this.dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .build();
        this.logger = new TubeLogger(identity);
        
        // Core components
        this.processor = createProcessor();
        this.monitor = createMonitor();
        this.resources = createResourceManager();
    }

    // Core processing logic
    private TubeProcessor createProcessor() {
        return input -> {
            if (input == null) {
                return ValidationResult.invalid("Email cannot be null");
            }

            String email = input.toString();
            boolean isValid = EMAIL_PATTERN.matcher(email).matches();

            return isValid
                ? ValidationResult.valid(email)
                : ValidationResult.invalid("Invalid email format: " + email);
        };
    }

    // Health monitoring
    private TubeMonitor createMonitor() {
        return () -> new HealthAssessment.Builder()
            .withTimestamp(Instant.now())
            .withStatus(designState == TubeState.FLOWING ? "HEALTHY" : "UNHEALTHY")
            .build();
    }

    // Resource management
    private TubeResourceManager createResourceManager() {
        return new TubeResourceManager() {
            @Override
            public void initialize() {
                logger.info("Resources initialized");
            }

            @Override
            public void release() {
                logger.info("Resources released");
            }
        };
    }

    // Domain-specific result
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        private final String value;

        private ValidationResult(boolean valid, String message, String value) {
            this.valid = valid;
            this.message = message;
            this.value = value;
        }

        public static ValidationResult valid(String value) {
            return new ValidationResult(true, "Valid", value);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message, null);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getValue() { return value; }
    }
}

## Understanding the Flow

### Processing pathway

1. **Input Reception**: Data enters through `process()`
2. **Validation**: Input is checked against rules
3. **Result Generation**: `ValidationResult` is created
4. **Output Delivery**: Result flows to caller

### Adding self-awareness

Enhanced processor with state tracking and metrics:

```java
private TubeProcessor createProcessor() {
    return input -> {
        // Track processing state
        dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withProperty("processing", true)
            .build();

        try {
            if (input == null) {
                metrics.increment("null_input");
                return ValidationResult.invalid("Email cannot be null");
            }

            String email = input.toString();
            boolean isValid = EMAIL_PATTERN.matcher(email).matches();

            if (isValid) {
                metrics.increment("valid_email");
                return ValidationResult.valid(email);
            } else {
                metrics.increment("invalid_email");
                return ValidationResult.invalid("Invalid email format: " + email);
            }
        } finally {
            // Mark processing complete
            dynamicState = new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("processing", false)
                .build();
        }
    };
}

### Connecting the flow

Simple pipeline implementation:

```java
public class EmailProcessingFlow {
    private final EmailValidatorTube validator;
    private final EmailFormatterTube formatter;

    public EmailProcessingFlow() {
        this.validator = new EmailValidatorTube();
        this.formatter = new EmailFormatterTube();
    }

    public FormatResult processEmail(String email) {
        ValidationResult validationResult = 
            (ValidationResult) validator.process(email);
        return (FormatResult) formatter.process(validationResult);
    }
}

### Adaptive behavior

Self-adaptation implementation:

```java
public void checkHealth() {
    HealthAssessment health = monitor.assessHealth();

    if ("DEGRADED".equals(health.getStatus())) {
        if (designState == TubeState.FLOWING) {
            // Start adaptation
            designState = TubeState.ADAPTING;
            logger.warn("Error rate {} exceeds threshold - adapting",
                     health.getMetric("error_rate"));
                     
            // Adaptation logic here
            // - Adjust validation rules
            // - Implement stricter checking
            // - Alert upstream systems
            
            logger.info("Adaptation complete");
        }
    } else if (designState == TubeState.ADAPTING) {
        // Return to normal flow
        designState = TubeState.FLOWING;
        logger.info("Health restored");
    }
}

## Common Patterns

### Validator-transformer-persister

A standard processing pipeline:

1. **Validator Tube**: Validates input requirements
2. **Transformer Tube**: Converts valid data
3. **Persister Tube**: Stores processed data

### Observer tube

Passive monitoring without modification:

```java
public class EmailAuditTube implements Tube {
    private TubeProcessor createProcessor() {
        return input -> {
            // Log for audit purposes
            logger.info("Email processed: {}", input);
            metrics.increment("emails_processed");
            
            // Return unchanged - observation only
            return input;
        };
    }
}

## Troubleshooting

### Communication issues

- **Type compatibility**: Ensure output/input type matching between tubes
- **State verification**: Confirm all tubes are in `FLOWING` state
- **Error propagation**: Implement consistent error handling

### Adaptation problems

- **Metrics collection**: Implement comprehensive metrics
- **Transition conditions**: Define clear state transition rules
- **Logging**: Add detailed state change logging

### Complexity management

- **Start minimal**: Begin with 2-3 tubes
- **Focus on interfaces**: Define clear contracts between tubes
- **Use hierarchical organization**: Group related tubes into bundles

## Next Steps

1. **Expand functionality**: Add specialized tubes
2. **Implement state management**: Add sophisticated state transitions
3. **Organize as machines**: Group bundles into cohesive machines
4. **Add metrics**: Implement statistical monitoring 
5. **Test with BDD**: Create behavior specifications

### Related resources

- [State Management](./state-management.md): Dual state system
- [Bundles and Machines](./bundles-and-machines.md): Component organization
- [Testing Strategy](./testing.md): BDD verification

[← Core Concepts](./core-concepts.md) | [State Management →](./state-management.md)
