#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to add copyright headers to all Java files in the Samstraumr project
# Usage: ./add-java-copyright.sh

# Ensure we're in the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT" || exit 1

# Create temporary file for header
TEMP_HEADER=$(mktemp)
cat > "$TEMP_HEADER" << 'EOF'
/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

EOF

echo "Adding copyright headers to Java files..."
find . -name "*.java" -type f -not -path "*/node_modules/*" -not -path "*/target/*" | while read -r file; do
    # Check if header is already present
    if grep -q "Copyright.*Eric C. Mumford" "$file"; then
        echo "Header already exists in $file. Skipping."
        continue
    fi
    
    # Create a temporary file with header + original content
    TEMP_FILE=$(mktemp)
    cat "$TEMP_HEADER" "$file" > "$TEMP_FILE"
    
    # Replace original file with new content
    mv "$TEMP_FILE" "$file"
    echo "Added header to $file"
done

# Clean up
rm "$TEMP_HEADER"

echo "Done adding copyright headers to Java files."