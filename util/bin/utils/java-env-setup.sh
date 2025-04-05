#!/bin/bash
# Java environment setup script
# Sets up default Java flags and suppresses annoying Java warnings

# Source configuration if available
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source "${PROJECT_ROOT}/.samstraumr.configsource "${PROJECT_ROOT}/.samstraumr.config pwd)/../../.samstraumr/config.sh""
fi

# Set default Java options for correct file encoding and memory settings
export _JAVA_OPTIONS="${_JAVA_OPTIONS:--Dfile.encoding=UTF-8}"
export MAVEN_OPTS="${MAVEN_OPTS:--Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1}"

# Suppress annoying Java security warning about using insecure RMI registry
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:--Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -Djava.net.preferIPv4Stack=true}"

# We no longer directly pass arguments to _JAVA_OPTIONS
# This was causing issues when script arguments were being passed to Java
# Instead, use the configuration file for setting Java options

# Print Java environment settings if verbose flag is set
if [ "${JAVA_ENV_VERBOSE:-false}" = "true" ]; then
  echo "Java Environment:"
  echo "  JAVA_HOME:            $JAVA_HOME"
  echo "  _JAVA_OPTIONS:        $_JAVA_OPTIONS"
  echo "  MAVEN_OPTS:           $MAVEN_OPTS"
  echo "  JAVA_TOOL_OPTIONS:    $JAVA_TOOL_OPTIONS"
fi