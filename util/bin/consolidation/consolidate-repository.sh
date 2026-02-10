#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# consolidate-repository.sh: Master script to clean up and organize the repository
# This script runs all consolidation scripts to clean up duplicated or temporary files
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

echo -e "${BLUE}Starting repository consolidation${NC}"

# Make all consolidation scripts executable
chmod +x ./consolidate-temporary-files.sh
chmod +x ./consolidate-documentation.sh
chmod +x ./consolidate-header-scripts.sh

# Run each consolidation script
echo -e "${BLUE}===============================================================${NC}"
echo -e "${BLUE}Step 1: Consolidating temporary and redundant files${NC}"
echo -e "${BLUE}===============================================================${NC}"
./consolidate-temporary-files.sh

echo -e "${BLUE}===============================================================${NC}"
echo -e "${BLUE}Step 2: Consolidating duplicate documentation${NC}"
echo -e "${BLUE}===============================================================${NC}"
./consolidate-documentation.sh

echo -e "${BLUE}===============================================================${NC}"
echo -e "${BLUE}Step 3: Consolidating duplicate header scripts${NC}"
echo -e "${BLUE}===============================================================${NC}"
./consolidate-header-scripts.sh

echo -e "${GREEN}✓ All consolidation scripts completed successfully.${NC}"
echo -e "${YELLOW}Please review the changes and run tests before committing.${NC}"

# Suggest running tests
echo -e "${BLUE}Suggestion: Run tests to validate changes${NC}"
echo -e "${YELLOW}./s8r test unit${NC}"

# Create a summary report
echo -e "${BLUE}Creating consolidation summary...${NC}"

SUMMARY_FILE="./consolidation-summary.md"
cat > "$SUMMARY_FILE" << 'EOF'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Repository Consolidation Summary

## Overview

This document summarizes the consolidation work performed to clean up duplicate, temporary, and redundant files in the Samstraumr repository.

## 1. Temporary and Redundant Files

### Removed/Consolidated Files
- Removed redundant `update_headers.sh` (replaced by `update-standardized-headers.sh`)
- Archived completed migration scripts (`migrate-packages.sh`, `migrate-packages-v2.sh`) 
- Removed empty temporary files

### Created Unified Libraries
- Created header library at `./util/lib/header-lib.sh` to centralize header update functionality

## 2. Documentation Consolidation

### Canonical Documentation Files
- Folder Structure: `./docs/reference/folder-structure.md`
- Testing Strategy: `./docs/testing/testing-strategy.md`
- Java Standards: `./docs/reference/standards/java-naming-standards.md`
- Documentation Standards: `./docs/reference/standards/documentation-standards.md`
- Logging Standards: `./docs/reference/standards/logging-standards.md`

### Documentation Structure
- Updated `./docs/README.md` with clear documentation structure and canonical file locations
- Added redirect notices to non-canonical documentation files

## 3. Header Script Consolidation

### Canonical Header Scripts
- Main Header Script: `./update-standardized-headers.sh`
- Header Library: `./util/lib/header-lib.sh`
- S8r-Compatible Script: `./util/bin/update-headers.sh`

### Redirected Scripts
- Redirected various duplicate header scripts to the canonical versions
- Created clear redirect notices so references continue to work

## Benefits of Consolidation

1. Improved maintainability by eliminating duplication
2. Reduced confusion by establishing canonical file locations
3. Better organization of documentation with clear structure
4. Streamlined script structure with libraries and redirects
5. Automated header management with standardized templates

## Next Steps

- Review all changes to ensure functionality is preserved
- Run tests to validate changes
- Consider removing the consolidation scripts once verified
- Update any documentation referring to old file locations

EOF

echo -e "${GREEN}Consolidation summary created at: $SUMMARY_FILE${NC}"
echo -e "${GREEN}✓ Repository consolidation complete.${NC}"