#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# 
# Samstraumr root directory cleanup script for directories
# This script moves or reorganizes directories from the root directory
#

set -e

# Print message in color
print_message() {
  echo -e "\033[1;34m$1\033[0m"
}

# Print error message in color
print_error() {
  echo -e "\033[1;31m$1\033[0m"
}

# Print success message in color
print_success() {
  echo -e "\033[1;32m$1\033[0m"
}

# Print warning message in color
print_warning() {
  echo -e "\033[1;33m$1\033[0m"
}

# Print section header
print_section() {
  echo -e "\033[1;36m==== $1 ====\033[0m"
}

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
TARGET_DIR="$PROJECT_ROOT/target"
DOCS_DIR="$PROJECT_ROOT/docs"
PLANNING_DIR="$DOCS_DIR/planning/archive"

print_section "Root Directory Cleanup (Directories)"
print_message "Project root: $PROJECT_ROOT"
print_message "Target directory: $TARGET_DIR"

# Ensure target directory exists
mkdir -p "$TARGET_DIR"

# Move logs to target
if [[ -d "$PROJECT_ROOT/logs" ]]; then
  print_message "Moving logs directory to target..."
  
  # Create target logs directory if it doesn't exist
  if [[ ! -d "$TARGET_DIR/logs" ]]; then
    mkdir -p "$TARGET_DIR/logs"
  fi
  
  # Move log files
  find "$PROJECT_ROOT/logs" -type f -exec mv -v {} "$TARGET_DIR/logs/" \;
  
  # Remove the original directory if it's empty
  if [[ -z "$(ls -A "$PROJECT_ROOT/logs")" ]]; then
    rmdir "$PROJECT_ROOT/logs"
    print_success "Removed empty logs directory"
  else
    print_warning "logs directory not empty after moving files"
  fi
fi

# Move test-results to target
if [[ -d "$PROJECT_ROOT/test-results" ]]; then
  print_message "Moving test-results directory to target..."
  
  # Create target test-results directory if it doesn't exist
  if [[ ! -d "$TARGET_DIR/test-results" ]]; then
    mkdir -p "$TARGET_DIR/test-results"
  fi
  
  # Move test result files
  find "$PROJECT_ROOT/test-results" -type f -exec mv -v {} "$TARGET_DIR/test-results/" \;
  
  # Remove the original directory if it's empty
  if [[ -z "$(ls -A "$PROJECT_ROOT/test-results")" ]]; then
    rmdir "$PROJECT_ROOT/test-results"
    print_success "Removed empty test-results directory"
  else
    print_warning "test-results directory not empty after moving files"
  fi
fi

# Move test-temp to target
if [[ -d "$PROJECT_ROOT/test-temp" ]]; then
  print_message "Moving test-temp directory to target..."
  
  # Create target test-temp directory if it doesn't exist
  if [[ ! -d "$TARGET_DIR/test-temp" ]]; then
    mkdir -p "$TARGET_DIR/test-temp"
  fi
  
  # Move test temp files
  find "$PROJECT_ROOT/test-temp" -type f -exec mv -v {} "$TARGET_DIR/test-temp/" \;
  
  # Remove the original directory if it's empty
  if [[ -z "$(ls -A "$PROJECT_ROOT/test-temp")" ]]; then
    rmdir "$PROJECT_ROOT/test-temp"
    print_success "Removed empty test-temp directory"
  else
    print_warning "test-temp directory not empty after moving files"
  fi
fi

# Move reports to target (except for ones that should be preserved)
if [[ -d "$PROJECT_ROOT/reports" ]]; then
  print_message "Processing reports directory..."
  
  # Create target reports directory if it doesn't exist
  if [[ ! -d "$TARGET_DIR/reports" ]]; then
    mkdir -p "$TARGET_DIR/reports"
  fi
  
  # Create target reports/docs directory if it doesn't exist
  if [[ ! -d "$TARGET_DIR/reports/docs" ]]; then
    mkdir -p "$TARGET_DIR/reports/docs"
  fi
  
  # Move report files
  find "$PROJECT_ROOT/reports" -type f -not -path "*/docs/*" -exec mv -v {} "$TARGET_DIR/reports/" \;
  find "$PROJECT_ROOT/reports/docs" -type f -exec mv -v {} "$TARGET_DIR/reports/docs/" \;
  
  # Remove the original directory if it's empty
  if [[ -z "$(find "$PROJECT_ROOT/reports" -type f)" ]]; then
    rm -rf "$PROJECT_ROOT/reports"
    print_success "Removed empty reports directory"
  else
    print_warning "reports directory not empty after moving files"
  fi
fi

# Move duplicate-cleanup-plan to docs/planning/archive
if [[ -d "$PROJECT_ROOT/duplicate-cleanup-plan" ]]; then
  print_message "Moving duplicate-cleanup-plan to docs/planning/archive..."
  
  # Ensure target directory exists
  mkdir -p "$PLANNING_DIR"
  
  # Move the component-duplicates.md file
  if [[ -f "$PROJECT_ROOT/duplicate-cleanup-plan/component-duplicates.md" ]]; then
    mv -v "$PROJECT_ROOT/duplicate-cleanup-plan/component-duplicates.md" "$PLANNING_DIR/"
  fi
  
  # Remove the original directory if it's empty
  if [[ -z "$(ls -A "$PROJECT_ROOT/duplicate-cleanup-plan")" ]]; then
    rmdir "$PROJECT_ROOT/duplicate-cleanup-plan"
    print_success "Removed empty duplicate-cleanup-plan directory"
  else
    print_warning "duplicate-cleanup-plan directory not empty after moving files"
  fi
fi

# Update .gitignore to ensure target subdirectories are ignored
print_message "Updating .gitignore to ensure target subdirectories are ignored..."

if ! grep -q "target/logs/" "$PROJECT_ROOT/.gitignore"; then
  echo -e "\n# Additional target subdirectories" >> "$PROJECT_ROOT/.gitignore"
  echo "target/logs/" >> "$PROJECT_ROOT/.gitignore"
  echo "target/test-results/" >> "$PROJECT_ROOT/.gitignore"
  echo "target/test-temp/" >> "$PROJECT_ROOT/.gitignore"
  echo "target/reports/" >> "$PROJECT_ROOT/.gitignore"
  print_success "Updated .gitignore with additional target subdirectories"
fi

print_section "Directory Cleanup Complete"
print_success "Root directory cleanup completed successfully!"
print_message "Remember to commit these changes to the repository."