#!/bin/bash

# Script to map between industry-standard and Samstraumr-specific test terminology

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