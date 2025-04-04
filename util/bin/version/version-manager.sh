#!/bin/bash
#==============================================================================
# Filename: version-manager.sh
# Description: Comprehensive version management tool for Samstraumr
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
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"
VERSION_FILE="${PROJECT_ROOT}/Samstraumr/version.properties"
VERSION_PROPERTY_NAME="samstraumr.version"

# Source the version library for enhanced functionality
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

# Define color codes if not already defined (ensures script works both standalone and via s8r)
: ${COLOR_RED:='\033[0;31m'}
: ${COLOR_GREEN:='\033[0;32m'}
: ${COLOR_YELLOW:='\033[0;33m'}
: ${COLOR_BLUE:='\033[0;34m'}
: ${COLOR_RESET:='\033[0m'}
: ${COLOR_BOLD:='\033[1m'}

#------------------------------------------------------------------------------
# Utility Functions
#------------------------------------------------------------------------------

function print_header() {
  echo -e "${COLOR_BLUE}${COLOR_BOLD}$1${COLOR_RESET}" >&2
}

function print_success() {
  echo -e "${COLOR_GREEN}$1${COLOR_RESET}" >&2
}

function print_warning() {
  echo -e "${COLOR_YELLOW}$1${COLOR_RESET}" >&2
}

function print_error() {
  echo -e "${COLOR_RED}Error: $1${COLOR_RESET}" >&2
}

function print_info() {
  echo -e "${COLOR_BLUE}$1${COLOR_RESET}" >&2
}

function show_help() {
  echo -e "${COLOR_BOLD}Samstraumr Version Manager${COLOR_RESET}"
  echo ""
  echo -e "${COLOR_BOLD}USAGE:${COLOR_RESET}"
  echo "  ./version-manager.sh <command> [options]"
  echo ""
  echo -e "${COLOR_BOLD}COMMANDS:${COLOR_RESET}"
  echo "  get                  Show current version information"
  echo "  export               Output only the current version (for scripts)"
  echo "  bump <type>          Bump version (type: major, minor, patch)"
  echo "  set <version>        Set a specific version (format: x.y.z)"
  echo "  verify               Verify that version and tag are in sync"
  echo "  fix-tag              Create a git tag matching the current version"
  echo "  test <type>          Bump version, run tests, then commit and tag"
  echo "  history              Show version history"
  echo ""
  echo -e "${COLOR_BOLD}OPTIONS:${COLOR_RESET}"
  echo "  -h, --help           Display this help message"
  echo "  -v, --verbose        Enable verbose output"
  echo "  --no-commit          Don't automatically commit the version change"
  echo "  --skip-tests         Skip running tests (for test command only)"
  echo "  --skip-quality       Skip quality checks (for test command only)"
  echo "  --push               Push changes to remote (for test command only)"
  echo ""
  echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
  echo "  ./version-manager.sh get                 # Show current version"
  echo "  ./version-manager.sh bump patch          # Bump patch version"
  echo "  ./version-manager.sh set 1.2.3           # Set version to 1.2.3"
  echo "  ./version-manager.sh test patch          # Bump patch, test, commit, tag"
}

#------------------------------------------------------------------------------
# Version Management Functions
#------------------------------------------------------------------------------

function get_current_version() {
  if [ ! -f "$VERSION_FILE" ]; then
    print_error "Version file not found: $VERSION_FILE"
    return 1
  fi
  
  # Use grep to extract the version, trim whitespace and newlines
  grep "${VERSION_PROPERTY_NAME}=" "$VERSION_FILE" | cut -d= -f2 | tr -d ' \t\n\r'
}

function validate_version() {
  local version="$1"
  if [[ ! "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_error "Invalid version format: $version. Expected format: x.y.z (e.g., 1.2.3)"
    return 1
  fi
  return 0
}

function set_version() {
  local new_version="$1"
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return 1
  fi
  
  if ! validate_version "$new_version"; then
    return 1
  fi
  
  # Use enhanced function from version-lib.sh that updates all files
  update_version_in_files "$current_version" "$new_version"
  
  return $?
}

function commit_version_change() {
  local old_version="$1"
  local new_version="$2"
  
  # Add version file to git
  git -C "$PROJECT_ROOT" add "$VERSION_FILE"
  
  # Commit the change with a simple message
  git -C "$PROJECT_ROOT" commit -m "Bump version from $old_version to $new_version"
  
  if [ $? -ne 0 ]; then
    print_error "Failed to commit version change"
    return 1
  fi
  
  print_success "Committed version change"
  return 0
}

function create_version_tag() {
  local version="$1"
  
  # Create annotated tag
  git -C "$PROJECT_ROOT" tag -a "v$version" -m "Version $version"
  
  if [ $? -ne 0 ]; then
    print_error "Failed to create tag v$version"
    return 1
  fi
  
  print_success "Created tag v$version"
  return 0
}

function bump_version() {
  local component="$1"
  local current_version=$(get_current_version)
  local new_version=""
  
  if [ -z "$current_version" ]; then
    return 1
  fi
  
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
      print_error "Invalid version component: $component (use major, minor, or patch)"
      return 1
      ;;
  esac
  
  # Update version
  if ! set_version "$new_version"; then
    return 1
  fi
  
  # Return both versions
  echo "$current_version $new_version"
  return 0
}

function verify_version_tag() {
  local current_version=$(get_current_version)
  local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "v$current_version")
  
  if [ -z "$tag_exists" ]; then
    print_warning "No tag exists for current version v$current_version"
    return 1
  else
    print_success "Tag v$current_version exists and matches current version"
    return 0
  fi
}

function fix_version_tag() {
  local current_version=$(get_current_version)
  local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "v$current_version")
  
  if [ -n "$tag_exists" ]; then
    print_warning "Tag v$current_version already exists. Use --force to override."
    return 1
  fi
  
  create_version_tag "$current_version"
  return $?
}

function show_version_history() {
  print_header "Version History"
  echo ""
  
  git -C "$PROJECT_ROOT" tag -l "v*" --sort=-v:refname | while read -r tag; do
    local date=$(git -C "$PROJECT_ROOT" log -1 --format=%ad --date=short "$tag")
    local message=$(git -C "$PROJECT_ROOT" tag -l --format="%(contents:subject)" "$tag")
    
    echo -e "${COLOR_BOLD}$tag${COLOR_RESET} ($date): $message"
  done
}

function run_tests() {
  local skip_quality="$1"
  
  print_header "Running Tests"
  
  # Build command
  local test_cmd="${PROJECT_ROOT}/util/test-run.sh all"
  
  if [ "$skip_quality" = true ]; then
    test_cmd="$test_cmd --skip-quality"
  fi
  
  # Run tests
  eval "$test_cmd"
  
  if [ $? -ne 0 ]; then
    print_error "Tests failed"
    return 1
  fi
  
  print_success "Tests passed"
  return 0
}

#------------------------------------------------------------------------------
# Command Handlers
#------------------------------------------------------------------------------

function handle_get_command() {
  local verbose="$1"
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return 1
  fi
  
  if [ "$verbose" = true ]; then
    print_header "Version Information"
    echo ""
    echo -e "${COLOR_BOLD}Current Version:${COLOR_RESET} $current_version"
    
    # Check if tag exists
    local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "v$current_version")
    if [ -n "$tag_exists" ]; then
      echo -e "${COLOR_BOLD}Version Tag:${COLOR_RESET} v$current_version (exists)"
    else
      echo -e "${COLOR_BOLD}Version Tag:${COLOR_RESET} v$current_version (missing)"
    fi
    
    # Show last commit info
    echo -e "${COLOR_BOLD}Last Commit:${COLOR_RESET}"
    git -C "$PROJECT_ROOT" log -1 --pretty=format:"  %h (%ad) - %s" --date=short
    echo ""
  else
    echo "Current version: $current_version"
  fi
  
  return 0
}

function handle_export_command() {
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return 1
  fi
  
  echo "$current_version"
  return 0
}

function handle_bump_command() {
  local component="$1"
  local no_commit="$2"
  
  if [ -z "$component" ]; then
    print_error "Missing version component (major, minor, or patch)"
    return 1
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  if ! validate_version "$current_version"; then
    print_error "Current version is not valid: $current_version"
    return 1
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
      print_error "Invalid version component: $component (use major, minor, or patch)"
      return 1
      ;;
  esac
  
  # Update version
  set_version "$new_version"
  
  # Create commit and tag
  if [ "$no_commit" != true ]; then
    commit_version_change "$current_version" "$new_version"
    create_version_tag "$new_version"
  fi
  
  return 0
}

function handle_set_command() {
  local new_version="$1"
  local no_commit="$2"
  
  if [ -z "$new_version" ]; then
    print_error "Missing version argument"
    return 1
  fi
  
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return 1
  fi
  
  # Set version
  if ! set_version "$new_version"; then
    return 1
  fi
  
  # Create commit and tag
  if [ "$no_commit" != true ]; then
    commit_version_change "$current_version" "$new_version"
    create_version_tag "$new_version"
  fi
  
  return 0
}

function handle_verify_command() {
  verify_version_tag
  return $?
}

function handle_fix_tag_command() {
  fix_version_tag
  return $?
}

function handle_test_command() {
  local component="$1"
  local skip_tests="$2"
  local skip_quality="$3"
  local push="$4"
  
  if [ -z "$component" ]; then
    print_error "Missing version component (major, minor, or patch)"
    return 1
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  if ! validate_version "$current_version"; then
    print_error "Current version is not valid: $current_version"
    return 1
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
      print_error "Invalid version component: $component (use major, minor, or patch)"
      return 1
      ;;
  esac
  
  # Update version
  set_version "$new_version"
  
  # Run tests
  if [ "$skip_tests" != true ]; then
    run_tests "$skip_quality"
    if [ $? -ne 0 ]; then
      # Revert version change
      set_version "$current_version"
      print_error "Tests failed. Version reverted to $current_version"
      return 1
    fi
  fi
  
  # Create commit and tag
  commit_version_change "$current_version" "$new_version"
  create_version_tag "$new_version"
  
  # Push changes
  if [ "$push" = true ]; then
    print_header "Pushing Changes"
    git -C "$PROJECT_ROOT" push origin HEAD --tags
    
    if [ $? -ne 0 ]; then
      print_error "Failed to push changes"
      return 1
    fi
    
    print_success "Changes pushed successfully"
  fi
  
  return 0
}

function handle_history_command() {
  show_version_history
  return $?
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

function parse_arguments() {
  local command=""
  local verbose=false
  local no_commit=false
  local skip_tests=false
  local skip_quality=false
  local push=false
  local args=()
  
  # Parse options
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        show_help
        exit 0
        ;;
      -v|--verbose)
        verbose=true
        shift
        ;;
      --no-commit)
        no_commit=true
        shift
        ;;
      --skip-tests)
        skip_tests=true
        shift
        ;;
      --skip-quality)
        skip_quality=true
        shift
        ;;
      --push)
        push=true
        shift
        ;;
      *)
        if [ -z "$command" ]; then
          command="$1"
        else
          args+=("$1")
        fi
        shift
        ;;
    esac
  done
  
  # Execute appropriate command handler
  case "$command" in
    get)
      handle_get_command "$verbose"
      ;;
    export)
      handle_export_command
      ;;
    bump)
      handle_bump_command "${args[0]}" "$no_commit"
      ;;
    set)
      handle_set_command "${args[0]}" "$no_commit"
      ;;
    verify)
      handle_verify_command
      ;;
    fix-tag)
      handle_fix_tag_command
      ;;
    test)
      handle_test_command "${args[0]}" "$skip_tests" "$skip_quality" "$push"
      ;;
    history)
      handle_history_command
      ;;
    *)
      show_help
      if [ -n "$command" ]; then
        print_error "Unknown command: $command"
        exit 1
      fi
      ;;
  esac
  
  exit $?
}

# Parse command line arguments
parse_arguments "$@"