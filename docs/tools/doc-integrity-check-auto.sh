#!/usr/bin/env bash
# doc-integrity-check-auto.sh - Non-interactive version of doc-integrity-check.sh for CI/CD
# 
# This script performs the same checks as doc-integrity-check.sh but automatically
# fixes common issues without prompting and is suitable for automation pipelines.

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Find repository root
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Functions for prettier output
info() { echo -e "${BLUE}$1${NC}"; }
success() { echo -e "${GREEN}$1${NC}"; }
error() { echo -e "${RED}Error: $1${NC}" >&2; }
warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }

info "Starting automated documentation integrity check..."

# Source the functions from the main script
source "$PROJECT_ROOT/docs/tools/doc-integrity-check.sh" --source-only

if [[ "$1" != "--source-only" ]]; then
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

  # Automatically fix common issues for CI/CD
  if [[ $ERRORS -gt 0 || $WARNINGS -gt 0 ]]; then
    info "Auto-fixing common issues..."
    fix_common_issues
  fi

  # Exit with error if there are errors
  if [[ $ERRORS -gt 0 ]]; then
    exit 1
  fi

  exit 0
fi