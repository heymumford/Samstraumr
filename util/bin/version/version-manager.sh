#!/bin/bash
#==============================================================================
# Filename: version-manager.sh
# Description: Comprehensive version management tool for Samstraumr
# Author: Original author (refactored by Claude)
# Created: 2025-04-03
# Updated: 2025-04-03
#==============================================================================
# Usage: ./version-manager.sh <command> [options]
#
# Commands:
#   get                  Show current version information
#   export               Output only the current version (for scripts)
#   bump <type>          Bump version (type: major, minor, patch)
#   set <version>        Set a specific version (format: x.y.z)
#   verify               Verify that version and tag are in sync
#   fix-tag              Create a git tag matching the current version
#   test <type>          Bump version, run tests, then commit and tag
#   history              Show version history
#
# Options:
#   -h, --help           Display this help message
#   -v, --verbose        Enable verbose output
#   --no-commit          Don't automatically commit the version change
#   --skip-tests         Skip running tests (for test command only)
#   --skip-quality       Skip quality checks (for test command only)
#   --push               Push changes to remote (for test command only)
#
# Examples:
#   ./version-manager.sh get                 # Show current version
#   ./version-manager.sh bump patch          # Bump patch version
#   ./version-manager.sh set 1.2.3           # Set version to 1.2.3
#   ./version-manager.sh test patch          # Bump patch, test, commit, tag
#==============================================================================

# Determine script directory and load libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"

# Source shared libraries
source "${PROJECT_ROOT}/util/lib/common.sh"
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function show_help() {
  local script_name="${BASH_SOURCE[0]}"
  local description="Comprehensive version management tool for Samstraumr"
  
  local options=$(cat <<EOF
Commands:
  get                  Show current version information
  export               Output only the current version (for scripts)
  bump <type>          Bump version (type: major, minor, patch)
  set <version>        Set a specific version (format: x.y.z)
  verify               Verify that version and tag are in sync
  fix-tag              Create a git tag matching the current version
  test <type>          Bump version, run tests, then commit and tag
  history              Show version history

Options:
  -h, --help           Display this help message
  -v, --verbose        Enable verbose output
  --no-commit          Don't automatically commit the version change
  --skip-tests         Skip running tests (for test command only)
  --skip-quality       Skip quality checks (for test command only)
  --push               Push changes to remote (for test command only)
EOF
)

  local examples=$(cat <<EOF
  ./version-manager.sh get                 # Show current version
  ./version-manager.sh bump patch          # Bump patch version
  ./version-manager.sh set 1.2.3           # Set version to 1.2.3
  ./version-manager.sh test patch          # Bump patch, test, commit, tag
EOF
)

  echo -e "${COLOR_BOLD}$(basename "$script_name")${COLOR_RESET} - $description"
  echo ""
  echo -e "${COLOR_BOLD}USAGE:${COLOR_RESET}"
  echo "  $(basename "$script_name") <command> [options]"
  echo ""
  echo -e "$options"
  echo ""
  echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
  echo -e "$examples"
  echo ""
}

function parse_arguments() {
  # Default values
  COMMAND=""
  BUMP_TYPE=""
  NEW_VERSION=""
  AUTO_COMMIT=true
  SKIP_TESTS=false
  SKIP_QUALITY=false
  PUSH_CHANGES=false
  
  # Parse common arguments first
  parse_common_args "$@"
  if [ $? -eq 1 ]; then
    show_help
    exit 0
  fi
  
  # First argument should be the command
  if [ $# -eq 0 ]; then
    show_help
    exit 0
  fi
  
  COMMAND="$1"
  shift
  
  # Handle command-specific arguments
  case "$COMMAND" in
    bump)
      if [ $# -gt 0 ]; then
        BUMP_TYPE="$1"
        shift
      else
        print_error "Missing bump type (major, minor, patch)"
        show_help
        exit 1
      fi
      ;;
    set)
      if [ $# -gt 0 ]; then
        NEW_VERSION="$1"
        shift
      else
        print_error "Missing version number to set"
        show_help
        exit 1
      fi
      ;;
    test)
      if [ $# -gt 0 ] && [[ "$1" =~ ^(major|minor|patch)$ ]]; then
        BUMP_TYPE="$1"
        shift
      else
        BUMP_TYPE="patch"  # Default to patch
      fi
      ;;
  esac
  
  # Parse remaining options
  while [ $# -gt 0 ]; do
    case "$1" in
      -h|--help)
        show_help
        exit 0
        ;;
      -v|--verbose)
        # Already handled by parse_common_args
        shift
        ;;
      --no-commit)
        AUTO_COMMIT=false
        shift
        ;;
      --skip-tests)
        SKIP_TESTS=true
        shift
        ;;
      --skip-quality)
        SKIP_QUALITY=true
        shift
        ;;
      --push)
        PUSH_CHANGES=true
        shift
        ;;
      *)
        print_error "Unknown option: $1"
        show_help
        exit 1
        ;;
    esac
  done
  
  # Debug output
  if [ "$VERBOSE" = "true" ]; then
    print_debug "COMMAND: $COMMAND"
    print_debug "BUMP_TYPE: $BUMP_TYPE"
    print_debug "NEW_VERSION: $NEW_VERSION"
    print_debug "AUTO_COMMIT: $AUTO_COMMIT"
    print_debug "SKIP_TESTS: $SKIP_TESTS"
    print_debug "SKIP_QUALITY: $SKIP_QUALITY"
    print_debug "PUSH_CHANGES: $PUSH_CHANGES"
  fi
}

function show_version_info() {
  local version=$(get_current_version)
  local updated=$(get_last_updated_date)
  
  print_header "Samstraumr Version Information"
  echo -e "Version:     ${COLOR_GREEN}$version${COLOR_RESET}"
  echo -e "Updated:     ${COLOR_CYAN}$updated${COLOR_RESET}"
  
  # Check if version matches git tag
  if check_version_tag_alignment; then
    print_success "Version and git tag are in sync"
  else
    print_warning "Version and git tag are not in sync"
    print_info "Run 'version-manager.sh fix-tag' to create the missing tag"
  fi
}

function execute_version_bump() {
  local bump_type="$1"
  local auto_commit="$2"
  
  # Validate bump type
  if [[ ! "$bump_type" =~ ^(major|minor|patch)$ ]]; then
    print_error "Invalid bump type: $bump_type. Must be major, minor, or patch."
    exit 1
  fi
  
  # Get current version and calculate new version
  local current_version=$(get_current_version)
  local new_version=$(calculate_new_version "$bump_type")
  
  # Update version in files
  update_version_in_files "$current_version" "$new_version"
  
  # Commit changes if requested
  if [ "$auto_commit" = "true" ]; then
    commit_version_changes "$new_version" "$bump_type"
  else
    print_info "Changes not committed (--no-commit option used)"
    print_info "To commit changes manually, use:"
    echo -e "  ${COLOR_YELLOW}git add -u${COLOR_RESET}"
    echo -e "  ${COLOR_YELLOW}git commit -m \"Bump $bump_type version to $new_version\"${COLOR_RESET}"
    echo -e "  ${COLOR_YELLOW}git tag -a \"v$new_version\" -m \"Version $new_version\"${COLOR_RESET}"
  fi
}

function execute_version_set() {
  local new_version="$1"
  local auto_commit="$2"
  
  # Validate version format
  if ! [[ $new_version =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_error "Invalid version format: $new_version. Must be in format x.y.z"
    exit 1
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  
  # Update version in files
  update_version_in_files "$current_version" "$new_version"
  
  # Commit changes if requested
  if [ "$auto_commit" = "true" ]; then
    commit_version_changes "$new_version" "set"
  else
    print_info "Changes not committed (--no-commit option used)"
    print_info "To commit changes manually, use:"
    echo -e "  ${COLOR_YELLOW}git add -u${COLOR_RESET}"
    echo -e "  ${COLOR_YELLOW}git commit -m \"Set version to $new_version\"${COLOR_RESET}"
    echo -e "  ${COLOR_YELLOW}git tag -a \"v$new_version\" -m \"Version $new_version\"${COLOR_RESET}"
  fi
}

function execute_test_version_bump() {
  local bump_type="$1"
  local skip_tests="$2"
  local skip_quality="$3"
  local push_changes="$4"
  
  print_header "Version Bump with Testing"
  
  # Bump the version
  execute_version_bump "$bump_type" false
  
  # Run tests if not skipped
  if [ "$skip_tests" = "false" ]; then
    print_section "Running ATL Tests"
    if [ -f "$PROJECT_ROOT/util/bin/test/run-atl-tests.sh" ]; then
      "$PROJECT_ROOT/util/bin/test/run-atl-tests.sh"
      if [ $? -ne 0 ]; then
        print_error "Tests failed - not committing version change"
        return 1
      fi
      print_success "All tests passed"
    else
      print_warning "Test script not found - skipping tests"
    fi
  fi
  
  # Commit and tag
  commit_version_changes "$(get_current_version)" "$bump_type"
  
  # Push changes if requested
  if [ "$push_changes" = "true" ]; then
    print_section "Pushing Changes to Remote"
    print_info "Pushing commits and tags to remote repository..."
    
    # Push commits
    git push
    
    # Push tag
    git push origin "v$(get_current_version)"
    
    print_success "All changes and tags have been pushed to remote"
  else
    print_info "To push the changes and tag to the remote repository, run:"
    echo -e "  ${COLOR_YELLOW}git push && git push origin v$(get_current_version)${COLOR_RESET}"
  fi
}

function process_command() {
  case "$COMMAND" in
    get)
      show_version_info
      ;;
    
    export|echo)
      echo "$(get_current_version)"
      ;;
    
    verify)
      check_version_tag_alignment
      ;;
    
    fix-tag)
      local current_version=$(get_current_version)
      create_git_tag_for_version "$current_version"
      ;;
    
    test)
      execute_test_version_bump "$BUMP_TYPE" "$SKIP_TESTS" "$SKIP_QUALITY" "$PUSH_CHANGES"
      ;;
    
    bump)
      execute_version_bump "$BUMP_TYPE" "$AUTO_COMMIT"
      ;;
    
    set)
      execute_version_set "$NEW_VERSION" "$AUTO_COMMIT"
      ;;
    
    history)
      show_version_history
      ;;
    
    *)
      print_error "Unknown command: $COMMAND"
      show_help
      exit 1
      ;;
  esac
}

function main() {
  # Parse command line arguments
  parse_arguments "$@"
  
  # Process the command
  process_command
  
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------
main "$@"
exit $?