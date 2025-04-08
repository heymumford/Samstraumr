#!/usr/bin/env bash
# doc-integrity-check.sh - Checks documentation integrity across the repository
# 
# This script performs several checks on documentation files:
# 1. Verifies all links in markdown files reference existing files
# 2. Ensures consistency in section references
# 3. Validates that key sections and document structures are consistent
# 4. Checks for package reference consistency
# 5. Validates code examples use correct package names
# 6. Ensures consistent header format with copyright notices
# 7. Generates a documentation integrity report

set -e

# Exit if this script is being sourced by another script
if [[ "$1" == "--source-only" ]]; then
  return 0
fi

# Find repository root
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
  # Terminal colors
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  NC='\033[0m' # No Color

  # Functions for prettier output
  info() { echo -e "${BLUE}$1${NC}"; }
  success() { echo -e "${GREEN}$1${NC}"; }
  error() { echo -e "${RED}Error: $1${NC}" >&2; }
  warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }
fi

# Only show the startup message if not sourced
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  info "Starting documentation integrity check..."
fi

# Variable to track errors
ERRORS=0
WARNINGS=0

# Create reports directory if it doesn't exist yet
REPORT_DIR="${PROJECT_ROOT}/target/doc-reports"
mkdir -p "$REPORT_DIR"

# 1. Check for broken internal markdown links
check_internal_links() {
  info "Checking for broken internal markdown links..."
  
  # Find all markdown files
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Use library function if available
    if [[ "$USING_LIB" == true ]] && type find_broken_links &>/dev/null; then
      local broken_links=$(find_broken_links "$file")
      
      if [ -n "$broken_links" ]; then
        # Process each broken link found by the library function
        echo "$broken_links" | while read -r link_info; do
          error "Broken link in $file: $link_info"
          ERRORS=$((ERRORS + 1))
        done
      fi
    else
      # Fall back to original implementation
      # Extract links with the form [text](path.md) or [text](path/to/file.md)
      local links=($(grep -oP '\[.*?\]\(\K[^)]+(?=\))' "$file" | grep '\.md'))
      
      for link in "${links[@]}"; do
        # Skip external links
        if [[ "$link" =~ ^https?:// ]]; then
          continue
        fi
        
        # Handle relative links
        local target_file
        if [[ "$link" = /* ]]; then
          # Absolute path within the repository
          target_file="${PROJECT_ROOT}${link}"
        else
          # Relative path
          target_file="$(dirname "$file")/$link"
        fi
        
        # Normalize path to handle ../ references
        target_file=$(realpath --relative-to="$PROJECT_ROOT" "$target_file" 2>/dev/null || echo "invalid_path")
        
        # Check if the file exists
        if [[ ! -f "$target_file" && "$target_file" != "invalid_path" ]]; then
          error "Broken link in $file: $link -> $target_file (file not found)"
          ERRORS=$((ERRORS + 1))
        fi
      done
    fi
  done
}

# 2. Check for missing section references
check_section_references() {
  info "Checking for missing section references..."
  
  # Find all markdown files
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Extract links with section references like [text](#section)
    local section_refs=($(grep -oP '\[.*?\]\(\K#[^)]+(?=\))' "$file"))
    
    for section_ref in "${section_refs[@]}"; do
      # Remove the # and convert to lowercase
      local section_name="${section_ref#\#}"
      section_name=$(echo "$section_name" | tr '[:upper:]' '[:lower:]' | tr ' ' '-' | sed 's/[^a-z0-9-]//g')
      
      # Check if the section exists in the file
      if ! grep -q "^#.*${section_name}" "$file"; then
        warning "Possible missing section reference in $file: $section_ref"
        WARNINGS=$((WARNINGS + 1))
      fi
    done
  done
}

# 3. Check for consistent README structure
check_readme_structure() {
  info "Checking README structure consistency..."
  
  local readme_files
  
  # Use library function if available
  if [[ "$USING_LIB" == true ]] && type find_readme_files &>/dev/null; then
    # Get README files using the library function
    readme_files=($(find_readme_files))
    
    # Also check for missing README files in directories
    if type find_missing_readmes &>/dev/null; then
      local missing_readmes=($(find_missing_readmes))
      for dir in "${missing_readmes[@]}"; do
        warning "Directory missing README.md: $dir"
        WARNINGS=$((WARNINGS + 1))
      done
    fi
  else
    # Fall back to original implementation
    readme_files=($(find . -type f -name "README.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  fi
  
  for file in "${readme_files[@]}"; do
    # Check if file contains required sections
    if [[ "$USING_LIB" == true ]] && type has_level1_header &>/dev/null; then
      if ! has_level1_header "$file"; then
        warning "Missing title in $file"
        WARNINGS=$((WARNINGS + 1))
      fi
    else
      # Fallback implementation
      if ! grep -q "^# " "$file"; then
        warning "Missing title in $file"
        WARNINGS=$((WARNINGS + 1))
      fi
    fi
    
    if [[ "$file" =~ docs/.*/README.md ]]; then
      # For documentation README files, check for additional requirements
      if ! grep -q -E "^## (Purpose|Contents|Related)" "$file"; then
        warning "Documentation README $file missing standard sections (Purpose, Contents, or Related)"
        WARNINGS=$((WARNINGS + 1))
      fi
    fi
  done
}

# 4. Check for package reference consistency
check_package_references() {
  info "Checking package references in documentation..."
  
  # Find all markdown files
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Look for old package references
    if grep -q -E "org\.s8r|org\.tube" "$file"; then
      warning "Found old package references in $file (org.s8r or org.tube)"
      WARNINGS=$((WARNINGS + 1))
    fi
  done
}

# 5. Check for code examples using correct package
check_code_examples() {
  info "Checking code examples in documentation..."
  
  # Find all markdown files
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Look for Java code blocks
    if grep -q '```java' "$file"; then
      # Get all content between ```java and the next ```
      local code_blocks=$(awk '/```java/,/```/ { print }' "$file")
      
      # Check if code blocks contain old package references
      if echo "$code_blocks" | grep -q -E "org\.s8r|org\.tube"; then
        warning "Found old package references in code examples in $file"
        WARNINGS=$((WARNINGS + 1))
      fi
    fi
  done
}

# 6. Check for consistent header format
check_header_format() {
  info "Checking header format consistency..."
  
  # Find all markdown files
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Check if file has the standard copyright header
    if ! grep -q "Copyright (c)" "$file"; then
      warning "Missing copyright header in $file"
      WARNINGS=$((WARNINGS + 1))
    fi
  done
}

# 7. Check for kebab-case filenames
check_filename_format() {
  info "Checking markdown filename format (kebab-case)..."
  
  # Use library function if available
  if [[ "$USING_LIB" == true ]] && type find_non_kebab_case_files &>/dev/null; then
    # Find non-kebab-case files using the library function
    local non_kebab_files=$(find_non_kebab_case_files "${PROJECT_ROOT}")
    
    if [ -n "$non_kebab_files" ]; then
      echo "$non_kebab_files" | while read -r file; do
        warning "Non-kebab-case filename: $file"
        WARNINGS=$((WARNINGS + 1))
      done
    fi
  else
    # Find all markdown files
    local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
    
    for file in "${md_files[@]}"; do
      # Get just the filename without path
      local filename=$(basename "$file")
      
      # Skip README.md and CHANGELOG.md (conventional uppercase names)
      if [[ "$filename" == "README.md" || "$filename" == "CHANGELOG.md" ]]; then
        continue
      fi
      
      # Check if filename is kebab-case
      if [[ ! "$filename" =~ ^[a-z0-9]+(-[a-z0-9]+)*\.md$ ]]; then
        warning "Non-kebab-case filename: $file"
        WARNINGS=$((WARNINGS + 1))
      fi
    done
  fi
}

# 8. Check for header conventions
check_header_conventions() {
  info "Checking header conventions..."
  
  # Generate a header report if the library is available
  if [[ "$USING_LIB" == true ]] && type generate_header_report &>/dev/null; then
    local report_file="${REPORT_DIR}/header-standards-report.md"
    
    # Generate header report and get violation count
    if generate_header_report "$report_file"; then
      info "Header report generated at $report_file (no violations found)"
    else
      warning "Header report generated at $report_file (violations found)"
      # Violations are already counted by the library function
    fi
    
    # Return early as library function already did the checks
    return
  fi
  
  # Fall back to original implementation
  local md_files=($(find . -type f -name "*.md" -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*"))
  
  for file in "${md_files[@]}"; do
    # Skip files without content
    if [[ ! -s "$file" ]]; then
      continue
    fi
    
    # Check for only one level 1 header
    local level1_count=$(grep -c "^# " "$file")
    if [[ "$level1_count" -eq 0 ]]; then
      warning "No level 1 header in $file"
      WARNINGS=$((WARNINGS + 1))
    elif [[ "$level1_count" -gt 1 ]]; then
      warning "Multiple level 1 headers in $file"
      WARNINGS=$((WARNINGS + 1))
    fi
    
    # Check for header hierarchy (no skipping levels)
    if grep -q "^#### " "$file" && ! grep -q "^### " "$file"; then
      warning "Skipped header level in $file (h4 without h3)"
      WARNINGS=$((WARNINGS + 1))
    fi
  done
}

# 9. Generate report of key documentation files
generate_doc_report() {
  info "Generating documentation report..."
  
  local report_file="docs/documentation-integrity-report.md"
  
  # Create report file
  cat > "$report_file" << EOF
<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Documentation Integrity Report

Generated on: $(date "+%Y-%m-%d %H:%M:%S")

This report provides an overview of the documentation structure and identifies any integrity issues.

## Documentation Structure

### Key Documentation Files

| Category | Files |
|----------|-------|
EOF
  
  # Add root documentation
  echo "| Root | $(find . -maxdepth 1 -type f -name "*.md" | sort | tr '\n' ', ' | sed 's/,$/\n/') |" >> "$report_file"
  
  # Add documentation by directory
  for dir in $(find docs -type d | sort); do
    if [[ "$dir" != "docs" ]]; then
      local files=$(find "$dir" -maxdepth 1 -type f -name "*.md" | sort | tr '\n' ', ' | sed 's/,$//')
      if [[ -n "$files" ]]; then
        echo "| ${dir#docs/} | $files |" >> "$report_file"
      fi
    fi
  done
  
  # Add integrity summary
  cat >> "$report_file" << EOF

## Integrity Check Results

* **Errors Found**: $ERRORS
* **Warnings Found**: $WARNINGS

### Specific Issues

The following issues were identified during the integrity check:

EOF
  
  # Add placeholder for manual editing of specific issues
  echo "*(Issues will be listed here after running the integrity check script)*" >> "$report_file"
  
  success "Documentation report generated at $report_file"
}

# Function to fix common markdown issues
fix_common_issues() {
  info "Attempting to fix common documentation issues..."
  
  # Use library function if available
  if [[ "$USING_LIB" == true ]] && type fix_common_issues &>/dev/null; then
    # Use library function for fixing common issues
    if fix_common_issues "${PROJECT_ROOT}/docs" "all"; then
      success "Fixed common documentation issues using library function"
    else
      warning "Some issues could not be fixed automatically"
    fi
    
    return
  fi
  
  # Fall back to original implementation
  # 1. Replace old package references in markdown files
  find docs -type f -name "*.md" -exec sed -i 's/org\.s8r/org.s8r/g' {} \;
  find docs -type f -name "*.md" -exec sed -i 's/org\.tube/org.s8r.tube.legacy/g' {} \;
  success "Updated package references in markdown files"
  
  # 2. Ensure all README.md files have title
  find docs -type f -name "README.md" | while read -r file; do
    if ! grep -q "^# " "$file"; then
      # Extract directory name for title
      local dir_name=$(basename "$(dirname "$file")")
      dir_name=$(echo "$dir_name" | sed -E 's/(^|-)([a-z])/\1\u\2/g') # Capitalize words
      
      # Add title at the beginning of the file
      sed -i "1i# $dir_name Documentation" "$file"
      sed -i "2i\n" "$file"
      success "Added title to $file"
    fi
  done
  
  # 3. Add standard sections to documentation README files
  find docs -type f -name "README.md" | while read -r file; do
    local missing_purpose=$(grep -c "^## Purpose" "$file" || echo "0")
    local missing_contents=$(grep -c "^## Contents" "$file" || echo "0")
    local missing_related=$(grep -c "^## Related" "$file" || echo "0")
    
    if [[ "$missing_purpose" -eq 0 || "$missing_contents" -eq 0 || "$missing_related" -eq 0 ]]; then
      # Add template sections at the end of the file
      
      if [[ "$missing_purpose" -eq 0 ]]; then
        echo -e "\n## Purpose\n\nThis directory contains documentation for...\n" >> "$file"
      fi
      
      if [[ "$missing_contents" -eq 0 ]]; then
        echo -e "\n## Contents\n\n- ...\n" >> "$file"
      fi
      
      if [[ "$missing_related" -eq 0 ]]; then
        echo -e "\n## Related\n\n- ...\n" >> "$file"
      fi
      
      success "Added missing sections to $file"
    fi
  done
  
  # 4. Add copyright headers to files missing them
  find docs -type f -name "*.md" | while read -r file; do
    if ! grep -q "Copyright (c)" "$file"; then
      # Add standard copyright header
      sed -i "1i<!-- \nCopyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.\n-->\n" "$file"
      success "Added copyright header to $file"
    fi
  done
  
  # 5. Fix kebab-case filenames
  find docs -type f -name "*.md" -not -name "README.md" -not -name "CHANGELOG.md" | while read -r file; do
    local filename=$(basename "$file")
    local dirname=$(dirname "$file")
    
    # Skip if already kebab-case
    if [[ "$filename" =~ ^[a-z0-9]+(-[a-z0-9]+)*\.md$ ]]; then
      continue
    fi
    
    # Convert to kebab-case
    local kebab_name=$(echo "$filename" | sed -E 's/([A-Z])/-\L\1/g' | sed 's/^-//' | sed 's/_/-/g' | tr ' ' '-' | tr -s '-' | tr '[:upper:]' '[:lower:]')
    
    # Only rename if the name would actually change
    if [[ "$filename" != "$kebab_name" ]]; then
      mv "$file" "$dirname/$kebab_name"
      success "Renamed $filename to $kebab_name"
    fi
  done
  
  success "Fixed common issues where possible"
}

# Run checks
check_internal_links
check_section_references
check_readme_structure
check_package_references
check_code_examples
check_header_format
check_filename_format
check_header_conventions
generate_doc_report

# Print summary
info "Documentation integrity check complete."
info "-------------------------------------"
info "Summary:"
info "  - Errors: $ERRORS"
info "  - Warnings: $WARNINGS"

# Offer to fix common issues
if [[ $ERRORS -gt 0 || $WARNINGS -gt 0 ]]; then
  read -p "Would you like to attempt to fix common issues automatically? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    fix_common_issues
  fi
fi

# Exit with error if there are errors
if [[ $ERRORS -gt 0 ]]; then
  exit 1
fi

exit 0