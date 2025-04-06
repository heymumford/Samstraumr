#!/bin/bash
#==============================================================================
# Filename: doc-lib.sh
# Description: Library of shared documentation utilities for Samstraumr scripts
#
# This library provides specialized functions for documentation-related scripts
# including markdown file processing, filename standardization, cross-reference
# validation, and report generation.
#
# USAGE:
#   source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
#
# PROVIDES:
#   - File naming utilities (kebab-case, PascalCase)
#   - Markdown content analysis
#   - Cross-reference functions
#   - TODO processing
#   - Documentation reporting
#   - Format standardization
#
# DEPENDENCIES:
#   - unified-common.sh (optional, will use basic implementations if not available)
#==============================================================================

# Determine script paths
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the unified common library if available
if [ -f "${SCRIPT_LIB_DIR}/unified-common.sh" ]; then
    source "${SCRIPT_LIB_DIR}/unified-common.sh"
    USING_UNIFIED_LIB=true
else
    USING_UNIFIED_LIB=false
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
    
    function command_exists() {
        command -v "$1" &> /dev/null
    }
fi

#------------------------------------------------------------------------------
# Constants
#------------------------------------------------------------------------------
REPORTS_DIR="${PROJECT_ROOT}/reports/docs"
CURRENT_DATE=$(date +"%Y-%m-%d")
CURRENT_TIME=$(date +"%H-%M-%S")

#------------------------------------------------------------------------------
# Report Handling
#------------------------------------------------------------------------------
# Functions for creating and managing documentation reports
#------------------------------------------------------------------------------

# Create the reports directory if it doesn't exist
function ensure_reports_directory() {
    if [ ! -d "$REPORTS_DIR" ]; then
        mkdir -p "$REPORTS_DIR"
        print_info "Created reports directory: $REPORTS_DIR"
    fi
}

# Initialize a new report with header
# Usage: initialize_report "report_name"
# Returns: Path to the report file
function initialize_report() {
    local report_name="$1"
    local timestamp="${CURRENT_DATE}_${CURRENT_TIME}"
    local report_file="${REPORTS_DIR}/${report_name}_${timestamp}.md"
    
    ensure_reports_directory
    
    # Create report with header
    cat > "$report_file" << EOF
# ${report_name} Report
Generated: $(date "+%Y-%m-%d %H:%M:%S")

EOF
    
    echo "$report_file"
}

# Add a line to a report file
# Usage: add_to_report "report_file" "content"
function add_to_report() {
    local report_file="$1"
    local content="$2"
    
    echo "$content" >> "$report_file"
}

# Add a section header to a report file
# Usage: add_report_section "report_file" "section_title"
function add_report_section() {
    local report_file="$1"
    local section_title="$2"
    
    echo -e "\n## $section_title\n" >> "$report_file"
}

# Add a subsection header to a report file
# Usage: add_report_subsection "report_file" "subsection_title"
function add_report_subsection() {
    local report_file="$1"
    local subsection_title="$2"
    
    echo -e "\n### $subsection_title\n" >> "$report_file"
}

# Finalize a report with summary information
# Usage: finalize_report "report_file" "summary_text"
# Returns: Path to the finalized report
function finalize_report() {
    local report_file="$1"
    local summary="$2"
    
    # Add summary section
    echo -e "\n## Summary\n" >> "$report_file"
    echo "$summary" >> "$report_file"
    echo -e "\n*End of Report*" >> "$report_file"
    
    print_info "Report generated: $report_file"
    echo "$report_file"
}

#------------------------------------------------------------------------------
# File Naming Functions
#------------------------------------------------------------------------------
# Functions for standardizing file names across documentation
#------------------------------------------------------------------------------

# Convert filename to kebab-case (lowercase with hyphens)
# Usage: kebab_name=$(to_kebab_case "FileName.txt")
# Returns: Kebab-case filename (e.g., "file-name.txt")
function to_kebab_case() {
    local filename="$1"
    local basename="${filename%.*}"  # Remove extension
    local extension="${filename##*.}"  # Keep extension
    
    # First handle camelCase and PascalCase by inserting hyphens before capital letters
    local with_hyphens=$(echo "$basename" | sed -E 's/([a-z0-9])([A-Z])/\1-\2/g')
    
    # Convert to lowercase and replace underscores and spaces with hyphens
    local kebab_case=$(echo "$with_hyphens" | tr '[:upper:]' '[:lower:]' | tr '_' '-' | tr ' ' '-')
    
    # Replace multiple consecutive hyphens with a single hyphen
    kebab_case=$(echo "$kebab_case" | sed -E 's/-+/-/g')
    
    # Remove any leading or trailing hyphens
    kebab_case=$(echo "$kebab_case" | sed -E 's/^-+|-+$//g')
    
    # Return result with extension
    echo "${kebab_case}.${extension}"
}

# Convert filename to PascalCase (capital first letters, no separators)
# Usage: pascal_name=$(to_pascal_case "file-name.txt")
# Returns: PascalCase filename (e.g., "FileName.txt")
function to_pascal_case() {
    local filename="$1"
    local basename="${filename%.*}"  # Remove extension
    local extension="${filename##*.}"  # Keep extension
    
    # Special case for README.md and other documentation files
    if [[ "$basename" == "README" || "$basename" == "CHANGELOG" || 
          "$basename" == "LICENSE" || "$basename" == "CONTRIBUTING" ||
          "$basename" == "CLAUDE" ]]; then
        echo "$filename"
        return
    fi
    
    # Handle acronyms (all caps)
    if [[ "$basename" =~ ^[A-Z]{2,}$ ]]; then
        echo "$filename"
        return
    fi
    
    # Convert kebab-case or snake_case to PascalCase
    # Replace hyphens and underscores with spaces
    local spaced=$(echo "$basename" | tr '-_' '  ')
    
    # Capitalize first letter of each word and remove spaces
    local pascal_case=$(echo "$spaced" | awk 'BEGIN{FS=OFS=" "} {for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)} 1' | tr -d ' ')
    
    # Return result with extension
    echo "${pascal_case}.${extension}"
}

# Standardize directory filenames based on conventions
# Usage: standardize_directory_filenames "directory" "kebab|pascal" "pattern"
# Returns: Number of files renamed
function standardize_directory_filenames() {
    local directory="$1"
    local case_type="${2:-kebab}"
    local pattern="${3:-*}"
    local count=0
    
    if [ ! -d "$directory" ]; then
        print_error "Directory not found: $directory"
        return 1
    fi
    
    # Create a report file for tracking changes
    local report_file=$(initialize_report "filename_standardization")
    add_to_report "$report_file" "Standardizing filenames in: $directory"
    add_to_report "$report_file" "Case type: $case_type"
    add_to_report "$report_file" "Pattern: $pattern"
    add_report_section "$report_file" "Files Renamed"
    
    # Process files in the directory
    for filepath in "$directory"/$pattern; do
        if [ -f "$filepath" ]; then
            local filename=$(basename "$filepath")
            local new_filename=""
            
            # Apply the appropriate case conversion
            if [ "$case_type" = "kebab" ]; then
                new_filename=$(to_kebab_case "$filename")
            elif [ "$case_type" = "pascal" ]; then
                new_filename=$(to_pascal_case "$filename")
            else
                print_error "Invalid case type. Use 'kebab' or 'pascal'."
                return 1
            fi
            
            # Rename the file if needed
            if [ "$filename" != "$new_filename" ]; then
                local dirpath=$(dirname "$filepath")
                
                # Check if the new filename already exists
                if [ -f "$dirpath/$new_filename" ]; then
                    print_warning "Cannot rename: Target exists: $new_filename"
                    add_to_report "$report_file" "⚠️ SKIPPED: $filename → $new_filename (target file exists)"
                    continue
                fi
                
                # Rename the file
                if mv "$filepath" "$dirpath/$new_filename"; then
                    print_success "Renamed: $filename → $new_filename"
                    add_to_report "$report_file" "✅ Renamed: $filename → $new_filename"
                    count=$((count + 1))
                else
                    print_error "Failed to rename: $filename"
                    add_to_report "$report_file" "❌ Failed: $filename → $new_filename"
                fi
            fi
        fi
    done
    
    # Finalize the report
    finalize_report "$report_file" "Total files renamed: $count"
    
    return $count
}

#------------------------------------------------------------------------------
# Markdown Content Analysis
#------------------------------------------------------------------------------
# Functions for analyzing and validating markdown content
#------------------------------------------------------------------------------

# Extract headers from markdown file
# Usage: extract_markdown_headers "file.md"
# Returns: List of headers with their levels
function extract_markdown_headers() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Extract ATX headers (# Header format)
    grep -n "^#\+ " "$file" | sed -E 's/^([0-9]+):(#+) (.*)$/\1|\2|\3/'
}

# Extract front matter from markdown file
# Usage: extract_markdown_frontmatter "file.md"
# Returns: Content between --- markers
function extract_markdown_frontmatter() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Check if file starts with ---
    if grep -q "^---" "$file"; then
        sed -n '/^---$/,/^---$/p' "$file"
    fi
}

# Check if markdown file has a title (# Title)
# Usage: if has_markdown_title "file.md"; then ...
# Returns: 0 if has title, 1 otherwise
function has_markdown_title() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Check for level 1 header at start of file (allowing for front matter)
    if grep -q "^---" "$file"; then
        # Skip front matter
        if sed -n '/^---$/,/^---$/!p' "$file" | grep -q "^# "; then
            return 0
        fi
    else
        # No front matter, check from beginning
        if grep -q "^# " "$file"; then
            return 0
        fi
    fi
    
    return 1
}

# Create a markdown table
# Usage: create_markdown_table "header1,header2" "row1col1,row1col2" "row2col1,row2col2"
# Returns: Markdown table as string
function create_markdown_table() {
    local headers="$1"
    shift
    
    # Parse headers
    IFS=',' read -ra header_array <<< "$headers"
    
    # Generate header row
    local table="| "
    for header in "${header_array[@]}"; do
        table+="$header | "
    done
    table+="\n"
    
    # Generate separator row
    local separator="| "
    for header in "${header_array[@]}"; do
        separator+="------ | "
    done
    table+="$separator\n"
    
    # Generate data rows
    for row in "$@"; do
        IFS=',' read -ra row_array <<< "$row"
        local row_str="| "
        for ((i=0; i<${#header_array[@]}; i++)); do
            row_str+="${row_array[$i]:-} | "
        done
        table+="$row_str\n"
    done
    
    # Output table
    echo -e "$table"
}

#------------------------------------------------------------------------------
# Cross-Reference Functions
#------------------------------------------------------------------------------
# Functions for handling and validating cross-references in documentation
#------------------------------------------------------------------------------

# Extract all markdown links from a file
# Usage: extract_markdown_links "file.md"
# Returns: List of links with line numbers
function extract_markdown_links() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Extract markdown links [text](url)
    grep -n -o "\[.*\](.*)" "$file" | sed -E 's/^([0-9]+):(.*)$/\1|\2/'
}

# Validate internal links in a markdown file
# Usage: validate_internal_links "file.md" "root_dir"
# Returns: List of broken links
function validate_internal_links() {
    local file="$1"
    local root_dir="${2:-$(dirname "$file")}"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Extract all links
    local links=$(extract_markdown_links "$file")
    local broken_links=()
    
    # Check each link
    while IFS="|" read -r line_num link; do
        # Extract the URL part
        local url=$(echo "$link" | sed -E 's/.*\]\(([^)]+)\).*/\1/')
        
        # Skip external links and anchor links
        if [[ "$url" =~ ^http || "$url" =~ ^# ]]; then
            continue
        fi
        
        # Handle relative paths
        local target_path=""
        if [[ "$url" == /* ]]; then
            # Absolute path from repo root
            target_path="${root_dir}${url}"
        else
            # Relative path from current file
            target_path="$(dirname "$file")/${url}"
        fi
        
        # Remove any anchor part
        target_path="${target_path%%#*}"
        
        # Check if file exists
        if [ ! -e "$target_path" ]; then
            broken_links+=("$line_num|$url")
        fi
    done <<< "$links"
    
    # Output broken links
    if [ ${#broken_links[@]} -gt 0 ]; then
        for broken in "${broken_links[@]}"; do
            echo "$broken"
        done
        return 1
    fi
    
    return 0
}

#------------------------------------------------------------------------------
# TODO Processing
#------------------------------------------------------------------------------
# Functions for extracting and processing TODOs from the codebase
#------------------------------------------------------------------------------

# Extract TODO comments from files
# Usage: extract_todos "directory" "pattern"
# Returns: List of TODOs with file and line information
function extract_todos() {
    local directory="$1"
    local pattern="${2:-*}"
    
    if [ ! -d "$directory" ]; then
        print_error "Directory not found: $directory"
        return 1
    fi
    
    # Create a report file for tracking TODOs
    local report_file=$(initialize_report "todo_extraction")
    add_to_report "$report_file" "Extracting TODOs from: $directory"
    add_to_report "$report_file" "Pattern: $pattern"
    add_report_section "$report_file" "TODOs Found"
    
    # Find files matching the pattern and grep for TODO comments
    local todos=$(find "$directory" -type f -name "$pattern" -not -path "*/\.*" | xargs grep -n "TODO" 2>/dev/null)
    
    # Process and format each TODO
    local count=0
    while IFS=':' read -r file line content; do
        # Extract the actual TODO text
        local todo_text=$(echo "$content" | sed -E 's/.*TODO:? *(.*)/\1/')
        
        # Add to report
        add_to_report "$report_file" "- **$file** (Line $line): $todo_text"
        count=$((count + 1))
    done <<< "$todos"
    
    # Finalize the report
    finalize_report "$report_file" "Total TODOs found: $count"
    
    # Output the TODO count
    echo "$count"
}

# Check TODO format compliance
# Usage: check_todo_format "directory" "pattern"
# Returns: List of non-compliant TODOs
function check_todo_format() {
    local directory="$1"
    local pattern="${2:-*}"
    
    if [ ! -d "$directory" ]; then
        print_error "Directory not found: $directory"
        return 1
    fi
    
    # Create a report file for tracking compliance
    local report_file=$(initialize_report "todo_format_check")
    add_to_report "$report_file" "Checking TODO format compliance in: $directory"
    add_to_report "$report_file" "Pattern: $pattern"
    add_report_section "$report_file" "Non-Compliant TODOs"
    
    # Find files matching the pattern and grep for TODO comments
    local todos=$(find "$directory" -type f -name "$pattern" -not -path "*/\.*" | xargs grep -n "TODO" 2>/dev/null)
    
    # Standard format: TODO: description [owner] [YYYY-MM-DD]
    local non_compliant=0
    local total=0
    
    while IFS=':' read -r file line content; do
        total=$((total + 1))
        
        # Check if the TODO has a colon
        if ! echo "$content" | grep -q "TODO:"; then
            add_to_report "$report_file" "- **$file** (Line $line): Missing colon after TODO"
            non_compliant=$((non_compliant + 1))
            continue
        fi
        
        # Check if the TODO has a description
        local description=$(echo "$content" | sed -E 's/.*TODO: *(.*)/\1/')
        if [ -z "$description" ]; then
            add_to_report "$report_file" "- **$file** (Line $line): Missing description"
            non_compliant=$((non_compliant + 1))
            continue
        fi
    done <<< "$todos"
    
    # Calculate compliance percentage
    local compliance=0
    if [ $total -gt 0 ]; then
        compliance=$(( (total - non_compliant) * 100 / total ))
    fi
    
    # Finalize the report
    finalize_report "$report_file" "Total TODOs: $total\nNon-compliant TODOs: $non_compliant\nCompliance rate: ${compliance}%"
    
    # Output the number of non-compliant TODOs
    echo "$non_compliant"
}

#------------------------------------------------------------------------------
# Document Generation
#------------------------------------------------------------------------------
# Functions for generating documentation from templates and source code
#------------------------------------------------------------------------------

# Generate a README from a template
# Usage: generate_readme_from_template "template.md" "output.md" "replacements.txt"
# Returns: Path to generated README
function generate_readme_from_template() {
    local template="$1"
    local output="$2"
    local replacements="$3"
    
    if [ ! -f "$template" ]; then
        print_error "Template file not found: $template"
        return 1
    fi
    
    # Copy template to output
    cp "$template" "$output"
    
    # Apply replacements from file
    if [ -f "$replacements" ]; then
        while IFS='=' read -r key value; do
            # Skip empty lines and comments
            if [[ -z "$key" || "$key" == \#* ]]; then
                continue
            fi
            
            # Replace placeholders in the form {{key}}
            sed -i "s#{{$key}}#$value#g" "$output"
        done < "$replacements"
    fi
    
    # Remove any remaining placeholders
    sed -i "s#{{[^}]*}}##g" "$output"
    
    print_success "Generated README: $output"
    echo "$output"
}

# Extract JavaDoc-style comments from a Java file
# Usage: extract_javadoc "file.java"
# Returns: Markdown representation of JavaDoc comments
function extract_javadoc() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Create an output markdown file
    local output="${file%.java}.md"
    
    # Extract class name
    local class_name=$(grep -m 1 "class " "$file" | sed -E 's/.*class +([A-Za-z0-9_]+).*/\1/')
    
    # Start the markdown file
    echo "# ${class_name} Documentation" > "$output"
    echo "" >> "$output"
    
    # Extract class javadoc
    local class_doc=$(sed -n '/\/\*\*/,/\*\//p' "$file" | grep -v "\*\*\|\*/\|/\*\*" | sed 's/^ *\* *//')
    
    if [ -n "$class_doc" ]; then
        echo "## Overview" >> "$output"
        echo "" >> "$output"
        echo "$class_doc" >> "$output"
        echo "" >> "$output"
    fi
    
    # Extract methods and their javadoc
    echo "## Methods" >> "$output"
    echo "" >> "$output"
    
    # This complex grep and sed extracts method javadoc blocks and the following method signature
    grep -A 1 -B 20 "public\|protected\|private" "$file" | awk 'BEGIN{RS="--"} /\/\*\*.*\*\/.*\)/' | while read -r block; do
        # Extract method name and signature
        local method=$(echo "$block" | grep -oE "(public|protected|private)[ \t]+[A-Za-z0-9_<>]+[ \t]+[A-Za-z0-9_]+\([^)]*\)" | head -1)
        
        if [ -n "$method" ]; then
            local method_name=$(echo "$method" | grep -oE "[A-Za-z0-9_]+\([^)]*\)" | sed 's/(.*//')
            
            echo "### ${method_name}" >> "$output"
            echo "" >> "$output"
            echo "```java" >> "$output"
            echo "$method" >> "$output"
            echo "```" >> "$output"
            echo "" >> "$output"
            
            # Extract javadoc for this method
            local javadoc=$(echo "$block" | sed -n '/\/\*\*/,/\*\//p' | grep -v "\*\*\|\*/\|/\*\*" | sed 's/^ *\* *//')
            
            if [ -n "$javadoc" ]; then
                echo "$javadoc" >> "$output"
                echo "" >> "$output"
            fi
        fi
    done
    
    print_success "Generated documentation: $output"
    echo "$output"
}

#------------------------------------------------------------------------------
# Documentation Fixing
#------------------------------------------------------------------------------
# Functions for automatically fixing common documentation issues
#------------------------------------------------------------------------------

# Fix markdown links to match target case
# Usage: fix_markdown_links "file.md" "root_dir"
# Returns: Number of links fixed
function fix_markdown_links() {
    local file="$1"
    local root_dir="${2:-$(dirname "$file")}"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Create a report file for tracking changes
    local report_file=$(initialize_report "markdown_link_fixes")
    add_to_report "$report_file" "Fixing markdown links in: $file"
    add_report_section "$report_file" "Links Fixed"
    
    # Extract all links
    local links=$(extract_markdown_links "$file")
    local fixed_count=0
    
    # Create a temporary file
    local temp_file="${file}.tmp"
    cp "$file" "$temp_file"
    
    # Check each link
    while IFS="|" read -r line_num link; do
        # Extract the URL part
        local url=$(echo "$link" | sed -E 's/.*\]\(([^)]+)\).*/\1/')
        
        # Skip external links and anchor links
        if [[ "$url" =~ ^http || "$url" =~ ^# ]]; then
            continue
        fi
        
        # Handle relative paths
        local target_path=""
        if [[ "$url" == /* ]]; then
            # Absolute path from repo root
            target_path="${root_dir}${url}"
        else
            # Relative path from current file
            target_path="$(dirname "$file")/${url}"
        fi
        
        # Remove any anchor part
        local anchor=""
        if [[ "$url" == *#* ]]; then
            anchor="#${url#*#}"
            target_path="${target_path%%#*}"
        fi
        
        # Check if the path exists with a different case
        if [ ! -e "$target_path" ]; then
            # Try to find the actual file with case-insensitive search
            local dir_path=$(dirname "$target_path")
            local base_name=$(basename "$target_path")
            
            if [ -d "$dir_path" ]; then
                local actual_file=$(find "$dir_path" -maxdepth 1 -type f -iname "$base_name" | head -1)
                
                if [ -n "$actual_file" ]; then
                    # Get the actual filename with correct case
                    local actual_name=$(basename "$actual_file")
                    
                    # Determine the correct URL to replace with
                    local correct_url=""
                    if [[ "$url" == /* ]]; then
                        # Absolute path, replace just the filename
                        correct_url="$(dirname "$url")/${actual_name}${anchor}"
                    else
                        # Relative path, replace just the filename
                        correct_url="$(dirname "$url")/${actual_name}${anchor}"
                    fi
                    
                    # Replace the link in the file
                    local original_link=$(echo "$link" | sed -E 's/\[/\\[/g' | sed -E 's/\]/\\]/g' | sed -E 's/\(/\\(/g' | sed -E 's/\)/\\)/g')
                    local fixed_link=$(echo "$link" | sed -E "s#\\($url\\)#($correct_url)#")
                    
                    sed -i "s#$original_link#$fixed_link#g" "$temp_file"
                    
                    add_to_report "$report_file" "- Line $line_num: $url → $correct_url"
                    fixed_count=$((fixed_count + 1))
                fi
            fi
        fi
    done <<< "$links"
    
    # Only replace the original file if changes were made
    if [ $fixed_count -gt 0 ]; then
        mv "$temp_file" "$file"
        print_success "Fixed $fixed_count links in $file"
    else
        rm "$temp_file"
        print_info "No links needed fixing in $file"
    fi
    
    # Finalize the report
    finalize_report "$report_file" "Total links fixed: $fixed_count"
    
    # Return the number of links fixed
    echo $fixed_count
}

# Fix common markdown formatting issues
# Usage: fix_markdown_formatting "file.md"
# Returns: Number of issues fixed
function fix_markdown_formatting() {
    local file="$1"
    
    if [ ! -f "$file" ]; then
        print_error "File not found: $file"
        return 1
    fi
    
    # Create a report file for tracking changes
    local report_file=$(initialize_report "markdown_formatting_fixes")
    add_to_report "$report_file" "Fixing markdown formatting in: $file"
    add_report_section "$report_file" "Issues Fixed"
    
    # Create a temporary file
    local temp_file="${file}.tmp"
    cp "$file" "$temp_file"
    
    local fixed_count=0
    
    # 1. Fix headers missing space after #
    if grep -q "^#[^#[:space:]]" "$temp_file"; then
        sed -i -E 's/^(#+)([^#[:space:]])/\1 \2/g' "$temp_file"
        add_to_report "$report_file" "- Fixed headers missing space after #"
        fixed_count=$((fixed_count + 1))
    fi
    
    # 2. Fix inconsistent list item spacing
    if grep -q "^[[:space:]]*[-*+][^[:space:]]" "$temp_file"; then
        sed -i -E 's/^([[:space:]]*[-*+])([^[:space:]])/\1 \2/g' "$temp_file"
        add_to_report "$report_file" "- Fixed list items missing space after marker"
        fixed_count=$((fixed_count + 1))
    fi
    
    # 3. Fix missing blank lines between sections
    if grep -n "^#" "$temp_file" | grep -v '^1:' | while read -r line; do
        line_num=$(echo "$line" | cut -d: -f1)
        prev_line=$((line_num - 1))
        if [ "$(sed -n "${prev_line}p" "$temp_file" | grep -c "^[^[:space:]]")" -gt 0 ]; then
            sed -i "${prev_line}s/$/\n/" "$temp_file"
            add_to_report "$report_file" "- Fixed missing blank line before header at line $line_num"
            fixed_count=$((fixed_count + 1))
        fi
    done; then
        :  # No-op to handle the while loop
    fi
    
    # 4. Fix trailing whitespace
    if grep -q "[[:space:]]$" "$temp_file"; then
        sed -i 's/[[:space:]]*$//' "$temp_file"
        add_to_report "$report_file" "- Fixed trailing whitespace"
        fixed_count=$((fixed_count + 1))
    fi
    
    # Only replace the original file if changes were made
    if [ $fixed_count -gt 0 ]; then
        mv "$temp_file" "$file"
        print_success "Fixed $fixed_count formatting issues in $file"
    else
        rm "$temp_file"
        print_info "No formatting issues found in $file"
    fi
    
    # Finalize the report
    finalize_report "$report_file" "Total issues fixed: $fixed_count"
    
    # Return the number of issues fixed
    echo $fixed_count
}

#------------------------------------------------------------------------------
# Conversion Functions
#------------------------------------------------------------------------------
# Functions for document format conversion and standardization
#------------------------------------------------------------------------------

# Initialize a conversion report
# Usage: initialize_conversion_report "report_name"
# Returns: Path to the report file
function initialize_conversion_report() {
    local report_name="$1"
    local report_file=$(initialize_report "$report_name")
    
    # Store the report file path in a global variable
    CONVERSION_REPORT_FILE="$report_file"
    
    echo "$report_file"
}

# Add a line to the conversion report
# Usage: add_to_conversion_report "content"
function add_to_conversion_report() {
    local content="$1"
    
    if [ -n "$CONVERSION_REPORT_FILE" ] && [ -f "$CONVERSION_REPORT_FILE" ]; then
        add_to_report "$CONVERSION_REPORT_FILE" "$content"
    fi
}

# Start processing a directory in the conversion report
# Usage: start_directory_processing "directory"
function start_directory_processing() {
    local directory="$1"
    
    if [ -n "$CONVERSION_REPORT_FILE" ] && [ -f "$CONVERSION_REPORT_FILE" ]; then
        add_report_section "$CONVERSION_REPORT_FILE" "Processing: $directory"
    fi
}

# End processing a directory in the conversion report
# Usage: end_directory_processing "directory" "converted" "skipped" "errors"
function end_directory_processing() {
    local directory="$1"
    local converted="$2"
    local skipped="${3:-0}"
    local errors="${4:-0}"
    
    if [ -n "$CONVERSION_REPORT_FILE" ] && [ -f "$CONVERSION_REPORT_FILE" ]; then
        add_to_report "$CONVERSION_REPORT_FILE" "**Summary for $directory**:"
        add_to_report "$CONVERSION_REPORT_FILE" "- Converted: $converted files"
        add_to_report "$CONVERSION_REPORT_FILE" "- Skipped: $skipped files"
        add_to_report "$CONVERSION_REPORT_FILE" "- Errors: $errors files"
    fi
}

# Finalize the conversion report
# Usage: finalize_conversion_report
# Returns: Path to the finalized report
function finalize_conversion_report() {
    if [ -n "$CONVERSION_REPORT_FILE" ] && [ -f "$CONVERSION_REPORT_FILE" ]; then
        add_report_section "$CONVERSION_REPORT_FILE" "Conversion Complete"
        
        # Add timestamp
        add_to_report "$CONVERSION_REPORT_FILE" "Finished: $(date "+%Y-%m-%d %H:%M:%S")"
        
        # Return the report file path
        echo "$CONVERSION_REPORT_FILE"
    fi
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------
# Initialization section for the documentation library
# This exports all functions and performs final setup tasks
#------------------------------------------------------------------------------

# Export all library functions to make them available to sourcing scripts
# Reports
export -f ensure_reports_directory
export -f initialize_report
export -f add_to_report
export -f add_report_section
export -f add_report_subsection
export -f finalize_report

# File Naming
export -f to_kebab_case
export -f to_pascal_case
export -f standardize_directory_filenames

# Markdown Content
export -f extract_markdown_headers
export -f extract_markdown_frontmatter
export -f has_markdown_title
export -f create_markdown_table

# Cross-References
export -f extract_markdown_links
export -f validate_internal_links

# TODOs
export -f extract_todos
export -f check_todo_format

# Document Generation
export -f generate_readme_from_template
export -f extract_javadoc

# Documentation Fixing
export -f fix_markdown_links
export -f fix_markdown_formatting

# Conversion Utilities
export -f initialize_conversion_report
export -f add_to_conversion_report
export -f start_directory_processing
export -f end_directory_processing
export -f finalize_conversion_report

print_info "Loaded documentation library (doc-lib.sh)"
