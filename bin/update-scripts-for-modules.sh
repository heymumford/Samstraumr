#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to update all scripts to use the modules/ directory instead of modules/

set -e

# Color settings
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Define directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BIN_DIR="${PROJECT_ROOT}/bin"
SCRIPT_BACKUPS="${PROJECT_ROOT}/bin/backups/$(date +%Y%m%d_%H%M%S)"

# Print functions
print_header() {
  echo -e "${BLUE}=== $1 ===${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Create backup directory
mkdir -p "$SCRIPT_BACKUPS"
print_success "Created backup directory at $SCRIPT_BACKUPS"

# Find all shell scripts that reference modules/ path
print_header "Finding scripts that reference the old directory structure"
SCRIPTS1=$(grep -r -l "modules/" "$BIN_DIR" --include="*.sh" 2>/dev/null || echo "")
SCRIPTS2=$(find "$PROJECT_ROOT" -name "s8r*" -type f -exec grep -l "modules/" {} \; 2>/dev/null || echo "")

# Combine script lists
ALL_SCRIPTS=""
if [ -n "$SCRIPTS1" ]; then
  ALL_SCRIPTS="$SCRIPTS1"
fi

if [ -n "$SCRIPTS2" ]; then
  if [ -n "$ALL_SCRIPTS" ]; then
    ALL_SCRIPTS="$ALL_SCRIPTS"$'\n'"$SCRIPTS2"
  else
    ALL_SCRIPTS="$SCRIPTS2"
  fi
fi

if [ -z "$ALL_SCRIPTS" ]; then
  print_warning "No scripts found referencing modules/"
  exit 0
fi

# Count scripts to be updated
SCRIPT_COUNT=$(echo "$ALL_SCRIPTS" | wc -l)
print_success "Found $SCRIPT_COUNT scripts that need updating"

# Update scripts
print_header "Updating scripts"
COUNT=0
echo "$ALL_SCRIPTS" | while IFS= read -r script; do
  if [ -z "$script" ]; then
    continue
  fi
  
  if [ ! -f "$script" ]; then
    print_warning "Skipping non-existent file: $script"
    continue
  fi
  
  # Create backup
  cp "$script" "$SCRIPT_BACKUPS/$(basename "$script")"
  
  # Update modules/ references to modules/
  ORIGINAL_REFS=$(grep -c "modules/" "$script" || echo "0")
  sed -i 's|modules/|modules/|g' "$script"
  
  # Verify changes
  NEW_REFS=$(grep -c "modules/" "$script" || echo "0")
  
  print_success "Updated $script ($ORIGINAL_REFS references changed)"
  ((COUNT++))
done

# Update Java classpath in config.json if it exists
if [ -f "$PROJECT_ROOT/.s8r/config.json" ]; then
  cp "$PROJECT_ROOT/.s8r/config.json" "$SCRIPT_BACKUPS/config.json"
  sed -i 's|"modules/|"modules/|g' "$PROJECT_ROOT/.s8r/config.json"
  print_success "Updated .s8r/config.json"
fi

# Summary
print_header "Summary"
echo "Updated scripts to use the new modules/ directory structure"
echo "Backups created in $SCRIPT_BACKUPS"
echo ""
echo "Next steps:"
echo "1. Test the scripts to ensure they work correctly"
echo "2. Update any remaining documentation references"
echo "3. Consider removing the old modules/ directory after thorough testing"

exit 0