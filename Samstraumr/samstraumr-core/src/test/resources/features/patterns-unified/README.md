<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Unified Design Pattern Tests

## Purpose

This directory contains the consolidated design pattern test features for components and composites. These tests verify the implementation of common design patterns like Observer, Transformer, and Filter in a composite component context.

## Test Organization

The tests are organized by design pattern:

- **Observer Pattern**: Components that monitor state changes in other components
- **Transformer Pattern**: Components that transform data from one format to another
- **Filter Pattern**: Components that validate and filter incoming data
- **Circuit Breaker Pattern**: Components that implement fault tolerance patterns

## File Naming Conventions

Files follow the pattern: `pattern-[pattern-name]-test.feature`

## Related Files

- Java step definitions: `org.s8r.test.tube` and `org.s8r.test.component` packages
- Implementation classes: `org.s8r.component.composite` package
