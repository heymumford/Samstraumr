<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Proposal for Configuration Improvement

After reviewing utility scripts and identifying hardcoded paths that may break during refactoring, this document proposes a more maintainable approach using environment variables and project-level key-value configuration.

## Current Issues

1. **Hardcoded Directory Structure**:
   - Scripts directly reference paths like `Samstraumr/samstraumr-core/src/test/java/org/samstraumr/tube/...`
   - Recent refactoring of the directory structure from `org/samstraumr/tube/*` to `org/tube/*` and `org/test/*` would break these scripts
2. **Duplicated Path Definitions**:
   - Multiple scripts define the same paths independently
   - No central configuration file for important paths and settings
3. **Mixed Build Command Styles**:
   - Some scripts use relative paths, others use absolute paths
   - Inconsistent use of Maven parameters and profiles

## Proposed Solution

### 1. create a central configuration file

Create a `.samstraumr.config` file in the project root with key-value pairs:

```bash
# Proposal for Configuration Improvement
SAMSTRAUMR_PROJECT_ROOT="${SAMSTRAUMR_PROJECT_ROOT:-$(pwd)}"
SAMSTRAUMR_CORE_MODULE="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core"
SAMSTRAUMR_SRC_MAIN="${SAMSTRAUMR_CORE_MODULE}/src/main"
SAMSTRAUMR_SRC_TEST="${SAMSTRAUMR_CORE_MODULE}/src/test"
SAMSTRAUMR_TARGET="${SAMSTRAUMR_CORE_MODULE}/target"

# Proposal for Configuration Improvement
SAMSTRAUMR_JAVA_MAIN="${SAMSTRAUMR_SRC_MAIN}/java"
SAMSTRAUMR_JAVA_TEST="${SAMSTRAUMR_SRC_TEST}/java"
SAMSTRAUMR_RESOURCES_TEST="${SAMSTRAUMR_SRC_TEST}/resources"

# Proposal for Configuration Improvement
SAMSTRAUMR_CORE_PACKAGE="org.tube.core"
SAMSTRAUMR_TEST_PACKAGE="org.test"
SAMSTRAUMR_COMPOSITE_PACKAGE="org.tube.composite"
SAMSTRAUMR_MACHINE_PACKAGE="org.tube.machine"

# Proposal for Configuration Improvement
SAMSTRAUMR_TEST_FEATURES="${SAMSTRAUMR_RESOURCES_TEST}/test/features"
SAMSTRAUMR_TEST_PATTERNS="${SAMSTRAUMR_RESOURCES_TEST}/test/patterns"

# Proposal for Configuration Improvement
SAMSTRAUMR_FAST_PROFILE="fast"
SAMSTRAUMR_SKIP_QUALITY_PROFILE="skip-quality-checks"
SAMSTRAUMR_ATL_PROFILE="atl-tests"
SAMSTRAUMR_BTL_PROFILE="btl-tests"

# Proposal for Configuration Improvement
SAMSTRAUMR_PARALLEL_FLAG="-T 1C"
SAMSTRAUMR_MEMORY_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"
```

### 2. source this configuration in all scripts

Add to the beginning of all utility scripts:

```bash
# Proposal for Configuration Improvement

# Proposal for Configuration Improvement
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Proposal for Configuration Improvement
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi
```

### 3. refactor scripts to use configuration variables

#### Example 1: fix logger script

**Current:**

```bash
PATTERN_STEPS_FILE="Samstraumr/samstraumr-core/src/test/java/org/samstraumr/tube/steps/PatternSteps.java"
sed -i 's/logger\./LOGGER\./g' "$PATTERN_STEPS_FILE"
```

**Refactored:**

```bash
PATTERN_STEPS_FILE="${SAMSTRAUMR_JAVA_TEST}/${SAMSTRAUMR_TEST_PACKAGE//./\/}/steps/PatternSteps.java"
sed -i 's/logger\./LOGGER\./g' "$PATTERN_STEPS_FILE"
```

#### Example 2: standardize feature files

**Current:**

```bash
if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" ]; then
  mv -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" \
       "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature"
fi
```

**Refactored:**

```bash
EXAMPLES_DIR="${SAMSTRAUMR_TEST_FEATURES}/examples"
if [ -f "${EXAMPLES_DIR}/TBD-AtomicBoundaryTest-Example.feature" ]; then
  mv -v "${EXAMPLES_DIR}/TBD-AtomicBoundaryTest-Example.feature" \
        "${EXAMPLES_DIR}/TBD-atomic-boundary-test-example.feature"
fi
```

#### Example 3: optimal build script

**Current:**

```bash
echo "🚀 Building Samstraumr with optimized settings"
mvn $CLEAN $MODE $PARALLEL_FLAG $PROFILE $ADDITIONAL_ARGS
```

**Refactored:**

```bash
echo "🚀 Building Samstraumr with optimized settings"
mvn -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" $CLEAN $MODE ${SAMSTRAUMR_PARALLEL_FLAG} $PROFILE $ADDITIONAL_ARGS
```

### 4. create a update script for path changes

When paths change due to refactoring (as in the recent move from `org.samstraumr.tube` to `org.tube`), create a simple script to update the central configuration:

```bash
# Proposal for Configuration Improvement
# Proposal for Configuration Improvement

# Proposal for Configuration Improvement
source ./.samstraumr.config

# Proposal for Configuration Improvement
sed -i 's/SAMSTRAUMR_CORE_PACKAGE="org.tube.core"/SAMSTRAUMR_CORE_PACKAGE="org.newpath.core"/g' ./.samstraumr.config
sed -i 's/SAMSTRAUMR_TEST_PACKAGE="org.test"/SAMSTRAUMR_TEST_PACKAGE="org.newpath.test"/g' ./.samstraumr.config

echo "Configuration updated. Please review .samstraumr.config to verify changes."
```

## Benefits

1. **Single Point of Truth**:
   - All paths and important configurations defined in one place
   - Reduces risk of inconsistency across scripts
2. **Easier Refactoring**:
   - When paths change, only update the central configuration file
   - Scripts automatically use the new paths
3. **Better Documentation**:
   - New developers can quickly understand project structure
   - Configuration file serves as documentation
4. **Environment Flexibility**:
   - Different environments can override variables as needed
   - CI/CD systems can inject custom configurations

## Implementation Plan

1. Create the `.samstraumr.config` file with current paths
2. Update one critical script to use the configuration
3. Test the script works correctly
4. Progressively update remaining scripts
5. Add documentation in CLAUDE.md about the configuration approach
