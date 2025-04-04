#!/bin/bash
#==============================================================================
# Filename: version-lib.sh
# Description: Shared functions for version management scripts
#==============================================================================

# Determine script paths and load common library
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the common library
source "${SCRIPT_LIB_DIR}/common.sh"

#------------------------------------------------------------------------------
# Version Management Functions
#------------------------------------------------------------------------------

function get_current_version() {
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
  
  if [ ! -f "$version_file" ]; then
    print_error "Version properties file not found: $version_file"
    return 1
  }
  
  grep "samstraumr.version=" "$version_file" | cut -d= -f2
}

function get_last_updated_date() {
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
  
  if [ ! -f "$version_file" ]; then
    print_error "Version properties file not found: $version_file"
    return 1
  }
  
  grep "samstraumr.last.updated=" "$version_file" | cut -d= -f2
}

function calculate_new_version() {
  local current_version=$(get_current_version)
  local bump_type="$1"
  
  # Split version into components
  IFS='.' read -r major minor patch <<< "$current_version"
  
  case "$bump_type" in
    major)
      echo "$((major + 1)).0.0"
      ;;
    minor)
      echo "$major.$((minor + 1)).0"
      ;;
    patch)
      echo "$major.$minor.$((patch + 1))"
      ;;
    *)
      print_error "Invalid bump type: $bump_type"
      return 1
      ;;
  esac
}

function update_version_in_files() {
  local current_version="$1"
  local new_version="$2"
  local version_file="${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
  
  print_section "Updating Version to $new_version"
  print_info "Current version: $current_version"
  print_info "New version:     $new_version"
  
  # Update version.properties - This is the source of truth for version
  sed -i "s/samstraumr.version=.*/samstraumr.version=$new_version/" "$version_file"
  print_success "Updated version.properties"
  
  # Update last updated date
  local today=$(date +"%B %d, %Y")
  sed -i "s/samstraumr.last.updated=.*/samstraumr.last.updated=$today/" "$version_file"
  print_success "Updated last updated date to $today"
  
  # Update all POM files including parent and all modules
  # This ensures consistent version across all modules
  local pom_files=(
    "${PROJECT_ROOT}/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/samstraumr-core/pom.xml"
  )
  
  print_info "Updating version in POM files:"
  for pom_file in "${pom_files[@]}"; do
    if [ -f "$pom_file" ]; then
      # Update version tags
      sed -i "s/<version>$current_version<\/version>/<version>$new_version<\/version>/g" "$pom_file"
      
      # Update properties
      sed -i "s/<samstraumr.version>$current_version<\/samstraumr.version>/<samstraumr.version>$new_version<\/samstraumr.version>/g" "$pom_file"
      
      # Update parent version references if applicable
      sed -i "s/<parent>.*<version>$current_version<\/version>/<parent>\\n    <groupId>org.samstraumr<\/groupId>\\n    <artifactId>samstraumr-modules<\/artifactId>\\n    <version>$new_version<\/version>/g" "$pom_file"
      
      print_info "  Updated $pom_file"
    fi
  done
  
  print_success "Updated all POM files to version $new_version"
  
  # Update README.md version and badge
  if [ -f "${PROJECT_ROOT}/README.md" ]; then
    # Update version string
    sed -i "s/Version: .*/Version: $new_version/g" "${PROJECT_ROOT}/README.md"
    
    # Update version badge URL
    sed -i "s|version-[0-9.]\+-blue|version-$new_version-blue|g" "${PROJECT_ROOT}/README.md"
    
    print_success "Updated version and badge in README.md"
  fi
  
  print_success "Version update complete"
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
  
  # Check for changes
  if ! has_git_changes; then
    print_warning "No files changed, skipping commit"
    return 0
  fi
  
  # Files to commit
  local file_list=(
    "${PROJECT_ROOT}/README.md"
    "${VERSION_PROPERTIES_FILE:-${PROJECT_ROOT}/Samstraumr/version.properties}"
    "${PROJECT_ROOT}/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/pom.xml"
    "${PROJECT_ROOT}/Samstraumr/samstraumr-core/pom.xml"
    "${PROJECT_ROOT}/docs/guides/getting-started.md"
    "${PROJECT_ROOT}/CLAUDE.md"
  )
  
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
  
  # Create git add command
  git add "${changed_files[@]}"
  
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
  
  # Add Claude attribution
  commit_message="$commit_message

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
  
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

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded version-lib.sh library"
fi