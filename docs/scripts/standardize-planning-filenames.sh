#!/bin/bash
#==============================================================================
# Filename: standardize-planning-filenames.sh
# Description: Standardizes filenames in the planning directory to follow 
#              project naming conventions (kebab-case for documentation)
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  # Try the common utilities if doc-lib is not available
  if [ -f "${PROJECT_ROOT}/util/lib/common.sh" ]; then
    source "${PROJECT_ROOT}/util/lib/common.sh"
    USING_LIB=false
  else
    echo "Warning: Documentation library not found. Using fallback functions."
    USING_LIB=false
    
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
fi

# Planning directory to process
PLANNING_DIR="${PROJECT_ROOT}/docs/planning"

# Function to convert filename to kebab-case
# Converts: PascalCase, camelCase, snake_case, or space separated names
function to_kebab_case() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  local extension="${filename##*.}"  # Keep extension
  
  # Special cases 
  # 1. Files that should remain uppercase (README.md, KANBAN.md)
  if [[ "$basename" == "README" || "$basename" == "KANBAN" ]]; then
    echo "$filename"
    return
  fi
  
  # 2. UPPERCASE files with hyphens like VALIDATION-TODOS
  if [[ "$basename" =~ ^[A-Z-]+$ ]]; then
    # Convert to lowercase keeping hyphens
    local lowercase=$(echo "$basename" | tr '[:upper:]' '[:lower:]')
    echo "${lowercase}.${extension}"
    return
  fi
  
  # Convert PascalCase or camelCase to kebab-case
  # First, add a hyphen before each capital letter and lowercase everything
  local kebab=$(echo "$basename" | sed -E 's/([A-Z])/-\L\1/g')
  
  # Replace underscores with hyphens
  kebab="${kebab//_/-}"
  
  # Replace spaces with hyphens
  kebab="${kebab// /-}"
  
  # Remove leading hyphen if it exists
  kebab="${kebab#-}"
  
  # Replace multiple consecutive hyphens with a single one
  kebab=$(echo "$kebab" | sed -E 's/-+/-/g')
  
  # Return result with extension
  echo "${kebab}.${extension}"
}

# Function to apply standardization to a specific directory
function standardize_directory() {
  local dir="$1"
  local dry_run="${2:-false}"
  local renamed_count=0
  local unchanged_count=0
  local errors=0
  
  print_info "Processing directory: $dir"
  
  # Use the library's functions if available
  if [[ "$USING_LIB" == true ]] && type to_kebab_case &>/dev/null; then
    print_info "Using documentation library for standardization"
    
    # Handle special cases before processing other files
    
    # Handle the case of README.md.new files
    for filepath in "$dir"/*; do
      if [ -f "$filepath" ]; then
        local filename=$(basename "$filepath")
        
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
      fi
    done
    
    # Now process other files using PascalCase conversion
    for filepath in "$dir"/*; do
      if [ -f "$filepath" ]; then
        local filename=$(basename "$filepath")
        local extension="${filename##*.}"
        
        # Skip the KANBAN.md special file and README files
        if [[ "$filename" == "KANBAN.md" || "$filename" == "README.md"* ]]; then
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
  else
    # Fall back to original implementation
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
          local standardized_name=$(to_kebab_case "$filename")
          
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
  fi
  
  # Create a report if using the library
  if [[ "$USING_LIB" == true ]] && [ -d "$REPORT_DIR" ]; then
    date_stamp=$(date +%Y%m%d)
    report_file="${REPORT_DIR}/planning-standardization-${date_stamp}.md"
    
    # Create a report
    {
      echo "# Planning Document Filename Standardization Report"
      echo "Generated: $(date)"
      echo
      echo "## Naming Conventions"
      echo "- README.md files remain in UPPER_CASE"
      echo "- KANBAN.md remains in UPPER_CASE"
      echo "- Other markdown files use kebab-case"
      echo
      echo "## Results"
      echo
      echo "* **Files Renamed**: $renamed_count"
      echo "* **Files Already Correct**: $unchanged_count"
      echo "* **Errors Encountered**: $errors"
      
      if [ $renamed_count -gt 0 ]; then
        echo
        echo "### Renamed Files"
        echo
        echo "Files were renamed to follow kebab-case convention."
      fi
    } > "$report_file"
    
    print_success "Standardization report created at ${report_file}"
  fi
  
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
        if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
          print_header "Planning Document Filename Standardization"
          echo ""
          print_info "Standardizes filenames in the planning directory to follow project naming conventions."
          echo ""
          print_bold "USAGE:"
          echo "  $(basename "$0") [options]"
          echo ""
          print_bold "OPTIONS:"
          echo "  -h, --help    Show this help message"
          echo "  -d, --dry-run Show what would be renamed without making changes"
          echo ""
          print_bold "DESCRIPTION:"
          echo "  This script standardizes filenames in the planning directory to follow"
          echo "  the project's naming conventions (kebab-case for documentation files)."
          echo "  For example, 'RefactoringSummary.md' becomes 'refactoring-summary.md'."
          echo ""
        else
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
          echo "  the project's naming conventions (kebab-case for documentation files)."
          echo "  For example, 'RefactoringSummary.md' becomes 'refactoring-summary.md'."
          echo ""
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
  
  print_header "Planning Document Filename Standardization"
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Check if planning directory exists
  if [ ! -d "$PLANNING_DIR" ]; then
    print_error "Planning directory not found: $PLANNING_DIR"
    exit 1
  fi
  
  # Create reports directory if using the library
  if [[ "$USING_LIB" == true ]]; then
    REPORT_DIR="${PROJECT_ROOT}/reports/docs"
    if [ ! -d "$REPORT_DIR" ]; then
      mkdir -p "$REPORT_DIR"
    fi
  fi
  
  # Run standardization on main directory and all subdirectories
  standardize_directory "$PLANNING_DIR" "$dry_run"
  local main_result=$?
  
  # Process subdirectories if they exist
  for subdir in "$PLANNING_DIR"/*; do
    if [ -d "$subdir" ]; then
      print_info "Processing subdirectory: $subdir"
      standardize_directory "$subdir" "$dry_run"
    fi
  done
  
  exit $main_result
}

# Run the main function
main "$@"