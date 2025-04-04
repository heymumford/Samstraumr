#!/bin/bash
#==============================================================================
# Filename: convert-to-markdown.sh
# Description: Converts various document formats to Markdown
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

# Function to check if a command is available
function command_exists() {
  command -v "$1" &> /dev/null
}

# Check for required tools
function check_dependencies() {
  local missing_tools=()
  
  # List of tools to check
  local tools=("pandoc" "textutil" "libreoffice" "unrtf")
  
  for tool in "${tools[@]}"; do
    if ! command_exists "$tool"; then
      missing_tools+=("$tool")
    fi
  done
  
  if [ ${#missing_tools[@]} -gt 0 ]; then
    print_warning "The following conversion tools are not installed:"
    for tool in "${missing_tools[@]}"; do
      echo "  - $tool"
    done
    
    print_info "For best results, please install these tools:"
    print_info "  sudo apt-get install pandoc unrtf libreoffice-writer"
    print_info "  (macOS: brew install pandoc unrtf libreoffice)"
    
    # Return false if any tools are missing
    return 1
  else
    print_success "All required tools are installed"
    return 0
  fi
}

# Function to convert .rtf to .md
function convert_rtf_to_md() {
  local input_file="$1"
  local output_file="${input_file%.rtf}.md"
  
  print_info "Converting RTF to Markdown: $input_file"
  
  # Try with pandoc first if available
  if command_exists "pandoc"; then
    if pandoc -f rtf -t markdown -o "$output_file" "$input_file"; then
      print_success "Successfully converted using pandoc: $output_file"
      return 0
    else
      print_warning "Pandoc conversion failed, trying alternative method"
    fi
  fi
  
  # Try with textutil on macOS
  if command_exists "textutil" && [[ "$OSTYPE" == "darwin"* ]]; then
    if textutil -convert txt -output "${input_file%.rtf}.txt" "$input_file"; then
      # Convert the text file to markdown
      if command_exists "pandoc"; then
        if pandoc -f plain -t markdown -o "$output_file" "${input_file%.rtf}.txt"; then
          rm "${input_file%.rtf}.txt"  # Clean up temporary file
          print_success "Successfully converted using textutil+pandoc: $output_file"
          return 0
        fi
      else
        # Just rename the .txt to .md
        mv "${input_file%.rtf}.txt" "$output_file"
        print_success "Converted to plain text and renamed as Markdown: $output_file"
        return 0
      fi
    fi
  fi
  
  # Try with unrtf if available
  if command_exists "unrtf"; then
    if unrtf --text "$input_file" > "${input_file%.rtf}.txt"; then
      # Convert the text file to markdown
      if command_exists "pandoc"; then
        if pandoc -f plain -t markdown -o "$output_file" "${input_file%.rtf}.txt"; then
          rm "${input_file%.rtf}.txt"  # Clean up temporary file
          print_success "Successfully converted using unrtf+pandoc: $output_file"
          return 0
        fi
      else
        # Just rename the .txt to .md
        mv "${input_file%.rtf}.txt" "$output_file"
        print_success "Converted to plain text and renamed as Markdown: $output_file"
        return 0
      fi
    else
      print_error "unrtf conversion failed for: $input_file"
    fi
  fi
  
  print_error "Failed to convert RTF file: $input_file"
  return 1
}

# Function to convert .docx to .md
function convert_docx_to_md() {
  local input_file="$1"
  local output_file="${input_file%.docx}.md"
  
  print_info "Converting DOCX to Markdown: $input_file"
  
  # Try with pandoc first if available
  if command_exists "pandoc"; then
    if pandoc -f docx -t markdown -o "$output_file" "$input_file"; then
      print_success "Successfully converted using pandoc: $output_file"
      return 0
    else
      print_warning "Pandoc conversion failed, trying alternative method"
    fi
  fi
  
  # Try with LibreOffice if available
  if command_exists "libreoffice"; then
    local temp_dir=$(mktemp -d)
    
    # Convert to HTML first
    if libreoffice --headless --convert-to html --outdir "$temp_dir" "$input_file"; then
      local html_file="$temp_dir/$(basename "${input_file%.docx}.html")"
      
      # Convert HTML to Markdown
      if command_exists "pandoc"; then
        if pandoc -f html -t markdown -o "$output_file" "$html_file"; then
          rm -rf "$temp_dir"  # Clean up temporary files
          print_success "Successfully converted using libreoffice+pandoc: $output_file"
          return 0
        fi
      else
        # Just save the HTML with .md extension
        cp "$html_file" "$output_file"
        rm -rf "$temp_dir"  # Clean up temporary files
        print_warning "Converted to HTML and saved as Markdown (not ideal): $output_file"
        return 0
      fi
    else
      rm -rf "$temp_dir"  # Clean up temporary directory
      print_error "LibreOffice conversion failed for: $input_file"
    fi
  fi
  
  print_error "Failed to convert DOCX file: $input_file"
  return 1
}

# Function to convert .txt to .md
function convert_txt_to_md() {
  local input_file="$1"
  local output_file="${input_file%.txt}.md"
  
  print_info "Converting TXT to Markdown: $input_file"
  
  # Simply create a copy with .md extension
  if cp "$input_file" "$output_file"; then
    print_success "Successfully copied as Markdown: $output_file"
    return 0
  else
    print_error "Failed to create Markdown from TXT: $input_file"
    return 1
  fi
}

# Main conversion function
function convert_to_markdown() {
  local input_file="$1"
  local extension="${input_file##*.}"
  local dry_run="${2:-false}"
  local keep_original="${3:-false}"
  
  # Skip files that are already markdown
  if [[ "$extension" == "md" ]]; then
    if [[ "$verbose" == "true" ]]; then
      print_info "Skipping already Markdown file: $input_file"
    fi
    return 0
  fi
  
  # Skip files with extensions we don't handle
  if [[ "$extension" != "rtf" && "$extension" != "docx" && "$extension" != "txt" ]]; then
    print_warning "Unsupported file type ($extension): $input_file"
    return 1
  fi
  
  if [[ "$dry_run" == "true" ]]; then
    print_info "Would convert to Markdown: $input_file"
    return 0
  fi
  
  # Call the appropriate conversion function based on extension
  local result=0
  case "$extension" in
    rtf)
      convert_rtf_to_md "$input_file"
      result=$?
      ;;
    docx)
      convert_docx_to_md "$input_file"
      result=$?
      ;;
    txt)
      convert_txt_to_md "$input_file"
      result=$?
      ;;
    *)
      print_warning "Unsupported file type ($extension): $input_file"
      result=1
      ;;
  esac
  
  # Delete original file if conversion was successful and keep_original is false
  if [[ $result -eq 0 && "$keep_original" == "false" ]]; then
    print_info "Removing original file: $input_file"
    rm "$input_file"
  fi
  
  return $result
}

# Process a directory recursively
function process_directory() {
  local dir="$1"
  local dry_run="${2:-false}"
  local keep_original="${3:-false}"
  local recursive="${4:-true}"
  local verbose="${5:-false}"
  
  local converted_count=0
  local skipped_count=0
  local error_count=0
  
  if [[ "$verbose" == "true" ]]; then
    print_info "Processing directory: $dir"
  fi
  
  # Process files in the directory
  for filepath in "$dir"/*; do
    if [ -f "$filepath" ]; then
      local extension="${filepath##*.}"
      
      # Skip if already markdown or not a supported format
      if [[ "$extension" == "md" || "$extension" == "sh" || "$extension" == "html" ]]; then
        if [[ "$verbose" == "true" ]]; then
          print_info "Skipping file: $(basename "$filepath")"
        fi
        skipped_count=$((skipped_count + 1))
        continue
      fi
      
      # Process supported file types
      if [[ "$extension" == "rtf" || "$extension" == "docx" || "$extension" == "txt" ]]; then
        if convert_to_markdown "$filepath" "$dry_run" "$keep_original"; then
          converted_count=$((converted_count + 1))
        else
          error_count=$((error_count + 1))
        fi
      else
        if [[ "$verbose" == "true" ]]; then
          print_warning "Unsupported file type ($extension): $(basename "$filepath")"
        fi
        skipped_count=$((skipped_count + 1))
      fi
    elif [ -d "$filepath" ] && [ "$recursive" = "true" ]; then
      # Process subdirectories if recursive is enabled
      # Skip .git directory
      if [[ "$filepath" != *"/.git"* ]]; then
        local subdir_result
        process_directory "$filepath" "$dry_run" "$keep_original" "$recursive" "$verbose"
        subdir_result=$?
        
        # Add counts from subdirectory
        converted_count=$((converted_count + subdir_result))
      fi
    fi
  done
  
  # Return the count of converted files
  echo $converted_count
}

# Main function
function main() {
  local dry_run=false
  local keep_original=false
  local verbose=false
  local recursive=true
  local target_dir="${PROJECT_ROOT}/docs"
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        print_header "Document to Markdown Converter"
        echo ""
        echo "Converts various document formats to Markdown"
        echo ""
        echo "USAGE:"
        echo "  $(basename "$0") [options] [directory]"
        echo ""
        echo "OPTIONS:"
        echo "  -h, --help           Show this help message"
        echo "  -d, --dry-run        Show what would be converted without making changes"
        echo "  -k, --keep-original  Keep original files after conversion"
        echo "  -v, --verbose        Show detailed information during conversion"
        echo "  -n, --no-recursive   Do not process subdirectories"
        echo ""
        echo "SUPPORTED FORMATS:"
        echo "  .rtf, .docx, .txt"
        echo ""
        echo "EXAMPLES:"
        echo "  $(basename "$0")                # Convert all docs in repo"
        echo "  $(basename "$0") docs/proposals # Convert only proposal docs"
        echo "  $(basename "$0") --dry-run      # See what would be converted"
        echo "  $(basename "$0") --keep-original # Keep original files"
        echo ""
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      -k|--keep-original)
        keep_original=true
        shift
        ;;
      -v|--verbose)
        verbose=true
        shift
        ;;
      -n|--no-recursive)
        recursive=false
        shift
        ;;
      -*)
        print_error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
      *)
        # If argument doesn't start with -, treat as directory
        target_dir="$1"
        shift
        ;;
    esac
  done
  
  print_header "Document to Markdown Converter"
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  if [ "$keep_original" = "true" ]; then
    print_info "Will keep original files after conversion"
  fi
  
  # Check if target directory exists
  if [ ! -d "$target_dir" ]; then
    print_error "Target directory not found: $target_dir"
    exit 1
  fi
  
  # Check for required tools and notify of missing ones
  check_dependencies
  
  # Get the number of files that would be converted
  potential_files=$(find "$target_dir" -type f -not -name "*.md" -not -name "*.sh" -not -name "*.html" | wc -l)
  
  print_info "Found $potential_files non-Markdown files in $target_dir"
  print_info "Starting conversion process..."
  
  # Convert files in the target directory
  converted_count=$(process_directory "$target_dir" "$dry_run" "$keep_original" "$recursive" "$verbose")
  
  # Print summary
  if [ "$dry_run" = "true" ]; then
    print_info "Dry run completed. Would convert $converted_count files."
  else
    print_success "Conversion completed. Converted $converted_count files."
  fi
  
  exit 0
}

# Run the main function
main "$@"