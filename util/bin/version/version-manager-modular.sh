#!/bin/bash
#==============================================================================
# Filename: version-manager-modular.sh
# Description: Comprehensive version management tool for Samstraumr (modular)
#==============================================================================
# Usage: ./version-manager-modular.sh <command> [options]
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

# Source the version library
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

# Source command modules
source "${SCRIPT_DIR}/commands/get-commands.sh" 2>/dev/null || true
source "${SCRIPT_DIR}/commands/set-commands.sh" 2>/dev/null || true
source "${SCRIPT_DIR}/commands/git-commands.sh" 2>/dev/null || true
source "${SCRIPT_DIR}/commands/test-commands.sh" 2>/dev/null || true

# Config file with version settings
VERSION_CONFIG_FILE="${PROJECT_ROOT}/.s8r/config/version.conf"

# Load config if it exists
if [ -f "$VERSION_CONFIG_FILE" ]; then
  source "$VERSION_CONFIG_FILE"
fi

#------------------------------------------------------------------------------
# Command Implementation Functions (if module not loaded)
#------------------------------------------------------------------------------

# Implementations are only used if the module isn't loaded

if ! type cmd_get_version &>/dev/null; then
  function cmd_get_version() {
    local verbose="${1:-false}"
    local current_version=$(get_current_version)
    
    if [ -z "$current_version" ]; then
      return 1
    fi
    
    if [ "$verbose" = "true" ]; then
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

  function cmd_export_version() {
    local current_version=$(get_current_version)
    
    if [ -z "$current_version" ]; then
      return 1
    fi
    
    echo "$current_version"
    return 0
  }

  function cmd_verify_version() {
    check_version_tag_alignment
    return $?
  }

  function cmd_show_version_history() {
    show_version_history
    return 0
  }
fi

if ! type cmd_set_version &>/dev/null; then
  function cmd_set_version() {
    local new_version="$1"
    local old_version=$(get_current_version)
    
    # Update version in files
    update_version_in_files "$old_version" "$new_version"
    return $?
  }

  function cmd_bump_version() {
    local component="$1"
    local current_version=$(get_current_version)
    local new_version=$(calculate_new_version "$current_version" "$component")
    
    # Set new version
    cmd_set_version "$new_version"
    return $?
  }
fi

if ! type cmd_commit_version_change &>/dev/null; then
  function cmd_commit_version_change() {
    local old_version="$1"
    local new_version="$2"
    local component="${3:-patch}"
    
    commit_version_changes "$new_version" "$component"
    return $?
  }

  function cmd_create_version_tag() {
    local version="$1"
    
    create_git_tag_for_version "$version"
    return $?
  }

  function cmd_fix_version_tag() {
    local current_version=$(get_current_version)
    
    create_git_tag_for_version "$current_version"
    return $?
  }

  function cmd_push_version_changes() {
    print_header "Pushing Version Changes"
    
    git -C "$PROJECT_ROOT" push origin HEAD --tags
    
    if [ $? -ne 0 ]; then
      print_error "Failed to push changes"
      return 1
    fi
    
    print_success "Pushed version changes to remote"
    return 0
  }
fi

if ! type cmd_test_version_bump &>/dev/null; then
  function cmd_run_tests() {
    local skip_quality="${1:-false}"
    
    print_header "Running Tests"
    
    # Build command
    local test_cmd="${PROJECT_ROOT}/util/bin/test/run-tests.sh all"
    
    if [ "$skip_quality" = "true" ]; then
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

  function cmd_test_version_bump() {
    local component="$1"
    local skip_tests="${2:-false}"
    local skip_quality="${3:-false}"
    local push="${4:-false}"
    
    # Get old version 
    local old_version=$(get_current_version)
    
    # Bump version
    cmd_bump_version "$component"
    local new_version=$(get_current_version)
    
    # Run tests if not skipped
    if [ "$skip_tests" != "true" ]; then
      cmd_run_tests "$skip_quality"
      if [ $? -ne 0 ]; then
        # Revert version change
        cmd_set_version "$old_version"
        print_error "Tests failed. Version reverted to $old_version"
        return 1
      fi
    fi
    
    # Commit and tag
    cmd_commit_version_change "$old_version" "$new_version" "$component"
    cmd_create_version_tag "$new_version"
    
    # Push if requested
    if [ "$push" = "true" ]; then
      cmd_push_version_changes
    fi
    
    return 0
  }
fi

#------------------------------------------------------------------------------
# Diagnostic Functions
#------------------------------------------------------------------------------

# Run diagnostics to identify and fix version issues
function run_version_diagnostics() {
  print_header "Version Management Diagnostics"
  
  # Check version file existence
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
  
  echo "Checking version configuration..."
  
  if [ -f "$version_file" ]; then
    print_success "Version file exists: $version_file"
    
    # Check version property existence
    if grep -q "^samstraumr.version=" "$version_file"; then
      local current_version=$(grep "^samstraumr.version=" "$version_file" | cut -d= -f2)
      
      if [ -z "$current_version" ]; then
        print_error "Version property is empty in $version_file"
        
        # Try to extract version from git tag
        if command -v git &>/dev/null && git rev-parse --is-inside-work-tree &>/dev/null; then
          local git_version=$(git describe --tags --abbrev=0 2>/dev/null | sed 's/^v//')
          
          if [ -n "$git_version" ] && [[ "$git_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
            print_info "Found version from git tag: $git_version"
            
            # Offer to fix
            print_warning "Would you like to set version to $git_version? (y/n)"
            read -r response
            if [[ "$response" =~ ^[Yy]$ ]]; then
              sed -i "s/^samstraumr.version=.*/samstraumr.version=$git_version/" "$version_file"
              print_success "Version set to $git_version"
            fi
          else
            print_warning "No version tag found in git"
            print_info "Setting default version to 1.0.0"
            sed -i "s/^samstraumr.version=.*/samstraumr.version=1.0.0/" "$version_file"
            print_success "Version set to 1.0.0"
          fi
        else
          print_warning "Git not available, setting default version to 1.0.0"
          sed -i "s/^samstraumr.version=.*/samstraumr.version=1.0.0/" "$version_file"
          print_success "Version set to 1.0.0"
        fi
      else
        if [[ "$current_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
          print_success "Valid version found: $current_version"
        else
          print_error "Invalid version format: $current_version"
          print_info "Setting default version to 1.0.0"
          sed -i "s/^samstraumr.version=.*/samstraumr.version=1.0.0/" "$version_file"
          print_success "Version set to 1.0.0"
        fi
      fi
    else
      print_error "Version property not found in $version_file"
      print_info "Adding version property with default value 1.0.0"
      echo "samstraumr.version=1.0.0" >> "$version_file"
      print_success "Version property added"
    fi
  else
    print_error "Version file not found: $version_file"
    print_info "Creating version file with default settings"
    
    # Create parent directory if it doesn't exist
    mkdir -p "$(dirname "$version_file")"
    
    # Create version file with default content
    cat > "$version_file" << EOF
# Samstraumr Project Version
# This file is the single source of truth for project versioning

# Project version
samstraumr.version=1.0.0

# Last updated date
samstraumr.last.updated=$(date +"%B %d, %Y")

# Maintainer info
samstraumr.maintainer=Eric C. Mumford (@heymumford)

# License
samstraumr.license=Mozilla Public License 2.0
EOF
    print_success "Created version file: $version_file"
  fi
  
  # Verify git tags
  if command -v git &>/dev/null && git rev-parse --is-inside-work-tree &>/dev/null; then
    print_info "Checking git tags..."
    
    local current_version=$(grep "^samstraumr.version=" "$version_file" | cut -d= -f2)
    local tag_name="v$current_version"
    
    if git tag | grep -q "^$tag_name$"; then
      print_success "Git tag exists for current version: $tag_name"
    else
      print_warning "Git tag missing for current version: $tag_name"
      print_info "You can create it with: ./version-manager-modular.sh fix-tag"
    fi
  fi
  
  print_header "Diagnostic Complete"
  echo ""
  print_info "Current version: $(grep "^samstraumr.version=" "$version_file" | cut -d= -f2)"
  print_info "Version file: $version_file"
  echo ""
  print_success "Run 'get -v' to see detailed version information"
}

#------------------------------------------------------------------------------
# Help Function
#------------------------------------------------------------------------------

function show_help() {
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}Samstraumr Version Manager (Modular)${COLOR_RESET}"
  else
    echo "Samstraumr Version Manager (Modular)"
  fi
  echo ""
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}USAGE:${COLOR_RESET}"
  else
    echo "USAGE:"
  fi
  echo "  ./version-manager-modular.sh <command> [options]"
  echo ""
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}COMMANDS:${COLOR_RESET}"
  else
    echo "COMMANDS:"
  fi
  echo "  get                  Show current version information"
  echo "  export               Output only the current version (for scripts)"
  echo "  bump <type>          Bump version (type: major, minor, patch)"
  echo "  set <version>        Set a specific version (format: x.y.z)"
  echo "  verify               Verify that version and tag are in sync"
  echo "  fix-tag              Create a git tag matching the current version"
  echo "  test <type>          Bump version, run tests, then commit and tag"
  echo "  history              Show version history"
  echo "  diagnose             Run diagnostics to identify and fix version issues"
  echo ""
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}OPTIONS:${COLOR_RESET}"
  else
    echo "OPTIONS:"
  fi
  echo "  -h, --help           Display this help message"
  echo "  -v, --verbose        Enable verbose output"
  echo "  --no-commit          Don't automatically commit the version change"
  echo "  --skip-tests         Skip running tests (for test command only)"
  echo "  --skip-quality       Skip quality checks (for test command only)"
  echo "  --push               Push changes to remote (for test command only)"
  echo ""
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
  else
    echo "EXAMPLES:"
  fi
  echo "  ./version-manager-modular.sh get                 # Show current version"
  echo "  ./version-manager-modular.sh bump patch          # Bump patch version"
  echo "  ./version-manager-modular.sh set 1.2.3           # Set version to 1.2.3"
  echo "  ./version-manager-modular.sh test patch          # Bump patch, test, commit, tag"
  echo "  ./version-manager-modular.sh diagnose            # Run diagnostics"
}

#------------------------------------------------------------------------------
# Main Command Handler
#------------------------------------------------------------------------------

function handle_command() {
  local command="$1"
  shift
  
  case "$command" in
    get)
      local verbose="false"
      if [[ "$*" == *"--verbose"* || "$*" == *"-v"* ]]; then
        verbose="true"
      fi
      cmd_get_version "$verbose"
      ;;
    export)
      cmd_export_version
      ;;
    bump)
      local component="$1"
      local no_commit="false"
      if [[ "$*" == *"--no-commit"* ]]; then
        no_commit="true"
      fi
      
      # Get old version before bumping
      local old_version=$(get_current_version)
      
      # Bump version
      cmd_bump_version "$component"
      
      # Get new version after bumping
      local new_version=$(get_current_version)
      
      # Create commit and tag if not skipped
      if [ "$no_commit" != "true" ]; then
        cmd_commit_version_change "$old_version" "$new_version" "$component"
        cmd_create_version_tag "$new_version"
      fi
      ;;
    set)
      local new_version="$1"
      local no_commit="false"
      if [[ "$*" == *"--no-commit"* ]]; then
        no_commit="true"
      fi
      
      # Get old version before setting
      local old_version=$(get_current_version)
      
      # Set version
      cmd_set_version "$new_version"
      
      # Create commit and tag if not skipped
      if [ "$no_commit" != "true" ]; then
        cmd_commit_version_change "$old_version" "$new_version" "set"
        cmd_create_version_tag "$new_version"
      fi
      ;;
    verify)
      cmd_verify_version
      ;;
    fix-tag)
      cmd_fix_version_tag
      ;;
    test)
      local component="$1"
      local skip_tests="false"
      local skip_quality="false"
      local push="false"
      
      if [[ "$*" == *"--skip-tests"* ]]; then
        skip_tests="true"
      fi
      
      if [[ "$*" == *"--skip-quality"* ]]; then
        skip_quality="true"
      fi
      
      if [[ "$*" == *"--push"* ]]; then
        push="true"
      fi
      
      cmd_test_version_bump "$component" "$skip_tests" "$skip_quality" "$push"
      ;;
    history)
      cmd_show_version_history
      ;;
    diagnose)
      # Run diagnostic tests to identify and fix version issues
      run_version_diagnostics
      ;;
    debug)
      # Display detailed version and configuration information
      print_header "Version Configuration Debug Information"
      
      # Show paths and config
      print_info "PROJECT_ROOT: $PROJECT_ROOT"
      print_info "VERSION_FILE: ${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
      print_info "VERSION_CONFIG_FILE: $VERSION_CONFIG_FILE"
      
      # Show loaded functions
      print_info "Available commands:"
      declare -F | grep -E 'cmd_|version_' | sed 's/declare -f /  /'
      
      # Show current version
      local current_version=$(get_current_version)
      print_info "Current version: $current_version"
      
      # Show git status
      if command -v git &>/dev/null && git rev-parse --is-inside-work-tree &>/dev/null; then
        print_info "Git branch: $(git -C "$PROJECT_ROOT" rev-parse --abbrev-ref HEAD)"
        print_info "Git status:"
        git -C "$PROJECT_ROOT" status --short
      else
        print_warning "Git not available or not a git repository"
      fi
      ;;
    *)
      show_help
      if [ -n "$command" ]; then
        print_error "Unknown command: $command"
        return 1
      fi
      ;;
  esac
  
  return $?
}

#------------------------------------------------------------------------------
# Argument Parsing
#------------------------------------------------------------------------------

# Handle no arguments
if [ $# -eq 0 ]; then
  show_help
  exit 0
fi

# Handle help
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  # Global help
  show_help
  exit 0
elif [[ "$1" == "help" ]]; then
  shift
  if [ -z "$1" ]; then
    # Just 'help' without command shows main help
    show_help
    exit 0
  fi
  
  command="$1"
  
  # Command-specific help
  case "$command" in
    get|export|verify|history)
      if [[ -f "${SCRIPT_DIR}/commands/get-commands.sh" ]]; then
        source "${SCRIPT_DIR}/commands/get-commands.sh"
        show_help
      else
        show_help
      fi
      ;;
    set|bump)
      if [[ -f "${SCRIPT_DIR}/commands/set-commands.sh" ]]; then
        source "${SCRIPT_DIR}/commands/set-commands.sh"
        show_help
      else
        show_help
      fi
      ;;
    fix-tag|push)
      if [[ -f "${SCRIPT_DIR}/commands/git-commands.sh" ]]; then
        source "${SCRIPT_DIR}/commands/git-commands.sh"
        show_help
      else
        show_help
      fi
      ;;
    test)
      if [[ -f "${SCRIPT_DIR}/commands/test-commands.sh" ]]; then
        source "${SCRIPT_DIR}/commands/test-commands.sh"
        show_help
      else
        show_help
      fi
      ;;
    *)
      show_help
      ;;
  esac
  exit 0
fi

# First argument is the command
command="$1"
shift

# Parse remaining arguments and execute command
handle_command "$command" "$@"
exit $?