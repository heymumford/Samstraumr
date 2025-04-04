#!/bin/bash
#==============================================================================
# Filename: test-commands.sh
# Description: Test integration commands for version management
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../../" && pwd)"

# Source the version library
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

# Source other command modules
source "${SCRIPT_DIR}/set-commands.sh"
source "${SCRIPT_DIR}/git-commands.sh"

#------------------------------------------------------------------------------
# Help Functions
#------------------------------------------------------------------------------

function show_help() {
  if type print_header &>/dev/null; then
    print_header "Test Integration Commands"
  else
    echo "=== Test Integration Commands ==="
  fi
  echo ""
  echo "COMMANDS:"
  echo "  test <type>          Bump version, run tests, then commit and tag"
  echo "    major              Increment major version (breaking changes)"
  echo "    minor              Increment minor version (new features)"
  echo "    patch              Increment patch version (bug fixes)"
  echo ""
  echo "OPTIONS:"
  echo "  --skip-tests         Skip running tests"
  echo "  --skip-quality       Skip quality checks during testing"
  echo "  --push               Push changes to remote after success"
  echo ""
  echo "WORKFLOW:"
  echo "  1. Increments version based on component type"
  echo "  2. Runs project tests (unless --skip-tests specified)"
  echo "  3. If tests pass, commits changes and creates tag"
  echo "  4. If tests fail, reverts version change"
  echo "  5. Optionally pushes changes to remote (with --push)"
  echo ""
  echo "EXAMPLES:"
  echo "  test patch           # Bump patch version with tests"
  echo "  test minor --push    # Bump minor version and push changes"
  echo "  test major --skip-tests # Bump major without running tests"
}

#------------------------------------------------------------------------------
# Test Integration Commands
#------------------------------------------------------------------------------

# Run tests for the project
# Usage: cmd_run_tests [skip_quality]
function cmd_run_tests() {
  local skip_quality="${1:-false}"
  
  version_header "Running Tests"
  
  # Build command
  local test_cmd="${TEST_COMMAND:-${PROJECT_ROOT}/util/bin/test/run-tests.sh all}"
  
  if [ "$skip_quality" = "true" ]; then
    test_cmd="$test_cmd --skip-quality"
  fi
  
  # Run tests
  eval "$test_cmd"
  
  if [ $? -ne 0 ]; then
    version_error "Tests failed" $ERR_TEST_FAILURE
    return $ERR_TEST_FAILURE
  fi
  
  version_success "Tests passed"
  return 0
}

# Test version bump workflow (bump, test, commit, tag)
# Usage: cmd_test_version_bump "component" [skip_tests] [skip_quality] [push]
function cmd_test_version_bump() {
  local component="$1"
  local skip_tests="${2:-false}"
  local skip_quality="${3:-false}"
  local push="${4:-false}"
  
  if [ -z "$component" ]; then
    version_error "Missing version component (major, minor, or patch)" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  if ! validate_version "$current_version"; then
    version_error "Current version is not valid: $current_version" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Calculate new version
  local new_version=""
  # Split version into components
  IFS='.' read -r major minor patch <<< "$current_version"
  
  # Bump appropriate component
  case "$component" in
    major)
      new_version="$((major + 1)).0.0"
      ;;
    minor)
      new_version="${major}.$((minor + 1)).0"
      ;;
    patch)
      new_version="${major}.${minor}.$((patch + 1))"
      ;;
    *)
      version_error "Invalid version component: $component (use major, minor, or patch)" $ERR_INVALID_VERSION
      return $ERR_INVALID_VERSION
      ;;
  esac
  
  # Update version
  cmd_set_version "$new_version" true
  
  # Run tests if not skipped
  if [ "$skip_tests" != "true" ]; then
    cmd_run_tests "$skip_quality"
    if [ $? -ne 0 ]; then
      # Revert version change
      cmd_set_version "$current_version" true
      version_error "Tests failed. Version reverted to $current_version" $ERR_TEST_FAILURE
      return $ERR_TEST_FAILURE
    fi
  fi
  
  # Create commit and tag
  cmd_commit_version_change "$current_version" "$new_version" "$component"
  cmd_create_version_tag "$new_version"
  
  # Push changes if requested
  if [ "$push" = "true" ]; then
    cmd_push_version_changes
  fi
  
  return 0
}

# Revert version change if tests fail
# Usage: cmd_revert_on_failure "old_version" "result_code"
function cmd_revert_on_failure() {
  local old_version="$1"
  local result_code="$2"
  
  if [ "$result_code" -ne 0 ]; then
    version_warning "Operation failed. Reverting to version $old_version"
    cmd_set_version "$old_version" true
    return 0
  fi
  
  return 0
}

# Export the functions
export -f cmd_run_tests
export -f cmd_test_version_bump
export -f cmd_revert_on_failure