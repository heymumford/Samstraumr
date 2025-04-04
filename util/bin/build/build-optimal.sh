#!/bin/bash
#==============================================================================
# Filename: build-optimal.sh
# Description: Optimized Maven build script with flexible modes
# Author: Original author (refactored by Claude)
# Created: 2025-04-03
# Updated: 2025-04-03
#==============================================================================
# Usage: ./build-optimal.sh [options] [mode]
#
# Options:
#   -h, --help                Display this help message
#   -v, --verbose             Enable verbose output
#   -c, --clean               Clean before building
#   -p, --profile <profile>   Use specific Maven profile
#   --skip-quality            Skip quality checks
#
# Modes:
#   fast                      Fast build with quality checks skipped (default)
#   compile                   Compile only
#   test                      Compile and run tests
#   package                   Create JAR package
#   install                   Install to local repository
#
# Examples:
#   ./build-optimal.sh                     # Fast build
#   ./build-optimal.sh test                # Run tests
#   ./build-optimal.sh -c test             # Clean and run tests
#   ./build-optimal.sh -p atl-tests test   # Run tests with ATL profile
#==============================================================================

# Determine script directory and load libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"
source "${PROJECT_ROOT}/util/lib/build-lib.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function show_help() {
  local script_name="${BASH_SOURCE[0]}"
  local description="Optimized Maven build script with flexible modes"
  
  local options=$(cat <<EOF
  -h, --help                Display this help message
  -v, --verbose             Enable verbose output
  -c, --clean               Clean before building
  -p, --profile <profile>   Use specific Maven profile
  --skip-quality            Skip quality checks
EOF
)

  local examples=$(cat <<EOF
  ./build-optimal.sh                     # Fast build
  ./build-optimal.sh test                # Run tests
  ./build-optimal.sh -c test             # Clean and run tests
  ./build-optimal.sh -p atl-tests test   # Run tests with ATL profile
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
  
  # Show build modes
  echo -e "${COLOR_BOLD}Build Modes:${COLOR_RESET}"
  echo "  fast                      Fast build with quality checks skipped (default)"
  echo "  compile                   Compile only"
  echo "  test                      Compile and run tests"
  echo "  package                   Create JAR package"
  echo "  install                   Install to local repository"
  echo ""
}

function parse_arguments() {
  # Default values
  BUILD_MODE="fast"
  CLEAN_FLAG=false
  PROFILE="${SAMSTRAUMR_FAST_PROFILE}"
  SKIP_QUALITY=false
  ADDITIONAL_ARGS=""
  
  # Parse common arguments first
  parse_common_args "$@"
  if [ $? -eq 1 ]; then
    show_help
    exit 0
  fi
  
  # Parse remaining arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        show_help
        exit 0
        ;;
      -v|--verbose)
        # Already handled by parse_common_args
        shift
        ;;
      -c|--clean)
        CLEAN_FLAG=true
        shift
        ;;
      -p|--profile)
        PROFILE="$2"
        shift 2
        ;;
      --skip-quality)
        SKIP_QUALITY=true
        shift
        ;;
      fast|compile|test|package|install|clean)
        BUILD_MODE="$1"
        shift
        ;;
      *)
        ADDITIONAL_ARGS="$ADDITIONAL_ARGS $1"
        shift
        ;;
    esac
  done
  
  # Special handling for 'fast' mode
  if [ "$BUILD_MODE" = "fast" ]; then
    PROFILE="${SAMSTRAUMR_FAST_PROFILE}"
    SKIP_QUALITY=true
  fi
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "BUILD_MODE: $BUILD_MODE"
    print_debug "CLEAN_FLAG: $CLEAN_FLAG"
    print_debug "PROFILE: $PROFILE"
    print_debug "SKIP_QUALITY: $SKIP_QUALITY"
    print_debug "ADDITIONAL_ARGS: $ADDITIONAL_ARGS"
  fi
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Validate build mode
  BUILD_MODE=$(parse_build_mode "$BUILD_MODE")
  
  # Build the project
  build_project "$CLEAN_FLAG" "$BUILD_MODE" "$PROFILE" "$ADDITIONAL_ARGS" "$SKIP_QUALITY"
  
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?