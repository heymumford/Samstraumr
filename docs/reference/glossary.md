<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
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
