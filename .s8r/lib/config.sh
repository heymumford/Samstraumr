#!/bin/bash
# Samstraumr Configuration File
# This file centralizes all path and configuration settings for the Samstraumr project
# Source this file in all scripts to ensure consistent settings across the build system

# Project structure
SAMSTRAUMR_PROJECT_ROOT="${SAMSTRAUMR_PROJECT_ROOT:-$(pwd)}"
SAMSTRAUMR_CORE_MODULE="${SAMSTRAUMR_PROJECT_ROOT}/modules/samstraumr-core"
SAMSTRAUMR_SRC_MAIN="${SAMSTRAUMR_CORE_MODULE}/src/main"
SAMSTRAUMR_SRC_TEST="${SAMSTRAUMR_CORE_MODULE}/src/test"
SAMSTRAUMR_TARGET="${SAMSTRAUMR_CORE_MODULE}/target"

# Java source structure
SAMSTRAUMR_JAVA_MAIN="${SAMSTRAUMR_SRC_MAIN}/java"
SAMSTRAUMR_JAVA_TEST="${SAMSTRAUMR_SRC_TEST}/java"
SAMSTRAUMR_RESOURCES_TEST="${SAMSTRAUMR_SRC_TEST}/resources"

# Package structure (updated for the new flattened structure)
SAMSTRAUMR_CORE_PACKAGE="org.tube.core"
SAMSTRAUMR_TEST_PACKAGE="org.test"
SAMSTRAUMR_COMPOSITE_PACKAGE="org.tube.composite"
SAMSTRAUMR_MACHINE_PACKAGE="org.tube.machine"

# Legacy package structure (kept for reference during migration)
SAMSTRAUMR_LEGACY_CORE_PACKAGE="org.s8r.tube"
SAMSTRAUMR_LEGACY_TEST_PACKAGE="org.s8r.tube.test"

# Test resources
SAMSTRAUMR_TEST_FEATURES="${SAMSTRAUMR_RESOURCES_TEST}/test/features"
SAMSTRAUMR_TEST_PATTERNS="${SAMSTRAUMR_RESOURCES_TEST}/test/patterns"

# Maven profiles
SAMSTRAUMR_FAST_PROFILE="fast"
SAMSTRAUMR_SKIP_QUALITY_PROFILE="skip-quality-checks"
SAMSTRAUMR_ATL_PROFILE="atl-tests"
SAMSTRAUMR_BTL_PROFILE="btl-tests"
SAMSTRAUMR_ADAM_TUBE_PROFILE="adam-tube-tests"
SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE="adam-tube-atl-tests"

# Maven options
SAMSTRAUMR_PARALLEL_FLAG="-T 1C"
SAMSTRAUMR_MEMORY_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Maven build flags
SAMSTRAUMR_SKIP_TESTS="-DskipTests=true"
SAMSTRAUMR_RUN_TESTS="-DskipTests=false -Dmaven.test.skip=false"
SAMSTRAUMR_SKIP_QUALITY="-Dspotless.check.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true"

# Report locations
SAMSTRAUMR_CUCUMBER_REPORT="${SAMSTRAUMR_TARGET}/cucumber-reports/cucumber.html"
SAMSTRAUMR_JACOCO_REPORT="${SAMSTRAUMR_TARGET}/site/jacoco/index.html"

# Helper for converting package paths
# Usage: path_for_package "org.test.steps"
path_for_package() {
  echo "${1//./\/}"
}

# Version information
SAMSTRAUMR_VERSION="1.2.9"  # Should be read from version.properties in a real implementation