#!/bin/bash
# Filename: map-test-type.sh
# Purpose: Maps between industry-standard and Samstraumr-specific test terminology
# Location: util/test/mapping/
# Usage: ./util/test-map-type.sh <test-type>
#   Example: ./util/test-map-type.sh unit -> Returns "tube"
#   Example: ./util/test-map-type.sh tube -> Returns "unit"

# Get the test type and convert to lowercase
test_type=$(echo "$1" | tr '[:upper:]' '[:lower:]')

case "$test_type" in
  # Industry standard to Samstraumr
  "smoke")
    echo "orchestration"
    ;;
  "unit")
    echo "tube"
    ;;
  "component")
    echo "composite"
    ;;
  "integration")
    echo "flow"
    ;;
  "api")
    echo "machine"
    ;;
  "system")
    echo "stream"
    ;;
  "endtoend")
    echo "acceptance"
    ;;
  "property")
    echo "adaptation"
    ;;
  
  # Samstraumr to industry standard
  "orchestration")
    echo "smoke"
    ;;
  "tube")
    echo "unit"
    ;;
  "composite")
    echo "component"
    ;;
  "flow")
    echo "integration"
    ;;
  "machine")
    echo "api"
    ;;
  "stream")
    echo "system"
    ;;
  "acceptance")
    echo "endtoend"
    ;;
  "adaptation")
    echo "property"
    ;;
  *)
    echo "unknown"
    exit 1
    ;;
esac