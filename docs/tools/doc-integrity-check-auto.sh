#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# doc-integrity-check-auto.sh - Non-interactive version of doc-integrity-check.sh for CI/CD
# 
# This script performs the same checks as doc-integrity-check.sh but automatically
# fixes common issues without prompting and is suitable for automation pipelines.
#==============================================================================

set -e

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the doc-lib shared library
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
else
  echo "Error: doc-lib.sh not found. Falling back to legacy method."
  # Source the functions from the main script as fallback
  source "$PROJECT_ROOT/docs/tools/doc-integrity-check.sh" --source-only
fi

print_info "Starting automated documentation integrity check..."

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