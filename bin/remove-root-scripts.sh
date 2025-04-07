#!/bin/bash
# 
# Samstraumr root scripts removal tool
# This script safely removes scripts from the root directory after they've been moved to bin/
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

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"

print_message "Samstraumr Root Scripts Removal Tool"
print_message "Project root: $PROJECT_ROOT"
print_message "Bin directory: $BIN_DIR"

# Check if bin directory is in PATH
if ! echo "$PATH" | tr ':' '\n' | grep -q "$BIN_DIR"; then
  print_warning "⚠️  WARNING: $BIN_DIR is not in your PATH!"
  print_warning "This means you won't be able to run Samstraumr scripts directly."
  print_warning "Run bin/setup-path.sh first to set up your PATH."
  
  if ! ask_yes_no "Continue anyway?"; then
    print_message "Exiting without making changes."
    exit 0
  fi
fi

# Find all s8r* scripts in the root directory
print_message "Finding s8r* scripts in the root directory..."
ROOT_SCRIPTS=$(find "$PROJECT_ROOT" -maxdepth 1 -name "s8r*" -type f -o -name "s8r*" -type l | sort)

if [ -z "$ROOT_SCRIPTS" ]; then
  print_message "No s8r* scripts found in the root directory."
  exit 0
fi

# Display scripts to be removed
print_message "The following scripts will be removed from the root directory:"
echo "$ROOT_SCRIPTS" | while read -r script; do
  echo "  - $(basename "$script")"
done

# Ask for confirmation
read -p "Are you sure you want to remove these scripts? (y/N) " confirm
if [[ "$confirm" != [yY]* ]]; then
  print_message "Operation cancelled. No changes were made."
  exit 0
fi

# Remove the scripts
print_message "Removing scripts..."
echo "$ROOT_SCRIPTS" | while read -r script; do
  # Check if corresponding script exists in bin directory structure
  script_name=$(basename "$script")
  
  # Look for the script in all subdirectories
  bin_script=$(find "$BIN_DIR" -name "$script_name" | head -1)
  
  if [ -z "$bin_script" ]; then
    print_warning "⚠️  WARNING: $script_name not found in bin directory structure!"
    print_warning "This script might be lost if you remove it."
    
    read -p "Remove $script_name anyway? (y/N) " confirm
    if [[ "$confirm" != [yY]* ]]; then
      print_message "Skipping $script_name."
      continue
    fi
  fi
  
  # Remove the script
  rm -v "$script"
  print_success "Removed $script"
done

print_success "Root scripts removal completed!"
print_message "You can now use the bin directory executables instead."
print_message "Make sure to update any documentation, CI/CD pipelines, or personal workflows."