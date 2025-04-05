#!/bin/bash
# Filename: map-test-type.sh
# Purpose: Map between industry-standard and Samstraumr-specific test type names
# Location: util/test/mapping/
# Usage: ./map-test-type.sh <test-type>

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source "${PROJECT_ROOT}/.samstraumr.configsource "${PROJECT_ROOT}/.samstraumr.config pwd)/../../.samstraumr/config.sh""
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

# Map test type names
case "$1" in
  # Industry standard -> Samstraumr specific
  smoke)
    echo "orchestration"
    ;;
  unit)
    echo "tube"
    ;;
  component)
    echo "composite"
    ;;
  integration)
    echo "flow"
    ;;
  api)
    echo "machine"
    ;;
  system)
    echo "stream"
    ;;
  endtoend)
    echo "acceptance"
    ;;
  property)
    echo "adaptation"
    ;;
    
  # Samstraumr specific -> Industry standard
  orchestration)
    echo "smoke"
    ;;
  tube)
    echo "unit"
    ;;
  composite)
    echo "component"
    ;;
  flow)
    echo "integration"
    ;;
  machine)
    echo "api"
    ;;
  stream)
    echo "system"
    ;;
  acceptance)
    echo "endtoend"
    ;;
  adaptation)
    echo "property"
    ;;
    
  # Handle bundle (legacy term for composite)
  bundle)
    echo "component"
    ;;
    
  # Special case for all tests
  all)
    echo "all"
    ;;
    
  # Default case for unknown test types
  *)
    echo "unknown"
    echo "Error: Unknown test type: $1" >&2
    exit 1
    ;;
esac