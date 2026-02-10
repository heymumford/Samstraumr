#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# check-todo-format.sh - CI check for TODO format compliance
# This script validates that TODOs follow the standard format
#==============================================================================

set -e

# Find project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
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
  
  # Fallback output functions
  info() { echo -e "${BLUE}$1${NC}"; }
  success() { echo -e "${GREEN}$1${NC}"; }
  warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }
  error() { echo -e "${RED}Error: $1${NC}" >&2; }
fi

# Default settings
STRICT=false
HIGH_PRIORITY_ONLY=false
CHECK_CATEGORIES=true
CHECK_GITHUB_ISSUES=false
VERBOSE=false
EXIT_CODE=0

# Standard TODO format pattern
TODO_FORMAT_REGEX="^\s*\/\/\s*TODO\s+\[P[0-3]\](\s+\([A-Z]+\))?(\s+\(#[0-9]+\))?\s*:\s*.+$"

# Function to show usage
show_help() {
  echo "Usage: $0 [options]"
  echo
  echo "Validates that TODOs in the codebase follow the standard format."
  echo
  echo "Options:"
  echo "  -h, --help              Show this help message"
  echo "  -s, --strict            Fail on any non-compliant TODO"
  echo "  -p, --high-priority     Only check P0 and P1 TODOs"
  echo "  -n, --no-categories     Don't require categories"
  echo "  -g, --github-issues     Check that P0/P1 TODOs have GitHub issues"
  echo "  -v, --verbose           Show verbose output"
  echo
  echo "Exit codes:"
  echo "  0 - All TODOs comply with the format (or non-compliant TODOs are allowed)"
  echo "  1 - Some TODOs do not comply with the format"
  echo "  2 - Some high-priority TODOs do not have GitHub issues"
  echo
}

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      show_help
      exit 0
      ;;
    -s|--strict)
      STRICT=true
      shift
      ;;
    -p|--high-priority)
      HIGH_PRIORITY_ONLY=true
      shift
      ;;
    -n|--no-categories)
      CHECK_CATEGORIES=false
      shift
      ;;
    -g|--github-issues)
      CHECK_GITHUB_ISSUES=true
      shift
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    *)
      error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done

info "Checking TODO format compliance..."

# Create temporary files for processing
TODO_TMP=$(mktemp)
NONCOMPLIANT_TMP=$(mktemp)

# If we're checking GitHub issues, verify that gh CLI is available
if [[ "$CHECK_GITHUB_ISSUES" == true ]]; then
  if ! command -v gh &> /dev/null; then
    warning "GitHub CLI (gh) not found. Disabling GitHub issue checks."
    CHECK_GITHUB_ISSUES=false
  fi
fi

# Find all files containing "TODO" and analyze them
FILES_WITH_TODOS=$(find "$PROJECT_ROOT" -type f \
  -name "*.java" -o -name "*.sh" -o -name "*.js" -o -name "*.ts" -o -name "*.md" -o -name "*.html" -o -name "*.xml" \
  | grep -v "node_modules\|target\|build\|dist\|vendor\|.git" \
  | xargs grep -l "TODO" 2>/dev/null || echo "")

if [[ -z "$FILES_WITH_TODOS" ]]; then
  success "No files with TODOs found."
  exit 0
fi

# Counters
TOTAL_TODOS=0
COMPLIANT_TODOS=0
NONCOMPLIANT_TODOS=0
MISSING_ISSUES=0

# Loop through the files and check TODOs
for file in $FILES_WITH_TODOS; do
  if [[ "$VERBOSE" == true ]]; then
    info "Checking file: ${file#$PROJECT_ROOT/}"
  fi

  # Extract lines with TODOs
  while IFS= read -r line_info; do
    line_num=$(echo "$line_info" | cut -d: -f1)
    line_content=$(echo "$line_info" | cut -d: -f2-)
    
    # Skip standardize-todos or check-todo-format code
    if [[ "$line_content" =~ (standardize-todos|check-todo-format) ]]; then
      continue
    fi
    
    # Increment total TODOs counter
    ((TOTAL_TODOS++))
    
    # Use the library function if available, otherwise use regex matching
    local is_compliant=false
    local priority=""
    
    if [[ "$USING_LIB" == true ]] && type is_standard_todo &>/dev/null; then
      # Use library function to check if the TODO follows the standard format
      if is_standard_todo "$line_content"; then
        is_compliant=true
        # Get priority using library function
        if type get_todo_priority &>/dev/null; then
          priority=$(get_todo_priority "$line_content")
          # Remove P prefix if present
          priority="${priority#P}"
        fi
      fi
    else
      # Legacy implementation - use regex
      if [[ "$line_content" =~ $TODO_FORMAT_REGEX ]]; then
        is_compliant=true
        # Extract priority using regex
        if [[ "$line_content" =~ \[P([0-3])\] ]]; then
          priority="${BASH_REMATCH[1]}"
        fi
      fi
    fi
    
    # Process compliant TODOs
    if [[ "$is_compliant" == true ]]; then
      # TODO complies with the standard format
      ((COMPLIANT_TODOS++))
      
      # Check if high-priority TODOs have GitHub issues
      if [[ "$CHECK_GITHUB_ISSUES" == true && "$priority" =~ [01] ]]; then
        # Use library function if available to check for GitHub issues
        local has_issue=false
        local issue_num=""
        
        if [[ "$USING_LIB" == true ]] && type get_todo_description &>/dev/null; then
          # Check if the line contains a GitHub issue reference
          if [[ "$line_content" =~ \(#([0-9]+)\) ]]; then
            has_issue=true
            issue_num="${BASH_REMATCH[1]}"
          fi
        else
          # Legacy implementation
          if [[ "$line_content" =~ \(#([0-9]+)\) ]]; then
            has_issue=true
            issue_num="${BASH_REMATCH[1]}"
          fi
        fi
        
        if [[ "$has_issue" == false ]]; then
          ((MISSING_ISSUES++))
          echo "${file}:${line_num}: High-priority TODO (P${priority}) without GitHub issue: ${line_content}" >> "$NONCOMPLIANT_TMP"
        elif [[ -n "$issue_num" ]]; then
          # Verify the issue exists on GitHub (if requested)
          if [[ "$CHECK_GITHUB_ISSUES" == true ]]; then
            if ! gh issue view "$issue_num" &> /dev/null; then
              ((MISSING_ISSUES++))
              echo "${file}:${line_num}: High-priority TODO references non-existent GitHub issue #${issue_num}: ${line_content}" >> "$NONCOMPLIANT_TMP"
            fi
          fi
        fi
      fi
    else
      # TODO does not comply with the standard format
      # Only count as non-compliant if we're not only checking high-priority TODOs,
      # or if it might be a high-priority TODO without the right format
      if [[ "$HIGH_PRIORITY_ONLY" == false || "$line_content" =~ (urgent|critical|immediately|asap|crash|security|vulnerability|important|soon|bug|fix|issue) ]]; then
        ((NONCOMPLIANT_TODOS++))
        echo "${file}:${line_num}: Non-compliant TODO format: ${line_content}" >> "$NONCOMPLIANT_TMP"
      fi
    fi
  done < <(grep -n "TODO" "$file" 2>/dev/null || true)
done

# Print summary
info "TODO Format Check Results:"
echo "Total TODOs: $TOTAL_TODOS"
echo "Compliant TODOs: $COMPLIANT_TODOS"
echo "Non-compliant TODOs: $NONCOMPLIANT_TODOS"

if [[ "$CHECK_GITHUB_ISSUES" == true ]]; then
  echo "High-priority TODOs without valid GitHub issues: $MISSING_ISSUES"
fi

# Print non-compliant TODOs if any
if [[ -s "$NONCOMPLIANT_TMP" ]]; then
  echo
  echo "=== Non-compliant TODOs ==="
  cat "$NONCOMPLIANT_TMP"
  echo
  
  if [[ "$STRICT" == true || ("$HIGH_PRIORITY_ONLY" == true && "$MISSING_ISSUES" -gt 0) ]]; then
    error "TODO format check failed"
    EXIT_CODE=1
    
    # Additional error for missing GitHub issues
    if [[ "$CHECK_GITHUB_ISSUES" == true && "$MISSING_ISSUES" -gt 0 ]]; then
      error "Some high-priority TODOs are missing GitHub issues"
      EXIT_CODE=2
    fi
  else
    warning "Some TODOs do not follow the standard format."
    echo "Run ./docs/scripts/standardize-todos.sh --fix to automatically fix non-compliant TODOs."
  fi
else
  success "All TODOs follow the standard format!"
fi

# Clean up
rm -f "$TODO_TMP" "$NONCOMPLIANT_TMP"

exit $EXIT_CODE