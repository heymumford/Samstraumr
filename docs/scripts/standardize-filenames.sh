#!/bin/bash
#==============================================================================
# Filename: standardize-filenames.sh
# Description: Standardizes filenames in documentation directories to follow 
#              project naming conventions (PascalCase for documentation)
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source common utilities if available
if [ -f "${PROJECT_ROOT}/util/lib/common.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/common.sh"
else
  # Define minimal color codes if common.sh not available
  COLOR_RED='\033[0;31m'
  COLOR_GREEN='\033[0;32m'
  COLOR_YELLOW='\033[0;33m'
  COLOR_BLUE='\033[0;34m'
  COLOR_RESET='\033[0m'
  COLOR_BOLD='\033[1m'
  
  function print_header() {
    echo -e "${COLOR_BOLD}$1${COLOR_RESET}"
    echo -e "${COLOR_BOLD}$(printf '=%.0s' $(seq 1 ${#1}))${COLOR_RESET}"
  }
  
  function print_success() {
    echo -e "${COLOR_GREEN}✓ $1${COLOR_RESET}"
  }
  
  function print_error() {
    echo -e "${COLOR_RED}✗ $1${COLOR_RESET}" >&2
  }
  
  function print_warning() {
    echo -e "${COLOR_YELLOW}! $1${COLOR_RESET}"
  }
  
  function print_info() {
    echo -e "${COLOR_BLUE}→ $1${COLOR_RESET}"
  }
fi

# Function to convert filename to PascalCase
# Converts: kebab-case, snake_case, or space separated names
function to_pascal_case() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  local extension="${filename##*.}"  # Keep extension
  
  # Special case for uppercase files like README.md or KANBAN.md
  if [[ "$basename" =~ ^[A-Z]+$ ]]; then
    echo "$filename"
    return
  fi
  
  # Replace hyphens and underscores with spaces
  local spaced="${basename//-/ }"
  spaced="${spaced//_/ }"
  
  # Capitalize each word and remove spaces
  local pascal_case=""
  for word in $spaced; do
    # Keep existing capitalization for acronyms (like ATL, BTL, TDD)
    if [[ "$word" =~ ^[A-Z]{2,}$ ]]; then
      pascal_case="${pascal_case}${word}"
    else
      # Capitalize first letter, keep rest as is
      pascal_case="${pascal_case}${word^}"
    fi
  done
  
  # Return result with extension
  echo "${pascal_case}.${extension}"
}

# Function to apply standardization to a specific directory
function standardize_directory() {
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
      
      # Skip special files
      if [[ "$filename" == "README.md" || "$filename" == "KANBAN.md" || "$filename" == "CLAUDE.md" || "$filename" == "LICENSE" ]]; then
        print_info "Skipping special file: $filename"
        continue
      fi
      
      # Only process Markdown files
      if [[ "$extension" == "md" ]]; then
        local standardized_name=$(to_pascal_case "$filename")
        
        # If the filename needs to be changed
        if [[ "$filename" != "$standardized_name" ]]; then
          local new_path="${dir}/${standardized_name}"
          
          # Check if the target file already exists
          if [ -f "$new_path" ]; then
            print_warning "Cannot rename $filename - $standardized_name already exists"
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
        print_warning "Skipping non-Markdown file: $filename"
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

# Function to process multiple directories
function process_directories() {
  local dirs=("$@")
  local dry_run="${DRYRUN:-false}"
  local success_count=0
  local error_count=0
  
  for dir in "${dirs[@]}"; do
    if [ -d "$dir" ]; then
      if standardize_directory "$dir" "$dry_run"; then
        success_count=$((success_count + 1))
      else
        error_count=$((error_count + 1))
      fi
    else
      print_error "Directory not found: $dir"
      error_count=$((error_count + 1))
    fi
  done
  
  # Print overall summary
  print_header "Standardization Summary"
  print_info "Successfully processed $success_count directories"
  if [ $error_count -gt 0 ]; then
    print_error "Encountered errors in $error_count directories"
    return 1
  fi
  
  return 0
}

# Main function
function main() {
  local dry_run=false
  local target_dirs=()
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        print_header "Documentation Filename Standardization"
        echo ""
        echo "Standardizes filenames in documentation directories to follow project naming conventions."
        echo ""
        echo "USAGE:"
        echo "  $(basename "$0") [options] [directories...]"
        echo ""
        echo "OPTIONS:"
        echo "  -h, --help    Show this help message"
        echo "  -d, --dry-run Show what would be renamed without making changes"
        echo ""
        echo "DEFAULTS:"
        echo "  If no directories are specified, processes the following directories:"
        echo "  - docs/planning"
        echo "  - docs/concepts"
        echo "  - docs/guides"
        echo "  - docs/reference"
        echo "  - docs/proposals"
        echo ""
        echo "DESCRIPTION:"
        echo "  This script standardizes filenames in documentation directories to follow"
        echo "  the project's naming conventions (PascalCase for documentation files)."
        echo "  For example, 'refactoring-summary.md' becomes 'RefactoringSummary.md'."
        echo ""
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      -*)
        print_error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
      *)
        # If argument doesn't start with -, treat as directory
        target_dirs+=("$1")
        shift
        ;;
    esac
  done
  
  # Export DRYRUN for child functions
  export DRYRUN="$dry_run"
  
  print_header "Documentation Filename Standardization"
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # If no directories specified, use defaults
  if [ ${#target_dirs[@]} -eq 0 ]; then
    target_dirs=(
      "${PROJECT_ROOT}/docs/planning"
      "${PROJECT_ROOT}/docs/concepts"
      "${PROJECT_ROOT}/docs/guides"
      "${PROJECT_ROOT}/docs/reference"
      "${PROJECT_ROOT}/docs/proposals"
    )
  fi
  
  # Process all target directories
  process_directories "${target_dirs[@]}"
  exit $?
}

# Run the main function
main "$@"