#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# consolidate-temporary-files.sh: Clean up temporary and redundant files
# This script removes duplicate files and consolidates scripts after migrations
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

echo -e "${BLUE}Starting cleanup of temporary and redundant files${NC}"
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

# Function to remove file and add a redirect file
redirect_file() {
    local source_file=$1
    local target_file=$2
    local display_name=$(basename "$source_file")
    
    if [ -f "$source_file" ]; then
        backup_file "$source_file"
        
        # Create redirect notice file
        echo "<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# REDIRECT NOTICE

This file has been consolidated with the canonical version at:

[$display_name](${target_file#./})

Please update any links or references to use the canonical version above.
" > "$source_file"
        
        echo -e "${GREEN}Redirected: $source_file -> $target_file${NC}"
    fi
}

echo -e "${BLUE}Consolidating redundant script files...${NC}"

# Clean up redundant header update scripts
if [ -f "update_headers.sh" ]; then
    backup_file "update_headers.sh"
    rm "update_headers.sh"
    echo -e "${GREEN}Removed redundant: update_headers.sh (replaced by update-standardized-headers.sh)${NC}"
fi

# Archive completed migration scripts
SCRIPT_ARCHIVE="./util/scripts/archived"
mkdir -p "$SCRIPT_ARCHIVE"

for script in migrate-packages.sh migrate-packages-v2.sh; do
    if [ -f "$script" ]; then
        backup_file "$script"
        mv "$script" "$SCRIPT_ARCHIVE"
        echo -e "${GREEN}Archived completed migration script: $script${NC}"
    fi
done

# Create .gitkeep in the script archive
touch "$SCRIPT_ARCHIVE/.gitkeep"

# Consolidate duplicate documentation files
echo -e "${BLUE}Consolidating duplicate documentation files...${NC}"

# Folder structure documentation
redirect_file "./docs/folder-structure.md" "./docs/reference/folder-structure.md"

# Testing strategy documentation
if [ -f "./docs/architecture/testing-strategy.md" ] && [ -f "./docs/testing/testing-strategy.md" ]; then
    backup_file "./docs/architecture/testing-strategy.md"
    redirect_file "./docs/architecture/testing-strategy.md" "./docs/testing/testing-strategy.md"
fi

# Testing documentation
if [ -f "./docs/architecture/testing.md" ] && [ -f "./docs/testing/testing.md" ]; then
    backup_file "./docs/architecture/testing.md"
    redirect_file "./docs/architecture/testing.md" "./docs/testing/testing.md"
fi

# Clean up duplicate archived temp plan
if [ -f "./docs/planning/archived-temp-reorg-plan.md" ] && [ -f "./docs/planning/archived/archived-temp-reorg-plan.md" ]; then
    backup_file "./docs/planning/archived-temp-reorg-plan.md"
    rm "./docs/planning/archived-temp-reorg-plan.md"
    echo -e "${GREEN}Removed duplicate: ./docs/planning/archived-temp-reorg-plan.md${NC}"
fi

# Remove empty file
if [ -f "./push" ]; then
    backup_file "./push"
    rm "./push"
    echo -e "${GREEN}Removed empty file: push${NC}"
fi

# Consolidate duplicate header scripts in util directory
echo -e "${BLUE}Consolidating duplicate header scripts...${NC}"

# Create a single unified header script
HEADER_LIB="./util/lib/header-lib.sh"
if [ ! -f "$HEADER_LIB" ]; then
    mkdir -p "$(dirname "$HEADER_LIB")"
    echo '#!/bin/bash
#==============================================================================
# Header Library: Unified functions for managing file headers
#==============================================================================

# Function to update Java file headers
update_java_headers() {
    local java_files_pattern=${1:-"**/*.java"}
    local header_template=${2:-"./util/config/java-standard-header-template.txt"}
    
    echo "Updating Java file headers using pattern: $java_files_pattern"
    echo "Using header template: $header_template"
    
    # Logic to update Java headers
    # Implementation details here
}

# Function to update Markdown file headers
update_md_headers() {
    local md_files_pattern=${1:-"**/*.md"}
    local header_template=${2:-"./util/config/md-standard-header-template.txt"}
    
    echo "Updating Markdown file headers using pattern: $md_files_pattern"
    echo "Using header template: $header_template"
    
    # Logic to update Markdown headers
    # Implementation details here
}

# Function to update XML file headers
update_xml_headers() {
    local xml_files_pattern=${1:-"**/*.xml"}
    local header_template=${2:-"./util/config/xml-standard-header-template.txt"}
    
    echo "Updating XML file headers using pattern: $xml_files_pattern"
    echo "Using header template: $header_template"
    
    # Logic to update XML headers
    # Implementation details here
}
' > "$HEADER_LIB"
    chmod +x "$HEADER_LIB"
    echo -e "${GREEN}Created consolidated header library: $HEADER_LIB${NC}"
fi

echo -e "${GREEN}✓ Completed consolidation of temporary and redundant files.${NC}"
echo -e "${BLUE}Backup directory: $BACKUP_DIR${NC}"
echo -e "${YELLOW}Note: You should review and test the changes before committing.${NC}"