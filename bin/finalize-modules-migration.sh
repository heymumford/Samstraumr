#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to finalize the migration from modules/ to modules/

set -e

# Color settings
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Define directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOURCE_DIR="${PROJECT_ROOT}/Samstraumr"
TARGET_DIR="${PROJECT_ROOT}/modules"

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

# Check if source directory exists
if [ ! -d "$SOURCE_DIR" ]; then
  print_error "Source directory $SOURCE_DIR does not exist!"
  exit 1
fi

# Create target if it doesn't exist (should already exist)
if [ ! -d "$TARGET_DIR" ]; then
  mkdir -p "$TARGET_DIR"
  print_success "Created target directory $TARGET_DIR"
fi

# Function to copy file if target doesn't exist or is different
copy_if_needed() {
  local src="$1"
  local dst="$2"
  
  if [ ! -f "$dst" ]; then
    mkdir -p "$(dirname "$dst")"
    cp "$src" "$dst"
    print_success "Copied $src to $dst"
  elif ! cmp -s "$src" "$dst"; then
    cp "$src" "$dst"
    print_success "Updated $dst from $src (files were different)"
  else
    print_warning "Skipped $src (already copied to $dst and identical)"
  fi
}

print_header "Copying configuration files"

# Copy critical files that should be migrated
if [ -f "$SOURCE_DIR/version.properties" ]; then
  copy_if_needed "$SOURCE_DIR/version.properties" "$TARGET_DIR/version.properties"
fi

if [ -f "$SOURCE_DIR/checkstyle.xml" ]; then
  copy_if_needed "$SOURCE_DIR/checkstyle.xml" "$TARGET_DIR/checkstyle.xml"
fi

if [ -f "$SOURCE_DIR/owasp-suppressions.xml" ]; then
  copy_if_needed "$SOURCE_DIR/owasp-suppressions.xml" "$TARGET_DIR/owasp-suppressions.xml"
fi

# Verify build with the migrated setup
print_header "Testing the build with the migrated structure"
cd "$PROJECT_ROOT"
mvn clean compile -DskipTests

print_success "Migration finalization complete! The build is working with the new modules/ directory structure."
echo ""
echo "Next steps:"
echo "1. Update any remaining documentation references to modules/"
echo "2. Update scripts that reference modules/"
echo "3. Consider removing the old modules/ directory after thorough testing"

exit 0