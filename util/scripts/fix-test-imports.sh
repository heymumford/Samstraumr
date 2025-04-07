#!/bin/bash
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Fix Test Imports - For Phase 5 Documentation Approach
#
# This script updates import statements in test files to work with
# the revised directory structure with package-info.java files.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
TEST_DIR="${ROOT_DIR}/Samstraumr/samstraumr-core/src/test/java"

echo "==================================================================="
echo "  Fixing Test Import Statements"
echo "==================================================================="
echo

echo "Creating symbolic links for backwards compatibility..."
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core"
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/composite" 
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/identity"
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/machine"
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/exception"
mkdir -p "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/logging"

# Create symbolic links from old component subdirectories to main component directory
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Component.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/Component.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Environment.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/Environment.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/State.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/core/State.java"

ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Composite.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/composite/Composite.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/CompositeException.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/composite/CompositeException.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/CompositeFactory.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/composite/CompositeFactory.java"

ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Identity.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/identity/Identity.java"

ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Machine.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/machine/Machine.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/MachineException.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/machine/MachineException.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/MachineFactory.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/machine/MachineFactory.java"

ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/ComponentException.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/exception/ComponentException.java"
ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/InvalidStateTransitionException.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/exception/InvalidStateTransitionException.java"

ln -sf "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/Logger.java" "${ROOT_DIR}/Samstraumr/samstraumr-core/src/main/java/org/s8r/component/logging/Logger.java"

echo "✓ Created symbolic links for backwards compatibility"

echo
echo "==================================================================="
echo "  Test Import Fixes Complete!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Run tests to verify backward compatibility works:"
echo "   cd ${ROOT_DIR} && ./s8r-test unit"
echo

echo "Done!"