#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# consolidate-documentation.sh: Clean up duplicate documentation files
# This script identifies and consolidates duplicate documentation in the project
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
BACKUP_DIR="./.docs_backups/$(date +%Y%m%d%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo -e "${BLUE}Starting consolidation of duplicate documentation${NC}"
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

# Identify and redirect duplicate documentation files
echo -e "${BLUE}Consolidating duplicate documentation files...${NC}"

# Define canonical paths for common documentation topics
declare -A canonical_docs
canonical_docs["folder-structure.md"]="./docs/reference/folder-structure.md"
canonical_docs["testing-strategy.md"]="./docs/testing/testing-strategy.md"
canonical_docs["testing.md"]="./docs/testing/testing.md"
canonical_docs["maven-structure.md"]="./docs/reference/maven-structure.md"
canonical_docs["java-naming-standards.md"]="./docs/reference/standards/java-naming-standards.md"
canonical_docs["documentation-standards.md"]="./docs/reference/standards/documentation-standards.md"
canonical_docs["logging-standards.md"]="./docs/reference/standards/logging-standards.md"
canonical_docs["work-tracking-guide.md"]="./docs/plans/work-tracking-guide.md"
canonical_docs["kanban.md"]="./docs/plans/kanban.md"

# Find and redirect all non-canonical copies of these files
for filename in "${!canonical_docs[@]}"; do
    canonical="${canonical_docs[$filename]}"
    
    # Skip if canonical doesn't exist
    if [ ! -f "$canonical" ]; then
        echo -e "${YELLOW}Warning: Canonical file not found: $canonical${NC}"
        continue
    fi
    
    # Find all instances of this file
    find ./docs -name "$filename" | while read -r file; do
        if [ "$file" != "$canonical" ]; then
            redirect_file "$file" "$canonical"
        fi
    done
done

# Handle specific duplicates in the planning folder
echo -e "${BLUE}Consolidating planning documentation...${NC}"

# Create mapping of planning docs to reference docs
if [ -f "./docs/planning/version-refactoring-summary.md" ] && [ -f "./docs/plans/complete-version-refactoring-summary.md" ]; then
    redirect_file "./docs/planning/version-refactoring-summary.md" "./docs/plans/complete-version-refactoring-summary.md"
fi

# Clean up archived planning docs
if [ -f "./docs/planning/archived-temp-reorg-plan.md" ] && [ -f "./docs/planning/archived/archived-temp-reorg-plan.md" ]; then
    redirect_file "./docs/planning/archived-temp-reorg-plan.md" "./docs/planning/archived/archived-temp-reorg-plan.md"
fi

# Create a README for the docs folder to explain structure
echo -e "${BLUE}Creating/updating docs README with folder organization...${NC}"

if [ -f "./docs/README.md" ]; then
    backup_file "./docs/README.md"
fi

echo "<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Samstraumr Documentation

This directory contains all documentation for the Samstraumr project. The documentation is organized into a structure that follows the project's conceptual model.

## Documentation Structure

- **reference/** - Canonical reference documentation
  - **standards/** - Coding and documentation standards
  - **release/** - Release notes and changelogs
  
- **concepts/** - Core concepts and architectural principles
  
- **guides/** - How-to guides and tutorials
  - **migration/** - Migration guides for version upgrades
  
- **architecture/** - Architectural documentation
  - **clean/** - Clean architecture implementation details
  - **event/** - Event-driven architecture documentation
  - **patterns/** - Design patterns used in the project
  - **monitoring/** - Monitoring and observability
  
- **testing/** - Testing strategy and methodologies
  
- **plans/** - Current planning documents
  
- **research/** - Research documents and proposals

## Standards for Documentation

All documentation follows these standards:

1. Files are named using kebab-case (e.g., `file-name.md`)
2. Each directory has a README.md explaining its purpose
3. Documentation follows Markdown syntax standards
4. Code examples use appropriate syntax highlighting
5. Documentation is kept up-to-date with code changes

## Canonical File Locations

To avoid duplication, these are the canonical locations for common documentation topics:

- Folder Structure: \`reference/folder-structure.md\`
- Maven Structure: \`reference/maven-structure.md\`
- Testing Strategy: \`testing/testing-strategy.md\`
- Java Standards: \`reference/standards/java-naming-standards.md\`
- Documentation Standards: \`reference/standards/documentation-standards.md\`
- Logging Standards: \`reference/standards/logging-standards.md\`

Please update references in your files to point to these canonical locations.
" > "./docs/README.md"

echo -e "${GREEN}✓ Completed consolidation of duplicate documentation.${NC}"
echo -e "${BLUE}Backup directory: $BACKUP_DIR${NC}"
echo -e "${YELLOW}Note: You should review and test the changes before committing.${NC}"