#!/bin/bash
# ==================================================================
# Filename: java-env-setup.sh
# Purpose: Set up Java environment variables to avoid redundant messages and ensure consistent encoding
# Location: util/build/
# Usage: source ./util/build/java-env-setup.sh
# 
# FEATURES:
# - Eliminates redundant "Picked up JAVA_TOOL_OPTIONS" messages during Maven builds
# - Ensures consistent UTF-8 encoding for all Java processes
# - Properly handles restoration of original environment variables when script exits
# - Can be sourced by other build scripts to ensure consistent configuration
# - Uses trap to automatically restore environment on script exit
#
# TECHNICAL DETAILS:
# This script temporarily unsets JAVA_TOOL_OPTIONS to avoid redundant messages
# during Maven builds, while preserving its functionality by moving the encoding
# settings into MAVEN_OPTS. The original JAVA_TOOL_OPTIONS is restored when the
# script exits or when restore_java_tool_options is called explicitly.
# ==================================================================

# Save existing JAVA_TOOL_OPTIONS if set
SAVED_JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS"

# Unset to avoid redundant messages
unset JAVA_TOOL_OPTIONS

# Set Maven options with proper encoding included
# This will keep existing MAVEN_OPTS if set, or default to -Xmx1g
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx1g} -Dfile.encoding=UTF-8"

# Function to restore original Java options
restore_java_tool_options() {
    # Restore original JAVA_TOOL_OPTIONS if it was set
    if [ -n "$SAVED_JAVA_TOOL_OPTIONS" ]; then
        export JAVA_TOOL_OPTIONS="$SAVED_JAVA_TOOL_OPTIONS"
    fi
}

# Install trap to restore options on script exit
trap restore_java_tool_options EXIT

# Export the restore function so it can be called explicitly if needed
export -f restore_java_tool_options

echo "Java environment set up with proper encoding and Maven options"
echo "MAVEN_OPTS: $MAVEN_OPTS"