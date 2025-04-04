#!/bin/bash
# Run tests for the biological lifecycle model phases
#
# Usage: 
#   ./util/test-bio-lifecycle.sh <phase> [options]
#
# Arguments:
#   phase:    The biological phase to test (conception, embryonic, infancy, childhood)
#             Special values: all (run all phases), list (list available phases)
#
# Options:
#   --initiative=<name>    Run tests for specific initiative (substrate, structural, memory, functional)
#   --type=<type>          Run tests by type (positive, negative)
#   --atl                  Run only Above The Line tests
#   --btl                  Run only Below The Line tests
#   --verbose              Enable verbose output
#   --help                 Show this help message
#
# Examples:
#   ./util/test-bio-lifecycle.sh conception          # Run conception phase tests
#   ./util/test-bio-lifecycle.sh all --atl           # Run all ATL tests for all phases
#   ./util/test-bio-lifecycle.sh infancy --initiative=memory  # Run memory identity tests for infancy phase

# Define colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Root directory
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Default values
VERBOSE=false
ATL=""
BTL=""
INITIATIVE=""
TYPE=""
PHASE=""
MVN_CMD="mvn"

# Display help
show_help() {
  echo -e "${BLUE}Biological Lifecycle Test Runner${NC}"
  echo
  echo "This script runs tests organized by the biological lifecycle model phases."
  echo
  echo -e "${YELLOW}Usage:${NC}"
  echo "  ./util/test-bio-lifecycle.sh <phase> [options]"
  echo
  echo -e "${YELLOW}Arguments:${NC}"
  echo "  phase:    The biological phase to test (conception, embryonic, infancy, childhood)"
  echo "            Special values: all (run all phases), list (list available phases)"
  echo
  echo -e "${YELLOW}Options:${NC}"
  echo "  --initiative=<name>    Run tests for specific initiative (substrate, structural, memory, functional)"
  echo "  --type=<type>          Run tests by type (positive, negative)"
  echo "  --atl                  Run only Above The Line tests"
  echo "  --btl                  Run only Below The Line tests"
  echo "  --verbose              Enable verbose output"
  echo "  --help                 Show this help message"
  echo
  echo -e "${YELLOW}Examples:${NC}"
  echo "  ./util/test-bio-lifecycle.sh conception          # Run conception phase tests"
  echo "  ./util/test-bio-lifecycle.sh all --atl           # Run all ATL tests for all phases"
  echo "  ./util/test-bio-lifecycle.sh infancy --initiative=memory  # Run memory identity tests for infancy phase"
  echo
}

# List available phases
list_phases() {
  echo -e "${BLUE}Available Biological Lifecycle Phases:${NC}"
  echo -e "${GREEN}conception${NC} - Initial creation phase (Substrate Identity)"
  echo -e "${GREEN}embryonic${NC}  - Structural formation phase (Structural Identity)"
  echo -e "${GREEN}infancy${NC}    - Early capability development phase (Memory Identity)"
  echo -e "${GREEN}childhood${NC}  - Functional development phase (Functional Identity)"
  echo 
  echo -e "${YELLOW}Upcoming Phases (Not Yet Implemented):${NC}"
  echo -e "adolescence - Rapid change phase (Adaptive Identity)"
  echo -e "adulthood   - Mature functionality phase (Cognitive Identity)"
  echo -e "maturity    - Optimization phase (Specialized Identity)"
  echo -e "senescence  - Graceful degradation phase (Resilience Identity)"
  echo -e "termination - Shutdown/cleanup phase (Closure Identity)"
  echo -e "legacy      - Knowledge preservation phase (Heritage Identity)"
  echo
}

# Parse command line arguments
parse_args() {
  if [ $# -eq 0 ]; then
    show_help
    exit 1
  fi

  # First argument is the phase
  PHASE=$1
  shift

  # Handle special commands
  if [ "$PHASE" = "help" ] || [ "$PHASE" = "--help" ]; then
    show_help
    exit 0
  fi

  if [ "$PHASE" = "list" ]; then
    list_phases
    exit 0
  fi

  # Validate phase
  if [ "$PHASE" != "all" ] && [ "$PHASE" != "conception" ] && [ "$PHASE" != "embryonic" ] &&
     [ "$PHASE" != "infancy" ] && [ "$PHASE" != "childhood" ]; then
    echo -e "${RED}Error: Invalid phase '$PHASE'${NC}"
    echo "Run './util/test-bio-lifecycle.sh list' to see available phases"
    exit 1
  fi

  # Parse the rest of the arguments
  for arg in "$@"; do
    case $arg in
      --atl)
        ATL="ATL"
        shift
        ;;
      --btl)
        BTL="BTL"
        shift
        ;;
      --verbose)
        VERBOSE=true
        shift
        ;;
      --initiative=*)
        INITIATIVE="${arg#*=}"
        shift
        ;;
      --type=*)
        TYPE="${arg#*=}"
        shift
        ;;
      --help)
        show_help
        exit 0
        ;;
      *)
        echo -e "${RED}Error: Unknown option '$arg'${NC}"
        show_help
        exit 1
        ;;
    esac
  done
}

# Build the Maven command
build_command() {
  local tags=""
  local profile=""
  
  # Add phase tag if not "all"
  if [ "$PHASE" != "all" ]; then
    case $PHASE in
      conception)
        profile="conception-tests"
        tags="@Conception"
        ;;
      embryonic)
        profile="embryonic-tests"
        tags="@Embryonic"
        ;;
      infancy)
        profile="infancy-tests"
        tags="@Infancy"
        ;;
      childhood)
        profile="childhood-tests"
        tags="@Childhood"
        ;;
    esac
  else
    tags="@Conception or @Embryonic or @Infancy or @Childhood"
  fi

  # Add initiative if specified
  if [ -n "$INITIATIVE" ]; then
    local initiative_tag=""
    case $INITIATIVE in
      substrate)
        initiative_tag="SubstrateIdentity"
        ;;
      structural)
        initiative_tag="StructuralIdentity"
        ;;
      memory)
        initiative_tag="MemoryIdentity"
        ;;
      functional)
        initiative_tag="FunctionalIdentity"
        ;;
      *)
        echo -e "${RED}Error: Invalid initiative '$INITIATIVE'${NC}"
        echo "Valid initiatives: substrate, structural, memory, functional"
        exit 1
        ;;
    esac
    
    if [ -n "$tags" ]; then
      tags="$tags and @$initiative_tag"
    else
      tags="@$initiative_tag"
    fi
  fi

  # Add test type if specified
  if [ -n "$TYPE" ]; then
    local type_tag=""
    case $TYPE in
      positive)
        type_tag="Positive"
        ;;
      negative)
        type_tag="Negative"
        ;;
      *)
        echo -e "${RED}Error: Invalid test type '$TYPE'${NC}"
        echo "Valid types: positive, negative"
        exit 1
        ;;
    esac
    
    if [ -n "$tags" ]; then
      tags="$tags and @$type_tag"
    else
      tags="@$type_tag"
    fi
  fi

  # Add ATL/BTL if specified
  if [ -n "$ATL" ]; then
    if [ -n "$tags" ]; then
      tags="$tags and @ATL"
    else
      tags="@ATL"
    fi
  fi
  
  if [ -n "$BTL" ]; then
    if [ -n "$tags" ]; then
      tags="$tags and @BTL"
    else
      tags="@BTL"
    fi
  fi

  # Construct the Maven command
  if [ -n "$profile" ]; then
    MVN_CMD="$MVN_CMD test -P $profile"
  else
    MVN_CMD="$MVN_CMD test"
  fi
  
  if [ -n "$tags" ]; then
    MVN_CMD="$MVN_CMD -Dcucumber.filter.tags=\"$tags\""
  fi

  if [ "$VERBOSE" = true ]; then
    MVN_CMD="$MVN_CMD -Dcucumber.plugin=\"pretty\""
  fi
}

# Execute the test command
run_tests() {
  if [ "$VERBOSE" = true ]; then
    echo -e "${BLUE}Executing: ${YELLOW}$MVN_CMD${NC}"
  fi
  
  echo -e "${GREEN}Running $PHASE phase tests...${NC}"
  eval $MVN_CMD
  
  local exit_code=$?
  if [ $exit_code -eq 0 ]; then
    echo -e "${GREEN}Tests completed successfully${NC}"
  else
    echo -e "${RED}Tests failed with exit code $exit_code${NC}"
    exit $exit_code
  fi
}

# Main function
main() {
  parse_args "$@"
  build_command
  run_tests
}

# Run the script
main "$@"