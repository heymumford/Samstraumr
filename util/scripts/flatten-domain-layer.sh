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

# Flatten Domain Layer - Part of Phase 5 (Source Code Simplification)
#
# This script implements the second part of Phase 5, focusing on consolidating
# the small subdirectories in the domain layer to reduce nesting while
# maintaining clear organization through file naming conventions.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src/main/java/org/s8r"
DOMAIN_DIR="${SRC_DIR}/domain"
DOMAIN_COMP_DIR="${DOMAIN_DIR}/component"
DOMAIN_NEW_DIR="${DOMAIN_DIR}-new"
DOMAIN_COMP_NEW_DIR="${DOMAIN_NEW_DIR}/component"

echo "==================================================================="
echo "  Flattening Domain Layer Structure"
echo "==================================================================="
echo

# Create the new directory structure
echo "Creating new domain directory structure..."
mkdir -p "${DOMAIN_COMP_NEW_DIR}"
mkdir -p "${DOMAIN_NEW_DIR}/identity"
mkdir -p "${DOMAIN_NEW_DIR}/event"
mkdir -p "${DOMAIN_NEW_DIR}/exception"
echo "✓ Created new domain directories"

# Create README for the consolidated domain directory
cat > "${DOMAIN_NEW_DIR}/README.md" << 'EOL'
# Domain Layer

This directory contains the domain model of the S8r framework, following Clean Architecture principles. The domain layer contains business logic and rules independent of other layers.

## File Organization

Files use prefixes to indicate their category and are organized into logical subdirectories:

### component/
- `Component*.java` - Core component domain entities
- `Composite*.java` - Composite component domain entities
- `Pattern*.java` - Pattern-related domain entities
- `Monitoring*.java` - Monitoring-related domain entities

### event/
- `Component*.java` - Component-related events
- `Machine*.java` - Machine-related events
- `DomainEvent.java` - Base event class

### identity/
- `Component*.java` - Component identity entities
- `Identity*.java` - Core identity abstractions

### exception/
- `Component*.java` - Component-related exceptions
- `Domain*.java` - General domain exceptions

## Design Patterns

The domain layer implements several domain patterns:
- Entity-Value Pattern: Separates identity-carrying entities from value objects
- Domain Events: Uses events to communicate between components
- Repository Pattern: Abstracts data access through repository interfaces

## Related Directories

- `../application` - Application services that use domain entities
- `../infrastructure` - Infrastructure implementations of domain interfaces
EOL
echo "✓ Created README.md for domain directory"

# Create README for the consolidated domain/component directory
cat > "${DOMAIN_COMP_NEW_DIR}/README.md" << 'EOL'
# Domain Component Layer

This directory contains the component-related domain entities that form the core business model of the S8r framework.

## File Organization

Files use prefixes to indicate their category:
- `Component*.java` - Core component domain entities
- `Composite*.java` - Composite component domain entities
- `Pattern*.java` - Pattern-related domain entities
- `Monitoring*.java` - Monitoring-related domain entities

## Domain Concepts

The component domain model represents:
- Components: Base building blocks of the system
- Composites: Components that contain other components
- Patterns: Reusable component interaction patterns
- Monitoring: Component health and metrics tracking

## Related Directories

- `../event` - Domain events related to components
- `../identity` - Identity model for components
- `../exception` - Domain exceptions related to components
EOL
echo "✓ Created README.md for domain/component directory"

# Copy package-info.java files
echo "Copying package-info.java files..."
cp "${DOMAIN_DIR}/package-info.java" "${DOMAIN_NEW_DIR}/" 2>/dev/null || true
cp "${DOMAIN_COMP_DIR}/package-info.java" "${DOMAIN_COMP_NEW_DIR}/" 2>/dev/null || true
cp "${DOMAIN_DIR}/event/package-info.java" "${DOMAIN_NEW_DIR}/event/" 2>/dev/null || true
cp "${DOMAIN_DIR}/identity/package-info.java" "${DOMAIN_NEW_DIR}/identity/" 2>/dev/null || true
cp "${DOMAIN_DIR}/exception/package-info.java" "${DOMAIN_NEW_DIR}/exception/" 2>/dev/null || true
echo "✓ Copied package-info.java files"

# Process main component directory
echo "Processing domain/component files..."
for file in "${DOMAIN_COMP_DIR}"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      cp "$file" "${DOMAIN_COMP_NEW_DIR}/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

# Process component/composite subdirectory
echo "Processing domain/component/composite subdirectory..."
for file in "${DOMAIN_COMP_DIR}/composite"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      # Rename files to have a Composite prefix
      newname="Composite${filename}"
      cp "$file" "${DOMAIN_COMP_NEW_DIR}/${newname}"
      echo "  Copied $filename → ${newname}"
    fi
  fi
done

# Process component/pattern subdirectory
echo "Processing domain/component/pattern subdirectory..."
for file in "${DOMAIN_COMP_DIR}/pattern"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      # Rename files to have a Pattern prefix
      newname="Pattern${filename}"
      cp "$file" "${DOMAIN_COMP_NEW_DIR}/${newname}"
      echo "  Copied $filename → ${newname}"
    fi
  fi
done

# Process component/monitoring subdirectory
echo "Processing domain/component/monitoring subdirectory..."
for file in "${DOMAIN_COMP_DIR}/monitoring"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      # Rename files to have a Monitoring prefix
      newname="Monitoring${filename}"
      cp "$file" "${DOMAIN_COMP_NEW_DIR}/${newname}"
      echo "  Copied $filename → ${newname}"
    fi
  fi
done

# Process event directory
echo "Processing domain/event directory..."
for file in "${DOMAIN_DIR}/event"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      cp "$file" "${DOMAIN_NEW_DIR}/event/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

# Process identity directory
echo "Processing domain/identity directory..."
for file in "${DOMAIN_DIR}/identity"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      cp "$file" "${DOMAIN_NEW_DIR}/identity/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

# Process exception directory
echo "Processing domain/exception directory..."
for file in "${DOMAIN_DIR}/exception"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      cp "$file" "${DOMAIN_NEW_DIR}/exception/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

# Process domain/lifecycle directory
echo "Processing domain/lifecycle directory..."
for file in "${DOMAIN_DIR}/lifecycle"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      # Move lifecycle files to domain root with Lifecycle prefix
      newname="Lifecycle${filename}"
      cp "$file" "${DOMAIN_NEW_DIR}/${newname}"
      echo "  Copied $filename → ${newname}"
    fi
  fi
done

echo "✓ Copied all domain files to new structure"
echo

# Update package declarations in the new files
echo "Updating package declarations..."

# Update component/composite files
for file in "${DOMAIN_COMP_NEW_DIR}"/Composite*.java; do
  if [ -f "$file" ]; then
    sed -i 's/package org.s8r.domain.component.composite;/package org.s8r.domain.component;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

# Update component/pattern files
for file in "${DOMAIN_COMP_NEW_DIR}"/Pattern*.java; do
  if [ -f "$file" ]; then
    sed -i 's/package org.s8r.domain.component.pattern;/package org.s8r.domain.component;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

# Update component/monitoring files
for file in "${DOMAIN_COMP_NEW_DIR}"/Monitoring*.java; do
  if [ -f "$file" ]; then
    sed -i 's/package org.s8r.domain.component.monitoring;/package org.s8r.domain.component;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

# Update lifecycle files
for file in "${DOMAIN_NEW_DIR}"/Lifecycle*.java; do
  if [ -f "$file" ]; then
    sed -i 's/package org.s8r.domain.lifecycle;/package org.s8r.domain;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

echo "✓ Updated package declarations"

echo
echo "==================================================================="
echo "  Domain Layer structure flattened!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Review the new structure in ${DOMAIN_NEW_DIR}"
echo "2. Run compilation check to verify changes"
echo "3. Update imports in dependent files"
echo "4. Replace the original directory when verified"
echo

echo "To verify compilation, run:"
echo "  cd ${ROOT_DIR}/Samstraumr && mvn compile -Dmaven.test.skip=true"
echo
echo "To update imports in dependent files, you can use:"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.domain.component.composite\" | xargs sed -i 's/org.s8r.domain.component.composite/org.s8r.domain.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.domain.component.pattern\" | xargs sed -i 's/org.s8r.domain.component.pattern/org.s8r.domain.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.domain.component.monitoring\" | xargs sed -i 's/org.s8r.domain.component.monitoring/org.s8r.domain.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.domain.lifecycle\" | xargs sed -i 's/org.s8r.domain.lifecycle/org.s8r.domain/g'"
echo

echo "To apply the changes when verified:"
echo "  mv ${DOMAIN_DIR} ${DOMAIN_DIR}-old"
echo "  mv ${DOMAIN_NEW_DIR} ${DOMAIN_DIR}"
echo "  rm -rf ${DOMAIN_DIR}-old"
echo

echo "Done!"