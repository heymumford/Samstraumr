#!/bin/bash
# Adam Tube tests runner using Maven

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

# Determine if we want ATL tests only
if [ "$1" == "atl" ]; then
  PROFILE="${SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE}"
  REPORT_SUFFIX="adam-tube-atl"
  echo "Running Adam Tube ATL tests only"
elif [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
  echo "Usage: ./run-adam-tube-tests.sh [atl|--help|-h]"
  echo "  atl: Run only ATL (Above The Line) Adam Tube tests"
  echo "  --help, -h: Show this help message"
  exit 0
else
  PROFILE="${SAMSTRAUMR_ADAM_TUBE_PROFILE}"
  REPORT_SUFFIX="adam-tube"
  echo "Running all Adam Tube tests"
fi

# Run tests through Maven
echo "Running Adam Tube tests using Maven profile: $PROFILE"
mvn -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" clean test -P ${PROFILE} ${SAMSTRAUMR_RUN_TESTS}

# Check exit code
STATUS=$?
if [ $STATUS -eq 0 ]; then
  echo "Tests completed successfully!"
  echo "HTML report available at: ${SAMSTRAUMR_TARGET}/cucumber-reports/${REPORT_SUFFIX}.html"
else
  echo "Tests failed with exit code $STATUS"
fi

exit $STATUS