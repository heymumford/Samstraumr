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
# Version Modification Commands
#------------------------------------------------------------------------------

# Set version to a specific value
# Usage: cmd_set_version "1.2.3" [no_commit]
function cmd_set_version() {
  local new_version="$1"
  local no_commit="${2:-false}"
  
  if [ -z "$new_version" ]; then
    version_error "Missing version argument" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  local current_version=$(get_current_version)
  
  if [ -z "$current_version" ]; then
    return $ERR_GENERAL
  fi
  
  # Validate version format
  if ! validate_version "$new_version"; then
    return $ERR_INVALID_VERSION
  fi
  
  # Update version file
  sed -i "s/${VERSION_PROPERTY_NAME}=${current_version}/${VERSION_PROPERTY_NAME}=${new_version}/" "$VERSION_FILE"
  
  if [ $? -ne 0 ]; then
    version_error "Failed to update version in $VERSION_FILE" $ERR_GENERAL
    return $ERR_GENERAL
  fi
  
  # Update last updated date
  cmd_update_version_date
  
  version_success "Version set from $current_version to $new_version"
  
  return 0
}

# Bump version based on component
# Usage: cmd_bump_version "patch|minor|major" [no_commit]
function cmd_bump_version() {
  local component="$1"
  local no_commit="${2:-false}"
  
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
  
  # Update files that reference the version
  cmd_update_version_references "$current_version" "$new_version"
  
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