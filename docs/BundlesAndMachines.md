# Bundles and Machines: Composing Intelligent Systems

```
Last updated: April 2, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

## Table of Contents
- [Introduction: From Cells to Organisms](#introduction-from-cells-to-organisms)
- [Bundles: Collaborative Communities](#bundles-collaborative-communities)
- [Machines: Orchestrated Systems](#machines-orchestrated-systems)
- [The Flow of Communication](#the-flow-of-communication)
- [State Propagation and Awareness](#state-propagation-and-awareness)
- [Design Patterns for Composition](#design-patterns-for-composition)
- [Implementation Guidelines](#implementation-guidelines)
- [Real-World Examples](#real-world-examples)

## Introduction: From Cells to Organisms

Nature teaches us that complexity emerges gradually through thoughtful organization. A single cell does remarkable work, but when cells collaborate in specialized groups—forming tissues, then organs, then full systems—they create capabilities that no single cell could manifest alone.

Samstraumr embodies this wisdom in software through its hierarchical structure:

- **Tubes**: Individual processing units with specific responsibilities
- **Bundles**: Collaborative collections of tubes working toward related goals
- **Machines**: Orchestrated systems of bundles addressing complex domains

This progressive composition allows systems to grow organically, maintaining clarity even as their capabilities expand. Just as you can understand your circulatory system without needing to track each individual blood cell, developers can work with bundles and machines without managing every internal tube directly.

## Bundles: Collaborative Communities

### What Is a Bundle?

A bundle is a collection of tubes united by a common purpose or domain responsibility. Like a heart composed of specialized chambers and valves, a bundle brings together complementary tubes that collaborate to fulfill a shared mission.

### Key Characteristics

1. **Shared Context**: Tubes within a bundle often share resources, configuration, and state awareness

2. **Protected Communication**: Internal tube-to-tube communication happens through controlled, managed pathways

3. **Unified Interface**: A bundle presents a coherent external interface while managing complexity internally

4. **Collective Identity**: Referenced through the `B<ID>` notation, each bundle maintains its own integrity

### Bundle Composition

Create bundles by grouping tubes that:

- Work within the same domain
- Process related data
- Collaborate frequently
- Share similar lifecycles

```java
// Example bundle composition (simplified)
public class AuthenticationBundle implements Bundle {
    private final Tube loginProcessor;
    private final Tube credentialValidator;
    private final Tube tokenGenerator;
    private final Tube accessManager;

    // Bundle state management
    private BundleState state;

    // Methods for configuration, initialization, etc.
}
```

### Bundle State Management

Like tubes, bundles maintain both Design State and Dynamic State, but at a higher level:

- **Bundle Design State**: Reflects the collective state of contained tubes
    - `FLOWING`: All critical tubes are functioning as expected
    - `DEGRADED`: Some non-critical tubes experiencing issues
    - `ADAPTING`: The bundle is reconfiguring internal pathways
    - `CRITICAL`: Multiple tubes in error or critical tubes failing

- **Bundle Dynamic State**: Captures the operational context and capabilities
    - May include metrics across tubes
    - Tracks internal resource allocation
    - Records communication patterns
    - Monitors overall health

## Machines: Orchestrated Systems

### What Is a Machine?

A machine is a system of coordinated bundles addressing a complex domain. Like an entire organism with multiple organ systems, a machine integrates specialized bundles to create a complete, cohesive solution.

### Key Characteristics

1. **System-Level Awareness**: Machines observe and direct the interplay between bundles

2. **Resource Governance**: Allocation of resources across bundles based on priorities and needs

3. **Flow Orchestration**: Coordination of complex workflows spanning multiple bundles

4. **Adaptive Configuration**: Runtime adjustment of bundle parameters based on system objectives

### Machine Composition

Build machines by integrating bundles that:

- Support a cohesive user or system journey
- Represent a complete business capability
- Must be managed as a unified whole
- Share operational concerns

```java
// Example machine composition (simplified)
public class CustomerManagementMachine implements Machine {
    private final Bundle registrationBundle;
    private final Bundle profileBundle;
    private final Bundle analyticsBundle;
    private final Bundle communicationBundle;

    // Machine state management
    private MachineState state;

    // Cross-bundle orchestration methods
}
```

### Machine State Management

Machines extend the state concept further:

- **Machine Design State**: Reflects the system's overall operational status
    - `OPERATIONAL`: System functioning within expected parameters
    - `PARTIAL`: Some bundles degraded but core functions operational
    - `REORGANIZING`: Major internal restructuring in progress
    - `IMPAIRED`: Critical capabilities unavailable

- **Machine Dynamic State**: Complex state tracking health, performance, and capabilities
    - System-wide health indicators
    - Cross-bundle performance metrics
    - Capability availability matrix
    - Resource allocation tracking

## The Flow of Communication

In Samstraumr, communication follows natural pathways inspired by biological systems:

### Internal to Bundle Communication

- **Direct Pathways**: Tubes within a bundle communicate through established, direct channels
- **Mediated Exchange**: The bundle may provide internal messaging services or shared memory spaces
- **State Observation**: Tubes can observe each other's states through the bundle's coordination

### Bundle to Bundle Communication

- **Formal Interfaces**: Communication occurs through well-defined public interfaces
- **Machine Mediation**: The parent machine often regulates or monitors inter-bundle exchanges
- **Protocol Enforcement**: Communication follows established protocols matching bundle capabilities

### Cross-Machine Communication

- **System Boundaries**: Clear demarcation of where one machine ends and another begins
- **Contract-Based Exchange**: Formalized contracts define what can be exchanged and how
- **Identity-Based Routing**: Full identity notation (`M<ID>.B<ID>.T<ID>`) enables precise addressing

## State Propagation and Awareness

State changes ripple through the hierarchy in meaningful ways:

1. **Bottom-Up Propagation**: Significant tube state changes affect bundle state, which may affect machine state

2. **Top-Down Influence**: Machine state transitions may trigger adaptation in contained bundles and tubes

3. **Horizontal Awareness**: Peers at the same level may observe and respond to each other's state changes

4. **Threshold-Based Transitions**: Collective state changes occur when specific conditions or thresholds are met

```java
// Example of state propagation (simplified)
public void evaluateBundleState() {
    int flowingCount = 0;
    int errorCount = 0;

    for (Tube tube : tubes) {
        if (tube.getDesignState() == TubeState.FLOWING) {
            flowingCount++;
        } else if (tube.getDesignState() == TubeState.ERROR) {
            errorCount++;
        }
    }

    if (errorCount > criticalThreshold) {
        setDesignState(BundleState.CRITICAL);
        notifyMachine();
    } else if (flowingCount < minimumFlowingRequired) {
        setDesignState(BundleState.DEGRADED);
    } else {
        setDesignState(BundleState.FLOWING);
    }
}
```

## Design Patterns for Composition

Several patterns emerge when composing tubes into larger structures:

### The Guardian Pattern

A specialized tube monitors the health of other tubes within a bundle, acting as an immune system:

- Detects anomalies in tube behavior
- Initiates recovery procedures
- Isolates problematic tubes
- Reports persistent issues to the bundle level

### The Tributary Pattern

Multiple tubes feed their outputs into a collector tube that aggregates and processes combined data:

- Combines related data streams
- Normalizes varied inputs
- Provides a unified output
- Manages backpressure

### The Circuit Breaker Pattern

Monitors flow between components and temporarily halts processing when error thresholds are exceeded:

- Prevents cascading failures
- Allows for graceful degradation
- Automatically attempts recovery
- Protects downstream components

### The Adapting Membrane Pattern

Specialized boundary tubes transform data flowing between bundles or machines:

- Translates between different data formats
- Filters sensitive information
- Enforces security policies
- Logs cross-boundary traffic

## Implementation Guidelines

When implementing bundles and machines, consider these principles:

1. **Start Small**: Begin with coherent bundles before attempting machine composition

2. **Clear Boundaries**: Define precise responsibilities and interfaces for each level

3. **Consistent Identity**: Follow the identity notation system rigorously

4. **Thoughtful State Design**: Create meaningful state definitions that reflect component health

5. **Measured Growth**: Add new tubes and bundles incrementally, validating at each step

6. **Self-Documentation**: Name components to reflect their purpose within the system

7. **Test at Boundaries**: Create tests that verify expected behavior at each interface

## Real-World Examples

### Content Management System

```
M0 (Content Platform)
├── B0 (Content Creation Bundle)
│   ├── T0 (Text Editor Tube)
│   ├── T1 (Media Manager Tube)
│   └── T2 (Version Control Tube)
├── B1 (Content Storage Bundle)
│   ├── T0 (Database Interface Tube)
│   ├── T1 (Caching Tube)
│   └── T2 (Backup Tube)
└── B2 (Content Delivery Bundle)
    ├── T0 (Template Processor Tube)
    ├── T1 (Page Assembler Tube)
    └── T2 (Delivery Optimizer Tube)
```

### Payment Processing System

```
M0 (Payment Platform)
├── B0 (Customer Management Bundle)
│   ├── T0 (Account Lookup Tube)
│   └── T1 (Profile Validation Tube)
├── B1 (Transaction Processing Bundle)
│   ├── T0 (Payment Gateway Tube)
│   ├── T1 (Fraud Detection Tube)
│   └── T2 (Receipt Generator Tube)
└── B2 (Reporting Bundle)
    ├── T0 (Transaction Logger Tube)
    ├── T1 (Analytics Tube)
    └── T2 (Report Generator Tube)
```

---

*In Samstraumr, we don't just build applications—we grow ecosystems that breathe, adapt, and evolve with purpose.*

[← Return to Core Concepts](./CoreConcepts.md) | [Explore State Management →](./StateManagement.md)
