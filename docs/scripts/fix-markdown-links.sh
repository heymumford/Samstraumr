#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: fix-markdown-links.sh
# Description: Script to update Markdown links to follow documentation standards
#==============================================================================
#
# This script standardizes Markdown links throughout the documentation by:
# - Converting absolute paths to relative paths
# - Adding .md extensions where missing
# - Updating links to renamed files (PascalCase to kebab-case)
# - Fixing broken links when possible
#
# Runs recursively through documentation directories to find and fix all issues.

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

# Function to fix links in a file
fix_file_links() {
  local file="$1"
  local dry_run="${2:-false}"
  local changes=0
  
  print_info "Processing file: $file"
  
  # Initialize link tracking for this file in the report
  if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
    add_report_subsection "$LINKS_REPORT_FILE" "$(basename "$file")"
  fi
  
  # Use library function if available, otherwise use local implementation
  if [[ "$USING_LIB" == true ]] && type extract_markdown_links &>/dev/null; then
    local links_data=$(extract_markdown_links "$file")
    if [ -z "$links_data" ]; then
      # No links found
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
        add_to_report "$LINKS_REPORT_FILE" "No links found."
      fi
      return 0
    fi
  fi
  
  # Create a temporary file
  local temp_file=$(mktemp)
  
  # Fix absolute paths to relative paths and add .md extension
  while IFS= read -r line; do
    local updated_line="$line"
    
    # Fix absolute paths to docs directory
    if [[ "$line" == *"](/docs/"* ]]; then
      # Extract the path components to calculate the relative path
      local file_dir=$(dirname "$file")
      local rel_path="../"
      
      # Replace absolute links with relative ones
      updated_line=$(echo "$line" | sed "s|\](/docs/|\]($rel_path|g")
      
      if [[ "$updated_line" != "$line" ]]; then
        if [ "$dry_run" = "true" ]; then
          print_info "Would fix absolute path in line:"
          print_info "  From: $line"
          print_info "  To:   $updated_line"
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
            add_to_report "$LINKS_REPORT_FILE" "- Would fix absolute path: $line → $updated_line"
          fi
        else
          changes=$((changes + 1))
          
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
            add_to_report "$LINKS_REPORT_FILE" "- Fixed absolute path: $line → $updated_line"
          fi
        fi
      fi
    fi
    
    # Fix links to PascalCase files that have been renamed
    # For each link pattern [text](path/to/SomeFile)
    if echo "$line" | grep -q -E '\[[^]]+\]\([^)]*[A-Z][^)]*\)'; then
      # Process potential links to files with capital letters
      for word in $(echo "$line" | grep -o -E '\[[^]]+\]\([^)]+\)' | sed 's/\[.*\](\(.*\))/\1/g'); do
        if [[ "$word" == *[A-Z]* && "$word" != *"://"* && "$word" != "#"* ]]; then
          # This might be a PascalCase filename - get just the filename part
          local filename=$(basename "$word")
          
          # Use to_kebab_case from doc-lib if available
          if [[ "$USING_LIB" == true ]] && type to_kebab_case &>/dev/null; then
            local kebab_case=$(to_kebab_case "$filename")
          else
            # Convert to kebab-case locally
            local kebab_case=$(echo "$filename" | sed 's/\([A-Z]\)/-\L\1/g' | sed 's/^-//')
          fi
          
          # Replace in the line if it's a full word
          if [[ "$word" == *"$filename"* ]]; then
            updated_line=$(echo "$updated_line" | sed "s|$filename|$kebab_case|g")
            
            if [[ "$updated_line" != "$line" ]]; then
              if [ "$dry_run" = "true" ]; then
                print_info "Would update link to renamed file:"
                print_info "  From: $line"
                print_info "  To:   $updated_line"
                
                if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
                  add_to_report "$LINKS_REPORT_FILE" "- Would fix PascalCase filename: $filename → $kebab_case"
                fi
              else
                changes=$((changes + 1))
                
                if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
                  add_to_report "$LINKS_REPORT_FILE" "- Fixed PascalCase filename: $filename → $kebab_case"
                fi
              fi
            fi
          fi
        fi
      done
    fi
    
    # Ensure links have .md extension if they don't already
    if echo "$line" | grep -q -E '\[[^]]+\]\([^)]*[^\.md][^)]*\)'; then
      # Process links that might be missing .md extension
      for word in $(echo "$line" | grep -o -E '\[[^]]+\]\([^)]+\)' | sed 's/\[.*\](\(.*\))/\1/g'); do
        # If it's not a URL, anchor, or already has .md extension
        if [[ "$word" != *".md"* && "$word" != *"://"* && "$word" != "#"* ]]; then
          # Add .md extension
          updated_line=$(echo "$updated_line" | sed "s|$word|$word.md|g")
          
          if [[ "$updated_line" != "$line" ]]; then
            if [ "$dry_run" = "true" ]; then
              print_info "Would add .md extension to link:"
              print_info "  From: $line"
              print_info "  To:   $updated_line"
              
              if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
                add_to_report "$LINKS_REPORT_FILE" "- Would add .md extension: $word → $word.md"
              fi
            else
              changes=$((changes + 1))
              
              if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
                add_to_report "$LINKS_REPORT_FILE" "- Added .md extension: $word → $word.md"
              fi
            fi
          fi
        fi
      done
    fi
    
    # Validate internal links if library function available
    if [[ "$USING_LIB" == true ]] && type validate_internal_links &>/dev/null && ! "$dry_run"; then
      # This is handled separately via the library function in the main flow
      # to avoid redundancy in the line-by-line processing
      :
    fi
    
    # Write the updated line to the temp file
    echo "$updated_line" >> "$temp_file"
  done < "$file"
  
  # If changes were made and not in dry run mode, update the file
  if [[ $changes -gt 0 && "$dry_run" == false ]]; then
    mv "$temp_file" "$file"
    print_success "Updated $changes links in $file"
    
    # Add summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
      add_to_report "$LINKS_REPORT_FILE" "**Total changes**: $changes link(s) updated."
    fi
  else
    rm "$temp_file"
    if [[ $changes -eq 0 ]]; then
      print_info "No changes needed for $file"
      
      # Add to report
      if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
        add_to_report "$LINKS_REPORT_FILE" "**No changes needed**."
      fi
    fi
  fi
  
  return $changes
}

# Function to fix links in all markdown files in a directory
fix_directory_links() {
  local dir="$1"
  local dry_run="${2:-false}"
  local updated_count=0
  local unchanged_count=0
  
  # Use library section formatting if available
  if [[ "$USING_LIB" == true ]] && type print_section &>/dev/null; then
    print_section "Processing directory: $dir"
  else
    print_info "Processing directory: $dir"
  fi
  
  # Add section to report for this directory
  if [[ "$USING_LIB" == true ]] && type add_report_section &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
    add_report_section "$LINKS_REPORT_FILE" "Directory: $dir"
  fi
  
  # Process markdown files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ] && [[ "$filepath" == *.md ]]; then
      # Call fix_file_links
      if fix_file_links "$filepath" "$dry_run"; then
        updated_count=$((updated_count + 1))
      else
        unchanged_count=$((unchanged_count + 1))
      fi
      
      # If library is available, also use the fix_markdown_links validation
      if [[ "$USING_LIB" == true ]] && type fix_markdown_links &>/dev/null && ! "$dry_run"; then
        # Use the specialized library function to find and fix additional links
        if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
          add_to_report "$LINKS_REPORT_FILE" "Using library link validator..."
        fi
        
        # Capture and report additional fixed links
        local additional_fixed=$(fix_markdown_links "$filepath" "$PROJECT_ROOT" 2>/dev/null)
        if [ -n "$additional_fixed" ] && [ "$additional_fixed" -gt 0 ]; then
          print_success "Library validator fixed $additional_fixed additional links in $filepath"
          if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
            add_to_report "$LINKS_REPORT_FILE" "- Library validator fixed $additional_fixed additional links"
          fi
        fi
      fi
    fi
  done
  
  # Process subdirectories recursively
  for subdir in "$dir"/*/; do
    if [ -d "$subdir" ]; then
      # Skip version control and temporary directories
      if [[ ! "$subdir" == *"/.git/"* && ! "$subdir" == *"/node_modules/"* ]]; then
        # Using local function recursively
        local subdir_result=$(fix_directory_links "$subdir" "$dry_run")
        
        # We can't easily capture counts from subdirectories with this structure
        # but the reporting will be handled at each level independently
      fi
    fi
  done
  
  # Print summary for this directory
  if [ "$dry_run" = "true" ]; then
    print_info "Dry run completed for $dir. Would update $updated_count files."
    
    # Add directory summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
      add_to_report "$LINKS_REPORT_FILE" "**Directory Summary**: Would update $updated_count files out of $((updated_count + unchanged_count)) total markdown files."
    fi
  else
    print_info "Update completed for $dir. Updated $updated_count files, $unchanged_count already correct."
    
    # Add directory summary to report
    if [[ "$USING_LIB" == true ]] && type add_to_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
      add_to_report "$LINKS_REPORT_FILE" "**Directory Summary**: Updated $updated_count files out of $((updated_count + unchanged_count)) total markdown files."
    fi
  fi
  
  # Return the number of updated files from this directory (not including subdirectories)
  echo $updated_count
}

# Main function
main() {
  # Use the library header formatting if available
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
    print_header "Markdown Links Standardization"
  else
    print_header "Fixing Markdown Links to Follow Documentation Standards"
  fi
  
  # Print standards information
  echo "Standards:"
  echo "- Use relative paths with .md extension"
  echo "- Update links to renamed files (PascalCase to kebab-case)"
  echo "- Fix broken links where possible"
  echo ""
  
  local dry_run=false
  local target_dir="docs"
  local verbose=false
  
  # Initialize report if library functions available
  if [[ "$USING_LIB" == true ]] && type initialize_report &>/dev/null; then
    LINKS_REPORT_FILE=$(initialize_report "markdown_links_standardization")
    add_to_report "$LINKS_REPORT_FILE" "# Markdown Links Standardization Report"
    add_to_report "$LINKS_REPORT_FILE" "This report shows changes made to standardize markdown links."
    add_to_report "$LINKS_REPORT_FILE" ""
    add_to_report "$LINKS_REPORT_FILE" "## Standards Applied"
    add_to_report "$LINKS_REPORT_FILE" "- Converting absolute paths to relative paths"
    add_to_report "$LINKS_REPORT_FILE" "- Adding .md extensions where missing"
    add_to_report "$LINKS_REPORT_FILE" "- Converting PascalCase filenames to kebab-case"
    add_to_report "$LINKS_REPORT_FILE" "- Fixing broken links where possible"
    add_to_report "$LINKS_REPORT_FILE" ""
  fi
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        # Use library's help formatting if available
        if [[ "$USING_LIB" == true ]] && type show_help_template &>/dev/null; then
          local script_name="$(basename "$0")"
          local description="Updates Markdown links to follow documentation standards"
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
  if [[ "$USING_LIB" == true ]] && type add_report_section &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
    add_report_section "$LINKS_REPORT_FILE" "Run Configuration"
    add_to_report "$LINKS_REPORT_FILE" "- Target directory: $target_dir"
    add_to_report "$LINKS_REPORT_FILE" "- Dry run mode: $dry_run"
    add_to_report "$LINKS_REPORT_FILE" "- Verbose mode: $verbose"
    add_to_report "$LINKS_REPORT_FILE" "- Using doc-lib.sh: $USING_LIB"
    add_to_report "$LINKS_REPORT_FILE" "- Run date: $(date '+%Y-%m-%d %H:%M:%S')"
    add_to_report "$LINKS_REPORT_FILE" ""
  fi
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Print information about library usage
  if [[ "$USING_LIB" == true ]]; then
    print_info "Using documentation utility library for enhanced link fixing"
  fi
  
  # Process target directory
  if [ -d "$target_dir" ]; then
    local total_updated=$(fix_directory_links "$target_dir" "$dry_run")
    
    # Print information about total updates
    if [ "$dry_run" = "true" ]; then
      print_info "Dry run completed. Would update links in $total_updated files."
    else
      print_success "Link fixing completed. Updated links in $total_updated files."
    fi
    
    # Finalize report if using library
    if [[ "$USING_LIB" == true ]] && type finalize_report &>/dev/null && [ -n "$LINKS_REPORT_FILE" ]; then
      local summary=""
      if [ "$dry_run" = "true" ]; then
        summary="Dry run completed. Would update links in $total_updated files."
      else
        summary="Link fixing completed. Updated links in $total_updated files."
      fi
      
      local report_path=$(finalize_report "$LINKS_REPORT_FILE" "$summary")
      print_info "Report generated: $report_path"
    fi
  else
    print_error "Directory does not exist: $target_dir"
    exit 1
  fi
  
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
    print_header "Link Standardization Complete"
  else
    print_header "Link Fixing Complete"
  fi
  
  echo "Markdown links have been updated to follow documentation standards."
}

# Run the main function
main "$@"