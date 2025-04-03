#!/bin/bash
# Legacy wrapper - redirects to the new location

echo -e "\033[1;33mWARNING: run-tests.sh has been moved to util/test/\033[0m"
echo -e "Please use \033[1;32m./util/test/run-tests.sh\033[0m or \033[1;32m./util/test/run-all-tests.sh\033[0m instead."
echo ""

# Forward to the appropriate script
if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
  echo -e "\033[1;32mShowing help from ./util/test/run-all-tests.sh:\033[0m"
  ./util/test/run-all-tests.sh --help
else
  ./util/test/run-all-tests.sh "$@"
fi
