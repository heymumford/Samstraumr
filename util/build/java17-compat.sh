#!/bin/bash
# This script sets up compatibility options for using Java 21 with Java 17 source code
# It should be sourced before running Maven commands

# Check current Java version
java_version_output=$(java -version 2>&1)
echo "Java version:"
echo "$java_version_output"

# Source the Java environment setup script
source "$(dirname "$0")/java-env-setup.sh"

# Special handling for Java 21 with Java 17 projects
echo "Setting Maven to target Java 17 regardless of JDK version"
# Append Java 17 targeting options to MAVEN_OPTS
export MAVEN_OPTS="$MAVEN_OPTS -Dmaven.compiler.source=17 -Dmaven.compiler.target=17"

# Show the configured settings
echo "MAVEN_OPTS: ${MAVEN_OPTS}"

# Add project-specific settings to path to help with development
if [ -d "$HOME/NativeLinuxProjects/Samstraumr" ]; then
    PATH="$PATH:$HOME/NativeLinuxProjects/Samstraumr"
    echo "Added Samstraumr directory to PATH"
fi