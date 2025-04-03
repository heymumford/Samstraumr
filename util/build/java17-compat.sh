#!/bin/bash
# This script sets up compatibility options for using Java 21 with Java 17 source code
# It should be sourced before running Maven commands

# Check current Java version
java_version_output=$(java -version 2>&1)
echo "Java version:"
echo "$java_version_output"

# Unset any existing JAVA_TOOL_OPTIONS to avoid conflicts
unset JAVA_TOOL_OPTIONS

# Special handling for Java 21 with Java 17 projects
echo "Setting Maven to target Java 17 regardless of JDK version"
# Clear previous MAVEN_OPTS to avoid duplication
export MAVEN_OPTS="-Dmaven.compiler.source=17 -Dmaven.compiler.target=17 -Xmx1g"

# Show the configured settings
echo "MAVEN_OPTS: ${MAVEN_OPTS}"

# Add project-specific settings to path to help with development
if [ -d "$HOME/NativeLinuxProjects/Samstraumr" ]; then
    PATH="$PATH:$HOME/NativeLinuxProjects/Samstraumr"
    echo "Added Samstraumr directory to PATH"
fi