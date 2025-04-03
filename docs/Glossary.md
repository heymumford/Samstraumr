# Glossary

```
Version: 0.6.1
Last updated: April 03, 2025
Author: Eric C. Mumford (@heymumford)
Contributors: Samstraumr Core Team
```

This document contains definitions of terms used throughout the Samstraumr project.

## Terms

### Architecture Components

- **Tube**: A core component of the Samstraumr architecture that represents a self-contained processing unit with identity, state, and awareness
- **Bundle**: A collection of connected Tubes that form a processing pipeline or functional unit
- **Machine**: A runtime execution context for Bundles that orchestrates their interactions
- **Environment**: Configuration context that affects Tube behavior and provides operational parameters

### Testing Terminology

- **ATL (Above The Line)**: Critical tests that must pass with every build
- **BTL (Below The Line)**: Important but non-blocking tests that can run separately
- **TBD (Tube-Based Development)**: The development methodology used in Samstraumr

### State Terms

- **Design State**: The fundamental operational mode of a component (FLOWING, BLOCKED, ADAPTING, ERROR)
- **Dynamic State**: The moment-to-moment context and conditions captured as properties and metrics

---

[← FAQ](./FAQ.md) | [Testing →](./Testing.md)