#!/bin/bash
# DEPRECATED: Legacy wrapper for version management
# This redirects to the new consolidated version utility

# Display deprecation warning
echo -e "\033[1;33mWARNING: util/version.sh is deprecated and will be removed in a future release.\033[0m"
echo -e "Please use \033[1;32m./util/version\033[0m instead with one of the following commands:"
echo -e "  \033[1;32m./util/version get\033[0m            - Show version information"
echo -e "  \033[1;32m./util/version bump patch\033[0m     - Bump patch version"
echo -e "  \033[1;32m./util/version test patch\033[0m     - Bump version, run tests, commit and tag"
echo -e "  \033[1;32m./util/version set VERSION\033[0m    - Set specific version"
echo ""

# Pass all arguments to the consolidated version utility
./util/version "$@"