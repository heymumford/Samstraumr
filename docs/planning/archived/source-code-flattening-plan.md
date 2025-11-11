<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Source Code Flattening Plan

## Overview

This document outlines the specific implementation steps for Phase 5 of the directory flattening plan: Source Code Simplification. Based on our analysis, we've identified numerous small directories with fewer than 5 files that can be consolidated to reduce nesting while improving organization.

## Current Structure Analysis

Our source code contains many small directories with fewer than 5 files, leading to excessive nesting and fragmentation:

| Directory Size | Count | Examples |
|--------------|-------|----------|
| 1 file | 8 | `/org/s8r/adapter/in`, `/org/s8r/core`, `/org/s8r/tube/legacy` |
| 2 files | 16 | `/org/s8r/component/identity`, `/org/s8r/tube/exception` |
| 3 files | 7 | `/org/s8r/component/exception`, `/org/s8r/tube/composite` |
| 4 files | 8 | `/org/s8r/component/composite`, `/org/s8r/component/machine` |

This leads to several paths exceeding our maximum target depth of 9 levels, with the longest paths reaching 13 levels.

## Target Structure

The target structure consolidates small directories using file naming conventions:

### 1. component layer

```
org/s8r/component/
  Component.java                  # Core interface
  ComponentException.java         # Exception base class
  CompositeComponent.java         # From component/composite/
  ComponentIdentity.java          # From component/identity/
  ComponentLogger.java            # From component/logging/
  ComponentMachine.java           # From component/machine/
  package-info.java
```

### 2. domain layer

```
org/s8r/domain/
  component/
    Component.java                # Domain model
    ComponentFactory.java         # Factory interface
    CompositeComponent.java       # From component/composite/
    MachineComponent.java         # From component/machine/
    package-info.java
  event/
    ComponentEvent.java           # Event base class
    ComponentCreatedEvent.java    # Component creation event
    MachineEvent.java             # Machine event base class
    package-info.java
  identity/
    Identity.java                 # Identity base class
    ComponentIdentity.java        # Component identity
    package-info.java
  lifecycle/
    LifecycleState.java           # Lifecycle state enum
    package-info.java
  exception/
    DomainException.java          # Exception base class
    ComponentException.java       # Component exceptions
    package-info.java
```

### 3. core layer

```
org/s8r/core/
  CoreException.java              # Core exception base
  tube/
    Tube.java                     # Core tube interface
    TubeIdentity.java             # Tube identity
    TubeEnvironment.java          # Tube environment
    package-info.java
  exception/
    CoreException.java            # Core exception
    package-info.java
```

## Implementation Steps

### 1. component layer consolidation

1. Move `/org/s8r/component/composite/*.java` to `/org/s8r/component/`
   - `Composite.java` → `CompositeComponent.java`
   - `CompositeException.java` → `ComponentCompositeException.java`
   - `CompositeFactory.java` → `ComponentCompositeFactory.java`

2. Move `/org/s8r/component/identity/*.java` to `/org/s8r/component/`
   - `Identity.java` → `ComponentIdentity.java`

3. Move `/org/s8r/component/logging/*.java` to `/org/s8r/component/`
   - `Logger.java` → `ComponentLogger.java`

4. Move `/org/s8r/component/machine/*.java` to `/org/s8r/component/`
   - `Machine.java` → `ComponentMachine.java`
   - `MachineException.java` → `ComponentMachineException.java`
   - `MachineFactory.java` → `ComponentMachineFactory.java`

5. Move `/org/s8r/component/exception/*.java` to `/org/s8r/component/`
   - Exception classes should be consolidated

6. Update package declarations in all moved files

7. Update import statements in all dependent files

### 2. domain layer consolidation

1. Consolidate `/org/s8r/domain/component/composite/` into `/org/s8r/domain/component/`
   - Use appropriate naming prefixes
   - Update package declarations

2. Consolidate `/org/s8r/domain/component/pattern/` into `/org/s8r/domain/component/`
   - Use appropriate naming prefixes
   - Update package declarations

3. Consolidate `/org/s8r/domain/component/monitoring/` into `/org/s8r/domain/component/`
   - Use appropriate naming prefixes
   - Update package declarations

4. Consolidate `/org/s8r/domain/lifecycle/` into `/org/s8r/domain/`
   - Use appropriate naming prefixes
   - Update package declarations

### 3. tube layer consolidation

1. Consolidate `/org/s8r/tube/composite/` into `/org/s8r/tube/`
   - Use appropriate naming prefixes
   - Update package declarations

2. Consolidate `/org/s8r/tube/machine/` into `/org/s8r/tube/`
   - Use appropriate naming prefixes
   - Update package declarations

3. Consolidate `/org/s8r/tube/exception/` into `/org/s8r/tube/`
   - Use appropriate naming prefixes
   - Update package declarations

### 4. infrastructure layer consolidation

1. Consolidate `/org/s8r/infrastructure/config/` into `/org/s8r/infrastructure/`
   - Use appropriate naming prefixes
   - Update package declarations

2. Consolidate `/org/s8r/infrastructure/logging/` into `/org/s8r/infrastructure/`
   - Use appropriate naming prefixes
   - Update package declarations

3. Consolidate `/org/s8r/infrastructure/initialization/` into `/org/s8r/infrastructure/`
   - Use appropriate naming prefixes
   - Update package declarations

## Implementation Sequence

To minimize disruption and ensure incremental testing:

1. Begin with core & component layers:
   - These form the foundation of the system
   - Changes here impact many other parts

2. Then domain layer:
   - Most domain classes have fewer dependencies

3. Then tube layer:
   - Legacy code that will eventually be replaced

4. Finally infrastructure layer:
   - Dependent on other layers

## Technical Details

For each consolidation step:

1. Create new file with appropriate naming
2. Move and adapt content (update class name if needed)
3. Update package declaration
4. Update imports within the file
5. Run compilation check
6. Update imports in dependent files
7. Run tests for affected components

## Script Implementation

The implementation script will:

1. Use `grep` to identify dependencies between files
2. Use `sed` to update package declarations and imports
3. Use `git mv` to move files
4. Create appropriate README files in consolidated directories
5. Run compilation checks after each significant change

## Verification Plan

After each consolidation step:

1. Run `mvn compile` to verify compilation
2. Run unit tests for affected components
3. Run integration tests to verify larger system behavior
4. Update documentation as needed

## Backward Compatibility

During the transition period:

1. Add appropriate @Deprecated annotations on old paths
2. Document changes in release notes
3. Update import statements in dependent code

## Expected Outcomes

After implementation:

1. Overall directory depth reduced from max 13 to max 9 levels
2. Number of directories reduced by approximately 40%
3. Improved code discoverability through consistent naming
4. Simplified navigation and improved IDE performance

## Risks and Mitigation

| Risk | Mitigation |
|------|------------|
| Breaking changes to the API | Carefully preserve public API signatures |
| Compilation failures | Incremental testing approach |
| Missing import updates | Use IDE refactoring tools to catch missed imports |
