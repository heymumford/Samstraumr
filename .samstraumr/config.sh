#!/bin/bash
#==============================================================================
# Filename: config.sh
# Description: Samstraumr configuration for shell scripts
#==============================================================================
# AUTO-GENERATED FILE - DO NOT EDIT DIRECTLY
# Generated from config.json
#==============================================================================

# Determine project root automatically
SAMSTRAUMR_PROJECT_ROOT="${SAMSTRAUMR_PROJECT_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"

# Project Configuration
SAMSTRAUMR_NAME="Samstraumr"
SAMSTRAUMR_VERSION="1.6.3"
SAMSTRAUMR_DESCRIPTION="Tube-Based Design (TBD) framework for dynamic adaptive systems"
SAMSTRAUMR_DEFAULT_COMMAND="build"

# Path Configuration
SAMSTRAUMR_ROOT="${SAMSTRAUMR_PROJECT_ROOT}/."
SAMSTRAUMR_CORE_MODULE="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core"
SAMSTRAUMR_SRC_MAIN="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/main"
SAMSTRAUMR_SRC_TEST="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/test"
SAMSTRAUMR_JAVA_MAIN="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/main/java"
SAMSTRAUMR_JAVA_TEST="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/test/java"
SAMSTRAUMR_RESOURCES_MAIN="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/main/resources"
SAMSTRAUMR_RESOURCES_TEST="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/test/resources"
SAMSTRAUMR_TARGET="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/target"
SAMSTRAUMR_SCRIPTS_DIR="${SAMSTRAUMR_PROJECT_ROOT}/util/bin"
SAMSTRAUMR_DOCS="${SAMSTRAUMR_PROJECT_ROOT}/docs"
SAMSTRAUMR_TEMPLATES="${SAMSTRAUMR_PROJECT_ROOT}/Samstraumr/samstraumr-core/src/main/resources/templates"
SAMSTRAUMR_QUALITY_TOOLS="${SAMSTRAUMR_PROJECT_ROOT}/quality-tools"

# Package Configuration
SAMSTRAUMR_CORE_PACKAGE="org.tube.core"
SAMSTRAUMR_S8R_CORE_PACKAGE="org.s8r.core"
SAMSTRAUMR_TEST_PACKAGE="org.test"
SAMSTRAUMR_COMPOSITE_PACKAGE="org.tube.composite"
SAMSTRAUMR_MACHINE_PACKAGE="org.tube.machine"
SAMSTRAUMR_LEGACY_CORE_PACKAGE="org.samstraumr.tube"
SAMSTRAUMR_LEGACY_TEST_PACKAGE="org.samstraumr.tube.test"

# Test Configuration
SAMSTRAUMR_FAST_PROFILE="fast"
SAMSTRAUMR_SKIP_QUALITY_PROFILE="skip-quality-checks"
SAMSTRAUMR_ATL_PROFILE="atl-tests"
SAMSTRAUMR_BTL_PROFILE="btl-tests"
SAMSTRAUMR_ADAM_TUBE_PROFILE="adam-tube-tests"
SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE="adam-tube-atl-tests"
SAMSTRAUMR_SKIP_TESTS="-DskipTests=true"
SAMSTRAUMR_RUN_TESTS="-DskipTests=false -Dmaven.test.skip=false"
SAMSTRAUMR_SKIP_QUALITY="-Dspotless.check.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true"

# Maven Configuration
SAMSTRAUMR_PARALLEL_FLAG="-T 1C"
SAMSTRAUMR_MEMORY_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Helper for converting package paths
# Usage: path_for_package "org.test.steps"
path_for_package() {
  echo "${1//./\/}"
}

# Helper to get absolute path
# Usage: absolute_path "relative/path"
absolute_path() {
  echo "${SAMSTRAUMR_PROJECT_ROOT}/$1"
}
