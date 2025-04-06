<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Getting Started with S8r

## Table of Contents

- [Setting Up Your Environment](#setting-up-your-environment)
- [Using the CLI](#using-the-cli)
- [Creating Your First Component](#creating-your-first-component)
- [Understanding the Flow](#understanding-the-flow)
- [Connecting Components Together](#connecting-components-together)
- [Monitoring and Adaptation](#monitoring-and-adaptation)
- [Building Your First Composite](#building-your-first-composite)
- [Common Patterns](#common-patterns)
- [Troubleshooting](#troubleshooting)
- [Next Steps](#next-steps)

## Setting Up Your Environment

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IDE with Java support

### Adding S8r to your project

Maven dependency:

```xml
<dependency>
    <groupId>org.s8r</groupId>
    <artifactId>s8r-core</artifactId>
    <version>${samstraumr.version}</version>
</dependency>
```

### Project structure

Recommended project organization:

```
my-project/
├── pom.xml                       # Maven Project Object Model
├── src/
│   ├── main/
│   │   ├── java/                 # Java source files
│   │   │   └── org/example/
│   │   │       ├── components/   # Individual components
│   │   │       ├── composites/   # Composites (collections of components)
│   │   │       └── machines/     # Machines (orchestrators)
│   │   └── resources/            # Resources and configuration
│   └── test/
│       ├── java/                 # Test source files
│       │   └── org/example/
│       │       └── features/     # Component tests
│       └── resources/
│           └── features/         # Cucumber feature files
└── README.md                     # Project documentation
```

## Using the CLI

S8r includes a unified CLI interface called `s8r` that simplifies common tasks like building, testing, and version management.

### Basic commands

```bash
# Build the project with default settings
./s8r build

# Run all tests
./s8r test all

# Get current version
./s8r version get
```

### Building the project

```bash
# Fast build (skip quality checks)
./s8r build fast

# Clean and run tests
./s8r build -c test

# Install to local Maven repository
./s8r build install
```

### Running tests

```bash
# Run unit tests (also called component tests)
./s8r test unit

# Run integration tests (also called flow tests)
./s8r test integration

# Run critical tests
./s8r test atl

# Run flow tests with ATL profile 
./s8r test -p atl-tests flow
```

### Managing versions

The CLI includes a complete version management system:

```bash
# Get current version information
./s8r version get

# Bump versions
./s8r version bump patch       # Bug fixes (1.2.3 -> 1.2.4)
./s8r version bump minor       # New features (1.2.3 -> 1.3.0)
./s8r version bump major       # Breaking changes (1.2.3 -> 2.0.0)

# Set a specific version
./s8r version set 1.5.0
```

For complete documentation of version management, see [Version Management](../reference/version-management.md).

### Quality checks

```bash
# Run all quality checks
./s8r quality check

# Run specific checks
./s8r quality spotless         # Code formatting
./s8r quality checkstyle       # Style checks
./s8r quality encoding         # File encoding checks
```

### Getting help

Every command has built-in help available:

```bash
# General help
./s8r help

# Command-specific help
./s8r help build
./s8r help test
./s8r help version
```

## Creating Your First Component

### 1. Implement a basic component

```java
package org.yourdomain.components;

import org.s8r.component.core.Component;
import org.s8r.component.core.Identity;
import org.s8r.component.core.State;
import org.s8r.component.core.ComponentProcessor;
import org.s8r.component.logging.Logger;
import org.s8r.component.core.Environment;

import java.time.Instant;
import java.util.regex.Pattern;

public class EmailValidatorComponent implements Component {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private final Identity identity;
    private State state;
    private final Logger logger;
    private final ComponentProcessor processor;
    private final Environment environment;

    public EmailValidatorComponent(Environment environment) {
        this.environment = environment;
        this.identity = new Identity("email-validator", "Validates email addresses");
        this.state = State.INITIALIZING;
        this.logger = Logger.forComponent(this);
        this.processor = createProcessor();
        
        setState(State.READY);
    }

    // Core processing logic
    private ComponentProcessor createProcessor() {
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

    @Override
    public Object process(Object input) {
        if (getState() != State.ACTIVE) {
            setState(State.ACTIVE);
        }
        
        return processor.process(input);
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public State getState() {
        return state;
    }
    
    @Override
    public void setState(State newState) {
        logger.info("State changing from {} to {}", state, newState);
        this.state = newState;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
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
```

### 2. Create and use a component

```java
// Create an environment
Environment env = new Environment.Builder("validation-env")
    .withParameter("strictMode", "true")
    .build();

// Create component
Component validator = Component.create("Validate user emails", env);

// Process data
Object result = validator.process("user@example.com");
```

## Understanding the Flow

### Processing pathway

1. **Input Reception**: Data enters through `process()`
2. **Validation**: Input is checked against rules
3. **Result Generation**: `ValidationResult` is created
4. **Output Delivery**: Result flows to caller

### Adding self-awareness

Enhanced processor with state tracking and metrics:

```java
private ComponentProcessor createProcessor() {
    return input -> {
        // Track processing start
        setProperty("processing", true);
        setProperty("lastProcessingStart", Instant.now());

        try {
            if (input == null) {
                incrementMetric("null_input");
                return ValidationResult.invalid("Email cannot be null");
            }

            String email = input.toString();
            boolean isValid = EMAIL_PATTERN.matcher(email).matches();

            if (isValid) {
                incrementMetric("valid_email");
                return ValidationResult.valid(email);
            } else {
                incrementMetric("invalid_email");
                return ValidationResult.invalid("Invalid email format: " + email);
            }
        } finally {
            // Mark processing complete
            setProperty("processing", false);
            setProperty("lastProcessingEnd", Instant.now());
        }
    };
}
```

## Connecting Components Together

### Creating a processing pipeline

```java
public class EmailProcessingFlow {
    private final Component validator;
    private final Component formatter;

    public EmailProcessingFlow(Environment env) {
        this.validator = Component.create("Email validator", env);
        this.formatter = Component.create("Email formatter", env);
    }

    public FormatResult processEmail(String email) {
        ValidationResult validationResult = 
            (ValidationResult) validator.process(email);
            
        if (!validationResult.isValid()) {
            return FormatResult.failed(validationResult.getMessage());
        }
        
        return (FormatResult) formatter.process(validationResult.getValue());
    }
}
```

### Using a composite

```java
public class EmailProcessor {
    private final Composite emailComposite;
    
    public EmailProcessor(Environment env) {
        // Create a composite
        this.emailComposite = CompositeFactory.create("Email processing", env);
        
        // Add components to the composite
        emailComposite.addComponent("validator", new EmailValidatorComponent(env));
        emailComposite.addComponent("formatter", new EmailFormatterComponent(env));
        emailComposite.addComponent("sender", new EmailSenderComponent(env));
        
        // Connect components in sequence
        emailComposite.createConnection("validator", "formatter");
        emailComposite.createConnection("formatter", "sender");
    }
    
    public boolean processEmail(String email) {
        // Process through the entire composite
        Object result = emailComposite.process(email);
        return result instanceof Boolean ? (Boolean) result : false;
    }
}
```

## Monitoring and Adaptation

### Implementing adaptive behavior

```java
public void checkHealth() {
    // Get current metrics
    double errorRate = getMetric("error_rate").doubleValue();
    
    if (errorRate > 0.1) { // More than 10% errors
        if (getState() == State.ACTIVE) {
            // Start adaptation
            setState(State.DEGRADED);
            logger.warn("Error rate {} exceeds threshold - adapting", errorRate);
                 
            // Adaptation logic here
            // - Adjust validation rules
            // - Implement stricter checking
            // - Alert upstream systems
            
            logger.info("Adaptation complete");
        }
    } else if (getState() == State.DEGRADED && errorRate < 0.05) {
        // Return to normal operation
        setState(State.ACTIVE);
        logger.info("Health restored");
    }
}
```

## Building Your First Composite

### Creating a composite of components

```java
// Create environment
Environment env = new Environment.Builder("email-env")
    .withParameter("maxRetries", "3")
    .build();

// Create composite
Composite emailComposite = CompositeFactory.create("Email processing composite", env);

// Add components to composite
emailComposite.addComponent("validator", new EmailValidatorComponent(env));
emailComposite.addComponent("normalizer", new EmailNormalizerComponent(env));
emailComposite.addComponent("categorizer", new EmailCategorizerComponent(env));

// Use the composite
Object result = emailComposite.process("USER@example.COM");
```

### Configuring a composite with error handling

```java
// Create a composite with error handling
Composite robustComposite = CompositeFactory.create("Robust composite", env);

// Add components
robustComposite.addComponent("validator", new EmailValidatorComponent(env));
robustComposite.addComponent("processor", new EmailProcessorComponent(env));
robustComposite.addComponent("errorHandler", new ErrorHandlerComponent(env));

// Configure error path
robustComposite.addErrorHandler("validator", "errorHandler");
robustComposite.addErrorHandler("processor", "errorHandler");

// Process with built-in error handling
try {
    Object result = robustComposite.process(input);
    // Normal processing result
} catch (Exception e) {
    // Only happens if error handler itself fails
    logger.error("Critical failure", e);
}
```

## Common Patterns

### Validator-Transformer-Persister

A standard processing pipeline:

1. **Validator Component**: Validates input requirements
2. **Transformer Component**: Converts valid data
3. **Persister Component**: Stores processed data

### Observer Component

Passive monitoring without modification:

```java
public class EmailAuditComponent implements Component {
    private ComponentProcessor createProcessor() {
        return input -> {
            // Log for audit purposes
            logger.info("Email processed: {}", input);
            incrementMetric("emails_processed");
            
            // Return unchanged - observation only
            return input;
        };
    }
}
```

### Circuit Breaker Component

Preventing cascading failures:

```java
public class CircuitBreakerComponent implements Component {
    private boolean circuitOpen = false;
    private int failureCount = 0;
    private final int threshold = 5;
    private Instant lastTest = null;
    private final Duration resetInterval = Duration.ofMinutes(1);
    
    private ComponentProcessor createProcessor() {
        return input -> {
            // Check if circuit is open
            if (circuitOpen) {
                // See if we should try again
                if (lastTest == null || 
                    Duration.between(lastTest, Instant.now()).compareTo(resetInterval) > 0) {
                    // Try the service
                    lastTest = Instant.now();
                    try {
                        // Forward to protected service
                        Object result = protectedService.process(input);
                        // Success - close the circuit
                        circuitOpen = false;
                        failureCount = 0;
                        return result;
                    } catch (Exception e) {
                        // Still failing
                        logger.warn("Service still failing after test", e);
                        return new ServiceUnavailableResult();
                    }
                } else {
                    // Circuit still open, not time to test yet
                    return new ServiceUnavailableResult();
                }
            }
            
            // Circuit closed - try the service
            try {
                Object result = protectedService.process(input);
                // Success - reset failure count
                failureCount = 0;
                return result;
            } catch (Exception e) {
                // Failure - increment count
                failureCount++;
                if (failureCount >= threshold) {
                    // Open the circuit
                    circuitOpen = true;
                    logger.warn("Circuit opened after {} failures", failureCount);
                }
                throw e;
            }
        };
    }
}
```

## Troubleshooting

### Communication issues

- **Type compatibility**: Ensure output/input type matching between components
- **State verification**: Confirm all components are in `ACTIVE` state
- **Error propagation**: Implement consistent error handling

### Adaptation problems

- **Metrics collection**: Implement comprehensive metrics
- **Transition conditions**: Define clear state transition rules
- **Logging**: Add detailed state change logging

### Complexity management

- **Start minimal**: Begin with 2-3 components
- **Focus on interfaces**: Define clear contracts between components
- **Use hierarchical organization**: Group related components into composites

## Next Steps

1. **Expand functionality**: Add specialized components
2. **Implement state management**: Add sophisticated state transitions
3. **Organize as machines**: Group composites into cohesive machines
4. **Add metrics**: Implement statistical monitoring
5. **Test with BDD**: Create behavior specifications

### Related resources

- [Core Concepts](../concepts/core-concepts.md): Fundamental S8r concepts
- [Component Patterns](./component-patterns.md): Reusable component patterns
- [Composites and Machines](../concepts/composites-and-machines.md): Component organization
- [Testing Strategy](../testing/testing-strategy.md): BDD verification

[← Introduction](./introduction.md) | [Component Patterns →](./component-patterns.md)
