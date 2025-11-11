<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Unified Lifecycle Tests

## Purpose

This directory contains the consolidated lifecycle test features for both the Tube and Component models. These tests verify the lifecycle state transitions, initialization, and identity establishment processes.

## Test Organization

The tests are organized by lifecycle phase, with each phase having its own feature file:

- **Conception Tests**: Initial identity establishment and creation
- **Embryonic Tests**: Early structure and basic validation
- **Infancy Tests**: Initial capability and connection establishment
- **Childhood Tests**: Functional development and expanded capabilities
- **Full Lifecycle Tests**: End-to-end lifecycle transitions

## File Naming Conventions

Files follow the pattern: `lifecycle-[phase]-test.feature`

## Related Files

- Java step definitions: `org.s8r.test.tube.lifecycle` package
- Implementation classes: `org.s8r.component.core` package
