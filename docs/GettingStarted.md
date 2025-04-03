# Getting Started with Samstraumr

```
Version: 0.5.7
Last updated: April 2, 2025
Author: Eric C. Mumford (@heymumford)
```

## Table of Contents
- [Setting Up Your Environment](#setting-up-your-environment)
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

### Adding Samstraumr to Your Project

Maven dependency:

```xml
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>0.5.7</version>
</dependency>
```

Gradle dependency:

```groovy
implementation 'org.samstraumr:samstraumr-core:0.5.7'
```

### Project Structure

Recommended project organization:

```
src/main/java/org/yourdomain/
├── tubes/        # Individual tube implementations
├── bundles/      # Bundle compositions
├── machines/     # Machine orchestration
├── config/       # Configuration classes
├── models/       # Domain models
└── app/          # Application entry points
```

## Creating Your First Tube

### 1. Implement Basic Tube

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
```

### 2. Test the Tube

```java
package org.yourdomain.tubes.test;

import org.junit.jupiter.api.Test;
import org.yourdomain.tubes.EmailValidatorTube;
import org.yourdomain.tubes.EmailValidatorTube.ValidationResult;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTubeTest {
    @Test
    void shouldValidateCorrectEmailFormat() {
        EmailValidatorTube tube = new EmailValidatorTube();
        ValidationResult result = (ValidationResult) tube.process("user@example.com");
        
        assertTrue(result.isValid());
        assertEquals("user@example.com", result.getValue());
    }

    @Test
    void shouldRejectInvalidEmailFormat() {
        EmailValidatorTube tube = new EmailValidatorTube();
        ValidationResult result = (ValidationResult) tube.process("invalid-email");
        
        assertFalse(result.isValid());
        assertNull(result.getValue());
        assertTrue(result.getMessage().contains("Invalid email format"));
    }
}
```

## Understanding the Flow

### Processing Pathway

1. **Input Reception**: Data enters through `process()`
2. **Validation**: Input is checked against rules
3. **Result Generation**: `ValidationResult` is created
4. **Output Delivery**: Result flows to caller

### Adding Self-Awareness

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
```

## Connecting Tubes Together

### Creating a Complementary Tube

Formatter tube for display formatting:

```java
public class EmailFormatterTube implements Tube {
    // Standard tube initialization code omitted

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
}
```

### Connecting the Flow

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
```

## Monitoring and Adaptation

### Self-Monitoring

Health monitoring implementation:

```java
private TubeMonitor createMonitor() {
    return () -> {
        long totalProcessed = metrics.getCount("valid_email") +
                             metrics.getCount("invalid_email") +
                             metrics.getCount("null_input");

        double errorRate = totalProcessed > 0 ?
            (double)(metrics.getCount("invalid_email") + 
                    metrics.getCount("null_input")) / totalProcessed :
            0.0;

        String status = errorRate < 0.5 ? "HEALTHY" : "DEGRADED";

        return new HealthAssessment.Builder()
            .withTimestamp(Instant.now())
            .withStatus(status)
            .withMetric("total_processed", totalProcessed)
            .withMetric("error_rate", errorRate)
            .build();
    };
}
```

### Adaptive Behavior

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
```

## Building Your First Bundle

### Bundle Implementation

```java
public class EmailProcessingBundle implements Bundle {
    private final EmailValidatorTube validator;
    private final EmailFormatterTube formatter;
    private final EmailStorageTube storage;

    private BundleState designState;
    private DynamicState dynamicState;

    public EmailProcessingBundle() {
        // Initialize tubes
        this.validator = new EmailValidatorTube();
        this.formatter = new EmailFormatterTube();
        this.storage = new EmailStorageTube();

        // Set initial state
        this.designState = BundleState.FLOWING;
        this.dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .build();
    }

    public FormatResult processAndStoreEmail(String email) {
        if (designState != BundleState.FLOWING) {
            throw new IllegalStateException("Bundle not flowing: " + designState);
        }

        // Process through tube chain
        ValidationResult validationResult = 
            (ValidationResult) validator.process(email);

        if (!validationResult.isValid()) {
            return FormatResult.error(validationResult.getMessage());
        }

        FormatResult formatResult = 
            (FormatResult) formatter.process(validationResult);
        storage.process(formatResult);

        return formatResult;
    }

    public void monitorHealth() {
        boolean allHealthy = Stream.of(validator, formatter, storage)
            .map(tube -> tube.getMonitor().assessHealth())
            .allMatch(health -> "HEALTHY".equals(health.getStatus()));

        designState = allHealthy ? BundleState.FLOWING : BundleState.DEGRADED;
    }
}
```

## Common Patterns

### Validator-Transformer-Persister

A standard processing pipeline:

1. **Validator Tube**: Validates input requirements
2. **Transformer Tube**: Converts valid data
3. **Persister Tube**: Stores processed data

### Observer Tube

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
```

### Circuit Breaker

Failure protection pattern:

```java
public class CircuitBreakerTube implements Tube {
    private int consecutiveFailures = 0;
    private static final int THRESHOLD = 5;

    private TubeProcessor createProcessor() {
        return input -> {
            if (designState == TubeState.BLOCKED) {
                return ErrorResult.circuitOpen("Circuit open");
            }

            try {
                Object result = doActualProcessing(input);
                consecutiveFailures = 0; // Reset on success
                return result;
            } catch (Exception e) {
                consecutiveFailures++;

                if (consecutiveFailures >= THRESHOLD) {
                    designState = TubeState.BLOCKED;
                    logger.error("Circuit opened after {} failures", THRESHOLD);
                    scheduleReset(); // Attempt reset after timeout
                }

                return ErrorResult.processingFailure(e.getMessage());
            }
        };
    }
}
```

## Troubleshooting

### Communication Issues

- **Type compatibility**: Ensure output/input type matching between tubes
- **State verification**: Confirm all tubes are in `FLOWING` state
- **Error propagation**: Implement consistent error handling

### Adaptation Problems

- **Metrics collection**: Implement comprehensive metrics
- **Transition conditions**: Define clear state transition rules
- **Logging**: Add detailed state change logging

### Complexity Management

- **Start minimal**: Begin with 2-3 tubes
- **Focus on interfaces**: Define clear contracts between tubes
- **Use hierarchical organization**: Group related tubes into bundles

## Next Steps

1. **Expand functionality**: Add specialized tubes
2. **Implement state management**: Add sophisticated state transitions
3. **Organize as machines**: Group bundles into cohesive machines
4. **Add metrics**: Implement statistical monitoring 
5. **Test with BDD**: Create behavior specifications

### Related Resources

- [State Management](./StateManagement.md): Dual state system
- [Bundles and Machines](./BundlesAndMachines.md): Component organization
- [Testing Strategy](./Testing.md): BDD verification

[← Core Concepts](./CoreConcepts.md) | [State Management →](./StateManagement.md)
