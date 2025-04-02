# Getting Started with Samstraumr

```
Last updated: April 2, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

## Table of Contents
- [Introduction: Your First Steps](#introduction-your-first-steps)
- [Setting Up Your Environment](#setting-up-your-environment)
- [Creating Your First Tube](#creating-your-first-tube)
- [Understanding the Flow](#understanding-the-flow)
- [Connecting Tubes Together](#connecting-tubes-together)
- [Monitoring and Adaptation](#monitoring-and-adaptation)
- [Building Your First Bundle](#building-your-first-bundle)
- [Common Patterns for Beginners](#common-patterns-for-beginners)
- [Troubleshooting](#troubleshooting)
- [Next Steps](#next-steps)

## Introduction: Your First Steps

Welcome to Samstraumr, where software flows like water and adapts like life. This guide will take you from theory to practice, helping you build your first flowing system with the care and patience of a gardener planting seeds.

Before we begin coding, take a moment to shift your perspective. Rather than thinking about building a static structure, imagine you're creating a living stream—something that will grow, respond, and evolve over time. Your role is not just to write code, but to create conditions where beneficial flows can emerge naturally.

## Setting Up Your Environment

Let's prepare the soil for your first Samstraumr project.

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code with Java extensions)

### Adding Samstraumr to Your Project

Add the Samstraumr dependency to your Maven `pom.xml`:

```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>0.4</version>
</dependency>
```

Or if you're using Gradle:

```groovy
implementation 'org.samstraumr:samstraumr-core:1.2.0'
```

### Project Structure

While Samstraumr is flexible, a thoughtful organization helps maintain clarity:

```
src/main/java/
├── org/
    ├── yourdomain/
        ├── tubes/        # Individual tube implementations
        ├── bundles/      # Bundle compositions
        ├── machines/     # Machine orchestration
        ├── config/       # Configuration classes
        ├── models/       # Domain models flowing through tubes
        └── app/          # Application entry points
```

## Creating Your First Tube

Let's create a simple tube that validates input data—perhaps checking if a string is a valid email address.

### 1. Implement the Tube Interface

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

    // Tube implementation components
    private final TubeProcessor processor;
    private final TubeMonitor monitor;
    private final TubeResourceManager resources;

    public EmailValidatorTube() {
        // Create a unique identity for this tube
        this.identity = new BirthCertificate.Builder()
            .withID("T1")
            .withPurpose("Email address validation")
            .withCreationTime(Instant.now())
            .build();

        // Initialize state
        this.designState = TubeState.FLOWING;
        this.dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .build();

        // Set up logging
        this.logger = new TubeLogger(identity);

        // Initialize core components
        this.processor = createProcessor();
        this.monitor = createMonitor();
        this.resources = createResourceManager();

        logger.info("Tube created and initialized");
    }

    // Implementation of required Tube interface methods...

    // Core processing logic
    private TubeProcessor createProcessor() {
        return input -> {
            if (input == null) {
                return ValidationResult.invalid("Email cannot be null");
            }

            String email = input.toString();
            boolean isValid = EMAIL_PATTERN.matcher(email).matches();

            if (isValid) {
                return ValidationResult.valid(email);
            } else {
                return ValidationResult.invalid("Invalid email format: " + email);
            }
        };
    }

    // Basic monitoring capability
    private TubeMonitor createMonitor() {
        return () -> {
            // Gather current stats and return health assessment
            return new HealthAssessment.Builder()
                .withTimestamp(Instant.now())
                .withStatus(designState == TubeState.FLOWING ? "HEALTHY" : "UNHEALTHY")
                .build();
        };
    }

    // Simple resource management
    private TubeResourceManager createResourceManager() {
        return new TubeResourceManager() {
            @Override
            public void initialize() {
                // Minimal resources needed for this tube
                logger.info("Resources initialized");
            }

            @Override
            public void release() {
                // Clean up any resources
                logger.info("Resources released");
            }
        };
    }

    // Domain-specific result class
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

### 2. Test Your Tube

Create a simple test to verify your tube works correctly:

```java
package org.yourdomain.tubes.test;

import org.junit.jupiter.api.Test;
import org.yourdomain.tubes.EmailValidatorTube;
import org.yourdomain.tubes.EmailValidatorTube.ValidationResult;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTubeTest {
    @Test
    void shouldValidateCorrectEmailFormat() {
        // Arrange
        EmailValidatorTube tube = new EmailValidatorTube();

        // Act
        ValidationResult result = (ValidationResult) tube.process("user@example.com");

        // Assert
        assertTrue(result.isValid());
        assertEquals("user@example.com", result.getValue());
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        // Arrange
        EmailValidatorTube tube = new EmailValidatorTube();

        // Act
        ValidationResult result = (ValidationResult) tube.process("invalid-email");

        // Assert
        assertFalse(result.isValid());
        assertNull(result.getValue());
        assertTrue(result.getMessage().contains("Invalid email format"));
    }
}
```

## Understanding the Flow

Now that you have a functional tube, let's explore how data flows through it.

### The Processing Pathway

1. **Input Arrives**: Data enters the tube through the `process()` method
2. **Validation Occurs**: The processor examines the input against defined rules
3. **Result Created**: A `ValidationResult` object is created based on the evaluation
4. **Output Returned**: The result flows out of the tube to the caller

This simple flow demonstrates the core concept of a tube as a transformative pathway—data comes in, undergoes a change, and emerges as something new.

### Enriching the Flow

Let's enhance our tube to make it more aware and adaptive:

```java
private TubeProcessor createProcessor() {
    return input -> {
        // Update dynamic state to track processing
        dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withProperty("processing", true)
            .build();

        try {
            if (input == null) {
                updateMetrics("null_input");
                return ValidationResult.invalid("Email cannot be null");
            }

            String email = input.toString();
            boolean isValid = EMAIL_PATTERN.matcher(email).matches();

            if (isValid) {
                updateMetrics("valid_email");
                return ValidationResult.valid(email);
            } else {
                updateMetrics("invalid_email");
                return ValidationResult.invalid("Invalid email format: " + email);
            }
        } finally {
            // Update dynamic state to reflect completion
            dynamicState = new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("processing", false)
                .build();
        }
    };
}

private void updateMetrics(String type) {
    // In a real implementation, this would update counters or gauges
    logger.debug("Processed {}: {}", type, Instant.now());
}
```

This enhancement adds:
- State tracking during processing
- Metrics collection for different outcomes
- Proper cleanup after processing

## Connecting Tubes Together

Individual tubes are powerful, but the true magic of Samstraumr emerges when tubes connect to form flowing systems.

### Creating a Complementary Tube

Let's create another tube that formats valid email addresses for display:

```java
public class EmailFormatterTube implements Tube {
    // Similar setup code to EmailValidatorTube...

    private TubeProcessor createProcessor() {
        return input -> {
            if (!(input instanceof ValidationResult)) {
                logger.warn("Expected ValidationResult but received: {}", input.getClass().getName());
                return FormatResult.error("Invalid input type");
            }

            ValidationResult validationResult = (ValidationResult) input;

            if (!validationResult.isValid()) {
                return FormatResult.error(validationResult.getMessage());
            }

            String email = validationResult.getValue();
            String username = email.substring(0, email.indexOf('@'));
            String domain = email.substring(email.indexOf('@') + 1);

            return new FormatResult(
                username,
                domain,
                username.substring(0, 1).toUpperCase() + "." + domain
            );
        };
    }

    // FormatResult class definition...
}
```

### Connecting the Flow

Now let's connect our tubes to create a simple flow:

```java
public class EmailProcessingFlow {
    private final EmailValidatorTube validator;
    private final EmailFormatterTube formatter;

    public EmailProcessingFlow() {
        this.validator = new EmailValidatorTube();
        this.formatter = new EmailFormatterTube();
    }

    public FormatResult processEmail(String email) {
        // Flow the data through the tubes
        ValidationResult validationResult = (ValidationResult) validator.process(email);
        return (FormatResult) formatter.process(validationResult);
    }
}
```

This simple flow demonstrates how tubes can be connected to create a pipeline of transformations.

## Monitoring and Adaptation

A distinguishing feature of Samstraumr is the ability of tubes to monitor their own health and adapt to changing conditions.

### Implementing Health Checks

Let's enhance our tubes with better self-monitoring:

```java
private TubeMonitor createMonitor() {
    return () -> {
        long totalProcessed = getMetricCount("valid_email") +
                             getMetricCount("invalid_email") +
                             getMetricCount("null_input");

        double errorRate = totalProcessed > 0 ?
            (double)(getMetricCount("invalid_email") + getMetricCount("null_input")) / totalProcessed :
            0.0;

        // Determine health based on error rate
        String status = errorRate < 0.5 ? "HEALTHY" : "DEGRADED";

        return new HealthAssessment.Builder()
            .withTimestamp(Instant.now())
            .withStatus(status)
            .withMetric("total_processed", totalProcessed)
            .withMetric("error_rate", errorRate)
            .build();
    };
}

private long getMetricCount(String metric) {
    // In a real implementation, this would retrieve actual metrics
    // For this example, we'll just return dummy values
    switch (metric) {
        case "valid_email": return 75;
        case "invalid_email": return 20;
        case "null_input": return 5;
        default: return 0;
    }
}
```

### Adding Adaptive Behavior

Now let's make our tube adapt to changing conditions:

```java
public void checkHealth() {
    HealthAssessment health = monitor.assessHealth();

    if ("DEGRADED".equals(health.getStatus())) {
        if (designState == TubeState.FLOWING) {
            // Begin adaptation
            designState = TubeState.ADAPTING;
            logger.warn("High error rate detected ({}) - beginning adaptation",
                     health.getMetric("error_rate"));

            // In a real implementation, this might:
            // - Adjust validation rules
            // - Implement stricter checking
            // - Alert upstream systems

            // For this example, we'll just log the adaptation
            logger.info("Adaptation complete - monitoring for improvement");
        }
    } else if (designState == TubeState.ADAPTING) {
        // Return to normal flow if health improves
        designState = TubeState.FLOWING;
        logger.info("Health restored - resuming normal operation");
    }
}
```

This adaptation logic allows the tube to respond to its own performance metrics, changing its behavior when necessary to maintain optimal operation.

## Building Your First Bundle

As your system grows, organizing related tubes into bundles provides clarity and coordination.

### Creating a Simple Bundle

Let's bundle our email processing tubes together:

```java
public class EmailProcessingBundle implements Bundle {
    private final EmailValidatorTube validator;
    private final EmailFormatterTube formatter;
    private final EmailStorageTube storage; // Assume we've created this tube

    private BundleState designState;
    private DynamicState dynamicState;

    public EmailProcessingBundle() {
        // Create and initialize tubes
        this.validator = new EmailValidatorTube();
        this.formatter = new EmailFormatterTube();
        this.storage = new EmailStorageTube();

        // Initialize bundle state
        this.designState = BundleState.FLOWING;
        this.dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .build();
    }

    public FormatResult processAndStoreEmail(String email) {
        // Check bundle state
        if (designState != BundleState.FLOWING) {
            throw new IllegalStateException("Bundle is not in FLOWING state: " + designState);
        }

        // Flow through the tubes
        ValidationResult validationResult = (ValidationResult) validator.process(email);

        if (!validationResult.isValid()) {
            return FormatResult.error(validationResult.getMessage());
        }

        FormatResult formatResult = (FormatResult) formatter.process(validationResult);
        storage.process(formatResult);

        return formatResult;
    }

    public void monitorHealth() {
        // Check health of all tubes
        boolean allHealthy = Stream.of(validator, formatter, storage)
            .map(tube -> tube.getMonitor().assessHealth())
            .allMatch(health -> "HEALTHY".equals(health.getStatus()));

        // Update bundle state based on tube health
        designState = allHealthy ? BundleState.FLOWING : BundleState.DEGRADED;
    }

    // Additional bundle methods...
}
```

This bundle provides:
- Coordinated access to the email processing flow
- Collective health monitoring across all tubes
- A unified interface hiding the internal tube connections

## Common Patterns for Beginners

As you start working with Samstraumr, these patterns will help you create clean, effective designs:

### The Validator-Transformer-Persister Pattern

A common flow involves validation, transformation, and persistence:

1. **Validator Tube**: Ensures inputs meet requirements
2. **Transformer Tube**: Converts valid data into new formats
3. **Persister Tube**: Stores transformed data

This pattern separates concerns while creating a natural flow of responsibility.

### The Observer Tube Pattern

Sometimes you need to observe data without modifying it:

```java
public class EmailAuditTube implements Tube {
    // Implementation details...

    private TubeProcessor createProcessor() {
        return input -> {
            // Log the email processing for audit purposes
            logger.info("Email processed: {}", input);

            // Return input unchanged - this tube only observes
            return input;
        };
    }
}
```

This pattern allows for monitoring, metrics, or logging without interrupting the main flow.

### The Circuit Breaker Pattern

Protect downstream systems by breaking the circuit when errors occur:

```java
public class CircuitBreakerTube implements Tube {
    private int consecutiveFailures = 0;
    private static final int THRESHOLD = 5;

    // Implementation details...

    private TubeProcessor createProcessor() {
        return input -> {
            if (designState == TubeState.BLOCKED) {
                return ErrorResult.circuitOpen("Circuit is open - too many failures");
            }

            try {
                // Process normally
                Object result = doActualProcessing(input);
                consecutiveFailures = 0; // Reset on success
                return result;
            } catch (Exception e) {
                consecutiveFailures++;

                if (consecutiveFailures >= THRESHOLD) {
                    // Open the circuit
                    designState = TubeState.BLOCKED;
                    logger.error("Circuit opened after {} consecutive failures", THRESHOLD);
                }

                return ErrorResult.processingFailure(e.getMessage());
            }
        };
    }

    // Implementation of circuit reset logic...
}
```

This pattern prevents cascading failures by temporarily stopping flow when problems are detected.

## Troubleshooting

Common issues you might encounter when starting with Samstraumr:

### "My tubes aren't communicating properly"

- **Check data types**: Ensure the output of one tube matches the expected input of the next
- **Verify state**: Make sure all tubes are in the `FLOWING` state
- **Review error handling**: Add proper exception management to see where the flow breaks

### "My tubes aren't adapting as expected"

- **Implement proper monitoring**: Ensure your tubes gather meaningful metrics
- **Add adaptation logic**: Create clear conditions for state transitions
- **Use logging**: Add detailed logging to trace the adaptation process

### "My system feels too complex"

- **Start smaller**: Begin with just 2-3 tubes before expanding
- **Focus on flow**: Ensure data moves smoothly before adding complex adaptation
- **Use bundles**: Group related tubes to manage complexity

## Next Steps

Now that you've created your first flowing system with Samstraumr, consider these next steps:

1. **Expand your bundle**: Add more tubes to handle additional aspects of email processing
2. **Explore state management**: Implement more sophisticated state tracking and transitions
3. **Create a machine**: Organize multiple bundles into a cohesive machine
4. **Add metrics and monitoring**: Implement real metrics collection for better adaptation
5. **Explore testing**: Use Behavior-Driven Development to verify your tubes' behavior

Continue your journey with these related resources:

- [State Management](./StateManagement.md) - Deeper exploration of the dual state system
- [Bundles and Machines](./BundlesAndMachines.md) - Building larger structures
- [Testing Strategy](./Testing.md) - Approaches for verifying tube behavior

---

*Remember, in Samstraumr, we don't just write code—we nurture flows that grow, adapt, and evolve naturally over time.*

[← Return to Core Concepts](./CoreConcepts.md) | [Explore State Management →](./StateManagement.md)
