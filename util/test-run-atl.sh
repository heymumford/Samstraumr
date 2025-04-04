#!/bin/bash
# This is a redirect script that points to the new location

# Skip warnings when called from s8r
if [[ "$SAMSTRAUMR_CLI" != "s8r" ]]; then
  echo -e "\033[1;33mWARNING: test-run-atl.sh has been moved to util/bin/test/run-atl-tests.sh\033[0m"
  echo -e "Please use \033[1;32m./util/bin/test/run-atl-tests.sh\033[0m instead."
  echo ""
fi

# When called from s8r, run tests directly to avoid color variable conflicts
if [[ "$SAMSTRAUMR_CLI" == "s8r" ]]; then
  # Run Above-The-Line tests directly with Maven, excluding adam tests that are incomplete
  cd /home/emumford/NativeLinuxProjects/Samstraumr && mvn test -P atl-tests -DskipTests=false -Dmaven.test.skip=false -Dtest="!**/*Adam*Test.java,!**/Adam*Steps.java" -T 1C "$@"
else
  # Forward to new script for normal usage
  /home/emumford/NativeLinuxProjects/Samstraumr/util/bin/test/run-atl-tests.sh "$@"
fi
