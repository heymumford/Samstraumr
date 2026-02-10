#!/bin/bash
# 
# Samstraumr root directory cleanup script
# This script moves files from the root directory to their appropriate locations
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

# Move file to destination, ensure destination directory exists
move_file() {
  local src="$1"
  local dest="$2"
  local dest_dir=$(dirname "$dest")
  
  if [[ ! -f "$src" ]]; then
    print_warning "Source file not found: $src (skipping)"
    return 0
  fi
  
  mkdir -p "$dest_dir"
  
  if [[ -f "$dest" ]]; then
    print_warning "Destination file already exists: $dest"
    if [[ ! -f "$dest.bak" ]]; then
      cp -v "$dest" "$dest.bak"
      print_message "Created backup: $dest.bak"
    fi
  fi
  
  mv -v "$src" "$dest"
  print_success "Moved $src to $dest"
  return 0
}

PROJECT_ROOT=$(find_project_root)
DOCS_DIR="$PROJECT_ROOT/docs"
REF_DIR="$DOCS_DIR/reference"
MIGRATION_DIR="$DOCS_DIR/guides/migration"
PLANNING_DIR="$DOCS_DIR/planning"
CONTRIB_DIR="$DOCS_DIR/contrib"
VERSION_DIR="$REF_DIR/release"

print_section "Root Directory Cleanup"
print_message "Project root: $PROJECT_ROOT"

# Folder management documents
print_message "Moving folder management documents..."
move_file "$PROJECT_ROOT/FOLDERS.md" "$CONTRIB_DIR/folder-organization.md"
move_file "$PROJECT_ROOT/FOLDER_README_TEMPLATE.md" "$CONTRIB_DIR/folder-readme-template.md"

# Documentation files
print_message "Moving documentation files..."
# If CHANGELOG.md exists, move it
if [[ -f "$PROJECT_ROOT/CHANGELOG.md" ]]; then
  move_file "$PROJECT_ROOT/CHANGELOG.md" "$VERSION_DIR/changelog.md"
fi

# Release notes
if [[ -f "$PROJECT_ROOT/RELEASE-2.2.0.md" ]]; then
  move_file "$PROJECT_ROOT/RELEASE-2.2.0.md" "$VERSION_DIR/release-2.2.0.md"
fi

# Version management
if [[ -f "$PROJECT_ROOT/VERSION_MANAGEMENT.md" ]]; then
  move_file "$PROJECT_ROOT/VERSION_MANAGEMENT.md" "$REF_DIR/version-management.md"
fi

# Migration plans
if [[ -f "$PROJECT_ROOT/classpath-migration-plan.md" ]]; then
  move_file "$PROJECT_ROOT/classpath-migration-plan.md" "$MIGRATION_DIR/classpath-migration-plan.md"
fi

# Cleanup plans
if [[ -f "$PROJECT_ROOT/cleanup-plan.md" ]]; then
  move_file "$PROJECT_ROOT/cleanup-plan.md" "$PLANNING_DIR/archive/cleanup-plan.md"
fi

# Package files
if [[ -f "$PROJECT_ROOT/package-flattening-fixes.md" ]]; then
  move_file "$PROJECT_ROOT/package-flattening-fixes.md" "$PLANNING_DIR/archive/package-flattening-fixes.md"
fi

if [[ -f "$PROJECT_ROOT/package-mapping.md" ]]; then
  move_file "$PROJECT_ROOT/package-mapping.md" "$MIGRATION_DIR/package-mapping.md"
fi

# Architecture report
if [[ -f "$PROJECT_ROOT/architecture-report.md" ]]; then
  move_file "$PROJECT_ROOT/architecture-report.md" "$DOCS_DIR/architecture/reports/architecture-report.md"
fi

# Miscellaneous files
if [[ -f "$PROJECT_ROOT/moreTubeTests.md" ]]; then
  move_file "$PROJECT_ROOT/moreTubeTests.md" "$DOCS_DIR/testing/tube-testing/more-tube-tests.md"
fi

if [[ -f "$PROJECT_ROOT/tag-standardization-changes.log" ]]; then
  move_file "$PROJECT_ROOT/tag-standardization-changes.log" "$DOCS_DIR/testing/logs/tag-standardization-changes.log"
fi

# Create necessary directories if they don't exist
print_message "Creating necessary directories..."
mkdir -p "$DOCS_DIR/architecture/reports"
mkdir -p "$DOCS_DIR/testing/tube-testing"
mkdir -p "$DOCS_DIR/testing/logs"
mkdir -p "$PLANNING_DIR/archive"
mkdir -p "$MIGRATION_DIR"
mkdir -p "$VERSION_DIR"

# Safe files that should remain in the root
ROOT_SAFE_FILES=(
  "$PROJECT_ROOT/README.md"
  "$PROJECT_ROOT/LICENSE"
  "$PROJECT_ROOT/pom.xml"
  "$PROJECT_ROOT/.gitignore"
)

# Check for remaining non-directory, non-safe files
print_message "Checking for remaining files in root directory..."
REMAINING_FILES=$(find "$PROJECT_ROOT" -maxdepth 1 -type f \
  ! -path "$PROJECT_ROOT/README.md" \
  ! -path "$PROJECT_ROOT/LICENSE" \
  ! -path "$PROJECT_ROOT/pom.xml" \
  ! -path "$PROJECT_ROOT/.gitignore" \
  ! -path "$PROJECT_ROOT/.git/*" \
  ! -path "$PROJECT_ROOT/index.html" \
  ! -path "$PROJECT_ROOT/robots.txt" \
  | sort)

if [[ -n "$REMAINING_FILES" ]]; then
  print_warning "The following files remain in the root directory:"
  echo "$REMAINING_FILES" | while read -r file; do
    echo "  - $(basename "$file")"
  done
  
  print_message "Consider moving these files to appropriate locations or adding them to the script."
else
  print_success "No additional files remain in the root directory!"
fi

print_section "Cleanup Complete"
print_success "Root directory cleanup completed successfully!"
print_message "Remember to commit these changes to the repository."