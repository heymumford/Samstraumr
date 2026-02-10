#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: simplify-java-headers.sh
# Description: Remove author and date information from Java file headers
#==============================================================================

# Determine script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"

echo "Simplifying headers in Java files..."

# Find all Java files in the project
find "${PROJECT_ROOT}" -type f -name "*.java" | while read -r file; do
  echo "Processing: $file"
  
  # Use sed to modify the file in place
  sed -i '
    # Remove @author, Copyright, and date-related Javadoc tags
    /@author/d
    /@since/d
    /@version/d
    /@created/d
    /@modified/d
    /^ \* Copyright/d
    /^ \* Created on/d
    /^ \* Last modified/d
    /^ \* Modified on/d
    /^ \* Created:/d
    /^ \* Updated:/d
    /^ \* Last Updated:/d
    /^ \* Date:/d
  ' "$file"
done

echo "✓ Headers simplified in all Java files"