#!/bin/bash
# Legacy wrapper - redirects to the new location

echo -e "\033[1;33mWARNING: update-java-headers.sh has been moved to util/maintenance/headers/\033[0m"
echo -e "Please use \033[1;32m./util/maintenance/headers/update-java-headers.sh\033[0m instead."
echo ""

# Forward to new script
./util/maintenance/headers/update-java-headers.sh "$@"
