#!/bin/bash
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Flatten Component Layer (Alternative Approach) - Part of Phase 5 (Source Code Simplification)
#
# This script implements an alternative approach to flattening the component layer,
# maintaining original class names while modernizing imports and package structure.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
SRC_DIR="${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r"
COMPONENT_DIR="${SRC_DIR}/component"
TMP_DIR=$(mktemp -d)

echo "==================================================================="
echo "  Flattening Component Layer Structure (Alternative Approach)"
echo "==================================================================="
echo

# Create README for the component directory
cat > "${TMP_DIR}/README.md" << 'EOL'
# Component Layer

This directory contains the component abstractions that form the foundation of the S8r framework.

## File Organization

Files are organized by their primary responsibility:
- `Component.java` - Core component interface
- `State.java`, `Environment.java` - Core component support classes
- `Composite.java`, `CompositeFactory.java` - Composite component classes
- `Machine.java`, `MachineFactory.java` - Machine-related classes
- `Identity.java` - Identity-related class
- `Logger.java` - Logging-related class

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

# Create a list of files to update
echo "Creating flattened component structure..."

# Copy the package-info.java file
cp "${COMPONENT_DIR}/package-info.java" "${TMP_DIR}/"

# Create a subdirectory list
subdirs=("core" "composite" "identity" "logging" "machine" "exception")

# Copy files from subdirectories to main component directory while preserving class names
for subdir in "${subdirs[@]}"; do
  echo "Processing component/${subdir} subdirectory..."
  for file in "${COMPONENT_DIR}/${subdir}"/*.java; do
    if [ -f "$file" ]; then
      filename=$(basename "$file")
      if [ "$filename" != "package-info.java" ]; then
        cp "$file" "${TMP_DIR}/"
        echo "  Copied ${subdir}/${filename} → ${filename}"
      fi
    fi
  done
done

echo "✓ Copied all component files to flattened structure"
echo

# Update package declarations in the files
echo "Updating package declarations..."
for file in "${TMP_DIR}"/*.java; do
  if [ -f "$file" ]; then
    # Skip package-info.java as it already has the correct package declaration
    if [ "$(basename "$file")" != "package-info.java" ]; then
      # Update package declarations from subpackages to the main package
      sed -i 's/package org.s8r.component.[a-z]*;/package org.s8r.component;/' "$file"
      echo "  Updated package declaration in $(basename "$file")"
    fi
  fi
done

# Update imports in the files
echo "Updating import statements..."
for file in "${TMP_DIR}"/*.java; do
  if [ -f "$file" ]; then
    # Update imports from subpackages to the main package
    sed -i 's/import org.s8r.component.core./import org.s8r.component./g' "$file"
    sed -i 's/import org.s8r.component.composite./import org.s8r.component./g' "$file"
    sed -i 's/import org.s8r.component.identity./import org.s8r.component./g' "$file"
    sed -i 's/import org.s8r.component.logging./import org.s8r.component./g' "$file"
    sed -i 's/import org.s8r.component.machine./import org.s8r.component./g' "$file"
    sed -i 's/import org.s8r.component.exception./import org.s8r.component./g' "$file"
    echo "  Updated imports in $(basename "$file")"
  fi
done
echo "✓ Updated imports"

# Scan for any remaining imports that need fixing
echo "Scanning for any remaining component imports that need fixing..."
remaining_imports=$(grep -r "import org.s8r.component." --include="*.java" "${TMP_DIR}" || true)
if [ -n "$remaining_imports" ]; then
  echo "Warning: Found remaining component subpackage imports that need to be updated:"
  echo "$remaining_imports"
else
  echo "✓ No remaining component subpackage imports found"
fi

echo
echo "==================================================================="
echo "  Component Layer structure flattened!"
echo "==================================================================="
echo
echo "Temporary directory with flattened structure: ${TMP_DIR}"
echo
echo "Next steps:"
echo "1. Review the flattened structure in ${TMP_DIR}"
echo "2. Back up the original component directory:"
echo "   mv ${COMPONENT_DIR} ${COMPONENT_DIR}-old"
echo "3. Apply the changes:"
echo "   mkdir -p ${COMPONENT_DIR} && cp ${TMP_DIR}/*.java ${TMP_DIR}/README.md ${COMPONENT_DIR}/"
echo "4. Run compilation check to verify changes"
echo "5. Update imports in dependent files (outside component package)"
echo
echo "To verify compilation after applying changes, run:"
echo "  cd ${ROOT_DIR}/Samstraumr && mvn compile -Dmaven.test.skip=true"
echo
echo "To update imports in dependent files, you can use:"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.core\" | xargs sed -i 's/import org.s8r.component.core./import org.s8r.component./g'"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.composite\" | xargs sed -i 's/import org.s8r.component.composite./import org.s8r.component./g'"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.identity\" | xargs sed -i 's/import org.s8r.component.identity./import org.s8r.component./g'"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.logging\" | xargs sed -i 's/import org.s8r.component.logging./import org.s8r.component./g'"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.machine\" | xargs sed -i 's/import org.s8r.component.machine./import org.s8r.component./g'"
echo "  find ${SRC_DIR} -name \"*.java\" -not -path \"*${COMPONENT_DIR}*\" | xargs grep -l \"org.s8r.component.exception\" | xargs sed -i 's/import org.s8r.component.exception./import org.s8r.component./g'"
echo
echo "Done!"