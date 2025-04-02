# Samstraumr Framework

> *Software that breathes, flows, and grows like a living stream*

```
Last updated: April 2, 2025
Maintainer: Eric C. Mumford (@Vorthruna)
Version: 1.2.0
License: Mozilla Public License 2.0
```

## Table of Contents

- [Overview](#overview)
- [Core Concepts](#core-concepts)
    - [Tubes as Building Blocks](#tubes-as-building-blocks)
    - [Identity Notation](#identity-notation)
    - [State Management](#state-management)
- [Benefits](#benefits)
- [Getting Started](#getting-started)
- [Advanced Topics](#advanced-topics)
- [Community](#community)
- [Resources](#resources)
- [Connect](#connect)

## Overview

Imagine software that adapts to change as gracefully as a river adjusts its course around new stones. This is the vision behind Samstraumr (from Old Norse, meaning "unified flow").

Samstraumr is a design framework that reimagines how we build software by drawing inspiration from nature's elegant solutions. Just as rivers find the most efficient path downhill, Samstraumr helps developers create systems that naturally find balance and harmony through **Tube-Based Development (TBD)**.

At its heart, Samstraumr envisions software components as interconnected "tubes" – mindful pathways that guide data and functionality through your system with purpose and awareness.

[→ Explore our systems theory foundation](./docs/SystemsTheoryFoundation.md)

## Core Concepts

### Tubes as Building Blocks

In nature, specialized structures channel resources where they're needed – from rivers carving landscapes to veins delivering nutrients through your body. Samstraumr's **tubes** bring this wisdom to code.

A tube is a self-aware component with clear responsibilities, mindful of what flows in and what flows out. Like organs in a body, tubes perform specific functions while contributing to a greater purpose:

- **Atomic tubes** – Individual specialists, like a heart cell with one clear job
- **Bundles** – Collaborative teams, like a complete heart with coordinated chambers
- **Machines** – Orchestrated systems, like a full circulatory system working in concert

This nested approach mirrors nature's elegant organization – from cells to organisms to ecosystems – allowing complex symphonies to emerge from simple melodies.

[→ Discover detailed core concepts](./docs/CoreConcepts.md)  
[→ Learn about tube design patterns](./docs/TubePatterns.md)  
[→ Understand bundles and machines](./docs/BundlesAndMachines.md)

### Identity Notation

Even in complex ecosystems, every creature has its place and name. Samstraumr brings this clarity to software with a naming system as elegant as it is practical:

- `T<ID>` – A single tube, like referring to "the oak tree" (`T7`)
- `B<ID>.T<ID>` – A tube within its bundle, like "the oak in the north grove" (`B3.T2`)
- `M<ID>.B<ID>.T<ID>` – A fully qualified address, like "the oak in the north grove of Sherwood Forest" (`M0.B1.T4`)

This whispered language allows developers to reference any part of even the most intricate systems with brevity and precision – a gift to future caretakers of your code.

### State Management

Just as a river might be flowing, frozen, or flooding – each with different implications – Samstraumr components understand themselves through two complementary lenses:

1. **Design State** – Fundamental conditions that change rarely but significantly:
    - `FLOWING` – Healthy operation, like a river in its banks
    - `BLOCKED` – Temporarily unable to process, like a river meeting a new dam
    - `ADAPTING` – Reconfiguring to new conditions, like a river finding a path around obstacles
    - `ERROR` – Experiencing distress, like a river during toxic contamination

2. **Dynamic State** – Moment-to-moment awareness that shifts fluidly with changing conditions, like the ripples, eddies, and currents that animate a flowing stream

This dual awareness allows components to maintain their essence while dancing with the ever-changing now.

[→ Explore state harmony in depth](./docs/StateManagement.md)

## Benefits

When you embrace Samstraumr's flowing wisdom:

- **Unexpected intelligence emerges** – Components become more than the sum of their parts
- **Systems bend without breaking** – Resilience becomes woven into your software's fabric
- **Growth feels organic, not painful** – Evolution without revolution
- **Technical debt becomes rare** – Clean boundaries prevent tangled roots
- **Teams work in harmony** – Clear responsibilities foster collaboration without conflict
- **Solutions scale naturally** – From garden pond to mighty ocean

## Getting Started

Begin your journey with Samstraumr through these gentle steps:

1. **Start with a single stream** – Create one thoughtful tube with clear purpose
2. **Map the natural flows** – Identify how information wants to move through your system
3. **Listen and respond** – Enable your tubes to hear and adapt to what's happening around them
4. **Let complexity emerge gradually** – Nurture growth through thoughtful composition

[→ Follow our step-by-step guide](./docs/GettingStarted.md)

## Advanced Topics

For those ready to dive deeper:

- **Migrating existing systems** – Bringing Samstraumr wisdom to established codebases
- **Testing approaches** – Verification through conversation
- **Design patterns** – Recurring solutions to common challenges

[→ Discover migration strategies](./docs/Migration.md)  
[→ Explore testing approaches](./docs/Testing.md)

## Community

Samstraumr thrives within a garden of diverse practitioners tending their own implementations while sharing discoveries. Join our growing community:

- **Share your creations** through pull requests
- **Plant new ideas** via thoughtful issues
- **Join conversations** about patterns observed in the wild
- **Tend the documentation** to help future explorers

## Resources

- [Glossary of Terms](./docs/Glossary.md)
- [Frequently Asked Questions](./docs/FAQ.md)
- [Testing Strategy](./docs/proposals/SamstraumrTestingStrategy.md)
- [LLM Context Composite Tube Proposal](./docs/proposals/LLMContextCompositeTubeProposal.md)

## Connect

- **Author:** [Eric C. Mumford](mailto:heymumford@samstraumr.org)
- **GitHub:** [github.com/heymumford](https://github.com/heymumford)
- **LinkedIn:** [linkedin.com/in/eric-mumford](https://www.linkedin.com/in/eric-mumford/)

---

*Samstraumr: Where code flows like water, finding harmony in change*