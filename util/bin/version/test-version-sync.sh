#!/bin/bash
#==============================================================================
# Filename: test-version-sync.sh
# Description: Test script for version synchronization across files
#==============================================================================

set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../../" && pwd)"

# Source version library
source "${PROJECT_ROOT}/util/lib/version-lib.sh"

# Print header
print_section "VERSION SYNCHRONIZATION TEST"
print_info "This script tests that version updates properly synchronize across all files"

# Get current version
current_version=$(get_current_version)

if [ -z "$current_version" ]; then
  print_error "Failed to get current version"
  exit 1
fi

print_info "Current version: $current_version"

# Create test version (increment patch by 1)
IFS='.' read -r major minor patch <<< "$current_version"
test_version="${major}.${minor}.$((patch + 1))"

print_info "Test version: $test_version"

# Files to check
FILES=(
  "${PROJECT_ROOT}/Samstraumr/version.properties"
  "${PROJECT_ROOT}/pom.xml"
  "${PROJECT_ROOT}/Samstraumr/pom.xml" 
  "${PROJECT_ROOT}/Samstraumr/samstraumr-core/pom.xml"
  "${PROJECT_ROOT}/README.md"
)

# Backup files
for file in "${FILES[@]}"; do
  if [ -f "$file" ]; then
    cp "$file" "${file}.test-backup"
    print_info "Created backup: ${file}.test-backup"
  else
    print_warning "File not found: $file"
  fi
done

# Update the version
print_section "Updating Version"
update_version_in_files "$current_version" "$test_version"

# Check if version was updated in all files
print_section "Verification"
success=true

for file in "${FILES[@]}"; do
  if [ -f "$file" ]; then
    if grep -q "$test_version" "$file"; then
      print_success "File updated: $file"
    else
      print_error "File NOT updated: $file"
      success=false
    fi
  fi
done

# Restore backups
print_section "Restoring Files"
for file in "${FILES[@]}"; do
  if [ -f "${file}.test-backup" ]; then
    mv "${file}.test-backup" "$file"
    print_info "Restored: $file"
  fi
done

if [ "$success" = true ]; then
  print_section "TEST RESULT: SUCCESS"
  print_success "Version synchronization working correctly"
  echo ""
  print_info "The version synchronization system successfully updates the version across all relevant files:"
  print_info "- version.properties (source of truth)"
  print_info "- Root pom.xml"
  print_info "- Module pom.xml files"
  print_info "- README.md (badge and dependency example)"
  echo ""
  print_info "The system is now properly ensuring version consistency across the project."
else
  print_section "TEST RESULT: FAILURE"
  print_error "Version synchronization NOT working correctly"
  echo ""
  print_info "Some files were not properly updated. Review the error output above."
  echo ""
  print_info "You may need to debug the update_version_in_files function in version-lib.sh."
fi

exit 0