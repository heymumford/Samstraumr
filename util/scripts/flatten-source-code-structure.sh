#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Flatten Source Code Structure - Phase 5 of Directory Flattening Plan
#
# This script implements Phase 5 of the directory flattening plan, which focuses
# on simplifying the source code directory structure by consolidating small 
# directories with fewer than 5 files and creating clearer naming conventions.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src/main/java/org/s8r"

echo "==================================================================="
echo "  Flattening Source Code Directory Structure (Phase 5)"
echo "==================================================================="
echo

# Analyze directories with fewer than 5 files
echo "Analyzing source code directories with fewer than 5 files..."
small_dirs=()
while IFS= read -r dir; do
  file_count=$(find "$dir" -maxdepth 1 -type f -name "*.java" | wc -l)
  if [ "$file_count" -lt 5 ] && [ "$file_count" -gt 0 ]; then
    small_dirs+=("$dir")
    echo "  Found small directory: $dir ($file_count files)"
  fi
done < <(find "$SRC_DIR" -type d -not -path "*/\.*")

echo
echo "Found ${#small_dirs[@]} directories with fewer than 5 files"
echo

# Create target directories where files will be consolidated
echo "Creating consolidated directory structure..."
mkdir -p "${SRC_DIR}/domain-new/component"
mkdir -p "${SRC_DIR}/domain-new/event"
mkdir -p "${SRC_DIR}/domain-new/identity"
mkdir -p "${SRC_DIR}/domain-new/lifecycle"
mkdir -p "${SRC_DIR}/application-new/service"
mkdir -p "${SRC_DIR}/infrastructure-new/persistence"
mkdir -p "${SRC_DIR}/infrastructure-new/event"
echo "✓ Created consolidated directories"

# Create README files for each new directory
echo "Creating README files for consolidated directories..."
cat > "${SRC_DIR}/domain-new/component/README.md" << 'EOL'
# Component Domain Classes

This directory contains all domain classes related to components, organized with clear naming conventions rather than nested directories.

## File Organization

Files use prefixes to indicate their category:
- `Component*.java` - Core component domain classes
- `Composite*.java` - Composite component related classes
- `Machine*.java` - Machine-related domain classes

## Related Directories

- `../event` - Domain events related to components
- `../identity` - Identity classes for components
- `../lifecycle` - Lifecycle management for components
EOL

cat > "${SRC_DIR}/domain-new/event/README.md" << 'EOL'
# Domain Events

This directory contains all domain event classes, organized with clear naming conventions rather than nested directories.

## File Organization

Files use prefixes to indicate their category:
- `Component*.java` - Component-related events
- `Machine*.java` - Machine-related events
- `System*.java` - System-level events

## Related Directories

- `../component` - Component domain classes
- `../../infrastructure/event` - Event handling infrastructure
EOL

cat > "${SRC_DIR}/domain-new/identity/README.md" << 'EOL'
# Identity Domain Classes

This directory contains all identity-related domain classes, organized with clear naming conventions rather than nested directories.

## File Organization

Files use prefixes to indicate their category:
- `Component*.java` - Component identity classes
- `Identity*.java` - Core identity abstractions and interfaces
- `Converter*.java` - Identity conversion utilities

## Related Directories

- `../component` - Component domain classes that use identities
EOL

echo "✓ Created README.md files for consolidated directories"

echo
echo "This script is a template for Phase 5 implementation."
echo "Due to the complexity of source code refactoring, this template must be"
echo "completed and expanded before executing the actual refactoring."
echo
echo "Key steps for completion:"
echo "1. Analyze each small directory to determine appropriate consolidation strategy"
echo "2. Create specific file naming conventions for each target directory"
echo "3. Implement file movement logic with proper package updates"
echo "4. Add import statement updates for affected files"
echo "5. Add verification steps to ensure compilation after consolidation"

echo
echo "==================================================================="
echo "  Source code flattening plan template created!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Complete this script with detailed implementation steps"
echo "2. Review the plan with team members"
echo "3. Implement changes incrementally, testing after each stage"
echo "4. Update import statements in dependent code"
echo

# Create directory flattening summary
echo "Creating summary file..."
cat > "${ROOT_DIR}/source-code-flattening-plan.md" << 'EOL'
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

This document outlines the specific implementation steps for Phase 5 of the directory flattening plan: Source Code Simplification.

## Current Structure Analysis

Our source code currently has several small directories with fewer than 5 files, leading to excessive nesting and fragmentation:

```
samstraumr-core/src/main/java/org/s8r/
  domain/
    component/
      composite/ (4 files)
        Composite.java
        CompositeException.java
        CompositeFactory.java
        package-info.java
      identity/ (2 files)
        Identity.java
        package-info.java
      lifecycle/ (1 file)
        LifecycleState.java
    event/
      small-subdirectories/...
    exception/
      small-subdirectories/...
```

## Target Structure

The target structure consolidates small directories using file naming conventions:

```
samstraumr-core/src/main/java/org/s8r/
  domain/
    component/ 
      Component.java
      ComponentException.java
      CompositeComponent.java
      CompositeComponentFactory.java
      package-info.java
    event/
      ComponentEvent.java
      MachineEvent.java
      ...
    identity/
      ComponentIdentity.java
      IdentityConverter.java
      ...
```

## Implementation Steps

### 1. Component Domain Consolidation

1. Move and rename files from `domain/component/composite/` to `domain/component/`:
   - `Composite.java` → `CompositeComponent.java`
   - `CompositeException.java` → `CompositeComponentException.java`
   - `CompositeFactory.java` → `CompositeComponentFactory.java`

2. Update package declarations in all moved files.

3. Update import statements in all dependent files.

### 2. Event Domain Consolidation

1. Consolidate event directories with appropriate prefixes:
   - `component/events/` → `event/Component*.java`
   - `machine/events/` → `event/Machine*.java`

2. Update package declarations and imports.

### 3. Identity Consolidation

1. Consolidate identity-related classes:
   - `domain/identity/*.java` → `domain/identity/`
   - `domain/component/identity/*.java` → `domain/identity/Component*.java`

2. Update package declarations and imports.

### 4. Infrastructure Consolidation

Apply similar patterns to infrastructure layer directories with fewer than 5 files.

## Implementation Sequence

To minimize disruption and ensure incremental testing:

1. Begin with leaf packages that have no dependencies
2. Move through the hierarchy, testing after each step
3. Handle cross-cutting concerns last

## Verification Plan

After each consolidation step:

1. Run `mvn compile` to verify compilation
2. Run unit tests for affected components
3. Run integration tests to verify larger system behavior
4. Update documentation as needed

## Backward Compatibility

During the transition period:

1. Consider using package forwarding where needed
2. Add appropriate @Deprecated annotations on old paths
3. Document changes in release notes

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
| Circular dependencies | Analyze and break any circular dependencies before consolidation |
EOL
echo "✓ Created source-code-flattening-plan.md"

echo "Done!"