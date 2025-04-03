#!/bin/bash
# DEPRECATED: Legacy redirect script
# This redirects to the new consolidated version utility

# Display deprecation warning
echo -e "\033[1;33mWARNING: bin/update-version.sh is deprecated and will be removed in a future release.\033[0m"
echo -e "Please use \033[1;32m./util/version\033[0m instead with one of the following commands:"
echo -e "  \033[1;32m./util/version bump patch\033[0m     - Bump patch version"
echo -e "  \033[1;32m./util/version test patch\033[0m     - Bump version, run tests, commit and tag"
echo ""

# Forward to new script
./util/version "$@"