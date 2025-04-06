#!/bin/bash
#==============================================================================
# generate-changelog.sh - Generates a changelog from git commit history
# This script analyzes git commit history and generates a structured changelog
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

# Default output file
OUTPUT_FILE="${PROJECT_ROOT}/CHANGELOG.md"

# Default range: compare current with previous version
FROM_TAG="$(git describe --abbrev=0 --tags 2>/dev/null || echo "")"
if [ -z "$FROM_TAG" ]; then
  # If no tags exist, use initial commit
  FROM_TAG="$(git rev-list --max-parents=0 HEAD)"
fi
TO_TAG="HEAD"

# Override defaults with command line arguments
show_help() {
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null && type print_info &>/dev/null; then
    print_header "Changelog Generator Help"
    print_info "Generates a structured changelog from git commit history"
    echo
    
    print_bold "Usage: $(basename "$0") [options]"
    echo
    
    print_bold "Options:"
    echo "  -f, --from TAG       Start tag or commit (default: latest tag)"
    echo "  -t, --to TAG         End tag or commit (default: HEAD)"
    echo "  -o, --output FILE    Output file (default: CHANGELOG.md)"
    echo "  -u, --update         Update existing changelog instead of overwriting"
    echo "  -h, --help           Show this help message"
    echo
    
    print_bold "Examples:"
    echo "  $(basename "$0") --from v1.0.0 --to v2.0.0"
    echo "  $(basename "$0") --from 0c7d3a1 --to HEAD"
    echo "  $(basename "$0") --output docs/CHANGELOG.md"
  else
    # Fallback to original implementation
    echo "Usage: $(basename "$0") [options]"
    echo
    echo "Options:"
    echo "  -f, --from TAG       Start tag or commit (default: latest tag)"
    echo "  -t, --to TAG         End tag or commit (default: HEAD)"
    echo "  -o, --output FILE    Output file (default: CHANGELOG.md)"
    echo "  -u, --update         Update existing changelog instead of overwriting"
    echo "  -h, --help           Show this help message"
    echo
    echo "Examples:"
    echo "  $(basename "$0") --from v1.0.0 --to v2.0.0"
    echo "  $(basename "$0") --from 0c7d3a1 --to HEAD"
    echo "  $(basename "$0") --output docs/CHANGELOG.md"
  fi
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -f|--from)
      FROM_TAG="$2"
      shift 2
      ;;
    -t|--to)
      TO_TAG="$2"
      shift 2
      ;;
    -o|--output)
      OUTPUT_FILE="$2"
      shift 2
      ;;
    -u|--update)
      UPDATE_MODE=true
      shift
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

info "Generating changelog from ${FROM_TAG} to ${TO_TAG}"

# Get the current project version
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

VERSION=$(get_project_version)
info "Current project version: ${VERSION}"

# Create a temporary file
TEMP_FILE=$(mktemp)

# Function to parse commits and organize by type
parse_commits() {
  local from_tag="$1"
  local to_tag="$2"
  local output_file="$3"
  
  # Map to collect commits by type
  declare -A COMMIT_TYPES=(
    ["feat"]="Features"
    ["fix"]="Bug Fixes"
    ["docs"]="Documentation"
    ["style"]="Styling"
    ["refactor"]="Refactoring"
    ["perf"]="Performance"
    ["test"]="Tests"
    ["build"]="Build System"
    ["ci"]="CI/CD"
    ["chore"]="Chores"
    ["revert"]="Reverts"
    ["security"]="Security"
  )
  
  # Initialize arrays for each commit type
  for type in "${!COMMIT_TYPES[@]}"; do
    declare -a "commits_${type}=()"
  done
  
  # Array for uncategorized commits
  declare -a commits_other=()
  
  # Parse commits and organize by type
  while IFS= read -r commit; do
    # Skip empty lines
    [ -z "$commit" ] && continue
    
    # Extract commit hash
    hash=$(echo "$commit" | cut -d' ' -f1)
    # Extract commit message
    message=$(echo "$commit" | cut -d' ' -f2-)
    
    # Skip merge commits and version bump commits
    if [[ "$message" == Merge* || "$message" == "chore: bump version"* ]]; then
      continue
    fi
    
    # Check for conventional commit format: type: message
    if [[ "$message" =~ ^([a-z]+)(\([a-z0-9_-]+\))?!?:\ (.+)$ ]]; then
      type="${BASH_REMATCH[1]}"
      # If the commit type is in our map, add it to the corresponding array
      if [[ -n "${COMMIT_TYPES[$type]}" ]]; then
        # Extract detail (after the type and scope)
        detail="${BASH_REMATCH[3]}"
        
        # Look for reference to issue/PR with "#123"
        issue_ref=""
        if [[ "$detail" =~ \#([0-9]+) ]]; then
          issue_num="${BASH_REMATCH[1]}"
          issue_ref=" ([#${issue_num}](https://github.com/emumford/Samstraumr/issues/${issue_num}))"
        fi
        
        # Format the commit entry and add to appropriate array
        commit_entry="* \`${hash:0:7}\` ${detail}${issue_ref}"
        eval "commits_${type}+=(\"$commit_entry\")"
      else
        # Format as uncategorized and add to other array
        commits_other+=("* \`${hash:0:7}\` ${message}")
      fi
    else
      # Format as uncategorized and add to other array
      commits_other+=("* \`${hash:0:7}\` ${message}")
    fi
  done < <(git log --pretty=format:"%h %s" "${from_tag}..${to_tag}")
  
  # Generate the changelog
  {
    echo "# Changelog"
    echo
    echo "All notable changes to this project will be documented in this file."
    echo
    echo "## [${VERSION}] - $(date +%Y-%m-%d)"
    echo
    
    # Add each commit type section if it has entries
    for type in "${!COMMIT_TYPES[@]}"; do
      # Get array variable name
      array_var="commits_${type}[@]"
      
      # Check if array has elements
      if [ ${#array_var} -gt 0 ]; then
        echo "### ${COMMIT_TYPES[$type]}"
        echo
        
        # Print each commit in this section
        eval "printf '%s\n' \"\${$array_var}\""
        echo
      fi
    done
    
    # Add other commits if any
    if [ ${#commits_other[@]} -gt 0 ]; then
      echo "### Other Changes"
      echo
      printf '%s\n' "${commits_other[@]}"
      echo
    fi
    
    # Add version link at the bottom
    local repo_url="https://github.com/emumford/Samstraumr"
    echo "[${VERSION}]: ${repo_url}/compare/${from_tag}...${to_tag}"
  } > "$TEMP_FILE"
  
  success "Generated changelog entries for version ${VERSION}"
}

# Generate new changelog content
parse_commits "$FROM_TAG" "$TO_TAG" "$TEMP_FILE"

# Create the reports directory if it doesn't exist
if [[ "$USING_LIB" == true ]] && [ ! -d "$REPORT_DIR" ]; then
  mkdir -p "$REPORT_DIR"
fi

# Handle update mode if specified
if [ "$UPDATE_MODE" = true ] && [ -f "$OUTPUT_FILE" ]; then
  info "Updating existing changelog at ${OUTPUT_FILE}"
  
  # Create a temporary file for the new content
  NEW_TEMP=$(mktemp)
  
  # Extract header from existing changelog (lines before first "## [")
  awk '/^## \[/{exit} {print}' "$OUTPUT_FILE" > "$NEW_TEMP"
  
  # Add new content (everything after the first line in temp file)
  tail -n +2 "$TEMP_FILE" >> "$NEW_TEMP"
  
  # Add remaining content from existing changelog (everything after the header)
  awk 'BEGIN{found=0} /^## \[/{found=1} found{print}' "$OUTPUT_FILE" >> "$NEW_TEMP"
  
  # Replace temp file with new combined content
  mv "$NEW_TEMP" "$TEMP_FILE"
else
  info "Creating new changelog at ${OUTPUT_FILE}"
fi

# Move the temp file to the output location
mv "$TEMP_FILE" "$OUTPUT_FILE"

# Also save a copy in the reports directory if we're using the library
if [[ "$USING_LIB" == true ]] && [ -d "$REPORT_DIR" ]; then
  cp "$OUTPUT_FILE" "${REPORT_DIR}/changelog-$(date +%Y%m%d).md"
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "Changelog also saved to ${REPORT_DIR}/changelog-$(date +%Y%m%d).md"
  fi
fi

success "Changelog successfully generated at ${OUTPUT_FILE}"