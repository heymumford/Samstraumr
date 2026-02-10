#!/bin/bash
#==============================================================================
# Filename: simplify-headers.sh
# Description: Remove author and date information from bash script headers
#==============================================================================

# Determine script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"

echo "Simplifying headers in bash scripts..."

# Find all bash scripts in the project
find "${PROJECT_ROOT}" -type f -name "*.sh" -o -name "samstraumr" | while read -r file; do
  echo "Processing: $file"
  
  # Use sed to modify the file in place
  sed -i '
    # Remove Author, Created, Updated, and Modified lines with various formats
    /^# *Author:/d
    /^# *Author: /d
    /^# *Created:/d
    /^# *Created: /d
    /^# *Created on:/d
    /^# *Created on: /d
    /^# *Updated:/d
    /^# *Updated: /d
    /^# *Last modified:/d
    /^# *Last modified: /d
    /^# *Modified:/d
    /^# *Modified: /d
  ' "$file"
done

echo "✓ Headers simplified in all bash scripts"