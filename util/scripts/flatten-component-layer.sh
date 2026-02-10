#!/bin/bash
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Flatten Component Layer - Part of Phase 5 (Source Code Simplification)
#
# This script implements the first part of Phase 5, focusing on consolidating
# the small subdirectories in the component layer to reduce nesting while
# maintaining clear organization through file naming conventions.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src/main/java/org/s8r"
COMPONENT_DIR="${SRC_DIR}/component"
COMPONENT_NEW_DIR="${COMPONENT_DIR}-new"

echo "==================================================================="
echo "  Flattening Component Layer Structure"
echo "==================================================================="
echo

# Create the new directory structure
echo "Creating new component directory structure..."
mkdir -p "${COMPONENT_NEW_DIR}"
echo "✓ Created new component directory"

# Create README for the consolidated component directory
cat > "${COMPONENT_NEW_DIR}/README.md" << 'EOL'
# Component Layer

This directory contains the component abstractions that form the foundation of the S8r framework.

## File Organization

Files use prefixes to indicate their category:
- `Component*.java` - Core component interfaces and classes
- `Composite*.java` - Composite component related classes
- `Machine*.java` - Machine-related component classes
- `Identity*.java` - Identity-related component classes
- `Logger*.java` - Logging-related component classes

## Design Patterns

The component layer implements several key design patterns:
- Composite Pattern: Allows treating individual objects and compositions uniformly
- Factory Pattern: Provides interfaces for creating objects without specifying concrete classes
- Identity Pattern: Provides unique identification and addressing for components
- State Pattern: Manages component lifecycle states

## Related Directories

- `../domain/component` - Domain model implementation of components
- `../application` - Application services that use components
EOL
echo "✓ Created README.md for component directory"

# Copy the package-info.java file first
echo "Copying package-info.java..."
cp "${COMPONENT_DIR}/package-info.java" "${COMPONENT_NEW_DIR}/"
echo "✓ Copied package-info.java"

# Process core subdirectory
echo "Processing component/core subdirectory..."
for file in "${COMPONENT_DIR}/core"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    # Skip package-info.java as we've already copied it
    if [ "$filename" != "package-info.java" ]; then
      # For these core files, we don't rename them as they're already at the root level
      cp "$file" "${COMPONENT_NEW_DIR}/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

# Process composite subdirectory
echo "Processing component/composite subdirectory..."
for file in "${COMPONENT_DIR}/composite"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      case "$filename" in
        "Composite.java")
          cp "$file" "${COMPONENT_NEW_DIR}/CompositeComponent.java"
          echo "  Copied $filename → CompositeComponent.java"
          ;;
        "CompositeException.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentCompositeException.java"
          echo "  Copied $filename → ComponentCompositeException.java"
          ;;
        "CompositeFactory.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentCompositeFactory.java"
          echo "  Copied $filename → ComponentCompositeFactory.java"
          ;;
        *)
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentComposite${filename}"
          echo "  Copied $filename → ComponentComposite${filename}"
          ;;
      esac
    fi
  fi
done

# Process identity subdirectory
echo "Processing component/identity subdirectory..."
for file in "${COMPONENT_DIR}/identity"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      case "$filename" in
        "Identity.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentIdentity.java"
          echo "  Copied $filename → ComponentIdentity.java"
          ;;
        *)
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentIdentity${filename}"
          echo "  Copied $filename → ComponentIdentity${filename}"
          ;;
      esac
    fi
  fi
done

# Process logging subdirectory
echo "Processing component/logging subdirectory..."
for file in "${COMPONENT_DIR}/logging"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      case "$filename" in
        "Logger.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentLogger.java"
          echo "  Copied $filename → ComponentLogger.java"
          ;;
        *)
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentLogger${filename}"
          echo "  Copied $filename → ComponentLogger${filename}"
          ;;
      esac
    fi
  fi
done

# Process machine subdirectory
echo "Processing component/machine subdirectory..."
for file in "${COMPONENT_DIR}/machine"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      case "$filename" in
        "Machine.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentMachine.java"
          echo "  Copied $filename → ComponentMachine.java"
          ;;
        "MachineException.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentMachineException.java"
          echo "  Copied $filename → ComponentMachineException.java"
          ;;
        "MachineFactory.java")
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentMachineFactory.java"
          echo "  Copied $filename → ComponentMachineFactory.java"
          ;;
        *)
          cp "$file" "${COMPONENT_NEW_DIR}/ComponentMachine${filename}"
          echo "  Copied $filename → ComponentMachine${filename}"
          ;;
      esac
    fi
  fi
done

# Process exception subdirectory
echo "Processing component/exception subdirectory..."
for file in "${COMPONENT_DIR}/exception"/*.java; do
  if [ -f "$file" ]; then
    filename=$(basename "$file")
    if [ "$filename" != "package-info.java" ]; then
      # For exception files, we keep the original name since they're already prefixed with Component
      cp "$file" "${COMPONENT_NEW_DIR}/"
      echo "  Copied $filename → $filename"
    fi
  fi
done

echo "✓ Copied all component files to new structure"
echo

# Update package declarations in the new files
echo "Updating package declarations..."
for file in "${COMPONENT_NEW_DIR}"/*.java; do
  if [ -f "$file" ]; then
    # Skip package-info.java as it already has the correct package declaration
    if [ "$(basename "$file")" != "package-info.java" ]; then
      # Update package declarations from subpackages to the main package
      sed -i 's/package org.s8r.component.[a-z]*;/package org.s8r.component;/' "$file"
      echo "  Updated package declaration in $(basename "$file")"
    fi
  fi
done
echo "✓ Updated package declarations"

echo
echo "==================================================================="
echo "  Component Layer structure flattened!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Review the new structure in ${COMPONENT_NEW_DIR}"
echo "2. Run compilation check to verify changes"
echo "3. Update imports in dependent files"
echo "4. Replace the original directory when verified"
echo

echo "To verify compilation, run:"
echo "  cd ${ROOT_DIR}/Samstraumr && mvn compile -Dmaven.test.skip=true"
echo
echo "To update imports in dependent files, you can use:"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.composite\" | xargs sed -i 's/org.s8r.component.composite/org.s8r.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.identity\" | xargs sed -i 's/org.s8r.component.identity/org.s8r.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.logging\" | xargs sed -i 's/org.s8r.component.logging/org.s8r.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.machine\" | xargs sed -i 's/org.s8r.component.machine/org.s8r.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.exception\" | xargs sed -i 's/org.s8r.component.exception/org.s8r.component/g'"
echo "  find ${SRC_DIR} -name \"*.java\" | xargs grep -l \"org.s8r.component.core\" | xargs sed -i 's/org.s8r.component.core/org.s8r.component/g'"
echo

echo "To apply the changes when verified:"
echo "  mv ${COMPONENT_DIR} ${COMPONENT_DIR}-old"
echo "  mv ${COMPONENT_NEW_DIR} ${COMPONENT_DIR}"
echo "  rm -rf ${COMPONENT_DIR}-old"
echo

echo "Done!"