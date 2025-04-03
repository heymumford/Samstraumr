#!/bin/bash
# This is a redirect script that points to the new location

echo -e "\033[1;33mWARNING: setup-fast.sh has been moved to util/scripts/\033[0m"
echo -e "Please use \033[1;32m./util/scripts/setup-fast-build.sh\033[0m instead."
echo ""

# Forward to new script
./util/scripts/setup-fast-build.sh "$@"
