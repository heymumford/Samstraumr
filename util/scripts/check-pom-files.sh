#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script for checking and fixing POM files using XML tools
# Utilizes xmlstarlet for precise XML manipulation

# Navigate to script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Source required libraries
source "$ROOT_DIR/util/lib/common.sh" || { echo "Failed to source common.sh"; exit 1; }
source "$ROOT_DIR/util/lib/xml-lib.sh" || { echo "Failed to source xml-lib.sh"; exit 1; }

# Set default action
ACTION="validate"
TARGET_PATH="$ROOT_DIR"
VERBOSE=0

# Print usage information
print_usage() {
  cat << EOF
Usage: $(basename $0) [OPTIONS] [PATH]

Check and fix POM files in the specified path (defaults to repository root)

Options:
  -a, --action ACTION   Action to perform: validate, fix, check (default: validate)
  -v, --verbose         Display verbose output
  -h, --help            Display this help message

Examples:
  $(basename $0)                         # Validate all POM files in the repository
  $(basename $0) --action fix            # Fix issues in all POM files
  $(basename $0) ./modules/samstraumr-core  # Validate POM files in the samstraumr-core module
  $(basename $0) --action check ./modules/samstraumr-core/pom.xml  # Check a specific POM file

EOF
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -a|--action)
      ACTION="$2"
      shift 2
      ;;
    -v|--verbose)
      VERBOSE=1
      shift
      ;;
    -h|--help)
      print_usage
      exit 0
      ;;
    *)
      TARGET_PATH="$1"
      shift
      ;;
  esac
done

# Set verbosity level
if [ $VERBOSE -eq 1 ]; then
  set -x
fi

# Validate action
case "$ACTION" in
  validate|fix|check)
    # Valid action
    ;;
  *)
    echo_error "Invalid action: $ACTION"
    print_usage
    exit 1
    ;;
esac

# Check if target path exists
if [ ! -e "$TARGET_PATH" ]; then
  echo_error "Path does not exist: $TARGET_PATH"
  exit 1
fi

echo_info "Performing '$ACTION' on POM files in '$TARGET_PATH'"

# Execute the requested action
if [ -f "$TARGET_PATH" ]; then
  # Single file
  case "$ACTION" in
    validate)
      pom_validate_file "$TARGET_PATH"
      ;;
    fix)
      pom_fix_file "$TARGET_PATH"
      ;;
    check)
      pom_check_issues "$TARGET_PATH"
      ;;
  esac
else
  # Directory
  pom_process_directory "$TARGET_PATH" "$ACTION"
fi

# Check exit code
exit_code=$?
if [ $exit_code -eq 0 ]; then
  case "$ACTION" in
    validate)
      echo_success "All POM files are valid"
      ;;
    fix)
      echo_success "Fixed issues in POM files"
      ;;
    check)
      echo_success "Checked POM files for issues"
      ;;
  esac
else
  case "$ACTION" in
    validate)
      echo_error "Some POM files have validation errors"
      ;;
    fix)
      echo_error "Failed to fix some POM files"
      ;;
    check)
      echo_error "Found issues in some POM files"
      ;;
  esac
fi

exit $exit_code