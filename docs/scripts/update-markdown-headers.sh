#!/bin/bash
#==============================================================================
# Filename: update-markdown-headers.sh
# Description: Script to update Markdown headers to follow standards
#==============================================================================
#
# This script standardizes Markdown headers throughout the documentation by:
# - Setting level 1 headers to match title case of file name
# - Setting level 2 headers to use title case
# - Setting level 3+ headers to use sentence case
# - Adding level 1 header if missing
#
# The script processes files recursively and can operate in dry-run mode.

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Define variable to track library availability
USING_LIB=false

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
  
  # Create a reports directory for any generated reports
  if [[ "$USING_LIB" == true ]] && type ensure_reports_directory &>/dev/null; then
    ensure_reports_directory
  fi
elif [ -f "${PROJECT_ROOT}/util/lib/unified-common.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/unified-common.sh"
  USING_LIB=false
else
  # Define minimal color codes if libraries are not available
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
fi

# Change to project root directory
cd "$PROJECT_ROOT"

# Function to convert string to title case
to_title_case() {
  local string="$1"
  
  # Use library function if available
  if [[ "$USING_LIB" == true ]] && type format_title_case &>/dev/null; then
    format_title_case "$string"
    return
  fi
  
  # Fallback implementation
  # Split string by spaces and capitalize first letter of each word
  # Skip specific words (prepositions, conjunctions, etc.)
  echo "$string" | sed -E 's/\b([a-z])([a-z]*)\b/\u\1\2/g' | \
    sed -E 's/\b(A|An|The|For|And|But|Or|Nor|At|By|For|From|In|Into|Of|On|To|With)\b/\L&/g' | \
    sed -E 's/^([a-z])/\u\1/' | \
    sed -E 's/[-:]([a-z])/\-\u\1/g'
}

# Function to convert string to sentence case
to_sentence_case() {
  local string="$1"
  
  # Use library function if available
  if [[ "$USING_LIB" == true ]] && type format_sentence_case &>/dev/null; then
    format_sentence_case "$string"
    return
  fi
  
  # Fallback implementation
  # Lowercase everything, then capitalize first letter
  echo "$string" | tr '[:upper:]' '[:lower:]' | sed -E 's/^([a-z])/\u\1/'
}

# Function to extract title from kebab-case filename
get_title_from_filename() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  
  # Use library function if available for converting from kebab-case to title
  if [[ "$USING_LIB" == true ]] && type kebab_to_title &>/dev/null; then
    kebab_to_title "$basename"
    return
  fi
  
  # Fallback implementation
  # Replace hyphens with spaces
  local title="${basename//-/ }"
  
  # Convert to title case
  to_title_case "$title"
}

# Function to update headers in a file
update_file_headers() {
  local file="$1"
  local dry_run="${2:-false}"
  local filename=$(basename "$file")
  local file_title=$(get_title_from_filename "$filename")
  local has_level1=false
  local changes=0
  
  print_info "Processing file: $file"
  
  # Initialize file tracking in the report
  if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
    add_report_subsection "$HEADERS_REPORT_FILE" "$(basename "$file")"
    add_to_report "$HEADERS_REPORT_FILE" "Target title: $file_title"
  fi
  
  # Use library for extract_markdown_headers if available
  local headers_data=""
  if [[ "$USING_LIB" == true ]] && type extract_markdown_headers &>/dev/null; then
    headers_data=$(extract_markdown_headers "$file")
    
    # Check if file has a level 1 header from headers_data
    if echo "$headers_data" | grep -q "|#|"; then
      has_level1=true
      
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
        add_to_report "$HEADERS_REPORT_FILE" "Level 1 header found."
      fi
    else
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
        add_to_report "$HEADERS_REPORT_FILE" "No level 1 header found."
      fi
    fi
  fi
  
  # Create a temporary file
  local temp_file=$(mktemp)
  
  # Process each line in the file
  while IFS= read -r line; do
    # Check for level 1 header
    if [[ "$line" =~ ^#[^#] ]]; then
      has_level1=true
      new_line="# $file_title"
      
      if [[ "$line" != "$new_line" ]]; then
        if [ "$dry_run" = "true" ]; then
          print_info "Would change level 1 header:"
          print_info "  From: $line"
          print_info "  To:   $new_line"
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Would change level 1 header: '$line' → '$new_line'"
          fi
        else
          line="$new_line"
          changes=$((changes + 1))
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Changed level 1 header: '$line' → '$new_line'"
          fi
        fi
      fi
    # Check for level 2 header
    elif [[ "$line" =~ ^##[^#] ]]; then
      # Extract header text
      local header_text=${line#"## "}
      local title_case=$(to_title_case "$header_text")
      
      if [[ "$header_text" != "$title_case" ]]; then
        if [ "$dry_run" = "true" ]; then
          print_info "Would change level 2 header:"
          print_info "  From: $line"
          print_info "  To:   ## $title_case"
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Would change level 2 header: '$line' → '## $title_case'"
          fi
        else
          line="## $title_case"
          changes=$((changes + 1))
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Changed level 2 header: '$header_text' → '$title_case'"
          fi
        fi
      fi
    # Check for level 3+ header
    elif [[ "$line" =~ ^###+ ]]; then
      # Extract header level and text
      local header_level=${line%%[^#]*}
      local header_text=${line#"$header_level "}
      local sentence_case=$(to_sentence_case "$header_text")
      
      if [[ "$header_text" != "$sentence_case" ]]; then
        if [ "$dry_run" = "true" ]; then
          print_info "Would change level ${#header_level} header:"
          print_info "  From: $line"
          print_info "  To:   $header_level $sentence_case"
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Would change level ${#header_level} header: '$line' → '$header_level $sentence_case'"
          fi
        else
          line="$header_level $sentence_case"
          changes=$((changes + 1))
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
            add_to_report "$HEADERS_REPORT_FILE" "- Changed level ${#header_level} header: '$header_text' → '$sentence_case'"
          fi
        fi
      fi
    fi
    
    # Write the line to the temp file
    echo "$line" >> "$temp_file"
  done < "$file"
  
  # If no level 1 header and not in dry run mode, add one
  if [[ "$has_level1" == false && "$dry_run" == false ]]; then
    print_info "No level 1 header found. Adding: # $file_title"
    
    # Create a new temp file with the header at the top
    local header_temp_file=$(mktemp)
    echo "# $file_title" > "$header_temp_file"
    echo "" >> "$header_temp_file"  # Add a blank line
    cat "$temp_file" >> "$header_temp_file"
    mv "$header_temp_file" "$temp_file"
    changes=$((changes + 1))
    
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      add_to_report "$HEADERS_REPORT_FILE" "- Added level 1 header: '# $file_title'"
    fi
  elif [[ "$has_level1" == false && "$dry_run" == true ]]; then
    print_info "Would add level 1 header: # $file_title"
    
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      add_to_report "$HEADERS_REPORT_FILE" "- Would add level 1 header: '# $file_title'"
    fi
  fi
  
  # If changes were made and not in dry run mode, update the file
  if [[ $changes -gt 0 && "$dry_run" == false ]]; then
    mv "$temp_file" "$file"
    print_success "Updated $changes headers in $file"
    
    # Add summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      add_to_report "$HEADERS_REPORT_FILE" "**Total changes**: $changes header(s) updated."
    fi
  else
    rm "$temp_file"
    if [[ $changes -eq 0 ]]; then
      print_info "No changes needed for $file"
      
      # Add to report
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
        add_to_report "$HEADERS_REPORT_FILE" "**No changes needed**."
      fi
    fi
  fi
  
  # Use library for validation if available
  if [[ "$USING_LIB" == true ]] && type has_markdown_title &>/dev/null && ! "$dry_run"; then
    if has_markdown_title "$file"; then
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
        add_to_report "$HEADERS_REPORT_FILE" "Verification: Level 1 header is present and valid."
      fi
    else
      print_warning "Verification failed: No level 1 header found in $file after update."
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
        add_to_report "$HEADERS_REPORT_FILE" "⚠️ Verification failed: No level 1 header found after update!"
      fi
    fi
  fi
  
  return $changes
}

# Function to update headers in all markdown files in a directory
update_directory_headers() {
  local dir="$1"
  local dry_run="${2:-false}"
  local updated_count=0
  local unchanged_count=0
  local errors=0
  
  # Use library section formatting if available
  if [[ "$USING_LIB" == true ]] && type print_section &>/dev/null; then
    print_section "Processing directory: $dir"
  else
    print_info "Processing directory: $dir"
  fi
  
  # Add section to report for this directory
  if [[ "$USING_LIB" == true ]] && type add_report_section &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
    add_report_section "$HEADERS_REPORT_FILE" "Directory: $dir"
  fi
  
  # Process markdown files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ] && [[ "$filepath" == *.md ]]; then
      # Call update_file_headers
      if update_file_headers "$filepath" "$dry_run"; then
        updated_count=$((updated_count + 1))
      else
        unchanged_count=$((unchanged_count + 1))
      fi
    fi
  done
  
  # Process subdirectories recursively
  for subdir in "$dir"/*/; do
    if [ -d "$subdir" ]; then
      # Skip version control and temporary directories
      if [[ ! "$subdir" == *"/.git/"* && ! "$subdir" == *"/node_modules/"* ]]; then
        # Using local function recursively
        local subdir_result=$(update_directory_headers "$subdir" "$dry_run")
        
        # We can't easily capture counts from subdirectories with this structure
        # but the reporting will be handled at each level independently
      fi
    fi
  done
  
  # Print summary for this directory
  if [ "$dry_run" = "true" ]; then
    print_info "Dry run completed for $dir. Would update $updated_count files."
    
    # Add directory summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      add_to_report "$HEADERS_REPORT_FILE" "**Directory Summary**: Would update $updated_count files out of $((updated_count + unchanged_count)) total markdown files."
    fi
  else
    print_info "Update completed for $dir. Updated $updated_count files, $unchanged_count already correct."
    
    # Add directory summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      add_to_report "$HEADERS_REPORT_FILE" "**Directory Summary**: Updated $updated_count files out of $((updated_count + unchanged_count)) total markdown files."
    fi
  fi
  
  # Return the number of updated files from this directory (not including subdirectories)
  echo $updated_count
}

# Main function
main() {
  # Use the library header formatting if available
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
    print_header "Markdown Headers Standardization"
  else
    print_header "Updating Markdown Headers to Follow Documentation Standards"
  fi
  
  # Print standards information
  echo "Standards:"
  echo "- Level 1 headers match title case of file"
  echo "- Level 2 headers use title case"
  echo "- Level 3+ headers use sentence case"
  echo "- Add level 1 header if missing"
  echo ""
  
  local dry_run=false
  local target_dir="docs"
  local verbose=false
  
  # Initialize report if library functions available
  if [[ "$USING_LIB" == true ]] && type initialize_report &>/dev/null; then
    HEADERS_REPORT_FILE=$(initialize_report "markdown_headers_standardization")
    add_to_report "$HEADERS_REPORT_FILE" "# Markdown Headers Standardization Report"
    add_to_report "$HEADERS_REPORT_FILE" "This report shows changes made to standardize markdown headers."
    add_to_report "$HEADERS_REPORT_FILE" ""
    add_to_report "$HEADERS_REPORT_FILE" "## Standards Applied"
    add_to_report "$HEADERS_REPORT_FILE" "- Level 1 headers match title case of file"
    add_to_report "$HEADERS_REPORT_FILE" "- Level 2 headers use title case"
    add_to_report "$HEADERS_REPORT_FILE" "- Level 3+ headers use sentence case"
    add_to_report "$HEADERS_REPORT_FILE" "- Add level 1 header if missing"
    add_to_report "$HEADERS_REPORT_FILE" ""
  fi
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        # Use library's help formatting if available
        if [[ "$USING_LIB" == true ]] && type show_help_template &>/dev/null; then
          local script_name="$(basename "$0")"
          local description="Updates Markdown headers to follow documentation standards"
          local options=$(cat <<EOF
  -h, --help      Show this help message
  -d, --dry-run   Show what would be changed without making changes
  -v, --verbose   Show more detailed information during processing
EOF
)
          local examples=$(cat <<EOF
  $(basename "$0")            # Process all docs directory
  $(basename "$0") docs/core  # Process only core documentation
  $(basename "$0") --dry-run  # Show what would be fixed without changing files
EOF
)
          show_help_template "$0" "$description" "$options" "$examples"
        else
          echo "Usage: $0 [options] [directory]"
          echo ""
          echo "Options:"
          echo "  -h, --help      Show this help message"
          echo "  -d, --dry-run   Show what would be changed without making changes"
          echo "  -v, --verbose   Show more detailed information during processing"
          echo ""
          echo "If no directory is specified, processes the entire docs directory."
          echo ""
          echo "Examples:"
          echo "  $(basename "$0")            # Process all docs directory"
          echo "  $(basename "$0") docs/core  # Process only core documentation"
          echo "  $(basename "$0") --dry-run  # Show what would be fixed without changing files"
        fi
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      -v|--verbose)
        verbose=true
        shift
        ;;
      *)
        # If argument is a directory, use it as target
        if [ -d "$1" ]; then
          target_dir="$1"
          shift
        else
          print_error "Unknown option or invalid directory: $1"
          print_info "Use --help for usage information."
          exit 1
        fi
        ;;
    esac
  done
  
  # Log to report if using library
  if [[ "$USING_LIB" == true ]] && type add_report_section &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
    add_report_section "$HEADERS_REPORT_FILE" "Run Configuration"
    add_to_report "$HEADERS_REPORT_FILE" "- Target directory: $target_dir"
    add_to_report "$HEADERS_REPORT_FILE" "- Dry run mode: $dry_run"
    add_to_report "$HEADERS_REPORT_FILE" "- Verbose mode: $verbose"
    add_to_report "$HEADERS_REPORT_FILE" "- Using doc-lib.sh: $USING_LIB"
    add_to_report "$HEADERS_REPORT_FILE" "- Run date: $(date '+%Y-%m-%d %H:%M:%S')"
    add_to_report "$HEADERS_REPORT_FILE" ""
  fi
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Print information about library usage
  if [[ "$USING_LIB" == true ]]; then
    print_info "Using documentation utility library for enhanced header formatting"
  fi
  
  # Process target directory
  if [ -d "$target_dir" ]; then
    local total_updated=$(update_directory_headers "$target_dir" "$dry_run")
    
    # Print information about total updates
    if [ "$dry_run" = "true" ]; then
      print_info "Dry run completed. Would update headers in $total_updated files."
    else
      print_success "Header standardization completed. Updated headers in $total_updated files."
    fi
    
    # Finalize report if using library
    if [[ "$USING_LIB" == true ]] && type finalize_report &>/dev/null && [ -n "$HEADERS_REPORT_FILE" ]; then
      local summary=""
      if [ "$dry_run" = "true" ]; then
        summary="Dry run completed. Would update headers in $total_updated files."
      else
        summary="Header standardization completed. Updated headers in $total_updated files."
      fi
      
      local report_path=$(finalize_report "$HEADERS_REPORT_FILE" "$summary")
      print_info "Report generated: $report_path"
    fi
  else
    print_error "Directory does not exist: $target_dir"
    exit 1
  fi
  
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
    print_header "Header Standardization Complete"
  else
    print_header "Header Update Complete"
  fi
  
  echo "Markdown headers have been updated to follow documentation standards."
}

# Run the main function
main "$@"