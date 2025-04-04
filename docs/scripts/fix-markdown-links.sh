#!/bin/bash
# fix-markdown-links.sh
#
# Script to update Markdown links to follow documentation standards:
# - Use relative paths with .md extension
# - Update links to renamed files (PascalCase to kebab-case)

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

# Function to fix links in a file
fix_file_links() {
  local file="$1"
  local dry_run="${2:-false}"
  local changes=0
  
  print_info "Processing file: $file"
  
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
        else
          changes=$((changes + 1))
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
          
          # Convert to kebab-case
          local kebab_case=$(echo "$filename" | sed 's/\([A-Z]\)/-\L\1/g' | sed 's/^-//')
          
          # Replace in the line if it's a full word
          if [[ "$word" == *"$filename"* ]]; then
            updated_line=$(echo "$updated_line" | sed "s|$filename|$kebab_case|g")
            
            if [[ "$updated_line" != "$line" ]]; then
              if [ "$dry_run" = "true" ]; then
                print_info "Would update link to renamed file:"
                print_info "  From: $line"
                print_info "  To:   $updated_line"
              else
                changes=$((changes + 1))
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
            else
              changes=$((changes + 1))
            fi
          fi
        fi
      done
    fi
    
    # Write the updated line to the temp file
    echo "$updated_line" >> "$temp_file"
  done < "$file"
  
  # If changes were made and not in dry run mode, update the file
  if [[ $changes -gt 0 && "$dry_run" == false ]]; then
    mv "$temp_file" "$file"
    print_success "Updated $changes links in $file"
  else
    rm "$temp_file"
    if [[ $changes -eq 0 ]]; then
      print_info "No changes needed for $file"
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
  
  print_info "Processing directory: $dir"
  
  # Process markdown files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ] && [[ "$filepath" == *.md ]]; then
      if fix_file_links "$filepath" "$dry_run"; then
        updated_count=$((updated_count + 1))
      else
        unchanged_count=$((unchanged_count + 1))
      fi
    fi
  done
  
  # Process subdirectories recursively
  for subdir in "$dir"/*/; do
    if [ -d "$subdir" ]; then
      fix_directory_links "$subdir" "$dry_run"
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
  print_header "Fixing Markdown Links to Follow Documentation Standards"
  echo "Standards:"
  echo "- Use relative paths with .md extension"
  echo "- Update links to renamed files (PascalCase to kebab-case)"
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
    fix_directory_links "$target_dir" "$dry_run"
  else
    print_error "Directory does not exist: $target_dir"
    exit 1
  fi
  
  print_header "Link Fixing Complete"
  echo "Markdown links have been updated to follow documentation standards."
}

# Run the main function
main "$@"