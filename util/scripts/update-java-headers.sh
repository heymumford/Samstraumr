#!/bin/bash
# Filename: update-java-headers.sh
# Purpose: Removes legacy headers from Java files to standardize file header format
# Location: util/maintenance/headers/
# Usage: ./update-java-headers.sh
#
# This script scans for all Java files in the project and removes any comment blocks
# that appear before the package declaration. This helps standardize the file format
# and removes outdated header information.

# Get the project root directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Find all Java files in the project
java_files=$(find "$PROJECT_ROOT" -type f -name "*.java")

for file in $java_files; do
  # Check if file has a comment header block
  if grep -q "^\/\*" "$file"; then
    # Get the package line number
    package_line=$(grep -n "^package" "$file" | head -1 | cut -d: -f1)
    
    if [ ! -z "$package_line" ]; then
      # Delete everything before the package line
      sed -i "1,$(($package_line-1))d" "$file"
      echo "Updated $file"
    fi
  fi
done

echo "Completed updating Java files"