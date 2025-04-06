#!/bin/bash
#==============================================================================
# update-cross-references.sh
# This script updates cross-references in markdown files to match the new
# directory structure and file naming conventions
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Directory mapping (old to new paths)
declare -A DIR_MAPPING=(
  ["docs/architecture"]="docs/architecture"
  ["docs/building"]="docs/reference/build"
  ["docs/contribution"]="docs/contrib"
  ["docs/core"]="docs/concepts"
  ["docs/dev"]="docs/dev"
  ["docs/development"]="docs/dev"
  ["docs/general"]="docs/reference"
  ["docs/guide"]="docs/guides"
  ["docs/guides"]="docs/guides"
  ["docs/planning"]="docs/plans"
  ["docs/plans"]="docs/plans"
  ["docs/proposals"]="docs/research"
  ["docs/reference"]="docs/reference"
  ["docs/ref"]="docs/reference/standards"
  ["docs/research"]="docs/research"
  ["docs/specs"]="docs/reference"
  ["docs/testing"]="docs/testing"
  ["docs/tools"]="docs/scripts"
)

# Create a directory for reports
REPORT_DIR="${PROJECT_ROOT}/target/cross-ref-reports"
mkdir -p "$REPORT_DIR"

# Create a file mapping for renamed files
create_file_mapping() {
  header "Creating file mapping database"
  
  local mapping_file="${REPORT_DIR}/file-mapping.txt"
  > "$mapping_file"  # Clear the file
  
  # Find all markdown files
  find "${PROJECT_ROOT}/docs" -type f -name "*.md" | while read -r file; do
    local rel_path="${file#${PROJECT_ROOT}/}"
    local dir_name=$(dirname "$rel_path")
    local file_name=$(basename "$file")
    
    # Store in the mapping file
    echo "$rel_path" >> "$mapping_file"
  done
  
  success "Created file mapping with $(wc -l < "$mapping_file") entries"
  return 0
}

# Find potential broken links
check_links() {
  header "Analyzing cross-references"
  
  local report_file="${REPORT_DIR}/link-report.md"
  local broken_links=0
  
  {
    echo "# Cross-Reference Analysis Report"
    echo "Generated on: $(date)"
    echo
    echo "## Potential Broken Links"
    echo
  } > "$report_file"
  
  # Find all markdown files and check their links
  find "${PROJECT_ROOT}/docs" -type f -name "*.md" | sort | while read -r file; do
    local rel_path="${file#${PROJECT_ROOT}/}"
    local file_links=0
    local broken_file_links=0
    
    # Extract markdown links from the file
    grep -o -E '\[[^]]+\]\([^)]+\)' "$file" | while read -r link; do
      # Extract just the URL part
      local url=$(echo "$link" | sed -E 's/\[.+\]\((.+)\)/\1/')
      
      # Skip external links, anchors, and absolute paths
      if [[ "$url" == "http"* || "$url" == "#"* || "$url" == "/"* ]]; then
        continue
      fi
      
      file_links=$((file_links + 1))
      
      # Normalize the URL
      # Remove trailing spaces, anchors, and ensure .md extension
      local normalized_url=$(echo "$url" | sed -E 's/#.*$//' | sed -E 's/ +$//')
      if [[ "$normalized_url" != *.md && "$normalized_url" != "" ]]; then
        normalized_url="${normalized_url}.md"
      fi
      
      # Skip empty URLs
      if [[ -z "$normalized_url" ]]; then
        continue
      fi
      
      # Resolve the link relative to the file's directory
      local file_dir=$(dirname "$file")
      local target_path
      
      if [[ "$normalized_url" == *"../"* ]]; then
        # It's a relative path with parent directory references
        target_path=$(realpath --relative-to="$PROJECT_ROOT" "$file_dir/$normalized_url")
      else
        # It's a relative path in the same directory
        target_path=$(realpath --relative-to="$PROJECT_ROOT" "$file_dir/$normalized_url")
      fi
      
      # Check if the target file exists
      if [[ ! -f "${PROJECT_ROOT}/$target_path" ]]; then
        broken_file_links=$((broken_file_links + 1))
        broken_links=$((broken_links + 1))
        
        echo "- In \`$rel_path\`: Link to \`$url\` (resolved to \`$target_path\`)" >> "$report_file"
      fi
    done
    
    if [ "$broken_file_links" -gt 0 ]; then
      warn "Found $broken_file_links broken links in $rel_path"
    fi
  done
  
  if [ "$broken_links" -gt 0 ]; then
    error "Found $broken_links potentially broken links in total"
    info "See detailed report at: $report_file"
  else
    success "No broken links found"
  fi
}

# Update links in a file to the new directory structure
update_file_links() {
  local file="$1"
  local dry_run="${2:-false}"
  local changes=0
  
  # Skip files in certain directories
  local rel_path="${file#${PROJECT_ROOT}/}"
  if [[ "$rel_path" == *"/backup/"* || "$rel_path" == *"/backup-"* ]]; then
    return 0
  fi
  
  info "Processing: $rel_path"
  
  # Create a temporary file
  local temp_file=$(mktemp)
  
  # Process the file line by line
  while IFS= read -r line; do
    local updated_line="$line"
    
    # Find markdown links
    if echo "$line" | grep -q -E '\[[^]]+\]\([^)]+\)'; then
      # Process each link in the line
      while read -r link; do
        # Skip if no links found
        [ -z "$link" ] && continue
        
        # Extract just the URL part
        local url=$(echo "$link" | sed -E 's/\[.+\]\((.+)\)/\1/')
        
        # Skip external links, anchors, and absolute URLs that aren't to /docs
        if [[ "$url" == "http"* || "$url" == "#"* ]]; then
          continue
        fi
        
        # Handle absolute paths to docs
        if [[ "$url" == "/docs/"* ]]; then
          local new_url="${url#/docs/}"
          # Now treat as a relative path from docs directory
          updated_line=${updated_line//"$url"/"../$new_url"}
          changes=$((changes + 1))
          continue
        fi
        
        # Skip URLs that don't point to directories we're mapping
        local match_found=false
        for old_dir in "${!DIR_MAPPING[@]}"; do
          # Check if the URL contains the old directory path
          if [[ "$url" == "$old_dir"* || "$url" == *"$old_dir"* ]]; then
            local new_dir="${DIR_MAPPING[$old_dir]}"
            
            # Replace the old directory with the new directory
            local new_url="${url/$old_dir/$new_dir}"
            updated_line=${updated_line//"$url"/"$new_url"}
            changes=$((changes + 1))
            match_found=true
            break
          fi
        done
        
        # Try to fix missing .md extensions
        if [[ "$url" != *.md && "$url" != "" && "$url" != *"/"$ ]]; then
          if [[ "$updated_line" == *"($url)"* ]]; then
            updated_line=${updated_line//"($url)"/"($url.md)"}
            changes=$((changes + 1))
          fi
        fi
        
      done < <(echo "$line" | grep -o -E '\[[^]]+\]\([^)]+\)')
    fi
    
    # Write the updated line to the temporary file
    echo "$updated_line" >> "$temp_file"
  done < "$file"
  
  # If changes were made and not in dry-run mode, update the file
  if [[ $changes -gt 0 && "$dry_run" == "false" ]]; then
    mv "$temp_file" "$file"
    success "Updated $changes links in: $rel_path"
  else
    rm "$temp_file"
    if [[ $changes -gt 0 ]]; then
      info "Would update $changes links in: $rel_path (dry run)"
    fi
  fi
  
  return $changes
}

# Update links in all markdown files in a directory
update_directory_links() {
  local dir="$1"
  local dry_run="${2:-false}"
  local updated_count=0
  local processed_count=0
  
  info "Processing directory: $dir"
  
  # Process markdown files in the directory
  find "$dir" -type f -name "*.md" | sort | while read -r file; do
    processed_count=$((processed_count + 1))
    
    # Update links in the file
    if update_file_links "$file" "$dry_run"; then
      updated_count=$((updated_count + 1))
    fi
  done
  
  # Print summary
  if [ "$dry_run" = "true" ]; then
    info "Dry run completed for $dir. Would update $updated_count out of $processed_count files."
  else
    success "Update completed for $dir. Updated $updated_count out of $processed_count files."
  fi
}

# Check for README files in each directory
check_readmes() {
  header "Checking for README.md files in directories"
  
  local missing_readme=0
  local report_file="${REPORT_DIR}/missing-readmes.txt"
  > "$report_file"
  
  # Find all directories in the docs directory
  find "${PROJECT_ROOT}/docs" -type d | sort | while read -r dir; do
    # Skip the docs root directory and hidden directories
    if [[ "$dir" == "${PROJECT_ROOT}/docs" || "$(basename "$dir")" == .* ]]; then
      continue
    fi
    
    # Check if README.md exists
    if [ ! -f "$dir/README.md" ]; then
      local rel_dir="${dir#${PROJECT_ROOT}/}"
      echo "$rel_dir" >> "$report_file"
      missing_readme=$((missing_readme + 1))
      warn "Missing README.md in: $rel_dir"
    fi
  done
  
  if [ "$missing_readme" -gt 0 ]; then
    error "Found $missing_readme directories without README.md files"
    info "See the list at: $report_file"
  else
    success "All directories have README.md files"
  fi
}

# Main function
main() {
  local dry_run=false
  local target_dir="${PROJECT_ROOT}/docs"
  local check_only=false
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        header "Cross-Reference Updater"
        echo "Updates cross-references in markdown files to match new directory structure."
        echo ""
        echo "Usage: $(basename "$0") [options]"
        echo ""
        echo "Options:"
        echo "  -h, --help     Show this help message"
        echo "  -d, --dry-run  Show what would be changed without making changes"
        echo "  -c, --check    Only check for broken links, don't update"
        echo "  -p, --path DIR Process only the specified directory"
        echo ""
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      -c|--check)
        check_only=true
        shift
        ;;
      -p|--path)
        if [[ -n "$2" && -d "$2" ]]; then
          target_dir="$2"
          shift 2
        else
          error "Missing or invalid directory for --path option"
          exit 1
        fi
        ;;
      *)
        error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
    esac
  done
  
  header "Cross-Reference Updater"
  
  if [ "$dry_run" = "true" ]; then
    info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Create the file mapping
  create_file_mapping
  
  # Check for missing README files
  check_readmes
  
  # Check for broken links
  check_links
  
  # Update links if not in check-only mode
  if [ "$check_only" = "false" ]; then
    header "Updating cross-references"
    update_directory_links "$target_dir" "$dry_run"
  fi
  
  header "Cross-Reference Update Complete"
  info "Reports are available in: $REPORT_DIR"
}

# Run the main function
main "$@"