<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Package Mapping: Migration to `org.s8r` standard

This document provides a detailed mapping for the package migration from the current mixed naming to a standardized `org.s8r` package hierarchy.

## Mapping Rules

1. `org.samstraumr` → `org.s8r`
2. `org.tube` → `org.s8r.tube.legacy`
3. Existing `org.s8r` packages remain unchanged

## Detailed Package Mappings

### Clean Architecture Layers

#### Domain Layer
- `org.samstraumr.domain` → `org.s8r.domain`
- `org.samstraumr.domain.component` → `org.s8r.domain.component`
- `org.samstraumr.domain.component.composite` → `org.s8r.domain.component.composite`
- `org.samstraumr.domain.component.monitoring` → `org.s8r.domain.component.monitoring`
- `org.samstraumr.domain.component.pattern` → `org.s8r.domain.component.pattern`
- `org.samstraumr.domain.event` → `org.s8r.domain.event`
- `org.samstraumr.domain.exception` → `org.s8r.domain.exception`
- `org.samstraumr.domain.identity` → `org.s8r.domain.identity`
- `org.samstraumr.domain.lifecycle` → `org.s8r.domain.lifecycle`
- `org.samstraumr.domain.machine` → `org.s8r.domain.machine`

#### Application Layer
- `org.samstraumr.application` → `org.s8r.application`
- `org.samstraumr.application.dto` → `org.s8r.application.dto`
- `org.samstraumr.application.port` → `org.s8r.application.port`
- `org.samstraumr.application.service` → `org.s8r.application.service`

#### Infrastructure Layer
- `org.samstraumr.infrastructure` → `org.s8r.infrastructure`
- `org.samstraumr.infrastructure.config` → `org.s8r.infrastructure.config`
- `org.samstraumr.infrastructure.event` → `org.s8r.infrastructure.event`
- `org.samstraumr.infrastructure.logging` → `org.s8r.infrastructure.logging`
- `org.samstraumr.infrastructure.persistence` → `org.s8r.infrastructure.persistence`

#### Adapter Layer
- `org.samstraumr.adapter` → `org.s8r.adapter`
- `org.samstraumr.adapter.in` → `org.s8r.adapter.in`
- `org.samstraumr.adapter.in.cli` → `org.s8r.adapter.in.cli`
- `org.samstraumr.adapter.out` → `org.s8r.adapter.out`

#### Main Application
- `org.samstraumr.app` → `org.s8r.app`
- `org.samstraumr.Samstraumr` → `org.s8r.Samstraumr`

### Legacy Tube System

#### Core Tube System
- `org.tube.core` → `org.s8r.tube.legacy.core`
- `org.tube.core.exception` → `org.s8r.tube.legacy.core.exception`
- `org.tube.composite` → `org.s8r.tube.legacy.composite`
- `org.tube.machine` → `org.s8r.tube.legacy.machine`

#### Samstraumr Tube Implementation
- `org.samstraumr.tube` → `org.s8r.tube`
- `org.samstraumr.tube.composite` → `org.s8r.tube.composite`
- `org.samstraumr.tube.exception` → `org.s8r.tube.exception`
- `org.samstraumr.tube.machine` → `org.s8r.tube.machine`
- `org.samstraumr.tube.reporting` → `org.s8r.tube.reporting`

## Class-level Special Cases

- `org.samstraumr.Samstraumr` → `org.s8r.Samstraumr` (Main class)
- `org.samstraumr.app.CliApplication` → `org.s8r.app.CliApplication` (CLI entrypoint)

## Test Packages

- `org.samstraumr.tube.test` → `org.s8r.tube.test`
- `org.samstraumr.tube.lifecycle` → `org.s8r.tube.lifecycle`
- `org.samstraumr.tube.orchestration` → `org.s8r.tube.orchestration`
- `org.tube.test` → `org.s8r.tube.legacy.test`

## Excluded Packages (Already Standardized)

The following packages already use the `org.s8r` naming and will not change:

- `org.s8r.component.*`
- `org.s8r.core.*`
- `org.s8r.test.*`
- `org.s8r.adapter.*`

## Implementation Notes

1. Package declarations and imports must be updated in all files
2. References to fully qualified class names in code must be updated
3. Maven build files need to be updated to reflect the new package structure
4. Test configurations need to be updated with the new package names
5. Documentation should reflect the new package structure
