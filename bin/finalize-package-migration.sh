#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Samstraumr package migration finalization script
# This script completes the package migration from .s8r to .s8r
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
S8R_DIR="$PROJECT_ROOT/.s8r"
S8R_DIR="$PROJECT_ROOT/.s8r"

print_section "Package Migration Finalization"
print_message "Project root: $PROJECT_ROOT"

# Check if .s8r directory exists
if [[ ! -d "$S8R_DIR" ]]; then
  print_error "The .s8r directory does not exist. Cannot proceed with migration."
  exit 1
fi

# Check if .s8r directory exists
if [[ ! -d "$S8R_DIR" ]]; then
  print_warning "The .s8r directory does not exist. Nothing to migrate."
  exit 0
fi

# Copy any missing files from .s8r to .s8r
print_message "Copying missing files from .s8r to .s8r..."

# Make sure we have the required directories in .s8r
mkdir -p "$S8R_DIR/config"
mkdir -p "$S8R_DIR/lib"
mkdir -p "$S8R_DIR/user"
mkdir -p "$S8R_DIR/templates"

# Copy config files if they don't exist
if [[ -f "$S8R_DIR/config.json" && ! -f "$S8R_DIR/config.json" ]]; then
  cp "$S8R_DIR/config.json" "$S8R_DIR/config.json"
  print_success "Copied config.json"
fi

# Copy config.sh to lib/config.sh in .s8r
if [[ -f "$S8R_DIR/config.sh" && ! -f "$S8R_DIR/lib/config.sh" ]]; then
  cp "$S8R_DIR/config.sh" "$S8R_DIR/lib/config.sh"
  # Update references to .s8r to .s8r
  sed -i 's/\.s8r/\.s8r/g' "$S8R_DIR/lib/config.sh"
  print_success "Copied and updated config.sh"
fi

# Copy any template files
if [[ -d "$S8R_DIR/templates" ]]; then
  for template in "$S8R_DIR/templates"/*; do
    if [[ -f "$template" ]]; then
      template_name=$(basename "$template")
      if [[ ! -f "$S8R_DIR/templates/$template_name" ]]; then
        cp "$template" "$S8R_DIR/templates/$template_name"
        print_success "Copied template: $template_name"
      fi
    fi
  done
fi

# Update scripts that reference .s8r to use .s8r
print_message "Updating script references..."

# Find all shell scripts that reference .s8r
SCRIPTS=$(grep -r -l "\.s8r" "$PROJECT_ROOT" --include="*.sh" | grep -v "$S8R_DIR")

for script in $SCRIPTS; do
  print_message "Updating script: $script"
  
  # First, create a backup
  cp "$script" "${script}.bak"
  
  # Update .s8r references to .s8r
  sed -i 's/\.s8r/\.s8r/g' "$script"
  
  # Update variable names if they're prefixed with SAMSTRAUMR
  sed -i 's/S8R_/S8R_/g' "$script"
  
  print_success "Updated: $script"
done

# Update unified-common.sh which has special config file handling
if [[ -f "$PROJECT_ROOT/util/lib/unified-common.sh" ]]; then
  print_message "Updating unified-common.sh..."
  
  # Create a backup
  cp "$PROJECT_ROOT/util/lib/unified-common.sh" "$PROJECT_ROOT/util/lib/unified-common.sh.bak"
  
  # Update the config file reference
  sed -i 's/\.s8r\.config/\.s8r\.config/g' "$PROJECT_ROOT/util/lib/unified-common.sh"
  sed -i 's/\.s8r\/config\.sh/\.s8r\/lib\/config\.sh/g' "$PROJECT_ROOT/util/lib/unified-common.sh"
  
  print_success "Updated unified-common.sh"
fi

# Create a .s8r.config file if it doesn't exist
if [[ ! -f "$PROJECT_ROOT/.s8r.config" && -f "$PROJECT_ROOT/.s8r.config" ]]; then
  print_message "Creating .s8r.config from .s8r.config..."
  
  # Copy the file
  cp "$PROJECT_ROOT/.s8r.config" "$PROJECT_ROOT/.s8r.config"
  
  # Update variable names
  sed -i 's/S8R_/S8R_/g' "$PROJECT_ROOT/.s8r.config"
  
  print_success "Created .s8r.config"
fi

# Update finalize-root-cleanup.sh to remove .s8r from standard dirs
if [[ -f "$PROJECT_ROOT/bin/finalize-root-cleanup.sh" ]]; then
  print_message "Updating finalize-root-cleanup.sh to remove .s8r from standard dirs..."
  
  # Create a backup
  cp "$PROJECT_ROOT/bin/finalize-root-cleanup.sh" "$PROJECT_ROOT/bin/finalize-root-cleanup.sh.bak"
  
  # Update the standard dirs line
  sed -i 's/STANDARD_DIRS=".*\.s8r.*/STANDARD_DIRS=".git .github .idea .mvn .s8r Samstraumr bin docs quality-tools src target util"/g' "$PROJECT_ROOT/bin/finalize-root-cleanup.sh"
  
  print_success "Updated finalize-root-cleanup.sh"
fi

# Create a migration completion marker
echo "Migration completed on $(date)" > "$S8R_DIR/migration-complete.txt"

print_section "Migration Summary"
print_success "Package migration from .s8r to .s8r completed successfully!"
print_message "What was done:"
echo "  - Copied missing files from .s8r to .s8r"
echo "  - Updated script references from .s8r to .s8r"
echo "  - Updated variable naming conventions from S8R_ to S8R_"
echo "  - Created .s8r.config from .s8r.config"
echo "  - Updated unified-common.sh config handling"
echo ""
print_warning "Next steps:"
echo "  1. Test your configuration by running some basic commands"
echo "  2. If everything works, delete the .s8r directory:"
echo "     rm -rf \"$S8R_DIR\""
echo "  3. Remove backup files after confirming everything works:"
echo "     find \"$PROJECT_ROOT\" -name \"*.bak\" -delete"
echo ""
print_message "You can also run the \"finalize-root-cleanup.sh\" script to clean up the root directory."