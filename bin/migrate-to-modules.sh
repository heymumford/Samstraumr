#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Script to migrate from Samstraumr directory to modules directory
# This implements the directory structure standardization
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
SOURCE_DIR="$PROJECT_ROOT/Samstraumr"
TARGET_DIR="$PROJECT_ROOT/modules"

print_section "Module Directory Migration"
print_message "Project root: $PROJECT_ROOT"
print_message "Source directory: $SOURCE_DIR"
print_message "Target directory: $TARGET_DIR"

# Validation checks
if [[ ! -d "$SOURCE_DIR" ]]; then
  print_error "Source directory $SOURCE_DIR does not exist."
  exit 1
fi

if [[ -d "$TARGET_DIR" ]]; then
  print_warning "Target directory $TARGET_DIR already exists."
  read -p "Do you want to continue? This will merge contents. (y/n): " -n 1 -r
  echo
  if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_message "Migration aborted by user."
    exit 0
  fi
fi

# Step 1: Create target directory if it doesn't exist
print_message "Creating target directory if it doesn't exist..."
mkdir -p "$TARGET_DIR"

# Step 2: Copy all files from source to target
print_message "Copying files from $SOURCE_DIR to $TARGET_DIR..."
rsync -av "$SOURCE_DIR/" "$TARGET_DIR/"

# Step 3: Update the root pom.xml to point to modules instead of Samstraumr
print_message "Updating root pom.xml..."
if [[ -f "$PROJECT_ROOT/pom.xml" ]]; then
  sed -i 's|<module>Samstraumr</module>|<module>modules</module>|g' "$PROJECT_ROOT/pom.xml"
  print_success "Updated module reference in root pom.xml"
else
  print_error "Root pom.xml not found."
  exit 1
fi

# Step 4: Update paths in other configuration files
print_message "Updating paths in other configuration files..."

# Update .s8r config references
if [[ -f "$PROJECT_ROOT/.s8r/config.json" ]]; then
  sed -i 's|"modules/|"modules/|g' "$PROJECT_ROOT/.s8r/config.json"
  print_success "Updated paths in .s8r/config.json"
fi

# Update module POM parent paths
if [[ -f "$TARGET_DIR/pom.xml" ]]; then
  sed -i 's|<relativePath>..</relativePath>|<relativePath>..</relativePath>|g' "$TARGET_DIR/pom.xml"
  print_success "Updated parent relativePath in modules/pom.xml"
fi

# Update submodule relative paths if necessary
if [[ -f "$TARGET_DIR/samstraumr-core/pom.xml" ]]; then
  # No changes needed as the relative path should be the same
  print_success "No changes needed for samstraumr-core/pom.xml"
fi

# Step 5: Update scripts and references
print_message "Updating script references..."

# Find all shell scripts that reference modules/ path
SCRIPTS=$(grep -r -l "modules/" "$PROJECT_ROOT" --include="*.sh" | grep -v "$0")

for script in $SCRIPTS; do
  print_message "Updating script: $script"
  
  # Create a backup
  cp "$script" "${script}.bak"
  
  # Update modules/ references to modules/
  sed -i 's|modules/|modules/|g' "$script"
  
  print_success "Updated: $script"
done

# Step 6: Test the build
print_message "Testing the build..."
cd "$PROJECT_ROOT"
if mvn clean test-compile -DskipTests; then
  print_success "Build test succeeded"
else
  print_error "Build test failed. Some references might need manual updating."
fi

# Step 7: Don't remove the source directory yet, wait for verification
print_warning "Migration completed, but the source directory has not been removed."
print_warning "After verifying that everything works correctly, you can remove the source directory with:"
print_warning "rm -rf \"$SOURCE_DIR\""

print_section "Migration Summary"
print_success "Directory structure migration completed!"
print_message "The following changes were made:"
echo "  - Created new 'modules' directory with content from 'Samstraumr'"
echo "  - Updated references in pom.xml"
echo "  - Updated paths in configuration files"
echo "  - Updated script references"
echo ""
print_warning "Next steps:"
echo "  1. Test your application thoroughly"
echo "  2. Verify that all builds and tests work correctly"
echo "  3. If everything works, remove the 'Samstraumr' directory"
echo "  4. Commit the changes"