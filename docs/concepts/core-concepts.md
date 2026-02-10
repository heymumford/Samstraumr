<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->




# Core Concepts

## Table of Contents

- [Introduction: A New Way of Seeing Software](#introduction-a-new-way-of-seeing-software)
- [The Component: Fundamental Building Block](#the-component-fundamental-building-block)
- [Flow: The Movement of Information and Control](#flow-the-movement-of-information-and-control)
- [Identity: Clear Names in a Complex World](#identity-clear-names-in-a-complex-world)
- [State: Unified Lifecycle Management](#state-unified-lifecycle-management)
- [Self-Awareness: Software That Knows Itself](#self-awareness-software-that-knows-itself)
- [Core Principles in Practice](#core-principles-in-practice)
- [The Journey Ahead](#the-journey-ahead)
- [Related Documentation](#related-documentation)

## Introduction: a New Way of Seeing Software

For too long, we've built software as if it were a static machine—something to be assembled once and maintained with occasional repairs. But what if we could create systems that behave more like living organisms—adapting, evolving, and responding intelligently to their environment?

S8r represents a fundamental shift in how we conceive of and design software. Rather than thinking in terms of classes, functions, and data structures alone, we invite you to envision systems composed of mindful, flowing pathways—**components** that carry functionality, data, and awareness throughout your application.

This document introduces the core concepts that make S8r unique and powerful. Each concept builds upon the others to create a cohesive approach to software that embraces complexity without becoming complicated.

## The Component: Fundamental Building Block

At the heart of S8r lies the component—a self-contained, purposeful unit that processes inputs and produces outputs while maintaining awareness of its own state and context.

### Anatomy of a component

A component combines several essential elements:

1. **Processing Logic**: The core functionality that transforms inputs into outputs
2. **Monitoring Capabilities**: The ability to observe and evaluate its own operation
3. **Resource Management**: Control over the resources needed for operation
4. **State Management**: Unified tracking of lifecycle and operational states
5. **Identity**: A clear, unique identifier within the larger system

```java
// Example of a basic component structure (simplified)
public class DataValidatorComponent implements Component {
    private final Identity identity;
    private State state;
    private final Environment environment;
    private final Logger logger;
    private final Map<String, Object> properties;
    
    // Component implementation methods
}
```

The identity serves as both documentation and a formal verification mechanism, allowing components to authenticate themselves and others within the system.

## State: Unified Lifecycle Management

S8r components maintain a unified state system that captures both the lifecycle phase and operational status of a component.

### Component states

The State enum represents the fundamental operational and developmental phases of a component:

- **CONCEPTION**: Initial creation phase
- **INITIALIZING**: Setting up internal structures
- **CONFIGURING**: Establishing boundaries and configuration
- **SPECIALIZING**: Determining core functionality
- **DEVELOPING_FEATURES**: Building specific capabilities
- **READY**: Configured and ready for operation
- **ACTIVE**: Fully operational and processing
- **DEGRADED**: Operating with reduced capabilities
- **TERMINATING**: In process of shutting down
- **TERMINATED**: No longer operating

### State transitions

State changes follow a biological-inspired lifecycle:

1. **Creation Phase**: CONCEPTION → INITIALIZING → CONFIGURING
2. **Development Phase**: SPECIALIZING → DEVELOPING_FEATURES → READY
3. **Operational Phase**: READY → ACTIVE ↔ DEGRADED
4. **Termination Phase**: TERMINATING → TERMINATED

### State propagation and recovery

S8r implements sophisticated patterns for managing state across components:

1. **State Propagation**: The way state changes in one component influence others
2. **Recovery Paths**: Predetermined routes back to healthy states after disruptions
3. **Parent-Child Relationships**: How state flows through component hierarchies
4. **Environment Awareness**: How environmental conditions affect state transitions

## Self-Awareness: Software That Knows Itself

Perhaps the most revolutionary aspect of S8r is its emphasis on self-awareness—the ability of software components to observe, evaluate, and adjust their own operation.

### Dimensions of awareness

Components maintain awareness across several dimensions:

1. **Health Awareness**: Understanding of operational wellness
2. **Resource Awareness**: Knowledge of resource consumption and needs
3. **Context Awareness**: Recognition of position within larger flows
4. **History Awareness**: Memory of past states and transitions
5. **Capability Awareness**: Understanding of current processing abilities

### Awareness in action

This self-awareness manifests in practical behaviors:

- **Self-Healing**: Detecting and addressing issues without external intervention
- **Adaptive Scaling**: Adjusting resource usage based on demand
- **Predictive Preparation**: Anticipating needs based on observed patterns
- **Graceful Degradation**: Maintaining core functionality when resources are constrained
- **Cooperative Load Balancing**: Working with other components to optimize system-wide performance

```java
// Example of awareness in action (simplified)
public void assessHealth() {
    Map<String, Object> stats = gatherVitalStats();

    if ((double)stats.get("errorRate") > errorThreshold) {
        // Self-healing attempt
        performRecoveryProcedure();

        if ((double)gatherVitalStats().get("errorRate") > criticalThreshold) {
            // If recovery fails, change state
            setState(State.DEGRADED);
            notifyParent();
        }
    }

    // Update property with latest metrics
    setProperty("healthStats", stats);
    setProperty("lastHealthCheck", Instant.now());
}
```

## Related Documentation

- [Main Documentation](../../readme.md) - Return to main Samstraumr documentation
- [Origins and Vision](./origins-and-vision.md) - The 30-year journey behind Samstraumr
- [Systems Theory Foundation](./systems-theory-foundation.md) - The natural foundations
- [Architecture Overview](../architecture/readme.md) - Technical architecture details
- [Getting Started Guide](../guides/getting-started.md) - Practical implementation of concepts
- [State Management](./state-management.md) - Detailed view of component state
- [Identity Addressing](./identity-addressing.md) - Component identity principles
