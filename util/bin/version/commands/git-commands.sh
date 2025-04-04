#!/bin/bash
#==============================================================================
# Filename: git-commands.sh
# Description: Git integration commands for version management
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
  echo -e "${COLOR_BOLD}Git Integration Commands${COLOR_RESET}"
  echo ""
  echo -e "${COLOR_BOLD}COMMANDS:${COLOR_RESET}"
  echo "  commit_version_change    Commit version changes to git"
  echo "  create_version_tag       Create git tag for version"
  echo "  fix-tag                  Create missing git tag for current version"
  echo "  push_version_changes     Push changes and tags to remote"
  echo ""
  echo -e "${COLOR_BOLD}GIT OPERATIONS:${COLOR_RESET}"
  echo "  - Commits modified version files"
  echo "  - Creates annotated tags with format v1.2.3"
  echo "  - Generates descriptive commit messages"
  echo "  - Optionally pushes changes to remote repository"
  echo ""
  echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
  echo "  fix-tag               # Create missing tag for current version"
  echo "  push                  # Push changes and tags to remote"
}

#------------------------------------------------------------------------------
# Git Integration Commands
#------------------------------------------------------------------------------

# Commit version change to git
# Usage: cmd_commit_version_change "old_version" "new_version" "component"
function cmd_commit_version_change() {
  local old_version="$1"
  local new_version="$2"
  local component="${3:-patch}"
  
  # Add version file to git
  git -C "$PROJECT_ROOT" add "$VERSION_FILE"
  
  # Add other files that might have version references
  if [ -f "${PROJECT_ROOT}/README.md" ]; then
    git -C "$PROJECT_ROOT" add "${PROJECT_ROOT}/README.md"
  fi
  
  # Add POM files if they exist
  local pom_files=(
    "${PROJECT_ROOT}/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/samstraumr-core/pom.xml"
  )
  
  for pom_file in "${pom_files[@]}"; do
    if [ -f "$pom_file" ]; then
      git -C "$PROJECT_ROOT" add "$pom_file"
    fi
  done
  
  # Determine commit message based on component type
  local commit_msg=""
  
  case "$component" in
    major)
      commit_msg=$(printf "${COMMIT_MESSAGE_MAJOR}" "$old_version" "$new_version")
      ;;
    minor)
      commit_msg=$(printf "${COMMIT_MESSAGE_MINOR}" "$old_version" "$new_version")
      ;;
    patch|*)
      commit_msg=$(printf "${COMMIT_MESSAGE_PATCH}" "$old_version" "$new_version")
      ;;
  esac
  
  # Create the commit (using a simple message without Claude signature)
  git -C "$PROJECT_ROOT" commit -m "Bump version from $old_version to $new_version"
  
  if [ $? -ne 0 ]; then
    version_error "Failed to commit version change" $ERR_GIT_OPERATION
    return $ERR_GIT_OPERATION
  fi
  
  version_success "Committed version change"
  return 0
}

# Create a git tag for the version
# Usage: cmd_create_version_tag "version"
function cmd_create_version_tag() {
  local version="$1"
  
  # Create tag message
  local tag_msg=$(printf "${GIT_TAG_MESSAGE_FORMAT}" "$version")
  
  # Create annotated tag
  git -C "$PROJECT_ROOT" tag -a "${GIT_TAG_PREFIX}$version" -m "$tag_msg"
  
  if [ $? -ne 0 ]; then
    version_error "Failed to create tag ${GIT_TAG_PREFIX}$version" $ERR_GIT_OPERATION
    return $ERR_GIT_OPERATION
  fi
  
  version_success "Created tag ${GIT_TAG_PREFIX}$version"
  return 0
}

# Fix missing git tag for current version
# Usage: cmd_fix_version_tag
function cmd_fix_version_tag() {
  local current_version=$(get_current_version)
  local tag_exists=$(git -C "$PROJECT_ROOT" tag -l "${GIT_TAG_PREFIX}$current_version")
  
  if [ -n "$tag_exists" ]; then
    version_warning "Tag ${GIT_TAG_PREFIX}$current_version already exists. Use --force to override."
    return 1
  fi
  
  cmd_create_version_tag "$current_version"
  return $?
}

# Push version changes to remote
# Usage: cmd_push_version_changes
function cmd_push_version_changes() {
  version_header "Pushing Version Changes"
  
  # Push changes to current branch
  git -C "$PROJECT_ROOT" push
  
  if [ $? -ne 0 ]; then
    version_error "Failed to push changes" $ERR_GIT_OPERATION
    return $ERR_GIT_OPERATION
  fi
  
  # Push tags
  git -C "$PROJECT_ROOT" push --tags
  
  if [ $? -ne 0 ]; then
    version_error "Failed to push tags" $ERR_GIT_OPERATION
    return $ERR_GIT_OPERATION
  fi
  
  version_success "Pushed version changes and tags to remote"
  return 0
}

# Export the functions
export -f cmd_commit_version_change
export -f cmd_create_version_tag
export -f cmd_fix_version_tag
export -f cmd_push_version_changes