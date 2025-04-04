#!/bin/bash
# This script sets up compatibility options for using Java 21 with Java 17 source code
# It should be sourced before running Maven commands

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
fi

# Check current Java version
java_version_output=$(java -version 2>&1)
echo "Java version:"
echo "$java_version_output"

# Source the Java environment setup script
source "${SCRIPT_DIR}/java-env-setup.sh"

# Special handling for Java 21 with Java 17 projects
echo "Setting Maven to target Java 17 regardless of JDK version"
# Append Java 17 targeting options to MAVEN_OPTS
export MAVEN_OPTS="$MAVEN_OPTS -Dmaven.compiler.source=17 -Dmaven.compiler.target=17"

# Show the configured settings
echo "MAVEN_OPTS: ${MAVEN_OPTS}"

# Add project-specific settings to path to help with development
if [ -d "${PROJECT_ROOT}" ]; then
    PATH="$PATH:${PROJECT_ROOT}"
    echo "Added Samstraumr directory to PATH"
fi