#!/bin/bash
# update-markdown-headers.sh
#
# Script to update Markdown headers to follow documentation standards:
# - Level 1 headers match title case of file
# - Level 2 headers use title case
# - Level 3+ headers use sentence case

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

# Function to convert string to title case
to_title_case() {
  local string="$1"
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
  # Lowercase everything, then capitalize first letter
  echo "$string" | tr '[:upper:]' '[:lower:]' | sed -E 's/^([a-z])/\u\1/'
}

# Function to extract title from kebab-case filename
get_title_from_filename() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  
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
        else
          line="$new_line"
          changes=$((changes + 1))
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
        else
          line="## $title_case"
          changes=$((changes + 1))
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
        else
          line="$header_level $sentence_case"
          changes=$((changes + 1))
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
  elif [[ "$has_level1" == false && "$dry_run" == true ]]; then
    print_info "Would add level 1 header: # $file_title"
  fi
  
  # If changes were made and not in dry run mode, update the file
  if [[ $changes -gt 0 && "$dry_run" == false ]]; then
    mv "$temp_file" "$file"
    print_success "Updated $changes headers in $file"
  else
    rm "$temp_file"
    if [[ $changes -eq 0 ]]; then
      print_info "No changes needed for $file"
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
  
  print_info "Processing directory: $dir"
  
  # Process markdown files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ] && [[ "$filepath" == *.md ]]; then
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
      update_directory_headers "$subdir" "$dry_run"
    fi
  done
  
  # Print summary for this directory
  if [ "$dry_run" = "true" ]; then
    print_info "Dry run completed for $dir. Would update $updated_count files."
  else
    print_info "Update completed for $dir. Updated $updated_count files, $unchanged_count already correct."
  fi
}

# Main function
main() {
  print_header "Updating Markdown Headers to Follow Documentation Standards"
  echo "Standards:"
  echo "- Level 1 headers match title case of file"
  echo "- Level 2 headers use title case"
  echo "- Level 3+ headers use sentence case"
  echo ""
  
  local dry_run=false
  local target_dir="docs"
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        echo "Usage: $0 [options] [directory]"
        echo ""
        echo "Options:"
        echo "  -h, --help     Show this help message"
        echo "  -d, --dry-run  Show what would be changed without making changes"
        echo ""
        echo "If no directory is specified, processes the entire docs directory."
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      *)
        # If argument is a directory, use it as target
        if [ -d "$1" ]; then
          target_dir="$1"
          shift
        else
          echo "Unknown option or invalid directory: $1"
          echo "Use --help for usage information."
          exit 1
        fi
        ;;
    esac
  done
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi

  # Process target directory
  if [ -d "$target_dir" ]; then
    update_directory_headers "$target_dir" "$dry_run"
  else
    print_error "Directory does not exist: $target_dir"
    exit 1
  fi
  
  print_header "Header Update Complete"
  echo "Markdown headers have been updated to follow documentation standards."
}

# Run the main function
main "$@"