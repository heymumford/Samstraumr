#!/bin/bash
#==============================================================================
# Filename: update-java-headers.sh
# Description: Update file headers in Java source files
# Author: Original author (refactored by Claude)
# Created: 2025-04-03
# Updated: 2025-04-03
#==============================================================================
# Usage: ./update-java-headers.sh [options] [path]
#
# Options:
#   -h, --help          Display this help message
#   -v, --verbose       Enable verbose output
#   -t, --template <file> Use custom header template file
#
# Examples:
#   ./update-java-headers.sh                    # Update all Java files
#   ./update-java-headers.sh src/main/java      # Update Java files in specified path
#   ./update-java-headers.sh -t my-header.txt   # Use custom header template
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
  local description="Update file headers in Java source files"
  
  local options=$(cat <<EOF
  -h, --help          Display this help message
  -v, --verbose       Enable verbose output
  -t, --template <file> Use custom header template file
EOF
)

  local examples=$(cat <<EOF
  ./update-java-headers.sh                    # Update all Java files
  ./update-java-headers.sh src/main/java      # Update Java files in specified path
  ./update-java-headers.sh -t my-header.txt   # Use custom header template
EOF
)

  show_help_template "$script_name" "$description" "$options" "$examples"
}

function parse_arguments() {
  # Default values
  UPDATE_PATH="${SAMSTRAUMR_JAVA_MAIN}"
  TEMPLATE_FILE="${PROJECT_ROOT}/java-standard-header-template.txt"
  
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
      -t|--template)
        TEMPLATE_FILE="$2"
        shift 2
        ;;
      *)
        # Assume this is a path
        UPDATE_PATH="$1"
        shift
        ;;
    esac
  done
  
  # If template file doesn't exist, create a default one
  if [ ! -f "$TEMPLATE_FILE" ]; then
    print_warning "Template file not found: $TEMPLATE_FILE"
    print_info "Creating default template file"
    
    # Create default template
    cat > "$TEMPLATE_FILE" << 'EOF'
/*
 * FILENAME
 * 
 * Copyright (c) YEAR Samstraumr Project Contributors
 * 
 * This file is part of the Samstraumr Framework.
 *
 * Updated: DATE
 */
EOF
    
    print_success "Created default template at $TEMPLATE_FILE"
  fi
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "UPDATE_PATH: $UPDATE_PATH"
    print_debug "TEMPLATE_FILE: $TEMPLATE_FILE"
  fi
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Update Java file headers
  print_header "Updating Java File Headers"
  
  # Check if the path exists
  if [ ! -d "$UPDATE_PATH" ]; then
    print_error "Path does not exist or is not a directory: $UPDATE_PATH"
    return 1
  fi
  
  # Update headers
  update_file_headers "java" "$TEMPLATE_FILE" "$UPDATE_PATH"
  
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?