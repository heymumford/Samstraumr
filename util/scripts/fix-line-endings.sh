#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to fix line endings for all files according to .gitattributes

set -e

# Function to print colored output
print_header() {
  echo -e "\033[1;34m===== $1 =====\033[0m"
}

print_success() {
  echo -e "\033[1;32m✓ $1\033[0m"
}

print_warning() {
  echo -e "\033[1;33m! $1\033[0m"
}

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

print_header "Fixing Line Endings and File Permissions"

# 1. Ensure all shell scripts are executable
print_header "Setting Executable Permissions on Shell Scripts"
find . -name "*.sh" -type f -not -path "*/target/*" -not -path "*/.git/*" -exec chmod +x {} \;
print_success "Set executable permissions on shell scripts"

# 2. Fix line endings using Git
print_header "Fixing Line Endings with Git"
echo "This will normalize all line endings according to .gitattributes..."

# Ensure we have the latest .gitattributes
git add .gitattributes

# Set Git to use the specified line endings
git config core.autocrlf false

# Remove all files from the index
git rm --cached -r .
print_success "Cleared Git index"

# Re-add all files with the correct line endings
git add --all
print_success "Re-added all files to Git index with normalized line endings"

# Commit the changes if there are any
if ! git diff --cached --quiet; then
  echo "Changes detected after line ending normalization."
  git status
  echo "Ready to commit fixes. Use 'git commit -m \"Fix line endings\" && git push' to apply changes."
else
  print_success "No line ending issues to fix!"
fi

print_header "Line Ending Check Complete"