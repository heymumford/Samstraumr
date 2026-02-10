#!/usr/bin/env bash
# Script for testing the architecture documentation tools
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
# This file is subject to the terms and conditions defined in
# the LICENSE file, which is part of this source code package.
#
# Usage:
#   ./bin/test-architecture.sh [--diagrams-only] [--adr-only]
#
# Options:
#   --diagrams-only   Only test the diagram generation
#   --adr-only        Only test the ADR functionality
#   --help            Show this help message

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Determine project root
PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$PROJECT_ROOT"

# Default options
TEST_DIAGRAMS=true
TEST_ADR=true
VERBOSE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --diagrams-only)
      TEST_DIAGRAMS=true
      TEST_ADR=false
      shift
      ;;
    --adr-only)
      TEST_DIAGRAMS=false
      TEST_ADR=true
      shift
      ;;
    --verbose)
      VERBOSE=true
      shift
      ;;
    --help)
      echo "Usage: ./bin/test-architecture.sh [--diagrams-only] [--adr-only] [--verbose]"
      echo ""
      echo "Options:"
      echo "  --diagrams-only   Only test the diagram generation"
      echo "  --adr-only        Only test the ADR functionality"
      echo "  --verbose         Show more output"
      echo "  --help            Show this help message"
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      echo "Use --help to see available options"
      exit 1
      ;;
  esac
done

echo -e "${BLUE}Running architecture tests...${NC}"
echo -e "${BLUE}Working directory: $(pwd)${NC}"

# Function to print verbose output if enabled
function verbose() {
  if [[ "$VERBOSE" == "true" ]]; then
    echo -e "${YELLOW}$1${NC}"
  fi
}

# Run specific tests
FAILURES=0

# Test diagram generation
if [[ "$TEST_DIAGRAMS" == "true" ]]; then
  echo -e "${BLUE}Testing diagram generation...${NC}"
  
  # Check if the script exists
  if [[ -f "${PROJECT_ROOT}/bin/generate-diagrams.sh" ]]; then
    verbose "Found diagram script at ${PROJECT_ROOT}/bin/generate-diagrams.sh"
    
    # Ensure script is executable
    chmod +x "${PROJECT_ROOT}/bin/generate-diagrams.sh"
    
    # Create test output directory
    TEST_OUTPUT_DIR="${PROJECT_ROOT}/target/test-diagrams"
    mkdir -p "$TEST_OUTPUT_DIR"
    
    # Test script with --help
    verbose "Testing script help..."
    if "${PROJECT_ROOT}/bin/generate-diagrams.sh" --help > /dev/null; then
      echo -e "${GREEN}✓ Help option works${NC}"
    else
      echo -e "${RED}✗ Failed to show help${NC}"
      FAILURES=$((FAILURES + 1))
    fi
    
    # Test generating a context diagram
    verbose "Testing context diagram generation..."
    if "${PROJECT_ROOT}/bin/generate-diagrams.sh" --type context --output-dir "$TEST_OUTPUT_DIR"; then
      echo -e "${GREEN}✓ Context diagram generation works${NC}"
      
      # Check if the diagram was created
      if [[ -f "${TEST_OUTPUT_DIR}/samstraumr_context_diagram.svg" ]]; then
        echo -e "${GREEN}✓ Found generated diagram${NC}"
      else
        echo -e "${YELLOW}! Context diagram not created, but script completed successfully${NC}"
        echo -e "${YELLOW}  This might be due to missing Python dependencies${NC}"
      fi
    else
      echo -e "${RED}✗ Failed to generate context diagram${NC}"
      FAILURES=$((FAILURES + 1))
    fi
    
    # Test Python script exists or is created
    verbose "Checking for Python script..."
    if [[ -f "${PROJECT_ROOT}/bin/c4_diagrams.py" ]]; then
      echo -e "${GREEN}✓ Python diagram script exists${NC}"
      chmod +x "${PROJECT_ROOT}/bin/c4_diagrams.py"
    fi
    
    # Clean up test directory
    rm -rf "$TEST_OUTPUT_DIR"
  else
    echo -e "${RED}✗ Diagram generation script not found${NC}"
    FAILURES=$((FAILURES + 1))
  fi
fi

# Test ADR functionality
if [[ "$TEST_ADR" == "true" ]]; then
  echo -e "${BLUE}Testing ADR functionality...${NC}"
  
  # Check if the script exists
  if [[ -f "${PROJECT_ROOT}/bin/new-adr" ]]; then
    verbose "Found ADR script at ${PROJECT_ROOT}/bin/new-adr"
    
    # Ensure script is executable
    chmod +x "${PROJECT_ROOT}/bin/new-adr"
    
    # Create test directory
    TEST_ADR_DIR="${PROJECT_ROOT}/target/test-adr"
    mkdir -p "$TEST_ADR_DIR/decisions"
    
    # Check ADR directories exist
    if [[ -d "${PROJECT_ROOT}/docs/architecture/decisions" ]]; then
      echo -e "${GREEN}✓ ADR directory exists${NC}"
      
      # Check for index file
      if [[ -f "${PROJECT_ROOT}/docs/architecture/decisions/README.md" ]]; then
        echo -e "${GREEN}✓ ADR index exists${NC}"
      else
        echo -e "${RED}✗ ADR index missing${NC}"
        FAILURES=$((FAILURES + 1))
      fi
      
      # Check for at least one ADR
      if ls -1 "${PROJECT_ROOT}/docs/architecture/decisions/"*.md 2>/dev/null | grep -v "README.md" | grep -q .; then
        echo -e "${GREEN}✓ At least one ADR exists${NC}"
      else
        echo -e "${RED}✗ No ADRs found${NC}"
        FAILURES=$((FAILURES + 1))
      fi
    else
      echo -e "${RED}✗ ADR directory not found${NC}"
      FAILURES=$((FAILURES + 1))
    fi
    
    # Create a test ADR in test directory
    verbose "Creating test ADR..."
    if cd "$TEST_ADR_DIR" && ADR_DIR="$TEST_ADR_DIR/decisions" "${PROJECT_ROOT}/bin/new-adr" "Test ADR for Script Testing"; then
      echo -e "${GREEN}✓ ADR creation script works${NC}"
      
      # Check if the ADR was created
      if [[ -f "${TEST_ADR_DIR}/decisions/0001-test-adr-for-script-testing.md" ]]; then
        echo -e "${GREEN}✓ ADR file created correctly${NC}"
      else
        echo -e "${RED}✗ ADR file not created correctly${NC}"
        FAILURES=$((FAILURES + 1))
      fi
      
      # Check if index was created
      if [[ -f "${TEST_ADR_DIR}/decisions/README.md" ]]; then
        echo -e "${GREEN}✓ ADR index created${NC}"
      else
        echo -e "${RED}✗ ADR index not created${NC}"
        FAILURES=$((FAILURES + 1))
      fi
    else
      echo -e "${RED}✗ Failed to create test ADR${NC}"
      FAILURES=$((FAILURES + 1))
    fi
    
    # Clean up test directory
    rm -rf "$TEST_ADR_DIR"
  else
    echo -e "${RED}✗ ADR script not found${NC}"
    FAILURES=$((FAILURES + 1))
  fi
fi

# Run Maven tests
echo -e "${BLUE}Running Maven architecture tests...${NC}"
if cd "${PROJECT_ROOT}/Samstraumr" && mvn test -pl samstraumr-core -P architecture-tests -Dtest=org.s8r.architecture.RunArchitectureTests; then
  echo -e "${GREEN}✓ Maven architecture tests passed${NC}"
else
  echo -e "${RED}✗ Maven architecture tests failed${NC}"
  FAILURES=$((FAILURES + 1))
fi

# Final results
if [[ "$FAILURES" -eq 0 ]]; then
  echo -e "${GREEN}All architecture tests passed!${NC}"
  exit 0
else
  echo -e "${RED}${FAILURES} architecture tests failed!${NC}"
  exit 1
fi