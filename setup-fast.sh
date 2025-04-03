#!/bin/bash
# Legacy wrapper - redirects to the new location

echo -e "\033[1;33mWARNING: setup-fast.sh has been moved to util/maintenance/\033[0m"
echo -e "Please use \033[1;32m./util/maintenance/setup-fast.sh\033[0m instead."
echo ""

# Forward to new script
./util/maintenance/setup-fast.sh "$@"
