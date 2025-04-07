#\!/bin/bash
#==============================================================================
# flatten-test-structure.sh - Implement Phase 4 of the Directory Flattening Plan
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0
#==============================================================================

set -e

echo "Starting Phase 4 of Directory Flattening Plan: Test Structure Simplification"
echo "=========================================================================="

# Define directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_ROOT"

# Create the new flattened test structure
echo "Creating new directory structure..."

# Java test class structure
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/component

# Feature directories
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/tube-lifecycle
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/identity
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/machine
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/system

# Create README files for the new directories
echo "Creating README files for the new directories..."

# tube-lifecycle README
cat > Samstraumr/samstraumr-core/src/test/resources/features/tube-lifecycle/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Tube Lifecycle Tests

This directory contains Cucumber feature files for testing the tube lifecycle functionality.

## Overview

The tube lifecycle tests verify the behavior of tubes as they progress through their lifecycle phases:

- Pre-conception
- Conception
- Embryonic
- Childhood
- Infancy
- Maturity

## Feature Files

- `adam-tube-tests.feature` - Tests for the Adam tube implementation
- `atomic-tube-tests.feature` - Tests for atomic tube behavior
- `childhood-phase-tests.feature` - Tests for the childhood lifecycle phase
- `conception-phase-tests.feature` - Tests for the conception lifecycle phase
- `creation-lifecycle-tests.feature` - Tests for the entire creation lifecycle
- `early-conception-phase-tests.feature` - Tests for early conception state
- `embryonic-phase-tests.feature` - Tests for the embryonic lifecycle phase
- `infancy-phase-tests.feature` - Tests for the infancy lifecycle phase
- `pre-conception-phase-tests.feature` - Tests for pre-conception state
- `tube-initialization-tests.feature` - Tests for tube initialization process

## Related Directories

- `/features/identity` - Tests for tube identity functionality
- `/features/composite-patterns` - Tests for tube pattern implementations
EOL

# identity README
cat > Samstraumr/samstraumr-core/src/test/resources/features/identity/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Identity Tests

This directory contains Cucumber feature files for testing tube identity functionality.

## Overview

The identity tests verify the behavior of different identity mechanisms:

- Memory identity
- Substrate identity
- Persistence across lifecycle phases

## Feature Files

- `memory-identity-tests.feature` - Tests for in-memory identity implementation
- `substrate-identity-tests.feature` - Tests for substrate-based identity

## Related Directories

- `/features/tube-lifecycle` - Tests for tube lifecycle phases
EOL

# composite-patterns README
cat > Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Composite Patterns Tests

This directory contains Cucumber feature files for testing composite design patterns.

## Overview

The composite patterns tests verify the behavior of different pattern implementations:

- Observer pattern
- Transformer pattern
- Validator pattern

## Feature Files

- `observer-tube-test.feature` - Tests for the observer pattern implementation
- `transformer-tube-test.feature` - Tests for the transformer pattern implementation
- `validator-tube-test.feature` - Tests for the validator pattern implementation

## Related Directories

- `/features/tube-lifecycle` - Tests for tube lifecycle phases
- `/features/machine` - Tests for machine orchestration
EOL

# Java test structure READMEs
cat > Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Tube Test Classes

This package contains test classes for the tube functionality of the Samstraumr framework.

## Overview

The tube test classes provide the implementation for the Cucumber steps defined in the feature files.
These classes are organized by functionality rather than by deep nesting.

## Key Classes

- `TubeSteps.java` - Base implementation for tube-related test steps
- `S8rTestRunner.java` - Test runner for executing tube tests

## Related Packages

- `org.s8r.test.tube.lifecycle` - Step implementations for tube lifecycle tests
- `org.s8r.test.legacy` - Step implementations for legacy tube functionality
EOL

# Move the files to the new locations
echo "Moving files to new locations..."

# Java test class moves
# AdamTubeSteps from lifecycle/steps/adam to test/tube/lifecycle
if [ -f "Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/lifecycle/steps/adam/AdamTubeSteps.java" ]; then
  cp "Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/lifecycle/steps/adam/AdamTubeSteps.java" \
     "Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle/AdamTubeSteps.java"
  echo "  Moved AdamTubeSteps.java to test/tube/lifecycle"
fi

# Core tube test steps to test/tube
if [ -d "Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/steps" ]; then
  cp Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/steps/*.java \
     Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/
  echo "  Moved core tube test steps to test/tube"
fi

# Test runners to test/tube
if [ -d "Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/runners" ]; then
  cp Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/runners/*.java \
     Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/
  echo "  Moved test runners to test/tube"
fi

# Legacy test steps to test/legacy
if [ -d "Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/legacy/test/steps" ]; then
  cp Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/legacy/test/steps/*.java \
     Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy/
  echo "  Moved legacy test steps to test/legacy"
fi

# Feature file moves
# Lifecycle feature files to tube-lifecycle
if [ -d "Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle" ]; then
  cp Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle/*.feature \
     Samstraumr/samstraumr-core/src/test/resources/features/tube-lifecycle/
  echo "  Moved lifecycle feature files to features/tube-lifecycle"
fi

# Identity feature files to identity
if [ -d "Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle" ]; then
  # Move specific identity feature files
  for identity_file in memory-identity-tests.feature substrate-identity-tests.feature; do
    if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle/$identity_file" ]; then
      cp "Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle/$identity_file" \
         "Samstraumr/samstraumr-core/src/test/resources/features/identity/"
      echo "  Moved $identity_file to features/identity"
    fi
  done
fi

# Composite pattern feature files to composite-patterns
if [ -d "Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Bundle/patterns" ]; then
  cp Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Bundle/patterns/*.feature \
     Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns/
  echo "  Moved bundle pattern features to features/composite-patterns"
fi

if [ -d "Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Composite/patterns" ]; then
  cp Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Composite/patterns/*.feature \
     Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns/
  echo "  Moved composite pattern features to features/composite-patterns"
fi

# Machine feature files to machine
if [ -d "Samstraumr/samstraumr-core/src/test/resources/tube/features/L2_Machine" ]; then
  cp Samstraumr/samstraumr-core/src/test/resources/tube/features/L2_Machine/*.feature \
     Samstraumr/samstraumr-core/src/test/resources/features/machine/
  echo "  Moved machine feature files to features/machine"
fi

# System feature files to system
if [ -d "Samstraumr/samstraumr-core/src/test/resources/tube/features/L3_System" ]; then
  cp Samstraumr/samstraumr-core/src/test/resources/tube/features/L3_System/*.feature \
     Samstraumr/samstraumr-core/src/test/resources/features/system/
  echo "  Moved system feature files to features/system"
fi

# Fix package declarations in Java files
echo "Updating package declarations in Java files..."

# Update Adam tube steps
if [ -f "Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle/AdamTubeSteps.java" ]; then
  sed -i 's/package org.s8r.tube.lifecycle.steps.adam;/package org.s8r.test.tube.lifecycle;/' \
      "Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle/AdamTubeSteps.java"
  echo "  Updated package declaration in AdamTubeSteps.java"
fi

# Update core tube steps
for file in Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/*.java; do
  if grep -q "package org.s8r.core.tube.test" "$file"; then
    sed -i 's/package org.s8r.core.tube.test.\(steps\|runners\);/package org.s8r.test.tube;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

# Update legacy steps
for file in Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy/*.java; do
  if grep -q "package org.s8r.tube.legacy.test.steps" "$file"; then
    sed -i 's/package org.s8r.tube.legacy.test.steps;/package org.s8r.test.legacy;/' "$file"
    echo "  Updated package declaration in $(basename "$file")"
  fi
done

# Update imports
echo "Updating import statements..."

# Update imports in all test Java files
find Samstraumr/samstraumr-core/src/test/java -name "*.java" -type f -exec sed -i 's/import org.s8r.tube.lifecycle.steps.adam/import org.s8r.test.tube.lifecycle/g' {} \;
find Samstraumr/samstraumr-core/src/test/java -name "*.java" -type f -exec sed -i 's/import org.s8r.core.tube.test.steps/import org.s8r.test.tube/g' {} \;
find Samstraumr/samstraumr-core/src/test/java -name "*.java" -type f -exec sed -i 's/import org.s8r.core.tube.test.runners/import org.s8r.test.tube/g' {} \;
find Samstraumr/samstraumr-core/src/test/java -name "*.java" -type f -exec sed -i 's/import org.s8r.tube.legacy.test.steps/import org.s8r.test.legacy/g' {} \;

echo "Updating feature file references in step definitions..."

# Update references to feature files in step definitions
for file in Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/*.java \
            Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle/*.java \
            Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy/*.java; do
  if [ -f "$file" ]; then
    sed -i 's|resources/tube/features/L0_Tube/lifecycle|resources/features/tube-lifecycle|g' "$file"
    sed -i 's|resources/composites/features/L1_Bundle/patterns|resources/features/composite-patterns|g' "$file"
    sed -i 's|resources/composites/features/L1_Composite/patterns|resources/features/composite-patterns|g' "$file"
    sed -i 's|resources/tube/features/L2_Machine|resources/features/machine|g' "$file"
    sed -i 's|resources/tube/features/L3_System|resources/features/system|g' "$file"
  fi
done

# Create a README for machine directory
cat > Samstraumr/samstraumr-core/src/test/resources/features/machine/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Machine Tests

This directory contains Cucumber feature files for testing machine functionality in the Samstraumr framework.

## Overview

The machine tests verify the behavior of tube machines, which orchestrate and coordinate tubes:

- Machine configuration
- Machine connectivity
- Machine data flow
- Machine state management

## Feature Files

- `machine-configuration-test.feature` - Tests for machine configuration
- `machine-connectivity-patterns-test.feature` - Tests for machine connectivity patterns
- `machine-data-flow-test.feature` - Tests for data flow between tubes in a machine

## Related Directories

- `/features/tube-lifecycle` - Tests for tube lifecycle phases
- `/features/system` - Tests for system-level functionality
EOL

# Create a README for system directory
cat > Samstraumr/samstraumr-core/src/test/resources/features/system/README.md << 'EOL'
<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# System Tests

This directory contains Cucumber feature files for system-level tests in the Samstraumr framework.

## Overview

The system tests verify the end-to-end behavior of the Samstraumr framework:

- System integration
- System reliability
- System resilience
- System scalability
- System security
- End-to-end functionality

## Feature Files

- `system-end-to-end-test.feature` - End-to-end tests for complete workflows
- `system-integration-test.feature` - Tests for system integration
- `system-reliability-test.feature` - Tests for system reliability under various conditions
- `system-resilience-test.feature` - Tests for system ability to recover from failures
- `system-scalability-test.feature` - Tests for system behavior under increased load
- `system-security-test.feature` - Tests for system security aspects

## Related Directories

- `/features/machine` - Tests for machine-level functionality
EOL

echo "Directory flattening for test structure completed\!"
echo "=================================================="
echo "Next steps:"
echo "1. Verify the new structure with './util/scripts/flatten-directories.sh'"
echo "2. Run tests to verify functionality with './s8r-test all'"
echo "3. Commit changes after verification"
