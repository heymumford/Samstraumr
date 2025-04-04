#!/bin/bash
# This is a redirect script that points to the new location

echo -e "\033[1;33mWARNING: update-version.sh has been moved to util/bin/version/version-manager.sh\033[0m"
echo -e "Please use \033[1;32m./util/bin/version/version-manager.sh\033[0m instead."
echo ""

# Forward to new script
/home/emumford/NativeLinuxProjects/Samstraumr/util/bin/version/version-manager.sh "$@"
