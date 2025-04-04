#!/bin/bash
#==============================================================================
# Filename: get-commands.sh
# Description: Version retrieval commands
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../../" && pwd)"

# Source the version library
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

#------------------------------------------------------------------------------
# Help Functions
#------------------------------------------------------------------------------

function show_help() {
  echo -e "${COLOR_BOLD}Version Retrieval Commands${COLOR_RESET}"
  echo ""
  echo -e "${COLOR_BOLD}COMMANDS:${COLOR_RESET}"
  echo "  get                  Show current version information"
  echo "    -v, --verbose      Show detailed information"
  echo "  export               Output only the current version (for scripts)"
  echo "  verify               Verify that version and tag are in sync"
  echo "  history              Show version history"
  echo ""
  echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
  echo "  get                  # Show current version"
  echo "  get -v               # Show detailed information"
  echo "  export               # Output only version number"
  echo "  verify               # Check if tag exists for current version"
  echo "  history              # Show version history in git"
}

#------------------------------------------------------------------------------
# Version Retrieval Commands
#------------------------------------------------------------------------------

# Get current version (simple output format)
# Usage: cmd_get_version [options]
# Options:
#  --verbose  Show detailed information
function cmd_get_version() {
  local verbose="${1:-false}"
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return $ERR_GENERAL
  fi
  
  if [ "$verbose" = "true" ]; then
    version_header "Version Information"
    echo ""
    echo -e "${COLOR_BOLD}Current Version:${COLOR_RESET} $current_version"
    
    # Check if tag exists
    local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "${GIT_TAG_PREFIX}$current_version")
    if [ -n "$tag_exists" ]; then
      echo -e "${COLOR_BOLD}Version Tag:${COLOR_RESET} ${GIT_TAG_PREFIX}$current_version (exists)"
    else
      echo -e "${COLOR_BOLD}Version Tag:${COLOR_RESET} ${GIT_TAG_PREFIX}$current_version (missing)"
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

# Export version for use in scripts
# Usage: cmd_export_version
function cmd_export_version() {
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return $ERR_GENERAL
  fi
  
  echo "$current_version"
  return 0
}

# Verify version tag exists and matches current version
# Usage: cmd_verify_version
function cmd_verify_version() {
  local current_version=$(get_current_version)
  local tag_name="${GIT_TAG_PREFIX}$current_version"
  local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "$tag_name")
  
  version_header "Version Tag Verification"
  echo -e "Current version: $current_version"
  
  if [ -z "$tag_exists" ]; then
    version_warning "No tag exists for current version $tag_name"
    return 1
  else
    version_success "Tag $tag_name exists and matches current version"
    return 0
  fi
}

# Show version history from git tags
# Usage: cmd_show_version_history
function cmd_show_version_history() {
  version_header "Version History"
  echo ""
  
  git -C "$PROJECT_ROOT" tag -l "${GIT_TAG_PREFIX}*" --sort=-v:refname | while read -r tag; do
    local date=$(git -C "$PROJECT_ROOT" log -1 --format=%ad --date=short "$tag")
    local message=$(git -C "$PROJECT_ROOT" tag -l --format="%(contents:subject)" "$tag")
    
    echo -e "${COLOR_BOLD}$tag${COLOR_RESET} ($date): $message"
  done
  
  return 0
}

# Export the functions
export -f cmd_get_version
export -f cmd_export_version
export -f cmd_verify_version
export -f cmd_show_version_history