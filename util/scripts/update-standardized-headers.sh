#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to update copyright headers across the repository with standardized formats
# Usage: ./update-standardized-headers.sh

# Ensure we're in the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
cd "$PROJECT_ROOT" || exit 1

echo "===== Updating copyright headers in repository ====="

# Create temporary file for Java header
JAVA_HEADER=$(mktemp)
cat > "$JAVA_HEADER" << 'EOF'
/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

EOF

# Create temporary file for markdown header
MD_HEADER=$(mktemp)
cat > "$MD_HEADER" << 'EOF'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

EOF

# Function to update Java files
update_java_files() {
    echo "Updating Java copyright headers..."
    find . -name "*.java" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file doesn't exist (in case of race conditions)
        [ ! -f "$file" ] && continue
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Remove existing copyright/license header (common patterns)
        # This removes everything between the start of the file and the package declaration,
        # preserving the package and import statements
        awk '
        BEGIN { skip=1; found=0; }
        /^package / { skip=0; found=1; }
        !skip { print }
        END { if (!found) { exit 1; } }
        ' "$file" > "$TEMP_FILE"
        
        # If awk failed (no package statement), try another approach
        if [ $? -ne 0 ]; then
            # Remove just the copyright notice (between /* and */)
            sed -E '/^\/\*/,/\*\//d' "$file" > "$TEMP_FILE"
        fi
        
        # Add new header + original content
        cat "$JAVA_HEADER" "$TEMP_FILE" > "${TEMP_FILE}.new"
        
        # Replace original file
        mv "${TEMP_FILE}.new" "$file"
        rm -f "$TEMP_FILE"
        
        echo "Updated header in $file"
    done
}

# Function to update markdown files
update_md_files() {
    echo "Updating Markdown copyright headers..."
    find . -name "*.md" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file doesn't exist
        [ ! -f "$file" ] && continue
        
        # Handle README.md specially
        if [[ "$file" == *"/README.md" || "$file" == "./README.md" ]]; then
            continue  # README.md has been handled manually
        fi
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Remove existing HTML comments at the start of the file
        sed '/^<!--/,/-->$/d' "$file" > "$TEMP_FILE"
        
        # Add new header + original content
        cat "$MD_HEADER" "$TEMP_FILE" > "${TEMP_FILE}.new"
        
        # Replace original file
        mv "${TEMP_FILE}.new" "$file"
        rm -f "$TEMP_FILE"
        
        echo "Updated header in $file"
    done
}

# Function to update XML files (pom.xml, etc.)
update_xml_files() {
    echo "Updating XML copyright headers..."
    find . -name "*.xml" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file doesn't exist
        [ ! -f "$file" ] && continue
        
        # Skip if not a POM file or other important XML
        if ! grep -q "<project" "$file" && ! grep -q "<module" "$file"; then
            continue
        fi
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Check if file already has a comment header
        if grep -q "<?xml" "$file" && grep -q "<!--" "$file"; then
            # Extract XML declaration
            XML_DECL=$(grep "<?xml" "$file")
            
            # Remove existing comment
            sed -n '1{/<?xml/p;d}; /<!--/,/-->/d; p' "$file" > "$TEMP_FILE"
            
            # Add XML declaration if it was removed
            if ! grep -q "<?xml" "$TEMP_FILE" && [ -n "$XML_DECL" ]; then
                echo "$XML_DECL" > "${TEMP_FILE}.new"
                cat "$TEMP_FILE" >> "${TEMP_FILE}.new"
                mv "${TEMP_FILE}.new" "$TEMP_FILE"
            fi
            
            # Add new comment after XML declaration
            if grep -q "<?xml" "$TEMP_FILE"; then
                sed -i '1{/<?xml/a\\n<!--\nCopyright (c) 2025 Eric C. Mumford (@heymumford)\n\nThis software was developed with analytical assistance from AI tools \nincluding Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,\nwhich were used as paid services. All intellectual property rights \nremain exclusively with the copyright holder listed above.\n\nLicensed under the Mozilla Public License 2.0\n-->\n' "$TEMP_FILE"
            else
                sed -i '1i<!--\nCopyright (c) 2025 Eric C. Mumford (@heymumford)\n\nThis software was developed with analytical assistance from AI tools \nincluding Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,\nwhich were used as paid services. All intellectual property rights \nremain exclusively with the copyright holder listed above.\n\nLicensed under the Mozilla Public License 2.0\n-->\n' "$TEMP_FILE"
            fi
            
            # Replace original file
            mv "$TEMP_FILE" "$file"
            
            echo "Updated header in $file"
        fi
    done
}

# Execute the update functions
update_java_files
update_md_files
update_xml_files

# Clean up
rm -f "$JAVA_HEADER" "$MD_HEADER"

echo "===== Completed updating copyright headers ====="
echo "Note: README.md and LICENSE have been updated manually."