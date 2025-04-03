#!/bin/bash
# Samstraumr Version Manager
# A unified interface for managing project versions
#
# Usage:
#   ./util/version <command> [options]
#
# Commands:
#   get                Show current version information
#   bump major         Bump the major version (X.y.z → X+1.0.0)
#   bump minor         Bump the minor version (x.Y.z → x.Y+1.0)
#   bump patch         Bump the patch version (x.y.Z → x.y.Z+1)
#   set x.y.z          Set a specific version number
#   history            Show version history
#
# Options:
#   --no-commit        Don't automatically commit the version change
#   --help             Show this help message

set -e

# ANSI color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print styled messages
print_header() {
  echo -e "${BLUE}===== $1 =====${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}! $1${NC}"
}

print_info() {
  echo -e "${CYAN}→ $1${NC}"
}

# Function to display usage information
show_usage() {
  echo -e "${BLUE}Samstraumr Version Manager${NC}"
  echo "A unified interface for managing project versions"
  echo ""
  echo -e "Usage: ${YELLOW}./util/version <command> [options]${NC}"
  echo ""
  echo "Commands:"
  echo "  get                Show current version information"
  echo "  export|echo        Output only the current version (for scripts and CI)"
  echo "  bump major         Bump the major version (X.y.z → X+1.0.0)"
  echo "  bump minor         Bump the minor version (x.Y.z → x.Y+1.0)"
  echo "  bump patch         Bump the patch version (x.y.Z → x.y.Z+1)"
  echo "  set x.y.z          Set a specific version number"
  echo "  history            Show version history"
  echo ""
  echo "Options:"
  echo "  --no-commit        Don't automatically commit the version change"
  echo "  --help             Show this help message"
}

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Version file path
VERSION_PROPERTIES_FILE="Samstraumr/version.properties"

# Check if version.properties exists
if [ ! -f "$VERSION_PROPERTIES_FILE" ]; then
  print_error "Version properties file not found at: $VERSION_PROPERTIES_FILE"
  exit 1
fi

# Function to get the current version
get_current_version() {
  grep "samstraumr.version=" "$VERSION_PROPERTIES_FILE" | cut -d= -f2
}

# Function to get the last updated date
get_last_updated() {
  grep "samstraumr.last.updated=" "$VERSION_PROPERTIES_FILE" | cut -d= -f2
}

# Function to show current version info
show_version_info() {
  local version=$(get_current_version)
  local updated=$(get_last_updated)
  local maintainer=$(grep "samstraumr.maintainer=" "$VERSION_PROPERTIES_FILE" | cut -d= -f2)
  local license=$(grep "samstraumr.license=" "$VERSION_PROPERTIES_FILE" | cut -d= -f2)
  
  print_header "Samstraumr Version Information"
  echo -e "Version:     ${GREEN}$version${NC}"
  echo -e "Updated:     ${CYAN}$updated${NC}"
  echo -e "Maintainer:  ${CYAN}$maintainer${NC}"
  echo -e "License:     ${CYAN}$license${NC}"
}

# Function to calculate a new version number
calculate_new_version() {
  local current_version=$(get_current_version)
  local bump_type=$1
  
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
      exit 1
      ;;
  esac
}

# Function to update version in all files
update_version() {
  local current_version=$(get_current_version)
  local new_version=$1
  
  print_header "Updating Version to $new_version"
  echo -e "Current version: ${YELLOW}$current_version${NC}"
  echo -e "New version:     ${GREEN}$new_version${NC}"
  
  # Update version.properties - This is the source of truth for version
  sed -i "s/samstraumr.version=.*/samstraumr.version=$new_version/" "$VERSION_PROPERTIES_FILE"
  print_success "Updated version.properties"
  
  # Update last updated date
  local today=$(date +"%B %d, %Y")
  sed -i "s/samstraumr.last.updated=.*/samstraumr.last.updated=$today/" "$VERSION_PROPERTIES_FILE"
  print_success "Updated last updated date to $today"
  
  # Update all POM files including parent and all modules
  # This ensures consistent version across all modules
  local pom_files=(
    "pom.xml"
    "Samstraumr/pom.xml"
    "Samstraumr/samstraumr-core/pom.xml"
  )
  
  echo "Updating version in POM files:"
  for pom_file in "${pom_files[@]}"; do
    if [ -f "$pom_file" ]; then
      # Update version tags
      sed -i "s/<version>$current_version<\/version>/<version>$new_version<\/version>/g" "$pom_file"
      # Update properties
      sed -i "s/<samstraumr.version>$current_version<\/samstraumr.version>/<samstraumr.version>$new_version<\/samstraumr.version>/g" "$pom_file"
      # Update parent version references if applicable
      sed -i "s/<parent>.*<version>$current_version<\/version>/<parent>\\n    <groupId>org.samstraumr<\/groupId>\\n    <artifactId>samstraumr-modules<\/artifactId>\\n    <version>$new_version<\/version>/g" "$pom_file"
      echo "  - Updated $pom_file"
    fi
  done
  print_success "Updated all POM files to version $new_version"
  
  # Update README.md version and badge
  if [ -f "README.md" ]; then
    # Update version string
    sed -i "s/Version: .*/Version: $new_version/g" README.md
    # Update version badge URL
    sed -i "s|version-[0-9.]\+-blue|version-$new_version-blue|g" README.md
    print_success "Updated version and badge in README.md"
  fi
  
  # Apply code formatting after version update
  print_header "Running Quality Checks After Version Update"
  # Run Spotless to ensure formatting is correct after changes
  if command -v mvn &> /dev/null; then
    echo "Running Spotless formatting check..."
    if mvn spotless:apply -q; then
      print_success "Code formatting applied successfully"
    else
      print_warning "Spotless formatting failed - some files may need manual formatting"
    fi
  else
    print_warning "Maven not available - skipping formatting check"
  fi
  
  print_header "Version Update Complete"
  echo "All files have been updated to version $new_version"
  echo "Changes will be committed automatically unless --no-commit was specified"
  echo ""
  echo "To build the project with the new version, run:"
  echo -e "  ${CYAN}./util/build/build-optimal.sh clean${NC}"
}

# Function to commit version changes and optionally create a tag
commit_version_changes() {
  local new_version=$1
  local bump_type=$2
  
  print_info "Creating commit for version $new_version"
  
  local file_list=(
    "README.md"
    "$VERSION_PROPERTIES_FILE"
    "pom.xml"
    "Samstraumr/pom.xml"
    "Samstraumr/samstraumr-core/pom.xml"
    "docs/guides/getting-started.md"
    "CLAUDE.md"
    "util/badges/generate-badges.sh"
    ".github/workflows/samstraumr-pipeline.yml"
  )
  
  # Only add files that exist and have changes
  local changed_files=()
  for file in "${file_list[@]}"; do
    if [ -f "$file" ] && git diff --quiet -- "$file"; then
      continue
    elif [ -f "$file" ]; then
      changed_files+=("$file")
    fi
  done
  
  if [ ${#changed_files[@]} -eq 0 ]; then
    print_warning "No files changed, skipping commit"
    return
  fi
  
  # Create Git commit with proper message
  git add "${changed_files[@]}"
  
  case "$bump_type" in
    major)
      commit_message="Bump major version to $new_version

* Update version from $(get_current_version) to $new_version
* Major version change indicates breaking API changes
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    minor)
      commit_message="Bump minor version to $new_version

* Update version from $(get_current_version) to $new_version
* Minor version change indicates new features without breaking changes
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    patch)
      commit_message="Bump patch version to $new_version

* Update version from $(get_current_version) to $new_version
* Patch version change indicates bug fixes and small improvements
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
    set)
      commit_message="Set version to $new_version

* Update version from $(get_current_version) to $new_version
* Update last updated date to $(date +"%B %d, %Y")"
      ;;
  esac
  
  # Add Claude attribution
  commit_message="$commit_message

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"
  
  # Create the commit
  git commit -m "$commit_message"
  print_success "Created version commit"
  
  # Ask user if they want to create a git tag
  echo -n "Would you like to create a git tag for version $new_version? (y/n): "
  read -r response
  if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    # Create an annotated tag
    git tag -a "v$new_version" -m "Version $new_version

Release version $new_version of Samstraumr.

Generated with Claude Code"
    
    print_success "Created git tag v$new_version"
    echo "To push the tag to remote, use:"
    echo -e "  ${YELLOW}git push origin v$new_version${NC}"
  fi
}

# Function to display version history
show_version_history() {
  print_header "Samstraumr Version History"
  git log --oneline --grep="Bump version to\|Bump major version to\|Bump minor version to\|Bump patch version to\|Set version to" | head -15
}

# Main command processing
if [ $# -eq 0 ]; then
  show_usage
  exit 0
fi

COMMAND=$1
shift

# Default options
AUTO_COMMIT=true
BUMP_TYPE=""

# First argument for "bump" command is the type, don't treat it as an option
if [ "$COMMAND" = "bump" ] && [ $# -gt 0 ]; then
  BUMP_TYPE=$1
  shift
fi

# First argument for "set" command is the version, don't treat it as an option
if [ "$COMMAND" = "set" ] && [ $# -gt 0 ]; then
  NEW_VERSION=$1
  shift
fi

# Process options
while [ $# -gt 0 ]; do
  case "$1" in
    --no-commit)
      AUTO_COMMIT=false
      shift
      ;;
    --help)
      show_usage
      exit 0
      ;;
    *)
      print_error "Unknown option: $1"
      show_usage
      exit 1
      ;;
  esac
done

# Process commands
case "$COMMAND" in
  get)
    show_version_info
    ;;
  
  export|echo)
    echo "$(get_current_version)"
    ;;
  
  bump)
    if [ -z "$BUMP_TYPE" ]; then
      print_error "Missing bump type (major, minor, patch)"
      show_usage
      exit 1
    fi
    
    if [[ ! "$BUMP_TYPE" =~ ^(major|minor|patch)$ ]]; then
      print_error "Invalid bump type: $BUMP_TYPE. Must be major, minor, or patch."
      exit 1
    fi
    
    NEW_VERSION=$(calculate_new_version "$BUMP_TYPE")
    update_version "$NEW_VERSION"
    
    if [ "$AUTO_COMMIT" = true ]; then
      commit_version_changes "$NEW_VERSION" "$BUMP_TYPE"
    else
      print_info "Changes not committed (--no-commit option used)"
      print_info "To commit changes manually, use:"
      echo -e "  ${YELLOW}git add README.md $VERSION_PROPERTIES_FILE pom.xml Samstraumr/pom.xml Samstraumr/samstraumr-core/pom.xml${NC}"
      echo -e "  ${YELLOW}git commit -m \"Bump $BUMP_TYPE version to $NEW_VERSION\"${NC}"
    fi
    ;;
  
  set)
    if [ -z "$NEW_VERSION" ]; then
      print_error "Missing version number to set"
      show_usage
      exit 1
    fi
    # Validate version format (x.y.z)
    if ! [[ $NEW_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      print_error "Invalid version format: $NEW_VERSION. Must be in format x.y.z"
      exit 1
    fi
    
    update_version "$NEW_VERSION"
    
    if [ "$AUTO_COMMIT" = true ]; then
      commit_version_changes "$NEW_VERSION" "set"
    else
      print_info "Changes not committed (--no-commit option used)"
      print_info "To commit changes manually, use:"
      echo -e "  ${YELLOW}git add README.md $VERSION_PROPERTIES_FILE pom.xml Samstraumr/pom.xml Samstraumr/samstraumr-core/pom.xml${NC}"
      echo -e "  ${YELLOW}git commit -m \"Set version to $NEW_VERSION\"${NC}"
    fi
    ;;
  
  history)
    show_version_history
    ;;
  
  *)
    print_error "Unknown command: $COMMAND"
    show_usage
    exit 1
    ;;
esac

exit 0