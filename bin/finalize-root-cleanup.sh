#!/bin/bash
# 
# Samstraumr root directory final cleanup script
# This script handles the remaining directories in the root
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
BIN_DIR="$PROJECT_ROOT/bin"
UTIL_DIR="$PROJECT_ROOT/util"

print_section "Final Root Directory Cleanup"
print_message "Project root: $PROJECT_ROOT"

# Move test-scripts to bin/test-scripts
if [[ -d "$PROJECT_ROOT/test-scripts" ]]; then
  print_message "Moving test-scripts to bin/test-scripts..."
  
  # Create bin/test-scripts directory if it doesn't exist
  mkdir -p "$BIN_DIR/test-scripts"
  
  # Copy test script files
  find "$PROJECT_ROOT/test-scripts" -type f -name "*.sh" -exec cp -v {} "$BIN_DIR/test-scripts/" \;
  
  # Make them executable
  find "$BIN_DIR/test-scripts" -type f -name "*.sh" -exec chmod +x {} \;
  
  # Create README.md for test-scripts directory
  cat > "$BIN_DIR/test-scripts/README.md" << 'EOF'
# Test Scripts

This directory contains scripts used for testing specific components of the Samstraumr framework. These scripts are primarily used for development and verification purposes.

## Scripts

- `s8r-acceptance-tests.sh` - Runs acceptance tests
- `s8r-component-tests.sh` - Runs component-level tests
- `s8r-composite-tests.sh` - Runs composite-level tests
- `s8r-machine-tests.sh` - Runs machine-level tests
- `s8r-run-tests.sh` - Main test runner script
- `s8r-system-tests.sh` - Runs system-level tests
- `s8r-test-framework.sh` - Test framework utilities
- `s8r-unit-tests.sh` - Runs unit tests
- `test-cli-short.sh` - Quick CLI tests
- `test-help.sh` - Tests help system
- `test-machine-adapter.sh` - Tests machine adapter functionality
- `test-one.sh` - Runs a single test
- `verify-cli.sh` - Verifies CLI functionality

## Usage

These scripts should be used from the bin directory or via aliases set up by the `create-aliases.sh` script.

Example:
```bash
# Run component tests
./bin/test-scripts/s8r-component-tests.sh

# Run a single test
./bin/test-scripts/test-one.sh TestClass#testMethod
```
EOF
  
  # Remove the original directory
  print_warning "The test-scripts directory will be removed. Backup has been made to bin/test-scripts."
  print_warning "Press Enter to confirm, or Ctrl+C to cancel..."
  read
  
  rm -rf "$PROJECT_ROOT/test-scripts"
  print_success "Removed test-scripts directory after copying to bin/test-scripts"
fi

# Handle lib directory
if [[ -d "$PROJECT_ROOT/lib" ]]; then
  print_message "Checking lib directory..."
  
  # Check if it contains only Maven dependencies
  if find "$PROJECT_ROOT/lib" -type f -name "*.jar" | grep -q .; then
    print_warning "The lib directory contains JAR files that appear to be Maven dependencies."
    print_warning "This directory should be preserved but marked in .gitignore."
    
    # Add to .gitignore if not already there
    if ! grep -q "^/lib/" "$PROJECT_ROOT/.gitignore"; then
      echo -e "\n# Local Maven repository dependencies\n/lib/" >> "$PROJECT_ROOT/.gitignore"
      print_success "Added /lib/ to .gitignore"
    fi
  else
    print_message "The lib directory doesn't contain JAR files. It may be moved."
    
    # Create util/lib directory if it doesn't exist
    mkdir -p "$UTIL_DIR/lib"
    
    # Copy lib files
    find "$PROJECT_ROOT/lib" -type f -exec cp -v {} "$UTIL_DIR/lib/" \;
    
    # Remove the original directory
    print_warning "The lib directory will be removed. Backup has been made to util/lib."
    print_warning "Press Enter to confirm, or Ctrl+C to cancel..."
    read
    
    rm -rf "$PROJECT_ROOT/lib"
    print_success "Removed lib directory after copying to util/lib"
  fi
fi

# Check for any remaining non-standard directories
print_message "Checking for remaining non-standard directories..."

STANDARD_DIRS=".git .github .idea .mvn .s8r .s8r Samstraumr bin docs quality-tools src target util"
REMAINING_DIRS=$(find "$PROJECT_ROOT" -maxdepth 1 -type d -not -path "$PROJECT_ROOT" | while read dir; do
  dir_name=$(basename "$dir")
  if ! echo "$STANDARD_DIRS" | grep -q -w "$dir_name"; then
    echo "$dir"
  fi
done)

if [[ -n "$REMAINING_DIRS" ]]; then
  print_warning "The following non-standard directories remain in the root:"
  echo "$REMAINING_DIRS" | while read -r dir; do
    echo "  - $(basename "$dir")"
  done
  
  print_message "Consider manually reviewing and handling these directories."
else
  print_success "No additional non-standard directories remain in the root!"
fi

print_section "Final Cleanup Complete"
print_success "Root directory final cleanup completed successfully!"
print_message "Remember to commit these changes to the repository."