#!/bin/bash
# Legacy script - redirects to the new unified version utility
# This script is kept for backward compatibility

# Display deprecation warning
echo -e "\033[1;33mWARNING: util/maintenance/update-version.sh is deprecated.\033[0m"
echo -e "Please use \033[1;32m./util/version bump patch\033[0m (preferred) or \033[1;32m./util/version set VERSION\033[0m instead."
echo ""

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Change to project root directory
cd "$PROJECT_ROOT"

# Pass all arguments to the new version utility
if [ "$1" != "" ]; then
  if [[ "$1" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    # If argument is a version number (x.y.z format), use 'set'
    ./util/version set "$1" "${@:2}"
  else
    # For any other input, display help
    echo -e "\033[1;31mError: Invalid version format. Must be in format x.y.z\033[0m"
    ./util/version --help
    exit 1
  fi
else
  # Default to 'bump patch' when no arguments are provided (most common use case)
  echo -e "\033[1;32mDefaulting to patch version update\033[0m"
  ./util/version bump patch
fi