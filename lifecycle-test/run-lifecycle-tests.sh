#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to run the Component lifecycle state machine tests

# Set colors for output
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Print header
echo -e "${YELLOW}=======================================${NC}"
echo -e "${YELLOW}  Component Lifecycle Tests Runner     ${NC}"
echo -e "${YELLOW}=======================================${NC}"

# Parse arguments
coverage=false
verbose=false

while [[ $# -gt 0 ]]; do
  case $1 in
    --coverage|-c)
      coverage=true
      shift
      ;;
    --verbose|-v)
      verbose=true
      shift
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      echo "Usage: $0 [--coverage|-c] [--verbose|-v]"
      exit 1
      ;;
  esac
done

# Set Maven options based on arguments
mvn_opts=""
if $verbose; then
  mvn_opts="-X"
fi

# Execute tests
cd "$(dirname "$0")" || { echo -e "${RED}Failed to change directory${NC}"; exit 1; }

echo -e "${YELLOW}Running Component lifecycle state machine tests...${NC}"

if $coverage; then
  echo -e "${YELLOW}Generating coverage report...${NC}"
  mvn test jacoco:report $mvn_opts
else
  mvn test $mvn_opts
fi

# Check if tests passed
if [ $? -eq 0 ]; then
  echo -e "${GREEN}All tests passed successfully!${NC}"
  
  # Point to the test report
  if $coverage; then
    echo -e "${YELLOW}Coverage report available at:${NC}"
    echo -e "${GREEN}$(pwd)/target/site/jacoco/index.html${NC}"
  fi
  
  echo -e "${YELLOW}Test report available at:${NC}"
  echo -e "${GREEN}/home/emumford/NativeLinuxProjects/Samstraumr/docs/test-reports/lifecycle-test/component-lifecycle-test-report.md${NC}"
  
  exit 0
else
  echo -e "${RED}Tests failed. Please check the output for details.${NC}"
  exit 1
fi