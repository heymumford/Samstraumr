#!/bin/bash
#==============================================================================
# Filename: standardize-planning-filenames.sh
# Description: Standardizes filenames in the planning directory to follow 
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

# Planning directory to process
PLANNING_DIR="${PROJECT_ROOT}/docs/planning"

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
      
      # Skip the KANBAN.md special file
      if [[ "$filename" == "KANBAN.md" ]]; then
        print_info "Skipping special file: $filename"
        continue
      fi
      
      # Skip README.md files
      if [[ "$filename" == "README.md"* ]]; then
        print_info "Skipping README file: $filename"
        
        # Handle README.md.new -> move to README.md
        if [[ "$filename" == "README.md.new" ]]; then
          local new_path="${dir}/README.md"
          if [ -f "$new_path" ]; then
            print_warning "Cannot rename $filename - README.md already exists"
          else
            if [ "$dry_run" = "true" ]; then
              print_info "Would rename: $filename -> README.md"
            else
              if mv "$filepath" "$new_path"; then
                print_success "Renamed: $filename -> README.md"
                renamed_count=$((renamed_count + 1))
              else
                print_error "Failed to rename: $filename -> README.md"
                errors=$((errors + 1))
              fi
            fi
          fi
        fi
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
    print_info "Dry run completed. Would rename $renamed_count files."
  else
    print_info "Standardization completed. Renamed $renamed_count files, $unchanged_count already correct."
  fi
  
  if [ $errors -gt 0 ]; then
    print_error "Encountered $errors errors during standardization."
    return 1
  fi
  
  return 0
}

# Main function
function main() {
  local dry_run=false
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        print_header "Planning Document Filename Standardization"
        echo ""
        echo "Standardizes filenames in the planning directory to follow project naming conventions."
        echo ""
        echo "USAGE:"
        echo "  $(basename "$0") [options]"
        echo ""
        echo "OPTIONS:"
        echo "  -h, --help    Show this help message"
        echo "  -d, --dry-run Show what would be renamed without making changes"
        echo ""
        echo "DESCRIPTION:"
        echo "  This script standardizes filenames in the planning directory to follow"
        echo "  the project's naming conventions (PascalCase for documentation files)."
        echo "  For example, 'refactoring-summary.md' becomes 'RefactoringSummary.md'."
        echo ""
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
  
  print_header "Planning Document Filename Standardization"
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Check if planning directory exists
  if [ ! -d "$PLANNING_DIR" ]; then
    print_error "Planning directory not found: $PLANNING_DIR"
    exit 1
  fi
  
  # Run standardization
  standardize_directory "$PLANNING_DIR" "$dry_run"
  exit $?
}

# Run the main function
main "$@"