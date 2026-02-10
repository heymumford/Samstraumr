<!--
Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>

Licensed under Mozilla Public License 2.0.
See LICENSE file for details.
-->




# Glossary

This document contains definitions of terms used throughout the Samstraumr project.

## Terms

### Architecture components

- **Tube**: A core component of the Samstraumr architecture that represents a self-contained processing unit with identity, state, and awareness
- **Bundle**: A collection of connected Tubes that form a processing pipeline or functional unit
- **Machine**: A runtime execution context for Bundles that orchestrates their interactions
- **Environment**: Configuration context that affects Tube behavior and provides operational parameters

### Testing terminology

- **ATL (Above The Line)**: Critical tests that must pass with every build
- **BTL (Below The Line)**: Important but non-blocking tests that can run separately
- **TBD (Tube-Based Development)**: The development methodology used in Samstraumr

### State terms

- **Design State**: The fundamental operational mode of a component (FLOWING, BLOCKED, ADAPTING, ERROR)
- **Dynamic State**: The moment-to-moment context and conditions captured as properties and metrics

---
