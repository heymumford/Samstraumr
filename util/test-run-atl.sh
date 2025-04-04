#!/bin/bash
# This is a redirect script that points to the new location

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Source the configuration file
if [ -f "${SCRIPT_DIR}/.samstraumr.config" ]; then
  source "${SCRIPT_DIR}/.samstraumr.config"
fi

echo -e "\033[1;33mWARNING: run-atl-tests.sh has been moved to util/test/\033[0m"
echo -e "Please use \033[1;32m./util/test/run-atl-tests.sh\033[0m instead."
echo ""

# Forward to new script
./util/test/run-atl-tests.sh "$@"