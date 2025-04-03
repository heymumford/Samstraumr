#!/bin/bash
# Legacy wrapper - redirects to the new location

echo -e "\033[1;33mWARNING: map-test-type.sh has been moved to util/test/mapping/\033[0m"
echo -e "Please use \033[1;32m./util/test/mapping/map-test-type.sh\033[0m instead."
echo ""

# Forward to new script
./util/test/mapping/map-test-type.sh "$@"
