#!/bin/bash
#==============================================================================
# consolidate-header-scripts.sh: Consolidate duplicate header scripts
# This script identifies and consolidates redundant header update scripts
#==============================================================================

set -e

# Define color codes for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Ensure we're in the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

# Create backup directory if it doesn't exist
BACKUP_DIR="./.script_backups/$(date +%Y%m%d%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo -e "${BLUE}Starting consolidation of duplicate header scripts${NC}"
echo -e "${BLUE}Creating backup directory: $BACKUP_DIR${NC}"

# Function to move file to backup directory
backup_file() {
    local file=$1
    if [ -f "$file" ]; then
        local dir_path=$(dirname "$file")
        local target_dir="$BACKUP_DIR/$dir_path"
        mkdir -p "$target_dir"
        cp "$file" "$target_dir"
        echo -e "${YELLOW}Backed up: $file${NC}"
    fi
}

# Function to create a redirect script
create_redirect_script() {
    local source_file=$1
    local target_file=$2
    
    if [ -f "$source_file" ]; then
        backup_file "$source_file"
        
        # Create redirect script
        echo "#!/bin/bash
#==============================================================================
# REDIRECT NOTICE: This script has been consolidated
#==============================================================================

# This script has been redirected to the canonical version at: $target_file
echo -e \"\033[1;33mWARNING: $source_file has been moved to $target_file\033[0m\"
echo -e \"Please use \033[1;32m$target_file\033[0m instead.\"
echo \"\"

# Forward to canonical script
\"$target_file\" \"\$@\"
" > "$source_file"
        
        chmod +x "$source_file"
        echo -e "${GREEN}Created redirect script: $source_file -> $target_file${NC}"
    fi
}

# Define the canonical header scripts
CANONICAL_JAVA_HEADERS="./update-standardized-headers.sh"
CANONICAL_HEADER_LIB="./util/lib/header-lib.sh"

# Create a single unified header library if it doesn't exist yet
echo -e "${BLUE}Creating unified header library...${NC}"

if [ ! -f "$CANONICAL_HEADER_LIB" ]; then
    mkdir -p "$(dirname "$CANONICAL_HEADER_LIB")"
    
    # Copy functionality from update-standardized-headers.sh to create a library version
    echo '#!/bin/bash
#==============================================================================
# Header Library: Unified functions for managing file headers
#==============================================================================

# Create temporary file for Java header
create_java_header_template() {
    local temp_file=$(mktemp)
    cat > "$temp_file" << "EOF"
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
    echo "$temp_file"
}

# Create temporary file for markdown header
create_md_header_template() {
    local temp_file=$(mktemp)
    cat > "$temp_file" << "EOF"
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

EOF
    echo "$temp_file"
}

# Function to update Java files
update_java_files() {
    local path_pattern=${1:-"."}
    
    echo "Updating Java copyright headers..."
    JAVA_HEADER=$(create_java_header_template)
    
    find "$path_pattern" -name "*.java" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file does not exist (race condition)
        [ ! -f "$file" ] && continue
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Remove existing copyright/license header
        awk "
        BEGIN { skip=1; found=0; }
        /^package / { skip=0; found=1; }
        !skip { print }
        END { if (!found) { exit 1; } }
        " "$file" > "$TEMP_FILE"
        
        # If awk failed (no package statement), try another approach
        if [ $? -ne 0 ]; then
            # Remove just the copyright notice (between /* and */)
            sed -E "/^\/\*/,/\*\//d" "$file" > "$TEMP_FILE"
        fi
        
        # Add new header + original content
        cat "$JAVA_HEADER" "$TEMP_FILE" > "${TEMP_FILE}.new"
        
        # Replace original file
        mv "${TEMP_FILE}.new" "$file"
        rm -f "$TEMP_FILE"
        
        echo "Updated header in $file"
    done
    
    # Clean up
    rm -f "$JAVA_HEADER"
}

# Function to update markdown files
update_md_files() {
    local path_pattern=${1:-"."}
    
    echo "Updating Markdown copyright headers..."
    MD_HEADER=$(create_md_header_template)
    
    find "$path_pattern" -name "*.md" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file does not exist
        [ ! -f "$file" ] && continue
        
        # Skip README.md in root (handled specially)
        if [[ "$file" == "*/README.md" || "$file" == "./README.md" ]]; then
            continue
        fi
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Remove existing HTML comments at the start of the file
        sed "/^<!--/,/-->$/d" "$file" > "$TEMP_FILE"
        
        # Add new header + original content
        cat "$MD_HEADER" "$TEMP_FILE" > "${TEMP_FILE}.new"
        
        # Replace original file
        mv "${TEMP_FILE}.new" "$file"
        rm -f "$TEMP_FILE"
        
        echo "Updated header in $file"
    done
    
    # Clean up
    rm -f "$MD_HEADER"
}

# Function to update XML files
update_xml_files() {
    local path_pattern=${1:-"."}
    
    echo "Updating XML copyright headers..."
    
    find "$path_pattern" -name "*.xml" -type f \
        -not -path "*/target/*" \
        -not -path "*/node_modules/*" \
        -not -path "*/.git/*" | while read -r file; do
        
        # Skip if file does not exist
        [ ! -f "$file" ] && continue
        
        # Skip non-important XML files
        if ! grep -q "<project" "$file" && ! grep -q "<module" "$file"; then
            continue
        fi
        
        # Create temp file
        TEMP_FILE=$(mktemp)
        
        # Check if file has a comment header
        if grep -q "<?xml" "$file" && grep -q "<!--" "$file"; then
            # Extract XML declaration
            XML_DECL=$(grep "<?xml" "$file")
            
            # Remove existing comment
            sed -n "1{/<?xml/p;d}; /<!--/,/-->/d; p" "$file" > "$TEMP_FILE"
            
            # Add XML declaration if it was removed
            if ! grep -q "<?xml" "$TEMP_FILE" && [ -n "$XML_DECL" ]; then
                echo "$XML_DECL" > "${TEMP_FILE}.new"
                cat "$TEMP_FILE" >> "${TEMP_FILE}.new"
                mv "${TEMP_FILE}.new" "$TEMP_FILE"
            fi
            
            # Add new comment after XML declaration
            if grep -q "<?xml" "$TEMP_FILE"; then
                sed -i "1{/<?xml/a\\n<!--\\nCopyright (c) 2025 Eric C. Mumford (@heymumford)\\n\\nThis software was developed with analytical assistance from AI tools \\nincluding Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,\\nwhich were used as paid services. All intellectual property rights \\nremain exclusively with the copyright holder listed above.\\n\\nLicensed under the Mozilla Public License 2.0\\n-->\\n" "$TEMP_FILE"
            else
                sed -i "1i<!--\\nCopyright (c) 2025 Eric C. Mumford (@heymumford)\\n\\nThis software was developed with analytical assistance from AI tools \\nincluding Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,\\nwhich were used as paid services. All intellectual property rights \\nremain exclusively with the copyright holder listed above.\\n\\nLicensed under the Mozilla Public License 2.0\\n-->\\n" "$TEMP_FILE"
            fi
            
            # Replace original file
            mv "$TEMP_FILE" "$file"
            
            echo "Updated header in $file"
        fi
    done
}

# Main function to update all headers
update_all_headers() {
    update_java_files "$1"
    update_md_files "$1"
    update_xml_files "$1"
}
' > "$CANONICAL_HEADER_LIB"
    
    chmod +x "$CANONICAL_HEADER_LIB"
    echo -e "${GREEN}Created consolidated header library: $CANONICAL_HEADER_LIB${NC}"
fi

# Find all header update scripts
echo -e "${BLUE}Consolidating redundant header scripts...${NC}"

# Clean up redundant header update scripts in root directory
if [ -f "update_headers.sh" ] && [ -f "$CANONICAL_JAVA_HEADERS" ]; then
    backup_file "update_headers.sh"
    rm "update_headers.sh"
    echo -e "${GREEN}Removed redundant: update_headers.sh (replaced by $CANONICAL_JAVA_HEADERS)${NC}"
fi

# Identify and redirect all other header update scripts to canonical versions
find ./util -name "update*header*.sh" | while read -r script; do
    if [ "$script" != "$CANONICAL_HEADER_LIB" ] && [ -f "$script" ]; then
        if [[ "$script" == *"java"* ]]; then
            create_redirect_script "$script" "$CANONICAL_JAVA_HEADERS"
        elif [[ "$script" == *"md"* ]]; then
            create_redirect_script "$script" "$CANONICAL_JAVA_HEADERS"
        else
            create_redirect_script "$script" "$CANONICAL_JAVA_HEADERS"
        fi
    fi
done

# Create a special script to be used by the s8r command
S8R_HEADER_SCRIPT="./util/bin/update-headers.sh"
mkdir -p "$(dirname "$S8R_HEADER_SCRIPT")"

echo '#!/bin/bash
#==============================================================================
# s8r header update script: Calls the canonical header update script
#==============================================================================

# Get project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Call the canonical header update script
"$PROJECT_ROOT/update-standardized-headers.sh" "$@"
' > "$S8R_HEADER_SCRIPT"

chmod +x "$S8R_HEADER_SCRIPT"
echo -e "${GREEN}Created s8r-compatible header script: $S8R_HEADER_SCRIPT${NC}"

echo -e "${GREEN}✓ Completed consolidation of duplicate header scripts.${NC}"
echo -e "${BLUE}Backup directory: $BACKUP_DIR${NC}"
echo -e "${YELLOW}Note: You should review and test the changes before committing.${NC}"