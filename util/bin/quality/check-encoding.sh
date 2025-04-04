#!/bin/bash
#==============================================================================
# Filename: check-encoding.sh
# Description: Check and fix file encodings and line endings
#==============================================================================
# Usage: ./check-encoding.sh [options] [path]
#
# Options:
#   -h, --help          Display this help message
#   -v, --verbose       Enable verbose output and show all files
#   -f, --fix           Fix issues automatically
#   -p, --path <path>   Specify the path to check (default: project root)
#
# Examples:
#   ./check-encoding.sh                  # Check all files in project
#   ./check-encoding.sh -v               # Check all files with verbose output
#   ./check-encoding.sh -f               # Fix encoding issues automatically
#   ./check-encoding.sh -p src/main/java # Check only files in src/main/java
#==============================================================================

# Determine script directory and load libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"
source "${PROJECT_ROOT}/util/lib/quality-lib.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function show_help() {
  local script_name="${BASH_SOURCE[0]}"
  local description="Check and fix file encodings and line endings"
  
  local options=$(cat <<EOF
  -h, --help          Display this help message
  -v, --verbose       Enable verbose output and show all files
  -f, --fix           Fix issues automatically
  -p, --path <path>   Specify the path to check (default: project root)
EOF
)

  local examples=$(cat <<EOF
  ./check-encoding.sh                  # Check all files in project
  ./check-encoding.sh -v               # Check all files with verbose output
  ./check-encoding.sh -f               # Fix encoding issues automatically
  ./check-encoding.sh -p src/main/java # Check only files in src/main/java
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
}

function parse_arguments() {
  # Default values
  CHECK_PATH="${PROJECT_ROOT}"
  FIX_ISSUES=false
  
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
      -f|--fix)
        FIX_ISSUES=true
        shift
        ;;
      -p|--path)
        CHECK_PATH="$2"
        shift 2
        ;;
      *)
        # Assume this is a path
        CHECK_PATH="$1"
        shift
        ;;
    esac
  done
  
  # Make sure CHECK_PATH is an absolute path
  if [[ "$CHECK_PATH" != /* ]]; then
    CHECK_PATH="${PROJECT_ROOT}/${CHECK_PATH}"
  fi
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "CHECK_PATH: $CHECK_PATH"
    print_debug "FIX_ISSUES: $FIX_ISSUES"
  fi
}

function check_requirements() {
  # Check for required commands
  local required_commands=("file" "dos2unix")
  local missing_commands=()
  
  for cmd in "${required_commands[@]}"; do
    if ! command -v "$cmd" &> /dev/null; then
      missing_commands+=("$cmd")
    fi
  done
  
  if [ ${#missing_commands[@]} -gt 0 ]; then
    print_error "Missing required commands: ${missing_commands[*]}"
    print_info "Please install them using your package manager:"
    echo "  Ubuntu/Debian: sudo apt-get install file dos2unix"
    echo "  RHEL/CentOS: sudo yum install file dos2unix"
    echo "  macOS: brew install file dos2unix"
    return 1
  fi
  
  return 0
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Check requirements
  if ! check_requirements; then
    exit 1
  fi
  
  # Check file encodings
  check_file_encoding "$CHECK_PATH" "$VERBOSE" "$FIX_ISSUES"
  
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?