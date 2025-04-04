#!/bin/bash
# Legacy wrapper - redirects to the new location

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
fi

echo -e "\033[1;33mWARNING: map-test-type.sh has been moved to util/test/mapping/\033[0m"
echo -e "Please use \033[1;32m./util/test/mapping/map-test-type.sh\033[0m instead."
echo ""

# Forward to new script
"${SCRIPT_DIR}/test/mapping/map-test-type.sh" "$@"
