# Composites and Machines


## Table of Contents
- [Introduction: From Cells to Organisms](#introduction-from-cells-to-organisms)
- [Composites: Collaborative Communities](#composites-collaborative-communities)
- [Machines: Orchestrated Systems](#machines-orchestrated-systems)
- [The Flow of Communication](#the-flow-of-communication)
- [State Propagation and Awareness](#state-propagation-and-awareness)
- [Design Patterns for Composition](#design-patterns-for-composition)
- [Implementation Guidelines](#implementation-guidelines)
- [Real-World Examples](#real-world-examples)

## Introduction: from Cells to Organisms

Nature teaches us that complexity emerges gradually through thoughtful organization. A single cell does remarkable work, but when cells collaborate in specialized groups—forming tissues, then organs, then full systems—they create capabilities that no single cell could manifest alone.

Samstraumr embodies this wisdom in software through its hierarchical structure:

- **Tubes**: Individual processing units with specific responsibilities
- **Composites**: Collaborative collections of tubes working toward related goals
- **Machines**: Orchestrated systems of composites addressing complex domains

This progressive composition allows systems to grow organically, maintaining clarity even as their capabilities expand. Just as you can understand your circulatory system without needing to track each individual blood cell, developers can work with composites and machines without managing every internal tube directly.

## Composites: Collaborative Communities

### What is a composite?

A composite is a collection of tubes united by a common purpose or domain responsibility. Like a heart composed of specialized chambers and valves, a composite brings together complementary tubes that collaborate to fulfill a shared mission.

### Key characteristics

1. **Shared Context**: Tubes within a composite often share resources, configuration, and state awareness

2. **Protected Communication**: Internal tube-to-tube communication happens through controlled, managed pathways

3. **Unified Interface**: A composite presents a coherent external interface while managing complexity internally

4. **Collective Identity**: Referenced through the `C<ID>` notation, each composite maintains its own integrity

### Composite composition

Create composites by grouping tubes that:

- Work within the same domain
- Process related data
- Collaborate frequently
- Share similar lifecycles

```java
// Example composite composition (simplified)
public class AuthenticationComposite implements Composite {
    private final Tube loginProcessor;
    private final Tube credentialValidator;
    private final Tube tokenGenerator;
    private final Tube accessManager;

    // Composite state management
    private CompositeState state;

    // Methods for configuration, initialization, etc.
}

### Machine state management

Machines extend the state concept further:

- **Machine Design State**: Reflects the system's overall operational status
    - `OPERATIONAL`: System functioning within expected parameters
    - `PARTIAL`: Some composites degraded but core functions operational
    - `REORGANIZING`: Major internal restructuring in progress
    - `IMPAIRED`: Critical capabilities unavailable

- **Machine Dynamic State**: Complex state tracking health, performance, and capabilities
    - System-wide health indicators
    - Cross-composite performance metrics
    - Capability availability matrix
    - Resource allocation tracking

## The Flow of Communication

In Samstraumr, communication follows natural pathways inspired by biological systems:

### Internal to composite communication

- **Direct Pathways**: Tubes within a composite communicate through established, direct channels
- **Mediated Exchange**: The composite may provide internal messaging services or shared memory spaces
- **State Observation**: Tubes can observe each other's states through the composite's coordination

### Composite to composite communication

- **Formal Interfaces**: Communication occurs through well-defined public interfaces
- **Machine Mediation**: The parent machine often regulates or monitors inter-composite exchanges
- **Protocol Enforcement**: Communication follows established protocols matching composite capabilities

### Cross-machine communication

- **System Boundaries**: Clear demarcation of where one machine ends and another begins
- **Contract-Based Exchange**: Formalized contracts define what can be exchanged and how
- **Identity-Based Routing**: Full identity notation (`M<ID>.C<ID>.T<ID>`) enables precise addressing

## State Propagation and Awareness

State changes ripple through the hierarchy in meaningful ways:

1. **Bottom-Up Propagation**: Significant tube state changes affect composite state, which may affect machine state

2. **Top-Down Influence**: Machine state transitions may trigger adaptation in contained composites and tubes

3. **Horizontal Awareness**: Peers at the same level may observe and respond to each other's state changes

4. **Threshold-Based Transitions**: Collective state changes occur when specific conditions or thresholds are met

```java
// Example of state propagation (simplified)
public void evaluateCompositeState() {
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
        setDesignState(CompositeState.CRITICAL);
        notifyMachine();
    } else if (flowingCount < minimumFlowingRequired) {
        setDesignState(CompositeState.DEGRADED);
    } else {
        setDesignState(CompositeState.FLOWING);
    }
}
M0 (Content Platform)
├── C0 (Content Creation Composite)
│   ├── T0 (Text Editor Tube)
│   ├── T1 (Media Manager Tube)
│   └── T2 (Version Control Tube)
├── C1 (Content Storage Composite)
│   ├── T0 (Database Interface Tube)
│   ├── T1 (Caching Tube)
│   └── T2 (Backup Tube)
└── C2 (Content Delivery Composite)
    ├── T0 (Template Processor Tube)
    ├── T1 (Page Assembler Tube)
    └── T2 (Delivery Optimizer Tube)
M0 (Payment Platform)
├── C0 (Customer Management Composite)
│   ├── T0 (Account Lookup Tube)
│   └── T1 (Profile Validation Tube)
├── C1 (Transaction Processing Composite)
│   ├── T0 (Payment Gateway Tube)
│   ├── T1 (Fraud Detection Tube)
│   └── T2 (Receipt Generator Tube)
└── C2 (Reporting Composite)
    ├── T0 (Transaction Logger Tube)
    ├── T1 (Analytics Tube)
