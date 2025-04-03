#!/bin/bash
# Legacy wrapper for version management
# This redirects to the new unified version utility

# Display deprecation warning
echo -e "\033[1;33mWARNING: update-version.sh is deprecated and will be removed in a future release.\033[0m"
echo -e "Please use \033[1;32m./util/version set VERSION\033[0m or \033[1;32m./util/version bump patch\033[0m instead."
echo ""

# Pass all arguments to the new version utility
if [ "$1" != "" ]; then
  ./util/version set "$1" "${@:2}"
else
  ./util/version --help
fi