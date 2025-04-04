#!/bin/bash
# standardize-kebab-case.sh
#
# Script to standardize Markdown file naming conventions to kebab-case
# This script implements the new documentation standards that require kebab-case
# instead of the previous PascalCase standard.

# Set colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print a header
print_header() {
  echo -e "\n${YELLOW}==== $1 ====${NC}"
}

# Function to print success message
print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error message
print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Function to print info message
print_info() {
  echo -e "${BLUE}→ $1${NC}"
}

# Store the project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Function to convert filename to kebab-case
to_kebab_case() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  local extension="${filename##*.}"  # Keep extension
  
  # Special case for uppercase README.md and similar files
  if [[ "$basename" == "README" || "$basename" == "LICENSE" || "$basename" == "CONTRIBUTING" ]]; then
    echo "$filename"
    return
  fi
  
  # Convert PascalCase to kebab-case
  # 1. Insert hyphen before each capital letter and convert to lowercase
  # 2. Replace underscores with hyphens
  # 3. Replace multiple hyphens with a single hyphen
  # 4. Remove any leading/trailing hyphens
  local kebab_case=$(echo "$basename" | sed 's/\([A-Z]\)/-\L\1/g' | sed 's/_/-/g' | sed 's/--/-/g' | sed 's/^-//' | sed 's/-$//')
  
  # Return result with extension
  echo "${kebab_case}.${extension}"
}

# Function to apply standardization to a specific directory
standardize_directory() {
  local dir="$1"
  local dry_run="${2:-false}"
  local renamed_count=0
  local unchanged_count=0
  local errors=0
  
  print_info "Processing directory: $dir"
  
  # Process files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ]; then
      local filename=$(basename "$filepath")
      local extension="${filename##*.}"
      
      # Skip special files we want to leave as is
      if [[ "$filename" == "README.md" || "$filename" == "LICENSE" || "$filename" == "CONTRIBUTING.md" ]]; then
        print_info "Skipping special file: $filename"
        continue
      fi
      
      # Only process Markdown files
      if [[ "$extension" == "md" ]]; then
        local standardized_name=$(to_kebab_case "$filename")
        
        # If the filename needs to be changed
        if [[ "$filename" != "$standardized_name" ]]; then
          local new_path="${dir}/${standardized_name}"
          
          # Check if the target file already exists
          if [ -f "$new_path" ]; then
            print_error "Cannot rename $filename - $standardized_name already exists"
            continue
          fi
          
          if [ "$dry_run" = "true" ]; then
            print_info "Would rename: $filename -> $standardized_name"
          else
            if mv "$filepath" "$new_path"; then
              print_success "Renamed: $filename -> $standardized_name"
              renamed_count=$((renamed_count + 1))
            else
              print_error "Failed to rename: $filename -> $standardized_name"
              errors=$((errors + 1))
            fi
          fi
        else
          unchanged_count=$((unchanged_count + 1))
        fi
      else
        print_info "Skipping non-Markdown file: $filename"
      fi
    fi
  done
  
  # Print summary
  if [ "$dry_run" = "true" ]; then
    print_info "Dry run completed for $dir. Would rename $renamed_count files."
  else
    print_info "Standardization completed for $dir. Renamed $renamed_count files, $unchanged_count already correct."
  fi
  
  if [ $errors -gt 0 ]; then
    print_error "Encountered $errors errors during standardization."
    return 1
  fi
  
  return 0
}

# Main function
main() {
  print_header "Converting Documentation to Kebab-Case Standard"
  echo "Following new naming conventions:"
  echo "- README.md files remain as README.md"
  echo "- Other documentation files use kebab-case (e.g., core-concepts.md)"
  echo ""
  
  local dry_run=false
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        echo "Usage: $0 [options] [directories...]"
        echo ""
        echo "Options:"
        echo "  -h, --help     Show this help message"
        echo "  -d, --dry-run  Show what would be renamed without making changes"
        echo ""
        echo "If no directories are specified, processes all documentation directories."
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      *)
        echo "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
    esac
  done
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi

  # Process documentation directories
  declare -a doc_dirs=(
    "docs/concepts"
    "docs/guides"
    "docs/reference"
    "docs/testing"
    "docs/contribution"
    "docs/research"
    "docs/planning"
    "docs/compatibility"
    "docs/proposals"
    "docs/general"
  )
  
  for dir in "${doc_dirs[@]}"; do
    if [ -d "$dir" ]; then
      standardize_directory "$dir" "$dry_run"
    else
      print_info "Skipping non-existent directory: $dir"
    fi
  done
  
  # Check for files in the root docs directory
  if [ -d "docs" ]; then
    standardize_directory "docs" "$dry_run"
  fi
  
  print_header "Standardization Complete"
  echo "Documentation files have been standardized to kebab-case format."
  echo "Please update cross-references in files to match the new names."
}

# Run the main function
main "$@"