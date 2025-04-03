#!/bin/bash
# This is a redirect script that points to the new location

echo -e "\033[1;33mWARNING: run-tests.sh has been moved to util/\033[0m"
echo -e "Please use \033[1;32m./util/test-run.sh\033[0m instead."
echo ""

# Forward to new script
./util/test-run.sh "$@"
