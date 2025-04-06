#!/bin/bash
#==============================================================================
# standardize-kebab-case.sh
#
# Script to standardize Markdown file naming conventions to kebab-case
# This script implements the new documentation standards that require kebab-case
# instead of the previous PascalCase standard.
#==============================================================================

# Find project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
else
  echo "Error: Documentation library not found. Using fallback functions."
  
  # Fallback implementations of required functions
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  NC='\033[0m' # No Color
  
  # Fallback output functions
  print_header() { echo -e "\n${YELLOW}==== $1 ====${NC}"; }
  print_success() { echo -e "${GREEN}✓ $1${NC}"; }
  print_error() { echo -e "${RED}✗ $1${NC}"; }
  print_info() { echo -e "${BLUE}→ $1${NC}"; }
  
  # Fallback kebab-case function
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
    local kebab_case=$(echo "$basename" | sed 's/\([A-Z]\)/-\L\1/g' | sed 's/_/-/g' | sed 's/--/-/g' | sed 's/^-//' | sed 's/-$//')
    
    # Return result with extension
    echo "${kebab_case}.${extension}"
  }
fi

# Function to apply standardization to a specific directory
# This function is a wrapper around the standardize_directory_filenames function from doc-lib.sh
standardize_directory() {
  local dir="$1"
  local dry_run="${2:-false}"
  
  # Use the standardize_directory_filenames function from doc-lib.sh if available
  # Otherwise, use a simple directory scan to find and rename files
  if type standardize_directory_filenames &>/dev/null; then
    # Library function available - use it
    standardize_directory_filenames "$dir" "$dry_run"
    return $?
  else
    # Fallback implementation if library function not available
    local renamed_count=0
    local unchanged_count=0
    local errors=0
    
    print_info "Processing directory: $dir (fallback method)"
    
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
  fi
}

# Main function
main() {
  print_header "Converting Documentation to Kebab-Case Standard"
  
  local dry_run=false
  local description="Standardizes Markdown file naming conventions to kebab-case."
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        # Use show_help_template from unified-common.sh if available
        if type show_help_template &>/dev/null; then
          local script_name="$(basename "$0")"
          local options="  -h, --help     Show this help message\n  -d, --dry-run  Show what would be renamed without making changes"
          local examples="  $script_name --dry-run\n  $script_name docs/concepts\n  $script_name --dry-run docs/guides"
          show_help_template "$script_name" "$description" "$options" "$examples"
        else
          # Fallback help display
          echo "Usage: $0 [options] [directories...]"
          echo ""
          echo "Description: $description"
          echo ""
          echo "Options:"
          echo "  -h, --help     Show this help message"
          echo "  -d, --dry-run  Show what would be renamed without making changes"
          echo ""
          echo "If no directories are specified, processes all documentation directories."
        fi
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      *)
        print_error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
    esac
  done
  
  print_info "Following new naming conventions:"
  print_info "- README.md files remain as README.md"
  print_info "- Other documentation files use kebab-case (e.g., core-concepts.md)"
  
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