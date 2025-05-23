#!/bin/bash
#==============================================================================
# Filename: version-lib.sh
# Description: Core utilities for version management
#==============================================================================

# Determine script paths and load common library
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the common library if it exists
if [ -f "${SCRIPT_LIB_DIR}/common.sh" ]; then
  source "${SCRIPT_LIB_DIR}/common.sh"
fi

# Default paths and settings (can be overridden by version.conf)
: ${VERSION_CONFIG_FILE:="${PROJECT_ROOT}/.s8r/config/version.conf"}
: ${VERSION_FILE:="${PROJECT_ROOT}/modules/version.properties"}
: ${VERSION_PROPERTY_NAME:="samstraumr.version"}
: ${VERSION_DATE_PROPERTY_NAME:="samstraumr.last.updated"}
: ${GIT_TAG_PREFIX:="v"}

# Use existing color definitions if common.sh was loaded
if ! declare -p COLOR_RED &> /dev/null; then
  # Define our own color codes if they don't exist yet
  COLOR_RED='\033[0;31m'
  COLOR_GREEN='\033[0;32m'
  COLOR_YELLOW='\033[0;33m'
  COLOR_BLUE='\033[0;34m'
  COLOR_RESET='\033[0m'
  COLOR_BOLD='\033[1m'
fi

# Error codes
ERR_GENERAL=1
ERR_FILE_NOT_FOUND=2
ERR_INVALID_VERSION=3
ERR_GIT_OPERATION=4
ERR_TEST_FAILURE=5

# Log error message and return error code
# Usage: version_error "Error message" [error_code]
function version_error() {
  local message="$1"
  local code="${2:-$ERR_GENERAL}"
  if type print_error &>/dev/null; then
    print_error "$message"
  else
    echo -e "${COLOR_RED}Error: $message${COLOR_RESET}" >&2
  fi
  return "$code"
}

# Log success message
# Usage: version_success "Success message"
function version_success() {
  if type print_success &>/dev/null; then
    print_success "$1"
  else
    echo -e "${COLOR_GREEN}$1${COLOR_RESET}" >&2
  fi
}

# Log warning message
# Usage: version_warning "Warning message"
function version_warning() {
  if type print_warning &>/dev/null; then
    print_warning "$1"
  else
    echo -e "${COLOR_YELLOW}$1${COLOR_RESET}" >&2
  fi
}

# Log info message
# Usage: version_info "Info message"
function version_info() {
  if type print_info &>/dev/null; then
    print_info "$1"
  else
    echo -e "${COLOR_BLUE}$1${COLOR_RESET}" >&2
  fi
}

# Log header message
# Usage: version_header "Header message"
function version_header() {
  if type print_header &>/dev/null; then
    print_header "$1"
  else
    echo -e "${COLOR_BLUE}${COLOR_BOLD}$1${COLOR_RESET}" >&2
  fi
}

# Load configuration from version.conf if it exists
function load_version_config() {
  if [ -f "$VERSION_CONFIG_FILE" ]; then
    source "$VERSION_CONFIG_FILE"
    return 0
  else
    # Not an error if config doesn't exist yet
    return 0
  fi
}

# Validates that a version string follows semantic versioning format
# Usage: validate_version "1.2.3"
# Returns: 0 if valid, 1 otherwise
function validate_version() {
  local version="$1"
  
  # Check if version is empty
  if [ -z "$version" ]; then
    version_error "Version cannot be empty" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Check version format (MAJOR.MINOR.PATCH)
  if ! [[ "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    version_error "Invalid version format: $version (must be in format x.y.z)" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Extract components to ensure they're valid numbers
  local -i major minor patch
  IFS='.' read -r major minor patch <<< "$version"
  
  # Validate components are non-negative numbers
  if [ "$major" -lt 0 ] || [ "$minor" -lt 0 ] || [ "$patch" -lt 0 ]; then
    version_error "Version components must be non-negative: $version" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  return 0
}

# Initialize by loading configuration
load_version_config

#------------------------------------------------------------------------------
# Version Management Functions
#------------------------------------------------------------------------------

# Get the current version from version.properties
# Usage: get_current_version
# Returns: Current version or fallback version if not found
function get_current_version() {
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/modules/version.properties}"
  local fallback_version="1.0.0"
  
  # Check if version file exists
  if [ ! -f "$version_file" ]; then
    print_error "Version properties file not found: $version_file"
    print_warning "Using fallback version: $fallback_version"
    echo "$fallback_version"
    return 1
  fi
  
  # Get version from file
  local version=$(grep "samstraumr.version=" "$version_file" | cut -d= -f2)
  
  # Validate that version is not empty
  if [ -z "$version" ]; then
    print_error "Version property is empty in $version_file"
    
    # Try to extract version from git tag as fallback
    if command -v git &>/dev/null && git rev-parse --is-inside-work-tree &>/dev/null; then
      local git_version=$(git describe --tags --abbrev=0 2>/dev/null | sed 's/^v//')
      
      if [ -n "$git_version" ] && [[ "$git_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        print_warning "Using git tag as fallback version: $git_version"
        echo "$git_version"
        return 0
      fi
    fi
    
    # Use hard-coded fallback as last resort
    print_warning "Using fallback version: $fallback_version"
    echo "$fallback_version"
    return 1
  fi
  
  # Validate semantic version format
  if ! [[ "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_error "Invalid version format in $version_file: '$version'"
    print_warning "Using fallback version: $fallback_version"
    echo "$fallback_version"
    return 1
  fi
  
  echo "$version"
  return 0
}

function get_last_updated_date() {
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/modules/version.properties}"
  
  if [ ! -f "$version_file" ]; then
    print_error "Version properties file not found: $version_file"
    return 1
  fi
  
  grep "samstraumr.last.updated=" "$version_file" | cut -d= -f2
}

# Calculate a new version based on bump type
# Usage: calculate_new_version "patch|minor|major"
# Returns: New version string
function calculate_new_version() {
  local bump_type="$1"
  local current_version=$(get_current_version)
  
  # Check if bump type is valid
  if [ -z "$bump_type" ]; then
    version_error "Missing bump type (use 'major', 'minor', or 'patch')" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Validate bump type
  if [[ ! "$bump_type" =~ ^(major|minor|patch)$ ]]; then
    version_error "Invalid bump type: $bump_type (must be 'major', 'minor', or 'patch')" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Check if current version is valid
  if ! validate_version "$current_version"; then
    version_error "Cannot calculate new version: current version is invalid" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Split version into components
  local -i major minor patch
  IFS='.' read -r major minor patch <<< "$current_version"
  
  # Calculate new version based on bump type
  local new_version=""
  case "$bump_type" in
    major)
      new_version="$((major + 1)).0.0"
      ;;
    minor)
      new_version="${major}.$((minor + 1)).0"
      ;;
    patch)
      # Allow patch to go to 999 before rolling over
      if [ "$patch" -lt 999 ]; then
        new_version="${major}.${minor}.$((patch + 1))"
      else
        # Roll over patch to 0 and increment minor
        new_version="${major}.$((minor + 1)).0"
        version_info "Patch version reached 999, incrementing minor version instead"
      fi
      ;;
  esac
  
  # Validate the calculated version
  if ! validate_version "$new_version"; then
    version_error "Calculated version is invalid: $new_version" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  echo "$new_version"
  return 0
}

# Update version across all project files
# Usage: update_version_in_files "current_version" "new_version"
# Returns: 0 on success, error code on failure
function update_version_in_files() {
  local current_version="$1"
  local new_version="$2"
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/modules/version.properties}"
  
  # Validate input parameters
  if [ -z "$current_version" ]; then
    version_error "Missing current version parameter" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  if [ -z "$new_version" ]; then
    version_error "Missing new version parameter" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Validate version formats
  if ! validate_version "$new_version"; then
    version_error "Cannot update to invalid version: $new_version" $ERR_INVALID_VERSION
    return $ERR_INVALID_VERSION
  fi
  
  # Check if version file exists
  if [ ! -f "$version_file" ]; then
    version_error "Version properties file not found: $version_file" $ERR_FILE_NOT_FOUND
    return $ERR_FILE_NOT_FOUND
  fi
  
  # Create a backup of the version file
  local backup_file="${version_file}.bak"
  cp "$version_file" "$backup_file"
  
  print_section "Updating Version to $new_version"
  print_info "Current version: $current_version"
  print_info "New version:     $new_version"
  print_info "Backup created:  $backup_file"
  
  # Update version.properties - This is the source of truth for version
  # Check if property line exists, add it if not
  if grep -q "^samstraumr.version=" "$version_file"; then
    # Update existing property
    sed -i "s/^samstraumr.version=.*/samstraumr.version=$new_version/" "$version_file"
  else
    # Property doesn't exist, add it
    echo "samstraumr.version=$new_version" >> "$version_file"
  fi
  
  # Verify the update was successful
  local updated_version=$(grep "^samstraumr.version=" "$version_file" | cut -d= -f2)
  if [ "$updated_version" != "$new_version" ]; then
    version_error "Failed to update version in $version_file" $ERR_GENERAL
    # Restore backup
    cp "$backup_file" "$version_file"
    return $ERR_GENERAL
  fi
  
  print_success "Updated version.properties"
  
  # Update last updated date
  local today=$(date +"%B %d, %Y")
  
  # Check if date property line exists, add it if not
  if grep -q "^samstraumr.last.updated=" "$version_file"; then
    # Update existing property
    sed -i "s/^samstraumr.last.updated=.*/samstraumr.last.updated=$today/" "$version_file"
  else
    # Property doesn't exist, add it
    echo "samstraumr.last.updated=$today" >> "$version_file"
  fi
  
  print_success "Updated last updated date to $today"
  
  # Update all POM files including parent and all modules
  # This ensures consistent version across all modules
  local pom_files=(
    "${PROJECT_ROOT}/pom.xml"
    "${PROJECT_ROOT}/modules/pom.xml"
    "${PROJECT_ROOT}/modules/samstraumr-core/pom.xml"
  )
  
  print_info "Updating version in POM files:"
  local pom_update_failures=0
  
  for pom_file in "${pom_files[@]}"; do
    if [ -f "$pom_file" ]; then
      # Create a backup of the POM file
      local pom_backup="${pom_file}.bak"
      cp "$pom_file" "$pom_backup"
      
      print_info "  Processing $pom_file"
      
      # ENHANCED POM VERSION UPDATE APPROACH
      # Use a more robust approach with targeted XML manipulation
      
      # 1. Check if this is the root POM
      if [ "$pom_file" == "${PROJECT_ROOT}/pom.xml" ]; then
        print_info "  Root POM detected - updating project version element"
        
        # Update the version in the root project element
        if grep -q "<version>${current_version}</version>" "$pom_file"; then
          sed -i "s|<version>${current_version}</version>|<version>${new_version}</version>|" "$pom_file"
          print_info "  Updated root POM project version"
        else
          print_warning "  Could not find current version in root project element"
        fi
        
        # Update samstraumr.version property if it exists
        if grep -q "<samstraumr.version>" "$pom_file"; then
          sed -i "s|<samstraumr.version>${current_version}</samstraumr.version>|<samstraumr.version>${new_version}</samstraumr.version>|" "$pom_file"
          print_info "  Updated samstraumr.version property"
        fi
      fi
      
      # 2. Check if this is a module POM with parent
      if [ "$pom_file" == "${PROJECT_ROOT}/modules/pom.xml" ] || 
         [ "$pom_file" == "${PROJECT_ROOT}/modules/samstraumr-core/pom.xml" ]; then
        print_info "  Module POM detected - updating parent and version elements"
        
        # Update parent version
        if grep -q "<parent>" "$pom_file"; then
          sed -i "/<parent>/,/<\/parent>/s|<version>${current_version}</version>|<version>${new_version}</version>|" "$pom_file"
          print_info "  Updated parent version"
        fi
        
        # Update project version if it exists
        if grep -q "<version>${current_version}</version>" "$pom_file" | grep -v "<parent>"; then
          # Use more precise targeting to avoid changing dependency versions
          awk -v old="${current_version}" -v new="${new_version}" '
            # Flag when inside parent or dependency blocks
            /<parent>/ {in_parent=1}
            /<\/parent>/ {in_parent=0}
            /<dependency>/ {in_dep=1}
            /<\/dependency>/ {in_dep=0}
            
            # Only replace version outside dependency blocks
            !in_dep && !in_parent && /<version>/ && $0 ~ old {
              gsub("<version>" old "</version>", "<version>" new "</version>")
            }
            {print}
          ' "$pom_file" > "${pom_file}.temp" && mv "${pom_file}.temp" "$pom_file"
          
          print_info "  Updated module version"
        fi
        
        # Update samstraumr.version property if it exists
        if grep -q "<samstraumr.version>" "$pom_file"; then
          sed -i "s|<samstraumr.version>${current_version}</samstraumr.version>|<samstraumr.version>${new_version}</samstraumr.version>|" "$pom_file"
          print_info "  Updated samstraumr.version property"
        fi
      fi
      
      # Verify that at least some changes were made
      if diff -q "$pom_file" "$pom_backup" > /dev/null; then
        print_warning "  No changes made to $pom_file"
        pom_update_failures=$((pom_update_failures + 1))
      else
        # Check if the new version appears in the file at all
        local updated_count=$(grep -c "$new_version" "$pom_file")
        if [ "$updated_count" -gt 0 ]; then
          print_success "  Successfully updated $pom_file"
        else
          print_warning "  Update may have failed in $pom_file - new version not found"
          pom_update_failures=$((pom_update_failures + 1))
        fi
      fi
    else
      print_warning "  POM file not found: $pom_file"
      pom_update_failures=$((pom_update_failures + 1))
    fi
  done
  
  # Report overall success or failure with POM files
  if [ $pom_update_failures -eq 0 ]; then
    print_success "Successfully updated all POM files to version $new_version"
  else
    print_warning "Some POM files ($pom_update_failures) may not have been updated properly"
    
    # Offer detailed diagnostics
    print_info "Checking current POM file versions:"
    for pom_file in "${pom_files[@]}"; do
      if [ -f "$pom_file" ]; then
        local pom_version=$(grep -o "<version>[0-9]\+\.[0-9]\+\.[0-9]\+</version>" "$pom_file" | head -1 | sed 's/<version>\(.*\)<\/version>/\1/')
        print_info "  $pom_file: $pom_version"
      fi
    done
  fi
  
  # Update README.md
  # 1. Maven dependency example
  # 2. Version badge
  if [ -f "${PROJECT_ROOT}/README.md" ]; then
    # Create a backup of README.md
    local readme_backup="${PROJECT_ROOT}/README.md.bak"
    cp "${PROJECT_ROOT}/README.md" "$readme_backup"
    
    # Update Maven dependency version in the code example
    sed -i 's|<version>[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*</version>|<version>'"$new_version"'</version>|g' "${PROJECT_ROOT}/README.md"
    
    # Update version badge URL
    sed -i 's|version-[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*-blue|version-'"$new_version"'-blue|g' "${PROJECT_ROOT}/README.md"
    
    # Verify changes were made
    if grep -q "$new_version" "${PROJECT_ROOT}/README.md"; then
      print_success "Updated version references in README.md"
    else
      print_warning "No version changes detected in README.md"
    fi
  else
    print_warning "README.md not found, skipping update"
  fi
  
  # Look for CLAUDE.md if it exists and update version references there as well
  if [ -f "${PROJECT_ROOT}/CLAUDE.md" ]; then
    # Create a backup
    local claude_backup="${PROJECT_ROOT}/CLAUDE.md.bak"
    cp "${PROJECT_ROOT}/CLAUDE.md" "$claude_backup"
    
    # Update version references
    sed -i 's/[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*/'"$new_version"'/g' "${PROJECT_ROOT}/CLAUDE.md"
    
    # Verify changes were made
    if grep -q "$new_version" "${PROJECT_ROOT}/CLAUDE.md"; then
      print_success "Updated version references in CLAUDE.md"
    else
      print_warning "No version changes detected in CLAUDE.md"
    fi
  fi

  # Update the Changelog.md with new version entry
  if [ -f "${PROJECT_ROOT}/docs/reference/release/changelog.md" ]; then
    # Create a backup of changelog.md
    local changelog_backup="${PROJECT_ROOT}/docs/reference/release/changelog.md.bak"
    cp "${PROJECT_ROOT}/docs/reference/release/changelog.md" "$changelog_backup"
    
    # Extract current changelog content
    local changelog_content=$(cat "${PROJECT_ROOT}/docs/reference/release/changelog.md")
    
    # Today's date for the new entry
    local today_iso=$(date +"%Y-%m-%d")
    
    # Create new version section template
    local new_version_section="## [$new_version] - $today_iso

### Added

- 

### Changed

- 

### Fixed

- 

"
    
    # Find where to insert the new version (after the header, before the first version)
    local header_end=$(grep -n "All notable changes to this project will be documented in this file." "${PROJECT_ROOT}/docs/reference/release/changelog.md" | cut -d: -f1)
    
    if [ -n "$header_end" ]; then
      # Increment to get to the empty line after the header
      header_end=$((header_end + 2))
      
      # Create new changelog content
      local new_changelog_content=$(head -n $header_end "${PROJECT_ROOT}/docs/reference/release/changelog.md")
      new_changelog_content="${new_changelog_content}
${new_version_section}$(tail -n +$((header_end+1)) "${PROJECT_ROOT}/docs/reference/release/changelog.md")"
      
      # Write the new content
      echo "$new_changelog_content" > "${PROJECT_ROOT}/docs/reference/release/changelog.md"
      
      print_success "Added new version entry to changelog.md"
    else
      print_warning "Could not find proper insertion point in changelog.md"
    fi
  else
    print_warning "changelog.md not found at ${PROJECT_ROOT}/docs/reference/release/changelog.md, skipping update"
  fi
  
  # Success message
  print_section "Version Update Summary"
  print_success "Updated from $current_version to $new_version"
  print_info "- version.properties updated (source of truth)"
  print_info "- POM files synchronized"
  print_info "- README.md version references updated"
  print_info "- docs/reference/release/changelog.md updated with new entry"
  if [ -f "${PROJECT_ROOT}/CLAUDE.md" ]; then
    print_info "- CLAUDE.md version references updated"
  fi
  print_info "- Last updated date set to $today"
  
  # Suggest next steps
  print_info ""
  print_info "Suggested next steps:"
  print_info "1. Add notes to the new changelog entry for version $new_version"
  print_info "2. Review changes with: git diff"
  print_info "3. Create a commit: git commit -am \"Bump version to $new_version\""
  print_info "4. Create a tag: git tag -a v$new_version -m \"Version $new_version\""
  
  return 0
}

function is_git_tag_exists() {
  local tag_name="$1"
  git tag | grep -q "^$tag_name$"
  return $?
}

function check_version_tag_alignment() {
  local current_version=$(get_current_version)
  local tag_name="v$current_version"
  
  print_section "Version-Tag Alignment Check"
  print_info "Current version: $current_version"
  
  if is_git_tag_exists "$tag_name"; then
    print_success "Tag $tag_name exists and matches current version"
    return 0
  else
    print_warning "Tag $tag_name does not exist"
    return 1
  fi
}

function create_git_tag_for_version() {
  local version="$1"
  local tag_name="v$version"
  
  print_section "Creating Git Tag for Version $version"
  
  # Check if tag already exists
  if is_git_tag_exists "$tag_name"; then
    print_error "Tag $tag_name already exists"
    return 1
  fi
  
  # Create an annotated tag
  if git tag -a "$tag_name" -m "Version $version

Release version $version of Samstraumr."; then
    print_success "Created git tag $tag_name"
    print_info "To push the tag to remote, use: git push origin $tag_name"
    return 0
  else
    print_error "Failed to create git tag $tag_name"
    return 1
  fi
}

function commit_version_changes() {
  local new_version="$1"
  local bump_type="$2"
  
  print_section "Committing Version Changes"
  
  # Check for changes specifically in version-managed files
  if ! has_version_managed_changes; then
    print_warning "No version-managed files changed, skipping commit"
    return 0
  fi
  
  # Files to commit - use the centralized version-managed files list
  local file_list=("${VERSION_MANAGED_FILES[@]}")
  
  # Only add files that exist and have changes
  local changed_files=()
  for file in "${file_list[@]}"; do
    if [ -f "$file" ] && ! git diff-index --quiet HEAD -- "$file"; then
      changed_files+=("$file")
    fi
  done
  
  if [ ${#changed_files[@]} -eq 0 ]; then
    print_warning "No files changed, skipping commit"
    return 0
  fi
  
  # Create git add command - explicitly add each changed file
  git add "${changed_files[@]}"
  
  # Show which files are being committed
  print_info "Adding files to commit:"
  for file in "${changed_files[@]}"; do
    print_info "  - $file"
  done
  
  # Create commit message based on bump type
  local commit_message=""
  local current_version=$(get_current_version)
  
  case "$bump_type" in
    major)
      commit_message="Bump major version to $new_version

* Update version from $current_version to $new_version
* Major version change indicates breaking API changes
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    minor)
      commit_message="Bump minor version to $new_version

* Update version from $current_version to $new_version
* Minor version change indicates new features without breaking changes
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    patch)
      commit_message="Bump patch version to $new_version

* Update version from $current_version to $new_version
* Patch version change indicates bug fixes and small improvements
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    set)
      commit_message="Set version to $new_version

* Update version from $current_version to $new_version
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
  esac
  
  # Final commit message
  commit_message="$commit_message"
  
  # Create the commit
  if git commit -m "$commit_message"; then
    print_success "Created version commit"
    
    # Create git tag
    create_git_tag_for_version "$new_version"
    
    return 0
  else
    print_error "Failed to create version commit"
    return 1
  fi
}

function show_version_history() {
  print_section "Version History"
  
  git log --oneline --grep="Bump version to\|Bump major version to\|Bump minor version to\|Bump patch version to\|Set version to" | head -15
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------

# CHECK SETTINGS
# Define a centralized list of version-managed files for consistency across functions
# This ensures that the same set of files is updated, checked, and committed
VERSION_MANAGED_FILES=(
  # Source of truth - primary version file
  "${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/modules/version.properties}"
  
  # Maven POM files to keep in sync
  "${PROJECT_ROOT}/pom.xml"
  "${PROJECT_ROOT}/modules/pom.xml"
  "${PROJECT_ROOT}/modules/samstraumr-core/pom.xml"
  
  # Documentation files with version reference
  "${PROJECT_ROOT}/README.md"
  "${PROJECT_ROOT}/CLAUDE.md"
  "${PROJECT_ROOT}/docs/reference/release/changelog.md"
)

# Check if there are any changes to commit in version-managed files
# Usage: if has_version_managed_changes; then ... fi
# Returns: 0 if there are changes, 1 otherwise
function has_version_managed_changes() {
  # List of synchronized version files to check
  local files_to_check=("${VERSION_MANAGED_FILES[@]}")
  
  # Check for changes in any of the version-related files
  for file in "${files_to_check[@]}"; do
    if [ -f "$file" ] && ! git diff-index --quiet HEAD -- "$file" 2>/dev/null; then
      print_debug "Changed file: $file"
      return 0  # Changes detected
    fi
  done
  
  # Check for untracked version files
  for file in "${files_to_check[@]}"; do
    if [ -f "$file" ] && git ls-files --error-unmatch -- "$file" 2>/dev/null; then
      local status=$(git status --porcelain -- "$file" 2>/dev/null)
      if [ -n "$status" ]; then
        print_debug "Untracked/modified file: $file ($status)"
        return 0  # Untracked/modified file
      fi
    fi
  done
  
  print_debug "No changes detected in version-managed files"
  return 1  # No changes detected
}

# Export functions
export -f get_current_version
export -f get_last_updated_date
export -f calculate_new_version
export -f update_version_in_files
export -f is_git_tag_exists
export -f check_version_tag_alignment
export -f create_git_tag_for_version
export -f commit_version_changes
export -f show_version_history
export -f has_version_managed_changes

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded version-lib.sh library"
fi