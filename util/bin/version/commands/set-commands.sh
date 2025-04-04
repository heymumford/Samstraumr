#!/bin/bash
#==============================================================================
# Filename: set-commands.sh
# Description: Version modification commands
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
  if type print_header &>/dev/null; then
    print_header "Version Modification Commands"
  else
    echo "=== Version Modification Commands ==="
  fi
  echo ""
  if type print_bold &>/dev/null; then
    print_bold "COMMANDS:"
  else
    echo "COMMANDS:"
  fi
  echo "  set <version>        Set a specific version (format: x.y.z)"
  echo "  bump <type>          Bump version (type: major, minor, patch)"
  echo "    major              Increment major version (breaking changes)"
  echo "    minor              Increment minor version (new features)"
  echo "    patch              Increment patch version (bug fixes)"
  echo ""
  if type print_bold &>/dev/null; then
    print_bold "OPTIONS:"
  else
    echo "OPTIONS:"
  fi
  echo "  --no-commit          Don't automatically commit the version change"
  echo ""
  if type print_bold &>/dev/null; then
    print_bold "FILES UPDATED:"
  else
    echo "FILES UPDATED:"
  fi
  echo "  - version.properties (primary version source)"
  echo "  - Maven POM files (if present)"
  echo "  - README.md version badge (if present)"
  echo ""
  if type print_bold &>/dev/null; then
    print_bold "EXAMPLES:"
  else
    echo "EXAMPLES:"
  fi
  echo "  set 1.2.3            # Set specific version"
  echo "  bump patch           # Increment patch version (1.2.3 -> 1.2.4)"
  echo "  bump minor           # Increment minor version (1.2.3 -> 1.3.0)"
  echo "  bump major           # Increment major version (1.2.3 -> 2.0.0)"
  echo "  bump patch --no-commit # Bump without committing"
}

#------------------------------------------------------------------------------
# Version Modification Commands
#------------------------------------------------------------------------------

# Set version to a specific value
# Usage: cmd_set_version "1.2.3" [no_commit]
function cmd_set_version() {
  local new_version="$1"
  local no_commit="${2:-false}"
  
  # Validate new version
  if [ -z "$new_version" ]; then
    version_error "Missing version argument" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Validate version format
  if ! validate_version "$new_version"; then
    version_error "Invalid version format: $new_version (must be in format x.y.z)" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    version_error "Failed to retrieve current version" $ERR_GENERAL
    return $ERR_GENERAL
  fi
  
  # Verify the version file exists
  if [ ! -f "$VERSION_FILE" ]; then
    version_error "Version file not found: $VERSION_FILE" $ERR_FILE_NOT_FOUND
    return $ERR_FILE_NOT_FOUND
  fi
  
  # Create a backup of the version file
  local backup_file="${VERSION_FILE}.bak"
  cp "$VERSION_FILE" "$backup_file"
  
  version_info "Setting version from $current_version to $new_version"
  version_info "Backup created: $backup_file"
  
  # Update version using the enhanced library function
  if ! update_version_in_files "$current_version" "$new_version"; then
    version_error "Failed to update version files" $ERR_GENERAL
    version_info "Restoring backup from $backup_file"
    cp "$backup_file" "$VERSION_FILE"
    return $ERR_GENERAL
  fi
  
  # If we're not committing, we're done
  if [ "$no_commit" = "true" ]; then
    version_info "Skipping commit as requested"
    return 0
  fi
  
  # Commit changes if requested
  if type commit_version_changes &>/dev/null; then
    if ! commit_version_changes "$new_version" "set"; then
      version_error "Failed to commit version changes" $ERR_GIT_OPERATION
      return $ERR_GIT_OPERATION
    fi
  fi
  
  version_success "Version successfully set to $new_version"
  return 0
}

# Bump version based on component
# Usage: cmd_bump_version "patch|minor|major" [no_commit]
function cmd_bump_version() {
  local component="$1"
  local no_commit="${2:-false}"
  
  # Validate input
  if [ -z "$component" ]; then
    version_error "Missing version component (major, minor, or patch)" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Validate component type
  if [[ ! "$component" =~ ^(major|minor|patch)$ ]]; then
    version_error "Invalid version component: $component (use major, minor, or patch)" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Get current version
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    version_error "Failed to retrieve current version" $ERR_GENERAL
    return $ERR_GENERAL
  fi
  
  version_info "Current version: $current_version"
  
  # Use the enhanced library function to calculate new version
  local new_version=$(calculate_new_version "$component")
  local ret_code=$?
  
  if [ $ret_code -ne 0 ] || [ -z "$new_version" ]; then
    version_error "Failed to calculate new version for component: $component" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  version_info "New version: $new_version"
  
  # Verify that new version is different from current
  if [ "$current_version" = "$new_version" ]; then
    version_warning "New version is the same as current version: $current_version"
    return 0
  fi
  
  # Update version file first - this is the source of truth
  if ! update_version_in_files "$current_version" "$new_version"; then
    version_error "Failed to update version files" $ERR_GENERAL
    return $ERR_GENERAL
  fi
  
  # If we're not committing, we're done
  if [ "$no_commit" = "true" ]; then
    version_info "Skipping commit as requested"
    return 0
  fi
  
  # Commit changes if requested
  if type commit_version_changes &>/dev/null; then
    if ! commit_version_changes "$new_version" "$component"; then
      version_error "Failed to commit version changes" $ERR_GIT_OPERATION
      return $ERR_GIT_OPERATION
    fi
  fi
  
  return 0
}

# Update last updated date in version file
# Usage: cmd_update_version_date
function cmd_update_version_date() {
  local today=$(date "+%B %d, %Y")
  
  sed -i "s/${VERSION_DATE_PROPERTY_NAME}=.*/${VERSION_DATE_PROPERTY_NAME}=${today}/" "$VERSION_FILE"
  
  if [ $? -ne 0 ]; then
    version_error "Failed to update date in $VERSION_FILE" $ERR_GENERAL
    return $ERR_GENERAL
  fi
  
  return 0
}

# Update version references in project files
# Usage: cmd_update_version_references "old_version" "new_version"
function cmd_update_version_references() {
  local old_version="$1"
  local new_version="$2"
  
  version_header "Updating Version References"
  
  # Check if we have POM files to update
  local pom_files=(
    "${PROJECT_ROOT}/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/samstraumr-core/pom.xml"
  )
  
  for pom_file in "${pom_files[@]}"; do
    if [ -f "$pom_file" ]; then
      # Update version tags
      sed -i "s/<version>$old_version<\/version>/<version>$new_version<\/version>/g" "$pom_file"
      
      # Update properties
      sed -i "s/<samstraumr.version>$old_version<\/samstraumr.version>/<samstraumr.version>$new_version<\/samstraumr.version>/g" "$pom_file"
      
      version_success "Updated version in $pom_file"
    fi
  done
  
  # Update README.md if it exists
  if [ -f "${PROJECT_ROOT}/README.md" ]; then
    # Update version string
    sed -i "s/Version: .*/Version: $new_version/g" "${PROJECT_ROOT}/README.md"
    
    # Update version badge URL
    sed -i "s|version-[0-9.]\+-blue|version-$new_version-blue|g" "${PROJECT_ROOT}/README.md"
    
    version_success "Updated version in README.md"
  fi
  
  return 0
}

# Export the functions
export -f cmd_set_version
export -f cmd_bump_version
export -f cmd_update_version_date
export -f cmd_update_version_references