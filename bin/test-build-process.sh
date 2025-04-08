#!/bin/bash
# 
# Test the Samstraumr build process
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

set -e

# Determine script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Add colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Parse arguments
SKIP_COMPILE=false
VERBOSE=false
TAG_FILTER="@Build"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-compile)
      SKIP_COMPILE=true
      shift
      ;;
    --verbose|-v)
      VERBOSE=true
      shift
      ;;
    --tag|-t)
      TAG_FILTER="$2"
      shift 2
      ;;
    --help|-h)
      echo "Usage: $0 [options]"
      echo
      echo "Options:"
      echo "  --skip-compile       Skip compilation step"
      echo "  --verbose, -v        Enable verbose output"
      echo "  --tag, -t TAG        Run tests with specific tag (default: @Build)"
      echo "  --help, -h           Show this help message"
      echo
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      exit 1
      ;;
  esac
done

# Navigate to project directory
cd "${PROJECT_ROOT}"

echo -e "${BLUE}=== Testing Samstraumr Build Process ===${NC}"
echo "Working directory: $(pwd)"

# Compile test classes first (if not skipped)
if [ "$SKIP_COMPILE" = false ]; then
  echo -e "${YELLOW}Compiling test classes...${NC}"
  
  MVN_ARGS=("compile" "test-compile" "-DskipTests")
  if [ "$VERBOSE" = true ]; then
    MVN_ARGS+=("-e")
  else
    MVN_ARGS+=("-q")
  fi
  
  mvn -pl modules/samstraumr-core "${MVN_ARGS[@]}"
fi

# Run the build process tests
echo -e "${YELLOW}Running build process tests...${NC}"

MVN_ARGS=("test" "-Dtest=BuildProcessTests" "-Dcucumber.filter.tags=\"${TAG_FILTER}\"")
if [ "$VERBOSE" = true ]; then
  MVN_ARGS+=("-e")
fi

mvn -pl modules/samstraumr-core "${MVN_ARGS[@]}"

# Check if tests passed
if [ $? -eq 0 ]; then
  echo -e "${GREEN}All build process tests passed!${NC}"
  echo -e "Test report is available at: ${BLUE}modules/samstraumr-core/target/cucumber-reports/build-process-tests.html${NC}"
  exit 0
else
  echo -e "${RED}Some tests failed. See above output for details.${NC}"
  exit 1
fi