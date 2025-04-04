#!/bin/bash
# Source the Samstraumr unified configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the unified configuration
if [ -f "${PROJECT_ROOT}/.samstraumr/config.sh" ]; then
  source "${PROJECT_ROOT}/.samstraumr/config.sh"
else
  echo "Error: Configuration file not found: ${PROJECT_ROOT}/.samstraumr/config.sh"
  exit 1
fi

# Script implementation here
echo "Samstraumr project version: ${SAMSTRAUMR_VERSION}"
echo "Samstraumr core module: ${SAMSTRAUMR_CORE_MODULE}"