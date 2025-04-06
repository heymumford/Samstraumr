#!/bin/bash
#==============================================================================
# update-readme.sh - Updates the README.md file with the latest project info
# This script updates version, badges, and other dynamic content in README.md
#==============================================================================

set -e

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
  # Terminal colors
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  NC='\033[0m' # No Color

  # Functions for prettier output
  info() { echo -e "${BLUE}$1${NC}"; }
  success() { echo -e "${GREEN}$1${NC}"; }
  error() { echo -e "${RED}Error: $1${NC}" >&2; }
  warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }
fi

# Default README.md
README_FILE="${PROJECT_ROOT}/README.md"

# Parameter handling
show_help() {
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null && type print_info &>/dev/null; then
    print_header "README Updater Help"
    print_info "Updates the README.md file with the latest project info"
    echo
    
    print_bold "Usage: $(basename "$0") [options]"
    echo
    
    print_bold "Options:"
    echo "  -r, --readme FILE    Path to README file (default: ./README.md)"
    echo "  -t, --template FILE  Path to README template file"
    echo "  -s, --sections LIST  Comma-separated list of sections to update (version,badges,usage,all)"
    echo "  -h, --help           Show this help message"
    echo
    
    print_bold "Examples:"
    echo "  $(basename "$0") --sections version,badges"
    echo "  $(basename "$0") --template docs/templates/README-template.md"
  else
    # Fallback to original implementation
    echo "Usage: $(basename "$0") [options]"
    echo
    echo "Options:"
    echo "  -r, --readme FILE    Path to README file (default: ./README.md)"
    echo "  -t, --template FILE  Path to README template file"
    echo "  -s, --sections LIST  Comma-separated list of sections to update (version,badges,usage,all)"
    echo "  -h, --help           Show this help message"
    echo
    echo "Examples:"
    echo "  $(basename "$0") --sections version,badges"
    echo "  $(basename "$0") --template docs/templates/README-template.md"
  fi
}

# Default settings
UPDATE_SECTIONS="all"
TEMPLATE_FILE=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    -r|--readme)
      README_FILE="$2"
      shift 2
      ;;
    -t|--template)
      TEMPLATE_FILE="$2"
      shift 2
      ;;
    -s|--sections)
      UPDATE_SECTIONS="$2"
      shift 2
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done

info "Updating README file: ${README_FILE}"

# Create a backup of the original README
BACKUP_FILE="${README_FILE}.bak"
cp "$README_FILE" "$BACKUP_FILE"
info "Created backup at ${BACKUP_FILE}"

# Get current project version
get_project_version() {
  # Use the common library function if available
  if [[ "$USING_LIB" == true ]] && type get_maven_property &>/dev/null; then
    # Attempt to use the unified library function
    local version
    
    # First try the version.properties file
    if [ -f "${PROJECT_ROOT}/Samstraumr/version.properties" ]; then
      version=$(grep "version=" "${PROJECT_ROOT}/Samstraumr/version.properties" | cut -d= -f2)
    fi
    
    # If not found, try to get from pom.xml using the library function
    if [ -z "$version" ] && [ -f "${PROJECT_ROOT}/pom.xml" ]; then
      version=$(get_maven_property "${PROJECT_ROOT}/pom.xml" "project.version")
    fi
    
    echo "$version"
  else
    # Fall back to original implementation
    # Try to get version from version.properties
    if [ -f "${PROJECT_ROOT}/Samstraumr/version.properties" ]; then
      grep "version=" "${PROJECT_ROOT}/Samstraumr/version.properties" | cut -d= -f2
    else
      # Fall back to extracting from pom.xml
      grep -m 1 "<version>" "${PROJECT_ROOT}/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/'
    fi
  fi
}

# Get project name
get_project_name() {
  # Use the common library function if available
  if [[ "$USING_LIB" == true ]] && type get_maven_property &>/dev/null; then
    # Try to extract project name using the library function
    get_maven_property "${PROJECT_ROOT}/pom.xml" "project.name"
  else
    # Extract name from root pom.xml
    grep -m 1 "<n>" "${PROJECT_ROOT}/pom.xml" | sed 's/.*<n>\(.*\)<\/n>.*/\1/'
  fi
}

# Update version references in README
update_version_references() {
  local file="$1"
  local version="$(get_project_version)"
  local version_badge="![Version](https://img.shields.io/badge/version-${version}-blue.svg)"
  
  info "Updating version references to: ${version}"
  
  # Use temporary file for atomic updates
  local temp_file=$(mktemp)
  
  # Update Maven dependency versions
  sed -E "s/<version>[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9]+)?<\/version>/<version>${version}<\/version>/g" "$file" > "$temp_file"
  
  # Update version badge
  sed -i -E "s|!\[Version\]\(https://img\.shields\.io/badge/version-[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9]+)?-blue\.svg\)|${version_badge}|g" "$temp_file"
  
  # Update plain version references
  sed -i -E "s/Version: [0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9]+)?/Version: ${version}/g" "$temp_file"
  
  # Move the temp file back to the original
  mv "$temp_file" "$file"
}

# Update build badges in README
update_badges() {
  local file="$1"
  local temp_file=$(mktemp)
  
  info "Updating badges"
  
  # Define badges
  local version="$(get_project_version)"
  local version_badge="![Version](https://img.shields.io/badge/version-${version}-blue.svg)"
  local license_badge="![License](https://img.shields.io/badge/license-MPL--2.0-green.svg)"
  local java_badge="![Java](https://img.shields.io/badge/java-21-orange.svg)"
  
  # Find badge section in README (between <!-- BADGES START --> and <!-- BADGES END -->)
  if grep -q "<!-- BADGES START -->" "$file"; then
    # Extract before, badge section, and after
    awk '/<!-- BADGES START -->/{print; getline; while(!/<!-- BADGES END -->/){getline}; print; next}1' "$file" > "$temp_file"
    
    # Insert badges after <!-- BADGES START -->
    sed -i "/<!-- BADGES START -->/a\\
${version_badge} ${license_badge} ${java_badge}" "$temp_file"
  else
    # If no badge section exists, look for the first heading
    awk 'NR==1,/^# /{if(/^# /){print; print ""; print "<!-- BADGES START -->"; print "'"${version_badge} ${license_badge} ${java_badge}"'"; print "<!-- BADGES END -->"; print ""; next}}1' "$file" > "$temp_file"
  fi
  
  # Move the temp file back to the original
  mv "$temp_file" "$file"
}

# Update usage examples
update_usage_examples() {
  local file="$1"
  local version="$(get_project_version)"
  local temp_file=$(mktemp)
  
  info "Updating usage examples"
  
  # Find usage example section in README (between <!-- USAGE START --> and <!-- USAGE END -->)
  if grep -q "<!-- USAGE START -->" "$file"; then
    # Extract before and after the usage section
    awk '/<!-- USAGE START -->/{print; getline; while(!/<!-- USAGE END -->/){getline}; print; next}1' "$file" > "$temp_file"
    
    # Insert updated usage examples
    sed -i "/<!-- USAGE START -->/a\\
\`\`\`xml\\
<dependency>\\
  <groupId>org.s8r</groupId>\\
  <artifactId>samstraumr-core</artifactId>\\
  <version>${version}</version>\\
</dependency>\\
\`\`\`" "$temp_file"
  else
    # If no usage section exists, don't modify
    cp "$file" "$temp_file"
  fi
  
  # Move the temp file back to the original
  mv "$temp_file" "$file"
}

# Update README from template
update_from_template() {
  local template="$1"
  local output="$2"
  local version="$(get_project_version)"
  local project_name="$(get_project_name)"
  
  info "Generating README from template: ${template}"
  
  # Create a temporary file
  local temp_file=$(mktemp)
  
  # Replace template variables
  sed -e "s/\${version}/${version}/g" \
      -e "s/\${project.name}/${project_name}/g" \
      -e "s/\${year}/$(date +%Y)/g" \
      -e "s/\${date}/$(date +%Y-%m-%d)/g" \
      "$template" > "$temp_file"
  
  # Update the file
  mv "$temp_file" "$output"
}

# Update README based on specified sections
if [[ -n "$TEMPLATE_FILE" && -f "$TEMPLATE_FILE" ]]; then
  # If template is provided, use it as the base
  update_from_template "$TEMPLATE_FILE" "$README_FILE"
  success "README updated from template"
else
  # Otherwise, update specified sections of existing README
  IFS=',' read -ra SECTIONS <<< "$UPDATE_SECTIONS"
  
  for section in "${SECTIONS[@]}"; do
    case "$section" in
      version)
        update_version_references "$README_FILE"
        ;;
      badges)
        update_badges "$README_FILE"
        ;;
      usage)
        update_usage_examples "$README_FILE"
        ;;
      all)
        update_version_references "$README_FILE"
        update_badges "$README_FILE"
        update_usage_examples "$README_FILE"
        break
        ;;
      *)
        warning "Unknown section: $section"
        ;;
    esac
  done
  
  success "README updated successfully"
fi