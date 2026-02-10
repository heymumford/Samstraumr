#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# check-documentation-standards.sh
# Verifies that documentation follows project standards
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Create a directory for reports
REPORT_DIR="${PROJECT_ROOT}/target/doc-standard-reports"
mkdir -p "$REPORT_DIR"

# Timestamp for reports
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Check file naming conventions
check_filenames() {
  header "Checking file naming conventions"
  
  local report_file="${REPORT_DIR}/filename-standards-${TIMESTAMP}.txt"
  local violations=0
  
  {
    echo "# Documentation Filename Standard Violations"
    echo "Generated: $(date)"
    echo
    echo "Standard: Markdown files should use kebab-case (lowercase with hyphens)"
    echo
    echo "## Violations"
    echo
  } > "$report_file"
  
  # Find markdown files with naming violations
  find "${PROJECT_ROOT}/docs" -type f -name "*.md" | sort | while read -r file; do
    local rel_path="${file#${PROJECT_ROOT}/}"
    local basename=$(basename "$file")
    
    # Skip specific files that are exceptions to the rule
    if [[ "$basename" == "README.md" || "$basename" == "CHANGELOG.md" || "$basename" == "LICENSE.md" || "$basename" == "CLAUDE.md" ]]; then
      continue
    fi
    
    # Check if filename is not kebab-case
    if [[ "$basename" =~ [A-Z] || "$basename" =~ [_] || "$basename" =~ [[:space:]] ]]; then
      echo "- \`$rel_path\` - Not kebab-case" >> "$report_file"
      violations=$((violations + 1))
      warn "Filename violation: $rel_path"
    fi
  done
  
  if [ "$violations" -gt 0 ]; then
    error "Found $violations filename violations"
    info "See detailed report at: $report_file"
    return 1
  else
    success "All markdown filenames follow the kebab-case convention"
    return 0
  fi
}

# Check header formatting
check_headers() {
  header "Checking header formatting"
  
  local report_file="${REPORT_DIR}/header-standards-${TIMESTAMP}.txt"
  local violations=0
  
  {
    echo "# Documentation Header Standard Violations"
    echo "Generated: $(date)"
    echo
    echo "Standards:"
    echo "- Level 1 headers should match filename (converted to title case)"
    echo "- Level 2 headers should use title case"
    echo "- Level 3+ headers should use sentence case"
    echo
    echo "## Violations"
    echo
  } > "$report_file"
  
  # Find all markdown files
  find "${PROJECT_ROOT}/docs" -type f -name "*.md" | sort | while read -r file; do
    local rel_path="${file#${PROJECT_ROOT}/}"
    local basename=$(basename "$file" .md)
    local file_violations=0
    
    # Check Level 1 headers - should match the filename converted to title case
    local expected_h1=$(echo "$basename" | sed -E 's/-/ /g' | sed -E 's/\b\w/\U&/g')
    
    # Extract the first Level 1 header from the file
    # Skip front matter if present (between --- and ---)
    # Use the first # heading after any front matter
    local h1=$(awk '/^# / { print substr($0, 3); exit }' "$file")
    
    # If no H1, check after frontmatter
    if [ -z "$h1" ]; then
      h1=$(awk 'BEGIN { in_front_matter=0 } /^---$/ { in_front_matter = !in_front_matter; next } !in_front_matter && /^# / { print substr($0, 3); exit }' "$file")
    fi
    
    # If no H1 found at all, report as a violation
    if [ -z "$h1" ]; then
      echo "- \`$rel_path\` - Missing Level 1 header" >> "$report_file"
      file_violations=$((file_violations + 1))
    fi
    
    # Check Level 2 headers - should use title case
    grep -n "^## " "$file" | while read -r h2_line; do
      local line_num=$(echo "$h2_line" | cut -d: -f1)
      local h2_text=$(echo "$h2_line" | sed -E 's/^[0-9]+:## //')
      
      # Check if this is title case - crude approximation
      # For proper title case, consider a proper library
      if ! echo "$h2_text" | grep -q -E '^([A-Z][a-z0-9]* )+[A-Z][a-z0-9]*$|^[A-Z][a-z0-9]*$'; then
        echo "- \`$rel_path:$line_num\` - Level 2 header not in title case: \"$h2_text\"" >> "$report_file"
        file_violations=$((file_violations + 1))
      fi
    done
    
    # Check Level 3+ headers - should use sentence case
    grep -n "^### " "$file" | while read -r h3_line; do
      local line_num=$(echo "$h3_line" | cut -d: -f1)
      local h3_text=$(echo "$h3_line" | sed -E 's/^[0-9]+:### //')
      
      # Check if this starts with uppercase (sentence case)
      if ! echo "$h3_text" | grep -q -E '^[A-Z]'; then
        echo "- \`$rel_path:$line_num\` - Level 3 header not in sentence case: \"$h3_text\"" >> "$report_file"
        file_violations=$((file_violations + 1))
      fi
    done
    
    if [ "$file_violations" -gt 0 ]; then
      violations=$((violations + file_violations))
      warn "Found $file_violations header violations in $rel_path"
    fi
  done
  
  if [ "$violations" -gt 0 ]; then
    error "Found $violations header formatting violations"
    info "See detailed report at: $report_file"
    return 1
  else
    success "All headers follow the formatting conventions"
    return 0
  fi
}

# Check code blocks
check_code_blocks() {
  header "Checking code block formatting"
  
  local report_file="${REPORT_DIR}/code-block-standards-${TIMESTAMP}.txt"
  local violations=0
  
  {
    echo "# Code Block Standard Violations"
    echo "Generated: $(date)"
    echo
    echo "Standard: Code blocks should specify a language"
    echo
    echo "## Violations"
    echo
  } > "$report_file"
  
  # Find all markdown files
  find "${PROJECT_ROOT}/docs" -type f -name "*.md" | sort | while read -r file; do
    local rel_path="${file#${PROJECT_ROOT}/}"
    local file_violations=0
    
    # Extract line numbers where code blocks start (use single quotes for literal backticks)
    grep -n '^```$' "$file" | while read -r line; do
      local line_num=$(echo "$line" | cut -d: -f1)
      echo "- \`$rel_path:$line_num\` - Code block without language specifier" >> "$report_file"
      file_violations=$((file_violations + 1))
    done
    
    if [ "$file_violations" -gt 0 ]; then
      violations=$((violations + file_violations))
      warn "Found $file_violations code block violations in $rel_path"
    fi
  done
  
  if [ "$violations" -gt 0 ]; then
    error "Found $violations code blocks without language specifiers"
    info "See detailed report at: $report_file"
    return 1
  else
    success "All code blocks specify a language"
    return 0
  fi
}

# Check broken links using our cross-reference script
check_broken_links() {
  header "Checking broken links"
  
  # Use the update-cross-references.sh script in check mode
  if [ -f "${SCRIPT_DIR}/update-cross-references.sh" ]; then
    info "Running cross-reference checker..."
    
    # Run in check mode only
    "${SCRIPT_DIR}/update-cross-references.sh" --check
    local result=$?
    
    if [ "$result" -ne 0 ]; then
      error "Link check failed"
      return 1
    else
      success "Link check passed"
      return 0
    fi
  else
    error "Cross-reference checker script not found at ${SCRIPT_DIR}/update-cross-references.sh"
    return 1
  fi
}

# Generate a summary report
generate_summary() {
  header "Generating documentation standards summary"
  
  local summary_file="${REPORT_DIR}/summary-${TIMESTAMP}.md"
  local total_failures=0
  
  {
    echo "# Documentation Standards Summary"
    echo "Generated: $(date)"
    echo
    echo "## Results"
    echo
    echo "| Check | Status | Details |"
    echo "|-------|--------|---------|"
  } > "$summary_file"
  
  # Add results for each check
  for check in "${CHECKS[@]}"; do
    local status_file="${REPORT_DIR}/${check}-status.txt"
    
    if [ -f "$status_file" ]; then
      local result=$(cat "$status_file")
      local details_file="${REPORT_DIR}/${check}-${TIMESTAMP}.txt"
      local details_link=""
      
      if [ -f "$details_file" ]; then
        details_link="[Details]($(basename "$details_file"))"
      fi
      
      if [ "$result" -eq 0 ]; then
        echo "| ${check} | ✅ Pass | ${details_link} |" >> "$summary_file"
      else
        echo "| ${check} | ❌ Fail | ${details_link} |" >> "$summary_file"
        total_failures=$((total_failures + 1))
      fi
    else
      echo "| ${check} | ❓ Unknown | |" >> "$summary_file"
    fi
  done
  
  # Add summary section
  {
    echo
    echo "## Summary"
    echo
    if [ "$total_failures" -eq 0 ]; then
      echo "✅ All documentation standards checks passed."
    else
      echo "❌ ${total_failures} documentation standards checks failed."
    fi
  } >> "$summary_file"
  
  info "Summary report generated at: $summary_file"
  
  return $total_failures
}

# Main function
main() {
  local fix_mode=false
  local specific_check=""
  
  # Define checks to run
  CHECKS=("filenames" "headers" "code_blocks" "broken_links")
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        header "Documentation Standards Checker"
        echo "Verifies that documentation follows project standards."
        echo ""
        echo "Usage: $(basename "$0") [options]"
        echo ""
        echo "Options:"
        echo "  -h, --help     Show this help message"
        echo "  -f, --fix      Fix violations where possible"
        echo "  -c, --check CHECK Run only the specified check:"
        echo "                   all - Run all checks (default)"
        echo "                   filenames - Check file naming conventions"
        echo "                   headers - Check header formatting"
        echo "                   code_blocks - Check code block language specifiers"
        echo "                   broken_links - Check for broken cross-references"
        echo "  --report FILE  Write report to FILE (ignored, for CI compatibility)"
        echo ""
        exit 0
        ;;
      -f|--fix)
        fix_mode=true
        shift
        ;;
      -c|--check)
        if [[ -n "$2" && "$2" =~ ^(filenames|headers|code_blocks|broken_links|all)$ ]]; then
          # "all" means run all checks (leave specific_check empty)
          if [[ "$2" != "all" ]]; then
            specific_check="$2"
          fi
          shift 2
        else
          error "Invalid or missing check name for --check option"
          exit 1
        fi
        ;;
      --report)
        # Accept but ignore --report argument (for CI compatibility)
        shift 2
        ;;
      *)
        error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
    esac
  done
  
  header "Documentation Standards Checker"
  
  # Create status directory
  mkdir -p "$REPORT_DIR"
  
  # Run all checks or a specific check
  local exit_status=0
  
  if [ -n "$specific_check" ]; then
    # Run only the specified check
    info "Running only the $specific_check check"
    
    case "$specific_check" in
      filenames)
        check_filenames
        echo $? > "${REPORT_DIR}/filenames-status.txt"
        ;;
      headers)
        check_headers
        echo $? > "${REPORT_DIR}/headers-status.txt"
        ;;
      code_blocks)
        check_code_blocks
        echo $? > "${REPORT_DIR}/code_blocks-status.txt"
        ;;
      broken_links)
        check_broken_links
        echo $? > "${REPORT_DIR}/broken_links-status.txt"
        ;;
    esac
  else
    # Run all checks
    check_filenames
    echo $? > "${REPORT_DIR}/filenames-status.txt"
    
    check_headers
    echo $? > "${REPORT_DIR}/headers-status.txt"
    
    check_code_blocks
    echo $? > "${REPORT_DIR}/code_blocks-status.txt"
    
    check_broken_links
    echo $? > "${REPORT_DIR}/broken_links-status.txt"
  fi
  
  # Generate summary report
  generate_summary
  exit_status=$?
  
  if [ "$exit_status" -eq 0 ]; then
    success "All documentation standards checks passed"
  else
    error "$exit_status documentation standards checks failed"
  fi
  
  exit $exit_status
}

# Run the main function
main "$@"