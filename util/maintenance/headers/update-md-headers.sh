#!/bin/bash
# Filename: update-md-headers.sh
# Purpose: Standardizes headers in Markdown documentation files
# Location: util/maintenance/headers/
# Usage: ./update-md-headers.sh
#
# This script processes Markdown files to:
# 1. Keep version information in README.md files but remove other metadata
# 2. Remove entire version blocks from other documentation files
# 3. Clean up extra newlines to keep formatting consistent

# Get the project root directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Find all Markdown files, excluding target directories
md_files=$(find "$PROJECT_ROOT" -type f -name "*.md" | grep -v "target/")

for file in $md_files; do
  # Skip non-documentation files
  if [[ "$file" == *"README.md" ]]; then
    # Special handling for README.md which needs version but not other metadata
    sed -i '/^```$/,/^```$/s/^Last updated:.*$//' "$file"
    sed -i '/^```$/,/^```$/s/^Author:.*$//' "$file"
    sed -i '/^```$/,/^```$/s/^Contributors:.*$//' "$file"
    # Remove empty lines in the version block
    sed -i '/^```$/,/^```$/s/^[[:space:]]*$//g' "$file"
    # Clean up multiple newlines
    sed -i ':a;N;$!ba;s/\n\n\n\n/\n\n/g' "$file"
  else
    # For other markdown files, remove the entire version block
    sed -i '/^```$/,/^```$/d' "$file"
    # Clean up multiple newlines after removal
    sed -i ':a;N;$!ba;s/\n\n\n\n/\n\n/g' "$file"
  fi
  
  echo "Updated $file"
done

echo "Completed updating Markdown files"